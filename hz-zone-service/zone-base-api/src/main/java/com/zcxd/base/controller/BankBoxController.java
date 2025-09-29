package com.zcxd.base.controller;

import javax.validation.constraints.Min;

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

import com.zcxd.base.config.UserContextHolder;
import com.zcxd.base.dto.BankInfoDTO;
import com.zcxd.base.service.BankService;
import com.zcxd.base.vo.BankVO;
import com.zcxd.common.annotation.OperateLog;
import com.zcxd.common.annotation.OperateType;
import com.zcxd.common.constant.BankCategoryTypeEnum;
import com.zcxd.common.constant.BankTypeEnum;
import com.zcxd.common.util.Result;
import com.zcxd.common.util.TreeUtil;
import com.zcxd.db.model.Bank;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;

/**
 * 
 * @ClassName BankController
 * @Description 机构管理控制器
 * @author 秦江南
 * @Date 2021年5月20日上午10:01:14
 */
@RestController
@RequestMapping("/bankBox")
@Api(tags = "尾箱机构管理")
public class BankBoxController {
	@Autowired
    private BankService bankService;

    @ApiOperation(value = "查询机构树")
    @ApiImplicitParam(name = "departmentId", value = "部门id", required = true, dataType = "Long")
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @GetMapping("/tree")
    public Result tree(@RequestParam @Min(value=1, message="departmentId不能小于1") Long departmentId){
    	Bank bank = new Bank();
    	bank.setBankType(BankTypeEnum.BOX.getValue());
    	bank.setDepartmentId(departmentId);
    	return Result.success(TreeUtil.listToTree(bankService.getBankList(bank)));
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
    	
    	Bank storageBank = bankService.getStorageBank(bankVO.getDepartmentId());
    	if(null != storageBank){
    		return Result.fail("已存在库房类型机构");
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
        bank.setBankType(BankTypeEnum.BOX.getValue());
        bank.setCarryType(0);
        bank.setStatusT(0);
        bank.setDeleted(0);
        boolean save = bankService.save(bank);
    	if(!save)
    		return Result.fail("新增机构失败");
    	
    	boolean update = true;
    	
		bank.setBankNo("02" + String.format("%05d", bank.getId()));
		update = bankService.updateById(bank);
	
    	if(update)
    		return Result.success();
    	
    	return Result.fail("新增机构失败");
    }
    
    @OperateLog(value="修改机构信息", type=OperateType.EDIT)
    @ApiOperation(value = "修改机构信息")
    @ApiImplicitParam(name = "bankVO", value = "机构信息", required = true, dataType = "BankVO")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/update")
    public Result update(@RequestBody @Validated BankVO bankVO){
    	if(bankVO.getBankCategory().equals(BankCategoryTypeEnum.STORAGE.getValue())){
    		Bank storageBank = bankService.getStorageBank(bankVO.getDepartmentId());
        	if(null != storageBank && !bankVO.getId().equals(storageBank.getId())){
        		return Result.fail("已存在库房类型机构");
        	}
    	}
    	
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
    
    @ApiOperation(value = "尾箱机构下拉选项")
    @ApiImplicitParam(name = "departmentId", value = "部门id", required = true, dataType = "Long")
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @GetMapping("/option")
    public Result option(@RequestParam Long departmentId){
    	Bank bank = new Bank();
    	bank.setBankType(BankTypeEnum.BOX.getValue());
    	bank.setDepartmentId(departmentId);
    	return Result.success(TreeUtil.listToTree(bankService.getBankList(bank)));
    }
    
}
