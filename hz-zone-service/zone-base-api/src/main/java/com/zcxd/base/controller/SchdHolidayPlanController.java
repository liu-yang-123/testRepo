package com.zcxd.base.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zcxd.base.dto.SchdCleanPassAuthDTO;
import com.zcxd.base.service.SchdHolidayPlanService;
import com.zcxd.base.vo.SchdCleanPassCodeVO;
import com.zcxd.base.vo.SchdHolidayPlanVO;
import com.zcxd.common.annotation.OperateLog;
import com.zcxd.common.annotation.OperateType;
import com.zcxd.common.util.Result;
import com.zcxd.common.util.ResultList;
import com.zcxd.db.model.SchdHolidayPlan;
import io.swagger.annotations.*;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 放假，调休安排 前端控制器
 * </p>
 *
 * @author admin
 * @since 2021-07-05
 */
@RestController
@RequestMapping("/holiday")
@Api(tags="排班管理-放假调休计划")
public class SchdHolidayPlanController {

    @Resource
    SchdHolidayPlanService schdHolidayPlanService;

    @OperateLog(value="添加放假计划",type= OperateType.ADD)
    @ApiOperation(value = "添加放假计划")
    @ApiImplicitParam(name = "schdHolidayPlanVO", value = "备案信息", required = true, dataType = "SchdHolidayPlanVO")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/save")
    public Result save(@RequestBody @Validated SchdHolidayPlanVO schdHolidayPlanVO) {

        SchdHolidayPlan where = new SchdHolidayPlan();
        where.setPlanDay(schdHolidayPlanVO.getPlanDay());
        SchdHolidayPlan schdHolidayPlan = schdHolidayPlanService.getByCondition(where);
        if (schdHolidayPlan != null) {
            return Result.fail("不能重复添加");
        }
        SchdHolidayPlan newSchdHolidayPlan = new SchdHolidayPlan();
        BeanUtils.copyProperties(schdHolidayPlanVO,newSchdHolidayPlan);
        boolean bRet = schdHolidayPlanService.save(newSchdHolidayPlan);
        return bRet? Result.success() : Result.fail();
    }

    @OperateLog(value="编辑放假计划",type= OperateType.EDIT)
    @ApiOperation(value = "编辑放假计划")
    @ApiImplicitParam(name = "schdHolidayPlanVO", value = "备案信息", required = true, dataType = "SchdHolidayPlanVO")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/update")
    public Result update(@RequestBody @Validated SchdHolidayPlanVO schdHolidayPlanVO) {
        SchdHolidayPlan oldSchdHolidayPlan = schdHolidayPlanService.getById(schdHolidayPlanVO.getId());
        if (oldSchdHolidayPlan == null) {
            return Result.fail("无效参数id");
        }
        SchdHolidayPlan where = new SchdHolidayPlan();
        where.setPlanDay(schdHolidayPlanVO.getPlanDay());
        SchdHolidayPlan schdHolidayPlan = schdHolidayPlanService.getByCondition(where);
        if (schdHolidayPlan != null && schdHolidayPlan.getId().longValue() != oldSchdHolidayPlan.getId().longValue()) {
            return Result.fail("不能重复添加");
        }
        SchdHolidayPlan newSchdHolidayPlan = new SchdHolidayPlan();
        BeanUtils.copyProperties(schdHolidayPlanVO,newSchdHolidayPlan);
        boolean bRet = schdHolidayPlanService.updateById(newSchdHolidayPlan);
        return bRet? Result.success() : Result.fail();
    }

    @OperateLog(value="删除放假计划",type= OperateType.DELETE)
    @ApiOperation(value = "删除放假计划")
    @ApiImplicitParam(name = "id", value = "放假计划id", required = true, dataType = "Long")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/delete/{id}")
    public Result delete(@PathVariable("id")  Long id){
        SchdHolidayPlan oldSchdHolidayPlan = schdHolidayPlanService.getById(id);
        if (oldSchdHolidayPlan == null) {
            return Result.fail("无效参数id");
        }
        boolean bRet = schdHolidayPlanService.removeById(id);
        return bRet? Result.success() : Result.fail();
    }

//    @ApiOperation(value = "查询放假计划详情")
//    @ApiImplicitParam(name = "id", value = "放假计划id", required = true, dataType = "Long")
//    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
//    @PostMapping("/info/{id}")
//    public Result getInfo(@PathVariable("id")  Long id){
//        return Result.success(null);
//    }

    @ApiOperation(value = "查询放假计划列表-分页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页数", required = true, dataType = "Integer ",defaultValue = "1"),
            @ApiImplicitParam(name = "limit", value = "显示数据条数", required = true, dataType = "Integer ",defaultValue = "10"),
    })
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @GetMapping("/list")
    public Result list(@RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit) {

        IPage<SchdHolidayPlan> schdHolidayPlanIPage = schdHolidayPlanService.listPage(page,limit);

        List<SchdHolidayPlanVO> holidayPlanVOList = schdHolidayPlanIPage.getRecords().stream().map(schdHolidayPlan -> {
            SchdHolidayPlanVO schdHolidayPlanVO = new SchdHolidayPlanVO();
            BeanUtils.copyProperties(schdHolidayPlan,schdHolidayPlanVO);
            return schdHolidayPlanVO;
        }).collect(Collectors.toList());
        ResultList resultList = new ResultList.Builder().total(schdHolidayPlanIPage.getTotal()).list(holidayPlanVOList).build();
        return Result.success(resultList);
    }
}

