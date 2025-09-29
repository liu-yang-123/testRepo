package com.zcxd.db.mapper;

import com.zcxd.db.model.SysLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 操作日志表 Mapper 接口
 * </p>
 *
 * @author admin
 * @since 2021-04-27
 */
public interface SysLogMapper extends BaseMapper<SysLog> {

    /**
     *
     * @param id id
     * @param message 结果数据
     */
    void updateResult(@Param("id") long id, @Param("result") String message);

}
