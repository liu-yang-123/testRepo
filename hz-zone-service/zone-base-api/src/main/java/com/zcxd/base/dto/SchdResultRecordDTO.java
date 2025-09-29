package com.zcxd.base.dto;

import com.zcxd.db.model.SchdResultRecord;
import lombok.Data;

/**
 * @author songanwei
 * @date 2021-08-16
 */
@Data
public class SchdResultRecordDTO  extends SchdResultRecord {

    /**
     * 创建用户名称
     */
    private String createUserName;

}
