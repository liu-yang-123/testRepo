package com.zcxd.base.service;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.Size;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.common.constant.FingerTypeEnum;
import com.zcxd.db.mapper.FingerprintMapper;
import com.zcxd.db.model.Fingerprint;

/**
 * 
 * @ClassName FingerprintService
 * @Description 指纹管理服务类
 * @author 秦江南
 * @Date 2021年5月19日上午10:48:16
 */
@Service
public class FingerprintService extends ServiceImpl<FingerprintMapper, Fingerprint>{

	public IPage<Map<String, Object>> findListByPage(Integer page, Integer limit, String userType, String empNo, String empName, Long jobId) {
		return baseMapper.findListByPage(new Page(page,limit),userType,empNo,empName,jobId,0);
	}

	public void deleteByUserId(Long userId,Integer userType) {
		baseMapper.deleteUserPrint(userId,userType,System.currentTimeMillis());
	}

	/**
	 * 查询员工指纹记录，过滤指纹数据
	 * @param userId
	 * @return
	 */
	public List<Fingerprint> listByUserId(Long userId) {
		QueryWrapper<Fingerprint> queryWrapper = new QueryWrapper<>();
		queryWrapper.select("id","user_id","user_type","finger_idx","update_time")
				.eq("user_id",userId).eq("user_type",0);
		return baseMapper.selectList(queryWrapper);
	}
}
