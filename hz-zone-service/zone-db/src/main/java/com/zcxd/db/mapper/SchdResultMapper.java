package com.zcxd.db.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zcxd.db.model.SchdResult;

/**
 * <p>
 * 排班结果 Mapper 接口
 * </p>
 *
 * @author admin
 * @since 2021-07-05
 */
public interface SchdResultMapper extends BaseMapper<SchdResult> {

    /**
     * 添加
     * @param resultList
     * @return
     */
    Integer insertAll(List<SchdResult> resultList);
    
    /**
     * 批量修改
     *
     * @param recordList 列表数据
     * @return
     */
    Integer updateAll(List<SchdResult> resultList);
}
