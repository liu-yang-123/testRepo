package com.zcxd.pda.controller;

import com.zcxd.common.annotation.OperateLog;
import com.zcxd.common.annotation.OperateType;
import com.zcxd.common.constant.RouteStatusEnum;
import com.zcxd.common.util.Result;
import com.zcxd.pda.config.UserContextHolder;
import com.zcxd.pda.dto.*;
import com.zcxd.pda.service.RouteManagerService;
import com.zcxd.pda.vo.*;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 
 * @ClassName RouteManagerController
 * @Description 线路任务控制器
 * @author shijin
 * @Date 2021年5月20日上午11:19:03
 */
@RequestMapping("/route")
@RestController
@Api(tags = "线路管理")
@Slf4j
public class RouteManagerController {

    @Autowired
    private RouteManagerService routeManagerService;


    @ApiOperation(value = "查询待配钞线路列表")
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @PostMapping("/checked/list")
    public Result listCheckedRoutes() {
        List<RouteDTO> list = routeManagerService.listRoute(System.currentTimeMillis(), RouteStatusEnum.CHECKED);
        return Result.success(list);
    }
    @OperateLog(value = "查询我的线路列表",type= OperateType.EDIT)
    @ApiOperation(value = "押运管理->查询我的线路列表")
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @PostMapping("/mylist")
    public Result listMyRoutes() {
        List<RouteDTO> list = routeManagerService.listRouteByMan(System.currentTimeMillis(), UserContextHolder.getUserId());
        return Result.success(list);
    }

    @OperateLog(value = "查询线路配钞详情",type= OperateType.EDIT)
    @ApiOperation(value = "查询线路配钞详情")
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @PostMapping("/detail/{routeId}")
    public Result getDispatchDetail(@PathVariable Long routeId) {
        RouteDetailDTO detail = routeManagerService.getRouteDiapatchDetail(routeId);
        return Result.success(detail);
    }

    @OperateLog(value = "查询线路信息",type= OperateType.EDIT)
    @ApiOperation(value = "查询线路信息")
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @PostMapping("/info/{routeId}")
    public Result getInfo(@PathVariable Long routeId) {
        RouteDetailDTO detail = routeManagerService.getRouteInfo(routeId);
        return Result.success(detail);
    }

    @OperateLog(value = "线路配钞-库管员配钞",type= OperateType.EDIT)
    @ApiOperation(value = "线路配钞-库管员配钞")
    @ApiImplicitParam(name = "routeDispCashVO",value = "配钞信息", required = true,dataType = "RouteDispCashVO")
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @PostMapping("/dispath/cash")
    public Result dispatchCash(@RequestBody RouteDispCashVO routeDispCashVO) {
        routeManagerService.dispatchRouteCash(routeDispCashVO);
        return Result.success();
    }

    @OperateLog(value = "线路配钞-业务员复核配钞",type= OperateType.EDIT)
    @ApiOperation(value = "线路配钞-业务员复核")
    @ApiImplicitParam(name = "routeDispCashConfirmVO",value = "配钞复核信息", required = true,dataType = "RouteDispCashConfirmVO")
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @PostMapping("/dispath/confirm")
    public Result dispatchCashConfirm(@RequestBody RouteDispCashConfirmVO routeDispCashConfirmVO) {
        routeManagerService.dispatchRouteCashConfirm(routeDispCashConfirmVO);
        return Result.success();
    }

    @OperateLog(value = "线路配钞-业务员复核-红外",type= OperateType.EDIT)
    @ApiOperation(value = "线路配钞-业务员复核-红外")
    @ApiImplicitParam(name = "routeDispCashConfirmVO",value = "配钞复核信息", required = true,dataType = "RouteDispCashConfirmVO")
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @PostMapping("/dispath/confirm/scaner")
    public Result dispatchCashConfirmPda(@RequestBody RouteDispCashConfirmVO routeDispCashConfirmVO) {
        boolean bRet = routeManagerService.validateEmployeePassword(routeDispCashConfirmVO.getUserName(),routeDispCashConfirmVO.getPassword());
        if (!bRet) {
            return Result.fail("密码错误");
        }
        routeManagerService.dispatchRouteCashConfirm(routeDispCashConfirmVO);
        return Result.success();
    }


    @OperateLog(value = "登记线路领取钞袋数量",type= OperateType.EDIT)
    @ApiOperation(value = "登记线路领取钞袋数量")
    @ApiImplicitParam(name = "routeDispBagVO",value = "配钞复核信息", required = true,dataType = "RouteDispBagVO")
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @PostMapping("/dispath/bag")
    public Result dispatchCashBag(@RequestBody RouteDispBagVO routeDispBagVO) {
        routeManagerService.saveDispatchBagCount(routeDispBagVO);
        return Result.success();
    }

    @ApiOperation(value = "押运管理->查询线路所有ATM任务列表")
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @PostMapping("/atmtask/list")
    public Result listAtmTask(@RequestParam Long routeId) {
        List<AtmTaskGroupDTO> list = routeManagerService.listAtmTaskByRouteNew(routeId);
        return Result.success(list);
    }

    @ApiOperation(value = "押运管理->查询指定ATM的所有任务")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "routeId", value = "线路id", required = true, dataType = "Long"),
            @ApiImplicitParam(name = "atmNo", value = "设备编号", required = true, dataType = "String"),
    })
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @PostMapping("/atmtask/list/atm")
    public Result listTaskByAtm(@RequestParam Long routeId, @RequestParam String atmNo) {
        List<AtmTaskGroupItem> list = routeManagerService.listByAtmId(routeId,atmNo);
        return Result.success(list);
    }

    @ApiOperation(value = "押运管理->查询ATM任务详情")
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @PostMapping("/atmtask/detail")
    public Result getAtmTaskDetail(@RequestParam Long taskId) {
        AtmTaskDetailDTO atmTaskDetailDTO = routeManagerService.getAtmTaskDetail(taskId);
        return Result.success(atmTaskDetailDTO);
    }

    @OperateLog(value = "上传ATM任务执行记录",type= OperateType.EDIT)
    @ApiOperation(value = "押运管理->上传ATM任务执行记录")
    @ApiImplicitParam(name = "atmTaskResultVO",value = "任务执行记录", required = true,dataType = "AtmTaskResultVO")
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @PostMapping("/task/finish")
    public Result finishAtmTask(@RequestBody AtmTaskResultVO atmTaskResultVO) {
        log.debug("finishAtmTask: " + atmTaskResultVO.toString());
        if (0L == atmTaskResultVO.getTaskId()) {
            //临时维护
            routeManagerService.createAtmTaskResult(atmTaskResultVO);
        } else if (1 == atmTaskResultVO.getOffline()){
            routeManagerService.saveOfflineAtmTaskResult(atmTaskResultVO);
        } else {
            routeManagerService.saveAtmTaskResult(atmTaskResultVO);
        }
        return Result.success();
    }


    @OperateLog(value = "上传离行式网点巡检记录",type= OperateType.EDIT)
    @ApiOperation(value = "押运管理->上传离行式网点巡检记录")
    @ApiImplicitParam(name = "atmBankCheckRecordVO",value = "离行式网点巡检记录", required = true,dataType = "AtmBankCheckRecordVO")
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @PostMapping("/bank/check")
    public Result finishBankCheck(@RequestBody AtmBankCheckRecordVO atmBankCheckRecordVO) {
        routeManagerService.saveBankCheckResult(atmBankCheckRecordVO);
        return Result.success();
    }


    @ApiOperation(value = "交接线路回笼钞袋")
    @ApiImplicitParam(name = "routeReturnBackVO",value = "交接信息", required = true,dataType = "RouteReturnBackVO")
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @PostMapping("/handover")
    public Result handover(@RequestBody RouteReturnBackVO routeReturnBackVO) {
        routeManagerService.handover(routeReturnBackVO);
        return Result.success();
    }

    /**
    -------------------------------------------------------------------------------------------
    */
    @ApiOperation(value = "查询吞没卡任务（按网点分组）")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "routeId", value = "线路id", required = true, dataType = "Long"),
        @ApiImplicitParam(name = "type", value = "查询类别(0 - 回收，1 - 派送)", required = true, dataType = "Integer"),
    })
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @PostMapping("/card/list")
    public Result listRouteBankCard(@RequestParam Long routeId,@RequestParam Integer type) {
        Object object = routeManagerService.listBankCardByRoute(routeId,type);
        return Result.success(object);
    }

    @ApiOperation(value = "查询待交接吞没卡列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "routeId", value = "线路id", required = true, dataType = "Long"),
            @ApiImplicitParam(name = "type", value = "查询类别(0 - 上缴交接，1 - 派送交接)", required = true, dataType = "Integer"),
    })
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @PostMapping("/card/list/handover")
    public Result listRouteBankCardHandover(@RequestParam Long routeId,@RequestParam Integer type) {
        Object object = routeManagerService.listHandOverBankCardByRoute(routeId,type);
        return Result.success(object);
    }

//    @ApiOperation(value = "交接吞没卡")
//    @ApiImplicitParam(name = "atmTaskCardDealDTO",value = "交接信息", required = true,dataType = "AtmTaskCardDealDTO")
//    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
//    @PostMapping("/card/collect")
//    public Result collectBankCard(@RequestBody AtmTaskCardDealDTO atmTaskCardDealDTO) {
//        routeManagerService.updateBankCardCollect(atmTaskCardDealDTO);
//        return Result.success();
//    }
//    @ApiOperation(value = "分配吞没卡")
//    @ApiImplicitParam(name = "atmTaskCardDealDTO",value = "交接信息", required = true,dataType = "AtmTaskCardDealDTO")
//    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
//    @PostMapping("/card/dispatch")
//    public Result dispatchBankCard(@RequestBody AtmTaskCardDealDTO atmTaskCardDealDTO) {
//        routeManagerService.updateBankCardDispatch(atmTaskCardDealDTO);
//        return Result.success();
//    }
    @ApiOperation(value = "取回吞没卡")
    @ApiImplicitParam(name = "atmTaskCardDealDTO",value = "交接信息", required = true,dataType = "AtmTaskCardDealDTO")
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @PostMapping("/card/deliver")
    public Result deliverBankCard(@RequestBody AtmTaskCardDealDTO atmTaskCardDealDTO) {
        routeManagerService.updateBankCardDeliver(atmTaskCardDealDTO);
        return Result.success();
    }

    @ApiOperation(value = "上传车长日志")
    @ApiImplicitParam(name = "routeLogDTO", value = "日志信息", required = true, dataType = "RouteLogDTO")
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @PostMapping("/leader/log/save")
    public Result saveLeaderLog(@RequestBody RouteLogDTO routeLogDTO) {
        routeManagerService.saveRouteLog(routeLogDTO);
        return Result.success();
    }

    @ApiOperation(value = "查询车长日志项列表")
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @PostMapping("/leader/log/template")
    public Result leaderLogTemplate() {
        Object object = routeManagerService.queryRouteLogTemplate();
        return Result.success(object);
    }

    /**
     * 根据线路查询已分配钞盒列表
     * @param routeId
     * @return
     */
    @PostMapping("/list/dispbox")
    public Result listDispbox(@RequestParam Long routeId) {
        Object object = routeManagerService.listDispBox(routeId);
        return Result.success(object);
    }


//    @ApiOperation(value = "查询车长日志")
//    @ApiImplicitParam(name = "id", value = "唯一标识", required = true, dataType = "Long")
//    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
//    @PostMapping("/leader/log/info/{id}")
//    public Result getLeaderLog(@PathVariable Long id) {
//        Object object = routeManagerService.getRouteLog(id);
//        return Result.success(object);
//    }
}
