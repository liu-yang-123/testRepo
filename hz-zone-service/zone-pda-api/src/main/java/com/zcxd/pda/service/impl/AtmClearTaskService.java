package com.zcxd.pda.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.common.constant.DeleteFlagEnum;
import com.zcxd.db.mapper.AtmClearTaskMapper;
import com.zcxd.db.model.AtmClearTask;
import com.zcxd.pda.dto.AtmClearSumDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 
 * @ClassName AtmClearTaskService
 * @Description atm清分任务管理服务
 * @author shijin
 * @Date 2021年5月20日上午10:20:05
 */
@Service
public class AtmClearTaskService extends ServiceImpl<AtmClearTaskMapper, AtmClearTask> {


    /**
     * 查询同一线路的清分任务列表
     * @param routeId  - 线路id
     * @param bankId - 银行id（总行）
     * @return
     */
    public List<AtmClearTask> listByRouteId(Long routeId, Long bankId) {

        AtmClearTask where = new AtmClearTask();
        where.setRouteId(routeId);
        where.setDeleted(DeleteFlagEnum.NOT.getValue());
        if (bankId != null) {
            where.setBankId(bankId);
        }
        return baseMapper.selectList(new QueryWrapper<>(where));
    }

    public List<AtmClearTask> listByCondition(AtmClearTask where) {
        return baseMapper.selectList(new QueryWrapper<>(where));
    }

    public List<AtmClearTask> listByAtmId(Long atmId, String taskDate) {
        AtmClearTask where = new AtmClearTask();
        where.setTaskDate(taskDate);
        where.setAtmId(atmId);
        where.setDeleted(DeleteFlagEnum.NOT.getValue());
        return baseMapper.selectList(new QueryWrapper<>(where));
    }

//    public List<AtmClearSumDTO> sumClearAmountByRoute(Long clearMan, String taskDate) {
    public List<AtmClearSumDTO> sumClearAmountByRoute(Long clearMan, Long beginTime,Long endTime) {
        List<Map<String,Object>> mapList = baseMapper.sumClearManTotalAmountByRoute(clearMan,beginTime,endTime);
        List<AtmClearSumDTO> sumDTOList = mapList.stream().map(map -> {
            AtmClearSumDTO atmClearSumDTO = new AtmClearSumDTO();
            Long routeId = (Long)map.getOrDefault("routeId",0L);
            atmClearSumDTO.setRouteId(routeId);
            Long bankId = (Long)map.getOrDefault("bankId",0L);
            atmClearSumDTO.setBankId(bankId);
            BigDecimal totalPlan = (BigDecimal) map.getOrDefault("totalPlan",BigDecimal.ZERO);
            atmClearSumDTO.setPlanAmount(totalPlan);
            BigDecimal totalClear = (BigDecimal)map.getOrDefault("totalClear",BigDecimal.ZERO);
            atmClearSumDTO.setClearAmount(totalClear);
            atmClearSumDTO.setDiffAmount(totalClear.subtract(totalPlan));
            return atmClearSumDTO;
        }).collect(Collectors.toList());
        return sumDTOList;
    }
}
