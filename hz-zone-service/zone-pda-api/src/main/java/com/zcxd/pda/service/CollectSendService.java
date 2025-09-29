package com.zcxd.pda.service;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zcxd.common.constant.BankCategoryTypeEnum;
import com.zcxd.common.constant.BoxpackStatusEnum;
import com.zcxd.common.constant.BoxpackTaskStatusEnum;
import com.zcxd.common.constant.CollectSendTaskReportEnum;
import com.zcxd.common.constant.DeleteFlagEnum;
import com.zcxd.common.constant.FingerTypeEnum;
import com.zcxd.common.constant.PdaUserRoleTypeEnum;
import com.zcxd.common.constant.RouteStatusEnum;
import com.zcxd.db.model.Bank;
import com.zcxd.db.model.BankTeller;
import com.zcxd.db.model.Boxpack;
import com.zcxd.db.model.BoxpackTask;
import com.zcxd.db.model.Employee;
import com.zcxd.db.model.Fingerprint;
import com.zcxd.db.model.Pda;
import com.zcxd.db.model.Route;
import com.zcxd.db.model.RouteTemplateBank;
import com.zcxd.db.model.Vehicle;
import com.zcxd.pda.config.UserContextHolder;
import com.zcxd.pda.dto.BoxpackDTO;
import com.zcxd.pda.dto.BoxpackEmpDTO;
import com.zcxd.pda.dto.BoxpackListDTO;
import com.zcxd.pda.dto.BoxpackTaskInfoDTO;
import com.zcxd.pda.dto.BoxpackTaskStartDTO;
import com.zcxd.pda.dto.EscortDTO;
import com.zcxd.pda.dto.FingerprintDTO;
import com.zcxd.pda.exception.BusinessException;
import com.zcxd.pda.service.impl.BankService;
import com.zcxd.pda.service.impl.BankTellerService;
import com.zcxd.pda.service.impl.BoxpackService;
import com.zcxd.pda.service.impl.BoxpackTaskService;
import com.zcxd.pda.service.impl.CommonService;
import com.zcxd.pda.service.impl.EmployeeService;
import com.zcxd.pda.service.impl.FingerprintService;
import com.zcxd.pda.service.impl.PdaService;
import com.zcxd.pda.service.impl.RouteService;
import com.zcxd.pda.service.impl.RouteTemplateBankService;
import com.zcxd.pda.service.impl.RouteTemplateService;
import com.zcxd.pda.service.impl.VehicleService;
import com.zcxd.pda.vo.BoxpackTaskEndVO;
import com.zcxd.pda.vo.BoxpackTaskReportVO;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @ClassName CollectSendService
 * @Description 早送晚收服务类
 * @author 秦江南
 * @Date 2021年11月26日下午3:15:32
 */
@Slf4j
@Service
public class CollectSendService {
	
	@Resource
	private PdaService pdaService;
    @Resource
    private BankService bankService;
    @Resource
    private BankTellerService bankTellerService;
    @Resource
    private BoxpackService boxPackService;
    @Resource
    private BoxpackTaskService boxpackTaskService;
    @Resource
    private RouteTemplateBankService routeTemplateBankService;
    @Resource
    private RouteTemplateService routeTemplateService;
    @Resource
    private RouteService routeService;
    @Resource
    private EmployeeService employeeService;
    @Resource
    private VehicleService vehicleService;
    @Resource
	private CommonService commonService;
	@Resource
	private FingerprintService fingerprintService;
    
    /**
     * 
     * @Title bankInfo
     * @Description 获取网点信息
     * @param terSN
     * @return
     * @return 返回类型 Object
     */
	public Object bankInfo(String terSN) {
		Pda pda = pdaService.getByTerSN(terSN);
		if (null == pda) {
			log.warn("无效终端: " + terSN);
			throw new BusinessException("无效终端");
		}
		Map<String, Object> map = new HashMap<>();
		if(pda.getUseType().equals(0)){
			map.put("bankName", "公司");
			map.put("bankNo", "0");
			map.put("bankId", 0);
		}else{
		    Bank bank = bankService.getById(pda.getBankId());
		    if(null == bank){
		    	log.warn("终端信息有误: " + terSN);
				throw new BusinessException("终端信息有误");
		    }
			map.put("bankName", bank.getFullName());
			map.put("bankNo", bank.getBankNo());
			map.put("bankId", bank.getId());
		}
		return map;
	}

	/**
	 * 
	 * @Title boxPack
	 * @Description 获取网点箱包列表
	 * @param bankId
	 * @return
	 * @return 返回类型 Object
	 */
	public Object boxPackList(Long bankId) {
		BoxpackListDTO boxpackDto = new BoxpackListDTO();
		
		if(bankId == 0L) {
			return boxpackDto;
		}
		
		Boxpack boxpackTmp = new Boxpack();
		boxpackTmp.setBankId(bankId);
		List<Boxpack> boxpackList = boxPackService.listByCondition(boxpackTmp);
		
		List<BoxpackDTO> boxpackDTOList = boxpackList.stream().map(boxpack -> {
			BoxpackDTO boxpackDTO = new BoxpackDTO();
			BeanUtils.copyProperties(boxpack, boxpackDTO);
			return boxpackDTO;
		}).collect(Collectors.toList());
		boxpackDTOList.sort((x, y) -> x.getBoxNo().compareTo(y.getBoxNo()));
		boxpackDto.setList(boxpackDTOList);
		
		boxpackTmp = new Boxpack();
		boxpackTmp.setShareBankId(bankId);
		List<Boxpack> boxpackShareList = boxPackService.listByCondition(boxpackTmp);

		List<BoxpackDTO> boxpackDTOShareList = boxpackShareList.stream().map(boxpack -> {
			BoxpackDTO boxpackDTO = new BoxpackDTO();
			BeanUtils.copyProperties(boxpack, boxpackDTO);
			return boxpackDTO;
		}).collect(Collectors.toList());
		boxpackDTOShareList.sort((x, y) -> x.getBoxNo().compareTo(y.getBoxNo()));
		boxpackDto.setShareList(boxpackDTOShareList);
		return boxpackDto;
	}

	/**
	 * 
	 * @Title reportTask
	 * @Description 申报任务
	 * @param boxpackTaskReportVO
	 * @return
	 * @return 返回类型 true
	 */
	@Transactional(rollbackFor = Exception.class)
	public boolean reportTask(BoxpackTaskReportVO boxpackTaskReportVO) {
		if(boxpackTaskReportVO.getBankId() == null || boxpackTaskReportVO.getBankId() == 0L){
			log.warn("非法请求：reportTask");
			throw new BusinessException("非法请求");
		}
	    Bank bank = bankService.getById(boxpackTaskReportVO.getBankId());
	    if(null == bank){
	    	log.warn("终端网点信息有误: " + boxpackTaskReportVO.getBankId());
			throw new BusinessException("终端信息有误");
	    }
		Optional<Integer> MM = Arrays.stream(CollectSendTaskReportEnum.values()).map(CollectSendTaskReportEnum::getValue)
				.filter(r -> r.equals(boxpackTaskReportVO.getTaskType())).findFirst();
		if(!MM.isPresent()){
			log.warn("任务类型有误: " + boxpackTaskReportVO.getTaskType());
			throw new BusinessException("任务类型有误");
		}
		if(boxpackTaskReportVO.getBoxpacks() == null || boxpackTaskReportVO.getBoxpacks().size() == 0){
			log.warn("箱包列表为空");
			throw new BusinessException("箱包列表为空");
		}
		
		RouteTemplateBank templateBank = routeTemplateBankService.getByBankId(boxpackTaskReportVO.getBankId());
		if( null == templateBank){
			log.warn("线路规划信息有误");
			throw new BusinessException("线路规划信息有误");
		}
		
		Bank bankStorage = bankService.getStorageBank(bank.getDepartmentId());
	    if(null == bankStorage){
	    	log.warn("库房机构不存在");
			throw new BusinessException("库房机构不存在");
	    }
			 
	    LocalDate localDate = LocalDate.parse(boxpackTaskReportVO.getTaskDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        long taskDate = localDate.atStartOfDay().toInstant(ZoneOffset.of("+8")).toEpochMilli();
	    String boxList = StringUtils.join(boxpackTaskReportVO.getBoxpacks(), ",");
	    
		BoxpackTask boxpackTask = new BoxpackTask();
		boxpackTask.setBankId(boxpackTaskReportVO.getBankId());
		boxpackTask.setDepartmentId(bank.getDepartmentId());
		boxpackTask.setTaskType(boxpackTaskReportVO.getTaskType());
		boxpackTask.setTaskDate(taskDate);
		boxpackTask.setBoxList(boxList);
		
		String routeNo = routeTemplateService.getById(templateBank.getRouteTemplateId()).getRouteNo();
		boxpackTask.setRouteNo(routeNo);
		
		Long routeId = 0L;
		Route route = routeService.getByRouteNo(taskDate, routeNo);
		if(null != route){
			routeId = route.getId();
			boxpackTask.setRouteId(routeId);
		}
		
		boxpackTask.setStatusT(BoxpackTaskStatusEnum.CHECKED.getValue());
		boxpackTask.setCreateUser(UserContextHolder.getUserId());
		boolean save = boxpackTaskService.save(boxpackTask);
		if(!save){
			log.warn("任务申报失败");
			throw new BusinessException("任务申报失败");
		}
		BoxpackTask storageTask = boxpackTaskService.getStorageTask(taskDate,bankStorage.getId(),routeNo,boxpackTaskReportVO.getTaskType());
		if(storageTask == null){
			boxpackTask = new BoxpackTask();
			boxpackTask.setBankId(bankStorage.getId());
			boxpackTask.setDepartmentId(bankStorage.getDepartmentId());
			boxpackTask.setTaskType(boxpackTaskReportVO.getTaskType());
			boxpackTask.setTaskDate(taskDate);
			boxpackTask.setBoxList(boxList);
			boxpackTask.setRouteNo(routeNo);
			if(routeId != 0L){
				boxpackTask.setRouteId(routeId);
			}
			boxpackTask.setStatusT(BoxpackTaskStatusEnum.CHECKED.getValue());
			save = boxpackTaskService.save(boxpackTask);
			if(!save){
				log.warn("添加库房任务失败");
				throw new BusinessException("添加库房任务失败");
			}
		}else{
			String[] boxAll= storageTask.getBoxList().split(",");
			Set<String> boxSetAll = new HashSet<>(Arrays.asList(boxAll));
			
			String[] boxThis = boxList.split(",");
			Set<String> boxSetThis = new HashSet<>(Arrays.asList(boxThis));
			boxSetAll.addAll(boxSetThis);
			
		    String splitSet = StringUtils.join(boxSetAll,",");
			storageTask.setBoxList(splitSet);
			save = boxpackTaskService.updateById(storageTask);
			if(!save){
				log.warn("修改库房任务失败");
				throw new BusinessException("修改库房任务失败");
			}
		}
		return true;
	}

	/**
	 * 
	 * @Title taskStart
	 * @Description 任务开始
	 * @param taskId
	 * @return
	 * @return 返回类型 Object
	 */
	public Object taskStart(Long taskId) {
		BoxpackTaskStartDTO infoDTO = new BoxpackTaskStartDTO();
    	BoxpackTask boxpackTask = boxpackTaskService.getById(taskId);
    	if(null == boxpackTask){
    		log.warn("任务不存在");
			throw new BusinessException("任务不存在");
    	}
    	
    	if(!boxpackTask.getStatusT().equals(BoxpackTaskStatusEnum.CHECKED.getValue())){
    		log.warn("任务状态不正确");
			throw new BusinessException("任务状态不正确");
    	}
    	
  		if(boxpackTask.getRouteId() == 0L){
  			log.warn("任务线路信息有误");
			throw new BusinessException("任务线路信息有误");
  		}
		Route route = routeService.getById(boxpackTask.getRouteId());
		
		if(null == route){
			log.warn("线路不存在");
			throw new BusinessException("线路不存在");
		}
		
		if(route.getStatusT() == RouteStatusEnum.CREATE.getValue()){
			log.warn("线路未确认");
			throw new BusinessException("线路未确认");
		}
		
		if(route.getRouteKeyMan() == 0L || route.getRouteOperMan() == 0L){
			log.warn("线路人员信息有误");
			throw new BusinessException("线路人员信息有误");
		}
		
		Set<Long> empIds = new HashSet<>();
		empIds.add(route.getRouteKeyMan());
		empIds.add(route.getRouteOperMan());
    	List<Employee> employeeList = employeeService.listByIds(empIds);
    	Map<Long,Employee> employeeMap = employeeList.stream().collect(Collectors.toMap(Employee::getId, Employee -> Employee));
    	
    	Employee keyEmp = employeeMap.get(route.getRouteKeyMan());
    	Employee OperEmp = employeeMap.get(route.getRouteOperMan());
    	
    	
    	//添加钥匙员信息
		infoDTO.setKeyMan(getBoxpackEmp(keyEmp));

		//添加密码员信息
		infoDTO.setOperMan(getBoxpackEmp(OperEmp));
		
		//箱包信息
    	String[] boxs= boxpackTask.getBoxList().split(",");
		Set<String> boxSet = new HashSet<>(Arrays.asList(boxs));
		List<Boxpack> boxpackList = boxPackService.listByIds(boxSet);
		
		List<BoxpackDTO> boxpackDTOs = new ArrayList<>();
		boxpackList.stream().forEach(boxpack -> {
			BoxpackDTO boxpackDTO = new BoxpackDTO();
			BeanUtils.copyProperties(boxpack, boxpackDTO);
			boxpackDTOs.add(boxpackDTO);
		});
		
		boxpackDTOs.sort((x, y) -> x.getBoxNo().compareTo(y.getBoxNo()));
		infoDTO.setBoxpackList(boxpackDTOs);
		return infoDTO;
	}
	
	//获取员工信息
	public BoxpackEmpDTO getBoxpackEmp(Employee emp){
		BoxpackEmpDTO man = new BoxpackEmpDTO();
		man.setEmpName(emp.getEmpName());
		man.setPhoto("");
	    if(!StringUtils.isBlank(emp.getPhotoUrl())){
	    	man.setPhoto(commonService.showImg(emp.getPhotoUrl()));
	    }
		
		List<Fingerprint> operFingerprintList = fingerprintService.list((QueryWrapper)Wrappers.query()
				.eq("user_id", emp.getId()).eq("user_type", FingerTypeEnum.STAFF.getValue()).eq("deleted", 0));
		
		List<FingerprintDTO> operfingerprintDTOs = new ArrayList<FingerprintDTO>();
		operFingerprintList.stream().forEach(fingerprint -> {
			FingerprintDTO fingerprintDTO = new FingerprintDTO();
			BeanUtils.copyProperties(fingerprint,fingerprintDTO);
			fingerprintDTO.setUserName(emp.getEmpName());
			fingerprintDTO.setMaster(emp.getTitle());
			fingerprintDTO.setUserType(PdaUserRoleTypeEnum.BUSINESS_MAN.getValue());
			operfingerprintDTOs.add(fingerprintDTO);
		});
		man.setFingerprint(operfingerprintDTOs);
		return man;
	}

	/**
	 * 
	 * @Title taskEnd
	 * @Description 结束任务
	 * @param boxpackTaskEndVO
	 * @return
	 * @return 返回类型 boolean
	 */
	@Transactional(rollbackFor = Exception.class)
	public boolean taskEnd(BoxpackTaskEndVO boxpackTaskEndVO) {
    	BoxpackTask boxpackTask = boxpackTaskService.getById(boxpackTaskEndVO.getTaskId());
    	if(null == boxpackTask){
    		log.warn("任务不存在");
			throw new BusinessException("任务不存在");
    	}
    	
    	if(!boxpackTask.getStatusT().equals(BoxpackTaskStatusEnum.CHECKED.getValue())){
    		log.warn("任务状态不正确");
			throw new BusinessException("任务状态不正确");
    	}
    	Bank bank = bankService.getById(boxpackTask.getBankId());
    	
    	Integer boxpackStatus = BoxpackStatusEnum.UNKNOWN.getValue();
    	boolean isStorage = false;
		
		if(bank.getBankCategory().equals(BankCategoryTypeEnum.STORAGE.getValue())){
			isStorage = true;
			if(boxpackTask.getTaskType().equals(CollectSendTaskReportEnum.FIXED_PAY.getValue()) 
					|| boxpackTask.getTaskType().equals(CollectSendTaskReportEnum.TEMP_PAY.getValue())){
				boxpackStatus = BoxpackStatusEnum.STORAGE.getValue();
			}else{
				boxpackStatus = BoxpackStatusEnum.ROUTE.getValue();
			}
		}else{
			isStorage = false;
			if(boxpackTask.getTaskType().equals(CollectSendTaskReportEnum.FIXED_PAY.getValue()) 
					|| boxpackTask.getTaskType().equals(CollectSendTaskReportEnum.TEMP_PAY.getValue())){
				boxpackStatus = BoxpackStatusEnum.ROUTE.getValue();
			}else{
				boxpackStatus = BoxpackStatusEnum.BANK.getValue();
			}
		}
		
		if(!isStorage){
			Set<Long> tellerIds = new HashSet<>();
			tellerIds.add(boxpackTaskEndVO.getHandOpManA());
			tellerIds.add(boxpackTaskEndVO.getHandOpManB());
  			Map<Long,BankTeller> tellerMap = new HashMap<Long,BankTeller>();
	    	if(tellerIds.size()>0){
	    		List<BankTeller> tellerList = bankTellerService.listByIds(tellerIds);
	    		tellerMap = tellerList.stream().collect(Collectors.toMap(BankTeller::getId, BankTeller -> BankTeller));
	    	}
	    	boxpackTask.setHandOpMansName(tellerMap.get(boxpackTaskEndVO.getHandOpManA()).getTellerName()
	    			+ "," + tellerMap.get(boxpackTaskEndVO.getHandOpManB()).getTellerName());
		}else{
			Set<Long> empIds = new HashSet<>();
  			empIds.add(boxpackTaskEndVO.getHandOpManA());
  			empIds.add(boxpackTaskEndVO.getHandOpManB());
  			Map<Long,Employee> employeeMap = new HashMap<Long,Employee>();
	    	if(empIds.size()>0){
	    		List<Employee> employeeList = employeeService.listByIds(empIds);
	    		employeeMap = employeeList.stream().collect(Collectors.toMap(Employee::getId, Employee -> Employee));
	    	}
	    	boxpackTask.setHandOpMansName(employeeMap.get(boxpackTaskEndVO.getHandOpManA()).getEmpName() 
	    			+ "," + employeeMap.get(boxpackTaskEndVO.getHandOpManB()).getEmpName());
		}
    	
    	
    	boxpackTask.setHandEsortMans(boxpackTaskEndVO.getKeyMan() + "," + boxpackTaskEndVO.getOperMan());
    	boxpackTask.setHandEsortMansName(boxpackTaskEndVO.getKeyManName() + "," + boxpackTaskEndVO.getOperManName());
    	boxpackTask.setHandOpMans(boxpackTaskEndVO.getHandOpManA() + "," + boxpackTaskEndVO.getHandOpManB());
    	boxpackTask.setHandTime(System.currentTimeMillis());
    	boxpackTask.setStatusT(BoxpackTaskStatusEnum.FINISH.getValue());
    	boxpackTask.setUpdateUser(UserContextHolder.getUserId());
    	boolean save = boxpackTaskService.updateById(boxpackTask);
		if(!save){
			log.warn("任务提交失败");
			throw new BusinessException("任务提交失败");
		}
		
		
		final Integer boxpackStatusFinal = boxpackStatus;
		//箱包信息
    	String[] boxs= boxpackTask.getBoxList().split(",");
		Set<String> boxSet = new HashSet<>(Arrays.asList(boxs));
		List<Boxpack> boxpackList = boxPackService.listByIds(boxSet);
		boxpackList.stream().forEach(f -> f.setStatusT(boxpackStatusFinal));
		save = boxPackService.updateBatchById(boxpackList);
		if(!save){
			log.warn("修改箱包状态失败");
			throw new BusinessException("修改箱包状态失败");
		}
		return true;
	}
	
	/**
	 * 查询当天的箱包列表数据
	 * @return
	 */
	public List<BoxpackDTO> getTodayBoxpackList(){
		Set<Long> boxIds = boxpackTaskService.getTodayIdsByType(0);
		if (boxIds.size() == 0){
			return Collections.EMPTY_LIST;
		}
		List<Boxpack> boxpackList = boxPackService.listByIds(boxIds);
		return boxpackList.stream().map(item -> {
			BoxpackDTO boxpackDTO = new BoxpackDTO();
			BeanUtils.copyProperties(item, boxpackDTO);
			return boxpackDTO;
		}).collect(Collectors.toList());
	}

	/**
	 * 
	 * @Title taskDel
	 * @Description 任务删除
	 * @param taskId
	 * @return
	 * @return 返回类型 boolean
	 */
	public boolean taskDel(Long taskId) {
		BoxpackTask boxpackTask = boxpackTaskService.getById(taskId);
    	if(null == boxpackTask){
    		log.warn("任务不存在");
			throw new BusinessException("任务不存在");
    	}
    	
    	if(!boxpackTask.getStatusT().equals(BoxpackTaskStatusEnum.CHECKED.getValue())){
    		log.warn("任务状态不正确");
			throw new BusinessException("任务状态不正确");
    	}
    	
    	Bank bank = bankService.getById(boxpackTask.getBankId());
    	if(bank.getBankCategory().equals(BankCategoryTypeEnum.STORAGE.getValue())){
    		log.warn("库房不能进行任务删除");
			throw new BusinessException("库房不能进行任务删除");
    	}
    	
		boxpackTask.setUpdateUser(UserContextHolder.getUserId());
		boxpackTask.setDeleted(DeleteFlagEnum.YES.getValue());
		boolean save = boxpackTaskService.updateById(boxpackTask);
		if(!save){
			log.warn("任务删除失败");
			throw new BusinessException("任务删除失败");
		}
		
    	//得到库房机构
    	Bank bankStorage = bankService.getStorageBank(boxpackTask.getDepartmentId());
    	
    	//得到库房任务
    	BoxpackTask storageTask = boxpackTaskService.getStorageTask(boxpackTask.getTaskDate(),
    			bankStorage.getId(),boxpackTask.getRouteNo(),boxpackTask.getTaskType());
    	
    	String[] boxAll= storageTask.getBoxList().split(",");
		Set<String> boxSetAll = new HashSet<>(Arrays.asList(boxAll));
		
		String[] boxThis = boxpackTask.getBoxList().split(",");
		Set<String> boxSetThis = new HashSet<>(Arrays.asList(boxThis));
		boxSetAll.removeAll(boxSetThis);
		
		
		QueryWrapper wrapper = Wrappers.query().eq("deleted",DeleteFlagEnum.NOT.getValue()).eq("task_date",boxpackTask.getTaskDate())
				.eq("bank_id",boxpackTask.getBankId()).eq("task_type",boxpackTask.getTaskType());
		List<BoxpackTask> taskList = boxpackTaskService.list(wrapper);
		
		if(taskList.size() <= 0){
			if(boxSetAll.size() == 0){
				storageTask.setDeleted(DeleteFlagEnum.YES.getValue());
				save = boxpackTaskService.updateById(storageTask);
				if(!save){
					log.warn("库房任务删除失败");
					throw new BusinessException("库房任务删除失败");
				}
				return true;
			}
		}else{
			taskList.stream().forEach(boxpackTaskTmp -> {
				String[] boxTmp = boxpackTaskTmp.getBoxList().split(",");
				Set<String> boxSetTmp = new HashSet<>(Arrays.asList(boxTmp));
				boxSetAll.addAll(boxSetTmp);
			});
		}
		
		Object[] array = boxSetAll.toArray();
	    String splitSet = StringUtils.join(array,",");
	    storageTask.setBoxList(splitSet);
		save = boxpackTaskService.updateById(storageTask);
		if(!save){
			log.warn("库房任务修改失败");
			throw new BusinessException("库房任务修改失败");
		}
		return true;
	}
	
	/**
	 * 查询当天护送人员数据
	 * @return
	 */
	public List<EscortDTO> getTodayEscortList(){
		Set<Long> routeIds = boxpackTaskService.getTodayIdsByType(1);
		if (routeIds.size() == 0){
			return Collections.EMPTY_LIST;
		}
		List<Route> routeList = routeService.listByIds(routeIds);
		Set<Long> escortIds = routeList.stream().flatMap(r -> Stream.of(r.getRouteKeyMan(),r.getRouteOperMan())).collect(Collectors.toSet());
		if (escortIds.size() == 0){
			return Collections.EMPTY_LIST;
		}
		List<Employee> employeeList = employeeService.listByIds(escortIds);
		return employeeList.stream().map(item -> {
			EscortDTO escortDTO = new EscortDTO();
			escortDTO.setId(item.getId());
			escortDTO.setNo(item.getEmpNo());
			escortDTO.setName(item.getEmpName());
			escortDTO.setPhoto(commonService.showImg(item.getPhotoUrl()));
			return escortDTO;
		}).collect(Collectors.toList());
	}

	/**
	 * 
	 * @Title taskInfo
	 * @Description 查询任务详情
	 * @param taskId
	 * @return
	 * @return 返回类型 Object
	 */
	public Object taskInfo(Long taskId) {
		BoxpackTask boxpackTask = boxpackTaskService.getById(taskId);
		if(null == boxpackTask){
    		log.warn("任务不存在");
			throw new BusinessException("任务不存在");
    	}
		
		BoxpackTaskInfoDTO infoDTO = new BoxpackTaskInfoDTO();
		Route route = null;
		if(!boxpackTask.getRouteId().equals(0L)){
			route = routeService.getById(boxpackTask.getRouteId());
		}
		
		if(boxpackTask.getStatusT().equals(BoxpackTaskStatusEnum.FINISH.getValue())){
			infoDTO.setKeyManName(boxpackTask.getHandEsortMansName().split(",")[0]);
			infoDTO.setOperManName(boxpackTask.getHandEsortMansName().split(",")[1]);
			infoDTO.setHandOpMansName(boxpackTask.getHandOpMansName());
		}else{
			if(route != null){
				Set<Long> empIds = new HashSet<>();
	  			empIds.add(route.getRouteKeyMan());
	  			empIds.add(route.getRouteOperMan());
	  			empIds.remove(0L);
	  			Map<Long,Employee> employeeMap = new HashMap<Long,Employee>();
		    	if(empIds.size()>0){
		    		List<Employee> employeeList = employeeService.listByIds(empIds);
		    		employeeMap = employeeList.stream().collect(Collectors.toMap(Employee::getId, Employee -> Employee));
		    	}
		    	Employee keyMan = employeeMap.get(route.getRouteKeyMan());
		    	Employee OperMan = employeeMap.get(route.getRouteOperMan());
				infoDTO.setKeyManName(keyMan == null ? "" : keyMan.getEmpName());
				infoDTO.setOperManName(OperMan == null ? "" : OperMan.getEmpName());
			}
		}
		
		if(route != null){
			Vehicle vehicle = null;
			if(route.getVehicleId() != 0L){
				vehicle = vehicleService.getById(route.getVehicleId());
			}
			infoDTO.setLpno(vehicle == null ? "" : vehicle.getLpno());
		}
		
		String[] boxs= boxpackTask.getBoxList().split(",");
		Set<String> boxSet = new HashSet<>(Arrays.asList(boxs));
		List<Boxpack> boxpackList = boxPackService.listByIds(boxSet);
		
		List<BoxpackDTO> boxpackDTOs = new ArrayList<>();
		boxpackList.stream().forEach(boxpack -> {
			BoxpackDTO boxpackDTO = new BoxpackDTO();
			boxpackDTO.setBoxNo(boxpack.getBoxNo());
			boxpackDTO.setBoxName(boxpack.getBoxName());
			boxpackDTO.setStatusT(boxpack.getStatusT());
			boxpackDTOs.add(boxpackDTO);
		});
		
		boxpackDTOs.sort((x, y) -> x.getBoxNo().compareTo(y.getBoxNo()));
		infoDTO.setBoxpackList(boxpackDTOs);
		return infoDTO;
	}

}
