package com.zcxd.db.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zcxd.db.model.TradeClearTaskRecord;
import com.zcxd.db.model.VaultOrderRecord;

/**
 * <p>
 * 清分任务明细 Mapper 接口
 * </p>
 *
 * @author admin
 * @since 2022-05-13
 */
public interface TradeClearTaskRecordMapper extends BaseMapper<TradeClearTaskRecord> {

	/**
     * 批量添加
     *
     * @param recordList 列表数据
     * @return
     */
    Integer insertAll(List<TradeClearTaskRecord> recordList);

}
