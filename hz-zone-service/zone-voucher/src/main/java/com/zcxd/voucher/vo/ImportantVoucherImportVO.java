package com.zcxd.voucher.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

/**
 * @author zccc
 */
@Data
public class ImportantVoucherImportVO extends BaseRowModel {
    @ExcelProperty(value = "序号", index = 0)
    private Integer index;

    @ExcelProperty(value = "银行名称", index = 1)
    private String bankName;

    @ExcelProperty(value = "重空名称", index = 2)
    private String name;

    @ExcelProperty(value = "重空类型", index = 3)
    private String importantVoucherType;

    @ExcelProperty(value = "备注", index = 4)
    private String remark;

    private Long bankId;

    public void cleanBlankCharacters() {
        if (this.bankName != null) {
            setBankName(this.bankName.replaceAll("\\s", ""));
        }
        if (this.name != null) {
            setName(this.name.replaceAll("\\s", ""));
        }
        if (this.importantVoucherType != null) {
            setImportantVoucherType(this.importantVoucherType.replaceAll("\\s", ""));
        }
    }

    public boolean everyAtriEmpty() {
        return StringUtils.isBlank(this.bankName) && StringUtils.isBlank(this.name)
                && StringUtils.isBlank(this.importantVoucherType);
    }
}
