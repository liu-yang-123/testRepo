package com.zcxd.base.vo;

import lombok.Data;

/**
 * @author songanwei
 * @date 2021-07-05
 */
@Data
public class ClearUploadExcelVO {

    /**
     * 所在部门ID
     */
    private Long departmentId;

    /**
     * 银行机构ID
     */
    private Long bankId;

    /**
     * 上传文件路径
     */
    private String excelFile;

}
