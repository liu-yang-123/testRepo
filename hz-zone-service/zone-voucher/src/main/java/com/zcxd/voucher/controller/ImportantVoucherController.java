package com.zcxd.voucher.controller;

import com.zcxd.common.annotation.OperateLog;
import com.zcxd.common.annotation.OperateType;
import com.zcxd.common.util.Result;
import com.zcxd.voucher.service.ImportantVoucherServer;
import com.zcxd.voucher.service.ImportantVoucherService;
import com.zcxd.voucher.vo.ImportantVoucherQueryVO;
import com.zcxd.voucher.vo.ImportantVoucherVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author zccc
 */
@RestController
@RequestMapping("/importantVoucher")
public class ImportantVoucherController {
    @Autowired
    ImportantVoucherServer importantVoucherServer;

    @Autowired
    ImportantVoucherService importantVoucherService;

    @OperateLog(value = "新增重空", type = OperateType.ADD)
    @PostMapping("/save")
    public Object save(@Validated @RequestBody ImportantVoucherVO importantVoucherVo) {
        return importantVoucherServer.saveImportantVoucher(importantVoucherVo);
    }

    @PostMapping("/list")
    public Object findListByPage(@RequestParam Integer page,
                                 @RequestParam Integer limit,
                                 @RequestBody ImportantVoucherQueryVO queryVO) throws IllegalAccessException {
        return importantVoucherServer.findImportantVoucherListByPage(page, limit, queryVO);
    }

    @OperateLog(value = "修改重空", type=OperateType.EDIT)
    @PostMapping("/update")
    public Object update(@RequestBody ImportantVoucherVO importantVoucherVO) {
        try {
            return importantVoucherServer.updateImportantVoucherById(importantVoucherVO);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    @OperateLog(value = "删除重空", type=OperateType.DELETE)
    @PostMapping("/delete")
    public Object delete(@RequestBody ImportantVoucherQueryVO queryVO){
        try {
            return importantVoucherServer.deleteImportantVoucher(queryVO);
        }  catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    @OperateLog(value = "导入重空信息", type=OperateType.ADD)
    @PostMapping("/importImportantVoucherInfo")
    public Object importImportantVoucherInfo(@RequestParam("file") MultipartFile file, @RequestParam(value = "departmentId") Integer departmentId){
        try {
            return importantVoucherServer.importantVoucherExcelImport(file, departmentId);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }
}
