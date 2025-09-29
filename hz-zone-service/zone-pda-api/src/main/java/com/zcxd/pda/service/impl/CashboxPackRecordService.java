package com.zcxd.pda.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.db.mapper.CashboxPackRecordMapper;
import com.zcxd.db.model.Cashbox;
import com.zcxd.db.model.CashboxPackRecord;
import com.zcxd.pda.dto.AtmClearBoxCountDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 
 * @ClassName CashboxPackRecordService
 * @Description 钞盒封装记录服务
 * @author shijin
 * @Date 2021年7月26日上午14:50:05
 */
@Service
public class CashboxPackRecordService extends ServiceImpl<CashboxPackRecordMapper, CashboxPackRecord> {


    public List<AtmClearBoxCountDTO> countClearBoxByBank(Long beginTime,Long endTime,Integer status,Long clearMan) {
        List<Map<String,Object>> mapList = baseMapper.countClearManTotalBoxByBank(beginTime,endTime,status,clearMan);

        List<AtmClearBoxCountDTO> boxCountDTOS = mapList.stream().map(stringObjectMap -> {
            AtmClearBoxCountDTO countDTO = new AtmClearBoxCountDTO();
            Long bankId = (Long)stringObjectMap.getOrDefault("bankId",0L);
            countDTO.setBankId(bankId);
            Long total = (Long)stringObjectMap.getOrDefault("total",0L);
            countDTO.setTotal(total.intValue());
            return countDTO;
        }).collect(Collectors.toList());
        return boxCountDTOS;
    }

    public List<CashboxPackRecord> listByUser(Long empId,Long taskDate) {
        QueryWrapper<CashboxPackRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("task_date",taskDate).and(i->i.eq("clear_man_id",empId).or().eq("check_man_id",empId));
        return baseMapper.selectList(queryWrapper);
    }

    public List<CashboxPackRecord> listByRoute(Long routeId) {
        CashboxPackRecord where = new CashboxPackRecord();
        where.setRouteId(routeId);
        return baseMapper.selectList(new QueryWrapper<>(where));
    }

    public List<CashboxPackRecord> listBindsByTime(Long departmentId,Long beginTime, Long endTime,Long bankId) {
        QueryWrapper<CashboxPackRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("department_id",departmentId)
                .ge("task_date",beginTime)
                .le("task_date",endTime)
                .eq("deleted",0);
        if (null != bankId && 0L != bankId) {
            queryWrapper.eq("bank_id",bankId);
        }
        return baseMapper.selectList(queryWrapper);
    }
}
