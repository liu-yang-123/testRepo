package com.zcxd.base.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.common.util.DateTimeUtil;
import com.zcxd.db.mapper.DepartmentMapper;
import com.zcxd.db.mapper.HandoverLogMapper;
import com.zcxd.db.model.HandoverLog;
import com.zcxd.db.model.SysUser;

/**
 * 
 * @ClassName HandoverLogService
 * @Description 交班日志服务类
 * @author 秦江南
 * @Date 2022年5月20日下午3:11:07
 */
@Service
public class HandoverLogService extends ServiceImpl<HandoverLogMapper, HandoverLog>{
    @Resource
    private SysUserService sysUserService;
    @Resource
    private DepartmentMapper departmentMapper;
    
    public Object findListByPage(Integer page,Integer limit, Long departmentId,String title, Long dateBegin,Long dateEnd){
        IPage<HandoverLog> wherePage = new Page<>(page,limit);
        
        QueryWrapper<HandoverLog> queryWrapper = new QueryWrapper<>();
        if (departmentId != null && departmentId != 0L) {
            queryWrapper.eq("department_id", departmentId);
        }
        if (dateBegin != null) {
            Long dtBegin = DateTimeUtil.getDailyStartTimeMs(dateBegin);
            queryWrapper.ge("create_time", dtBegin);
        }
        if (dateEnd != null) {
            Long dtEnd = DateTimeUtil.getDailyEndTimeMs(dateEnd);
            queryWrapper.le("create_time", dtEnd);
        }
        if (!StringUtils.isEmpty(title)) {
            queryWrapper.like("title", title);
        }
        
        queryWrapper.eq("deleted",0).orderByDesc("create_time");
        IPage<HandoverLog> iPage = baseMapper.selectPage(wherePage,queryWrapper);

        JSONObject jsonObject = new JSONObject();
        if (iPage.getRecords() != null && iPage.getRecords().size() > 0) {
            List<JSONObject> jsonObjectList = new ArrayList<>();
            
            iPage.getRecords().stream().forEach(handoverLog ->{
            	JSONObject jsonObject1 = (JSONObject)JSON.toJSON(handoverLog);
                SysUser user = sysUserService.getUserById(handoverLog.getCreateUser());
                jsonObject1.put("createUserName", user == null ? "" : user.getUsername());
                jsonObjectList.add(jsonObject1);
            });
            
            jsonObject.put("list",  jsonObjectList);
            jsonObject.put("total", iPage.getTotal());
        } else {
            jsonObject.put("list", null);
            jsonObject.put("total", 0);
        }
        return jsonObject;
    }
    
}
