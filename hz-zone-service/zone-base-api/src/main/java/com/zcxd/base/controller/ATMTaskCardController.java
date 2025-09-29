package com.zcxd.base.controller;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Min;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
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

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.WriteTable;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zcxd.base.dto.AtmTaskCardDTO;
import com.zcxd.base.dto.BankCardHandOverDTO;
import com.zcxd.base.dto.excel.AtmTaskCardCollectHead;
import com.zcxd.base.dto.excel.AtmTaskCardDeliverHead;
import com.zcxd.base.dto.excel.AtmTaskCardHead;
import com.zcxd.base.exception.BusinessException;
import com.zcxd.base.service.ATMService;
import com.zcxd.base.service.ATMTaskCardService;
import com.zcxd.base.service.ATMTaskService;
import com.zcxd.base.service.BankService;
import com.zcxd.base.service.SysUserService;
import com.zcxd.base.util.ExcelUtil;
import com.zcxd.base.vo.AtmTaskCardVO;
import com.zcxd.base.vo.BankCardDeliverSettingVO;
import com.zcxd.base.vo.BankCardEditVO;
import com.zcxd.common.annotation.OperateLog;
import com.zcxd.common.annotation.OperateType;
import com.zcxd.common.constant.BankCardStatusEnum;
import com.zcxd.common.util.DateTimeUtil;
import com.zcxd.common.util.EasyExcelUtil;
import com.zcxd.common.util.Result;
import com.zcxd.common.util.ResultList;
import com.zcxd.db.model.AtmClearTask;
import com.zcxd.db.model.AtmDevice;
import com.zcxd.db.model.AtmTask;
import com.zcxd.db.model.AtmTaskCard;
import com.zcxd.db.model.Bank;
import com.zcxd.db.model.SysUser;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;

/**
 * 
 * @ClassName ATMTaskCardController
 * @Description 吞没卡控制器
 * @author 秦江南
 * @Date 2021年7月25日上午10:49:04
 */
@RestController
@RequestMapping("/atmTaskCard")
@Api(tags="吞没卡")
public class ATMTaskCardController {
	private static int QUERY_GET = 0;
	private static int QUERY_SEND = 1;
    @Autowired
    private ATMTaskCardService atmTaskCardService;
    
	@Autowired
	private SysUserService userService;
	
	@Autowired
	private ATMService atmDeviceService;
	
	@Autowired
	private BankService bankService;
	
	@Autowired
	private ATMTaskService atmTaskService;
	
    @ApiOperation(value = "查询吞没卡列表")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "page", value = "当前页数", required = true, dataType = "Integer",defaultValue = "1"),
			@ApiImplicitParam(name = "limit", value = "显示数据条数", required = true, dataType = "Integer",defaultValue = "10"),
			@ApiImplicitParam(name = "bankId", value = "所属机构", required = false, dataType = "Long"),
			@ApiImplicitParam(name = "routeNo", value = "送卡线路", required = false, dataType = "String"),
			@ApiImplicitParam(name = "cardNo", value = "银行卡号", required = false, dataType = "String"),
			@ApiImplicitParam(name = "queryDay", value = "查询日期", required = false, dataType = "Long"),
			@ApiImplicitParam(name = "statusT", value = "状态", required = false, dataType = "Integer"),
			@ApiImplicitParam(name = "queryType", value = "查询类型(0 - 回笼查询，1 - 派送查询)", required = false, dataType = "Integer"),
        	@ApiImplicitParam(name = "departmentId", value = "部门id", required = true, dataType = "Long")
    })
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @GetMapping("/list")
    public Result findListByPage(@RequestParam Integer page,
	                                 @RequestParam Integer limit,
	                                 @RequestParam(required=false) Long bankId,
	                                 @RequestParam(required=false) String routeNo,
	                                 @RequestParam(required=false) String cardNo,
								 	@RequestParam(required=false) Long queryDay,
	                                 @RequestParam(required=false) Integer statusT,
	                                 @RequestParam(required=true) Integer queryType,
	                                 @RequestParam Long departmentId){

    	AtmTaskCard atmTaskCard = new AtmTaskCard();
    	if (queryType == QUERY_GET) { //查询回笼
			atmTaskCard.setRouteNo(routeNo);
			atmTaskCard.setCreateTime(queryDay);
		} else {  //查询配送
			atmTaskCard.setDeliverRouteNo(routeNo);
			if (null != queryDay) {
				atmTaskCard.setDeliverDay(DateTimeUtil.timeStampMs2Date(queryDay, "yyyy-MM-dd"));
			}
		}
    	atmTaskCard.setBankId(bankId);
    	atmTaskCard.setStatusT(statusT);
    	atmTaskCard.setCardNo(cardNo);
    	atmTaskCard.setDepartmentId(departmentId);

    	IPage<AtmTaskCard> atmTaskCardPage = atmTaskCardService.findListByPage(page, limit, atmTaskCard);
    	List<AtmTaskCardDTO> atmTaskCardList = new ArrayList<AtmTaskCardDTO>();
    	
    	if(atmTaskCardPage.getTotal()>0){
    		//数据涉及用户id
	    	Set<Long> userIds = new HashSet<>();
	    	//数据涉及atmId
	    	Set<Long> atmIds = new HashSet<>();
	    	//数据设计bankId
	    	Set<Long> bankIds = new HashSet<>();
	    	
	    	//提取Id
			atmTaskCardPage.getRecords().stream().forEach(atmTaskCardTmp->{
				atmIds.add(atmTaskCardTmp.getAtmId());
				bankIds.add(atmTaskCardTmp.getDeliverBankId());
				bankIds.add(atmTaskCardTmp.getBankId());

				if(atmTaskCardTmp.getCollectManA() != 0){
					userIds.add(atmTaskCardTmp.getCollectManA());
				}
				if(atmTaskCardTmp.getDispatchManA() != 0){
					userIds.add(atmTaskCardTmp.getDispatchManA());
				}
			});

			List<SysUser> userList = new ArrayList<>();
			if(userIds.size()>0){
				userList = userService.listByIds(userIds);
			}
	    	Map<Long,String> userMap = userList.stream().collect(Collectors.toMap(SysUser::getId,SysUser::getNickName));

			Map<Long,String> atmMap = new HashMap<>();
			if (atmIds.size() > 0) {
				List<AtmDevice> atmList = atmDeviceService.listByIds(atmIds);
				atmMap =atmList.stream().collect(Collectors.toMap(AtmDevice::getId, AtmDevice::getTerNo));
			}
			
			Map<Long,String> bankCleanMap = new HashMap<>();
			if (bankIds.size() > 0) {
		    	List<Bank> bankList = bankService.listByIds(bankIds);
		    	bankCleanMap = bankList.stream().collect(Collectors.toMap(Bank::getId,Bank::getShortName));
			}

			Map<Long, String> finalAtmMap = atmMap;
			Map<Long, String> finalBankCleanMap = bankCleanMap;
			atmTaskCardList = atmTaskCardPage.getRecords().stream().map(atmTaskCardTmp -> {
	    		AtmTaskCardDTO atmTaskCardDTO = new AtmTaskCardDTO();
	    		BeanUtils.copyProperties(atmTaskCardTmp, atmTaskCardDTO);
				atmTaskCardDTO.setBankName(finalBankCleanMap.get(atmTaskCardTmp.getBankId()));
				atmTaskCardDTO.setRetriveDay(DateTimeUtil.timeStampMs2Date(atmTaskCardTmp.getCreateTime(),"yyyy-MM-dd"));
	    		atmTaskCardDTO.setAtmTerNo(finalAtmMap.get(atmTaskCardTmp.getAtmId()));
	    		atmTaskCardDTO.setCollectManAName(atmTaskCardTmp.getCollectManA() == 0 ? "" : userMap.get(atmTaskCardTmp.getCollectManA()));
	    		atmTaskCardDTO.setDispatchManAName(atmTaskCardTmp.getDispatchManA() == 0 ? "" : userMap.get(atmTaskCardTmp.getDispatchManA()));
	    		atmTaskCardDTO.setDeliverBankName(finalBankCleanMap.get(atmTaskCardTmp.getDeliverBankId()));
	    		return atmTaskCardDTO;
	    	}).collect(Collectors.toList());
    	}
	  	ResultList resultList = new ResultList.Builder().total(atmTaskCardPage.getTotal()).list(atmTaskCardList).build();
	    return Result.success(resultList);
    }
    
    @OperateLog(value = "批量分配", type=OperateType.EDIT)
    @ApiOperation(value = "批量分配")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @ApiImplicitParam(name = "deliverDay", value = "分配日期", required = true, dataType = "Long")
    @PostMapping("/batchDistribute")
    public Result batchDistribute(@RequestParam Long deliverDay){
    	return atmTaskCardService.batchDistribute(deliverDay);
    }
    
    @OperateLog(value = "修改送卡信息", type=OperateType.EDIT)
    @ApiOperation(value = "修改送卡信息")
	@ApiImplicitParam(name = "bankCardDeliverSettingVO", value = "更新设置", required = true, dataType = "BankCardDeliverSettingVO")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/update")
    public Result update(@RequestBody BankCardDeliverSettingVO bankCardDeliverSettingVO){
    	AtmTaskCard atmTaskCard = atmTaskCardService.getById(bankCardDeliverSettingVO.getId());
    	if(!atmTaskCard.getStatusT().equals(BankCardStatusEnum.COLLECT.getValue())){
    		return Result.fail("吞没卡未入库");
    	}

		AtmTaskCard newAtmTaskCard = new AtmTaskCard();
		newAtmTaskCard.setId(atmTaskCard.getId());
		newAtmTaskCard.setDeliverBankId(bankCardDeliverSettingVO.getDeliverBankId());
    	newAtmTaskCard.setDeliverRouteNo(bankCardDeliverSettingVO.getDeliverRouteNo());
		String newDeliveDay = DateTimeUtil.timeStampMs2Date(bankCardDeliverSettingVO.getDeliverDay(),"yyyy-MM-dd");
		newAtmTaskCard.setDeliverDay(newDeliveDay);
		boolean bRet = atmTaskCardService.updateById(newAtmTaskCard);
    	return bRet? Result.success() : Result.fail("修改送卡信息失败");
    }
    
    @OperateLog(value = "修改吞卡信息", type=OperateType.EDIT)
    @ApiOperation(value = "修改吞卡信息")
	@ApiImplicitParam(name = "bankCardEditVO", value = "吞卡信息", required = true, dataType = "BankCardEditVO")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/edit")
    public Result edit(@RequestBody BankCardEditVO bankCardEditVO){
    	AtmTaskCard atmTaskCard = atmTaskCardService.getById(bankCardEditVO.getId());

		AtmTaskCard newAtmTaskCard = new AtmTaskCard();
		newAtmTaskCard.setId(atmTaskCard.getId());
		newAtmTaskCard.setCardBank(bankCardEditVO.getCardBank());
    	newAtmTaskCard.setCardNo(bankCardEditVO.getCardNo());
		boolean bRet = atmTaskCardService.updateById(newAtmTaskCard);
    	return bRet? Result.success() : Result.fail("修改吞卡信息失败");
    }
    

    @OperateLog(value = "删除吞卡信息", type= OperateType.DELETE)
    @ApiOperation(value = "删除吞卡信息")
    @ApiImplicitParam(name = "id", value = "吞卡id", required = true, dataType = "Long", dataTypeClass = Long.class)
    @PostMapping("/delete/{id}")
    public Result delete(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){
        atmTaskCardService.removeById(id);
        return Result.success("删除吞卡信息成功");
    }

	@OperateLog(value = "交接吞没卡", type=OperateType.EDIT)
	@ApiOperation(value = "交接（收卡/配卡")
	@ApiResponse(code = 200, message = "处理成功", response = Result.class)
	@ApiImplicitParam(name = "bankCardHandOverDTO", value = "交接信息", required = true, dataType = "BankCardHandOverDTO")
	@PostMapping("/handover")
	public Result handover(@RequestBody BankCardHandOverDTO bankCardHandOverDTO){
		atmTaskCardService.updateBankCardHandover(bankCardHandOverDTO);
		return Result.success();
	}

	@GetMapping("/download")
	public void download(HttpServletResponse response,
						  @RequestParam(required=false) Long bankId,
						  @RequestParam(required=false) Long queryDay,
						  @RequestParam(required=false) String routeNo,
						  @RequestParam(required=false) Integer statusT,
						  @RequestParam(required=false) String cardNo,
						  @RequestParam(required=true) Integer queryType,
						  @RequestParam Long departmentId) throws IOException {
		AtmTaskCard atmTaskCard = new AtmTaskCard();
        //设置日期
		LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(queryDay/1000, 0, ZoneOffset.of("+8"));
		String taskDate =  localDateTime.format(DateTimeFormatter.ofPattern("MM/dd"));

		//查询回笼
		if (queryType == QUERY_GET) {
			atmTaskCard.setRouteNo(routeNo);
			atmTaskCard.setCreateTime(queryDay);
		} else {  //查询配送
			atmTaskCard.setDeliverRouteNo(routeNo);
			if (null != queryDay) {
				atmTaskCard.setDeliverDay(DateTimeUtil.timeStampMs2Date(queryDay, "yyyy-MM-dd"));
			}
		}
		atmTaskCard.setBankId(bankId);
		atmTaskCard.setStatusT(statusT);
		atmTaskCard.setCardNo(cardNo);
		atmTaskCard.setDepartmentId(departmentId);

		List<AtmTaskCard> cardList = atmTaskCardService.getTaskCardByCondition(atmTaskCard);

		//数据按线路进行分组
		Map<String,List<AtmTaskCard>> routeMap = cardList.stream().collect(Collectors.groupingBy(AtmTaskCard::getRouteNo));

		Set<Long> atmIds = cardList.stream().map(AtmTaskCard::getAtmId).collect(Collectors.toSet());
		Map<Long,String> atmMap = new HashMap<>();
		if(atmIds.size() > 0){
			List<AtmDevice> deviceList = atmDeviceService.listByIds(atmIds);
			atmMap = deviceList.stream().collect(Collectors.toMap(AtmDevice::getId,AtmDevice::getTerNo));
		}
		Map<Long,String> bankMap = new HashMap<>();
		Set<Long> bankIds = cardList.stream().map(AtmTaskCard::getDeliverBankId).collect(Collectors.toSet());
		if (bankIds.size() > 0){
			List<Bank> bankList = bankService.listByIds(bankIds);
			bankMap = bankList.stream().collect(Collectors.toMap(Bank::getId,Bank::getShortName));
		}

		//excel基础数据设置
		ExcelUtil excelUtil = new ExcelUtil();
		ExcelWriter excelWriter = excelUtil.getExcelWriter("吞没卡信息数据",response);
		WriteSheet writeSheet = EasyExcel.writerSheet("吞没卡信息数据").needHead(Boolean.FALSE).build();

		AtomicInteger index = new AtomicInteger();
		Map<Long, String> finalAtmMap = atmMap;
		Map<Long, String> finalBankMap = bankMap;
		routeMap.entrySet().stream().forEach(entry -> {
			String routeNoT = entry.getKey();
			List<AtmTaskCard> taskCardList = entry.getValue();
			AtomicInteger innerIndex = new AtomicInteger();
			innerIndex.getAndIncrement();
			List<AtmTaskCardHead> headList = taskCardList.stream().map(item -> {
				AtmTaskCardHead cardHead = new AtmTaskCardHead();
				cardHead.setIndex(innerIndex.get());
				cardHead.setCardNo(item.getCardNo());
				cardHead.setTerNo(Optional.ofNullable(finalAtmMap.get(item.getAtmId())).orElse(""));
				cardHead.setDeliverBankName(Optional.ofNullable(finalBankMap.get(item.getDeliverBankId())).orElse(""));
				innerIndex.getAndIncrement();
				return cardHead;
			}).collect(Collectors.toList());
            int size = headList.size();
			int total = 35;
			int start = size+1;
			List<AtmTaskCardHead> headList2 = IntStream.rangeClosed(start,total).mapToObj(t -> {
				AtmTaskCardHead cardHead = new AtmTaskCardHead();
				cardHead.setIndex(t);
				cardHead.setCardNo("");
				cardHead.setTerNo("");
				return cardHead;
			}).collect(Collectors.toList());
            headList.addAll(headList2);

			try {
				AtmTaskCardHead.setExcelPropertyTitle(taskDate,routeNoT);
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}

			//设置边框
			HorizontalCellStyleStrategy strategy = getTableStyle();
			WriteTable writeTable0 = EasyExcel.writerTable(index.get()).needHead(Boolean.TRUE)
					.head(AtmTaskCardHead.class).registerWriteHandler(strategy).build();
			excelWriter.write(headList,writeSheet,writeTable0);
			index.getAndIncrement();
		});

		excelWriter.finish();
	}

//	@ApiOperation(value = "导出吞没卡列表")
//	@ApiImplicitParams({
//			@ApiImplicitParam(name = "bankId", value = "所属机构", required = false, dataType = "Long"),
//			@ApiImplicitParam(name = "routeNo", value = "送卡线路", required = false, dataType = "String"),
//			@ApiImplicitParam(name = "cardNo", value = "银行卡号", required = false, dataType = "String"),
//			@ApiImplicitParam(name = "queryDay", value = "查询日期", required = false, dataType = "Long"),
//			@ApiImplicitParam(name = "statusT", value = "状态", required = false, dataType = "Integer"),
//			@ApiImplicitParam(name = "queryType", value = "查询类型(0 - 回笼查询，1 - 派送查询)", required = false, dataType = "Integer"),
//			@ApiImplicitParam(name = "departmentId", value = "部门id", required = true, dataType = "Long")
//	})
//	@ApiResponse(code = 200, message = "处理成功", response = Result.class)
//	@GetMapping("/bankExport")
//	public void bankExport(HttpServletResponse response,
//						   @RequestParam Long bankId,
//						   @RequestParam(required=false) Long queryDay,
//						   @RequestParam(required=false) String routeNo,
//						   @RequestParam(required=false) Integer statusT,
//						   @RequestParam(required=false) String cardNo,
//						   @RequestParam Long departmentId) throws IOException {
//		AtmTaskCard atmTaskCard = new AtmTaskCard();
//		atmTaskCard.setBankId(bankId);
//		atmTaskCard.setStatusT(statusT);
//		atmTaskCard.setCardNo(cardNo);
//		atmTaskCard.setDepartmentId(departmentId);
//		atmTaskCard.setDeliverRouteNo(routeNo);
//		//查询当天回笼卡
//		atmTaskCard.setCollectTime(queryDay);
////		List<AtmTaskCard> cardListT = atmTaskCardService.getTaskCardByCondition(atmTaskCard);
//		List<AtmTaskCard> cardListT = atmTaskCardService.listCollectCardByDay(departmentId,bankId,queryDay);
//		//查询当天送卡
//		atmTaskCard.setCollectTime(null);
//		atmTaskCard.setReceiveTime(queryDay);
//
//		AtomicInteger atomicInteger = new AtomicInteger(1);
////		List<AtmTaskCard> cardList = atmTaskCardService.getTaskCardByCondition(atmTaskCard);
//		List<AtmTaskCard> cardList = atmTaskCardService.listDeliverCardByDay(departmentId,bankId,queryDay);
//
//		LinkedList<AtmTaskCard> resultList = new LinkedList();
//		resultList.addAll(cardListT);
//		
//		for (int i = 0; i < 10; i++) {
//			resultList.add(new AtmTaskCard());
//		}
//		
//		resultList.addAll(cardList);
//
//		Set<Long> atmIds = resultList.stream().map(AtmTaskCard::getAtmId).collect(Collectors.toSet());
//        Map<Long,String> atmMap = new HashMap<>();
//        if(atmIds.size() > 0){
//        	List<AtmDevice> deviceList = atmDeviceService.listByIds(atmIds);
//        	atmMap = deviceList.stream().collect(Collectors.toMap(AtmDevice::getId,AtmDevice::getTerNo));
//		}
//        //记录当前时间
//		Map<Long,String> bankMap = new HashMap<>();
//        Set<Long> bankIds = resultList.stream().map(AtmTaskCard::getDeliverBankId).collect(Collectors.toSet());
//        if (bankIds.size() > 0){
//			List<Bank> bankList = bankService.listByIds(bankIds);
//			bankMap = bankList.stream().collect(Collectors.toMap(Bank::getId,Bank::getShortName));
//		}
//
//		Map<Long, String> finalAtmMap = atmMap;
//		Map<Long, String> finalBankMap = bankMap;
//		LocalDate localDate = LocalDate.now().minusDays(1L);
//		String today = LocalDate.now().format(DateTimeFormatter.ofPattern("MM-dd"));
//		List<AtmCardExcelVO> excelVOList = resultList.stream().map(item -> {
//			AtmCardExcelVO excelVO = new AtmCardExcelVO();
//			if(item.getId()!=null){
//				Long collectTime = item.getCollectTime();
//				boolean isYesterday = false;
//				String acceptDate = "";
//				if(collectTime != 0L) {
//					LocalDate localDateT = LocalDateTime.ofInstant(Instant.ofEpochMilli(collectTime), ZoneOffset.of("+8")).toLocalDate();
//					isYesterday = localDate.equals(localDateT);
//					acceptDate = localDateT.format(DateTimeFormatter.ofPattern("MM-dd"));
//				}
//				
//				
//				excelVO.setIndex(atomicInteger.get());
//				excelVO.setCardNo(item.getCardNo());
//				excelVO.setIsYesterday(isYesterday ? "是" : "否");
//				excelVO.setCategory(item.getCategory() == 0 ? "实" : "回");
//				excelVO.setDeliverType(item.getDeliverType() == 0 ? "交" : "现");
//				excelVO.setRouteNo(item.getRouteNo()+"号线");
//				excelVO.setAtmNo(Optional.ofNullable(finalAtmMap.get(item.getAtmId())).orElse(""));
//				excelVO.setCardBank(item.getCardBank());
//				excelVO.setDeliveryRouteNo(item.getDeliverRouteNo()+"号线");
//				excelVO.setAddress(Optional.ofNullable(finalBankMap.get(item.getDeliverBankId())).orElse(""));
//				excelVO.setDate(acceptDate);
//				excelVO.setMakeMan("");
//				excelVO.setCheckMan("");
//				excelVO.setDeliverDate(today);
//				excelVO.setComments(item.getComments());
//				excelVO.setAcceptMan("");
//				atomicInteger.getAndIncrement();
//			}
//			return excelVO;
//		}).collect(Collectors.toList());
//
//		ClassPathResource resource = new ClassPathResource("doc/card_bank_template.xlsx");
//		InputStream templateFileStream = resource.getInputStream();
//		//excel基础数据设置
//		ExcelUtil excelUtil = new ExcelUtil();
//		ExcelWriter excelWriter = excelUtil.getTemplateWriter("银行-吞没卡信息数据",templateFileStream,response);
//
//		//请求数据
//		WriteSheet writeSheet = EasyExcel.writerSheet().build();
//		FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.TRUE).build();
//		excelWriter.fill(excelVOList, fillConfig, writeSheet);
//		excelWriter.finish();
//	}
	
	
	private HorizontalCellStyleStrategy getTableStyle(){
		// 头的策略
		WriteCellStyle headWriteCellStyle = new WriteCellStyle();
		// 设置标题
		WriteFont headWriteFont = new WriteFont();
		headWriteFont.setFontHeightInPoints((short)16);
		headWriteCellStyle.setWriteFont(headWriteFont);
		headWriteCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
		// 内容的策略
		WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
		WriteFont contentWriteFont = new WriteFont();
		// 字体大小
		contentWriteFont.setFontHeightInPoints((short)12);
		//设置边框
		contentWriteCellStyle.setWriteFont(contentWriteFont);
		contentWriteCellStyle.setBorderTop(BorderStyle.THIN);
		contentWriteCellStyle.setBorderLeft(BorderStyle.THIN);
		contentWriteCellStyle.setBorderBottom(BorderStyle.THIN);
		contentWriteCellStyle.setBorderRight(BorderStyle.THIN);
		contentWriteCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
		// 这个策略是 头是头的样式 内容是内容的样式 其他的策略可以自己实现
		HorizontalCellStyleStrategy strategy = new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
		return strategy;
	}
	
	
	@ApiOperation(value = "导出回收汇总")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "bankId", value = "所属机构", required = false, dataType = "Long"),
			@ApiImplicitParam(name = "queryDay", value = "查询日期", required = true, dataType = "Long"),
			@ApiImplicitParam(name = "departmentId", value = "部门id", required = true, dataType = "Long")
	})
	@ApiResponse(code = 200, message = "处理成功", response = Result.class)
	@GetMapping("/exportCollect")
	public void exportCollect(HttpServletResponse response,
						   @RequestParam(required=false) Long bankId,
						   @RequestParam Long queryDay,
						   @RequestParam Long departmentId) {
	  List<AtmTaskCard> cardList = atmTaskCardService.listCollectCardByDay(departmentId,bankId,queryDay);
	  List<AtmTaskCardCollectHead> cardCollectList = new ArrayList<>();
	  if(cardList != null && cardList.size() > 0){
		  	AtomicInteger atomicInteger = new AtomicInteger(1);
	    	//数据涉及atmId
	    	Set<Long> atmIds = new HashSet<>();
	    	//数据设计bankId
	    	Set<Long> bankIds = new HashSet<>();
	    	
	    	//提取Id
	    	cardList.stream().forEach(atmTaskCardTmp->{
				atmIds.add(atmTaskCardTmp.getAtmId());
				bankIds.add(atmTaskCardTmp.getDeliverBankId());
			});

			Map<Long,String> atmMap = new HashMap<>();
			if (atmIds.size() > 0) {
				List<AtmDevice> atmList = atmDeviceService.listByIds(atmIds);
				atmMap =atmList.stream().collect(Collectors.toMap(AtmDevice::getId, AtmDevice::getTerNo));
			}
			
	    	List<Bank> bankList = bankService.listByIds(bankIds);
	    	Map<Long,String> bankCleanMap = bankList.stream().collect(Collectors.toMap(Bank::getId,Bank::getShortName));

			Map<Long, String> finalAtmMap = atmMap;
			cardCollectList = cardList.stream().map(atmTaskCardTmp -> {
				AtmTaskCardCollectHead cardCollectHead = new AtmTaskCardCollectHead();
	    		BeanUtils.copyProperties(atmTaskCardTmp, cardCollectHead);
	    		cardCollectHead.setIndex(atomicInteger.get());
	    		cardCollectHead.setTerNo(finalAtmMap.get(atmTaskCardTmp.getAtmId()));
	    		cardCollectHead.setCardBankName(atmTaskCardTmp.getCardBank());
	    		cardCollectHead.setRouteNo(atmTaskCardTmp.getRouteNo() + "号线");
	    		cardCollectHead.setDeliverRouteNo(atmTaskCardTmp.getDeliverRouteNo()+ "号线");
	    		cardCollectHead.setDeliverBankName(bankCleanMap.get(atmTaskCardTmp.getDeliverBankId()));
	    		atomicInteger.getAndIncrement();
	    		return cardCollectHead;
	    	}).collect(Collectors.toList());
	  	}
	  
	  	String title = DateTimeUtil.getDateTimeDisplayString(queryDay) + " 由金融服务业务部带回的吞没卡汇总表";
  		try {
  		AtmTaskCardCollectHead.setExcelPropertyTitle(title);
			EasyExcelUtil.exportPrintExcel(response,title,AtmTaskCardCollectHead.class,cardCollectList,null);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(-1,"下载吞没卡回收汇总出错");
		}
	}
	
	@ApiOperation(value = "导出派送汇总")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "bankId", value = "所属机构", required = false, dataType = "Long"),
			@ApiImplicitParam(name = "queryDay", value = "查询日期", required = true, dataType = "Long"),
			@ApiImplicitParam(name = "departmentId", value = "部门id", required = true, dataType = "Long")
	})
	@ApiResponse(code = 200, message = "处理成功", response = Result.class)
	@GetMapping("/exportDeliver")
	public void exportDeliver(HttpServletResponse response,
						   @RequestParam(required=false) Long bankId,
						   @RequestParam Long queryDay,
						   @RequestParam Long departmentId) {
		List<AtmTaskCard> cardList = atmTaskCardService.listDeliverCardByDay(departmentId,bankId,queryDay);
		List<AtmTaskCardDeliverHead> cardDeliverList = new ArrayList<>();
		
		if(cardList != null && cardList.size() > 0){
			AtomicInteger atomicInteger = new AtomicInteger(1);
			
			//数据涉及atmId
			Set<Long> atmIds = new HashSet<>();
			//数据设计bankId
			Set<Long> bankIds = new HashSet<>();
			
			//提取Id
			cardList.stream().forEach(atmTaskCardTmp->{
				atmIds.add(atmTaskCardTmp.getAtmId());
				bankIds.add(atmTaskCardTmp.getDeliverBankId());
			});
		
			Map<Long,String> atmMap = new HashMap<>();
			if (atmIds.size() > 0) {
				List<AtmDevice> atmList = atmDeviceService.listByIds(atmIds);
				atmMap =atmList.stream().collect(Collectors.toMap(AtmDevice::getId, AtmDevice::getTerNo));
			}
			List<Bank> bankList = bankService.listByIds(bankIds);
			Map<Long,String> bankMap = bankList.stream().collect(Collectors.toMap(Bank::getId,Bank::getShortName));
			
			
			Map<Long, String> finalAtmMap = atmMap;
			Map<Long, String> finalBankMap = bankMap;
			LocalDate localDate = LocalDate.now().minusDays(1L);
			String today = LocalDateTime.ofInstant(Instant.ofEpochMilli(queryDay), ZoneId.systemDefault()).toLocalDate().format(DateTimeFormatter.ofPattern("MM月dd日"));
			cardDeliverList = cardList.stream().map(item -> {
				AtmTaskCardDeliverHead cardDeliverHead = new AtmTaskCardDeliverHead();
				BeanUtils.copyProperties(item, cardDeliverHead);
				Long collectTime = item.getCollectTime();
				boolean isYesterday = false;
				String acceptDate = "";
				if(collectTime != 0L) {
					LocalDate localDateT = LocalDateTime.ofInstant(Instant.ofEpochMilli(collectTime), ZoneOffset.of("+8")).toLocalDate();
					isYesterday = localDate.equals(localDateT);
					acceptDate = localDateT.format(DateTimeFormatter.ofPattern("MM月dd日"));
				}
				
				cardDeliverHead.setIndex(atomicInteger.get());
				cardDeliverHead.setCardNo(item.getCardNo());
				cardDeliverHead.setIsYesterday(isYesterday ? "是" : "否");
				cardDeliverHead.setAtmNo(Optional.ofNullable(finalAtmMap.get(item.getAtmId())).orElse(""));
				cardDeliverHead.setCardBankName(item.getCardBank());
				cardDeliverHead.setRouteNo(item.getRouteNo()+"号线");
				cardDeliverHead.setDeliveryRouteNo(item.getDeliverRouteNo()+"号线");
				cardDeliverHead.setDeliverBankName(Optional.ofNullable(finalBankMap.get(item.getDeliverBankId())).orElse(""));
				cardDeliverHead.setDate(acceptDate);
				cardDeliverHead.setMakeMan("");
				cardDeliverHead.setCheckMan("");
				cardDeliverHead.setDeliverDate(today);
				cardDeliverHead.setAcceptManA("");
				cardDeliverHead.setAcceptManB("");
				atomicInteger.getAndIncrement();
				return cardDeliverHead;
			}).collect(Collectors.toList());
		}
		
		String title = DateTimeUtil.getTimeYear(new Date(queryDay)) + "年 金融服务业务部 接收/移交吞没卡汇总表";
  		try {
  			AtmTaskCardDeliverHead.setExcelPropertyTitle(title);
			EasyExcelUtil.exportPrintExcel(response,title,AtmTaskCardDeliverHead.class,cardDeliverList,null);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(-1,"下载吞没卡派送汇总出错");
		}
	}
	
	@OperateLog(value = "添加吞没卡", type=OperateType.ADD)
    @ApiOperation(value = "添加吞没卡")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/save")
    public Result save(@RequestBody @Validated AtmTaskCardVO atmTaskCardVO){
        AtmTaskCard atmTaskCard = new AtmTaskCard();
        atmTaskCard.setRouteId(atmTaskCardVO.getRouteId());
        atmTaskCard.setRouteNo(atmTaskCardVO.getRouteNo());
        atmTaskCard.setTaskId(atmTaskCardVO.getTaskId());
        atmTaskCard.setCategory(atmTaskCardVO.getCategory());
        AtmTask atmTask = atmTaskService.getById(atmTaskCardVO.getTaskId());
        atmTaskCard.setAtmId(atmTask.getAtmId());
        atmTaskCard.setBankId(atmTask.getBankId());
        atmTaskCard.setDepartmentId(atmTask.getDepartmentId());
        atmTaskCard.setCardNo(atmTaskCardVO.getCardNo());
        atmTaskCard.setCardBank(atmTaskCardVO.getCardBank());
        AtmDevice atmDevice = atmDeviceService.getById(atmTask.getAtmId());
        if (null != atmDevice) {
            atmTaskCard.setDeliverBankId(atmDevice.getGulpBankId());
            Bank bank  = bankService.getById(atmDevice.getGulpBankId());
            if (null != bank) {
                if (!StringUtils.isEmpty(bank.getRouteNo())) {
                    atmTaskCard.setDeliverRouteNo(bank.getRouteNo());
                }
            }
        }
        atmTaskCard.setDeliverDay(DateTimeUtil.getNextdayString());
        atmTaskCard.setStatusT(BankCardStatusEnum.RETRIVE.getValue());
        
		boolean save = atmTaskCardService.save(atmTaskCard);
    	if(save)
    		return Result.success();
    	
    	return Result.fail("添加吞没卡失败");
        
    }
}
