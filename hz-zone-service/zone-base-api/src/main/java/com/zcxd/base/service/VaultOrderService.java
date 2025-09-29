package com.zcxd.base.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.base.dto.AtmTaskDTO;
import com.zcxd.base.dto.OrderRecordDTO;
import com.zcxd.base.dto.RouteTaskDTO;
import com.zcxd.base.exception.BusinessException;
import com.zcxd.base.vo.AuditVO;
import com.zcxd.base.vo.BatchIdsVO;
import com.zcxd.base.vo.VaultOrderQuickVO;
import com.zcxd.base.vo.VaultOrderRecordVO;
import com.zcxd.base.vo.VaultOrderVO;
import com.zcxd.common.constant.OrderStatusEnum;
import com.zcxd.common.constant.OrderTypeEnum;
import com.zcxd.common.util.KeyValue;
import com.zcxd.db.mapper.AtmAdditionCashMapper;
import com.zcxd.db.mapper.AtmDeviceMapper;
import com.zcxd.db.mapper.AtmTaskMapper;
import com.zcxd.db.mapper.DenomMapper;
import com.zcxd.db.mapper.RouteMapper;
import com.zcxd.db.mapper.VaultOrderMapper;
import com.zcxd.db.mapper.VaultOrderRecordMapper;
import com.zcxd.db.mapper.VaultOrderTaskMapper;
import com.zcxd.db.mapper.VaultVolumMapper;
import com.zcxd.db.model.AtmAdditionCash;
import com.zcxd.db.model.AtmDevice;
import com.zcxd.db.model.AtmTask;
import com.zcxd.db.model.Denom;
import com.zcxd.db.model.Route;
import com.zcxd.db.model.VaultOrder;
import com.zcxd.db.model.VaultOrderRecord;
import com.zcxd.db.model.VaultOrderTask;
import com.zcxd.db.model.VaultVolum;

/**
 * @author songanweo
 * @date 2021-05-31
 */
@Service
public class VaultOrderService extends ServiceImpl<VaultOrderMapper, VaultOrder> {
	@Autowired
	private VaultOrderService orderService;
	@Autowired
    private AtmTaskMapper taskMapper;
	@Autowired
    private VaultOrderTaskMapper orderTaskMapper;
	@Autowired
    private VaultOrderRecordMapper recordMapper;
	@Autowired
    private WorkflowRecordService recordService;
	@Autowired
    private RouteMapper routeMapper;
	@Autowired
    private AtmDeviceMapper atmDeviceMapper;
	@Autowired
    private AtmAdditionCashMapper additionCashMapper;
	@Autowired
    private DenomMapper denomMapper;
	@Autowired
    private VaultVolumMapper vaultVolumMapper;

    /**
     * 返回分页数据对象
     * @param page
     * @param limit
     * @param vaultOrder
     * @return
     */
    public Page<VaultOrder> findListByPage(Integer page, Integer limit, VaultOrder vaultOrder) {
        Page<VaultOrder> orderPage = new Page<>(page, limit);
        QueryWrapper queryWrapper = getQueryWrapper(vaultOrder);
        return baseMapper.selectPage(orderPage, queryWrapper);
    }

    /**
     * 查询条件对象
     * @param vaultOrder
     * @return
     */
    private QueryWrapper getQueryWrapper(VaultOrder vaultOrder){
        QueryWrapper<VaultOrder> queryWrapper = new QueryWrapper<>();
        if(vaultOrder != null){
            if(vaultOrder.getDepartmentId() > 0){
                queryWrapper.eq("department_id", vaultOrder.getDepartmentId());
            }
            if(vaultOrder.getBankId() > 0){
                queryWrapper.eq("bank_id", vaultOrder.getBankId());
            }
            if (vaultOrder.getOrderType() > -1){
                queryWrapper.eq("order_type", vaultOrder.getOrderType());
            }
            if (vaultOrder.getSubType() > -1){
                queryWrapper.eq("sub_type", vaultOrder.getSubType());
            }
            if (vaultOrder.getOrderDate() > 0){
                queryWrapper.eq("order_date", vaultOrder.getOrderDate());
            }
        }
        queryWrapper.eq("deleted", 0);
        queryWrapper.orderBy(true,false,"create_time");
        return queryWrapper;
    }

    /**
     * 出入库创建订单
     * @param orderVO
     * @return
     */
    public boolean create(VaultOrderVO orderVO){
        //计算订单总金额
        BigDecimal totalAmount = orderVO.getVaultRecordList().stream().filter(s -> s.getAmount() != null)
                .map(VaultOrderRecordVO::getAmount).reduce(BigDecimal.ZERO,BigDecimal::add);
        //构造数据
        Long bankId = orderVO.getBankId();
        Long departmentId = orderVO.getDepartmentId();
        Long orderDate = orderVO.getOrderDate();
        Integer orderType = orderVO.getOrderType();
        Integer subType = orderVO.getSubType();
        String comments = orderVO.getComments();

        List<OrderRecordDTO> recordDTOList = orderVO.getVaultRecordList().stream()
                .filter(s -> (s.getAmount() != null && s.getAmount().compareTo(BigDecimal.ZERO) != 0)
                            || (s.getCount() != null && s.getCount() > 0))
                .map(s -> {
            OrderRecordDTO recordDTO = new OrderRecordDTO();
            recordDTO.setDenomType(s.getDenomType());
            recordDTO.setCount(s.getCount());
            recordDTO.setComments("");
            recordDTO.setDenomId(s.getDenomId());
            recordDTO.setAmount(s.getAmount());
            return recordDTO;
        }).collect(Collectors.toList());

        //选择taskId
        List<String> taskIdStrList = orderVO.getTaskIds();
        Map<Integer,List<Long>> map = new HashMap<>(2);
        if (taskIdStrList != null){
            //计算加钞任务ID
            List<Long> taskIdList = taskIdStrList.stream().filter(s -> s.startsWith("task-"))
                    .map(r -> r.replace("task-",""))
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
            //计算清机任务ID
            List<Long> cashIdList = taskIdStrList.stream().filter(s -> s.startsWith("cash-"))
                    .map(r -> r.replace("cash-",""))
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
            map.put(0,taskIdList);
            map.put(1,cashIdList);
        }

        return orderService.createOrder(orderDate,totalAmount,departmentId,bankId,orderType,subType,comments,recordDTOList,map);
    }

    /**
     * ATM加钞快捷出库创建订单
     * @param quickVO
     * @return
     */
    public boolean quickOut(VaultOrderQuickVO quickVO){
        //先查询待出库的任务数据
        Long orderDate = quickVO.getOrderDate();
        Long departmentId = quickVO.getDepartmentId();
        Long bankId = quickVO.getBankId();
        //获取清机任务
        List<AtmTask> resultList = taskMapper.getAtmCleanTask(orderDate, departmentId,bankId);
        //过滤是否执行出库并确认状态才会出库
        List<AtmTask> filterList = resultList.stream()
                .filter(t -> t.getIsOut() == 0).filter(s -> s.getStatusT() == 1).collect(Collectors.toList());
        if (filterList.size() == 0){
            throw new BusinessException(1,"当前已无可出库的加钞任务");
        }

        //构造数据
        Integer orderType = 1;
        Integer subType = 0;
        String comments = "ATM加钞任务出库";

        //券别分组
        Map<Long,List<AtmTask>> denomMap = filterList.stream().collect(Collectors.groupingBy(r -> r.getDenomId()));
        //分别对每种券别进行计算出库金额
        List<KeyValue<Long,BigDecimal>> denomList = denomMap.entrySet().stream().map(r -> {
            KeyValue<Long,BigDecimal>  keyValue = new KeyValue<>();
            List<AtmTask>  atmTaskList = r.getValue();
            BigDecimal taskAmount = atmTaskList.stream().map(AtmTask::getAmount).reduce(BigDecimal.ZERO,BigDecimal::add);
            keyValue.setKey(r.getKey());
            keyValue.setValue(taskAmount);
            return keyValue;
        }).collect(Collectors.toList());

        //计算总金额
        BigDecimal totalAmount = denomList.stream().map(KeyValue::getValue).reduce(BigDecimal.ZERO,BigDecimal::add);
        List<OrderRecordDTO> recordDTOList = denomList.stream().map(s -> {
            OrderRecordDTO recordDTO = new OrderRecordDTO();
            recordDTO.setDenomType(0);
            recordDTO.setCount(1);
            recordDTO.setComments("");
            recordDTO.setDenomId(s.getKey());
            recordDTO.setAmount(s.getValue());
            return recordDTO;
        }).collect(Collectors.toList());

        //选择taskId
        List<Long> taskIdList = filterList.stream().map(AtmTask::getId).collect(Collectors.toList());
        HashMap map = new HashMap(1);
        map.put(0,taskIdList);





       /*

        //创建订单
        VaultOrder vaultOrder = new VaultOrder();
        vaultOrder.setDepartmentId(departmentId);
        vaultOrder.setBankId(bankId);
        vaultOrder.setOrderType(1);
        vaultOrder.setSubType(0);
        vaultOrder.setOrderDate(orderDate);
        vaultOrder.setOrderAmount(totalAmount);
        vaultOrder.setNextUserIds("");
        vaultOrder.setStatusT(0);
        this.baseMapper.insert(vaultOrder);

        //写入order_record券别明细数据
        List<VaultOrderRecord> recordList =  denomList.stream().map(r -> {
            VaultOrderRecord orderRecord = new VaultOrderRecord();
            orderRecord.setOrderId(vaultOrder.getId());
            orderRecord.setDenomType(0);
            orderRecord.setDenomId(r.getKey());
            orderRecord.setAmount(r.getValue());
            orderRecord.setCount(0);
            orderRecord.setComments("");
            return orderRecord;
        }).collect(Collectors.toList());
        if (recordList.size() > 0){
            recordMapper.insertAll(recordList);
        }
        //写入order_task表数据
        List<VaultOrderTask> taskList = filterList.stream().map(t -> {
            VaultOrderTask orderTask = new VaultOrderTask();
            orderTask.setOrderId(vaultOrder.getId());
            orderTask.setCategory(0);
            orderTask.setBankId(t.getBankId());
            orderTask.setRouteId(t.getRouteId());
            orderTask.setTaskId(t.getId());
            orderTask.setAtmId(t.getAtmId());
            orderTask.setAmount(t.getAmount());
            orderTask.setDenomId(t.getDenomId());
            orderTask.setCreateTime(System.currentTimeMillis());
            return orderTask;
        }).collect(Collectors.toList());
        if (taskList.size() > 0){
            orderTaskMapper.insertAll(taskList);
        }
        */

        //更新is_out状态值
        List<Long> atmIds = filterList.stream().map(AtmTask::getId).collect(Collectors.toList());
        if (atmIds.size() > 0){
            QueryWrapper wrapper = Wrappers.query().in("id",atmIds);
            AtmTask atmTask = new AtmTask();
            atmTask.setIsOut(1);
            taskMapper.update(atmTask,wrapper);
        }
        return orderService.createOrder(orderDate,totalAmount,departmentId,bankId,orderType,subType,comments,recordDTOList,map);
    }

    /**
     * 备用金快捷创建出库单
     * @param batchIdsVO
     * @return
     */
    public boolean quickCashOut(BatchIdsVO batchIdsVO){
        List<Long> idList = batchIdsVO.getIds();
        if (idList == null || idList.size() == 0){
            throw new BusinessException(1,"数据错误,请检查");
        }
        Integer orderType = 1;
        Integer subType = 0;
        String comments = "";
        List<AtmAdditionCash> cashList = additionCashMapper.selectBatchIds(idList);
        Long size = cashList.stream().filter(t -> t.getStatusT() ==1).count();
        //判断数据是否相等
        if (size != idList.size()){
            throw new BusinessException(1,"请求数据错误,请检查");
        }
        Long orderDate = LocalDateTime.of(LocalDate.now(),LocalTime.MIN).toInstant(ZoneOffset.of("+8")).toEpochMilli();
        //根据银行进行数据分组
        Map<Long,List<AtmAdditionCash>> bankGroupMap = cashList.stream().collect(Collectors.groupingBy(AtmAdditionCash::getBankId));
        bankGroupMap.entrySet().stream().forEach(t -> {
            Long bankId = t.getKey();
            List<AtmAdditionCash> bankCashList = t.getValue();
            Long departmentId = bankCashList.get(0).getDepartmentId();
            //计算金额
            BigDecimal bankTotalAmount = bankCashList.stream().map(AtmAdditionCash::getAmount).reduce(BigDecimal.ZERO,BigDecimal::add);
            //计算券别分组求和
            Map<Long,List<AtmAdditionCash>> cashMap = bankCashList.stream().collect(Collectors.groupingBy(AtmAdditionCash::getDenomId));
            List<OrderRecordDTO> recordDTOList = cashMap.entrySet().stream().map(s -> {
                OrderRecordDTO recordDTO = new OrderRecordDTO();
                recordDTO.setDenomType(0);
                recordDTO.setCount(1);
                recordDTO.setComments("");
                recordDTO.setDenomId(s.getKey());
                List<AtmAdditionCash> cashes = s.getValue();
                BigDecimal totalAmount = cashes.stream().map(AtmAdditionCash::getAmount).reduce(BigDecimal.ZERO,BigDecimal::add);
                recordDTO.setAmount(totalAmount);
                return recordDTO;
            }).collect(Collectors.toList());

            Map<Integer,List<Long>> map = new HashMap<>();
            // map  0-ATM加钞 1-备用金或其他其他
            List<Long> cashIdList = bankCashList.stream().map(AtmAdditionCash::getId).collect(Collectors.toList());
            map.put(1,cashIdList);
            orderService.createOrder(orderDate,bankTotalAmount,departmentId,bankId,orderType,subType,comments,recordDTOList, map);
        });

        return true;
    }

    /**
     * 修改
     * @param orderVO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean edit(VaultOrderVO orderVO){
        //计算订单总金额
        BigDecimal totalAmount = orderVO.getVaultRecordList().stream()
                .map(VaultOrderRecordVO::getAmount).reduce(BigDecimal.ZERO,BigDecimal::add);

        //查询数据
        VaultOrder order = this.baseMapper.selectById(orderVO.getId());
        if (order == null){
            throw new BusinessException(1,"数据不存在");
        }
        QueryWrapper queryWrapper = Wrappers.query().eq("order_id",orderVO.getId()).eq("deleted",0);
        List<VaultOrderRecord>  recordList = recordMapper.selectList(queryWrapper);

        //修改订单
        VaultOrder vaultOrder = new VaultOrder();
        vaultOrder.setId(orderVO.getId());
        vaultOrder.setDepartmentId(orderVO.getDepartmentId());
        vaultOrder.setBankId(orderVO.getBankId());
        vaultOrder.setOrderType(orderVO.getOrderType());
        vaultOrder.setOrderDate(orderVO.getOrderDate());
        vaultOrder.setOrderAmount(totalAmount);
        vaultOrder.setComments(orderVO.getComments());
        vaultOrder.setNextUserIds("");
        vaultOrder.setStatusT(0);
        this.baseMapper.updateById(vaultOrder);

        //数据比较
        List<VaultOrderRecordVO> addRecordList = orderVO.getVaultRecordList().stream()
                .filter(r ->  r.getId() == null || r.getId() == 0).collect(Collectors.toList());
        List<VaultOrderRecord> deleteRecordList = recordList.stream().filter(t -> {
            Optional<VaultOrderRecordVO> recordVOOptional = orderVO.getVaultRecordList().stream().
                    filter(r -> r.getId() != null)
                    .filter(s -> s.getId().equals(t.getId())).findFirst();
            return !recordVOOptional.isPresent();
        }).collect(Collectors.toList());
        List<VaultOrderRecordVO> updateRecordList = orderVO.getVaultRecordList().stream()
                .filter(r -> {
                    Optional<VaultOrderRecordVO> recordVOOptional = orderVO.getVaultRecordList().stream()
                            .filter(t -> t.getId() != null)
                            .filter(s -> s.getId().equals(r.getId())).findFirst();
                    return recordVOOptional.isPresent();
                }).collect(Collectors.toList());
        //添加
        for (VaultOrderRecordVO item: addRecordList) {
            VaultOrderRecord orderRecord = new VaultOrderRecord();
            orderRecord.setOrderId(vaultOrder.getId());
            orderRecord.setDenomType(item.getDenomType());
            orderRecord.setDenomId(item.getDenomId());
            orderRecord.setAmount(item.getAmount());
            orderRecord.setCount(item.getCount());
            orderRecord.setComments("");
            recordMapper.insert(orderRecord);
        }
        //修改
        for (VaultOrderRecordVO item: updateRecordList) {
            VaultOrderRecord orderRecord = new VaultOrderRecord();
            orderRecord.setId(item.getId());
            orderRecord.setOrderId(vaultOrder.getId());
            orderRecord.setDenomType(item.getDenomType());
            orderRecord.setDenomId(item.getDenomId());
            orderRecord.setAmount(item.getAmount());
            orderRecord.setCount(item.getCount());
            orderRecord.setComments("");
            recordMapper.updateById(orderRecord);
        }
        //删除
        for (VaultOrderRecord item: deleteRecordList) {
            VaultOrderRecord orderRecord = new VaultOrderRecord();
            orderRecord.setId(item.getId());
            orderRecord.setDeleted(1);
            recordMapper.updateById(orderRecord);
        }

        //出库类别ATM加钞类型 task-  cash- 前缀字符串
        if (orderVO.getSubType() == 0 && orderVO.getOrderType() == 1){
            List<String> taskIdStrList = orderVO.getTaskIds();
            //计算加钞任务ID
            List<Long> taskIdList = taskIdStrList.stream().filter(s -> s.startsWith("task-"))
                    .map(r -> r.replace("task-",""))
                    .map(h -> Long.parseLong(h))
                    .collect(Collectors.toList());
            //计算清机任务ID
            List<Long> cashIdList = taskIdStrList.stream().filter(s -> s.startsWith("cash-"))
                    .map(r -> r.replace("cash-",""))
                    .map(h -> Long.parseLong(h))
                    .collect(Collectors.toList());
            //查询原数据
            QueryWrapper orderWrapper = Wrappers.query().eq("order_id", vaultOrder.getId());
            List<VaultOrderTask> orderTaskList = orderTaskMapper.selectList(orderWrapper);
            List<String> oldTaskIdStrList = orderTaskList.stream().map(r -> {
                String res = "";
                if(r.getCategory() == 0){
                    res = "task-" + r.getId();
                }
                if(r.getCategory() == 1){
                    res = "cash-" + r.getId();
                }
                return res;
            }).collect(Collectors.toList());
            boolean check = checkDifferent(taskIdStrList,oldTaskIdStrList);
            if (!check){
                //删除原数据
                QueryWrapper deleteWrapper = Wrappers.query().eq("order_id",vaultOrder.getId());
                orderTaskMapper.delete(deleteWrapper);
                List<VaultOrderTask> atmTaskList = getAtmTaskList(taskIdList, order.getId());
                List<VaultOrderTask> cashTaskList = getCashTaskList(cashIdList, order.getId());
                //合并任务数据
                atmTaskList.addAll(cashTaskList);
                if (atmTaskList.size() > 0){
                    orderTaskMapper.insertAll(atmTaskList);
                }
            }
        }

        return true;
    }

    /**
     * 获取订单详情数据
     * @param orderId
     * @return
     */
    public List<VaultOrderRecord> getDetail(Long orderId){
        QueryWrapper queryWrapper = Wrappers.query().eq("order_id",orderId).eq("deleted",0);
        return recordMapper.selectList(queryWrapper);
    }

    /**
     * 审核
     * @param auditVO
     * @return
     */
    @Transactional
    public boolean audit(AuditVO auditVO){
        //查询当前状态
        VaultOrder vaultOrder = this.getBaseMapper().selectById(auditVO.getId());
        //当前订单状态 <4 审核出入库   5 = 撤销中
        int status = vaultOrder.getStatusT();
        int orderType = vaultOrder.getOrderType();
        String eventId = status == 5 ? "STORAGE_CANCEL" : ( orderType == 1 ? "STORAGE_IN_OUT" : "STORAGE_IN");
        boolean b = recordService.addRecord(auditVO.getId(),eventId,auditVO.getStatus(),auditVO.getComments(), vaultOrder.getDepartmentId());
        if (!b){
            return false;
        }
        ///////////////撤销流程///////////////////
        // 已入库 -> 撤销中
        if (status == OrderStatusEnum.CANCELING.getValue()){
            //撤销审核中
            if (auditVO.getStatus() == 1){
                String userIds = recordService.getUserIds(auditVO.getId(),eventId,vaultOrder.getDepartmentId());
                //更改为审核通过
                if (StringUtils.isEmpty(userIds)){ //终审
                    //已完成出入库需要变更库存余额
                    if (vaultOrder.getFinish() == 1) {
                        vaultAmountRollback(vaultOrder);
                    }
                    //更改为已撤销
                    VaultOrder vaultOrder1 = new VaultOrder();
                    vaultOrder1.setId(auditVO.getId());
                    vaultOrder1.setNextUserIds(userIds);
                    vaultOrder1.setStatusT(OrderStatusEnum.CANCELED.getValue());
                    this.baseMapper.updateById(vaultOrder1);
                } else {
                    VaultOrder vaultOrder1 = new VaultOrder();
                    vaultOrder1.setId(auditVO.getId());
                    vaultOrder1.setNextUserIds(userIds);
                    vaultOrder1.setStatusT(OrderStatusEnum.CANCELING.getValue());
                    this.baseMapper.updateById(vaultOrder1);
                }
            }else{
                //更改为审核拒绝
                VaultOrder vaultOrder1 = new VaultOrder();
                vaultOrder1.setId(auditVO.getId());
                if (vaultOrder.getFinish() == 1) { //已经完成出库的撤销-拒绝，变更会原状态
                    vaultOrder1.setStatusT(OrderStatusEnum.FINISH.getValue());
                } else { //未完成出库的撤销-拒绝，变更会原状态
                    vaultOrder1.setStatusT(OrderStatusEnum.CHECK_APPROVE.getValue());
                }
                vaultOrder1.setNextUserIds("");
                this.baseMapper.updateById(vaultOrder1);
            }
        }

        if (status == 0 || status == 1 || status == 2){
            //出入库审核
             if (auditVO.getStatus() == 1){
                 String userIds = recordService.getUserIds(auditVO.getId(),eventId,vaultOrder.getDepartmentId());
                 //更改为审核通过
                 if (StringUtils.isEmpty(userIds)){
                     //更改为审核拒绝
                     VaultOrder vaultOrder1 = new VaultOrder();
                     vaultOrder1.setId(auditVO.getId());
                     vaultOrder1.setNextUserIds(userIds);
                     vaultOrder1.setStatusT(OrderStatusEnum.CHECK_APPROVE.getValue());
                     this.baseMapper.updateById(vaultOrder1);
                 } else {
                     VaultOrder vaultOrder1 = new VaultOrder();
                     vaultOrder1.setId(auditVO.getId());
                     vaultOrder1.setNextUserIds(userIds);
                     vaultOrder1.setStatusT(OrderStatusEnum.CHECKING.getValue());
                     this.baseMapper.updateById(vaultOrder1);
                 }
             }else{
                 //更改为审核拒绝
                 VaultOrder vaultOrder1 = new VaultOrder();
                 vaultOrder1.setId(auditVO.getId());
                 vaultOrder1.setStatusT(OrderStatusEnum.CHECK_REJECT.getValue());
                 vaultOrder1.setNextUserIds("");
                 this.baseMapper.updateById(vaultOrder1);
             }
        }

        return true;
    }

    private List<VaultOrderRecord> listOrderRecordsByOrderId(Long orderId) {
        VaultOrderRecord where = new VaultOrderRecord();
        where.setOrderId(orderId);
        where.setDeleted(0);
        return recordMapper.selectList(new QueryWrapper<>(where));
    }


    private List<Denom> listDenomsByBatchIds(Set<Long> ids) {
        QueryWrapper<Denom> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id",ids);
        return denomMapper.selectList(queryWrapper);
    }


    private void vaultAmountRollback(VaultOrder order) {
        List<VaultOrderRecord> records = listOrderRecordsByOrderId(order.getId());
        if (records.size() > 0) {
            Set<Long> denomIds = records.stream().map(VaultOrderRecord::getDenomId).collect(Collectors.toSet());
            List<Denom> denomList = listDenomsByBatchIds(denomIds);
            Map<Long,Denom> denomMap = denomList.stream().collect(Collectors.toMap(Denom::getId, Function.identity(),(key1,key2)->key2));
            for (VaultOrderRecord record : records) {
                Denom denom = denomMap.get(record.getDenomId());
                if (denom != null) {
                    if (order.getOrderType() == OrderTypeEnum.STOCK_IN.getValue()) {
                        //入库回滚
                        subVolum(order.getBankId(),denom,record.getDenomType(),record.getAmount(),record.getCount().longValue());
                    } else if (order.getOrderType() == OrderTypeEnum.STOCK_OUT.getValue()) {
                        //出库回滚
                        addVolum(order.getBankId(),denom,record.getDenomType(),record.getAmount(),record.getCount().longValue());
                    }
                }
            }
        }

    }

    /**
     * 增加库存
     * @param bankId
     * @param denom
     * @param denomType
     * @param amount
     * @return
     */
    public void addVolum(Long bankId, Denom denom, int denomType, BigDecimal amount, Long piecesCount) {
        VaultVolum where = new VaultVolum();
        where.setBankId(bankId);
        where.setDenomId(denom.getId());
        where.setDenomType(denomType);
        VaultVolum vaultVolum = vaultVolumMapper.selectOne(new QueryWrapper<>(where));
        if (null == vaultVolum) { //不存在，新增券别
            vaultVolum = new VaultVolum();
            BeanUtils.copyProperties(where,vaultVolum);
            vaultVolum.setCount(piecesCount);
            vaultVolum.setAmount(amount);
            vaultVolumMapper.insert(vaultVolum);
        } else { //增加余额
            if (amount != null) {
                vaultVolum.setAmount(vaultVolum.getAmount().add(amount));
            }
            if (piecesCount != null) {
                vaultVolum.setCount(vaultVolum.getCount() + piecesCount);
            }
            vaultVolumMapper.updateById(vaultVolum);
        }
    }

    /**
     * 扣减库存
     * @param bankId
     * @param denom
     * @param denomType
     * @param amount
     * @return
     */
    public int subVolum(Long bankId, Denom denom, int denomType, BigDecimal amount,Long piecesCount) {
        VaultVolum where = new VaultVolum();
        where.setBankId(bankId);
        where.setDenomId(denom.getId());
        where.setDenomType(denomType);
        VaultVolum vaultVolum = vaultVolumMapper.selectOne(new QueryWrapper<>(where));
        if (null == vaultVolum) {
            return 0;
        }
//        if (denom.getVersion() == DenomVerFlagEnum.VER_BAD.getValue()) {
//            if (vaultVolum.getCount() < piecesCount) {
//                return 0;
//            }
//        } else {
//            if (vaultVolum.getAmount().compareTo(amount) < 0) {
//                return 0;
//            }
//        }
        vaultVolum.setAmount(vaultVolum.getAmount().subtract(amount));
        vaultVolum.setCount(vaultVolum.getCount() - piecesCount);
        vaultVolumMapper.updateById(vaultVolum);
        return 1;
    }


    /**
     * 获取ATM加钞任务数据
     * @param orderId
     * @return
     */
    public List<RouteTaskDTO> getAtmTask(Long orderId){
        QueryWrapper wrapper = Wrappers.query().eq("order_id",orderId);
        List<VaultOrderTask> allTaskList = orderTaskMapper.selectList(wrapper);

        List<Long> routeIdList = allTaskList.stream().map(VaultOrderTask::getRouteId).distinct().collect(Collectors.toList());

        //订单备用金业务
        List<VaultOrderTask> orderCashList = allTaskList.stream().filter(r -> r.getCategory() != 0).collect(Collectors.toList());
        //按线路分组
        Map<Long,List<VaultOrderTask>> orderTaskMap = allTaskList.stream().collect(Collectors.groupingBy(VaultOrderTask::getRouteId));
        Map<Long,String> routeMap = new HashMap<>();
        Set<Long> routeIds = orderTaskMap.keySet();
        if (routeIds.size() > 0){
            Wrapper routeWrapper = Wrappers.query().in("id",routeIds).orderBy(true,true,"route_no * 1");
            List<Route> routeList = routeMapper.selectList(routeWrapper);
            //线路ID重新排序
            routeIdList = routeList.stream().map(Route::getId).collect(Collectors.toList());
            routeMap = routeList.stream().collect(Collectors.toMap(Route::getId,Route::getRouteName));
        }
        //线路备用金分组
        Map<Long,List<VaultOrderTask>> orderCashMap = orderCashList.stream().collect(Collectors.groupingBy(VaultOrderTask::getRouteId));
        //遍历
        Map<Long, String> finalRouteMap = routeMap;
        return routeIdList.stream().map(routeId -> {
            List<VaultOrderTask> taskList = new LinkedList<>();
            if(orderTaskMap.get(routeId) != null){
                List<VaultOrderTask>  orderTaskList = orderTaskMap.get(routeId);
                //订单ATM任务
                taskList = orderTaskList.stream().filter(r -> r.getCategory() == 0).collect(Collectors.toList());
            }
            RouteTaskDTO taskDTO = new RouteTaskDTO();
            //如果
            if (taskList.size() > 0){
                //获取设备ID
                Map<Long,String> atmMap = new HashMap<>();
                List<Long> atmIds = taskList.stream().map(VaultOrderTask::getAtmId).collect(Collectors.toList());
                if(atmIds.size() > 0){
                    List<AtmDevice>  atmDeviceList = atmDeviceMapper.selectBatchIds(atmIds);
                    atmMap = atmDeviceList.stream().collect(Collectors.toMap(AtmDevice::getId,AtmDevice::getTerNo));
                }
                Map<Long, String> finalAtmMap = atmMap;
                List<AtmTaskDTO> atmTaskDTOList = taskList.stream().map(s -> {
                    AtmTaskDTO atmTaskDTO = new AtmTaskDTO();
                    atmTaskDTO.setId(s.getTaskId());
                    atmTaskDTO.setAtmId(s.getAtmId());
                    atmTaskDTO.setAmount(s.getAmount());
                    atmTaskDTO.setTerNo(Optional.ofNullable(finalAtmMap.get(s.getAtmId())).orElse(""));
                    return atmTaskDTO;
                }).collect(Collectors.toList());
                taskDTO.setTaskList(atmTaskDTOList);
            }

            taskDTO.setOrderId(orderId);
            taskDTO.setRouteId(routeId);
            taskDTO.setRouteName(Optional.ofNullable(finalRouteMap.get(routeId)).orElse(""));
            //计算线路备用金
            List<VaultOrderTask> cashList = orderCashMap.get(routeId);
            if (cashList != null && cashList.size() > 0){
                List<Long> cashIds = cashList.stream().map(VaultOrderTask::getTaskId).collect(Collectors.toList());
                List<AtmAdditionCash> additionCashList = additionCashMapper.selectBatchIds(cashIds);
                BigDecimal cashTotal = additionCashList.stream().map(AtmAdditionCash::getAmount).reduce(BigDecimal.ZERO,BigDecimal::add);
                taskDTO.setCashAmount(cashTotal);
            }
            return taskDTO;
        }).collect(Collectors.toList());
    }

    /**
     * 判断两个列表元素是否相等
     * @param list
     * @param list1
     * @param <T>
     * @return
     */
    private <T> boolean checkDifferent(List<T> list, List<T> list1) {
        if(list.size() != list1.size()) {
            return false;
        }
        for(T id : list) {
            if(!list1.contains(id)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 根据taskId列表获取订单ATM任务列表数据
     * @param taskIdList
     * @return
     */
    private List<VaultOrderTask> getAtmTaskList(List<Long> taskIdList, Long orderId){
        if (taskIdList == null || taskIdList.size() == 0){
            return new LinkedList<>();
        }
        List<AtmTask> atmTaskList = taskMapper.selectBatchIds(taskIdList);
        Map<Long, AtmTask> atmTaskMap = atmTaskList.stream().collect(Collectors.toMap(AtmTask::getId, r-> r));
        List<VaultOrderTask> taskList = taskIdList.stream().map(taskId -> {
            AtmTask atmTask = atmTaskMap.get(taskId);
            VaultOrderTask orderTask = new VaultOrderTask();
            orderTask.setOrderId(orderId);
            orderTask.setRouteId(atmTask.getRouteId());
            orderTask.setTaskId(taskId);
            orderTask.setBankId(atmTask.getBankId());
            orderTask.setCategory(0);
            orderTask.setAtmId(atmTask.getAtmId());
            orderTask.setDenomId(atmTask.getDenomId());
            orderTask.setAmount(atmTask.getAmount());
            orderTask.setCreateTime(System.currentTimeMillis());
            return orderTask;
        }).collect(Collectors.toList());
        return taskList;
    }

    /**
     * 根据备用金cashId列表获取订单备用金任务列表数据
     * @param cashIdList 备用金任务ID列表
     * @param orderId 订单ID
     * @return
     */
    private List<VaultOrderTask> getCashTaskList(List<Long> cashIdList, Long orderId){
        if (cashIdList == null || cashIdList.size() == 0){
            return new LinkedList<>();
        }
        List<AtmAdditionCash> cashList = additionCashMapper.selectBatchIds(cashIdList);
        Map<Long, AtmAdditionCash> cashMap = cashList.stream().collect(Collectors.toMap(AtmAdditionCash::getId, r-> r));
        List<VaultOrderTask> taskList = cashIdList.stream().map(cashId -> {
            AtmAdditionCash additionCash = cashMap.get(cashId);
            Integer cashType = additionCash.getCashType();
            VaultOrderTask orderTask = new VaultOrderTask();
            orderTask.setOrderId(orderId);
            orderTask.setRouteId(additionCash.getRouteId());
            orderTask.setTaskId(cashId);
            orderTask.setBankId(additionCash.getBankId());
            orderTask.setCategory(cashType + 1);
            orderTask.setAtmId(0L);
            orderTask.setDenomId(additionCash.getDenomId());
            orderTask.setAmount(additionCash.getAmount());
            orderTask.setCreateTime(System.currentTimeMillis());
            return orderTask;
        }).collect(Collectors.toList());
        return taskList;
    }

    /**
     * 创建订单
     * @param orderDate  订单任务日期时间戳
     * @param totalAmount 订单总额
     * @param departmentId 部门ID
     * @param bankId 机构ID
     * @param orderType 订单类型
     * @param subType 子类型
     * @param comments 备注信息
     * @param recordDTOList  券别列表
     * @param map  类别Map  key(0-ATM加钞 1-备用金  2-其他)
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean createOrder(Long orderDate, BigDecimal totalAmount, Long departmentId, Long bankId, Integer orderType, Integer subType,
                               String comments, List<OrderRecordDTO> recordDTOList,Map<Integer,List<Long>> map){
        VaultOrder vaultOrder = new VaultOrder();
        vaultOrder.setDepartmentId(departmentId);
        vaultOrder.setBankId(bankId);
        vaultOrder.setOrderType(orderType);
        vaultOrder.setSubType(subType);
        vaultOrder.setOrderDate(orderDate);
        vaultOrder.setOrderAmount(totalAmount);
        vaultOrder.setComments(comments);
        vaultOrder.setNextUserIds("");
        vaultOrder.setStatusT(0);
        this.baseMapper.insert(vaultOrder);
        //创建订单详情
        List<VaultOrderRecord> recordList = recordDTOList.stream()
                .map(t -> {
                    VaultOrderRecord orderRecord = new VaultOrderRecord();
                    orderRecord.setOrderId(vaultOrder.getId());
                    orderRecord.setDenomType(t.getDenomType());
                    orderRecord.setDenomId(t.getDenomId());
                    orderRecord.setAmount(t.getAmount());
                    orderRecord.setCount(t.getCount());
                    orderRecord.setComments("");
                    return orderRecord;
                }).collect(Collectors.toList());
        if(recordList.size() > 0){
            recordMapper.insertAll(recordList);
        }
        //针对出库类别ATM加钞类型
        if (subType== 0 && orderType == 1){
            //计算加钞任务ID
            List<Long> taskIdList = map.get(0);
            List<Long> cashIdList = map.get(1);
            //计算清机任务ID、备用金任务
            List<VaultOrderTask> atmTaskList = getAtmTaskList(taskIdList, vaultOrder.getId());
            List<VaultOrderTask> cashTaskList = getCashTaskList(cashIdList, vaultOrder.getId());
            //合并任务数据
            atmTaskList.addAll(cashTaskList);
            if (atmTaskList.size() > 0){
                orderTaskMapper.insertAll(atmTaskList);
            }
        }
        return true;
    }
}
