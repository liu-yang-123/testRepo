package com.zcxd.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zcxd.common.constant.DeleteFlagEnum;
import com.zcxd.db.model.SchdCleanPassAuth;
import com.zcxd.db.mapper.SchdCleanPassAuthMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 清机通行证授权 服务实现类
 * </p>
 *
 * @author admin
 * @since 2021-07-05
 */
@Service
public class SchdCleanPassAuthService extends ServiceImpl<SchdCleanPassAuthMapper, SchdCleanPassAuth> {

    public IPage<SchdCleanPassAuth> listPage(int page,int limit, long departmentId,int passType, Long bankId, Set<Long> empIds) {
        Page<SchdCleanPassAuth> queryPage = new Page<>(page,limit);
        QueryWrapper<SchdCleanPassAuth> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("department_id",departmentId).eq("pass_type", passType);
        if (bankId != null) {
            queryWrapper.eq("bank_id", bankId);
        }
        if (empIds != null && empIds.size() > 0) {
            queryWrapper.in("emp_id", empIds);
        }
        queryWrapper.eq("deleted",0).orderByDesc("id");
        return baseMapper.selectPage(queryPage,queryWrapper);
    }

    private SchdCleanPassAuth getByCondition(SchdCleanPassAuth where) {
        QueryWrapper<SchdCleanPassAuth> queryWrapper = new QueryWrapper<>();
        if (null != where) {
            if (null != where.getBankId()) {
                queryWrapper.eq("bank_id", where.getBankId());
            }
            if (null != where.getEmpId()) {
                queryWrapper.eq("emp_id", where.getEmpId());
            }
            queryWrapper.eq("deleted",DeleteFlagEnum.NOT.getValue());
            return baseMapper.selectOne(queryWrapper);
        }
        return null;
    }

    public SchdCleanPassAuth getByEmpId(Long bankId,Long empId) {
        SchdCleanPassAuth where = new SchdCleanPassAuth();
        where.setBankId(bankId);
        where.setEmpId(empId);

        return getByCondition(where);
    }

    /**
     * 获取通行证编号列表
     * @param authId
     * @return
     */
    public String getAuthList(Long authId){
        if (authId == 0){
            return null;
        }
        QueryWrapper queryWrapper = Wrappers.query().eq("emp_id",authId).eq("pass_type",0).eq("deleted",0);
        List<SchdCleanPassAuth> authList = baseMapper.selectList(queryWrapper);
        return authList.stream().map(SchdCleanPassAuth::getPassCode).collect(Collectors.joining(","));
    }

}
