package com.zcxd.base.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.constraints.Size;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zcxd.base.dto.LogDTO;
import com.zcxd.base.service.SysLogService;
import com.zcxd.base.service.SysUserService;
import com.zcxd.common.util.Result;
import com.zcxd.common.util.ResultList;
import com.zcxd.db.model.SysLog;
import com.zcxd.db.model.SysUser;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;

/**
 * 
 * @ClassName SysLogController
 * @Description 日志管理控制器
 * @author 秦江南
 * @Date 2021年5月12日下午4:56:29
 */
@RestController
@RequestMapping("/log")
@Api(tags = "日志管理")
public class SysLogController {

    @Autowired
    private SysLogService logService;
    
    @Autowired
    private SysUserService userService;

    @ApiOperation(value = "查询日志列表")
    @ApiImplicitParams({
    	@ApiImplicitParam(name = "page", value = "当前页数", required = true, dataType = "Integer ",defaultValue = "1"),
        @ApiImplicitParam(name = "limit", value = "显示数据条数", required = true, dataType = "Integer ",defaultValue = "10"),
        @ApiImplicitParam(name = "userName", value = "管理员名称", required = false, dataType = "String"),
    })
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @GetMapping("/list")
    public Result list(@RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @RequestParam(required=false) @Size(max=32,message="userName最大长度为32") String userName,
                       @RequestParam(required=false) Integer type,
                       @RequestParam(required=false) String result) {
    	
    	Map<Long,String> userMap = new HashMap<>();
		SysUser userTmp = new SysUser();
		userTmp.setNickName(userName);
		List<SysUser> userList = userService.getUserByCondition(userTmp);
		if (userList.size() == 0) {
			ResultList resultList = new ResultList.Builder().total(0).list(null).build();
			return Result.success(resultList);
		} else {
            for(SysUser user : userList) {
               userMap.put(user.getId(),user.getNickName());
            }
		}
    	
		SysLog logTmp = new SysLog();
		logTmp.setTypeT(type);
		logTmp.setResult(result);
        IPage<SysLog> logPage = logService.findListByPage(page, limit, logTmp, userMap.keySet());
        
        List<LogDTO> logList = null;
        if (logPage.getRecords().size() > 0) {
        	logList = logPage.getRecords().stream().map(log->{
        		LogDTO logDTO = new LogDTO();
        		BeanUtils.copyProperties(log, logDTO);
        		logDTO.setNickName(userMap.get(log.getUserId()).toString());
        		return logDTO;
        	}).collect(Collectors.toList());
        }
        
        ResultList resultList = new ResultList.Builder().total(logPage.getTotal()).list(logList).build();
        return Result.success(resultList);
    }

}
