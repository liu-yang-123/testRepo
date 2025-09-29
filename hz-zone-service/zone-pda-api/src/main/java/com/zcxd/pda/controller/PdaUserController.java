package com.zcxd.pda.controller;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zcxd.common.constant.Constant;
import com.zcxd.common.util.Result;
import com.zcxd.common.util.ResultList;
import com.zcxd.db.model.Employee;
import com.zcxd.db.model.PdaUser;
import com.zcxd.pda.config.UserContextHolder;
import com.zcxd.pda.dto.FingerprintDTO;
import com.zcxd.pda.service.LoginService;
import com.zcxd.pda.service.impl.BankTellerService;
import com.zcxd.pda.service.impl.EmployeeService;
import com.zcxd.pda.vo.BankTellerVO;
import com.zcxd.pda.vo.LoginAccountVO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;

/**
 * 
 * @ClassName SysUserController
 * @Description 用户管理控制器
 * @author shijin
 * @Date 2021年5月11日上午11:19:03
 */
@RequestMapping("/user")
@RestController
@Api(tags = "pda用户管理")
public class PdaUserController {

    @Resource
    private LoginService loginService;
    @Resource
    private EmployeeService employeeService;
    @Resource
	private BankTellerService tellerService;

    @ApiOperation(value = "账号登录")
	@ApiImplicitParam(name = "loginAccountVO", value = "账号登录VO", required = true, dataType = "LoginAccountVO",dataTypeClass = LoginAccountVO.class)
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @PostMapping("/login/account")
    public Result loginAccount(@RequestBody @Validated LoginAccountVO loginAccountVO) {
        Object result = loginService.loginAccount(loginAccountVO);
        return Result.success(result);
    }

	@ApiOperation(value = "押运员账号登录")
	@ApiImplicitParam(name = "loginAccountVO", value = "账号登录VO", required = true, dataType = "LoginAccountVO",dataTypeClass = LoginAccountVO.class)
	@ApiResponse(code = 0, message = "处理成功", response = Result.class)
	@PostMapping("/login/esort")
	public Result loginEsort(@RequestBody @Validated LoginAccountVO loginAccountVO) {
		Object result = loginService.loginEsort(loginAccountVO);
		return Result.success(result);
	}

	@ApiOperation(value = "指纹登录")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "Integer",dataTypeClass = Integer.class),
			@ApiImplicitParam(name = "terSN", value = "设备sn", required = true, dataType = "String",dataTypeClass=String.class)
	})
	@ApiResponse(code = 0, message = "处理成功", response = Result.class)
	@PostMapping("/login/print")
	public Result loginPrint(Integer userId,String terSN) {
		Object result = loginService.loginPrint(userId,terSN);
		return Result.success(result);
	}


	@ApiOperation(value = "修改密码")
    @ApiImplicitParams({
    	@ApiImplicitParam(name = "oldPassword", value = "旧密码", required = true, dataType = "String",dataTypeClass=String.class),
    	@ApiImplicitParam(name = "newPassword", value = "新密码", required = true, dataType = "String",dataTypeClass=String.class)
    })
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
	@PostMapping("/password/update")
	public Result updatePwd(@RequestParam @NotBlank(message = "原密码不能为空") @Size(min=32,message = "密码长度不符") String oldPassword,
			@RequestParam @NotBlank(message = "原密码不能为空") @Size(min=32,message = "密码长度不符") String newPassword) {
		loginService.updatePassword(oldPassword,newPassword);
		return Result.success();
	}

	@ApiOperation(value = "查询指纹数据列表")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "page", value = "页码", required = true, dataType = "Integer",dataTypeClass=Integer.class),
			@ApiImplicitParam(name = "limit", value = "每页记录", required = true, dataType = "Integer",dataTypeClass=Integer.class),
			@ApiImplicitParam(name = "terSN", value = "设备sn", required = true, dataType = "String",dataTypeClass=String.class)
	})
	@ApiResponse(code = 0, message = "处理成功", response = Result.class)
	@PostMapping("/print/list")
	public Result listFingerPrint(@RequestParam(defaultValue = "1") Integer page,
								  @RequestParam(defaultValue = "10") Integer limit,
								  @RequestParam String terSN) {
		Object result = loginService.listFingerPrint(page,limit,terSN);
		return Result.success(result);
	}

	@ApiOperation(value = "查询用户指纹录入情况")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "userType", value = "用户类型", required = true, dataType = "Integer",dataTypeClass=Integer.class),
			@ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "Long",dataTypeClass=Long.class)
	})
	@ApiResponse(code = 0, message = "处理成功", response = Result.class)
	@PostMapping("/print/info")
	public Result getUserFingerInfo(@RequestParam Integer userType,
								  @RequestParam Long userId) {
		Object result = loginService.getUserFingerInfo(userType,userId);
		return Result.success(result);
	}

	@ApiOperation(value = "设置用户指纹特征")
	@ApiImplicitParam(name = "fingerprintDTO", value = "指纹数据", required = true, dataType = "FingerprintDTO")
	@ApiResponse(code = 0, message = "处理成功", response = Result.class)
	@PostMapping("/print/save")
	public Result saveFingerPrint(@RequestBody FingerprintDTO fingerprintDTO) {
		Long id = loginService.saveFingerPrint(fingerprintDTO);
		Map<String, Long> res = new HashMap<String, Long>();
		res.put("id", id);
		return id != 0L ? Result.success(res) : Result.fail();
	}

	@ApiOperation(value = "删除用户指纹特征")
	@ApiResponse(code = 0, message = "处理成功", response = Result.class)
	@PostMapping("/print/delete/{id}")
	public Result deleteFingerPrint(@PathVariable Long id) {
		boolean bRet = loginService.deleteFingerPrint(id);
		return bRet?Result.success() : Result.fail();
	}

	@ApiOperation(value = "获取员工列表")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "page", value = "页码", required = true, dataType = "Integer",dataTypeClass = Integer.class),
			@ApiImplicitParam(name = "limit", value = "记录数", required = true, dataType = "Integer",dataTypeClass = Integer.class)
	})
	@ApiResponse(code = 0, message = "处理成功", response = Result.class)
	@PostMapping("/employee/list")
	public Result listEmployeeUser(@RequestParam(defaultValue = "1") Integer page,
								   @RequestParam(defaultValue = "10") Integer limit) {
		PdaUser pdaUser = UserContextHolder.getUser();
		Long userId = UserContextHolder.getUserId();
		ResultList resultList = null;
    	if (pdaUser.getUserType() == Constant.USER_EMPLOYEE){
			Employee employee = employeeService.getEmployeeUserById(userId);
			if (null == employee) {
				return Result.success();
			}
			 resultList = loginService.listEmployeePage(employee.getDepartmentId(),page,limit);
		} else{
    		resultList = tellerService.listEmployee(userId,page,limit);
		}
		return Result.success(resultList);
	}

	@ApiOperation(value = "获取柜员员工列表")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "page", value = "页码", required = true, dataType = "Integer",dataTypeClass = Integer.class),
			@ApiImplicitParam(name = "limit", value = "记录数", required = true, dataType = "Integer",dataTypeClass = Integer.class)
	})
	@ApiResponse(code = 0, message = "处理成功", response = Result.class)
	@PostMapping("/bankEmployee/list")
	public Result listBankEmployeeUser(@RequestParam(defaultValue = "1") Integer page,
								   @RequestParam(defaultValue = "10") Integer limit) {

		Employee employee = employeeService.getEmployeeUserById(UserContextHolder.getUserId());
		if (null == employee) {
			return Result.fail();
		}
		ResultList resultList = loginService.listEmployeePage(employee.getDepartmentId(),page,limit);
		return Result.success(resultList);
	}

	@ApiOperation(value = "检查指纹更新")
	@ApiImplicitParam(name = "terSN", value = "PDA设备SN", required = true, dataType = "String",dataTypeClass = String.class)
	@ApiResponse(code = 0, message = "处理成功", response = Result.class)
	@PostMapping("/print/lasttime")
	public Result getFingerprintLastTime(@RequestParam String terSN) {
		Long time = loginService.getFingerprintUpdateTime(terSN);
		return Result.success(time);
	}

	@ApiOperation(value = "添加银行员工")
	@PostMapping("/bankEmployee/create")
	public Result bankEmployeeCreate(@RequestBody BankTellerVO bankTellerVO){
        boolean b = tellerService.create(bankTellerVO);
    	return b ? Result.success("添加成功") : Result.fail("添加失败");
	}

	@ApiOperation(value = "修改银行员工")
	@PostMapping("/bankEmployee/update")
	public Result bankEmployeeUpdate(@RequestBody BankTellerVO bankTellerVO){
		boolean b = tellerService.edit(bankTellerVO);
		return b ? Result.success("修改成功") : Result.fail("修改失败");
	}

	@ApiOperation(value = "删除银行员工")
	@PostMapping("/bankEmployee/delete/{id}")
	public Result bankEmployeeDelete(@PathVariable Long id){
		boolean b = tellerService.delete(id);
		return b ? Result.success("删除成功") : Result.fail("删除失败");
	}

}
