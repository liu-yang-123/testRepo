package com.zcxd.sync.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zcxd.sync.model.UploadLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 日志上传Mapper
 * @author lilanglang
 * @date 2021-09-02 11:12
 */
@Mapper
public interface UploadLogMapper extends BaseMapper<UploadLog> {
}