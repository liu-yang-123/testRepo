package com.zcxd.base.controller;


import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zcxd.base.service.CommonService;
import com.zcxd.base.service.HandoverLogService;
import com.zcxd.base.vo.HandoverLogVO;
import com.zcxd.common.annotation.OperateLog;
import com.zcxd.common.annotation.OperateType;
import com.zcxd.common.util.Result;
import com.zcxd.db.model.HandoverLog;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
/**
 * 交班日志
 * @ClassName HandoverLogController
 * @Description 交班日志
 * @author 秦江南
 * @Date 2022年5月13日下午2:58:01
 */
@RestController
@RequestMapping("/handoverLog")
@Validated
public class HandoverLogController {

	@Autowired
    private HandoverLogService handoverLogService;
    @Autowired
    private CommonService commonService;
    
    @Value("${services.filepath.win}")
    private String fileServerPath_win;
    @Value("${services.filepath.linux}")
    private String fileServerPath_linux;
    

    @OperateLog(value="新增交班日志", type=OperateType.ADD)
    @ApiOperation(value = "新增交班日志")
    @ApiImplicitParam(name = "handoverLogVO", value = "交班日志", required = true, dataType = "HandoverLogVO")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/save")
    public Object save(@RequestBody HandoverLogVO handoverLogVO){
    	HandoverLog handoverLog = new HandoverLog();
    	BeanUtils.copyProperties(handoverLogVO, handoverLog);
    	boolean save = handoverLogService.save(handoverLog);
    	if(save){
    		if (!StringUtils.isBlank(handoverLog.getFileUrl())) {
    			commonService.moveFile(handoverLog.getFileUrl());
    		}
    		
    		if (!StringUtils.isBlank(handoverLog.getImageUrl())) {
    			commonService.moveFile(handoverLog.getImageUrl());
    		}
    		
    		return Result.success("添加成功");
    	}
    	return Result.fail("添加失败");
    }

    @OperateLog(value="删除交班日志", type=OperateType.DELETE)
    @ApiOperation(value = "删除交班日志")
    @ApiImplicitParam(name = "id", value = "日志id", required = true, dataType = "Long")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/del/{id}")
    public Object delete(@PathVariable("id") Long id){
    	HandoverLog handoverLog = handoverLogService.getById(id);
    	handoverLog.setDeleted(1);
    	boolean update = handoverLogService.updateById(handoverLog);
    	if(update){
            if(!StringUtils.isEmpty(handoverLog.getFileUrl())){
    	        File file = new File(getFileServerPath() + handoverLog.getFileUrl());
    	        if (file.exists()) {
    	        	file.delete();
    	        }
            }
            
            if(!StringUtils.isEmpty(handoverLog.getImageUrl())){
    	        File file = new File(getFileServerPath() + handoverLog.getImageUrl());
    	        if (file.exists()) {
    	        	file.delete();
    	        }
            }
    		
    		return Result.success("删除成功");
    	}
    	return Result.fail("删除失败");
    }

    @OperateLog(value="修改交班日志", type=OperateType.EDIT)
    @ApiOperation(value = "修改交班日志")
    @ApiImplicitParam(name = "handoverLogVO", value = "交班日志", required = true, dataType = "HandoverLogVO")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/update")
    public Object update(@RequestBody HandoverLogVO handoverLogVO){
    	HandoverLog handoverLogTmp = handoverLogService.getById(handoverLogVO.getId());
    	HandoverLog handoverLog = new HandoverLog();
    	BeanUtils.copyProperties(handoverLogVO, handoverLog);
    	
    	boolean update = handoverLogService.updateById(handoverLog);
    	if(update){
    		//文件处理
    		if(StringUtils.isEmpty(handoverLogVO.getFileUrl())){
        		if(!StringUtils.isEmpty(handoverLogTmp.getFileUrl())){
    		        File file = new File(getFileServerPath() + handoverLogTmp.getFileUrl());
    		        if (file.exists()) {
    		        	file.delete();
    		        }
        		}
            }else{
            	
            	if(StringUtils.isEmpty(handoverLogTmp.getFileUrl())){
            		commonService.moveFile(handoverLogVO.getFileUrl());
            	}
            	
        		if(!StringUtils.isEmpty(handoverLogTmp.getFileUrl()) &&
        				!handoverLogVO.getFileUrl().equals(handoverLogTmp.getFileUrl())){
    		        File file = new File(getFileServerPath() + handoverLogTmp.getFileUrl());
    		        if (file.exists()) {
    		        	file.delete();
    		        }
            		commonService.moveFile(handoverLogVO.getFileUrl());
        		}
        		
            }
            
    		if(StringUtils.isEmpty(handoverLogVO.getImageUrl())){
        		if(!StringUtils.isEmpty(handoverLogTmp.getImageUrl())){
    		        File file = new File(getFileServerPath() + handoverLogTmp.getImageUrl());
    		        if (file.exists()) {
    		        	file.delete();
    		        }
        		}
            }else{
            	
            	if(StringUtils.isEmpty(handoverLogTmp.getImageUrl())){
            		commonService.moveFile(handoverLogVO.getImageUrl());
            	}
            	
        		if(!StringUtils.isEmpty(handoverLogTmp.getImageUrl()) &&
        				!handoverLogVO.getImageUrl().equals(handoverLogTmp.getImageUrl())){
    		        File file = new File(getFileServerPath() + handoverLogTmp.getImageUrl());
    		        if (file.exists()) {
    		        	file.delete();
    		        }
            		commonService.moveFile(handoverLogVO.getImageUrl());
        		}
            }
    		
    		return Result.success("操作成功");
    	}
    	return Result.fail("操作失败");
    }

    @ApiOperation(value = "查询交班日志列表")
    @ApiImplicitParams({
    		@ApiImplicitParam(name = "page", value = "当前页数", required = true, dataType = "Integer",defaultValue = "1"),
    		@ApiImplicitParam(name = "limit", value = "显示数据条数", required = true, dataType = "Integer",defaultValue = "10"),
    		@ApiImplicitParam(name = "departmentId", value = "部门", required = true, dataType = "Long"),
            @ApiImplicitParam(name = "title", value = "标题", required = false, dataType = "String"),
            @ApiImplicitParam(name = "dateBegin", value = "开始日期", required = false, dataType = "Long"),
            @ApiImplicitParam(name = "dateEnd", value = "结束日期", required = false, dataType = "Long")
    })
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/list")
    public Object findListByPage(@RequestParam Integer page,
                                  @RequestParam Integer limit,
                                  @RequestParam Long departmentId,
                                  String title,
                                  Long dateBegin,
                                  Long dateEnd){
        Object object = handoverLogService.findListByPage(page, limit, departmentId, title,dateBegin,dateEnd);
        return Result.success(object);
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
}

