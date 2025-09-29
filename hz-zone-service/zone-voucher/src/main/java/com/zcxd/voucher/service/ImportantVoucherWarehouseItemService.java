package com.zcxd.voucher.service;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.common.util.DateTimeUtil;
import com.zcxd.common.util.Result;
import com.zcxd.voucher.db.mapper.ImportantVoucherWarehouseItemMapper;
import com.zcxd.voucher.db.model.ImportantVoucherWarehouseItem;
import com.zcxd.voucher.vo.ImportantVoucherWarehouseItemQueryVO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zccc
 */
@Service
public class ImportantVoucherWarehouseItemService extends ServiceImpl<ImportantVoucherWarehouseItemMapper, ImportantVoucherWarehouseItem> {
    /**
     * 新增重空出入库记录
     * @param item 属性
     * @return 结果
     */
    public Result saveItem(ImportantVoucherWarehouseItem item) {
        Result result = checkOutRepeat(item);
        if (result.isFailed()) {
            return result;
        }

        boolean save = save(item);
        if (!save) {
            return Result.fail("新增失败");
        }
        return Result.success();
    }

    /**
     * 分页查询记录
     * @param page 页数
     * @param limit 数量
     * @param queryVo 查询条件
     * @return 查询结果
     */
    public Result findListByPage(Integer page, Integer limit, ImportantVoucherWarehouseItemQueryVO queryVo) {
        Page<ImportantVoucherWarehouseItem> ipage = new Page<>(page,limit);
        LambdaQueryWrapper<ImportantVoucherWarehouseItem> queryWrapper = getImportantVoucherQueryWrapper(queryVo);
        return Result.success(baseMapper.selectPage(ipage, queryWrapper));
    }

    /**
     * 查询记录
     * @param queryVo 查询条件
     * @return 列表
     */
    public List<ImportantVoucherWarehouseItem> findList(ImportantVoucherWarehouseItemQueryVO queryVo) {
        LambdaQueryWrapper<ImportantVoucherWarehouseItem> queryWrapper = getImportantVoucherQueryWrapper(queryVo);
        return baseMapper.selectList(queryWrapper);
    }

    /**
     * 更新重空信息
     * @param item 属性
     * @return 结果
     */
    public Result updateItemById(ImportantVoucherWarehouseItem item) {
        ImportantVoucherWarehouseItem warehouseItem = getById(item.getId());
        if (warehouseItem == null) {
            return Result.fail("记录选择错误");
        }
        if (warehouseItem.getIsConfirm() == 1) {
            return Result.fail("已审核通过，无法修改");
        }

        boolean update = updateById(item);
        if(update) {
            return Result.success();
        }

        return Result.fail("修改记录失败");
    }

    /**
     * 删除记录信息
     * @param queryVo 属性
     * @return 结果
     */
    public Result deleteItem(ImportantVoucherWarehouseItemQueryVO queryVo) {
        ImportantVoucherWarehouseItem importantVoucherWarehouseItem = baseMapper.selectById(queryVo.getId());
        if (importantVoucherWarehouseItem == null) {
            return Result.fail("未找到目标重空操作记录");
        }

        int delete = baseMapper.deleteById(queryVo.getId());
        return delete > 0 ? Result.success(importantVoucherWarehouseItem) : Result.fail("删除失败");
    }

    /**
     * 构建查询的wrapper
     * @param queryVo 属性
     * @return 结果
     */
    private LambdaQueryWrapper<ImportantVoucherWarehouseItem> getImportantVoucherQueryWrapper(ImportantVoucherWarehouseItemQueryVO queryVo) {
        LambdaQueryWrapper<ImportantVoucherWarehouseItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(queryVo.getId() != null, ImportantVoucherWarehouseItem::getId, queryVo.getId())
                .eq(StringUtils.isNotEmpty(queryVo.getVoucherName()), ImportantVoucherWarehouseItem::getVoucherName, queryVo.getVoucherName())
                .eq(StringUtils.isNotEmpty(queryVo.getBankName()), ImportantVoucherWarehouseItem::getBankName, queryVo.getBankName())
                .eq(StringUtils.isNotEmpty(queryVo.getOrderNumber()), ImportantVoucherWarehouseItem::getOrderNumber, queryVo.getOrderNumber())
                .eq(StringUtils.isNotEmpty(queryVo.getVoucherType()), ImportantVoucherWarehouseItem::getVoucherType, queryVo.getVoucherType())
                .eq(queryVo.getOperateType() != null, ImportantVoucherWarehouseItem::getOperateType, queryVo.getOperateType())
                .eq(queryVo.getIsConfirm() != null, ImportantVoucherWarehouseItem::getIsConfirm, queryVo.getIsConfirm())
                .and(queryVo.getCreateTime() != null, wrapperItem -> {
                    wrapperItem.ge(ImportantVoucherWarehouseItem::getCreateTime, DateTimeUtil.getDailyStartTimeMs(queryVo.getCreateTime()))
                            .le(ImportantVoucherWarehouseItem::getCreateTime, DateTimeUtil.getDailyEndTimeMs(queryVo.getCreateTime()));
                })
                .ge(queryVo.getCreateTimeStart() != null, ImportantVoucherWarehouseItem::getCreateTime, DateTimeUtil.getDailyStartTimeMs(queryVo.getCreateTimeStart()))
                .le(queryVo.getCreateTimeEnd() != null, ImportantVoucherWarehouseItem::getCreateTime, DateTimeUtil.getDailyEndTimeMs(queryVo.getCreateTimeEnd()))
                .eq(StringUtils.isNotEmpty(queryVo.getTargetBankName()), ImportantVoucherWarehouseItem::getTargetBankName, queryVo.getTargetBankName())
                .eq(ImportantVoucherWarehouseItem::getDepartmentId, queryVo.getDepartmentId());
        return wrapper;
    }

    /**
     * 插入检查
     * @param item 属性
     * @return 结果
     */
    private Result checkOutRepeat(ImportantVoucherWarehouseItem item) {
        LambdaQueryWrapper<ImportantVoucherWarehouseItem> checkRepeatWrapper = getCheckRepeatWrapper(item);
        List<ImportantVoucherWarehouseItem> importantVoucherWarehouseItems = baseMapper.selectList(checkRepeatWrapper);
        if (!importantVoucherWarehouseItems.isEmpty()) {
            return Result.fail("已有同一重空：" + item.getVoucherName() + " 部分号段重复出库中");
        }

        return Result.success();
    }

    /**
     * 构建重复性检查的wrapper
     * @param item 属性
     * @return 结果
     */
    private LambdaQueryWrapper<ImportantVoucherWarehouseItem> getCheckRepeatWrapper(ImportantVoucherWarehouseItem item) {
        LambdaQueryWrapper<ImportantVoucherWarehouseItem> wrapper = new LambdaQueryWrapper<>();
        // 同一银行下相同的凭证出库时进行号段检查
        wrapper.eq(!StringUtils.isBlank(item.getBankName()), ImportantVoucherWarehouseItem::getBankName, item.getBankName())
                .eq(!StringUtils.isBlank(item.getVoucherName()), ImportantVoucherWarehouseItem::getVoucherName, item.getVoucherName())
                .eq(ImportantVoucherWarehouseItem::getDepartmentId, item.getDepartmentId())
                .and(w -> {
                    w.or(item1 -> {
                        item1.le(ImportantVoucherWarehouseItem::getStart, item.getStart())
                                .ge(ImportantVoucherWarehouseItem::getEnd, item.getStart());
                    });
                    w.or(item1 -> {
                        item1.le(ImportantVoucherWarehouseItem::getStart, item.getEnd())
                                .ge(ImportantVoucherWarehouseItem::getEnd, item.getEnd());
                    });
                    w.or(item1 -> {
                        item1.ge(ImportantVoucherWarehouseItem::getStart, item.getStart())
                                .le(ImportantVoucherWarehouseItem::getEnd, item.getEnd());
                    });
                });
        return wrapper;
    }
}
