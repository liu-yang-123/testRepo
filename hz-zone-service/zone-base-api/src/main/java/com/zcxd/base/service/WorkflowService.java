package com.zcxd.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.base.dto.WorkflowEventDTO;
import com.zcxd.base.vo.WorkflowEventVO;
import com.zcxd.base.vo.WorkflowVO;
import com.zcxd.db.mapper.WorkflowEventMapper;
import com.zcxd.db.mapper.WorkflowMapper;
import com.zcxd.db.model.Denom;
import com.zcxd.db.model.Workflow;
import com.zcxd.db.model.WorkflowEvent;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author songanwei
 * @date 2021-06-09
 */
@Service
public class WorkflowService extends ServiceImpl<WorkflowMapper, Workflow> {

    @Autowired
    private WorkflowEventMapper workflowEventMapper;

    public IPage<WorkflowEventDTO> findListByPage(Integer page, Integer limit, WorkflowEvent workflowEvent) {
        Page<WorkflowEvent> ipage=new Page<>(page,limit);
        QueryWrapper<WorkflowEvent> queryWrapper = new QueryWrapper<>();
        if (workflowEvent != null){
            if ( !StringUtils.isEmpty(workflowEvent.getEventName())){
                queryWrapper.like("event_name", workflowEvent.getEventName());
            }
            if (workflowEvent.getDepartmentId() > 0){
                queryWrapper.like("department_id", workflowEvent.getDepartmentId());
            }
        }


        queryWrapper.orderBy(true,true,"id");

        IPage<WorkflowEvent>  workflowEventIPage = workflowEventMapper.selectPage(ipage,queryWrapper);

        List<WorkflowEventDTO>  eventDTOList = workflowEventIPage.getRecords().stream().map(item -> {
            WorkflowEventDTO eventDTO = new WorkflowEventDTO();
            BeanUtils.copyProperties(item, eventDTO);
            QueryWrapper queryWrapperT = Wrappers.query().eq("event_id",item.getId());
            List<Workflow> workflowList = this.baseMapper.selectList(queryWrapperT);
            eventDTO.setWorkflowList(workflowList);
            return  eventDTO;
        }).collect(Collectors.toList());

        IPage<WorkflowEventDTO>  eventDTOIPage = new Page<>(page,limit);
        eventDTOIPage.setRecords(eventDTOList);
        eventDTOIPage.setTotal(ipage.getTotal());
        return eventDTOIPage;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean edit(WorkflowEventVO workflowEventVO){
        //查询元数据
        QueryWrapper queryWrapper = Wrappers.query().eq("event_id",workflowEventVO.getId());
        List<Workflow> existWorkflowList = this.baseMapper.selectList(queryWrapper);

        //更新event表数据
        WorkflowEvent workflowEvent = new WorkflowEvent();
        workflowEvent.setId(workflowEventVO.getId());
        workflowEvent.setStatus(workflowEventVO.getStatus());
        workflowEvent.setMsgStatus(workflowEventVO.getMsgStatus());
        workflowEventMapper.updateById(workflowEvent);
        //更新workflow表数据

        List<WorkflowVO> workflowVOList = workflowEventVO.getWorkflowList();
        //数据比较
        List<WorkflowVO> addWorkflowVOList = workflowVOList.stream().filter(t -> t.getId() != null)
                .filter(r -> r.getId() == 0).collect(Collectors.toList());
        List<Workflow> deleteWorkflowList = existWorkflowList.stream().filter(t -> {
            Optional<WorkflowVO> optionalWorkflow = workflowVOList.stream()
                    .filter(m -> m.getId() != null)
                    .filter(s -> t.getId().equals(s.getId())).findFirst();
            return !optionalWorkflow.isPresent();
        }).collect(Collectors.toList());
        List<WorkflowVO> updateWorkflowList = workflowVOList.stream().filter(m -> m.getId() != null)
                .filter(t -> {
            Optional<Workflow> optionalWorkflow = existWorkflowList.stream()
                    .filter(s -> s.getId().equals(t.getId())).findFirst();
            return optionalWorkflow.isPresent();
        }).collect(Collectors.toList());
        //添加数据
        addWorkflowVOList.stream().forEach(item -> {
            Workflow workflow = new Workflow();
            workflow.setEventId(workflowEventVO.getId());
            workflow.setNodeName(item.getNodeName());
            workflow.setUserIds(item.getUserIds());
            workflow.setSort(item.getSort());
            this.baseMapper.insert(workflow);
        });
        //修改数据
        updateWorkflowList.stream().forEach(item -> {
            Workflow workflow = new Workflow();
            workflow.setId(item.getId());
            workflow.setNodeName(item.getNodeName());
            workflow.setUserIds(item.getUserIds());
            workflow.setSort(item.getSort());
            this.baseMapper.updateById(workflow);
        });

        //删除数据
        if (deleteWorkflowList.size() > 0){
            Set<Long> ids = deleteWorkflowList.stream().map(r -> r.getId()).collect(Collectors.toSet());
            this.baseMapper.deleteBatchIds(ids);
        }
        return true;
    }

}
