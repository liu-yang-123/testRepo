package com.zcxd.base.service.report;

import com.zcxd.base.dto.report.CleanAmountReportDTO;
import com.zcxd.base.dto.report.CleanWorkloadReportDTO;
import com.zcxd.base.service.BankService;
import com.zcxd.common.constant.ClearErrorDetailTypeEnum;
import com.zcxd.common.constant.ClearErrorTypeEnum;
import com.zcxd.db.mapper.AtmClearErrorMapper;
import com.zcxd.db.mapper.AtmClearTaskMapper;
import com.zcxd.db.mapper.EmployeeMapper;
import com.zcxd.db.model.Bank;
import com.zcxd.db.model.Employee;
import com.zcxd.db.model.result.CountAmountResult;
import com.zcxd.db.model.result.IdResult;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author songanwei
 * @date 2021-10-13
 */
@AllArgsConstructor
@Service
public class CleanReportService {

    private BankService bankService;

    private AtmClearTaskMapper atmClearTaskMapper;

    private EmployeeMapper employeeMapper;

    private AtmClearErrorMapper atmClearErrorMapper;

    /**
     *
     * @param departmentId  部门ID
     * @param date 查询日期  2021-10
     * @return
     */
    public List<CleanAmountReportDTO> getAmount(Long departmentId, String date){
        //获取顶级银行
        List<Bank> bankList = getTopBank(departmentId);
        Map<Long,String> bankMap = bankList.stream().collect(Collectors.toMap(Bank::getId,Bank::getFullName));
        return bankList.stream().map(item -> {
            Long bankId = item.getId();
            CleanAmountReportDTO reportDTO = new CleanAmountReportDTO();
            reportDTO.setBankId(bankId);
            reportDTO.setBankName(Optional.ofNullable(bankMap.get(bankId)).orElse(""));
            //查询总金额
            CountAmountResult clearCountAmount = atmClearTaskMapper.getBankCountAmount(bankId,date);
            //长款
            CountAmountResult amountResult = atmClearTaskMapper.getCountAmountByBank(departmentId,date,bankId, ClearErrorTypeEnum.MORE.getValue());
            //短款
            CountAmountResult amountResult2 = atmClearTaskMapper.getCountAmountByBank(departmentId,date,bankId, ClearErrorTypeEnum.LESS.getValue());
            //假疑币、夹张、残缺币
            List<CountAmountResult> amountResultList = atmClearErrorMapper.getCountAmountGroupByType(departmentId,date,bankId);

            Optional<CountAmountResult> falseAmountResultOptional = amountResultList.stream()
                    .filter(r -> r.getKey().equals(ClearErrorDetailTypeEnum.FAKE.getValue())).findAny();
            Optional<CountAmountResult> badAmountResultOptional = amountResultList.stream()
                    .filter(r -> r.getKey().equals(ClearErrorDetailTypeEnum.BAD.getValue())).findAny();
            Optional<CountAmountResult> carryAmountResultOptional = amountResultList.stream()
                    .filter(r -> r.getKey().equals(ClearErrorDetailTypeEnum.CARRY.getValue())).findAny();

            reportDTO.setCleanAmount(Optional.ofNullable(clearCountAmount.getAmount()).orElse(BigDecimal.ZERO));
            reportDTO.setCleanCount(Optional.ofNullable(clearCountAmount.getCount()).orElse(0L));
            reportDTO.setMoreNumber(amountResult.getCount());
            reportDTO.setMoreAmount(amountResult.getAmount());
            reportDTO.setLessNumber(amountResult2.getCount());
            reportDTO.setLessAmount(amountResult2.getAmount());
            if (falseAmountResultOptional.isPresent()){
                reportDTO.setFalseNumber(falseAmountResultOptional.get().getCount());
                reportDTO.setFalseAmount(falseAmountResultOptional.get().getAmount());
            }else {
                reportDTO.setFalseNumber(0L);
                reportDTO.setFalseAmount(BigDecimal.ZERO);
            }
            if (badAmountResultOptional.isPresent()){
                reportDTO.setMissNumber(badAmountResultOptional.get().getCount());
                reportDTO.setMissAmount(badAmountResultOptional.get().getAmount());
            }else {
                reportDTO.setMissNumber(0L);
                reportDTO.setMissAmount(BigDecimal.ZERO);
            }
            if (carryAmountResultOptional.isPresent()){
                reportDTO.setBringNumber(carryAmountResultOptional.get().getCount());
                reportDTO.setBringAmount(carryAmountResultOptional.get().getAmount());
            } else{
                reportDTO.setBringNumber(0L);
                reportDTO.setBringAmount(BigDecimal.ZERO);
            }
            //格式化数据

            reportDTO.setMoreNumber(Optional.ofNullable(reportDTO.getMoreNumber()).orElse(0L));
            reportDTO.setMoreAmount(Optional.ofNullable(reportDTO.getMoreAmount()).orElse(BigDecimal.ZERO));
            reportDTO.setLessNumber(Optional.ofNullable(reportDTO.getLessNumber()).orElse(0L));
            reportDTO.setLessAmount(Optional.ofNullable(reportDTO.getLessAmount()).orElse(BigDecimal.ZERO));
            return reportDTO;
        }).collect(Collectors.toList());
    }

    /**
     * 返回清分用户ID
     * @param departmentId 部门ID
     * @param date 日期 2021-10-15
     * @return
     */
    public List<IdResult> getWorkloadIdList(Long departmentId, String date){
        //查询清点员、复核员用户ID，只能通过代码进行分页
        List<IdResult> clearManIdList = atmClearTaskMapper.getClearManList(departmentId,date);
        List<IdResult> checkManIdList = atmClearTaskMapper.getCheckManList(departmentId,date);
        clearManIdList.removeAll(checkManIdList);
        clearManIdList.addAll(checkManIdList);
        return clearManIdList;
    }

    /**
     * @desc
     * @param departmentId 部门ID
     * @param date 日期  2021-10
     * @return
     */
    public List<CleanWorkloadReportDTO> getWorkloadList(Long departmentId, String date, List<IdResult> idList, Integer index){
        AtomicReference<Integer> atomicIndex = new AtomicReference<>(index);
        List<Long> userIdList = idList.stream().map(IdResult::getId).collect(Collectors.toList());
        //查询用户数据
        Map<Long,String> empMap = new HashMap<>();
        if (userIdList.size() > 0){
            List<Employee> employeeList = employeeMapper.selectBatchIds(userIdList);
            empMap = employeeList.stream().collect(Collectors.toMap(Employee::getId,Employee::getEmpName));
        }
        Map<Long, String> finalEmpMap = empMap;
        return idList.stream().map(item -> {
            Long userId = item.getId();
            CleanWorkloadReportDTO reportDTO = new CleanWorkloadReportDTO();
            //查询总金额
            BigDecimal clearAmount = atmClearTaskMapper.getAmountByUser(departmentId,date,userId);
            //差错笔数
            Long number = atmClearTaskMapper.getErrorCountByUser(departmentId,date,userId);
            reportDTO.setAmount(Optional.ofNullable(clearAmount).orElse(BigDecimal.ZERO));
            reportDTO.setNumber(number);
            reportDTO.setName(Optional.ofNullable(finalEmpMap.get(userId)).orElse("----"));
            reportDTO.setIndex(atomicIndex.get());
            atomicIndex.getAndSet(atomicIndex.get() + 1);
            return reportDTO;
        }).collect(Collectors.toList());
    }

    private List<Bank> getTopBank(Long departmentId){
        Bank bank = new Bank();
        bank.setBankType(0);
        bank.setDepartmentId(departmentId);
        List<Bank> bankList = bankService.getTopBank(bank);
        return bankList;
    }

}
