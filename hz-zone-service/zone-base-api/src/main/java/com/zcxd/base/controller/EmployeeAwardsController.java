package com.zcxd.base.controller;

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
import com.zcxd.base.service.EmployeeAwardsService;
import com.zcxd.base.vo.EmployeeAwardsVO;
import com.zcxd.common.annotation.OperateLog;
import com.zcxd.common.annotation.OperateType;
import com.zcxd.common.util.Result;
import com.zcxd.common.util.ResultList;
import com.zcxd.db.model.EmployeeAwards;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;

/**
 * 
 * @ClassName EmployeeAwardsController
 * @Description 员工奖惩记录前端控制器
 * @author 秦江南
 * @Date 2021年5月19日上午9:15:12
 */
@RestController
@RequestMapping("/awards")
@Api(tags = "员工奖惩记录")
public class EmployeeAwardsController {

	@Autowired
    private EmployeeAwardsService employeeAwardsService;


	@OperateLog(value="添加奖惩记录",type=OperateType.ADD)
    @ApiOperation(value = "添加奖惩记录")
    @ApiImplicitParam(name = "employeeAwardsVO", value = "奖惩记录", required = true, dataType = "EmployeeAwardsVO")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/save")
    public Result save(@RequestBody @Validated EmployeeAwardsVO employeeAwardsVO){
        EmployeeAwards awards = new EmployeeAwards();
        BeanUtils.copyProperties(employeeAwardsVO, awards);
        awards.setId(null);
        
        boolean save = employeeAwardsService.save(awards);
    	if(save)
    		return Result.success();
    	
    	return Result.fail("添加奖惩记录失败");
    }

	@OperateLog(value="删除奖惩记录",type=OperateType.DELETE)
    @ApiOperation(value = "删除奖惩记录")
    @ApiImplicitParam(name = "id", value = "奖惩记录ID", required = true, dataType = "Long")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/delete/{id}")
    public Result delete(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){
        EmployeeAwards awards = new EmployeeAwards();
        awards.setId(id);
        awards.setDeleted(1);
		boolean update = employeeAwardsService.updateById(awards);
		
		if(update)
    		return Result.success();
    	
    	return Result.fail("删除奖惩记录失败");
    }

	@OperateLog(value="修改奖惩记录",type=OperateType.EDIT)
    @ApiOperation(value = "修改奖惩记录")
    @ApiImplicitParam(name = "employeeAwardsVO", value = "奖惩记录", required = true, dataType = "EmployeeAwardsVO")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/update")
    public Result update(@RequestBody @Validated EmployeeAwardsVO employeeAwardsVO){
		if (employeeAwardsVO.getId() == null) {
            return Result.fail("奖惩记录Id不能为空");
        }
    	
		EmployeeAwards awards = new EmployeeAwards();
        BeanUtils.copyProperties(employeeAwardsVO, awards);
		boolean update = employeeAwardsService.updateById(awards);
    	if(update)
    		return Result.success();
    	
    	return Result.fail("修改奖惩记录失败");
    }

    @ApiOperation(value = "查询奖惩记录列表")
    @ApiImplicitParams({
    	@ApiImplicitParam(name = "page", value = "当前页数", required = true, dataType = "Integer ",defaultValue = "1"),
        @ApiImplicitParam(name = "limit", value = "显示数据条数", required = true, dataType = "Integer ",defaultValue = "10"),
        @ApiImplicitParam(name = "awardsType", value = "奖惩类型", required = false, dataType = "String"),
        @ApiImplicitParam(name = "empNo", value = "员工工号", required = false, dataType = "String"),
        @ApiImplicitParam(name = "empName", value = "员工名称", required = false, dataType = "String")        
    })
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @GetMapping("/list")
    public Result list(@RequestParam(defaultValue = "1") Integer page,
			   @RequestParam(defaultValue = "10") Integer limit,
			   @RequestParam(required=false) @Size(max=32,message="奖惩类型最大长度为32") String awardsType,
			   @RequestParam(required=false) @Size(max=32,message="员工工号最大长度为32") String empNo,
			   @RequestParam(required=false) @Size(max=32,message="员工姓名最大长度为32") String empName){
    	IPage<Map<String,Object>> findListByPage = employeeAwardsService.findListByPage(page, limit, awardsType, empNo, empName);
    	ResultList resultList = new ResultList.Builder().total(findListByPage.getTotal()).list(findListByPage.getRecords()).build();
        return Result.success(resultList);
    }
    
}
