package com.zcxd.pda.service;

import com.alibaba.fastjson.JSONObject;
import com.zcxd.common.util.Result;
import com.zcxd.db.model.AppUpdateSetting;
import com.zcxd.pda.service.impl.AppUpdateSettingService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 
 * @ClassName AppSettingService
 * @Description app管理
 * @author shijin
 * @Date 2021年9月13日上午10:50:05
 */
@Service
public class AppSettingService {

    public static final int APP_MOBILE = 1;
    public static final int APP_PDA = 2;

    @Resource
    private AppUpdateSettingService appUpdateSettingService;

    /**
     * 检查版本升级
     * @param appType
     * @param curVer
     * @return
     */
    public Result checkAppUpdate(Integer appType, Integer curVer) {
        Long dateTime = System.currentTimeMillis();
        AppUpdateSetting updateSetting = appUpdateSettingService.getLatestVersion(dateTime,appType);
        JSONObject jsonObject = new JSONObject();
        if (updateSetting == null) {
            return Result.fail();
        }
        jsonObject.put("verNo",updateSetting.getVersionNumber());
        jsonObject.put("verName",updateSetting.getVersionName());
        jsonObject.put("describe",updateSetting.getDescription());
        jsonObject.put("url",updateSetting.getPackageUrl());
        jsonObject.put("forceUpdate",updateSetting.getForceUpdate());
        return Result.success(jsonObject);
    }

}
