package com.zcxd.base.service;

import java.util.*;

import com.zcxd.base.config.UserContextHolder;
import com.zcxd.base.dto.BankCardHandOverDTO;
import com.zcxd.db.model.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.base.exception.BusinessException;
import com.zcxd.common.constant.BankCardStatusEnum;
import com.zcxd.common.util.DateTimeUtil;
import com.zcxd.common.util.Result;
import com.zcxd.db.mapper.AtmTaskCardMapper;

/**
 * 
 * @ClassName ATMTaskCardService
 * @Description 取吞卡服务类
 * @author 秦江南
 * @Date 2021年7月25日上午11:53:17
 */
@Service
public class ATMTaskCardService extends ServiceImpl<AtmTaskCardMapper, AtmTaskCard>{
	
	@Autowired
	private RouteService routeService;
	
	
	/**
	 * 
	 * @Title findListByPage
	 * @Description 分页查询吞没卡
	 * @param page
	 * @param limit
	 * @param where
	 * @return
	 * @return 返回类型 IPage<AtmTaskCard>
	 */
	public IPage<AtmTaskCard> findListByPage(Integer page, Integer limit, AtmTaskCard where) {
		Page<AtmTaskCard> ipage=new Page<AtmTaskCard>(page,limit);
    	QueryWrapper<AtmTaskCard> queryWrapper=new QueryWrapper<AtmTaskCard>();
    	if(where != null){
    		if (where.getDepartmentId() != null) {
				queryWrapper.eq("department_id", where.getDepartmentId());
			}
    		if (where.getBankId() != null) {
				queryWrapper.eq("bank_id", where.getBankId());
			}
    		if(where.getStatusT() != null){
				queryWrapper.eq("status_t", where.getStatusT());
			}

    		if (!StringUtils.isEmpty(where.getRouteNo())) {
				queryWrapper.eq("route_no", where.getRouteNo());
			}

    		if (where.getCreateTime() != null) {
				queryWrapper.ge("create_time", DateTimeUtil.getDailyStartTimeMs(where.getCreateTime()));
				queryWrapper.le("create_time", DateTimeUtil.getDailyEndTimeMs(where.getCreateTime()));
			}

    		if(!StringUtils.isEmpty(where.getDeliverRouteNo())){
				queryWrapper.eq("deliver_route_no", where.getDeliverRouteNo());
			}
    		if(!StringUtils.isEmpty(where.getCardNo())){
				queryWrapper.likeLeft("card_no", where.getCardNo());
			}
    		if(!StringUtils.isEmpty(where.getDeliverDay())){
				queryWrapper.eq("deliver_day", where.getDeliverDay());
			}
    	}

    	queryWrapper.orderBy(true,false,"create_time");
        return baseMapper.selectPage(ipage, queryWrapper);
	}

	/**
	 * 
	 * @param deliverDay 
	 * @Title batchDistribute
	 * @Description 批量分配
	 * @return
	 * @return 返回类型 Result
	 */
	@Transactional(rollbackFor=Exception.class)
	public Result batchDistribute(Long deliverDay) {
		List<AtmTaskCard> atmTaskCardList = baseMapper.selectList((QueryWrapper)Wrappers.query()
				.eq("status_t", BankCardStatusEnum.COLLECT.getValue()).eq("deliver_day", DateTimeUtil.timeStampMs2Date(deliverDay,"yyyy-MM-dd")));
		if(atmTaskCardList == null || atmTaskCardList.size() == 0){
			return Result.fail(DateTimeUtil.getDateTimeDisplayString(deliverDay) + "没有吞卡需要分配");
		}
		for (AtmTaskCard atmTaskCard : atmTaskCardList) {
			if(StringUtils.isBlank(atmTaskCard.getDeliverRouteNo())){
				throw new BusinessException(-1,"吞卡线路编号为空");
			}
	    	Route route = new Route();
	    	route.setRouteDate(DateTimeUtil.getDailyStartTimeMs(deliverDay));
	    	route.setRouteNo(atmTaskCard.getDeliverRouteNo());
	    	List<Route> routeByCondition = routeService.getRouteByCondition(route);
	    	if(routeByCondition == null || routeByCondition.size() == 0){
	    		throw new BusinessException(-1,DateTimeUtil.getDateTimeDisplayString(route.getRouteDate()) + route.getRouteNo() + "线路不存在,请先创建线路");
    		}
	    	atmTaskCard.setDeliverRouteId(routeByCondition.get(0).getId());
	    	atmTaskCard.setStatusT(BankCardStatusEnum.ALLOCATION.getValue());
	    	boolean update = this.updateById(atmTaskCard);
	    	
	    	if(!update)
	    		throw new BusinessException(-1,"分配失败");
		}
		
		return Result.success();
	}

	/**
	 * 吞没卡交接
	 * @param bankCardHandOverDTO
	 * @return
	 */
	@Transactional
	public void updateBankCardHandover(BankCardHandOverDTO bankCardHandOverDTO) throws BusinessException{
		if (bankCardHandOverDTO.getCardIdList() == null || bankCardHandOverDTO.getCardIdList().size() == 0) {
			throw new BusinessException(-1,"缺少吞没卡");
		}
		try {
			if (bankCardHandOverDTO.getType() == 0) { //收卡
				for (Long id : bankCardHandOverDTO.getCardIdList()) {
					AtmTaskCard atmTaskCard = baseMapper.selectById(id);
					if (null == atmTaskCard) {
						throw new BusinessException(-1,"无效id");
					}
					if (atmTaskCard.getStatusT() == BankCardStatusEnum.RETRIVE.getValue()
							|| atmTaskCard.getStatusT() == BankCardStatusEnum.DISPATCH.getValue()) {
						AtmTaskCard newAtmTaskCard = new AtmTaskCard();
						newAtmTaskCard.setId(id);
						newAtmTaskCard.setStatusT(BankCardStatusEnum.COLLECT.getValue());
						newAtmTaskCard.setCollectTime(System.currentTimeMillis());
						newAtmTaskCard.setCollectManA(UserContextHolder.getUserId());
						baseMapper.updateById(newAtmTaskCard);
					} else {
						throw new BusinessException(-1,"卡片状态不正确");
					}
				}
			} else { //配卡
				for (Long id : bankCardHandOverDTO.getCardIdList()) {
					AtmTaskCard atmTaskCard = baseMapper.selectById(id);
					if (null == atmTaskCard) {
						throw new BusinessException(-1,"无效id");
					}
					if (atmTaskCard.getStatusT() == BankCardStatusEnum.ALLOCATION.getValue()) {
						AtmTaskCard newAtmTaskCard = new AtmTaskCard();
						newAtmTaskCard.setId(id);
						newAtmTaskCard.setStatusT(BankCardStatusEnum.DISPATCH.getValue());
						newAtmTaskCard.setDispatchTime(System.currentTimeMillis());
						newAtmTaskCard.setDispatchManA(UserContextHolder.getUserId());
						baseMapper.updateById(newAtmTaskCard);
					} else {
						throw new BusinessException(-1,"卡片状态不正确");
					}
				}
			}
		} catch (Exception e) {
			throw new BusinessException(-1,e.getMessage());
		}
	}

	/**
	 * 
	 * @Title getTaskCardByCondition
	 * @Description 根据条件查询吞没卡数据
	 * @param taskCard
	 * @return
	 * @return 返回类型 List<AtmTaskCard>
	 */
	public List<AtmTaskCard> getTaskCardByCondition(AtmTaskCard taskCard) {
    	QueryWrapper<AtmTaskCard> queryWrapper=new QueryWrapper<AtmTaskCard>();
    	if(taskCard != null){
    		if (taskCard.getDepartmentId() != null) {
				queryWrapper.eq("department_id", taskCard.getDepartmentId());
			}
    		if (taskCard.getBankId() != null) {
				queryWrapper.eq("bank_id", taskCard.getBankId());
			}
    		if(taskCard.getStatusT() != null){
				queryWrapper.eq("status_t", taskCard.getStatusT());
			}

    		if (!StringUtils.isEmpty(taskCard.getRouteNo())) {
				queryWrapper.eq("route_no", taskCard.getRouteNo());
			}

    		if (taskCard.getCreateTime() != null) {
				queryWrapper.ge("create_time", DateTimeUtil.getDailyStartTimeMs(taskCard.getCreateTime()));
				queryWrapper.le("create_time", DateTimeUtil.getDailyEndTimeMs(taskCard.getCreateTime()));
			}
    		
    		if (taskCard.getCollectTime() != null) {
				queryWrapper.ge("collect_time", DateTimeUtil.getDailyStartTimeMs(taskCard.getCollectTime()));
				queryWrapper.le("collect_time", DateTimeUtil.getDailyEndTimeMs(taskCard.getCollectTime()));
			}
    		
    		if (taskCard.getReceiveTime() != null) {
				queryWrapper.ge("receive_time", DateTimeUtil.getDailyStartTimeMs(taskCard.getReceiveTime()));
				queryWrapper.le("receive_time", DateTimeUtil.getDailyEndTimeMs(taskCard.getReceiveTime()));
			}

    		if(!StringUtils.isEmpty(taskCard.getDeliverRouteNo())){
				queryWrapper.eq("deliver_route_no", taskCard.getDeliverRouteNo());
			}
    		if(!StringUtils.isEmpty(taskCard.getCardNo())){
				queryWrapper.likeLeft("card_no", taskCard.getCardNo());
			}
    		if(!StringUtils.isEmpty(taskCard.getDeliverDay())){
				queryWrapper.eq("deliver_day", taskCard.getDeliverDay());
			}
    	}

    	queryWrapper.orderBy(true,true,"deliver_route_no * 1");
    	queryWrapper.orderBy(true,false,"create_time");
        return baseMapper.selectList(queryWrapper);
	}

	/**
	 * 
	 * @Title listCollectCardByDay
	 * @Description 回收汇总
	 * @param departmentId
	 * @param bankId
	 * @param day
	 * @return
	 * @return 返回类型 List<AtmTaskCard>
	 */
	public List<AtmTaskCard> listCollectCardByDay(Long departmentId, Long bankId, Long day) {
		QueryWrapper<AtmTaskCard> queryWrapper=new QueryWrapper<AtmTaskCard>();
		queryWrapper.eq("department_id", departmentId)
				.ge("collect_time", DateTimeUtil.getDailyStartTimeMs(day))
				.le("collect_time", DateTimeUtil.getDailyEndTimeMs(day))
				.and(wrapper -> wrapper.eq("status_t",BankCardStatusEnum.COLLECT.getValue())
						.or().eq("status_t",BankCardStatusEnum.ALLOCATION.getValue()));
		if(bankId != null){
			queryWrapper.eq("bank_id", bankId);
		}

		queryWrapper.orderBy(true,true,"deliver_route_no * 1");
		queryWrapper.orderBy(true,false,"create_time");
		return baseMapper.selectList(queryWrapper);
	}

	/**
	 * 
	 * @Title listDeliverCardByDay
	 * @Description 派送汇总
	 * @param departmentId
	 * @param bankId
	 * @param day
	 * @return
	 * @return 返回类型 List<AtmTaskCard>
	 */
	public List<AtmTaskCard> listDeliverCardByDay(Long departmentId, Long bankId, Long day) {
		QueryWrapper<AtmTaskCard> queryWrapper=new QueryWrapper<AtmTaskCard>();
		queryWrapper.eq("department_id", departmentId)
				.eq("deliver_day",DateTimeUtil.timeStampMs2Date(day,"yyyy-MM-dd"))
				.and(wrapper -> wrapper.eq("status_t",BankCardStatusEnum.ALLOCATION.getValue())
						.or().eq("status_t",BankCardStatusEnum.DISPATCH.getValue()));
		
		if(bankId != null){
			queryWrapper.eq("bank_id", bankId);
		}
		
		queryWrapper.orderBy(true,true,"deliver_route_no * 1");
		queryWrapper.orderBy(true,false,"create_time");
		return baseMapper.selectList(queryWrapper);
	}
}
