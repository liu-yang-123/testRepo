package com.zcxd.voucher.service;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.common.util.Result;
import com.zcxd.voucher.constant.ImportantVoucherTypeEnum;
import com.zcxd.voucher.db.mapper.ImportantVoucherMapper;
import com.zcxd.voucher.db.model.ImportantVoucher;
import com.zcxd.voucher.vo.ImportantVoucherQueryVO;
import com.zcxd.voucher.vo.ImportantVoucherVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;


/**
 * @author zccc
 */
@Service
public class ImportantVoucherService extends ServiceImpl<ImportantVoucherMapper, ImportantVoucher> {
    /**
     * 新增重空
     * @param importantVoucherVo 属性
     * @return 结果
     */
    public Result saveImportantVoucher(ImportantVoucherVO importantVoucherVo) {
        Result result = checkRepeat(importantVoucherVo);
        if (result.isFailed()) {
            return result;
        }

        ImportantVoucher importantVoucher = new ImportantVoucher();
        BeanUtils.copyProperties(importantVoucherVo, importantVoucher);
        boolean save = save(importantVoucher);
        if (!save) {
            return Result.fail("新增失败");
        }
        return Result.success();
    }

    /**
     * 分页查询重空
     * @param page 页数
     * @param limit 数量
     * @param queryVo 查询条件
     * @return 查询结果
     * @throws IllegalAccessException 错误
     */
    public Result findListByPage(Integer page, Integer limit, ImportantVoucherQueryVO queryVo) {
        Page<ImportantVoucher> ipage = new Page<>(page,limit);
        LambdaQueryWrapper<ImportantVoucher> importantVoucherQueryWrapper = getImportantVoucherQueryWrapper(queryVo);
        return Result.success(baseMapper.selectPage(ipage, importantVoucherQueryWrapper));
    }

    /**
     * 更新重空信息
     * @param importantVoucherVO 属性
     * @return 结果
     */
    public Result updateImportantVoucherById(ImportantVoucherVO importantVoucherVO) {
        if (importantVoucherVO.getId() == null || getById(importantVoucherVO.getId()) == null) {
            return Result.fail("重空Id错误");
        }

        // 检查名称是否重复
        if (StringUtils.isNotEmpty(importantVoucherVO.getName())) {
            Result result = checkRepeat(importantVoucherVO);
            if (result.isFailed()) {
                return result;
            }
        }

        ImportantVoucher importantVoucher = new ImportantVoucher();
        BeanUtils.copyProperties(importantVoucherVO, importantVoucher);

        boolean update = updateById(importantVoucher);
        if(update) {
            return Result.success();
        }

        return Result.fail("修改重空失败");
    }

    /**
     * 删除重空信息
     * @param queryVo 属性
     * @return 结果
     */
    public Result deleteImportantVoucher(ImportantVoucherQueryVO queryVo) {
        LambdaQueryWrapper<ImportantVoucher> importantVoucherQueryWrapper = getImportantVoucherQueryWrapper(queryVo);
        ImportantVoucher importantVoucher = baseMapper.selectOne(importantVoucherQueryWrapper);
        if (importantVoucher == null) {
            return Result.fail("未找到目标重空");
        }

        int delete = baseMapper.delete(importantVoucherQueryWrapper);
        return delete > 0 ? Result.success() : Result.fail("删除失败");
    }

    /**
     * 构建查询的wrapper
     * @param queryVo 属性
     * @return 结果
     */
    private LambdaQueryWrapper<ImportantVoucher> getImportantVoucherQueryWrapper(ImportantVoucherQueryVO queryVo) {
        LambdaQueryWrapper<ImportantVoucher> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(queryVo.getId() != null, ImportantVoucher::getId, queryVo.getId());
        wrapper.eq(!StringUtils.isBlank(queryVo.getName()), ImportantVoucher::getName, queryVo.getName());
        wrapper.eq(queryVo.getType() != null, ImportantVoucher::getType, queryVo.getType());
        wrapper.eq(queryVo.getBankId() != null, ImportantVoucher::getBankId, queryVo.getBankId());
        wrapper.eq(ImportantVoucher::getDepartmentId, queryVo.getDepartmentId());
        return wrapper;
    }

    /**
     * 查询一个重空
     * @param voucherVO 条件
     * @return 结果
     */
    public ImportantVoucher selectOne(ImportantVoucherQueryVO voucherVO) {
        LambdaQueryWrapper<ImportantVoucher> importantVoucherQueryWrapper = getImportantVoucherQueryWrapper(voucherVO);
        return baseMapper.selectOne(importantVoucherQueryWrapper);
    }

    /**
     * 插入检查
     * @param importantVoucherVo 属性
     * @return 结果
     */
    private Result checkRepeat(ImportantVoucherVO importantVoucherVo) {
        LambdaQueryWrapper<ImportantVoucher> checkRepeatWrapper = getCheckRepeatWrapper(importantVoucherVo);
        ImportantVoucher importantVoucher = baseMapper.selectOne(checkRepeatWrapper);
        if (importantVoucher != null) {
            return Result.fail("重空名称重复：" + importantVoucher.getName());
        }

        // 重空类型
        if (importantVoucherVo.getType() != null) {
            if (StringUtils.isNotEmpty(ImportantVoucherTypeEnum.getTextByValue((int)importantVoucher.getType()))) {
                return Result.fail("重空类型错误");
            }
        }
        return Result.success();
    }

    /**
     * 构建重复性检查的wrapper
     * @param importantVoucherVo 属性
     * @return 结果
     */
    private LambdaQueryWrapper<ImportantVoucher> getCheckRepeatWrapper(ImportantVoucherVO importantVoucherVo) {
        LambdaQueryWrapper<ImportantVoucher> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(!StringUtils.isBlank(importantVoucherVo.getName()), ImportantVoucher::getName, importantVoucherVo.getName())
            .eq(ImportantVoucher::getBankId, importantVoucherVo.getBankId())
            .eq(ImportantVoucher::getDepartmentId, importantVoucherVo.getDepartmentId());
        return wrapper;
    }
}
