package com.zcxd.base.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zcxd.base.service.DeviceFactoryService;
import com.zcxd.base.service.DeviceModelService;
import com.zcxd.base.vo.DeviceModelVO;
import com.zcxd.common.annotation.OperateLog;
import com.zcxd.common.annotation.OperateType;
import com.zcxd.common.util.Result;
import com.zcxd.common.util.ResultList;
import com.zcxd.db.model.DeviceFactory;
import com.zcxd.db.model.DeviceModel;
import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author songanwei
 * @date 2021-05-14
 */
@Api(tags = "设备型号模块")
@AllArgsConstructor
@RequestMapping("/deviceModel")
@RestController
public class DeviceModelController {

    private DeviceModelService modelService;

    private DeviceFactoryService factoryService;

    @ApiOperation(value = "设备型号列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页数", required = true, dataType = "Integer", dataTypeClass = Integer.class, defaultValue = "1"),
            @ApiImplicitParam(name = "limit", value = "显示数据条数", required = true, dataType = "Integer", dataTypeClass = Integer.class, defaultValue = "10"),
            @ApiImplicitParam(name = "modelName", value = "型号", dataType = "String", dataTypeClass = String.class),
    })
    @GetMapping("/list")
    public Result list(@RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @RequestParam(required=false) @Size(max=32,message="userName最大长度为32") String modelName){
        DeviceModel deviceModel = new DeviceModel();
        deviceModel.setModelName(modelName);
        Page<DeviceModel> modelPage = modelService.findListByPage(page,limit,deviceModel);

        List<DeviceModelVO> modelVOList = modelPage.getRecords().stream().map(item -> {
            DeviceModelVO deviceModelVO = new DeviceModelVO();
            BeanUtils.copyProperties(item,deviceModelVO);
            return deviceModelVO;
        }).collect(Collectors.toList());
        ResultList resultList = ResultList.builder().total(modelPage.getTotal()).list(modelVOList).build();
        return Result.success(resultList);
    }

    @ApiOperation("创建设备型号")
    @OperateLog(value = "创建设备型号",type = OperateType.ADD)
    @PostMapping("/save")
    public Result save(@RequestBody @Validated DeviceModelVO modelVO){
        DeviceModel model = new DeviceModel();
        BeanUtils.copyProperties(modelVO,model);
        //验证名称是否已经存在
        DeviceModel existModel = modelService.getDeviceModelByCondition(model);
        if (existModel != null){
            return Result.fail("设备型号已经创建");
        }
        boolean b = modelService.save(model);
        if (b){
            return Result.success();
        }
        return Result.fail("设备型号创建失败");
    }

    @ApiOperation(value = "修改设备型号")
    @OperateLog(value = "修改设备型号",type = OperateType.EDIT)
    @PostMapping("/update")
    public Result update(@RequestBody @Validated DeviceModelVO modelVO){
        DeviceModel model = new DeviceModel();
        BeanUtils.copyProperties(modelVO,model);
        DeviceModel existModel = modelService.getDeviceModelByCondition(model);
        if (existModel != null && !existModel.getId().equals(modelVO.getId())){
            return Result.fail("设备型号已经创建");
        }
        boolean b = modelService.updateById(model);
        if (b){
            return Result.success();
        }
        return Result.fail("设备型号修改失败");
    }

    @ApiOperation(value = "删除设备型号")
    @ApiImplicitParam(name = "id", value = "型号id", required = true, dataType = "Long", dataTypeClass = Long.class)
    @OperateLog(value = "删除设备型号",type = OperateType.DELETE)
    @PostMapping("/delete/{id}")
    public Result delete(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){
        DeviceModel model = new DeviceModel();
        model.setId(id);
        model.setDeleted(1);
        boolean b = modelService.updateById(model);
        if (b){
            return Result.success();
        }
        return Result.fail("设备型号删除失败");
    }

    @ApiOperation(value = "设备型号下拉列表选项")
    @GetMapping("/option")
    public Result option(){
        QueryWrapper queryWrapper = Wrappers.query().eq("deleted",0);
        List<DeviceModel> modelList = modelService.list(queryWrapper);
        //品牌数据
        List<DeviceFactory> factoryList = factoryService.list();
        Map factoryMap = factoryList.stream().collect(Collectors.toMap(DeviceFactory::getId,DeviceFactory::getName));

        List<Map> mapList = modelList.stream().map(r -> {
            HashMap map = new HashMap();
            map.put("id",r.getId());
            String factoryName = Optional.ofNullable(factoryMap.get(r.getFactoryId()).toString()).orElse("");
            map.put("name",factoryName +"/"+ r.getModelName());
            return map;
        }).collect(Collectors.toList());
        return Result.success(mapList);
    }

}
