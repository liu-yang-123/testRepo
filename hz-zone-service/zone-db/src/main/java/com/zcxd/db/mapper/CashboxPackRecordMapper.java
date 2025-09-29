package com.zcxd.db.mapper;

import com.zcxd.db.model.CashboxPackRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 钞盒包装记录接口
 * </p>
 *
 * @author admin
 * @since 2021-04-27
 */
public interface CashboxPackRecordMapper extends BaseMapper<CashboxPackRecord> {

    List<Map<String,Object>> countClearManTotalBoxByBank(@Param("beginTime") Long beginTime,
                                                         @Param("endTime") Long endTime,
                                                         @Param("status") Integer status,
                                                         @Param("clearMan") Long clearMan);
}
