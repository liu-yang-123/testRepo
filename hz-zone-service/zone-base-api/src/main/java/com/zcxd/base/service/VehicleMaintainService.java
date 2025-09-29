package com.zcxd.base.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.db.mapper.VehicleMaintainMapper;
import com.zcxd.db.model.VehicleMaintain;

/**
 * 
 * @ClassName VehicleMaintainService
 * @Description 车辆维保管理服务类
 * @author 秦江南
 * @Date 2021年5月19日下午3:10:26
 */
@Service
public class VehicleMaintainService extends ServiceImpl<VehicleMaintainMapper, VehicleMaintain>{

	public IPage<Map<String, Object>> findListByPage(Integer page, Integer limit, String lpno, String seqno, 
			String mtType, Integer mtResult, Long departmentId) {
		return baseMapper.findListByPage(new Page(page,limit),lpno,seqno,mtType,mtResult,departmentId,0);
	}

}
