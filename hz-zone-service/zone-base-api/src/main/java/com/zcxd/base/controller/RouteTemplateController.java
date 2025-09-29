package com.zcxd.base.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import org.springframework.beans.BeanUtils;
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
import com.zcxd.base.dto.RouteTemplateDTO;
import com.zcxd.base.service.BankService;
import com.zcxd.base.service.RouteService;
import com.zcxd.base.service.RouteTemplateAtmService;
import com.zcxd.base.service.RouteTemplateBankService;
import com.zcxd.base.service.RouteTemplateRecordService;
import com.zcxd.base.service.RouteTemplateService;
import com.zcxd.base.vo.RouteTemplateATMVO;
import com.zcxd.base.vo.RouteTemplateBankVO;
import com.zcxd.base.vo.RouteTemplateVO;
import com.zcxd.common.annotation.OperateLog;
import com.zcxd.common.annotation.OperateType;
import com.zcxd.common.constant.BankCategoryTypeEnum;
import com.zcxd.common.util.Result;
import com.zcxd.common.util.ResultList;
import com.zcxd.db.model.Bank;
import com.zcxd.db.model.RouteTemplate;
import com.zcxd.db.model.RouteTemplateBank;
import com.zcxd.db.model.RouteTemplateRecord;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import lombok.AllArgsConstructor;

/**
 * 
 * @ClassName RouteTemplateController
 * @Description 线路模板控制器
 * @author 秦江南
 * @Date 2021年5月28日上午11:02:12
 */
@AllArgsConstructor
@RestController
@RequestMapping("/routeTemplate")
@Api(tags="线路模板管理")
public class RouteTemplateController {


    private RouteTemplateService routeTemplateService;

    private RouteTemplateAtmService routeTemplateAtmService;
    
    private RouteTemplateBankService routeTemplateBankService;

    private BankService bankService;

	private RouteTemplateRecordService recordService;

	private RouteService routeService;
    
    @OperateLog(value = "添加线路模板", type=OperateType.ADD)
    @ApiOperation(value = "添加线路模板")
    @ApiImplicitParam(name = "routeTemplateVO", value = "线路模板", required = true, dataType = "RouteTemplateVO")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/save")
    public Result save(@RequestBody @Validated RouteTemplateVO routeTemplateVO){
    	RouteTemplate routeTemplate = new RouteTemplate();
    	routeTemplate.setRouteNo(routeTemplateVO.getRouteNo());
		List<RouteTemplate> routeTemplateList = routeTemplateService.getRouteTemplateByCondition(routeTemplate);
		if(routeTemplateList != null && routeTemplateList.size()>0)
			return Result.fail("该线路模板编号已存在，请重新填写！");
		
		BeanUtils.copyProperties(routeTemplateVO, routeTemplate);
		if(routeTemplate.getRule() == 0){
			routeTemplate.setSign(0);
		}
		routeTemplate.setId(null);
		routeTemplate.setDeleted(0);
		
		boolean save = routeTemplateService.save(routeTemplate);
    	if(save)
    		return Result.success();
    	
    	return Result.fail("添加线路模板失败");
    }

    @OperateLog(value = "删除线路模板", type=OperateType.DELETE)
    @ApiOperation(value = "删除线路模板")
    @ApiImplicitParam(name = "id", value = "线路模板id", required = true, dataType = "Long")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/delete/{id}")
    public Result delete(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){
    	RouteTemplate routeTemplate = new RouteTemplate();
    	routeTemplate.setId(id);
    	routeTemplate.setDeleted(1);
    	boolean update = routeTemplateService.updateById(routeTemplate);
    	
    	if(update)
    		return Result.success();
    	
    	return Result.fail("删除线路模板失败");
    }

    @OperateLog(value = "修改线路模板", type=OperateType.EDIT)
    @ApiOperation(value = "修改线路模板")
    @ApiImplicitParam(name = "routeTemplateVO", value = "线路模板", required = true, dataType = "RouteTemplateVO")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/update")
    public Result update(@RequestBody @Validated RouteTemplateVO routeTemplateVO){
    	if (routeTemplateVO.getId() == null) {
            return Result.fail("线路模板Id不能为空");
        }
    	
    	RouteTemplate routeTemplate = new RouteTemplate();
    	routeTemplate.setRouteNo(routeTemplateVO.getRouteNo());
		List<RouteTemplate> routeTemplateList = routeTemplateService.getRouteTemplateByCondition(routeTemplate);
		if(routeTemplateList != null && routeTemplateList.size()>0)
			if(!routeTemplateList.get(0).getId().equals(routeTemplateVO.getId()))
				return Result.fail("该线路模板编号已存在，请重新填写！");
		
		BeanUtils.copyProperties(routeTemplateVO, routeTemplate);
		if(routeTemplate.getRule() == 0){
			routeTemplate.setSign(0);
		}
		boolean update = routeTemplateService.updateById(routeTemplate);
    	if(update)
    		return Result.success();
    	
    	return Result.fail("修改线路模板失败");
    }

    @ApiOperation(value = "查询线路模板列表")
    @ApiImplicitParams({
    	@ApiImplicitParam(name = "page", value = "当前页数", required = true, dataType = "Integer",defaultValue = "1"),
        @ApiImplicitParam(name = "limit", value = "显示数据条数", required = true, dataType = "Integer",defaultValue = "10"),
        @ApiImplicitParam(name = "routeNo", value = "线路编号", required = false, dataType = "String"),
        @ApiImplicitParam(name = "routeName", value = "线路名称", required = false, dataType = "String"),
        @ApiImplicitParam(name = "departmentId", value = "部门id", required = true, dataType = "Long")
    })
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @GetMapping("/list")
    public Result findListByPage(@RequestParam Integer page,
                                   @RequestParam Integer limit,
                                   @RequestParam(required=false) @Size(max=32,message="routeNo最大长度为32") String routeNo,
                                   @RequestParam(required=false) @Size(max=32,message="routeName最大长度为32") String routeName,
                                   @RequestParam @Min(value=1, message="departmentId不能小于1") Long departmentId){
    	RouteTemplate routeTemplate = new RouteTemplate();
    	routeTemplate.setRouteNo(routeNo);
    	routeTemplate.setRouteName(routeName);
    	routeTemplate.setDepartmentId(departmentId);
    	IPage<RouteTemplate> routeTemplatePage = routeTemplateService.findListByPage(page, limit, routeTemplate);
    	
    	List<RouteTemplateDTO> routeTemplateList = routeTemplatePage.getRecords().stream().map(routeTemplateTmp -> {
    		RouteTemplateDTO routeTemplateDTO = new RouteTemplateDTO();
    		BeanUtils.copyProperties(routeTemplateTmp, routeTemplateDTO);
    		return routeTemplateDTO;
    	}).collect(Collectors.toList());
    	
    	ResultList resultList = new ResultList.Builder().total(routeTemplatePage.getTotal()).list(routeTemplateList).build();
        return Result.success(resultList);
    }
    
    @OperateLog(value = "添加线路模板途经设备", type=OperateType.ADD)
    @ApiOperation(value = "添加线路模板途经设备")
    @ApiImplicitParam(name = "routeTemplateATMVO", value = "线路模板途经设备", required = true, dataType = "RouteTemplateATMVO")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/saveAtm")
    public Result saveAtm(@RequestBody @Validated RouteTemplateATMVO routeTemplateATMVO){
    	return routeTemplateAtmService.saveAtm(routeTemplateATMVO);
    }
    
    
//    @OperateLog(value = "修改线路模板途经设备", type=OperateType.EDIT)
//    @ApiOperation(value = "修改线路模板途经设备")
//    @ApiImplicitParam(name = "routeTemplateATMVO", value = "线路模板途经设备", required = true, dataType = "RouteTemplateATMVO")
//    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
//    @PostMapping("/updateAtm")
//    public Result updateAtm(@RequestBody @Validated RouteTemplateATMVO routeTemplateATMVO){
//    	if (routeTemplateATMVO.getId() == null) {
//            return Result.fail("线路模板设备Id不能为空");
//        }
//    	
//    	RouteTemplateAtm routeTemplateAtm = new RouteTemplateAtm();
//    	routeTemplateAtm.setRouteTemplateId(routeTemplateATMVO.getRouteTemplateId());
//    	routeTemplateAtm.setAtmId(routeTemplateATMVO.getAtmId());
//		List<RouteTemplateAtm> routeTemplateAtmList = routeTemplateAtmService.getRouteTemplateAtmByCondition(routeTemplateAtm);
//		if(routeTemplateAtmList != null && routeTemplateAtmList.size()>0)
//			if(!routeTemplateAtmList.get(0).getId().equals(routeTemplateATMVO.getId()))
//				return Result.fail("该设备已存在，不能重复添加");
//		
//		BeanUtils.copyProperties(routeTemplateATMVO, routeTemplateAtm);
//		//设置所属网点和所属顶级网点
//        Bank bank = bankService.getById(routeTemplateATMVO.getBankId());
//        String[] parentsIds = bank.getParentIds().substring(1, bank.getParentIds().length()-1).split("/");
//		if (parentsIds.length > 1) {
//			routeTemplateAtm.setBankId(Long.parseLong(parentsIds[1]));
//		}else{
//			routeTemplateAtm.setBankId(routeTemplateATMVO.getBankId());
//		}
//		routeTemplateAtm.setSubBankId(routeTemplateATMVO.getBankId());
//		
//		boolean update = routeTemplateAtmService.updateById(routeTemplateAtm);
//    	if(update)
//    		return Result.success();
//    	
//    	return Result.fail("修改线路模板设备失败");
//    }
    
    
    @OperateLog(value = "修改线路模板途经设备顺序", type=OperateType.EDIT)
    @ApiOperation(value = "修改线路模板途经设备顺序")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "线路模板设备id", required = true, dataType = "Long"),
        @ApiImplicitParam(name = "newSort", value = "新位置", required = true, dataType = "Integer")
    })
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/updateAtmSort")
    public Result updateAtmSort(@RequestParam Long id,@RequestParam Integer newSort){
    	return routeTemplateAtmService.updateAtmSort(id,newSort);
    }
    
    @OperateLog(value = "删除线路模板途经设备", type=OperateType.DELETE)
    @ApiOperation(value = "删除线路模板途经设备")
    @ApiImplicitParam(name = "id", value = "线路模板设备id", required = true, dataType = "Long")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/deleteAtm/{id}")
    public Result deleteAtm(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){
    	return routeTemplateAtmService.deleteAtm(id);
    }
    
//    @ApiOperation(value = "查询线路模板途径设备信息")
//    @ApiImplicitParams({
//		@ApiImplicitParam(name = "page", value = "当前页数", required = true, dataType = "Integer",defaultValue = "1"),
//	    @ApiImplicitParam(name = "limit", value = "显示数据条数", required = true, dataType = "Integer",defaultValue = "10"),
//	    @ApiImplicitParam(name = "id", value = "线路模板id", required = true, dataType = "Long")
//    })
//    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
//    @GetMapping("/atmInfo")
//    public Result atmInfo(@RequestParam Integer page,
//            @RequestParam Integer limit,
//            @RequestParam @Min(value=1, message="id不能小于1") Long id){
//    	RouteTemplate routeTemplate = routeTemplateService.getById(id);
//    	
//    	List<ATMDTO> atmDTOList = new ArrayList<ATMDTO>();
//    	Long total = 0L;
//    	
//    	//ATM设备列表
//    	if(routeTemplate.getAtmList() != "[]"){
//    		List<Map<String,Object>> atmMap = JSONArray.parseObject(routeTemplate.getAtmList(),List.class);
//    		List<Integer> idList = new ArrayList<>();
//    		atmMap.stream().forEach( atm -> {
//    			List<Integer> list = (List<Integer>) atm.get("atmId");
//    			idList.addAll(list);
//    		});
//    		
////    		List<String> idList = Arrays.asList(routeTemplate.getAtmList().substring(1, routeTemplate.getAtmList().length()-1).split("/"));
////	    	List<AtmDevice> atmDeviceList = atmService.listByIds(idList);
//    		
//	    	IPage<AtmDevice> atmPage = atmService.findListByIDs(page, limit, idList);
//	    	
//	    	atmDTOList = atmPage.getRecords().stream().map(atmTmp -> {
//	    		ATMDTO atmDTO = new ATMDTO();
//	    		BeanUtils.copyProperties(atmTmp, atmDTO);
//	    		return atmDTO;
//	    	}).collect(Collectors.toList());
//	    	
//	    	total = atmPage.getTotal();
//    	}
//    	ResultList resultList = new ResultList.Builder().total(total).list(atmDTOList).build();
//    	return Result.success(resultList);
//    }
    
    
  @ApiOperation(value = "查询线路模板途径设备信息")
  @ApiImplicitParams({
		@ApiImplicitParam(name = "routeTemplateId", value = "线路模板id", required = true, dataType = "Long"),
	    @ApiImplicitParam(name = "bankId", value = "网点id", required = false, dataType = "Long")
  })
  @ApiResponse(code = 200, message = "处理成功", response = Result.class)
  @GetMapping("/atmInfo")
  public Result atmInfo(@RequestParam Long routeTemplateId,
          @RequestParam(required=false) Long bankId){
	  List<Map<String, Object>> routeTemplateAtmList = routeTemplateAtmService.getRouteTemplateAtmList(routeTemplateId, bankId);
	  
	  if(routeTemplateAtmList != null && routeTemplateAtmList.size()>0){
		  Set<Long> bankIds = routeTemplateAtmList.stream().map(map-> Long.parseLong(map.get("headBankId").toString())).collect(Collectors.toSet());
	  	  List<Bank> bankList = bankService.listByIds(bankIds);
	  	  Map<Long,String> bankCleanMap = bankList.stream().collect(Collectors.toMap(Bank::getId,Bank::getShortName));
		  
		  routeTemplateAtmList.stream().forEach(map->{
			  map.put("headBank", bankCleanMap.get(map.get("headBankId")));
			  map.remove("headBankId");
		  });
	  }
	  return Result.success(routeTemplateAtmList);
  }
    
    
    @ApiOperation(value = "查询线路模板途径网点信息")
	@ApiImplicitParam(name = "id", value = "线路模板id", required = true, dataType = "Long")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @GetMapping("/bankInfo/{id}")
    public Result bankInfo(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){
    	List<Map<String, Object>> templateBankList = routeTemplateBankService.listMaps((QueryWrapper)Wrappers.query().select("id,bank_id as bankId")
	    		.eq("route_template_id", id));
    	
    	if(templateBankList != null && templateBankList.size() > 0){
    		Set<Long> bankIds = templateBankList.stream().map(map-> Long.parseLong(map.get("bankId").toString())).collect(Collectors.toSet());
        	List<Bank> bankList = bankService.listByIds(bankIds);
    	  	Map<Long,String> bankCleanMap = bankList.stream().collect(Collectors.toMap(Bank::getId,Bank::getShortName));
    	  	templateBankList.stream().forEach(templateBank -> {
    	  		templateBank.put("bankName", bankCleanMap.get(Long.parseLong(templateBank.get("bankId").toString())));
    	  	});
    	}
    	return Result.success(templateBankList);
    }
    
    
    @OperateLog(value = "添加线路模板途经网点", type=OperateType.ADD)
    @ApiOperation(value = "添加线路模板途经网点")
    @ApiImplicitParam(name = "routeTemplateBankVO", value = "线路模板途经网点", required = true, dataType = "RouteTemplateBankVO")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/saveBank")
    public Result saveBank(@RequestBody @Validated RouteTemplateBankVO routeTemplateBankVO){
    	Bank bank = bankService.getById(routeTemplateBankVO.getBankId());
    	if(bank.getBankCategory().equals(BankCategoryTypeEnum.STORAGE.getValue())){
    		RouteTemplateBank byCondition = routeTemplateBankService.getByCondition(routeTemplateBankVO.getBankId(), routeTemplateBankVO.getRouteTemplateId());
    		if(null != byCondition){
    			return Result.fail("库房不能在同一条线路上不能出现两次");
    		}
    	}else{
    		RouteTemplateBank byCondition = routeTemplateBankService.getByCondition(routeTemplateBankVO.getBankId(), null);
    		if(null != byCondition){
    			return Result.fail("营业机构不能出现在多条线路上");
    		}
    	}
    	
    	RouteTemplateBank routeTemplateBank = new RouteTemplateBank();
    	BeanUtils.copyProperties(routeTemplateBankVO, routeTemplateBank);
    	boolean save = routeTemplateBankService.save(routeTemplateBank);
    	if(save)
    		return Result.success();
    	
    	return Result.fail("添加途经网点失败");
    }
    
    @OperateLog(value = "删除线路模板途经网点", type=OperateType.DELETE)
    @ApiOperation(value = "删除线路模板途经网点")
    @ApiImplicitParam(name = "id", value = "线路模板网点id", required = true, dataType = "Long")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/deleteBank/{id}")
    public Result deleteBank(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){
    	
    	boolean del = routeTemplateBankService.removeById(id);
    	if(del)
    		return Result.success();
    	
    	return Result.fail("删除途经网点失败");
    }


	@ApiOperation(value = "查询线路模板下拉列表")
	@ApiImplicitParam(name = "departmentId", value = "部门id", required = true, dataType = "Long")
	@ApiResponse(code = 200, message = "处理成功", response = Result.class)
	@GetMapping("/listOption")
	public Result listOption(@RequestParam @Min(value=1, message="departmentId不能小于1") Long departmentId){
		RouteTemplate routeTemplate = new RouteTemplate();
		routeTemplate.setDepartmentId(departmentId);
		List<RouteTemplate> routeTemplateList = routeTemplateService.getRouteTemplateByCondition(routeTemplate);

		List<RouteTemplateDTO> routeTemplateDTOList = routeTemplateList.stream().map(routeTemplateTmp -> {
			RouteTemplateDTO routeTemplateDTO = new RouteTemplateDTO();
			routeTemplateDTO.setId(routeTemplateTmp.getId());
			routeTemplateDTO.setRouteNo(routeTemplateTmp.getRouteNo());
			routeTemplateDTO.setRouteName(routeTemplateTmp.getRouteName());
			return routeTemplateDTO;
		}).collect(Collectors.toList());

		return Result.success(routeTemplateDTOList);
	}

	@ApiOperation(value = "定时线路生成记录信息-分页")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "page", value = "当前页数", required = true, dataType = "Integer ",defaultValue = "1",dataTypeClass = Integer.class),
			@ApiImplicitParam(name = "limit", value = "显示数据条数", required = true, dataType = "Integer ",defaultValue = "10",dataTypeClass = Integer.class),
			@ApiImplicitParam(name = "departmentId", value = "部门id", required = true, dataType = "Long", dataTypeClass = Long.class)
	})
	@GetMapping("/record")
	public Result record(@RequestParam(defaultValue = "1") Integer page,
						 @RequestParam(defaultValue = "10") Integer limit,
						 @RequestParam(defaultValue = "0") Long departmentId){
		IPage<RouteTemplateRecord> recordIPage = recordService.findListByPage(page, limit, departmentId);
		long total = recordIPage.getTotal();
		List<RouteTemplateRecord> recordList = recordIPage.getRecords();
		ResultList resultList = new ResultList.Builder().total(total).list(recordList).build();
		return Result.success(resultList);
	}

	@OperateLog(value = "线路任务初始化执行操作", type = OperateType.EDIT)
	@ApiImplicitParam(name = "departmentId", value = "部门id", required = true, dataType = "Long", dataTypeClass = Long.class)
	@PostMapping("/executeTask")
	public Result executeTask(@RequestParam(defaultValue = "0") Long departmentId){
        boolean b = routeService.executeRoute(departmentId);
    	return b ? Result.success("线路任务初始化执行成功") : Result.fail("线路任务执行失败或已初始化");
	}
}
