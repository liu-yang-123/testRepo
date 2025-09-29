package com.zcxd.base.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zcxd.base.dto.DictionaryDTO;
import com.zcxd.base.service.SysDictionaryService;
import com.zcxd.base.vo.DictionaryVO;
import com.zcxd.common.annotation.OperateLog;
import com.zcxd.common.annotation.OperateType;
import com.zcxd.common.util.Result;
import com.zcxd.common.util.ResultList;
import com.zcxd.db.model.SysDictionary;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;

/**
 * 
 * @ClassName SysDictionaryController
 * @Description 数据字典控制器
 * @author 秦江南
 * @Date 2021年5月13日下午7:47:08
 */
@RestController
@RequestMapping("/dictionary")
@Api(tags="数据字典")
public class SysDictionaryController {


	@Autowired
    private SysDictionaryService dictionaryService;

    @OperateLog(value = "添加数据字典", type=OperateType.ADD)
    @ApiOperation(value = "添加数据字典")
    @ApiImplicitParam(name = "dictionaryVO", value = "数据字典", required = true, dataType = "DictionaryVO")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/save")
    public Result save(@RequestBody @Validated DictionaryVO dictionaryVO){
    	SysDictionary dictionary = new SysDictionary();
    	dictionary.setCode(dictionaryVO.getCode());
    	dictionary.setGroups(dictionaryVO.getGroups());
		List<SysDictionary> dictionaryList = dictionaryService.getDictionaryByCondition(dictionary);
		if(dictionaryList != null && dictionaryList.size()>0)
			return Result.fail("该类型编号已存在，请重新填写！");
		
		BeanUtils.copyProperties(dictionaryVO, dictionary);
		dictionary.setId(null);
		
		boolean save = dictionaryService.save(dictionary);
    	if(save)
    		return Result.success();
    	
    	return Result.fail("添加数据字典失败");
    }

    @OperateLog(value = "删除数据字典", type=OperateType.DELETE)
    @ApiOperation(value = "删除数据字典")
    @ApiImplicitParam(name = "id", value = "数据字典id", required = true, dataType = "Long")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/delete/{id}")
    public Result delete(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){
    	SysDictionary dictionary = new SysDictionary();
    	dictionary.setId(id);
    	dictionary.setDeleted(1);
    	boolean update = dictionaryService.updateById(dictionary);
    	
    	if(update)
    		return Result.success();
    	
    	return Result.fail("删除数据字典失败");
    }

    @OperateLog(value = "修改数据字典", type=OperateType.EDIT)
    @ApiOperation(value = "修改数据字典")
    @ApiImplicitParam(name = "dictionaryVO", value = "数据字典", required = true, dataType = "DictionaryVO")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/update")
    public Result update(@RequestBody @Validated DictionaryVO dictionaryVO){
    	if (dictionaryVO.getId() == null) {
            return Result.fail("数据字典Id不能为空");
        }
    	
    	SysDictionary dictionary = new SysDictionary();
    	dictionary.setCode(dictionaryVO.getCode());
    	dictionary.setGroups(dictionaryVO.getGroups());
		List<SysDictionary> dictionaryList = dictionaryService.getDictionaryByCondition(dictionary);
		if(dictionaryList != null && dictionaryList.size()>0)
			if(!dictionaryList.get(0).getId().equals(dictionaryVO.getId()))
				return Result.fail("该类型编号已存在，请重新填写！");
		
		
		BeanUtils.copyProperties(dictionaryVO, dictionary);

		boolean update = dictionaryService.updateById(dictionary);
    	if(update)
    		return Result.success();
    	
    	return Result.fail("修改数据字典失败");
    }

    @ApiOperation(value = "查询数据字典列表")
    @ApiImplicitParams({
    	@ApiImplicitParam(name = "page", value = "当前页数", required = true, dataType = "Integer",defaultValue = "1"),
        @ApiImplicitParam(name = "limit", value = "显示数据条数", required = true, dataType = "Integer",defaultValue = "10"),
        @ApiImplicitParam(name = "code", value = "编号", required = false, dataType = "String"),
        @ApiImplicitParam(name = "groups", value = "分类名称", required = false, dataType = "String"),
        @ApiImplicitParam(name = "content", value = "内容", required = false, dataType = "String"),
    })
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @GetMapping("/list")
    public Result findListByPage(@RequestParam Integer page,
                                   @RequestParam Integer limit,
                                   @RequestParam(required=false) @Size(max=32,message="code最大长度为32") String code,
                                   @RequestParam(required=false) @Size(max=32,message="groups最大长度为32") String groups,
                                   @RequestParam(required=false) @Size(max=32,message="content最大长度为32") String content){
    	SysDictionary dictionary = new SysDictionary();
    	dictionary.setCode(code);
    	dictionary.setGroups(groups);
    	dictionary.setContent(content);
    	IPage<SysDictionary> dictionaryPage = dictionaryService.findListByPage(page, limit, dictionary);
    	List<DictionaryDTO> dictionaryList = dictionaryPage.getRecords().stream().map(dictionaryTmp -> {
    		DictionaryDTO dictionaryDTO = new DictionaryDTO();
    		BeanUtils.copyProperties(dictionaryTmp, dictionaryDTO);
    		return dictionaryDTO;
    	}).collect(Collectors.toList());
    	
    	ResultList resultList = new ResultList.Builder().total(dictionaryPage.getTotal()).list(dictionaryList).build();
        return Result.success(resultList);
    }
    
    
    @ApiOperation(value = "查询指定组数据字典")
    @ApiImplicitParam(name = "groups", value = "分类名称", required = true, dataType = "String")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/group")
    public Result groups(@RequestParam @Size(max=32,message="groups最大长度为32") String groups){
    	SysDictionary dictionary = new SysDictionary();
    	dictionary.setGroups(groups);
    	
    	List<SysDictionary> dictionaryByCondition = dictionaryService.getDictionaryByCondition(dictionary);
    	List<Map<String,Object>> dictionaryOptionList = dictionaryByCondition.stream().map(dictionaryTmp->{
    		Map<String,Object> map = new HashMap<>(2);
    		map.put("code", dictionaryTmp.getCode());
    		map.put("content", dictionaryTmp.getContent());
    		return map;
    	}).collect(Collectors.toList());
    	return Result.success(dictionaryOptionList);
    }

}
