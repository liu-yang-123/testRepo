package com.zcxd.base.controller;

import java.util.ArrayList;
import java.util.HashMap;
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
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zcxd.base.config.UserContextHolder;
import com.zcxd.base.dto.FileRecordDTO;
import com.zcxd.base.service.CommonService;
import com.zcxd.base.service.FileCompanyService;
import com.zcxd.base.service.FileRecordService;
import com.zcxd.base.service.SysUserService;
import com.zcxd.base.vo.FileRecordQueryVO;
import com.zcxd.base.vo.FileRecordVO;
import com.zcxd.common.annotation.OperateLog;
import com.zcxd.common.annotation.OperateType;
import com.zcxd.common.util.Result;
import com.zcxd.common.util.ResultList;
import com.zcxd.db.model.FileCompany;
import com.zcxd.db.model.FileRecord;
import com.zcxd.db.model.SysUser;

import io.swagger.annotations.ApiOperation;

/**
 * 
 * @ClassName FileRecordController
 * @Description 文件传输记录
 * @author 秦江南
 * @Date 2022年11月9日下午3:54:59
 */
@RestController
@RequestMapping("/fileRecord")
public class FileRecordController {
	
	@Autowired
    private CommonService commonService;
    @Autowired
    private FileRecordService fileRecordService;
    @Autowired
    private FileCompanyService fileCompanyService;
    @Autowired
    private SysUserService userService;
	
    
    @ApiOperation(value = "接收文件列表")
    @GetMapping("/receiveList")
    public Result receiveList(@Validated FileRecordQueryVO fileRecordQueryVO){
    	//获取用户所在单位
    	FileCompany fileCompanyTmp = new FileCompany();
    	fileCompanyTmp.setUserIds("/" + UserContextHolder.getUserId() + "/");
    	List<FileCompany> fileCompanyList = fileCompanyService.getFileCompanyByCondition(fileCompanyTmp);
    	
    	if(fileCompanyList.size() == 0){
    		ResultList resultList = new ResultList.Builder().total(0).list(new ArrayList<>()).build();
            return Result.success(resultList);
    	}
    	
    	fileCompanyTmp = fileCompanyList.get(0);
    	//获取与用户单位文件目录相同的单位
    	String fileDirectory = fileCompanyTmp.getFileDirectory();
    	fileCompanyTmp = new FileCompany();
    	fileCompanyTmp.setFileDirectory(fileDirectory);
    	fileCompanyList = fileCompanyService.getFileCompanyByCondition(fileCompanyTmp);
    	Set<Long> companyIds = fileCompanyList.stream().map(FileCompany::getId).collect(Collectors.toSet());
    	
    	//查询文件传输记录
    	FileRecord fileRecord = new FileRecord();
    	fileRecord.setRecordTitle(fileRecordQueryVO.getRecordTitle());
    	fileRecord.setSendCompanyId(fileRecordQueryVO.getCompanyId());
    	IPage<FileRecord> fileRecordPage = fileRecordService.findListByPage(fileRecordQueryVO.getPage(), fileRecordQueryVO.getLimit(), 
    			fileRecord, fileRecordQueryVO.getDateBegin(), fileRecordQueryVO.getDateEnd(), companyIds);
    	
    	//返回结果处理
    	List<FileRecordDTO> recordDTOList = new ArrayList<FileRecordDTO>();
    	if(fileRecordPage.getTotal() > 0){
    		Set<Long> userIds = fileRecordPage.getRecords().stream().map(FileRecord::getUserId).collect(Collectors.toSet());
    		Map<Long,String>  userMap = new HashMap<>();
            List<SysUser> userList = userService.listByIds(userIds);
            userMap = userList.stream().collect(Collectors.toMap(SysUser::getId,SysUser::getNickName));
            
    		Map<Long,String>  companyMap = new HashMap<>();
    		fileCompanyList = fileCompanyService.getFileCompanyByCondition(null);
    		companyMap = fileCompanyList.stream().collect(Collectors.toMap(FileCompany::getId,FileCompany::getCompanyName));
    		
    		Map<Long, String> finalUserMap = userMap;
    		Map<Long, String> finalCompanyMap = companyMap;
    		recordDTOList = fileRecordPage.getRecords().stream().map(fileRecordTmp->{
    			FileRecordDTO fileRecordDTO = new FileRecordDTO();
     			BeanUtils.copyProperties(fileRecordTmp, fileRecordDTO);
     			fileRecordDTO.setUserName(finalUserMap.get(fileRecordTmp.getUserId()));
     			fileRecordDTO.setCompanyName(finalCompanyMap.get(fileRecordTmp.getSendCompanyId()));
     			return fileRecordDTO;
     		}).collect(Collectors.toList());
    	}
    	
        ResultList resultList = new ResultList.Builder().total(fileRecordPage.getTotal()).list(recordDTOList).build();
        return Result.success(resultList);
    }
    
    @ApiOperation(value = "发送文件列表")
    @GetMapping("/sendList")
    public Result sendList(@Validated FileRecordQueryVO fileRecordQueryVO){
    	//获取用户所在单位
    	FileCompany fileCompanyTmp = new FileCompany();
    	fileCompanyTmp.setUserIds("/" + UserContextHolder.getUserId() + "/");
    	List<FileCompany> fileCompanyList = fileCompanyService.getFileCompanyByCondition(fileCompanyTmp);
    	
    	if(fileCompanyList.size() == 0){
    		ResultList resultList = new ResultList.Builder().total(0).list(new ArrayList<>()).build();
            return Result.success(resultList);
    	}
	    	
    	//查询文件传输记录
    	FileRecord fileRecord = new FileRecord();
    	fileRecord.setRecordTitle(fileRecordQueryVO.getRecordTitle());
    	fileRecord.setSendCompanyId(fileCompanyList.get(0).getId());
    	fileRecord.setCompanyId(fileRecordQueryVO.getCompanyId());
    	IPage<FileRecord> fileRecordPage = fileRecordService.findListByPage(fileRecordQueryVO.getPage(), fileRecordQueryVO.getLimit(), 
    			fileRecord, fileRecordQueryVO.getDateBegin(), fileRecordQueryVO.getDateEnd(), null);
    	
    	//返回结果处理
    	List<FileRecordDTO> recordDTOList = new ArrayList<FileRecordDTO>();
    	if(fileRecordPage.getTotal() > 0){
    		Set<Long> userIds = fileRecordPage.getRecords().stream().map(FileRecord::getUserId).collect(Collectors.toSet());
    		Map<Long,String>  userMap = new HashMap<>();
            List<SysUser> userList = userService.listByIds(userIds);
            userMap = userList.stream().collect(Collectors.toMap(SysUser::getId,SysUser::getNickName));
            
            Set<Long> companyIds = fileRecordPage.getRecords().stream().map(FileRecord::getCompanyId).collect(Collectors.toSet());
    		Map<Long,String>  companyMap = new HashMap<>();
    		fileCompanyList = fileCompanyService.listByIds(companyIds);
    		companyMap = fileCompanyList.stream().collect(Collectors.toMap(FileCompany::getId,FileCompany::getCompanyName));
    		
    		Map<Long, String> finalUserMap = userMap;
    		Map<Long, String> finalCompanyMap = companyMap;
    		recordDTOList = fileRecordPage.getRecords().stream().map(fileRecordTmp->{
    			FileRecordDTO fileRecordDTO = new FileRecordDTO();
     			BeanUtils.copyProperties(fileRecordTmp, fileRecordDTO);
     			fileRecordDTO.setUserName(finalUserMap.get(fileRecordTmp.getUserId()));
     			fileRecordDTO.setCompanyName(finalCompanyMap.get(fileRecordTmp.getCompanyId()));
     			return fileRecordDTO;
     		}).collect(Collectors.toList());
    	}
    	ResultList resultList = new ResultList.Builder().total(fileRecordPage.getTotal()).list(recordDTOList).build();
        return Result.success(resultList);
    	
    }

    
    @OperateLog(value="添加文件传输记录",type=OperateType.ADD)
    @ApiOperation(value = "添加文件传输记录")
	@PostMapping("/save")
	public Result save(@RequestBody @Validated FileRecordVO fileRecordVO) {
    	//获取用户所在单位
    	FileCompany fileCompanyTmp = new FileCompany();
    	fileCompanyTmp.setUserIds("/" + UserContextHolder.getUserId() + "/");
    	List<FileCompany> fileCompanyList = fileCompanyService.getFileCompanyByCondition(fileCompanyTmp);
    	if(fileCompanyList.size() == 0){
    		return Result.fail("不是单位操作员，无法上传文件");
    	}
    	
    	FileRecord fileRecord = new FileRecord();
    	BeanUtils.copyProperties(fileRecordVO, fileRecord);
    	fileRecord.setFilePath(fileRecordVO.getFileUrl());
    	fileRecord.setId(null);
    	fileRecord.setUserId(UserContextHolder.getUserId());
    	fileRecord.setSendCompanyId(fileCompanyList.get(0).getId());
    	
    	//文件移动处理
    	Long fileSize = commonService.moveSendFile(fileRecordVO.getFileUrl());
    	fileRecord.setFileSize(fileSize);
    	
		boolean save = fileRecordService.save(fileRecord);
    	if(save){
    		return Result.success();
    	}
    	
    	return Result.fail("添加文件传输记录失败");

	}
 
    @OperateLog(value="删除上传文件记录",type=OperateType.DELETE)
    @ApiOperation(value = "删除上传文件记录")
	@PostMapping("/delete/{id}")
	public Result delete(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id) {

    	FileRecord fileRecord = fileRecordService.getById(id);
    	fileRecord.setId(id);
    	fileRecord.setDeleted(1);
		boolean update = fileRecordService.updateById(fileRecord);
		if(update){
			commonService.deleteSendFile(fileRecord.getFilePath());
    		return Result.success();
		}
    	
    	return Result.fail("删除文件传输记录失败");

	}
    
    
    @ApiOperation(value = "所有文件列表")
    @GetMapping("/all")
    public Result all(@Validated FileRecordQueryVO fileRecordQueryVO){
    	FileRecord fileRecord = new FileRecord();
    	fileRecord.setFileName(fileRecordQueryVO.getFileName());
    	fileRecord.setRecordTitle(fileRecordQueryVO.getRecordTitle());
    	fileRecord.setSendCompanyId(fileRecordQueryVO.getCompanyId());
    	IPage<FileRecord> fileRecordPage = fileRecordService.findListByPage(fileRecordQueryVO.getPage(), fileRecordQueryVO.getLimit(), 
    			fileRecord, fileRecordQueryVO.getDateBegin(), fileRecordQueryVO.getDateEnd(), null);
    	
    	
		Map<Long,String>  companyMap = new HashMap<>();
		List<FileCompany> fileCompanyList = fileCompanyService.getFileCompanyByCondition(null);
		companyMap = fileCompanyList.stream().collect(Collectors.toMap(FileCompany::getId,FileCompany::getCompanyName));
		
		Map<Long, String> finalCompanyMap = companyMap;
    	//返回结果处理
    	List<FileRecordDTO> recordDTOList = fileRecordPage.getRecords().stream().map(fileRecordTmp->{
    			FileRecordDTO fileRecordDTO = new FileRecordDTO();
     			BeanUtils.copyProperties(fileRecordTmp, fileRecordDTO);
     			fileRecordDTO.setCompanyName(finalCompanyMap.get(fileRecordTmp.getSendCompanyId()));
     			return fileRecordDTO;
     		}).collect(Collectors.toList());
        ResultList resultList = new ResultList.Builder().total(fileRecordPage.getTotal()).list(recordDTOList).build();
        return Result.success(resultList);
    }
    
    @OperateLog(value="删除文件记录",type=OperateType.DELETE)
    @ApiOperation(value = "删除文件记录")
	@PostMapping("/remove/{id}")
	public Result remove(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id) {

    	FileRecord fileRecord = fileRecordService.getById(id);
    	fileRecord.setId(id);
    	fileRecord.setDeleted(1);
		boolean update = fileRecordService.updateById(fileRecord);
		if(update){
			commonService.deleteSendFile(fileRecord.getFilePath());
    		return Result.success();
		}
    	return Result.fail("删除文件记录失败");

	}
}
