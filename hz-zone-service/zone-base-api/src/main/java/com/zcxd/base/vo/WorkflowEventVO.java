package com.zcxd.base.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * @author songanwei
 * @date 2021-06-09
 */
@ApiModel("审批流事件修改视图对象")
@Data
public class WorkflowEventVO implements Serializable {

    /**
     * 事件ID
     */
    @ApiModelProperty("事件ID")
    private Long id;

    /**
     * 状态
     */
    @ApiModelProperty("事件状态")
    private Integer status;

    /**
     * 消息通知状态
     */
    @ApiModelProperty("消息通知状态")
    private Integer msgStatus;

    /**
     * 审批节点列表
     */
    @ApiModelProperty("节点列表数据对象")
    private List<WorkflowVO>  workflowList;

}
