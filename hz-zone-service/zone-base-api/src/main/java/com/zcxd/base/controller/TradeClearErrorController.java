package com.zcxd.base.controller;

import com.zcxd.base.dto.TradeClearErrorDto;
import com.zcxd.base.service.BankService;
import com.zcxd.base.service.TradeClearErrorService;
import com.zcxd.base.vo.ClearErrorQueryVO;
import com.zcxd.common.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 *
 * 商业清分-清分差错 前端控制器
 * @author admin
 * @since 2020-09-16
 */
@Slf4j
@RestController
@RequestMapping("/clearError")
@Validated

public class TradeClearErrorController {

    @Resource
    private TradeClearErrorService tradeClearErrorService;
    
    @Resource
    private BankService bankService;

    /**
     * 添加差错记录(批量)
     * @param clearErrorDtoList
     * @return
     */
    @PostMapping("/addmulti")
    public Result addmulti(@RequestBody List<TradeClearErrorDto> clearErrorDtoList){
        if (clearErrorDtoList.size() == 0) {
            return Result.fail();
        }
        boolean bRet = tradeClearErrorService.addMulti(clearErrorDtoList);
        return bRet? Result.success() : Result.fail();
    }

    /**
     * 删除差错
     * @param id
     * @return
     */
    @PostMapping("/del/{id}")
    public Result delete(@PathVariable("id") Long id){
        boolean bRet = tradeClearErrorService.delete(id);
        return bRet? Result.success() : Result.fail();
    }

    /**
     * 更新清分差错
     * @param id
     * @param clearErrorDto
     * @return
     */
    @PostMapping("/update/{id}")
    public Result update(@PathVariable("id") Long id, @RequestBody TradeClearErrorDto clearErrorDto){
        boolean bRet = tradeClearErrorService.edit(id,clearErrorDto);
        return Result.success();
    }


    /**
     * 查询清分差错列表
     * @return
     */
    @PostMapping("/list")
    public Object findListByPage(@RequestBody @Validated ClearErrorQueryVO queryVO){
        Object object = tradeClearErrorService.findListByPage(queryVO.getPage(),
                queryVO.getLimit(),
                queryVO.getDepartmentId(),
                queryVO.getBankId(),
                queryVO.getDateBegin(),
                queryVO.getDateEnd(),
                queryVO.getDenomId(),
                queryVO.getFindName());
        return Result.success(object);
    }

    /**
     * 查询清分差错详情
     * @param id
     * @return
     */
    //@PostMapping("/info/{id}")
//    public Result findById(@PathVariable Long id){
//        return Result.success(tradeClearErrorService.info(id));
//    }

//    /**
//     * 确认差错(批量)
//     * @param idList
//     * @return
//     * @throws Exception
//     */
//    @PostMapping("/confirm/multi")
//    public Object confirmMulti(@RequestBody List<Long> idList) throws  Exception{
//        return tradeClearErrorService.confirmMulti(idList);
//    }

}
