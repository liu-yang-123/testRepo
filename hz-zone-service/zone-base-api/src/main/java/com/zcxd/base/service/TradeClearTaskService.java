package com.zcxd.base.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.base.exception.BusinessException;
import com.zcxd.base.vo.TradeClearTaskAddVO;
import com.zcxd.base.vo.TradeClearTaskFinishVO;
import com.zcxd.base.vo.TradeClearTaskQueryVO;
import com.zcxd.base.vo.TradeClearTaskRecordVO;
import com.zcxd.common.constant.TradeClearTaskStatusEnum;
import com.zcxd.common.util.DateTimeUtil;
import com.zcxd.db.mapper.TradeClearResultRecordMapper;
import com.zcxd.db.mapper.TradeClearTaskMapper;
import com.zcxd.db.mapper.TradeClearTaskRecordMapper;
import com.zcxd.db.model.TradeClearResultRecord;
import com.zcxd.db.model.TradeClearTask;
import com.zcxd.db.model.TradeClearTaskRecord;

import lombok.AllArgsConstructor;

/**
 * 清分任务服务类
 * @ClassName TradeClearTaskService
 * @Description TODO
 * @author 秦江南
 * @Date 2022年5月23日下午2:41:55
 */
@Service
@AllArgsConstructor
public class TradeClearTaskService extends ServiceImpl<TradeClearTaskMapper, TradeClearTask>{
	
    private TradeClearTaskRecordMapper recordMapper;
    
    private TradeClearResultRecordMapper resultRecordMapper;

	/**
	 * 分页查询清分任务
	 * @Title findListByPage
	 * @Description TODO
	 * @param clearTaskVO
	 * @return
	 * @return 返回类型 IPage<TradeClearTask>
	 */
	public IPage<TradeClearTask> findListByPage(TradeClearTaskQueryVO clearTaskVO) {
		IPage<TradeClearTask> wherePage = new Page<>(clearTaskVO.getPage(),clearTaskVO.getLimit());
        
        QueryWrapper<TradeClearTask> queryWrapper = new QueryWrapper<>();
        if (clearTaskVO.getDateBegin() != null) {
            Long dtBegin = DateTimeUtil.getDailyStartTimeMs(clearTaskVO.getDateBegin());
            queryWrapper.ge("task_date", clearTaskVO.getDateBegin());
        }
        
        if (clearTaskVO.getDateEnd() != null) {
            Long dtEnd = DateTimeUtil.getDailyEndTimeMs(clearTaskVO.getDateEnd());
            queryWrapper.le("task_date", clearTaskVO.getDateEnd());
        }
        
        if (clearTaskVO.getBankId() != null) {
            queryWrapper.eq("bank_id", clearTaskVO.getBankId());
        }
        
        if (clearTaskVO.getClearType() != null) {
            queryWrapper.eq("clear_type", clearTaskVO.getClearType());
        }
        queryWrapper.eq("department_id", clearTaskVO.getDepartmentId()).eq("deleted",0).orderByDesc("create_time");
        return baseMapper.selectPage(wherePage,queryWrapper);
	}

	/**
	 * 添加清分任务
	 * @Title create
	 * @Description TODO
	 * @param clearTaskAddVO
	 * @return
	 * @return 返回类型 boolean
	 */
	@Transactional(rollbackFor = Exception.class)
	public boolean create(TradeClearTaskAddVO clearTaskAddVO) {

        clearTaskAddVO.setTaskRecordList(clearTaskAddVO.getTaskRecordList().stream()
                .filter(s -> (s.getAmount() != null && s.getAmount().compareTo(BigDecimal.ZERO) != 0)
                            || (s.getCount() != null && s.getCount() > 0)).collect(Collectors.toList()));

        TradeClearTask tradeClearTask = new TradeClearTask();
        BeanUtils.copyProperties(clearTaskAddVO, tradeClearTask);
        tradeClearTask.setId(null);
        tradeClearTask.setStatus(TradeClearTaskStatusEnum.CREATE.getValue());
        if(clearTaskAddVO.getHaveDetail() == 1){
        	//计算订单总金额
            BigDecimal totalAmount = clearTaskAddVO.getTaskRecordList().stream()
            		.map(TradeClearTaskRecordVO::getAmount).reduce(BigDecimal.ZERO,BigDecimal::add);
            tradeClearTask.setTotalAmount(totalAmount);
        }
        this.baseMapper.insert(tradeClearTask);
        //创建订单详情
        List<TradeClearTaskRecord> recordList = clearTaskAddVO.getTaskRecordList().stream()
                .map(t -> {
                	TradeClearTaskRecord taskRecord = new TradeClearTaskRecord();
                    BeanUtils.copyProperties(t, taskRecord);
                    taskRecord.setTaskId(tradeClearTask.getId());
                    taskRecord.setComments("");
                    return taskRecord;
                }).collect(Collectors.toList());
        if(recordList.size() > 0){
            recordMapper.insertAll(recordList);
        }
        return true;
	}

	
	/**
	 * 修改清分任务
	 * @Title edit
	 * @Description TODO
	 * @param tradeClearTaskAddVO
	 * @return
	 * @return 返回类型 boolean
	 */
	@Transactional(rollbackFor = Exception.class)
	public boolean edit(TradeClearTaskAddVO tradeClearTaskAddVO) {
        //查询数据
        TradeClearTask task = this.baseMapper.selectById(tradeClearTaskAddVO.getId());
        if (task == null){
            throw new BusinessException(1,"数据不存在");
        }
        QueryWrapper queryWrapper = Wrappers.query().eq("task_id",tradeClearTaskAddVO.getId()).eq("deleted",0);
        List<TradeClearTaskRecord>  recordList = recordMapper.selectList(queryWrapper);

        //修改订单
        TradeClearTask tradeClearTask = new TradeClearTask();
        BeanUtils.copyProperties(tradeClearTaskAddVO, tradeClearTask);
        tradeClearTask.setId(tradeClearTaskAddVO.getId());
        if(tradeClearTaskAddVO.getHaveDetail() == 1){
        	//计算订单总金额
            BigDecimal totalAmount = tradeClearTaskAddVO.getTaskRecordList().stream()
            		.map(TradeClearTaskRecordVO::getAmount).reduce(BigDecimal.ZERO,BigDecimal::add);
            tradeClearTask.setTotalAmount(totalAmount);
        }
        this.baseMapper.updateById(tradeClearTask);

        //数据比较
        List<TradeClearTaskRecordVO> addRecordList = tradeClearTaskAddVO.getTaskRecordList().stream()
                .filter(r ->  r.getId() == null || r.getId() == 0).collect(Collectors.toList());
        List<TradeClearTaskRecord> deleteRecordList = recordList.stream().filter(t -> {
            Optional<TradeClearTaskRecordVO> recordVOOptional = tradeClearTaskAddVO.getTaskRecordList().stream().
                    filter(r -> r.getId() != null)
                    .filter(s -> s.getId().equals(t.getId())).findFirst();
            return !recordVOOptional.isPresent();
        }).collect(Collectors.toList());
        List<TradeClearTaskRecordVO> updateRecordList = tradeClearTaskAddVO.getTaskRecordList().stream()
                .filter(r -> {
                    Optional<TradeClearTaskRecordVO> recordVOOptional = tradeClearTaskAddVO.getTaskRecordList().stream()
                            .filter(t -> t.getId() != null)
                            .filter(s -> s.getId().equals(r.getId())).findFirst();
                    return recordVOOptional.isPresent();
                }).collect(Collectors.toList());
        //添加
        for (TradeClearTaskRecordVO item: addRecordList) {
        	TradeClearTaskRecord taskRecord = new TradeClearTaskRecord();
        	BeanUtils.copyProperties(item, taskRecord);
            taskRecord.setTaskId(tradeClearTask.getId());
            taskRecord.setComments("");
            recordMapper.insert(taskRecord);
        }
        //修改
        for (TradeClearTaskRecordVO item: updateRecordList) {
        	TradeClearTaskRecord taskRecord = new TradeClearTaskRecord();
        	BeanUtils.copyProperties(item, taskRecord);
        	taskRecord.setTaskId(tradeClearTask.getId());
            recordMapper.updateById(taskRecord);
        }
        //删除
        for (TradeClearTaskRecord item: deleteRecordList) {
        	TradeClearTaskRecord model = new TradeClearTaskRecord();
        	model.setId(item.getId());
        	model.setDeleted(1);
            recordMapper.updateById(model);
        }
        return true;
	}

	/**
	 * 任务详情
	 * @Title getDetail
	 * @Description TODO
	 * @param taskId
	 * @return
	 * @return 返回类型 List<TradeClearTaskRecord>
	 */
	public List<TradeClearTaskRecord> getDetail(Long taskId) {
		QueryWrapper queryWrapper = Wrappers.query().eq("task_id",taskId).eq("deleted",0);
        return recordMapper.selectList(queryWrapper);
	}

	/**
	 * 完成清分任务
	 * @Title finish
	 * @Description TODO
	 * @param tradeClearTaskFinishVO
	 * @return
	 * @return 返回类型 boolean
	 */
	@Transactional(rollbackFor = Exception.class)
	public boolean finish(TradeClearTaskFinishVO tradeClearTaskFinishVO) {
		tradeClearTaskFinishVO.setTaskRecordList(tradeClearTaskFinishVO.getTaskRecordList().stream()
                .filter(s -> (s.getAmount() != null && s.getAmount().compareTo(BigDecimal.ZERO) != 0)
                            || (s.getCount() != null && s.getCount() > 0)).collect(Collectors.toList()));

		//计算订单总金额
        BigDecimal realityAmount = tradeClearTaskFinishVO.getTaskRecordList().stream()
        		.map(TradeClearTaskRecordVO::getAmount).reduce(BigDecimal.ZERO,BigDecimal::add);
        TradeClearTask tradeClearTask = new TradeClearTask();
        tradeClearTask.setId(tradeClearTaskFinishVO.getId());
        tradeClearTask.setRealityAmount(realityAmount);
        tradeClearTask.setStatus(TradeClearTaskStatusEnum.FINISH.getValue());
        this.baseMapper.updateById(tradeClearTask);
        //创建订单详情
        List<TradeClearResultRecord> resultRecordList = tradeClearTaskFinishVO.getTaskRecordList().stream()
                .map(t -> {
                	TradeClearResultRecord resultRecord = new TradeClearResultRecord();
                    BeanUtils.copyProperties(t, resultRecord);
                    resultRecord.setTaskId(tradeClearTask.getId());
                    resultRecord.setComments("");
                    return resultRecord;
                }).collect(Collectors.toList());
        if(resultRecordList.size() > 0){
        	resultRecordMapper.insertAll(resultRecordList);
        }
        return true;
	}

	/**
	 * 结果明细
	 * @Title getResultDetail
	 * @Description TODO
	 * @param taskId
	 * @return
	 * @return 返回类型 List<TradeClearResultRecord>
	 */
	public List<TradeClearResultRecord> getResultDetail(Long taskId) {
		QueryWrapper queryWrapper = Wrappers.query().eq("task_id",taskId).eq("deleted",0);
        return resultRecordMapper.selectList(queryWrapper);
	}
	
}
