package com.zcxd.base.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.base.dto.DeviceMaintainDto;
import com.zcxd.db.mapper.DeviceMaintainMapper;
import com.zcxd.db.model.DeviceMaintain;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author songanwei
 * @date 2021-05-14
 */
@Service
public class DeviceMaintainService extends ServiceImpl<DeviceMaintainMapper, DeviceMaintain> {

    /**
     * 查询总数
     * @param deviceNo
     * @param mtEngineer
     * @param mtType
     * @param mtResult
     * @return
     */
    public int findTotal(String deviceNo, String mtEngineer, String mtType, Integer mtResult, Long departmentId){
        return  this.baseMapper.findTotal(deviceNo, mtEngineer, mtType, mtResult,departmentId);
    }

    /**
     * 查询列表数据
     * @param page
     * @param limit
     * @param deviceNo
     * @param mtEngineer
     * @param mtType
     * @param mtResult
     * @return
     */
    public List<DeviceMaintainDto> findListByCondition(Integer page, Integer limit, String deviceNo,
                                                       String mtEngineer, String mtType, Integer mtResult,Long departmentId){
        List<Map> mapList = this.baseMapper.findListByCondition(page, limit, deviceNo, mtEngineer, mtType, mtResult,departmentId);
        return mapList.stream().map(map -> {
            DeviceMaintainDto deviceMaintainDto = new DeviceMaintainDto();
            deviceMaintainDto.setId((Long) map.get("id"));
            deviceMaintainDto.setDeviceId((Long) map.get("device_id"));
            deviceMaintainDto.setDeviceNo((String) map.get("device_no"));
            deviceMaintainDto.setMtDate((Long) map.get("mt_date"));
            deviceMaintainDto.setMtType((String) map.get("mt_type"));
            deviceMaintainDto.setMtContent((String) map.get("mt_content"));
            deviceMaintainDto.setMtReason((String) map.get("mt_reason"));
            deviceMaintainDto.setMtCost((BigDecimal) map.get("mt_cost"));
            deviceMaintainDto.setMtResult((Integer) map.get("mt_result"));
            deviceMaintainDto.setMtEngineer((String) map.get("mt_engineer"));
            return deviceMaintainDto;
        }).collect(Collectors.toList());
    }
}
