package com.zcxd.base.dto;

import com.zcxd.db.model.WorkflowRecord;
import lombok.Data;

import java.io.Serializable;

/**
 * @author songanwei
 * @date 2021-06-11
 */
@Data
public class WorkflowRecordDTO extends WorkflowRecord {

    /**
     * 用户名
     */
    private String userName;


}
