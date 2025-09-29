package com.zcxd.base.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zcxd.base.dto.VaultVolumeDenomDto;
import com.zcxd.base.dto.VaultVolumeDto;
import com.zcxd.base.service.BankService;
import com.zcxd.base.service.DenomService;
import com.zcxd.base.service.VaultVolumeService;
import com.zcxd.common.util.Result;
import com.zcxd.common.util.ResultList;
import com.zcxd.db.model.Bank;
import com.zcxd.db.model.Denom;
import com.zcxd.db.model.VaultVolum;
import io.swagger.annotations.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author songanwei
 * @date 2021-05-29
 */
@Api(tags = "金库-库存查询")
@RequestMapping("/vaultVolume")
@RestController
public class VaultVolumeController {

    @Autowired
    private VaultVolumeService volumeService;

    @Autowired
    private DenomService denomService;

    @Autowired
    private BankService bankService;

    @ApiOperation(value = "库存记录列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页数", required = true, dataType = "Integer ",defaultValue = "1",dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "limit", value = "显示数据条数", required = true, dataType = "Integer ",defaultValue = "10",dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "departmentId", value = "部门ID", required = true, dataType = "Long", defaultValue = "0",dataTypeClass = Long.class),
            @ApiImplicitParam(name = "bankId", value = "银行机构ID", dataType = "Long", defaultValue = "0", dataTypeClass = Long.class)
    })
    @GetMapping("/list")
    public Result list(@RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @RequestParam Long departmentId,
                       @RequestParam(name = "bankId",defaultValue = "0") Long bankId){
        VaultVolum vaultVolum = new VaultVolum();
        vaultVolum.setDepartmentId(departmentId);
        vaultVolum.setBankId(bankId);
        IPage<VaultVolum> vaultVolumPage = volumeService.findListByPage(page,limit,vaultVolum);
        List<Bank> bankList = bankService.list();
        List<Denom> denomList = denomService.list();
        Map bankMap = bankList.stream().collect(Collectors.toMap(Bank::getId,Bank::getFullName));
        Map denomMap = denomList.stream().collect(Collectors.toMap(Denom::getId,Denom::getName));
        List<VaultVolumeDto>  volumeDtoList = vaultVolumPage.getRecords().stream().map(t -> {
            VaultVolumeDto vaultVolumeDto = new VaultVolumeDto();
            BeanUtils.copyProperties(t,vaultVolumeDto);
            vaultVolumeDto.setBankName(Optional.ofNullable(bankMap.get(t.getBankId())).orElse("").toString());
            return vaultVolumeDto;
        }).collect(Collectors.toList());

        ResultList resultList = ResultList.builder().total(vaultVolumPage.getTotal()).list(volumeDtoList).build();
        return Result.success(resultList);
    }

    @ApiOperation(value = "库存银行列表")
    @GetMapping("/bank")
    public Result bank(Long bankId){
        List<VaultVolum> volumList = volumeService.getBankList(bankId);
        volumList = volumList.stream().filter(t -> t.getCount() != 0L || t.getAmount().compareTo(BigDecimal.ZERO) != 0).collect(Collectors.toList());
        return Result.success(volumList);
    }

    @ApiOperation(value = "库存银行券别列表")
    @GetMapping("/bankDenom")
    public Result bankDenom(Long bankId){
        List<Denom> denomList = denomService.list();
        Map denomMap = denomList.stream().collect(Collectors.toMap(Denom::getId,Denom::getName));
        List<VaultVolum> volumList = volumeService.getBankList(bankId);
        Map<Integer,List<VaultVolum>> denomTypeMap = volumList.stream().collect(Collectors.groupingBy(VaultVolum::getDenomType));

        HashMap map = new HashMap();
        denomTypeMap.entrySet().stream().forEach(entry -> {
            List<VaultVolum> volumList1 = entry.getValue();
            List<VaultVolumeDenomDto> newVolumList = volumList1.stream()
                    .filter(s -> (s.getAmount().compareTo(BigDecimal.ZERO) != 0 || s.getCount() != 0))
                    .map(s -> {
                        VaultVolumeDenomDto vaultVolumeDenomDto = new VaultVolumeDenomDto();
                        vaultVolumeDenomDto.setDenomId(s.getDenomId());
                        vaultVolumeDenomDto.setAmount(s.getAmount());
                        vaultVolumeDenomDto.setDenomType(s.getDenomType());
                        vaultVolumeDenomDto.setCount(s.getCount());
                        vaultVolumeDenomDto.setDenomName((String) Optional.ofNullable(denomMap.get(s.getDenomId())).orElse(""));
                        return vaultVolumeDenomDto;
                    }).collect(Collectors.toList());
            map.put(entry.getKey(), newVolumList);
        });

        return Result.success(map);
    }
}
