package com.zcxd.base.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zcxd.base.config.UserContextHolder;
import com.zcxd.base.dto.RoleDTO;
import com.zcxd.base.exception.BusinessException;
import com.zcxd.base.service.SysMenuService;
import com.zcxd.base.service.SysPermissionService;
import com.zcxd.base.service.SysRoleService;
import com.zcxd.base.service.SysUserService;
import com.zcxd.base.vo.RoleVO;
import com.zcxd.common.annotation.OperateLog;
import com.zcxd.common.annotation.OperateType;
import com.zcxd.common.util.JacksonUtil;
import com.zcxd.common.util.Result;
import com.zcxd.common.util.ResultList;
import com.zcxd.db.model.SysMenu;
import com.zcxd.db.model.SysPermission;
import com.zcxd.db.model.SysRole;
import com.zcxd.db.model.SysUser;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;

/**
 * 
 * @ClassName SysRoleController
 * @Description 角色管理控制器
 * @author 秦江南
 * @Date 2021年5月7日上午11:18:50
 */
@RequestMapping("/role")
@RestController
@Api(tags = "角色管理")
public class SysRoleController {

    @Autowired
    private SysRoleService roleService;

	@Autowired
	private SysUserService userService;
	
	@Autowired
	private SysMenuService menuService;

	@Autowired
	private SysPermissionService permissionService;
    
    
    @ApiOperation(value = "角色列表")
    @ApiImplicitParams({
    	@ApiImplicitParam(name = "page", value = "当前页数", required = true, dataType = "Integer ",defaultValue = "1"),
        @ApiImplicitParam(name = "limit", value = "显示数据条数", required = true, dataType = "Integer ",defaultValue = "10"),
        @ApiImplicitParam(name = "roleName", value = "角色名称", required = false, dataType = "String"),
    })
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @GetMapping("/list")
    public Result list(@RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required=false) @Size(max=32,message="roleName最大长度为32") String roleName) {
    	SysRole role = new SysRole();
    	role.setRoleName(roleName);
    	role.setRoleType(0);
        IPage<SysRole> rolePage = roleService.findListByPage(page, limit,role);
 		List<RoleDTO> roleDTOList = rolePage.getRecords().stream().map(roleTmp->{
 			RoleDTO roleDTO = new RoleDTO();
 			BeanUtils.copyProperties(roleTmp, roleDTO);
 			return roleDTO;
 		}).collect(Collectors.toList());
         
         ResultList resultList = new ResultList.Builder().total(rolePage.getTotal()).list(roleDTOList).build();
         return Result.success(resultList);
         
    }

    @OperateLog(value="修改角色",type=OperateType.EDIT)
    @ApiOperation(value = "修改角色")
    @ApiImplicitParam(name = "roleVO", value = "角色信息", required = true, dataType = "RoleVO" )
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
	@PostMapping("/update")
	public Result update(@RequestBody @Validated RoleVO roleVO) {
    	if (roleVO.getId() == null) {
            return Result.fail("角色Id不能为空");
        }
    	
		SysRole role = new SysRole();
		role.setRoleName(roleVO.getRoleName());
		role.setRoleType(0);
		List<SysRole> roleList = roleService.getRoleByCondition(role);
		if(roleList != null && roleList.size()>0){
			if(!roleList.get(0).getId().equals(roleVO.getId()))
				return Result.fail("该角色名已存在，请重新填写！");
		}	
		
		role.setId(roleVO.getId());
		role.setDescribes(roleVO.getDescribes());
		boolean update = roleService.updateById(role);
    	if(update)
    		return Result.success();
    	
    	return Result.fail("修改角色失败");
	}

    
    @OperateLog(value="添加角色",type=OperateType.ADD)
    @ApiOperation(value = "添加角色")
    @ApiImplicitParam(name = "roleVO", value = "角色信息", required = true, dataType = "RoleVO" )
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
	@PostMapping("/save")
	public Result save(@RequestBody @Validated RoleVO roleVO) {
		
		SysRole role = new SysRole();
		role.setRoleName(roleVO.getRoleName());
		role.setRoleType(0);
		List<SysRole> roleList = roleService.getRoleByCondition(role);
		if(roleList != null && roleList.size()>0)
			return Result.fail("该角色名已存在，请重新填写！");
		
		role.setId(null);
		role.setDescribes(roleVO.getDescribes());
		role.setRoleType(0);
		boolean save = roleService.save(role);
    	if(save)
    		return Result.success();
    	
    	return Result.fail("添加角色失败");

	}
	
    @OperateLog(value="删除角色",type=OperateType.DELETE)
    @ApiOperation(value = "删除角色")
    @ApiImplicitParam(name = "id", value = "角色id", required = true, dataType = "Long")
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
	@PostMapping("/delete/{id}")
	public Result delete(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id) {

    	SysUser user = new SysUser();
    	user.setRoleIds(id+"");
    	List<SysUser> userList = userService.getUserByCondition(user);
    	if(userList != null && userList.size()>0)
    		return Result.fail("该角色存有用户，无法删除");
    	
    	
    	SysRole role = new SysRole();
		role.setId(id);
		role.setDeleted(1);
		permissionService.deletePerssionCache(id); //淘汰缓存
		boolean update = roleService.updateById(role);
		
		if(update)
    		return Result.success();
    	
    	return Result.fail("删除角色失败");

	}
    
    @ApiOperation(value = "获取权限详情")
    @ApiImplicitParam(name = "roleId", value = "角色id", required = true, dataType = "Integer")
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @GetMapping("/permissions/get")
    public Result getPermissions(@RequestParam @Min(value=1, message="id不能小于1") Integer roleId) {
		SysMenu menu = new SysMenu();
		menu.setMenuType(0);
        List<Map<String, Object>> systemPermissions = menuService.getTree(menu);
        
//        String[] roleIds = new String[]{roleId+""};
//        Set<String> assignedPermissions = permissionService.getPermissionByRoleIds(roleIds);
        Set<String> assignedPermissions = permissionService.getPermissionByRoleId(roleId.longValue());

        Map<String, Object> data = new HashMap<>();
        data.put("systemPermissions", systemPermissions);
        data.put("assignedPermissions", assignedPermissions);
        return Result.success(data);
    }
    
    @OperateLog(value="修改权限",type=OperateType.EDIT)
    @ApiOperation(value = "修改权限")
    @ApiImplicitParam(name = "body", value = "权限列表和角色id", required = true, dataType = "String")
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @PostMapping("/permissions/update")
    public Result updatePermissions(@RequestBody String body) {
        Long roleId = JacksonUtil.parseLong(body, "roleId");
        List<String> permissions = JacksonUtil.parseStringList(body, "permissions");
        if (roleId == null || permissions == null) {
        	return Result.fail("参数有误");
        }
        
        List<SysPermission> sysPermissions = new ArrayList<>();
		for (String permission : permissions) {
			SysPermission sysPermission = new SysPermission();
            sysPermission.setRoleId(roleId);
            sysPermission.setPermission(permission);
            
			Long userId = UserContextHolder.getUserId();
			Long time = System.currentTimeMillis();
			sysPermission.setCreateUser(userId);
			sysPermission.setCreateTime(time);
			sysPermission.setUpdateUser(userId);
			sysPermission.setUpdateTime(time);
            sysPermissions.add(sysPermission);
        }
		permissionService.deletePerssionCache(roleId); //淘汰缓存
        boolean update = permissionService.updatePermission(sysPermissions,roleId);
		if(update)
    		return Result.success();
    	
    	return Result.fail("修改权限失败");
    }
    
    @ApiOperation(value = "角色下拉框选项")
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @GetMapping("/option")
    public Result option() {
    	SysRole role = new SysRole();
    	role.setRoleType(0);
        List<SysRole> roleList = roleService.getRoleByCondition(role);
        
        List<Map<String, Object>> options = new ArrayList<>(roleList.size());
        roleList.stream().forEach(roleTmp -> {
        	Map<String, Object> option = new HashMap<>(2);
            option.put("value", roleTmp.getId());
            option.put("label", roleTmp.getRoleName());
            options.add(option);
        });
        return Result.success(options);
    }
}
