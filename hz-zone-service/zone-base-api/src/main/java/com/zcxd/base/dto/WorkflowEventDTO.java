package com.zcxd.base.dto;

import com.zcxd.db.model.Workflow;
import com.zcxd.db.model.WorkflowEvent;
import lombok.Data;
import java.util.List;

/**
 * @author songanwei
 * @date 2021--06-09
 * */
@Data
public class WorkflowEventDTO extends WorkflowEvent {

    /**
     * 审批节点列表
     */
    private List<Workflow> workflowList;

}
