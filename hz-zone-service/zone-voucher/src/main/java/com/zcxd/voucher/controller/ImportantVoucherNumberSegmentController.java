package com.zcxd.voucher.controller;

import com.zcxd.common.annotation.OperateLog;
import com.zcxd.common.annotation.OperateType;
import com.zcxd.voucher.service.ImportantVoucherNumberSegmentService;
import com.zcxd.voucher.service.ImportantVoucherServer;
import com.zcxd.voucher.vo.ImportantVoucherNumberSegmentQueryVO;
import com.zcxd.voucher.vo.ImportantVoucherNumberSegmentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author zccc
 */
@RestController
@RequestMapping("/importantVoucher/numberSegment")
public class ImportantVoucherNumberSegmentController {
    @Autowired
    ImportantVoucherNumberSegmentService segmentService;

    @Autowired
    ImportantVoucherServer importantVoucherServer;

    @OperateLog(value = "新增重空分段", type = OperateType.ADD)
    @PostMapping("/save")
    public Object save(@Validated @RequestBody ImportantVoucherNumberSegmentVO segmentVO) {
        return importantVoucherServer.saveImportantVoucherNumberSegment(segmentVO);
    }


    @PostMapping("/list")
    public Object findListByPage(@RequestParam Integer page,
                                 @RequestParam Integer limit,
                                 @RequestBody ImportantVoucherNumberSegmentQueryVO queryVO) throws IllegalAccessException {
        return importantVoucherServer.findImportantVoucherNumberSegmentListByPage(page, limit, queryVO);
    }

    @OperateLog(value = "修改重空分段", type=OperateType.EDIT)
    @PostMapping("/update")
    public Object update(@RequestBody ImportantVoucherNumberSegmentVO segmentVO){
        return importantVoucherServer.updateImportantVoucherNumberSegmentById(segmentVO);
    }

    @OperateLog(value = "删除重空分段", type=OperateType.DELETE)
    @PostMapping("/delete")
    public Object delete(@RequestBody ImportantVoucherNumberSegmentQueryVO queryVO){
        return importantVoucherServer.deleteImportantVoucherNumberSegment(queryVO);
    }
}
