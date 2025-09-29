package com.zcxd.base.controller;

import java.util.List;

import javax.validation.constraints.Min;

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

import com.zcxd.base.dto.DepartmentInfoDTO;
import com.zcxd.base.service.DepartmentService;
import com.zcxd.base.vo.DepartmentVO;
import com.zcxd.common.annotation.OperateLog;
import com.zcxd.common.annotation.OperateType;
import com.zcxd.common.util.Result;
import com.zcxd.common.util.TreeUtil;
import com.zcxd.db.model.Department;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;

/**
 * 
 * @ClassName DepartmentController
 * @Description 部门管理控制器
 * @author 秦江南
 * @Date 2021年5月7日上午11:19:31
 */
@RestController
@RequestMapping("/department")
@Api(tags = "部门管理")
public class DepartmentController {


	@Autowired
    private DepartmentService departmentService;

    @ApiOperation(value = "查询部门树")
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @GetMapping("/tree")
    public Result tree(){
    	return Result.success(TreeUtil.listToTree(departmentService.getDepartmentList(null)));
    }
    
    @ApiOperation(value = "查询部门信息详情")
    @ApiImplicitParam(name = "id", value = "部门id", required = true, dataType = "Long")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/info/{id}")
    public Result info(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){
    	Department department = departmentService.getById(id);
    	
    	DepartmentInfoDTO infoDTO = new DepartmentInfoDTO();
    	BeanUtils.copyProperties(department, infoDTO);
    	
    	String[] parentsIds = department.getParentIds().substring(1, department.getParentIds().length()-1).split("/");
		if (parentsIds.length > 1) {
			Department parent = departmentService.getById(parentsIds[parentsIds.length - 1]);
			infoDTO.setParentName(parent.getName());
		}
    	
    	return Result.success(infoDTO);
    }
    
    
    @OperateLog(value="新增部门", type=OperateType.ADD)
    @ApiOperation(value = "新增部门")
    @ApiImplicitParam(name = "departmentVO", value = "部门信息", required = true, dataType = "DepartmentVO")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/save")
    public Result save(@RequestBody @Validated DepartmentVO departmentVO){
    	Department department = new Department();
    	if(!departmentVO.getParentIds().equals("0")){
    		Department departmentParent = departmentService.getById(departmentVO.getParentIds());
    		department.setParentIds(departmentParent.getParentIds() + departmentVO.getParentIds() +"/");
    	}else{
    		department.setParentIds("/0/");
    	}
    	department.setName(departmentVO.getName());
    	List<Department> departmentList = departmentService.getDepartmentByCondition(department);
		if(departmentList != null && departmentList.size()>0)
			return Result.fail("该部门已存在，请重新填写！");
    	
    	department.setId(null);
    	department.setDescription(departmentVO.getDescription());
    	department.setLinkmanName(departmentVO.getLinkmanName());
    	department.setLinkmanMobile(departmentVO.getLinkmanMobile());
    	boolean save = departmentService.save(department);
    	if(save)
    		return Result.success();
    	
    	return Result.fail("新增部门失败");
    }
    
    @OperateLog(value="修改部门", type=OperateType.EDIT)
    @ApiOperation(value = "修改部门")
    @ApiImplicitParam(name = "departmentVO", value = "部门信息", required = true, dataType = "DepartmentVO")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/update")
    public Result update(@RequestBody @Validated DepartmentVO departmentVO){
    	
        if (departmentVO.getId() == null) {
            return Result.fail("部门Id不能为空");
        }
        
    	Department department = new Department();
    	if(!departmentVO.getParentIds().equals("0")){
    		Department departmentParent = departmentService.getById(departmentVO.getParentIds());
    		department.setParentIds(departmentParent.getParentIds() + departmentVO.getParentIds() +"/");
    	}else{
    		department.setParentIds("/0/");
    	}
    	department.setName(departmentVO.getName());
    	List<Department> departmentList = departmentService.getDepartmentByCondition(department);
		if(departmentList != null && departmentList.size()>0){
			if(!departmentList.get(0).getId().equals(departmentVO.getId()))
				return Result.fail("该部门已存在，请重新填写！");
		}
    	
    	department.setId(departmentVO.getId());
    	department.setDescription(departmentVO.getDescription());
    	department.setLinkmanName(departmentVO.getLinkmanName());
    	department.setLinkmanMobile(departmentVO.getLinkmanMobile());
    	boolean update = departmentService.updateById(department);
    	if(update)
    		return Result.success();
    	
    	return Result.fail("修改部门失败");
    }
    
    @ApiOperation(value = "部门树下拉选项")
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @GetMapping("/option")
    public Result option(){
    	return Result.success(TreeUtil.listToTree(departmentService.getDepartmentList(null)));
    }
    
    @ApiOperation(value = "权限部门树下拉选项")
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @GetMapping("/authOption")
    public Result authOption(){
    	return Result.success(TreeUtil.listToTree(departmentService.getAuthDepartmentList()));
    }

    
    @ApiOperation(value = "顶级部门列表")
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @GetMapping("/tops")
    public Result tops(){
    	return Result.success(departmentService.getTopDepartment());
    }
    
    @ApiOperation(value = "权限部门列表")
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @GetMapping("/auth")
    public Result auth(){
    	return Result.success(departmentService.getAuthDepartment());
    }

	@GetMapping("/isAuth")
	public Result<Boolean> isAuth(@RequestParam("id")Integer id){
		return departmentService.isAuthDepartment(id) ? Result.success() :Result.fail("您部门无权限");
	}
}
