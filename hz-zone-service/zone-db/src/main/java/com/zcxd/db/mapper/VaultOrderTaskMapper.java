package com.zcxd.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zcxd.db.model.VaultOrderRecord;
import com.zcxd.db.model.VaultOrderTask;

import java.util.List;

/**
 * <p>
 * 出入库明细 Mapper 接口
 * </p>
 *
 * @author admin
 * @since 2021-04-27
 */
public interface VaultOrderTaskMapper extends BaseMapper<VaultOrderTask> {

    /**
     * 批量添加
     * @param recordList 列表数据
     * @return
     */
    Integer insertAll(List<VaultOrderTask> recordList);
}
