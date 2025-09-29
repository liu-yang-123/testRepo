package com.zcxd.base.controller;

import java.util.Map;

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
import com.zcxd.base.service.VehicleMaintainService;
import com.zcxd.base.vo.VehicleMaintainVO;
import com.zcxd.common.annotation.OperateLog;
import com.zcxd.common.annotation.OperateType;
import com.zcxd.common.util.Result;
import com.zcxd.common.util.ResultList;
import com.zcxd.db.model.VehicleMaintain;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;

/**
 * 
 * @ClassName VehicleMaintainController
 * @Description 车辆维保管理控制器
 * @author 秦江南
 * @Date 2021年5月19日下午4:02:57
 */
@RestController
@RequestMapping("/vehicleMaintain")
@Api(tags = "车辆维保管理")
public class VehicleMaintainController {
	@Autowired
    private VehicleMaintainService vehicleMaintainService;


	@OperateLog(value="添加维保记录",type=OperateType.ADD)
    @ApiOperation(value = "添加维保记录")
    @ApiImplicitParam(name = "vehicleMaintainVO", value = "维保信息", required = true, dataType = "VehicleMaintainVO")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/save")
    public Result save(@RequestBody @Validated VehicleMaintainVO vehicleMaintainVO){
		VehicleMaintain vehicleMaintain = new VehicleMaintain();
        BeanUtils.copyProperties(vehicleMaintainVO, vehicleMaintain);
        vehicleMaintain.setId(null);
        
        boolean save = vehicleMaintainService.save(vehicleMaintain);
    	if(save)
    		return Result.success();
    	
    	return Result.fail("添加维保记录失败");
    }

	@OperateLog(value="删除维保记录",type=OperateType.DELETE)
    @ApiOperation(value = "删除维保记录")
    @ApiImplicitParam(name = "id", value = "维保记录ID", required = true, dataType = "Long")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/delete/{id}")
    public Result delete(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){
		VehicleMaintain vehicleMaintain = new VehicleMaintain();
		vehicleMaintain.setId(id);
		vehicleMaintain.setDeleted(1);
		boolean update = vehicleMaintainService.updateById(vehicleMaintain);
		
		if(update)
    		return Result.success();
    	
    	return Result.fail("删除维保记录失败");
    }

	@OperateLog(value="修改维保记录",type=OperateType.EDIT)
    @ApiOperation(value = "修改维保记录")
    @ApiImplicitParam(name = "vehicleMaintainVO", value = "维保信息", required = true, dataType = "VehicleMaintainVO")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/update")
    public Result update(@RequestBody @Validated VehicleMaintainVO vehicleMaintainVO){
		
		if (vehicleMaintainVO.getId() == null) {
            return Result.fail("维保记录Id不能为空");
        }
		VehicleMaintain vehicleMaintain = new VehicleMaintain();
        BeanUtils.copyProperties(vehicleMaintainVO, vehicleMaintain);
        
        boolean update = vehicleMaintainService.updateById(vehicleMaintain);
    	if(update)
    		return Result.success();
    	
    	return Result.fail("修改维保记录失败");
		
    }

    @ApiOperation(value = "查询维保记录列表")
    @ApiImplicitParams({
    	@ApiImplicitParam(name = "page", value = "当前页数", required = true, dataType = "Integer ",defaultValue = "1"),
        @ApiImplicitParam(name = "limit", value = "显示数据条数", required = true, dataType = "Integer ",defaultValue = "10"),
        @ApiImplicitParam(name = "lpno", value = "车牌号码", required = false, dataType = "String"),
        @ApiImplicitParam(name = "seqno", value = "车辆编号", required = false, dataType = "String"),
        @ApiImplicitParam(name = "mtType", value = "维保类型", required = false, dataType = "String"),
        @ApiImplicitParam(name = "mtResult", value = "维保结果", required = false, dataType = "Integer"),
        @ApiImplicitParam(name = "departmentId", value = "部门id", required = true, dataType = "Long")
    })
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @GetMapping("/list")
    public Result list(@RequestParam(defaultValue = "1") Integer page,
			   @RequestParam(defaultValue = "10") Integer limit,
			   @RequestParam(required=false) @Size(max=32,message="车牌号码最大长度为32") String lpno,
			   @RequestParam(required=false) @Size(max=32,message="车辆编号最大长度为32") String seqno,
			   @RequestParam(required=false) @Size(max=32,message="维保类型最大长度为32") String mtType,
			   @RequestParam(required=false) Integer mtResult,
			   @RequestParam @Min(value=1, message="departmentId不能小于1") Long departmentId){
    	
    	IPage<Map<String,Object>> findListByPage = vehicleMaintainService.findListByPage(page, limit, lpno, seqno, mtType, mtResult,departmentId);
    	ResultList resultList = new ResultList.Builder().total(findListByPage.getTotal()).list(findListByPage.getRecords()).build();
        return Result.success(resultList);
    }
}
