package com.zcxd.base.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zcxd.base.dto.AtmAdditionalCashDTO;
import com.zcxd.base.service.AtmAdditionCashService;
import com.zcxd.base.service.BankService;
import com.zcxd.base.service.CommonService;
import com.zcxd.base.service.RouteService;
import com.zcxd.base.service.SysUserService;
import com.zcxd.base.service.VaultOrderService;
import com.zcxd.base.vo.AtmAdditionCashBatchVO;
import com.zcxd.base.vo.AtmAdditionCashVO;
import com.zcxd.base.vo.BatchIdsVO;
import com.zcxd.common.annotation.OperateLog;
import com.zcxd.common.annotation.OperateType;
import com.zcxd.common.constant.AtmAdditionCashStatusEnum;
import com.zcxd.common.util.DateTimeUtil;
import com.zcxd.common.util.Result;
import com.zcxd.common.util.ResultList;
import com.zcxd.db.model.AtmAdditionCash;
import com.zcxd.db.model.Bank;
import com.zcxd.db.model.Route;
import com.zcxd.db.model.SysUser;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;

/**
 * 
 * @ClassName AtmAdditionalCashController
 * @Description 备用金控制器
 * @author shijin
 * @Date 2021年8月11日上午9:31:38
 */
@RestController
@RequestMapping("/addition/cash")
@Api(tags = "线路备用金管理")
public class AtmAdditionCashController {
	@Autowired
    private AtmAdditionCashService atmAdditionCashService;
	@Autowired
    private RouteService routeService;
	@Autowired
    private BankService bankService;
	@Autowired
    private SysUserService sysUserService;
	@Autowired
    private CommonService commonService;
	@Autowired
	private VaultOrderService orderService;

	@ApiOperation(value = "查询备用金列表")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "page", value = "当前页数", required = true, dataType = "Integer ",defaultValue = "1"),
			@ApiImplicitParam(name = "limit", value = "显示数据条数", required = true, dataType = "Integer ",defaultValue = "10"),
			@ApiImplicitParam(name = "departmentId", value = "部门Id", required = true, dataType = "Long"),
			@ApiImplicitParam(name = "bankId", value = "银行Id", required = false, dataType = "Long"),
			@ApiImplicitParam(name = "taskDate", value = "任务日期", required = false, dataType = "Long"),
			@ApiImplicitParam(name = "routeId", value = "线路Id", required = false, dataType = "Long")
	})
	@ApiResponse(code = 200, message = "处理成功", response = Result.class)
	@GetMapping("/list")
	public Result listPage( @RequestParam(defaultValue = "1") Integer page,
							@RequestParam(defaultValue = "10") Integer limit,
							@RequestParam Long departmentId,
							@RequestParam(required=false)  Long bankId,
							@RequestParam(required=false)  Long taskDate,
							@RequestParam(required=false) Long routeId){
		AtmAdditionCash where = new AtmAdditionCash();
		where.setDepartmentId(departmentId);
		where.setBankId(bankId);
		if (taskDate != null) {
			String dateStr = DateTimeUtil.timeStampMs2Date(taskDate,"yyyy-MM-dd");
			where.setTaskDate(dateStr);
		}
		where.setRouteId(routeId);
		Page<AtmAdditionCash> ipage = atmAdditionCashService.findListByPage(page,limit,where);
		if (ipage.getRecords().size() == 0) {
			ResultList resultList = ResultList.builder().total(ipage.getTotal()).list(ipage.getRecords()).build();
			return Result.success(resultList);
		}
		Set<Long> routeIds = new HashSet<>();
		Set<Long> bankIds = new HashSet<>();
		Set<Long> userIds = new HashSet<>();
		ipage.getRecords().stream().forEach(atmAdditionCash -> {
			routeIds.add(atmAdditionCash.getRouteId());
			routeIds.add(atmAdditionCash.getCarryRouteId());
			bankIds.add(atmAdditionCash.getBankId());
			userIds.add(atmAdditionCash.getCreateUser());
		});
		List<Route> routeList = routeService.listByIds(routeIds);
		Map<Long,String> routeNameMap = routeList.stream().collect(Collectors.toMap(Route::getId,Route::getRouteNo));
		List<Bank> bankList = bankService.listByIds(bankIds);
		Map<Long,String> bankNameMap = bankList.stream().collect(Collectors.toMap(Bank::getId,Bank::getFullName));
		List<SysUser> userList = sysUserService.listByIds(userIds);
		Map<Long,String> userNameMap = userList.stream().collect(Collectors.toMap(SysUser::getId,SysUser::getUsername));

		List<AtmAdditionalCashDTO> additionalCashDTOS = ipage.getRecords().stream().map(atmAdditionalCash -> {
			AtmAdditionalCashDTO additionalCashDTO = new AtmAdditionalCashDTO();
			BeanUtils.copyProperties(atmAdditionalCash,additionalCashDTO);
			additionalCashDTO.setBankName(bankNameMap.getOrDefault(atmAdditionalCash.getBankId(),""));
			additionalCashDTO.setRouteNo(routeNameMap.getOrDefault(atmAdditionalCash.getRouteId(),""));
			additionalCashDTO.setCarryRouteNo(routeNameMap.getOrDefault(atmAdditionalCash.getCarryRouteId(),""));
			additionalCashDTO.setCreateUserName(userNameMap.getOrDefault(atmAdditionalCash.getCreateUser(),""));
			additionalCashDTO.setDenomName(atmAdditionalCash.getDenomValue()+"元");
			additionalCashDTO.setStatusText(AtmAdditionCashStatusEnum.getText(atmAdditionalCash.getStatusT()));
			return additionalCashDTO;
		}).collect(Collectors.toList());
		ResultList resultList = ResultList.builder().total(ipage.getTotal()).list(additionalCashDTOS).build();
		return Result.success(resultList);
	}

	@OperateLog(value="创建", type= OperateType.ADD)
	@ApiOperation(value = "新建")
	@ApiImplicitParam(name = "atmAdditionCashVO", value = "新建数据", required = true, dataType = "AtmAdditionCashVO")
	@PostMapping("/save")
	public Result save(@RequestBody AtmAdditionCashVO atmAdditionCashVO){
		Route route = routeService.getById(atmAdditionCashVO.getRouteId());
		if (null == route) {
			return Result.fail("无效线路");
		}
		AtmAdditionCash atmAdditionCash = new AtmAdditionCash();
		Map<String,Long> denomMap = commonService.getDenomMap();
		BeanUtils.copyProperties(atmAdditionCashVO,atmAdditionCash);
		atmAdditionCash.setTaskDate(DateTimeUtil.timeStampMs2Date(atmAdditionCashVO.getTaskDate(),"yyyy-MM-dd"));
		atmAdditionCash.setDenomId(denomMap.get(atmAdditionCashVO.getDenomValue()+""));
		atmAdditionCash.setDepartmentId(route.getDepartmentId());
		boolean bRet = atmAdditionCashService.save(atmAdditionCash);
		return bRet? Result.success() : Result.fail();
	}
	
	@OperateLog(value="批量添加", type= OperateType.ADD)
	@ApiOperation(value = "批量添加")
	@ApiImplicitParam(name = "atmAdditionCashBatchVO", value = "批量添加数据", required = true, dataType = "AtmAdditionCashBatchVO")
	@PostMapping("/saveBatch")
	public Result save(@RequestBody AtmAdditionCashBatchVO atmAdditionCashBatchVO){
		
		String[] routeIds = atmAdditionCashBatchVO.getRouteIds();
		
		Route route = routeService.getById(routeIds[0]);
		if (null == route) {
			return Result.fail("无效线路");
		}
		
		Map<String,Long> denomMap = commonService.getDenomMap();
		
		List<AtmAdditionCash> atmAdditionCashs = new ArrayList<AtmAdditionCash>();
		for (String routeIdStr : routeIds) {
			AtmAdditionCash atmAdditionCash = new AtmAdditionCash();
			BeanUtils.copyProperties(atmAdditionCashBatchVO,atmAdditionCash);
			atmAdditionCash.setTaskDate(DateTimeUtil.timeStampMs2Date(atmAdditionCashBatchVO.getTaskDate(),"yyyy-MM-dd"));
			atmAdditionCash.setDenomId(denomMap.get(atmAdditionCashBatchVO.getDenomValue()+""));
			Long routeId = Long.parseLong(routeIdStr);
			atmAdditionCash.setRouteId(routeId);
			atmAdditionCash.setCarryRouteId(routeId);
			atmAdditionCash.setDepartmentId(route.getDepartmentId());
			atmAdditionCashs.add(atmAdditionCash);
		}
		
		boolean bRet = atmAdditionCashService.saveBatch(atmAdditionCashs);
		return bRet? Result.success() : Result.fail();
	}

	@OperateLog(value="编辑", type= OperateType.EDIT)
	@ApiOperation(value = "编辑")
	@ApiImplicitParam(name = "atmAdditionCashVO", value = "编辑数据", required = true, dataType = "AtmAdditionCashVO")
	@PostMapping("/update")
	public Result update(@RequestBody AtmAdditionCashVO atmAdditionCashVO){
		AtmAdditionCash oldAtmAdditionCash = atmAdditionCashService.getById(atmAdditionCashVO.getId());
		if (null == oldAtmAdditionCash) {
			return Result.fail("无效id");
		}
		AtmAdditionCash newAtmAdditionalCash = new AtmAdditionCash();
		BeanUtils.copyProperties(atmAdditionCashVO,newAtmAdditionalCash);
		newAtmAdditionalCash.setTaskDate(DateTimeUtil.timeStampMs2Date(atmAdditionCashVO.getTaskDate(),"yyyy-MM-dd"));
		if (oldAtmAdditionCash.getDenomValue().equals(atmAdditionCashVO.getDenomValue())) {
			Map<String,Long> denomMap = commonService.getDenomMap();
			newAtmAdditionalCash.setDenomId(denomMap.get(oldAtmAdditionCash.getDenomValue()+""));
		}

		boolean bRet = atmAdditionCashService.updateById(newAtmAdditionalCash);
		return bRet? Result.success() : Result.fail();
	}

	@OperateLog(value="删除",type=OperateType.DELETE)
	@ApiOperation(value = "删除")
	@ApiImplicitParam(name = "id", value = "备用金记录id", required = true, dataType = "Long")
	@ApiResponse(code = 0, message = "处理成功", response = Result.class)
	@PostMapping("/delete/{id}")
	public Result delete(@PathVariable("id") Long id) {
		AtmAdditionCash oldAtmAdditionCash = atmAdditionCashService.getById(id);
		if (null == oldAtmAdditionCash) {
			return Result.fail("无效id");
		}
		AtmAdditionCash newAtmAdditionCash = new AtmAdditionCash();
		newAtmAdditionCash.setId(id);
		newAtmAdditionCash.setDeleted(1);
		boolean bRet = atmAdditionCashService.updateById(newAtmAdditionCash);
		return bRet? Result.success() : Result.fail();
	}

	@OperateLog(value = "确认", type=OperateType.EDIT)
	@ApiOperation(value = "确认")
	@ApiImplicitParam(name = "id", value = "备用金记录id", required = true, dataType = "Long")
	@ApiResponse(code = 200, message = "处理成功", response = Result.class)
	@PostMapping("/confirm/{id}")
	public Result confirm(@PathVariable("id")Long id){
		AtmAdditionCash atmAdditionCash = atmAdditionCashService.getById(id);
		if(!atmAdditionCash.getStatusT().equals(AtmAdditionCashStatusEnum.CREATE.getValue())){
			return Result.fail("无法确认");
		}
		AtmAdditionCash newAtmAdditionCash = new AtmAdditionCash();
		newAtmAdditionCash.setId(id);
		newAtmAdditionCash.setStatusT(AtmAdditionCashStatusEnum.CONFIRM.getValue());
		boolean bRet = atmAdditionCashService.updateById(newAtmAdditionCash);
		return bRet? Result.success() : Result.fail();
	}

	@OperateLog(value = "批量确认", type=OperateType.EDIT)
	@ApiOperation(value = "批量确认")
	@ApiImplicitParam(name = "batchIdsVO", value = "任务id", required = true, dataType = "BatchIdsVO")
	@ApiResponse(code = 200, message = "处理成功", response = Result.class)
	@PostMapping("/batchConfirm")
	public Result batchConfirm(@RequestBody BatchIdsVO batchIdsVO){
		boolean bRet = true;
		if (batchIdsVO.getIds().size() > 0) {
			List<AtmAdditionCash> additionalCashes = new ArrayList<>();
			for (Long id : batchIdsVO.getIds()) {
				AtmAdditionCash atmAdditionalCash = new AtmAdditionCash();
				atmAdditionalCash.setId(id);
				atmAdditionalCash.setStatusT(AtmAdditionCashStatusEnum.CONFIRM.getValue());
				additionalCashes.add(atmAdditionalCash);
			}
			bRet = atmAdditionCashService.updateBatchById(additionalCashes);
		}
		return bRet? Result.success() : Result.fail();
	}

	@OperateLog(value = "快捷创建出库单", type=OperateType.EDIT)
	@ApiOperation(value = "批量确认")
	@ApiImplicitParam(name = "batchIdsVO", value = "任务id", required = true, dataType = "BatchIdsVO")
	@PostMapping("/quickOut")
	public Result quickOut(@RequestBody BatchIdsVO batchIdsVO){
       boolean b = orderService.quickCashOut(batchIdsVO);
       return b ? Result.success("快捷出库单已创建") : Result.fail("快捷出库单创建失败");
	}

	@OperateLog(value = "撤销", type=OperateType.EDIT)
	@ApiOperation(value = "撤销")
	@ApiImplicitParam(name = "id", value = "备用金记录id", required = true, dataType = "Long")
	@ApiResponse(code = 200, message = "处理成功", response = Result.class)
	@PostMapping("/cancel/{id}")
	public Result cancel(@PathVariable("id")Long id){
		AtmAdditionCash newAtmAdditionCash = new AtmAdditionCash();
		newAtmAdditionCash.setId(id);
		newAtmAdditionCash.setStatusT(AtmAdditionCashStatusEnum.CANCEL.getValue());
		boolean bRet = atmAdditionCashService.updateById(newAtmAdditionCash);
		return bRet? Result.success() : Result.fail();
	}


}
