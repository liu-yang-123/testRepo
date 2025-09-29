package com.zcxd.base.controller.report;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import com.zcxd.base.dto.report.CleanAmountReportDTO;
import com.zcxd.base.dto.report.CleanWorkloadReportDTO;
import com.zcxd.base.dto.report.TradeClearFeeReportDTO;
import com.zcxd.base.handler.ExcelMergeHandler;
import com.zcxd.base.service.report.CleanReportService;
import com.zcxd.base.service.report.TradeClearReportService;
import com.zcxd.base.util.ExcelUtil;
import com.zcxd.base.vo.TradeCalcFeeVO;
import com.zcxd.common.util.DateTimeUtil;
import com.zcxd.common.util.Result;
import com.zcxd.common.util.ResultList;
import com.zcxd.db.model.result.IdResult;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 商业清分统计
 * @author shijin
 *
 * @date 2022-5-26
 */
@Slf4j
@AllArgsConstructor
@RequestMapping("/clearReport")
@RestController
public class TradeClearReportController {

    @Resource
    private TradeClearReportService tradeClearReportService;

    private String formatDateStr(int year, int month,int day) {
        return String.format("%04d-%02d-%02d 00:00:00",year,month,day);
    }

    /**
     * 统计清分费用
     * @param calcFeeVO - 查询参数
     * @return
     */

    @PostMapping("/calcFee")
    Result<List<TradeClearFeeReportDTO>> calcFee(@RequestBody TradeCalcFeeVO calcFeeVO) {
        long beginDate;
        long endDate;
        try {
            Integer year = calcFeeVO.getYear();
            Integer month = calcFeeVO.getMonth();
            Integer day = calcFeeVO.getDay();
            if (null != year) {
                if (null != month) {
                    if (null != day) {
                        beginDate = DateTimeUtil.date2TimeStampMs(formatDateStr(year, month, day), null);
                        endDate = beginDate; //两个相同
                    } else {
                        beginDate = DateTimeUtil.date2TimeStampMs(formatDateStr(year, month, 1), null);
                        int maxDay = DateTimeUtil.getMaxDayOfMonth(year, month);
                        endDate = DateTimeUtil.date2TimeStampMs(formatDateStr(year, month, maxDay), null);
                    }
                } else {
                    beginDate = DateTimeUtil.date2TimeStampMs(formatDateStr(year, 1, 1), null);
                    endDate = DateTimeUtil.date2TimeStampMs(formatDateStr(year, 12, 31), null);
                }
            } else  if (!StringUtils.isEmpty(calcFeeVO.getBeginDay()) && !StringUtils.isEmpty(calcFeeVO.getEndDay())) {
                beginDate = DateTimeUtil.date2TimeStampMs(calcFeeVO.getBeginDay()+" 00:00:00", null);
                endDate = DateTimeUtil.date2TimeStampMs(calcFeeVO.getEndDay()+" 00:00:00", null);
            } else {
                return Result.fail("无效参数");
            }

            List<TradeClearFeeReportDTO> list = tradeClearReportService.calcClearFeeByTime(calcFeeVO.getDepartmentId(),beginDate,endDate,calcFeeVO.getBankId());
            return Result.success(list);
        } catch (Exception ex) {
            log.error("计算费用error:",ex);
            return Result.fail("查询失败");
        }

    }

    /**
     * 按月统计费用
     * @param departmentId
     * @param year
     * @param month
     * @param bankId
     * @return
     */
//    @GetMapping("/calcMonthFee")
//    List<TradeClearFeeReportDTO> calcMonthFee(@RequestParam Long departmentId,
//                                             @RequestParam Integer year,
//                                             @RequestParam Integer month,
//                                             @RequestParam(required = false) Long bankId) {
//        String sBeginDate = String.format("%04d-%02d-01 00:00:00",year,month);
//        long beginDate = DateTimeUtil.date2TimeStampMs(sBeginDate,"");
//        int maxDay = DateTimeUtil.getMaxDayOfMonth(year, month);
//        String sEndDate = String.format("%04d-%02d-%02d 00:00:00",year,month,maxDay);
//        long endDate = DateTimeUtil.date2TimeStampMs(sEndDate,"");
//
//        return tradeClearReportService.calcClearFeeByTime(departmentId,beginDate,endDate,bankId);
//    }

    /**
     * 按日期区间统计费用
     * @param departmentId
     * @param dayBegin yyyy-MM-dd
     * @param dayEnd yyyy-MM-dd
     * @param bankId - 银行
     * @return
     */
//    @GetMapping("/calcDayZoneFee")
//    List<TradeClearFeeReportDTO> calcDayZoneFee(@RequestParam Long departmentId,
//                                              @RequestParam String dayBegin,
//                                              @RequestParam String dayEnd,
//                                              @RequestParam(required = false) Long bankId) {
//
//
//        return tradeClearReportService.calcClearFeeByTime(departmentId,beginDate,endDate,bankId);
//    }
}
