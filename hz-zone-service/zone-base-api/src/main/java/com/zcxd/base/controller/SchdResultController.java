package com.zcxd.base.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zcxd.base.config.UserContextHolder;
import com.zcxd.base.dto.*;
import com.zcxd.base.service.*;
import com.zcxd.base.util.ExcelUtil;
import com.zcxd.base.vo.SchdResultChangeVO;
import com.zcxd.base.vo.excel.SchdResultExcelVO;
import com.zcxd.common.annotation.OperateLog;
import com.zcxd.common.annotation.OperateType;
import com.zcxd.common.util.DateTimeUtil;
import com.zcxd.common.util.Result;
import com.zcxd.common.util.ResultList;
import com.zcxd.common.util.SecurityUtils;
import com.zcxd.db.model.*;
import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 排班结果 前端控制器
 * </p>
 *
 * @author admin
 * @since 2021-07-05
 */
@AllArgsConstructor
@RestController
@RequestMapping("/schdResult")
@Api(tags="排班管理-排班结果管理")
public class SchdResultController {

    private SchdResultService schdResultService;

    private EmployeeService employeeService;

    private SchdResultServiceImpl resultServiceImpl;

    private SchdCleanPassAuthService authService;

    private SchdResultRecordService recordService;

    private SysUserService userService;

    @PostMapping("/findByIds")
    public Result<List<SchdResult>> findByIds(@RequestParam("ids") List<Long> ids,
                                              @RequestParam("departmentId") Long departmentId) {
        List<SchdResult> schdResults = schdResultService.listByIds(ids, departmentId);
        return schdResults.isEmpty() ? Result.fail() : Result.success(schdResults);
    }

    @ApiOperation(value = "查询排班结果信息-分页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页数", required = true, dataType = "Integer ",defaultValue = "1"),
            @ApiImplicitParam(name = "limit", value = "显示数据条数", required = true, dataType = "Integer ",defaultValue = "10"),
            @ApiImplicitParam(name = "empId", value = "员工ID", dataType = "Long", dataTypeClass = Long.class),
            @ApiImplicitParam(name = "departmentId", value = "部门", required = true, dataType = "Long ", dataTypeClass = Long.class),
            @ApiImplicitParam(name = "planDay", value = "日期", dataType = "Long", dataTypeClass = Long.class),
            @ApiImplicitParam(name = "routeNo", value = "线路", dataType = "String", dataTypeClass = String.class),
    })
    @GetMapping("/list")
    public Result list(@RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Validated SchdResultRowDTO schdResultDTO) {
        IPage<SchdResult> schdResultList = schdResultService.listResult(page,limit,schdResultDTO);
        Map<Long,String> employeeMap = getEmployeeMap();
        List<SchdResultDTO>  resultDtoList=schdResultList.getRecords().stream().map(item->{
            SchdResultDTO resultDTO=new SchdResultDTO();
            BeanUtils.copyProperties(item, resultDTO);
            resultDTO.setDriverName(Optional.ofNullable(employeeMap.get(item.getDriver())).orElse(""));
            resultDTO.setKeyManName(Optional.ofNullable(employeeMap.get(item.getKeyMan())).orElse(""));
            resultDTO.setLeaderName(Optional.ofNullable(employeeMap.get(item.getLeader())).orElse(""));
            resultDTO.setScurityAName(Optional.ofNullable(employeeMap.get(item.getScurityA())).orElse(""));
            resultDTO.setScurityBName(Optional.ofNullable(employeeMap.get(item.getScurityB())).orElse(""));
            resultDTO.setOpManName(Optional.ofNullable(employeeMap.get(item.getOpMan())).orElse(""));
            resultDTO.setKeyManCodeList(authService.getAuthList(item.getKeyMan()));
            resultDTO.setOpManCodeList(authService.getAuthList(item.getOpMan()));
            return resultDTO;
        }).collect(Collectors.toList());
        ResultList resultList = ResultList.builder().total(schdResultList.getTotal()).list(resultDtoList).build();
        return Result.success(resultList);
    }

    @OperateLog(value="手动调班", type= OperateType.EDIT)
    @ApiOperation(value = "手动调班")
    @PostMapping("/update")
    public Result update(@RequestBody @Validated SchdResultInfoDTO schdResultInfoDTO){
        SchdResult schdResult = schdResultService.getById(schdResultInfoDTO.getId());
        if(!schdResultInfoDTO.getRouteNo().equals(schdResult.getRouteNo())){
            return Result.fail("不允许修改线路");
        }
        //验证选择数据是否在其他线路中出现
//        schdResultService.check(schdResultInfoDTO,schdResult);

        BeanUtils.copyProperties(schdResultInfoDTO, schdResult);
        schdResult.setUpdateTime(DateTimeUtil.getCurrentTimeStampMs());
        schdResult.setUpdateUser(UserContextHolder.getUserId());
        schdResultService.updateById(schdResult);

        //插入/更新排班结果记录表
        SchdResultRecord record = new SchdResultRecord();
        record.setPlanDay(schdResult.getPlanDay());
        record.setDepartmentId(schdResult.getDepartmentId());
        record.setCreateTime(System.currentTimeMillis());
        record.setCategory(1);
        record.setContent("修改线路【"+schdResult.getRouteNo()+"】数据");
        record.setOpType(0);
        long userId = UserContextHolder.getUserId();
        record.setCreateUser(userId);
        recordService.save(record);

        return Result.success("操作成功");
    }

    @OperateLog(value="发起排班", type= OperateType.ADD)
    @ApiOperation(value = "发起排班")
    @ApiImplicitParam(name = "planDay", value = "排班时间戳", required = true, dataType = "Long", dataTypeClass = Long.class)
    @PostMapping("/save")
    public Result save(@RequestParam Long planDay, @RequestParam Long departmentId){
        boolean b = resultServiceImpl.makeResult(departmentId,planDay,1);
        return b ? Result.success("发起排班成功") : Result.fail("发起排班失败");
    }

    /**
     * @Description:返回人员Map
     * @Author: lilanglang
     * @Date: 2021/7/7 20:44
     **/
    public Map<Long,String> getEmployeeMap(){
        Map<Long,String> employeeMap;
        List<Employee> empList = employeeService.list();
        employeeMap = empList.stream().collect(Collectors.toMap(Employee::getId,Employee::getEmpName));
        return employeeMap;
    }

    @ApiOperation(value = "切换线路")
    @PostMapping("/change")
    public Result change(@RequestBody List<SchdResultChangeVO> changeVOList){
        boolean b = schdResultService.change(changeVOList);
        return b ? Result.success("线路人员切换成功") : Result.fail("线路人员切换失败");
    }

    @ApiOperation(value = "排班信息导出", notes = "排班信息导出")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "empId", value = "员工ID",dataType = "Long", dataTypeClass = Long.class),
            @ApiImplicitParam(name = "departmentId", value = "部门", required = true, dataType = "Long",dataTypeClass = Long.class),
            @ApiImplicitParam(name = "planDay", value = "日期", dataType = "Long", dataTypeClass = Long.class),
            @ApiImplicitParam(name = "routeNo", value = "线路", dataType = "String", dataTypeClass = String.class),
    })
    @GetMapping("/download")
    public void  download(@Validated SchdResultRowDTO schdResultDTO, HttpServletResponse response) throws IOException {
        List<SchdResult> schdResultList = schdResultService.getResultList(schdResultDTO);
        //excel基础数据设置
        ExcelUtil excelUtil = new ExcelUtil();
        ExcelWriter excelWriter = excelUtil.getExcelWriter("线路排班信息数据",response);
        //创建excel sheet
        WriteSheet writeSheet = EasyExcel.writerSheet("线路排班信息数据").head(SchdResultExcelDTO.class).build();
        Map<Long,String> employeeMap = getEmployeeMap();

        Map<Long, String> finalEmployeeMap = employeeMap;
        List<SchdResultExcelDTO> resultDtoList = schdResultList.stream().map(item->{
            SchdResultExcelDTO excelDto = new SchdResultExcelDTO();
            BeanUtils.copyProperties(item, excelDto);
            excelDto.setPlanDays(DateTimeUtil.timeStampMs2Date(item.getPlanDay(),"yyyy-MM-dd"));
            String keyManCode = authService.getAuthList(item.getKeyMan());
            String opManCode = authService.getAuthList(item.getOpMan());
            String keyManName = Optional.ofNullable(finalEmployeeMap.get(item.getKeyMan())).orElse("");
            String opManName = Optional.ofNullable(finalEmployeeMap.get(item.getOpMan())).orElse("");
            excelDto.setDriverName(Optional.ofNullable(finalEmployeeMap.get(item.getDriver())).orElse(""));
            excelDto.setKeyManName( keyManCode != null ? keyManName +"/"+keyManCode : keyManName);
            excelDto.setLeaderName(Optional.ofNullable(finalEmployeeMap.get(item.getLeader())).orElse(""));
            excelDto.setScurityAName(Optional.ofNullable(finalEmployeeMap.get(item.getScurityA())).orElse(""));
            excelDto.setScurityBName(Optional.ofNullable(finalEmployeeMap.get(item.getScurityB())).orElse(""));
            excelDto.setOpManName(opManCode != null ? opManName + "/" + opManCode : opManName);
            return excelDto;
        }).collect(Collectors.toList());
        excelWriter.write(resultDtoList, writeSheet);
        //关闭流
        if (excelWriter != null){
            excelWriter.finish();
        }
    }

    @ApiOperation(value = "排班记录信息-分页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页数", required = true, dataType = "Integer",defaultValue = "1",dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "limit", value = "显示数据条数", required = true, dataType = "Integer",defaultValue = "10",dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "departmentId", value = "部门", required = true, dataType = "Long", dataTypeClass = Long.class)
    })
    @GetMapping("/record")
    public Result record(@RequestParam(defaultValue = "1") Integer page,
                         @RequestParam(defaultValue = "10") Integer limit,
                         @RequestParam(defaultValue = "0") Long departmentId){
        IPage<SchdResultRecord> recordIPage = recordService.findListByPage(page, limit, departmentId);
        long total = recordIPage.getTotal();

        //获取用户ID集合
        Set<Long> ids = recordIPage.getRecords().stream().filter(r -> r.getCreateUser() > 0)
                .map(SchdResultRecord::getCreateUser).collect(Collectors.toSet());
        Map<Long,String>  userMap = new HashMap<>();
        if (ids != null && ids.size() > 0){
            List<SysUser> userList = userService.listByIds(ids);
            userMap = userList.stream().collect(Collectors.toMap(SysUser::getId,SysUser::getNickName));
        }
        Map<Long, String> finalUserMap = userMap;
        List<SchdResultRecordDTO> recordDTOList = recordIPage.getRecords().stream().map(item -> {
            SchdResultRecordDTO recordDTO = new SchdResultRecordDTO();
            BeanUtils.copyProperties(item, recordDTO);
            String userName = item.getCreateUser() > 0 ? Optional.ofNullable(finalUserMap.get(item.getCreateUser())).orElse("") : "系统用户";
            recordDTO.setCreateUserName(userName);
            return recordDTO;
        }).collect(Collectors.toList());
        ResultList resultList = ResultList.builder().total(total).list(recordDTOList).build();
        return Result.success(resultList);
    }

    @ApiOperation(value = "排班日期检查")
    @ApiImplicitParam(name = "planDay", value = "排班时间戳", required = true, dataType = "Long", dataTypeClass = Long.class)
    @GetMapping("/checkTime")
    public Result checkTime(@RequestParam Long planDay){
        boolean b = recordService.checkTime(planDay);
        HashMap map = new HashMap();
        map.put("isExist", b ? 1 : 0);
        return Result.success(map);
    }

    @ApiOperation(value = "银行排班信息导出", notes = "银行排班信息导出")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "departmentId", value = "部门", required = true, dataType = "Long ",dataTypeClass = Long.class),
            @ApiImplicitParam(name = "planDay", value = "日期", dataType = "Long", dataTypeClass = Long.class),
    })
    @GetMapping("/export")
    public void export(@RequestParam Long departmentId, @RequestParam Long planDay,HttpServletResponse response) throws IOException{
        ClassPathResource resource = new ClassPathResource("doc/template.xlsx");
        InputStream templateFileStream = resource.getInputStream();
        //excel基础数据设置
        ExcelUtil excelUtil = new ExcelUtil();
        ExcelWriter excelWriter = excelUtil.getTemplateWriter("线路排班信息数据",templateFileStream,response);

        SchdResultRowDTO searchDTO = new SchdResultRowDTO();
        searchDTO.setDepartmentId(departmentId);
        searchDTO.setPlanDay(planDay);

        List<SchdResult> resultList = schdResultService.getResultList(searchDTO);
        Set<Long> empIds = new HashSet<>();
        resultList.stream().forEach(m -> {
            empIds.add(m.getKeyMan());
            empIds.add(m.getOpMan());
        });
        List<Employee> employeeList = empIds.size() > 0 ? employeeService.listByIds(empIds) : new LinkedList<>();
        Map<Long,Employee> employeeMap = employeeList.stream().collect(Collectors.toMap(Employee::getId,v -> v));
        List<SchdResultExcelVO> excelVOList = resultList.stream().map(item->{
            SchdResultExcelVO excelVO = new SchdResultExcelVO();
            Employee keyEmployee = employeeMap.get(item.getKeyMan());
            Employee opEmployee = employeeMap.get(item.getOpMan());
            Employee leaderEmployee = employeeMap.get(item.getLeader());

            String keyManCode = authService.getAuthList(item.getKeyMan());
            String opManCode = authService.getAuthList(item.getOpMan());

            String keyManName = keyEmployee != null ? keyEmployee.getEmpName() : "";
            String opManName = opEmployee != null ? opEmployee.getEmpName() : "";
            String leader = leaderEmployee != null ? leaderEmployee.getEmpName() : "";
            String phoneA = keyEmployee != null ? SecurityUtils.decryptAES(keyEmployee.getMobile()) : "";
            String phoneB = opEmployee != null ? SecurityUtils.decryptAES(opEmployee.getMobile()) : "";
            excelVO.setRouteNo(item.getRouteNo() + "号线");
            excelVO.setUserA(keyManName);
            excelVO.setPassportA(keyManCode);
            excelVO.setPhoneA(phoneA);
            excelVO.setUserB(opManName);
            excelVO.setPassportB(opManCode);
            excelVO.setPhoneB(phoneB);
            excelVO.setCarNo(item.getVehicleNo());
            excelVO.setLeader(leader);
            return excelVO;
        }).collect(Collectors.toList());
        //请求数据
        WriteSheet writeSheet = EasyExcel.writerSheet().build();
        FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.TRUE).build();

        excelWriter.fill(excelVOList, fillConfig, writeSheet);

        LocalDate localDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(searchDTO.getPlanDay()), ZoneOffset.of("+8")).toLocalDate();
        Map<String, String> map = new HashMap<>();
        map.put("year",localDate.getYear() +"年");
        map.put("month",localDate.getMonthValue() +"月");
        map.put("date", localDate.getDayOfMonth()+"日");
        excelWriter.fill(map, writeSheet);
        excelWriter.finish();
    }
    
    @OperateLog(value = "导入排班", type=OperateType.EDIT)
    @ApiOperation(value = "导入排班")
    @ApiImplicitParams({
	    @ApiImplicitParam(name = "file", value = "文件", required = true, dataType = "MultipartFile"),
	    @ApiImplicitParam(name = "planDay", value = "排班日期", required = true, dataType = "Long"),
	    @ApiImplicitParam(name = "departmentId", value = "部门", required = true, dataType = "Long")
    })
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/importSchd")
    public Result importSchd(MultipartFile file,@RequestParam Long planDay,@RequestParam Long departmentId) {
    	if(file == null){
    		return Result.fail("文件不存在");
    	}

    	return schdResultService.importSchd(planDay, departmentId, file);
    }
}

