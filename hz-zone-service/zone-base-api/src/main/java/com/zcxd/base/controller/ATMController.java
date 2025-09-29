package com.zcxd.base.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zcxd.base.dto.ATMDTO;
import com.zcxd.base.service.ATMService;
import com.zcxd.base.service.BankService;
import com.zcxd.base.vo.ATMVO;
import com.zcxd.common.annotation.OperateLog;
import com.zcxd.common.annotation.OperateType;
import com.zcxd.common.util.Result;
import com.zcxd.common.util.ResultList;
import com.zcxd.db.model.AtmDevice;
import com.zcxd.db.model.Bank;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;

/**
 * 
 * @ClassName ATMController
 * @Description ATM设备控制器
 * @author 秦江南
 * @Date 2021年5月27日下午3:49:17
 */
@RestController
@RequestMapping("/atm")
@Api(tags="ATM设备管理")
public class ATMController {

    @Autowired
    private ATMService atmService;
    
	@Autowired
    private BankService bankService;

	@OperateLog(value = "添加ATM设备", type=OperateType.ADD)
    @ApiOperation(value = "添加ATM设备")
    @ApiImplicitParam(name = "atmVO", value = "ATM设备", required = true, dataType = "ATMVO")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/save")
    public Result save(@RequestBody @Validated ATMVO atmVO){
    	AtmDevice atmDevice = new AtmDevice();
    	atmDevice.setTerNo(atmVO.getTerNo());
		List<AtmDevice> atmDeviceList = atmService.getATMByCondition(atmDevice);
		if(atmDeviceList != null && atmDeviceList.size()>0)
			return Result.fail("该ATM设备编号已存在，请重新填写！");
		
		BeanUtils.copyProperties(atmVO, atmDevice);
		
		//设置所属网点和所属顶级网点
        Bank bank = bankService.getById(atmVO.getBankId());
        String[] parentsIds = bank.getParentIds().substring(1, bank.getParentIds().length()-1).split("/");
		if (parentsIds.length > 1) {
			atmDevice.setBankId(Long.parseLong(parentsIds[1]));
		}else{
			atmDevice.setBankId(atmVO.getBankId());
		}
		
		atmDevice.setSubBankId(atmVO.getBankId());
        
        
		atmDevice.setId(null);
		atmDevice.setStatusT(0);
		atmDevice.setDeleted(0);
		
		boolean save = atmService.save(atmDevice);
    	if(save)
    		return Result.success();
    	
    	return Result.fail("添加ATM设备失败");
    }

//    @OperateLog(value = "删除ATM设备", type=OperateType.DELETE)
//    @ApiOperation(value = "删除ATM设备")
//    @ApiImplicitParam(name = "id", value = "ATM设备id", required = true, dataType = "Long")
//    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
//    @PostMapping("/delete/{id}")
//    public Result delete(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){
//    	AtmDevice atmDevice = new AtmDevice();
//    	atmDevice.setId(id);
//    	atmDevice.setDeleted(1);
//    	boolean update = atmService.updateById(atmDevice);
//    	
//    	if(update)
//    		return Result.success();
//    	
//    	return Result.fail("删除ATM设备失败");
//    }

    @OperateLog(value = "修改ATM设备", type=OperateType.EDIT)
    @ApiOperation(value = "修改ATM设备")
    @ApiImplicitParam(name = "atmVO", value = "ATM设备", required = true, dataType = "ATMVO")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/update")
    public Result update(@RequestBody @Validated ATMVO atmVO){
    	if (atmVO.getId() == null) {
            return Result.fail("ATM设备Id不能为空");
        }
    	
    	AtmDevice atmDevice = new AtmDevice();
    	atmDevice.setTerNo(atmVO.getTerNo());
		List<AtmDevice> atmDeviceList = atmService.getATMByCondition(atmDevice);
		if(atmDeviceList != null && atmDeviceList.size()>0)
			if(!atmDeviceList.get(0).getId().equals(atmVO.getId()))
				return Result.fail("该ATM设备编号已存在，请重新填写！");
		
		BeanUtils.copyProperties(atmVO, atmDevice);
		
        Bank bank = bankService.getById(atmVO.getBankId());
        String[] parentsIds = bank.getParentIds().substring(1, bank.getParentIds().length()-1).split("/");
		if (parentsIds.length > 1) {
			atmDevice.setBankId(Long.parseLong(parentsIds[1]));
		}else{
			atmDevice.setBankId(atmVO.getBankId());
		}
		
		atmDevice.setSubBankId(atmVO.getBankId());
    	
		boolean update = atmService.updateById(atmDevice);
    	if(update)
    		return Result.success();
    	
    	return Result.fail("修改ATM设备失败");
    }

//    @ApiOperation(value = "查询ATM设备列表")
//    @ApiImplicitParams({
//    	@ApiImplicitParam(name = "page", value = "当前页数", required = true, dataType = "Integer",defaultValue = "1"),
//        @ApiImplicitParam(name = "limit", value = "显示数据条数", required = true, dataType = "Integer",defaultValue = "10"),
//        @ApiImplicitParam(name = "terNo", value = "设备编号", required = false, dataType = "String"),
//        @ApiImplicitParam(name = "terType", value = "设备类型", required = false, dataType = "String"),
//        @ApiImplicitParam(name = "terFactory", value = "设备品牌", required = false, dataType = "String"),
//    })
//    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
//    @GetMapping("/list")
//    public Result findListByPage(@RequestParam Integer page,
//                                   @RequestParam Integer limit,
//                                   @RequestParam(required=false) @Size(max=32,message="terNo最大长度为32") String terNo,
//                                   @RequestParam(required=false) @Size(max=32,message="terType最大长度为32") String terType,
//                                   @RequestParam(required=false) @Size(max=32,message="terFactory最大长度为32") String terFactory){
//    	AtmDevice atmDevice = new AtmDevice();
//    	atmDevice.setTerNo(terNo);
//    	atmDevice.setTerType(terType);
//    	atmDevice.setTerFactory(terFactory);
//    	IPage<AtmDevice> atmPage = atmService.findListByPage(page, limit, atmDevice);
//    	
//    	Map<String, String> bankBoxMap = commonService.getBankMap(1);
//    	
//    	List<ATMDTO> atmList = atmPage.getRecords().stream().map(atmTmp -> {
//    		ATMDTO atmDTO = new ATMDTO();
//    		BeanUtils.copyProperties(atmTmp, atmDTO);
//    		atmDTO.setBankId(atmTmp.getBankId());
//    		atmDTO.setBankName(bankBoxMap.get(atmTmp.getSubBankId()+""));
//    		
//    		return atmDTO;
//    	}).collect(Collectors.toList());
//    	
//    	ResultList resultList = new ResultList.Builder().total(atmPage.getTotal()).list(atmList).build();
//        return Result.success(resultList);
//    }
    
    
  @ApiOperation(value = "查询ATM设备列表")
  @ApiImplicitParams({
  	  @ApiImplicitParam(name = "page", value = "当前页数", required = true, dataType = "Integer",defaultValue = "1"),
      @ApiImplicitParam(name = "limit", value = "显示数据条数", required = true, dataType = "Integer",defaultValue = "10"),
      @ApiImplicitParam(name = "bankId", value = "网点Id", required = true, dataType = "Long"),
      @ApiImplicitParam(name = "terNo", value = "设备编号", required = false, dataType = "String"),
      @ApiImplicitParam(name = "routeNo", value = "线路编号", required = false, dataType = "String")
  })
  @ApiResponse(code = 200, message = "处理成功", response = Result.class)
  @GetMapping("/list")
  public Result findListByPage(@RequestParam Integer page,
                                 @RequestParam Integer limit,
                                 @RequestParam Long bankId,
                                 @RequestParam(required=false) String terNo,
                                 @RequestParam(required=false) String routeNo){
  	AtmDevice atmDevice = new AtmDevice();
  	atmDevice.setBankId(bankId);
  	atmDevice.setTerNo(terNo);
  	IPage<AtmDevice> atmPage = atmService.findListByPage(page, limit, atmDevice , routeNo);
  	
  	List<ATMDTO> atmList = new ArrayList<ATMDTO>();
  	if(atmPage.getTotal() > 0){
	  	Set<Long> bankIds = new HashSet<>();
	  	atmPage.getRecords().stream().forEach(atmDeviceTmp-> {
	  		bankIds.add(atmDeviceTmp.getSubBankId());
	  		bankIds.add(atmDeviceTmp.getGulpBankId());
	  	});
    	List<Bank> bankList = bankService.listByIds(bankIds);
    	Map<Long,Bank> bankCleanMap = bankList.stream().collect(Collectors.toMap(Bank::getId, Bank -> Bank));
	  	
	  	atmList = atmPage.getRecords().stream().map(atmTmp -> {
	  		ATMDTO atmDTO = new ATMDTO();
	  		BeanUtils.copyProperties(atmTmp, atmDTO);
	  		atmDTO.setBankId(atmTmp.getSubBankId());
	  		atmDTO.setBankNo(bankCleanMap.get(atmTmp.getSubBankId()).getBankNo());
	  		atmDTO.setBankName(bankCleanMap.get(atmTmp.getSubBankId()).getShortName());
	  		atmDTO.setGulpBankName(bankCleanMap.get(atmTmp.getGulpBankId()).getShortName());
	  		return atmDTO;
	  	}).collect(Collectors.toList());
  	}
  	
  	ResultList resultList = new ResultList.Builder().total(atmPage.getTotal()).list(atmList).build();
      return Result.success(resultList);
  }
    
    
    @ApiOperation(value = "停用ATM")
    @ApiImplicitParam(name = "id", value = "ATM设备id", required = true, dataType = "Long")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/stop/{id}")
    public Result stop(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){
    	AtmDevice atmDevice = new AtmDevice();
    	atmDevice.setId(id);
    	atmDevice.setStatusT(1);
        boolean update = atmService.updateById(atmDevice);
    	if(update)
    		return Result.success();
    	
    	return Result.fail("停用ATM失败");
    }
    
    @ApiOperation(value = "启用ATM")
    @ApiImplicitParam(name = "id", value = "ATM设备id", required = true, dataType = "Long")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/enable/{id}")
    public Result enable(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){
    	AtmDevice atmDevice = new AtmDevice();
    	atmDevice.setId(id);
    	atmDevice.setStatusT(0);
        boolean update = atmService.updateById(atmDevice);
    	if(update)
    		return Result.success();
    	
    	return Result.fail("启用ATM失败");
    }
    
    
//	  @ApiOperation(value = "ATM设备下拉选项")
//	  @ApiImplicitParam(name = "bankId", value = "部门id", required = true, dataType = "Long")
//	  @ApiResponse(code = 200, message = "处理成功", response = Result.class)
//	  @GetMapping("/option/{bankId}")
//	  public Result option(@PathVariable("bankId") @Min(value=1, message="bankId不能小于1") Long bankId){
//	  	return Result.success(atmService.getATMOption(bankId));
//	  }
    
	  @ApiOperation(value = "ATM设备下拉选项")
	  @ApiImplicitParam(name = "departmentId", value = "部门id", required = true, dataType = "Long")
	  @ApiResponse(code = 200, message = "处理成功", response = Result.class)
	  @GetMapping("/option")
	  public Result option(@RequestParam Long departmentId){
		  
	    List<Map<String, Object>> bankList = bankService.listMaps((QueryWrapper)Wrappers.query().select("id,short_name as shortName,route_no as routeNo")
	    		.eq("department_id", departmentId).eq("deleted", 0));
	    Map<Object, Map> bankMap = bankList.stream().collect(Collectors.toMap(map->map.get("id"),map->map));
	    
	    Set<Object> bankIds = bankMap.keySet();
	    
		//获取atm编号集合
	    List<Map<String, Object>> atmTerNoList = atmService.listMaps((QueryWrapper)Wrappers.query().select("id,sub_bank_id as bankId,ter_no as terNo")
	    		.in("bank_id", bankIds).eq("deleted", 0));
	    
	    List<Map<String,Object>> atmOption = atmTerNoList.stream().map(atm->{
	    	atm.put("bankName", bankMap.get(atm.get("bankId")).get("shortName"));
	    	atm.put("routeNo", bankMap.get(atm.get("bankId")).get("routeNo").toString() + "号线");
	    	atm.remove("bankId");
	    	return atm;
	    }).collect(Collectors.toList());
	    
	  	return Result.success(atmOption);
	  }
  
}
