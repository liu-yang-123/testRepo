package com.zcxd.base.controller;

import com.zcxd.base.dto.DenomValuesOptionDTO;
import com.zcxd.base.dto.TradeClearChargeRuleDTO;
import com.zcxd.base.service.DenomService;
import com.zcxd.base.service.TradeClearChargeRuleService;
import com.zcxd.base.util.DenomUtil;
import com.zcxd.base.util.EnumUtil;
import com.zcxd.base.vo.DenomVO;
import com.zcxd.base.vo.TradeClearChargeRuleVO;
import com.zcxd.common.constant.DenomAttrEnum;
import com.zcxd.common.constant.DenomTypeEnum;
import com.zcxd.common.constant.DenomVerFlagEnum;
import com.zcxd.common.util.Result;
import com.zcxd.db.mapper.DenomMapper;
import com.zcxd.db.model.Denom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * 商业清分-清分收费标准
 *
 *
 * @author admin
 * @since 2020-09-16
 */
@Slf4j
@RestController
@RequestMapping("/clear/charge/rule")
@Validated

public class TradeClearChargeRuleController {

    @Resource
    private TradeClearChargeRuleService tradeClearChargeRuleService;

    @Resource
    private DenomService denomService;
    /**
     * 添加标准
     * @param tradeClearChargeRuleVO
     * @return
     */
    @PostMapping("/add")
    public Result add(@RequestBody TradeClearChargeRuleVO tradeClearChargeRuleVO) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        if (!EnumUtil.isInclude(DenomAttrEnum.class,tradeClearChargeRuleVO.getAttr())) {
            return Result.fail("无效参数");
        }
        if (tradeClearChargeRuleVO.getGbFlag() != DenomTypeEnum.BAD.getValue()
        && tradeClearChargeRuleVO.getGbFlag() != DenomTypeEnum.GOOD.getValue()) {
            return Result.fail("无效参数");
        }
        return tradeClearChargeRuleService.create(tradeClearChargeRuleVO);
    }

    /**
     * 删除差错
     * @param id
     * @return
     */
    @PostMapping("/del/{id}")
    public Result delete(@PathVariable("id") int id){
        return tradeClearChargeRuleService.delete(id);
    }

    /**
     * 更新收费规则
     * @param tradeClearChargeRuleVO
     * @return
     */
    @PostMapping("/update")
    public Result update(@RequestBody TradeClearChargeRuleVO tradeClearChargeRuleVO){
        return tradeClearChargeRuleService.edit(tradeClearChargeRuleVO);
    }


    /**
     * 查询清分差错列表
     * @return
     */
    @PostMapping("/list")
    public Object findList(@RequestParam long bankId){
        List<TradeClearChargeRuleDTO> list = tradeClearChargeRuleService.listByBank(bankId);
        return Result.success(list);
    }

    /**
     * 面额选项
     * @return
     */
    @PostMapping("denomOption")
    public Result<List<DenomValuesOptionDTO>> denomOption() {
        Denom where = new Denom();
        where.setVersion(DenomVerFlagEnum.VER_NO.getValue());
        List<Denom> denomList = denomService.getDenomByCondition(where);

        Set<String> existSet = new HashSet<>();
        java.text.DecimalFormat decimalFormat = new java.text.DecimalFormat("#0.00");
        List<DenomValuesOptionDTO> denomOptions = new ArrayList<>();

        for (Denom denom : denomList) {
            String values = decimalFormat.format(denom.getValue());
            String key = denom.getAttr()+values;
            if (!existSet.contains(key)) {
                existSet.add(key);
                DenomValuesOptionDTO option = new DenomValuesOptionDTO();
                option.setValue(values);
                String denomName = DenomUtil.denomValueToName(values,denom.getAttr().equals(DenomAttrEnum.COIN.getValue()));
                option.setLabel(denomName);
                option.setType(denom.getAttr());
                denomOptions.add(option);
            }
        }
        return Result.success(denomOptions);
    }
}
