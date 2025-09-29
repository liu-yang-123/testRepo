package com.zcxd.pda.dto;

import lombok.Data;

/**
 * @author songanwei
 * @date 2021-12-03
 */
@Data
public class EscortDTO {

    /**
     * 护送ID
     */
    private Long id;

    /**
     * 护送编号
     */
    private String no;

    /**
     * 护送姓名
     */
    private String name;

    /**
     * 图片
     */
    private String photo;

}
