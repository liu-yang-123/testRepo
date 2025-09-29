package com.zcxd.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.base.config.UserContextHolder;
import com.zcxd.base.dto.EmployeeResultDTO;
import com.zcxd.base.dto.SchdResultValueDTO;
import com.zcxd.common.util.KeyValue;
import com.zcxd.common.util.ToolUtil;
import com.zcxd.db.mapper.*;
import com.zcxd.db.model.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author songanwei
 * @date 2021-07-24
 */
@Slf4j
@AllArgsConstructor
@Service
public class SchdResultServiceImpl extends ServiceImpl<SchdResultMapper, SchdResult> {

    /**
     * 特殊线路
     */
    private static final Integer ESP_ROUTE_TYPE = 1;

    private DepartmentMapper departmentMapper;

    private EmployeeMapper employeeMapper;

    private VehicleMapper vehicleMapper;

    private RouteTemplateMapper templateMapper;

    private SchdDriveRestrictMapper driveRestrictMapper;

    private SchdDriveRestrictXhMapper schdDriveRestrictXhMapper;

    private SchdHolidayPlanMapper planMapper;

    private SchdVacatePlanMapper vacatePlanMapper;

    private SchdVacateSettingMapper vacateSettingMapper;

    private SchdVacateAdjustMapper adjustMapper;

    private SchdAlternateAssignMapper assignMapper;

    private RouteTemplateService routeTemplateService;

    private SchdResultRecordMapper resultRecordMapper;

    private SchdCleanPassAuthMapper authMapper;

    private BankMapper bankMapper;

//        @PostConstruct
    public void init(){
        // 获取需要生成排班的日期时间戳
        long time = LocalDateTime.of(LocalDate.now().plusDays(1L), LocalTime.MIN).toInstant(ZoneOffset.of("+8")).toEpochMilli();
        //初始化部门数据
        QueryWrapper wrapper = Wrappers.query().eq("parent_ids","/0/").eq("deleted",0);
        List<Department> departmentList = departmentMapper.selectList(wrapper);
        if (departmentList == null || departmentList.size() == 0){
            return;
        }
        //根据部门ID进行线路生成
        departmentList.stream().forEach(r -> {
            Long departmentId = r.getId();
            //如果当天已经进行了排班，则不需要重排
            SchdResultRecord record = this.getResultRecord(departmentId,time);
            if (record == null){
                boolean result = this.makeResult(departmentId,time, 0);
            }
        });
    }

    /**
     * 查询当天【排班的创建记录
     * @param departmentId 部门ID
     * @param time  查询时间
     * @return
     */
    private SchdResultRecord getResultRecord(Long departmentId, Long time){
        QueryWrapper wrapper = Wrappers.query().eq("department_id",departmentId)
                .eq("plan_day",time).eq("op_type",0);
        wrapper.last("LIMIT 1");
        return  resultRecordMapper.selectOne(wrapper);
    }


    /**
     * 删除数据库已排班结果
     * @param time 需要生成排班的具体时间
     * @return
     */
    public boolean makeResult(Long departmentId,Long time, Integer makeType){
        // 1=司机岗  2=护卫岗  3=钥匙岗  4=密码岗
        Collection<Integer> jobType = Stream.of(1,2,3,4).collect(Collectors.toSet());
        //查询下属部门 通过前缀匹配 /0/departmentId/%
        StringBuilder stringBuilder = new StringBuilder().append("/0/").append(departmentId).append("/%");
        //通过当前部门获取下属部门的列表数据
        QueryWrapper departmentWrapper = Wrappers.query().like("parent_ids",stringBuilder.toString()).eq("deleted",0);
        List<Department> departmentList = departmentMapper.selectList(departmentWrapper);
        Set<Long> subDepartmentId = departmentList.stream().map(Department::getId).collect(Collectors.toSet());
        subDepartmentId.add(departmentId);

        //查询加钞岗位的所有在职员工
        QueryWrapper jobQueryWrapper = Wrappers.query().eq("status_t",0).in("department_id",subDepartmentId)
                .eq("deleted",0).in("job_type",jobType);
        List<Employee> employeeListT = employeeMapper.selectList(jobQueryWrapper);

        //员工departmentId转化为顶级部门ID
        List<Employee> employeeList = employeeListT.stream().map(r -> {
            Employee employee = new Employee();
            BeanUtils.copyProperties(r, employee);
            employee.setDepartmentId(departmentId);
            return employee;
        }).collect(Collectors.toList());
        //格式化数据对象
        List<EmployeeResultDTO> employeeResultDTOList = employeeList.stream().map(s -> {
            EmployeeResultDTO resultDTO = new EmployeeResultDTO();
            resultDTO.setDepartmentId(s.getDepartmentId());
            resultDTO.setEmployeeId(s.getId());
            resultDTO.setType(s.getJobType());
            resultDTO.setRouteLeader(s.getRouteLeader());
            return resultDTO;
        }).collect(Collectors.toList());
        log.info("所有员工数量: ---->" + employeeResultDTOList.size());
        log.info("司机员工数量:----->"+ employeeResultDTOList.stream().filter(t -> t.getType() == 1).count());
        log.info("护卫员工数量:----->" + employeeResultDTOList.stream().filter(t -> t.getType() == 2).count());
        log.info("钥匙员工数量:----->" + employeeResultDTOList.stream().filter(t -> t.getType() == 3).count());
        log.info("密码员工数量:----->" + employeeResultDTOList.stream().filter(t -> t.getType() == 4).count());

        //过滤员工数据
        List<EmployeeResultDTO> employeeLinkedList = filterEmployeeRule(employeeResultDTOList,time,departmentId,employeeList);

        if (employeeLinkedList == null || employeeLinkedList.size() == 0){
           // throw new BusinessException(1,"无可用员工数据，无法排班");
            return false;
        }

        //获取组合线路
        List<SchdResultValueDTO> resultList = getComposeRoute(time,employeeLinkedList,departmentId);
        //执行排班结果
        return saveRouteResult(departmentId,time,resultList, makeType);
    }

    /**
     * 执行重排班操作数据
     * @param departmentId 部门ID
     * @param time 排班时间
     * @param resultList 排班结果列表数据
     * @param makeType 操作类型
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean saveRouteResult(Long departmentId, Long time, List<SchdResultValueDTO> resultList,Integer makeType){
        if (resultList.size() == 0){
            return false;
        }
        List<SchdResult> targetList = resultList.stream().map(r -> {
            SchdResult schdResult = new SchdResult();
            BeanUtils.copyProperties(r, schdResult);
            schdResult.setPlanDay(time);
            return schdResult;
        }).collect(Collectors.toList());
        // 删除数据库表数据
        QueryWrapper wrapper = Wrappers.query().eq("plan_day", time);
        baseMapper.delete(wrapper);
        // 添加数据
        Integer result = baseMapper.insertAll(targetList);
        //插入/更新排班结果记录表
        SchdResultRecord record = new SchdResultRecord();
        record.setPlanDay(time);
        record.setDepartmentId(departmentId);
        record.setCreateTime(System.currentTimeMillis());
        record.setCategory(makeType);
        record.setContent("执行预排班");
        record.setOpType(0);
        if (makeType > 0){
            long userId = UserContextHolder.getUserId();
            record.setCreateUser(userId);
        }
        resultRecordMapper.insert(record);

        return  result != null && result >= 1;
    }

    /**
     * 通用人员休假处理，不包括特定银行、线路指定人员
     * @param employeeList  员工列表
     * @param time 过滤时间
     * @param departmentId 部门ID
     * @return
     */
    private List<EmployeeResultDTO> filterEmployeeRule(List<EmployeeResultDTO> resultDTOList, long time, long departmentId,List<Employee> employeeList){
        //获取当前星期几
        log.info("====区域所有岗位员工数量====="+ employeeList.size());
        LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(time/1000, 0, ZoneOffset.of("+8"));
        int weekday = localDateTime.getDayOfWeek().getValue();
        String weekdayStr = String.valueOf(weekday);

        log.info("当前排班星期几:------->"+weekday);
        //根据区域查询休假表
        QueryWrapper wrapper = Wrappers.query().ge("end_date",time)
                .le("begin_date",time)
                .eq("department_id",departmentId)
                .eq("deleted",0);
        List<SchdVacatePlan> vacatePlanList = vacatePlanMapper.selectList(wrapper);
        if (vacatePlanList == null){
            return null;
        }
        //查询休假计划表
        List<Long> planIds = vacatePlanList.stream().map(SchdVacatePlan::getId).collect(Collectors.toList());
        if (planIds.size() == 0){
            return null;
        }
        QueryWrapper settingWrapper = Wrappers.query().in("plan_id", planIds).eq("deleted",0);
        List<SchdVacateSetting> settingList = vacateSettingMapper.selectList(settingWrapper);
        Map<Long,String> settingMap = settingList.stream().collect(Collectors.toMap(SchdVacateSetting::getEmpId,SchdVacateSetting::getVacateDays));
        log.info("===计划数据===="+settingMap);
        //员工数据过滤
        resultDTOList = resultDTOList.stream().filter(r -> {
            String vacateDays = settingMap.get(r.getEmployeeId());
            //为null说明当前员工未进行休息计划设置
            if (vacateDays == null){
                return false;
            }
            if (vacateDays != null && (vacateDays.indexOf(weekdayStr) > -1)){
                return false;
            }
            return true;
        }).collect(Collectors.toList());
        log.info("过滤【做5休2】休假计划后的员工数据：------>"+resultDTOList);
        //当天休假、加班、替班相关人员进行过滤 0=休假 1=加班 2=替班
        String adjustDate =  localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        QueryWrapper adjustWrapper = Wrappers.query().eq("adjust_date",adjustDate)
                .eq("department_id",departmentId).eq("deleted",0);
        List<SchdVacateAdjust> adjustList = adjustMapper.selectList(adjustWrapper);
        if (adjustList == null){
            return resultDTOList;
        }
        //获取休假员工列表
        List<SchdVacateAdjust> adjustList1 = adjustList.stream().filter(r -> r.getAdjustType() == 0).collect(Collectors.toList());
        //获取加班、替班员工列表
        List<Long> adjustEmpIdList = adjustList.stream().filter(r -> r.getAdjustType() != 0)
                .map(SchdVacateAdjust::getEmpId).collect(Collectors.toList());
        // 过滤休假员工
        resultDTOList = resultDTOList.stream().filter(s -> {
                    Optional<SchdVacateAdjust> adjustOptional = adjustList1.stream()
                            .filter(m -> m.getEmpId().equals(s.getEmployeeId())).findFirst();
                    return !adjustOptional.isPresent();
                }
        ).collect(Collectors.toList());
        log.info("过滤休假后的员工数据:------->"+resultDTOList);
        //增加加班、替班员工
        resultDTOList = resultDTOList.stream().map(t -> {
            EmployeeResultDTO resultDTO = new EmployeeResultDTO();
            BeanUtils.copyProperties(t,resultDTO);
            if (adjustEmpIdList.contains(t.getEmployeeId())){
                resultDTO.setExist(true);
            }
            return resultDTO;
        }).collect(Collectors.toList());
        log.info("===加班、替班人员Id====="+adjustEmpIdList);
        // 如果不在resultDTOList列表中，需要从 employeeList取出
        List<EmployeeResultDTO> finalResultDTOList = resultDTOList;
        //如果empId在finalResultDTOList则不需要增加，如果不在，则需要查询数据追加
        List<EmployeeResultDTO> newResultDTOList = adjustEmpIdList.stream().filter(k -> {
            Optional<EmployeeResultDTO> dtoOptional = finalResultDTOList.stream().filter(n -> n.getEmployeeId().equals(k)).findFirst();
            return !dtoOptional.isPresent();
        }).map(r -> {
            Optional<Employee> employeeOptional = employeeList.stream().filter(p -> p.getId().equals(r)).findFirst();
            EmployeeResultDTO resultDTO = new EmployeeResultDTO();
            resultDTO.setDepartmentId(employeeOptional.get().getDepartmentId());
            resultDTO.setType(employeeOptional.get().getJobType());
            resultDTO.setEmployeeId(employeeOptional.get().getId());
            resultDTO.setRouteLeader(employeeOptional.get().getRouteLeader());
            resultDTO.setExist(true);
            return resultDTO;
        }).collect(Collectors.toList());

        log.info("加班、替班员工数据========>"+newResultDTOList);
        if (newResultDTOList != null && newResultDTOList.size() > 0){
            resultDTOList.addAll(newResultDTOList);
        }
        log.info("==加班后员工数据==="+resultDTOList);

        return resultDTOList;
    }

    /**
     * 获取排班数据结果
     * @param time  排班时间
     * @param employeeList 可用员工列表
     * @param departmentId 部门ID
     * @return
     */
    private List<SchdResultValueDTO> getComposeRoute(Long time, List<EmployeeResultDTO> employeeList, Long departmentId){
        //根据区域查询休假表
        QueryWrapper wrapper = Wrappers.query().ge("end_date",time).le("begin_date",time)
                .eq("department_id",departmentId)
                .eq("deleted",0);
        List<SchdVacatePlan> vacatePlanList = vacatePlanMapper.selectList(wrapper);
        //清机类别（司机、密码、钥匙岗位人员）季度计划ID  planType = 0
        Optional<SchdVacatePlan> clearOptional = vacatePlanList.stream().filter(s -> s.getPlanType() == 0).findFirst();
        Long clearPlanId = clearOptional.isPresent() ? clearOptional.get().getId() : 0L;
        //护卫类别季度计划ID   planType = 1
        Optional<SchdVacatePlan> securityOptional = vacatePlanList.stream().filter(s -> s.getPlanType() == 1).findFirst();
        Long securityPlanId = securityOptional.isPresent() ? securityOptional.get().getId() : 0L;

        //查询车辆数据,过滤维修状态
        QueryWrapper vehicleQueryWrapper = Wrappers.query().eq("department_id",departmentId).eq("status_t",0).eq("deleted",0);
        List<Vehicle> vehicleList = vehicleMapper.selectList(vehicleQueryWrapper);
        //查询模板线路并对特殊线路进行过滤
        QueryWrapper templateWrapper = Wrappers.query().eq("department_id",departmentId).eq("deleted",0);
        List<RouteTemplate> routeTemplateList = templateMapper.selectList(templateWrapper);
        //60号线、61号线特殊处理【当天不生成线路】
        List<RouteTemplate> excludeRouteList = routeTemplateService.getExcludeRoute(departmentId,time);
        Set<Long> excludeRouteIds = excludeRouteList.stream().map(RouteTemplate::getId).collect(Collectors.toSet());
        //过滤特殊线路即当天不生成线路，主要包括【60，61号线】，线路转化为可标记特殊处理的输出对象并进行初始化数据
        List<SchdResultValueDTO> valueDTOList = routeTemplateList.stream().filter(s -> !excludeRouteIds.contains(s.getId())).map(r -> {
            SchdResultValueDTO valueDTO = new SchdResultValueDTO();
            valueDTO.setRouteId(r.getId());
            valueDTO.setRouteNo(r.getRouteNo());
            valueDTO.setRouteType(r.getRouteType());
            valueDTO.setDepartmentId(r.getDepartmentId());
            valueDTO.setVehicleNo("");
            valueDTO.setDriver(0L);
            valueDTO.setScurityA(0L);
            valueDTO.setScurityB(0L);
            valueDTO.setKeyMan(0L);
            valueDTO.setOpMan(0L);
            valueDTO.setLeader(0L);
            return valueDTO;
        }).collect(Collectors.toList());

        //车选取
        formatVehicle(valueDTOList,vehicleList,time, departmentId, clearPlanId);
        //司机选取
        formatDriver(valueDTOList,employeeList,clearPlanId);

        //分离出特殊线路：特殊线路只排车、排司机
        List<SchdResultValueDTO> espValueDTOList = valueDTOList.stream()
                .filter(s -> s.getRouteType().equals(ESP_ROUTE_TYPE)).collect(Collectors.toList());

        List<SchdResultValueDTO> normalValueDTOList = valueDTOList.stream()
                .filter(s -> !s.getRouteType().equals(ESP_ROUTE_TYPE)).collect(Collectors.toList());

        // 护卫选取
        formatSecurity(normalValueDTOList,employeeList, securityPlanId);
        //清机员、密码员选取
        formatCleaner(normalValueDTOList,employeeList, departmentId);
        //合并正常排线和特殊线路
        normalValueDTOList.addAll(espValueDTOList);

        //替班员工替换
        formatReplace(normalValueDTOList,time);

        return normalValueDTOList;
    }

    /**
     * 判断是否为放假日
     * @param time
     * @return
     */
    private boolean isHoliday(long time){
        LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(time/1000, 0, ZoneOffset.of("+8"));
        int weekday = localDateTime.getDayOfWeek().getValue();

        //当天是否执行休假规则,如果国家规则放假，则根据调休日规则进行执行 0 = 放假 1=调休
        //如果当天执行放假操作，则可定是放假
        QueryWrapper planWrapper = Wrappers.query().eq("plan_day",time).eq("holiday_type",0).eq("deleted",0);
        planWrapper.last("LIMIT 1");
        SchdHolidayPlan holidayPlan = planMapper.selectOne(planWrapper);
        if (holidayPlan != null){
            return true;
        }
//        // 如果当天执行调休，则肯定不是放假   不受调班的影响
//        QueryWrapper planWrapper2 = Wrappers.query().eq("plan_day",time).eq("holiday_type",1).eq("deleted",0);
//        planWrapper.last("LIMIT 1");
//        SchdHolidayPlan holidayPlan2 = planMapper.selectOne(planWrapper2);
//        if (holidayPlan2 != null){
//            return false;
//        }
        // 若果是周末，按放假处理
        if (weekday == 6 || weekday == 7){
            return true;
        }
        return false;
    }

    /**
     * 西湖限行规则，获取尾号容许的线路列表数据
     * @param departmentId 区域部门
     * @param time 限制时间
     * @return
     */
    private SchdDriveRestrictXh getXiRestrictList(long departmentId, long time){
        //获取排班日期,并计算单双日
        LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(time/1000, 0, ZoneOffset.of("+8"));
        int value = localDateTime.getDayOfMonth() %2;
        //查询当天限行的路线及限行规则
        QueryWrapper xhWrapper = Wrappers.query().eq("day_type",value)
                .eq("department_id",departmentId).eq("deleted",0).last("LIMIT 1");
        SchdDriveRestrictXh restrictXh = schdDriveRestrictXhMapper.selectOne(xhWrapper);
        return restrictXh;
    }

    /**
     * 线路排车
     * @param targetList 目标数据
     * @param vehicleList
     * @param time
     * @param clearPlanId  清机季度计划ID
     */
    private void formatVehicle(List<SchdResultValueDTO> targetList,List<Vehicle> vehicleList, long time, Long departmentId, Long clearPlanId){
        // 查询主班线路 是否有可用车辆
        boolean isHoliday = isHoliday(time);

        //计算主替班线路车辆
        QueryWrapper wrapper = Wrappers.query().in("plan_id",clearPlanId)
                .eq("plan_type",0).eq("deleted",0);
        List<SchdAlternateAssign> allAssignList  = assignMapper.selectList(wrapper);

        /////////////1.主班司机进行线路排班/////////////////////
        List<SchdAlternateAssign> assignList = allAssignList.stream().filter(s -> s.getAlternateType() == 0).collect(Collectors.toList());
        ///替班数据
        List<SchdAlternateAssign> assignListT = allAssignList.stream().filter(s -> s.getAlternateType() == 1).collect(Collectors.toList());
        // 限行路线优先满足排班 {[线路1:2,4]，[线路2:2,4]}
        List<KeyValue<String,String>> xhRouteNumberList = new LinkedList<>();
        SchdDriveRestrictXh restrictXh = getXiRestrictList(departmentId,time);
        if (restrictXh != null){
            String[] routes = restrictXh.getEffectRoutes().split(",");
            for (int k =0; k < routes.length; k++){
                KeyValue<String,String> keyValue = new KeyValue<>();
                keyValue.setKey(routes[k]);
                keyValue.setValue(restrictXh.getPermitNumbers());
                xhRouteNumberList.add(keyValue);
            }
        }

        Set<String> xhRouteIds = xhRouteNumberList.stream().map(KeyValue::getKey).collect(Collectors.toSet());
        //获取所有西湖规则的线路
        log.info("当天排班是否为节假日:------>"+isHoliday);
        log.info("西湖线路ID:----->"+xhRouteIds);
        // 派车受约束
        if (isHoliday){
            //如果节假日，只有西湖线路受影响，其他线路不受限行规则影响
            //主班车辆进行排班
            targetList.stream().forEach(t -> {
                String routeId = String.valueOf(t.getRouteId());
                //西湖线路
                if (xhRouteIds.contains(routeId) && "".equals(t.getVehicleNo())){
                    Optional<SchdAlternateAssign> assign = assignList.stream().filter(m -> m.getRouteIds().equals(routeId)).findFirst();
                    if(assign.isPresent()){
                        String vehicleNo = assign.get().getVehicleNos();
                        //获取当前车尾号
                        String number = ToolUtil.getLastNumber(vehicleNo);
                        //如果车尾号存在，则更新线路
                        Optional<KeyValue<String,String>> valueOptional = xhRouteNumberList.stream().filter(n -> n.getKey().equals(routeId)).findFirst();
                        if (valueOptional.isPresent()){
                            //验证number是否在其中  2,4
                            if (valueOptional.get().getValue().contains(number)){
                                Optional<Vehicle> resultDTO = vehicleList.stream().filter(n -> n.getLpno().equals(vehicleNo)).findAny();
                                if (resultDTO.isPresent()) {
                                    t.setVehicleNo(vehicleNo);
                                    vehicleList.remove(resultDTO.get());
                                }
                            }
                        }
                    }
                }
                //非西湖线路主班
                if (!xhRouteIds.contains(routeId) && "".equals(t.getVehicleNo())){
                    Optional<SchdAlternateAssign> assign = assignList.stream().filter(m -> m.getRouteIds().equals(routeId)).findFirst();
                    if(assign.isPresent()){
                        String vehicleNo = assign.get().getVehicleNos();
                        Optional<Vehicle> resultDTO = vehicleList.stream().filter(n -> n.getLpno().equals(vehicleNo)).findAny();
                        if (resultDTO.isPresent()) {
                            t.setVehicleNo(vehicleNo);
                            vehicleList.remove(resultDTO.get());
                        }

                    }
                }
            });
           //替班车辆排班 提班车是否能全排
            //未排车数量
           int routeSize = (int) targetList.stream().filter(u -> "".equals(u.getVehicleNo())).count();
           
           //线路替班车补充处理
           if(routeSize == 0){
        	   return;
           }
           //提班车列表
            List<KeyValue<String,String>> keyValueList = new LinkedList<>();
            assignListT.stream().forEach(s -> {
                String[] routes = s.getRouteIds().split(",");
                for (int k =0; k < routes.length; k++){
                    KeyValue<String,String> keyValue = new KeyValue<>();
                    keyValue.setKey(routes[k]);
                    keyValue.setValue(s.getVehicleNos());
                    keyValueList.add(keyValue);
                }
            });
            //获取替班 线路车辆列表集合
            List<KeyValue<String,String>> tbRouteVehicleList = keyValueList.stream().filter(o -> {
                Optional<Vehicle> vehicleOptional = vehicleList.stream().filter(k -> k.getLpno().equals(o.getValue())).findFirst();
                return vehicleOptional.isPresent();
            }).collect(Collectors.toList());
            //获取替班车辆列表，从替班中选出size个车，在进行验证是否满足替班要求
            List<String> tbVehicleList = tbRouteVehicleList.stream().map(KeyValue::getValue).collect(Collectors.toList());
            int tbVehicleSize = tbVehicleList.size();
            if (tbVehicleSize > routeSize){
                List<List<String>> combineList = new LinkedList<>();
                int[] b = new int[routeSize];
                getCombine(tbVehicleSize,routeSize, routeSize,b,tbVehicleList,combineList);
                //验证是否满足条件
                combineList.stream().forEach(s -> {
                    //获取排序结果，
                    List<List<String>> seqList = new LinkedList<>();
                    getSequence(s.toArray(), 0, seqList);
                    log.info("======排班替班车辆排列顺序======" + seqList);
                    Optional<List<String>> targetDriverList = seqList.stream().filter(r -> {
                        //验证当前序列数据是否在线路中
                         return checkVehicle(r, targetList, xhRouteIds, tbRouteVehicleList, xhRouteNumberList);
                    }).findFirst();
                    if (targetDriverList.isPresent()){
                        int numberIndex = 0;
                        //替班司机设置
                        targetList.stream().forEach(m -> {
                            String routeId = String.valueOf(m.getRouteId());
                            String vehicleNo = targetDriverList.get().get(numberIndex);
                            if("".equals(m.getVehicleNo())){
                                //从employeeList列表中移除
                                Optional<Vehicle> resultDTO = vehicleList.stream().filter(o -> o.getLpno().equals(vehicleNo)).findFirst();
                                if (resultDTO.isPresent()){
                                    vehicleList.remove(resultDTO.get());
                                }
                                m.setVehicleNo(vehicleNo);
                            }
                        });
                    }
                });
            }
            //替班按顺序处理
            // tbRouteVehicleList 数据处理过滤已选取的车辆数据
            List<KeyValue<String,String>> restList = tbRouteVehicleList.stream().filter(o -> {
                Optional<SchdResultValueDTO> valueDTO = targetList.stream().filter(j -> j.getVehicleNo().equals(o.getValue())).findFirst();
                return !valueDTO.isPresent();
            }).collect(Collectors.toList());
            targetList.stream().forEach(h -> {
              if ("".equals(h.getVehicleNo())){
                  String routeId = String.valueOf(h.getRouteId());
                  //西湖线路替班
                  if (xhRouteIds.contains(routeId)){
                      //西湖
                      Optional<KeyValue<String,String>> keyValueOptional = restList.stream().filter(u -> u.getKey().equals(routeId)).findAny();
                      if (keyValueOptional.isPresent()){
                          KeyValue<String,String> keyValue = keyValueOptional.get();
                          String carNo = keyValue.getValue();
                          //需要验证尾号，并且验证车辆是否在该线路中
                          String number = ToolUtil.getLastNumber(carNo);
                          Optional<KeyValue<String,String>> valueOptionalT = xhRouteNumberList.stream().filter(m -> m.getKey().equals(routeId)).findFirst();
                          //如果尾号满足要求，则更新数据
                          if (valueOptionalT.get().getValue().contains(number)){
                              restList.remove(keyValue);
                              h.setVehicleNo(carNo);
                              Optional<Vehicle> vehicleOptional = vehicleList.stream().filter(o -> o.getLpno().equals(carNo)).findAny();
                              if (vehicleOptional.isPresent()){
                                  vehicleList.remove(vehicleOptional.get());
                              }
                          }
                      }
                  }
                  //非西湖线路替班
                  if(!xhRouteIds.contains(routeId)){
                     Optional<KeyValue<String,String>> keyValueOptional = restList.stream().filter(u -> u.getKey().equals(routeId)).findAny();
                     if (keyValueOptional.isPresent()){
                         KeyValue<String,String> keyValue = keyValueOptional.get();
                         String carNo = keyValue.getValue();
                         h.setVehicleNo(carNo);
                         restList.remove(keyValue);
                         Optional<Vehicle> vehicleOptional = vehicleList.stream().filter(o -> o.getLpno().equals(carNo)).findAny();
                         if (vehicleOptional.isPresent()){
                             vehicleList.remove(vehicleOptional.get());
                         }
                     }
                  }
              }
            });
            log.info("西湖线路主替班车辆排班结果：---->"+targetList);
            //其他线路随机排车
            targetList.stream().forEach(h -> {
                String routeId = String.valueOf(h.getRouteId());
                if ("".equals(h.getVehicleNo()) && xhRouteIds.contains(routeId)){
                    Optional<KeyValue<String,String>> valueOptionalT = xhRouteNumberList.stream().filter(m -> m.getKey().equals(routeId)).findFirst();
                    if (valueOptionalT.isPresent()){
                        String permitNumber = valueOptionalT.get().getValue();
                        Vehicle vehicle = filterVehicleHolidayRule(vehicleList,permitNumber);
                        h.setVehicleNo(vehicle.getLpno());
                    }

                }
                if ("".equals(h.getVehicleNo()) && !xhRouteIds.contains(routeId)){
                    Optional<Vehicle> vehicleOptional = vehicleList.stream().findAny();
                    if (vehicleOptional.isPresent()){
                        Vehicle vehicle = vehicleOptional.get();
                        h.setVehicleNo(vehicle.getLpno());
                        vehicleList.remove(vehicle);
                    }
                }
            });
        } else{
            //如果工作日，其他线路受影响，西湖线路不受任何限行规则影响
            //非西湖线路可用的车辆
            List<Vehicle> vehicleLinkedList = filterVehicleRule(vehicleList,time,departmentId);

            log.info("===线路主班车辆数据==="+assignList);
            //非西湖线路根据查找主班车、替班车
            targetList.stream().forEach(s -> {
                if ("".equals(s.getVehicleNo())) {
                    Optional<SchdAlternateAssign> assignOptional = assignList.stream()
                            .filter(r -> String.valueOf(s.getRouteId()).equals(r.getRouteIds())).findFirst();
                    if (assignOptional.isPresent()) {
                        Optional<Vehicle> resultDTO = vehicleLinkedList.stream()
                                .filter(p -> p.getLpno().equals(assignOptional.get().getVehicleNos())).findFirst();
                        if (resultDTO.isPresent()) {
                            Vehicle vehicle = resultDTO.get();
                            s.setVehicleNo(vehicle.getLpno());
                            vehicleLinkedList.remove(resultDTO.get());
                            Optional<Vehicle> vehicleT = vehicleList.stream().filter(o -> o.getLpno().equals(vehicle.getLpno())).findFirst();
                            if (vehicleT.isPresent()) {
                                vehicleList.remove(vehicleT);
                            }
                        }
                    }
                }
            });
            log.info("=====主班车辆排班结果====="+targetList);
            //需要找到未排车的替班车线路，再进行组合排列验证

            //TODO 替班车辆操作,需要知道那
            // 替班线路车辆数据
            List<KeyValue<String,String>> keyValueList = new LinkedList<>();
            assignListT.stream().forEach(s -> {
                String[] routes = s.getRouteIds().split(",");
                for (int k =0; k < routes.length; k++){
                    KeyValue<String,String> keyValue = new KeyValue<>();
                    keyValue.setKey(routes[k]);
                    keyValue.setValue(s.getVehicleNos());
                    keyValueList.add(keyValue);
                }
            });
            //过滤不可用的车辆数据
            List<KeyValue<String,String>> keyValueListT = keyValueList.stream().filter(s -> {
                String vehicleNo = s.getValue();
                Optional<Vehicle> resultDTO = vehicleList.stream().filter(r -> r.getLpno().equals(vehicleNo)).findFirst();
                return resultDTO.isPresent();
            }).collect(Collectors.toList());
            //随机取出可匹配的线路车辆，但不是最大化
            targetList.stream().forEach(o -> {
                Long routeId = o.getRouteId();
                if ("".equals(o.getVehicleNo())) {
                    Optional<KeyValue<String, String>> existOptional = keyValueListT.stream().filter(t -> t.getKey().equals(String.valueOf(routeId))).findFirst();
                    if (existOptional.isPresent()) {
                        KeyValue<String, String> keyValue = existOptional.get();
                        String vehicleNo = keyValue.getValue();
                        Optional<Vehicle> resultDTO = vehicleList.stream().filter(n -> n.getLpno().equals(vehicleNo)).findAny();
                        if (resultDTO.isPresent()) {
                            o.setVehicleNo(vehicleNo);
                            keyValueListT.remove(keyValue);
                            vehicleList.remove(resultDTO.get());
                        }
                    }
                }
            });
            log.info("====非西湖线路可用车辆=======");
            log.info(vehicleLinkedList.stream().map(Vehicle::getLpno).collect(Collectors.joining(",")));
            //计算线路车辆数据
            targetList.stream().forEach(k -> {
                String routeId = String.valueOf(k.getRouteId());

                if ("".equals(k.getVehicleNo())){
                    //从 vehicleLinkedList 中选取可用的车辆
                    Optional<Vehicle> vehicleOptional = vehicleLinkedList.stream().findAny();
                    if (vehicleOptional.isPresent()){
                        Vehicle vehicle = vehicleOptional.get();
                        vehicleLinkedList.remove(vehicle);
                        k.setVehicleNo(vehicle.getLpno());
                        //并从vehicleList中删除该数据
                        Optional<Vehicle> vehicleT = vehicleList.stream().filter(u -> u.getId().equals(vehicle.getId())).findFirst();
                        if (vehicleT.isPresent()){
                            vehicleList.remove(vehicleT.get());
                        }
                    }
                }
            });
            log.info("非西湖线路已排好车辆数据：------>"+targetList);
//            //西湖线路不受规则影响
//            targetList.stream().forEach(k -> {
//                String routeId = String.valueOf(k.getRouteId());
//                Long departmentId = k.getDepartmentId();
//                if (xhRouteIds.contains(routeId) && "".equals(k.getVehicleNo())){
//                    //从vehicleList中选取可用的车辆
//                    Optional<Vehicle> vehicleOptional = vehicleList.stream().filter(o -> o.getDepartmentId().equals(departmentId)).findAny();
//                    if (vehicleOptional.isPresent()){
//                        Vehicle vehicle = vehicleOptional.get();
//                        k.setVehicleNo(vehicle.getLpno());
//                        vehicleList.remove(vehicle);
//                    }
//                }
//            });
//            log.info("所有线路排好车辆数据: ------->"+targetList);
        }
    }

    /**
     * 线路排班司机  1.主班司机进行线路排班   2.替班司机进行线路排班 3.主班、替班均无司机数据的进行线路排班
     * 1.排班实现过程（略）
     * 2.排班实现过程【从替班司机列表中根据线路数量情况进行排列组合选取数据，并将选取的数据进行线路验证，只要有一个司机序列满足即可】
     * 3.实现过程： 可用司机列表数据中随机选择
     * @param targetList 目标结果数据
     * @param employeeList 可选员工列表
     * @param clearPlanId  清机季度计划ID
     * @return
     */
    private void formatDriver(List<SchdResultValueDTO> targetList, List<EmployeeResultDTO> employeeList, Long clearPlanId){
        //查询主替班
        QueryWrapper wrapper = Wrappers.query().in("plan_id",clearPlanId).eq("plan_type",0).eq("deleted",0);
        List<SchdAlternateAssign> allAssignList = assignMapper.selectList(wrapper);

        /////////////1.主班司机进行线路排班/////////////////////
        List<SchdAlternateAssign> assignList = allAssignList.stream().filter(s -> s.getAlternateType() == 0).collect(Collectors.toList());
        //如果主班司机存在
        log.info("==主班司机线路数据=="+assignList);
        targetList.stream().forEach(s -> {
            //属性设置
            Optional<SchdAlternateAssign> assignOptional = assignList.stream()
                    .filter(r -> String.valueOf(s.getRouteId()).equals(r.getRouteIds())).findFirst();
            if (assignOptional.isPresent()){
                Optional<EmployeeResultDTO>  resultDTO = employeeList.stream()
                        .filter(p -> p.getEmployeeId().equals(assignOptional.get().getEmployeeId())).findFirst();
                if (resultDTO.isPresent()){
                    s.setDriver(resultDTO.get().getEmployeeId());
                    employeeList.remove(resultDTO.get());
                }
            }
        });
        log.info("=====主班司机排班结果=============="+targetList);
        /////////////////////2.替班司机进行线路排班////////////////////
        //其他司机进行替班选择
       List<String> selectRouteIds = targetList.stream().filter(r->r.getDriver() != null && r.getDriver() > 0L)
               .map(s -> String.valueOf(s.getRouteId())).collect(Collectors.toList());
       //提取线路与司机的映射关系数据
        List<KeyValue<String,Long>> keyValueList = new LinkedList<>();
        allAssignList.stream().forEach(s -> {
            String[] routes = s.getRouteIds().split(",");
            for (int k =0; k < routes.length; k++){
                KeyValue<String,Long> keyValue = new KeyValue<>();
                keyValue.setKey(routes[k]);
                keyValue.setValue(s.getId());
                keyValueList.add(keyValue);
            }
        });
        Map<String, List<KeyValue<String, Long>>> routeDriverMap = keyValueList.stream().collect(Collectors.groupingBy(KeyValue::getKey));
        log.info("===线路司机映射===" + routeDriverMap);
        //得到Map数据 {1=[KeyValue(key=1, value=1)], 2=[KeyValue(key=2, value=1), KeyValue(key=2, value=2)], 3=[KeyValue(key=3, value=1)}
        //剔除已经选择司机的线路数据 得到List数据 {1=[KeyValue(key=1, value=1)], 2=[KeyValue(key=2, value=1), KeyValue(key=2, value=2)], 3=[KeyValue(key=3, value=1)}
        List<Map.Entry<String, List<KeyValue<String, Long>>>> entryList = routeDriverMap.entrySet().stream().filter(m -> !selectRouteIds.contains(m.getKey())).collect(Collectors.toList());
        //数据进行随机选取实现
        //计算需要替班需要的线路数据
        List<String> needRouteIdList = entryList.stream().map(Map.Entry::getKey).collect(Collectors.toList());
        //计算司机，过滤提取当天可用的driver司机
        //为了保证司机的随机性，需要设置driverList随机排序
        Collections.shuffle(entryList);
        List<Long> driverList = entryList.stream().flatMap(t -> t.getValue().stream()).map(KeyValue::getValue)
                .filter(m -> {
                    Optional<EmployeeResultDTO> resultDTO = employeeList.stream().filter(o -> o.getEmployeeId().equals(m)).findFirst();
                    return resultDTO.isPresent();
                }).distinct().collect(Collectors.toList());
        int routeSize = needRouteIdList.size();
        int driverSize = driverList.size();
        //司机排列组合选取并进行线路司机验证是否其满足，只要有一满足即可
        //得到线路列表[[1,2,3],[2,3,4],[3,4,5]]
        List<List<Long>> combineList = new LinkedList<>();
        int[] b = new int[routeSize];

        log.info("需要替班排班线路数："+routeSize);
        log.info("需要替班司机数量："+driverSize);
        log.info("需要替班的司机列表数据:-------->"+driverList);
        if (driverSize > 0 && routeSize > 0){
            getCombine(driverSize,routeSize, routeSize,b,driverList,combineList);
            log.info("====排班替班司机组合数据:=====" + combineList);
            //排序
            combineList.stream().forEach(s -> {
                //获取排序结果，
                List<List<Long>> seqList = new LinkedList<>();
                getSequence(s.toArray(),0,seqList);
                log.info("======排班替班司机排列顺序======"+seqList);
                Optional<List<Long>> targetDriverList = seqList.stream().filter(r -> {
                    //验证当前序列数据是否在线路中
                    return checkRoute(r,needRouteIdList,entryList);
                }).findFirst();
                //得到所满足条件的结果列表数据
                //设置线路结果数据【线路顺序与司机顺序对应一致】
                if (targetDriverList.isPresent()){
                    Map<String,Long> targetMap = new HashMap<>();
                    for (int p = 0; p < targetDriverList.get().size(); p++){
                        String routeId = needRouteIdList.get(p);
                        Long driverId = targetDriverList.get().get(p);
                        targetMap.put(routeId,driverId);
                    }
                    //替班司机设置
                    targetList.stream().forEach(m -> {
                        String routeId = String.valueOf(m.getRouteId());
                        Long driverId = targetMap.get(routeId);
                        if(driverId != null){
                            //从employeeList列表中移除
                            Optional<EmployeeResultDTO> resultDTO = employeeList.stream().filter(o -> o.getEmployeeId().equals(driverId)).findFirst();
                            if (resultDTO.isPresent()){
                                employeeList.remove(resultDTO.get());
                            }
                            m.setDriver(driverId);

                        }
                    });
                }
            });
        }

        ////////////////////3.未设置替班线路的随机排[需要优先考虑加班的员工]/////////////////////
        //需要优先排加班的员工数据
        log.info("===主替班已排好司机数据:------->"+targetList);
        formatData(targetList,employeeList,true,1);
        formatData(targetList,employeeList,false,1);
    }

    /**
     * 护卫线路设置[1条线路需要两名护卫]
     * @param targetList 目标结果数据列表
     * @param employeeList 可选员工列表
     */
    private void formatSecurity (List<SchdResultValueDTO> targetList, List<EmployeeResultDTO> employeeList, Long securityPlanId ){
        //查询主替班护卫数据查询
        QueryWrapper wrapper = Wrappers.query().in("plan_id",securityPlanId)
                .eq("plan_type",1).eq("deleted",0);
        List<SchdAlternateAssign> allAssignList = assignMapper.selectList(wrapper);

        /////////////1.主班护卫进行线路排班/////////////////////
        List<SchdAlternateAssign> assignList = allAssignList.stream().filter(s -> s.getAlternateType() == 0).collect(Collectors.toList());
        ///替班数据
        List<SchdAlternateAssign> assignListT = allAssignList.stream().filter(s -> s.getAlternateType() == 1).collect(Collectors.toList());
        //如果主班护卫存在
        //设置护卫1
        targetList.stream().forEach(s -> {
            //属性设置
            Optional<SchdAlternateAssign> assignOptional = assignList.stream()
                    .filter(r -> String.valueOf(s.getRouteId()).equals(r.getRouteIds())).findFirst();
            if (assignOptional.isPresent()){
                assignList.remove(assignOptional.get());
                Optional<EmployeeResultDTO>  resultDTO = employeeList.stream()
                        .filter(p -> p.getEmployeeId().equals(assignOptional.get().getEmployeeId())).findFirst();
                if (resultDTO.isPresent()){
                    s.setScurityA(resultDTO.get().getEmployeeId());
                    employeeList.remove(resultDTO.get());
                }
            }
        });
        //设置护卫2
        targetList.stream().forEach(s -> {
            Optional<SchdAlternateAssign> assignOptional = assignList.stream()
                    .filter(r -> String.valueOf(s.getRouteId()).equals(r.getRouteIds())).findFirst();
            if (assignOptional.isPresent()) {
                assignList.remove(assignOptional.get());
                Optional<EmployeeResultDTO> resultDTO = employeeList.stream()
                        .filter(p -> p.getEmployeeId().equals(assignOptional.get().getEmployeeId())).findFirst();
                if (resultDTO.isPresent()) {
                    s.setScurityB(resultDTO.get().getEmployeeId());
                    employeeList.remove(resultDTO.get());
                }
            }
        });
        ///////////2.替班护卫排班///////////////////////
        //TODO 替班人员选择
        //提取替班线路与护卫的映射关系数据
        List<KeyValue<String,Long>> keyValueList = new LinkedList<>();
        assignListT.stream().forEach(s -> {
            String[] routes = s.getRouteIds().split(",");
            for (int k =0; k < routes.length; k++){
                KeyValue<String,Long> keyValue = new KeyValue<>();
                keyValue.setKey(routes[k]);
                keyValue.setValue(s.getEmployeeId());
                keyValueList.add(keyValue);
            }
        });
        //过滤线路替班护卫是否在当天可选人员中
        List<KeyValue<String,Long>> keyValueListT = keyValueList.stream().filter(s -> {
            Long empId = s.getValue();
            Optional<EmployeeResultDTO> resultDTO = employeeList.stream().filter(r -> r.getEmployeeId().equals(empId)).findFirst();
            return resultDTO.isPresent();
        }).collect(Collectors.toList());
        //替班护卫ID集合
        List<Long> securityList = keyValueListT.stream().map(KeyValue::getValue).distinct().collect(Collectors.toList());

        //计算替班线路护卫所缺数量【{线路1：2}，{线路2:1}，{线路3:2}】
        List<KeyValue<Long,Integer>> routeNumList = targetList.stream().filter(r-> r.getScurityA() == 0L || r.getScurityB() == 0L)
                .map(t -> {
                    int count = 0;
                    if(t.getScurityA() == 0L) {  count++; }
                    if (t.getScurityB() == 0L) { count++; }
                    KeyValue<Long,Integer> keyValue = new KeyValue<>();
                    keyValue.setKey(t.getRouteId());
                    keyValue.setValue(count);
                    return keyValue;
                }).collect(Collectors.toList());
        //获取需要抽取的总数
        Integer needTotal = routeNumList.stream().collect(Collectors.summingInt(KeyValue::getValue));
        Integer securitySize = securityList.size();
        //组合数据
        if (securitySize < needTotal){
            //TODO 替班无法一次性排，部分可以排出，是否要考虑【排部分】？？？
            log.info("=====替班无法一次排完=======");
            //按顺序选取护卫  从keyValueListT中取出，只要满足条件就取出，但不是最大化满足替班排班结果
            targetList.stream().forEach(o -> {
                Long routeId = o.getRouteId();
                if (o.getScurityA() == 0L ){
                    Optional<KeyValue<String,Long>> existOptional = keyValueListT.stream().filter(t -> t.getKey().equals(String.valueOf(routeId))).findFirst();
                    if(existOptional.isPresent()){
                        KeyValue<String,Long> keyValue = existOptional.get();
                        Long securityId = keyValue.getValue();
                        Optional<EmployeeResultDTO> resultDTO = employeeList.stream()
                                .filter(n ->n.getEmployeeId().equals(securityId)).findAny();
                        if (resultDTO.isPresent()){
                            o.setScurityA(securityId);
                            keyValueListT.remove(keyValue);
                            employeeList.remove(resultDTO.get());
                        }
                    }
                }
                if (o.getScurityB() == 0L ){
                    Optional<KeyValue<String,Long>> existOptional = keyValueListT.stream().filter(t -> t.getKey().equals(String.valueOf(routeId))).findFirst();
                    if(existOptional.isPresent()){
                        KeyValue<String,Long> keyValue = existOptional.get();
                        Long securityId = keyValue.getValue();
                        Optional<EmployeeResultDTO> resultDTO = employeeList.stream()
                                .filter(n ->n.getEmployeeId().equals(securityId)).findAny();
                        if (resultDTO.isPresent()){
                            o.setScurityB(securityId);
                            keyValueListT.remove(keyValue);
                            employeeList.remove(resultDTO.get());
                        }
                    }
                }
            });
        } else {
            //排列组合进行选取
            List<List<Long>> combineList = new LinkedList<>();
            int[] b = new int[needTotal];
            if (securitySize  > 0 && needTotal > 0) {
                getCombine(securitySize, needTotal, needTotal, b, securityList, combineList);
            }
            //得到组合数据
            combineList.stream().forEach(s -> {
                //获取排序结果，
                List<List<Long>> seqList = new LinkedList<>();
                getSequence(s.toArray(),0,seqList);
                log.info("======排班替班司机排列顺序======"+seqList);
                Optional<List<Long>> targetSecurityList = seqList.stream().filter(r -> {
                    //验证当前序列数据是否在线路中 1,2,3
                    return checkGuard(r,targetList, keyValueListT);
                }).findFirst();

                //设置线路结果数据【线路顺序与司机顺序对应一致】
                if (targetSecurityList.isPresent()){
                    int numberIndex = 0;
                    List<Long> securityIdList = targetSecurityList.get();
                    //替班司机设置
                    targetList.stream().forEach(m -> {
                        String routeId = String.valueOf(m.getRouteId());
                        if (m.getScurityA() == 0L){
                            Long securityId = securityIdList.get(numberIndex);
                            if(securityId != null){
                                //从employeeList列表中移除
                                Optional<EmployeeResultDTO> resultDTO = employeeList.stream().filter(o -> o.getEmployeeId().equals(securityId)).findFirst();
                                if (resultDTO.isPresent()){
                                    employeeList.remove(resultDTO.get());
                                }
                                m.setScurityA(securityId);
                            }
                        }
                        if (m.getScurityB() == 0L){
                            Long securityId = securityIdList.get(numberIndex);
                            if(securityId != null){
                                //从employeeList列表中移除
                                Optional<EmployeeResultDTO> resultDTO = employeeList.stream().filter(o -> o.getEmployeeId().equals(securityId)).findFirst();
                                if (resultDTO.isPresent()){
                                    employeeList.remove(resultDTO.get());
                                }
                                m.setScurityB(securityId);
                            }
                        }
                    });
                }
            });
            log.info("====替班护卫排班====="+targetList);
        }

        //////////3.主班、替班均无护卫数据的进行线路排班
        //优先级高先排
        formatData(targetList,employeeList,true,2);
        formatData(targetList,employeeList,true,5);
        //其他护卫1随机排
        targetList.stream().forEach(o -> {
            if (o.getScurityA() == 0L){
                Optional<EmployeeResultDTO> resultDTO = employeeList.stream().filter(n ->n.getType() == 2).findAny();
                if (resultDTO.isPresent()){
                    o.setScurityA(resultDTO.get().getEmployeeId());
                    employeeList.remove(resultDTO.get());
                }
            }
        });
        //其他护卫2随机排
        targetList.stream().forEach(o -> {
            if (o.getScurityB() == 0L){
                Optional<EmployeeResultDTO> resultDTO = employeeList.stream().filter(n ->n.getType() == 2).findAny();
                if (resultDTO.isPresent()){
                    o.setScurityB(resultDTO.get().getEmployeeId());
                    employeeList.remove(resultDTO.get());
                }
            }
        });
    }

    /**
     * 清机、钥匙员线路设置
     * @param targetList  目标结果数据列表
     * @param employeeList 可选员工列表
     */
    private void formatCleaner(List<SchdResultValueDTO> targetList, List<EmployeeResultDTO> employeeList,Long departmentId){
        //优先考虑网点备案人员情况
        filterPassAuth(targetList, employeeList, departmentId);

        //优先从清机岗、密码岗中选出车长，提取车长集合
        List<EmployeeResultDTO> leaderResultList = employeeList.stream().filter(o -> o.getType() == 3 || o.getType() == 4)
                .filter(p -> p.getRouteLeader() == 1).collect(Collectors.toList());
        if (leaderResultList.size() == 0){
           // throw new BusinessException(1,"清机岗无可用车长");
            return;
        }
        log.info("===车长数据==="+leaderResultList);
        //优先分配一个车长
        targetList.stream().forEach(m -> {
            if (m.getLeader() == 0L){
                //钥匙员、密码员均不存在
                if (m.getKeyMan() == 0L && m.getOpMan() == 0L){
                    Optional<EmployeeResultDTO> resultDTOOptional = leaderResultList.parallelStream().findAny();
                    if (resultDTOOptional.isPresent()) {
                        EmployeeResultDTO employeeResultDTO = resultDTOOptional.get();
                        Long employeeId = employeeResultDTO.getEmployeeId();
                        if (employeeResultDTO.getType() == 3) {
                            m.setKeyMan(employeeId);
                        } else {
                            m.setOpMan(employeeId);
                        }
                        m.setLeader(employeeId);
                        leaderResultList.remove(employeeResultDTO);
                        //从employeeList中移除
                        Optional<EmployeeResultDTO> resultDTO = employeeList.stream().filter(n -> n.getEmployeeId().equals(employeeId)).findFirst();
                        if (resultDTO.isPresent()) {
                            employeeList.remove(resultDTO.get());
                        }
                    }
                } else if (m.getKeyMan() == 0L){
                    Optional<EmployeeResultDTO> resultDTOOptional = leaderResultList.parallelStream().filter(t -> t.getType() == 3).findAny();
                    if (resultDTOOptional.isPresent()) {
                        EmployeeResultDTO employeeResultDTO = resultDTOOptional.get();
                        Long employeeId = employeeResultDTO.getEmployeeId();
                        m.setKeyMan(employeeId);
                        m.setLeader(employeeId);
                        leaderResultList.remove(employeeResultDTO);
                        //从employeeList中移除
                        Optional<EmployeeResultDTO> resultDTO = employeeList.stream().filter(n -> n.getEmployeeId().equals(employeeId)).findFirst();
                        if (resultDTO.isPresent()) {
                            employeeList.remove(resultDTO.get());
                        }
                    }
                } else if (m.getOpMan() == 0L){
                    Optional<EmployeeResultDTO> resultDTOOptional = leaderResultList.parallelStream().filter(t -> t.getType() == 4).findAny();
                    if (resultDTOOptional.isPresent()) {
                        EmployeeResultDTO employeeResultDTO = resultDTOOptional.get();
                        Long employeeId = employeeResultDTO.getEmployeeId();
                        m.setKeyMan(employeeId);
                        m.setLeader(employeeId);
                        leaderResultList.remove(employeeResultDTO);
                        //从employeeList中移除
                        Optional<EmployeeResultDTO> resultDTO = employeeList.stream().filter(n -> n.getEmployeeId().equals(employeeId)).findFirst();
                        if (resultDTO.isPresent()) {
                            employeeList.remove(resultDTO.get());
                        }
                    }
                }
            }
        });

        log.info("===车长排班分配数据====="+targetList);

        //先排加班清机员  1=司机岗  2=护卫岗  3=钥匙岗  4=密码岗
        formatData(targetList,employeeList,true,3);
        formatData(targetList,employeeList,false,3);
        //再排密码员
        formatData(targetList,employeeList,true,4);
        formatData(targetList,employeeList,false,4);
        //如果清机员、或密码员人员不够，则相互进行替补
        targetList.stream().forEach(o -> {
            if (o.getKeyMan() == 0L){
                Stream<EmployeeResultDTO> targetStream = employeeList.parallelStream().filter(n ->(n.getType() == 3 || n.getType() == 4 ));
                Optional<EmployeeResultDTO> resultDTO = targetStream.findAny();
                if (resultDTO.isPresent()){
                    o.setKeyMan(resultDTO.get().getEmployeeId());
                    employeeList.remove(resultDTO.get());
                }
            }
        });
        targetList.stream().forEach(o -> {
            if (o.getOpMan() == 0L){
                Stream<EmployeeResultDTO> targetStream = employeeList.parallelStream().filter(n ->(n.getType() == 3 || n.getType() == 4 ));
                Optional<EmployeeResultDTO> resultDTO = targetStream.findAny();
                if (resultDTO.isPresent()){
                    o.setOpMan(resultDTO.get().getEmployeeId());
                    employeeList.remove(resultDTO.get());
                }
            }
        });

        //leader设置
        targetList.stream().forEach(h -> {
            if (h.getLeader() == 0){
                Long leader = h.getKeyMan();
                h.setLeader(leader);
            }
        });
    }

    /**
     * 过滤网点备案员工
     * @param targetList 目标结果
     * @param employeeList 员工列表
     * @param departmentId 部门ID
     */
    private void filterPassAuth(List<SchdResultValueDTO> targetList, List<EmployeeResultDTO> employeeList,Long departmentId){
        QueryWrapper wrapper = Wrappers.query().eq("department_id",departmentId)
                .eq("deleted",0).eq("pass_type",1);
        List<SchdCleanPassAuth> authList = authMapper.selectList(wrapper);
        //进行线路人员设置
        Set<Long> bankIds = authList.stream().map(SchdCleanPassAuth::getBankId).collect(Collectors.toSet());
        Map<Long,String> bankMap = new HashMap<>();
        if (bankIds.size() > 0){
            List<Bank> bankList = bankMapper.selectBatchIds(bankIds);
            bankMap = bankList.stream().collect(Collectors.toMap(Bank::getId,Bank::getRouteNo));
        }
        Map<Long, String> finalBankMap = bankMap;
        List<KeyValue<Long,String>> keyValueList =  authList.stream().map(item -> {
            KeyValue<Long,String> keyValue = new KeyValue<>();
            keyValue.setKey(item.getEmpId());
            String routeNo = finalBankMap.get(item.getBankId());
            keyValue.setValue(routeNo);

            return keyValue;
        }).collect(Collectors.toList());
        //按线路分组
        Map<String,List<KeyValue<Long,String>>> routeMap = keyValueList.stream().collect(Collectors.groupingBy(KeyValue::getValue));
        log.info("==线路备案人员==="+routeMap);
        //线路进行人员过滤
        List<KeyValue<String,List<Long>>> routeEmpList =  routeMap.entrySet().stream().map(entry -> {
            List<KeyValue<Long,String>> keyValues = entry.getValue();
            List<Long> empIdList = keyValues.stream().filter(s -> {
                Optional<EmployeeResultDTO> valueOptional = employeeList.stream().filter(t -> t.getEmployeeId().equals(s.getKey())).findFirst();
                return valueOptional.isPresent();
            }).map(KeyValue::getKey).collect(Collectors.toList());
            KeyValue<String,List<Long>> keyValue = new KeyValue<>();
            keyValue.setKey(entry.getKey());
            keyValue.setValue(empIdList);
            return keyValue;
        }).collect(Collectors.toList());

        log.info("==过滤后线路备案人员==="+routeEmpList);

        //线路清机员、密码员进行设置
        targetList.stream().forEach(o -> {
           String routeNo = o.getRouteNo();
           Optional<List<Long>> empIdOptional = routeEmpList.stream().filter(r -> r.getKey().equals(routeNo)).map(KeyValue::getValue).findFirst();
           if (empIdOptional.isPresent()){
               List<Long> empIdList = empIdOptional.get();
               //查询钥匙员
               EmployeeResultDTO resultDTO = findLeader(empIdList,employeeList,3);
               //设置人员
               Long leader = 0L;
               if (resultDTO != null){
                   Long keyEmpId = resultDTO.getEmployeeId();
                   o.setKeyMan(resultDTO.getEmployeeId());
                   Optional<EmployeeResultDTO> employeeOptional = employeeList.stream().filter(m -> m.getEmployeeId().equals(keyEmpId)).findFirst();
                   if(employeeOptional.isPresent()){
                       employeeList.remove(employeeOptional.get());
                   }
                   if (resultDTO.getRouteLeader() == 1){
                       leader = resultDTO.getEmployeeId();
                   }
               }

               //查询密码员
               EmployeeResultDTO resultDTO2 = findLeader(empIdList,employeeList,4);
               //设置人员
               if (resultDTO2 != null){
                   Long opEmpId = resultDTO2.getEmployeeId();
                   o.setOpMan(resultDTO2.getEmployeeId());
                   Optional<EmployeeResultDTO> employeeOptional2 = employeeList.stream().filter(m -> m.getEmployeeId().equals(opEmpId)).findFirst();
                   if(employeeOptional2.isPresent()){
                       employeeList.remove(employeeOptional2.get());
                   }
                   if (resultDTO2.getRouteLeader() == 1){
                       leader = resultDTO2.getEmployeeId();
                   }
               }
               o.setLeader(leader);
           }
        });
    }

    /**
     *
     * @param empIdList 员工ID
     * @param employeeList 员工列表
     * @param jobType 岗位类型
     * @return
     */
    private EmployeeResultDTO findLeader(List<Long> empIdList, List<EmployeeResultDTO> employeeList, Integer jobType){
        //优先查找具有车长资格的员工
        EmployeeResultDTO resultDTO = null;
        for (Long empId: empIdList) {
            Optional<EmployeeResultDTO> resultDTOOptional = employeeList.parallelStream()
                    .filter(m -> m.getEmployeeId().equals(empId) && m.getRouteLeader() == 1 && m.getType().equals(jobType)).findFirst();
            if (resultDTOOptional.isPresent()){
                resultDTO = resultDTOOptional.get();
                break;
            }
            Optional<EmployeeResultDTO> resultDTOOptional2 = employeeList.parallelStream()
                    .filter(m -> m.getEmployeeId().equals(empId) && m.getType().equals(jobType)).findFirst();
            if (resultDTOOptional2.isPresent()){
                resultDTO = resultDTOOptional2.get();
                break;
            }
        }
        return resultDTO;
    }


    /**
     *  司机、线路个数应一致
     * @param driverList 司机列表数据
     * @param routeIdList 线路列表数据
     * @param entryList 线路 司机列表数据
     * @return
     */
    private boolean checkRoute(List<Long> driverList, List<String> routeIdList,List<Map.Entry<String, List<KeyValue<String, Long>>>> entryList){
        boolean check = true;
        for (int k = 0;  k< driverList.size(); k++){
            Long driverId = driverList.get(k);
            String routeId = routeIdList.get(k);
            Optional<List<KeyValue<String,Long>>> keyValueList = entryList.stream()
                    .filter(s -> s.getKey().equals(routeId)).map(Map.Entry::getValue).findFirst();
            if (!keyValueList.isPresent()){
                check = false;
                break;
            }
            //验证该线路上是否存在该司机
            Optional<KeyValue<String,Long>> resultKeyValue = keyValueList.get().stream().filter(o -> o.getValue().equals(driverId)).findFirst();
            if (!resultKeyValue.isPresent()){
                check = false;
                break;
            }
        }
        return check;
    }

    /**
     * 按顺序进行处理,如果所有元素都在routeSecurityList【{线路ID,护卫ID}】中，则表示该序列满足要求
     * @param seqList  目标序列
     * @param valueDTOList
     * @param routeSecurityList
     * @return
     */
    private boolean checkGuard(List<Long> seqList, List<SchdResultValueDTO> valueDTOList, List<KeyValue<String,Long>> routeSecurityList){
        boolean check = true;
        //过滤数据
        List<SchdResultValueDTO> valueDTOListT = valueDTOList.stream()
                .filter(r -> r.getScurityA() == 0 || r.getScurityB() == 0).collect(Collectors.toList());
        int index = 0;
        for (int h = 0; h < valueDTOListT.size(); h++){
            Long routeId = valueDTOListT.get(h).getRouteId();
            if (valueDTOList.get(h).getScurityA() == 0L){
                Long securityId = seqList.get(index);
                Optional<KeyValue<String,Long>> keyValueOptional = routeSecurityList.stream()
                        .filter(m -> m.getKey().equals(String.valueOf(routeId)) && m.getValue().equals(securityId)).findFirst();
                if (!keyValueOptional.isPresent()){
                    check = false;
                    break;
                }
                index++;
            }
            if (valueDTOList.get(h).getScurityB() == 0L){
                Long securityId = seqList.get(index);
                Optional<KeyValue<String,Long>> keyValueOptional = routeSecurityList.stream()
                        .filter(m -> m.getKey().equals(String.valueOf(routeId)) && m.getValue().equals(securityId)).findFirst();
                if (!keyValueOptional.isPresent()){
                    check = false;
                    break;
                }
                index++;
            }
        }
        return check;
    }

    /**
     * 车辆、线路个数应一致
     * @param vehicleList 车辆列表数据
     * @param valueDTOList 线路列表数据
     * @param xhRouteIds  西湖线路ID
     * @param tbRouteNumberList [{线路1：车辆编号234}，{线路2： 车辆编号345}]
     * @param xhRouteNumberList  西湖线路尾号  [{线路1： 2,4}， {线路2： 3，4}]
     * @return
     */
    private boolean checkVehicle(List<String> vehicleList,List<SchdResultValueDTO> valueDTOList, Set<String> xhRouteIds,
                                 List<KeyValue<String,String>> tbRouteNumberList, List<KeyValue<String,String>> xhRouteNumberList ){
        boolean check = true;
        int index = 0;
        for (int k = 0; k < valueDTOList.size(); k++){
            String vehicleNo = valueDTOList.get(k).getVehicleNo();
            String routeId = String.valueOf(valueDTOList.get(k).getRouteId());
            if("".equals(vehicleNo)){
                String resultVehicleNo = vehicleList.get(index);
                if (xhRouteIds.contains(routeId)){
                    //需要验证尾号，并且验证车辆是否在该线路中
                    String number = ToolUtil.getLastNumber(resultVehicleNo);
                    Optional<KeyValue<String,String>>  valueOptional = tbRouteNumberList.stream()
                            .filter(m -> m.getKey().equals(routeId) && m.getValue().equals(resultVehicleNo)).findFirst();
                    if(!valueOptional.isPresent()){
                        check = false;
                        break;
                    }
                    Optional<KeyValue<String,String>> valueOptionalT = xhRouteNumberList.stream().filter(m -> m.getKey().equals(routeId)).findFirst();
                    if (!valueOptionalT.get().getValue().contains(number)){
                        check = false;
                        break;
                    }
                }
                //非西湖线路
                if (xhRouteIds.contains(routeId)){
                    //验证车辆是否在该线路中
                    Optional<KeyValue<String,String>>  valueOptional = tbRouteNumberList.stream()
                            .filter(m -> m.getKey().equals(routeId) && m.getValue().equals(resultVehicleNo)).findFirst();
                    if(!valueOptional.isPresent()){
                        check = false;
                        break;
                    }
                }
                index++;
            }
        }

        return check;
    }

    /**
     * M个数的列表中随机取出N个数的组合结果
     * @param m 变量因子m
     * @param n 变量因子 n
     * @param number 提取列表数量
     * @param b  迭代的列表数据
     * @param a  原列表数据
     * @param lists  目标结果数据
     */
    <T> void getCombine(int m,int n,int number, int[] b, List<T> a, List<List<T>> lists){
        int i,j;
        for(i=n;i<=m;i++) {
            b[n-1] = i-1;
            if(n>1) {
                getCombine(i - 1, n - 1, number, b, a, lists);
            } else {
                List<T> t = new LinkedList<>();
                for(j=0;j<=number-1;j++){
                    t.add(a.get(b[j]));
                }
                lists.add(t);
            }
        }
    }

    /**
     * 根据数据集合获取所有的排列 [A,B] -> [[A,B],[B,A]]
     * @param data  排序的数据集合
     * @param k 迭代的索引
     * @param list  结果数据
     * @param <T>
     */
    public <T> void getSequence(T[] data, int k, List list) {
       /* if (k == data.length) {
            List<T> t = new LinkedList();
            for (int i = 0; i < data.length; i++) {
                t.add(data[i]);
            }
            list.add(t);
        }
        for (int i = k; i < data.length; i++) {
            {
                T temp = data[k];
                data[k] = data[i];
                data[i] = temp;
            }
            getSequence(data, k + 1, list);
            {
                T temp = data[k];
                data[k] = data[i];
                data[i] = temp;
            }
        }
        */
        List<T> t = new LinkedList();
        for (int i = 0; i < data.length; i++) {
            t.add(data[i]);
        }
        list.add(t);
    }

    /**
     * 具体排班人员数据更新
     * @param targetList
     * @param employeeList
     * @param priority true =优先排班  false = 正常排班
     * @param jobType 1=司机岗  2=护卫岗A  3=钥匙岗  4=密码岗 5=护卫岗B   其中 2,5对应数据库2=护卫岗
     */
    private void formatData(List<SchdResultValueDTO> targetList, List<EmployeeResultDTO> employeeList, boolean priority, int jobType){
        targetList.stream().forEach(o -> {
            Long value = 0L;
            if (jobType == 1){
                value = o.getDriver();
            } else if (jobType == 2){
                value = o.getScurityA();
            } else if (jobType == 3){
                value = o.getKeyMan();
            } else if (jobType == 4){
                value = o.getOpMan();
            } else if (jobType == 5){
                value = o.getScurityB();
            }
            final int queryJobType = jobType == 5 ? 2 : jobType;
            if (value == 0L){
                Stream<EmployeeResultDTO> targetStream = employeeList.parallelStream().filter(n ->n.getType() == queryJobType);
                if (priority){
                    targetStream = targetStream.filter(m -> m.getExist() != null && m.getExist());
                }
                if (targetStream != null){
                    Optional<EmployeeResultDTO> resultDTO = targetStream.findAny();
                    if (resultDTO.isPresent()){
                        if (jobType == 1){
                            o.setDriver(resultDTO.get().getEmployeeId());
                        } else if (jobType == 2){
                            o.setScurityA(resultDTO.get().getEmployeeId());
                        } else if (jobType == 3){
                            o.setKeyMan(resultDTO.get().getEmployeeId());
                        } else if (jobType == 4){
                            o.setOpMan(resultDTO.get().getEmployeeId());
                        } else if (jobType == 5){
                            o.setScurityB(resultDTO.get().getEmployeeId());
                        }
                        employeeList.remove(resultDTO.get());
                    }
                }
            }
        });
    }

    /**
     * 根据尾号规则，随机取出一个可以排班的车牌号
     * @param vehicleList  车辆列表
     * @param permitNumbers 容许限行尾号
     * @return
     */
    private Vehicle filterVehicleHolidayRule(List<Vehicle> vehicleList, String permitNumbers){
        List<String> numberList = Arrays.asList(permitNumbers.split(","));
        //随机取出一个
        Optional<Vehicle> vehicleOptional = vehicleList.stream().filter(r ->{
            String number = ToolUtil.getLastNumber(r.getLpno());
            return numberList.contains(number);
        }).findFirst();
        if (!vehicleOptional.isPresent()){
            // throw new BusinessException(1,"西湖限行规则不满足");
            return null;
        }
        Vehicle vehicle = vehicleOptional.get();
        vehicleList.remove(vehicle);
        return vehicle;
    }

    /**
     * 非放假日通用限行规则过滤
     * @param vehicleList  车辆列表数据
     * @param time 限行时间
     * @param departmentId 部门ID
     * @return
     */
    private List<Vehicle> filterVehicleRule(List<Vehicle> vehicleList, long time, long departmentId){
        LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(time/1000, 0, ZoneOffset.of("+8"));
        int weekday = localDateTime.getDayOfWeek().getValue();
        log.info("排班日为星期几？----->"+weekday);
        // 如果当天是调休，则按调休日执行限行规则
        QueryWrapper planWrapper = Wrappers.query().eq("plan_day",time).eq("holiday_type",1).eq("deleted",0);
        planWrapper.last("LIMIT 1");
        SchdHolidayPlan holidayPlan = planMapper.selectOne(planWrapper);
        if (holidayPlan != null){
            weekday = holidayPlan.getWeekday();
        }
        log.info("排班日与法定调休合并，计算得到星期几? ---->"+weekday);
        //查询限行规则数据
        QueryWrapper wrapper = Wrappers.query().eq("department_id",departmentId).eq("deleted",0);
        List<SchdDriveRestrict>  restrictList = driveRestrictMapper.selectList(wrapper);
        Map<Integer,String> weekdayMap = restrictList.stream().collect(Collectors.toMap(SchdDriveRestrict::getWeekday,SchdDriveRestrict::getForbidNumbers));
        String ruleNumbers = weekdayMap.get(weekday);
        log.info("排班日当天限行规则尾号:------->"+ruleNumbers);
        if (ruleNumbers != null){
            // 过滤车辆号码
            String[] numberArr = ruleNumbers.split(",");
            List<String> numberList = Arrays.asList(numberArr);
            vehicleList = vehicleList.stream().filter(s -> {
                String number = ToolUtil.getLastNumber(s.getLpno());
                return  !numberList.contains(number);
            }).collect(Collectors.toList());
        }
        log.info("=====限行车辆过滤后的数据=====");
        log.info(vehicleList.stream().map(Vehicle::getLpno).collect(Collectors.joining(",")));
        return vehicleList;
    }


    /**
     * 替班人员替换
     * @param valueDTOList
     * @param time
     * @return
     */
    private void formatReplace(List<SchdResultValueDTO> valueDTOList, long time){
        LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(time/1000, 0, ZoneOffset.of("+8"));
        String adjustDate =  localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        //替班人员查询
        QueryWrapper adjustWrapper = Wrappers.query().eq("adjust_date",adjustDate)
                .eq("adjust_type",2).eq("deleted",0);
        List<SchdVacateAdjust> adjustList = adjustMapper.selectList(adjustWrapper);
        Map<Long,Long> adjustMap = adjustList.stream().collect(Collectors.toMap(SchdVacateAdjust::getEmpId,SchdVacateAdjust::getRepEmpId));
        if (adjustMap == null){
            return;
        }
        //人员进行替换
        valueDTOList.stream().forEach(t -> {
            Long driverId = adjustMap.get(t.getDriver());
            Long keyMan = adjustMap.get(t.getKeyMan());
            Long opMan = adjustMap.get(t.getOpMan());
            Long scurityA = adjustMap.get(t.getScurityA());
            Long scurityB = adjustMap.get(t.getScurityB());
            if (driverId != null){
                t.setDriver(driverId);
            }
            if (keyMan != null){
                t.setKeyMan(keyMan);
            }
            if (opMan != null){
                t.setOpMan(opMan);
            }
            if (scurityA != null){
                t.setScurityA(scurityA);
            }
            if (scurityB != null){
                t.setScurityB(scurityB);
            }
        });
    }
}
