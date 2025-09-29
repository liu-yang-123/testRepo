package com.zcxd.voucher.service;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.common.util.DateTimeUtil;
import com.zcxd.common.util.Result;
import com.zcxd.voucher.db.mapper.ImportantVoucherWarehouseRecordMapper;
import com.zcxd.voucher.db.model.ImportantVoucherWarehouseRecord;
import com.zcxd.voucher.vo.ImportantVoucherWarehouseRecordQueryVO;
import org.springframework.stereotype.Service;

/**
 * @author zccc
 */
@Service
public class ImportantVoucherWarehouseRecordService extends
        ServiceImpl<ImportantVoucherWarehouseRecordMapper, ImportantVoucherWarehouseRecord> {
    /**
     * 分页查询记录
     * @param page 页数
     * @param limit 数量
     * @param queryVo 查询条件
     * @return 查询结果
     */
    public Result findListByPage(Integer page, Integer limit, ImportantVoucherWarehouseRecordQueryVO queryVo) {
        Page<ImportantVoucherWarehouseRecord> ipage = new Page<>(page,limit);
        LambdaQueryWrapper<ImportantVoucherWarehouseRecord> queryWrapper = getRecordQueryWrapper(queryVo);
        return Result.success(baseMapper.selectPage(ipage, queryWrapper));
    }

    /**
     * 构建查询的wrapper
     * @param queryVo 属性
     * @return 结果
     */
    private LambdaQueryWrapper<ImportantVoucherWarehouseRecord> getRecordQueryWrapper(ImportantVoucherWarehouseRecordQueryVO queryVo) {
        LambdaQueryWrapper<ImportantVoucherWarehouseRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(queryVo.getId() != null, ImportantVoucherWarehouseRecord::getId, queryVo.getId())
                .eq(StringUtils.isNotEmpty(queryVo.getVoucherName()), ImportantVoucherWarehouseRecord::getVoucherName, queryVo.getVoucherName())
                .eq(StringUtils.isNotEmpty(queryVo.getBankName()), ImportantVoucherWarehouseRecord::getBankName, queryVo.getBankName())
                .eq(StringUtils.isNotEmpty(queryVo.getVoucherType()), ImportantVoucherWarehouseRecord::getVoucherType, queryVo.getVoucherType())
                .eq(queryVo.getOperateType() != null, ImportantVoucherWarehouseRecord::getOperateType, queryVo.getOperateType())
                .eq(queryVo.getIsConfirm() != null, ImportantVoucherWarehouseRecord::getIsConfirm, queryVo.getIsConfirm())
                .and(queryVo.getConfirmTime() != null, wrapperItem -> {
                    wrapperItem.ge(ImportantVoucherWarehouseRecord::getConfirmTime, DateTimeUtil.getDailyStartTimeMs(queryVo.getConfirmTime()))
                            .le(ImportantVoucherWarehouseRecord::getConfirmTime, DateTimeUtil.getDailyEndTimeMs(queryVo.getConfirmTime()));
                })
                .ge(queryVo.getConfirmTimeStart() != null, ImportantVoucherWarehouseRecord::getConfirmTime, DateTimeUtil.getDailyStartTimeMs(queryVo.getConfirmTimeStart()))
                .le(queryVo.getConfirmTimeEnd() != null, ImportantVoucherWarehouseRecord::getConfirmTime, DateTimeUtil.getDailyEndTimeMs(queryVo.getConfirmTimeEnd()))
                .eq(StringUtils.isNotEmpty(queryVo.getTargetBankName()), ImportantVoucherWarehouseRecord::getTargetBankName, queryVo.getTargetBankName())
                .eq(ImportantVoucherWarehouseRecord::getDepartmentId, queryVo.getDepartmentId());
        return wrapper;
    }
}
