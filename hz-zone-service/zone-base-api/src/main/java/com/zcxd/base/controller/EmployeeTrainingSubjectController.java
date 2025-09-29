package com.zcxd.base.controller;

import java.util.List;
import java.util.stream.Collectors;

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
import com.zcxd.base.dto.EmployeeTrainingSubjectDTO;
import com.zcxd.base.service.EmployeeTrainingSubjectService;
import com.zcxd.base.vo.EmployeeTrainingSubjectVO;
import com.zcxd.common.annotation.OperateLog;
import com.zcxd.common.annotation.OperateType;
import com.zcxd.common.util.Result;
import com.zcxd.common.util.ResultList;
import com.zcxd.db.model.EmployeeTrainingSubject;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;

/**
 * 
 * @ClassName EmployeeTrainingSubjectController
 * @Description 员工培训主题前端控制器
 * @author 秦江南
 * @Date 2021年5月18日下午3:14:04
 */
@RestController
@RequestMapping("/subjects")
@Api(tags = "员工培训主题")
public class EmployeeTrainingSubjectController {


	@Autowired
    private EmployeeTrainingSubjectService employeeTrainingSubjectService;


	@OperateLog(value="添加培训主题", type=OperateType.ADD)
    @ApiOperation(value = "添加培训主题")
    @ApiImplicitParam(name = "employeeTrainingSubjectVO", value = "培训主题", required = true, dataType = "EmployeeTrainingSubjectVO")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/save")
    public Result save(@RequestBody @Validated EmployeeTrainingSubjectVO employeeTrainingSubjectVO){
    	EmployeeTrainingSubject employeeTrainingSubject = new EmployeeTrainingSubject();
    	BeanUtils.copyProperties(employeeTrainingSubjectVO, employeeTrainingSubject);
    	
    	employeeTrainingSubject.setId(null);
		
		boolean save = employeeTrainingSubjectService.save(employeeTrainingSubject);
    	if(save)
    		return Result.success();
    	
    	return Result.fail("添加培训主题失败");
    }

	@OperateLog(value="删除培训主题", type=OperateType.DELETE)
    @ApiOperation(value = "删除培训主题")
    @ApiImplicitParam(name = "id", value = "唯一ID", required = true, dataType = "Long")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/delete/{id}")
    public Result delete(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){
        EmployeeTrainingSubject employeeTrainingSubject = new EmployeeTrainingSubject();
    	employeeTrainingSubject.setId(id);
    	employeeTrainingSubject.setDeleted(1);
		boolean update = employeeTrainingSubjectService.updateById(employeeTrainingSubject);
		
		if(update)
    		return Result.success();
    	
    	return Result.fail("删除培训主题失败");
    }

	@OperateLog(value="修改培训主题", type=OperateType.EDIT)
    @ApiOperation(value = "修改培训主题")
    @ApiImplicitParam(name = "employeeTrainingSubjectVO", value = "主题信息", required = true, dataType = "EmployeeTrainingSubjectVO")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/update")
    public Result update(@RequestBody @Validated EmployeeTrainingSubjectVO employeeTrainingSubjectVO){
    	if (employeeTrainingSubjectVO.getId() == null) {
            return Result.fail("培训主题Id不能为空");
        }
    	
    	EmployeeTrainingSubject employeeTrainingSubject = new EmployeeTrainingSubject();
    	BeanUtils.copyProperties(employeeTrainingSubjectVO, employeeTrainingSubject);
    	
		boolean update = employeeTrainingSubjectService.updateById(employeeTrainingSubject);
    	if(update)
    		return Result.success();
    	
    	return Result.fail("修改培训主题失败");
    }

    @ApiOperation(value = "查询培训主题列表")
    @ApiImplicitParams({
    	@ApiImplicitParam(name = "page", value = "当前页数", required = true, dataType = "Integer ",defaultValue = "1"),
        @ApiImplicitParam(name = "limit", value = "显示数据条数", required = true, dataType = "Integer ",defaultValue = "10"),
        @ApiImplicitParam(name = "trainDate", value = "培训时间", required = false, dataType = "Long"),
        @ApiImplicitParam(name = "trainTitle", value = "培训主题", required = false, dataType = "String"),
        @ApiImplicitParam(name = "trainType", value = "培训类别", required = false, dataType = "String"),
    })
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @GetMapping("/list")
    public Result list(@RequestParam(defaultValue = "1") Integer page,
			   @RequestParam(defaultValue = "10") Integer limit,
			   @RequestParam(required=false) Long trainDate,
			   @RequestParam(required=false) @Size(max=32,message="培训主题最大长度为32") String trainTitle,
			   @RequestParam(required=false) @Size(max=32,message="培训类别最大长度为32") String trainType){
    	EmployeeTrainingSubject employeeTrainingSubject = new EmployeeTrainingSubject();
    	employeeTrainingSubject.setTrainDate(trainDate);
    	employeeTrainingSubject.setTrainTitle(trainTitle);
    	employeeTrainingSubject.setTrainType(trainType);
        IPage<EmployeeTrainingSubject> employeeTrainingSubjectPage = employeeTrainingSubjectService.findListByPage(page, limit,employeeTrainingSubject);
        
 		List<EmployeeTrainingSubjectDTO> employeeTrainingSubjectList = employeeTrainingSubjectPage.getRecords().stream().map(employeeTrainingSubjectTmp->{
 			EmployeeTrainingSubjectDTO employeeTrainingSubjectDTO = new EmployeeTrainingSubjectDTO();
 			BeanUtils.copyProperties(employeeTrainingSubjectTmp, employeeTrainingSubjectDTO);
 			return employeeTrainingSubjectDTO;
 		}).collect(Collectors.toList());
         
         ResultList resultList = new ResultList.Builder().total(employeeTrainingSubjectPage.getTotal()).list(employeeTrainingSubjectList).build();
         return Result.success(resultList);
    }

}
