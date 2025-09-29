package com.zcxd.base.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zcxd.base.config.UserContextHolder;
import com.zcxd.base.dto.*;
import com.zcxd.base.service.*;
import com.zcxd.base.vo.AuditVO;
import com.zcxd.common.annotation.OperateLog;
import com.zcxd.common.annotation.OperateType;
import com.zcxd.common.util.Result;
import com.zcxd.common.util.ResultList;
import com.zcxd.db.model.*;
import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author songanwei
 * @date 2021-07-20
 */
@Api(tags = "清分审批任务")
@AllArgsConstructor
@RestController
@RequestMapping("/clearTaskAudit")
public class ClearTaskAuditController {

    private AtmClearTaskAuditService taskAuditService;

    private BankService bankService;

    private ATMService atmService;

    private RouteService routeService;

    private EmployeeService employeeService;

    private WorkflowRecordService recordService;

    @ApiOperation(value = "清分任务审核记录列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页数", required = true, dataType = "Integer ",defaultValue = "1", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "limit", value = "显示数据条数", required = true, dataType = "Integer ",defaultValue = "10", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "departmentId", value = "所在部门", required = true, dataType = "Long ",defaultValue = "0", dataTypeClass = Long.class),
            @ApiImplicitParam(name = "taskDate", value = "任务日期", dataType = "String", defaultValue = "", dataTypeClass = String.class),
            @ApiImplicitParam(name = "bankId", value = "银行机构ID", dataType = "Long", defaultValue = "0", dataTypeClass = Long.class),
            @ApiImplicitParam(name = "routeId", value = "线路ID", dataType = "Long", defaultValue = "0", dataTypeClass = Long.class),
            @ApiImplicitParam(name = "statusT", value = "审核状态", dataType = "Integer", defaultValue = "-1", dataTypeClass = Integer.class)
    })
    @GetMapping("/list")
    public Result list(@RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @RequestParam(defaultValue = "") String taskDate,
                       @RequestParam(defaultValue = "-1") Integer statusT,
                       @RequestParam(defaultValue = "0") Long routeId,
                       @RequestParam(defaultValue = "0") Long departmentId,
                       @RequestParam(defaultValue = "0") Long bankId){
        AtmClearTaskAudit clearTask = new AtmClearTaskAudit();
        clearTask.setDepartmentId(departmentId);
        clearTask.setBankId(bankId);
        clearTask.setRouteId(routeId);
        clearTask.setStatusT(statusT);
        clearTask.setTaskDate(taskDate);

        IPage<AtmClearTaskAudit> auditPage = taskAuditService.findListByPage(page,limit,clearTask);
        Set<Long> bankIds = auditPage.getRecords().stream().map(AtmClearTaskAudit::getBankId).collect(Collectors.toSet());
        Set<Long> atmIds = auditPage.getRecords().stream().map(AtmClearTaskAudit::getAtmId).collect(Collectors.toSet());
        Set<Long> routeIds = auditPage.getRecords().stream().map(AtmClearTaskAudit::getRouteId).collect(Collectors.toSet());
        Set<Long> empIds = auditPage.getRecords().stream().map(t -> {
            HashSet<Long> set = new HashSet<>(2);
            set.add(t.getCheckMan());
            set.add(t.getClearMan());
            return set;
        }).flatMap(Collection::stream).filter(p -> p != 0).collect(Collectors.toSet());

        Map<Long,String> bankMap = new HashMap<>();
        if (bankIds.size() > 0){
            List<Bank> bankList = bankService.listByIds(bankIds);
            bankMap = bankList.stream().collect(Collectors.toMap(Bank::getId,Bank::getFullName));
        }
        Map<Long,String> atmMap = new HashMap<>();
        if (atmIds.size() > 0){
            List<AtmDevice> atmList = atmService.listByIds(atmIds);
            atmMap = atmList.stream().collect(Collectors.toMap(AtmDevice::getId,AtmDevice::getTerNo));
        }
        //清点员、复点员
        Map<Long,String> empMap = new HashMap<>();
        if (empIds.size() > 0){
            List<Employee> employeeList = employeeService.listByIds(empIds);
            empMap = employeeList.stream().collect(Collectors.toMap(Employee::getId,s -> s.getEmpNo() +"/" +s.getEmpName()));
        }
        //计算线路ID
        Map<Long,String> routeMap = new HashMap<>();
        if (routeIds.size() > 0){
            List<Route> routeList = routeService.listByIds(routeIds);
            routeMap = routeList.stream().collect(Collectors.toMap(Route::getId, r-> r.getRouteName() + "/" + r.getRouteNo()));
        }

        Map<Long, String> finalBankMap = bankMap;
        Map<Long, String> finalAtmMap = atmMap;
        Map<Long, String> finalRouteMap = routeMap;
        Map<Long, String> finalEmpMap = empMap;
        //当前用户ID
        long userId = UserContextHolder.getUserId();
        List<AtmClearTaskDTO> clearTaskDTOList = auditPage.getRecords().stream().map(item -> {
            AtmClearTaskDTO clearTaskDTO = new AtmClearTaskDTO();
            BeanUtils.copyProperties(item,clearTaskDTO);
            clearTaskDTO.setRouteText(Optional.ofNullable(finalRouteMap.get(item.getRouteId())).orElse(""));
            clearTaskDTO.setBankName(Optional.ofNullable(finalBankMap.get(item.getBankId())).orElse(""));
            clearTaskDTO.setTerNo(Optional.ofNullable(finalAtmMap.get(item.getAtmId())).orElse(""));
            clearTaskDTO.setClearManName(Optional.ofNullable(finalEmpMap.get(item.getClearMan())).orElse(""));
            clearTaskDTO.setCheckManName(Optional.ofNullable(finalEmpMap.get(item.getCheckMan())).orElse(""));
             //获取下一个审核用户
            String userIds = recordService.getUserIds(item.getId(),"ATM_CLEAR_TASK", item.getDepartmentId());
            if (StringUtils.isEmpty(userIds)){
                clearTaskDTO.setAudit(false);
            }else{
                OptionalLong optional = Arrays.stream(userIds.split(",")).mapToLong(Long::parseLong).filter(s -> s == userId).findFirst();
                clearTaskDTO.setAudit(optional.isPresent());
            }
            return clearTaskDTO;
        }).collect(Collectors.toList());
        ResultList resultList = ResultList.builder().total(auditPage.getTotal()).list(clearTaskDTOList).build();
        return Result.success(resultList);
    }

    @ApiOperation(value = "审核清分任务")
    @ApiImplicitParam(name = "auditVO", value = "审核视图对象", required = true, dataType = "AuditVO", dataTypeClass = AuditVO.class)
    @OperateLog(value = "审核清分任务",type = OperateType.EDIT)
    @PostMapping("/audit")
    public Result audit(@RequestBody @Validated AuditVO auditVO){
        boolean b = taskAuditService.audit(auditVO);
        return b ? Result.success(0, "审核成功") : Result.fail("审核失败");
    }

    @ApiOperation(value = "清分审核记录")
    @GetMapping("/auditHistory")
    public Result auditHistory(Long auditId){
        AtmClearTaskAudit taskAudit = taskAuditService.getById(auditId);
        List<WorkflowRecordDTO> recordDTOList = recordService.getDetailList(auditId,"ATM_CLEAR_TASK", taskAudit.getDepartmentId());
        return Result.success(recordDTOList);
    }

}
