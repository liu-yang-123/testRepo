package com.zcxd.base.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Size;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.zcxd.base.config.UserContextHolder;
import com.zcxd.common.constant.FileRecordReadSignEnum;
import com.zcxd.common.util.DateTimeUtil;
import com.zcxd.common.util.Result;
import com.zcxd.db.model.Denom;
import com.zcxd.db.model.Department;
import com.zcxd.db.model.FileCompany;
import com.zcxd.db.model.FileRecord;
import com.zcxd.db.model.Vehicle;

/**
 * 
 * @ClassName CommonService
 * @Description 公用服务类
 * @author 秦江南
 * @Date 2021年5月18日上午9:49:29
 */
@Service
public class CommonService {
	
    @Value("${services.filepath.win}")
    private String fileServerPath_win;
    @Value("${services.filepath.linux}")
    private String fileServerPath_linux;
    
    @Value("${services.sendpath.win}")
    private String sendServerPath_win;
    @Value("${services.sendpath.linux}")
    private String sendServerPath_linux;
	
    @Autowired
    private DepartmentService departmentService;
    
	@Autowired
    private DenomService denomService;
	
	@Autowired
    private VehicleService vehicleService;
	
	@Autowired
    private FileRecordService fileRecordService;
	
	@Autowired
    private FileCompanyService fileCompanyService;
	
    /**
     * 
     * @Title getDepartmentMap
     * @Description 获得部门Map
     * @return
     * @return 返回类型 Map<String,String>
     */
	public Map<String, String> getDepartmentMap(Department department){
    	List<Map<String, Object>> departmentList = departmentService.getDepartmentList(department);
    	Map<String, String> departmentMap = new HashMap<>(2);
    	departmentList.stream().forEach(departmentTmp -> {
    		departmentMap.put(departmentTmp.get("id").toString(), departmentTmp.get("name").toString());
    	});
    	return departmentMap;
	}
	
	/**
	 * 
	 * @Title getVehicleMap
	 * @Description 获得车辆Map
	 * @return
	 * @return 返回类型 Map<String,Object>
	 */
	public Map<String, Object> getVehicleMap(){
    	List<Vehicle> vehicleList = vehicleService.getVehicleByCondition(null);
    	Map<String, Object> vehicleMap = new HashMap<>(2);
    	vehicleList.stream().forEach(vehicleTmp -> {
    		Map map = new HashMap<>(2);
    		map.put("lpno", vehicleTmp.getLpno());
    		map.put("seqno", vehicleTmp.getSeqno());
    		vehicleMap.put(vehicleTmp.getId()+"", map);
    	});
    	return vehicleMap;
	}
	
	
	/**
	 * 
	 * @Title getDenomMap
	 * @Description 获取券别Map
	 * @return
	 * @return 返回类型 Map<String,AtmDevice>
	 */
	public Map<String, Long> getDenomMap(){
		Map<String, Long> denomMap = new HashMap<>();
		Denom denomTmp = new Denom();
		denomTmp.setCurCode("CNY");
		denomTmp.setAttr("P");
		denomTmp.setValue(new BigDecimal(100));
		denomTmp.setVersion(0);
		List<Denom> denomList = denomService.getDenomByCondition(denomTmp);
		
		denomMap.put(100 + "", denomList.get(0).getId());
		

		denomTmp.setValue(new BigDecimal(10));
		denomList = denomService.getDenomByCondition(denomTmp);
		
		denomMap.put(10 + "", denomList.get(0).getId());
		return denomMap;
		
	}

	/**
	 * 
	 * @Title upload
	 * @Description 上传文件
	 * @param file
	 * @return
	 * @return 返回类型 Result
	 */
	public Result upload(MultipartFile file) {
		String fileName = file.getOriginalFilename();
	    fileName = genFileName() + fileName.substring(fileName.lastIndexOf('.'));
	    String filePath = DateTimeUtil.getNowDateFormat();
	    String path = getFileServerPath() + "tmp/" + filePath + "/";
	    (new File(path)).mkdirs();
	    
	    File dest = new File(path + fileName);
	    try {
	        file.transferTo(dest);
	        return Result.success(filePath+"/"+fileName);
	    } catch (IOException e) {
	    	e.printStackTrace();
	    }
	    return Result.fail();
	}
	
	
	/**
	 * 
	 * @Title send
	 * @Description 文件传输
	 * @param file
	 * @return
	 * @return 返回类型 Result
	 */
	public Result send(MultipartFile file) {
		String fileName = file.getOriginalFilename();
	    fileName = genFileName() + fileName.substring(fileName.lastIndexOf('.'));
	    Calendar calendar = Calendar.getInstance();
	    String filePath = calendar.get(Calendar.YEAR) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.DATE);
	    String path = getSendServerPath() + "tmp/" + filePath + "/";
	    (new File(path)).mkdirs();
	    
	    File dest = new File(path + fileName);
	    try {
	        file.transferTo(dest);
	        return Result.success(filePath+"/"+fileName);
	    } catch (IOException e) {
	    	e.printStackTrace();
	    }
	    return Result.fail();
	}

	/**
	 * 
	 * @Title showImg
	 * @Description 查看图片
	 * @param fileUrl
	 * @return
	 * @return 返回类型 Result
	 */
	public String showImg(@Size(max = 128, message = "文件最大长度为128") String fileUrl) {
		fileUrl = getFileServerPath() + fileUrl;
	   	 File file = new File(fileUrl);
	        StringBuilder sb = new StringBuilder();
	   	 if (file.exists()) {
		         //将图片文件转化为字节数组字符串，并对其进行Base64编码处理
		         InputStream in = null;
		         byte[] data = null;
		         //读取图片字节数组
		         try 
		         {
	       		 in = new FileInputStream(fileUrl);   
		             data = new byte[in.available()];
		             in.read(data);
		             in.close();
		         }catch (IOException e){
		             e.printStackTrace();
		         } finally {
		            try {
		            	if(in != null){
		            		in.close();
		            	}
		            } catch (IOException e) {
		                e.printStackTrace();
		            }
		 	    }
			 	 sb.append("data:image/png;base64,");
			 	 sb.append(new String(Base64.encodeBase64(data)));
	   	 }
	     return sb.toString();
	}

	
	/**
	 * 
	 * @Title downLoad
	 * @Description 下载文件
	 * @param response
	 * @param fileUrl
	 * @param fileName
	 * @return 返回类型 void
	 */
	public void downLoad(HttpServletResponse response, String fileUrl, String fileName) {
    	OutputStream out = null;
		if (fileUrl.contains("..")) {
			return;
		}
 	    try {
 	        response.reset();
 	        response.setContentType("application/octet-stream; charset=utf-8");
 	        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
 	        out = response.getOutputStream();
 	        File file = new File(getFileServerPath() + fileUrl);
 	        out.write(FileUtils.readFileToByteArray(file));
 	        out.flush();
 	    } catch (IOException e) {
 	        e.printStackTrace();
 	    } finally {
 	        if (out != null) {
 	            try {
 	                out.close();
 	            } catch (IOException e) {
 	                e.printStackTrace();
 	            }
 	        }
 	    }
	}
	
	/**
	 * 
	 * @Title fileSendDownLoad
	 * @Description 文件传输下载
	 * @param response
	 * @param fileUrl
	 * @param fileName
	 * @param recordId
	 * @return 返回类型 void
	 */
	public void fileSendDownLoad(HttpServletResponse response, String fileUrl, String fileName,Long recordId) {
		if (fileUrl.contains("..")) {
			return;
		}
		if(recordId != null){
			FileRecord fileRecord = fileRecordService.getById(recordId);
			if(fileRecord != null && fileRecord.getReadSign().equals(FileRecordReadSignEnum.UNREAD.getValue())){
				//修改已读标识
				FileCompany fileCompanyTmp = new FileCompany();
				fileCompanyTmp.setUserIds("/" + UserContextHolder.getUserId() + "/");
				List<FileCompany> fileCompanyList = fileCompanyService.getFileCompanyByCondition(fileCompanyTmp);
				if(fileCompanyList.size() > 0 && fileRecord.getCompanyId().equals(fileCompanyList.get(0).getId())){
					fileRecord.setReadSign(FileRecordReadSignEnum.READ.getValue());
					fileRecordService.updateById(fileRecord);
				}
			}
		}
		
		//文件下载
		response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
		response.setHeader("Content-Type","application/octet-stream;charset=utf-8");
		response.setHeader("X-Accel-Redirect", "/resource/" + fileUrl);
		response.setHeader("X-Accel-Charset","utf-8");
		response.setHeader("Pragma","No-cache");
		response.setHeader("Cache-Control","No-cache");
		response.setHeader("Expires", "0");
	}
	
	/**
	 * 
	 * @Title moveFile
	 * @Description 移动文件
	 * @param fileUrl
	 * @return 返回类型 void
	 */
	public void moveFile(String fileUrl){
		String path = getFileServerPath() + fileUrl.substring(0,fileUrl.lastIndexOf("/")+1);
		(new File(path)).mkdirs();
		File file = new File(getFileServerPath() + "tmp/" + fileUrl);
 		file.renameTo(new File(path+file.getName()));
	}
	
	/**
	 * 
	 * @Title moveSendFile
	 * @Description 移动文件传输管理文件并得到文件大小
	 * @param fileUrl
	 * @return 
	 * @return 返回类型 Long
	 */
	public Long moveSendFile(String fileUrl){
		String path = getSendServerPath() + fileUrl.substring(0,fileUrl.lastIndexOf("/")+1);
		(new File(path)).mkdirs();
		File file = new File(getSendServerPath() + "tmp/" + fileUrl);
		Long fileSize =  file.length();
 		file.renameTo(new File(path+file.getName()));
 		return fileSize;
	}
	
	/**
	 * 
	 * @Title deleteFile
	 * @Description 删除文件传输文件
	 * @param fileUrl
	 * @return 返回类型 boolean
	 */
	public boolean deleteSendFile(String fileUrl){
		File file = new File(getSendServerPath() + fileUrl);
		if (file.exists()) {
        	return file.delete();
        }
		return false;
	}
	
	/**
	 * 
	 * @Title moveFileToPath
	 * @Description 移动文件到指定目录
	 * @param fileUrl
	 * @return 返回类型 void
	 */
	public String moveFileToPath(String fileUrl, String toPath){
		String path = getFileServerPath() + fileUrl.substring(0,fileUrl.lastIndexOf("/")+1) + toPath + "/";;
		(new File(path)).mkdirs();
		File file = new File(getFileServerPath() + "tmp/" + fileUrl);
		String fileName = path +file.getName();
 		file.renameTo(new File(fileName));
 		return fileName;
	}
	
	//生成唯一文件名
	public String genFileName() {
		//取当前时间的长整形值包含毫秒
		long millis = System.currentTimeMillis();
		//long millis = System.nanoTime();
		//加上两位随机数
		Random random = new Random();
		int end2 = random.nextInt(999);
		//如果不足两位前面补0
		String str = millis + String.format("%03d", end2);
		return str;
    }
        
    //获取文件路径
    public String getFileServerPath(){
 	   String osName = System.getProperties().getProperty("os.name");
       if(osName.equals("Linux")){
       	return fileServerPath_linux;
       }else{
       	return fileServerPath_win;
       }
    }
    
    //获取文件传输管理文件路径
    public String getSendServerPath(){
 	   String osName = System.getProperties().getProperty("os.name");
       if(osName.equals("Linux")){
       	return sendServerPath_linux;
       }else{
       	return sendServerPath_win;
       }
    }
    
    public static void main(String[] args) {
		File file = new File("D://Data//file//tmp//");
	}
	
}
