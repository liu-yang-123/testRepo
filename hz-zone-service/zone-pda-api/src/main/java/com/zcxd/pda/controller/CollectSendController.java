package com.zcxd.pda.controller;

import java.util.List;

import javax.validation.constraints.Size;

import com.zcxd.pda.dto.EscortDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zcxd.common.util.Result;
import com.zcxd.common.util.ResultList;
import com.zcxd.pda.dto.BoxpackDTO;
import com.zcxd.pda.dto.BoxpackTaskDTO;
import com.zcxd.pda.service.CollectSendService;
import com.zcxd.pda.service.impl.BoxpackTaskService;
import com.zcxd.pda.vo.BoxpackTaskEndVO;
import com.zcxd.pda.vo.BoxpackTaskReportVO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;

@RequestMapping("/collectSend")
@RestController
@Api(tags = "早送晚收")
public class CollectSendController {
    @Autowired
    private CollectSendService collectSendService;

    @Autowired
    private BoxpackTaskService taskService;
	
	@ApiOperation(value = "获取网点信息")
	@ApiImplicitParam(name = "terSN", value = "设备sn", required = true, dataType = "String",dataTypeClass=String.class)
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
	@PostMapping("/bank/info")
	public Result bankInfo(@RequestParam @Size(max=32,message="设备sn最大长度为32") String terSN) {
		Object result = collectSendService.bankInfo(terSN);
		return Result.success(result);
	}
	
	@ApiOperation(value = "获取网点箱包列表")
	@ApiImplicitParam(name = "bankId", value = "网点id", required = true, dataType = "Long",dataTypeClass=Long.class)
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
	@PostMapping("/boxPack/list")
	public Result boxPackList(@RequestParam Long bankId) {
		Object result = collectSendService.boxPackList(bankId);
		return Result.success(result);
	}
	
	@ApiOperation(value = "申报任务")
	@ApiResponse(code = 0, message = "处理成功", response = Result.class)
	@PostMapping("/task/report")
	public Result reportTask(@RequestBody BoxpackTaskReportVO boxpackTaskReportVO) {
		boolean result = collectSendService.reportTask(boxpackTaskReportVO);
		return result ? Result.success("申报任务成功") : Result.fail("申报任务失败");
	}

	@ApiOperation(value = "当天箱包任务列表")
	@GetMapping("/task/list")
	public Result taskList(){
		List<BoxpackTaskDTO> taskDTOList = taskService.getTaskList();
		return Result.success(taskDTOList);
	}

	@ApiOperation(value = "所有任务列表（分页）")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "page", value = "页码", required = true, dataType = "Integer",dataTypeClass = Integer.class),
			@ApiImplicitParam(name = "limit", value = "记录数", required = true, dataType = "Integer",dataTypeClass = Integer.class),
			@ApiImplicitParam(name = "date", value = "日期", dataType = "String", dataTypeClass = String.class)
	})
	@GetMapping("/task/all/list")
	public Result taskAllList(@RequestParam(defaultValue = "1") Integer page,
							  @RequestParam(defaultValue = "10") Integer limit,
							  String date){
		IPage<BoxpackTaskDTO> iPage = taskService.listPage(page, limit, date);
		ResultList resultList = ResultList.builder().total(iPage.getTotal()).list(iPage.getRecords()).build();
		return Result.success(resultList);
	}
	
	@ApiOperation(value = "任务详情")
	@ApiImplicitParam(name = "taskId", value = "任务id", required = true, dataType = "Long",dataTypeClass=Long.class)
	@PostMapping("/task/info")
	public Result taskInfo(@RequestParam Long taskId){
		Object result = collectSendService.taskInfo(taskId);
		return Result.success(result);
	}
	
	@ApiOperation(value = "开始任务")
	@ApiImplicitParam(name = "taskId", value = "任务id", required = true, dataType = "Long",dataTypeClass=Long.class)
	@PostMapping("/task/start")
	public Result taskStart(@RequestParam Long taskId){
		Object result = collectSendService.taskStart(taskId);
		return Result.success(result);
	}
	
	
	@ApiOperation(value = "结束任务")
	@ApiImplicitParam(name = "boxpackTaskEndVO", value = "早送晚收任务完成VO", required = true, dataType = "BoxpackTaskEndVO",dataTypeClass=BoxpackTaskEndVO.class)
	@PostMapping("/task/end")
	public Result taskEnd(@RequestBody @Validated BoxpackTaskEndVO boxpackTaskEndVO){
		boolean result = collectSendService.taskEnd(boxpackTaskEndVO);
		return result ? Result.success("任务提交成功") : Result.fail("任务提交失败");
	}
	
	@ApiOperation(value = "当天的箱包列表数据")
	@GetMapping("/boxPack/list/today")
	public Result boxPackListToday(){
		List<BoxpackDTO> boxpackDTOList = collectSendService.getTodayBoxpackList();
		return Result.success(boxpackDTOList);
	}
	
	@ApiOperation(value = "删除任务")
	@ApiImplicitParam(name = "taskId", value = "任务id", required = true, dataType = "Long",dataTypeClass=Long.class)
	@PostMapping("/task/del")
	public Result taskDel(@RequestParam Long taskId){
		boolean result = collectSendService.taskDel(taskId);
		return result ? Result.success("任务删除成功") : Result.fail("任务删除失败");
	}
		
	@ApiOperation(value = "当天护送人员")
	@GetMapping("/boxPack/escort/today")
	public Result escortToday(){
		List<EscortDTO> escortDTOList = collectSendService.getTodayEscortList();
		return Result.success(escortDTOList);
	}
}
