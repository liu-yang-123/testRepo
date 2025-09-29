package com.zcxd.sync.model;

import lombok.Data;

/**
 * @author songanwei
 * @date 2021/4/16 9:12
 */
@Data
public class TableMap {

    /**
     * 表名
     */
    private String tableName;

    /**
     * 查询类型  1 = 全部查询  2=部分查询
     */
    private int type;

    /**
     * 条件筛选字段
     */
    private String field;

    /**
     * 文件类型，详细看 util-->TableTypeEnum
     */
    private Integer fileType;
}
