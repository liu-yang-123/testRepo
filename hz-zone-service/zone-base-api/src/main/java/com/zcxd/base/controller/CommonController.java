package com.zcxd.base.controller;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Size;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.zcxd.base.service.CommonService;
import com.zcxd.common.util.Result;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;

/**
 * 
 * @ClassName CommonController
 * @Description 通用工具控制器
 * @author 秦江南
 * @Date 2021年5月19日下午2:28:37
 */
@RestController
@RequestMapping("/common")
@Api(tags = "通用工具")
public class CommonController {

	@Autowired
	private CommonService commonService;
    
    @ApiOperation(value = "上传文件")
    @ApiImplicitParam(name = "file", value = "文件", required = true, dataType = "MultipartFile")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
	@PostMapping("/fileUpload")
	public Result upload(MultipartFile file) {
    	return commonService.upload(file);
	}
    
    @ApiOperation(value = "文件传输")
    @ApiImplicitParam(name = "file", value = "文件", required = true, dataType = "MultipartFile")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
	@PostMapping("/fileSend")
	public Result fileSend(MultipartFile file) {
    	return commonService.send(file);
	}
	
    @ApiOperation(value = "查看图片")
    @ApiImplicitParam(name = "fileUrl", value = "文件地址", required = true, dataType = "String")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
	@PostMapping("/showImg")
	public Result showImg(@RequestParam @Size(max=128,message="文件最大长度为128") String fileUrl) {
    	return Result.fail();
//		return Result.success(commonService.showImg(fileUrl));
	}
     
     @ApiOperation(value = "下载文件")
     @ApiImplicitParams({
    	 @ApiImplicitParam(name = "fileUrl", value = "文件地址", required = true, dataType = "String"),
    	 @ApiImplicitParam(name = "fileName", value = "文件名称", required = true, dataType = "String")
     })
     @ApiResponse(code = 200, message = "处理成功", response = Result.class)
	 @PostMapping("/fileDownLoad")
	 public void downLoad(HttpServletResponse response,
			 @RequestParam @Size(max=128,message="文件最大长度为128") String fileUrl,
			 @RequestParam @Size(max=32,message="文件最大长度为32") String fileName) {
    	commonService.downLoad(response, fileUrl, fileName);
	 }
     
     @ApiOperation(value = "文件传输下载")
     @ApiImplicitParams({
    	 @ApiImplicitParam(name = "fileUrl", value = "文件地址", required = true, dataType = "String"),
    	 @ApiImplicitParam(name = "fileName", value = "文件名称", required = true, dataType = "String")
     })
     @ApiResponse(code = 200, message = "处理成功", response = Result.class)
	 @GetMapping("/fileSendDownLoad")
	 public void fileSendDownLoad(HttpServletResponse response,
			 @RequestParam @Size(max=128,message="文件最大长度为128") String fileUrl,
			 @RequestParam @Size(max=32,message="文件最大长度为32") String fileName,
			 Long recordId) {
    	commonService.fileSendDownLoad(response, fileUrl, fileName, recordId);
	 }
     
}
