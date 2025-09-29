package com.zcxd.base.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zcxd.base.dto.DistrictDTO;
import com.zcxd.base.service.DistrictService;
import com.zcxd.base.vo.DistrictVO;
import com.zcxd.common.annotation.OperateLog;
import com.zcxd.common.annotation.OperateType;
import com.zcxd.common.util.Result;
import com.zcxd.common.util.ResultList;
import com.zcxd.db.model.District;
import io.swagger.annotations.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/district")
@Api(tags = "区域管理")
public class DistrictController {
    @Autowired
    private DistrictService districtService;

    @ApiOperation(value = "区域列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页数", required = true, dataType = "Integer ",defaultValue = "1"),
            @ApiImplicitParam(name = "limit", value = "显示数据条数", required = true, dataType = "Integer ",defaultValue = "10"),
    })
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @GetMapping("/list")
    public Result list(@RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit) {
        IPage<District> districtPage = districtService.findListByPage(page, limit);
        List<DistrictDTO> districtDTOList = districtPage.getRecords().stream().map(district->{
            DistrictDTO districtDTO = new DistrictDTO();
            BeanUtils.copyProperties(district, districtDTO);
            return districtDTO;
        }).collect(Collectors.toList());

        ResultList resultList = new ResultList.Builder().total(districtPage.getTotal()).list(districtDTOList).build();
        return Result.success(resultList);
    }

    @OperateLog(value="添加区域",type= OperateType.ADD)
    @ApiOperation(value = "添加区域")
    @ApiImplicitParam(name = "districtVO", value = "区域信息", required = true, dataType = "DistrictVO" )
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @PostMapping("/save")
    public Result save(@RequestBody @Validated DistrictVO districtVO) {

        District district = new District();

        BeanUtils.copyProperties(districtVO, district);
        district.setId(null);

        boolean save = districtService.save(district);
        if(save)
            return Result.success();

        return Result.fail("添加区域失败");

    }

    @OperateLog(value="修改区域",type=OperateType.EDIT)
    @ApiOperation(value = "修改区域")
    @ApiImplicitParam(name = "denomVO", value = "区域信息", required = false, dataType = "DenomVO" )
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @PostMapping("/update")
    public Result update(@RequestBody @Validated DistrictVO districtVO) {
        if (districtVO.getId() == null) {
            return Result.fail("区域Id不能为空");
        }

        District district = new District();

        BeanUtils.copyProperties(districtVO, district);

        boolean update = districtService.updateById(district);
        if(update)
            return Result.success();

        return Result.fail("修改区域失败");
    }


    @OperateLog(value="删除区域",type=OperateType.DELETE)
    @ApiOperation(value = "删除区域")
    @ApiImplicitParam(name = "id", value = "区域id", required = true, dataType = "Long")
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @PostMapping("/delete/{id}")
    public Result delete(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id) {

        District district = new District();
        district.setId(id);
        district.setDeleted(1);
        boolean update = districtService.updateById(district);

        if(update)
            return Result.success();

        return Result.fail("删除区域失败");

    }
}
