package com.zcxd.gun.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.zcxd.common.util.EasyExcelUtil;
import com.zcxd.common.util.Result;
import com.zcxd.common.util.SecurityUtils;
import com.zcxd.db.model.EmployeeJob;
import com.zcxd.gun.db.mapper.GunLicenceMapper;
import com.zcxd.db.model.Employee;
import com.zcxd.gun.db.model.GunLicence;
import com.zcxd.gun.db.model.GunSecurity;
import com.zcxd.gun.dto.GunLicenceDTO;
import com.zcxd.gun.dto.excel.ExportHead;
import com.zcxd.gun.dto.excel.GunLicenceInfoRecordHead;
import com.zcxd.gun.exception.BusinessException;
import com.zcxd.gun.vo.GunLicenceQueryVO;
import com.zcxd.gun.vo.GunLicenceVO;
import com.zcxd.gun.utils.MinioUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @author zccc
 */
@Service
public class GunLicenceService extends ServiceImpl<GunLicenceMapper, GunLicence> {
    @Autowired
    MinioUtil minioUtil;
    @Value("${minio.gun}")
    String gunBucketName;
    static final String LICENCE_BUCKET_PREFIX = "licence/";

    /**
     * 新增持枪证
     * @param gunLicenceVo 持枪证信息
     * @return 新增结果
     */
    public Result saveGunLicence(GunLicenceVO gunLicenceVo) {
        // to GunLicence
        GunLicence gunGunLicence = new GunLicence();
        BeanUtils.copyProperties(gunLicenceVo, gunGunLicence);

        gunGunLicence.setId(null);

        // save
        return save(gunGunLicence) ? Result.success() : Result.fail("添加失败");
    }

    /**
     * 通过Id修改持枪证
     * @param gunLicenceVo 持枪证属性
     * @return 修改结果
     */
    public Object updateGunLicenceById(GunLicenceVO gunLicenceVo) {
        GunLicence gunLicence = getById(gunLicenceVo.getId());
        BeanUtils.copyProperties(gunLicenceVo, gunLicence);

        boolean update = updateById(gunLicence);
        if(update) {
            return Result.success();
        }

        return Result.fail("修改持枪证失败");
    }

    /**
     * @Title getGunLicenceByLicenceNum
     * @Description 根据持枪证号查询持枪证
     * @return 返回类型 List<GunLicence>
     */
    public List<GunLicence> getGunLicenceByLicenceNum(GunLicenceVO gunLicenceVo) {
        LambdaQueryWrapper<GunLicence> queryWrapper = new LambdaQueryWrapper<>();
        if(!StringUtils.isBlank(gunLicenceVo.getGunLicenceNum())){
            queryWrapper.eq(GunLicence::getGunLicenceNum, gunLicenceVo.getGunLicenceNum());
        } else {
            return null;
        }
        queryWrapper.eq(GunLicence::getDepartmentId, gunLicenceVo.getDepartmentId());
        return baseMapper.selectList(queryWrapper);
    }

    /**
     * @Title gunLicenceNumExits
     * @Description 判断是否已有持枪证编号
     * @return 返回类型 boolean
     */
    public boolean gunLicenceNumExits(GunLicenceVO gunLicenceVo) {
        List<GunLicence> gunLicenceByLicenceNum = getGunLicenceByLicenceNum(gunLicenceVo);
        return gunLicenceByLicenceNum != null && !gunLicenceByLicenceNum.isEmpty();
    }

    /**
     * @Title getGunLicenceByEmployee
     * @Description 根据员工号查询持枪证
     * @return 返回类型 List<GunLicence>
     */
    private List<GunLicence> getGunLicenceByEmployee(GunLicenceVO gunLicenceVo) {
        QueryWrapper<GunLicence> queryWrapper = new QueryWrapper<>();
        if(gunLicenceVo.getEmployeeId() != null && gunLicenceVo.getEmployeeId() > 0){
            queryWrapper.eq("employee_id", gunLicenceVo.getEmployeeId());
        } else {
            return null;
        }
        queryWrapper.eq("department_id", gunLicenceVo.getDepartmentId());
        queryWrapper.eq("deleted", 0);
        return baseMapper.selectList(queryWrapper);
    }

    /**
     * @Title gunLicenceEmployeeExits
     * @Description 判断是否已有持枪证编号
     * @return 返回类型 boolean
     */
    public boolean gunLicenceEmployeeExits(GunLicenceVO gunLicenceVo) {
        List<GunLicence> gunLicenceByEmployee = getGunLicenceByEmployee(gunLicenceVo);
        return gunLicenceByEmployee != null && !gunLicenceByEmployee.isEmpty();
    }

    /**
     * 分页查询持枪证
     * @param page 页数
     * @param limit 数量
     * @param wrapper 查询条件
     * @return 查询结果
     * @throws IllegalAccessException 错误
     */
    public Object findListByPage(Integer page, Integer limit, MPJLambdaWrapper<GunLicence> wrapper) throws IllegalAccessException {
        Page<GunLicenceDTO> ipage = new Page<>(page,limit);
        Page<GunLicenceDTO> data = baseMapper.selectJoinPage(ipage, GunLicenceDTO.class,
                wrapper);
        // 手机、身份证解码
        data.getRecords().forEach(x -> {
            x.setMobile(SecurityUtils.decryptAES(x.getMobile()));
            x.setIdno(SecurityUtils.decryptAES(x.getIdno()));
        });
        return Result.success(data);
    }

    /**
     * 构建持枪证查询条件Map
     * @param queryVo 查询条件
     * @return Map
     */
    private Map<SFunction<GunLicence, ?>, Object> getGunLicenceQueryMap(GunLicenceQueryVO queryVo) {
        Map<SFunction<GunLicence, ?>, Object> map = new HashMap<>();
        map.put(GunLicence::getGunLicenceNum, queryVo.getGunLicenceNum());
        map.put(GunLicence::getDepartmentId, queryVo.getDepartmentId());
        return map;
    }

    /**
     * 构建查询条件Wrapper
     * @param queryVo 查询条件
     * @return Wrapper
     */
    public MPJLambdaWrapper<GunLicence> getGunLicenceMPJLambdaWrapper(GunLicenceQueryVO queryVo) {
        return new MPJLambdaWrapper<GunLicence>()
                .selectAll(GunLicence.class)
                .selectAs(Employee::getEmpName, GunLicenceDTO::getEmpName)
                .selectAs(Employee::getEmpNo, GunLicenceDTO::getEmpNo)
                .selectAs(Employee::getIdno, GunLicenceDTO::getIdno)
                .selectAs(Employee::getStatusT, GunLicenceDTO::getStatusT)
                .selectAs(Employee::getMobile, GunLicenceDTO::getMobile)
                .selectAs(EmployeeJob::getName, GunLicenceDTO::getJobName)
                .selectAs(GunSecurity::getSecurityNum, GunLicenceDTO::getSecurityNum)
                .leftJoin(Employee.class, Employee::getId, GunLicence::getEmployeeId)
                .leftJoin(GunSecurity.class, GunSecurity::getEmployeeId, GunLicence::getEmployeeId)
                .leftJoin(EmployeeJob.class, EmployeeJob::getId, Employee::getJobIds)
                .allEq(getGunLicenceQueryMap(queryVo), false);
    }

    /**
     * 上传持枪证图片
     * @param gunLicenceVO 持枪证属性
     * @param file 文件
     * @return
     */
    public Result uploadGunLicencePhoto(GunLicenceVO gunLicenceVO, MultipartFile file) {
        if (file != null && file.getSize() > 0) {
            // 上传图片
            String minioPath = LICENCE_BUCKET_PREFIX + file.getOriginalFilename();
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
            gunLicenceVO.setGunLicencePhoto(file.getOriginalFilename());
        }
        return Result.success();
    }

    /**
     * 导出持枪证信息
     * @param response 网络
     * @param wrapper 查询条件
     */
    public void exportGunLicenceInfo(HttpServletResponse response, MPJLambdaWrapper<GunLicence> wrapper) {
        List<GunLicenceDTO> gunLicenceDTOS = baseMapper.selectJoinList(GunLicenceDTO.class, wrapper);

        List<GunLicenceInfoRecordHead> gunLicenceInfoRecordHeads = new ArrayList<>();
        for (int i = 0; i < gunLicenceDTOS.size(); i++) {
            GunLicenceDTO gunLicenceDTO = gunLicenceDTOS.get(i);
            GunLicenceInfoRecordHead gunInfoRecordHead = new GunLicenceInfoRecordHead();
            gunInfoRecordHead.setIndex(i + 1L);
            BeanUtils.copyProperties(gunLicenceDTO, gunInfoRecordHead);
            gunInfoRecordHead.setGunLicenceValidity(GunService.SDF.format(new Date(gunLicenceDTO.getGunLicenceValidity())));
            gunLicenceInfoRecordHeads.add(gunInfoRecordHead);
        }

        gunLicenceInfoRecordHeads.sort((x, y) -> Long.compare(x.getIndex(), y.getIndex()));
        String title = "持枪证信息";
        try {
            ExportHead.setExcelPropertyTitle(GunLicenceInfoRecordHead.class, title);
            EasyExcelUtil.exportPrintExcel(response,title,GunLicenceInfoRecordHead.class, gunLicenceInfoRecordHeads,null);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(-1,"导出持枪证信息出错");
        }
    }
}
