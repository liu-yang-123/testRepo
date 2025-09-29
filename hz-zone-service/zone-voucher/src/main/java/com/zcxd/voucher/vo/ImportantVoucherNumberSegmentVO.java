package com.zcxd.voucher.vo;

import lombok.Data;

import javax.validation.constraints.Size;

/**
 * @author zccc
 */
@Data
public class ImportantVoucherNumberSegmentVO {
    /**
     * 唯一标识
     */
    private Long id;

    /**
     * 重空凭证Id
     */
    private Long voucherId;

    /**
     * 重空名称
     */
    private String name;

    /**
     * 重空类型
     */
    private Short type;

    /**
     * 起始号段
     */
    private Long numberStart;

    /**
     * 结束号段
     */
    private Long numberEnd;

    /**
     * 所属
     */
    private Integer departmentId;

    /**
     * 备注
     */
    @Size(max=64,message="备注最大长度为64")
    private String remark;

    public ImportantVoucherNumberSegmentVO() {
    }

    public ImportantVoucherNumberSegmentVO(Long voucherId, Long numberStart, Long numberEnd, Integer departmentId) {
        this.voucherId = voucherId;
        this.numberStart = numberStart;
        this.numberEnd = numberEnd;
        this.departmentId = departmentId;
    }
}
