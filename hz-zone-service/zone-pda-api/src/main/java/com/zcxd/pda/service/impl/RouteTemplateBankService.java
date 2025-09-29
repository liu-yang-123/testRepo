package com.zcxd.pda.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.db.mapper.RouteTemplateBankMapper;
import com.zcxd.db.model.RouteTemplateBank;

/**
 * 
 * @ClassName RouteTemplateBankService
 * @Description 早送晚收线路规划服务类
 * @author 秦江南
 * @Date 2021年11月29日下午4:14:13
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
    public RouteTemplateBank getByBankId(Long bankId) {
    	RouteTemplateBank where = new RouteTemplateBank();
    	where.setBankId(bankId);
        List<RouteTemplateBank> list = baseMapper.selectList(new QueryWrapper<>(where));
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }
}
