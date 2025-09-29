package com.zcxd.base.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.db.mapper.RouteTemplateBankMapper;
import com.zcxd.db.model.RouteTemplateBank;

/**
 * 
 * @ClassName RouteTemplateAtmService
 * @Description 线路管理模板网点服务类
 * @author 秦江南
 * @Date 2021年6月18日上午11:30:53
 */
@Service
public class RouteTemplateBankService extends ServiceImpl<RouteTemplateBankMapper, RouteTemplateBank>{
    /**
     * 
     * @Title getByBankId
     * @Description 根据网点查询线路规划
     * @param bankId
     * @return
     * @return 返回类型RouteTemplateBank
     */
    public RouteTemplateBank getByCondition(Long bankId,Long routeTemplateId) {
    	RouteTemplateBank where = new RouteTemplateBank();
    	if(bankId != null){
    		where.setBankId(bankId);
    	}
    	if(routeTemplateId != null){
    		where.setRouteTemplateId(routeTemplateId);
    	}
        List<RouteTemplateBank> list = baseMapper.selectList(new QueryWrapper<>(where));
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }
}
