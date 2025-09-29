package com.zcxd.base.controller;

import java.util.List;

import javax.validation.constraints.Min;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zcxd.base.service.SysMenuService;
import com.zcxd.base.vo.MenuVO;
import com.zcxd.common.annotation.OperateLog;
import com.zcxd.common.annotation.OperateType;
import com.zcxd.common.util.Result;
import com.zcxd.db.model.SysMenu;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;

/**
 * 
 * @ClassName SysMenuController
 * @Description 菜单管理控制器
 * @author 秦江南
 * @Date 2021年5月6日下午3:35:54
 */

@RestController
@RequestMapping("/menu")
@Api(tags = "菜单管理")
public class SysMenuController {
	
	@Autowired
	private SysMenuService menuService;
	
    @ApiOperation(value = "菜单列表")
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @GetMapping("/list")
    public Result list(){
		SysMenu menu = new SysMenu();
		menu.setMenuType(0);
    	return Result.success(menuService.getTree(menu));
    }
    
    
    @OperateLog(value="修改菜单",type=OperateType.EDIT)
    @ApiOperation(value = "修改菜单")
    @ApiImplicitParam(name = "menuVO", value = "菜单信息", required = true, dataType = "MenuVO")
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @PostMapping("/update")
	public Result update(@RequestBody @Validated MenuVO menuVO) {
    	
        if (menuVO.getId() == null) {
            return Result.fail("菜单Id不能为空");
        }
        
		SysMenu menu = new SysMenu();
		BeanUtils.copyProperties(menuVO, menu);
    	boolean update = menuService.updateById(menu);
		if(update)
    		return Result.success();
    	
    	return Result.fail("修改菜单失败");
	}
	
    @OperateLog(value="添加菜单",type=OperateType.ADD)
    @ApiOperation(value = "添加菜单")
    @ApiImplicitParam(name = "menuVO", value = "菜单信息", required = true, dataType = "MenuVO")
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
	@PostMapping("/save")
	public Result save(@RequestBody @Validated MenuVO menuVO) {
		SysMenu menu = new SysMenu();
		BeanUtils.copyProperties(menuVO, menu);
    	menu.setId(null);
    	if(menuVO.getPid()==null)
    		menu.setPid(0L);
    	
    	menu.setMenuType(0);
    	boolean save = menuService.save(menu);
		if(save)
    		return Result.success();
    	
    	return Result.fail("添加菜单失败");
	}

    @OperateLog(value="删除菜单",type=OperateType.DELETE)
    @ApiOperation(value = "删除菜单")
    @ApiImplicitParam(name = "id", value = "菜单id", required = true, dataType = "Long")
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
	@PostMapping("/delete/{id}")
	public Result delete(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id) {
    	SysMenu menu = new SysMenu();
    	menu.setPid(id);
    	List<SysMenu> menuList = menuService.getMenuByCondition(menu);
    	if(menuList != null && menuList.size()>0)
    		return Result.fail("该菜单存有子菜单，无法删除");
    	
		boolean remove = menuService.removeById(id);
		if(remove)
    		return Result.success();
    	
    	return Result.fail("删除菜单失败");
	}

}
