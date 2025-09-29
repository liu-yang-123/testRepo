package com.zcxd.base.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zcxd.base.dto.EmployeeJobDTO;
import com.zcxd.base.service.EmployeeJobService;
import com.zcxd.base.service.EmployeeService;
import com.zcxd.base.vo.EmployeeJobVO;
import com.zcxd.common.annotation.OperateLog;
import com.zcxd.common.annotation.OperateType;
import com.zcxd.common.constant.JobTypeEnum;
import com.zcxd.common.util.Result;
import com.zcxd.common.util.ResultList;
import com.zcxd.db.model.Employee;
import com.zcxd.db.model.EmployeeJob;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;

/**
 * 
 * @ClassName EmployeeJobController
 * @Description 岗位管理控制器
 * @author 秦江南
 * @Date 2021年5月13日下午4:32:18
 */
@RestController
@RequestMapping("/jobs")
@Api(tags = "岗位管理")
public class EmployeeJobController {

	@Autowired
    private EmployeeService employeeService;
	
	@Autowired
    private EmployeeJobService employeeJobService;

    @OperateLog(value="添加岗位",type=OperateType.ADD)
    @ApiOperation(value = "添加岗位")
    @ApiImplicitParam(name = "employeeJobVO", value = "岗位信息", required = true, dataType = "EmployeeJobVO")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/save")
    public Result save(@RequestBody @Validated EmployeeJobVO employeeJobVO){
    	EmployeeJob employeeJob = new EmployeeJob();
    	if(employeeJobVO != null && employeeJobVO.getJobType() != null && !employeeJobVO.getJobType().equals(JobTypeEnum.OTHER.getValue())){
	    	employeeJob.setJobType(employeeJobVO.getJobType());
			List<EmployeeJob> employeeJobList = employeeJobService.getEmployeeJobByCondition(employeeJob);
			if(employeeJobList != null && employeeJobList.size()>0)
				return Result.fail("该岗位类型已存在，请重新填写！");
    	}
		
    	employeeJob.setName(employeeJobVO.getName());
		employeeJob.setDescript(employeeJobVO.getDescript());
		employeeJob.setJobType(employeeJobVO.getJobType());
		
		boolean save = employeeJobService.save(employeeJob);
    	if(save)
    		return Result.success();
    	
    	return Result.fail("添加岗位失败");
    }

    @OperateLog(value="删除岗位",type=OperateType.DELETE)
    @ApiOperation(value = "删除岗位")
    @ApiImplicitParam(name = "id", value = "岗位id", required = true, dataType = "Long")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/delete/{id}")
    public Result delete(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){
		Employee employee = new Employee();
		employee.setJobIds(id);
    	List<Employee> employeeByCondition = employeeService.getEmployeeByCondition(employee);
    	if(employeeByCondition.size() > 0){
    		return Result.fail("岗位存在员工，不能删除");
    	}
    	
    	EmployeeJob employeeJob = new EmployeeJob();
    	employeeJob.setId(id);
    	employeeJob.setDeleted(1);
		boolean update = employeeJobService.updateById(employeeJob);
		
		if(update)
    		return Result.success();
    	
    	return Result.fail("删除岗位失败");
    }

    @OperateLog(value="修改岗位",type=OperateType.EDIT)
    @ApiOperation(value = "修改岗位")
    @ApiImplicitParam(name = "employeeJobVO", value = "岗位信息", required = true, dataType = "EmployeeJobVO")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/update")
    public Result update(@RequestBody @Validated EmployeeJobVO employeeJobVO){
        if (employeeJobVO.getId() == null) {
            return Result.fail("岗位Id不能为空");
        }
        
        EmployeeJob employeeJobTmp = employeeJobService.getById(employeeJobVO.getId());
        
    	EmployeeJob employeeJob = new EmployeeJob();
    	
    	boolean flag = false;
    	
        if(!employeeJobTmp.getJobType().equals(employeeJobVO.getJobType())){
        	flag = true;
        	if(!employeeJobVO.getJobType().equals(JobTypeEnum.OTHER.getValue())){
    	    	employeeJob.setJobType(employeeJobVO.getJobType());
    			List<EmployeeJob> employeeJobList = employeeJobService.getEmployeeJobByCondition(employeeJob);
    			if(employeeJobList != null && employeeJobList.size()>0)
    				if(!employeeJobList.get(0).getId().equals(employeeJobVO.getId()))
    					return Result.fail("该岗位类型已存在，请重新填写！");
        	}
        }
    	
		
    	employeeJob.setId(employeeJobVO.getId());
		employeeJob.setDescript(employeeJobVO.getDescript());
    	employeeJob.setName(employeeJobVO.getName());

		boolean update = employeeJobService.updateJob(employeeJob,flag);
		
    	if(update)
    		return Result.success();
    	
    	return Result.fail("修改岗位失败");
    }

    @ApiOperation(value = "查询岗位列表")
    @ApiImplicitParams({
    	@ApiImplicitParam(name = "page", value = "当前页数", required = true, dataType = "Integer ",defaultValue = "1"),
        @ApiImplicitParam(name = "limit", value = "显示数据条数", required = true, dataType = "Integer ",defaultValue = "10"),
        @ApiImplicitParam(name = "name", value = "岗位名称", required = false, dataType = "String"),
    })
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @GetMapping("/list")
    public Result list(@RequestParam(defaultValue = "1") Integer page,
    							   @RequestParam(defaultValue = "10") Integer limit,
    							   @RequestParam(required=false) @Size(max=32,message="name最大长度为32") String name){
    	EmployeeJob employeeJob = new EmployeeJob();
    	employeeJob.setName(name);
    	Page<EmployeeJob> employeeJobPage = employeeJobService.findListByPage(page, limit,employeeJob);
    	List<EmployeeJobDTO> employeeJobDTOList = employeeJobPage.getRecords().stream().map(employeeJobTmp -> {
    		EmployeeJobDTO employeeJobDTO = new EmployeeJobDTO();
    		BeanUtils.copyProperties(employeeJobTmp, employeeJobDTO);
    		return employeeJobDTO;
    	}).collect(Collectors.toList());
    	
    	ResultList resultList = new ResultList.Builder().total(employeeJobPage.getTotal()).list(employeeJobDTOList).build();
        return Result.success(resultList);
    }
    
    
    @ApiOperation(value = "岗位下拉框选项")
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @GetMapping("/option")
    public Result option() {
        List<EmployeeJob> employeeJobList = employeeJobService.getEmployeeJobByCondition(null);
        
        List<Map<String, Object>> options = new ArrayList<>(employeeJobList.size());
        employeeJobList.stream().forEach(employeeJob -> {
        	Map<String, Object> option = new HashMap<>(2);
            option.put("id", employeeJob.getId());
            option.put("name", employeeJob.getName());
            options.add(option);
        });
        return Result.success(options);  
    }
    
}
