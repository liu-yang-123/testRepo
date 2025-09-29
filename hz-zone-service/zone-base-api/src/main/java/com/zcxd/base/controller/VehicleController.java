package com.zcxd.base.controller;

import java.util.ArrayList;
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
import com.zcxd.base.dto.VehicleDTO;
import com.zcxd.base.service.VehicleService;
import com.zcxd.base.vo.VehicleVO;
import com.zcxd.common.annotation.OperateLog;
import com.zcxd.common.annotation.OperateType;
import com.zcxd.common.util.Result;
import com.zcxd.common.util.ResultList;
import com.zcxd.db.model.Vehicle;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;

/**
 * 
 * @ClassName VehicleController
 * @Description 车辆管理控制器
 * @author 秦江南
 * @Date 2021年5月19日下午3:08:13
 */
@RestController
@RequestMapping("/vehicle")
@Api(tags = "车辆管理")
public class VehicleController {
	@Autowired
    private VehicleService vehicleService;

	@OperateLog(value="添加车辆",type=OperateType.ADD)
    @ApiOperation(value = "添加车辆")
    @ApiImplicitParam(name = "vehicleVO", value = "车辆信息", required = true, dataType = "VehicleVO")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/save")
    public Result save(@RequestBody @Validated VehicleVO vehicleVO){
		Vehicle vehicle = new Vehicle();
		vehicle.setSeqno(vehicleVO.getSeqno());
		List<Vehicle> vehicleList = vehicleService.getVehicleByCondition(vehicle);
		if(vehicleList != null && vehicleList.size()>0)
			return Result.fail("车辆编号已存在，请重新填写！");
		
		vehicle = new Vehicle();
		vehicle.setLpno(vehicleVO.getLpno());
		List<Vehicle> vehicleLists = vehicleService.getVehicleByCondition(vehicle);
		if(vehicleLists != null && vehicleLists.size()>0)
			return Result.fail("车牌号码已存在，请重新填写！");
		
        BeanUtils.copyProperties(vehicleVO, vehicle);
        vehicle.setId(null);
        
        boolean save = vehicleService.save(vehicle);
    	if(save)
    		return Result.success();
    	
    	return Result.fail("添加车辆失败");
    }

	@OperateLog(value="删除车辆",type=OperateType.DELETE)
    @ApiOperation(value = "删除车辆")
    @ApiImplicitParam(name = "id", value = "车辆ID", required = true, dataType = "Long")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/delete/{id}")
    public Result delete(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){
		Vehicle vehicle = new Vehicle();
		vehicle.setId(id);
		vehicle.setDeleted(1);
		boolean update = vehicleService.updateById(vehicle);
		
		if(update)
    		return Result.success();
    	
    	return Result.fail("删除车辆失败");
    }

	@OperateLog(value="修改车辆信息",type=OperateType.EDIT)
    @ApiOperation(value = "修改车辆信息")
    @ApiImplicitParam(name = "vehicleVO", value = "奖惩记录", required = true, dataType = "VehicleVO")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/update")
    public Result update(@RequestBody @Validated VehicleVO vehicleVO){
		
		if (vehicleVO.getId() == null) {
            return Result.fail("车辆Id不能为空");
        }
		
		Vehicle vehicle = new Vehicle();
		vehicle.setSeqno(vehicleVO.getSeqno());
		List<Vehicle> vehicleList = vehicleService.getVehicleByCondition(vehicle);
		if(vehicleList != null && vehicleList.size()>0)
			if(!vehicleList.get(0).getId().equals(vehicleVO.getId()))
				return Result.fail("车辆编号已存在，请重新填写！");
		
		vehicle = new Vehicle();
		vehicle.setLpno(vehicleVO.getLpno());
		List<Vehicle> vehicleLists = vehicleService.getVehicleByCondition(vehicle);
		if(vehicleLists != null && vehicleLists.size()>0)
			if(!vehicleLists.get(0).getId().equals(vehicleVO.getId()))
				return Result.fail("车牌号码已存在，请重新填写！");
		
        BeanUtils.copyProperties(vehicleVO, vehicle);
        
        boolean update = vehicleService.updateById(vehicle);
    	if(update)
    		return Result.success();
    	
    	return Result.fail("修改车辆信息失败");
		
    }

	@PostMapping("/findVehicleByCondition")
	public Result<List<Vehicle>> findVehicleByCondition(@RequestBody VehicleVO vehicleVO){
		Vehicle vehicle = new Vehicle();
		vehicle.setLpno(vehicleVO.getLpno());
		vehicle.setSeqno(vehicle.getSeqno());
		vehicle.setStatusT(vehicle.getStatusT());
		vehicle.setDepartmentId(vehicle.getDepartmentId());
		List<Vehicle> vehicleByCondition = vehicleService.getVehicleByCondition(vehicle);

		return vehicleByCondition.isEmpty() ? Result.fail("未查找到指定车辆") : Result.success(vehicleByCondition);
	}

    @ApiOperation(value = "查询车辆列表")
    @ApiImplicitParams({
    	@ApiImplicitParam(name = "page", value = "当前页数", required = true, dataType = "Integer ",defaultValue = "1"),
        @ApiImplicitParam(name = "limit", value = "显示数据条数", required = true, dataType = "Integer ",defaultValue = "10"),
        @ApiImplicitParam(name = "lpno", value = "车牌号码", required = false, dataType = "String"),
        @ApiImplicitParam(name = "seqno", value = "车辆编号", required = false, dataType = "String"),
        @ApiImplicitParam(name = "departmentId", value = "部门id", required = true, dataType = "Long")
    })
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @GetMapping("/list")
    public Result list(@RequestParam(defaultValue = "1") Integer page,
			   @RequestParam(defaultValue = "10") Integer limit,
			   @RequestParam(required=false) @Size(max=32,message="车牌号码最大长度为32") String lpno,
			   @RequestParam(required=false) @Size(max=32,message="车辆编号最大长度为32") String seqno,
			   @RequestParam(required=false) Integer statusT,
			   @RequestParam @Min(value=1, message="departmentId不能小于1") Long departmentId){
    	
    	Vehicle vehicle = new Vehicle();
    	vehicle.setLpno(lpno);
    	vehicle.setSeqno(seqno);
    	vehicle.setStatusT(statusT);
    	vehicle.setDepartmentId(departmentId);
    	IPage<Vehicle> vehiclePage = vehicleService.findListByPage(page, limit, vehicle);
    	
 		List<VehicleDTO> vehicleDTOList = vehiclePage.getRecords().stream().map(vehicleTmp->{
 			VehicleDTO vehicleDTO = new VehicleDTO();
 			BeanUtils.copyProperties(vehicleTmp, vehicleDTO);
 			return vehicleDTO;
 		}).collect(Collectors.toList());
    	
    	ResultList resultList = new ResultList.Builder().total(vehiclePage.getTotal()).list(vehicleDTOList).build();
        return Result.success(resultList);
    }
    
    @ApiOperation(value = "车辆维修")
    @ApiImplicitParam(name = "id", value = "车辆id", required = true, dataType = "Long")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/repair/{id}")
    public Result stop(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){
    	Vehicle vehicle = new Vehicle();
    	vehicle.setId(id);
    	vehicle.setStatusT(1);
        boolean update = vehicleService.updateById(vehicle);
    	if(update)
    		return Result.success();
    	
    	return Result.fail("车辆维修失败");
    }
    
    @ApiOperation(value = "车辆启用")
    @ApiImplicitParam(name = "id", value = "车辆id", required = true, dataType = "Long")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/enable/{id}")
    public Result enable(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){
    	Vehicle vehicle = new Vehicle();
    	vehicle.setId(id);
    	vehicle.setStatusT(0);
        boolean update = vehicleService.updateById(vehicle);
    	if(update)
    		return Result.success();
    	
    	return Result.fail("车辆启用失败");
    }
    
    @ApiOperation(value = "车辆报废")
    @ApiImplicitParam(name = "id", value = "车辆id", required = true, dataType = "Long")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/scrap/{id}")
    public Result scrap(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){
    	Vehicle vehicle = new Vehicle();
    	vehicle.setId(id);
    	vehicle.setStatusT(2);
        boolean update = vehicleService.updateById(vehicle);
    	if(update)
    		return Result.success();
    	
    	return Result.fail("车辆报废失败");
    }
    
    
    @ApiOperation(value = "车辆下拉框选项")
    @ApiImplicitParam(name = "departmentId", value = "部门id", required = true, dataType = "Long")
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @GetMapping("/option")
    public Result option(@RequestParam Long departmentId) {
    	Vehicle vehicle = new Vehicle();
    	vehicle.setDepartmentId(departmentId);
    	vehicle.setStatusT(0);
        List<Vehicle> vehicleList = vehicleService.getVehicleByCondition(vehicle);
        
        List<Map<String, Object>> options = new ArrayList<>(vehicleList.size());
        vehicleList.stream().forEach(vehicleTmp -> {
        	Map<String, Object> option = new HashMap<>(2);
            option.put("id", vehicleTmp.getId());
            option.put("seqno", vehicleTmp.getSeqno());
            option.put("lpno", vehicleTmp.getLpno());
            options.add(option);
        });
        return Result.success(options);
    } 
    
}
