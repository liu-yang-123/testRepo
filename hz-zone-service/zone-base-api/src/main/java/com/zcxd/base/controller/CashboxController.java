package com.zcxd.base.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.Size;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zcxd.base.dto.CashboxDTO;
import com.zcxd.base.dto.CashboxUseDTO;
import com.zcxd.base.service.CashboxService;
import com.zcxd.base.vo.CashboxVO;
import com.zcxd.common.annotation.OperateLog;
import com.zcxd.common.annotation.OperateType;
import com.zcxd.common.util.Result;
import com.zcxd.common.util.ResultList;
import com.zcxd.db.model.Cashbox;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;

/**
 * 
 * @ClassName CashboxController
 * @Description 钞盒管理控制器
 * @author 秦江南
 * @Date 2021年5月24日上午9:31:38
 */
@RestController
@RequestMapping("/cashbox")
@Api(tags = "钞盒管理")
public class CashboxController {
	@Autowired
    private CashboxService cashboxService;
    
	@ApiOperation(value = "钞盒分页列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页数", required = true, dataType = "Integer ",defaultValue = "1"),
            @ApiImplicitParam(name = "limit", value = "显示数据条数", required = true, dataType = "Integer ",defaultValue = "10"),
            @ApiImplicitParam(name = "boxNo", value = "钞盒编号", required = false, dataType = "String"),
			@ApiImplicitParam(name = "used", value = "是否已使用", required = false, dataType = "Integer"),
			@ApiImplicitParam(name = "boxType", value = "钞盒/钞袋", required = false, dataType = "Integer"),
			@ApiImplicitParam(name = "rfid", value = "钞盒/钞袋", required = false, dataType = "String")
    })
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @GetMapping("/list")
    public Result list(@RequestParam(defaultValue = "1") Integer page,
                                 @RequestParam(defaultValue = "10") Integer limit,
                                 @RequestParam(required=false)  @Size(max=32,message="boxNo最大长度为32") String boxNo,
                                 @RequestParam(required=false)  Integer used,
                                 @RequestParam(required=false)  Integer boxType,
                                 @RequestParam(required=false)  String rfid
					   ){
		Cashbox cashbox = new Cashbox();
		cashbox.setBoxNo(boxNo);
		cashbox.setUsed(used);
		cashbox.setBoxType(boxType);
		cashbox.setRfid(rfid);
    	IPage<Cashbox> cashboxPage = cashboxService.findListByPage(page, limit, cashbox);
 		List<CashboxDTO> cashboxDTOList = cashboxPage.getRecords().stream().map(cashboxTmp->{
 			CashboxDTO cashboxDTO = new CashboxDTO();
 			BeanUtils.copyProperties(cashboxTmp, cashboxDTO);
 			return cashboxDTO;
 		}).collect(Collectors.toList());
 		
    	ResultList resultList = new ResultList.Builder().total(cashboxPage.getTotal()).list(cashboxDTOList).build();
        return Result.success(resultList);
    }


//    @OperateLog(value="停用钞盒", type=OperateType.EDIT)
//    @ApiOperation(value = "停用钞盒")
//    @ApiImplicitParam(name = "id", value = "钞盒id", required = true, dataType = "Long")
//    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
//    @PostMapping("/stop/{id}")
//    public Result stop(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){
//    	Cashbox cashbox = new Cashbox();
//    	cashbox.setId(id);
//    	cashbox.setStatusT(1);
//    	boolean update = cashboxService.updateById(cashbox);
//    	if(update)
//    		return Result.success();
//
//    	return Result.fail("停用钞盒失败");
//    }
//
//    @OperateLog(value="钞盒解绑", type=OperateType.EDIT)
//    @ApiOperation(value = "钞盒解绑")
//    @ApiImplicitParam(name = "id", value = "钞盒id", required = true, dataType = "Long")
//    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
//    @PostMapping("/unbind/{id}")
//    public Result unbind(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){
//    	Cashbox cashbox = new Cashbox();
//    	cashbox.setId(id);
//    	cashbox.setRfid("");
//    	boolean update = cashboxService.updateById(cashbox);
//    	if(update)
//    		return Result.success();
//
//    	return Result.fail("钞盒解绑失败");
//    }
	
	
  @OperateLog(value="编码绑定", type=OperateType.EDIT)
  @ApiOperation(value = "编码绑定")
  @ApiImplicitParam(name = "cashboxVO", value = "钞盒编码绑定VO", required = true, dataType = "CashboxVO")
  @ApiResponse(code = 200, message = "处理成功", response = Result.class)
  @PostMapping("/bind")
  public Result bind(@RequestBody @Validated CashboxVO cashboxVO){
	  
	Cashbox cashbox = new Cashbox();
	cashbox.setRfid(cashboxVO.getRfid());
	List<Cashbox> cashboxList = cashboxService.getCashboxByCondition(cashbox);
  	if(cashboxList != null && cashboxList.size() > 0){
  		return Result.fail("该编码已被绑定，请重新输入");
  	}
	cashbox.setId(cashboxVO.getId());
	boolean update = cashboxService.updateById(cashbox);
	
	if(update){
		return Result.success("绑定成功");
	}
	return Result.fail("绑定失败");
  }

	@OperateLog(value="批量生成条码", type=OperateType.ADD)
	@ApiOperation(value = "批量生成条码")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "count", value = "生产条码个数", required = true, dataType = "Integer"),
			@ApiImplicitParam(name = "type", value = "条码类别（1 - 钞盒编码，2 - 钞袋编码", required = true, dataType = "Integer")
	})
	@ApiResponse(code = 200, message = "处理成功", response = Result.class)
	@PostMapping("/produce")
	public Result produceBoxNo(@RequestParam("count")  Integer count, @RequestParam("type")  Integer type){

		if (count > 200 || count < 0) {
			Result.fail("最多生产200个");
		}
		/**
		 * 查询当前编码表中最大的编码
		 */
		int max = 0;
		List<String> boxNoList = cashboxService.listAllBoxNo(type);
		List<Integer> numbers = boxNoList.stream().map(Integer::parseInt).collect(Collectors.toList());
		if (numbers.size() > 0) {
			max = numbers.stream().reduce(Integer::max).get();
		} else {
			max = (type == 1)? 100000 : 200000;
		}
		List<Cashbox> cashboxList = new ArrayList<>();
		for (int i = 1; i<= count; i++ ) {
			Cashbox cashbox = new Cashbox();
			cashbox.setBoxType(type);
			cashbox.setBoxNo(String.format("%06d",max+i));
			cashbox.setUsed(0);
			cashboxList.add(cashbox);
		}
		boolean bRet = cashboxService.saveBatch(cashboxList);
		return bRet? Result.success() : Result.fail("条码生成失败");
	}

	@OperateLog(value="钞盒标签更新", type=OperateType.EDIT)
	@ApiOperation(value = "钞盒标签使用分配")
	@ApiImplicitParam(name = "cashboxUseDTO", value = "钞盒ids", required = true, dataType = "CashboxUseDTO")
	@ApiResponse(code = 200, message = "处理成功", response = Result.class)
	@PostMapping("/used")
	public Result setUsed(@RequestBody CashboxUseDTO cashboxUseDTO){
		if (cashboxUseDTO.getBoxIds().size() == 0) {
			return Result.fail("无效参数");
		}
		List<Cashbox> cashboxList = cashboxUseDTO.getBoxIds().stream().map(id -> {
			Cashbox cashbox = new Cashbox();
			cashbox.setId(id);
			cashbox.setUsed(1);
			return cashbox;
		}).collect(Collectors.toList());

		boolean bRet = cashboxService.updateBatchById(cashboxList);
		return bRet? Result.success() : Result.fail();
	}
}
