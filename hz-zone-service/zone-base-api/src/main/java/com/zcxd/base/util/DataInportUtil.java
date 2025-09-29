package com.zcxd.base.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

import com.zcxd.BaseApplication;
import com.zcxd.base.service.EmployeeService;
import com.zcxd.base.service.VehicleService;
import com.zcxd.common.util.CharUtil;
import com.zcxd.common.util.DateTimeUtil;
import com.zcxd.common.util.ExcelImportUtil;
import com.zcxd.common.util.SecurityUtils;
import com.zcxd.db.model.Employee;
import com.zcxd.db.model.Vehicle;

public class DataInportUtil {
//    public static void main(String[] args) throws FileNotFoundException {
//    	
//    	File file = new File("D://1.xlsx");
//    	String extString = file.getName().substring(file.getName().lastIndexOf("."));
//    	Workbook wb = ExcelUtil.readExcel(new FileInputStream(file) ,extString);
//    	
//		Sheet sheet = wb.getSheetAt(0);
//		//获取最大行数
//		int rownum = sheet.getPhysicalNumberOfRows();
//    	SpringApplication.run(BaseApplication.class, args);
//        ApplicationContext context = SpringBeanUtils.getApplicationContext();
//        BankTempService bankTempService = context.getBean(BankTempService.class);
//		//提取数据
//	    for (int i = 1; i < rownum; i++) {
//	    	BankTemp bankTemp = new BankTemp();
//	    	bankTemp.setId(Long.parseLong(ExcelUtil.getCellFormatValue(sheet.getRow(i).getCell(0)).toString()));
//	    	bankTemp.setBankName(ExcelUtil.getCellFormatValue(sheet.getRow(i).getCell(1)).toString());
//	    	bankTemp.setTerFactory(ExcelUtil.getCellFormatValue(sheet.getRow(i).getCell(2)).toString());
//	    	bankTemp.setTerType(ExcelUtil.getCellFormatValue(sheet.getRow(i).getCell(3)).toString());
//	    	bankTemp.setWorkInfo(ExcelUtil.getCellFormatValue(sheet.getRow(i).getCell(4)).toString());
//	    	bankTemp.setGulpInfo(ExcelUtil.getCellFormatValue(sheet.getRow(i).getCell(5)).toString());
//	    	bankTemp.setInstallInfo(ExcelUtil.getCellFormatValue(sheet.getRow(i).getCell(6)).toString());
//	    	bankTemp.setTerNo(ExcelUtil.getCellFormatValue(sheet.getRow(i).getCell(7)).toString());
//	    	bankTemp.setRouteNo(ExcelUtil.getCellFormatValue(sheet.getRow(i).getCell(8)).toString());
//	    	
//	    	bankTempService.save(bankTemp);
//		}
//    }
	
    public static void main(String[] args) throws FileNotFoundException, ParseException {
    	
    	File file = new File("D://1.xlsx");
    	String extString = file.getName().substring(file.getName().lastIndexOf("."));
    	Workbook wb = ExcelImportUtil.readExcel(new FileInputStream(file) ,extString);
    	
		Sheet sheet = wb.getSheetAt(0);
		//获取最大行数
		int rownum = sheet.getPhysicalNumberOfRows();
    	SpringApplication.run(BaseApplication.class, args);
        ApplicationContext context = SpringBeanUtils.getApplicationContext();
        EmployeeService employeeService = context.getBean(EmployeeService.class);
		//提取数据
	    for (int i = 1; i < rownum; i++) {
	    	Employee employee = new Employee();
	    	sheet.getRow(i).getCell(1).setCellType(CellType.STRING);
	    	employee.setEmpNo(ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(1)).toString());
	    	employee.setEmpName(ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(6)).toString());
	    	sheet.getRow(i).getCell(2).setCellType(CellType.STRING);
	    	employee.setServiceCertificate(ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(2)).toString());
	    	String a = ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(3)).toString();
	    	String as = "";
	    	switch (a) {
				case "派遣":
					as = "MQ001";
					break;
				case "企业":
					as = "MQ002";
					break;
				case "外包":
					as = "MQ003";
					break;
				case "退休":
					as = "MQ004";
					break;
			}
	    	
	    	employee.setManningQuotas(as);
	    	
	    	String b = ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(4)).toString();
	    	String bs = "";
	    	switch (b) {
				case "新世纪":
					bs = "AC001";
					break;
				case "中钞":
					bs = "AC002";
					break;
				case "融和":
					bs = "AC003";
					break;
				case "银雁":
					bs = "AC004";
					break;
				case "上保":
					bs = "AC005";
					break;
				case "市保":
					bs = "AC006";
					break;
				case "返聘":
					bs = "AC007";
					break;
				case "明邦":
					bs = "AC008";
					break;
			}
	    	
	    	
	    	employee.setAffiliatedCompany(bs);
	    	employee.setDepartmentId(10l);
	    	
	    	String c = ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(5)).toString();
	    	int jobId = 0;
	    	int jobType = 0;
	    	switch (c) {
			case "调度":
				jobId = 11;
				jobType = 0;
				break;
			case "清机":
				jobId = 7;
				jobType = 3;
				break;
			case "库管":
				jobId = 9;
				jobType = 6;
				break;
			case "清点":
				jobId = 1;
				jobType = 5;
				break;
			case "司机":
				jobId = 2;
				jobType = 1;
				break;
			case "护卫":
				jobId = 6;
				jobType = 2;
				break;
	    	}
	    	
	    	
	    	employee.setJobIds((long) jobId);
	    	employee.setJobType(jobType);
	    	
	    	employee.setTitle(0);
	    	String d =ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(9)).toString();
	    	
			employee.setIdno(SecurityUtils.encryptAES(d));
	    	employee.setSex(Integer.parseInt(d.substring(16,17))%2 == 0 ? 1 : 0);
			String birthDay = CharUtil.getBirthday(d);
			employee.setBirthday(DateTimeUtil.date2TimeStampMs(birthDay+" 00:00:00",null));
	    	employee.setNation("汉族");
	    	employee.setMarriage(0);
	    	
	    	String e = ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(13)).toString();
	    	String es = "";
	    	switch (e) {
			case "高中":
				es = "3";
				break;
			case "本科":
				es = "0";
				break;
			case "大专":
				es = "1";
				break;
			case "中专":
				es = "2";
				break;
			case "初中":
				es = "4";
				break;
	    	}
	    	
	    	employee.setEducation(es);
	    	
	    	String f = ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(14)).toString();
	    	if(f == null)
	    		f="";
	    	employee.setPolitic(f.equals("是")?"1":"0");
	    	String g = ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(15)).toString();
	    	if(g == null)
	    		g="";
	    	employee.setMilitary(g.equals("是")?1:0);
	    	
	    	employee.setAddress(ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(12)).toString());
	    	employee.setStatusT(0);
	    	String h =ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(7)).toString();
	    	SimpleDateFormat sdf = new SimpleDateFormat( "yyyy.MM.dd"); 
	    	employee.setEntryDate(h == "" ? 0 :sdf.parse(h).getTime());
	    	String k =ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(8)).toString();
	    	employee.setExpirationDate(k == "" ? 0 :sdf.parse(k).getTime());
	    	sheet.getRow(i).getCell(11).setCellType(CellType.STRING);
	    	employee.setMobile(SecurityUtils.encryptAES(ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(11)).toString()));
	    	sheet.getRow(i).getCell(17).setCellType(CellType.STRING);
	    	employee.setContactMobile(SecurityUtils.encryptAES(ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(17)).toString()));
	    	employee.setContactName(ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(16)).toString());
	    	employeeService.save(employee);
		}
    }
    
	public static void mains(String[] args) throws FileNotFoundException, ParseException {
    	
    	File file = new File("D://3.xlsx");
    	String extString = file.getName().substring(file.getName().lastIndexOf("."));
    	Workbook wb = ExcelImportUtil.readExcel(new FileInputStream(file) ,extString);
    	
		Sheet sheet = wb.getSheetAt(1);
		//获取最大行数
		int rownum = sheet.getPhysicalNumberOfRows();
    	SpringApplication.run(BaseApplication.class, args);
        ApplicationContext context = SpringBeanUtils.getApplicationContext();
        VehicleService vehicleService = context.getBean(VehicleService.class);
		//提取数据
	    for (int i = 1; i < rownum; i++) {
	    	Vehicle vehicleTemp = new Vehicle();
	    	sheet.getRow(i).getCell(1).setCellType(CellType.STRING);
	    	vehicleTemp.setLpno(ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(2)).toString());
	    	vehicleTemp.setSeqno(ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(1)).toString());
	    	vehicleTemp.setDepartmentId(13l);
	    	String a =sheet.getRow(i).getCell(3).getStringCellValue();
	    	String as = "";
	    	switch (a) {
	    	case "轻型非载货专项作业车":
	    		as= "VT001";
	    		break;
	    	case "轻型封闭货车":
	    		as= "VT002";
	    		break;
	    	case "轻型特殊结构货车":
	    		as= "VT003";
	    		break;
			case "轻型专项作业车":
				as= "VT004";
				break;
			case "小型专项作业车":
				as= "VT005";
				break;
			case "小型专业客车":
				as= "VT006";
				break;
			}
	    	vehicleTemp.setVehicleType(as);
	    	
	    	String b = sheet.getRow(i).getCell(4).getStringCellValue();
	    	String bs = "";
	    	switch (b) {
	    	case "CSZ5040XYCB2":
	    		bs = "VM001";
	    		break;
	    	case "CSZ5040XYCFB3":
	    		bs = "VM002";
	    		break;
	    	case "DC7162LYCM":
	    		bs = "VM003";
	    		break;
	    	case "JX5047XYCMA":
	    		bs = "VM004";
	    		break;
	    	case "SLT5043XYCE1L":
	    		bs = "VM005";
	    		break;
	    	case "TBL5042XYC-AMT5":
	    		bs = "VM006";
	    		break;
	    	case "TBL5049XYCF1":
	    		bs = "VM007";
	    		break;
	    	}
	    	vehicleTemp.setModel(bs);
	    	vehicleTemp.setFrameNumber(sheet.getRow(i).getCell(5).getStringCellValue());
	    	vehicleTemp.setEngineNumber(sheet.getRow(i).getCell(6).getStringCellValue());
	    	
	    	String c = sheet.getRow(i).getCell(7).getStringCellValue();
	    	String cs = "";
	    	switch (c) {
	    	case "国三":
	    		cs = "ES001";
	    		break;
	    	case "国四":
	    		cs = "ES002";
	    		break;
	    	case "国六":
	    		cs = "ES003";
	    		break;
	    	}
	    	vehicleTemp.setEmissionStandard(cs);
	    	vehicleTemp.setStatusT(0);
	    	
	    	SimpleDateFormat sdf = new SimpleDateFormat( "yyyy/MM/dd"); 
	    	vehicleTemp.setProductionDate(sheet.getRow(i).getCell(8).getDateCellValue().getTime());
	    	vehicleTemp.setEnrollDate(sheet.getRow(i).getCell(9).getDateCellValue().getTime());
	    	vehicleTemp.setCreateUser(0l);
	    	vehicleTemp.setCreateTime(0l);
	    	vehicleTemp.setUpdateUser(0l);
	    	vehicleTemp.setUpdateTime(0l);
	    	vehicleService.save(vehicleTemp);
		}
    }
}
