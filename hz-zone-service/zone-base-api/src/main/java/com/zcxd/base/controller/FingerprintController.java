package com.zcxd.base.controller;

import java.util.Map;

import javax.validation.constraints.Size;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zcxd.base.service.FingerprintService;
import com.zcxd.common.util.Result;
import com.zcxd.common.util.ResultList;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;

/**
 * 
 * @ClassName FingerprintController
 * @Description 指纹管理控制器
 * @author 秦江南
 * @Date 2021年5月19日上午10:46:34
 */
@RestController
@RequestMapping("/fingerprint")
@Api(tags = "指纹管理")
public class FingerprintController {
	@Autowired
    private FingerprintService fingerprintService;
    
    @ApiOperation(value = "查询指纹特征列表")
    @ApiImplicitParams({
    	@ApiImplicitParam(name = "page", value = "当前页数", required = true, dataType = "Integer ",defaultValue = "1"),
        @ApiImplicitParam(name = "limit", value = "显示数据条数", required = true, dataType = "Integer ",defaultValue = "10"),
        @ApiImplicitParam(name = "userType", value = "人员类型", required = false, dataType = "String"),
        @ApiImplicitParam(name = "empNo", value = "员工工号", required = false, dataType = "String"),
        @ApiImplicitParam(name = "empName", value = "员工名称", required = false, dataType = "String")        
    })
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @GetMapping("/list")
    public Result list(@RequestParam(defaultValue = "1") Integer page,
			   @RequestParam(defaultValue = "10") Integer limit,
			   @RequestParam(required=false) @Size(max=32,message="人员类型最大长度为32") String userType,
			   @RequestParam(required=false) @Size(max=32,message="员工工号最大长度为32") String empNo,
			   @RequestParam(required=false) @Size(max=32,message="员工姓名最大长度为32") String empName,
			   @RequestParam(required=false) Long jobId){
    	IPage<Map<String,Object>> findListByPage = fingerprintService.findListByPage(page, limit, userType, empNo, empName, jobId);
    	ResultList resultList = new ResultList.Builder().total(findListByPage.getTotal()).list(findListByPage.getRecords()).build();
        return Result.success(resultList);
    }
}
