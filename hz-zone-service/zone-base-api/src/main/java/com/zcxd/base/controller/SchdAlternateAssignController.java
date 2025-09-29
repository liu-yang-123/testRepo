package com.zcxd.base.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zcxd.base.dto.SchdAlternateAssignDTO;
import com.zcxd.base.exception.BusinessException;
import com.zcxd.base.service.*;
import com.zcxd.base.vo.SchdAlternateAssignVO;
import com.zcxd.base.vo.SchdDriverAssignVO;
import com.zcxd.common.annotation.OperateLog;
import com.zcxd.common.annotation.OperateType;
import com.zcxd.common.constant.DeleteFlagEnum;
import com.zcxd.common.constant.DriverAssignTypeEnum;
import com.zcxd.common.constant.DriverTypeEnum;
import com.zcxd.common.util.Result;
import com.zcxd.common.util.ResultList;
import com.zcxd.db.model.*;
import io.swagger.annotations.*;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 * 司机主替班配置 前端控制器
 * </p>
 *
 * @author shijin
 * @since 2021-07-05
 */
@RestController
@RequestMapping("/alternate")
@Api(tags="排班管理-候选分配管理")
public class SchdAlternateAssignController {

    @Resource
    SchdAlternateAssignService assignService;

    @Resource
    SchdVacatePlanService planService;

    @Resource
    EmployeeService employeeService;

    @Resource
    RouteTemplateService routeTemplateService;


    @OperateLog(value="添加候选分配",type= OperateType.ADD)
    @ApiOperation(value = "添加候选分配")
    @PostMapping({"/save","/guard/save"})
    public Result save(@RequestBody @Validated SchdAlternateAssignVO assignVO) {
        //验证是否添加
        int alternateType =  assignVO.getAlternateType().getValue();
        long planId = assignVO.getPlanId();
        //根据planType类型进行判断  0 = 清机类型即司机 1= 护卫（两名）
        if (assignVO.getAlternateType().equals(DriverTypeEnum.MAIN)){
            List<SchdAlternateAssign> assignList =  assignService.validateExist(planId, assignVO.getPlanType(),alternateType, assignVO.getRouteIds(), 0);
            int exist = assignList.size();
            if (assignVO.getPlanType() == 0 && exist >= 1){
                return Result.fail("该线路主班司机数据已经存在");
            }
            if (assignVO.getPlanType() == 1 && exist >= 2){
                return Result.fail("该线路主班护卫数据已经存在");
            }
          Optional<SchdAlternateAssign> assignOptional = assignList.stream().filter(t -> t.getEmployeeId().equals(assignVO.getEmployeeId())).findAny();
          if (assignOptional.isPresent()){
              return Result.fail("该线路员工数据已经存在");
          }
        }
        SchdAlternateAssign newSchdDriverAssign = new SchdAlternateAssign();
        BeanUtils.copyProperties(assignVO,newSchdDriverAssign);
        newSchdDriverAssign.setAlternateType(alternateType);
        boolean bRet = assignService.save(newSchdDriverAssign);
        return bRet? Result.success() : Result.fail();
    }

    @OperateLog(value="编辑司机分配",type= OperateType.EDIT)
    @ApiOperation(value = "编辑司机分配")
    @PostMapping({"/update","/guard/update"})
    public Result update(@RequestBody @Validated SchdAlternateAssignVO assignVO) {
        //验证是否添加
        int alternateType =  assignVO.getAlternateType().getValue();
        long planId = assignVO.getPlanId();
        //根据planType类型进行判断  0 = 清机类型即司机 1= 护卫（两名）
        if (assignVO.getAlternateType().equals(DriverTypeEnum.MAIN)){
            List<SchdAlternateAssign> assignList = assignService.validateExist(planId,assignVO.getPlanType(),alternateType, assignVO.getRouteIds(), assignVO.getId());
            int exist = assignList.size();

            if (assignVO.getPlanType() == 0 && exist >= 1){
                return Result.fail("该线路主班司机数据已经存在");
            }
            if (assignVO.getPlanType() == 1 && exist >= 2){
                return Result.fail("该线路主班护卫数据已经存在");
            }
            Optional<SchdAlternateAssign> assignOptional = assignList.stream().filter(t -> t.getEmployeeId().equals(assignVO.getEmployeeId())).findAny();
            if (assignOptional.isPresent()){
                return Result.fail("该线路员工数据已经存在");
            }
        }
        SchdAlternateAssign newSchdDriverAssign = new SchdAlternateAssign();
        BeanUtils.copyProperties(assignVO,newSchdDriverAssign);
        newSchdDriverAssign.setAlternateType(alternateType);
        boolean bRet = assignService.updateById(newSchdDriverAssign);
        return bRet? Result.success() : Result.fail();
    }

    @OperateLog(value="删除司机分配",type= OperateType.DELETE)
    @ApiOperation(value = "删除司机分配")
    @ApiImplicitParam(name = "id", value = "分配id", required = true, dataType = "Long")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping({"/delete/{id}","/guard/deleted/{id}"})
    public Result delete(@PathVariable("id")  Long id){
        SchdAlternateAssign oldSchdDriverAssign = assignService.getById(id);
        if (null == oldSchdDriverAssign) {
            return Result.fail("记录不存在");
        }
        SchdAlternateAssign newSchdDriverAssign = new SchdAlternateAssign();
        newSchdDriverAssign.setId(id);
        newSchdDriverAssign.setDeleted(DeleteFlagEnum.YES.getValue());
        boolean bRet = assignService.updateById(newSchdDriverAssign);
        return bRet? Result.success() : Result.fail();
    }

    @ApiOperation(value = "查询司机分配列表-分页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页数", required = true, dataType = "Integer",defaultValue = "1", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "limit", value = "显示数据条数", required = true, dataType = "Integer",defaultValue = "10", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "departmentId", value = "部门ID", required = true, dataType = "Long",defaultValue = "10", dataTypeClass = Long.class),
            @ApiImplicitParam(name = "planId", value = "计划ID", dataType = "Long", dataTypeClass = Long.class),
            @ApiImplicitParam(name = "routeId", value = "线路ID", dataType = "Long", dataTypeClass = Long.class),
            @ApiImplicitParam(name = "alternateType", value = "主替班类型", dataType = "Integer", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "planType", value = "主替班类型", dataType = "Integer", dataTypeClass = Integer.class),
    })
    @GetMapping({"/list","/guard/list"})
    public Result list(@RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @RequestParam Long departmentId,
                       @RequestParam(required = false) String name,
                       @RequestParam(defaultValue = "0", required = false) Long planId,
                       @RequestParam(defaultValue = "0", required = false) Long routeId,
                       @RequestParam(defaultValue = "-1", required = false) Integer alternateType,
                       @RequestParam(defaultValue = "0") Integer planType) {
        //查询员工数据
        Set<Long> empQueryIds = null;
        if (!StringUtils.isEmpty(name)){
            Employee employee = new Employee();
            employee.setDepartmentId(departmentId);
            employee.setEmpName(name);
            List<Employee> eList = employeeService.getEmployeeByCondition(employee);
            empQueryIds = eList.stream().map(Employee::getId).collect(Collectors.toSet());
        }

        //基础列表
        IPage<SchdAlternateAssign> iPage = assignService.listPage(page,limit,departmentId,planType,planId,routeId,alternateType, empQueryIds);
        //计划
        List<SchdVacatePlan> planList = planService.list();
        Map<Long,String> planMap = planList.stream().collect(Collectors.toMap(SchdVacatePlan::getId,SchdVacatePlan::getName));
        //员工
        Set<Long> empIds = iPage.getRecords().stream().map(SchdAlternateAssign::getEmployeeId).collect(Collectors.toSet());
        List<Employee> employeeList = !empIds.isEmpty() ? employeeService.listByIds(empIds) : new LinkedList<>();
        Map<Long,String> employeeMap = employeeList.stream().collect(Collectors.toMap(Employee::getId, s -> s.getEmpNo() +"/" +s.getEmpName()));
        Set<String> routeIds = iPage.getRecords().stream().map(s -> s.getRouteIds().split(","))
                .flatMap(Arrays::stream).filter(t -> !StringUtils.isEmpty(t)).collect(Collectors.toSet());
        List<RouteTemplate> routeTemplateList = !routeIds.isEmpty() ? routeTemplateService.listByIds(routeIds) : new LinkedList<>();
        final Map<Long,String> routeMap = routeTemplateList.stream().collect(Collectors.toMap(RouteTemplate::getId, RouteTemplate::getRouteNo));
        //格式化输出数据
        List<SchdAlternateAssignDTO> assignDTOList = iPage.getRecords().stream().map(s -> {
             SchdAlternateAssignDTO assignDTO = new SchdAlternateAssignDTO();
             BeanUtils.copyProperties(s,assignDTO);
             String employeeName = Optional.ofNullable(employeeMap.get(s.getEmployeeId())).orElse("");
             String planName = Optional.ofNullable(planMap.get(s.getPlanId())).orElse("");

            Stream<String> routeStream = Arrays.stream(s.getRouteIds().split(","));
             String routeNo = routeStream.map(Long::parseLong)
                     .map(t -> routeMap.get(t)).filter(m -> m != null).collect(Collectors.joining(","));
             assignDTO.setEmployeeName(employeeName);
             assignDTO.setPlanName(planName);
             assignDTO.setRouteNos(routeNo);
             return assignDTO;
            }).collect(Collectors.toList());

        ResultList resultList = new ResultList.Builder().total(iPage.getTotal()).list(assignDTOList).build();
        return Result.success(resultList);
    }
}

