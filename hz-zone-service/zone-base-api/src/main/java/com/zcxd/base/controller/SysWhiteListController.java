package com.zcxd.base.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.zcxd.base.vo.WhiteListVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zcxd.base.dto.WhiteListDTO;
import com.zcxd.base.service.SysWhiteListService;
import com.zcxd.common.annotation.OperateLog;
import com.zcxd.common.annotation.OperateType;
import com.zcxd.common.util.Result;
import com.zcxd.common.util.ResultList;
import com.zcxd.db.model.SysWhitelist;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;

/**
 * 
 * @ClassName SysWhiteListController
 * @Description 白名单管理控制器
 * @author 秦江南
 * @Date 2021年5月13日上午10:07:20
 */
@RestController
@RequestMapping("/whiteList")
@Api(tags = "白名单管理")
public class SysWhiteListController {

    @Autowired
    private SysWhiteListService whiteListService;
    
    @OperateLog(value="添加白名单",type=OperateType.ADD)
    @ApiOperation(value = "添加白名单")
    @PostMapping("/save")
	public Result save(@RequestBody WhiteListVO whiteListVO) {
    	//验证IP地址是否存在
    	SysWhitelist whitelist = new SysWhitelist();
        String ip = whiteListVO.getIpAddress();
        String mac = whiteListVO.getMacAddress();
		if (!StringUtils.isEmpty(ip)){
			boolean check = whiteListService.checkExist(ip, null, null);
			if(check){
				return Result.fail("该ip地址已存在！");
			}
		}
		//验证MAC地址是否存在
		if (!StringUtils.isEmpty(mac)){
			boolean check = whiteListService.checkExist(null, mac, null);
			if(check){
				return Result.fail("该mac地址已存在！");
			}
		}
		whitelist.setId(null);
		whitelist.setIpAddress(ip);
		whitelist.setMacAddress(mac);
		whitelist.setIpRemarks(whiteListVO.getIpRemarks());
		whiteListService.deleteWhiteListCache(); //淘汰缓存
		boolean save = whiteListService.save(whitelist);
    	if(save) {
			return Result.success();
		}
    	return Result.fail("添加白名单失败");
    }
    
    @OperateLog(value="修改白名单",type=OperateType.EDIT)
    @ApiOperation(value = "修改白名单")
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
	@PostMapping("/update")
	public Result update(@RequestBody WhiteListVO whiteListVO) {
    	Long id = whiteListVO.getId();
		String ip = whiteListVO.getIpAddress();
		String mac = whiteListVO.getMacAddress();
    	//验证IP地址
    	SysWhitelist whitelist = new SysWhitelist();
		if (!StringUtils.isEmpty(ip)){
			boolean check = whiteListService.checkExist(ip, null, id);
			if(check){
				return Result.fail("该ip地址已存在！");
			}
		}
		//验证MAC地址是否存在
		if (!StringUtils.isEmpty(mac)){
			boolean check = whiteListService.checkExist(null, mac, id);
			if(check){
				return Result.fail("该mac地址已存在！");
			}
		}
		whitelist.setId(id);
		whitelist.setIpAddress(ip);
		whitelist.setMacAddress(mac);
		whitelist.setIpRemarks(whiteListVO.getIpRemarks());
		whiteListService.deleteWhiteListCache(); //淘汰缓存
		boolean update = whiteListService.updateById(whitelist);
    	if(update) {
			return Result.success();
		}
    	return Result.fail("修改白名单失败");
	}
    

    @OperateLog(value="删除白名单", type=OperateType.DELETE)
    @ApiOperation(value = "删除白名单")
    @ApiImplicitParam(name = "id", value = "白名单id", required = true, dataType = "Long")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/delete/{id}")
    public Result delete(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){
    	SysWhitelist whitelist = new SysWhitelist();
    	whitelist.setId(id);
    	whitelist.setDeleted(1);
		whiteListService.deleteWhiteListCache(); //淘汰缓存
    	boolean update = whiteListService.updateById(whitelist);
    	if(update) {
			return Result.success();
		}
    	return Result.fail("删除白名单失败");
    }
    

    @ApiOperation(value = "白名单分页列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页数", required = true, dataType = "Integer ",defaultValue = "1"),
            @ApiImplicitParam(name = "limit", value = "显示数据条数", required = true, dataType = "Integer ",defaultValue = "10"),
            @ApiImplicitParam(name = "ipAddress", value = "ip地址", required = false, dataType = "String")
    })
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @GetMapping("/list")
    public Result list(@RequestParam(defaultValue = "1") Integer page,
                                 @RequestParam(defaultValue = "10") Integer limit,
                                 @RequestParam(required=false)  @Size(max=32,message="ipAddress最大长度为32") String ipAddress){
    	SysWhitelist whitelist = new SysWhitelist();
    	whitelist.setIpAddress(ipAddress);
    	IPage<SysWhitelist> whitelistPage = whiteListService.findListByPage(page, limit, whitelist);
 		List<WhiteListDTO> whitelistDTOList = whitelistPage.getRecords().stream().map(whitelistTmp->{
 			WhiteListDTO whitelistDTO = new WhiteListDTO();
 			BeanUtils.copyProperties(whitelistTmp, whitelistDTO);
 			return whitelistDTO;
 		}).collect(Collectors.toList());
    	ResultList resultList = ResultList.builder().total(whitelistPage.getTotal()).list(whitelistDTOList).build();
        return Result.success(resultList);
    }

    @PostMapping("/all")
    public Result all(){
		Set<String> set = whiteListService.queryAllWhiteIps(1);
		List<String> list = new ArrayList<>();
		if (set.size() > 0) {
			list.addAll(set);
		}
		return Result.success(list);
    }

	/**
	 * IP 或 mac 在白名单内可以通过
	 * @param ip
	 * @param macs
	 * @return
	 */
	@PostMapping("/check")
	public Result check(@RequestParam("ip") String ip, @RequestParam(value = "macs",required = false) String macs){
    	//验证IP白名单
		if (!StringUtils.isEmpty(ip)) {
			Set<String> ipSet = whiteListService.queryAllWhiteIps(1);
			if (ipSet != null) {
				if (ipSet.contains(ip)) {
					return Result.success();
				}
			}
		}
		//验证MAC白名单
		if (!StringUtils.isEmpty(macs)) {
			String[] macArray = StringUtils.split(macs,",");
			Set<String> macSet = whiteListService.queryAllWhiteIps(0);
			if (macSet != null && macArray != null) {
				for (int i = 0; i< macArray.length; i++) {
					if (macSet.contains(macArray[i])) {
						return Result.success();
					}
				}
			}
		}
		return Result.fail();
	}
}

