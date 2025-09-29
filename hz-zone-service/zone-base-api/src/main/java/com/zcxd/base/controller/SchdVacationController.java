package com.zcxd.base.controller;


import com.zcxd.base.dto.SchdVacatePlanDTO;
import com.zcxd.base.dto.SchdVacateSettingDTO;
import com.zcxd.base.service.SchdVacationService;
import com.zcxd.base.vo.SchdVacateAdjustVO;
import com.zcxd.base.vo.SchdVacateSettingItemVO;
import com.zcxd.base.vo.SchdVacateSettingVO;
import com.zcxd.base.vo.SchdVacatePlanVO;
import com.zcxd.common.annotation.OperateLog;
import com.zcxd.common.annotation.OperateType;
import com.zcxd.common.util.Result;
import com.zcxd.common.util.ResultList;
import io.swagger.annotations.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * <p>
 * 员工休息管理 前端控制器
 * </p>
 *
 * @author admin
 * @since 2021-07-05
 */
@RestController
@RequestMapping("/vacation")
@Api(tags = "排班管理-员工休息管理")
public class SchdVacationController {

    @Resource
    SchdVacationService schdVacationService;

    @OperateLog(value="添加休息计划",type= OperateType.ADD)
    @ApiOperation(value = "添加休息计划")
    @PostMapping({"/plan/save","/guard/plan/save"})
    public Result savePlan(@RequestBody @Validated SchdVacatePlanVO schdVacatePlanVO){
        schdVacationService.savePlan(schdVacatePlanVO);
        return Result.success();
    }

    @OperateLog(value="编辑休息计划",type= OperateType.EDIT)
    @ApiOperation(value = "编辑休息计划")
    @PostMapping({"/plan/update","/guard/plan/update"})
    public Result updatePlan(@RequestBody @Validated SchdVacatePlanVO schdVacatePlanVO){
        schdVacationService.updatePlan(schdVacatePlanVO);
        return Result.success();
    }

    @OperateLog(value="删除休息计划",type= OperateType.DELETE)
    @ApiOperation(value = "删除休息计划")
    @ApiImplicitParam(name = "id", value = "计划id", required = true, dataType = "Long", dataTypeClass = Long.class)
    @PostMapping({"/plan/delete/{id}","/guard/plan/deleted/{id}"})
    public Result deletePlan(@PathVariable("id")  Long id){
        schdVacationService.deletePlan(id);
        return Result.success();
    }

    @ApiOperation(value = "查询休息计划详情")
    @ApiImplicitParam(name = "id", value = "计划id", required = true, dataType = "Long", dataTypeClass = Long.class)
    @PostMapping({"/plan/info/{id}","/guard/plan/info/{id}"})
    public Result getPlan(@PathVariable("id")  Long id){
        SchdVacatePlanDTO vacatePlanDTO = schdVacationService.getPlan(id);
        return Result.success(vacatePlanDTO);
    }

    @ApiOperation(value = "查询休息计划列表-分页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页数", required = true, dataType = "Integer",defaultValue = "1", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "limit", value = "显示数据条数", required = true, dataType = "Integer",defaultValue = "10", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "departmentId", value = "部门id", required = true, dataType = "Long", dataTypeClass = Long.class),
            @ApiImplicitParam(name = "planType", value = "计划类别", required = true, dataType = "Integer", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "name", value = "计划名称", required = false, dataType = "String", dataTypeClass = String.class),
    })
    @GetMapping({"/plan/list","/guard/plan/list"})
    public Result listPlan(@RequestParam(defaultValue = "1") Integer page,
                           @RequestParam(defaultValue = "10") Integer limit,
                           @RequestParam Long departmentId,
                           @RequestParam Integer planType,
                           @RequestParam(required=false) @Size(max=32,message="计划名称过长") String name) {
        ResultList resultList = schdVacationService.listPagePlan(page,limit,departmentId,planType,name);
        return Result.success(resultList);
    }

    @ApiOperation(value = "查询休息计划列表-all")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "departmentId", value = "部门id", required = true, dataType = "Long", dataTypeClass = Long.class),
            @ApiImplicitParam(name = "planType", value = "计划类别", required = true, dataType = "Integer", dataTypeClass = Integer.class),
    })
    @GetMapping({"/plan/all","/guard/plan/all"})
    public Result listAllPlan(@RequestParam Long departmentId,@RequestParam Integer planType) {
        List<SchdVacatePlanVO> list = schdVacationService.listAllPlan(departmentId,planType);
        return Result.success(list);
    }

    @OperateLog(value="添加员工休息",type= OperateType.ADD)
    @ApiOperation(value = "添加员工休息")
    @PostMapping({"/setting/save","/guard/setting/save"})
    public Result saveSetting(@RequestBody @Validated SchdVacateSettingVO schdVacateSettingVO){
        schdVacationService.saveSetting(schdVacateSettingVO);
        return Result.success();
    }

    @OperateLog(value="编辑员工休息",type= OperateType.EDIT)
    @ApiOperation(value = "编辑员工休息")
    @PostMapping({"/setting/update","/guard/setting/update"})
    public Result updateSetting(@RequestBody @Validated SchdVacateSettingItemVO schdVacateSettingItemVO){
        schdVacationService.updateSetting(schdVacateSettingItemVO);
        return Result.success();
    }

    @OperateLog(value="删除休息设置",type= OperateType.DELETE)
    @ApiOperation(value = "删除休息设置")
    @ApiImplicitParam(name = "id", value = "计划id", required = true, dataType = "Long", dataTypeClass = Long.class)
    @PostMapping({"/setting/delete/{id}","/guard/setting/delete/{id}"})
    public Result deleteSeting(@PathVariable("id")  Long id){
        schdVacationService.deleteSetting(id);
        return Result.success();
    }

    @ApiOperation(value = "查询休息计划详情")
    @ApiImplicitParam(name = "id", value = "计划id", required = true, dataType = "Long", dataTypeClass = Long.class)
    @PostMapping({"/setting/info/{id}","/guard/setting/info/{id}"})
    public Result getSetting(@PathVariable("id")  Long id){
        SchdVacateSettingDTO vacateSettingDTO = schdVacationService.getSetting(id);
        return Result.success(vacateSettingDTO);
    }

    @ApiOperation(value = "员工休息配置列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页数", required = true, dataType = "Integer ",defaultValue = "1", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "limit", value = "显示数据条数", required = true, dataType = "Integer ",defaultValue = "10", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "planId", value = "计划id", required = true, dataType = "Long", dataTypeClass = Long.class),
            @ApiImplicitParam(name = "weekday", value = "星期几",  dataType = "Integer", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "jobType", value = "岗位类型", dataType = "Integer", dataTypeClass = Integer.class)
    })
    @GetMapping({"/setting/list","/guard/setting/list"})
    public Result listSetting(@RequestParam(defaultValue = "1") Integer page,
                              @RequestParam(defaultValue = "10") Integer limit,
                              @RequestParam Long planId,
                              Integer weekday,
                              @RequestParam(required = false)  Integer jobType) {
        ResultList resultList = schdVacationService.listPageSetting(page,limit,planId,jobType, weekday);
        return Result.success(resultList);
    }

    @ApiOperation(value = "统计每日员工上班人数")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "planId", value = "计划id", required = true, dataType = "Long", dataTypeClass = Long.class),
            @ApiImplicitParam(name = "jobType", value = "岗位类型", required = false, dataType = "Integer", dataTypeClass = Integer.class)
    })
    @GetMapping({"/setting/sum","/guard/setting/sum"})
    public Result sumSetting(@RequestParam Long planId,@RequestParam(required = false) Integer jobType) {
        Object object = schdVacationService.sumSetting(planId,jobType);
        return Result.success(object);
    }

    /***************************************************************************************
     *
     * 分割线
     *
     ***************************************************************************************/

    @OperateLog(value="添加调整计划",type= OperateType.ADD)
    @ApiOperation(value = "添加调整计划")
    @PostMapping({"/adjust/save","/guard/adjust/save"})
    public Result saveAdjust(@RequestBody @Validated SchdVacateAdjustVO schdVacateAdjustVO){
        boolean bRet = schdVacationService.saveAdjust(schdVacateAdjustVO);
        return bRet? Result.success() : Result.fail();
    }

    @OperateLog(value="编辑调整计划",type= OperateType.EDIT)
    @ApiOperation(value = "编辑调整计划")
    @PostMapping({"/adjust/update","/guard/adjust/update"})
    public Result updateAdjust(@RequestBody @Validated SchdVacateAdjustVO schdVacateAdjustVO){
        boolean bRet = schdVacationService.updateAdjust(schdVacateAdjustVO);
        return bRet? Result.success() : Result.fail();
    }

    @OperateLog(value="删除调整计划",type= OperateType.DELETE)
    @ApiOperation(value = "删除调整计划")
    @ApiImplicitParam(name = "id", value = "调整计划id", required = true, dataType = "Long", dataTypeClass = Long.class)
    @PostMapping({"/adjust/delete/{id}","/guard/adjust/delete/{id}"})
    public Result deleteAdjust(@PathVariable("id")  Long id){
        boolean bRet = schdVacationService.deleteAdjust(id);
        return bRet? Result.success() : Result.fail();
    }

    @ApiOperation(value = "员工休息调整列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页数", required = true, dataType = "Integer",defaultValue = "1", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "limit", value = "显示数据条数", required = true, dataType = "Integer",defaultValue = "10", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "departmentId", value = "部门id", required = true, dataType = "Long", dataTypeClass = Long.class),
            @ApiImplicitParam(name = "planType", value = "计划类别", required = true, dataType = "Integer", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "adjustDate", value = "日期", dataType = "String", dataTypeClass = String.class),
            @ApiImplicitParam(name = "adjustType", value = "岗位类型",  dataType = "Integer", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "name", value = "员工姓名", dataType = "String", dataTypeClass = String.class)
    })
    @GetMapping({"/adjust/list","/guard/adjust/list"})
    public Result listAdjust(@RequestParam(defaultValue = "1") Integer page,
                              @RequestParam(defaultValue = "10") Integer limit,
                              @RequestParam Long departmentId,
                              @RequestParam Integer planType,
                              @RequestParam(required = false)  String adjustDate,
                              @RequestParam(required = false)  Integer adjustType,
                              @RequestParam(required = false)  String name) {
        ResultList resultList = schdVacationService.listPageAdjust(page,limit,departmentId,planType,adjustDate,adjustType,name);
        return Result.success(resultList);
    }
}

