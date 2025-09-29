package com.zcxd.base.dto;

import lombok.Data;

/**
 * 清分差错审核DTO
 */
@Data
public class ClearErrorCheckDTO {
    private Long id; //差错 id
    private Integer check; //审批结果 0 - 不通过，1 - 通过
//    private String comments; //审批备注
}
