package com.zcxd.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.base.config.UserContextHolder;
import com.zcxd.base.dto.WorkflowRecordDTO;
import com.zcxd.db.mapper.SysUserMapper;
import com.zcxd.db.mapper.WorkflowEventMapper;
import com.zcxd.db.mapper.WorkflowMapper;
import com.zcxd.db.mapper.WorkflowRecordMapper;
import com.zcxd.db.model.SysUser;
import com.zcxd.db.model.Workflow;
import com.zcxd.db.model.WorkflowEvent;
import com.zcxd.db.model.WorkflowRecord;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author songanwei
 * @date 2021-06-10
 */
@AllArgsConstructor
@Service
public class WorkflowRecordService extends ServiceImpl<WorkflowRecordMapper, WorkflowRecord> {

    private WorkflowEventMapper eventMapper;

    private WorkflowMapper workflowMapper;

    private SysUserMapper userMapper;

    /**
     * 返回下一个审核用户IDs  //返回-1表示未开启审核
     * @return
     */
    public String getUserIds(long identityId, String eventId,Long departmentId){
        QueryWrapper queryWrapper = Wrappers.query().eq("event_code", eventId)
                .eq("department_id",departmentId)
                .last("LIMIT 1");
        WorkflowEvent workflowEvent = eventMapper.selectOne(queryWrapper);
        if (workflowEvent == null || workflowEvent.getStatus() == 0){
            return "-1";
        }

        WorkflowRecord record = null;
        if (identityId > 0){
            QueryWrapper queryWrapper1 = Wrappers.query().eq("identity_id",identityId)
                    .eq("event_id",workflowEvent.getId())
                    .orderByDesc("create_time").last("LIMIT 1");
            //如果最后一个审核拒绝或不存在  则回到原点
            record = this.getBaseMapper().selectOne(queryWrapper1);
            if (record == null || record.getStatus() == 0){
                //查询第一个节点
                QueryWrapper queryWrapper2 = Wrappers.query().eq("event_id",workflowEvent.getId())
                        .eq("sort",1).last("LIMIT 1");
                Workflow workflow = workflowMapper.selectOne(queryWrapper2);
                return workflow.getUserIds();
            }
        }

        int sort = record != null ? record.getSort() + 1 : 1;
        //查询下一个节点
        QueryWrapper queryWrapper3 = Wrappers.query().eq("event_id",workflowEvent.getId())
                .eq("sort",sort).last(" LIMIT 1");
        Workflow workflow = workflowMapper.selectOne(queryWrapper3);
        if (workflow == null){
            return "";
        }
        return workflow.getUserIds();
    }

    /**
     * 添加记录，触发消息通知
     * @return
     */
    public boolean addRecord(long identityId, String eventId,int status, String comments,Long departmentId){
        QueryWrapper queryWrapper = Wrappers.query().eq("event_code", eventId).eq("department_id",departmentId).last("LIMIT 1");
        WorkflowEvent workflowEvent = eventMapper.selectOne(queryWrapper);
        if (workflowEvent == null || workflowEvent.getStatus() == 0){
            return false;
        }

        int sort = 0;
        if (identityId > 0){
            QueryWrapper queryWrapper1 = Wrappers.query().eq("identity_id",identityId)
                    .eq("event_id",workflowEvent.getId())
                    .orderByDesc("create_time").last("LIMIT 1");
            //如果最后一个审核拒绝或不存在  则回到原点
            WorkflowRecord record = this.getBaseMapper().selectOne(queryWrapper1);
            if (record != null && record.getStatus() == 1){
                //查询第一个节点
                sort = record.getSort();
            }
            //如果最后一个审核已经拒绝了

        }
        sort = sort + 1;
        //查询下一个节点
        QueryWrapper queryWrapper3 = Wrappers.query().eq("event_id",workflowEvent.getId())
                .eq("sort",sort).last("LIMIT 1");
        Workflow workflow = workflowMapper.selectOne(queryWrapper3);
        int targetSort = workflow != null ? workflow.getSort() : 1;

        //
        WorkflowRecord workflowRecord = new WorkflowRecord();
        workflowRecord.setEventId(workflowEvent.getId());
        workflowRecord.setIdentityId(identityId);
        workflowRecord.setUserId(UserContextHolder.getUserId());
        workflowRecord.setSort(targetSort);
        workflowRecord.setStatus(status);
        workflowRecord.setComments(comments);
        Integer result = this.baseMapper.insert(workflowRecord);
        return result != null && result >= 1;
    }

    /**
     * 详情数据
     * @param identityId
     * @param eventId 事件ID
     * @return
     */
    public List<WorkflowRecordDTO>  getDetailList(long identityId, String eventId,Long departmentId){
        QueryWrapper wrapper = Wrappers.query().eq("event_code", eventId).eq("department_id",departmentId).last("LIMIT 1");
        WorkflowEvent workflowEvent = eventMapper.selectOne(wrapper);
        if (workflowEvent == null){
            return null;
        }

        QueryWrapper queryWrapper = Wrappers.query().eq("identity_id",identityId)
                .eq("event_id",workflowEvent.getId()).orderByAsc("id");
        List<WorkflowRecord> recordList = this.baseMapper.selectList(queryWrapper);

        return recordList.stream().map(item -> {
            WorkflowRecordDTO recordDTO = new WorkflowRecordDTO();
            BeanUtils.copyProperties(item,recordDTO);
            SysUser user = userMapper.selectById(item.getUserId());
            String nickName = user != null ? user.getNickName() : "--";
            recordDTO.setUserName(nickName);
            return recordDTO;
        }).collect(Collectors.toList());
    }

}
