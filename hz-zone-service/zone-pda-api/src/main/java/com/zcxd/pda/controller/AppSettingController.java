package com.zcxd.pda.controller;

import com.zcxd.common.util.Result;
import com.zcxd.pda.service.AppSettingService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 
 * @ClassName SysUserController
 * @Description 用户管理控制器
 * @author shijin
 * @Date 2021年5月1日上午11:19:03
 */
@RequestMapping("/app")
@RestController
@Api(tags = "版本管理")
public class AppSettingController {

    @Autowired
    private AppSettingService appSettingService;

    @ApiOperation(value = "查询最新版本")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "appType", value = "应用类型", required = true, dataType = "Integer",dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "curVer", value = "当前版本号", required = true, dataType = "Integer",dataTypeClass = Integer.class)
    })
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @PostMapping("/check/version")
    public Result listVaultOrders(@RequestParam Integer appType,
                                  @RequestParam Integer curVer) {
        return appSettingService.checkAppUpdate(appType,curVer);
    }
}