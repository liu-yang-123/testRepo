package com.zcxd.base.vo.excel;

import lombok.Data;
import java.util.List;

/**
 * @author songanwei
 * @date  2021-07-05
 */
@Data
public class ClearTaskExcelVO {

    /**
     * 所在机构ID
     */
    private Long departmentId;

    /**
     * 银行机构ID
     */
    private Long bankId;

    /**
     * 线路编号
     */
    private String lineNumber;

    /**
     * atm设备列表
     */
    private List<ClearAtmListVO>  atmList;

}
