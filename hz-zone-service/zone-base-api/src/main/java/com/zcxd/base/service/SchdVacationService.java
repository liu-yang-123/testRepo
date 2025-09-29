package com.zcxd.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zcxd.base.dto.SchdVacateAdjustDTO;
import com.zcxd.base.dto.SchdVacatePlanDTO;
import com.zcxd.base.dto.SchdVacateSettingDTO;
import com.zcxd.base.dto.SchdVacateSettingSumDTO;
import com.zcxd.base.exception.BusinessException;
import com.zcxd.base.exception.ExceptionCode;
import com.zcxd.base.vo.SchdVacateAdjustVO;
import com.zcxd.base.vo.SchdVacatePlanVO;
import com.zcxd.base.vo.SchdVacateSettingItemVO;
import com.zcxd.base.vo.SchdVacateSettingVO;
import com.zcxd.common.constant.DeleteFlagEnum;
import com.zcxd.common.constant.VacateAdjustTypeEnum;
import com.zcxd.common.util.DateTimeUtil;
import com.zcxd.common.util.ResultList;
import com.zcxd.common.util.ToolUtil;
import com.zcxd.db.model.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 员工休息计划设置 服务实现类
 * </p>
 *
 * @author admin
 * @since 2021-07-05
 */
@Service
public class SchdVacationService {

    private static int PLAN_TYPE_CLEAN = 0;
    private static int PLAN_TYPE_SECURITY = 1;
    @Resource
    SchdVacatePlanService schdVacatePlanService;
    @Resource
    SchdVacateSettingService schdVacateSettingService;
    @Resource
    SchdVacateAdjustService schdVacateAdjustService;

    @Resource
    SchdAlternateAssignService assignService;

    @Resource
    SysUserService sysUserService;
    @Resource
    EmployeeService employeeService;
    @Resource
    EmployeeJobService employeeJobService;

    /**
     * 保存计划配置
     * @param vacatePlanVO
     */
    public void savePlan(SchdVacatePlanVO vacatePlanVO) {
        //验证名称是否已经存在
        QueryWrapper wrapper = Wrappers.query().eq("name",vacatePlanVO.getName()).eq("plan_type",vacatePlanVO.getPlanType())
                .eq("department_id",vacatePlanVO.getDepartmentId()).eq("deleted",0);
        SchdVacatePlan exist = schdVacatePlanService.getOne(wrapper);
        if(exist != null){
            throw new BusinessException(1,"季度管理名称已经存在");
        }
        SchdVacatePlan vacatePlan = new SchdVacatePlan();
        BeanUtils.copyProperties(vacatePlanVO,vacatePlan);
        schdVacatePlanService.save(vacatePlan);
    }

    /**
     * 更新休息计划
     * @param vacatePlanVO
     * @throws BusinessException
     */
    public void updatePlan(SchdVacatePlanVO vacatePlanVO) throws BusinessException {
        SchdVacatePlan oldSchdVacatePlan = schdVacatePlanService.getById(vacatePlanVO.getId());
        if (null == oldSchdVacatePlan) {
            throw new BusinessException(ExceptionCode.BusinessError,"无效计划id");
        }
        //验证名称是否已经存在
        QueryWrapper wrapper = Wrappers.query().eq("name",vacatePlanVO.getName()).notIn("id",vacatePlanVO.getId())
                .eq("plan_type",oldSchdVacatePlan.getPlanType()).eq("department_id",vacatePlanVO.getDepartmentId()).eq("deleted",0);
        SchdVacatePlan exist = schdVacatePlanService.getOne(wrapper);
        if(exist != null){
            throw new BusinessException(1,"季度管理名称已经存在");
        }
        SchdVacatePlan vacatePlan = new SchdVacatePlan();
        BeanUtils.copyProperties(vacatePlanVO,vacatePlan);
        vacatePlan.setDepartmentId(null);
        vacatePlan.setPlanType(null);
        schdVacatePlanService.updateById(vacatePlan);
    }

    /**
     * 逻辑删除休息计划
     * @param id
     */
    public void deletePlan(Long id) {
        SchdVacatePlan oldVacatePlan = schdVacatePlanService.getById(id);
        if (null == oldVacatePlan) {
            throw new BusinessException(ExceptionCode.BusinessError,"无效计划id");
        }
        //验证删除条件
        QueryWrapper wrapper = Wrappers.query().eq("plan_id",id)
                .eq("deleted",DeleteFlagEnum.NOT.getValue()).last("LIMIT 1");
        SchdVacateSetting setting = schdVacateSettingService.getOne(wrapper);
        if (setting != null){
            throw new BusinessException(1,"当前季度管理存在员工，无法删除！");
        }

        SchdAlternateAssign assign = assignService.getOne(wrapper);
        if (assign != null){
            throw new BusinessException(1,"当前季度管理存在主备员工数据，无法删除！");
        }

        SchdVacatePlan newVacatePlan = new SchdVacatePlan();
        newVacatePlan.setId(id);
        newVacatePlan.setDeleted(DeleteFlagEnum.YES.getValue());
        schdVacatePlanService.updateById(newVacatePlan);
    }

    /**
     *
     * @param id
     * @return
     */
    public SchdVacatePlanDTO getPlan(Long id) {
        SchdVacatePlan oldSchdVacatePlan = schdVacatePlanService.getById(id);
        if (null == oldSchdVacatePlan) {
            throw new BusinessException(ExceptionCode.BusinessError,"无效计划id");
        }
        SchdVacatePlanDTO schdVacatePlanDTO = new SchdVacatePlanDTO();
        BeanUtils.copyProperties(oldSchdVacatePlan,schdVacatePlanDTO);

        if (oldSchdVacatePlan.getCreateUser() != 0L) {
            SysUser sysUser = sysUserService.getById(oldSchdVacatePlan.getCreateUser());
            if (null == sysUser) {
                schdVacatePlanDTO.setCreateUserName(sysUser.getUsername());
            }
        }
        if (oldSchdVacatePlan.getUpdateUser() != 0L) {
            SysUser sysUser = sysUserService.getById(oldSchdVacatePlan.getUpdateUser());
            if (null == sysUser) {
                schdVacatePlanDTO.setUpdateUserName(sysUser.getUsername());
            }
        }
        return schdVacatePlanDTO;
    }
    /**
     * 分页查询消息计划
     * @param page
     * @param limit
     * @param name
     * @return
     */
    public ResultList<SchdVacatePlanVO> listPagePlan(int page, int limit, long departmentId, int planType,String name) {
        IPage<SchdVacatePlan> planIPage =  schdVacatePlanService.listPage(page, limit, departmentId,planType,name);

        List<SchdVacatePlanVO> schdVacateSettingVOList = planIPage.getRecords().stream().map(schdVacatePlan -> {
            SchdVacatePlanVO schdVacatePlanVO = new SchdVacatePlanVO();
            BeanUtils.copyProperties(schdVacatePlan,schdVacatePlanVO);
            return schdVacatePlanVO;
        }).collect(Collectors.toList());
        return new ResultList.Builder().total(planIPage.getTotal()).list(schdVacateSettingVOList).build();
    }

    /**
     * 查询所有消息计划
     * @return
     */
    public List<SchdVacatePlanVO> listAllPlan(long departmentId,int planType) {
        List<SchdVacatePlan> settingList =  schdVacatePlanService.listAll(departmentId,planType);

        List<SchdVacatePlanVO> schdVacateSettingVOList = settingList.stream().map(schdVacateSetting -> {
            SchdVacatePlanVO schdVacateSettingVO = new SchdVacatePlanVO();
            BeanUtils.copyProperties(schdVacateSetting,schdVacateSettingVO);
            return schdVacateSettingVO;
        }).collect(Collectors.toList());
        return schdVacateSettingVOList;
    }


    /***********************************************************************************************
     *    分隔线
     ***********************************************************************************************/


    /**
     * 保存计划配置
     * @param schdVacateSettingVO
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveSetting(SchdVacateSettingVO schdVacateSettingVO) throws BusinessException{
        if (schdVacateSettingVO.getVacatePlanItemVOS().size() > 0) {
            Set<Long> empIds = schdVacateSettingVO.getVacatePlanItemVOS().stream().map(SchdVacateSettingItemVO::getEmpId).collect(Collectors.toSet());
            if (empIds.size() < schdVacateSettingVO.getVacatePlanItemVOS().size()) {
                throw new BusinessException(ExceptionCode.BusinessError, "提交员工数据重复");
            }
            List<SchdVacateSetting> schdVacateSettingList = schdVacateSettingService.listByBatchEmpId(schdVacateSettingVO.getPlanId(),empIds);
            if (schdVacateSettingList.size() > 0) {
                List<Long> existEmpIds = schdVacateSettingList.stream().map(SchdVacateSetting::getEmpId).collect(Collectors.toList());
                List<Employee> employeeList = employeeService.listByIds(existEmpIds);
                String employeeStr = employeeList.stream().map(Employee::getEmpName).collect(Collectors.joining(","));
                throw new BusinessException(ExceptionCode.BusinessError, employeeStr+"不能重复添加");
            }

            List<SchdVacateSetting> newSchdVacateSettingList = schdVacateSettingVO.getVacatePlanItemVOS().stream().map(schdVacateSettingItemVO -> {
                SchdVacateSetting newSchdVacateSetting = new SchdVacateSetting();
                newSchdVacateSetting.setEmpId(schdVacateSettingItemVO.getEmpId());
                if (schdVacateSettingItemVO.getWeekdays() != null && schdVacateSettingItemVO.getWeekdays().size() > 0) {
                    String weekdays = ToolUtil.intListToString(schdVacateSettingItemVO.getWeekdays());
                    newSchdVacateSetting.setVacateDays(weekdays);
                }
                newSchdVacateSetting.setPlanId(schdVacateSettingVO.getPlanId());
                return newSchdVacateSetting;
            }).collect(Collectors.toList());
            //批量插入
            schdVacateSettingService.saveBatch(newSchdVacateSettingList);
        }
    }

    /**
     * 更新休息设置
     * @param schdVacateSettingItemVO
     * @throws BusinessException
     */
    public void updateSetting(SchdVacateSettingItemVO schdVacateSettingItemVO) throws BusinessException {
        SchdVacateSetting oldSchdVacateSetting = schdVacateSettingService.getById(schdVacateSettingItemVO.getId());
        if (null == oldSchdVacateSetting) {
            throw new BusinessException(ExceptionCode.BusinessError,"无效设置");
        }
        SchdVacateSetting newSchdVacateSetting = new SchdVacateSetting();
        newSchdVacateSetting.setId(schdVacateSettingItemVO.getId());
        if (schdVacateSettingItemVO.getWeekdays().size() > 0) {
            newSchdVacateSetting.setVacateDays(ToolUtil.intListToString(schdVacateSettingItemVO.getWeekdays()));
        } else {
            newSchdVacateSetting.setVacateDays("");
        }

        schdVacateSettingService.updateById(newSchdVacateSetting);
    }

    /**
     * 逻辑删除休息计划
     * @param id
     */
    public void deleteSetting(Long id) {
        SchdVacateSetting oldSchdVacateSetting = schdVacateSettingService.getById(id);
        if (null == oldSchdVacateSetting) {
            throw new BusinessException(ExceptionCode.BusinessError,"无效设置id");
        }
        SchdVacateSetting newSchdVacateSetting = new SchdVacateSetting();
        newSchdVacateSetting.setId(id);
        newSchdVacateSetting.setDeleted(DeleteFlagEnum.YES.getValue());
        schdVacateSettingService.updateById(newSchdVacateSetting);
    }

    /**
     *
     * @param id
     * @return
     */
    public SchdVacateSettingDTO getSetting(Long id) {
        SchdVacateSetting oldSchdVacateSetting = schdVacateSettingService.getById(id);
        if (null == oldSchdVacateSetting) {
            throw new BusinessException(ExceptionCode.BusinessError,"无效设置id");
        }
        SchdVacateSettingDTO schdVacateSettingDTO = new SchdVacateSettingDTO();
        BeanUtils.copyProperties(oldSchdVacateSetting,schdVacateSettingDTO);
        schdVacateSettingDTO.setWeekdays(ToolUtil.stringToIntList(oldSchdVacateSetting.getVacateDays()));

        if (oldSchdVacateSetting.getCreateUser() != 0L) {
            SysUser sysUser = sysUserService.getById(oldSchdVacateSetting.getCreateUser());
            if (null == sysUser) {
                schdVacateSettingDTO.setCreateUserName(sysUser.getUsername());
            }
        }
        if (oldSchdVacateSetting.getUpdateUser() != 0L) {
            SysUser sysUser = sysUserService.getById(oldSchdVacateSetting.getUpdateUser());
            if (null == sysUser) {
                schdVacateSettingDTO.setUpdateUserName(sysUser.getUsername());
            }
        }
        return schdVacateSettingDTO;
    }
    /**
     * 分页查询休息设置列表
     * @param page
     * @param limit
     * @param planId - 计划
     * @param jobType - 岗位
     * @return
     */
    public ResultList<SchdVacateSettingDTO> listPageSetting(int page, int limit, Long planId, Integer jobType, Integer weekday) {
        List<Employee> employeeList = null;
        SchdVacatePlan schdVacatePlan = schdVacatePlanService.getById(planId);
        if (null == schdVacatePlan)  {
            return new ResultList.Builder().total(0).list(null).build();
        }
        Set<Long> empIds = null;
        Set<Long> jobIds = null;
        if (jobType != null) {
            //查询所有员工id
            Employee employee = new Employee();
            employee.setJobType(jobType);
            employee.setDepartmentId(schdVacatePlan.getDepartmentId());
            employeeList = employeeService.getEmployeeByCondition(employee);
            empIds = employeeList.stream().map(Employee::getId).collect(Collectors.toSet());
            jobIds = employeeList.stream().map(Employee::getJobIds).collect(Collectors.toSet());
        }
        IPage<SchdVacateSetting> settingIPage =  schdVacateSettingService.listPage(page, limit, planId,empIds, weekday);
        if (settingIPage.getRecords().size() == 0)  {
            return new ResultList.Builder().total(0).list(null).build();
        }
        if (jobType == null) {
            empIds = settingIPage.getRecords().stream().map(SchdVacateSetting::getEmpId).collect(Collectors.toSet());
            employeeList = employeeService.listByIds(empIds);
            jobIds = employeeList.stream().map(Employee::getJobIds).collect(Collectors.toSet());
        }
        List<EmployeeJob> jobList = employeeJobService.listByIds(jobIds);
        Map<Long,EmployeeJob> employeeJobMap = jobList.stream().collect(Collectors.toMap(EmployeeJob::getId,Function.identity(),(key1,key2)->key2));
        Map<Long,Employee> employeeMap = employeeList.stream().collect(Collectors.toMap(Employee::getId, Function.identity(),(key1,key2)->key2));

        List<SchdVacateSettingDTO> schdVacateSettingDTOS = settingIPage.getRecords().stream().map(schdVacateSetting -> {
            SchdVacateSettingDTO schdVacateSettingDTO = new SchdVacateSettingDTO();
            BeanUtils.copyProperties(schdVacateSetting,schdVacateSettingDTO);
            Employee employee = employeeMap.get(schdVacateSetting.getEmpId());
            if (null != employee) {
                schdVacateSettingDTO.setEmpName(employee.getEmpName());
                EmployeeJob employeeJob = employeeJobMap.get(employee.getJobIds());
                if (null != employeeJob) {
                    schdVacateSettingDTO.setJobName(employeeJob.getName());
                }
            }
            schdVacateSettingDTO.setPlanName(schdVacatePlan.getName());
            schdVacateSettingDTO.setWeekdays(ToolUtil.stringToIntList(schdVacateSetting.getVacateDays()));
            return schdVacateSettingDTO;
        }).collect(Collectors.toList());
        return ResultList.builder().total(settingIPage.getTotal()).list(schdVacateSettingDTOS).build();
    }

    /**
     * 统计休息计划中员工休息情况
     * @param planId
     * @return
     */
    public List<SchdVacateSettingSumDTO> sumSetting(Long planId,Integer jobType) {

        Set<Long> empIds = null;
        Set<Long> jobIds = null;
        List<Employee> employeeList = null;
        List<EmployeeJob> employeeJobList = null;

        SchdVacatePlan schdVacatePlan = schdVacatePlanService.getById(planId);
        if (null == schdVacatePlan) {
            return null;
        }
        if (jobType != null) {
            //查询所有员工id
            Employee employee = new Employee();
            employee.setJobType(jobType);
            employee.setDepartmentId(schdVacatePlan.getDepartmentId());
            employeeList = employeeService.getEmployeeByCondition(employee);
            empIds = employeeList.stream().map(Employee::getId).collect(Collectors.toSet());
            jobIds = employeeList.stream().map(Employee::getJobIds).collect(Collectors.toSet());
        }

        List<SchdVacateSetting> settingList = schdVacateSettingService.listByPlanId(planId,empIds);
        if (settingList.size() == 0) {
            return null;
        }
        if (jobType == null) {
            empIds = settingList.stream().map(SchdVacateSetting::getEmpId).collect(Collectors.toSet());
            employeeList = employeeService.listByIds(empIds);
            jobIds = employeeList.stream().map(Employee::getJobIds).collect(Collectors.toSet());
        }
        Map<Long,Employee> employeeMap = employeeList.stream().collect(Collectors.toMap(Employee::getId,Function.identity(),(key1,key2)->key2));
        employeeJobList = employeeJobService.listByIds(jobIds);
        Map<Long,EmployeeJob> employeeJobMap = employeeJobList.stream().collect(Collectors.toMap(EmployeeJob::getId,Function.identity(),(key1,key2)->key2));
        Map<Long,SchdVacateSettingSumDTO> settingSumDTOMap = new HashMap<>();

        //循环统计每日上班人数
        for (SchdVacateSetting setting : settingList) {
            Long empId = setting.getEmpId();
            Employee employee = employeeMap.get(empId);
            if (employee == null) {
                continue;
            }
            Long jobId = employee.getJobIds();
            SchdVacateSettingSumDTO settingSumDTO = settingSumDTOMap.get(jobId);
            if (settingSumDTO == null) {
                settingSumDTO = new SchdVacateSettingSumDTO();
                EmployeeJob employeeJob = employeeJobMap.get(jobId);
                if (employeeJob != null) {
                    settingSumDTO.setJobName(employeeJob.getName());
                }else{
                    settingSumDTO.setJobName("");
                }
                settingSumDTOMap.put(jobId,settingSumDTO);
            }
            //
            if (!setting.getVacateDays().contains("1")) {
                settingSumDTO.setCountMon(settingSumDTO.getCountMon()+1);
            }
            if (!setting.getVacateDays().contains("2")) {
                settingSumDTO.setCountTue(settingSumDTO.getCountTue()+1);
            }
            if (!setting.getVacateDays().contains("3")) {
                settingSumDTO.setCountWed(settingSumDTO.getCountWed()+1);
            }
            if (!setting.getVacateDays().contains("4")) {
                settingSumDTO.setCountThu(settingSumDTO.getCountThu()+1);
            }
            if (!setting.getVacateDays().contains("5")) {
                settingSumDTO.setCountFri(settingSumDTO.getCountFri()+1);
            }
            if (!setting.getVacateDays().contains("6")) {
                settingSumDTO.setCountSat(settingSumDTO.getCountSat()+1);
            }
            if (!setting.getVacateDays().contains("7")) {
                settingSumDTO.setCountSun(settingSumDTO.getCountSun()+1);
            }
        }

        List<Long> jobIdList = new ArrayList<>(jobIds);
        Collections.sort(jobIdList);

        List<SchdVacateSettingSumDTO> resultList = new ArrayList<>();
        for (Long l : jobIdList) {
            SchdVacateSettingSumDTO settingSumDTO = settingSumDTOMap.get(l);
            if (settingSumDTO != null) {
                resultList.add(settingSumDTO);
            }
        }

        return resultList;
     }


    /***********************************************************************************************
     *    分隔线
     ***********************************************************************************************/

    /**
     * 保存计划配置
     * @param schdVacateAdjustVO
     */
    private boolean validateParameter(SchdVacateAdjustVO schdVacateAdjustVO) {
        if (schdVacateAdjustVO.getPlanType() != PLAN_TYPE_CLEAN
                && schdVacateAdjustVO.getPlanType() != PLAN_TYPE_SECURITY) {
            return false;
        }
        if (schdVacateAdjustVO.getAdjustType() < VacateAdjustTypeEnum.VACATE.getValue()
        || schdVacateAdjustVO.getAdjustType() > VacateAdjustTypeEnum.REPLACE.getValue()) {
            return false;
        }

        if (!DateTimeUtil.isValidDate(schdVacateAdjustVO.getAdjustDate(),"yyyy-MM-dd")) {
            return false;
        }
        return true;
    }

    /**
     * 添加
     * @param schdVacateAdjustVO
     * @return
     * @throws BusinessException
     */
    public boolean saveAdjust(SchdVacateAdjustVO schdVacateAdjustVO) throws BusinessException{

        if (!this.validateParameter(schdVacateAdjustVO)) {
            throw new BusinessException(ExceptionCode.BusinessError,"无效参数");
        }
        SchdVacateAdjust oldSchdVacateAdjust = schdVacateAdjustService.getByDateEmpId(schdVacateAdjustVO.getAdjustDate(),schdVacateAdjustVO.getEmpId());
        if (oldSchdVacateAdjust != null) {
            throw new BusinessException(ExceptionCode.BusinessError,"不允许重复添加");
        }

        SchdVacateAdjust newSchdVacateAdjust = new SchdVacateAdjust();
        BeanUtils.copyProperties(schdVacateAdjustVO,newSchdVacateAdjust);

        return schdVacateAdjustService.save(newSchdVacateAdjust);
    }

    /**
     * 更新
     * @param schdVacateAdjustVO
     * @return
     * @throws BusinessException
     */
    public boolean updateAdjust(SchdVacateAdjustVO schdVacateAdjustVO) throws BusinessException{

        if (!this.validateParameter(schdVacateAdjustVO)) {
            throw new BusinessException(ExceptionCode.BusinessError,"无效参数");
        }
        SchdVacateAdjust oldSchdVacateAdjust = schdVacateAdjustService.getById(schdVacateAdjustVO.getId());
        if (oldSchdVacateAdjust == null) {
            throw new BusinessException(ExceptionCode.BusinessError,"无效参数id");
        }

        SchdVacateAdjust othSchdVacateAdjust = schdVacateAdjustService.getByDateEmpId(schdVacateAdjustVO.getAdjustDate(),schdVacateAdjustVO.getEmpId());
        if (othSchdVacateAdjust != null && othSchdVacateAdjust.getId() != schdVacateAdjustVO.getId().longValue()) {
            throw new BusinessException(ExceptionCode.BusinessError,"不允许重复添加");
        }

        SchdVacateAdjust newSchdVacateAdjust = new SchdVacateAdjust();
        BeanUtils.copyProperties(schdVacateAdjustVO,newSchdVacateAdjust);

        return schdVacateAdjustService.updateById(newSchdVacateAdjust);
    }

    public boolean deleteAdjust(Long id) {
        SchdVacateAdjust oldSchdVacateAdjust = schdVacateAdjustService.getById(id);
        if (oldSchdVacateAdjust == null) {
            throw new BusinessException(ExceptionCode.BusinessError,"无效参数id");
        }

        SchdVacateAdjust newSchdVacateAdjust = new SchdVacateAdjust();
        newSchdVacateAdjust.setDeleted(DeleteFlagEnum.YES.getValue());
        newSchdVacateAdjust.setId(id);

        return schdVacateAdjustService.updateById(newSchdVacateAdjust);
    }


    /**
     * 分页查询休息计划调整列表
     * @param page
     * @param limit
     * @param departmentId - 部门id
     * @param planType - 计划类别 0 - 1
     * @param adjustDate - 调整日期
     * @param adjustType - 调整类别
     * @param name  - 姓名查询
     * @return
     */
    public ResultList<SchdVacateAdjustDTO> listPageAdjust(int page,
                                                          int limit,
                                                          Long departmentId,
                                                          Integer planType,
                                                          String adjustDate,
                                                          Integer adjustType,
                                                          String name) {

        IPage<SchdVacateAdjust> adjustIPage =  schdVacateAdjustService.listPage(page, limit, departmentId,planType,adjustDate,adjustType,null);
        if (adjustIPage.getRecords().size() == 0)  {
            return new ResultList.Builder().total(0).list(null).build();
        }
        Set<Long> empIds = new HashSet<>();
        adjustIPage.getRecords().stream().forEach(t -> {
            if (t.getEmpId() != 0) {
                empIds.add(t.getEmpId());
            }
            if (t.getRepEmpId() != 0){
                empIds.add(t.getRepEmpId());
            }
        });

        List<Employee> employeeList = employeeService.listByIds(empIds);
        Map<Long,Employee> employeeMap = employeeList.stream().collect(Collectors.toMap(Employee::getId, Function.identity(),(key1,key2)->key2));

        List<SchdVacateAdjustDTO> schdVacateAdjustDTOS = adjustIPage.getRecords().stream().map(schdVacateAdjust -> {
            SchdVacateAdjustDTO schdVacateAdjustDTO = new SchdVacateAdjustDTO();
            BeanUtils.copyProperties(schdVacateAdjust,schdVacateAdjustDTO);
            Employee employee = employeeMap.get(schdVacateAdjust.getEmpId());
            if (null != employee) {
                schdVacateAdjustDTO.setEmpName(employee.getEmpName());
            }
            Employee repEmployee = employeeMap.get(schdVacateAdjust.getRepEmpId());
            if (null != repEmployee) {
                schdVacateAdjustDTO.setRepEmpName(repEmployee.getEmpName());
            }
            return schdVacateAdjustDTO;
        }).collect(Collectors.toList());
        return ResultList.builder().total(adjustIPage.getTotal()).list(schdVacateAdjustDTOS).build();
    }

}
