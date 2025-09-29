package com.zcxd.base.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.base.exception.BusinessException;
import com.zcxd.base.vo.RouteTemplateATMVO;
import com.zcxd.common.util.Result;
import com.zcxd.db.mapper.RouteTemplateAtmMapper;
import com.zcxd.db.model.AtmDevice;
import com.zcxd.db.model.Bank;
import com.zcxd.db.model.RouteTemplateAtm;

/**
 * 
 * @ClassName RouteTemplateAtmService
 * @Description 线路管理模板设备服务类
 * @author 秦江南
 * @Date 2021年6月18日上午11:30:53
 */
@Service
public class RouteTemplateAtmService extends ServiceImpl<RouteTemplateAtmMapper, RouteTemplateAtm>{
	
	@Autowired
    private BankService bankService;
	
    @Autowired
    private ATMService atmService;
	
	/**
	 * 
	 * @Title getRouteTemplateByCondition
	 * @Description 根据条件查询线路模板ATM设备
	 * @param routeTemplateAtm
	 * @return
	 * @return 返回类型 List<RouteTemplateAtm>
	 */
	public List<RouteTemplateAtm> getRouteTemplateAtmByCondition(RouteTemplateAtm routeTemplateAtm) {
		QueryWrapper<RouteTemplateAtm> queryWrapper = new QueryWrapper<>();
		if(routeTemplateAtm != null){
			if(routeTemplateAtm.getRouteTemplateId() != null){
				queryWrapper.eq("route_template_id", routeTemplateAtm.getRouteTemplateId());
			}
			
			if(routeTemplateAtm.getAtmId() != null){
				queryWrapper.eq("atm_id", routeTemplateAtm.getAtmId());
			}
		}
		
		queryWrapper.orderBy(true,true,"sort");
		return baseMapper.selectList(queryWrapper);
	}

	/**
	 * 
	 * @Title getMaxSort
	 * @Description 获得当前最大排序值
	 * @param routeTemplateAtm
	 * @return
	 * @return 返回类型 Long
	 */
	public Long getMaxSort(Long routeTemplateId) {
		Long maxSort = baseMapper.getMaxSort(routeTemplateId);
		return maxSort == null ? 0L : maxSort;
	}
	

	/**
	 * 
	 * @Title deleteAtm
	 * @Description 删除线路模板设备
	 * @param routeTemplateId
	 * @param id
	 * @return
	 * @return 返回类型 Result
	 */
	public Result deleteAtm(Long id) {
    	RouteTemplateAtm routeTemplateAtm = this.getById(id);

    	boolean delete = this.removeById(id);
    	
    	if(!delete)
    		return Result.fail("删除线路模板设备失败");
    	
    	Long maxSort = this.getMaxSort(routeTemplateAtm.getRouteTemplateId());
    	baseMapper.updateSort(routeTemplateAtm.getRouteTemplateId(),routeTemplateAtm.getSort() + 1, maxSort.intValue() ,"-"); 
    	return Result.success();
	}

	/**
	 * 
	 * @Title updateAtmSort
	 * @Description 修改线路模板设备顺序
	 * @param id
	 * @param oldSort
	 * @param newSort
	 * @return
	 * @return 返回类型 Result
	 */
	public Result updateAtmSort(Long id, Integer newSort) {
		RouteTemplateAtm routeTemplateAtmTmp = this.getById(id);
    	if(newSort + 1 > routeTemplateAtmTmp.getSort()){
    		baseMapper.updateSort(routeTemplateAtmTmp.getRouteTemplateId(),routeTemplateAtmTmp.getSort() + 1, newSort + 1,"-"); 
    	}else{
    		baseMapper.updateSort(routeTemplateAtmTmp.getRouteTemplateId(),newSort + 1, routeTemplateAtmTmp.getSort() - 1 ,"+"); 
    	}
		
		RouteTemplateAtm routeTemplateAtm = new RouteTemplateAtm();
    	routeTemplateAtm.setId(id);
    	routeTemplateAtm.setSort(newSort+1);
		boolean update = this.updateById(routeTemplateAtm);
    	if(update)
    		return Result.success();
    	
    	return Result.fail("修改线路模板设备顺序失败");
	}

	/**
	 * 
	 * @Title getRouteTemplateAtmList
	 * @Description 获得线路模板设备列表
	 * @param routeTemplateId
	 * @return
	 * @return 返回类型 List<Map<String, Object>>
	 */
	public List<Map<String, Object>> getRouteTemplateAtmList(Long routeTemplateId, Long bankId) {
		return baseMapper.getRouteTemplateAtmList(routeTemplateId, bankId);
	}

	
	/**
	 * 
	 * @Title saveAtm
	 * @Description 添加线路模板途径设备
	 * @param routeTemplateATMVO
	 * @return
	 * @return 返回类型 Result
	 */
	@Transactional(rollbackFor=Exception.class)
	public Result saveAtm(RouteTemplateATMVO routeTemplateATMVO) {
		
		String[] atmIds = routeTemplateATMVO.getAtmId().split(",");
		Long maxSort = this.getMaxSort(routeTemplateATMVO.getRouteTemplateId());
		
		RouteTemplateAtm routeTemplateAtm = new RouteTemplateAtm();
    	routeTemplateAtm.setRouteTemplateId(routeTemplateATMVO.getRouteTemplateId());
		List<RouteTemplateAtm> routeTemplateAtmList = this.getRouteTemplateAtmByCondition(routeTemplateAtm);
		Map<Long, Long> routeTemplateAtmMap = 
				routeTemplateAtmList.stream().collect(Collectors.toMap(RouteTemplateAtm::getAtmId, RouteTemplateAtm::getId));
		
		for (String atmId : atmIds) {
			maxSort+=1;
			
			if(routeTemplateAtmMap.containsKey(Long.parseLong(atmId)))
				throw new BusinessException(-1, "设备已存在，不能重复添加");
			
			BeanUtils.copyProperties(routeTemplateATMVO, routeTemplateAtm);
			routeTemplateAtm.setId(null);
			
			routeTemplateAtm.setAtmId(Long.parseLong(atmId));
			//得到ATM设备信息
			AtmDevice atmDevice = atmService.getById(routeTemplateAtm.getAtmId());
//			//得到所属行信息
//			Bank bank = bankService.getById(atmDevice.getBankId());
//			
//			//设置所属网点和所属顶级网点
//	        String[] parentsIds = bank.getParentIds().substring(1, bank.getParentIds().length()-1).split("/");
//			if (parentsIds.length > 1) {
//				routeTemplateAtm.setBankId(Long.parseLong(parentsIds[1]));
//			}else{
//				routeTemplateAtm.setBankId(routeTemplateAtm.getBankId());
//			}
			routeTemplateAtm.setBankId(atmDevice.getBankId());
			routeTemplateAtm.setSubBankId(atmDevice.getSubBankId());
			
			routeTemplateAtm.setSort(maxSort == null ? 1 : maxSort.intValue());
			
			boolean save = this.save(routeTemplateAtm);
	    	if(!save)
	    		throw new BusinessException(-1, "添加线路模板设备失败");
	    		
		}
		
		return Result.success();
	}

	
}
