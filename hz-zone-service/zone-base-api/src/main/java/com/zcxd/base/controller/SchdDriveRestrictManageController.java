package com.zcxd.base.controller;


import com.zcxd.base.service.SchdDriveRestrictManageService;
import com.zcxd.base.vo.SchdDriveRestrictVO;
import com.zcxd.base.vo.SchdDriveRestrictXhVO;
import com.zcxd.common.annotation.OperateLog;
import com.zcxd.common.annotation.OperateType;
import com.zcxd.common.util.Result;
import com.zcxd.common.util.ResultList;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import javax.annotation.Resource;

/**
 * <p>
 * 汽车尾号限行规则 前端控制器
 * </p>
 *
 * @author admin
 * @since 2021-07-05
 */
@RestController
@RequestMapping("/driving")
@Api(tags="排班管理-车辆限行管理")
public class SchdDriveRestrictManageController {

    @Resource
    SchdDriveRestrictManageService schdDriveRestrictManageService;

    @OperateLog(value="添加限行规则",type= OperateType.ADD)
    @ApiOperation(value = "添加限行规则")
    @ApiImplicitParam(name = "schdDriveRestrictVO", value = "限行规则", required = true, dataType = "SchdDriveRestrictVO")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/restrict/save")
    public Result saveRestrict(@RequestBody SchdDriveRestrictVO schdDriveRestrictVO) {

        boolean bRet = schdDriveRestrictManageService.saveRestrict(schdDriveRestrictVO);
        return bRet? Result.success() : Result.fail();
    }

    @OperateLog(value="编辑限行规则",type= OperateType.EDIT)
    @ApiOperation(value = "编辑限行规则")
    @ApiImplicitParam(name = "schdDriveRestrictVO", value = "限行规则", required = true, dataType = "SchdDriveRestrictVO")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/restrict/update")
    public Result updateRestrict(@RequestBody SchdDriveRestrictVO schdDriveRestrictVO) {

        boolean bRet = schdDriveRestrictManageService.updateRestrict(schdDriveRestrictVO);
        return bRet? Result.success() : Result.fail();
    }

    @OperateLog(value="删除限行规则",type= OperateType.DELETE)
    @ApiOperation(value = "删除限行规则")
    @ApiImplicitParam(name = "id", value = "限行规则Id", required = true, dataType = "Long")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/restrict/delete/{id}")
    public Result deleteRestrict(@PathVariable("id")  Long id){
        boolean bRet = schdDriveRestrictManageService.deleteRestrict(id);
        return bRet? Result.success() : Result.fail();
    }

    @ApiOperation(value = "查询限行规则列表-分页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页数", required = true, dataType = "Integer ",defaultValue = "1"),
            @ApiImplicitParam(name = "limit", value = "显示数据条数", required = true, dataType = "Integer ",defaultValue = "10"),
            @ApiImplicitParam(name = "departmentId", value = "所属部门", required = true, dataType = "Long"),
    })
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @GetMapping("/restrict/list")
    public Result list(@RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @RequestParam Long departmentId) {


        ResultList resultList = schdDriveRestrictManageService.listRestrict(page,limit,departmentId);
        return Result.success(resultList);
    }


    /********************************************************************************************
     *
     *  以下为西湖限行
     *
     ********************************************************************************************/


    @OperateLog(value="添加特殊限行规则",type= OperateType.ADD)
    @ApiOperation(value = "添加特殊限行规则")
    @ApiImplicitParam(name = "schdDriveRestrictXhVO", value = "限行规则", required = true, dataType = "SchdDriveRestrictXhVO")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/restrictXh/save")
    public Result saveRestrictXh(@RequestBody SchdDriveRestrictXhVO schdDriveRestrictXhVO) {

        boolean bRet = schdDriveRestrictManageService.saveRestrictXh(schdDriveRestrictXhVO);
        return bRet? Result.success() : Result.fail();
    }

    @OperateLog(value="编辑特殊限行规则",type= OperateType.EDIT)
    @ApiOperation(value = "编辑特殊限行规则")
    @ApiImplicitParam(name = "schdDriveRestrictXhVO", value = "限行规则", required = true, dataType = "SchdDriveRestrictXhVO")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/restrictXh/update")
    public Result updateRestrictXh(@RequestBody SchdDriveRestrictXhVO schdDriveRestrictXhVO) {

        boolean bRet = schdDriveRestrictManageService.updateRestrictXh(schdDriveRestrictXhVO);
        return bRet? Result.success() : Result.fail();
    }

    @OperateLog(value="删除特殊限行规则",type= OperateType.DELETE)
    @ApiOperation(value = "删除特殊限行规则")
    @ApiImplicitParam(name = "id", value = "限行规则Id", required = true, dataType = "Long")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/restrictXh/delete/{id}")
    public Result deleteRestrictXh(@PathVariable("id")  Long id){
        boolean bRet = schdDriveRestrictManageService.deleteRestrictXh(id);
        return bRet? Result.success() : Result.fail();
    }

    @ApiOperation(value = "查询特殊限行规则列表-分页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页数", required = true, dataType = "Integer ",defaultValue = "1"),
            @ApiImplicitParam(name = "limit", value = "显示数据条数", required = true, dataType = "Integer ",defaultValue = "10"),
            @ApiImplicitParam(name = "departmentId", value = "所属部门", required = true, dataType = "Long"),
    })
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @GetMapping("/restrictXh/list")
    public Result listXh(@RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @RequestParam Long departmentId) {


        ResultList resultList = schdDriveRestrictManageService.listRestrictXh(page,limit,departmentId);
        return Result.success(resultList);
    }

}

