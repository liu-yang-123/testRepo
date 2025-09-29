package com.zcxd.gun.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.gun.common.GunTypeEnum;
import com.zcxd.common.util.Result;
import com.zcxd.gun.db.mapper.GunCategoryMapper;
import com.zcxd.gun.db.model.GunCategory;
import com.zcxd.gun.feign.RemoteProvider;
import com.zcxd.gun.vo.GunCategoryQueryVO;
import com.zcxd.gun.vo.GunCategoryVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zccc
 */
@Service
public class GunCategoryService extends ServiceImpl<GunCategoryMapper, GunCategory> {
    @Autowired
    private RemoteProvider remoteService;

    /**
     * 新增枪支类型
     * @param vo 查询条件
     * @return 结果
     */
    public Result saveGunCategory(GunCategoryVO vo) {
        Result<Boolean> authResult = remoteService.isAuthDepartment(vo.getDepartmentId());
        if (authResult.isFailed()) {
            return Result.fail(authResult.getMsg());
        }

        if (!checkCategory(vo)) {
            return Result.fail("枪支类型重复");
        }

        GunCategory gunCategory = new GunCategory();
        BeanUtils.copyProperties(vo, gunCategory);

        boolean save = save(gunCategory);
        if(save) {
            return Result.success();
        }
        return Result.fail("添加枪支类型失败");
    }

    /**
     * 类型名称没有重复返回true
     * @param vo 查询条件
     * @return 结果
     */
    public boolean checkCategory(GunCategoryVO vo) {
        LambdaQueryWrapper<GunCategory> queryWrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isBlank(vo.getGunCategoryName())) {
            queryWrapper.eq(GunCategory::getGunCategoryName, vo.getGunCategoryName());
        }
        queryWrapper.eq(GunCategory::getDepartmentId, vo.getDepartmentId());
        return baseMapper.selectOne(queryWrapper) == null;
    }

    public Object updateGunById(GunCategoryVO vo) {
        if (vo.getId() == null || getById(vo.getId()) == null) {
            return Result.fail("Id错误");
        }
        Result<Boolean> authResult = remoteService.isAuthDepartment(vo.getDepartmentId());
        if (authResult.isFailed()) {
            return Result.fail(authResult.getMsg());
        }

        GunCategory gunCategory = new GunCategory();
        BeanUtils.copyProperties(vo, gunCategory);

        boolean update = updateById(gunCategory);
        if(update) {
            return Result.success();
        }

        return Result.fail("修改枪支类型失败");
    }

    /**
     * 分页查询持枪证
     * @param page 页数
     * @param limit 数量
     * @param queryVO 查询条件
     * @return 查询结果
     * @throws IllegalAccessException 错误
     */
    public Object findListByPage(Integer page, Integer limit, GunCategoryQueryVO queryVO) throws IllegalAccessException {
        Page<GunCategory> ipage = new Page<>(page,limit);
        LambdaQueryWrapper<GunCategory> queryWrapper = getGunCategoryLambdaWrapper(queryVO);
        return Result.success(baseMapper.selectPage(ipage,
                queryWrapper));
    }

    /**
     * 判断是否为枪支
     * @param id id
     * @return 结果
     */
    public boolean isGun(Long id) {
        GunCategory category = getById(id);
        return category != null && category.getGunType() == GunTypeEnum.GUN.getValue();
    }

    /**
     * 判断是否为枪盒
     * @param id id
     * @return 结果
     */
    public boolean isAmmunition(Long id) {
        GunCategory category = getById(id);
        return category != null && category.getGunType() == GunTypeEnum.AMMUNITION.getValue();
    }

    /**
     * 构建查询条件Wrapper
     * @param queryVo 查询条件
     * @return Wrapper
     */
    private LambdaQueryWrapper<GunCategory> getGunCategoryLambdaWrapper(GunCategoryQueryVO queryVo) {
        LambdaQueryWrapper<GunCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(!StringUtils.isBlank(queryVo.getGunCategoryName()), GunCategory::getGunCategoryName, queryVo.getGunCategoryName())
                .eq(queryVo.getGunType() != null, GunCategory::getGunType, queryVo.getGunType())
                .eq(GunCategory::getDepartmentId, queryVo.getDepartmentId())
                .select(GunCategory::getId, GunCategory::getGunCategoryName, GunCategory::getRemark, GunCategory::getGunType);
        return queryWrapper;
    }
}
