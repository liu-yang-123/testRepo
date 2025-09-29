package com.zcxd.gun.controller;

import com.zcxd.common.util.Result;
import com.zcxd.gun.service.server.GunLicenceServer;
import com.zcxd.gun.service.GunSecurityService;
import com.zcxd.gun.vo.GunSecurityQueryVO;
import com.zcxd.gun.vo.GunSecurityVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * @author zccc
 */

@RestController
@RequestMapping("/gun/security")
public class GunSecurityController {
    @Autowired
    GunLicenceServer gunLicenceServer;

    @Autowired
    GunSecurityService gunSecurityService;

    @PostMapping("/save")
    public Object save(GunSecurityVO gunSecurityVo, MultipartFile file) {
        return gunLicenceServer.saveGunSecurity(gunSecurityVo, file);
    }

    @PostMapping("/findListByPage")
    public Object findListByPage(@RequestParam Integer page,
                                 @RequestParam Integer limit,
                                 @Validated @RequestBody GunSecurityQueryVO gunSecurityQueryVo) throws IllegalAccessException {
        return gunSecurityService.findListByPage(page, limit, gunSecurityQueryVo);
    }

    @PostMapping("/update")
    public Object update(GunSecurityVO gunSecurityVo, MultipartFile file) {
        return gunLicenceServer.updateGunSecurityById(gunSecurityVo, file);
    }

    @PostMapping("/importSecurityInfo")
    public Object importSecurityInfo(@RequestParam("file") MultipartFile file, @RequestParam(value = "departmentId", required = true) Integer departmentId){
        try {
            return gunLicenceServer.gunSecurityExcelImport(file, departmentId);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    @PostMapping("/exportGunSecurityInfo")
    public void exportGunSecurityInfo(HttpServletResponse response, @RequestBody GunSecurityQueryVO queryVO) {
        gunSecurityService.exportGunLicenceInfo(response, queryVO);
    }
}
