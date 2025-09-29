package com.zcxd.base.controller;

import com.zcxd.base.service.BankService;
import com.zcxd.base.vo.BankVO;
import com.zcxd.common.util.Result;
import com.zcxd.db.model.Bank;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zccc
 */
@RequestMapping("/bank")
@AllArgsConstructor
@RestController
public class BankController {
    private BankService bankService;

    @PostMapping("/listByCondition")
    public Result<Bank> listByCondition(@RequestBody BankVO bankVO) {
        Bank bank = new Bank();
        BeanUtils.copyProperties(bankVO, bank);
        List<Bank> banks = bankService.listByCondition(bank);
        return banks.isEmpty() ? Result.fail("未找到指定银行") : Result.success(banks.get(0));
    }

    @PostMapping("/list")
    public Result list(@RequestParam(value = "id", required = false) Long id,
                       @RequestParam(value = "name",required = false) String name,
                       @RequestParam(value = "bankType",required = false) Integer bankType,
                       @RequestParam("departmentId") Integer departmentId) {
        return Result.success(bankService.listSubBank(id, name, bankType, departmentId));
    }
}
