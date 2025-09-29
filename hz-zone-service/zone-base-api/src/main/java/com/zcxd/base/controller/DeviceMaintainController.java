package com.zcxd.base.controller;

import com.zcxd.base.dto.DeviceMaintainDto;
import com.zcxd.base.service.DeviceMaintainService;
import com.zcxd.base.vo.DeviceMaintainVO;
import com.zcxd.common.annotation.OperateLog;
import com.zcxd.common.annotation.OperateType;
import com.zcxd.common.util.Result;
import com.zcxd.common.util.ResultList;
import com.zcxd.db.model.DeviceMaintain;
import io.swagger.annotations.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * @author songanwei
 * @date 2021-05-14
 */
@Api(tags = "设备维修模块")
@RequestMapping("/deviceMaintain")
@RestController
public class DeviceMaintainController {

    @Autowired
    private DeviceMaintainService maintainService;

    @ApiOperation(value = "设备维修列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页数", required = true, dataType = "Integer", dataTypeClass = Integer.class, defaultValue = "1"),
            @ApiImplicitParam(name = "limit", value = "显示数据条数", required = true, dataType = "Integer", dataTypeClass = Integer.class, defaultValue = "10"),
            @ApiImplicitParam(name = "deviceNo", value = "设备编号", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(name = "mtEngineer", value = "维保工程师", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(name = "mtType", value = "维保类型", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(name = "mtResult", value = "维保结果", dataType = "Integer", dataTypeClass = Integer.class),
    })
    @GetMapping("/list")
    public Result list(@RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @RequestParam(required=false) String deviceNo,
                       @RequestParam(required=false) String mtEngineer,
                       @RequestParam(required=false) String mtType,
                       @RequestParam(required=false) Integer mtResult,
                       @RequestParam Long departmentId){

        long total = maintainService.findTotal(deviceNo, mtEngineer, mtType, mtResult,departmentId);
        List<DeviceMaintainDto> maintainList = maintainService.findListByCondition(page, limit, deviceNo, mtEngineer, mtType, mtResult,departmentId);

        ResultList resultList = ResultList.builder().total(total).list(maintainList).build();
        return Result.success(resultList);
    }

    @ApiOperation(value = "创建设备维护")
    @OperateLog(value = "创建设备维护",type = OperateType.ADD)
    @PostMapping("/save")
    public Result save(@RequestBody @Validated DeviceMaintainVO maintainVO){
        DeviceMaintain maintain = new DeviceMaintain();
        BeanUtils.copyProperties(maintainVO,maintain);
        maintainService.save(maintain);
        return Result.success();
    }
    
}
