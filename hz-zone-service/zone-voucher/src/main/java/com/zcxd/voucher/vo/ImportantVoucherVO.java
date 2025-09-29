package com.zcxd.voucher.vo;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * @author zccc
 */
@Data
public class ImportantVoucherVO {
    /**
     * 唯一标识
     */
    private Long id;

    /**
     * 所属银行Id
     */
    private Long bankId;

    /**
     * 重空名称
     */
    @NotBlank(message = "重空名称不能为空")
    private String name;

    /**
     * 重空类型
     *  1：卡类
     *  2：贵金属
     *  3：其他凭证类
     */
    @Min(value = 1)
    @Max(value = 3)
    private Short type;

    /**
     * 所属
     */
    private Integer departmentId;

    /**
     * 备注
     */
    @Size(max=64,message="备注最大长度为64")
    private String remark;
}
