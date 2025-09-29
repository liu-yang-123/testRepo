package com.zcxd.base.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zcxd.base.service.DeviceFactoryService;
import com.zcxd.base.vo.DeviceFactoryVO;
import com.zcxd.common.annotation.OperateLog;
import com.zcxd.common.annotation.OperateType;
import com.zcxd.common.util.Result;
import com.zcxd.common.util.ResultList;
import com.zcxd.db.model.DeviceFactory;
import io.swagger.annotations.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author songanwei
 * @date 2021-05-14
 */
@Api(tags = "设备品牌模块")
@RequestMapping("/deviceFactory")
@RestController
public class DeviceFactoryController {

    @Autowired
    private DeviceFactoryService factoryService;

    @ApiOperation(value = "设备列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页数", required = true, dataType = "Integer", dataTypeClass = Integer.class, defaultValue = "1"),
            @ApiImplicitParam(name = "limit", value = "显示数据条数", required = true, dataType = "Integer", dataTypeClass = Integer.class, defaultValue = "10"),
            @ApiImplicitParam(name = "name", value = "品牌名称", dataType = "String", dataTypeClass = String.class),
    })
    @GetMapping("/list")
    public Result list(@RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @RequestParam(required=false) @Size(max=32,message="userName最大长度为32") String name){
        DeviceFactory factory = new DeviceFactory();
        factory.setName(name);
        Page<DeviceFactory> deviceFactoryPage = factoryService.findListByPage(page, limit,factory);
        List<DeviceFactoryVO> factoryVOList = deviceFactoryPage.getRecords().stream().map(item -> {
            DeviceFactoryVO factoryVO = new DeviceFactoryVO();
            BeanUtils.copyProperties(item,factoryVO);
            return factoryVO;
        }).collect(Collectors.toList());

        ResultList resultList = ResultList.builder().total(deviceFactoryPage.getTotal()).list(factoryVOList).build();
        return Result.success(resultList);
    }

    @ApiOperation(value = "创建设备品牌")
    @OperateLog(value = "创建设备品牌",type = OperateType.ADD)
    @PostMapping("/save")
    public Result save(@RequestBody @Validated DeviceFactoryVO deviceFactoryVO){
        DeviceFactory factory = new DeviceFactory();
        BeanUtils.copyProperties(deviceFactoryVO,factory);

        //验证名称是否已存在
        DeviceFactory existFactory = factoryService.getDeviceFactoryByCondition(factory);
        if (existFactory != null){
            return Result.fail("设备品牌名称已经存在");
        }
        factoryService.save(factory);
        return Result.success();
    }

    @ApiOperation(value = "修改设备品牌")
    @OperateLog(value = "修改设备品牌",type = OperateType.EDIT)
    @PostMapping("/update")
    public Result update(@RequestBody @Validated DeviceFactoryVO factoryVO){
        DeviceFactory factory = new DeviceFactory();
        BeanUtils.copyProperties(factoryVO,factory);
        DeviceFactory existFactory = factoryService.getDeviceFactoryByCondition(factory);
        if (existFactory != null && !existFactory.getId().equals(factoryVO.getId())){
            return Result.fail("设备品牌名称已经存在");
        }
        factoryService.updateById(factory);
        return Result.success();
    }

    @ApiOperation(value = "删除设备品牌")
    @ApiImplicitParam(name = "id", value = "品牌id", required = true, dataType = "Long", dataTypeClass = Long.class)
    @OperateLog(value = "删除设备品牌",type = OperateType.DELETE)
    @PostMapping("/delete/{id}")
    public Result delete(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){
        DeviceFactory deviceFactory = new DeviceFactory();
        deviceFactory.setId(id);
        deviceFactory.setDeleted(1);
        boolean b = factoryService.updateById(deviceFactory);
        if (b){
            return Result.success();
        }
        return Result.fail("删除设备品牌失败");
    }

    @ApiOperation(value = "品牌下拉框选项")
    @GetMapping("/option")
    public Result option() {
        QueryWrapper queryWrapper = Wrappers.query().eq("deleted",0);
        List<DeviceFactory> deviceFactoryList = factoryService.list(queryWrapper);
        List<Map> options = deviceFactoryList.stream().map(r -> {
            HashMap map = new HashMap(2);
            map.put("value",r.getId());
            map.put("label",r.getName());
            return map;
        }).collect(Collectors.toList());
        return Result.success(options);
    }

}
