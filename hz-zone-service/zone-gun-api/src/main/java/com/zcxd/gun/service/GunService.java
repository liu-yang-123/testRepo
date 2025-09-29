package com.zcxd.gun.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.query.MPJQueryWrapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.zcxd.gun.common.GunStatusEnum;
import com.zcxd.gun.common.GunTypeEnum;
import com.zcxd.common.util.EasyExcelUtil;
import com.zcxd.common.util.Result;
import com.zcxd.gun.config.UserContextHolder;
import com.zcxd.gun.db.mapper.GunMapper;
import com.zcxd.gun.db.model.Gun;
import com.zcxd.gun.db.model.GunCategory;
import com.zcxd.gun.db.model.GunLicence;
import com.zcxd.db.utils.RedisUtil;
import com.zcxd.gun.dto.GunAndLicenceDTO;
import com.zcxd.gun.dto.GunDTO;
import com.zcxd.gun.dto.GunStatisticsDTO;
import com.zcxd.gun.dto.excel.ExportHead;
import com.zcxd.gun.dto.excel.GunInfoRecordHead;
import com.zcxd.gun.exception.BusinessException;
import com.zcxd.gun.vo.GunStatisticsVO;
import com.zcxd.gun.vo.GunVO;
import com.zcxd.gun.vo.GunQueryVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;

/**
 * @author zccc
 */
@Service
public class GunService extends ServiceImpl<GunMapper, Gun> {
    private final String GUN_INTERNAL_NUM = "gun_internal_num";
    /**
     * 时间
      */
    public static SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Resource
    private RedisUtil redisUtil;

    /**
     * @Title getGunByGunCode
     * @Description 根据枪弹号查询枪
     * @return 返回类型 Gun
     */
    public Gun getGunByGunCode(GunVO gunVo) {
        QueryWrapper<Gun> queryWrapper = new QueryWrapper<>();
        if(!StringUtils.isBlank(gunVo.getGunCode())){
            queryWrapper.eq("gun_code", gunVo.getGunCode());
        } else {
            return null;
        }
        queryWrapper.eq("department_id", gunVo.getDepartmentId());
        queryWrapper.eq("deleted", 0);
        return baseMapper.selectOne(queryWrapper);
    }

    /**
     * @Title gunCodeExits
     * @Description 判断是否已有枪弹编号
     * @return 返回类型 boolean
     */
    public boolean gunCodeExits(GunVO gunVo) {
        Gun gunByGunCode = getGunByGunCode(gunVo);
        return gunByGunCode != null;
    }

    /**
     * 从redis中获取强内部编号
     * @return
     */
    private Long getGunInternalNum() {
        long gunInterNalNum = 1;
        if (redisUtil.hasKey(GUN_INTERNAL_NUM)) {
            gunInterNalNum = Long.parseLong(redisUtil.get(GUN_INTERNAL_NUM));

        }
        redisUtil.set(GUN_INTERNAL_NUM, String.valueOf(gunInterNalNum + 1));
        return gunInterNalNum;
    }

    /**
     * @Title gunLicenceNumExits
     * @Description 判断是否已有枪证编号
     * @return 返回类型 boolean
     */
    public boolean gunLicenceNumExits(GunVO gunVo) {
        MPJLambdaWrapper<Gun> wrapper = getGunLicenceMPJLambdaWrapper(gunVo);
        Gun gun = baseMapper.selectJoinOne(Gun.class, wrapper);
        return gun != null;
    }

    /**
     * @Title getGunByGunLicenceNum
     * @Description 根据枪证号获取枪支
     * @return 返回类型 Gun
     */
    public Gun getGunByGunLicenceNum(GunVO gunVo) {
        MPJLambdaWrapper<Gun> wrapper = getGunLicenceMPJLambdaWrapper(gunVo);
        return baseMapper.selectJoinOne(Gun.class, wrapper);
    }

    /**
     * 按枪支分类统计结果
     * @param statisticsVo 条件
     * @return 结果
     */
    public Result gunCategoryStatistics(GunStatisticsVO statisticsVo) {
        MPJQueryWrapper<Gun> gunCategoryMPJLambdaWrapper = getGunCategoryMPJLambdaWrapper(statisticsVo);
        List<GunStatisticsDTO> gunStatisticsDTOS = baseMapper.selectJoinList(GunStatisticsDTO.class, gunCategoryMPJLambdaWrapper);
        gunStatisticsDTOS.forEach(x -> x.setType(GunTypeEnum.getTextByValue(Integer.valueOf(x.getType()))));
        return Result.success(gunStatisticsDTOS);
    }

    /**
     * 构建查询条件Wrapper
     * @param queryVo 查询条件
     * @return Wrapper
     */
    public MPJLambdaWrapper<Gun> getGunMPJLambdaWrapper(GunQueryVO queryVo) {
        return new MPJLambdaWrapper<Gun>()
                .selectAll(Gun.class)
                .selectAs(GunLicence::getGunLicenceNum, GunDTO::getGunLicenceNum)
                .selectAs(GunCategory::getGunCategoryName, GunDTO::getGunCategoryName)
                .selectAs(GunCategory::getGunType, GunDTO::getGunType)
                .leftJoin(GunLicence.class, GunLicence::getId, Gun::getGunLicenceId)
                .leftJoin(GunCategory.class, GunCategory::getId, Gun::getGunCategory)
                .allEq(getGunQueryMap(queryVo), false)
                .ge(queryVo.getInternalNumStart() != null, Gun::getInternalNum, queryVo.getInternalNumStart())
                .le(queryVo.getInternalNumEnd() != null, Gun::getInternalNum, queryVo.getInternalNumEnd())
                .eq(!StringUtils.isBlank(queryVo.getGunLicenceNum()), GunLicence::getGunLicenceNum, queryVo.getGunLicenceNum())
                .eq(queryVo.getGunType() != null, GunCategory::getGunType, queryVo.getGunType())
                .eq(StringUtils.isNotBlank(queryVo.getOperatorName()), Gun::getOperatorName, queryVo.getOperatorName());
    }

    /**
     * 构建查询分类条件Wrapper
     * @param statisticsVo 持枪证号
     * @return Wrapper
     */
    private MPJQueryWrapper<Gun> getGunCategoryMPJLambdaWrapper(GunStatisticsVO statisticsVo) {
         return new MPJQueryWrapper<Gun>()
                .select("count(*) as count")
                .select("gun_type as type")
                .select("gun_category_name as categoryName")
                .leftJoin("gun_category on t.gun_category = gun_category.id")
                .groupBy("t.gun_category")
                .eq("t.department_id", statisticsVo.getDepartmentId())
                .eq(!StringUtils.isBlank(statisticsVo.getCategoryName()), "gun_category.gun_category_name", statisticsVo.getCategoryName())
                .eq(!StringUtils.isBlank(statisticsVo.getCategoryName()), "gun_category.gun_category_name", statisticsVo.getCategoryName())
                .eq("t.deleted", 0);
    }

    /**
     * 构建查询证件存在条件Wrapper
     * @param gunVO 持枪证号
     * @return Wrapper
     */
    private MPJLambdaWrapper<Gun> getGunLicenceMPJLambdaWrapper(GunVO gunVO) {
        return new MPJLambdaWrapper<Gun>()
                .select(Gun::getId)
                .selectAs(GunLicence::getId, GunAndLicenceDTO::getGunLicenceId)
                .leftJoin(GunLicence.class, GunLicence::getId, Gun::getGunLicenceId)
                .eq(GunLicence::getGunLicenceNum, gunVO.getGunLicenceNum())
                .eq(Gun::getDepartmentId, gunVO.getDepartmentId());
    }

    /**
     * 构建枪支查询条件Map
     * @param queryVo 查询条件
     * @return Map
     */
    private Map<SFunction<Gun, ?>, Object> getGunQueryMap(GunQueryVO queryVo) {
        Map<SFunction<Gun, ?>, Object> map = new HashMap<>();
        map.put(Gun::getGunCode, queryVo.getGunCode());
        map.put(Gun::getDepartmentId, queryVo.getDepartmentId());
        map.put(Gun::getGunCategory, queryVo.getGunCategory());
        map.put(Gun::getGunStatus, queryVo.getGunStatus());
        map.put(Gun::getInternalNum, queryVo.getInternalNum());
        map.put(Gun::getBuyDate, queryVo.getBuyDate());
        map.put(Gun::getCheckDate, queryVo.getCheckDate());
        map.put(Gun::getCleanDate, queryVo.getCleanDate());
        return map;
    }

    public Result saveGun(GunVO gunVo) {
        Gun gun = new Gun();
        BeanUtils.copyProperties(gunVo, gun);
        Long gunInternalNum = getGunInternalNum();

        gun.setId(null);
        gun.setGunStatus(GunStatusEnum.STOREIN.getValue());
        gun.setInternalNum(gunInternalNum);
        gun.setOperatorName(UserContextHolder.getUsername());

        boolean save = save(gun);
        if(save) {
            return Result.success();
        }
        return Result.fail("添加枪弹失败");
    }

    /**
     * 更新枪信息
     * @param gunVo 属性
     * @return 结果
     */
    public Object updateGunById(GunVO gunVo) {
        Gun gunToUpdate;
        if (gunVo.getId() == null || getById(gunVo.getId()) == null) {
            return Result.fail("枪弹Id错误");
        }
        gunToUpdate = getById(gunVo.getId());

        if (gunCodeExits(gunVo)) {
            return Result.fail("枪弹编号已存在，请重新填写！");
        }
        if (gunLicenceNumExits(gunVo)) {
            return Result.fail("该枪证号已存在，请重新填写！");
        }
        if (gunToUpdate.getGunStatus() == GunStatusEnum.UNABLE.getValue()) {
            return Result.fail("该枪已经停用，无法更改！");
        }

        Gun gun = new Gun();
        BeanUtils.copyProperties(gunVo, gun);

        boolean update = updateById(gun);
        if(update) {
            return Result.success();
        }

        return Result.fail("修改枪弹失败");
    }

    /**
     * 分页查询持枪证
     * @param page 页数
     * @param limit 数量
     * @param queryVO 查询条件
     * @return 查询结果
     * @throws IllegalAccessException 错误
     */
    public Result<Page<GunDTO>> findListByPage(Integer page, Integer limit, GunQueryVO queryVO) throws IllegalAccessException {
        Page<GunDTO> ipage = new Page<>(page,limit);
        MPJLambdaWrapper<Gun> wrapper = getGunMPJLambdaWrapper(queryVO);
        Page<GunDTO> data = baseMapper.selectJoinPage(ipage, GunDTO.class,
                wrapper);
        return Result.success(data);
    }

    /**
     * 停用
     * @param id 枪Id
     * @return 结果
     */
    public Object stop(Long id) {
        if (id == null || getById(id) == null) {
            return Result.fail("枪弹Id错误");
        }

        Gun gun = new Gun();
        gun.setId(id);
        gun.setGunStatus(3);

        boolean update = updateById(gun);
        if(update) {
            return Result.success();
        }

        return Result.fail("报废枪弹失败");
    }

    /**
     * 分解
     * @param id 枪id
     * @return 结果
     */
    public Result check(Long id) {
        Gun searchedGun = getById(id);
        if (id == null || searchedGun == null) {
            return Result.fail("枪弹Id错误");
        }
        if (searchedGun.getGunCategory() != GunTypeEnum.GUN.getValue()) {
            return Result.fail("枪盒不需要分解");
        }

        // 分解前检查
        if (searchedGun.getIsCheck() == 1) {
            // 获取明天时间戳
            LocalDate localDate = LocalDate.now().plusDays(1L);
            long tomorrowDate = localDate.atStartOfDay().toInstant(ZoneOffset.of("+8")).toEpochMilli();
            LocalDate localDate1 = LocalDate.now();
            long nowDate = localDate1.atStartOfDay().toInstant(ZoneOffset.of("+8")).toEpochMilli();
            // 同一天已经分解过
            if (tomorrowDate > searchedGun.getCheckDate() && searchedGun.getCheckDate() > nowDate) {
                return Result.fail("一天只能分解一次");
            }
        }

        // 执行分解
        Gun gun = new Gun();
        gun.setId(id);
        gun.setIsCheck(1);
        gun.setCheckDate(System.currentTimeMillis());

        boolean update = updateById(gun);
        if(update) {
            return Result.success();
        }

        return Result.fail("分解枪弹失败");
    }

    /**
     * 擦拭
     * @param id 枪id
     * @return 结果
     */
    public Result clean(Long id) {
        Gun searchedGun = getById(id);
        if (id == null || searchedGun == null) {
            return Result.fail("枪弹Id错误");
        }

        if (searchedGun.getIsClean() == 1) {
            // 获取明天时间戳
            LocalDate localDate = LocalDate.now().plusDays(1L);
            long tomorrowDate = localDate.atStartOfDay().toInstant(ZoneOffset.of("+8")).toEpochMilli();
            LocalDate localDate1 = LocalDate.now();
            long nowDate = localDate1.atStartOfDay().toInstant(ZoneOffset.of("+8")).toEpochMilli();
            // 同一天已经擦拭过
            if (tomorrowDate > searchedGun.getCheckDate() && searchedGun.getCheckDate() > nowDate) {
                return Result.fail("一天只能擦拭一次");
            }
        }

        // 擦拭
        Gun gun = new Gun();
        gun.setId(id);
        gun.setIsClean(1);
        gun.setCleanDate(System.currentTimeMillis());

        boolean update = updateById(gun);
        if(update) {
            return Result.success();
        }

        return Result.fail("擦拭枪弹失败");
    }

    public void exportGunInfo(HttpServletResponse response, GunQueryVO gunVo) {
        MPJLambdaWrapper<Gun> gunMPJLambdaWrapper = getGunMPJLambdaWrapper(gunVo);
        List<GunDTO> gunDTOS = baseMapper.selectJoinList(GunDTO.class, gunMPJLambdaWrapper);

        List<GunInfoRecordHead> gunExprotRecords = new ArrayList<>();
        for (int i = 0; i < gunDTOS.size(); i++) {
            GunDTO gunRecord = gunDTOS.get(i);
            GunInfoRecordHead gunInfoRecordHead = new GunInfoRecordHead();
            gunInfoRecordHead.setIndex(i + 1L);
            gunInfoRecordHead.setGunCode(gunRecord.getGunCode());
            gunInfoRecordHead.setGunLicenceNum(gunRecord.getGunLicenceNum());
            gunInfoRecordHead.setGunStatus(GunStatusEnum.values()[gunRecord.getGunStatus() - 1].getText());
            gunInfoRecordHead.setGunType(GunTypeEnum.values()[gunRecord.getGunType() - 1].getText());
            gunInfoRecordHead.setInternalNum(gunRecord.getInternalNum());
            gunInfoRecordHead.setBuyDate(gunRecord.getBuyDate());
            gunInfoRecordHead.setRemark(gunRecord.getRemark());
            gunInfoRecordHead.setUserCount(gunRecord.getUserCount());
            gunExprotRecords.add(gunInfoRecordHead);
        }

        gunExprotRecords.sort((x, y) -> Long.compare(x.getInternalNum(), y.getInternalNum()));
        String title = "枪弹信息";
        try {
            ExportHead.setExcelPropertyTitle(GunInfoRecordHead.class, title);
            EasyExcelUtil.exportPrintExcel(response,title,GunInfoRecordHead.class, gunExprotRecords,null);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(-1,"导出枪弹信息出错");
        }
    }
}
