package com.zcxd.pda.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.common.constant.DeleteFlagEnum;
import com.zcxd.db.mapper.BoxpackMapper;
import com.zcxd.db.model.Boxpack;

/**
 * 
 * @ClassName BoxpackService
 * @Description 箱包管理服务类
 * @author 秦江南
 * @Date 2021年11月26日下午4:14:32
 */
@Service
public class BoxpackService extends ServiceImpl<BoxpackMapper, Boxpack> {
	
    public List<Boxpack> listByCondition(Boxpack boxpack) {
    	Boxpack where = new Boxpack();
    	if(boxpack.getBankId() != null && boxpack.getBankId() != 0){
    		where.setBankId(boxpack.getBankId());
    	}
    	if(boxpack.getShareBankId() != null && boxpack.getShareBankId() != 0){
    		where.setShareBankId(boxpack.getShareBankId());
    	}
        where.setDeleted(DeleteFlagEnum.NOT.getValue());
        return baseMapper.selectList(new QueryWrapper<>(where));
    }
}
