package com.zcxd.gun.controller;

import com.zcxd.common.annotation.OperateLog;
import com.zcxd.common.annotation.OperateType;
import com.zcxd.common.util.Result;
import com.zcxd.gun.service.server.GunServer;
import com.zcxd.gun.service.GunService;
import com.zcxd.gun.vo.GunQueryVO;
import com.zcxd.gun.vo.GunStatisticsVO;
import com.zcxd.gun.vo.GunVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * @author zccc
 */
@RestController
@RequestMapping("/gun")
@Validated
public class GunController {
    @Autowired
    private GunService gunService;

    @Autowired
    private GunServer gunServer;

    @OperateLog(value = "新增枪弹", type = OperateType.ADD)
    @PostMapping("/save")
    public Object save(@RequestBody GunVO gunVo) {
        return gunServer.saveGun(gunVo);
    }

    @PostMapping("/list")
    public Object findListByPage(@RequestParam Integer page,
                                 @RequestParam Integer limit,
                                 @RequestBody GunQueryVO gunQueryVo) throws IllegalAccessException {
        return gunService.findListByPage(page, limit, gunQueryVo);
    }

    @OperateLog(value = "修改枪弹", type=OperateType.EDIT)
    @PostMapping("/update")
    public Object update(@RequestBody GunVO gunVo){
        return gunServer.updateGunById(gunVo);
    }

    @PostMapping("/statistics")
    public Object statisticsGun(@RequestBody GunStatisticsVO statisticsVo) {
        return gunService.gunCategoryStatistics(statisticsVo);
    }

    @OperateLog(value = "报废枪")
    @PostMapping("/stop")
    public Object stop(@RequestBody GunVO gunVO){
        return gunServer.gunStopOperate(gunVO);
    }

    @PostMapping("/check")
    public Object check(@RequestBody GunVO gunVO){
        return gunServer.gunCheckOperate(gunVO);
    }

    @PostMapping("/clean")
    public Object clean(@RequestBody GunVO gunVO){
        return gunServer.gunCleanOperate(gunVO);
    }

    @OperateLog(value = "导入枪弹信息", type=OperateType.ADD)
    @PostMapping("/exportGunInfo")
    public void exportGunInfo(HttpServletResponse response, @RequestBody GunQueryVO gunQueryVo) {
        gunService.exportGunInfo(response, gunQueryVo);
    }

    @OperateLog(value = "导入枪弹信息", type=OperateType.ADD)
    @PostMapping("/importGunInfo")
    public Object importGunInfo(@RequestParam("file") MultipartFile file, @RequestParam(value = "departmentId") Integer departmentId){
        try {
            return gunServer.gunExcelImport(file, departmentId);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }
}
