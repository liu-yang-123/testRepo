package com.zcxd.base.service;

import com.zcxd.common.util.DateTimeUtil;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.db.mapper.CashboxPackRecordMapper;
import com.zcxd.db.model.CashboxPackRecord;

/**
 * 
 * @ClassName CashboxPackRecordService
 * @Description 清分装盒列表
 * @author 秦江南
 * @Date 2021年8月9日下午7:04:50
 */
@Service
public class CashboxPackRecordService extends ServiceImpl<CashboxPackRecordMapper, CashboxPackRecord>{

	/**
	 * 
	 * @Title findListByPage
	 * @Description 分页查询装盒记录
	 * @param page
	 * @param limit
	 * @param cashboxPackRecord
	 * @return
	 * @return 返回类型 IPage<CashboxPackRecord>
	 */
	public IPage<CashboxPackRecord> findListByPage(Integer page, Integer limit, CashboxPackRecord cashboxPackRecord) {
		Page<CashboxPackRecord> ipage=new Page<CashboxPackRecord>(page,limit);
    	QueryWrapper<CashboxPackRecord> queryWrapper = new QueryWrapper<>();

		if (cashboxPackRecord != null) {
			if (cashboxPackRecord.getDepartmentId() != null) {
				queryWrapper.eq("department_id", cashboxPackRecord.getDepartmentId());
			}
			if(cashboxPackRecord.getTaskDate() != null){
				queryWrapper.le("task_date", DateTimeUtil.getDailyEndTimeMs(cashboxPackRecord.getTaskDate()));
				queryWrapper.ge("task_date",DateTimeUtil.getDailyStartTimeMs(cashboxPackRecord.getTaskDate()));
			}
			
			if(cashboxPackRecord.getPackTime() != null){
				queryWrapper.le("pack_time",cashboxPackRecord.getPackTime() + 86400000);
				queryWrapper.ge("pack_time",cashboxPackRecord.getPackTime());
			}
			
			if(cashboxPackRecord.getBankId() != null){
				queryWrapper.eq("bank_id",cashboxPackRecord.getBankId());
			}
			
			if(cashboxPackRecord.getDenomId() != null){
				queryWrapper.eq("denom_id",cashboxPackRecord.getDenomId());
			}
			
			if(cashboxPackRecord.getClearManId() != null){
				queryWrapper.and(wrapper -> wrapper.eq("clear_man_id",cashboxPackRecord.getClearManId()).or().eq("check_man_id", cashboxPackRecord.getClearManId()));
			}
			
			if(cashboxPackRecord.getStatusT() != null){
				queryWrapper.eq("status_t",cashboxPackRecord.getStatusT());
			}
			
			if(!StringUtils.isBlank(cashboxPackRecord.getBoxNo())){
				queryWrapper.eq("box_no",cashboxPackRecord.getBoxNo());
			}
		}

    	queryWrapper.eq("deleted", 0);
    	queryWrapper.orderBy(true,false,"id");
		
        return baseMapper.selectPage(ipage,queryWrapper);
	}
	
	/**
	 * 
	 * @Title listByRoute
	 * @Description 查询线路配钞详情
	 * @param routeId
	 * @return
	 * @return 返回类型 List<CashboxPackRecord>
	 */
    public List<CashboxPackRecord> listByRoute(Long routeId) {
        CashboxPackRecord where = new CashboxPackRecord();
        where.setRouteId(routeId);
        return baseMapper.selectList(new QueryWrapper<>(where).orderBy(true,false,"bank_id").orderBy(true,false,"denom_id"));
    }
}
