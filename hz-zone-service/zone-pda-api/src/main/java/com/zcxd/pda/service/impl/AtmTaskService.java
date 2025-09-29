package com.zcxd.pda.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.common.constant.AtmTaskStatusEnum;
import com.zcxd.common.constant.AtmTaskTypeEnum;
import com.zcxd.db.mapper.AtmTaskMapper;
import com.zcxd.db.model.AtmTask;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 
 * @ClassName AtmTaskService
 * @Description ATM总任务管理服务
 * @author shijin
 * @Date 2021年5月20日上午10:20:05
 */
@Service
public class AtmTaskService extends ServiceImpl<AtmTaskMapper, AtmTask> {

    /**
     * 根据线路查询所有任务列表
     * @param routeId
     * @return
     */
    public List<AtmTask> listByCarryRouteId(Long routeId, AtmTaskStatusEnum atmTaskStatusEnum) {
        AtmTask where = new AtmTask();
        where.setCarryRouteId(routeId);
        if (atmTaskStatusEnum != null) {
            where.setStatusT(atmTaskStatusEnum.getValue());
        }
        where.setDeleted(0);
        return baseMapper.selectList(new QueryWrapper<>(where));
    }

    /**
     * 根据线路查询所有任务列表
     * @param routeId
     * @return
     */
    public List<AtmTask> listByAtmId(Long routeId, Long atmId,AtmTaskStatusEnum atmTaskStatusEnum) {
        AtmTask where = new AtmTask();
        where.setCarryRouteId(routeId);
        where.setAtmId(atmId);
        where.setStatusT(atmTaskStatusEnum.getValue());
        where.setDeleted(0);
        return baseMapper.selectList(new QueryWrapper<>(where));
    }

    /**
     * 查询线路任务数量
     * @param routeId
     * @return
     */
    public long getTaskCountByRoute(Long routeId) {
        AtmTask where = new AtmTask();
        where.setCarryRouteId(routeId);
        where.setDeleted(0);
        return baseMapper.selectCount(new QueryWrapper<>(where));
    }

    /**
     * 查询该线路的上对应设备的清机任务
     * @param fixRouteId - 固定线路id
     * @param atmId
     * @return
     */
    public List<AtmTask> listTaskByAtmId(Long fixRouteId,Long atmId) {
        AtmTask where = new AtmTask();
        where.setRouteId(fixRouteId);
        where.setAtmId(atmId);
        where.setDeleted(0);
        return baseMapper.selectList(new QueryWrapper<>(where));
    }

//    /**
//     * 根据线路统计加钞信息
//     * @param routeId
//     */
//    public List<AtmTaskDenomSumDTO> sumTaskAmountByRoute(Long routeId) {
//        QueryWrapper<AtmTask> queryWrapper = new QueryWrapper<>();
//        queryWrapper.select("bank_id","denom_id","sum(amount) as amount","count(id) as total")
//                .eq("carry_route_id",routeId)
//                .eq("status_t",AtmTaskStatusEnum.CONFRIM.getValue())
//                .groupBy("bank_id","denom_id");
//
//        List<Map<String,Object>> mapList = baseMapper.selectMaps(queryWrapper);
//        List<AtmTaskDenomSumDTO> atmTaskDenomSumDTOS = mapList.stream().map(stringObjectMap -> {
//            AtmTaskDenomSumDTO atmTaskDenomSumDTO = new AtmTaskDenomSumDTO();
//            atmTaskDenomSumDTO.setDenomId(Long.parseLong(stringObjectMap.get("denom_id").toString()));
//            atmTaskDenomSumDTO.setBankId(Long.parseLong(stringObjectMap.get("bank_id").toString()));
//            atmTaskDenomSumDTO.setTotalAmount(new BigDecimal(stringObjectMap.get("amount").toString()));
//            atmTaskDenomSumDTO.setTotalCount(Integer.parseInt(stringObjectMap.get("total").toString()));
//            return atmTaskDenomSumDTO;
//        }).collect(Collectors.toList());
//
//        return atmTaskDenomSumDTOS;
//    }
}
