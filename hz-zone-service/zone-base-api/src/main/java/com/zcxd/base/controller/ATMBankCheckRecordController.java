package com.zcxd.base.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zcxd.base.dto.AtmBankCheckRecordDTO;
import com.zcxd.base.dto.BankInfoDTO;
import com.zcxd.base.service.ATMBankCheckRecordService;
import com.zcxd.base.service.BankService;
import com.zcxd.base.service.EmployeeService;
import com.zcxd.base.service.RouteService;
import com.zcxd.base.vo.BankVO;
import com.zcxd.common.annotation.OperateLog;
import com.zcxd.common.annotation.OperateType;
import com.zcxd.common.util.JacksonUtil;
import com.zcxd.common.util.Result;
import com.zcxd.common.util.TreeUtil;
import com.zcxd.db.model.AtmBankCheckRecord;
import com.zcxd.db.model.Bank;
import com.zcxd.db.model.Employee;
import com.zcxd.db.model.Route;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 
 * @ClassName ATMBankCheckRecordController
 * @Description 离行式网点巡检结果
 * @author shijin
 * @Date 2021年8月05日上午10:01:14
 */
@RestController
@RequestMapping("/bankCheck")
@Api(tags = "离行式网点巡检记录")
public class ATMBankCheckRecordController {
	@Autowired
    private ATMBankCheckRecordService atmBankCheckRecordService;
	@Autowired
    private BankService bankService;
	@Autowired
    private EmployeeService employeeService;


    @ApiOperation(value = "查询线路网点巡检记录")
    @ApiImplicitParam(name = "routeId", value = "线路id", required = true, dataType = "Long")
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @GetMapping("/list/route")
    public Result listByRoute(@RequestParam @Min(value=1, message="routeId不能小于1") Long routeId){
        AtmBankCheckRecord where = new AtmBankCheckRecord();
        where.setRouteId(routeId);
        List<AtmBankCheckRecord> list = atmBankCheckRecordService.list(new QueryWrapper<>(where));
        if (list.size() == 0) {
            return Result.success(new ArrayList<>());
        }

        Set<Long> bankIds = list.stream().map(AtmBankCheckRecord::getBankId).collect(Collectors.toSet());
        Set<Long> suBankIds = list.stream().map(AtmBankCheckRecord::getSubBankId).collect(Collectors.toSet());
        bankIds.addAll(suBankIds);

        List<Bank> bankList = bankService.listByIds(bankIds);
        Map<Long,Bank> bankMap = bankList.stream().collect(Collectors.toMap(Bank::getId,Function.identity(),(key1,key2) -> key2));

        List<AtmBankCheckRecordDTO> dtoList = list.stream().map(atmBankCheckRecord -> {
            AtmBankCheckRecordDTO recordDTO = new AtmBankCheckRecordDTO();
            BeanUtils.copyProperties(atmBankCheckRecord,recordDTO);
            Bank bank = bankMap.get(atmBankCheckRecord.getBankId());
            if (null != bank) {
                recordDTO.setBankName(bank.getFullName());
            }
            bank = bankMap.get(atmBankCheckRecord.getSubBankId());
            if (null != bank) {
                recordDTO.setSubBankName(bank.getFullName());
            }
            Map map = JSONObject.parseObject(atmBankCheckRecord.getRoomCheckResult());
            Map<String,Integer> resultMap = new HashMap<>();
            for (Object key : map.keySet()) {
                resultMap.put((String)key,(Integer) map.get(key));
            }
            recordDTO.setRoomCheckResults(resultMap);

            Map map2 = JSONObject.parseObject(atmBankCheckRecord.getHallCheckResult());
            Map<String,Integer> resultMap2 = new HashMap<>();
            for (Object key : map2.keySet()) {
                resultMap2.put((String)key,(Integer) map2.get(key));
            }
            recordDTO.setHallCheckResults(resultMap2);
            return recordDTO;
        }).collect(Collectors.toList());

    	return Result.success(dtoList);
    }

//    @ApiOperation(value = "查询网点巡检记录详情")
//    @ApiImplicitParam(name = "id", value = "记录id", required = true, dataType = "Long")
//    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
//    @PostMapping("/info/{id}")
//    public Result info(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){
//        AtmBankCheckRecord atmBankCheckRecord = atmBankCheckRecordService.getById(id);
//        if (null == atmBankCheckRecord) {
//            return Result.fail("无效id");
//        }
//        List<String> empIds = Arrays.asList(StringUtils.split(atmBankCheckRecord.getCheckMans()));
//        for (empIds )
//    	return Result.success(bankInfoDTO);
//    }
    

    
}
