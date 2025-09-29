package com.zcxd.pda.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zcxd.common.constant.*;
import com.zcxd.common.util.ResultList;
import com.zcxd.common.util.TokenUtils;
import com.zcxd.db.model.*;
import com.zcxd.pda.config.UserContextHolder;
import com.zcxd.pda.dto.EmployeeUserDTO;
import com.zcxd.pda.dto.FingerprintDTO;
import com.zcxd.pda.dto.LoginDTO;
import com.zcxd.pda.exception.BusinessException;
import com.zcxd.pda.service.impl.*;
import com.zcxd.pda.vo.LoginAccountVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 
 * @ClassName LoginService
 * @Description 用户管理服务类
 * @author shijin
 * @Date 2021年5月7日上午10:49:28
 */
@Slf4j
@Service
public class LoginService {

	@Resource
	private PdaService pdaService;
	@Resource
	private BankTellerService bankTellerService;
	@Resource
	private EmployeeService employeeService;
	@Resource
	private EmployeeJobService employeeJobService;
	@Resource
	private DepartmentService departmentService;
	@Resource
	private FingerprintService fingerprintService;

	private final static List<String> forbitPasswordList = Arrays.asList(
			"E10ADC3949BA59ABBE56E057F20F883E", //123456
			"FCEA920F7412B5DA7BE0CF42B8C93759", //1234567
			"25D55AD283AA400AF464C76D713C07AD", //12345678
			"C33367701511B4F6020EC61DED352059", //654321
			"96E79218965EB72C92A549DD5A330112", //111111
			"E3CEB5881A0A1FDAAD01296D7554868D", //222222
			"1A100D2C0DAB19C4430E7D73762B3423", //333333
			"73882AB1FA529D7273DA0DB6B49CC4F3", //444444
			"5B1B68A9ABF4D2CD155C81A9225FD158", //555555
			"F379EAF3C831B04DE153469D1BEC345E", //666666
			"F63F4FBC9F8C85D409F2F59F2B9E12D5", //777777
			"21218CCA77804D2BA1922C33E0151105", //888888
			"52C69E3A57331081823331C4E69D3F2E", //999999
			"670B14728AD9902AECBA32E22FA4F6BD"  //000000
			);
	private final static String DEFAULT_PASSWORD = "E10ADC3949BA59ABBE56E057F20F883E";

	private boolean isDefaultPassword(String password) {
		return DEFAULT_PASSWORD.compareToIgnoreCase(password) == 0;
	}

	private boolean isSimplePassword(String password) {
		for(String s : forbitPasswordList) {
			if (s.compareToIgnoreCase(password) == 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 押运员账号登录
	 * @param loginAccountVO
	 * @return
	 */
	public LoginDTO loginEsort(LoginAccountVO loginAccountVO) throws BusinessException {
		Employee employee = employeeService.getByEmployeeNo(loginAccountVO.getUserName());
		if (null == employee) {
			log.warn("无效用户: "+loginAccountVO.getUserName());
			throw new BusinessException("无效用户");
		}
		EmployeeJob employeeJob = employeeJobService.getById(employee.getJobIds());
		boolean isEsortJob = false;
		if (employeeJob != null &&
				(employeeJob.getJobType().intValue() == JobTypeEnum.KEY.getValue() || employeeJob.getJobType().intValue() == JobTypeEnum.CLEAN.getValue())) {
			isEsortJob = true;
		}
		if (!isEsortJob) {
			log.warn("无效用户类型");
			throw new BusinessException("无效用户");
		}

		if (0 != employee.getPassword().compareToIgnoreCase(loginAccountVO.getPassword())) {
			log.warn("密码错误");
			throw new BusinessException("密码错误");
		}
		boolean isDefaultPassword = isDefaultPassword(loginAccountVO.getPassword());
		int changePwd = isDefaultPassword? 1 : 0;
		return getEmployeeLoginDTO(employee,changePwd);
	}
	/**
	 * 账号登录
	 * @param loginAccountVO
	 * @return
	 */
	public LoginDTO loginAccount(LoginAccountVO loginAccountVO) throws BusinessException {

		Pda pda = pdaService.getByTerSN(loginAccountVO.getTersn());
		if (null == pda) {
			log.warn("无效终端: " + loginAccountVO.getTersn());
			throw new BusinessException("无效终端");
		}
		/**
		 * 银行柜员和员工分别使用不同的用户信息
		 */
		if (Constant.USER_EMPLOYEE == pda.getUseType()) {
			Employee employee = employeeService.getByEmployeeNo(loginAccountVO.getUserName());
			if (null == employee || employee.getPdaEnable() != Constant.ENBALE) {
				log.warn("无效用户: " + loginAccountVO.getUserName());
				throw new BusinessException("无效用户");
			}
			if (0 != employee.getPassword().compareToIgnoreCase(loginAccountVO.getPassword())) {
				log.warn("密码错误");
				throw new BusinessException("密码错误");
			}
			boolean isDefaultPassword = isDefaultPassword(loginAccountVO.getPassword());
			int changePwd = isDefaultPassword? 1:0;
			return getEmployeeLoginDTO(employee,changePwd);
		} else {
			BankTeller bankTeller = bankTellerService.getByTellerNo(loginAccountVO.getUserName(),pda.getBankId());
			if (null == bankTeller) {
				log.warn("无效用户: " + loginAccountVO.getUserName());
				throw new BusinessException("无效用户");
			}
			if (0 != bankTeller.getPassword().compareToIgnoreCase(loginAccountVO.getPassword())) {
				log.warn("密码错误");
				throw new BusinessException("密码错误");
			}
			return getTellerLoginDTO(bankTeller);
		}
	}

	/**
	 * 指纹登录
	 * @param userId - 用户id
	 * @param terSN - pda 终端SN
	 * @return
	 */
	public LoginDTO loginPrint(Integer userId,String terSN) throws BusinessException{
		Pda pda = pdaService.getByTerSN(terSN);
		if (null == pda) {
			log.warn("无效终端: " + terSN);
			throw new BusinessException("无效终端");
		}
		if (Constant.USER_EMPLOYEE == pda.getUseType()) {
			Employee employee = employeeService.getEmployeeById(userId);
			if (null == employee) {
				log.warn("无效用户: " + userId);
				throw new BusinessException("无效用户");
			}
			return getEmployeeLoginDTO(employee,0);
		} else {
			BankTeller bankTeller = bankTellerService.getTellerById(userId);
			if (null == bankTeller) {
				log.warn("无效用户: " + userId);
				throw new BusinessException("无效用户");
			}
			return getTellerLoginDTO(bankTeller);
		}
	}

	/**
	 * 将岗位类型转换为PDA用户类型
	 * @param jobType
	 * @return
	 */
	private int JobType2PdaUserRoleType(int jobType) {
		if (JobTypeEnum.CLEAN.getValue() == jobType || JobTypeEnum.KEY.getValue() == jobType) {
			return PdaUserRoleTypeEnum.BUSINESS_MAN.getValue();
		} else if (JobTypeEnum.CLEAR.getValue() == jobType) {
			return PdaUserRoleTypeEnum.CLEAR_MAN.getValue();
		} else if (JobTypeEnum.STORAGE.getValue() == jobType) {
			return PdaUserRoleTypeEnum.STORE_KEEPER.getValue();
		} else {
			return PdaUserRoleTypeEnum.UNKNOW.getValue();
		}
	}

	private LoginDTO getEmployeeLoginDTO(Employee employee,int changePwd) {

		//员工岗位类型与PDA用户角色进行映射
		//根据岗位类型获取PDA用户角色
		Map<Integer,Integer> pdaRoleJobTypeMap = new HashMap<>();
		pdaRoleJobTypeMap.put(JobTypeEnum.CLEAN.getValue(),PdaUserRoleTypeEnum.BUSINESS_MAN.getValue());
		pdaRoleJobTypeMap.put(JobTypeEnum.KEY.getValue(),PdaUserRoleTypeEnum.BUSINESS_MAN.getValue());
		pdaRoleJobTypeMap.put(JobTypeEnum.CLEAR.getValue(),PdaUserRoleTypeEnum.CLEAR_MAN.getValue());
		pdaRoleJobTypeMap.put(JobTypeEnum.STORAGE.getValue(),PdaUserRoleTypeEnum.STORE_KEEPER.getValue());

		Long jobId = employee.getJobIds();
		EmployeeJob employeeJob = employeeJobService.getById(jobId);

		String token = TokenUtils.creatToken(employee.getId(),Constant.USER_EMPLOYEE);
		LoginDTO loginDTO  = new LoginDTO();
		loginDTO.setUserId(employee.getId());
		loginDTO.setToken(token);
		loginDTO.setUserName(employee.getEmpName());
		loginDTO.setUserAccount(employee.getEmpNo());
		loginDTO.setChangePwd(changePwd);
		Integer roleType = pdaRoleJobTypeMap.get(employeeJob.getJobType());
		if (roleType == null) {
			if(employee.getPdaAdmin() == 1) {
				roleType = PdaUserRoleTypeEnum.SYSTEM_ADMIN.getValue();
			} else {
				roleType = 0;
			}
		}
		loginDTO.setRoleType(roleType);
		return loginDTO;
	}

	private LoginDTO getTellerLoginDTO(BankTeller bankTeller) {
		String token = TokenUtils.creatToken(bankTeller.getId(),Constant.USER_BANK_TELLER);
		LoginDTO loginDTO  = new LoginDTO();
		loginDTO.setUserId(bankTeller.getId());
		loginDTO.setToken(token);
		loginDTO.setUserName(bankTeller.getTellerName());
		loginDTO.setUserAccount(bankTeller.getTellerNo());
		loginDTO.setRoleType(PdaUserRoleTypeEnum.BANK_TELLER.getValue());
		if(bankTeller.getManagerFlag() == 1){
			loginDTO.setRoleType(PdaUserRoleTypeEnum.BANK_TELLER_ADMIN.getValue());
		}
		return loginDTO;
	}

	/**
	 * 修改密码
	 * @param oldPassword
	 * @param newPassword
	 * @return
	 */
	public void updatePassword(String oldPassword,String newPassword) throws BusinessException{
		Long userId = UserContextHolder.getUserId();
		int userType = UserContextHolder.getUserType();
		if (Constant.USER_EMPLOYEE == userType) {
			Employee employee = employeeService.getEmployeeById(userId);
			if(employee == null){
				log.warn("无效用户："+userId);
				throw new BusinessException("无效用户");
			}
			if (0 != employee.getPassword().compareToIgnoreCase(oldPassword)) {
				log.warn("原密码错误");
				throw new BusinessException("原密码错误");
			}
			if (isSimplePassword(newPassword)) {
				log.warn("密码过于简单");
				throw new BusinessException("密码过于简单");
			}
			Employee newEmployee = new Employee();
			newEmployee.setId(employee.getId());
			newEmployee.setPassword(newPassword);
			employeeService.updateById(newEmployee);
		} else {
			BankTeller bankTeller = bankTellerService.getTellerById(userId);
			if (0 != bankTeller.getPassword().compareToIgnoreCase(oldPassword)) {
				throw new BusinessException("原密码错误");
			}
			if (isSimplePassword(newPassword)) {
				throw new BusinessException("密码过于简单");
			}
			BankTeller newBankTeller = new BankTeller();
			newBankTeller.setId(bankTeller.getId());
			newBankTeller.setPassword(newPassword);
			bankTellerService.updateById(newBankTeller);
		}
	}

	/**
	 * 查询指纹列表数据,如果设备属于网点，只下发网点的指纹
	 * @param page
	 * @param limit
	 * @param terSN
	 * @return
	 */
	public ResultList listFingerPrint(Integer page, Integer limit, String terSN) throws BusinessException{
		Pda pda = pdaService.getByTerSN(terSN);
		if (pda == null || pda.getStatusT() != 0) {
			throw new BusinessException("无效终端");
		}
		//网点设备查询当前机构银行员工的指纹数据
		if (pda.getUseType() != Constant.USER_EMPLOYEE){
			Long bankId = pda.getBankId();
			Map<Long,String> tellerMap = bankTellerService.getTellerMap(bankId);
			//查询指纹数据
			List<Fingerprint> fingerprintList = fingerprintService.listPrint(pda.getUseType(),tellerMap.keySet());
			//格式化数据
			List<FingerprintDTO> fingerprintDTOList = fingerprintList.stream().map(item -> {
				FingerprintDTO fingerprintDTO = new FingerprintDTO();
				BeanUtils.copyProperties(item,fingerprintDTO);
				fingerprintDTO.setUserName(Optional.ofNullable(tellerMap.get(item.getUserId())).orElse(""));
				fingerprintDTO.setUserType(PdaUserRoleTypeEnum.BANK_TELLER.getValue());
				return fingerprintDTO;
			}).collect(Collectors.toList());
			return ResultList.builder().total(fingerprintDTOList.size()).list(fingerprintDTOList).build();
		}
		//杭州建德员工
		IPage<Fingerprint> iPage = fingerprintService.listPrintPage(pda.getUseType(),page,limit);
		Map<Long,Employee> employeeMap = new HashMap<>();
		Map<Long,EmployeeJob> employeeJobMap =  new HashMap<>();
		if (iPage.getRecords().size() > 0) {
			Set<Long> userIds;
			userIds = iPage.getRecords().stream().map(Fingerprint::getUserId).collect(Collectors.toSet());
			List<Employee> employeeList = employeeService.listByIds(userIds);
			employeeMap = employeeList.stream().collect(Collectors.toMap(Employee::getId,Function.identity(),(key1,key2) -> key2));

			Set<Long> jobIds = employeeList.stream().map(Employee::getJobIds).collect(Collectors.toSet());
			List<EmployeeJob> jobList = employeeJobService.listByIds(jobIds);
			employeeJobMap = jobList.stream().collect(Collectors.toMap(EmployeeJob::getId,Function.identity(),(key1,key2) -> key2));
		}
		Map<Long, Employee> finalEmployeeMap = employeeMap;
		Map<Long, EmployeeJob> finalEmployeeJobMap = employeeJobMap;
		List<FingerprintDTO> fingerprintDTOList = iPage.getRecords().stream().map(fingerprint -> {
			FingerprintDTO fingerprintDTO = new FingerprintDTO();
			BeanUtils.copyProperties(fingerprint,fingerprintDTO);
			Employee employee = finalEmployeeMap.get(fingerprint.getUserId());
			if (employee != null) {
				fingerprintDTO.setUserName(employee.getEmpName());
				fingerprintDTO.setMaster(employee.getTitle());
				//返回角色类型
				EmployeeJob employeeJob = finalEmployeeJobMap.get(employee.getJobIds());
				if (employeeJob != null) {
					fingerprintDTO.setUserType(this.JobType2PdaUserRoleType(employeeJob.getJobType()));
				}
			}
			return fingerprintDTO;
		}).collect(Collectors.toList());
		return ResultList.builder().total(iPage.getTotal()).list(fingerprintDTOList).build();
	}

	/**
	 * 获取用户指纹设置情况
	 * @param userType
	 * @param userId
	 * @return
	 */
	public List<FingerprintDTO> getUserFingerInfo(Integer userType,Long userId) {
		List<Fingerprint> list = fingerprintService.listUserPrint(userId,userType);
		if (null == list) {
			return null;
		}
		List<FingerprintDTO> fingerprintDTOList = list.stream().map(fingerprint -> {
			FingerprintDTO fingerprintDTO = new FingerprintDTO();
			fingerprintDTO.setId(fingerprint.getId());
			fingerprintDTO.setFingerIdx(fingerprint.getFingerIdx());
			fingerprintDTO.setUserId(fingerprint.getUserId());
			fingerprintDTO.setUserType(fingerprint.getUserType());
			return fingerprintDTO;
		}).collect(Collectors.toList());
		return fingerprintDTOList;
	}

	/**
	 * 设置指纹特征
	 * @param fingerprintDTO
	 * @return
	 */
	public Long saveFingerPrint(FingerprintDTO fingerprintDTO) {

		Fingerprint fingerprint = fingerprintService.getByUserId(fingerprintDTO.getUserId(),fingerprintDTO.getUserType(),fingerprintDTO.getFingerIdx());
		if (null != fingerprint) {
			//update
			fingerprint.setFingerPrint(fingerprintDTO.getFingerPrint());
			return  fingerprintService.updateById(fingerprint) ? fingerprint.getId() : 0L;
		} else {
			//insert
			fingerprint = new Fingerprint();
			BeanUtils.copyProperties(fingerprintDTO,fingerprint);
		    return fingerprintService.save(fingerprint) ? fingerprint.getId() : 0L;
		}
	}

	/**
	 * 删除指纹
	 * @param id
	 * @return
	 */
	public boolean deleteFingerPrint(Long id) {
		fingerprintService.softDelete(id);
		return true;
	}


	/**
	 * 分页查询员工列表
	 * @param page
	 * @param limit
	 * @return
	 */
	public ResultList<EmployeeUserDTO> listEmployeePage(Long departmentId,Integer page, Integer limit) {

		IPage<Employee> employeeIPage = employeeService.listPdaEmployeePage(departmentId,page,limit);
		if (employeeIPage.getRecords().size() == 0) {
			return ResultList.builder().total(employeeIPage.getTotal()).list(null).build();
		}
		Set<Long> departmentIds = new HashSet<>();
		Set<Long> jobIds = new HashSet<>();
		for (Employee employee : employeeIPage.getRecords()) {
			departmentIds.add(employee.getDepartmentId());
			jobIds.add(employee.getJobIds());
		}

		List<Department> departmentList = departmentService.listByIds(departmentIds);
		Map<Long,Department> departmentMap = departmentList.stream().collect(Collectors.toMap(Department::getId,Function.identity(),(key1,key2) -> key2));

		List<EmployeeJob> employeeJobList = employeeJobService.listByIds(jobIds);
		Map<Long,EmployeeJob> employeeJobMap = employeeJobList.stream().collect(Collectors.toMap(EmployeeJob::getId,Function.identity(),(key1,key2) -> key2));

		List<EmployeeUserDTO> employeeUserDTOList = employeeIPage.getRecords().stream().map(employee -> {
			EmployeeUserDTO employeeUserDTO = new EmployeeUserDTO();
			employeeUserDTO.setId(employee.getId());
			employeeUserDTO.setEmpNo(employee.getEmpNo());
			employeeUserDTO.setEmpName(employee.getEmpName());
			employeeUserDTO.setMaster(employee.getTitle());
			Department department = departmentMap.get(employee.getDepartmentId());
			if (null != department) {
				employeeUserDTO.setDepartmentName(department.getName());
			}

			EmployeeJob employeeJob = employeeJobMap.get(employee.getJobIds());
			if (null != employeeJob) {
				employeeUserDTO.setJobName(employeeJob.getName());
			}

			return employeeUserDTO;
		}).collect(Collectors.toList());

		return ResultList.builder().total(employeeIPage.getTotal()).list(employeeUserDTOList).build();
	}

	/**
	 * 查询最新更新时间
	 * @param terSN
	 * @return
	 */
	public Long getFingerprintUpdateTime(String terSN) {
		Pda pda = pdaService.getByTerSN(terSN);
		if (pda == null || pda.getStatusT() != 0) {
			throw new BusinessException("无效终端");
		}

		Long maxUpdateTime = fingerprintService.getLastUpdateTime(pda.getUseType());
		return maxUpdateTime == null? 0L : maxUpdateTime;
	}

}
