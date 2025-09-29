package com.zcxd.base.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.base.dto.MenuDTO;
import com.zcxd.common.util.TreeUtil;
import com.zcxd.db.mapper.SysMenuMapper;
import com.zcxd.db.model.SysMenu;

/**
 * 
 * @ClassName SysMenuServiceImpl
 * @Description 菜单管理服务类
 * @author 秦江南
 * @Date 2021年5月7日上午10:49:54
 */
@Service
public class SysMenuService extends ServiceImpl<SysMenuMapper, SysMenu> {

	/**
	 * 
	 * @Title getTree
	 * @Description 获取菜单树
	 * @return 返回类型 Result
	 */
	public List<Map<String, Object>> getTree(SysMenu menu) {
		List<SysMenu> menuList= this.getMenuByCondition(menu);
		List<MenuDTO> menuDTOList = menuList.stream().map(menuTmp->{
			MenuDTO menuDTO = new MenuDTO();
			BeanUtils.copyProperties(menuTmp, menuDTO);
			return menuDTO;
		}).collect(Collectors.toList());
		
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		menuDTOList.stream().forEach(menuTmp->{
			list.add(TreeUtil.getFiledInfo(menuTmp));
		});
		
		return TreeUtil.listToTree(list);		
	}

	/**
	 * 
	 * @Title getMenuByCondition
	 * @Description 根据条件查询菜单集合
	 * @param menu
	 * @return
	 * @return 返回类型 List<SysMenu>
	 */
	public List<SysMenu> getMenuByCondition(SysMenu menu) {
		
		QueryWrapper<SysMenu> queryWrapper = new QueryWrapper<>();
		if(menu != null){
			if(menu.getPid()!=null)
				queryWrapper.eq("pid", menu.getPid());
			
			if(menu.getMenuType()!=null)
				queryWrapper.eq("menu_type", menu.getMenuType());
		}
		queryWrapper.orderByAsc("sort");
		return baseMapper.selectList(queryWrapper);
	}

}
