package com.zcxd.gun.controller;

import com.zcxd.common.annotation.OperateLog;
import com.zcxd.common.annotation.OperateType;
import com.zcxd.common.util.Result;
import com.zcxd.gun.service.server.GunLicenceServer;
import com.zcxd.gun.service.GunLicenceService;
import com.zcxd.gun.vo.GunLicenceQueryVO;
import com.zcxd.gun.vo.GunLicenceVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * @author zccc
 */
@RestController
@RequestMapping("/gun/licence")
public class GunLicenceController {
    @Autowired
    GunLicenceServer gunLicenceServer;

    @Autowired
    GunLicenceService gunLicenceService;

    @PostMapping("/save")
    public Object save(GunLicenceVO gunLicenceVO, MultipartFile file) {
        return gunLicenceServer.saveGunLicence(gunLicenceVO, file);
    }

    @PostMapping("/findListByPage")
    public Object findListByPage(@RequestParam Integer page,
                                 @RequestParam Integer limit,
                                 @Validated @RequestBody GunLicenceQueryVO gunLicenceQueryVO) throws IllegalAccessException {
        return gunLicenceServer.findListByPage(page, limit, gunLicenceQueryVO);
    }

    @PostMapping("/update")
    public Object update(GunLicenceVO gunLicenceVO, MultipartFile file) {
        return gunLicenceServer.updateGunLicenceById(gunLicenceVO, file);
    }

    @OperateLog(value = "导入枪弹证信息", type= OperateType.ADD)
    @PostMapping("/importLicenceInfo")
    public Object importLicenceInfo(@RequestParam("file") MultipartFile file, @RequestParam(value = "departmentId", required = true) Integer departmentId){
        try {
            return gunLicenceServer.gunLicenceExcelImport(file, departmentId);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    @PostMapping("/exportGunLicenceInfo")
    public void exportGunLicenceInfo(HttpServletResponse response, @RequestBody GunLicenceQueryVO queryVO) {
        gunLicenceServer.exportGunLicenceInfo(response, queryVO);
    }
}
