package com.zcxd.pda.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.db.mapper.AppUpdateSettingMapper;
import com.zcxd.db.mapper.CashboxPackRecordMapper;
import com.zcxd.db.model.AppUpdateSetting;
import com.zcxd.db.model.CashboxPackRecord;
import com.zcxd.pda.dto.AtmClearBoxCountDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 
 * @ClassName AppUpdateSettingService
 * @Description APP远程升级服务
 * @author shijin
 * @Date 2021年9月13日上午14:50:05
 */
@Service
public class AppUpdateSettingService extends ServiceImpl<AppUpdateSettingMapper, AppUpdateSetting> {

    public AppUpdateSetting getLatestVersion(Long curDate,Integer appType) {
        QueryWrapper<AppUpdateSetting> queryWrapper = new QueryWrapper<>();
        queryWrapper.le("app_update_time",curDate)
                .eq("app_type",appType)
                .eq("status_t",1)
                .orderByDesc("version_number");
        List<AppUpdateSetting> list = baseMapper.selectList(queryWrapper);
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }
}
