package com.zcxd.gun.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.zcxd.common.util.EasyExcelUtil;
import com.zcxd.common.util.Result;
import com.zcxd.gun.db.mapper.GunSecurityMapper;
import com.zcxd.db.model.Employee;
import com.zcxd.gun.db.model.GunSecurity;
import com.zcxd.gun.dto.GunSecurityDTO;
import com.zcxd.gun.dto.excel.ExportHead;
import com.zcxd.gun.dto.excel.GunSecurityInfoRecordHead;
import com.zcxd.gun.exception.BusinessException;
import com.zcxd.gun.vo.GunSecurityVO;
import com.zcxd.gun.utils.MinioUtil;
import com.zcxd.gun.vo.GunSecurityQueryVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zccc
 */
@Service
public class GunSecurityService extends ServiceImpl<GunSecurityMapper, GunSecurity> {
    @Autowired
    MinioUtil minioUtil;
    @Value("${minio.gun}")
    String gunBucketName;
    static final String SECURITY_BUCKET_PREFIX = "security/";

    /**
     * 新增保安证
     * @param gunSecurityVo 保安证信息
     * @return 新增结果
     */
    public Result saveGunSecurity(GunSecurityVO gunSecurityVo) {
        // to GunSecurity
        GunSecurity gunSecurity = new GunSecurity();
        BeanUtils.copyProperties(gunSecurityVo, gunSecurity);

        gunSecurity.setStatus(1);
        gunSecurity.setId(null);

        // save
        return save(gunSecurity) ? Result.success() : Result.fail("添加失败");
    }

    /**
     * 通过Id修改保安证
     * @param gunSecurityVo 保安证属性
     * @return 修改结果
     */
    public Object updateGunSecurityById(GunSecurityVO gunSecurityVo) {
        GunSecurity gunSecurity = getById(gunSecurityVo.getId());
        BeanUtils.copyProperties(gunSecurityVo, gunSecurity);

        boolean update = updateById(gunSecurity);
        if(update) {
            return Result.success();
        }

        return Result.fail("修改保安证失败");
    }

    /**
     * @Title getGunSecurityBySecurityNum
     * @Description 根据保安证号查询保安证
     * @return 返回类型 List<GunSecurity>
     */
    private List<GunSecurity> getGunSecurityBySecurityNum(GunSecurityVO gunSecurityVo) {
        QueryWrapper<GunSecurity> queryWrapper = new QueryWrapper<>();
        if(!StringUtils.isBlank(gunSecurityVo.getSecurityNum())){
            queryWrapper.eq("security_num", gunSecurityVo.getSecurityNum());
        } else {
            return null;
        }
        queryWrapper.eq("department_id", gunSecurityVo.getDepartmentId());
        queryWrapper.eq("deleted", 0);
        return baseMapper.selectList(queryWrapper);
    }

    /**
     * @Title gunSecurityNumExits
     * @Description 判断是否已有保安证编号
     * @return 返回类型 boolean
     */
    public boolean gunSecurityNumExits(GunSecurityVO gunSecurityVo) {
        List<GunSecurity> gunSecurityBySecurityNum = getGunSecurityBySecurityNum(gunSecurityVo);
        return gunSecurityBySecurityNum != null && !gunSecurityBySecurityNum.isEmpty();
    }

    /**
     * @Title getGunSecurityByEmployee
     * @Description 根据员工号查询保安证
     * @return 返回类型 List<GunSecurity>
     */
    private List<GunSecurity> getGunSecurityByEmployee(GunSecurityVO gunSecurityVo) {
        QueryWrapper<GunSecurity> queryWrapper = new QueryWrapper<>();
        if(gunSecurityVo.getEmployeeId() != null && gunSecurityVo.getEmployeeId() > 0){
            queryWrapper.eq("employee_id", gunSecurityVo.getEmployeeId());
        } else {
            return null;
        }
        queryWrapper.eq("department_id", gunSecurityVo.getDepartmentId());
        queryWrapper.eq("deleted", 0);
        return baseMapper.selectList(queryWrapper);
    }

    /**
     * @Title gunSecurityNumExits
     * @Description 判断是否已有保安证编号
     * @return 返回类型 boolean
     */
    public boolean gunSecurityEmployeeExits(GunSecurityVO gunSecurityVo) {
        List<GunSecurity> gunSecurityByEmployee = getGunSecurityByEmployee(gunSecurityVo);
        return gunSecurityByEmployee != null && !gunSecurityByEmployee.isEmpty();
    }

    /**
     * 分页查询保安证
     * @param page 页数
     * @param limit 数量
     * @param queryVo 查询条件
     * @return 查询结果
     * @throws IllegalAccessException 错误
     */
    public Object findListByPage(Integer page, Integer limit, GunSecurityQueryVO queryVo) throws IllegalAccessException {
        Page<GunSecurityDTO> ipage = new Page<>(page,limit);
        MPJLambdaWrapper<GunSecurity> gunSecurityMPJLambdaWrapper = getGunSecurityMPJLambdaWrapper(queryVo);
        if (queryVo.getHasPhoto() != null) {
            if (queryVo.getHasPhoto()) {
                gunSecurityMPJLambdaWrapper.isNotNull(GunSecurity::getSecurityPhoto).ne(GunSecurity::getSecurityPhoto, "");
            } else {
                gunSecurityMPJLambdaWrapper.isNull(GunSecurity::getSecurityPhoto).or().ne(GunSecurity::getSecurityPhoto, "");
            }
        }
        return Result.success(baseMapper.selectJoinPage(ipage, GunSecurityDTO.class,
                gunSecurityMPJLambdaWrapper));
    }

    /**
     * 构建保安证查询条件Map
     * @param queryVo 查询条件
     * @return Map
     */
    private Map<SFunction<GunSecurity, ?> , Object> getGunSecurityQueryMap(GunSecurityQueryVO queryVo) {
        Map<SFunction<GunSecurity, ?>, Object> map = new HashMap<>();
        map.put(GunSecurity::getEmployeeId, queryVo.getEmployeeId());
        map.put(GunSecurity::getSecurityNum, queryVo.getSecurityNum());
        map.put(GunSecurity::getAuthority, queryVo.getAuthority());
        map.put(GunSecurity::getDepartmentId, queryVo.getDepartmentId());
        map.put(GunSecurity::getStatus, queryVo.getStatus());
        return map;
    }

    /**
     * 构建查询条件Wrapper
     * @param queryVo 查询条件
     * @return Wrapper
     */
    private MPJLambdaWrapper<GunSecurity> getGunSecurityMPJLambdaWrapper(GunSecurityQueryVO queryVo) {
        return new MPJLambdaWrapper<GunSecurity>()
                .selectAll(GunSecurity.class)
                .selectAs(Employee::getEmpName, GunSecurityDTO::getEmpName)
                .selectAs(Employee::getEmpNo, GunSecurityDTO::getEmpNo)
                .selectAs(Employee::getIdno, GunSecurityDTO::getIdno)
                .selectAs(Employee::getStatusT, GunSecurityDTO::getStatusT)
                .selectAs(Employee::getMobile, GunSecurityDTO::getMobile)
                .leftJoin(Employee.class, Employee::getId, GunSecurity::getEmployeeId)
                .allEq(getGunSecurityQueryMap(queryVo), false)
                .eq(StringUtils.isNotBlank(queryVo.getName()), Employee::getEmpName, queryVo.getName());
    }

    /**
     * 查询保安证
     * @param queryVo 查询条件
     * @return 查询结果
     */
    public Object findList(GunSecurityQueryVO queryVo) {
        MPJLambdaWrapper<GunSecurity> gunSecurityMPJLambdaWrapper = getGunSecurityMPJLambdaWrapper(queryVo);
        if (queryVo.getHasPhoto() != null) {
            if (queryVo.getHasPhoto()) {
                gunSecurityMPJLambdaWrapper.isNotNull(GunSecurity::getSecurityPhoto);
            } else {
                gunSecurityMPJLambdaWrapper.isNull(GunSecurity::getSecurityPhoto);
            }
        }

        List<GunSecurityDTO> gunSecurityDTOS = baseMapper.selectJoinList(GunSecurityDTO.class, gunSecurityMPJLambdaWrapper);

        return Result.success(gunSecurityDTOS);
    }

    /**
     * 上传保安证图片
     * @param gunSecurityVo 保安证属性
     * @param file 文件
     * @return
     */
    public Result uploadGunSecurityPhoto(GunSecurityVO gunSecurityVo, MultipartFile file) {
        if (file != null && file.getSize() > 0) {
            // 上传图片
            String minioPath = SECURITY_BUCKET_PREFIX + file.getOriginalFilename();
            String uploadRes;
            try {
                uploadRes = minioUtil.uploadFileByStream(file.getInputStream(), minioPath, gunBucketName, false);
            } catch (Exception e) {
                return Result.fail("文件上传出现错误");
            }
            if (!StringUtils.isBlank(uploadRes)) {
                return Result.fail(uploadRes);
            }

            // vo中增加图片地址
            gunSecurityVo.setSecurityPhoto(minioPath);
        }
        return Result.success();
    }

    /**
     * 导出保安证信息
     * @param response 传输
     * @param queryVO 查询条件
     */
    public void exportGunLicenceInfo(HttpServletResponse response, GunSecurityQueryVO queryVO) {
        MPJLambdaWrapper<GunSecurity> queryWrapper = getGunSecurityMPJLambdaWrapper(queryVO);
        List<GunSecurityDTO> gunSecurityDTOS = baseMapper.selectJoinList(GunSecurityDTO.class, queryWrapper);

        List<GunSecurityInfoRecordHead> gunSecurityInfoRecordHeads = new ArrayList<>();
        for (int i = 0; i < gunSecurityDTOS.size(); i++) {
            GunSecurityDTO gunSecurityDTO = gunSecurityDTOS.get(i);
            GunSecurityInfoRecordHead gunInfoRecordHead = new GunSecurityInfoRecordHead();

            BeanUtils.copyProperties(gunSecurityDTO, gunInfoRecordHead);
            gunInfoRecordHead.setIndex(i + 1L);
            gunInfoRecordHead.setStatus(getGunSecurityStatusStr(gunSecurityDTO.getStatus()));
            gunSecurityInfoRecordHeads.add(gunInfoRecordHead);
        }

        gunSecurityInfoRecordHeads.sort((x, y) -> Long.compare(x.getIndex(), y.getIndex()));
        String title = "保安证信息";
        try {
            ExportHead.setExcelPropertyTitle(GunSecurityInfoRecordHead.class, title);
            EasyExcelUtil.exportPrintExcel(response,title,GunSecurityInfoRecordHead.class, gunSecurityInfoRecordHeads,null);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(-1,"导出保安证信息出错");
        }
    }

    private String getGunSecurityStatusStr(Integer status) {
        switch (status) {
            case 1:
                return "在库";
            default:
                return "状态有误";
        }
    }
}
