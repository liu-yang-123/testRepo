package com.zcxd.base.service.report;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zcxd.base.dto.report.*;
import com.zcxd.base.service.BankService;
import com.zcxd.base.util.DenomUtil;
import com.zcxd.common.constant.*;
import com.zcxd.db.mapper.*;
import com.zcxd.db.model.*;
import com.zcxd.db.model.result.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 商业清分报表服务
 * @author shijin
 * @date 2022-5-26
 */
@AllArgsConstructor
@Service
public class TradeClearReportService {

    private BankService bankService;

    private DenomMapper denomMapper;

    private TradeClearChargeRuleMapper tradeClearChargeRuleMapper;

    private TradeClearResultRecordMapper tradeClearResultRecordMapper;


    private List<TradeClearChargeRule> getChargeRuleList(Long departmentId) {
        QueryWrapper<TradeClearChargeRule> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("department_id",departmentId).eq("deleted",0);
        return  tradeClearChargeRuleMapper.selectList(queryWrapper);
    }

    private Map<String,Denom> denomListToMap(List<Denom> denoms) {
        java.text.DecimalFormat decimalFormat = new java.text.DecimalFormat("#0.00");
        Map<String,Denom> denomMap = new HashMap<>();
        for (Denom denom : denoms) {
            String key = decimalFormat.format(denom.getValue());
            denomMap.putIfAbsent(key,denom);
        }
        return denomMap;
    }

    private Map<String,TradeClearChargeRule> ruleListToMap(List<TradeClearChargeRule> rules) {
        Map<String,TradeClearChargeRule> ruleMap = new HashMap<>();
        for (TradeClearChargeRule rule : rules) {
            String key = formatRuleKey(rule.getBankId(),rule.getGbFlag(),rule.getAttr(),rule.getDenom());
            ruleMap.putIfAbsent(key,rule);
        }
        return ruleMap;
    }

    private String formatRuleKey(long bankId,int denomType,String attr,String values) {
        return bankId+":"+denomType+":"+attr+":"+values; //分组键值
    }

    public List<TradeClearFeeReportDTO> calcClearFeeByTime(Long departmentId,Long beginDate,Long endDate,Long bankId) {
        java.text.DecimalFormat decimalFormat = new java.text.DecimalFormat("#0.00");
        //查询所有符合条件清分明细记录
        List<TradeClearRecordQueryResult> recordAllList = tradeClearResultRecordMapper.selectFinishRecords(departmentId, beginDate, endDate,bankId);
        List<TradeClearRecordQueryResult> recordFixList = recordAllList.stream()
                                .filter(tradeClearResultRecord -> tradeClearResultRecord.getGbFlag().equals(DenomTypeEnum.BAD.getValue())
                                                                    || tradeClearResultRecord.getGbFlag().equals(DenomTypeEnum.GOOD.getValue())).collect(Collectors.toList());

        if (recordFixList.size() == 0) {
            return new ArrayList<>();
        }

        //查询券别
        QueryWrapper<Denom> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted",0).orderByAsc("sort");
        List<Denom> denomList = denomMapper.selectList(queryWrapper);
        Map<Long,Denom> denomMap = denomList.stream().collect(Collectors.toMap(Denom::getId, Function.identity()));

        //查询银行
        Set<Long> bankIdSet = recordFixList.stream().map(TradeClearRecordQueryResult::getBankId).collect(Collectors.toSet());
        List<Bank> bankList = bankService.listByIds(bankIdSet);
        Map<Long, String> bankMap = bankList.stream().collect(Collectors.toMap(Bank::getId,Bank::getShortName));
        /**
         * 分组统计清分金额
         */
        Map<String,TradeClearFeeReportDTO> calcResultMap = new HashMap<>();
        for (TradeClearRecordQueryResult result : recordFixList) {
            Denom denom = denomMap.get(result.getDenomId());
            if (null == denom) {
                continue;
            }
            String denomValues = decimalFormat.format(denom.getValue());
            //分组键值
            String key = formatRuleKey(result.getBankId(),result.getGbFlag(),denom.getAttr(),denomValues);
            TradeClearFeeReportDTO reportDTO = calcResultMap.get(key);
            if (null == reportDTO) {
                reportDTO = new TradeClearFeeReportDTO();
                reportDTO.setBankId(result.getBankId());
                reportDTO.setBankName(bankMap.getOrDefault(result.getBankId(),""));
                reportDTO.setDenomType(result.getGbFlag());
                reportDTO.setDenomValues(denomValues);
                reportDTO.setDenomName(DenomUtil.denomValueToName(denomValues,denom.getAttr().equals(DenomAttrEnum.COIN.getValue())));
                reportDTO.setTotalAmount(result.getAmount());
                reportDTO.setDenomAttr(denom.getAttr());
                calcResultMap.put(key,reportDTO);
            } else { //存在，累加
                reportDTO.setTotalAmount(reportDTO.getTotalAmount().add(result.getAmount()));
            }
        }
        //查询收费标准
        List<TradeClearChargeRule> tradeClearChargeRuleList = getChargeRuleList(departmentId);
        Map<String,TradeClearChargeRule> ruleMap = ruleListToMap(tradeClearChargeRuleList);
        //将券别转换以面额为key
        List<Denom> denomValuesList = denomList.stream().filter(denom -> denom.getVersion().equals(DenomVerFlagEnum.VER_NO.getValue()))
                                                            .collect(Collectors.toList());
        Map<String,Denom> denomValuesMap = denomListToMap(denomValuesList);

        //根据收费标准进行计费
        for(String key : calcResultMap.keySet()) {
            TradeClearFeeReportDTO dto = calcResultMap.get(key);
            Denom denom = denomValuesMap.get(dto.getDenomValues());
            if (denom == null) {
                continue;
            }
            int bundles = denom.toBundle(dto.getTotalAmount()); //金额转捆数
            /**
             * 根据券别查询定价
             */
            String denomRuleKey = formatRuleKey(dto.getBankId(),dto.getDenomType(),dto.getDenomAttr(),dto.getDenomValues());
            TradeClearChargeRule chargeRule = ruleMap.get(denomRuleKey);
            if (chargeRule == null) {
                //采用用通用定价
                denomRuleKey = formatRuleKey(dto.getBankId(),dto.getDenomType(),dto.getDenomAttr(),"0");
                chargeRule = ruleMap.get(denomRuleKey);
            }
            BigDecimal feeAmount = BigDecimal.ZERO;
            if (chargeRule != null) {
                feeAmount = chargeRule.getPrice().multiply(BigDecimal.valueOf(bundles));
                dto.setPrice(chargeRule.getPrice());
            } else {
                dto.setPrice(BigDecimal.ZERO);
            }
            dto.setFeeAmount(feeAmount);
            dto.setBundles(bundles);
        }
//        //debug
//        for (String key : calcResultMap.keySet()) {
//            System.out.println("key = "+key);
//        }
//
//        //
//        List<Map.Entry<String,TradeClearFeeReportDTO>> dtoSortByKeyList = new ArrayList<Map.Entry<String, TradeClearFeeReportDTO>>(calcResultMap.entrySet());
//        dtoSortByKeyList.sort(Map.Entry.comparingByKey());

//        System.out.println("------------排序后--------------");
//        for (Map.Entry<String,TradeClearFeeReportDTO> map : dtoSortByKeyList) {
//            System.out.println("key = "+map.getKey());
//        }

//        List<TradeClearFeeReportDTO> dtoList = dtoSortByKeyList.stream().map(Map.Entry::getValue).collect(Collectors.toList());
//
        List<TradeClearFeeReportDTO> dtoList = sortResult(calcResultMap,bankList,denomValuesList);

        return dtoList;
    }

    private List<TradeClearFeeReportDTO> sortResult(Map<String,TradeClearFeeReportDTO> resultMap,List<Bank> bankList,List<Denom> denomList) {
        java.text.DecimalFormat decimalFormat = new java.text.DecimalFormat("#0.00");

        List<Integer> denomTypeList = new ArrayList<>();
        denomTypeList.add(DenomTypeEnum.GOOD.getValue());
        denomTypeList.add(DenomTypeEnum.BAD.getValue());

//        List<String> denomAttrList = new ArrayList<>();
//        denomAttrList.add(DenomAttrEnum.PAPER.getValue());
//        denomAttrList.add(DenomAttrEnum.COIN.getValue());
        List<TradeClearFeeReportDTO> dtoList = new ArrayList<>();
        for (Bank bank : bankList) {
            for (Integer denomType : denomTypeList) {
                for (Denom denom : denomList) {
                    String values = decimalFormat.format(denom.getValue());
                    String key = formatRuleKey(bank.getId(),denomType,denom.getAttr(),values);
                    TradeClearFeeReportDTO dto = resultMap.get(key);
                    if (null != dto) {
                        dtoList.add(dto);
                    }
                }
            }
        }
        return dtoList;
    }
}
