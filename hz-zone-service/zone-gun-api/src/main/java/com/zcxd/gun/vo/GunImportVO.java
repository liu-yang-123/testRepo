package com.zcxd.gun.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zccc
 */
@Data
public class GunImportVO extends BaseRowModel {
    @ExcelProperty(value = "序号", index = 0)
    private Integer index;
    /**
     * 枪号/弹盒号
     */
    @ExcelProperty(value = "枪号/弹盒号", index = 1)
    private String gunCode;

    /**
     * 持枪证号
     */
    @ExcelProperty(value = "持枪证号", index = 2)
    private String gunLicenceNum;

    /**
     * 配发日期
     */
    @ExcelProperty(value = "配发日期", index = 3)
    private String buyDate;

    /**
     * 枪型/弹盒
     */
    @ExcelProperty(value = "枪型/弹盒", index = 4)
    private String gunType;

    /**
     * 枪支类型
     */
    @ExcelProperty(value = "枪支类型", index = 5)
    private String gunCategory;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注", index = 6)
    private String remark;

    public static List<List<String>> head() {
        return new ArrayList<List<String>>(){{
            add(new ArrayList<String>() {{
//                add("序号");
                add("枪号/弹盒号");
                add("持枪证号");
                add("配发日期");
                add("枪型/弹盒");
                add("备注");
            }});
        }};
    }

    public void cleanBlankCharacters() {
        if (this.buyDate != null) {
            setBuyDate(this.buyDate.replaceAll("\\s", ""));
        }
        if (this.gunType != null) {
            setGunType(this.gunType.replaceAll("\\s", ""));
        }
        if (this.gunLicenceNum != null) {
            setGunLicenceNum(this.gunLicenceNum.replaceAll("\\s", ""));
        }
        if (this.gunCode != null) {
            setGunCode(this.gunCode.replaceAll("\\s", ""));
        }
    }

    public boolean everyAtriEmpty() {
        return StringUtils.isBlank(this.gunCode) && StringUtils.isBlank(this.gunType)
                && StringUtils.isBlank(this.buyDate) && StringUtils.isBlank(this.gunLicenceNum);
    }

}
