package com.zcxd.base.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.Min;

import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zcxd.base.config.UserContextHolder;
import com.zcxd.base.dto.TradeClearTaskDTO;
import com.zcxd.base.service.BankService;
import com.zcxd.base.service.DenomService;
import com.zcxd.base.service.TradeClearTaskService;
import com.zcxd.base.vo.TradeClearTaskAddVO;
import com.zcxd.base.vo.TradeClearTaskFinishVO;
import com.zcxd.base.vo.TradeClearTaskQueryVO;
import com.zcxd.base.vo.VaultOrderRecordVO;
import com.zcxd.common.annotation.OperateLog;
import com.zcxd.common.annotation.OperateType;
import com.zcxd.common.constant.DenomTypeEnum;
import com.zcxd.common.constant.TradeClearTaskStatusEnum;
import com.zcxd.common.util.Result;
import com.zcxd.common.util.ResultList;
import com.zcxd.db.model.Bank;
import com.zcxd.db.model.Denom;
import com.zcxd.db.model.TradeClearResultRecord;
import com.zcxd.db.model.TradeClearTask;
import com.zcxd.db.model.TradeClearTaskRecord;

import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;

/**
 * 清分任务
 * @ClassName TradeClearTaskController
 * @Description TODO
 * @author 秦江南
 * @Date 2022年5月23日下午2:20:55
 */
@RestController
@RequestMapping("/trade/clearTask")
@AllArgsConstructor
public class TradeClearTaskController {
	
	private TradeClearTaskService tradeClearTaskService;
	
	private BankService bankService;
	
	private DenomService denomService;
	
	/**
	 * 清分任务列表
	 * @Title list
	 * @Description TODO
	 * @param page
	 * @param limit
	 * @param bankId
	 * @param clearType
	 * @param dateBegin
	 * @param dateEnd
	 * @return
	 * @return 返回类型 Result
	 */
    @ApiOperation(value = "清分任务列表")
    @PostMapping("/list")
    public Result list(@RequestBody @Validated TradeClearTaskQueryVO clearTaskVO){
    	IPage<TradeClearTask> tradeClearTaskPage = tradeClearTaskService.findListByPage(clearTaskVO);
        List<TradeClearTaskDTO> tradeClearTaskDTOList = new ArrayList<TradeClearTaskDTO>();
        if(tradeClearTaskPage.getTotal() != 0){
        	Set<Long> bankIds = tradeClearTaskPage.getRecords().stream().map(TradeClearTask::getBankId).collect(Collectors.toSet());
            Map<Long,String> bankMap = new HashMap<>();
            if (bankIds.size() > 0){
                List<Bank> bankList = bankService.listByIds(bankIds);
                bankMap = bankList.stream().collect(Collectors.toMap(Bank::getId,Bank::getShortName));
            }
        	
            final Map<Long,String> bankMapFinal = bankMap;
            tradeClearTaskDTOList =  tradeClearTaskPage.getRecords().stream().map(tradeClearTask -> {
            	TradeClearTaskDTO tradeClearTaskDTO = new TradeClearTaskDTO();
	        	BeanUtils.copyProperties(tradeClearTask, tradeClearTaskDTO);
	        	tradeClearTaskDTO.setBankName(tradeClearTask.getBankId() != 0 ? bankMapFinal.get(tradeClearTask.getBankId()) : "");
	        	return tradeClearTaskDTO;
	        }).collect(Collectors.toList());
        }
        ResultList resultList = new ResultList.Builder().total(tradeClearTaskPage.getTotal()).list(tradeClearTaskDTOList).build();
        return Result.success(resultList);
    }
    
    /**
     * 创建清分任务
     * @Title save
     * @Description TODO
     * @param tradeClearTaskAddVO
     * @return
     * @return 返回类型 Result
     */
    @ApiOperation(value = "创建清分任务")
    @OperateLog(value = "创建清分任务",type = OperateType.ADD)
    @PostMapping("/save")
    public Result save(@RequestBody @Validated TradeClearTaskAddVO tradeClearTaskAddVO){
       boolean b = tradeClearTaskService.create(tradeClearTaskAddVO);
       return b ? Result.success() : Result.fail("创建清分任务失败");
    }
    
    /**
     * 编辑清分任务
     * @Title update
     * @Description TODO
     * @param tradeClearTaskAddVO
     * @return
     * @return 返回类型 Result
     */
    @ApiOperation(value = "编辑清分任务")
    @OperateLog(value = "编辑清分任务",type = OperateType.EDIT)
    @PostMapping("/update")
    public Result update(@RequestBody @Validated TradeClearTaskAddVO tradeClearTaskAddVO){
        boolean b = tradeClearTaskService.edit(tradeClearTaskAddVO);
        return b ? Result.success() : Result.fail("编辑失败");
    }

    /**
     * 删除清分任务
     * @Title delete
     * @Description TODO
     * @param id
     * @return
     * @return 返回类型 Result
     */
    @ApiOperation(value = "删除清分任务")
    @OperateLog(value = "删除清分任务",type = OperateType.DELETE)
    @PostMapping("/delete/{id}")
    public Result delete(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){

        //初始状态可删除
    	TradeClearTask task = tradeClearTaskService.getById(id);
        if (task == null || task.getStatus() != TradeClearTaskStatusEnum.CREATE.getValue()){
          return Result.fail("该清分任务无法删除");
        }
        TradeClearTask model = new TradeClearTask();
        model.setId(task.getId());
        model.setDeleted(1);
        boolean b = tradeClearTaskService.updateById(model);
        if (b){
            return Result.success();
        }
        return Result.fail("删除清分任务失败");
    }

    /**
     * 任务明细
     * @Title detail
     * @Description TODO
     * @param taskId
     * @return
     * @return 返回类型 Result
     */
    @ApiOperation(value = "任务明细")
    @GetMapping("/detail")
    public Result detail(Long taskId){
    	TradeClearTask task = tradeClearTaskService.getById(taskId);
        if (task == null){
            return Result.fail("数据错误");
        }
       List<TradeClearTaskRecord> recordList = tradeClearTaskService.getDetail(taskId);

       List<Denom> denomList = denomService.list();
       Map<Long,String> denomMap = denomList.stream().collect(Collectors.toMap(Denom::getId,Denom::getName));

        LinkedList usableList = new LinkedList();
        LinkedList goodList = new LinkedList();
        LinkedList badList = new LinkedList();
        LinkedList unclearList = new LinkedList();
        LinkedList remnantList = new LinkedList();
       //数据分类
       recordList.stream().forEach(item -> {
           VaultOrderRecordVO recordVO = new VaultOrderRecordVO();
           BeanUtils.copyProperties(item,recordVO);
           recordVO.setDenomText(Optional.ofNullable(denomMap.get(item.getDenomId())).get() );
           if (item.getGbFlag() == DenomTypeEnum.USABLE.getValue()){
               usableList.add(recordVO);
           }else if (item.getGbFlag() == DenomTypeEnum.BAD.getValue()){
               badList.add(recordVO);
           }else if (item.getGbFlag() == DenomTypeEnum.GOOD.getValue()){
               goodList.add(recordVO);
           } else if (item.getGbFlag() == DenomTypeEnum.UNCLEAR.getValue()){
               unclearList.add(recordVO);
           } else if (item.getGbFlag() == DenomTypeEnum.REMNANT.getValue()){
               remnantList.add(recordVO);
           }
       });

        HashMap map = new HashMap(4);
        map.put("usable",usableList);
        map.put("bad",badList);
        map.put("good",goodList);
        map.put("unclear",unclearList);
        map.put("remnant",remnantList);
       return Result.success(map);
    }
    
    /**
     * 确认清分任务
     * @Title confirm
     * @Description TODO
     * @param id
     * @return
     * @return 返回类型 Result
     */
    @ApiOperation(value = "确认清分任务")
    @OperateLog(value = "确认清分任务",type = OperateType.EDIT)
    @PostMapping("/confirm/{id}")
    public Result confirm(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){

    	TradeClearTask task = tradeClearTaskService.getById(id);
        if (task == null || task.getStatus() != TradeClearTaskStatusEnum.CREATE.getValue()){
          return Result.fail("该清分任务无法确认");
        }
        TradeClearTask model = new TradeClearTask();
        model.setId(task.getId());
        model.setStatus(TradeClearTaskStatusEnum.CONFRIM.getValue());
        model.setConfirmUser(UserContextHolder.getUserId());
        model.setConfirmTime(System.currentTimeMillis());
        boolean b = tradeClearTaskService.updateById(model);
        if (b){
            return Result.success();
        }
        return Result.fail("确认清分任务失败");
    }
    
    /**
     * 完成清分任务
     * @Title finish
     * @Description TODO
     * @param tradeClearTaskFinishVO
     * @return
     * @return 返回类型 Result
     */
    @ApiOperation(value = "完成清分任务")
    @OperateLog(value = "完成清分任务",type = OperateType.EDIT)
    @PostMapping("/finish")
    public Result finish(@RequestBody @Validated TradeClearTaskFinishVO tradeClearTaskFinishVO){
    	
    	TradeClearTask task = tradeClearTaskService.getById(tradeClearTaskFinishVO.getId());
        if (task == null || task.getStatus() != TradeClearTaskStatusEnum.CONFRIM.getValue()){
          return Result.fail("该清分任务无法完成");
        }
        boolean b = tradeClearTaskService.finish(tradeClearTaskFinishVO);
        return b ? Result.success() : Result.fail("完成清分任务失败");
    }
    
    /**
     * 结果明细
     * @Title resultDetail
     * @Description TODO
     * @param taskId
     * @return
     * @return 返回类型 Result
     */
    @ApiOperation(value = "结果明细")
    @GetMapping("/resultDetail")
    public Result resultDetail(Long taskId){
    	TradeClearTask task = tradeClearTaskService.getById(taskId);
        if (task == null){
            return Result.fail("数据错误");
        }
       List<TradeClearResultRecord> recordList = tradeClearTaskService.getResultDetail(taskId);

       List<Denom> denomList = denomService.list();
       Map<Long,String> denomMap = denomList.stream().collect(Collectors.toMap(Denom::getId,Denom::getName));

        LinkedList usableList = new LinkedList();
        LinkedList goodList = new LinkedList();
        LinkedList badList = new LinkedList();
        LinkedList unclearList = new LinkedList();
        LinkedList remnantList = new LinkedList();
       //数据分类
       recordList.stream().forEach(item -> {
           VaultOrderRecordVO recordVO = new VaultOrderRecordVO();
           BeanUtils.copyProperties(item,recordVO);
           recordVO.setDenomText(Optional.ofNullable(denomMap.get(item.getDenomId())).get() );
           if (item.getGbFlag() == DenomTypeEnum.USABLE.getValue()){
               usableList.add(recordVO);
           }else if (item.getGbFlag() == DenomTypeEnum.BAD.getValue()){
               badList.add(recordVO);
           }else if (item.getGbFlag() == DenomTypeEnum.GOOD.getValue()){
               goodList.add(recordVO);
           } else if (item.getGbFlag() == DenomTypeEnum.UNCLEAR.getValue()){
               unclearList.add(recordVO);
           } else if (item.getGbFlag() == DenomTypeEnum.REMNANT.getValue()){
               remnantList.add(recordVO);
           }
       });

        HashMap map = new HashMap(4);
        map.put("usable",usableList);
        map.put("bad",badList);
        map.put("good",goodList);
        map.put("unclear",unclearList);
        map.put("remnant",remnantList);
       return Result.success(map);
    }
    
    /**
     * 取消清分任务
     * @Title cancel
     * @Description TODO
     * @param id
     * @return
     * @return 返回类型 Result
     */
    @ApiOperation(value = "取消清分任务")
    @OperateLog(value = "取消清分任务",type = OperateType.EDIT)
    @PostMapping("/cancel/{id}")
    public Result cancel(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){

    	TradeClearTask task = tradeClearTaskService.getById(id);
        if (task == null || (task.getStatus() != TradeClearTaskStatusEnum.CONFRIM.getValue() 
        		&& task.getStatus() != TradeClearTaskStatusEnum.FINISH.getValue())){
          return Result.fail("该清分任务无法取消");
        }
        TradeClearTask model = new TradeClearTask();
        model.setId(task.getId());
        model.setStatus(TradeClearTaskStatusEnum.CANCEL.getValue());
        model.setCancelUser(UserContextHolder.getUserId());
        model.setCancelTime(System.currentTimeMillis());
        boolean b = tradeClearTaskService.updateById(model);
        if (b){
            return Result.success();
        }
        return Result.fail("取消清分任务失败");
    }

}
