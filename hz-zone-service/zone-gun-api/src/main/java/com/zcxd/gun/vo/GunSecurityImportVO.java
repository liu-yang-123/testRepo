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
public class GunSecurityImportVO extends BaseRowModel {
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
    @NotBlank(message = "保安证号不能为空")
    @ExcelProperty(value = "保安证号", index = 2)
    private String securityNum;

    /**
     * 保安证派发机构
     */
    @NotBlank(message = "保安证派发机构不能为空")
    @ExcelProperty(value = "保安证派发机构", index = 3)
    private String authority;

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
                add("保安证号");
                add("保安证派发机构");
                add("备注");
            }});
        }};
    }

    public void cleanBlankCharacters() {
        if (this.empName != null) {
            setEmpName(this.empName.replaceAll("\\s", ""));
        }
        if (this.getSecurityNum() != null) {
            setSecurityNum(this.securityNum.replaceAll("\\s", ""));
        }
        if (this.authority != null) {
            setAuthority(this.authority.replaceAll("\\s", ""));
        }
    }

    public boolean everyAtriEmpty() {
        return StringUtils.isBlank(this.empName) && StringUtils.isBlank(this.securityNum)
                && StringUtils.isBlank(this.authority);
    }
}
