package com.zcxd.base.controller;

import javax.validation.constraints.Min;

import com.zcxd.common.constant.BusinessModeEnum;
import com.zcxd.common.constant.Constant;
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

import com.zcxd.base.dto.BankInfoDTO;
import com.zcxd.base.service.BankService;
import com.zcxd.base.vo.BankVO;
import com.zcxd.common.annotation.OperateLog;
import com.zcxd.common.annotation.OperateType;
import com.zcxd.common.constant.BankTypeEnum;
import com.zcxd.common.util.Result;
import com.zcxd.common.util.TreeUtil;
import com.zcxd.db.model.Bank;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *  商业清分-机构控制器
 * @ClassName ClearBankControlller
 * @Description 商业清分机构控制器
 * @author 秦江南
 * @Date 2022年5月12日下午4:01:40
 */
@RestController
@RequestMapping("/bankTrade")
@Api(tags = "商业清分机构管理")
public class BankTradeControlller {
	@Autowired
    private BankService bankService;
	
    @ApiOperation(value = "查询机构树")
    @ApiImplicitParam(name = "departmentId", value = "部门id", required = true, dataType = "Long")
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @GetMapping("/tree")
    public Result tree(@RequestParam @Min(value=1, message="departmentId不能小于1") Long departmentId){
//    	Bank bank = new Bank();
//    	bank.setDepartmentId(departmentId);
//    	bank.setParentIds(Constant.TOP_BANK_PARENTS);
    	return Result.success(TreeUtil.listToTree(bankService.getTopBankList(departmentId)));
    }

    @ApiOperation(value = "查询机构详情")
    @ApiImplicitParam(name = "id", value = "机构id", required = true, dataType = "Long")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/info/{id}")
    public Result info(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){
    	Bank bank = bankService.getById(id);
    	
    	BankInfoDTO bankInfoDTO = new BankInfoDTO();
    	BeanUtils.copyProperties(bank, bankInfoDTO);
    	
    	String[] parentsIds = bank.getParentIds().substring(1, bank.getParentIds().length()-1).split("/");
		if (parentsIds.length > 1) {
			Bank parent = bankService.getById(parentsIds[parentsIds.length - 1]);
			bankInfoDTO.setParentName(parent.getShortName());
		}
    	
    	return Result.success(bankInfoDTO);
    }
    
    
    @OperateLog(value="新增机构", type=OperateType.ADD)
    @ApiOperation(value = "新增机构")
    @ApiImplicitParam(name = "bankVO", value = "机构信息", required = true, dataType = "BankVO")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/save")
    public Result save(@RequestBody @Validated BankVO bankVO){
    	Bank bank = new Bank();
    	
    	BeanUtils.copyProperties(bankVO, bank);
    	//上级机构为空，设置上级机构为 0/
    	if(bankVO.getParentIds().equals("0")){
        	bank.setParentIds("/0/");
        }else {
        	Bank bankParent = bankService.getById(bankVO.getParentIds());
        	bank.setParentIds(bankParent.getParentIds()+bankParent.getId()+"/");
        }
    	//兼容以往代码(只要开通ATM，则默认类型是ATM）
        if (bankVO.getHaveAtm().equals(1)) {
            bank.setBankType(BankTypeEnum.ATM.getValue());
        } else if(bankVO.getHaveBox().equals(1)) {
            bank.setBankType(BankTypeEnum.BOX.getValue());
        } else if (bankVO.getHaveClear().equals(1)) {
            bank.setBankType(BankTypeEnum.CLEAR.getValue());
        }

        bank.setStatusT(0);
        bank.setDeleted(0);
    	boolean save = bankService.save(bank);
    	if(!save) {
    		return Result.fail("新增机构失败");
    	}
    	
    	bank.setBankNo("03" + String.format("%05d", bank.getId()));
    	boolean update = bankService.updateById(bank);
    	
    	if(update) {
    		return Result.success();
    	}
    	
    	return Result.fail("新增机构失败");
    	
    	
    }
    
    @OperateLog(value="修改机构信息", type=OperateType.EDIT)
    @ApiOperation(value = "修改机构信息")
    @ApiImplicitParam(name = "bankVO", value = "机构信息", required = true, dataType = "BankVO")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/update")
    public Result update(@RequestBody @Validated BankVO bankVO){
    	
        if (bankVO.getId() == null) {
            return Result.fail("机构Id不能为空");
        }
        
    	Bank bank = new Bank();
    	BeanUtils.copyProperties(bankVO, bank);
    	//上级机构为空，设置上级机构为 0/
        if(bankVO.getParentIds().equals("0")){
        	bank.setParentIds("/0/");
        }else {
        	Bank bankParent = bankService.getById(bankVO.getParentIds());
        	bank.setParentIds(bankParent.getParentIds()+bankParent.getId()+"/");
        }

        //兼容以往代码(只要开通ATM，则默认类型是ATM）
        if (bankVO.getHaveAtm().equals(1)) {
            bank.setBankType(BankTypeEnum.ATM.getValue());
        } else if(bankVO.getHaveBox().equals(1)) {
            bank.setBankType(BankTypeEnum.BOX.getValue());
        } else if (bankVO.getHaveClear().equals(1)) {
            bank.setBankType(BankTypeEnum.CLEAR.getValue());
        }
        
    	boolean update = bankService.updateById(bank);
    	if(update)
    		return Result.success();
    	
    	return Result.fail("修改机构信息失败");
    }
    
    
    @ApiOperation(value = "停用机构")
    @ApiImplicitParam(name = "id", value = "机构id", required = true, dataType = "Long")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/stop/{id}")
    public Result stop(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){
    	Bank bank = new Bank();
    	bank.setId(id);
    	bank.setStatusT(1);
        boolean update = bankService.updateById(bank);
    	if(update)
    		return Result.success();
    	
    	return Result.fail("停用机构失败");
    }
    
    @ApiOperation(value = "启用机构")
    @ApiImplicitParam(name = "id", value = "机构id", required = true, dataType = "Long")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/enable/{id}")
    public Result enable(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){
    	Bank bank = new Bank();
    	bank.setId(id);
    	bank.setStatusT(0);
        boolean update = bankService.updateById(bank);
    	if(update)
    		return Result.success();
    	
    	return Result.fail("启用机构失败");
    }
    
    @ApiOperation(value = "机构下拉选项")
    @ApiImplicitParam(name = "departmentId", value = "部门id", required = true, dataType = "Long")
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @GetMapping("/option")
    public Result option(@RequestParam Long departmentId,@RequestParam Integer businessMode){
//    	Bank bank = new Bank();
//    	bank.setDepartmentId(departmentId);
//    	bank.setHaveClear(1);
//    	List<Bank> bankList = bankService.listByCondition(bank);
        BusinessModeEnum businessModeEnum = BusinessModeEnum.valueOf(businessMode);
        List<Bank> bankList = bankService.listTopBank(departmentId,businessModeEnum);
    	List<Map<String,Object>> resultMapList = bankList.stream().map(bank1 -> {
            Map<String,Object> itemMap = new HashMap<>();
            itemMap.put("id",bank1.getId());
            itemMap.put("name",bank1.getShortName());
            return itemMap;
        }).collect(Collectors.toList());
//    	return Result.success(TreeUtil.listToTree(bankService.getBankList(bank)));
    	return Result.success(resultMapList);
    }
}
