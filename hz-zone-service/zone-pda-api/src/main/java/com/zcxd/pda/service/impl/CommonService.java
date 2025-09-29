package com.zcxd.pda.service.impl;

import java.io.*;
import javax.validation.constraints.Size;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 
 * @ClassName CommonService
 * @Description 公共服务类
 * @author 秦江南
 * @Date 2021年12月2日下午2:20:54
 */
@Service
public class CommonService {
	@Value("${services.filepath.win}")
    private String fileServerPath_win;
    @Value("${services.filepath.linux}")
    private String fileServerPath_linux;
    
	/**
	 * 
	 * @Title showImg
	 * @Description 查看图片
	 * @param fileUrl
	 * @return
	 * @return 返回类型 Result
	 */
	public String showImg(@Size(max = 128, message = "文件最大长度为128") String fileUrl) {
		if (fileUrl == null || "".equals(fileUrl)){
			return "";
		}
		fileUrl = getFileServerPath() + fileUrl;
		File file = new File(fileUrl);
		StringBuilder sb = new StringBuilder();
		if (file.exists()) {
			//将图片文件转化为字节数组字符串，并对其进行Base64编码处理
			byte[] data = null;
			//读取图片字节数组
			try(InputStream in = new FileInputStream(fileUrl)) {
				data = new byte[in.available()];
				in.read(data);
			}catch (IOException e){
				e.printStackTrace();
			}
			sb.append(new String(Base64.encodeBase64(data)));
		}
		return sb.toString();
	}


	/**
	 * 获取路径
	 */
	public String getFileServerPath(){
		String osName = System.getProperties().getProperty("os.name");
		if(osName.equals("Linux")){
			return fileServerPath_linux;
		}else{
			return fileServerPath_win;
		}
	}



}
