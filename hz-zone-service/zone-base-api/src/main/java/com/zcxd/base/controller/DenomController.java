package com.zcxd.base.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zcxd.base.dto.DenomDTO;
import com.zcxd.base.service.DenomService;
import com.zcxd.base.vo.DenomVO;
import com.zcxd.common.annotation.OperateLog;
import com.zcxd.common.annotation.OperateType;
import com.zcxd.common.util.Result;
import com.zcxd.common.util.ResultList;
import com.zcxd.db.model.Denom;
import io.swagger.annotations.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @ClassName DenomController
 * @Description 券别管理前端控制器
 * @author 秦江南
 * @Date 2021年5月21日下午2:25:22
 */
@RestController
@RequestMapping("/denom")
@Api(tags = "券别管理")
public class DenomController {


    @Autowired
    private DenomService denomService;

    @ApiOperation(value = "券别列表")
    @ApiImplicitParams({
    	@ApiImplicitParam(name = "page", value = "当前页数", required = true, dataType = "Integer ",defaultValue = "1"),
        @ApiImplicitParam(name = "limit", value = "显示数据条数", required = true, dataType = "Integer ",defaultValue = "10"),
    })
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @GetMapping("/list")
    public Result list(@RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit) {
        IPage<Denom> denomPage = denomService.findListByPage(page, limit);
 		List<DenomDTO> denomDTOList = denomPage.getRecords().stream().map(denom->{
 			DenomDTO denomDTO = new DenomDTO();
 			BeanUtils.copyProperties(denom, denomDTO);
 			return denomDTO;
 		}).collect(Collectors.toList());

         ResultList resultList = new ResultList.Builder().total(denomPage.getTotal()).list(denomDTOList).build();
         return Result.success(resultList);

    }


    @OperateLog(value="添加券别",type=OperateType.ADD)
    @ApiOperation(value = "添加券别")
    @ApiImplicitParam(name = "denomVO", value = "券别信息", required = true, dataType = "DenomVO" )
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
	@PostMapping("/save")
	public Result save(@RequestBody @Validated DenomVO denomVO) {

    	Denom denom = new Denom();

    	BeanUtils.copyProperties(denomVO, denom);
    	denom.setId(null);

		boolean save = denomService.save(denom);
    	if(save)
    		return Result.success();

    	return Result.fail("添加券别失败");

	}

    @OperateLog(value="修改券别",type=OperateType.EDIT)
    @ApiOperation(value = "修改券别")
    @ApiImplicitParam(name = "denomVO", value = "券别信息", required = false, dataType = "DenomVO" )
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
	@PostMapping("/update")
	public Result update(@RequestBody @Validated DenomVO denomVO) {
    	if (denomVO.getId() == null) {
            return Result.fail("券别Id不能为空");
        }

    	Denom denom = new Denom();

    	BeanUtils.copyProperties(denomVO, denom);

		boolean update = denomService.updateById(denom);
    	if(update)
    		return Result.success();

    	return Result.fail("修改券别失败");
	}


    @OperateLog(value="删除券别",type=OperateType.DELETE)
    @ApiOperation(value = "删除券别")
    @ApiImplicitParam(name = "id", value = "券别id", required = true, dataType = "Long")
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
	@PostMapping("/delete/{id}")
	public Result delete(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id) {

    	Denom denom = new Denom();
    	denom.setId(id);
    	denom.setDeleted(1);
		boolean update = denomService.updateById(denom);

		if(update)
    		return Result.success();

    	return Result.fail("删除券别失败");

	}

	@ApiOperation(value = "券别下拉选项")
	@ApiResponse(code = 0, message = "处理成功", response = Result.class)
	@GetMapping("/option")
	public Result option(){
    	List<Denom> denomList = denomService.getDenomByCondition(null);
    	List<Map> mapList = denomList.stream().map(item -> {
			HashMap map = new HashMap();
			map.put("id",item.getId());
			map.put("name", item.getName());
			map.put("value", item.getValue());
			map.put("version", item.getVersion());
			return map;
		}).collect(Collectors.toList());
    	return Result.success(mapList);
	}
}
