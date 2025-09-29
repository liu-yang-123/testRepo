package com.zcxd.pda.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.common.util.DateTimeUtil;
import com.zcxd.db.mapper.VaultOrderMapper;
import com.zcxd.db.model.VaultOrder;
import org.springframework.stereotype.Service;

/**
 * 
 * @ClassName VaultInoutService
 * @Description 出入库单管理服务
 * @author shijin
 * @Date 2021年5月18日上午16:20:05
 */
@Service
public class VaultOrderService extends ServiceImpl<VaultOrderMapper, VaultOrder> {

    /**
     * 分页查询出入库订单
     * @param status  - 订单状态
     * @param orderDate - 订单日期
     * @param page - 页码
     * @param limit - 记录数
     * @return
     */
    public IPage<VaultOrder> listOrderPage(Long departmentId,Integer status, Long orderDate, int page, int limit) {

        Page<VaultOrder> ipage =new Page<>(page,limit);
        QueryWrapper<VaultOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("department_id",departmentId);
        if (null != status) {
            queryWrapper.eq("status_t", status);
        }
        if (null != orderDate && 0L != orderDate) {
            long dtBegin = DateTimeUtil.getDailyStartTimeMs(orderDate);
            long dtEnd = DateTimeUtil.getDailyEndTimeMs(orderDate);
            queryWrapper.ge("order_date",dtBegin).le("order_date",dtEnd);
        }
        return baseMapper.selectPage(ipage,queryWrapper);
    }

}
