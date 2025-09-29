package com.zcxd.pda.controller;
import com.alibaba.fastjson.JSONObject;
import com.zcxd.common.util.Result;
import com.zcxd.common.util.ResultList;
import com.zcxd.pda.dto.CashboxDTO;
import com.zcxd.pda.dto.FingerprintDTO;
import com.zcxd.pda.dto.OrderProcDTO;
import com.zcxd.pda.dto.VaultVolumDTO;
import com.zcxd.pda.service.LoginService;
import com.zcxd.pda.service.WarehouseService;
import com.zcxd.pda.vo.LoginAccountVO;
import com.zcxd.pda.vo.VaultCheckResultVO;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 
 * @ClassName WarehouseController
 * @Description 金库管理控制器
 * @author shijin
 * @Date 2021年5月18日上午16:19:03
 */
@RequestMapping("/vault")
@RestController
@Api(tags = "金库管理")
public class WarehouseController {

    @Autowired
    private WarehouseService warehouseService;

    @ApiOperation(value = "查询出入库单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", required = true, dataType = "Integer",dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "limit", value = "记录数", required = true, dataType = "Integer",dataTypeClass = Integer.class)
    })
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @PostMapping("/order/list")
    public Result listVaultOrders(@RequestParam(defaultValue = "1") Integer page,
                            @RequestParam(defaultValue = "10") Integer limit) {
        ResultList resultList = warehouseService.listUndoVaultOrder(page,limit);
        return Result.success(resultList);
    }

    @ApiOperation(value = "查询出入库单详情")
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @PostMapping("/order/detail/{id}")
    public Result listVaultOrders(@PathVariable Long id) {
        JSONObject jsonObject = warehouseService.getVaultOrderDenomRecords(id);
        return Result.success(jsonObject);
    }

    @ApiOperation(value = "订单出库")
    @ApiImplicitParam(name = "orderProcDTO", value = "出库处理信息", required = true, dataType = "OrderProcDTO")
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @PostMapping("/order/stockout")
    public Result orderStockOut(@RequestBody OrderProcDTO orderProcDTO) {
        warehouseService.orderStockOut(orderProcDTO);
        return Result.success();
    }

    @ApiOperation(value = "订单入库")
    @ApiImplicitParam(name = "orderProcDTO", value = "入库处理信息", required = true, dataType = "OrderProcDTO")
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @PostMapping("/order/stockin")
    public Result orderStockIn(@RequestBody OrderProcDTO orderProcDTO) {
        warehouseService.orderStockIn(orderProcDTO);
        return Result.success();
    }

    @ApiOperation(value = "查询银行库存列表")
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @PostMapping("/balance/list")
    public Result vaultBalanceList() {
        List<VaultVolumDTO> vaultVolumDTOList = warehouseService.getAllBankVaultVolum();
        return Result.success(vaultVolumDTOList);
    }

    @ApiOperation(value = "查询银行库存详情")
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @PostMapping("/balance/detail/{bankId}")
    public Result vaultBalanceDetail(@PathVariable Long bankId) {
        Object object = warehouseService.getBankVaultVolumDetail(bankId);
        return Result.success(object);
    }

    @ApiOperation(value = "金库盘点")
    @ApiImplicitParam(name = "vaultCheckResultVO", value = "盘点结果", required = true, dataType = "VaultCheckResultVO")
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @PostMapping("/balance/check")
    public Result vaultBalanceCheck(@RequestBody VaultCheckResultVO vaultCheckResultVO) {
        warehouseService.saveVolumCheckResult(vaultCheckResultVO);
        return Result.success();
    }


    @ApiOperation(value = "钞盒绑定(批量）")
    @ApiImplicitParam(name = "cashboxDTOList", value = "钞盒绑定列表", required = true, dataType = "List<CashboxDTO>",dataTypeClass = List.class)
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @PostMapping("/cashbox/bind/batch")
    public Result cashboxBindBatch(@RequestBody List<CashboxDTO> cashboxDTOList) {
        warehouseService.cashboxBindBatch(cashboxDTOList);
        return Result.success();
    }

    @ApiOperation(value = "钞盒绑定（单个）")
    @ApiImplicitParam(name = "cashboxDTO", value = "钞盒绑定信息", required = true, dataType = "CashboxDTO")
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @PostMapping("/cashbox/bind/single")
    public Result cashboxBindSingle(@RequestBody CashboxDTO cashboxDTO) {
        warehouseService.cashboxBindSingle(cashboxDTO);
        return Result.success();
    }

    @ApiOperation(value = "查询RFID已绑定钞盒信息")
    @ApiImplicitParam(name = "rfid", value = "RFID标签", required = true, dataType = "String",dataTypeClass = String.class)
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @PostMapping("/cashbox/info")
    public Result getCashboxInfo(@RequestParam String rfid) {
        CashboxDTO cashboxDTO = warehouseService.getCashBoxByRfid(rfid);
        return Result.success(cashboxDTO);
    }
}
