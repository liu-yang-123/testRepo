package com.zcxd.voucher.service;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.zcxd.common.util.Result;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.voucher.db.mapper.ImportantVoucherNumberSegmentMapper;
import com.zcxd.voucher.db.model.ImportantVoucherNumberSegment;
import com.zcxd.voucher.vo.ImportantVoucherNumberSegmentQueryVO;
import com.zcxd.voucher.vo.ImportantVoucherNumberSegmentVO;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zccc
 */
@Service
public class ImportantVoucherNumberSegmentService extends ServiceImpl<ImportantVoucherNumberSegmentMapper, ImportantVoucherNumberSegment> {
    /**
     * 新增重空分段
     * @param segmentVO
     * @return
     */
    public Result saveImportantVoucherNumberSegment(ImportantVoucherNumberSegmentVO segmentVO) {
        // check
        Result result = checkImportantVoucherNumberSegmentIn(segmentVO);
        if (result.isFailed()) {
            return result;
        }

        ImportantVoucherNumberSegment segment = (ImportantVoucherNumberSegment) result.getData();

        boolean save = save(segment);
        if (!save) {
            return Result.fail("新增失败");
        }
        return Result.success();
    }

    /**
     * 检查重空分段入库是否合法
     * @param segmentVO 条件
     * @return 结果
     */
    public Result checkImportantVoucherNumberSegmentIn(ImportantVoucherNumberSegmentVO segmentVO) {
        LambdaQueryWrapper<ImportantVoucherNumberSegment> checkRepeatQueryWrapper = getCheckRepeatQueryWrapper(segmentVO);
        ImportantVoucherNumberSegment segment = baseMapper.selectOne(checkRepeatQueryWrapper);
        if (segment != null) {
            return Result.fail("存在交叉号段");
        }

        ImportantVoucherNumberSegment importantVoucherNumberSegment = new ImportantVoucherNumberSegment();
        BeanUtils.copyProperties(segmentVO, importantVoucherNumberSegment);
        boolean b = importantVoucherNumberSegment.countSegmentNum();
        if (!b) {
            return Result.fail("分段起、始存在错误，新增失败");
        }

        return Result.success(importantVoucherNumberSegment);
    }

    /**
     * 出库检查
     * @param segmentVO 属性
     * @return 结果
     */
    public Result checkImportantVoucherNumberSegmentOut(ImportantVoucherNumberSegmentVO segmentVO) {
        Long start = segmentVO.getNumberStart();
        Long end = segmentVO.getNumberEnd();
        if (start > end) {
            return Result.fail("号段设置错误");
        }

        // 搜索当前所有同凭证的内容
        List<ImportantVoucherNumberSegment> segments = findByVoucherIdAndSortedBySegmentStart(
                segmentVO.getVoucherId(), segmentVO.getDepartmentId());

        // 号段起止设置错误判断
        if (!segments.isEmpty()) {
            ImportantVoucherNumberSegment segment = segments.get(0);
            ImportantVoucherNumberSegment segment1 = segments.get(segments.size() - 1);
            if (start < segment.getNumberStart()) {
                return Result.fail("号段起始设置错误");
            }
            if (end > segment1.getNumberEnd()) {
                return Result.fail("号段结束设置错误");
            }
        }

        // 构建对重空分段的出库标识
        List<VoucherSegmentOutRecord> records = buildOutRecords(start, end, segments);

        if (records == null) {
            return Result.fail("指定重空号段与库存不符");
        }
        return Result.success(records);
    }

    /**
     * 通过重空Id获取分段，并按照分段号排序
     * @param voucherId 重空Id
     * @param departmentId 部门号
     * @return 分段列表
     */
    private List<ImportantVoucherNumberSegment> findByVoucherIdAndSortedBySegmentStart(Long voucherId, Integer departmentId) {
        ImportantVoucherNumberSegmentQueryVO queryVO = new ImportantVoucherNumberSegmentQueryVO();
        queryVO.setDepartmentId(departmentId);
        queryVO.setVoucherId(voucherId);
        LambdaQueryWrapper<ImportantVoucherNumberSegment> searchQueryWrapper = getSearchQueryWrapper(queryVO);
        return baseMapper.selectList(searchQueryWrapper).stream().sorted(
                new Comparator<ImportantVoucherNumberSegment>() {
                    @Override
                    public int compare(ImportantVoucherNumberSegment o1, ImportantVoucherNumberSegment o2) {
                        return o1.getNumberStart() > o2.getNumberStart() ? 1 : 0;
                    }
                }).collect(Collectors.toList());
    }

    /**
     * 构建出库记录，如果库中分段无法囊括待出库要求，则返回null
     * @param start 号段开始
     * @param end 号段结束
     * @param segments 分段集合
     * @return 记录集合
     */
    private List<VoucherSegmentOutRecord> buildOutRecords(Long start, Long end, List<ImportantVoucherNumberSegment> segments) {
        List<VoucherSegmentOutRecord> outRecordList = new ArrayList<>();
        for (int i = 0; (i < segments.size()) && (start <= end); i++) {
            ImportantVoucherNumberSegment segment = segments.get(i);
            Long numberStart = segment.getNumberStart();
            Long numberEnd = segment.getNumberEnd();

            // 存在交叉，start设置为下一段起点
            if (numberStart <= end && start <= numberEnd && start >= numberStart) {
                VoucherSegmentOutRecord voucherSegmentOutRecord = new VoucherSegmentOutRecord(segment.getId());
                voucherSegmentOutRecord.setStart(start);
                // 设置变更边界
                voucherSegmentOutRecord.setStart(start);
                voucherSegmentOutRecord.setEnd(end > numberEnd ? numberEnd : end);
                start = numberEnd + 1;
                outRecordList.add(voucherSegmentOutRecord);
            }
        }
        if (start <= end) {
            return null;
        }
        return outRecordList;
    }

    /**
     * 重空出库
     * @param voucherId 重空Id
     * @param start 开始
     * @param end 结束
     * @param departmentId 部门Id
     * @return 出库结果
     */
    public Result importantVoucherNumberSegmentOut(Long voucherId, Long start, Long end, Integer departmentId) {
        List<ImportantVoucherNumberSegment> segments = findByVoucherIdAndSortedBySegmentStart(voucherId, departmentId);
        List<VoucherSegmentOutRecord> records = buildOutRecords(start, end, segments);

        Result result;
        try {
            result = doImportantVoucherNumberSegmentOut(records);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
        return result;
    }


    /**
     * 重空分段出库
     * @param records 分段信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Result doImportantVoucherNumberSegmentOut(List<VoucherSegmentOutRecord> records) throws Exception {
        if (records == null || records.isEmpty()) {
            return Result.fail("无待出库重空");
        }
        List<Long> ids = records.stream().map(VoucherSegmentOutRecord::getSegementId).collect(Collectors.toList());
        List<ImportantVoucherNumberSegment> segments = baseMapper.selectBatchIds(ids);

        for (int i = 0; i < ids.size(); i ++) {
            VoucherSegmentOutRecord record = records.get(i);
            ImportantVoucherNumberSegment segment = segments.get(i);

            // 删除原分段
            int deleteRes = baseMapper.deleteById(segment.getId());
            if (deleteRes <= 0) {
                throw new Exception("重空出库失败： 未能删除库中记录" + segment.getId());
            }

            // 创建新纪录
            // 分别对出库号段相对库中原原号段的左侧和右侧做新建操作，模拟切分
            boolean b = importantVoucherNumberSegmentOut(segment.getNumberStart(), record.getStart(), segment, true);
            if (!b) {
                throw new Exception("重空出库失败： 未能创建新记录" + segment.getName() + "~" + segment.getNumberStart() + "-" + record.getStart());
            }
            boolean b1 = importantVoucherNumberSegmentOut(record.getEnd(), segment.getNumberEnd(), segment, false);
            if (!b1) {
                throw new Exception("重空出库失败： 未能创建新记录" + segment.getName() + "~" + segment.getNumberEnd() + "-" + record.getEnd());
            }
        }
        return Result.success();
    }

    /**
     *
     * @param start 起
     * @param end 终
     * @param segment 分段
     * @param flag 判断新增号段是出库号段的左侧还是右侧
     *             true 左侧
     *             false 右侧
     * @return 结果
     */
    private boolean importantVoucherNumberSegmentOut(Long start, Long end, ImportantVoucherNumberSegment segment, boolean flag) {
        if (start.equals(end)) {
            return true;
        }

        ImportantVoucherNumberSegment newSegment = new ImportantVoucherNumberSegment();
        BeanUtils.copyProperties(segment, newSegment);

        if (flag) {
            end --;
        } else {
            start ++;
        }

        newSegment.setId(null);
        newSegment.setNumberStart(start);
        newSegment.setNumberEnd(end);
        newSegment.countSegmentNum();
        return save(newSegment);
    }

    /**
     * 分页查询持枪证
     * @param page 页数
     * @param limit 数量
     * @param queryVo 查询条件
     * @return 查询结果
     * @throws IllegalAccessException 错误
     */
    public Result findListByPage(Integer page, Integer limit, ImportantVoucherNumberSegmentQueryVO queryVo) {
        Page<ImportantVoucherNumberSegment> ipage = new Page<>(page,limit);
        LambdaQueryWrapper<ImportantVoucherNumberSegment> queryWrapper = getSearchQueryWrapper(queryVo);
        return Result.success(baseMapper.selectPage(ipage, queryWrapper));
    }

    /**
     * 搜索
     * @param queryVo 查询条件
     * @return 结果
     */
    public Result findList(ImportantVoucherNumberSegmentQueryVO queryVo) {
        LambdaQueryWrapper<ImportantVoucherNumberSegment> queryWrapper = getSearchQueryWrapper(queryVo);
        return Result.success(baseMapper.selectList(queryWrapper));
    }

    /**
     * 根据voucherId查询分段
     * @param ids id集合
     * @param departmentId 部门id
     * @return 结果
     */
    public List<ImportantVoucherNumberSegment> findListByVoucherIds(List<Long> ids, Integer departmentId) {
        LambdaQueryWrapper<ImportantVoucherNumberSegment> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(ImportantVoucherNumberSegment::getVoucherId, ids)
                .eq(ImportantVoucherNumberSegment::getDepartmentId, departmentId);
        return baseMapper.selectList(wrapper);
    }

    /**
     * 更新重空信息，用于直接修改分段信息，包括修改起始数量等
     * @param segmentVO 属性
     * @return 结果
     */
    public Result updateImportantVoucherNumberSegmentById(ImportantVoucherNumberSegmentVO segmentVO) {
        if (segmentVO.getId() == null || getById(segmentVO.getId()) == null) {
            return Result.fail("重空分段Id错误");
        }

        // 起始、终止测试
        ImportantVoucherNumberSegment toUpdate = getById(segmentVO.getId());
        Long numberStart = segmentVO.getNumberStart();
        Long numberEnd = segmentVO.getNumberEnd();
        Long start = numberStart != null ? numberStart : toUpdate.getNumberStart();
        Long end = numberEnd != null ? numberEnd : toUpdate.getNumberEnd();

        if (start > end) {
            return Result.fail("分段起、始存在错误，修改失败");
        }

        ImportantVoucherNumberSegment segment = new ImportantVoucherNumberSegment();
        BeanUtils.copyProperties(segmentVO, segment);
        segment.setCount(end - start + 1);

        boolean update = updateById(segment);
        if(update) {
            return Result.success();
        }

        return Result.fail("修改重空失败");
    }

    /**
     * 更新重空信息，用于修改基础属性，可同时修改多个
     * @param entity 更新属性
     * @param queryVO 搜索属性
     * @return 结果
     */
    public Result updateImportantVoucherNumberSegment(ImportantVoucherNumberSegment entity, ImportantVoucherNumberSegmentQueryVO queryVO) {
        boolean update = update(entity, getSearchQueryWrapper(queryVO));
        if(update) {
            return Result.success();
        }

        return Result.fail("修改重空分段失败");
    }

    /**
     * 删除重空分段
     * @param queryVo 属性
     * @return 结果
     */
    public Result deleteImportantVoucherNumberSegment(ImportantVoucherNumberSegmentQueryVO queryVo) {
        LambdaQueryWrapper<ImportantVoucherNumberSegment> queryWrapper = getSearchQueryWrapper(queryVo);
        List<ImportantVoucherNumberSegment> importantVoucherNumberSegments = baseMapper.selectList(queryWrapper);
        if (importantVoucherNumberSegments.isEmpty()) {
            return Result.fail("未找到目标重空分段");
        }

        int delete = baseMapper.delete(queryWrapper);
        return delete > 0 ? Result.success() : Result.fail("删除失败");
    }

    /**
     * 构建插入重复性检查的查询条件
     * @param segmentVO 条件
     * @return 查询条件
     */
    private LambdaQueryWrapper<ImportantVoucherNumberSegment> getCheckRepeatQueryWrapper(ImportantVoucherNumberSegmentVO segmentVO) {
        LambdaQueryWrapper<ImportantVoucherNumberSegment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ImportantVoucherNumberSegment::getDepartmentId, segmentVO.getDepartmentId())
                .eq(ImportantVoucherNumberSegment::getVoucherId, segmentVO.getVoucherId());
        // 判断交叉
        queryWrapper.and(wrapper -> {
            wrapper.and(w -> {
                w.or(item -> {
                    item.le(ImportantVoucherNumberSegment::getNumberStart, segmentVO.getNumberStart())
                            .ge(ImportantVoucherNumberSegment::getNumberEnd, segmentVO.getNumberStart());
                });
                w.or(item -> {
                    item.le(ImportantVoucherNumberSegment::getNumberStart, segmentVO.getNumberEnd())
                            .ge(ImportantVoucherNumberSegment::getNumberEnd, segmentVO.getNumberEnd());
                });
                w.or(item -> {
                    item.ge(ImportantVoucherNumberSegment::getNumberStart, segmentVO.getNumberStart())
                            .le(ImportantVoucherNumberSegment::getNumberEnd, segmentVO.getNumberEnd());
                });
            });
        });

        return queryWrapper;
    }

    /**
     * 构建搜索查询条件
     * @param queryVO 条件
     * @return 查询条件
     */
    private LambdaQueryWrapper<ImportantVoucherNumberSegment> getSearchQueryWrapper(ImportantVoucherNumberSegmentQueryVO queryVO) {
        LambdaQueryWrapper<ImportantVoucherNumberSegment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ImportantVoucherNumberSegment::getDepartmentId, queryVO.getDepartmentId())
                .eq(queryVO.getId() != null, ImportantVoucherNumberSegment::getId, queryVO.getId())
                .eq(StringUtils.isNotEmpty(queryVO.getName()), ImportantVoucherNumberSegment::getName, queryVO.getName())
                .eq(queryVO.getVoucherId() != null, ImportantVoucherNumberSegment::getVoucherId, queryVO.getVoucherId())
                .eq(queryVO.getType() != null, ImportantVoucherNumberSegment::getType, queryVO.getType());
        return queryWrapper;
    }

    @Data
    class VoucherSegmentOutRecord {
        // 待修改的分段Id
        private Long segementId;
        // 修改后的起始
        private Long start;
        // 修改后的结束
        private Long end;

        public VoucherSegmentOutRecord(Long segementId) {
            this.segementId = segementId;
        }
    }
}
