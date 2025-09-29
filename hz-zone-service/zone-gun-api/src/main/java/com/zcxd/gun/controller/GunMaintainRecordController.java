package com.zcxd.gun.controller;

import com.zcxd.gun.service.GunMaintainRecordService;
import com.zcxd.gun.vo.GunMaintainRecordQueryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author zccc
 */
@RestController
@RequestMapping("/gun/maintain")
public class GunMaintainRecordController {
    @Autowired
    GunMaintainRecordService gunMaintainRecordService;

    @PostMapping("/list")
    public Object findListByPage(@RequestParam Integer page,
                                 @RequestParam Integer limit,
                                 @RequestBody GunMaintainRecordQueryVO queryVo) throws IllegalAccessException {
        return gunMaintainRecordService.findListByPage(page, limit, queryVo);
    }
}
