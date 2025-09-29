package com.zcxd.base.controller;


import com.zcxd.base.dto.SchdCleanPassAuthDTO;
import com.zcxd.base.service.SchdBankAuthManageService;
import com.zcxd.base.vo.SchdCleanPassCodeVO;
import com.zcxd.common.annotation.OperateLog;
import com.zcxd.common.annotation.OperateType;
import com.zcxd.common.util.Result;
import com.zcxd.common.util.ResultList;
import io.swagger.annotations.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 * 清机通行证授权 前端控制器
 * </p>
 *
 * @author admin
 * @since 2021-07-05
 */
@RestController
@RequestMapping("/passAuth")
@Api(tags="排班管理-通行证备案")
public class SchdBankAuthManageController {

    @Resource
    SchdBankAuthManageService schdBankAuthManageService;

    @OperateLog(value="添加授权备案",type= OperateType.ADD)
    @ApiOperation(value = "添加授权备案")
    @ApiImplicitParam(name = "schdCleanPassCodeVO", value = "备案信息", required = true, dataType = "SchdCleanPassCodeVO")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/save")
    public Result save(@RequestBody @Validated SchdCleanPassCodeVO schdCleanPassCodeVO) {
        boolean bRet = schdBankAuthManageService.save(schdCleanPassCodeVO);
        return bRet? Result.success() : Result.fail();
    }

    @OperateLog(value="编辑授权备案",type= OperateType.EDIT)
    @ApiOperation(value = "编辑授权备案")
    @ApiImplicitParam(name = "schdCleanPassCodeVO", value = "备案信息", required = true, dataType = "SchdCleanPassCodeVO")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/update")
    public Result update(@RequestBody @Validated SchdCleanPassCodeVO schdCleanPassCodeVO) {
        boolean bRet = schdBankAuthManageService.update(schdCleanPassCodeVO);
        return bRet? Result.success() : Result.fail();
    }

    @OperateLog(value="删除授权备案",type= OperateType.DELETE)
    @ApiOperation(value = "删除授权备案")
    @ApiImplicitParam(name = "id", value = "备案id", required = true, dataType = "Long")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/delete/{id}")
    public Result delete(@PathVariable("id")  Long id){
        boolean bRet = schdBankAuthManageService.delete(id);
        return bRet? Result.success() : Result.fail();
    }

    @ApiOperation(value = "查询备案详情")
    @ApiImplicitParam(name = "id", value = "备案id", required = true, dataType = "Long")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/info/{id}")
    public Result getInfo(@PathVariable("id")  Long id){
        SchdCleanPassAuthDTO schdCleanPassAuthDTO = schdBankAuthManageService.getInfo(id);
        return Result.success(schdCleanPassAuthDTO);
    }

    @ApiOperation(value = "查询备案列表-分页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页数", required = true, dataType = "Integer ",defaultValue = "1"),
            @ApiImplicitParam(name = "limit", value = "显示数据条数", required = true, dataType = "Integer ",defaultValue = "10"),
            @ApiImplicitParam(name = "departmentId", value = "所属部门", required = true, dataType = "Long"),
            @ApiImplicitParam(name = "passType", value = "备案类型", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "bankId", value = "银行网点", required = false, dataType = "Long"),
            @ApiImplicitParam(name = "empId", value = "员工姓名", required = false, dataType = "String"),
    })
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @GetMapping("/list")
    public Result list(@RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @RequestParam Long departmentId,
                       @RequestParam Integer passType,
                       @RequestParam(required=false) Long bankId,
                       @RequestParam(required=false) Long empId) {
        ResultList resultList = schdBankAuthManageService.listPage(page,limit,departmentId,passType,bankId,empId);
        return Result.success(resultList);
    }
}

