package com.zcxd.sync.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

/**
 * Mapper接口
 * @author songanwei
 * @date 2021/4/15 17:47
 */
@Mapper
public interface TemplateMapper {

    /**
     * 查询数据
     * @param tableName 表名
     * @param type 查询类型
     * @param field 条件  过滤字段
     * @return
     */
    List<Map>  select(@Param("tableName") String tableName,
                      @Param("type") int type,
                      @Param("field") String field);
}
