package com.zcxd.base.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zcxd.base.config.UserContextHolder;
import com.zcxd.base.dto.UserDTO;
import com.zcxd.base.service.BankService;
import com.zcxd.base.service.CommonService;
import com.zcxd.base.service.DepartmentService;
import com.zcxd.base.service.SysUserService;
import com.zcxd.base.vo.ProfileVO;
import com.zcxd.base.vo.UserBankVO;
import com.zcxd.base.vo.UserVO;
import com.zcxd.common.annotation.OperateLog;
import com.zcxd.common.annotation.OperateType;
import com.zcxd.common.constant.Constant;
import com.zcxd.common.util.Result;
import com.zcxd.common.util.ResultList;
import com.zcxd.db.model.Bank;
import com.zcxd.db.model.SysUser;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;

/**
 * 
 * @ClassName SysUserController
 * @Description 用户管理控制器
 * @author 秦江南
 * @Date 2021年5月7日上午11:19:03
 */
@RequestMapping("/user")
@RestController
@Api(tags = "用户管理")
public class SysUserController {



    @Autowired
    private SysUserService userService;
    
    @Autowired
    private CommonService commonService;
    
    @Autowired
    private BankService bankService;
    
	@Autowired
    private DepartmentService departmentService;
    
    @ApiOperation(value = "用户列表")
    @ApiImplicitParams({
    	@ApiImplicitParam(name = "page", value = "当前页数", required = true, dataType = "Integer ",defaultValue = "1"),
        @ApiImplicitParam(name = "limit", value = "显示数据条数", required = true, dataType = "Integer ",defaultValue = "10"),
        @ApiImplicitParam(name = "userName", value = "用户名称", required = false, dataType = "String"),
    })
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @GetMapping("/list")
    public Result list(@RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required=false) @Size(max=32,message="userName最大长度为32") String userName) {
    	
    	//查询用户分页列表
    	SysUser user = new SysUser();
    	user.setUsername(userName);
    	IPage<SysUser> userPage = userService.findListByPage(page, limit,user);
    	
    	List<UserDTO> userDTOList = new ArrayList<UserDTO>();
    	
    	if(userPage.getTotal() != 0){
//    		Set<Long> bankIds = new HashSet<>();
//    		userPage.getRecords().stream().forEach(sysUser -> {
//    			if(!sysUser.getBankId().equals(0)){
//    				bankIds.add(sysUser.getBankId());
//    			}
//    			if(!StringUtils.isBlank(sysUser.getStockBank())){
//    				String[] stockBankId = sysUser.getStockBank().split(",");
//    				for (String bankId : stockBankId) {
//    					bankIds.add(Long.parseLong(bankId));
//					}
//    			}
//    			
//    		});
//    		
//    		Map<Long,String> bankNameMap = new HashMap<>();
//    		if(bankIds.size() > 0){
//	    		List<Bank> bankList = bankService.listByIds(bankIds);
//	    		bankNameMap = bankList.stream().collect(Collectors.toMap(Bank::getId,Bank::getShortName,(key1,key2)->key2));
//    		}
    		
            List<Bank> bankList = bankService.list();
            Map bankMap = bankList.stream().collect(Collectors.toMap(Bank::getId,Function.identity(),(key1,key2) -> key2));
    		
	    	//获取部门Map
	    	Map<String, String> departmentMap = commonService.getDepartmentMap(null);
	    	
    		Map<Long,Bank> bankNameMapFinal = bankMap;
	    	//转换返回数据结构并增加部门名称
	    	userDTOList = userPage.getRecords().stream().map(userTmp->{
	 			UserDTO userDTO = new UserDTO();
	 			BeanUtils.copyProperties(userTmp, userDTO);
	 			userDTO.setBankName(userTmp.getBankId() != 0 ? bankNameMapFinal.get(userTmp.getBankId()).getShortName() : "");
	 			if(!StringUtils.isBlank(userTmp.getStockBank())){
    				String[] stockBankId = userTmp.getStockBank().split(",");
    				StringBuffer stockBank = new StringBuffer();
    				for (String bankId : stockBankId) {
    					stockBank.append(departmentMap.get(bankNameMapFinal.get(Long.parseLong(bankId)).getDepartmentId().toString()));
    					stockBank.append("-");
    					stockBank.append(bankNameMapFinal.get(Long.parseLong(bankId)).getShortName());
    					stockBank.append(",");
					}
    				userDTO.setStockBankName(stockBank.substring(0, stockBank.length()-1));
    			}
//	 			userDTO.setDepartmentName(userTmp.getDepartmentId() != 0 ? departmentMap.get(userTmp.getDepartmentId().toString()) : "");
	 			return userDTO;
	 		}).collect(Collectors.toList());
    	}
 		
        ResultList resultList = new ResultList.Builder().total(userPage.getTotal()).list(userDTOList).build();
        return Result.success(resultList);
    }
    
    @OperateLog(value="添加用户",type=OperateType.ADD)
    @ApiOperation(value = "添加用户")
    @ApiImplicitParam(name = "userVO", value = "用户信息", required = true, dataType = "UserVO")
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
	@PostMapping("/save")
	public Result save(@RequestBody @Validated UserVO userVO) {
		SysUser user = new SysUser();
		user.setUsername(userVO.getUsername());
		List<SysUser> userList = userService.getUserByCondition(user);
		if(userList != null && userList.size()>0){
			return Result.fail("该用户名已存在，请重新填写！");
		}
		
		BeanUtils.copyProperties(userVO, user);
		
//		if (userVO.getDepartmentId() != null && userVO.getDepartmentId() != 0) {
//            Department department = departmentService.getById(userVO.getDepartmentId());
//            String[] parentIds = department.getParentIds().substring(1, department.getParentIds().length()-1).split("/");
//            if(parentIds.length > 1) {
//            	user.setTopDepartmentId(Long.parseLong(parentIds[1]));
//            } else {
//            	user.setTopDepartmentId(user.getDepartmentId()); //一级部门属于自己
//            }
//        }
		
		user.setId(null);
		BCryptPasswordEncoder bcryptPasswordEncoder = new BCryptPasswordEncoder();
		user.setPassword(bcryptPasswordEncoder.encode(Constant.USER_DEFAULT_PWD));
		user.setStatusT(0);
		user.setDeleted(0);
		boolean save = userService.save(user);
    	if(save)
    		return Result.success();
    	
    	return Result.fail("添加用户失败");
	}
    
    @OperateLog(value="添加银行用户",type=OperateType.ADD)
    @ApiOperation(value = "添加银行用户")
    @ApiImplicitParam(name = "userBankVO", value = "用户信息", required = true, dataType = "UserBankVO")
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
	@PostMapping("/bankSave")
	public Result bankSave(@RequestBody @Validated UserBankVO userBankVO) {
    	if(userBankVO.getBankId() == null){
    		return Result.fail("所属银行不能为空");
    	}
		SysUser user = new SysUser();
		user.setUsername(userBankVO.getUsername());
		List<SysUser> userList = userService.getUserByCondition(user);
		if(userList != null && userList.size()>0){
			return Result.fail("该用户名已存在，请重新填写！");
		}
		
		BeanUtils.copyProperties(userBankVO, user);
		
		user.setId(null);
		BCryptPasswordEncoder bcryptPasswordEncoder = new BCryptPasswordEncoder();
		user.setPassword(bcryptPasswordEncoder.encode(Constant.USER_DEFAULT_PWD));
		user.setStatusT(0);
		user.setDeleted(0);
		boolean save = userService.save(user);
    	if(save)
    		return Result.success();
    	
    	return Result.fail("添加银行用户失败");
	}

    @OperateLog(value="修改用户",type=OperateType.EDIT)
    @ApiOperation(value = "修改用户")
    @ApiImplicitParam(name = "userVO", value = "用户信息", required = true, dataType = "UserBankVO")
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @PostMapping("/update")
	public Result update(@RequestBody @Validated UserBankVO userVO) {
        if (userVO.getId() == null) {
            return Result.fail("用户Id不能为空");
        }
    	
    	SysUser user = new SysUser();
		user.setUsername(userVO.getUsername());;
		List<SysUser> userList = userService.getUserByCondition(user);
		if(userList != null && userList.size()>0){
			if(!userList.get(0).getId().equals(userVO.getId()))
				return Result.fail("该用户名已存在，请重新填写！");
		}
		
		BeanUtils.copyProperties(userVO, user);
		
//		if (userVO.getDepartmentId() != null && userVO.getDepartmentId() != 0) {
//            Department department = departmentService.getById(userVO.getDepartmentId());
//            String[] parentIds = department.getParentIds().substring(1, department.getParentIds().length()-1).split("/");
//            if(parentIds.length > 1) {
//            	user.setTopDepartmentId(Long.parseLong(parentIds[1]));
//            } else {
//            	user.setTopDepartmentId(user.getDepartmentId()); //一级部门属于自己
//            }
//        }
		userService.deleteSysUserCache(user.getId()); //淘汰缓存
		boolean update = userService.updateById(user);
    	if(update)
    		return Result.success();
    	
    	return Result.fail("修改用户失败");
	}

    @OperateLog(value="重置密码",type=OperateType.EDIT)
    @ApiOperation(value = "重置密码", notes = "重置密码")
    @ApiImplicitParam(name = "id", value = "用户id", required = true, dataType = "Long")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/resetpwd/{id}")
    public Result resetPassword(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id) {
    	SysUser user = new SysUser();
    	user.setId(id);
    	BCryptPasswordEncoder bcryptPasswordEncoder = new BCryptPasswordEncoder();
		user.setPassword(bcryptPasswordEncoder.encode(Constant.USER_DEFAULT_PWD));
		userService.deleteSysUserCache(user.getId()); //淘汰缓存
		boolean update = userService.updateById(user);
    	if(update)
    		return Result.success();
    	
    	return Result.fail("重置密码失败");
    }
	
    @OperateLog(value="修改密码",type=OperateType.EDIT)
    @ApiOperation(value = "修改密码")
	@ApiImplicitParam(name = "profileVO", value = "密码修改VO", required = true, dataType = "ProfileVO")
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
	@PostMapping("/editPwd")
	public Result editPwd(@RequestBody @Validated ProfileVO profileVO) {
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", UserContextHolder.getUserId()).eq("deleted", 0);
		SysUser user= userService.getOne(queryWrapper);

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		if (!encoder.matches(profileVO.getOldPassword(), user.getPassword())) {
			return Result.fail("原密码不正确");
		}
		user.setPassword(encoder.encode(profileVO.getPassword()));

		user.setUpdateUser(UserContextHolder.getUserId());
		user.setUpdateTime(System.currentTimeMillis());
		userService.deleteSysUserCache(user.getId()); //淘汰缓存
		boolean b = userService.updateById(user);
		if (b){
			return Result.success(null,"密码修改成功");
		}
		return Result.fail("修改密码失败");
	}

    @OperateLog(value="删除用户",type=OperateType.DELETE)
    @ApiOperation(value = "删除用户")
    @ApiImplicitParam(name = "id", value = "用户id", required = true, dataType = "Long")
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
	@PostMapping("/delete/{id}")
	public Result delete(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id) {
    	
        if (UserContextHolder.getUserId().equals(id)) {
            return Result.fail("不能删除自己");
        }
    	SysUser user = new SysUser();
    	user.setId(id);
    	user.setDeleted(1);
		userService.deleteSysUserCache(user.getId()); //淘汰缓存
    	boolean update = userService.updateById(user);
    	if(update)
    		return Result.success();
    	
    	return Result.fail("删除用户失败");
	}

	@ApiOperation(value = "用户下拉选项")
	@GetMapping("/option")
	public Result option(){
		QueryWrapper queryWrapper = Wrappers.query().eq("deleted",0);
		List<SysUser> userList = userService.list(queryWrapper);
		List<Map> mapList = userList.stream().map(item -> {
			HashMap map = new HashMap();
			map.put("id", item.getId());
			map.put("name", item.getNickName());
			return map;
		}).collect(Collectors.toList());
		return Result.success(mapList);
	}
}
