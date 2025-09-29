package com.zcxd.base.service.report;

import com.zcxd.base.dto.report.RouteTimeReportDTO;
import com.zcxd.db.mapper.AtmTaskMapper;
import com.zcxd.db.mapper.RouteMapper;
import com.zcxd.db.model.Route;
import com.zcxd.db.model.result.CountAmountResult;
import com.zcxd.db.model.result.MaxMinResult;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author songanwei
 * @date 2021-10-21
 */
@AllArgsConstructor
@Service
public class RouteReportService {

    private RouteMapper routeMapper;

    private AtmTaskMapper atmTaskMapper;

    public List<RouteTimeReportDTO> getWorkload(Long departmentId, String date){
        //转化为标准日期
        date = date + "-01";
        //获取月份的开始时间戳 结束时间戳
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        long start = localDate.atStartOfDay().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        long end = localDate.atStartOfDay().plusMonths(1L).toInstant(ZoneOffset.of("+8")).toEpochMilli();
        List<Route> routeList = routeMapper.getRouteNoList(departmentId,start,end);
        List<String> routeNoList = routeList.stream().map(Route::getRouteNo).collect(Collectors.toList());
        Map<String,Long> daysCountMap = new HashMap<>();
        Map<String,Long> totalCountMap = new HashMap<>();
        Map<String, BigDecimal> averagePerMap = new HashMap<>();
        List<MaxMinResult<String>> maxMinResultList = null;
        if (routeNoList.size() > 0){
            List<CountAmountResult<String>> countResultList =  atmTaskMapper.getDaysByRoute(departmentId,start,end,routeNoList);
            daysCountMap = countResultList.stream().collect(Collectors.toMap(CountAmountResult::getKey,CountAmountResult::getCount));
            List<CountAmountResult<String>> totalResultList =  atmTaskMapper.getCountByRoute(departmentId,start,end,routeNoList);
            totalCountMap = totalResultList.stream().collect(Collectors.toMap(CountAmountResult::getKey,CountAmountResult::getCount));
            List<CountAmountResult<String>> averagePerResultList =  atmTaskMapper.getAvgTimeByRoute(departmentId,start,end,routeNoList);
            averagePerMap = averagePerResultList.stream().collect(Collectors.toMap(CountAmountResult::getKey,CountAmountResult::getAmount));
            maxMinResultList = routeMapper.getMaxMinTime(departmentId,start,end,routeNoList);
        }

        Map<String, Long> finalDaysCountMap = daysCountMap;
        Map<String, Long> finalTotalCountMap = totalCountMap;
        Map<String, BigDecimal> finalAveragePerMap = averagePerMap;
        List<MaxMinResult<String>> finalMaxMinResultList = maxMinResultList;
        return routeList.stream().map(item -> {
            String routeNo = item.getRouteNo();
            Long days = Optional.ofNullable(finalDaysCountMap.get(routeNo)).orElse(0L);
            Long total = Optional.ofNullable(finalTotalCountMap.get(routeNo)).orElse(0L);
            Double averagePer = Optional.ofNullable(finalAveragePerMap.get(routeNo)).orElse(BigDecimal.ZERO).doubleValue();
            RouteTimeReportDTO reportDTO = new RouteTimeReportDTO();
            reportDTO.setRouteNo(routeNo);
            reportDTO.setTimes(days);
            reportDTO.setTotal(total);
            reportDTO.setAveragePer(formatNumber(averagePer));
            Optional<MaxMinResult<String>> maxMinResultOptional = finalMaxMinResultList.stream().filter(r -> routeNo.equals(r.getKey())).findFirst();
            reportDTO.setEarliest(0.0);
            reportDTO.setLatest(0.0);
            reportDTO.setAverage(0.0);
            // 转化为小时
            double hour  = 3600 * 1000;
            maxMinResultOptional.ifPresent(u -> {
                double max = Optional.ofNullable(u.getMax()).orElse(BigDecimal.ZERO).doubleValue();
                double min = Optional.ofNullable(u.getMin()).orElse(BigDecimal.ZERO).doubleValue();
                double average = Optional.ofNullable(u.getAverage()).orElse(BigDecimal.ZERO).doubleValue();
                reportDTO.setEarliest(formatNumber(min/hour));
                reportDTO.setLatest(formatNumber(max/hour));
                reportDTO.setAverage(formatNumber(average/hour));
            });
            return reportDTO;
        }).collect(Collectors.toList());
    }

    private double formatNumber(double value){
        return Math.round(value * 100 ) / 100.0;
    }

}
