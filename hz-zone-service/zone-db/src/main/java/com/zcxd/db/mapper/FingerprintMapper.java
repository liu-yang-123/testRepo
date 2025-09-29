package com.zcxd.db.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zcxd.db.model.Fingerprint;

/**
 * <p>
 * 指纹特征 Mapper 接口
 * </p>
 *
 * @author admin
 * @since 2021-04-27
 */
public interface FingerprintMapper extends BaseMapper<Fingerprint> {

	IPage<Map<String, Object>> findListByPage(Page page,@Param("userType") String userType,
			@Param("empNo") String empNo,@Param("empName") String empName,@Param("jobId") Long jobId, @Param("deleted") Integer deleted);

	Long getMaxUpdateTime(@Param("userType") Integer userType);

	/**
	 *
	 * @param userId
	 * @param userType
	 * @param updateTime - 删除指纹同时，更新时间，否则PDA不会触发下载
	 */
	void deleteUserPrint(@Param("userId") Long userId,@Param("userType") Integer userType,@Param("updateTime") Long updateTime);
}
