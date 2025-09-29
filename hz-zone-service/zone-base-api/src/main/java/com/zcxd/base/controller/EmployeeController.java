package com.zcxd.base.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.constraints.Min;

import com.zcxd.db.model.Fingerprint;
import com.zcxd.db.model.Vehicle;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.DigestUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zcxd.base.dto.EmployeeDTO;
import com.zcxd.base.dto.EmployeeInfoDTO;
import com.zcxd.base.service.CommonService;
import com.zcxd.base.service.EmployeeJobService;
import com.zcxd.base.service.EmployeeService;
import com.zcxd.base.service.FingerprintService;
import com.zcxd.base.vo.EmployeeQueryVO;
import com.zcxd.base.vo.EmployeeQuitVO;
import com.zcxd.base.vo.EmployeeVO;
import com.zcxd.common.annotation.OperateLog;
import com.zcxd.common.annotation.OperateType;
import com.zcxd.common.constant.Constant;
import com.zcxd.common.constant.FingerTypeEnum;
import com.zcxd.common.constant.JobTypeEnum;
import com.zcxd.common.util.CharUtil;
import com.zcxd.common.util.DateTimeUtil;
import com.zcxd.common.util.Result;
import com.zcxd.common.util.ResultList;
import com.zcxd.common.util.SecurityUtils;
import com.zcxd.db.model.Employee;
import com.zcxd.db.model.EmployeeJob;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;

/**
 * 
 * @ClassName EmployeeController
 * @Description 员工档案控制器
 * @author 秦江南
 * @Date 2021年5月14日上午10:56:08
 */
@RestController
@RequestMapping("/employee")
@Api(tags="员工档案")
public class EmployeeController {
	
	@Autowired
    private FingerprintService fingerprintService;
	
	@Autowired
    private EmployeeService employeeService;

	@Autowired
    private EmployeeJobService employeeJobService;
	
    @Autowired
    private CommonService commonService;


    @OperateLog(value="添加员工档案", type=OperateType.ADD)
    @ApiOperation(value = "添加员工档案")
    @ApiImplicitParam(name = "employeeVO", value = "员工档案", required = true, dataType = "EmployeeVO")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/save")
    public Result save(@RequestBody @Validated EmployeeVO employeeVO){
    	Employee employee = new Employee();
    	employee.setEmpNo(employeeVO.getEmpNo());
    	List<Employee> employeeList = employeeService.getEmployeeByCondition(employee);
    	if(employeeList != null && employeeList.size()>0)
			return Result.fail("该工号已存在，请重新填写！");
    	
    	BeanUtils.copyProperties(employeeVO, employee);
    	
		if (!StringUtils.isBlank(employeeVO.getIdno())) {
			employee.setIdno(SecurityUtils.encryptAES(employeeVO.getIdno()));
			String birthDay = CharUtil.getBirthday(employeeVO.getIdno());
			employee.setBirthday(DateTimeUtil.date2TimeStampMs(birthDay+" 00:00:00",null));
		}
		employee.setStatusT(0);
		employee.setQuitDate(0L);
		
		EmployeeJob employeeJob = employeeJobService.getById(employee.getJobIds());
		employee.setJobType(employeeJob.getJobType());
		
    	if (!StringUtils.isBlank(employee.getMobile())) {
			employee.setMobile(SecurityUtils.encryptAES(employee.getMobile()));
		}
    	
    	employee.setWxOpenid("");
    	
		if (!StringUtils.isBlank(employee.getContactMobile())) {
			employee.setContactMobile(SecurityUtils.encryptAES(employee.getContactMobile()));
		}
    	
		if (!StringUtils.isBlank(employee.getGuarantorMobile())) {
			employee.setGuarantorMobile(SecurityUtils.encryptAES(employee.getGuarantorMobile()));
		}

    	employee.setNextVisitDate(0L);
    	employee.setPdaEnable(0);
//    	employee.setPassword("");
    	employee.setPassword(DigestUtils.md5DigestAsHex(Constant.USER_DEFAULT_PWD.getBytes()).toUpperCase());
    	employee.setRoleId(0L);
    	employee.setDeleted(0);
    	boolean save = employeeService.save(employee);
    	if(save){
    		if (!StringUtils.isBlank(employee.getPhotoUrl())) 
    			commonService.moveFile(employee.getPhotoUrl());
    		
    		return Result.success();
    	}
    	return Result.fail("添加员工失败");
    }

    @OperateLog(value="删除员工档案", type=OperateType.DELETE)
    @ApiOperation(value = "删除员工档案")
    @ApiImplicitParam(name = "id", value = "员工档案id", required = true, dataType = "Long")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/delete/{id}")
    public Result delete(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){
    	Employee employee = new Employee();
    	employee.setId(id);
    	employee.setPdaEnable(0);
    	employee.setDeleted(1);
		employeeService.deleteEmployeeUserCache(id); //淘汰缓存
        boolean update = employeeService.updateById(employee);
    	if(!update) {
			return Result.fail("删除失败");
		}

    	fingerprintService.deleteByUserId(employee.getId(),FingerTypeEnum.STAFF.getValue());
    	return Result.success();
    	
}
    
    @OperateLog(value="员工离职处理", type=OperateType.EDIT)
    @ApiOperation(value = "员工离职处理")
    @ApiImplicitParam(name = "employeeQuitVO", value = "员工离职", required = true, dataType = "EmployeeQuitVO")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/quit")
    public Result quit(@RequestBody @Validated EmployeeQuitVO employeeQuitVO){
    	Employee employee = employeeService.getById(employeeQuitVO.getId());
    	if(employee.getStatusT() != 0)
    		return Result.fail("员工离职状态有误");
    		
    	employee = new Employee();
    	employee.setId(employeeQuitVO.getId());
    	employee.setStatusT(1);
    	employee.setPdaEnable(0);
    	employee.setQuitDate(employeeQuitVO.getQuitDate());
    	employee.setComments(employeeQuitVO.getReasons());
		employeeService.deleteEmployeeUserCache(employeeQuitVO.getId()); //淘汰缓存
    	boolean update = employeeService.updateById(employee);
    	if(!update) {
			return Result.fail("操作失败");
		}
    		
    	fingerprintService.deleteByUserId(employee.getId(),FingerTypeEnum.STAFF.getValue());
    	return Result.success();
    }

	/**
	 * 如果有指纹，更新指纹时间，以便PDA下载更新
	 * @param empId
	 * @author: shijin
	 */
	private void checkUpdateFingerPrintTime(Long empId) {
    	List<Fingerprint> fingerprints = fingerprintService.listByUserId(empId);
		/**
		 * 更新指纹updateTime, PDA检查可以重新下载
		 */
    	if (fingerprints.size() > 0) {
			Fingerprint fingerprint = new Fingerprint();
			fingerprint.setId(fingerprints.get(0).getId());
			fingerprint.setUpdateTime(System.currentTimeMillis());
			fingerprintService.updateById(fingerprint);
		}
	}

	@PostMapping(value = "/findByCondition")
	public Result<List<Employee>> findByCondition(@RequestBody EmployeeVO employeeVO) {
		Employee employee = new Employee();
		BeanUtils.copyProperties(employeeVO, employee);
		List<Employee> employeeByCondition = employeeService.getEmployeeByCondition(employee);
		return employeeByCondition.isEmpty() ? Result.fail("未查找到指定员工信息") : Result.success(employeeByCondition.get(0));
	}

    @OperateLog(value="修改员工档案", type=OperateType.EDIT)
    @ApiOperation(value = "修改员工档案")
    @ApiImplicitParam(name = "employeeVO", value = "员工信息", required = true, dataType = "EmployeeVO")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @PostMapping("/update")
    public Result update(@RequestBody @Validated EmployeeVO employeeVO){

		Employee oldEmployee = employeeService.getById(employeeVO.getId());
		if (null == oldEmployee) {
			return Result.fail("无效记录");
		}
    	
    	Employee employee = new Employee();
    	employee.setEmpNo(employeeVO.getEmpNo());
    	List<Employee> employeeList = employeeService.getEmployeeByCondition(employee);
    	if (null == employeeList) {
			return Result.fail("无效参数");
		}
    	if(employeeList.size()>0) {
			if (!employeeList.get(0).getId().equals(employeeVO.getId())) {
				return Result.fail("该工号已存在，请重新填写！");
			}
		}
    	//如果是清分岗或库管岗变更主管，设置指纹更新标志
		//add begin by shijin
    	if (JobTypeEnum.CLEAR.getValue().equals(oldEmployee.getJobType())||
				JobTypeEnum.STORAGE.getValue().equals(oldEmployee.getJobType())) {
			if (!oldEmployee.getTitle().equals(employeeVO.getTitle())) {
				checkUpdateFingerPrintTime(oldEmployee.getId());
			}
		}
    	//add end

    	BeanUtils.copyProperties(employeeVO, employee);
    	
    	if(!oldEmployee.getJobIds().equals(employeeVO.getJobIds())){
			EmployeeJob employeeJob = employeeJobService.getById(employee.getJobIds());
			employee.setJobType(employeeJob.getJobType());
    	}
    	
		if (!StringUtils.isBlank(employeeVO.getIdno())) {
			employee.setIdno(SecurityUtils.encryptAES(employeeVO.getIdno()));
			String birthDay = CharUtil.getBirthday(employeeVO.getIdno());
			employee.setBirthday(DateTimeUtil.date2TimeStampMs(birthDay+" 00:00:00",null));
		}
		
    	if (!StringUtils.isBlank(employee.getMobile())) {
			employee.setMobile(SecurityUtils.encryptAES(employee.getMobile()));
		}
    	
		if (!StringUtils.isBlank(employee.getContactMobile())) {
			employee.setContactMobile(SecurityUtils.encryptAES(employee.getContactMobile()));
		}
    	
		if (!StringUtils.isBlank(employee.getGuarantorMobile())) {
			employee.setGuarantorMobile(SecurityUtils.encryptAES(employee.getGuarantorMobile()));
		}
		employeeService.deleteEmployeeUserCache(employee.getId()); //淘汰缓存
    	boolean update = employeeService.updateById(employee);
    	if(update){
    		if (!StringUtils.isBlank(employee.getPhotoUrl())) 
    			commonService.moveFile(employee.getPhotoUrl());
    	
    		return Result.success();
    	}
    	return Result.fail("修改员工档案失败");
    }
    
    @ApiOperation(value = "查询员工档案列表")
    @ApiImplicitParams({
    	@ApiImplicitParam(name = "page", value = "当前页数", required = true, dataType = "Integer ",defaultValue = "1"),
        @ApiImplicitParam(name = "limit", value = "显示数据条数", required = true, dataType = "Integer ",defaultValue = "10"),
        @ApiImplicitParam(name = "empNo", value = "工号", required = false, dataType = "String"),
        @ApiImplicitParam(name = "empName", value = "姓名", required = false, dataType = "String"),
        @ApiImplicitParam(name = "minAge", value = "低年龄", required = false, dataType = "Integer"),
        @ApiImplicitParam(name = "maxAge", value = "高年龄", required = false, dataType = "Integer"),
        @ApiImplicitParam(name = "politic", value = "政治面貌", required = false, dataType = "String"),
        @ApiImplicitParam(name = "education", value = "教育程度", required = false, dataType = "String"),
        @ApiImplicitParam(name = "sex", value = "性别", required = false, dataType = "Integer"),
        @ApiImplicitParam(name = "jobId", value = "岗位", required = true, dataType = "Long"),
        @ApiImplicitParam(name = "departmentId", value = "部门", required = true, dataType = "Long"),
        @ApiImplicitParam(name = "status", value = "离职状态", required = false, dataType = "Integer"),
    })
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @GetMapping("/list")
    public Result findListByPage(@RequestParam(defaultValue = "1") Integer page,
			   @RequestParam(defaultValue = "10") Integer limit,
			   @Validated EmployeeQueryVO employeeQueryVO){

    	
        IPage<Employee> employeePage = employeeService.findListByPage(page, limit,employeeQueryVO);
        List<EmployeeDTO> employeeDTOList = new ArrayList<EmployeeDTO>();
        if(employeePage.getTotal() != 0){
	    	//获取部门Map
	    	Map<String, String> departmentMap = commonService.getDepartmentMap(null);
	        employeeDTOList =  employeePage.getRecords().stream().map(employee -> {
	        	EmployeeDTO employeeDTO = new EmployeeDTO();
	        	BeanUtils.copyProperties(employee, employeeDTO);
	        	employeeDTO.setAge(employee.getBirthday() != 0 ? DateTimeUtil.getAge(employee.getBirthday()) : 0);
	        	employeeDTO.setDepartmentName(employee.getDepartmentId() != 0 ? departmentMap.get(employee.getDepartmentId()+"") : "");
	        	return employeeDTO;
	        }).collect(Collectors.toList());
        }
        ResultList resultList = new ResultList.Builder().total(employeePage.getTotal()).list(employeeDTOList).build();
        return Result.success(resultList);
    }

	@PostMapping("/findById}")
	public Result findById(@PathVariable("id") Long id, @RequestParam("departmentId") Long departmentId){
		Employee employeeById = employeeService.getEmployeeById(id, departmentId);
		return employeeById == null ? Result.fail("未找到指定的员工") : Result.success(employeeById);
	}

    @ApiOperation(value = "查询员工档案详情")
    @ApiImplicitParam(name = "id", value = "员工档案id", required = true, dataType = "Long")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @GetMapping("/info/{id}")
    public Result info(@PathVariable("id") @Min(value=1, message="id不能小于1") Long id){
        Employee employee = employeeService.getById(id);
        EmployeeInfoDTO employeeInfoDTO = new EmployeeInfoDTO();
        if(employee != null){
        	BeanUtils.copyProperties(employee, employeeInfoDTO);
        	
    		//身份证和手机号需要解密
    		if (!StringUtils.isBlank(employeeInfoDTO.getIdno())) {
    			employeeInfoDTO.setIdno(SecurityUtils.decryptAES(employeeInfoDTO.getIdno()));
    		}
    		if (!StringUtils.isBlank(employeeInfoDTO.getMobile())) {
    			employeeInfoDTO.setMobile(SecurityUtils.decryptAES(employeeInfoDTO.getMobile()));
    		}
    		if (!StringUtils.isBlank(employeeInfoDTO.getContactMobile())) {
    			employeeInfoDTO.setContactMobile(SecurityUtils.decryptAES(employeeInfoDTO.getContactMobile()));
    		}

    		if (!StringUtils.isBlank(employee.getGuarantorMobile())) {
    			employeeInfoDTO.setGuarantorMobile(SecurityUtils.decryptAES(employee.getGuarantorMobile()));
    		}
        	
        }
        return Result.success(employeeInfoDTO);
    }
    
    @ApiOperation(value = "员工姓名列表")
    @ApiImplicitParam(name = "departmentId", value = "部门id", required = false, dataType = "Long")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @GetMapping("/namelist")
    public Result findNameList(@RequestParam(required=false) Long departmentId){
    	Employee employee = new Employee();
    	
    	employee.setDepartmentId(departmentId);
    	employee.setStatusT(0);
        List<Employee> employeeList = employeeService.getEmployeeByCondition(employee);
        List<Map<String, Object>> resultList = new ArrayList<>();
        employeeList.stream().forEach(employeeTmp -> {
        	Map<String, Object> map = new HashMap<>(3);
        	map.put("id", employeeTmp.getId());
        	map.put("empNo", employeeTmp.getEmpNo());
        	map.put("empName", employeeTmp.getEmpName());
        	resultList.add(map);
        });
        return Result.success(resultList);
    }
    
    @ApiOperation(value = "岗位员工姓名列表")
    @ApiImplicitParams({
    	@ApiImplicitParam(name = "departmentId", value = "部门id", required = true, dataType = "Long"),
	    @ApiImplicitParam(name = "jobTypes", value = "岗位类型", required = false, dataType = "String")
    })
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @GetMapping("/jobNameList")
    public Result findJobNameList(@RequestParam Long departmentId,@RequestParam String jobTypes){
    	Map<String,Object> resultMap = new HashMap<>();
    	
    	if(!StringUtils.isBlank(jobTypes)){
        	String[] jobType = jobTypes.split(",");
        	for (String str : jobType) {
        		List<Map<String, Object>> resultList = new ArrayList<>();
        		//为0时业务处理有误
        		if(str.equals(JobTypeEnum.OTHER.getValue() + "")){
        			return Result.fail("不能包含'其它'岗位类型");
        		}
        		
            	Employee employee = new Employee();
            	employee.setDepartmentId(departmentId);
        		employee.setJobType(Integer.parseInt(str));
        		employee.setStatusT(0);
        		employeeService.getEmployeeByCondition(employee).stream().forEach(employeeTmp -> {
    	        	Map<String, Object> map = new HashMap<>(3);
    	        	map.put("id", employeeTmp.getId());
    	        	map.put("empNo", employeeTmp.getEmpNo());
    	        	map.put("empName", employeeTmp.getEmpName());
    	        	resultList.add(map);
    	        });
    	        resultMap.put(str, resultList);
			}
    	}
        return Result.success(resultMap);
    }

	@ApiOperation(value = "员工车长")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "departmentId", value = "部门id", required = true, dataType = "Long")
	})
    @GetMapping("/getLeader")
    public Result getLeaders(@RequestParam Long departmentId){
      List<Employee> employeeList = employeeService.getLeader(departmentId);
      Map<Long,Integer> map = employeeList.stream().collect(Collectors.toMap(Employee::getId,Employee::getRouteLeader));
      return Result.success(map);
	}
}
