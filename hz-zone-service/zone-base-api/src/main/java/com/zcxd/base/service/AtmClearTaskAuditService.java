package com.zcxd.base.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.base.vo.AuditVO;
import com.zcxd.base.vo.ClearTaskVO;
import com.zcxd.db.mapper.*;
import com.zcxd.db.model.*;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * @author songanwei
 * @date 2021-06-07
 */
@AllArgsConstructor
@Service
public class AtmClearTaskAuditService extends ServiceImpl<AtmClearTaskAuditMapper, AtmClearTaskAudit> {

    private WorkflowRecordService recordService;

    private AtmClearTaskMapper clearTaskMapper;

    private AtmClearTaskService clearTaskService;

    public Page<AtmClearTaskAudit> findListByPage(Integer page, Integer limit, AtmClearTaskAudit clearTask) {
        Page<AtmClearTaskAudit> ipage = new Page<>(page,limit);
        QueryWrapper queryWrapper = getQueryWrapper(clearTask);
        return baseMapper.selectPage(ipage, queryWrapper);
    }

    private QueryWrapper getQueryWrapper(AtmClearTaskAudit clearTask){
        QueryWrapper<AtmClearTask> queryWrapper = new QueryWrapper<>();
        if(clearTask != null){
            if( clearTask.getDepartmentId() != null && clearTask.getDepartmentId() > 0){
                queryWrapper.eq("department_id", clearTask.getDepartmentId());
            }
            if( clearTask.getBankId() != null && clearTask.getBankId() > 0){
                queryWrapper.eq("bank_id", clearTask.getBankId());
            }
            if( clearTask.getRouteId() != null && clearTask.getRouteId() > 0){
                queryWrapper.eq("route_id", clearTask.getRouteId());
            }
            if(!StringUtils.isEmpty(clearTask.getTaskDate())){
                queryWrapper.eq("task_date", clearTask.getTaskDate());
            }
            if (clearTask.getStatusT() != null && clearTask.getStatusT() > -1){
                queryWrapper.eq("status_t", clearTask.getStatusT());
            }
        }
        queryWrapper.eq("deleted", 0);
        queryWrapper.orderBy(true,false,"id");
        return queryWrapper;
    }
    /**
     * 清分任务修改进入审核操作
     * @param clearTaskVO
     * @return
     */
    public boolean create(ClearTaskVO clearTaskVO){
        //原数据记录属性拷贝
        AtmClearTask clearTask = clearTaskMapper.selectById(clearTaskVO.getId());

        //验证审核操作是否启用,不启用直接更新数据
        String nextUserId = recordService.getUserIds(0,"ATM_CLEAR_TASK",clearTask.getDepartmentId());
        if ("-1".equals(nextUserId)){
            //直接更新数据
            return clearTaskService.update(clearTaskVO);
        }

        //实例化对象
        AtmClearTaskAudit taskAudit = new AtmClearTaskAudit();

        BeanUtils.copyProperties(clearTask,taskAudit);
        //用户修改数据属性拷贝
        BeanUtils.copyProperties(clearTaskVO,taskAudit);
        //计算差错金额 差错类型
        BigDecimal errorAmount = clearTaskVO.getClearAmount().subtract(clearTaskVO.getPlanAmount());
        taskAudit.setErrorAmount(errorAmount);
        Integer errorTypeValue = errorAmount.compareTo(BigDecimal.ZERO);
        // 差错类型 0=平账  1=长款 2=短款
        Integer errorType = errorTypeValue == 0 ? 0 : ( errorTypeValue > 0 ? 1 : 2);
        taskAudit.setErrorType(errorType);
        //特殊数据处理
        taskAudit.setId(null);
        taskAudit.setClearTaskId(clearTaskVO.getId());
        //差错明细数据
        String errorList = JSON.toJSONString(clearTaskVO.getErrorList());
        taskAudit.setErrorList(errorList);
        // 审核中状态
        taskAudit.setStatusT(1);
        int result = this.baseMapper.insert(taskAudit);
        return result >= 1;
    }

    /**
     * 清分任务审核
     * @param auditVO
     * @return
     */
    public boolean audit(AuditVO auditVO){
        //查询当前状态
        AtmClearTaskAudit taskAudit = this.getBaseMapper().selectById(auditVO.getId());
        //当前清分任务审核状态
        int status = taskAudit.getStatusT();
        String comments = auditVO.getComments() != null  ? auditVO.getComments() : "";
        boolean b = recordService.addRecord(auditVO.getId(),"ATM_CLEAR_TASK",auditVO.getStatus(),comments,taskAudit.getDepartmentId());
        if (!b){
            return false;
        }
        //正常审核流程
        if (status == 1){
            //如果当前状态通过
            if (auditVO.getStatus() == 1){
                String userIds = recordService.getUserIds(auditVO.getId(),"ATM_CLEAR_TASK",taskAudit.getDepartmentId());
                if (org.springframework.util.StringUtils.isEmpty(userIds)){
                    //更改审核状态
                    AtmClearTaskAudit audit = new AtmClearTaskAudit();
                    audit.setId(auditVO.getId());
                    audit.setStatusT(2);
                    this.baseMapper.updateById(audit);
                    clearTaskService.updateAudit(taskAudit);
                }
            }else{
                //更改为审核拒绝
                AtmClearTaskAudit audit = new AtmClearTaskAudit();
                audit.setId(auditVO.getId());
                audit.setStatusT(3);
                this.baseMapper.updateById(audit);
            }
        }

        return true;
    }
}
