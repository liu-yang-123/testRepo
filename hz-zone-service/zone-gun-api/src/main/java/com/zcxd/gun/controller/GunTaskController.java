package com.zcxd.gun.controller;

import com.zcxd.common.util.Result;
import com.zcxd.gun.service.server.GunTaskOperateServer;
import com.zcxd.gun.service.GunTaskService;
import com.zcxd.gun.vo.GunTaskIssueAndTakedownQueryVO;
import com.zcxd.gun.vo.GunTaskQueryVO;
import com.zcxd.gun.vo.GunTaskVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * @author zccc
 */
@RestController
@RequestMapping("/guntask")
public class GunTaskController {
    @Autowired
    GunTaskService guntaskService;

    @Autowired
    GunTaskOperateServer gunTaskOperateServer;

    @PostMapping("/save")
    public Object save(@Validated @RequestBody GunTaskVO gunTaskVo) {
        return gunTaskOperateServer.saveGunTask(gunTaskVo);
    }

    @PostMapping("/update")
    public Object update(@RequestBody GunTaskVO gunTaskVo) {
        return gunTaskOperateServer.updateGunTask(gunTaskVo);
    }

    @PostMapping("/list")
    public Result findListByPage(@RequestParam(defaultValue = "1") Integer page,
                                 @RequestParam(defaultValue = "10") Integer limit,
                                 @RequestBody GunTaskQueryVO queryVo) {
        return gunTaskOperateServer.findListByPage(page, limit, queryVo);
    }

    @PostMapping("/issueGun")
    public Result issueGun(@Validated @RequestBody GunTaskIssueAndTakedownQueryVO queryVo) {
        return gunTaskOperateServer.issueGun(queryVo);
    }

    @PostMapping("/takedownGun")
    public Result takedownGun(@Validated @RequestBody GunTaskIssueAndTakedownQueryVO queryVo) {
        return gunTaskOperateServer.takedownGun(queryVo);
    }

    @PostMapping("/exportGunInfo")
    public void exportGunInfo(HttpServletResponse response, @RequestBody GunTaskQueryVO gunTaskQueryVO) {
        gunTaskOperateServer.exportGunInfo(response, gunTaskQueryVO);
    }

}
