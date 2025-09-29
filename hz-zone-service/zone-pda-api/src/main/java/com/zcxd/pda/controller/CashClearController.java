package com.zcxd.pda.controller;

import com.zcxd.common.util.DateTimeUtil;
import com.zcxd.common.util.Result;
import com.zcxd.pda.dto.*;
import com.zcxd.pda.service.CashClearService;
import com.zcxd.pda.vo.BatchBoxNosVO;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 
 * @ClassName SysUserController
 * @Description 用户管理控制器
 * @author shijin
 * @Date 2021年5月1日上午11:19:03
 */
@RequestMapping("/clear")
@RestController
@Slf4j
@Api(tags = "清分管理")
public class CashClearController {

    @Autowired
    private CashClearService cashClearService;


    @ApiOperation(value = "扫码查询清分任务列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "boxNo", value = "钞盒钞袋编号", required = true, dataType = "String"),
        @ApiImplicitParam(name = "taskDate", value = "任务日期", required = true, dataType = "Long")
    })
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @PostMapping("/task/list")
    public Result listBankClearTask(@RequestParam String boxNo,@RequestParam(required = false) Long taskDate) {
        String taskDay = DateTimeUtil.timeStampMs2Date(System.currentTimeMillis(),"yyyy-MM-dd");
        if (null != taskDate) {
            try {
                taskDay = DateTimeUtil.timeStampMs2Date(taskDate,"yyyy-MM-dd");
            } catch (Exception e) {
                log.error(e.getMessage(),e);
                return Result.fail("无效参数");
            }
        }
        AtmClearTaskRouteDTO clearTaskRouteDTO = cashClearService.listRouteClearTask(boxNo,taskDay);
        return Result.success(clearTaskRouteDTO);
    }


    @ApiOperation(value = "扫码查询清分任务")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "boxBarCode", value = "钞盒钞袋编号", required = false, dataType = "String"),
            @ApiImplicitParam(name = "id", value = "任务id", required = false, dataType = "String"),
            @ApiImplicitParam(name = "routeId", value = "线路id", required = false, dataType = "Long"),
            @ApiImplicitParam(name = "taskDate", value = "任务日期", required = false, dataType = "Long")
    })
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @PostMapping("/task/detail")
    public Result getClearTaskDetail(@RequestParam(required = false) String boxBarCode,
                                     @RequestParam(required = false) Long id,
                                     @RequestParam(required = false) Long routeId,
                                     @RequestParam(required = false) Long taskDate) {
        if (StringUtils.isEmpty(boxBarCode) && (id == null || id == 0L)) {
            String errmsg = "缺少参数";
            log.error(errmsg);
            return Result.fail(errmsg);
        }
        AtmClearTaskDTO atmClearTaskDTO;
        if (!StringUtils.isEmpty(boxBarCode)) {
            String taskDay = DateTimeUtil.timeStampMs2Date(System.currentTimeMillis(),"yyyy-MM-dd");
            if (null != taskDate) {
                try {
                    taskDay = DateTimeUtil.timeStampMs2Date(taskDate,"yyyy-MM-dd");
                } catch (Exception e) {
                    log.error(e.getMessage(),e);
                    return Result.fail("无效参数");
                }
            }
            atmClearTaskDTO = cashClearService.getClearTaskDetail(boxBarCode,taskDay);
        } else {
            atmClearTaskDTO = cashClearService.getClearTaskDetail(id);
        }
        return Result.success(atmClearTaskDTO);
    }

    @ApiOperation(value = "保存ATM清分结果")
    @ApiImplicitParam(name = "atmClearResultDTO", value = "atm清分结果数据", required = true, dataType = "AtmClearResultDTO")
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @PostMapping("/task/result/save")
    public Result saveAtmClearResult(@RequestBody AtmClearResultDTO atmClearResultDTO) {
        cashClearService.saveAtmClearResult(atmClearResultDTO);
        return Result.success();
    }

    @ApiOperation(value = "绑定清分钞盒")
    @ApiImplicitParam(name = "cashboxClearDTO", value = "atm清分结果数据", required = true, dataType = "CashboxClearDTO")
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @PostMapping("/cashbox/mapping")
    public Result saveClearCashboxMapping(@RequestBody CashboxClearDTO cashboxClearDTO) {
        cashClearService.bindCashbox(cashboxClearDTO);
        return Result.success();
    }

    @ApiOperation(value = "归还原装钞盒登记")
    @ApiImplicitParam(name = "batchBoxNosVO", value = "批量钞盒列表", required = true, dataType = "BatchBoxNosVO")
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @PostMapping("/cashbox/org/return")
    public Result returnOriginalPackingCashbox(@RequestBody BatchBoxNosVO batchBoxNosVO) {
        cashClearService.orgPackingBoxReturn(batchBoxNosVO);
        return Result.success();
    }

    @ApiOperation(value = "解绑清分钞盒")
    @ApiImplicitParam(name = "batchBoxNosVO", value = "批量钞盒列表", required = true, dataType = "BatchBoxNosVO")
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @PostMapping("/cashbox/unbind")
    public Result unbindClearCashbox(@RequestBody BatchBoxNosVO batchBoxNosVO) {
        cashClearService.unBindCashbox(batchBoxNosVO.getBoxNos());
        return Result.success();
    }

    @ApiOperation(value = "查询清分券别列表")
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @PostMapping("/denom/list")
    public Result queryDenomList() {
        AtmClearDenomDTO atmClearDenomDTO = cashClearService.queryDenomList();
        return Result.success(atmClearDenomDTO);
    }

    @ApiOperation(value = "查询银行列表")
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @PostMapping("/bank/list")
    public Result listBank() {
        List<AtmClearBankDTO> clearBankDTOList = cashClearService.listClearBank();
        return Result.success(clearBankDTOList);
    }

    @ApiOperation(value = "统计我的清分金额")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "beginTime", value = "查询开始时间", required = false, dataType = "Long"),
            @ApiImplicitParam(name = "endTime", value = "查询结束时间", required = false, dataType = "Long")
    })
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @PostMapping("/my/total")
    public Result myTotal(@RequestParam(required = false) Long beginTime,@RequestParam(required = false) Long endTime) {
        AtmClearTotalDTO atmClearTotalDTO = cashClearService.calcMyClearTotalByRoute(beginTime,endTime);
        return Result.success(atmClearTotalDTO);
    }


//    @ApiOperation(value = "查询我当日绑定钞盒")
//    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
//    @PostMapping("/cashbox/mylist")
//    public Result myCashboxList() {
//        Long taskDate = DateTimeUtil.getDailyStartTimeMs(System.currentTimeMillis());
//        List<String> boxList = cashClearService.queryMyCashboxList(taskDate);
//        return Result.success(boxList);
//    }

    @ApiOperation(value = "查询所有绑定钞盒")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "beginTime", value = "查询开始时间", required = false, dataType = "Long"),
            @ApiImplicitParam(name = "endTime", value = "查询结束时间", required = false, dataType = "Long"),
            @ApiImplicitParam(name = "bankId", value = "查询银行", required = false, dataType = "Long")
    })
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @PostMapping("/cashbox/bindlist")
    public Result queryBindCashboxList(@RequestParam(required = false) Long beginTime,
                                       @RequestParam(required = false) Long endTime,
                                       @RequestParam(required = false) Long bankId) {
        Long beginDt = DateTimeUtil.getDailyStartTimeMs(System.currentTimeMillis());
        Long endDt = DateTimeUtil.getDailyEndTimeMs(System.currentTimeMillis());
        if (null != beginTime && 0L != beginTime) {
            beginDt = beginTime;
        }
        if (null != endTime && 0L != endTime) {
            endDt = endTime;
        }
        List<String> boxList = cashClearService.queryBindCashbox(beginDt,endDt,bankId);
        return Result.success(boxList);
    }
}
