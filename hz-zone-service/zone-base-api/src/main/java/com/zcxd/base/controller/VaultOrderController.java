package com.zcxd.base.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zcxd.base.config.UserContextHolder;
import com.zcxd.base.dto.RouteTaskDTO;
import com.zcxd.base.dto.VaultOrderDto;
import com.zcxd.base.dto.WorkflowRecordDTO;
import com.zcxd.base.service.*;
import com.zcxd.base.vo.*;
import com.zcxd.common.annotation.OperateLog;
import com.zcxd.common.annotation.OperateType;
import com.zcxd.common.constant.DenomTypeEnum;
import com.zcxd.common.constant.OrderStatusEnum;
import com.zcxd.common.util.Result;
import com.zcxd.common.util.ResultList;
import com.zcxd.db.model.*;
import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author songanwei
 * @date 2021-05-31
 */
@Api(tags = "金库-出入库管理")
@AllArgsConstructor
@RestController
@RequestMapping("/vaultOrder")
public class VaultOrderController {

    private VaultOrderService orderService;

    private DenomService denomService;

    private BankService bankService;

    private WorkflowRecordService recordService;

    private EmployeeService employeeService;

    @ApiOperation(value = "出入库记录列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页数", required = true, dataType = "Integer ",defaultValue = "1",dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "limit", value = "显示数据条数", required = true, dataType = "Integer ",defaultValue = "10", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "departmentId", value = "部门ID", required = true, dataType = "Long", defaultValue = "0", dataTypeClass = Long.class),
            @ApiImplicitParam(name = "orderDate", value = "查询日期", dataType = "Long", defaultValue = "0", dataTypeClass = Long.class),
            @ApiImplicitParam(name = "bankId", value = "银行机构ID", dataType = "Long", defaultValue = "0", dataTypeClass = Long.class),
            @ApiImplicitParam(name = "orderType", value = "类别", dataType = "Integer", defaultValue = "-1", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "subType", value = "类型", dataType = "Integer", defaultValue = "-1", dataTypeClass = Integer.class)
    })
    @GetMapping("/list")
    public Result list(@RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @RequestParam Long departmentId,
                       @RequestParam(name = "orderDate", defaultValue = "0") Long orderDate,
                       @RequestParam(name = "bankId",defaultValue = "0") Long bankId,
                       @RequestParam(name = "orderType", defaultValue = "-1") Integer orderType,
                       @RequestParam(name = "subType", defaultValue = "-1") Integer subType){
        VaultOrder vaultOrder = new VaultOrder();
        vaultOrder.setDepartmentId(departmentId);
        vaultOrder.setBankId(bankId);
        vaultOrder.setOrderDate(orderDate);
        vaultOrder.setOrderType(orderType);
        vaultOrder.setSubType(subType);
        IPage<VaultOrder> orderPage = orderService.findListByPage(page,limit,vaultOrder);

        //获取员工ID
        Set<Long> empIds = orderPage.getRecords().stream().map( t -> {
            HashSet<Long> set = new HashSet<>();
            set.add(t.getWhOpMan());
            set.add(t.getWhCheckMan());
            set.add(t.getWhConfirmMan());
            return set;
        }).flatMap(Collection::stream).filter(r -> r != 0).collect(Collectors.toSet());
        //查询员工表数据
        Map<Long,String> employeeMap = new HashMap<>();
        if (!empIds.isEmpty()){
            List<Employee> employeeList =  employeeService.listByIds(empIds);
            employeeMap = employeeList.stream().collect(Collectors.toMap(Employee::getId,Employee::getEmpName));
        }
        //当前用户ID
        long userId = UserContextHolder.getUserId();
        List<Bank> bankList = bankService.list();
        Map<Long,String> bankMap = bankList.stream().collect(Collectors.toMap(Bank::getId,Bank::getFullName));
        Map<Long, String> finalEmployeeMap = employeeMap;
        List<VaultOrderDto>  volumeDtoList = orderPage.getRecords().stream().map(t -> {
            VaultOrderDto vaultOrderDto = new VaultOrderDto();
            BeanUtils.copyProperties(t,vaultOrderDto);
            String userIds = t.getNextUserIds();
            if (StringUtils.isEmpty(userIds)){
                vaultOrderDto.setAudit(false);
            }else{
                OptionalLong optional = Arrays.stream(userIds.split(",")).mapToLong(Long::parseLong).filter(s -> s == userId).findFirst();
                vaultOrderDto.setAudit(optional.isPresent());
            }
            vaultOrderDto.setWhOpManName(Optional.ofNullable(finalEmployeeMap.get(t.getWhOpMan())).orElse(""));
            vaultOrderDto.setWhCheckManName(Optional.ofNullable(finalEmployeeMap.get(t.getWhCheckMan())).orElse(""));
            vaultOrderDto.setWhConfirmManName(Optional.ofNullable(finalEmployeeMap.get(t.getWhConfirmMan())).orElse(""));
            vaultOrderDto.setBankName(Optional.ofNullable(bankMap.get(t.getBankId())).orElse(""));
            return vaultOrderDto;
        }).collect(Collectors.toList());

        ResultList resultList = ResultList.builder().total(orderPage.getTotal()).list(volumeDtoList).build();
        return Result.success(resultList);
    }

    @ApiOperation(value = "创建入库单")
    @OperateLog(value = "创建入库单",type = OperateType.ADD)
    @PostMapping("/save")
    public Result save(@RequestBody @Validated VaultOrderVO vaultOrderVO){
       boolean b = orderService.create(vaultOrderVO);
       return b ? Result.success() : Result.fail("创建入库单失败");
    }

    @ApiOperation(value = "创建出库单")
    @OperateLog(value = "创建出库单",type = OperateType.ADD)
    @PostMapping("/outSave")
    public Result outSave(@RequestBody @Validated VaultOrderVO vaultOrderVO){
        boolean b = orderService.create(vaultOrderVO);
        return b ? Result.success() : Result.fail("创建出库单失败");
    }

    @ApiOperation(value = "快捷库单")
    @OperateLog(value = "快捷出库操作",type = OperateType.ADD)
    @PostMapping("/quickOut")
    public Result quickTaskOut(@RequestBody @Validated VaultOrderQuickVO quickVO){
        boolean b = orderService.quickOut(quickVO);
        return b ? Result.success() : Result.fail("添加失败");
    }

    @ApiOperation(value = "快捷库单")
    @OperateLog(value = "快捷出库操作",type = OperateType.ADD)
    @PostMapping("/quickCashOut")
    public Result quickCashOut(@RequestBody @Validated BatchIdsVO batchIdsVO){
        boolean b = orderService.quickCashOut(batchIdsVO);
        return b ? Result.success() : Result.fail("添加失败");
    }

    @ApiOperation(value = "编辑出入库单")
    @OperateLog(value = "编辑出入库单",type = OperateType.EDIT)
    @PostMapping("/update")
    public Result update(@RequestBody @Validated VaultOrderVO vaultOrderVO){
        boolean b = orderService.edit(vaultOrderVO);
        return b ? Result.success() : Result.fail("编辑失败");
    }

    @ApiOperation(value = "删除出入库单")
    @ApiImplicitParam(name = "id", value = "订单id", required = true, dataType = "Long", dataTypeClass = Long.class)
    @OperateLog(value = "删除出入库单",type = OperateType.DELETE)
    @PostMapping("/delete/{id}")
    public Result delete(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){

        //初始状态可删除
        VaultOrder order = orderService.getById(id);
        if (order == null || order.getStatusT() != 0){
          return Result.fail("该出入库单无法删除");
        }
        VaultOrder model = new VaultOrder();
        model.setId(id);
        model.setDeleted(1);
        boolean b = orderService.updateById(model);
        if (b){
            return Result.success();
        }
        return Result.fail("该出入库单删除失败");
    }

    @ApiOperation(value = "出入库单明细")
    @GetMapping("/detail")
    public Result detail(Long orderId){
        VaultOrder vaultOrder = orderService.getById(orderId);
        if (vaultOrder == null){
            return Result.fail("数据错误");
        }
       List<VaultOrderRecord> recordList = orderService.getDetail(orderId);

       List<Denom> denomList = denomService.list();
       Map<Long,String> denomMap = denomList.stream().collect(Collectors.toMap(Denom::getId,Denom::getName));

        LinkedList usableList = new LinkedList();
        LinkedList goodList = new LinkedList();
        LinkedList badList = new LinkedList();
        LinkedList unclearList = new LinkedList();
        LinkedList remnantList = new LinkedList();
       //数据分类
       recordList.stream().forEach(item -> {
           VaultOrderRecordVO recordVO = new VaultOrderRecordVO();
           BeanUtils.copyProperties(item,recordVO);
           recordVO.setDenomText(Optional.ofNullable(denomMap.get(item.getDenomId())).get() );
           if (item.getDenomType() == DenomTypeEnum.USABLE.getValue()){
               usableList.add(recordVO);
           }else if (item.getDenomType() == DenomTypeEnum.BAD.getValue()){
               badList.add(recordVO);
           }else if (item.getDenomType() == DenomTypeEnum.GOOD.getValue()){
               goodList.add(recordVO);
           } else if (item.getDenomType() == DenomTypeEnum.UNCLEAR.getValue()){
               unclearList.add(recordVO);
           } else if (item.getDenomType() == DenomTypeEnum.REMNANT.getValue()){
               remnantList.add(recordVO);
           }
       });

        HashMap map = new HashMap(4);
        map.put("usable",usableList);
        map.put("bad",badList);
        map.put("good",goodList);
        map.put("unclear",unclearList);
        map.put("remnant",remnantList);

        //ATM加钞详情
        if (vaultOrder.getSubType() == 0){
            List<RouteTaskDTO> taskDTOList = orderService.getAtmTask(orderId);
            map.put("routeList", taskDTOList);
        }
        //根据出入库类别判断
        String typeEvent = vaultOrder.getOrderType() == 0 ? "STORAGE_IN" : "STORAGE_IN_OUT";
        List<WorkflowRecordDTO> recordDTOList = recordService.getDetailList(orderId,typeEvent, vaultOrder.getDepartmentId());
        List<WorkflowRecordDTO> undoList = recordService.getDetailList(orderId,"STORAGE_CANCEL", vaultOrder.getDepartmentId());
        map.put("audit",recordDTOList);
        map.put("undo", undoList);
       return Result.success(map);
    }

    @ApiOperation(value = "提交审核出入库单")
    @ApiImplicitParam(name = "id", value = "订单id", required = true, dataType = "Long", dataTypeClass = Long.class)
    @OperateLog(value = "提交审核出入库单",type = OperateType.EDIT)
    @PostMapping("/submitAudit/{id}")
    public Result submitAudit(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){
        //初始状态可提交审核
        VaultOrder order = orderService.getById(id);
        if (order == null || order.getStatusT() != 0){
            return Result.fail("该出入库单无法提交审核");
        }
        //根据出入库类别判断
        String typeEvent = order.getOrderType() == 0 ? "STORAGE_IN" : "STORAGE_IN_OUT";
        String userIds = recordService.getUserIds(0,typeEvent, order.getDepartmentId());
        VaultOrder model = new VaultOrder();
        model.setId(id);
        model.setStatusT(1);
        model.setNextUserIds(userIds);
        boolean b = orderService.updateById(model);
        if (b){
            return Result.success("该出入库单已提交，等待审核");
        }
        return Result.fail("该出入库订单提交审核失败");
    }

    @ApiOperation(value = "审核出入库单")
    @ApiImplicitParam(name = "auditVO", value = "审核视图对象", required = true, dataType = "AuditVO", dataTypeClass = AuditVO.class)
    @OperateLog(value = "审核出入库单",type = OperateType.EDIT)
    @PostMapping("/audit")
    public Result audit(@RequestBody @Validated AuditVO auditVO){
        boolean b = orderService.audit(auditVO);
        return b ? Result.success(0, "审核成功") : Result.fail("审核失败");
    }

    @ApiOperation(value = "撤销出入库单")
    @ApiImplicitParam(name = "id", value = "订单id", required = true, dataType = "Long", dataTypeClass = Long.class)
    @OperateLog(value = "撤销出入库单",type = OperateType.DELETE)
    @PostMapping("/undo/{id}")
    public Result undo(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){
        //初始状态可删除
        VaultOrder order = orderService.getById(id);
        if (order == null
                || !(order.getStatusT() == OrderStatusEnum.CHECK_APPROVE.getValue()
                  || order.getStatusT() == OrderStatusEnum.FINISH.getValue())){
            return Result.fail("该出入库单无法撤销");
        }
        String userIds = recordService.getUserIds(0,"STORAGE_CANCEL", order.getDepartmentId());
        VaultOrder model = new VaultOrder();
        model.setId(id);
        model.setStatusT(5);
        model.setNextUserIds(userIds);
        boolean b = orderService.updateById(model);
        if (b){
            return Result.success();
        }
        return Result.fail("该出入库单撤销失败");
    }

}
