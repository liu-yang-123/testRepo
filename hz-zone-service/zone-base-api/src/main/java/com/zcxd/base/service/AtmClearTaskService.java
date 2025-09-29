package com.zcxd.base.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import com.zcxd.base.vo.*;
import com.zcxd.common.constant.AtmTaskStatusEnum;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.base.dto.AtmClearBankDTO;
import com.zcxd.base.dto.AtmClearRouteDTO;
import com.zcxd.base.exception.BusinessException;
import com.zcxd.common.constant.AtmClearTaskStatusEnum;
import com.zcxd.common.constant.AtmTaskTypeEnum;
import com.zcxd.common.constant.Constant;
import com.zcxd.common.util.DateTimeUtil;
import com.zcxd.common.util.ExcelImportUtil;
import com.zcxd.common.util.Result;
import com.zcxd.db.mapper.AtmClearErrorMapper;
import com.zcxd.db.mapper.AtmClearTaskMapper;
import com.zcxd.db.mapper.AtmTaskMapper;
import com.zcxd.db.mapper.BankMapper;
import com.zcxd.db.mapper.RouteMapper;
import com.zcxd.db.model.AtmClearError;
import com.zcxd.db.model.AtmClearTask;
import com.zcxd.db.model.AtmClearTaskAudit;
import com.zcxd.db.model.AtmDevice;
import com.zcxd.db.model.AtmTask;
import com.zcxd.db.model.AtmTaskImportRecord;
import com.zcxd.db.model.Bank;
import com.zcxd.db.model.Route;

import javax.annotation.Resource;

/**
 * @author songanwei
 * @date 2021-06-07
 */
//@AllArgsConstructor
@Service
public class AtmClearTaskService extends ServiceImpl<AtmClearTaskMapper, AtmClearTask> {
	@Resource
    private ATMTaskService atmTaskService;
	@Resource
    private AtmTaskMapper atmTaskMapper;
	@Resource
    private BankService bankService;
	@Resource
    private RouteService routeService;
	@Resource
    private ATMService atmService;
	@Resource
    private RouteMapper routeMapper;
	@Resource
    private BankMapper bankMapper;
	@Resource
    private CommonService commonService;
	@Resource
    private ATMTaskImportRecordService atmTaskImportRecordService;
	@Resource
    private AtmClearErrorMapper clearErrorMapper;
    
    @Value("${importType.hzicbc}")
    private int hzicbc;
    @Value("${importType.rcb}")
    private int rcb;
    @Value("${importType.bob}")
    private int bob;
    @Value("${importType.jdicbc}")
    private int jdicbc;

    public Page<AtmClearTask> findListByPage(Integer page, Integer limit, AtmClearTask clearTask) {
        Page<AtmClearTask> ipage = new Page<>(page,limit);
        QueryWrapper queryWrapper = getQueryWrapper(clearTask);
        return baseMapper.selectPage(ipage, queryWrapper);
    }

    public Page<AtmClearTask> findListByPage(Integer page, Integer limit, Long bankId,String dayBegin,String dayEnd,Long atmId,Integer errType) {
        Page<AtmClearTask> ipage = new Page<>(page,limit);
        QueryWrapper<AtmClearTask> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("bank_id",bankId);
        if (null != dayBegin) {
            queryWrapper.ge("task_date",dayBegin);
        }
        if (null != dayEnd) {
            queryWrapper.le("task_date",dayEnd);
        }
        if (null != atmId) {
            queryWrapper.eq("atm_id",atmId);
        }
        if (null != errType) {
            queryWrapper.eq("error_type",errType);
        }
        queryWrapper.orderByDesc("task_date");
        return baseMapper.selectPage(ipage, queryWrapper);
    }

    public List<AtmClearTask> findListAll(Long bankId,String dayBegin,String dayEnd,Long atmId,Integer errType) {
        QueryWrapper<AtmClearTask> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("bank_id",bankId);
        if (null != dayBegin) {
            queryWrapper.ge("task_date",dayBegin);
        }
        if (null != dayEnd) {
            queryWrapper.le("task_date",dayEnd);
        }
        if (null != atmId) {
            queryWrapper.eq("atm_id",atmId);
        }
        if (null != errType) {
            queryWrapper.eq("error_type",errType);
        }
        queryWrapper.orderByAsc("create_time");
        return baseMapper.selectList(queryWrapper);
    }

    private QueryWrapper getQueryWrapper(AtmClearTask clearTask){
        QueryWrapper<AtmClearTask> queryWrapper = new QueryWrapper<>();
        if(clearTask != null){
            if( clearTask.getDepartmentId() != null && clearTask.getDepartmentId() > 0){
                queryWrapper.eq("department_id", clearTask.getDepartmentId());
            }
            if( clearTask.getBankId() != null && clearTask.getBankId() > 0){
                queryWrapper.eq("bank_id", clearTask.getBankId());
            }
            if( clearTask.getRouteId() != null && clearTask.getRouteId() > 0){
                queryWrapper.eq("route_id", clearTask.getRouteId());
            }
            if(!StringUtils.isEmpty(clearTask.getTaskDate())){
                queryWrapper.eq("task_date", clearTask.getTaskDate());
            }
			if (clearTask.getErrorType() != null && clearTask.getErrorType() > -1){
				queryWrapper.eq("error_type", clearTask.getErrorType()).eq("status_t",AtmClearTaskStatusEnum.FINISH.getValue());
			} else if (clearTask.getStatusT() != null && clearTask.getStatusT() > -1){
				queryWrapper.eq("status_t", clearTask.getStatusT());
			}
            if (clearTask.getClearMan()!= null && clearTask.getClearMan() > 0){
                queryWrapper.eq("clear_man", clearTask.getClearMan());
            }
			
        }
        queryWrapper.eq("deleted", 0);
        queryWrapper.orderBy(true,false,"id");
        return queryWrapper;
    }

    public boolean create(ClearTaskCreateVO createVO){
		//验证是否有重复atm数据
		Collection<Long> atmIds = createVO.getAtmList().stream().map(ClearAtmVO::getAtmId).collect(Collectors.toSet());
		if (atmIds.size() == 0){
			throw new BusinessException(1,"请添加ATM设备");
		}
		String taskDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		List<AtmDevice> atmDevices = atmService.listByIds(atmIds);
		Map<Long, Integer> atmMap = atmDevices.stream().collect(Collectors.toMap(AtmDevice::getId,AtmDevice::getDenom));

		//获取券别信息
		Map<String,Long> denomMap = commonService.getDenomMap();

		List<AtmClearTask> clearTaskList = createVO.getAtmList().stream().map(item -> {
			AtmClearTask clearTask = new AtmClearTask();
			clearTask.setBankId(item.getBankId());
			clearTask.setAtmId(item.getAtmId());
			clearTask.setDenomId(denomMap.get(atmMap.get(item.getAtmId())  + ""));
			clearTask.setDepartmentId(createVO.getDepartmentId());
			clearTask.setRouteId(createVO.getRouteId());
			clearTask.setPlanAmount(item.getPlanAmount());
			clearTask.setTaskDate(taskDate);
			clearTask.setStatusT(0);
			return clearTask;
		}).collect(Collectors.toList());

		//插入数据
		if (clearTaskList.size() > 0){
			Integer result = this.baseMapper.insertAll(clearTaskList);
			return result != null && result >= 1;
		}
		return false;
    }

    public List<AtmClearRouteDTO> getRoutes(Long departmentId, String taskDate, Long routeId){
        //查询routeId
        List<Map>  routeIdList = this.baseMapper.getRouteIds(taskDate, departmentId);
        List<Long> dd = routeIdList.stream().map(s -> (long)s.get("route_id")).collect(Collectors.toList());
        if (routeId != null){
            dd = dd.stream().filter(s -> s.equals(routeId)).collect(Collectors.toList());
        }
        if (dd.size() == 0){
            return new LinkedList<>();
        }
        //查询线路表
        List<Route>  routeList = routeMapper.selectBatchIds(dd);
        return routeList.stream().map(item -> {
            AtmClearRouteDTO  clearRouteDTO = new AtmClearRouteDTO();
            clearRouteDTO.setRouteId(item.getId());
            clearRouteDTO.setTaskDate(taskDate);
            clearRouteDTO.setRouteNo(item.getRouteNo());
            clearRouteDTO.setRouteName(item.getRouteName());
            clearRouteDTO.setRouteNo(item.getRouteNo());

            List<AtmClearTask> taskList = this.baseMapper.getByRoute(item.getId(), taskDate);
            int total = taskList.size();
            int doneTask = 0;
            BigDecimal planAmount = BigDecimal.ZERO;
            BigDecimal clearAmount = BigDecimal.ZERO;
            clearRouteDTO.setTotalTask(total);
            for (AtmClearTask clearTask : taskList){
                if (clearTask.getStatusT() == 1){
                    doneTask++;
                }
                planAmount = planAmount.add(clearTask.getPlanAmount());
                clearAmount = clearAmount.add(clearTask.getClearAmount());
            }
            clearRouteDTO.setRouteType(item.getRouteType());
            clearRouteDTO.setPlanAmount(planAmount);
            clearRouteDTO.setClearAmount(clearAmount);
            clearRouteDTO.setDoneTask(doneTask);
            return clearRouteDTO;
        }).collect(Collectors.toList());
    }

    /**
     * 查询当天线路的ATM ID列表
     * @param routeId
     * @return
     */
    public List getAtmList(Long routeId){
        long taskDate = LocalDateTime.of(LocalDate.now(), LocalTime.MIN).toInstant(ZoneOffset.of("+8")).toEpochMilli();
        QueryWrapper queryWrapper = Wrappers.query().eq("route_id",routeId).eq("task_date",taskDate)
        		.in("task_type", AtmTaskTypeEnum.CASHIN.getValue(),AtmTaskTypeEnum.CLEAN.getValue()).eq("deleted", 0);
        List<AtmTask> cleanList = atmTaskMapper.selectList(queryWrapper);
        return cleanList.stream().map(AtmTask::getAtmId).collect(Collectors.toList());
    }

    /**
     * 根据所在部门获取银行机构的清分任务数据信息
     * @param departmentId 部门ID
     * @param taskDate  任务日期 yyyy-MM-dd
     * @param bankId 银行机构ID
     * @return
     */
    public List<AtmClearBankDTO> getBanks(Long departmentId, String taskDate, Long bankId){
        //查询bankId
        QueryWrapper wrapper = Wrappers.query().eq("department_id",departmentId)
                .eq("parent_ids","/0/").eq("bank_type", 0)
                .eq("status_t",0).eq("deleted",0);
        List<Bank> bankList = bankMapper.selectList(wrapper);
        List<Long> bankIdList = bankList.stream().map(Bank::getId).collect(Collectors.toList());
        Map bankMap = bankList.stream().collect(Collectors.toMap(Bank::getId,Bank::getFullName));
        if (bankId != null){
            bankIdList = bankIdList.stream().filter(s -> s.equals(bankId)).collect(Collectors.toList());
        }
        if (bankIdList.size() == 0){
            return new LinkedList<>();
        }
        //查询线路表
        return bankIdList.stream().map(bId -> {
            AtmClearBankDTO  clearRouteDTO = new AtmClearBankDTO();
            clearRouteDTO.setTaskDate(taskDate);
            clearRouteDTO.setBankId(bId);
            clearRouteDTO.setBankName((String) Optional.ofNullable(bankMap.get(bId)).orElse(""));

            List<AtmClearTask> taskList = this.baseMapper.getByBank(bId, taskDate);
            int total = taskList.size();
            int doneTask = 0;
            BigDecimal planAmount = BigDecimal.ZERO;
            BigDecimal clearAmount = BigDecimal.ZERO;
            for (AtmClearTask clearTask : taskList){
                if (clearTask.getStatusT() == 1){
                    doneTask++;
                }
                planAmount = planAmount.add(clearTask.getPlanAmount());
                clearAmount = clearAmount.add(clearTask.getClearAmount());
            }
            clearRouteDTO.setTotalTask(total);
            clearRouteDTO.setPlanAmount(planAmount);
            clearRouteDTO.setClearAmount(clearAmount);
            clearRouteDTO.setDoneTask(doneTask);
            return clearRouteDTO;
        }).collect(Collectors.toList());
    }

    /**
     * 
     * @Title clearExcelImport
     * @Description 银行清分任务导入
     * @param file
     * @param bankType
     * @param taskDate
     * @return
     * @return 返回类型 Result
     */
    public Result clearExcelImport(MultipartFile file,Integer bankType, Long taskDate) {
//    	if(bankType != 2){
//	    	AtmTaskImportRecord atmTaskImportRecord = atmTaskImportRecordService.getOne((QueryWrapper)Wrappers.query().eq("task_date", taskDate)
//	    			.eq("bank_type", bankType).eq("import_type", 1).eq("deleted", 0));
//	    	if(atmTaskImportRecord != null){
//	    		return Result.fail("不允许重复导入");
//	    	}
//    	}
    	
    	ExcelImportClearVO excelImportClearVO = null;
//    	switch (bankType) {
//			case Constant.BOBID:
//				excelImportClearVO = new ExcelImportClearVO(2, 1, "北京银行", 0, 2, 0, 2, 3, 0);
//				break;
//			case Constant.HZICBCID:
//			case Constant.JDICBCID:
//				excelImportClearVO = new ExcelImportClearVO(2, 1, "工商银行", 6, 2, 0, 2, 3, 11);
//				break;
//			case Constant.RCBID:
//				excelImportClearVO = new ExcelImportClearVO(2, 0, "余杭农商行", 6, 2, 0, 2, 3, 0);
//				break;
//			default:
//				return Result.fail("银行类型参数有误");
//		}
    	
    	if(bankType.equals(bob)){
    		excelImportClearVO = new ExcelImportClearVO(2, 1, 0, 2, 0, 2, 3, 0);
    	}else if(bankType.equals(hzicbc) || bankType.equals(jdicbc)){
    		excelImportClearVO = new ExcelImportClearVO(2, 1, 6, 2, 0, 2, 3, 11);
    	}else if(bankType.equals(rcb)){
    		excelImportClearVO = new ExcelImportClearVO(2, 0, 6, 2, 0, 2, 3, 0);
    	}else{
    		return Result.fail("银行类型参数有误");
    	}
    	
    	//Sheet数量
    	int sheetNum = excelImportClearVO.getSheetNum();
    	//数据Sheet开始位置
    	int sheetStartNum = excelImportClearVO.getSheetStartNum();
    	//银行名称
//    	String bankName = excelImportClearVO.getBankName();
    	//线路编号位置
    	int routeNoCell = excelImportClearVO.getRouteNoCell();
    	//序号位置
    	int serialCell = excelImportClearVO.getSerialCell();
    	//设备编号位置
    	int terNoCell = excelImportClearVO.getTerNoCell();
    	//加款金额位置
    	int amountCell = excelImportClearVO.getAmountCell();
    	//加款金额位置
    	int commentsCell = excelImportClearVO.getCommentsCell();
    	
    	//获取券别信息
    	Map<String,Long> denomMap = commonService.getDenomMap();
    	
    	//获取atm编号集合
    	List<Map<String, Object>> atmTerNoList = atmService.listMaps((QueryWrapper)Wrappers.query().select("id,bank_id as bankId,denom,ter_no as terNo").eq("deleted", 0));
    	Map<Object, Map> atmTerNoMap = atmTerNoList.stream().collect(Collectors.toMap(map->map.get("terNo"),map->map));
    	
		//任务列表
		List<ATMTaskClearImportBatchVO> taskBatchs = new ArrayList<>();
    	try {
    		String extString = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        	Workbook wb = ExcelImportUtil.readExcel(file.getInputStream(),extString);

        	if(wb == null){
        		return Result.fail("读取excel文件失败");
        	}
	    	for(int x = sheetStartNum; x <= sheetNum-1; x++){
	    		Sheet sheet = wb.getSheetAt(x);
	        	int num = 0;
	        	
	        	//获取最大行数
	        	int rownum = sheet.getPhysicalNumberOfRows();
	        	String terNo = "";
	        	//提取各线路表格开始行数
	        	for (int i = 0; i < rownum; i++) {
	        		if(sheet.getRow(i) != null){
		        		String str = ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(serialCell)).toString();
		        		if(str.replace(" ", "").equals("序号")){
		        			num = i;
		        			break;
		        		}
		        		
	        			//提取第一个有效数据设备编号
/*	        			if(StringUtils.isBlank(terNo)){
	        				if(!StringUtils.isBlank(ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(terNoCell)).toString())
	        					&& !StringUtils.isBlank(ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(amountCell)).toString())){
		        				sheet.getRow(i).getCell(terNoCell).setCellType(CellType.STRING);
		        				terNo = ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(terNoCell)).toString();
	        				}
	        			}*/
	        		}
	        	}
	        	
	        	/*if(StringUtils.isBlank(terNo)){
	        		continue;
	        	}*/
	        	
	        	//判断导入模板与所选银行类型是否相同
	        	/*if(!atmTerNoMap.containsKey(terNo)){
	        		return Result.fail("第" + (num + 4) + "行设备【" +terNo + "】不存在");
	        	}*/
	        	
	        	
/*	        	if(!atmTerNoMap.get(terNo).get("bankId").toString().equals(bankType.toString())){
	        		Bank bank = bankService.getById(bankType);
	        		return Result.fail("所选excel不是" + bank.getShortName() + "回款明细表");
	        	}*/
	        	
	        	List<ATMTaskClearImportVO> taskVOs = new ArrayList<>();
	    		for (int i = num+3; i < rownum; i++) {
	    			//判断一个线路表格中是否还包含有数据
	    			if(sheet.getRow(i) == null || StringUtils.isBlank(ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(terNoCell)).toString())){
	    				continue;
	    			}
	    			
	    			//判断设备和金额是否都有数据
	    			if(!StringUtils.isBlank(ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(terNoCell)).toString()) && 
	    					!StringUtils.isBlank(ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(amountCell)).toString())){
	    				
	    				if(!ExcelImportUtil.checkNum(ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(amountCell)).toString())){
                    		return Result.fail("第" + ( i +1 ) + "行任务金额数据有误,不是数字或小数");
                    	}
	    				if((new BigDecimal(ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(amountCell)).toString())).compareTo(BigDecimal.ZERO)!=0){
		    				//清机任务信息
		        			sheet.getRow(i).getCell(terNoCell).setCellType(CellType.STRING);
		        			terNo = ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(terNoCell)).toString();
		                	if(!atmTerNoMap.containsKey(terNo)){
		                		return Result.fail("第" + ( i + 1 ) + "行设备【" + terNo + "】不存在");
		                	}
		                	
		                	if(!atmTerNoMap.get(terNo).get("bankId").toString().equals(bankType.toString())){
		    	        		Bank bank = bankService.getById(bankType);
		    	        		return Result.fail("所选excel不是" + bank.getShortName() + "回款明细表");
		    	        	}
		                	
		                	ATMTaskClearImportVO clearTask = new ATMTaskClearImportVO();
		                	clearTask.setPlanAmount((new BigDecimal(ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(amountCell)).toString())));
		                	clearTask.setAtmId((Long) atmTerNoMap.get(terNo).get("id"));
		                	clearTask.setTerNo(terNo);
		                	clearTask.setDenomId(denomMap.get(atmTerNoMap.get(terNo).get("denom").toString()));
		                    clearTask.setBankId((Long) atmTerNoMap.get(terNo).get("bankId"));
		                    if(bankType.equals(hzicbc) || bankType.equals(jdicbc)){
		                    	clearTask.setComments(ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(commentsCell)).toString());
		                    }
	//	                	clearTask.setClearAmount(BigDecimal.ZERO);
	//	                	clearTask.setTaskDate(DateTimeUtil.timeStampMs2Date(taskDate,"yyyy-MM-dd"));
	//	                    clearTask.setStatusT(AtmClearTaskStatusEnum.UNDO.getValue());
		                    if(bankType.equals(bob)){
			                    AtmTask atmTaskTemp = new AtmTask();
			            		atmTaskTemp.setAtmId((Long) atmTerNoMap.get(terNo).get("id"));
			            		atmTaskTemp.setTaskDate(taskDate);
			            		List<AtmTask> atmTaskByCondition = atmTaskService.getATMTaskByCondition(atmTaskTemp);
			            		if(atmTaskByCondition == null || atmTaskByCondition.size() == 0){
			            			return Result.fail("第" + ( i + 1 )+ "行" + DateTimeUtil.getDateTimeDisplayString(taskDate) + "设备【" + terNo + "】没有清机任务");
			            		}
			            		clearTask.setDepartmentId(atmTaskByCondition.get(0).getDepartmentId());
	//		            		if(atmTaskByCondition.size() == 1){
			            		clearTask.setRouteId(atmTaskByCondition.get(0).getRouteId());
	//		            		}
		                    }
		                    
		                    taskVOs.add(clearTask);
	    				}
	    			}
	
	    		}
	    		if(taskVOs.size() == 0){
	    			continue;
	    		}

	        	String routeNo = "";
	        	//判断线路是否存在
	        	if(!bankType.equals(bob)){
	        		sheet.getRow(num-1).getCell(routeNoCell).setCellType(CellType.STRING);
	        		routeNo = ExcelImportUtil.getCellFormatValue(sheet.getRow(num-1).getCell(routeNoCell)).toString();
		        	if(StringUtils.isBlank(routeNo) || routeNo.equals("0")){
		        		if(bankType.equals(rcb)){
		        			if(x == 0){
		        				routeNo = "60";
		        			}else{
		        				routeNo = "61";
		        			}
		        		}else{
		        			continue;
		        		}
		        	}
//		        	if(bankType == 2){
//				    	AtmTaskImportRecord atmTaskImportRecord = atmTaskImportRecordService.getOne((QueryWrapper)Wrappers.query().eq("task_date", taskDate)
//				    			.eq("bank_type", bankType).eq("import_type", 1).eq("route_no",routeNo).eq("deleted", 0));
//				    	if(atmTaskImportRecord != null){
//				    		return Result.fail("不允许重复导入");
//				    	}
//		        	}
		        	
		        	Route routeTmp = new Route();
		        	routeTmp.setRouteNo(routeNo);
		        	routeTmp.setRouteDate(taskDate);
		        	List<Route> routeByCondition = routeService.getRouteByCondition(routeTmp);
		    		if(routeByCondition.size() <= 0){
		        		return Result.fail(DateTimeUtil.getDateTimeDisplayString(taskDate) + "线路【" + routeNo + "】不存在,请确认数据是否正确");
		    		}
		    		Long routeId = routeByCondition.get(0).getId();
		        	Long departmentId = routeByCondition.get(0).getDepartmentId();
		    		
		        	for (ATMTaskClearImportVO atmClearTask : taskVOs) {
		        		atmClearTask.setRouteId(routeId);
		        		atmClearTask.setDepartmentId(departmentId);
					}
	        	}
	        	
	        	ATMTaskClearImportBatchVO atmTaskClearImportBatchVO = new ATMTaskClearImportBatchVO();
	        	atmTaskClearImportBatchVO.setRouteNo(routeNo);
	        	atmTaskClearImportBatchVO.setAtmTashClearList(taskVOs);
	        	taskBatchs.add(atmTaskClearImportBatchVO);
	    	}
		} catch (Exception e) {
			return Result.fail("文件解析出错，请检查所选文件是否正确");
		}
    	
    	if(taskBatchs.size() == 0){
    		return Result.fail("所选excel中没有有效数据");
    	}
    	
    	Result upload = commonService.upload(file);
    	ATMTaskClearImportSaveVO atmTaskClearImportSaveVO = new ATMTaskClearImportSaveVO();
    	atmTaskClearImportSaveVO.setSystemFileName(upload.getData().toString());
    	atmTaskClearImportSaveVO.setAtmTashClearList(taskBatchs);
    	
    	return Result.success(atmTaskClearImportSaveVO);
//        Integer result = this.baseMapper.insertAll(atmTashList);
//        if(result != null && result >= 1){
//        	return Result.success();
//        }
//        
//        return Result.fail("导入回款明细表失败");
	}
    
    /**
     * 
     * @Title saveImportClear
     * @Description 清分任务导入保存
     * @param atmTaskClearImportSaveVO
     * @return
     * @return 返回类型 Result
     */
	@Transactional(rollbackFor=Exception.class)
	public Result saveImportClear(ATMTaskClearImportSaveVO atmTaskClearImportSaveVO) {
//		QueryWrapper<AtmTaskImportRecord> queryWrapper = new QueryWrapper<>();
//		queryWrapper.eq("task_date", atmTaskClearImportSaveVO.getTaskDate())
//		.eq("bank_type", atmTaskClearImportSaveVO.getBankType()).eq("import_type", 1).eq("deleted", 0);
//    	if(atmTaskClearImportSaveVO.getBankType() == 2){
//    		queryWrapper.eq("route_no",atmTaskClearImportSaveVO.getAtmTashClearList().get(0).getRouteNo());
//    	}
//    	AtmTaskImportRecord atmTaskImportRecord = atmTaskImportRecordService.getOne(queryWrapper);
//    	
//    	if(atmTaskImportRecord != null){
//    		return Result.fail("不允许重复导入");
//    	}
//		
		
		String path = "";
//		switch (atmTaskClearImportSaveVO.getBankType()) {
//			case Constant.BOBID:
//				path = "BOB";
//				break;
//			case Constant.HZICBCID:
//				path = "ICBC";
//				break;
//			case Constant.RCBID:
//				path = "RCB";
//				break;
//			case Constant.JDICBCID:
//				path = "JDICBC";
//				break;
//			default:
//				return Result.fail("银行类型参数有误");
//		}
		
		if(atmTaskClearImportSaveVO.getBankType().equals(bob)){
			path = "BOB";
    	}else if(atmTaskClearImportSaveVO.getBankType().equals(hzicbc)){
    		path = "ICBC";
    	}else if(atmTaskClearImportSaveVO.getBankType().equals(rcb)){
    		path = "RCB";
    	}else if(atmTaskClearImportSaveVO.getBankType().equals(jdicbc)){
    		path = "JDICBC";
    	}else{
    		return Result.fail("银行类型参数有误");
    	}
		
		//文件移动处理  数据库增加文件列
		String systemFileName = commonService.moveFileToPath(atmTaskClearImportSaveVO.getSystemFileName(), path);
		
		AtmTaskImportRecord atmTaskImportRecord = new AtmTaskImportRecord();
		atmTaskImportRecord.setDepartmentId(atmTaskClearImportSaveVO.getDepartmentId());
		atmTaskImportRecord.setImportType(1);
		atmTaskImportRecord.setTaskDate(atmTaskClearImportSaveVO.getTaskDate());
		atmTaskImportRecord.setBankType(atmTaskClearImportSaveVO.getBankType());
		if(atmTaskClearImportSaveVO.getBankType().equals(hzicbc) || atmTaskClearImportSaveVO.getBankType().equals(jdicbc)){
			atmTaskImportRecord.setRouteNo(atmTaskClearImportSaveVO.getAtmTashClearList().get(0).getRouteNo());
		}
		atmTaskImportRecord.setFileName(atmTaskClearImportSaveVO.getFileName());
		atmTaskImportRecord.setSystemFileName(systemFileName);
		boolean atmTaskImportRecordSave = atmTaskImportRecordService.save(atmTaskImportRecord);
		if(!atmTaskImportRecordSave){
			throw new BusinessException(-1, "添加导入记录出错");
		}
		
		List<ATMTaskClearImportBatchVO> atmTashClearBatchVOs = atmTaskClearImportSaveVO.getAtmTashClearList();
		
		for (ATMTaskClearImportBatchVO atmTaskClearBatchVO : atmTashClearBatchVOs) {
			for (ATMTaskClearImportVO atmTaskClearVO : atmTaskClearBatchVO.getAtmTashClearList()) {
				AtmClearTask clearTask = new AtmClearTask();
            	clearTask.setPlanAmount(atmTaskClearVO.getPlanAmount());
            	clearTask.setClearAmount(BigDecimal.ZERO);
            	clearTask.setAtmId(atmTaskClearVO.getAtmId());
                clearTask.setBankId(atmTaskClearVO.getBankId());
                clearTask.setDenomId(atmTaskClearVO.getDenomId());
            	clearTask.setTaskDate(DateTimeUtil.timeStampMs2Date(atmTaskClearImportSaveVO.getTaskDate(),"yyyy-MM-dd"));
            	clearTask.setImportBatch(atmTaskImportRecord.getId());
                clearTask.setStatusT(AtmClearTaskStatusEnum.UNDO.getValue());
                clearTask.setRouteId(atmTaskClearVO.getRouteId());
                clearTask.setDepartmentId(atmTaskClearVO.getDepartmentId());
                clearTask.setComments(atmTaskClearVO.getComments());
				boolean save = this.save(clearTask);
				if(!save){
					throw new BusinessException(-1, "清分任务添加出错");
				}
			}
		}
		
		return Result.success();
		
	}


    /**
     * 清机任务明细
     * @param taskId
     * @return
     */
	public ClearTaskVO getDetail(Long taskId){
        ClearTaskVO taskVO = new ClearTaskVO();
        AtmClearTask clearTask = baseMapper.selectById(taskId);
        if (clearTask == null){
            return null;
        }
        taskVO.setId(taskId);
        taskVO.setAtmId(clearTask.getAtmId());
        taskVO.setPlanAmount(clearTask.getPlanAmount());
        taskVO.setClearAmount(clearTask.getClearAmount());

        //查询差错明细
        QueryWrapper wrapper = Wrappers.query().eq("task_id",taskId).eq("deleted",0);
        List<AtmClearError>  errorList = clearErrorMapper.selectList(wrapper);
        List<AtmClearErrorVO> errorVOList = errorList.stream().map(r -> {
            AtmClearErrorVO errorVO = new AtmClearErrorVO();
            BeanUtils.copyProperties(r, errorVO);
            return errorVO;
        }).collect(Collectors.toList());
        taskVO.setErrorList(errorVOList);

        return taskVO;
    }


    /**
     * 审核数据同步
     * @param audit
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateAudit(AtmClearTaskAudit audit){
        Long taskId = audit.getClearTaskId();
        AtmClearTask clearTask = baseMapper.selectById(taskId);
        clearTask.setPlanAmount(audit.getPlanAmount());
        clearTask.setClearAmount(audit.getClearAmount());
        clearTask.setErrorType(audit.getErrorType());
        clearTask.setErrorAmount(audit.getErrorAmount());
        baseMapper.updateById(clearTask);

        String errorListT = audit.getErrorList();
        List<AtmClearErrorVO> errorVOList = JSON.parseArray(errorListT,AtmClearErrorVO.class);

        List<AtmClearError> errorList = errorVOList.stream().map(t -> {
            AtmClearError clearError = new AtmClearError();
            BeanUtils.copyProperties(t, clearError);
            clearError.setTaskId(taskId);
            clearError.setAtmId(clearTask.getAtmId());
            clearError.setCreateTime(System.currentTimeMillis());
            return clearError;
        }).collect(Collectors.toList());

        //删除
        AtmClearError clearError = new AtmClearError();
        clearError.setTaskId(audit.getClearTaskId());
        clearError.setDeleted(1);
        QueryWrapper wrapper = Wrappers.query().eq("task_id",taskId).eq("deleted",0);
        clearErrorMapper.update(clearError, wrapper);
        //批量插入
        if (errorList.size() > 0){
            clearErrorMapper.insertAll(errorList);
        }
        return true;
    }

	/**
	 * 修改数据同步
	 * @param clearTaskVO
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public boolean update(ClearTaskVO clearTaskVO){
		Long taskId = clearTaskVO.getId();
		AtmClearTask clearTask = baseMapper.selectById(taskId);

		clearTask.setPlanAmount(clearTaskVO.getPlanAmount());
		clearTask.setClearAmount(clearTaskVO.getClearAmount());
		//计算差错金额 差错类型
		BigDecimal errorAmount = clearTaskVO.getClearAmount().subtract(clearTaskVO.getPlanAmount());
		clearTask.setErrorAmount(errorAmount);
		Integer errorTypeValue = errorAmount.compareTo(BigDecimal.ZERO);
		// 差错类型 0=平账  1=长款 2=短款
		Integer errorType = errorTypeValue == 0 ? 0 : ( errorTypeValue > 0 ? 1 : 2);
		clearTask.setErrorType(errorType);

		baseMapper.updateById(clearTask);

		List<AtmClearErrorVO> errorVOList = clearTaskVO.getErrorList();

		List<AtmClearError> errorList = errorVOList.stream().map(t -> {
			AtmClearError clearError = new AtmClearError();
			BeanUtils.copyProperties(t, clearError);
			clearError.setTaskId(taskId);
			clearError.setAtmId(clearTask.getAtmId());
			clearError.setCreateTime(System.currentTimeMillis());
			return clearError;
		}).collect(Collectors.toList());

		//删除
		AtmClearError clearError = new AtmClearError();
		clearError.setTaskId(taskId);
		clearError.setDeleted(1);
		QueryWrapper wrapper = Wrappers.query().eq("task_id",taskId).eq("deleted",0);
		clearErrorMapper.update(clearError, wrapper);
		//批量插入
		if (errorList.size() > 0){
			clearErrorMapper.insertAll(errorList);
		}
		return true;
	}

	/**
	 * 手工完成清分任务
	 * @param clearTaskVO
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public boolean finish(ClearTaskFinishVO clearTaskVO){
		Long taskId = clearTaskVO.getId();
		AtmClearTask oldClearTask = baseMapper.selectById(taskId);
		if (oldClearTask.getStatusT() != AtmClearTaskStatusEnum.UNDO.getValue()) {
			return false;
		}
		AtmClearTask editClearTask = new AtmClearTask();

		editClearTask.setId(taskId);
		editClearTask.setClearAmount(clearTaskVO.getClearAmount());
		//计算差错金额 差错类型
		BigDecimal errorAmount = clearTaskVO.getClearAmount().subtract(oldClearTask.getPlanAmount());
		editClearTask.setErrorAmount(errorAmount);
		Integer errorTypeValue = errorAmount.compareTo(BigDecimal.ZERO);
		// 差错类型 0=平账  1=长款 2=短款
		Integer errorType = errorTypeValue == 0 ? 0 : ( errorTypeValue > 0 ? 1 : 2);
		editClearTask.setErrorType(errorType);
		editClearTask.setClearMan(clearTaskVO.getClearMan());
		editClearTask.setCheckMan(clearTaskVO.getCheckMan());
		editClearTask.setClearTime(System.currentTimeMillis());
		editClearTask.setErrorConfirmMan(clearTaskVO.getErrorConfirmMan());
		editClearTask.setStatusT(AtmClearTaskStatusEnum.FINISH.getValue());
		baseMapper.updateById(editClearTask);
		List<AtmClearErrorVO> errorVOList = clearTaskVO.getErrorList();

		List<AtmClearError> errorList = errorVOList.stream().map(t -> {
			AtmClearError clearError = new AtmClearError();
			BeanUtils.copyProperties(t, clearError);
			clearError.setTaskId(taskId);
			clearError.setAtmId(oldClearTask.getAtmId());
			clearError.setCreateTime(System.currentTimeMillis());
			return clearError;
		}).collect(Collectors.toList());

		//批量插入
		if (errorList.size() > 0){
			clearErrorMapper.insertAll(errorList);
		}
		return true;
	}

}
