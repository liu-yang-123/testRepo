package com.zcxd.pda.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.common.constant.Constant;
import com.zcxd.common.util.DateTimeUtil;
import com.zcxd.db.mapper.DenomMapper;
import com.zcxd.db.mapper.VaultOrderMapper;
import com.zcxd.db.model.Denom;
import com.zcxd.db.model.VaultOrder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 
 * @ClassName DenomService
 * @Description 券别服务
 * @author shijin
 * @Date 2021年5月18日上午16:20:05
 */
@Service
public class DenomService extends ServiceImpl<DenomMapper, Denom> {


    /**
     * 根据券别面额查询ATM券别信息
     * @param denomValue
     * @return
     */
    public Denom getByAtmDenomValue(Integer denomValue) {
        Denom where = new Denom();
        where.setValue(new BigDecimal(denomValue));
        where.setVersion(0);
        //modify by shijin, 解决多条记录问题
        where.setDeleted(0);
        List<Denom> list = baseMapper.selectList(new QueryWrapper<>(where).last("limit 1"));
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    /**
     * 查询所有atm加钞券别
     * @return
     */
    public List<Denom> listAtmDenom() {
        QueryWrapper<Denom> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("version", 0).and(denomQueryWrapper -> denomQueryWrapper.eq("value",Constant.ATM_DENOM_100).or().eq("value",Constant.ATM_DENOM_10));
        return baseMapper.selectList(queryWrapper);
    }

    public List<Denom> listByCondition(Denom where) {
        return baseMapper.selectList(new QueryWrapper<>(where));
    }
}
