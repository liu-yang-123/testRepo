package com.zcxd.db.mapper;

import com.zcxd.db.model.RouteTemplateAtm;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author admin
 * @since 2021-06-18
 */
public interface RouteTemplateAtmMapper extends BaseMapper<RouteTemplateAtm> {

	Long getMaxSort(@Param("routeTemplateId") Long routeTemplateId);

	void updateSort(@Param("routeTemplateId") Long routeTemplateId,@Param("beginSort") int beginSort,@Param("endSort") int endSort,@Param("symbol") String symbol);

	List<Map<String, Object>> getRouteTemplateAtmList(@Param("routeTemplateId") Long routeTemplateId, @Param("bankId") Long bankId);

}
