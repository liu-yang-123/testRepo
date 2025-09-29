package com.zcxd.base.service.report;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zcxd.base.dto.report.*;
import com.zcxd.base.service.BankService;
import com.zcxd.common.constant.AtmTaskStatusEnum;
import com.zcxd.common.constant.AtmTaskTypeEnum;
import com.zcxd.common.constant.BusinessModeEnum;
import com.zcxd.common.constant.OrderTypeEnum;
import com.zcxd.db.mapper.*;
import com.zcxd.db.model.*;
import com.zcxd.db.model.result.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author songanwei
 * @date 2021-10-12
 */
@AllArgsConstructor
@Service
public class BankReportService {

    private BankService bankService;

    private AtmTaskMapper atmTaskMapper;

    private DenomMapper denomMapper;

    private EmployeeMapper employeeMapper;

    private AtmDeviceMapper atmDeviceMapper;

    private VaultOrderRecordMapper vaultOrderRecordMapper;

    private AtmTaskCardMapper cardMapper;

    private VaultCheckMapper vaultCheckMapper;

    private AtmClearTaskMapper atmClearTaskMapper;

    /**
     *
     * @param departmentId 部门ID
     * @param date 月份 2021-10
     * @return
     */
    public List<BankTaskReportDTO> getTask(Long departmentId, String date){
        date = date + "-01";
        List<Bank> bankList = getTopBank(departmentId);
        //获取月份的开始时间戳 结束时间戳（即小于下一个月开始时间）
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        long start = localDate.atStartOfDay().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        long end = localDate.atStartOfDay().plusMonths(1L).toInstant(ZoneOffset.of("+8")).toEpochMilli();
        return bankList.stream().map(item -> {
            BankTaskReportDTO reportDTO = new BankTaskReportDTO();
            Long bankId = item.getId();
            reportDTO.setBankId(bankId);
            reportDTO.setBankName(item.getFullName());
            List<CountAmountResult<Integer>> countAmountResultList = atmTaskMapper.getNumberGroupByType(bankId,start,end);
            AtomicReference<Long> cashNumber = new AtomicReference<>(0L);
            AtomicReference<Long> cleanNumber = new AtomicReference<>(0L);
            AtomicReference<Long> maintainNumber = new AtomicReference<>(0L);
            countAmountResultList.stream().forEach(r -> {
                if (r.getKey() == AtmTaskTypeEnum.CASHIN.getValue()){
                    cashNumber.set(r.getCount());
                }
                if (r.getKey() == AtmTaskTypeEnum.CLEAN.getValue()){
                    cleanNumber.set(r.getCount());
                }
                if (r.getKey() == AtmTaskTypeEnum.REPAIR.getValue()){
                    maintainNumber.set(r.getCount());
                }
            });
            Long undoNumber = atmTaskMapper.getStatusNumber(bankId,start, end, AtmTaskStatusEnum.CANCEL.getValue());
            reportDTO.setCashNumber(cashNumber.get());
            reportDTO.setCleanNumber(cleanNumber.get());
            reportDTO.setMaintainNumber(maintainNumber.get());
            reportDTO.setUndoNumber(undoNumber);
            //TODO 计算应急任务台数
            reportDTO.setEmergencyNumber(0L);
            return reportDTO;
        }).collect(Collectors.toList());
    }

    /**
     *
     * @param departmentId 部门ID
     * @param date 日期 2021-10
     * @return
     */
    public List<BankAmountReportDTO> getAmount(Long departmentId, String date){
        date = date + "-01";
        //获取顶级银行
        List<Bank> bankList = getTopBank(departmentId);
        //获取月份的开始时间戳 结束时间戳（即小于下一个月开始时间）
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        long start = localDate.atStartOfDay().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        long end = localDate.atStartOfDay().plusMonths(1L).toInstant(ZoneOffset.of("+8")).toEpochMilli();

        List<AtmTask> atmTaskList = atmTaskMapper.getBankDenomList(departmentId,start,end);
        Map<Long,String> bankMap = bankList.stream().collect(Collectors.toMap(Bank::getId,Bank::getFullName));

        //计算券别
        List<Denom> denomList = denomMapper.selectList(Wrappers.query());
        Map<Long,String> denomMap = denomList.stream().collect(Collectors.toMap(Denom::getId,Denom::getName));

        return atmTaskList.stream().map(item -> {
            BankAmountReportDTO reportDTO = new BankAmountReportDTO();
            Long bankId = item.getBankId();
            Long denomId = item.getDenomId();
            reportDTO.setBankId(bankId);
            reportDTO.setDenomId(denomId);
            reportDTO.setBankName(Optional.ofNullable(bankMap.get(item.getBankId())).orElse(""));
            reportDTO.setDenomName(Optional.ofNullable(denomMap.get(item.getDenomId())).orElse(""));
            //计算加款、撤销金额 、数量
            CountAmountResult cashAmountResult = atmTaskMapper.getTypeCountAmount(start,end,bankId,denomId,AtmTaskTypeEnum.CASHIN.getValue());
            CountAmountResult undoAmountResult = atmTaskMapper.getStatusCountAmount(start,end,bankId,denomId,AtmTaskStatusEnum.CANCEL.getValue());
            reportDTO.setCashNumber(cashAmountResult.getCount());
            reportDTO.setCashAmount(Optional.ofNullable(cashAmountResult.getAmount()).orElse(BigDecimal.ZERO).divide(BigDecimal.valueOf(10000)));
            reportDTO.setUndoNumber(undoAmountResult.getCount());
            reportDTO.setUndoAmount(Optional.ofNullable(undoAmountResult.getAmount()).orElse(BigDecimal.ZERO).divide(BigDecimal.valueOf(10000)));

            return reportDTO;
        }).collect(Collectors.toList());
    }


    /**
     * 返回库存数据统计量
     * @param departmentId 部门ID
     * @param date 日期 2021-10
     * @return
     */
    public List<BankStockReportDTO> getStock(Long departmentId, String date){
        //转化为标准日期
        date = date + "-01";
        //获取顶级银行（只查询开通寄库的银行）
//        List<Bank> bankList = getTopBank(departmentId);
        List<Bank> bankList = bankService.listTopBank(departmentId,BusinessModeEnum.STORE);
        //获取月份的开始时间戳 结束时间戳
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        long start = localDate.atStartOfDay().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        long end = localDate.atStartOfDay().plusMonths(1L).toInstant(ZoneOffset.of("+8")).toEpochMilli();
        return bankList.stream().map(item -> {
            BankStockReportDTO reportDTO = new BankStockReportDTO();
            Long bankId = item.getId();
            List<BankAmountResult> resultList = vaultCheckMapper.getBankAmount(departmentId,bankId,start,end);
            BigDecimal average = BigDecimal.ZERO;
            BigDecimal max = BigDecimal.ZERO;
            BigDecimal min = BigDecimal.ZERO;
            if (resultList.size() > 0){
                //求平均值
                average = resultList.stream().map(BankAmountResult::getAmount).reduce(BigDecimal.ZERO,BigDecimal::add)
                        .divide(BigDecimal.valueOf(resultList.size()), 2, BigDecimal.ROUND_HALF_UP);
                //求最大值
                max = resultList.stream().map(BankAmountResult::getAmount).reduce(BigDecimal.ZERO,BigDecimal::max);
                //求最小值
                min = resultList.stream().map(BankAmountResult::getAmount).min((x1,x2) -> x1.compareTo(x2)).get();
            }
            reportDTO.setBankId(bankId);
            reportDTO.setBankName(item.getFullName());
            reportDTO.setAverage(average);
            reportDTO.setHigh(max);
            reportDTO.setLow(min);
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
        //转化为标准日期
        date = date + "-01";
        //获取月份的开始时间戳 结束时间戳
        LocalDate localDate =  LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        long start = localDate.atStartOfDay().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        long end = localDate.atStartOfDay().plusMonths(1L).toInstant(ZoneOffset.of("+8")).toEpochMilli();
        //查询清点员、复核员用户ID，只能通过代码进行分页
        //并行查询数据
        List<IdResult> opManIdList = atmTaskMapper.getOpManList(departmentId,start,end);
        List<IdResult> keyManIdList = atmTaskMapper.getKeyManList(departmentId,start,end);

        opManIdList.removeAll(keyManIdList);
        opManIdList.addAll(keyManIdList);
        return opManIdList;
    }

    /**
     * @desc
     * @param departmentId 部门ID
     * @param date 日期  2021-10
     * @return
     */
    public List<BankWorkloadReportDTO> getWorkloadList(Long departmentId, String date, List<IdResult> idList, Integer index){
        //转化为标准日期
        date = date + "-01";
        //获取月份的开始时间戳 结束时间戳
        LocalDate localDate =  LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        long start = localDate.atStartOfDay().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        long end = localDate.atStartOfDay().plusMonths(1L).toInstant(ZoneOffset.of("+8")).toEpochMilli();
        List<Long> userIdList = idList.stream().map(IdResult::getId).collect(Collectors.toList());

        //查询用户数据
        Map<Long,String> empMap = new HashMap<>();
        Map<Long,BigDecimal> empAmountMap = new HashMap<>();
        Map<Long,Long> empCashCountMap = new HashMap<>();
        Map<Long,Long> empCleanCountMap = new HashMap<>();
        Map<Long,Long> empMaintainCountMap = new HashMap<>();
        if (userIdList.size() > 0){
            List<Employee> employeeList = employeeMapper.selectBatchIds(userIdList);
            empMap = employeeList.stream().collect(Collectors.toMap(Employee::getId,Employee::getEmpName));

            List<CountAmountResult<Long>> opManAmountResultList = null;
            List<CountAmountResult<Long>> keyManAmountResultList = null;
            List<CountAmountResult<Long>> opManCashCountResultList = null;
            List<CountAmountResult<Long>> keyManCashCountResultList = null;
            List<CountAmountResult<Long>> opManCleanCountResultList = null;
            List<CountAmountResult<Long>> keyManCleanCountResultList = null;
            List<CountAmountResult<Long>> opManMaintainCountResultList = null;
            List<CountAmountResult<Long>> keyManMaintainCountResultList = null;
            opManAmountResultList = atmTaskMapper.getAmountByUsersOpMan(departmentId,start,end,userIdList,AtmTaskTypeEnum.CASHIN.getValue());
            keyManAmountResultList = atmTaskMapper.getAmountByUsersKeyMan(departmentId,start,end,userIdList,AtmTaskTypeEnum.CASHIN.getValue());
            opManCashCountResultList = atmTaskMapper.getCountByUsersOpMan(departmentId,start,end,userIdList,AtmTaskTypeEnum.CASHIN.getValue());
            keyManCashCountResultList = atmTaskMapper.getCountByUsersKeyMan(departmentId,start,end,userIdList,AtmTaskTypeEnum.CASHIN.getValue());
            opManCleanCountResultList = atmTaskMapper.getCountByUsersOpMan(departmentId,start,end,userIdList,AtmTaskTypeEnum.CLEAN.getValue());
            keyManCleanCountResultList = atmTaskMapper.getCountByUsersKeyMan(departmentId,start,end,userIdList,AtmTaskTypeEnum.CLEAN.getValue());
            opManMaintainCountResultList = atmTaskMapper.getCountByUsersOpMan(departmentId,start,end,userIdList,AtmTaskTypeEnum.REPAIR.getValue());
            keyManMaintainCountResultList = atmTaskMapper.getCountByUsersKeyMan(departmentId,start,end,userIdList,AtmTaskTypeEnum.REPAIR.getValue());
            //数据合并
            merge(userIdList,opManAmountResultList,keyManAmountResultList,empAmountMap,1);
            merge(userIdList,opManCashCountResultList,keyManCashCountResultList,empCashCountMap,0);
            merge(userIdList,opManCleanCountResultList,keyManCleanCountResultList,empCleanCountMap,0);
            merge(userIdList,opManMaintainCountResultList,keyManMaintainCountResultList,empMaintainCountMap,0);

        }
        AtomicReference<Integer> atomicIndex = new AtomicReference<>(index);
        Map<Long, String> finalEmpMap = empMap;
        return idList.stream().map(item -> {
            Long userId = item.getId();
            BankWorkloadReportDTO reportDTO = new BankWorkloadReportDTO();
            //查询总金额
            BigDecimal clearAmount = Optional.ofNullable(empAmountMap.get(userId)).orElse(BigDecimal.ZERO);
            //加款任务笔数
            Long number = Optional.ofNullable(empCashCountMap.get(userId)).orElse(0L);
            //清机
            Long cleanNumber = Optional.ofNullable(empCleanCountMap.get(userId)).orElse(0L);
            System.out.println("-----------cleanNumber: " + cleanNumber);
            //维修
            Long maintainNumber = Optional.ofNullable(empMaintainCountMap.get(userId)).orElse(0L);
            //天数
            Long days = atmTaskMapper.getDaysByUser(departmentId,start,end,userId);
            reportDTO.setAmount(Optional.ofNullable(clearAmount).orElse(BigDecimal.ZERO).divide(BigDecimal.valueOf(10000)));
            reportDTO.setCashNumber(number);
            reportDTO.setName(Optional.ofNullable(finalEmpMap.get(userId)).orElse(""));
            reportDTO.setCleanNumber(cleanNumber);
            reportDTO.setMaintainNumber(maintainNumber);
            reportDTO.setTotalNumber(number+cleanNumber+maintainNumber);
            reportDTO.setWorkDay(days);
            reportDTO.setIndex(atomicIndex.get());
            atomicIndex.getAndSet(atomicIndex.get() + 1);
            return reportDTO;
        }).collect(Collectors.toList());
    }

    public List<BankReceivePaymentReportDTO> getReceivePayment(Long departmentId, String date){
        //转化为标准日期
        date = date + "-01";
        //获取顶级银行
        // modify begin by shijin, 使用业务标志查询银行机构
//        List<Bank> bankList = getTopBank(departmentId);  //masked
        List<Bank> bankList = bankService.listTopBank(departmentId,BusinessModeEnum.STORE);
        //modify end
        //获取月份的开始时间戳 结束时间戳
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        long start = localDate.atStartOfDay().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        long end = localDate.atStartOfDay().plusMonths(1L).toInstant(ZoneOffset.of("+8")).toEpochMilli();
        //查询券别
        List<Denom> denomList = denomMapper.selectList(Wrappers.query());
        Map<Long,String> denomMap = denomList.stream().collect(Collectors.toMap(Denom::getId,Denom::getName));
        List<BankReceivePaymentReportDTO> resultList = new LinkedList<>();
        bankList.stream().forEach(item -> {
            Long bankId = item.getId();
            //领款数据
            List<AtmDenomAmountResult> resultListT = vaultOrderRecordMapper.getDenomAmount(bankId,start,end, OrderTypeEnum.STOCK_IN.getValue(), 1);
            //缴款数据
            List<AtmDenomAmountResult> resultList2 = vaultOrderRecordMapper.getDenomAmount(bankId,start,end, OrderTypeEnum.STOCK_OUT.getValue(), 1);
            //获取所有券别
            Set<Long> denomIds = resultListT.stream().map(AtmDenomAmountResult::getDenomId).collect(Collectors.toSet());
            Set<Long> denomIds2 = resultList2.stream().map(AtmDenomAmountResult::getDenomId).collect(Collectors.toSet());
            denomIds.addAll(denomIds2);
            List<BankReceivePaymentReportDTO> amountResultList = denomIds.stream().map(denomId -> {
                BankReceivePaymentReportDTO reportDTO = new BankReceivePaymentReportDTO();
                //提取券别对应的数据
                Optional<AtmDenomAmountResult> amountResultOptional = resultListT.stream().filter(r -> r.getDenomId().equals(denomId)).findFirst();
                Optional<AtmDenomAmountResult> amountResultOptional2 = resultList2.stream().filter(r -> r.getDenomId().equals(denomId)).findFirst();
                reportDTO.setBankId(bankId);
                reportDTO.setBankName(item.getFullName());
                reportDTO.setDenomId(denomId);
                reportDTO.setReceiveAmount(BigDecimal.ZERO);
                reportDTO.setPaymentAmount(BigDecimal.ZERO);
                reportDTO.setDenomName(Optional.ofNullable(denomMap.get(denomId)).orElse(""));
                amountResultOptional.ifPresent(u -> reportDTO.setReceiveAmount(u.getTaskAmount()));
                amountResultOptional2.ifPresent( u -> reportDTO.setPaymentAmount(u.getTaskAmount()));
                return reportDTO;
            }).collect(Collectors.toList());
            resultList.addAll(amountResultList);
        });
        return resultList;
    }

    /**
     * 查询设备总数
     * @param departmentId
     * @return
     */
    public Long getDeviceTotal(Long departmentId, String terNo){
        return atmDeviceMapper.getTotal(departmentId,terNo);
    }

    public List<BankDeviceCashReportDTO> getDeviceList(Long departmentId, String date, Integer page, Integer limit,String terNo){
        Integer offset = (page - 1) * limit;
        String finalDate = date;
        //转化为标准日期
        date = date + "-01";
        //获取月份的开始时间戳 结束时间戳
        LocalDate localDate =  LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        long start = localDate.atStartOfDay().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        long end = localDate.atStartOfDay().plusMonths(1L).toInstant(ZoneOffset.of("+8")).toEpochMilli();

        //如果设备列表
        List<AtmDeviceRouteResult> resultList = atmDeviceMapper.getList(departmentId,offset,limit,terNo);
        AtomicReference<Integer> atomicIndex = new AtomicReference<>(offset);
        //计算天数
        int days = localDate.atStartOfDay().plusMonths(1L).minusDays(1L).getDayOfMonth();

        Calendar cal = Calendar.getInstance();
        String sCurMonth = String.format("%04d-%02d",cal.get(Calendar.YEAR),cal.get(Calendar.MONTH) + 1);
        if (sCurMonth.equals(finalDate)) { //当前月份,天数等于1号到现在天数
            days = cal.get(Calendar.DATE);
        }

        //获取设备列表ID
        List<Long> atmIdList = resultList.stream().map(AtmDeviceRouteResult::getId).collect(Collectors.toList());
        List<CountAmountResult<Long>> atmCountResultList = null;
        List<CountAmountResult<Long>> atmCashResultList = null;
        List<CountAmountResult<Long>> atmCleanResultList = null;
        List<CountAmountResult<Long>> atmMaintainResultList = null;
        List<CountAmountResult<Long>> atmCashAmountResultList = null;
        List<CountAmountResult<Long>> atmBackAmountResultList = null;
        Map<Long,Long> totalCountMap = new HashMap<>();
        Map<Long,Long> cashCountMap = new HashMap<>();
        Map<Long,Long> cleanCountMap = new HashMap<>();
        Map<Long,Long> maintainCountMap = new HashMap<>();
        Map<Long,BigDecimal> cashAmountMap = new HashMap<>();
        Map<Long,BigDecimal> backAmountMap = new HashMap<>();
        if (atmIdList.size() > 0) {
            //任务总数
            atmCountResultList = atmTaskMapper.getCountByAtm(departmentId, start, end, atmIdList, null);
            totalCountMap = atmCountResultList.stream().collect(Collectors.toMap(CountAmountResult::getKey,CountAmountResult::getCount));
            //加钞任务数
            atmCashResultList = atmTaskMapper.getCountByAtm(departmentId, start, end, atmIdList, AtmTaskTypeEnum.CASHIN.getValue());
            cashCountMap = atmCashResultList.stream().collect(Collectors.toMap(CountAmountResult::getKey,CountAmountResult::getCount));
            //清机任务数
            atmCleanResultList = atmTaskMapper.getCountByAtm(departmentId, start, end, atmIdList, AtmTaskTypeEnum.CLEAN.getValue());
            cleanCountMap = atmCleanResultList.stream().collect(Collectors.toMap(CountAmountResult::getKey,CountAmountResult::getCount));
            //维修任务数
            atmMaintainResultList = atmTaskMapper.getCountByAtm(departmentId, start, end, atmIdList, AtmTaskTypeEnum.REPAIR.getValue());
            maintainCountMap = atmMaintainResultList.stream().collect(Collectors.toMap(CountAmountResult::getKey,CountAmountResult::getCount));
            //加钞金额
            atmCashAmountResultList = atmTaskMapper.getAmountByAtm(departmentId, start, end, atmIdList, AtmTaskTypeEnum.CASHIN.getValue());
            cashAmountMap = atmCashAmountResultList.stream().collect(Collectors.toMap(CountAmountResult::getKey,CountAmountResult::getAmount));
            //回款金额
            atmBackAmountResultList = atmClearTaskMapper.getAmountByAtm(departmentId, finalDate, atmIdList);
            backAmountMap = atmBackAmountResultList.stream().collect(Collectors.toMap(CountAmountResult::getKey,CountAmountResult::getAmount));
        }
        Map<Long, Long> finalTotalCountMap = totalCountMap;
        Map<Long, Long> finalCashCountMap = cashCountMap;
        Map<Long, Long> finalCleanCountMap = cleanCountMap;
        Map<Long, Long> finalMaintainCountMap = maintainCountMap;
        Map<Long, BigDecimal> finalCashAmountMap = cashAmountMap;
        Map<Long, BigDecimal> finalBackAmountMap = backAmountMap;
        int finalDays = days;
        return resultList.stream().map(item -> {
            Long atmId = item.getId();
            BankDeviceCashReportDTO reportDTO = new BankDeviceCashReportDTO();
            reportDTO.setTerNo(item.getTerNo());
            reportDTO.setRouteNo(item.getRouteNo());
            Long totalTask = Optional.ofNullable(finalTotalCountMap.get(atmId)).orElse(0L);
            Long cashNumber = Optional.ofNullable(finalCashCountMap.get(atmId)).orElse(0L);
            Long cleanNumber = Optional.ofNullable(finalCleanCountMap.get(atmId)).orElse(0L);
            Long maintainNumber =Optional.ofNullable(finalMaintainCountMap.get(atmId)).orElse(0L);
            BigDecimal cashAmount = Optional.ofNullable(finalCashAmountMap.get(atmId)).orElse(BigDecimal.ZERO);
            BigDecimal backAmount = Optional.ofNullable(finalBackAmountMap.get(atmId)).orElse(BigDecimal.ZERO);
            reportDTO.setTotalTask(totalTask);
            reportDTO.setCashNumber(cashNumber);
            reportDTO.setCleanNumber(cleanNumber);
            reportDTO.setMaintainNumber(maintainNumber);
            reportDTO.setCashAmount(Optional.ofNullable(cashAmount).orElse(BigDecimal.ZERO));
            reportDTO.setBackAmount(Optional.ofNullable(backAmount).orElse(BigDecimal.ZERO));
            double cashRate = cashNumber == 0 ? 0 : new BigDecimal((double) finalDays /cashNumber).setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue();
            reportDTO.setCashRate(cashRate);
            atomicIndex.getAndSet(atomicIndex.get() + 1);
            reportDTO.setIndex(atomicIndex.get());
            return reportDTO;
        }).collect(Collectors.toList());
    }

    /**
     * 吞没卡数量查询
     * @param departmentId
     * @param date
     * @param terNo ATM设备编号
     * @return
     */
    public Long getCardTotal(Long departmentId, String date, String terNo){
        //转化为标准日期
        date = date + "-01";
        //获取月份的开始时间戳 结束时间戳
        LocalDate localDate =  LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        long start = localDate.atStartOfDay().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        long end = localDate.atStartOfDay().plusMonths(1L).toInstant(ZoneOffset.of("+8")).toEpochMilli();
        Long total = cardMapper.getTotal(departmentId,start,end,terNo);
        return total;
    }

    /**
     * 吞没卡数据列表
     * @param departmentId
     * @param date
     * @param page
     * @param limit
     * @return
     */
    public List<BankCardReportDTO> getCardList(Long departmentId, String date, Integer page, Integer limit, String terNo){
        //转化为标准日期
        date = date + "-01";
        //获取月份的开始时间戳 结束时间戳
        LocalDate localDate =  LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        long start = localDate.atStartOfDay().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        long end = localDate.atStartOfDay().plusMonths(1L).toInstant(ZoneOffset.of("+8")).toEpochMilli();

        Integer offset = (page - 1) * limit;
        List<AtmTaskCardResult> cardList = cardMapper.getList(departmentId,start,end,offset,limit,terNo);
        Set<Long> deliverBankIds = cardList.stream().map(AtmTaskCard::getDeliverBankId).collect(Collectors.toSet());
        Map<Long,String> bankMap = new HashMap<>();
        if (deliverBankIds.size() > 0){
            List<Bank> bankList = bankService.listByIds(deliverBankIds);
            bankMap = bankList.stream().collect(Collectors.toMap(Bank::getId,Bank::getFullName));
        }
        AtomicReference<Integer> atomicIndex = new AtomicReference<>(offset);
        Map<Long, String> finalBankMap = bankMap;
        return cardList.stream().map(item -> {
            BankCardReportDTO reportDTO = new BankCardReportDTO();
            reportDTO.setCardNo(item.getCardNo());
            reportDTO.setTerNo(item.getTerNo());
            reportDTO.setCardBank(item.getCardBank());
            reportDTO.setRouteNo(item.getRouteNo());
            reportDTO.setDeliverRouteNo(item.getDeliverRouteNo());
            reportDTO.setBankName(Optional.ofNullable(finalBankMap.get(item.getDeliverBankId())).orElse(""));
            reportDTO.setDeliverDate(item.getDeliverDay());
            if (item.getCreateTime() > 0) {
                LocalDateTime localDateTime = Instant.ofEpochMilli(item.getCreateTime()).atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
                reportDTO.setCollectDate(localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }
            atomicIndex.getAndSet(atomicIndex.get() + 1);
            reportDTO.setIndex(atomicIndex.get());
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

    /**
     *
     * @param userIdList yonghuID列表
     * @param opManList 列表1
     * @param keyManList 列表2
     * @param empAmountMap  结果Map
     * @param type 类型 1=合并金额  0=合并数量
     */
    private void merge(List<Long> userIdList, List<CountAmountResult<Long>> opManList, List<CountAmountResult<Long>> keyManList, Map empAmountMap, Integer type ){
        List<CountAmountResult<Long>> finalopManList = opManList;
        List<CountAmountResult<Long>> finalkeyManList = keyManList;
        userIdList.stream().forEach(t -> {
            Optional<CountAmountResult<Long>> opManOptional = finalopManList.stream().filter(o -> o.getKey().equals(t)).findFirst();
            Optional<CountAmountResult<Long>> keyManOptional = finalkeyManList.stream().filter(o -> o.getKey().equals(t)).findFirst();
            if (type == 1){
                if (opManOptional.isPresent() && keyManOptional.isPresent()){
                    empAmountMap.put(t, opManOptional.get().getAmount().add(keyManOptional.get().getAmount()));
                } else if (opManOptional.isPresent()){
                    empAmountMap.put(t,opManOptional.get().getAmount());
                } else if (keyManOptional.isPresent()){
                    empAmountMap.put(t,keyManOptional.get().getAmount());
                } else{
                    empAmountMap.put(t,BigDecimal.ZERO);
                }
            }
            if (type == 0){
                if (opManOptional.isPresent() && keyManOptional.isPresent()){
                    empAmountMap.put(t, opManOptional.get().getCount()+ keyManOptional.get().getCount());
                } else if (opManOptional.isPresent()){
                    empAmountMap.put(t,opManOptional.get().getCount());
                } else if (keyManOptional.isPresent()){
                    empAmountMap.put(t,keyManOptional.get().getCount());
                } else{
                    empAmountMap.put(t,0L);
                }
            }
        });



    }

}
