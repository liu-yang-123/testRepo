package com.zcxd.voucher.controller;

import com.zcxd.voucher.service.ImportantVoucherServer;
import com.zcxd.voucher.vo.ImportantVoucherWarehouseRecordQueryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author zccc
 */
@RestController
@RequestMapping("/importantVoucher/record")
public class ImportantVoucherWarehouseRecordController {
    @Autowired
    ImportantVoucherServer importantVoucherServer;

    @PostMapping("/list")
    public Object findListByPage(@RequestParam Integer page,
                                 @RequestParam Integer limit,
                                 @RequestBody ImportantVoucherWarehouseRecordQueryVO queryVO) throws IllegalAccessException {
        return importantVoucherServer.findImportantVoucherWarehouseRecordListByPage(page, limit, queryVO);
    }
}
