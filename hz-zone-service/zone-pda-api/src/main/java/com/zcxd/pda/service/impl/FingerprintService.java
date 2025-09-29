package com.zcxd.pda.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.common.constant.Constant;
import com.zcxd.common.constant.DeleteFlagEnum;
import com.zcxd.db.mapper.FingerprintMapper;
import com.zcxd.db.model.Fingerprint;
import com.zcxd.pda.dto.FingerprintDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 
 * @ClassName FingerprintService
 * @Description 指纹管理服务类
 * @author shijin
 * @Date 2021年5月18日上午10:50:05
 */
@Service
public class FingerprintService extends ServiceImpl<FingerprintMapper, Fingerprint> {


    /**
     * 查询指纹列表
     * @param userType
     * @return
     */
    public IPage<Fingerprint> listPrintPage(int userType, int page, int limit) {

        Page<Fingerprint> ipage=new Page<>(page,limit);
        QueryWrapper<Fingerprint> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_type", userType).eq("deleted", DeleteFlagEnum.NOT.getValue());

        return baseMapper.selectPage(ipage,queryWrapper);
    }

    /**
     * 根据类型查询指纹列表数据
     * @param userType
     * @param userIds
     * @return
     */
    public List<Fingerprint> listPrint(int userType, Set<Long> userIds){
        if (userIds.size() == 0){
            return new ArrayList<>();
        }
        QueryWrapper<Fingerprint> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_type", userType).in("user_id",userIds).eq("deleted", DeleteFlagEnum.NOT.getValue());
        return this.baseMapper.selectList(queryWrapper);
    }


    /**
     * 查询指定用户的指纹
     * @param userId
     * @return
     */
    public List<Fingerprint> listUserPrint(long userId,int userType) {
        Fingerprint where = new Fingerprint();
        where.setUserType(userType);
        where.setUserId(userId);
        where.setDeleted(DeleteFlagEnum.NOT.getValue());
        return baseMapper.selectList(new QueryWrapper<>(where));
    }


    /**
     * 查询用户指定指纹数据
     * @param userId
     * @param userType
     * @param index
     * @return
     */
    public Fingerprint getByUserId(long userId, int userType,int index) {
        Fingerprint where = new Fingerprint();
        where.setFingerIdx(index);
        where.setUserId(userId);
        where.setUserType(userType);
        where.setDeleted(DeleteFlagEnum.NOT.getValue());
        return baseMapper.selectOne(new QueryWrapper<>(where));
    }

    /**
     * 逻辑删除
     * @param id
     */
    public void softDelete(Long id) {
        Fingerprint fingerprint = new Fingerprint();
        fingerprint.setId(id);
        fingerprint.setDeleted(DeleteFlagEnum.YES.getValue());
        baseMapper.updateById(fingerprint);
    }

    /**
     * 查询指纹最后更新日期
     * @param userType
     * @return
     */
    public Long getLastUpdateTime(Integer userType) {
        return baseMapper.getMaxUpdateTime(userType);
    }
}
