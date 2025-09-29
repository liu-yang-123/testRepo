package com.zcxd.base.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zcxd.base.dto.EmployeeDTO;
import com.zcxd.base.dto.SchdCondutorDTO;
import com.zcxd.base.service.EmployeeJobService;
import com.zcxd.base.service.EmployeeService;
import com.zcxd.base.vo.SchdCleanPassCodeVO;
import com.zcxd.common.annotation.OperateLog;
import com.zcxd.common.annotation.OperateType;
import com.zcxd.common.constant.JobTypeEnum;
import com.zcxd.common.util.Result;
import com.zcxd.common.util.ResultList;
import com.zcxd.db.model.Employee;
import com.zcxd.db.model.EmployeeJob;
import io.swagger.annotations.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 车长资格管理 前端控制器
 * </p>
 *
 * @author admin
 * @since 2021-07-07
 */
@RestController
@RequestMapping("/schdConductor")
@Api(tags="车长资格管理")
public class SchdConductorController {
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private EmployeeJobService employeeJobService;

    @ApiOperation(value = "查询列表信息-分页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页数", required = true, dataType = "Integer ",defaultValue = "1"),
            @ApiImplicitParam(name = "limit", value = "显示数据条数", required = true, dataType = "Integer ",defaultValue = "10"),
            @ApiImplicitParam(name = "empName", value = "员工姓名", required = false, dataType = "String"),
            @ApiImplicitParam(name = "departmentId", value = "部门", required = true, dataType = "Long ",defaultValue = "10"),
    })
    @GetMapping("/list")
    public Result list(@RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       String empName,
                       Integer leader,
                       @RequestParam(defaultValue = "10")Long departmentId) {
        SchdCondutorDTO schdCondutorDTO=new SchdCondutorDTO();
        schdCondutorDTO.setEmpName(empName);
        schdCondutorDTO.setRouteLeader(leader);
        schdCondutorDTO.setDepartmentId(departmentId);
        //
        List<Integer> jobTypeList = Arrays.asList(JobTypeEnum.KEY.getValue(),JobTypeEnum.CLEAN.getValue());
        IPage<Employee> iPage = employeeService.listSchdConductorPage(page,limit,schdCondutorDTO,jobTypeList);
        List<EmployeeJob> jobList = employeeJobService.list();
        //获取密码岗跟钥匙岗

        Map<Long,List<EmployeeJob>> jobMap=jobList.stream().filter(item->item.getJobType().equals(JobTypeEnum.KEY.getValue())||
                item.getJobType().equals(JobTypeEnum.CLEAN.getValue())).collect(Collectors.groupingBy(EmployeeJob::getId));

        List<SchdCondutorDTO> condutorDTOList = iPage.getRecords().stream().map(item->{
            SchdCondutorDTO condutorDTO =new SchdCondutorDTO();
            BeanUtils.copyProperties(item, condutorDTO);
            condutorDTO.setJobName(jobMap.get(item.getJobIds()).get(0).getName());
            condutorDTO.setDepartmentId(item.getDepartmentId());
            return condutorDTO;
        }).collect(Collectors.toList());

        ResultList resultList = ResultList.builder().total(iPage.getTotal()).list(condutorDTOList).build();
        return Result.success(resultList);
    }

    @OperateLog(value="车长资格授权",type= OperateType.EDIT)
    @ApiOperation(value = "车长资格授权")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "routeLeader", value = "车长资格", required = true, dataType = "Integer ",defaultValue = "1"),
            @ApiImplicitParam(name = "id", value = "唯一编号", required = true, dataType = "Long ",defaultValue = "1"),
    })
    @PostMapping("/update")
    public Result update(@RequestParam(defaultValue = "1") Long id,@RequestParam(defaultValue = "1") Integer routeLeader) {
        Employee employee=employeeService.getById(id);
        if(employee==null){
            return Result.fail("不存在的人员信息");
        }
        employee.setRouteLeader(routeLeader);
        employeeService.updateById(employee);
        return Result.success("操作成功");
    }


}