package com.zcxd.base.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zcxd.base.dto.DeviceDTO;
import com.zcxd.base.service.DepartmentService;
import com.zcxd.base.service.DeviceFactoryService;
import com.zcxd.base.service.DeviceModelService;
import com.zcxd.base.service.DeviceService;
import com.zcxd.base.vo.DeviceVO;
import com.zcxd.common.annotation.OperateLog;
import com.zcxd.common.annotation.OperateType;
import com.zcxd.common.util.Result;
import com.zcxd.common.util.ResultList;
import com.zcxd.db.model.Department;
import com.zcxd.db.model.Device;
import com.zcxd.db.model.DeviceFactory;
import com.zcxd.db.model.DeviceModel;
import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author songanwei
 * @date 2021/5/12 16:43
 */
@Api(tags = "设备模块")
@AllArgsConstructor
@RequestMapping("/device")
@RestController
public class DeviceController {

    private DeviceService deviceService;

    private DeviceModelService modelService;

    private DeviceFactoryService factoryService;

    private DepartmentService departmentService;

    @ApiOperation(value = "设备列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页数", required = true, dataType = "Integer", dataTypeClass = Integer.class, defaultValue = "1"),
            @ApiImplicitParam(name = "limit", value = "显示数据条数", required = true, dataType = "Integer", dataTypeClass = Integer.class, defaultValue = "10"),
            @ApiImplicitParam(name = "departmentId", value = "部门", dataType = "Integer", dataTypeClass = Integer.class),
    })
    @GetMapping("/list")
    public Result list(@RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @RequestParam Long departmentId){

        Device device = new Device();
        device.setDepartmentId(departmentId);
        IPage<Device> devicePage = deviceService.findListByPage(page,limit,device);

        List<DeviceModel> modelList = modelService.list();
        List<DeviceFactory> factoryList = factoryService.list();
        Map modelMap = modelList.stream().collect(Collectors.toMap(DeviceModel::getId,DeviceModel::getModelName));
        Map factoryMap = factoryList.stream().collect(Collectors.toMap(DeviceFactory::getId,DeviceFactory::getName));
        List<Department> departmentList = departmentService.list();
        Map departmentMap = departmentList.stream().collect(Collectors.toMap(Department::getId,Department::getName));

        List<DeviceDTO> deviceDTOList = devicePage.getRecords().stream().map(item -> {
            DeviceDTO deviceDTO = new DeviceDTO();
            BeanUtils.copyProperties(item,deviceDTO);
            deviceDTO.setFactoryName((String) Optional.ofNullable(factoryMap.get(item.getFactoryId())).orElse(""));
            deviceDTO.setModelName((String) Optional.ofNullable(modelMap.get(item.getModelId())).orElse(""));
            deviceDTO.setDepartmentName((String) Optional.ofNullable(departmentMap.get(item.getDepartmentId())).orElse(""));
            return deviceDTO;
        }).collect(Collectors.toList());

        ResultList resultList = ResultList.builder().total(devicePage.getTotal()).list(deviceDTOList).build();
        return Result.success(resultList);
    }

    @ApiOperation(value = "创建设备")
    @ApiImplicitParam(name = "deviceVO", value = "设备视图对象", required = true, dataType = "DeviceVO", dataTypeClass = DeviceVO.class)
    @OperateLog(value = "创建设备",type = OperateType.ADD)
    @PostMapping("/save")
    public Result save(@RequestBody @Validated DeviceVO deviceVO){
        DeviceModel model = modelService.getById(deviceVO.getModelId());
        Long factoryId = Optional.ofNullable(model.getFactoryId()).orElse(0L);
        Device oldDevice = deviceService.getByDeviceNo(deviceVO.getDeviceNo());
        if (null != oldDevice) {
            return Result.fail("设备编号重复");
        }
        Device device = new Device();
        BeanUtils.copyProperties(deviceVO,device);
        device.setFactoryId(factoryId);
        deviceService.save(device);
        return Result.success();
    }

    @ApiOperation(value = "编辑设备")
    @ApiImplicitParam(name = "deviceVO", value = "设备视图对象", required = true, dataType = "DeviceVO", dataTypeClass = DeviceVO.class)
    @OperateLog(value = "编辑设备",type = OperateType.EDIT)
    @PostMapping("/update")
    public Result update(@RequestBody @Validated DeviceVO deviceVO){
        DeviceModel model = modelService.getById(deviceVO.getModelId());
        Long factoryId = Optional.ofNullable(model.getFactoryId()).orElse(0L);

        Device oldDevice = deviceService.getByDeviceNo(deviceVO.getDeviceNo());
        if (null != oldDevice && oldDevice.getId() != deviceVO.getId().longValue()) {
            return Result.fail("设备编号重复");
        }

        Device device = new Device();
        BeanUtils.copyProperties(deviceVO,device);
        device.setFactoryId(factoryId);
        deviceService.updateById(device);
        return Result.success();
    }

    @ApiOperation(value = "删除设备")
    @ApiImplicitParam(name = "id", value = "设备id", required = true, dataType = "Long", dataTypeClass = Long.class)
    @OperateLog(value = "删除设备",type = OperateType.DELETE)
    @PostMapping("/delete/{id}")
    public Result delete(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){
        deviceService.removeById(id);
        return Result.success();
    }

}
