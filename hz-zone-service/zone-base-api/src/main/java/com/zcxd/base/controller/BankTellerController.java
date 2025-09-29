package com.zcxd.base.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.validation.constraints.Min;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zcxd.base.dto.BankTellerDTO;
import com.zcxd.base.service.BankService;
import com.zcxd.base.service.BankTellerService;
import com.zcxd.base.service.EmployeeService;
import com.zcxd.base.service.FingerprintService;
import com.zcxd.base.vo.BankTellerVO;
import com.zcxd.common.annotation.OperateLog;
import com.zcxd.common.annotation.OperateType;
import com.zcxd.common.constant.BankCardCategoryEnum;
import com.zcxd.common.constant.BankCategoryTypeEnum;
import com.zcxd.common.constant.Constant;
import com.zcxd.common.constant.FingerTypeEnum;
import com.zcxd.common.util.Result;
import com.zcxd.common.util.ResultList;
import com.zcxd.common.util.SecurityUtils;
import com.zcxd.db.model.Bank;
import com.zcxd.db.model.BankTeller;
import com.zcxd.db.model.Employee;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import lombok.AllArgsConstructor;

/**
 * 
 * @ClassName BankTellerController
 * @Description 早送晚收员工控制器
 * @author 秦江南
 * @Date 2021年11月22日下午7:11:46
 */
@RestController
@AllArgsConstructor
@RequestMapping("/bankTeller")
@Api(tags = "早送晚收员工控制器")
public class BankTellerController {
    private BankTellerService bankTellerService;
    
    private BankService bankService;
    
    private EmployeeService employeeService;
    
    private FingerprintService fingerprintService;

    @OperateLog(value="添加员工", type=OperateType.ADD)
    @ApiOperation(value = "添加员工")
    @ApiImplicitParam(name = "bankTellerVO", value = "员工信息", required = true, dataType = "BankTellerVO")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/save")
    public Result save(@RequestBody @Validated BankTellerVO bankTellerVO){
    	BankTeller bankTeller = new BankTeller();
    	bankTeller.setBankId(bankTellerVO.getBankId());
    	bankTeller.setDepartmentId(bankTellerVO.getDepartmentId());
    	if(bankTellerVO.getEmpId() == null){
	    	bankTeller.setTellerNo(bankTellerVO.getTellerNo());
	    	List<BankTeller> bankTellerList = bankTellerService.getBankTellerByCondition(bankTeller);
	    	if(bankTellerList != null && bankTellerList.size() > 0){
				return Result.fail("该编号已存在，请重新填写！");
	    	}
	    	bankTeller.setTellerName(bankTellerVO.getTellerName());
	    	bankTeller.setManagerFlag(bankTellerVO.getManagerFlag());
	    	if (!StringUtils.isBlank(bankTellerVO.getMobile())) {
	    		bankTeller.setMobile(SecurityUtils.encryptAES(bankTellerVO.getMobile()));
			}
	    	bankTeller.setPassword(DigestUtils.md5DigestAsHex(Constant.USER_DEFAULT_PWD.getBytes()).toUpperCase());
    	}else{
    		bankTeller.setEmpId(bankTellerVO.getEmpId());
    	}
    	
    	bankTeller.setComments(bankTellerVO.getComments());
    	boolean save = bankTellerService.save(bankTeller);
    	if(save){
    		return Result.success();
    	}
    	return Result.fail("添加员工失败");
    }
    
    @OperateLog(value="修改员工", type=OperateType.EDIT)
    @ApiOperation(value = "修改员工")
    @ApiImplicitParam(name = "bankTellerVO", value = "员工", required = true, dataType = "BankTellerVO")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/update")
    public Result update(@RequestBody @Validated BankTellerVO bankTellerVO){
    	BankTeller bankTeller = bankTellerService.getById(bankTellerVO.getId());
    	
    	if (null == bankTeller) {
			return Result.fail("无效记录");
		}
    	
    	if(bankTeller.getEmpId() != 0){
    		return Result.fail("库房人员不能进行编辑");
    	}
    	Bank bank = bankService.getById(bankTellerVO.getBankId());
    	if(bank.getBankCategory() == BankCategoryTypeEnum.STORAGE.getValue()){
    		return Result.fail("不能修改为库房人员");
    	}
    	
    	if(bankTellerVO.getEmpId() == null || bankTellerVO.getEmpId() == 0){
    		BankTeller bankTellerTmp = new BankTeller();
    		bankTellerTmp.setTellerNo(bankTellerVO.getTellerNo());
    		bankTellerTmp.setBankId(bankTellerVO.getBankId());
    		bankTellerTmp.setDepartmentId(bankTellerVO.getDepartmentId());
	    	List<BankTeller> bankTellerList = bankTellerService.getBankTellerByCondition(bankTellerTmp);
	    	if (null == bankTellerList) {
				return Result.fail("无效参数");
			}
	    	
	    	if(bankTellerList.size() > 0){
	    		if (!bankTellerList.get(0).getId().equals(bankTellerVO.getId())) {
	    			return Result.fail("该编号已存在，请重新填写！");
	    		}
	    	}
	    	
	    	bankTeller.setTellerNo(bankTellerVO.getTellerNo());
	    	bankTeller.setTellerName(bankTellerVO.getTellerName());
	    	if (!StringUtils.isBlank(bankTellerVO.getMobile())) {
	    		bankTeller.setMobile(SecurityUtils.encryptAES(bankTellerVO.getMobile()));
			}else{
				bankTeller.setMobile("");
			}
	    	bankTeller.setManagerFlag(bankTellerVO.getManagerFlag());
	    	bankTeller.setEmpId(0l);
    	}else{
    		bankTeller.setEmpId(bankTellerVO.getEmpId());
    		bankTeller.setPassword("");
    		bankTeller.setTellerNo("");
	    	bankTeller.setTellerName("");
	    	bankTeller.setMobile("");
	    	bankTeller.setManagerFlag(0);
    	}
    	bankTeller.setBankId(bankTellerVO.getBankId());
    	bankTeller.setComments(bankTellerVO.getComments());
    	boolean update = bankTellerService.updateById(bankTeller);
    	if(update){
    		return Result.success();
    	}
    	return Result.fail("修改员工失败");
    }

    @OperateLog(value="删除员工", type=OperateType.DELETE)
    @ApiOperation(value = "删除员工")
    @ApiImplicitParam(name = "id", value = "员工id", required = true, dataType = "Long")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/delete/{id}")
    public Result delete(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){
    	BankTeller bankTeller = bankTellerService.getById(id);
    	if(null == bankTeller){
    		return Result.fail("员工不存在");
    	}
    	
    	bankTeller.setDeleted(1);
        boolean update = bankTellerService.updateById(bankTeller);
    	if(!update) {
			return Result.fail("删除失败");
		}
    	
    	if(bankTeller.getEmpId() == 0L){
    		fingerprintService.deleteByUserId(id, FingerTypeEnum.COUNTER.getValue());
    	}
    	return Result.success();
    }
    
    @OperateLog(value="员工离职处理", type=OperateType.EDIT)
    @ApiOperation(value = "员工离职处理")
    @ApiImplicitParam(name = "id", value = "员工id", required = true, dataType = "Long")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/quit/{id}")
    public Result quit(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){
    	BankTeller bankTeller = bankTellerService.getById(id);
    	
    	if(null == bankTeller){
    		return Result.fail("员工不存在");
    	}
    	
    	if(bankTeller.getEmpId() != 0){
    		return Result.fail("库房人员不能在此进行离职处理");
    	}
    	
    	if(bankTeller.getStatusT() != 0){
    		return Result.fail("员工离职状态有误");
    	}
    		
    	bankTeller = new BankTeller();
    	bankTeller.setId(id);
    	bankTeller.setStatusT(1);
    	boolean update = bankTellerService.updateById(bankTeller);
    	if(!update) {
			return Result.fail("操作失败");
		}
    	
    	fingerprintService.deleteByUserId(id,FingerTypeEnum.COUNTER.getValue());
    	return Result.success();
    }
    
    @OperateLog(value="员工复岗处理", type=OperateType.EDIT)
    @ApiOperation(value = "员工复岗处理")
    @ApiImplicitParam(name = "id", value = "员工id", required = true, dataType = "Long")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/back/{id}")
    public Result back(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){
    	BankTeller bankTeller = bankTellerService.getById(id);
    	
    	if (null == bankTeller) {
			return Result.fail("无效记录");
		}
    	
    	if(bankTeller.getEmpId() != 0){
    		return Result.fail("库房人员不能在此进行复岗处理");
    	}
    	
    	if(bankTeller.getStatusT() != 1){
    		return Result.fail("员工复岗状态有误");
    	}
    		
    	bankTeller = new BankTeller();
    	bankTeller.setId(id);
    	bankTeller.setStatusT(0);
    	boolean update = bankTellerService.updateById(bankTeller);
    	if(!update) {
			return Result.fail("操作失败");
		}
    	return Result.success();
    }
    
	@OperateLog(value="重置密码",type=OperateType.EDIT)
    @ApiOperation(value = "重置密码", notes = "重置密码")
	@ApiImplicitParam(name = "id", value = "用户id", required = true, dataType = "Long")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/resetpwd/{id}")
    public Result resetPassword(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id) {
		BankTeller bankTeller = bankTellerService.getById(id);
    	if (null == bankTeller) {
			return Result.fail("无效记录");
		}
		
    	if(bankTeller.getEmpId() != 0){
    		return Result.fail("库房人员不能在此进行重置密码");
    	}
    	
		bankTeller.setPassword(DigestUtils.md5DigestAsHex(Constant.USER_DEFAULT_PWD.getBytes()).toUpperCase());
    	boolean update = bankTellerService.updateById(bankTeller);
    	if(update)
    		return Result.success();
    	
    	return Result.fail("重置密码失败");
    }
    
    @ApiOperation(value = "查询员工列表")
    @ApiImplicitParams({
    	@ApiImplicitParam(name = "page", value = "当前页数", required = true, dataType = "Integer",defaultValue = "1"),
        @ApiImplicitParam(name = "limit", value = "显示数据条数", required = true, dataType = "Integer",defaultValue = "10"),
        @ApiImplicitParam(name = "bankId", value = "机构Id", required = true, dataType = "Long")
    })
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @GetMapping("/list")
    public Result findListByPage(@RequestParam(defaultValue = "1") Integer page,
			   @RequestParam(defaultValue = "10") Integer limit,
			   @RequestParam Long bankId){

    	BankTeller bankTellerTmp = new BankTeller();
    	bankTellerTmp.setBankId(bankId);
        IPage<BankTeller> bankTellerPage = bankTellerService.findListByPage(page, limit,bankTellerTmp);
        List<BankTellerDTO> bankTellerDTOList = new ArrayList<BankTellerDTO>();
        if(bankTellerPage.getTotal() != 0){
        	Set<Long> bankIds = bankTellerPage.getRecords().stream().map(BankTeller::getBankId).collect(Collectors.toSet());
        	List<Bank> bankList = bankService.listByIds(bankIds);
            Map<Long,Bank> bankMap = bankList.stream().collect(Collectors.toMap(Bank::getId,Function.identity(),(key1,key2) -> key2));
        	
        	Set<Long> empIds = bankTellerPage.getRecords().stream().map(BankTeller::getEmpId).collect(Collectors.toSet());
        	empIds.remove(0l);
        	Map<Long,Employee> empMap = new HashMap<Long, Employee>();
        	if(empIds.size() > 0){
	        	List<Employee> empList = employeeService.listByIds(empIds);
	           	empMap = empList.stream().collect(Collectors.toMap(Employee::getId,Function.identity(),(key1,key2) -> key2));
        	}
            
        	final Map<Long,Employee> empMapFinal = empMap;
        	bankTellerDTOList =  bankTellerPage.getRecords().stream().map(bankTeller -> {
        		BankTellerDTO bankTellerDTO = new BankTellerDTO();
        		bankTellerDTO.setBankName(bankMap.get(bankTeller.getBankId()).getShortName());
        		bankTellerDTO.setBankCategory(bankMap.get(bankTeller.getBankId()).getBankCategory());
	        	BeanUtils.copyProperties(bankTeller, bankTellerDTO);
	        	if (!StringUtils.isBlank(bankTellerDTO.getMobile())) {
	        		bankTellerDTO.setMobile(SecurityUtils.decryptAES(bankTellerDTO.getMobile()));
	    		}
	        	
	        	if(bankTellerDTO.getEmpId() != 0){
	        		bankTellerDTO.setTellerNo(empMapFinal.get(bankTellerDTO.getEmpId()).getEmpNo());
	        		bankTellerDTO.setTellerName(empMapFinal.get(bankTellerDTO.getEmpId()).getEmpName());
	        		if(!StringUtils.isBlank(empMapFinal.get(bankTellerDTO.getEmpId()).getMobile())){
	        			bankTellerDTO.setMobile(SecurityUtils.decryptAES(empMapFinal.get(bankTellerDTO.getEmpId()).getMobile()));
	        		}
	        	}
	        	
	        	return bankTellerDTO;
	        }).collect(Collectors.toList());
        }
        ResultList resultList = new ResultList.Builder().total(bankTellerPage.getTotal()).list(bankTellerDTOList).build();
        return Result.success(resultList);
    }
    
}
