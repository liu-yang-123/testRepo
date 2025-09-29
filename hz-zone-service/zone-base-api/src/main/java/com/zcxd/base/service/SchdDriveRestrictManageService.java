package com.zcxd.base.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zcxd.base.exception.BusinessException;
import com.zcxd.base.exception.ExceptionCode;
import com.zcxd.base.vo.SchdDriveRestrictVO;
import com.zcxd.base.vo.SchdDriveRestrictXhVO;
import com.zcxd.common.constant.DeleteFlagEnum;
import com.zcxd.common.util.ResultList;
import com.zcxd.common.util.ToolUtil;
import com.zcxd.db.model.SchdDriveRestrict;
import com.zcxd.db.model.SchdDriveRestrictXh;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 汽车限行规则管理 服务实现类
 * </p>
 *
 * @author admin
 * @since 2021-07-05
 */
@Service
public class SchdDriveRestrictManageService {

    @Resource
    SchdDriveRestrictService schdDriveRestrictService;
    @Resource
    SchdDriveRestrictXhService schdDriveRestrictXhService;

    /**
     * 校验星期序号是否正确
     * @param weekday
     * @return
     */
    private boolean validateWeekday(Integer weekday) {
        return (weekday != null && weekday >=1 && weekday <=7);
    }

    public boolean saveRestrict(SchdDriveRestrictVO schdDriveRestrictVO) throws BusinessException {
        if (!validateWeekday(schdDriveRestrictVO.getWeekday())) {
            throw new BusinessException(ExceptionCode.BusinessError,"无效数据weekday");
        }
        SchdDriveRestrict where = new SchdDriveRestrict();
        where.setWeekday(schdDriveRestrictVO.getWeekday());
        List<SchdDriveRestrict> list = schdDriveRestrictService.listByCondition(where);
        if (list.size() > 0) {
            throw new BusinessException(ExceptionCode.BusinessError,"不能重复添加");
        }
        SchdDriveRestrict schdDriveRestrict = new SchdDriveRestrict();
        BeanUtils.copyProperties(schdDriveRestrictVO,schdDriveRestrict);
        if (schdDriveRestrictVO.getForbidNumberList().size() > 0) {
            schdDriveRestrict.setForbidNumbers(ToolUtil.intListToString(schdDriveRestrictVO.getForbidNumberList()));
        }
        return schdDriveRestrictService.save(schdDriveRestrict);
    }

    public boolean updateRestrict(SchdDriveRestrictVO schdDriveRestrictVO) {
        SchdDriveRestrict schdDriveRestrict = schdDriveRestrictService.getById(schdDriveRestrictVO.getId());
        if (null == schdDriveRestrict) {
            throw new BusinessException(ExceptionCode.BusinessError,"无效参数id");
        }
        if (!validateWeekday(schdDriveRestrictVO.getWeekday())) {
            throw new BusinessException(ExceptionCode.BusinessError,"无效数据weekday");
        }
        SchdDriveRestrict schdDriveRestrictTmp = schdDriveRestrictService.getByWeekDay(schdDriveRestrictVO.getWeekday(),schdDriveRestrict.getDepartmentId());
        if (null != schdDriveRestrictTmp && schdDriveRestrictTmp.getId() != schdDriveRestrict.getId().longValue()) {
            throw new BusinessException(ExceptionCode.BusinessError,"规则不能重复");
        }
        SchdDriveRestrict newSchdDriveRestrict = new SchdDriveRestrict();
        BeanUtils.copyProperties(schdDriveRestrictVO,newSchdDriveRestrict);
        newSchdDriveRestrict.setDepartmentId(null);
        if (schdDriveRestrictVO.getForbidNumberList().size() > 0) {
            newSchdDriveRestrict.setForbidNumbers(ToolUtil.intListToString(schdDriveRestrictVO.getForbidNumberList()));
        }
        return schdDriveRestrictService.updateById(newSchdDriveRestrict);
    }

    public boolean deleteRestrict(Long id) {
        SchdDriveRestrict schdDriveRestrict = schdDriveRestrictService.getById(id);
        if (null == schdDriveRestrict) {
            throw new BusinessException(ExceptionCode.BusinessError,"无效参数id");
        }
        return schdDriveRestrictService.removeById(id);
    }

    public ResultList<SchdDriveRestrictVO> listRestrict(int page, int limit, long departmentId) {
        IPage<SchdDriveRestrict> iPage = schdDriveRestrictService.listPage(page,limit,departmentId);

        List<SchdDriveRestrictVO> driveRestrictVOList = iPage.getRecords().stream().map(schdDriveRestrict -> {
            SchdDriveRestrictVO schdDriveRestrictVO = new SchdDriveRestrictVO();
            BeanUtils.copyProperties(schdDriveRestrict,schdDriveRestrictVO);
            schdDriveRestrictVO.setForbidNumberList(ToolUtil.stringToIntList(schdDriveRestrict.getForbidNumbers()));
            return schdDriveRestrictVO;
        }).collect(Collectors.toList());
        return new ResultList.Builder().total(iPage.getTotal()).list(driveRestrictVOList).build();
    }

    /***********************************************************************************************
     *
     *
     *
     ***********************************************************************************************/

    /**
     * 校验日期类型是否正确
     * @param dayType
     * @return
     */
    private boolean validateDayType(Integer dayType) {
        return (dayType != null && dayType >= 0 && dayType <= 1);
    }

    public boolean saveRestrictXh(SchdDriveRestrictXhVO schdDriveRestrictXhVO) throws BusinessException {
        if (!validateDayType(schdDriveRestrictXhVO.getDayType())) {
            throw new BusinessException(ExceptionCode.BusinessError,"无效数据dataType");
        }
        if (schdDriveRestrictXhVO.getRouteList() == null || schdDriveRestrictXhVO.getRouteList().size() == 0) {
            throw new BusinessException(ExceptionCode.BusinessError,"影响线路不能为空");
        }
        if (schdDriveRestrictXhVO.getPermitNumberList() ==null || schdDriveRestrictXhVO.getPermitNumberList().size() == 0) {
            throw new BusinessException(ExceptionCode.BusinessError,"限行尾号不能为空");
        }
        SchdDriveRestrictXh where = new SchdDriveRestrictXh();
        where.setDayType(schdDriveRestrictXhVO.getDayType());
        where.setDepartmentId(schdDriveRestrictXhVO.getDepartmentId());
        List<SchdDriveRestrictXh> list = schdDriveRestrictXhService.listByCondition(where);
        if (list.size() > 0) {
            throw new BusinessException(ExceptionCode.BusinessError,"不能重复添加");
        }
        SchdDriveRestrictXh schdDriveRestrictXh = new SchdDriveRestrictXh();
        BeanUtils.copyProperties(schdDriveRestrictXhVO,schdDriveRestrictXh);

        schdDriveRestrictXh.setPermitNumbers(ToolUtil.intListToString(schdDriveRestrictXhVO.getPermitNumberList()));
        schdDriveRestrictXh.setEffectRoutes(ToolUtil.longArrayToString(schdDriveRestrictXhVO.getRouteList()));

        return schdDriveRestrictXhService.save(schdDriveRestrictXh);
    }

    public boolean updateRestrictXh(SchdDriveRestrictXhVO schdDriveRestrictXhVO) {
        SchdDriveRestrictXh schdDriveRestrictXh = schdDriveRestrictXhService.getById(schdDriveRestrictXhVO.getId());
        if (null == schdDriveRestrictXh) {
            throw new BusinessException(ExceptionCode.BusinessError,"无效参数id");
        }
        if (!validateDayType(schdDriveRestrictXhVO.getDayType())) {
            throw new BusinessException(ExceptionCode.BusinessError,"无效数据dayType");
        }
        if (schdDriveRestrictXhVO.getRouteList() == null || schdDriveRestrictXhVO.getRouteList().size() == 0) {
            throw new BusinessException(ExceptionCode.BusinessError,"影响线路不能为空");
        }
        if (schdDriveRestrictXhVO.getPermitNumberList() == null || schdDriveRestrictXhVO.getPermitNumberList().size() == 0) {
            throw new BusinessException(ExceptionCode.BusinessError,"允许尾号不能为空");
        }

        SchdDriveRestrictXh where = new SchdDriveRestrictXh();
        where.setDayType(schdDriveRestrictXhVO.getDayType());
        where.setDepartmentId(schdDriveRestrictXhVO.getDepartmentId());
        List<SchdDriveRestrictXh> list = schdDriveRestrictXhService.listByCondition(where);
        if (list.size() > 0 && list.get(0).getId() != schdDriveRestrictXh.getId().longValue()) {
            throw new BusinessException(ExceptionCode.BusinessError,"不能重复添加");
        }

        SchdDriveRestrictXh newSchdDriveRestrictXh = new SchdDriveRestrictXh();
        BeanUtils.copyProperties(schdDriveRestrictXhVO,newSchdDriveRestrictXh);
        newSchdDriveRestrictXh.setPermitNumbers(ToolUtil.intListToString(schdDriveRestrictXhVO.getPermitNumberList()));
        newSchdDriveRestrictXh.setEffectRoutes(ToolUtil.longArrayToString(schdDriveRestrictXhVO.getRouteList()));

        return schdDriveRestrictXhService.updateById(newSchdDriveRestrictXh);
    }

    public boolean deleteRestrictXh(Long id) {
        SchdDriveRestrictXh schdDriveRestrictXh = schdDriveRestrictXhService.getById(id);
        if (null == schdDriveRestrictXh) {
            throw new BusinessException(ExceptionCode.BusinessError,"无效参数id");
        }

        return schdDriveRestrictXhService.removeById(id);
    }

    public ResultList<SchdDriveRestrictXhVO> listRestrictXh(int page, int limit, long departmentId) {

        IPage<SchdDriveRestrictXh> iPage = schdDriveRestrictXhService.listPage(page,limit,departmentId);


        List<SchdDriveRestrictXhVO> driveRestrictXhVOList = iPage.getRecords().stream().map(schdDriveRestrictXh -> {
            SchdDriveRestrictXhVO schdDriveRestrictXhVO = new SchdDriveRestrictXhVO();
            BeanUtils.copyProperties(schdDriveRestrictXh,schdDriveRestrictXhVO);
            schdDriveRestrictXhVO.setPermitNumberList(ToolUtil.stringToIntList(schdDriveRestrictXh.getPermitNumbers()));
            Long[] routeArry = ToolUtil.stringToLongArray(schdDriveRestrictXh.getEffectRoutes());
            if (routeArry != null) {
                List<Long> routeList = Arrays.asList(routeArry);
                schdDriveRestrictXhVO.setRouteList(routeList);
            }
            return schdDriveRestrictXhVO;
        }).collect(Collectors.toList());
        return new ResultList.Builder().total(iPage.getTotal()).list(driveRestrictXhVOList).build();
    }
}
