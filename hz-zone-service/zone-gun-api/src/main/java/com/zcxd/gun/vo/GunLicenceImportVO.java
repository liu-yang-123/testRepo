package com.zcxd.gun.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zccc
 */
@Data
public class GunLicenceImportVO extends BaseRowModel {
    @ExcelProperty(value = "序号", index = 0)
    private Integer index;
    /**
     * 员工名称
     */
    @NotBlank(message = "员工名称不能为空")
    @ExcelProperty(value = "员工名称", index = 1)
    private String empName;

    /**
     * 持枪证号
     */
    @NotBlank(message = "持枪证号不能为空")
    @ExcelProperty(value = "持枪证号", index = 2)
    private String gunLicenceNum;

    /**
     * 配发日期
     */
    @NotBlank(message = "持枪证有效期不能为空")
    @ExcelProperty(value = "持枪证有效期", index = 3)
    private String gunLicenceValidity;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注", index = 4)
    private String remark;

    /**
     * 用户ID
     */
    private Long employeeId;

    public static List<List<String>> head() {
        return new ArrayList<List<String>>(){{
            add(new ArrayList<String>() {{
                add("序号");
                add("员工名称");
                add("持枪证号");
                add("持枪证有效期");
                add("备注");
            }});
        }};
    }

    public void cleanBlankCharacters() {
        if (this.empName != null) {
            setEmpName(this.empName.replaceAll("\\s", ""));
        }
        if (this.gunLicenceNum != null) {
            setGunLicenceNum(this.gunLicenceNum.replaceAll("\\s", ""));
        }
        if (this.gunLicenceValidity != null) {
            setGunLicenceValidity(this.gunLicenceValidity.replaceAll("\\s", ""));
        }
    }

    public boolean everyAtriEmpty() {
        return StringUtils.isBlank(this.empName) && StringUtils.isBlank(this.gunLicenceNum)
                && StringUtils.isBlank(this.gunLicenceValidity);
    }
}
