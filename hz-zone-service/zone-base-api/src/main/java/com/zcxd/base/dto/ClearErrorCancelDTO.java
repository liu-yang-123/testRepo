package com.zcxd.base.dto;

import lombok.Data;

/**
 * 清分差错撤销DTO
 */
@Data
public class ClearErrorCancelDTO {
    private Long id; //差错 id
    private String comments; //审批备注
}
