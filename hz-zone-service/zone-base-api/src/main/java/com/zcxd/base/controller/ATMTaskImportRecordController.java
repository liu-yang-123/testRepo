package com.zcxd.base.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Min;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zcxd.base.dto.EmployeeDTO;
import com.zcxd.base.service.ATMTaskImportRecordService;
import com.zcxd.base.service.ATMTaskService;
import com.zcxd.base.service.AtmClearTaskService;
import com.zcxd.base.service.RouteService;
import com.zcxd.base.vo.ExcelImportAtmVO;
import com.zcxd.common.annotation.OperateLog;
import com.zcxd.common.annotation.OperateType;
import com.zcxd.common.constant.AtmClearTaskStatusEnum;
import com.zcxd.common.constant.Constant;
import com.zcxd.common.constant.RouteStatusEnum;
import com.zcxd.common.util.DateTimeUtil;
import com.zcxd.common.util.Result;
import com.zcxd.common.util.ResultList;
import com.zcxd.db.model.AtmClearTask;
import com.zcxd.db.model.AtmTaskImportRecord;
import com.zcxd.db.model.Route;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;

/**
 * 
 * @ClassName ATMTaskImportRecordController
 * @Description 任务导入记录控制器
 * @author 秦江南
 * @Date 2021年8月4日下午6:15:17
 */
@RestController
@RequestMapping("/taskImportRecord")
@Api(tags = "任务导入记录")
public class ATMTaskImportRecordController {
	
    @Autowired
    private AtmClearTaskService clearTaskService;
    
	@Autowired
    private ATMTaskImportRecordService atmTaskImportRecordService;
	
	@Autowired
	private ATMTaskService atmTaskService;
	
    @Autowired
    private RouteService routeService;
    
    @Value("${importType.hzicbc}")
    private int hzicbc;
    @Value("${importType.rcb}")
    private int rcb;
    @Value("${importType.bob}")
    private int bob;
    @Value("${importType.jdicbc}")
    private int jdicbc;
    
    @ApiOperation(value = "查询任务导入记录")
    @ApiImplicitParams({
    	@ApiImplicitParam(name = "page", value = "当前页数", required = true, dataType = "Integer ",defaultValue = "1"),
        @ApiImplicitParam(name = "limit", value = "显示数据条数", required = true, dataType = "Integer ",defaultValue = "10"),
        @ApiImplicitParam(name = "departmentId", value = "部门id", required = true, dataType = "Long"),
        @ApiImplicitParam(name = "importType", value = "导入类型", required = true, dataType = "Integer"),
        @ApiImplicitParam(name = "taskDate", value = "任务日期", required = false, dataType = "Long"),
        @ApiImplicitParam(name = "bankType", value = "银行类型", required = false, dataType = "Integer")        
    })
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @GetMapping({"/taskList","/clearList"})
    public Result list(@RequestParam(defaultValue = "1") Integer page,
			   @RequestParam(defaultValue = "10") Integer limit,
			   @RequestParam Integer importType,
			   @RequestParam Long departmentId,
			   @RequestParam(required=false) Long taskDate,
			   @RequestParam(required=false) Integer bankType){
    	AtmTaskImportRecord atmTaskImportRecord = new AtmTaskImportRecord();
    	atmTaskImportRecord.setDepartmentId(departmentId);
    	atmTaskImportRecord.setImportType(importType);
    	atmTaskImportRecord.setTaskDate(taskDate);
    	atmTaskImportRecord.setBankType(bankType);
    	
    	IPage<AtmTaskImportRecord> findListByPage = atmTaskImportRecordService.findListByPage(page, limit, atmTaskImportRecord);
    	List<AtmTaskImportRecord> taskImportRecorList =  findListByPage.getRecords().stream().map(atmTaskImportRecordTmp -> {
    		AtmTaskImportRecord importRecord = new AtmTaskImportRecord();
    		BeanUtils.copyProperties(atmTaskImportRecordTmp, importRecord);
//    		switch (atmTaskImportRecordTmp.getBankType()) {
//				case Constant.BOBID:
//					importRecord.setBankType(1);
//					break;
//				case Constant.HZICBCID:
//				case Constant.JDICBCID:
//					importRecord.setBankType(2);
//					break;
//				case Constant.RCBID:
//					importRecord.setBankType(3);
//					break;
//				default:
//					importRecord.setBankType(0);
//					break;
//    		}
    		
        	if(atmTaskImportRecordTmp.getBankType().equals(bob)){
        		importRecord.setBankType(1);
        	}else if(atmTaskImportRecordTmp.getBankType().equals(hzicbc) || atmTaskImportRecordTmp.getBankType().equals(jdicbc)){
        		importRecord.setBankType(2);
        	}else if(atmTaskImportRecordTmp.getBankType().equals(rcb)){
        		importRecord.setBankType(3);
        	}else{
        		importRecord.setBankType(0);
        	}
        	return importRecord;
        }).collect(Collectors.toList());
    	ResultList resultList = new ResultList.Builder().total(findListByPage.getTotal()).list(taskImportRecorList).build();
        return Result.success(resultList);
    }
    
    @OperateLog(value = "删除任务导入记录", type=OperateType.DELETE)
    @ApiOperation(value = "删除任务导入记录")
    @ApiImplicitParam(name = "id", value = "导入记录id", required = true, dataType = "Long")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping({"/taskDelete/{id}","/clearDelete/{id}"})
    public Result delete(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){
    	AtmTaskImportRecord atmTaskImportRecord = atmTaskImportRecordService.getById(id);
    	if(atmTaskImportRecord.getDeleted()!=0){
    		return Result.fail("导入记录为已删除状态，不能重复删除");
    	}
    	
    	if(atmTaskImportRecord.getImportType() == 0){
			List<Map<String, Object>> atmTaskList = atmTaskService.
					listMaps((QueryWrapper)Wrappers.query().select("route_id as routeId").eq("import_batch", id).eq("deleted", 0));
			List<Route> routeList = null;
			if(atmTaskList != null && atmTaskList.size() > 0){
				Set<Object> routeIds = atmTaskList.stream().map(t->t.get("routeId")).collect(Collectors.toSet());
				routeList = routeService.list((QueryWrapper)Wrappers.query().in("id", routeIds).gt("status_t", RouteStatusEnum.CHECKED.getValue()).eq("deleted", 0));
			}
			
			if(routeList != null && routeList.size()>0 ){
				return Result.fail("该批次加钞任务线路已经开始执行，无法删除");
			}
	    	
	    	return atmTaskImportRecordService.deleteCleanRecord(id);
    	}else{
    		List<AtmClearTask> clearTaskList = clearTaskService.list((QueryWrapper)Wrappers.query().eq("import_batch", id)
					.eq("status_t", AtmClearTaskStatusEnum.FINISH.getValue()));
			
			if(clearTaskList != null && clearTaskList.size()>0 ){
				return Result.fail("该批次清分任务中有任务已完成，不能删除");
			}
	    	
	    	return atmTaskImportRecordService.deleteClearRecord(id,atmTaskImportRecord.getSystemFileName());
    	}
    }
    
    
    @OperateLog(value = "导出清分任务")
    @ApiOperation(value = "导出清分任务")
    @ApiImplicitParam(name = "id", value = "导入记录id", required = true, dataType = "Long")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @GetMapping("/exportClearTask/{id}")
    public void exportClearTask(HttpServletResponse response,@PathVariable("id") @Min(value=1, message="id不能小于1") Long id) {
    	atmTaskImportRecordService.exportClearTask(response, id);
    }
    
}
