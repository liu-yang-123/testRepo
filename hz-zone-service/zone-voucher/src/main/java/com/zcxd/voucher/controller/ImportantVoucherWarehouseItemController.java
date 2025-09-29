package com.zcxd.voucher.controller;

import com.zcxd.common.annotation.OperateLog;
import com.zcxd.common.annotation.OperateType;
import com.zcxd.common.util.Result;
import com.zcxd.voucher.db.model.ImportantVoucherWarehouseItem;
import com.zcxd.voucher.service.ImportantVoucherServer;
import com.zcxd.voucher.service.ImportantVoucherWarehouseItemService;
import com.zcxd.voucher.vo.ImportantVoucherWarehouseItemQueryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author zccc
 */
@RestController
@RequestMapping("/importantVoucher/item")
public class ImportantVoucherWarehouseItemController {
    @Autowired
    ImportantVoucherServer importantVoucherServer;
    @Autowired
    ImportantVoucherWarehouseItemService itemService;

    @OperateLog(value = "重空入库申请", type= OperateType.ADD)
    @PostMapping("/entryWarehouseApply")
    public Result entryWarehouseApply(@RequestBody ImportantVoucherWarehouseItem item) {
        try {
            return importantVoucherServer.entryWarehouseApply(item);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    @OperateLog(value = "重空出库申请", type= OperateType.ADD)
    @PostMapping("/outWarehouseApply")
    public Result outWarehouseApply(@RequestBody ImportantVoucherWarehouseItem item) {
        try {
            return importantVoucherServer.outWarehouseApply(item);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    @PostMapping("/list")
    public Object findListByPage(@RequestParam Integer page,
                                 @RequestParam Integer limit,
                                 @RequestBody ImportantVoucherWarehouseItemQueryVO queryVO) throws IllegalAccessException {
        return itemService.findListByPage(page, limit, queryVO);
    }

    @OperateLog(value = "修改重空申请", type=OperateType.EDIT)
    @PostMapping("/update")
    public Object update(@RequestBody ImportantVoucherWarehouseItem item){
        return itemService.updateItemById(item);
    }

    @OperateLog(value = "删除重空申请", type=OperateType.DELETE)
    @PostMapping("/delete")
    public Object delete(@RequestBody ImportantVoucherWarehouseItemQueryVO queryVO){
        try {
            return itemService.deleteItem(queryVO);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    @OperateLog(value = "审批重空申请", type=OperateType.EDIT)
    @PostMapping("/confirm")
    public Object confirmApplyItem(@RequestParam("id") Long id, @RequestParam("departmentId") Integer departmentId) {
        try {
            return importantVoucherServer.confirmApplyItem(id, departmentId);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    @OperateLog(value = "反审批重空申请", type=OperateType.EDIT)
    @PostMapping("/cancel")
    public Object cancelApplyItem(@RequestParam("id") Long id, @RequestParam("departmentId") Integer departmentId) {
        try {
            return importantVoucherServer.cancelApplyItem(id, departmentId);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }

    }

    @OperateLog(value = "手动绑定任务", type=OperateType.EDIT)
    @PostMapping("/associateTaskManual")
    public Object associateTaskManual(@RequestParam("id") Long id, @RequestParam("taskId") Long taskId) {
        return importantVoucherServer.associateTaskManual(id, taskId);
    }
}
