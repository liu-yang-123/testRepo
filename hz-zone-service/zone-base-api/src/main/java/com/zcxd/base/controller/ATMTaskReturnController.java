package com.zcxd.base.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zcxd.base.dto.AtmTaskReturnDTO;
import com.zcxd.base.service.ATMService;
import com.zcxd.base.service.ATMTaskReturnService;
import com.zcxd.base.service.BankService;
import com.zcxd.base.service.RouteService;
import com.zcxd.common.constant.CarryTypeEnum;
import com.zcxd.common.util.Result;
import com.zcxd.common.util.ResultList;
import com.zcxd.db.model.AtmDevice;
import com.zcxd.db.model.AtmTaskReturn;
import com.zcxd.db.model.Bank;
import com.zcxd.db.model.Route;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 清机回笼信息
 * @ClassName ATMTaskReturnController
 * @Description TODO
 * @author 秦江南
 * @Date 2023年2月10日下午4:52:37
 */
@RestController
@RequestMapping("/taskReturn")
@Api(tags = "清机回笼信息")
public class ATMTaskReturnController {
	
    @Autowired
    private ATMTaskReturnService taskReturnService;
    
    @Autowired
    private RouteService routeService;
    
    @Autowired
    private BankService bankService;
    
    @Autowired
    private ATMService atmService;
	
	@ApiOperation(value = "查询清机回笼信息")
    @GetMapping("/list")
    public Result list(@RequestParam(defaultValue = "1") Integer page,
			   @RequestParam(defaultValue = "10") Integer limit,
			   @RequestParam Long departmentId,
			   String boxBarCode){
		
		//获取分页原始数据
		List<AtmTaskReturn> taskReturns = taskReturnService.findListByPage(page, limit,departmentId,boxBarCode);

		//封装返回显示数据
		List<AtmTaskReturnDTO> taskReturnDTOs = new ArrayList<AtmTaskReturnDTO>();
		if(taskReturns.size() > 0){
			//获取线路信息
    		Set<Long> routeIds = taskReturns.stream().map(AtmTaskReturn::getRouteId).collect(Collectors.toSet());
    		Map<Long,String>  routeMap = new HashMap<>();
            List<Route> routeList = routeService.listByIds(routeIds);
            routeMap = routeList.stream().collect(Collectors.toMap(Route::getId,Route::getRouteName));
            final Map<Long,String>  finalRouteMap = routeMap;
            
            //获取网点信息
    		Set<Long> bankIds = taskReturns.stream().map(AtmTaskReturn::getBankId).collect(Collectors.toSet());
    		Map<Long,String>  bankMap = new HashMap<>();
            List<Bank> bankList = bankService.listByIds(bankIds);
            bankMap = bankList.stream().collect(Collectors.toMap(Bank::getId,Bank::getShortName));
            final Map<Long,String>  finalBankeMap = bankMap;
            
            //获取设备信息
    		Set<Long> atmIds = taskReturns.stream().map(AtmTaskReturn::getAtmId).collect(Collectors.toSet());
    		Map<Long,String>  atmMap = new HashMap<>();
            List<AtmDevice> atmList = atmService.listByIds(atmIds);
            atmMap = atmList.stream().collect(Collectors.toMap(AtmDevice::getId,AtmDevice::getTerNo));
            final Map<Long,String>  finalAtmMap = atmMap;
		
			//封装返回显示数据
			taskReturnDTOs = taskReturns.stream().map(taskReturn -> {
				AtmTaskReturnDTO taskReturnDTO = new AtmTaskReturnDTO();
				BeanUtils.copyProperties(taskReturn, taskReturnDTO);
				taskReturnDTO.setCarryType(CarryTypeEnum.getText(taskReturn.getCarryType()));
				taskReturnDTO.setRouteName(finalRouteMap.get(taskReturn.getRouteId()));
				taskReturnDTO.setBankName(finalBankeMap.get(taskReturn.getBankId()));
				taskReturnDTO.setTerNo(finalAtmMap.get(taskReturn.getAtmId()));
				return taskReturnDTO;
			}).collect(Collectors.toList());
		}
    	ResultList resultList = new ResultList.Builder().total(taskReturnDTOs.size()).list(taskReturnDTOs).build();
        return Result.success(resultList);
    }
}
