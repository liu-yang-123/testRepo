package com.zcxd.base.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zcxd.base.dto.VaultCheckDto;
import com.zcxd.base.service.BankService;
import com.zcxd.base.service.EmployeeService;
import com.zcxd.base.service.VaultCheckService;
import com.zcxd.common.util.DateTimeUtil;
import com.zcxd.common.util.Result;
import com.zcxd.common.util.ResultList;
import com.zcxd.db.model.AtmClearTaskAudit;
import com.zcxd.db.model.Bank;
import com.zcxd.db.model.Employee;
import com.zcxd.db.model.VaultCheck;
import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author songanwei
 * @date 2021-05-29
 */
@Api(tags = "金库盘点")
@AllArgsConstructor
@RequestMapping("/vaultCheck")
@RestController
public class VaultCheckController {

    private VaultCheckService checkService;

    private BankService bankService;

    private EmployeeService employeeService;

    @ApiOperation(value = "盘点记录列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页数", required = true, dataType = "Integer ",defaultValue = "1",dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "limit", value = "显示数据条数", required = true, dataType = "Integer ",defaultValue = "10",dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "departmentId", value = "部门ID", required = true, dataType = "Long", defaultValue = "0",dataTypeClass = Long.class),
            @ApiImplicitParam(name = "bankId", value = "银行机构ID", dataType = "Long", defaultValue = "0",dataTypeClass = Long.class)
    })
    @GetMapping("/list")
    public Result list(@RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @RequestParam Long departmentId,
                       @RequestParam(name = "bankId",defaultValue = "0") Long bankId,
                       @RequestParam(required = false) Long beginDate,
                       @RequestParam(required = false) Long endDate){
        VaultCheck vaultCheck = new VaultCheck();
        vaultCheck.setBankId(bankId);
        vaultCheck.setDepartmentId(departmentId);
        Long beginDt = null;
        Long endDt = null;
        if (beginDate != null) {
            beginDt = DateTimeUtil.getDailyStartTimeMs(beginDate);
        }
        if (endDate != null) {
            endDt = DateTimeUtil.getDailyEndTimeMs(endDate);
        }
        IPage<VaultCheck> vaultCheckPage = checkService.findListByPage(page,limit,vaultCheck,beginDt,endDt);
        Set<Long> bankIds = vaultCheckPage.getRecords().stream().map(VaultCheck::getBankId).collect(Collectors.toSet());
        Map<Long,String> bankMap = new HashMap<>();
        if (bankIds.size() > 0){
            List<Bank> bankList = bankService.listByIds(bankIds);
            bankMap = bankList.stream().collect(Collectors.toMap(Bank::getId,Bank::getFullName));
        }

        Set<Long> empIds = vaultCheckPage.getRecords().stream().map(t -> {
            HashSet<Long> set = new HashSet<>(2);
            set.add(t.getWhOpMan());
            set.add(t.getWhCheckMan());
            set.add(t.getWhConfirmMan());
            return set;
        }).flatMap(Collection::stream).filter(p -> p != 0).collect(Collectors.toSet());

        Map<Long,String> empMap = new HashMap<>();
        if (empIds.size() > 0){
            List<Employee> employeeList = employeeService.listByIds(empIds);
            empMap = employeeList.stream().collect(Collectors.toMap(Employee::getId,s -> s.getEmpNo() +"/" +s.getEmpName()));
        }

        Map<Long, String> finalBankMap = bankMap;
        Map<Long, String> finalEmpMap = empMap;
        List<VaultCheckDto> vaultCheckDtoList = vaultCheckPage.getRecords().stream().map(t -> {
            VaultCheckDto vaultCheckDto = new VaultCheckDto();
            BeanUtils.copyProperties(t,vaultCheckDto);
            vaultCheckDto.setBankName(Optional.ofNullable(finalBankMap.get(t.getBankId())).orElse(""));
            vaultCheckDto.setWhOpManName(Optional.ofNullable(finalEmpMap.get(t.getWhOpMan())).orElse(""));
            vaultCheckDto.setWhCheckManName(Optional.ofNullable(finalEmpMap.get(t.getWhCheckMan())).orElse(""));
            vaultCheckDto.setWhConfirmManName(Optional.ofNullable(finalEmpMap.get(t.getWhConfirmMan())).orElse(""));
            return vaultCheckDto;
        }).collect(Collectors.toList());

        ResultList resultList = ResultList.builder().total(vaultCheckPage.getTotal()).list(vaultCheckDtoList).build();
        return Result.success(resultList);
    }

}
