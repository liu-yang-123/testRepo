package com.zcxd.base.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

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

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zcxd.base.config.UserContextHolder;
import com.zcxd.base.dto.FileCompanyDTO;
import com.zcxd.base.service.FileCompanyService;
import com.zcxd.base.service.SysUserService;
import com.zcxd.base.vo.FileCompanyVO;
import com.zcxd.common.annotation.OperateLog;
import com.zcxd.common.annotation.OperateType;
import com.zcxd.common.constant.FileCompanyStatusEnum;
import com.zcxd.common.constant.FileCompanyTypeEnum;
import com.zcxd.common.util.Result;
import com.zcxd.common.util.ResultList;
import com.zcxd.db.model.FileCompany;
import com.zcxd.db.model.SysUser;

import io.swagger.annotations.ApiOperation;

/**
 * 
 * @ClassName FileCompanyController
 * @Description 文件收发单位管理前端控制器
 * @author 秦江南
 * @Date 2022年11月8日下午2:25:22
 */
@RestController
@RequestMapping("/fileCompany")
public class FileCompanyController {

    @Autowired
    private FileCompanyService fileCompanyService;
    
    @Autowired
    private SysUserService userService;
    
    @ApiOperation(value = "单位列表")
    @GetMapping("/list")
    public Result list(@RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required=false) @Size(max=32,message="companyName最大长度为32") String companyName) {
    	FileCompany fileCompany = new FileCompany();
    	fileCompany.setCompanyName(companyName);
        IPage<FileCompany> fileCompanyPage = fileCompanyService.findListByPage(page, limit, fileCompany);
        
        List<FileCompanyDTO> fileCompanyDTOList = new ArrayList<FileCompanyDTO>();
        if(fileCompanyPage.getTotal() > 0){
	        //得到所有操作员id
	        List<String> userIds = new ArrayList<>();
	        fileCompanyPage.getRecords().forEach(t -> {
	        	List<String> userIdList = new ArrayList<>(Arrays.asList(t.getUserIds().split("/")));
	        	userIdList.remove(0);
	        	userIds.addAll(userIdList);
	        });
	        
	        Map<Long,String>  userMap = new HashMap<>();
	        List<SysUser> userList = userService.listByIds(userIds);
	        userMap = userList.stream().collect(Collectors.toMap(SysUser::getId,SysUser::getNickName));
	        
	        Map<Long, String> finalUserMap = userMap;
	 		fileCompanyDTOList = fileCompanyPage.getRecords().stream().map(fileCompanyTmp->{
	 			FileCompanyDTO fileCompanyDTO = new FileCompanyDTO();
	 			BeanUtils.copyProperties(fileCompanyTmp, fileCompanyDTO);
	 			
	 			List<String> userIdList = new ArrayList<>(Arrays.asList(fileCompanyTmp.getUserIds().split("/")));
	        	userIdList.remove(0);
	        	StringBuffer operator = new StringBuffer();
	        	userIdList.forEach(t -> {
	        		operator.append(finalUserMap.get(Long.parseLong(t))).append(",");
	        	});
	        	fileCompanyDTO.setOperator(operator.substring(0,operator.length()-1));
	        	fileCompanyDTO.setUserIds(fileCompanyTmp.getUserIds().substring(1,fileCompanyTmp.getUserIds().length()-1));
	        	
	 			return fileCompanyDTO;
	 		}).collect(Collectors.toList());
        }
         
         ResultList resultList = new ResultList.Builder().total(fileCompanyPage.getTotal()).list(fileCompanyDTOList).build();
         return Result.success(resultList);
         
    }

    
    @OperateLog(value="添加单位",type=OperateType.ADD)
    @ApiOperation(value = "添加单位")
	@PostMapping("/save")
	public Result save(@RequestBody @Validated FileCompanyVO fileCompanyVO) {
    	//得到操作员id集合
    	List<String> userIdList = new ArrayList<>(Arrays.asList(fileCompanyVO.getUserIds().split("/")));
//    	userIdList.remove(0);
    	if(userIdList.size() == 0){
    		return Result.fail("请选择操作员");
    	}
    	
    	//获取操作员姓名列表
    	Map<Long,String>  userMap = new HashMap<>();
        List<SysUser> userList = userService.listByIds(userIdList);
        userMap = userList.stream().collect(Collectors.toMap(SysUser::getId,SysUser::getNickName));
        
        //筛选重复操作员
        Map<Long, String> finalUserMap = userMap;
    	for (String userId : userIdList) {
    		FileCompany fileCompanyTmp = new FileCompany();
    		fileCompanyTmp.setUserIds("/" + userId + "/");
    		List<FileCompany> fileCompanyList = fileCompanyService.getFileCompanyByCondition(fileCompanyTmp);
    		if(fileCompanyList.size() != 0){
    			return Result.fail(finalUserMap.get(Long.parseLong(userId)) + "已经被其它单位选为操作员，不能重复选择");
    		}
    	}
    	
    	FileCompany fileCompany = new FileCompany();
    	BeanUtils.copyProperties(fileCompanyVO, fileCompany);
    	fileCompany.setId(null);
    	fileCompany.setUserIds("/" + fileCompanyVO.getUserIds() + "/");
    	
		boolean save = fileCompanyService.save(fileCompany);
    	if(save){
    		return Result.success();
    	}
    	
    	return Result.fail("添加单位失败");

	}
    
    @OperateLog(value="修改单位",type=OperateType.EDIT)
    @ApiOperation(value = "修改单位")
	@PostMapping("/update")
	public Result update(@RequestBody @Validated FileCompanyVO fileCompanyVO) {
    	if (fileCompanyVO.getId() == null) {
            return Result.fail("单位Id不能为空");
        }
    	
    	FileCompany fileCompany = fileCompanyService.getById(fileCompanyVO.getId());
    	
    	if(!fileCompanyVO.getUserIds().equals(fileCompany.getUserIds().substring(0, fileCompany.getUserIds().length()-1))){
	    	//得到操作员id集合
	    	List<String> userIdList = new ArrayList<>(Arrays.asList(fileCompanyVO.getUserIds().split("/")));
//	    	userIdList.remove(0);
	    	if(userIdList.size() == 0){
	    		return Result.fail("请选择操作员");
	    	}
	    	
	    	//获取操作员姓名列表
	    	Map<Long,String>  userMap = new HashMap<>();
	        List<SysUser> userList = userService.listByIds(userIdList);
	        userMap = userList.stream().collect(Collectors.toMap(SysUser::getId,SysUser::getNickName));
	        
	        //筛选重复操作员
	        Map<Long, String> finalUserMap = userMap;
	    	for (String userId : userIdList) {
	    		FileCompany fileCompanyTmp = new FileCompany();
	    		fileCompanyTmp.setUserIds("/" + userId + "/");
	    		List<FileCompany> fileCompanyList = fileCompanyService.getFileCompanyByCondition(fileCompanyTmp);
	    		if(fileCompanyList.size() != 0){
	    			if(fileCompanyList.get(0).getId() != fileCompanyVO.getId()){
	    				return Result.fail(finalUserMap.get(Long.parseLong(userId)) + "已经被其它单位选为操作员，不能重复选择");
	    			}
	    		}
	    	}
    	}
    	
    	fileCompany = new FileCompany();
    	BeanUtils.copyProperties(fileCompanyVO, fileCompany);
    	fileCompany.setUserIds("/" + fileCompanyVO.getUserIds() + "/");
		
		boolean update = fileCompanyService.updateById(fileCompany);
    	if(update){
    		return Result.success();
    	}
    	
    	return Result.fail("修改单位失败");
	}

    
    @OperateLog(value="删除单位",type=OperateType.DELETE)
    @ApiOperation(value = "删除单位")
	@PostMapping("/delete/{id}")
	public Result delete(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id) {

    	FileCompany fileCompany = new FileCompany();
    	fileCompany.setId(id);
    	fileCompany.setDeleted(1);
		boolean update = fileCompanyService.updateById(fileCompany);
		if(update){
    		return Result.success();
		}
    	
    	return Result.fail("删除单位失败");

	}
    
    @OperateLog(value="停用单位",type=OperateType.EDIT)
    @ApiOperation(value = "停用单位")
    @PostMapping("/stop/{id}")
    public Result stop(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){
    	FileCompany fileCompany = new FileCompany();
    	fileCompany.setId(id);
    	fileCompany.setStatusT(1);
		boolean update = fileCompanyService.updateById(fileCompany);
		if(update){
    		return Result.success();
		}
    	
    	return Result.fail("停用单位失败");
    }
    
    @OperateLog(value="启用单位",type=OperateType.EDIT)
    @ApiOperation(value = "启用单位")
    @PostMapping("/enable/{id}")
    public Result enable(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){
    	FileCompany fileCompany = new FileCompany();
    	fileCompany.setId(id);
    	fileCompany.setStatusT(0);
		boolean update = fileCompanyService.updateById(fileCompany);
		if(update){
    		return Result.success();
		}
    	
    	return Result.fail("启用单位失败");
    }

	@ApiOperation(value = "单位下拉选项")
	@GetMapping("/option")
	public Result option(){
		
		//得到用户单位类型
		FileCompany fileCompanyTmp = new FileCompany();
		fileCompanyTmp.setUserIds("/" + UserContextHolder.getUserId() + "/");
		List<FileCompany> fileCompanyList = fileCompanyService.getFileCompanyByCondition(fileCompanyTmp);
		
		List<Map> mapList = new ArrayList<Map>();
		if(fileCompanyList.size() > 0){
			FileCompany fileCompany = new FileCompany();
			fileCompany.setStatusT(FileCompanyStatusEnum.ENABLE.getValue());
			
			if(fileCompanyList.get(0).getCompanyType().equals(FileCompanyTypeEnum.BANK.getValue())){
				fileCompany.setCompanyType(FileCompanyTypeEnum.COMPANY.getValue());
			}
			
	    	fileCompanyList = fileCompanyService.getFileCompanyByCondition(fileCompany);
	    	mapList = fileCompanyList.stream().map(item -> {
				HashMap map = new HashMap();
				map.put("id",item.getId());
				map.put("name", item.getCompanyName());
				return map;
			}).collect(Collectors.toList());
		}
    	return Result.success(mapList);
	}
}
