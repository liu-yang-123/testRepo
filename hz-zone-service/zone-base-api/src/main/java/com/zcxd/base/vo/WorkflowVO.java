package com.zcxd.base.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

@Data
public class WorkflowVO implements Serializable {

    /**
     * ID
     */
    @ApiModelProperty("节点ID")
    private Long id;

    /**
     * 节点名称
     */
    @ApiModelProperty("节点名称")
    private String nodeName;

    /**
     * 排序
     */
    @ApiModelProperty("排序")
    private Integer sort;

    /**
     * 选择用户
     */
    @ApiModelProperty("用户ID字符串，逗号分隔")
    private String userIds;
}
