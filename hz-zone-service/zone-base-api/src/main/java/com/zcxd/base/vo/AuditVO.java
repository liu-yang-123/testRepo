package com.zcxd.base.vo;

import lombok.Data;

/**
 * @author songanwei
 * @date 2021-06-10
 */
@Data
public class AuditVO {

    /**
     * 审核ID
     */
    private long id;

    /**
     * 状态 0=拒绝  1=成功
     */
    private int status;

    /**
     * 审核内容
     */
    private String comments;

}
