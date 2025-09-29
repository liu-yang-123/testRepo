package com.zcxd.base.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

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

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zcxd.base.config.UserContextHolder;
import com.zcxd.base.service.EmployeeTrainingRecordService;
import com.zcxd.base.vo.EmployeeTrainingRecordBatch;
import com.zcxd.base.vo.EmployeeTrainingRecordBatchVO;
import com.zcxd.base.vo.EmployeeTrainingRecordVO;
import com.zcxd.common.annotation.OperateLog;
import com.zcxd.common.annotation.OperateType;
import com.zcxd.common.util.Result;
import com.zcxd.common.util.ResultList;
import com.zcxd.db.model.EmployeeTrainingRecord;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;

/**
 * 
 * @ClassName EmployeeTrainingRecordController
 * @Description 员工培训记录前端控制器
 * @author 秦江南
 * @Date 2021年5月18日下午3:15:08
 */
@RestController
@RequestMapping("/records")
@Api(tags = "员工培训记录")
public class EmployeeTrainingRecordController {

	@Autowired
    private EmployeeTrainingRecordService employeeTrainingRecordService;    

	@OperateLog(value="批量添加培训记录",type=OperateType.ADD)
    @ApiOperation(value = "批量添加培训记录")
    @ApiImplicitParam(name = "employeeTrainingRecordBatchVO", value = "培训记录数组", required = true, dataType = "EmployeeTrainingRecordBatchVO")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/savemulti")
    public Result savemulti(@RequestBody @Validated EmployeeTrainingRecordBatchVO employeeTrainingRecordBatchVO){
		List<EmployeeTrainingRecord> employeeTrainingRecords = new ArrayList<>();
		for (EmployeeTrainingRecordBatch employeeTrainingRecordBatch : employeeTrainingRecordBatchVO.getRecordDtoList()) {
			EmployeeTrainingRecord employeeTrainingRecord = new EmployeeTrainingRecord();
			employeeTrainingRecord.setTrainId(employeeTrainingRecordBatchVO.getTrainId());
			employeeTrainingRecord.setEmpId(employeeTrainingRecordBatch.getEmpId());
			employeeTrainingRecord.setScore(employeeTrainingRecordBatch.getScore());
			
			Long userId = UserContextHolder.getUserId();
			Long time = System.currentTimeMillis();
			employeeTrainingRecord.setCreateUser(userId);
			employeeTrainingRecord.setCreateTime(time);
			employeeTrainingRecord.setUpdateUser(userId);
			employeeTrainingRecord.setUpdateTime(time);
			
			employeeTrainingRecords.add(employeeTrainingRecord);
		}
		
         boolean savemulti = employeeTrainingRecordService.savemulti(employeeTrainingRecords);
         if(savemulti)
     		return Result.success();
     	
     	return Result.fail("批量添加培训记录失败");
    }

	@OperateLog(value="删除培训记录",type=OperateType.DELETE)
	@ApiOperation(value = "删除培训记录")
    @ApiImplicitParam(name = "id", value = "唯一ID", required = true, dataType = "Long")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/delete/{id}")
    public Result delete(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){
    	EmployeeTrainingRecord employeeTrainingRecord = new EmployeeTrainingRecord();
    	employeeTrainingRecord.setId(id);
    	employeeTrainingRecord.setDeleted(1);
    	boolean update = employeeTrainingRecordService.updateById(employeeTrainingRecord);
    	if(update)
    		return Result.success();
    	
    	return Result.fail("删除培训记录失败");
    }

	@OperateLog(value="修改培训记录",type=OperateType.EDIT)
    @ApiOperation(value = "修改培训记录")
    @ApiImplicitParam(name = "employeeTrainingRecordVO", value = "成绩信息", required = true, dataType = "EmployeeTrainingRecordVO")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/update")
    public Result update(@RequestBody @Validated EmployeeTrainingRecordVO employeeTrainingRecordVO){
    	
    	EmployeeTrainingRecord employeeTrainingRecord = new EmployeeTrainingRecord();
		BeanUtils.copyProperties(employeeTrainingRecordVO, employeeTrainingRecord);
		boolean update = employeeTrainingRecordService.updateById(employeeTrainingRecord);
    	if(update)
    		return Result.success();
    	
    	return Result.fail("修改培训记录失败");
    }

    @ApiOperation(value = "查询培训记录列表")
    @ApiImplicitParams({
    	@ApiImplicitParam(name = "page", value = "当前页数", required = true, dataType = "Integer ",defaultValue = "1"),
        @ApiImplicitParam(name = "limit", value = "显示数据条数", required = true, dataType = "Integer ",defaultValue = "10"),
        @ApiImplicitParam(name = "empNo", value = "员工工号", required = false, dataType = "String"),
        @ApiImplicitParam(name = "empName", value = "员工名称", required = false, dataType = "String"),
        @ApiImplicitParam(name = "trainTitle", value = "培训主题", required = false, dataType = "String")
    })
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @GetMapping("/list")
    public Result list(@RequestParam(defaultValue = "1") Integer page,
			   @RequestParam(defaultValue = "10") Integer limit,
               @RequestParam(required=false) @Size(max=32,message="员工工号最大长度为32") String empNo,
               @RequestParam(required=false) @Size(max=32,message="员工姓名最大长度为32") String empName,
               @RequestParam(required=false) @Size(max=32,message="培训主题最大长度为32") String trainTitle){
         IPage<Map<String,Object>> findListByPage = employeeTrainingRecordService.findListByPage(page, limit, empNo, empName, trainTitle);
         ResultList resultList = new ResultList.Builder().total(findListByPage.getTotal()).list(findListByPage.getRecords()).build();
         return Result.success(resultList);
    }

}
