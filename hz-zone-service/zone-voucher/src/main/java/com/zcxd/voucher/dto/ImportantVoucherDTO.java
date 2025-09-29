package com.zcxd.voucher.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @author zccc
 */
@Data
public class ImportantVoucherDTO {
    /**
     * 唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属银行Id
     */
    private Long bankId;

    /**
     * 重空名称
     */
    private String name;

    /**
     * 重空类型
     *  1：卡类
     *  2：贵金属
     *  3：其他凭证类
     */
    private Short type;

    /**
     * 总数量
     */
    private Long count;

    /**
     * 备注
     */
    private String remark;
}
