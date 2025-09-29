package com.zcxd.gun.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author zccc
 */
@Data
public class GunTaskIssueAndTakedownQueryVO {
    @NotNull(message = "未正确选择枪弹任务")
    private Long id;

    /**
     * 押运员Aid
     */
    @NotNull(message = "押运员AId为空")
    private Long supercargoIdA;

    /**
     * 枪支编号A
     */
    @NotBlank(message = "枪弹号A为空")
    private String gunCodeA;

    /**
     * 弹盒A
     */
    @NotBlank(message = "枪盒号A为空")
    private String gunBoxCodeA;

    /**
     * 枪证编号A
     */
    @NotBlank(message = "枪证号A为空")
    private String gunLicenceNumA;

    /**
     * 押运员Bid
     */
    @NotNull(message = "押运员BId为空")
    private Long supercargoIdB;

    /**
     * 枪支编号B
     */
    @NotBlank(message = "枪弹号B为空")
    private String gunCodeB;

    /**
     * 弹盒B
     */
    @NotBlank(message = "枪盒号B为空")
    private String gunBoxCodeB;

    /**
     * 枪证编号B
     */
    @NotBlank(message = "枪证号B为空")
    private String gunLicenceNumB;

    /**
     * 所属部门Id
     */
    @NotNull(message = "部门ID不能为空")
    private Integer departmentId;
}
