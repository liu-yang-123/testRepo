package com.zcxd.base.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zcxd.base.dto.PDADTO;
import com.zcxd.base.service.BankService;
import com.zcxd.base.service.PDAService;
import com.zcxd.base.vo.PDAVO;
import com.zcxd.common.annotation.OperateLog;
import com.zcxd.common.annotation.OperateType;
import com.zcxd.common.util.Result;
import com.zcxd.common.util.ResultList;
import com.zcxd.db.model.Bank;
import com.zcxd.db.model.Pda;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;

/**
 * 
 * @ClassName PDAController
 * @Description PDA终端管理控制器
 * @author 秦江南
 * @Date 2021年5月26日下午4:15:49
 */
@RestController
@RequestMapping("/pda")
@Api(tags="终端管理")
public class PDAController {


    @Autowired
    private PDAService pdaService;
    
	@Autowired
	private BankService bankService;

    @OperateLog(value = "添加终端设备", type=OperateType.ADD)
    @ApiOperation(value = "添加终端设备")
    @ApiImplicitParam(name = "pdaVO", value = "终端设备", required = true, dataType = "PDAVO")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/save")
    public Result save(@RequestBody @Validated PDAVO pdaVO){
    	Pda pda = new Pda();
    	pda.setTersn(pdaVO.getTersn());
		List<Pda> pdaList = pdaService.getPdaByCondition(pda);
		if(pdaList != null && pdaList.size()>0)
			return Result.fail("该设备编号已存在，请重新填写！");
		
		BeanUtils.copyProperties(pdaVO, pda);
		pda.setId(null);
		pda.setStatusT(0);
		pda.setDeleted(0);
		
		boolean save = pdaService.save(pda);
    	if(save)
    		return Result.success();
    	
    	return Result.fail("添加终端设备失败");
    }

    @OperateLog(value = "删除终端设备", type=OperateType.DELETE)
    @ApiOperation(value = "删除终端设备")
    @ApiImplicitParam(name = "id", value = "数据字典id", required = true, dataType = "Long")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/delete/{id}")
    public Result delete(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){
    	Pda pda = new Pda();
    	pda.setId(id);
    	pda.setDeleted(1);
    	boolean update = pdaService.updateById(pda);
    	
    	if(update)
    		return Result.success();
    	
    	return Result.fail("删除终端设备失败");
    }

    @OperateLog(value = "修改终端设备", type=OperateType.EDIT)
    @ApiOperation(value = "修改终端设备")
    @ApiImplicitParam(name = "pdaVO", value = "终端设备", required = true, dataType = "PDAVO")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/update")
    public Result update(@RequestBody @Validated PDAVO pdaVO){
    	if (pdaVO.getId() == null) {
            return Result.fail("终端设备Id不能为空");
        }
    	
    	Pda pda = new Pda();
    	pda.setTersn(pdaVO.getTersn());
		List<Pda> pdaList = pdaService.getPdaByCondition(pda);
		if(pdaList != null && pdaList.size()>0)
			if(!pdaList.get(0).getId().equals(pdaVO.getId()))
				return Result.fail("该设备编号已存在，请重新填写！");
		
		BeanUtils.copyProperties(pdaVO, pda);

		boolean update = pdaService.updateById(pda);
    	if(update)
    		return Result.success();
    	
    	return Result.fail("修改终端设备失败");
    }

    @ApiOperation(value = "查询终端设备列表")
    @ApiImplicitParams({
    	@ApiImplicitParam(name = "page", value = "当前页数", required = true, dataType = "Integer",defaultValue = "1"),
        @ApiImplicitParam(name = "limit", value = "显示数据条数", required = true, dataType = "Integer",defaultValue = "10"),
        @ApiImplicitParam(name = "tersn", value = "终端编号", required = false, dataType = "String"),
        @ApiImplicitParam(name = "useType", value = "用途", required = false, dataType = "Integer"),
        @ApiImplicitParam(name = "statusT", value = "状态", required = false, dataType = "Integer"),
        @ApiImplicitParam(name = "departmentId", value = "部门id", required = true, dataType = "Long")
    })
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @GetMapping("/list")
    public Result findListByPage(@RequestParam Integer page,
                                   @RequestParam Integer limit,
                                   @RequestParam(required=false) @Size(max=32,message="tersn最大长度为32") String tersn,
                                   @RequestParam(required=false) Integer useType,
                                   @RequestParam(required=false) Integer statusT,
                                   @RequestParam @Min(value=1, message="departmentId不能小于1") Long departmentId){
    	Pda pda = new Pda();
    	pda.setTersn(tersn);
    	pda.setUseType(useType);
    	pda.setStatusT(statusT);
    	pda.setDepartmentId(departmentId);
    	IPage<Pda> pdaPage = pdaService.findListByPage(page, limit, pda);
    	
    	List<PDADTO> pdaList = new ArrayList<PDADTO>();
    	
    	if(pdaPage.getTotal() > 0) {
	    	Set<Long> bankIds = pdaPage.getRecords().stream().map(Pda::getBankId).collect(Collectors.toSet());
	    	Map<Long,String> bankBoxMap = new HashMap<Long,String>();
	    	if(bankIds.size() > 0){
		    	List<Bank> bankList = bankService.listByIds(bankIds);
		    	bankBoxMap = bankList.stream().collect(Collectors.toMap(Bank::getId,Bank::getShortName));
	    	}
	    	
	    	final Map<Long,String> bankBoxMapFinal = bankBoxMap;
	    	pdaList = pdaPage.getRecords().stream().map(pdaTmp -> {
	    		PDADTO pdaDTO = new PDADTO();
	    		BeanUtils.copyProperties(pdaTmp, pdaDTO);
				pdaDTO.setBankName(pdaDTO.getBankId() == 0 ? "" : bankBoxMapFinal.get(pdaDTO.getBankId()));
	    		
	    		return pdaDTO;
	    	}).collect(Collectors.toList());
    	}
    	
    	ResultList resultList = new ResultList.Builder().total(pdaPage.getTotal()).list(pdaList).build();
        return Result.success(resultList);
    }
    
    
    @ApiOperation(value = "停用PDA")
    @ApiImplicitParam(name = "id", value = "PDAid", required = true, dataType = "Long")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/stop/{id}")
    public Result stop(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){
    	Pda pda = new Pda();
    	pda.setId(id);
    	pda.setStatusT(1);
        boolean update = pdaService.updateById(pda);
    	if(update)
    		return Result.success();
    	
    	return Result.fail("停用PDA失败");
    }
    
    @ApiOperation(value = "启用PDA")
    @ApiImplicitParam(name = "id", value = "PDAid", required = true, dataType = "Long")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/enable/{id}")
    public Result enable(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){
    	Pda pda = new Pda();
    	pda.setId(id);
    	pda.setStatusT(0);
        boolean update = pdaService.updateById(pda);
    	if(update)
    		return Result.success();
    	
    	return Result.fail("启用PDA失败");
    }
    
}
