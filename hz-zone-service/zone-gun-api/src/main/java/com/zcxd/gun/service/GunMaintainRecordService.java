package com.zcxd.gun.service;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.zcxd.common.util.Result;
import com.zcxd.gun.config.UserContextHolder;
import com.zcxd.gun.db.mapper.GunMaintainRecordMapper;
import com.zcxd.gun.db.model.Gun;
import com.zcxd.gun.db.model.GunCategory;
import com.zcxd.gun.db.model.GunMaintainRecord;
import com.zcxd.gun.dto.GunMaintainRecordDTO;
import com.zcxd.gun.vo.GunMaintainRecordQueryVO;
import com.zcxd.gun.vo.GunMaintainRecordVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zccc
 */
@Service
public class GunMaintainRecordService extends MPJBaseServiceImpl<GunMaintainRecordMapper, GunMaintainRecord> {
    /**
     * 新增维护记录
     * @param gunMaintainRecordVo 持枪证信息
     * @return 新增结果
     */
    public Result saveGunMaintainRecord(GunMaintainRecordVO gunMaintainRecordVo) {
        // to GunLicence
        GunMaintainRecord gunMaintainRecord = new GunMaintainRecord();
        BeanUtils.copyProperties(gunMaintainRecordVo, gunMaintainRecord);
        gunMaintainRecord.setOperateTime(System.currentTimeMillis());
        gunMaintainRecord.setOperator(UserContextHolder.getUsername());

        // save
        return save(gunMaintainRecord) ? Result.success() : Result.fail("添加失败");
    }

    /**
     * 分页查询持枪证
     * @param page 页数
     * @param limit 数量
     * @param queryVo 查询条件
     * @return 查询结果
     * @throws IllegalAccessException 错误
     */
    public Object findListByPage(Integer page, Integer limit, GunMaintainRecordQueryVO queryVo) throws IllegalAccessException {
        Page<GunMaintainRecordDTO> ipage = new Page<>(page,limit);
        MPJLambdaWrapper<GunMaintainRecord> wrapper = getGunMaintainRecordMPJLambdaWrapper(queryVo);
        return Result.success(baseMapper.selectJoinPage(ipage, GunMaintainRecordDTO.class,
                wrapper));
    }

    /**
     * 构建持枪证查询条件Map
     * @param queryVo 查询条件
     * @return Map
     */
    private Map<SFunction<GunMaintainRecord, ?>, Object> getGunMaintainRecordQueryMap(GunMaintainRecordQueryVO queryVo) {
        Map<SFunction<GunMaintainRecord, ?>, Object> map = new HashMap<>();
        map.put(GunMaintainRecord::getDepartmentId, queryVo.getDepartmentId());
        map.put(GunMaintainRecord::getOperateType, queryVo.getOperateType());
        map.put(GunMaintainRecord::getOperateTime, queryVo.getOperateTime());
        return map;
    }

    /**
     * 构建查询条件Wrapper
     * @param queryVo 查询条件
     * @return Wrapper
     */
    public MPJLambdaWrapper<GunMaintainRecord> getGunMaintainRecordMPJLambdaWrapper(GunMaintainRecordQueryVO queryVo) {
        return new MPJLambdaWrapper<GunMaintainRecord>()
                .selectAll(GunMaintainRecord.class)
                .selectAs(Gun::getGunCode, GunMaintainRecordDTO::getGunCode)
                .selectAs(GunCategory::getGunCategoryName, GunMaintainRecordDTO::getGunCategoryName)
                .selectAs(Gun::getInternalNum, GunMaintainRecordDTO::getInternalNum)
                .leftJoin(Gun.class, Gun::getId, GunMaintainRecord::getGunId)
                .leftJoin(GunCategory.class, GunCategory::getId, Gun::getGunCategory)
                .allEq(getGunMaintainRecordQueryMap(queryVo), false);
    }
}
