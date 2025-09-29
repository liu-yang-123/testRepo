package com.zcxd.gun.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.gun.common.GunTaskEnum;
import com.zcxd.common.util.DateTimeUtil;
import com.zcxd.common.util.Result;
import com.zcxd.gun.db.mapper.GunTaskMapper;
import com.zcxd.gun.db.model.GunTask;
import com.zcxd.gun.vo.GunTaskIssueAndTakedownQueryVO;
import com.zcxd.gun.vo.GunTaskQueryVO;
import com.zcxd.gun.vo.GunTaskVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author zccc
 */
@Service
public class GunTaskService extends ServiceImpl<GunTaskMapper, GunTask> {
    /**
     * 新增枪支任务
     * @param gunTaskVo 属性
     * @return 结果
     */
    public Result saveGunTask(GunTaskVO gunTaskVo) {
        // 属性补全
        GunTask gunTask = new GunTask();
        BeanUtils.copyProperties(gunTaskVo, gunTask);

        // save
        boolean save = save(gunTask);
        return save ? Result.success() : Result.fail("新增枪弹任务失败");
    }

    /**
     * 修改枪支任务
     * @param gunTaskVo 属性
     * @return 结果
     */
    public Result updateGunTask(GunTaskVO gunTaskVo) {
        Long id = gunTaskVo.getId();
        if (id == null || id <= 0) {
            return Result.fail("未选择正确的枪弹任务");
        }

        GunTask existGunTask = getById(id);
        if (existGunTask == null) {
            return Result.fail("未选择正确的枪弹任务");
        }
        // 属性补全
        GunTask gunTask = new GunTask();
        BeanUtils.copyProperties(gunTaskVo, gunTask);

        boolean update = updateById(gunTask);
        return update ? Result.success() : Result.fail("修改枪弹任务失败");
    }

    /**
     * 按页查询
     * @param page 页数
     * @param limit 单页数量
     * @param queryVo 查询条件
     * @return 结果
     */
    public Page<GunTask> findListByPage(Integer page, Integer limit, GunTaskQueryVO queryVo) {
        Page<GunTask> taskPage = new Page<>(page, limit);
        LambdaQueryWrapper<GunTask> queryWrapper = getGunTaskQueryWrapper(queryVo);

        Page<GunTask> gunTaskPage = baseMapper.selectPage(taskPage, queryWrapper);
        gunTaskPage.getRecords().forEach(record -> {
            record.setCreateTime(null);
            record.setCreateUser(null);
            record.setDeleted(null);
            record.setUpdateUser(null);
            record.setUpdateTime(null);
            record.setTaskStatusStr(GunTaskEnum.getTextByValue(record.getTaskStatus()));
        });
        return gunTaskPage;
    }

    /**
     * 查询
     * @param queryVo 查询条件
     * @return 结果
     */
    public List<GunTask> findList(GunTaskQueryVO queryVo) {
        LambdaQueryWrapper<GunTask> queryWrapper = getGunTaskQueryWrapper(queryVo);

        return baseMapper.selectList(queryWrapper);
    }

    /**
     * 根据排班记录ID查找枪弹任务
     * @param id 排班记录ID
     * @return 结果
     */
    public GunTask findBySchdId(Long id) {
        LambdaQueryWrapper<GunTask> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GunTask::getSchdId, id);
        return baseMapper.selectOne(queryWrapper);
    }

    /**
     * 获取枪弹任务查询语句
     * @param queryVo 查询条件
     * @return 查询语句
     */
    private LambdaQueryWrapper<GunTask> getGunTaskQueryWrapper(GunTaskQueryVO queryVo) {
        String gunCode = queryVo.getGunCode();
        String taskTimeStart = queryVo.getTaskTimeStart();
        String taskTimeEnd = queryVo.getTaskTimeEnd();
        String taskTime = queryVo.getTaskTime();

        LambdaQueryWrapper<GunTask> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(queryVo.getId() != null, GunTask::getId, queryVo.getId())
                .eq(GunTask::getDepartmentId, queryVo.getDepartmentId())
                .eq(!StringUtils.isBlank(queryVo.getTaskStatus()), GunTask::getTaskStatus, GunTaskEnum.getValuetByText(queryVo.getTaskStatus()))
                .eq(!StringUtils.isBlank(queryVo.getLineName()), GunTask::getLineName, queryVo.getLineName())
                .gt(!StringUtils.isBlank(taskTimeStart), GunTask::getPlanTime, DateTimeUtil.getDailyStartTimeMs(DateTimeUtil.date2TimeStamp(taskTimeStart, "yyyy-MM-dd")))
                .lt(!StringUtils.isBlank(taskTimeEnd), GunTask::getPlanTime, DateTimeUtil.getDailyEndTimeMs(DateTimeUtil.date2TimeStamp(taskTimeEnd, "yyyy-MM-dd")))
                .ge(!StringUtils.isBlank(taskTime), GunTask::getPlanTime, DateTimeUtil.getDailyStartTimeMs(DateTimeUtil.date2TimeStamp(taskTime, "yyyy-MM-dd")))
                .le(!StringUtils.isBlank(taskTime), GunTask::getPlanTime, DateTimeUtil.getDailyEndTimeMs(DateTimeUtil.date2TimeStamp(taskTime, "yyyy-MM-dd")));

        // 押运员ID
        queryWrapper.and(queryVo.getSupercargoId() != null, wrapper -> wrapper
                .eq(GunTask::getSupercargoIdA, queryVo.getSupercargoId())
                .or().eq(GunTask::getSupercargoIdB, queryVo.getSupercargoId()));
        // 枪弹号
        queryWrapper.and(!StringUtils.isBlank(queryVo.getGunCode()), wrapper -> wrapper
                .eq(GunTask::getGunCodeA, gunCode)
                .or().eq(GunTask::getGunCodeB, gunCode)
                .or().eq(GunTask::getGunBoxCodeA, gunCode)
                .or().eq(GunTask::getGunBoxCodeB, gunCode));
        // 押运员姓名
        queryWrapper.and(!StringUtils.isBlank(queryVo.getSupercargoName()), wrapper -> wrapper
                .eq(GunTask::getSupercargoNameA, queryVo.getSupercargoName())
                .or().eq(GunTask::getSupercargoNameB, queryVo.getSupercargoName()));
        return queryWrapper;
    }

    /**
     * 获取枪弹任务
     * @param queryVo 查询条件
     * @return 结果
     */
    private GunTask getOneGunTask(GunTaskQueryVO queryVo) {
        LambdaQueryWrapper<GunTask> gunTaskQueryWrapper = getGunTaskQueryWrapper(queryVo);
        return baseMapper.selectOne(gunTaskQueryWrapper);
    }

    /**
     * 收、发枪通用检查
     * @param gunTask 枪支任务
     * @param queryVo 条件
     * @return 结果
     */
    private Result checkBase(GunTask gunTask, GunTaskIssueAndTakedownQueryVO queryVo) {
        // 押运员检查
        // 考虑到可能没有按照顺序刷脸
        Set<Long> supercargoSet = new HashSet<>();
        supercargoSet.add(gunTask.getSupercargoIdA());
        supercargoSet.add(gunTask.getSupercargoIdB());
        // 押运员重复检查
        if (queryVo.getSupercargoIdA().equals(queryVo.getSupercargoIdB())) {
            return Result.fail("押运员A、B不能是同一个人");
        }
        // 检查押运员A
        if (!supercargoSet.contains(queryVo.getSupercargoIdA())) {
            return Result.fail("押运员A不是该任务指定押运员");
        } else {
            supercargoSet.remove(queryVo.getSupercargoIdA());
        }
        // 检查押运员B
        if (!supercargoSet.contains(queryVo.getSupercargoIdB())) {
            return Result.fail("押运员B不是该任务指定押运员");
        }

        // 枪支检查
        Set<String> gunCodeSet = new HashSet<>();
        gunCodeSet.add(gunTask.getGunCodeA());
        gunCodeSet.add(gunTask.getGunCodeB());
        // 枪支重复检查
        if (queryVo.getGunCodeA().equals(queryVo.getGunCodeB())) {
            return Result.fail("枪支A、B不能相同");
        }
        // 检查枪支A
        if (!gunCodeSet.contains(queryVo.getGunCodeA())) {
            return Result.fail("枪支A不是该任务指定枪支");
        } else {
            gunCodeSet.remove(queryVo.getGunCodeA());
        }
        // 检查枪支B
        if (!gunCodeSet.contains(queryVo.getGunCodeB())) {
            return Result.fail("枪支B不是该任务指定枪支");
        }

        // 枪盒检查
        Set<String> gunBoxCodeSet = new HashSet<>();
        gunBoxCodeSet.add(gunTask.getGunBoxCodeA());
        gunBoxCodeSet.add(gunTask.getGunBoxCodeB());
        // 弹盒重复检查
        if (queryVo.getGunBoxCodeA().equals(queryVo.getGunBoxCodeB())) {
            return Result.fail("枪盒A、B不能相同");
        }
        // 检查枪盒A
        if (!gunBoxCodeSet.contains(queryVo.getGunBoxCodeA())) {
            return Result.fail("枪盒A不是该任务指定弹盒");
        } else {
            gunBoxCodeSet.remove(queryVo.getGunBoxCodeA());
        }
        // 检查枪盒B
        if (!gunBoxCodeSet.contains(queryVo.getGunBoxCodeB())) {
            return Result.fail("枪盒B不是该任务指定弹盒");
        }

        // 枪证检查
        Set<String> gunLicenceSet = new HashSet<>();
        gunLicenceSet.add(gunTask.getGunLicenceNumA());
        gunLicenceSet.add(gunTask.getGunLicenceNumB());
        // 枪证重复检查
        if (queryVo.getGunLicenceNumA().equals(queryVo.getGunLicenceNumB())) {
            return Result.fail("枪证A、B不能相同");
        }
        // 检查枪证A
        if (!gunLicenceSet.contains(queryVo.getGunLicenceNumA())) {
            return Result.fail("枪证A不是该任务指定枪证");
        } else {
            gunLicenceSet.remove(queryVo.getGunLicenceNumA());
        }
        // 检查枪证B
        if (!gunLicenceSet.contains(queryVo.getGunLicenceNumB())) {
            return Result.fail("枪证B不是该任务指定枪证");
        }
        return Result.success();
    }

    /**
     * 发枪检查
     * @param gunTask 枪支任务
     * @param queryVo 查询条件
     * @return 结果
     */
    private Result checkIssueGun(GunTask gunTask, GunTaskIssueAndTakedownQueryVO queryVo) {
        if (gunTask == null) {
            return Result.fail("未正确选择任务");
        }

        if (gunTask.getTaskStatus() != GunTaskEnum.START.getValue()) {
            return Result.fail("该任务已发枪");
        }

        long currentTimeMillis = System.currentTimeMillis();
        // 任务开始前30分钟内才能领枪
        if (gunTask.getPlanTime() - currentTimeMillis > 30 * 60 * 1000) {
            return Result.fail("任务开始前30分钟内才可领枪");
        }

        return checkBase(gunTask, queryVo);
    }

    /**
     * 收枪检查
     * @param gunTask 枪支任务
     * @param queryVo 查询条件
     * @return 结果
     */
    private Result checkTakedownGun(GunTask gunTask, GunTaskIssueAndTakedownQueryVO queryVo) {
        if (gunTask == null) {
            return Result.fail("未正确选择任务");
        }

        if (gunTask.getTaskStatus() != GunTaskEnum.ISSUEDGUN.getValue()) {
            return Result.fail("当前任务状态无法收枪");
        }

        return checkBase(gunTask, queryVo);
    }

    /**
     * 发枪
     * @param queryVo 查询条件
     * @return 结果
     */
    public Result issueGun(GunTaskIssueAndTakedownQueryVO queryVo) {
        // 检查
        GunTaskQueryVO taskQueryVO = new GunTaskQueryVO();
        taskQueryVO.setId(queryVo.getId());
        taskQueryVO.setDepartmentId(queryVo.getDepartmentId());
        GunTask gunTask = getOneGunTask(taskQueryVO);

        Result checkResult = checkIssueGun(gunTask, queryVo);
        if (checkResult.isFailed()) {
            return checkResult;
        }

        // 发枪，修改表格（状态、时间）
        gunTask.setTaskStatus(GunTaskEnum.ISSUEDGUN.getValue());
        gunTask.setTakeOutTime(System.currentTimeMillis());
        gunTask.setGunLicenseOutA(2);
        gunTask.setGunLicenseOutB(2);
        updateById(gunTask);
        return Result.success();
    }

    /**
     * 收枪
     * @param queryVo 查询条件
     * @return 结果
     */
    public Result takedownGun(GunTaskIssueAndTakedownQueryVO queryVo) {
        GunTaskQueryVO taskQueryVO = new GunTaskQueryVO();
        taskQueryVO.setId(queryVo.getId());
        taskQueryVO.setDepartmentId(queryVo.getDepartmentId());
        GunTask gunTask = getOneGunTask(taskQueryVO);

        Result checkResult = checkTakedownGun(gunTask, queryVo);
        if (checkResult.isFailed()) {
            return checkResult;
        }

        // 发枪，修改表格（状态、时间）
        gunTask.setTaskStatus(GunTaskEnum.FINISHED.getValue());
        gunTask.setTakeInTime(System.currentTimeMillis());
        gunTask.setGunLicenseInA(2);
        gunTask.setGunLicenseInB(2);
        updateById(gunTask);
        return Result.success();
    }
}
