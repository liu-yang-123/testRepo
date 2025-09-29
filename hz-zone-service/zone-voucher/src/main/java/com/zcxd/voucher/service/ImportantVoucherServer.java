package com.zcxd.voucher.service;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zcxd.common.util.Result;
import com.zcxd.db.model.Bank;
import com.zcxd.db.model.BoxpackTask;
import com.zcxd.voucher.constant.ImportantVoucherTypeEnum;
import com.zcxd.voucher.constant.ImportantVoucherWarehouseApplyAction;
import com.zcxd.voucher.constant.ImportantVoucherWarehouseItemOperateTypeEnum;
import com.zcxd.voucher.db.model.ImportantVoucher;
import com.zcxd.voucher.db.model.ImportantVoucherNumberSegment;
import com.zcxd.voucher.db.model.ImportantVoucherWarehouseItem;
import com.zcxd.voucher.db.model.ImportantVoucherWarehouseRecord;
import com.zcxd.voucher.dto.ImportantVoucherDTO;
import com.zcxd.voucher.feign.RemoteProvider;
import com.zcxd.voucher.utils.UserContextHolder;
import com.zcxd.voucher.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zccc
 */
@Service
public class ImportantVoucherServer {
    @Autowired
    ImportantVoucherService importantVoucherService;
    @Autowired
    ImportantVoucherNumberSegmentService segmentService;
    @Autowired
    ImportantVoucherWarehouseItemService itemService;
    @Autowired
    ImportantVoucherWarehouseRecordService recordService;
    @Autowired
    RemoteProvider remoteService;

    /**
     * 新增重空
     * @param importantVoucherVo 属性
     * @return 结果
     */
    public Result saveImportantVoucher(ImportantVoucherVO importantVoucherVo) {
        // check
        Result result = checkImportantVoucherOperation(importantVoucherVo);
        if (result.isFailed()) {
            return result;
        }

        return importantVoucherService.saveImportantVoucher(importantVoucherVo);
    }

    /**
     * 新增重空分段
     * @param segmentVO 属性
     * @return 结果
     */
    public Result saveImportantVoucherNumberSegment(ImportantVoucherNumberSegmentVO segmentVO) {
        // check
        Result<Boolean> authResult = remoteService.isAuthDepartment(segmentVO.getDepartmentId());
        if (authResult.isFailed()) {
            return Result.fail(authResult.getMsg());
        }

        ImportantVoucherQueryVO importantVoucherVO = new ImportantVoucherQueryVO();
        importantVoucherVO.setId(segmentVO.getVoucherId());
        importantVoucherVO.setDepartmentId(segmentVO.getDepartmentId());
        if (importantVoucherService.selectOne(importantVoucherVO) == null) {
            return Result.fail("未查找到目标重空");
        }

        return segmentService.saveImportantVoucherNumberSegment(segmentVO);
    }

    /**
     * 分页查询重空
     * @param page 页数
     * @param limit 数量
     * @param queryVo 查询条件
     * @return 查询结果
     * @throws IllegalAccessException 错误
     */
    public Result findImportantVoucherListByPage(Integer page, Integer limit, ImportantVoucherQueryVO queryVo) {
        Result<Boolean> authResult = remoteService.isAuthDepartment(queryVo.getDepartmentId());
        if (authResult.isFailed()) {
            return Result.fail("您所在的部门无权限");
        }
        Result result = importantVoucherService.findListByPage(page, limit, queryVo);

        // 对每一个重空计算库存数量
        Page<ImportantVoucher> data = (Page<ImportantVoucher>) result.getData();
        List<ImportantVoucher> records = data.getRecords();
        List<Long> ids = records.stream().map(ImportantVoucher::getId).collect(Collectors.toList());

        // 构建Id为key，segment为value的Map
        List<ImportantVoucherNumberSegment> importantVoucherNumberSegments = segmentService.findListByVoucherIds(ids, queryVo.getDepartmentId());
        Map<Long, List<ImportantVoucherNumberSegment>> map = new HashMap<>();
        importantVoucherNumberSegments.forEach(segment -> {
            Long id = segment.getVoucherId();
            List<ImportantVoucherNumberSegment> list;
            if (!map.containsKey(id)) {
                map.put(id, new ArrayList<>());
            }
            list = map.get(id);
            list.add(segment);
        });

        // 构建新的重空类
        List<ImportantVoucherDTO> dtos = new ArrayList<>();
        for (int i = 0; i < records.size(); i++) {
            ImportantVoucher importantVoucher = records.get(i);
            Long count = 0L;
            List<ImportantVoucherNumberSegment> segments = map.get(importantVoucher.getId());
            for (ImportantVoucherNumberSegment segment: segments) {
                count += segment.getCount();
            }
            ImportantVoucherDTO importantVoucherDTO = new ImportantVoucherDTO();
            BeanUtils.copyProperties(importantVoucher, importantVoucherDTO);
            importantVoucherDTO.setCount(count);
            dtos.add(importantVoucherDTO);
        }
        Page<ImportantVoucherDTO> importantVoucherDTOPage = new Page<>();
        importantVoucherDTOPage.setSize(data.getSize());
        importantVoucherDTOPage.setCurrent(data.getCurrent());
        importantVoucherDTOPage.setTotal(data.getTotal());
        importantVoucherDTOPage.setRecords(dtos);

        return Result.success(importantVoucherDTOPage);
    }

    /**
     * 分页查询重空
     * @param page 页数
     * @param limit 数量
     * @param queryVo 查询条件
     * @return 查询结果
     * @throws IllegalAccessException 错误
     */
    public Result findImportantVoucherNumberSegmentListByPage(Integer page, Integer limit, ImportantVoucherNumberSegmentQueryVO queryVo) {
        Result<Boolean> authResult = remoteService.isAuthDepartment(queryVo.getDepartmentId());
        if (authResult.isFailed()) {
            return Result.fail(authResult.getMsg());
        }

        return segmentService.findListByPage(page, limit, queryVo);
    }

    /**
     * 分页查询重空审批记录
     * @param page 页数
     * @param limit 数量
     * @param queryVo 查询条件
     * @return 查询结果
     * @throws IllegalAccessException 错误
     */
    public Result findImportantVoucherWarehouseRecordListByPage(Integer page, Integer limit, ImportantVoucherWarehouseRecordQueryVO queryVo) {
        Result<Boolean> authResult = remoteService.isAuthDepartment(queryVo.getDepartmentId());
        if (authResult.isFailed()) {
            return Result.fail(authResult.getMsg());
        }

        return recordService.findListByPage(page, limit, queryVo);
    }

    /**
     * 审批申请
     * @param id 申请Id
     * @param departmentId 部门Id
     * @return 审批结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Result confirmApplyItem(Long id, Integer departmentId) throws Exception {
        Result<Boolean> authResult = remoteService.isAuthDepartment(departmentId);
        if (authResult.isFailed()) {
            return Result.fail(authResult.getMsg());
        }

        ImportantVoucherWarehouseItem warehouseItem = itemService.getById(id);
        if (warehouseItem == null) {
            return Result.fail("请选择一条申请");
        }

        if (warehouseItem.getIsConfirm() != 0) {
            return Result.fail("当前申请已经审核通过，不能重复审核");
        }

        warehouseItem.setIsConfirm((short) 1);
        warehouseItem.setConfirmName(UserContextHolder.getUsername());
        warehouseItem.setConfirmTime(System.currentTimeMillis());

        boolean b = itemService.updateById(warehouseItem);
        if (!b) {
            throw new Exception("审批失败");
        }

        ImportantVoucherWarehouseRecord record = new ImportantVoucherWarehouseRecord();
        BeanUtils.copyProperties(warehouseItem, record);
        record.setUpdateName(UserContextHolder.getUsername());
        record.setAction(ImportantVoucherWarehouseApplyAction.APPROVAL.getText());

        boolean c = recordService.save(record);
        if (!c) {
            throw new Exception("审批记录添加失败");
        }

        return Result.success();
    }

    /**
     * 反审批申请
     * @param id 申请Id
     * @param departmentId 部门Id
     * @return 反审批结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Result cancelApplyItem(Long id, Integer departmentId) throws Exception {
        Result<Boolean> authResult = remoteService.isAuthDepartment(departmentId);
        if (authResult.isFailed()) {
            return Result.fail(authResult.getMsg());
        }

        ImportantVoucherWarehouseItem warehouseItem = itemService.getById(id);
        if (warehouseItem == null) {
            return Result.fail("请选择一条申请");
        }

        if (warehouseItem.getIsConfirm() != 1) {
            return Result.fail("当前申请未审核通过，不需要反审");
        }

        warehouseItem.setIsConfirm((short) 0);
        warehouseItem.setCancelName(UserContextHolder.getUsername());
        warehouseItem.setCancelTime(System.currentTimeMillis());

        boolean b = itemService.updateById(warehouseItem);
        if (!b) {
            throw new Exception("反审失败");
        }

        ImportantVoucherWarehouseRecord record = new ImportantVoucherWarehouseRecord();
        BeanUtils.copyProperties(warehouseItem, record);
        record.setUpdateName(UserContextHolder.getUsername());
        record.setAction(ImportantVoucherWarehouseApplyAction.CANCEL.getText());

        boolean c = recordService.save(record);
        if (!c) {
            throw new Exception("反审记录添加失败");
        }

        return Result.success();
    }

    /**
     * 手动绑定任务
     * @param id 申请Id
     * @param taskId 任务Id
     * @return 绑定结果
     */
    public Result associateTaskManual(Long id, Long taskId) {
      //与尾箱任务绑定
        ImportantVoucherWarehouseItem warehouseItem = itemService.getById(id);
        if (warehouseItem == null || !warehouseItem.getOperateType().equals(
                ImportantVoucherWarehouseItemOperateTypeEnum.OUT.getText())) {
            return Result.fail("请选择一个出库申请");
        }

        Result<BoxpackTask> result = remoteService.findBoxpackById(taskId);
        if (result.isFailed()) {
            return Result.fail("请选择一个任务");
        }
        BoxpackTask task = result.getData();

        warehouseItem.setTaskId(taskId);
        warehouseItem.setTaskDate(task.getTaskDate());
        warehouseItem.setTaskLine(task.getRouteNo());
        return itemService.updateItemById(warehouseItem);
    }

    /**
     * 新增检查
     * @param importantVoucherVo 属性
     * @return 结果
     */
    private Result checkImportantVoucherOperation(ImportantVoucherVO importantVoucherVo) {
        Result<Boolean> authResult = remoteService.isAuthDepartment(importantVoucherVo.getDepartmentId());
        if (authResult.isFailed()) {
            return Result.fail(authResult.getMsg());
        }

        Bank bank = new Bank();
        bank.setId(importantVoucherVo.getBankId());
        bank.setDepartmentId((long)importantVoucherVo.getDepartmentId());
        bank.setDeleted(0);
        Result<Bank> bankResult = remoteService.findBankByCondition(bank);
        if (bankResult.isFailed()) {
            return Result.fail("目标银行不存在");
        }
        Bank bank1 = bankResult.getData();
        // 非顶层银行
        if (!"/0/".equals(bank1.getParentIds())) {
            return Result.fail("目标银行不是顶层银行");
        }

        return Result.success();
    }

    /**
     * 新增检查
     * @param importantVoucherNumberSegmentVO 属性
     * @return 结果
     */
    private Result checkImportantVoucherNumberSegmentOperation(ImportantVoucherNumberSegmentVO importantVoucherNumberSegmentVO) {
        Result<Boolean> authResult = remoteService.isAuthDepartment(importantVoucherNumberSegmentVO.getDepartmentId());
        if (authResult.isFailed()) {
            return Result.fail(authResult.getMsg());
        }

        return Result.success();
    }

    /**
     * 更新重空信息
     * @param importantVoucherVO 属性
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Object updateImportantVoucherById(ImportantVoucherVO importantVoucherVO) throws Exception {
        boolean needChangeSegment = false;

        Result result = checkImportantVoucherOperation(importantVoucherVO);
        if (result.isFailed()) {
            return result;
        }

        Result updateImportantVoucherResult = importantVoucherService.updateImportantVoucherById(importantVoucherVO);
        if (updateImportantVoucherResult.isFailed()) {
            return updateImportantVoucherResult;
        }

        // 同时修改分段
        ImportantVoucherNumberSegment segment = new ImportantVoucherNumberSegment();
        ImportantVoucherNumberSegmentQueryVO segmentVO = new ImportantVoucherNumberSegmentQueryVO();

        if (StringUtils.isNotEmpty(importantVoucherVO.getName())) {
            needChangeSegment = true;
            segment.setName(importantVoucherVO.getName());
        }
        if (importantVoucherVO.getType() != null) {
            needChangeSegment = true;
            segment.setType(importantVoucherVO.getType());
        }
        if (needChangeSegment) {
            segmentVO.setVoucherId(importantVoucherVO.getId());
            segmentVO.setDepartmentId(importantVoucherVO.getDepartmentId());
            Result res = segmentService.updateImportantVoucherNumberSegment(segment, segmentVO);
            if (res.isFailed()) {
                throw new Exception(res.getMsg());
            }
            return res;
        }

        return Result.success();
    }

    /**
     * 更新重空信息
     * @param segmentVO 属性
     * @return 结果
     */
    public Result updateImportantVoucherNumberSegmentById(ImportantVoucherNumberSegmentVO segmentVO) {
        Result result = checkImportantVoucherNumberSegmentOperation(segmentVO);
        if (result.isFailed()) {
            return result;
        }

        Result updateImportantVoucherResult = segmentService.updateImportantVoucherNumberSegmentById(segmentVO);
        if (updateImportantVoucherResult.isFailed()) {
            return updateImportantVoucherResult;
        }

        return Result.success();
    }

    /**
     * 删除重空信息
     * @param queryVO 属性
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Object deleteImportantVoucher(ImportantVoucherQueryVO queryVO) throws Exception {
        Result<Boolean> authResult = remoteService.isAuthDepartment(queryVO.getDepartmentId());
        if (authResult.isFailed()) {
            return Result.fail(authResult.getMsg());
        }

        Result deleteImportantVoucherResult = importantVoucherService.deleteImportantVoucher(queryVO);
        if (deleteImportantVoucherResult.isFailed()) {
            return deleteImportantVoucherResult;
        }

        // 同时删除分段
        ImportantVoucherNumberSegmentQueryVO importantVoucherNumberSegmentQueryVO = new ImportantVoucherNumberSegmentQueryVO();
        importantVoucherNumberSegmentQueryVO.setDepartmentId(queryVO.getDepartmentId());
        importantVoucherNumberSegmentQueryVO.setVoucherId(queryVO.getId());
        Result res = deleteImportantVoucherNumberSegment(importantVoucherNumberSegmentQueryVO);
        if (res.isFailed()) {
            throw new Exception(res.getMsg());
        }
        return res;
    }

    /**
     * 删除重空分段信息
     * @param queryVO 属性
     * @return 结果
     */
    public Result deleteImportantVoucherNumberSegment(ImportantVoucherNumberSegmentQueryVO queryVO) {
        Result<Boolean> authResult = remoteService.isAuthDepartment(queryVO.getDepartmentId());
        if (authResult.isFailed()) {
            return Result.fail(authResult.getMsg());
        }

        Result result = segmentService.deleteImportantVoucherNumberSegment(queryVO);
        if (result.isFailed()) {
            return result;
        }

        return Result.success();
    }

    /**
     * 删除记录信息
     * @param queryVo 属性
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Result deleteItem(ImportantVoucherWarehouseItemQueryVO queryVo) throws Exception {
        Result<Boolean> authResult = remoteService.isAuthDepartment(queryVo.getDepartmentId());
        if (authResult.isFailed()) {
            return Result.fail(authResult.getMsg());
        }

        Result result = itemService.deleteItem(queryVo);
        if (result.isFailed()) {
            return result;
        }

        ImportantVoucherWarehouseItem item = (ImportantVoucherWarehouseItem)result.getData();
        ImportantVoucherWarehouseRecord record = new ImportantVoucherWarehouseRecord();
        BeanUtils.copyProperties(item, record);
        record.setAction(ImportantVoucherWarehouseApplyAction.DELETE.getText());
        record.setUpdateName(UserContextHolder.getUsername());

        boolean c = recordService.save(record);
        if (!c) {
            throw new Exception("删除记录添加失败");
        }
        return Result.success();
    }

    /**
     * 重空入库申请
     * @param item 重空属性
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Result entryWarehouseApply(ImportantVoucherWarehouseItem item) throws Exception {
        Integer departmentId = item.getDepartmentId();
        Result<Boolean> authResult = remoteService.isAuthDepartment(departmentId);
        if (authResult.isFailed()) {
            return Result.fail(authResult.getMsg());
        }

        Long voucherId = item.getVoucherId();
        Long start = item.getStart();
        Long end = item.getEnd();
        item.countSegmentNum();
        item.setCreateName(UserContextHolder.getUsername());

        // 判断入库分段是否合法
        ImportantVoucherNumberSegmentVO segmentVO = new ImportantVoucherNumberSegmentVO(voucherId, start, end, departmentId);
        Result result = segmentService.checkImportantVoucherNumberSegmentIn(segmentVO);

        if (result.isFailed()) {
            return result;
        }

        // 入库申请
        Result res = itemService.saveItem(item);
        if (res.isFailed()) {
            return res;
        }

        ImportantVoucherWarehouseRecord record = new ImportantVoucherWarehouseRecord();
        BeanUtils.copyProperties(item, record);
        record.setAction(ImportantVoucherWarehouseApplyAction.EDIT.getText());
        record.setUpdateName(UserContextHolder.getUsername());

        boolean c = recordService.save(record);
        if (!c) {
            throw new Exception("审批记录添加失败");
        }
        return Result.success();
    }

    /**
     * 创建重空出库申请
     * @param item 重空属性
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Result outWarehouseApply(ImportantVoucherWarehouseItem item) throws Exception {
        Result<Boolean> authResult = remoteService.isAuthDepartment(item.getDepartmentId());
        if (authResult.isFailed()) {
            return Result.fail(authResult.getMsg());
        }

        ImportantVoucherNumberSegmentVO segmentVO = new ImportantVoucherNumberSegmentVO(
                item.getVoucherId(), item.getStart(), item.getEnd(), item.getDepartmentId());
        Result result = segmentService.checkImportantVoucherNumberSegmentOut(segmentVO);
        if (result.isFailed()) {
            return result;
        }
        item.countSegmentNum();
        item.setCreateName(UserContextHolder.getUsername());

        // 入库申请
        Result res = itemService.saveItem(item);
        if (res.isFailed()) {
            return res;
        }

        ImportantVoucherWarehouseRecord record = new ImportantVoucherWarehouseRecord();
        BeanUtils.copyProperties(item, record);
        record.setAction(ImportantVoucherWarehouseApplyAction.EDIT.getText());
        record.setUpdateName(UserContextHolder.getUsername());

        boolean c = recordService.save(record);
        if (!c) {
            throw new Exception("审批记录添加失败");
        }
        return Result.success();
    }

    /**
     * 重空出库操作
     * @param queryVO 重空出库申请
     * @return 结果
     */
    public Result outWarehouseOperation(ImportantVoucherWarehouseItemQueryVO queryVO) {
        List<ImportantVoucherWarehouseItem> list = itemService.findList(queryVO);
        if (list.isEmpty()) {
            return Result.fail("未正确选择重空出库申请记录");
        }

        ImportantVoucherWarehouseItem item = list.get(0);
        Long voucherId = item.getVoucherId();
        Long start = item.getStart();
        Long end = item.getEnd();
        Integer departmentId = item.getDepartmentId();
        return segmentService.importantVoucherNumberSegmentOut(voucherId, start, end, departmentId);
    }


    /**
     * 导入
     * @param file 枪信息文件
     * @param departmentId 部门
     * @return 结果
     * @throws Exception 错误
     */
    public Object importantVoucherExcelImport(MultipartFile file, Integer departmentId) throws Exception {
        if (file == null || file.getSize() == 0) {
            return Result.fail("文件不存在");
        }
        Result<Boolean> authResult = remoteService.isAuthDepartment(departmentId);
        if (authResult.isFailed()) {
            return Result.fail(authResult.getMsg());
        }

        ImportantVoucherExcelListener listener = new ImportantVoucherExcelListener(departmentId);
        // 读取所有Sheet的数据.每次读完一个Sheet就会调用这个方法
        EasyExcel.read(file.getInputStream(), ImportantVoucherImportVO.class, listener).sheet(0).doRead();
        List<ImportantVoucherImportVO> datas = listener.getDatas();
        List<String> failedInfo = listener.getFailedInfo();

        // 如果存在错误，需要重新修改
        if (!failedInfo.isEmpty()) {
            return Result.fail(failedInfo.toString());
        }

        List<String> insertFailedMsg = new ArrayList<>();
        datas.forEach(importantVoucherImportVO -> {
            ImportantVoucherVO importantVoucherVO = new ImportantVoucherVO();
            BeanUtils.copyProperties(importantVoucherImportVO, importantVoucherVO);
            importantVoucherVO.setDepartmentId(departmentId);
            importantVoucherVO.setType(ImportantVoucherTypeEnum.getValueByText(importantVoucherImportVO.getImportantVoucherType()).shortValue());
            importantVoucherVO.setBankId(importantVoucherImportVO.getBankId());

            Result result = importantVoucherService.saveImportantVoucher(importantVoucherVO);
            if (result.isFailed()) {
                insertFailedMsg.add(result.getMsg());
            }
        });
        if (!insertFailedMsg.isEmpty()) {
            throw new Exception(insertFailedMsg.toString());
        }
        return Result.success();
    }

    class ImportantVoucherExcelListener extends AnalysisEventListener<ImportantVoucherImportVO> {
        //自定义用于暂时存储data。
        //可以通过实例获取该值
        private final List<ImportantVoucherImportVO> datas = new ArrayList<>();
        private final List<String> failedInfo = new ArrayList<>();
        private int counter = 1;
        private boolean legal = true;

        private Integer departmentId;

        public ImportantVoucherExcelListener(int departmentId) {
            this.departmentId = departmentId;
        }

        /**
         * 通过 AnalysisContext 对象还可以获取当前 sheet，当前行等数据
         */
        @Override
        public void invoke(ImportantVoucherImportVO importantVoucherImportVO, AnalysisContext context) {
            boolean b = checkDataLegality(importantVoucherImportVO);
            if (b) {
                datas.add(importantVoucherImportVO);
            }
            counter ++;
        }

        /**
         * 根据业务自行实现该方法
         */
        private boolean checkDataLegality(ImportantVoucherImportVO importantVoucherImportVO) {
            legal = true;
            // 所有属性去空格
            importantVoucherImportVO.cleanBlankCharacters();

            // 如果是空行，跳过
            if (importantVoucherImportVO.everyAtriEmpty()) {
                return false;
            }

            Result<Boolean> authResult = remoteService.isAuthDepartment(departmentId);
            if (authResult.isFailed()) {
                addFailedInfo(authResult.getMsg());
            }

            if (!ImportantVoucherTypeEnum.containValue(importantVoucherImportVO.getImportantVoucherType())) {
                addFailedInfo("重空类型选择错误：" + importantVoucherImportVO.getImportantVoucherType());
            }

            Bank bank = new Bank();
            bank.setFullName(importantVoucherImportVO.getBankName());
            bank.setDeleted(0);
            bank.setDepartmentId((long)departmentId);
            Result<Bank> bankResult = remoteService.findBankByCondition(bank);
            if (bankResult.isFailed()) {
                addFailedInfo(bankResult.getMsg());
                return false;
            }
            Bank bank1 = bankResult.getData();
            // 非顶层银行
            if (!"/0/".equals(bank1.getParentIds())) {
                addFailedInfo("目标银行不是顶层银行");
            }
            importantVoucherImportVO.setBankId(bank1.getId());

            ImportantVoucherVO importantVoucherVO = new ImportantVoucherVO();
            importantVoucherVO.setDepartmentId(departmentId);
            importantVoucherVO.setBankId(bank1.getId());

            return legal;
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
        /*
            datas.clear();
            解析结束销毁不用的资源
         */
        }

        public List<ImportantVoucherImportVO> getDatas() {
            return datas;
        }

        public List<String> getFailedInfo() {
            return failedInfo;
        }

        private void addFailedInfo(String subInfo) {
            String info = "第" + counter + "行数据错误：" + subInfo;
            failedInfo.add(info);
            legal = false;
        }
    }
}
