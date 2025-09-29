package com.zcxd.base.service;

import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.common.constant.DeleteFlagEnum;
import com.zcxd.common.util.DateTimeUtil;
import com.zcxd.db.mapper.FileRecordMapper;
import com.zcxd.db.model.FileRecord;

/**
 * 
 * @ClassName FileRecordService
 * @Description 文件传输记录管理服务类
 * @author 秦江南
 * @Date 2022年11月11日上午11:13:22
 */
@Service
public class FileRecordService extends ServiceImpl<FileRecordMapper, FileRecord>{


	/**
	 * 
	 * @Title findListByPage
	 * @Description 分页查询文件传输记录列表
	 * @param page
	 * @param limit
	 * @return
	 * @return 返回类型 IPage<FileCompany>
	 */
	public IPage<FileRecord> findListByPage(Integer page, Integer limit, FileRecord fileRecord, Long dateBegin, Long dateEnd, Set<Long> companyIds) {
		Page<FileRecord> ipage=new Page<FileRecord>(page,limit);
    	QueryWrapper<FileRecord> queryWrapper = new QueryWrapper<>();
    	
    	if(fileRecord != null){
    		if(!StringUtils.isBlank(fileRecord.getRecordTitle())){
    			queryWrapper.like("record_title", fileRecord.getRecordTitle());
    		}
    		if(!StringUtils.isBlank(fileRecord.getFileName())){
    			queryWrapper.like("file_name", fileRecord.getFileName());
    		}
    		if (fileRecord.getSendCompanyId() != null) {
                queryWrapper.eq("send_company_id", fileRecord.getSendCompanyId());
            }
    		if (fileRecord.getCompanyId() != null) {
                queryWrapper.eq("company_id", fileRecord.getCompanyId());
            }
    	}
		
        if (dateBegin != null) {
            Long dtBegin = DateTimeUtil.getDailyStartTimeMs(dateBegin);
            queryWrapper.ge("create_time", dtBegin);
        }
        if (dateEnd != null) {
            Long dtEnd = DateTimeUtil.getDailyEndTimeMs(dateEnd);
            queryWrapper.le("create_time", dtEnd);
        }
        if (companyIds != null) {
            queryWrapper.in("company_id", companyIds);
        }
        
    	queryWrapper.eq("deleted", DeleteFlagEnum.NOT.getValue());
    	queryWrapper.orderBy(true,false,"create_time");
		
        return baseMapper.selectPage(ipage,queryWrapper);
	}
//	
//	/**
//	 * 
//	 * @Title getFileCompanyByCondition
//	 * @Description 根据条件查询单位信息
//	 * @param denom
//	 * @return
//	 * @return 返回类型 List<FileCompany>
//	 */
//	public List<FileCompany> getFileCompanyByCondition(FileCompany fileCompany){
//		QueryWrapper<FileCompany> queryWrapper = new QueryWrapper<>();
//		if(fileCompany != null){
//			if(fileCompany.getCompanyType() != null){
//				queryWrapper.eq("company_type", fileCompany.getCompanyType());
//			}
//			
//			if(!StringUtils.isBlank(fileCompany.getUserIds())){
//				queryWrapper.like("user_ids", fileCompany.getUserIds());
//			}
//			
//			if(fileCompany.getStatusT() != null){
//				queryWrapper.eq("status_t", fileCompany.getStatusT());
//			}
//		}
//		
//		queryWrapper.eq("deleted", DeleteFlagEnum.NOT.getValue()).orderByDesc("create_time");
//		return baseMapper.selectList(queryWrapper);
//	}
}
