package com.zcxd.base.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.base.config.UserContextHolder;
import com.zcxd.common.constant.DeleteFlagEnum;
import com.zcxd.db.mapper.FileCompanyMapper;
import com.zcxd.db.model.FileCompany;

/**
 * 
 * @ClassName FileCompanyService
 * @Description 文件收发单位管理服务类
 * @author 秦江南
 * @Date 2022年11月8日下午2:26:17
 */
@Service
public class FileCompanyService extends ServiceImpl<FileCompanyMapper, FileCompany>{


	/**
	 * 
	 * @Title findListByPage
	 * @Description 分页查询单位列表
	 * @param page
	 * @param limit
	 * @return
	 * @return 返回类型 IPage<FileCompany>
	 */
	public IPage<FileCompany> findListByPage(Integer page, Integer limit, FileCompany fileCompany) {
		Page<FileCompany> ipage=new Page<FileCompany>(page,limit);
    	QueryWrapper<FileCompany> queryWrapper = new QueryWrapper<>();
    	
    	if(fileCompany != null){
    		if(!StringUtils.isBlank(fileCompany.getCompanyName())){
    			queryWrapper.like("company_name",fileCompany.getCompanyName());
    		}
    	}
    	queryWrapper.eq("deleted", DeleteFlagEnum.NOT.getValue());
    	queryWrapper.orderBy(true,false,"create_time");
		
        return baseMapper.selectPage(ipage,queryWrapper);
	}
	
	/**
	 * 
	 * @Title getFileCompanyByCondition
	 * @Description 根据条件查询单位信息
	 * @param denom
	 * @return
	 * @return 返回类型 List<FileCompany>
	 */
	public List<FileCompany> getFileCompanyByCondition(FileCompany fileCompany){
		QueryWrapper<FileCompany> queryWrapper = new QueryWrapper<>();
		if(fileCompany != null){
			if(fileCompany.getCompanyType() != null){
				queryWrapper.eq("company_type", fileCompany.getCompanyType());
			}
			
			if(!StringUtils.isBlank(fileCompany.getUserIds())){
				queryWrapper.like("user_ids", fileCompany.getUserIds());
			}
			
			if(!StringUtils.isBlank(fileCompany.getFileDirectory())){
				queryWrapper.eq("file_directory", fileCompany.getFileDirectory());
			}
			
			if(fileCompany.getStatusT() != null){
				queryWrapper.eq("status_t", fileCompany.getStatusT());
			}
		}
		
		queryWrapper.eq("deleted", DeleteFlagEnum.NOT.getValue()).orderByDesc("create_time");
		return baseMapper.selectList(queryWrapper);
	}
}
