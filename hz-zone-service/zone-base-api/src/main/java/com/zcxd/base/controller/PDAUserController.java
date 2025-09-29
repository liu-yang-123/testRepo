package com.zcxd.base.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.constraints.Min;

import com.zcxd.common.constant.JobTypeEnum;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zcxd.base.dto.PDAUserDTO;
import com.zcxd.base.service.CommonService;
import com.zcxd.base.service.EmployeeJobService;
import com.zcxd.base.service.EmployeeService;
import com.zcxd.base.vo.PDAUserQueryVO;
import com.zcxd.common.annotation.OperateLog;
import com.zcxd.common.annotation.OperateType;
import com.zcxd.common.constant.Constant;
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
 * @ClassName PDAUserController
 * @Description PDA用户管理控制器
 * @author 秦江南
 * @Date 2021年5月25日下午7:08:52
 */
@RequestMapping("/pdaUser")
@RestController
@Api(tags = "PDA用户管理")
public class PDAUserController {
	@Autowired
    private EmployeeService employeeService;
	
	@Autowired
    private EmployeeJobService employeeJobService;

    @Autowired
    private CommonService commonService;
    
	@ApiOperation(value = "PDA用户列表")
    @ApiImplicitParams({
    	@ApiImplicitParam(name = "page", value = "当前页数", required = true, dataType = "Integer",defaultValue = "1"),
        @ApiImplicitParam(name = "limit", value = "显示数据条数", required = true, dataType = "Integer",defaultValue = "10"),
        @ApiImplicitParam(name = "empNo", value = "工号", required = false, dataType = "String"),
        @ApiImplicitParam(name = "empName", value = "姓名", required = false, dataType = "String"),
        @ApiImplicitParam(name = "departmentId", value = "部门", required = false, dataType = "Long"),
        @ApiImplicitParam(name = "pdaEnable", value = "是否启用PDA", required = false, dataType = "Integer")
    })
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @GetMapping("/list")
    public Result findListByPage(@RequestParam(defaultValue = "1") Integer page,
			   @RequestParam(defaultValue = "10") Integer limit,
			   @Validated PDAUserQueryVO pdaUserQueryVO){
		IPage<Employee> employeePage = employeeService.findListByPage(page, limit, pdaUserQueryVO);
        List<PDAUserDTO> pdaUserDTOList = new ArrayList<PDAUserDTO>();
        if(employeePage.getTotal() != 0){
	    	//获取部门Map
	    	Map<String, String> departmentMap = commonService.getDepartmentMap(null);
	    	List<EmployeeJob> employeeJoblist = employeeJobService.list((QueryWrapper)Wrappers.query().eq("deleted",0));
	    	Map<Long,String> employeeJobMap = employeeJoblist.stream().collect(Collectors.toMap(EmployeeJob::getId,EmployeeJob::getName)); 
	    	pdaUserDTOList =  employeePage.getRecords().stream().map(employee -> {
	    		PDAUserDTO pdaUserDTO = new PDAUserDTO();
	        	BeanUtils.copyProperties(employee, pdaUserDTO);
	        	pdaUserDTO.setDepartmentName(employee.getDepartmentId() != 0 ? departmentMap.get(employee.getDepartmentId()+"") : "");
	        	pdaUserDTO.setJobName(employeeJobMap.get(employee.getJobIds()));
	        	return pdaUserDTO;
	        }).collect(Collectors.toList());
        }
        ResultList resultList = new ResultList.Builder().total(employeePage.getTotal()).list(pdaUserDTOList).build();
        return Result.success(resultList);
    }
	
	@OperateLog(value="重置密码",type=OperateType.EDIT)
    @ApiOperation(value = "重置密码", notes = "重置密码")
	@ApiImplicitParam(name = "id", value = "用户id", required = true, dataType = "Long")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/resetpwd/{id}")
    public Result resetPassword(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id) {
		Employee employee = new Employee();
		employee.setId(id);
    	employee.setPassword(DigestUtils.md5DigestAsHex(Constant.USER_DEFAULT_PWD.getBytes()).toUpperCase());
		employeeService.deleteEmployeeUserCache(employee.getId()); //淘汰缓存
    	boolean update = employeeService.updateById(employee);
    	if(update)
    		return Result.success();
    	
    	return Result.fail("重置密码失败");
    }
	
	@ApiOperation(value = "停用PDA")
	@ApiImplicitParam(name = "id", value = "用户id", required = true, dataType = "Long")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/stop/{id}")
    public Result stop(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){
		Employee employee = new Employee();
		employee.setId(id);
		employee.setPdaEnable(0);
		employeeService.deleteEmployeeUserCache(employee.getId()); //淘汰缓存
        boolean update = employeeService.updateById(employee);
    	if(update)
    		return Result.success();
    	
    	return Result.fail("停用PDA失败");
    }
    
    @ApiOperation(value = "启用PDA")
    @ApiImplicitParam(name = "id", value = "机构id", required = true, dataType = "Long")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/enable/{id}")
    public Result enable(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){
    	Employee employee = new Employee();
		employee.setId(id);
		employee.setPdaEnable(1);
		employeeService.deleteEmployeeUserCache(employee.getId()); //淘汰缓存
        boolean update = employeeService.updateById(employee);
    	if(update)
    		return Result.success();
    	
    	return Result.fail("启用PDA失败");
    }
    
//    @OperateLog(value="修改用户角色", type=OperateType.EDIT)
//    @ApiOperation(value = "修改用户角色")
//    @ApiImplicitParams({
//	    @ApiImplicitParam(name = "id", value = "用户id", required = true, dataType = "Long"),
//	    @ApiImplicitParam(name = "roleId", value = "角色id", required = true, dataType = "Long")
//    })
//    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
//    @PostMapping("/update")
//    public Result update(@RequestParam @Min(value=1, message="id不能小于1") Long id,
//    		@RequestParam Long roleId){
//    	Employee employee = new Employee();
//    	employee.setId(id);
//    	employee.setRoleId(roleId);
//    	boolean update = employeeService.updateById(employee);
//    	if(update)
//    		return Result.success();
//    	
//    	return Result.fail("修改用户角色失败");
//    }

	@ApiOperation(value = "设置PDA管理员")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "用户id", required = true, dataType = "Long"),
			@ApiImplicitParam(name = "enable", value = "系统管理员标志(1 - 是，0 - 否)", required = true, dataType = "Integer"),
	})
	@ApiResponse(code = 200, message = "处理成功", response = Result.class)
	@PostMapping("/set/admin")
	public Result setAdmin(@RequestParam Long id, @RequestParam Integer enable){
		Employee employee = employeeService.getById(id);
		if (null == employee) {
			return Result.fail("无效用户");
		}
		if (employee.getPdaEnable() == 0) {
			return Result.fail("请启用PDA功能");
		}
		if (enable == 1 && !employee.getJobType().equals(JobTypeEnum.OTHER.getValue())) {
			return Result.fail("管理员不能是:"+JobTypeEnum.getText(employee.getJobType()));
		}
		Employee newEmployee = new Employee();
		newEmployee.setId(id);
		newEmployee.setPdaAdmin(enable);
		employeeService.deleteEmployeeUserCache(employee.getId()); //淘汰缓存
		boolean update = employeeService.updateById(newEmployee);
		if(update)
			return Result.success();

		return Result.fail("操作失败");
	}
}
