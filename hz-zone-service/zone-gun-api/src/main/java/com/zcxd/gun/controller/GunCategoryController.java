package com.zcxd.gun.controller;

import com.zcxd.common.annotation.OperateLog;
import com.zcxd.common.annotation.OperateType;
import com.zcxd.gun.service.GunCategoryService;
import com.zcxd.gun.vo.GunCategoryQueryVO;
import com.zcxd.gun.vo.GunCategoryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author zccc
 */
@RestController
@RequestMapping("/gun/category")
public class GunCategoryController {
    @Autowired
    GunCategoryService gunCategoryService;

    @PostMapping("/save")
    public Object save(@Validated @RequestBody GunCategoryVO gunCategoryVo) {
        return gunCategoryService.saveGunCategory(gunCategoryVo);
    }

    @PostMapping("/list")
    public Object findListByPage(@RequestParam Integer page,
                                 @RequestParam Integer limit,
                                 @Validated @RequestBody GunCategoryQueryVO queryVo) throws IllegalAccessException {
        return gunCategoryService.findListByPage(page, limit, queryVo);
    }

    @OperateLog(value = "修改枪支类型", type= OperateType.EDIT)
    @PostMapping("/update")
    public Object update(@RequestBody GunCategoryVO gunCategoryVo){
        return gunCategoryService.updateGunById(gunCategoryVo);
    }
}
