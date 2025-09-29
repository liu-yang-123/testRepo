package com.zcxd.base.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zcxd.base.dto.CashboxPackInfoDTO;
import com.zcxd.base.dto.CashboxPackRecordDTO;
import com.zcxd.base.dto.CashboxScanRecordDTO;
import com.zcxd.base.service.ATMService;
import com.zcxd.base.service.BankService;
import com.zcxd.base.service.CashboxPackRecordService;
import com.zcxd.base.service.CashboxScanRecordService;
import com.zcxd.base.service.DenomService;
import com.zcxd.base.service.DeviceService;
import com.zcxd.base.service.EmployeeService;
import com.zcxd.base.service.RouteService;
import com.zcxd.common.util.Result;
import com.zcxd.common.util.ResultList;
import com.zcxd.db.model.AtmDevice;
import com.zcxd.db.model.Bank;
import com.zcxd.db.model.CashboxPackRecord;
import com.zcxd.db.model.CashboxScanRecord;
import com.zcxd.db.model.Denom;
import com.zcxd.db.model.Device;
import com.zcxd.db.model.Employee;
import com.zcxd.db.model.Route;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;

/**
 * 
 * @ClassName CashboxPackRecordController
 * @Description 装盒记录控制器
 * @author 秦江南
 * @Date 2021年8月26日下午4:34:22
 */
@RestController
@RequestMapping("/cashboxPack")
@Api(tags = "装盒记录控制器")
public class CashboxPackRecordController {
	@Autowired
	private CashboxPackRecordService cashboxPackRecordService;
	
	@Autowired
	private CashboxScanRecordService cashboxScanRecordService;

	@Autowired
	private EmployeeService employeeService;
	
	@Autowired
	private BankService bankService;
	
	@Autowired
	private ATMService atmService;
	
	@Autowired
	private RouteService routeService;
	
	@Autowired
	private DeviceService deviceService;
	
	@Autowired
	private DenomService denomService;
	
	@ApiOperation(value = "钞盒分页列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页数", required = true, dataType = "Integer ",defaultValue = "1"),
            @ApiImplicitParam(name = "limit", value = "显示数据条数", required = true, dataType = "Integer ",defaultValue = "10"),
			@ApiImplicitParam(name = "taskDate", value = "任务日期", required = false, dataType = "Long"),
			@ApiImplicitParam(name = "packTime", value = "清点时间", required = false, dataType = "Long"),
			@ApiImplicitParam(name = "bankId", value = "所属银行", required = false, dataType = "Long"),
			@ApiImplicitParam(name = "clearManId", value = "清点人", required = false, dataType = "Long"),
			@ApiImplicitParam(name = "statusT", value = "状态", required = false, dataType = "Integer"),
            @ApiImplicitParam(name = "boxNo", value = "钞盒编号", required = false, dataType = "String"),
    })
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @GetMapping("/list")
    public Result list(@RequestParam(defaultValue = "1") Integer page,
                                 @RequestParam(defaultValue = "10") Integer limit,
                                 @RequestParam(required=false) Long departmentId,
                                 @RequestParam(required=false)  Long taskDate,
                                 @RequestParam(required=false)  Long packTime,
                                 @RequestParam(required=false)  Long bankId,
                                 @RequestParam(required=false)  Long clearManId,
                                 @RequestParam(required=false) Integer denom,
                                 @RequestParam(required=false) Integer statusT,
                                 @RequestParam(required=false)  @Size(max=32,message="boxNo最大长度为32") String boxNo ){
		
		CashboxPackRecord cashboxPackRecord = new CashboxPackRecord();
		if(denom != null){
			Denom denomTmp = denomService.getOne((QueryWrapper)Wrappers.query().eq("cur_code", "CNY").eq("attr", "P").eq("version", 0).eq("value", new BigDecimal(denom)));
			cashboxPackRecord.setDenomId(denomTmp.getId());
		}
		cashboxPackRecord.setTaskDate(taskDate);
		cashboxPackRecord.setPackTime(packTime);
		cashboxPackRecord.setBankId(bankId);
		cashboxPackRecord.setClearManId(clearManId);
		cashboxPackRecord.setBoxNo(boxNo);
		cashboxPackRecord.setDepartmentId(departmentId);
		cashboxPackRecord.setStatusT(statusT);
		
    	IPage<CashboxPackRecord> cashboxPackPage = cashboxPackRecordService.findListByPage(page, limit, cashboxPackRecord);
    	
    	Set<Long> userIds = new HashSet<>();
    	Set<Long> deviceIds = new HashSet<>();
    	Set<Long> denomIds = new HashSet<>();
    	Set<Long> bankIds = new HashSet<>();
    	
    	
    	cashboxPackPage.getRecords().stream().forEach(cashboxPackTmp->{
    		userIds.add(cashboxPackTmp.getClearManId());
    		userIds.add(cashboxPackTmp.getCheckManId());
    		deviceIds.add(cashboxPackTmp.getDevId());
    		denomIds.add(cashboxPackTmp.getDenomId());
    		bankIds.add(cashboxPackTmp.getBankId());
    	});
    	
    	List<Employee> employeeList = new ArrayList<>();
    	if(userIds.size()>0){
    		employeeList = employeeService.listByIds(userIds);
    	}
    	Map<Long,String> employeeMap = employeeList.stream().collect(Collectors.toMap(Employee::getId,Employee::getEmpName));
    	
    	List<Device> deviceList = new ArrayList<>();
    	if(deviceIds.size()>0){
    		deviceList = deviceService.listByIds(deviceIds);
    	}
    	Map<Long,String> deviceMap = deviceList.stream().collect(Collectors.toMap(Device::getId,Device::getDeviceNo));
    	
    	List<Denom> denomList = new ArrayList<>();
    	if(denomIds.size()>0){
    		denomList = denomService.listByIds(denomIds);
    	}
    	Map<Long,String> denomMap = denomList.stream().collect(Collectors.toMap(Denom::getId,Denom::getName));
    	
    	List<Bank> bankList = new ArrayList<>();
    	if(bankIds.size()>0){
    		bankList = bankService.listByIds(bankIds);
    	}
    	Map<Long,String> bankMap = bankList.stream().collect(Collectors.toMap(Bank::getId,Bank::getShortName));
    	
    	
 		List<CashboxPackRecordDTO> cashboxPackRecordDTOList = cashboxPackPage.getRecords().stream().map(cashboxPackTmp->{
 	    	CashboxPackRecordDTO packRecordDTO = new CashboxPackRecordDTO();
 	    	packRecordDTO.setId(cashboxPackTmp.getId());
 	    	packRecordDTO.setPackTime(cashboxPackTmp.getPackTime());
 	    	packRecordDTO.setTaskDate(cashboxPackTmp.getTaskDate());
 	    	packRecordDTO.setClearManName(employeeMap.get(cashboxPackTmp.getClearManId()));
 	    	packRecordDTO.setCheckManName(employeeMap.get(cashboxPackTmp.getCheckManId()));
 	    	packRecordDTO.setBoxNo(cashboxPackTmp.getBoxNo());
 	    	packRecordDTO.setDeviceNo(deviceMap.get(cashboxPackTmp.getDevId()));
 	    	packRecordDTO.setDenomName(denomMap.get(cashboxPackTmp.getDenomId()));
 	    	packRecordDTO.setAmount(cashboxPackTmp.getAmount());
 	    	packRecordDTO.setStatusT(cashboxPackTmp.getStatusT());
 	    	packRecordDTO.setBankName(bankMap.get(cashboxPackTmp.getBankId()));
 	    	return packRecordDTO;
 		}).collect(Collectors.toList());
 		
    	ResultList resultList = new ResultList.Builder().total(cashboxPackPage.getTotal()).list(cashboxPackRecordDTOList).build();
        return Result.success(resultList);
    }
	
	@ApiOperation(value = "查询装盒记录详情")
    @ApiImplicitParam(name = "id", value = "装盒记录id", required = true, dataType = "Long")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @GetMapping("/info/{id}")
    public Result info(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){
		CashboxPackRecord cashboxPackRecord = cashboxPackRecordService.getById(id);
		if(cashboxPackRecord == null){
			return Result.fail("装盒记录不存在");
		}
		CashboxPackInfoDTO infoDTO = new CashboxPackInfoDTO();
		if(cashboxPackRecord.getRouteId() != 0){
			Route route = routeService.getById(cashboxPackRecord.getRouteId());
			infoDTO.setRouteNo(route.getRouteNo());
		}
		
		if(cashboxPackRecord.getAtmId() != 0){
			AtmDevice atmDevice = atmService.getById(cashboxPackRecord.getAtmId());
			infoDTO.setAtmTerNo(atmDevice.getTerNo());
		}
		
		if(cashboxPackRecord.getSecondAtmId() != 0){
			AtmDevice atmDevice = atmService.getById(cashboxPackRecord.getSecondAtmId());
			infoDTO.setSecondAtmTerNo(atmDevice.getTerNo());
		}
		
		List<CashboxScanRecord> scanRecords = cashboxScanRecordService.list((QueryWrapper)Wrappers.query().eq("pack_id", cashboxPackRecord.getId()));
		Set<Long> empIds = scanRecords.stream().map(CashboxScanRecord::getScanUser).collect(Collectors.toSet());
    	List<Employee> empList = new ArrayList<>();
    	if(empIds.size() != 0){
    		empList = employeeService.listByIds(empIds);
    	}
		Map<Long,String> empNameMap = empList.stream().collect(Collectors.toMap(Employee::getId,Employee::getEmpName));
		
		List<CashboxScanRecordDTO> recordDTOs = scanRecords.stream().map(scanRecord ->{
			CashboxScanRecordDTO cashboxScanRecordDTO = new CashboxScanRecordDTO();
			cashboxScanRecordDTO.setScanTime(scanRecord.getScanTime());
			cashboxScanRecordDTO.setScanNode(scanRecord.getScanNode());
			cashboxScanRecordDTO.setScanUser(empNameMap.get(scanRecord.getScanUser()));
			return cashboxScanRecordDTO;
		}).collect(Collectors.toList());
		infoDTO.setScans(recordDTOs);	
		return Result.success(infoDTO);
		
	}
	
}
