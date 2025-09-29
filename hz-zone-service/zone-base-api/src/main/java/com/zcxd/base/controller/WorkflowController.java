package com.zcxd.base.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zcxd.base.dto.WorkflowEventDTO;
import com.zcxd.base.service.WorkflowService;
import com.zcxd.base.vo.WorkflowEventVO;
import com.zcxd.common.annotation.OperateLog;
import com.zcxd.common.annotation.OperateType;
import com.zcxd.common.util.Result;
import com.zcxd.common.util.ResultList;
import com.zcxd.db.model.WorkflowEvent;
import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author songanwei
 * @date 2021-06-09
 */
@Api(tags = "审批流程")
@AllArgsConstructor
@RestController
@RequestMapping("/workflow")
public class WorkflowController {

    private WorkflowService workflowService;

    @ApiOperation(value = "审批流程记录列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页数", required = true, dataType = "Integer ", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "limit", value = "显示数据条数", required = true, dataType = "Integer ", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "departmentId", value = "部门ID", required = true, dataType = "Long", dataTypeClass = Long.class),
            @ApiImplicitParam(name = "eventName", value = "事件名称", dataType = "String", defaultValue = "", dataTypeClass = String.class)
    })
    @GetMapping("/list")
    public Result list(@RequestParam Integer page,
                       @RequestParam Integer limit,
                       @RequestParam Long departmentId,
                       @RequestParam(defaultValue = "", required = false) String eventName){
        WorkflowEvent workflowEvent = new WorkflowEvent();
        workflowEvent.setEventName(eventName);
        workflowEvent.setDepartmentId(departmentId);
        IPage<WorkflowEventDTO> eventPage = workflowService.findListByPage(page,limit,workflowEvent);
        ResultList resultList = ResultList.builder().total(eventPage.getTotal()).list(eventPage.getRecords()).build();
        return Result.success(resultList);
    }

    @OperateLog(value = "审批流程修改", type = OperateType.EDIT)
    @ApiOperation(value = "审批流程修改")
    @PostMapping("/update")
    public Result update(@RequestBody @Validated WorkflowEventVO workflowEventVO){
        boolean b = workflowService.edit(workflowEventVO);
        return b ? Result.success("修改成功") : Result.fail("修改失败");
    }

}
