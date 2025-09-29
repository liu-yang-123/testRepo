package com.zcxd.gun.service.server;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zcxd.gun.common.GunTaskEnum;
import com.zcxd.gun.common.GunTypeEnum;
import com.zcxd.common.constant.JobTypeEnum;
import com.zcxd.common.util.DateTimeUtil;
import com.zcxd.common.util.EasyExcelUtil;
import com.zcxd.common.util.Result;
import com.zcxd.gun.config.UserContextHolder;
import com.zcxd.db.model.*;
import com.zcxd.gun.db.model.Gun;
import com.zcxd.gun.db.model.GunLicence;
import com.zcxd.gun.db.model.GunTask;
import com.zcxd.gun.dto.GunConfigDTO;
import com.zcxd.gun.dto.GunDTO;
import com.zcxd.gun.dto.excel.ExportHead;
import com.zcxd.gun.dto.excel.GunTaskInfoRecordHead;
import com.zcxd.gun.exception.BusinessException;
import com.zcxd.gun.feign.RemoteProvider;
import com.zcxd.gun.service.*;
import com.zcxd.gun.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zccc
 */
@Slf4j
@Service
public class GunTaskOperateServer {
    @Autowired
    GunTaskService gunTaskService;
    @Autowired
    GunLicenceService gunLicenceService;
    @Autowired
    GunService gunService;
    @Autowired
    EmployeeService employeeService;
    @Autowired
    RemoteProvider remoteService;
    @Autowired
    GunCategoryService gunCategoryService;

    /**
     * 检查枪支任务属性是否合规，同时增添属性，减少后期数据库查询
     *
     * @param gunTaskVo 枪支任务属性
     * @return 检查结果
     */
    public Result checkGunTask(GunTaskVO gunTaskVo) {
        List<String> failedMsg = new ArrayList<>();

        String gunCodeA = gunTaskVo.getGunCodeA();
        String gunBoxCodeA = gunTaskVo.getGunBoxCodeA();
        String gunCodeB = gunTaskVo.getGunCodeB();
        String gunBoxCodeB = gunTaskVo.getGunBoxCodeB();
        String gunLicenceNumA = gunTaskVo.getGunLicenceNumA();
        String gunLicenceNumB = gunTaskVo.getGunLicenceNumB();
        String carNo = gunTaskVo.getCarNo();
        Integer departmentId = gunTaskVo.getDepartmentId();
        Long lineId = gunTaskVo.getLineId();
        Long supercargoIdA = gunTaskVo.getSupercargoIdA();
        Long supercargoIdB = gunTaskVo.getSupercargoIdB();

        GunVO gunVo = new GunVO();
        gunVo.setDepartmentId(gunTaskVo.getDepartmentId());

        Gun gunA = null;
        Gun gunB = null;
        if (!StringUtils.isBlank(gunCodeA)) {
            // 枪号A检查
            gunVo.setGunCode(gunCodeA);
            gunA = gunService.getGunByGunCode(gunVo);
            if (gunA == null) {
                failedMsg.add("枪号A：" + gunCodeA + "不存在");
            } else if (!gunCategoryService.isGun(gunA.getGunCategory())) {
                failedMsg.add("枪号A：" + gunCodeA + "不是枪支编号");
            }
        }

        if (!StringUtils.isBlank(gunBoxCodeA)) {
            // 弹盒A检查
            gunVo.setGunCode(gunBoxCodeA);
            Gun gun = gunService.getGunByGunCode(gunVo);
            if (gun == null) {
                failedMsg.add("弹盒号A：" + gunBoxCodeA + "不存在");
            } else if (!gunCategoryService.isAmmunition(gun.getGunCategory())) {
                failedMsg.add("弹盒号A：" + gunBoxCodeA + "不是弹盒编号");
            }
        }

        if (!StringUtils.isBlank(gunBoxCodeA) && !StringUtils.isBlank(gunBoxCodeB) && gunCodeA.equals(gunCodeB)) {
            failedMsg.add("枪支A、B重复");
        }

        if (!StringUtils.isBlank(gunLicenceNumA)) {
            // 枪证A检查
            GunLicenceVO gunLicenceVO = new GunLicenceVO(gunLicenceNumA, departmentId);
            List<GunLicence> licenceA = gunLicenceService.getGunLicenceByLicenceNum(gunLicenceVO);
            if (licenceA.isEmpty()) {
                failedMsg.add("枪证A号：" + gunLicenceNumA + "不存在");
            } else if (gunA != null && !licenceA.get(0).getId().equals(gunA.getGunLicenceId())) {
                failedMsg.add("枪A与枪证A不匹配");
            }
        }

        if (supercargoIdA != null && supercargoIdA > 0) {
            // 押运员A检查
            Employee employeeA = employeeService.getEmployeeById(supercargoIdA, departmentId);
            if (employeeA == null) {
                failedMsg.add("未查找到押运员A");
            } else {
                Integer jobTypeA = employeeA.getJobType();
                if (!JobTypeEnum.GUARD.getValue().equals(jobTypeA)) {
                    failedMsg.add("押运员A不是护卫岗");
                } else {
                    gunTaskVo.setSupercargoNameA(employeeA.getEmpName());
                }
            }
        }

        if (!StringUtils.isBlank(gunCodeB)) {
            // 枪号B检查
            gunVo.setGunCode(gunCodeB);
            gunB = gunService.getGunByGunCode(gunVo);
            if (gunB == null) {
                failedMsg.add("枪号B：" + gunCodeB + "不存在");
            } else if (!gunCategoryService.isGun(gunB.getGunCategory())) {
                failedMsg.add("枪号B：" + gunCodeB + "不是枪支编号");
            }
        }

        if (!StringUtils.isBlank(gunBoxCodeB)) {
            // 弹盒B检查
            gunVo.setGunCode(gunBoxCodeB);
            Gun gun = gunService.getGunByGunCode(gunVo);
            if (gun == null) {
                failedMsg.add("弹盒号B：" + gunBoxCodeB + "不存在");
            } else if (!gunCategoryService.isAmmunition(gun.getGunCategory())) {
                failedMsg.add("弹盒号B：" + gunBoxCodeB + "不是弹盒编号");
            }
        }

        if (!StringUtils.isBlank(gunBoxCodeA) && !StringUtils.isBlank(gunBoxCodeB) && gunBoxCodeA.equals(gunBoxCodeB)) {
            failedMsg.add("枪盒A、B重复");
        }

        if (!StringUtils.isBlank(gunLicenceNumB)) {
            // 枪证B检查
            GunLicenceVO gunLicenceVO = new GunLicenceVO(gunLicenceNumB, departmentId);
            List<GunLicence> licenceB = gunLicenceService.getGunLicenceByLicenceNum(gunLicenceVO);
            if (licenceB.isEmpty()) {
                failedMsg.add("枪证B号：" + gunLicenceNumB + "不存在");
            } else if (gunB != null && !licenceB.get(0).getId().equals(gunB.getGunLicenceId())) {
                failedMsg.add("枪B与枪证B不匹配");
            }
        }

        if (supercargoIdB != null && supercargoIdB > 0) {
            // 押运员A检查
            Employee employeeB = employeeService.getEmployeeById(supercargoIdB, departmentId);
            if (employeeB == null) {
                failedMsg.add("未查找到押运员B");
            } else {
                Integer jobTypeB = employeeB.getJobType();
                if (!JobTypeEnum.GUARD.getValue().equals(jobTypeB)) {
                    failedMsg.add("押运员B不是护卫岗");
                } else {
                    gunTaskVo.setSupercargoNameB(employeeB.getEmpName());
                }
            }
        }

        if (departmentId != null && departmentId >= 0) {
            Result<Boolean> authResult = remoteService.isAuthDepartment(departmentId);
            if (authResult.isFailed()) {
                return authResult;
            }
        }

        // 确认车辆是否存在
        if (!StringUtils.isBlank(carNo)) {
            VehicleVO vehicle = new VehicleVO();
            vehicle.setLpno(carNo);
            vehicle.setDepartmentId(gunTaskVo.getDepartmentId().longValue());
            Result<List<Vehicle>> vehicleResult = remoteService.findVehicleByCondition(vehicle);
            if (vehicleResult.isFailed()) {
                failedMsg.add(vehicleResult.getMsg());
            }
            else if (vehicleResult.getData() == null || vehicleResult.getData().isEmpty()) {
                failedMsg.add("车辆：" + carNo + "查询失败或不存在");
            }
        }

        // 添加路线信息
        if (lineId != null && lineId >= 0) {
            Result<Route> result = remoteService.findRouteById(lineId);
            if (result.isFailed()) {
                failedMsg.add(result.getMsg());
            } else if (result.isFailed()) {
                failedMsg.add("线路Id：" + lineId + "错误");
            } else {
                Route route = result.getData();
                gunTaskVo.setLineName(route.getRouteName());
            }
        }

        return failedMsg.isEmpty() ? Result.success() : Result.fail(failedMsg.toString());
    }

    /**
     * 补充枪弹任务部分属性
     *
     * @param gunTaskVo vo
     */
    private void completeAttr(GunTaskVO gunTaskVo) {
        // 数据库中已经设置了枪支发放状态默认值，不需要再设置
        gunTaskVo.setOperatorName(UserContextHolder.getUsername());
        gunTaskVo.setId(null);
        // TODO 先不需要设置线路审核员
    }

    /**
     * 新建枪支任务
     *
     * @param gunTaskVo 任务属性
     * @return 结果
     */
    public Result saveGunTask(GunTaskVO gunTaskVo) {
        Result result = checkGunTask(gunTaskVo);
        if (result.isFailed()) {
            return result;
        }
        completeAttr(gunTaskVo);
        return gunTaskService.saveGunTask(gunTaskVo);
    }

    /**
     * 修改枪支任务
     *
     * @param gunTaskVo 属性
     * @return 结果
     */
    public Result updateGunTask(GunTaskVO gunTaskVo) {
        Result result = checkGunTask(gunTaskVo);
        if (result.isFailed()) {
            return result;
        }
        return gunTaskService.updateGunTask(gunTaskVo);
    }

    /**
     * 按页查询
     *
     * @param page    页数
     * @param limit   单页数量
     * @param queryVo 查询条件
     * @return 结果
     */
    public Result findListByPage(Integer page, Integer limit, GunTaskQueryVO queryVo) {
        // 通过工号查询时，将查询到的工号转换成ID，用于直接在guntask表查询
        String supercargoNum = queryVo.getSupercargoNum();
        if (!StringUtils.isBlank(supercargoNum)) {
            Employee employee = new Employee();
            employee.setEmpNo(supercargoNum);
            List<Employee> employeeByCondition = employeeService.getEmployeeByCondition(employee);
            if (!employeeByCondition.isEmpty()) {
                Employee employeeFound = employeeByCondition.get(0);
                queryVo.setSupercargoId(employeeFound.getId());
            }
        }

        return Result.success(gunTaskService.findListByPage(page, limit, queryVo));
    }

    /**
     * 发枪
     *
     * @param queryVo 查询条件
     * @return 结果
     */
    public Result issueGun(GunTaskIssueAndTakedownQueryVO queryVo) {
        Result<Boolean> authResult = remoteService.isAuthDepartment(queryVo.getDepartmentId());
        if (authResult.isFailed()) {
            return authResult;
        }
        return gunTaskService.issueGun(queryVo);
    }

    /**
     * 收枪
     *
     * @param queryVo 查询条件
     * @return 结果
     */
    public Result takedownGun(GunTaskIssueAndTakedownQueryVO queryVo) {
        Result<Boolean> authResult = remoteService.isAuthDepartment(queryVo.getDepartmentId());
        if (authResult.isFailed()) {
            return authResult;
        }
        return gunTaskService.takedownGun(queryVo);
    }

    /**
     * 枪弹配置
     * @param gunConfigDTOS 枪支配置属性
     * @param time 任务时间
     * @return 配置结果
     * @throws IllegalAccessException
     */
    public Result gunConfig(List<GunConfigDTO> gunConfigDTOS, Long departmentId, Long time) throws IllegalAccessException {
        // 删除之前生成的记录
        QueryWrapper wrapper = Wrappers.query().eq("plan_time", time).eq("department_id", departmentId);
        gunTaskService.getBaseMapper().delete(wrapper);

        if (gunConfigDTOS.isEmpty()) {
            return Result.success("无需分配枪支");
        }
        // 提取护卫Id
        List<Long> securityAIds = gunConfigDTOS.stream().map(GunConfigDTO::getScurityA).filter(v -> v != 0L).collect(Collectors.toList());
        List<Long> securityBIds = gunConfigDTOS.stream().map(GunConfigDTO::getScurityB).filter(v -> v != 0L).collect(Collectors.toList());
        List<Long> employeeIds = new ArrayList<>();
        employeeIds.addAll(securityAIds);
        employeeIds.addAll(securityBIds);
        int employeeNum = employeeIds.size();

        // 护卫id与entity映射
        List<Employee> employees = employeeService.listByIds(employeeIds);
        Map<Long, Employee> employeeMap = employees.stream().collect(Collectors.toMap(Employee::getId, Employee -> Employee));

        // 选择枪支
        GunQueryVO queryVO = new GunQueryVO();
        queryVO.setDepartmentId(Math.toIntExact(gunConfigDTOS.get(0).getDepartmentId()));
        queryVO.setGunType(GunTypeEnum.GUN.getValue());
        Page<GunDTO> gunPage = gunService.findListByPage(0, employeeNum, queryVO).getData();
        List<GunDTO> guns = gunPage.getRecords();
        // 打乱顺序
        Collections.shuffle(guns);

        // 枪盒
        queryVO.setGunType(GunTypeEnum.AMMUNITION.getValue());
        Page<GunDTO> gunBoxPage = gunService.findListByPage(0, employeeNum, queryVO).getData();
        List<GunDTO> gunBoxes = gunBoxPage.getRecords();
        // 打乱顺序
        Collections.shuffle(gunBoxes);

        List<GunTaskVO> gunTaskVOS = gunConfigDTOS.stream().map(x -> {
            GunTaskVO gunTaskVO = new GunTaskVO();
            gunTaskVO.setLineName(x.getRouteNo());
            gunTaskVO.setSupercargoIdA(x.getScurityA());
            gunTaskVO.setSupercargoIdB(x.getScurityB());
            gunTaskVO.setPlanTime(time);
            gunTaskVO.setDepartmentId(Math.toIntExact(x.getDepartmentId()));
            gunTaskVO.setCarNo(x.getVehicleNo());
            gunTaskVO.setSchdId(x.getId());

            // 当枪支不够时能尽量使所有路线至少持一把枪
            if (x.getScurityA() != 0L) {
                gunTaskVO.setSupercargoNameA(employeeMap.get(x.getScurityA()).getEmpName());
                if (guns.size() > 0) {
                    GunDTO gunDTO = guns.remove(0);
                    gunTaskVO.setGunCodeA(gunDTO.getGunCode());
                    gunTaskVO.setGunLicenceNumA(gunDTO.getGunLicenceNum());
                }
                if (gunBoxes.size() > 0) {
                    GunDTO box = gunBoxes.remove(0);
                    gunTaskVO.setGunBoxCodeA(box.getGunCode());
                }
            }

            if (x.getScurityB() != 0L) {
                gunTaskVO.setSupercargoNameB(employeeMap.get(x.getScurityB()).getEmpName());
            } else {
                gunTaskVO.setSupercargoNameB("");
            }

            return gunTaskVO;
        }).collect(Collectors.toList());

        // 剩余的枪弹、枪盒分发到B手中
        gunTaskVOS.forEach(gunTaskVO -> {
            if (gunTaskVO.getSupercargoIdB() != 0) {
                if (guns.size() > 0) {
                    GunDTO gunDTO = guns.remove(0);
                    gunTaskVO.setGunCodeB(gunDTO.getGunCode());
                    gunTaskVO.setGunLicenceNumB(gunDTO.getGunLicenceNum());
                }
                if (gunBoxes.size() > 0) {
                    GunDTO box = gunBoxes.remove(0);
                    gunTaskVO.setGunBoxCodeB(box.getGunCode());
                }
            }
        });

        for (GunTaskVO taskVO: gunTaskVOS) {
            Result result = saveGunTask(taskVO);
            if (result.isFailed()) {
                return result;
            }
        }
        log.info("=====配枪完成=====");
        return Result.success();
    }

    /**
     * 发生线路交换时，变更任务
     * @param changeVOList 交换参数
     */
    public void changeTask(List<SchdResultChangeVO> changeVOList) {
        boolean changed = false;
        SchdResultChangeVO changeVO1 = changeVOList.get(0);
        SchdResultChangeVO changeVO2 = changeVOList.get(1);

        // 获取两个枪弹任务
        Long id1 = changeVO1.getId();
        GunTask gunTask1 = gunTaskService.findBySchdId(id1);
        Long id2 = changeVO2.getId();
        GunTask gunTask2 = gunTaskService.findBySchdId(id2);

        // 交换过护卫A
        if (!gunTask1.getSupercargoIdA().equals(changeVO1.getScurityA())) {
            String temp = gunTask1.getSupercargoNameA();
            gunTask1.setSupercargoNameA(gunTask2.getSupercargoNameA());
            gunTask2.setSupercargoNameA(temp);

            gunTask2.setSupercargoIdA(gunTask1.getSupercargoIdA());
            gunTask1.setSupercargoIdA(changeVO1.getScurityA());
            changed = true;
        }

        // 交换过护卫B
        if (!gunTask1.getSupercargoIdB().equals(changeVO1.getScurityB())) {
            String temp = gunTask1.getSupercargoNameB();
            gunTask1.setSupercargoNameB(gunTask2.getSupercargoNameB());
            gunTask2.setSupercargoNameB(temp);

            gunTask2.setSupercargoIdB(gunTask1.getSupercargoIdB());
            gunTask1.setSupercargoIdB(changeVO1.getScurityB());
            changed = true;
        }

        if (changed) {
            gunTaskService.updateById(gunTask1);
            gunTaskService.updateById(gunTask2);
        }
    }

    private Map<Long, String> getEmployeeMap() {
        Map<Long, String> employeeMap;
        List<Employee> empList = employeeService.list();
        employeeMap = empList.stream().collect(Collectors.toMap(Employee::getId, Employee::getEmpName));
        return employeeMap;
    }

    /**
     * 导出
     * @param response response
     * @param taskQueryVO 任务查询条件
     */
    public void exportGunInfo(HttpServletResponse response, GunTaskQueryVO taskQueryVO) {
        List<GunTask> taskList = gunTaskService.findList(taskQueryVO);
        Map<Long, String> employeeMap = getEmployeeMap();

        List<Long> schdIds = taskList.isEmpty() ? new ArrayList<>()
                : taskList.stream().map(GunTask::getSchdId).collect(Collectors.toList());
        Result<List<SchdResult>> schdResultByIds = remoteService.findSchdResultByIds(schdIds,
                taskQueryVO.getDepartmentId().longValue());
        List<GunTaskInfoRecordHead> gunTaskExprotRecords = new ArrayList<>();

        if (!schdResultByIds.isFailed()) {
            List<SchdResult> schdResults = schdResultByIds.getData();
            Map<Long, SchdResult> schdResultMap = schdResults.stream().collect(Collectors.toMap(SchdResult::getId, r -> r));

            for (int i = 0; i < taskList.size(); i++) {
                GunTask gunTask = taskList.get(i);

                SchdResult schdResult = schdResultMap.get(gunTask.getSchdId());
                if (schdResult == null) {
                    continue;
                }

                Integer departmentId = gunTask.getDepartmentId();
                String date = DateTimeUtil.timeStampMs2Date(schdResult.getPlanDay(), null);
                String keyMan = Optional.ofNullable(employeeMap.get(schdResult.getKeyMan())).orElse("");
                String driver = Optional.ofNullable(employeeMap.get(schdResult.getDriver())).orElse("");
                String opMan = Optional.ofNullable(employeeMap.get(schdResult.getOpMan())).orElse("");

                // TODO 要改
                Employee employeeA = employeeService.getEmployeeById(gunTask.getSupercargoIdA(), departmentId);
                Employee employeeB = employeeService.getEmployeeById(gunTask.getSupercargoIdB(), departmentId);

                GunTaskInfoRecordHead gunTaskInfoRecordHead = new GunTaskInfoRecordHead();

                BeanUtils.copyProperties(gunTask, gunTaskInfoRecordHead);

                gunTaskInfoRecordHead.setIndex((long) i + 1);
                gunTaskInfoRecordHead.setSupercargoNumA(employeeA == null ? "" : employeeA.getEmpNo());
                gunTaskInfoRecordHead.setSupercargoNumB(employeeB == null ? "" : employeeB.getEmpNo());
                gunTaskInfoRecordHead.setPlanTime(date);
                gunTaskInfoRecordHead.setDirver(driver);
                gunTaskInfoRecordHead.setKeyManName(keyMan);
                gunTaskInfoRecordHead.setOperatorNameName(opMan);
                gunTaskInfoRecordHead.setTaskStartTime(DateTimeUtil.timeStampMs2Date(gunTask.getTakeOutTime(), null));
                gunTaskInfoRecordHead.setTaskEndTime(DateTimeUtil.timeStampMs2Date(gunTask.getTakeInTime(), null));
                gunTaskInfoRecordHead.setTaskStatus(GunTaskEnum.getTextByValue(gunTask.getTaskStatus()));
                gunTaskInfoRecordHead.setDuration(calculateTimeDifference(gunTask.getTakeOutTime(), gunTask.getTakeInTime()));

                gunTaskExprotRecords.add(gunTaskInfoRecordHead);
            }
        }

        String title = "枪支任务信息";
        try {
            ExportHead.setExcelPropertyTitle(GunTaskInfoRecordHead.class, title);
            EasyExcelUtil.exportPrintExcel(response, title, GunTaskInfoRecordHead.class, gunTaskExprotRecords, null);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(-1, "导出枪支任务信息出错");
        }
    }
    private String calculateTimeDifference(long startTimeMillis, long endTimeMillis) {
        if (startTimeMillis > endTimeMillis) {
            return "";
        }
        // 计算时间差（毫秒）
        long timeDifferenceMillis = endTimeMillis - startTimeMillis;

        // 转换毫秒为秒
        long timeDifferenceSeconds = timeDifferenceMillis / 1000;

        // 转换秒为小时和分钟
        long hours = timeDifferenceSeconds / 3600;
        long minutes = (timeDifferenceSeconds % 3600) / 60;

        // 格式化输出
        return String.format("%02d小时%02d分钟", hours, minutes);
    }
}
