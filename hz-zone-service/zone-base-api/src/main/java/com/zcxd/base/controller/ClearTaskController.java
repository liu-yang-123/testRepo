package com.zcxd.base.controller;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Min;

import com.zcxd.base.vo.ClearTaskFinishVO;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zcxd.base.dto.ATMDTO;
import com.zcxd.base.dto.AtmClearBankDTO;
import com.zcxd.base.dto.AtmClearRouteDTO;
import com.zcxd.base.dto.AtmClearTaskDTO;
import com.zcxd.base.service.ATMService;
import com.zcxd.base.service.AtmClearTaskAuditService;
import com.zcxd.base.service.AtmClearTaskService;
import com.zcxd.base.service.BankService;
import com.zcxd.base.service.EmployeeService;
import com.zcxd.base.service.RouteService;
import com.zcxd.base.vo.ATMTaskClearImportSaveVO;
import com.zcxd.base.vo.ClearTaskCreateVO;
import com.zcxd.base.vo.ClearTaskVO;
import com.zcxd.common.annotation.OperateLog;
import com.zcxd.common.annotation.OperateType;
import com.zcxd.common.util.Result;
import com.zcxd.common.util.ResultList;
import com.zcxd.db.model.AtmClearTask;
import com.zcxd.db.model.AtmDevice;
import com.zcxd.db.model.Bank;
import com.zcxd.db.model.Employee;
import com.zcxd.db.model.Route;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;

/**
 * 清分任务
 * @author songanwei
 * @date 2021-06-07
 */
@Api(tags = "清分任务")
@AllArgsConstructor
@RestController
@RequestMapping("/clearTask")
public class ClearTaskController {

    private AtmClearTaskService clearTaskService;

    private AtmClearTaskAuditService taskAuditService;

    private BankService bankService;

    private ATMService atmService;

    private RouteService routeService;

    private EmployeeService employeeService;

    @ApiOperation(value = "清分任务记录列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页数", required = true, dataType = "Integer ",defaultValue = "1", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "limit", value = "显示数据条数", required = true, dataType = "Integer ",defaultValue = "10", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "departmentId", value = "所在部门", required = true, dataType = "Long ",defaultValue = "0", dataTypeClass = Long.class),
            @ApiImplicitParam(name = "taskDate", value = "任务日期", dataType = "String", defaultValue = "", dataTypeClass = String.class),
            @ApiImplicitParam(name = "bankId", value = "银行机构ID", dataType = "Long", defaultValue = "0", dataTypeClass = Long.class),
            @ApiImplicitParam(name = "routeId", value = "线路ID", dataType = "Long", defaultValue = "0", dataTypeClass = Long.class),
            @ApiImplicitParam(name = "statusT", value = "清分状态", dataType = "Integer", defaultValue = "-1", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "errorType", value = "差错类型", dataType = "Integer", defaultValue = "-1", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "clearManId", value = "清点人", dataType = "Long", defaultValue = "0", dataTypeClass = Long.class)
    })
    @GetMapping("/list")
    public Result list(@RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @RequestParam(defaultValue = "") String taskDate,
                       @RequestParam(defaultValue = "-1") Integer statusT,
                       @RequestParam(defaultValue = "0") Long routeId,
                       @RequestParam(defaultValue = "0") Long departmentId,
                       @RequestParam(defaultValue = "0") Long bankId,
                       @RequestParam(defaultValue = "-1") Integer errorType,
                       @RequestParam(defaultValue = "0")  Long clearManId){
        AtmClearTask clearTask = new AtmClearTask();
        clearTask.setDepartmentId(departmentId);
        clearTask.setBankId(bankId);
        clearTask.setRouteId(routeId);
        clearTask.setErrorType(errorType);
        clearTask.setStatusT(statusT);
        clearTask.setTaskDate(taskDate);
        clearTask.setClearMan(clearManId);
        
        IPage<AtmClearTask> taskPage = clearTaskService.findListByPage(page,limit,clearTask);
        Set<Long> bankIds = taskPage.getRecords().stream().map(AtmClearTask::getBankId).collect(Collectors.toSet());
        Set<Long> atmIds = taskPage.getRecords().stream().map(AtmClearTask::getAtmId).collect(Collectors.toSet());
        Set<Long> routeIds = taskPage.getRecords().stream().map(AtmClearTask::getRouteId).collect(Collectors.toSet());
        Set<Long> empIds = taskPage.getRecords().stream().flatMap(t -> Stream.of(t.getCheckMan(),t.getClearMan())).filter(p -> p != 0).collect(Collectors.toSet());

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
        List<AtmClearTaskDTO> clearTaskDTOList = taskPage.getRecords().stream().map(item -> {
            AtmClearTaskDTO clearTaskDTO = new AtmClearTaskDTO();
            BeanUtils.copyProperties(item,clearTaskDTO);
            clearTaskDTO.setRouteText(Optional.ofNullable(finalRouteMap.get(item.getRouteId())).orElse(""));
            clearTaskDTO.setBankName(Optional.ofNullable(finalBankMap.get(item.getBankId())).orElse(""));
            clearTaskDTO.setTerNo(Optional.ofNullable(finalAtmMap.get(item.getAtmId())).orElse(""));
            clearTaskDTO.setClearManName(Optional.ofNullable(finalEmpMap.get(item.getClearMan())).orElse(""));
            clearTaskDTO.setCheckManName(Optional.ofNullable(finalEmpMap.get(item.getCheckMan())).orElse(""));
            return clearTaskDTO;
        }).collect(Collectors.toList());
        ResultList resultList = new ResultList.Builder<>().total(taskPage.getTotal()).list(clearTaskDTOList).build();
        return Result.success(resultList);
    }

    @OperateLog(value = "添加ATM清分任务", type=OperateType.ADD)
    @ApiOperation(value = "添加ATM清分任务")
    @PostMapping("/save")
    public Result saveAll(@RequestBody @Validated ClearTaskCreateVO clearTaskCreateVO){
        boolean b = clearTaskService.create(clearTaskCreateVO);
        return b ? Result.success("添加成功") : Result.fail("添加失败");
    }

    @OperateLog(value = "修改ATM清分任务", type=OperateType.EDIT)
    @ApiOperation(value = "修改ATM清分任务")
    @PostMapping("/update")
    public Result update(@RequestBody @Validated ClearTaskVO clearTaskVO){
        AtmClearTask task = clearTaskService.getById(clearTaskVO.getId());
        if (task.getStatusT() == 0){
            AtmClearTask clearTask = new AtmClearTask();
            clearTask.setId(clearTaskVO.getId());
            clearTask.setPlanAmount(clearTaskVO.getPlanAmount());
            boolean b = clearTaskService.updateById(clearTask);
            return b ? Result.success("编辑成功") : Result.fail("编辑失败");
        }else{
            boolean b = taskAuditService.create(clearTaskVO);
            return b ? Result.success(null,"操作成功,编辑数据等待审核....") : Result.fail("编辑失败");
        }
    }

    @OperateLog(value = "删除清分任务", type= OperateType.DELETE)
    @ApiOperation(value = "删除清分任务")
    @ApiImplicitParam(name = "id", value = "ATM清分任务id", required = true, dataType = "Long", dataTypeClass = Long.class)
    @PostMapping("/delete/{id}")
    public Result delete(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){
        AtmClearTask clearTask = new AtmClearTask();
        clearTask.setId(id);
        clearTask.setDeleted(1);
        boolean update = clearTaskService.updateById(clearTask);
        if(update) {
            return Result.success();
        }
        return Result.fail("删除ATM清分任务失败");
    }

    @ApiOperation(value = "查询ATM清分线路")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "departmentId", value = "所在部门", required = true, dataType = "Long ",defaultValue = "0", dataTypeClass = Long.class),
            @ApiImplicitParam(name = "routeId", value = "线路ID", required = true, dataType = "Long ",defaultValue = "0", dataTypeClass = Long.class),
            @ApiImplicitParam(name = "taskDate", value = "任务日期", dataType = "String", defaultValue = "", dataTypeClass = String.class)
    })
    @GetMapping("/route")
    public Result route(@RequestParam Long departmentId, @RequestParam String taskDate, Long routeId){
        List<AtmClearRouteDTO>  routeDTOList = clearTaskService.getRoutes(departmentId, taskDate, routeId);
        ResultList  resultLists = ResultList.builder().total(0).list(routeDTOList).build();
        return Result.success(resultLists);
    }

    @ApiOperation(value = "查询ATM清分设备列表")
    @GetMapping("/atmList")
    public Result atmList(Long routeId){
        List atmIdList = clearTaskService.getAtmList(routeId);
        List<ATMDTO> atmdtoList = atmService.findListByIds(atmIdList);
        //查询
        HashMap map = new HashMap(2);
        map.put("atmId", atmIdList);
        map.put("atmList",atmdtoList);
        return Result.success(map);
    }

    @ApiOperation(value = "查询ATM清分银行")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "departmentId", value = "所在部门", required = true, dataType = "Long ",defaultValue = "0", dataTypeClass = Long.class),
            @ApiImplicitParam(name = "bankId", value = "所在部门",  dataType = "Long",defaultValue = "0", dataTypeClass = Long.class),
            @ApiImplicitParam(name = "taskDate", value = "任务日期", required = true, dataType = "String", defaultValue = "", dataTypeClass = String.class)
    })
    @GetMapping("/bank")
    public Result bank(@RequestParam Long departmentId, @RequestParam String taskDate, Long bankId){
        List<AtmClearBankDTO> bankDTOList = clearTaskService.getBanks(departmentId, taskDate, bankId);
        ResultList resultLists = new ResultList.Builder<>().total(0).list(bankDTOList).build();
        return Result.success(resultLists);
    }

    @OperateLog(value = "导入清分任务", type=OperateType.ADD)
    @ApiOperation(value = "导入清分任务")
    @ApiImplicitParams({
	    @ApiImplicitParam(name = "file", value = "文件", required = true, dataType = "MultipartFile"),
	    @ApiImplicitParam(name = "bankType", value = "银行类型", required = true, dataType = "Integer"),
	    @ApiImplicitParam(name = "taskDate", value = "任务日期", required = true, dataType = "Long")
    })
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/importClearTask")
    public Result importAtmTask(MultipartFile file,@RequestParam Integer bankType,@RequestParam Long taskDate) {
    	if(file == null){
    		return Result.fail("文件不存在");
    	}
    	return clearTaskService.clearExcelImport(file, bankType,taskDate);
    }
    
    @OperateLog(value = "导入添加清分任务", type=OperateType.ADD)
    @ApiOperation(value = "导入添加清分任务")
    @ApiImplicitParam(name = "atmTaskClearImportSaveVO", value = "导入ATM清机任务", required = true, dataType = "ATMTaskClearImportSaveVO")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/saveImportClear")
    public Result saveImportClear(@RequestBody @Validated ATMTaskClearImportSaveVO atmTaskClearImportSaveVO){
    	return clearTaskService.saveImportClear(atmTaskClearImportSaveVO);
    }

    @ApiOperation(value = "清分任务详情数据")
    @ApiImplicitParam(name = "taskId", value = "ATM清分任务id", required = true, dataType = "Long", dataTypeClass = Long.class)
    @GetMapping("/detail")
    public Result taskDetail(Long taskId){
        ClearTaskVO taskVO = clearTaskService.getDetail(taskId);
        return Result.success(taskVO);
    }

    /**
     * 完成任务
     * @param clearTaskFinishVO
     * @return
     */
    @OperateLog(value = "完成ATM清分任务", type=OperateType.EDIT)
    @ApiOperation(value = "完成ATM清分任务")
    @PostMapping("/finish")
    public Result finish(@RequestBody @Validated ClearTaskFinishVO clearTaskFinishVO){
        boolean bRet = clearTaskService.finish(clearTaskFinishVO);
        return bRet? Result.success() : Result.fail("操作失败");
    }
}
