package com.zcxd.base.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.base.exception.BusinessException;
import com.zcxd.base.vo.ExcelImportClearVO;
import com.zcxd.common.constant.ClearErrorDetailTypeEnum;
import com.zcxd.common.constant.Constant;
import com.zcxd.common.util.DateTimeUtil;
import com.zcxd.common.util.ExcelImportUtil;
import com.zcxd.common.util.Result;
import com.zcxd.db.mapper.AtmClearErrorMapper;
import com.zcxd.db.mapper.AtmTaskImportRecordMapper;
import com.zcxd.db.model.AtmClearError;
import com.zcxd.db.model.AtmClearTask;
import com.zcxd.db.model.AtmDevice;
import com.zcxd.db.model.AtmTask;
import com.zcxd.db.model.AtmTaskImportRecord;
import com.zcxd.db.model.Denom;

/**
 * 
 * @ClassName ATMTaskImportRecordService
 * @Description 任务导入记录
 * @author 秦江南
 * @Date 2021年8月4日下午4:37:59
 */
@Service
public class ATMTaskImportRecordService extends ServiceImpl<AtmTaskImportRecordMapper, AtmTaskImportRecord>{
	
    @Autowired
    private AtmClearTaskService clearTaskService;
	
	@Autowired
	private ATMTaskService atmTaskService;
	
    @Autowired
    private ATMService atmService;
    
    @Resource
    private AtmClearErrorMapper clearErrorMapper;
    @Autowired
    private DenomService denomService;
    
    @Autowired
    private CommonService commonService;
    
    @Value("${importType.hzicbc}")
    private int hzicbc;
    @Value("${importType.rcb}")
    private int rcb;
    @Value("${importType.bob}")
    private int bob;
    @Value("${importType.jdicbc}")
    private int jdicbc;
    
	/**
	 * 
	 * @Title findListByPage
	 * @Description 分页查询导入记录
	 * @param page
	 * @param limit
	 * @param atmTaskImportRecord
	 * @return
	 * @return 返回类型 IPage<AtmTaskImportRecord>
	 */
	public IPage<AtmTaskImportRecord> findListByPage(Integer page, Integer limit,
			AtmTaskImportRecord atmTaskImportRecord) {
		Page<AtmTaskImportRecord> ipage=new Page<AtmTaskImportRecord>(page,limit);
    	QueryWrapper<AtmTaskImportRecord> queryWrapper=new QueryWrapper<AtmTaskImportRecord>();
    	if(atmTaskImportRecord != null){
	    	if(atmTaskImportRecord.getImportType() != null){
	    		queryWrapper.like("import_type", atmTaskImportRecord.getImportType());
	    	}
	    	
	    	if(atmTaskImportRecord.getTaskDate() != null){
	    		queryWrapper.eq("task_date", atmTaskImportRecord.getTaskDate());
	    	}
	    	
	    	if(atmTaskImportRecord.getBankType() != null){
	    		queryWrapper.eq("bank_type", atmTaskImportRecord.getBankType());
	    	}
	    	
    	}
    	queryWrapper.eq("department_id", atmTaskImportRecord.getDepartmentId());
    	queryWrapper.orderBy(true, false, "create_time");
        return baseMapper.selectPage(ipage, queryWrapper);
	}

	/**
	 * 
	 * @Title deleteRecord
	 * @Description 删除导入清机记录
	 * @param id
	 * @return
	 * @return 返回类型 Result
	 */
	@Transactional(rollbackFor=Exception.class)
	public Result deleteCleanRecord(Long id) {
		UpdateWrapper<AtmTask> updateWrapper = new UpdateWrapper<>();
		updateWrapper.eq("import_batch",id).set("deleted", 1);
		boolean atmTaskUpdate = atmTaskService.update(null, updateWrapper);

		if(!atmTaskUpdate){
			throw new BusinessException(-1,"删除ATM任务失败");
		}
		
		AtmTaskImportRecord atmTaskImportRecord = new AtmTaskImportRecord();
		atmTaskImportRecord.setId(id);
		atmTaskImportRecord.setDeleted(1);
		
		boolean recordUpdate = this.updateById(atmTaskImportRecord);
		if(!recordUpdate){
			throw new BusinessException(-1,"删除导入记录失败");
		}
    	
		return Result.success();
	}
	
	/**
	 * 
	 * @Title deleteClearRecord
	 * @Description 删除导入清分记录
	 * @param id
	 * @param string 
	 * @return
	 * @return 返回类型 Result
	 */
	@Transactional(rollbackFor=Exception.class)
	public Result deleteClearRecord(Long id, String systemFileName) {
		
		UpdateWrapper<AtmClearTask> updateWrapper = new UpdateWrapper<>();
		updateWrapper.eq("import_batch",id).set("deleted", 1);
		boolean clearTaskUpdate = clearTaskService.update(null, updateWrapper);

		if(!clearTaskUpdate){
			throw new BusinessException(-1,"删除清分任务失败");
		}
		
		AtmTaskImportRecord atmTaskImportRecord = new AtmTaskImportRecord();
		atmTaskImportRecord.setId(id);
		atmTaskImportRecord.setDeleted(1);
		
		boolean recordUpdate = this.updateById(atmTaskImportRecord);
		if(!recordUpdate){
			throw new BusinessException(-1,"删除导入记录失败");
		}
		
		File file = new File(systemFileName);
		if(!file.delete()){
			return Result.fail("文件删除失败");
		}
    	
		return Result.success();
	}
	
	/**
	 * 
	 * @Title exportClearTask
	 * @Description 导出清分任务
	 * @param response
	 * @param id
	 * @return
	 * @return 返回类型 Result
	 */
    public void exportClearTask(HttpServletResponse response, Long id) {
    	AtmTaskImportRecord atmTaskImportRecord = this.getById(id);
    	if(atmTaskImportRecord.getImportType() != 1 || atmTaskImportRecord.getDeleted() != 0){
    		throw new BusinessException(-1,"请选择未删除的回款明细记录");
    	}

    	ExcelImportClearVO excelImportClearVO = null;
//    	switch (atmTaskImportRecord.getBankType()) {
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
//				throw new BusinessException(-1,"银行类型参数有误");
//		}
    	
    	if(atmTaskImportRecord.getBankType().equals(bob)){
    		excelImportClearVO = new ExcelImportClearVO(2, 1, 0, 2, 0, 2, 3, 0);
    	}else if(atmTaskImportRecord.getBankType().equals(hzicbc) || atmTaskImportRecord.getBankType().equals(jdicbc)){
    		excelImportClearVO = new ExcelImportClearVO(2, 1, 6, 2, 0, 2, 3, 11);
    	}else if(atmTaskImportRecord.getBankType().equals(rcb)){
    		excelImportClearVO = new ExcelImportClearVO(2, 0, 6, 2, 0, 2, 3, 0);
    	}else{
    		throw new BusinessException(-1,"银行类型参数有误");
    	}
    	
    	//Sheet数量
    	int sheetNum = excelImportClearVO.getSheetNum();
    	//数据Sheet开始位置
    	int sheetStartNum = excelImportClearVO.getSheetStartNum();
    	//序号位置
    	int serialCell = excelImportClearVO.getSerialCell();
    	//设备编号位置
    	int terNoCell = excelImportClearVO.getTerNoCell();
    	//加款金额位置
    	int amountCell = excelImportClearVO.getAmountCell();
    	//实际清点金额位置
    	int realityAmountCell = 6;
    	//差额位置
    	int differenceCell = 7;
    	//差错类型位置
    	int typeCell = 8;
    	//差错原因
    	int errorReasonCell = 9;
    	//备注位置	
    	int commonCell = 11;
    	
    	Workbook wb = null;
    	
    	File file = new File(atmTaskImportRecord.getSystemFileName());
    	String extString = file.getName().substring(file.getName().lastIndexOf("."));
    	try {
    		wb = ExcelImportUtil.readExcel(new FileInputStream(file) ,extString);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(-1,"读取excel文件失败");
		}
    	
    	if(wb == null){
    		throw new BusinessException(-1,"读取excel文件失败");
    	}
    	
    	//获取券别信息
    	Map<String,Long> denomMap = commonService.getDenomMap();
    	
    	//得到当天清分任务
		List<AtmClearTask> clearTaskList = clearTaskService.list((QueryWrapper)Wrappers.query()
				.eq("task_date", DateTimeUtil.timeStampMs2Date(atmTaskImportRecord.getTaskDate(), "yyyy-MM-dd")).eq("deleted", 0));
    	
    	for(int x = sheetStartNum; x <= sheetNum-1; x++){
    		Sheet sheet = wb.getSheetAt(x);
        	int num = 0;
        	
        	//获取最大行数
        	int rownum = sheet.getPhysicalNumberOfRows();
        	
        	for (int i = 0; i < rownum; i++) {
        		if(sheet.getRow(i) != null){
	        		String str = ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(serialCell)).toString();
	        		if(str.replace(" ", "").equals("序号")){
	        			num = i;
	        			break;
	        		}
        		}
        	}
        	
        	double realityAmount = 0;
        	double differenceAmount = 0;
        	
    		for (int i = num+3; i< rownum; i++) {
    			if(sheet.getRow(i) != null && ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(0)).toString().replace(" ", "").equals("合计")){
    				if(realityAmount != 0){
    					 sheet.getRow(i).getCell(realityAmountCell).setCellValue(realityAmount);
    				}
    				
    				if(differenceAmount != 0){
    					sheet.getRow(i).getCell(differenceCell).setCellValue(differenceAmount);
    				}
    				
    			}
    			
    			//判断一个线路表格中是否还包含有数据
    			if(sheet.getRow(i) == null || StringUtils.isBlank(ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(terNoCell)).toString())){
    				continue;
    			}
    			
    			//判断设备和金额是否都有数据
    			if(!StringUtils.isBlank(ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(terNoCell)).toString()) && 
    					!StringUtils.isBlank(ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(amountCell)).toString())){
    				//得到设备编号对应的设备信息
        			sheet.getRow(i).getCell(terNoCell).setCellType(CellType.STRING);
        			String terNo = ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(terNoCell)).toString();
        			AtmDevice atmDevice = atmService.getOne((QueryWrapper)Wrappers.query().eq("ter_no", terNo).eq("deleted", 0));
        			BigDecimal planAmount = new BigDecimal(ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(amountCell)).toString());
        			
        			//得到清分记录
        			if(clearTaskList != null && clearTaskList.size() > 0){
        				AtmClearTask atmClearTask = null;
        				for (int j = 0; j < clearTaskList.size(); j++) {
        					AtmClearTask clearTask = clearTaskList.get(j);
        					
        					if(clearTask.getAtmId().equals(atmDevice.getId()) && 
        							clearTask.getPlanAmount().compareTo(planAmount) == 0){
        						atmClearTask = clearTask;
        						clearTaskList.remove(j);
        						break;
        					}
        					
						}
        				
        				if(atmClearTask == null){
        					continue;
        				}
        				
        				//填写清分结果内容
    					
        				//填写实际清分金额
    					sheet.getRow(i).getCell(realityAmountCell).setCellValue(atmClearTask.getClearAmount().doubleValue());
    					
    					realityAmount += atmClearTask.getClearAmount().doubleValue();
    					
    					//填写长短款
    					if(atmClearTask.getErrorType() != 0){
    						String errorType = "";
    						switch (atmClearTask.getErrorType()) {
							case 1:
								errorType = "长款";
								break;
							case 2:
								errorType = "短款";
								break;
							}
        					sheet.getRow(i).getCell(differenceCell).setCellValue(atmClearTask.getErrorAmount().doubleValue());
        					differenceAmount += atmClearTask.getErrorAmount().doubleValue();
        					
        					sheet.getRow(i).getCell(typeCell).setCellValue(errorType);
        					sheet.getRow(i).getCell(commonCell).setCellValue(DateTimeUtil.timeStampMs2Date(atmClearTask.getClearTime(), null).substring(11, 16) + 
        							(StringUtils.isBlank(atmClearTask.getComments()) ? "" : "  " + atmClearTask.getComments()) +
        							(denomMap.get("10").equals(atmClearTask.getDenomId()) ? "  10*" + atmClearTask.getClearAmount().longValue()/10 : "" ) );
    					}
    					
    					//填写差错原因
    					List<AtmClearError> clearErrorList = clearErrorMapper.selectList((QueryWrapper)Wrappers.query().eq("task_id", atmClearTask.getId()).eq("deleted", 0));
    					if(clearErrorList != null && clearErrorList.size() > 0){
    						
    						String errorReason = "";
    						for (AtmClearError clearError : clearErrorList) {
    						
    							String reason = "";
    							if(clearError.getDetailType() == ClearErrorDetailTypeEnum.FAKE.getValue()){
    								reason = "假币";
								}else if(clearError.getDetailType() == ClearErrorDetailTypeEnum.BAD.getValue()){
									reason = "残币";
								}else if(clearError.getDetailType() == ClearErrorDetailTypeEnum.CARRY.getValue()){
									reason = "夹张";
								}
    							Denom denom = denomService.getById(clearError.getDenomId());
    							errorReason += reason + clearError.getCount() + "张" + "，券别：" + denom.getName() + "，共"  
    							+ clearError.getAmount() + "元" + (clearError.getDetailType() != ClearErrorDetailTypeEnum.FAKE.getValue() ? "" : ("，冠字号：" + clearError.getRmbSn())) + "\r\n";
    						}
    						sheet.getRow(i).getCell(errorReasonCell).setCellValue(new HSSFRichTextString(errorReason.substring(0,errorReason.length()-2)));;
    					}
        			}
        			
    			}
    		}
        	
    	}
    	
        OutputStream output= null;
        try {
            output = response.getOutputStream();
	        response.reset();
	        //设置响应头，
	        response.setHeader("Content-disposition", "attachment; filename=clearTask.xls");
	        response.setContentType("application/msexcel");
	        wb.write(output);
	        output.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException(-1,"下载文件失败");
        }
    }
    
}
