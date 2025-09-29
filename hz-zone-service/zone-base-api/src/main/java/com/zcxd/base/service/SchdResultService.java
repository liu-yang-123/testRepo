package com.zcxd.base.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.base.config.UserContextHolder;
import com.zcxd.base.dto.SchdResultInfoDTO;
import com.zcxd.base.dto.SchdResultRowDTO;
import com.zcxd.base.exception.BusinessException;
import com.zcxd.base.vo.SchdResultChangeVO;
import com.zcxd.common.constant.DeleteFlagEnum;
import com.zcxd.common.util.DateTimeUtil;
import com.zcxd.common.util.ExcelImportUtil;
import com.zcxd.common.util.Result;
import com.zcxd.db.mapper.SchdResultMapper;
import com.zcxd.db.mapper.SchdResultRecordMapper;
import com.zcxd.db.model.Employee;
import com.zcxd.db.model.SchdResult;
import com.zcxd.db.model.SchdResultRecord;

import lombok.AllArgsConstructor;

/**
 * <p>
 * 排班结果 服务实现类
 * </p>
 *
 * @author admin
 * @since 2021-07-05
 */
@AllArgsConstructor
@Service
public class SchdResultService extends ServiceImpl<SchdResultMapper, SchdResult> {

    private SchdResultRecordService schdResultRecordService;
    
    private EmployeeService employeeService;
    
    private SchdResultMapper resultMapper;
    
    /**
     * @Description:获取排班结果列表
     * @Author: lilanglang
     * @Date: 2021/7/7 17:14
     * @param page:
     * @param limit:
     * @param schdResultDTO:
     **/
    public IPage<SchdResult> listResult(Integer page, Integer limit, SchdResultRowDTO schdResultDTO) {
        QueryWrapper<SchdResult> queryWrapper = Wrappers.query();
        IPage<SchdResult> ipage=new Page<>(page,limit);
        if(schdResultDTO != null) {
            getQueryWrapper(schdResultDTO, queryWrapper);
        }
        return baseMapper.selectPage(ipage, queryWrapper);
    }

    /**
     * 用id获取
     * @param ids id序列
     * @param departmentId 部门ID
     * @return 结果
     */
    public List<SchdResult> listByIds(List<Long> ids, Long departmentId) {
        LambdaQueryWrapper<SchdResult> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SchdResult::getId, ids);
        queryWrapper.eq(SchdResult::getDepartmentId, departmentId);
        queryWrapper.eq(SchdResult::getDeleted, 0);

        return baseMapper.selectList(queryWrapper);
    }

    /**
     * 查询条件封装
     * @param schdResultDTO
     * @param queryWrapper
     */
    private void getQueryWrapper(SchdResultRowDTO schdResultDTO, QueryWrapper<SchdResult> queryWrapper) {
        if (schdResultDTO.getPlanDay()!=null) {
            queryWrapper.eq("plan_day", schdResultDTO.getPlanDay());
        }
        if (schdResultDTO.getEmpId()!=null) {
            Long empId = schdResultDTO.getEmpId();
            queryWrapper.and(wrapper -> {
                wrapper.and(whereWrapper ->{
                    whereWrapper.or(itemWrapper ->{
                        itemWrapper.eq("driver",empId);
                    });
                    whereWrapper.or(itemWrapper ->{
                        itemWrapper.eq("scurity_a",empId);
                    });
                    whereWrapper.or(itemWrapper ->{
                        itemWrapper.eq("scurity_b",empId);
                    });
                    whereWrapper.or(itemWrapper ->{
                        itemWrapper.eq("key_man",empId);
                    });
                    whereWrapper.or(itemWrapper ->{
                        itemWrapper.eq("op_man",empId);
                    });
                });
            });
        }
        if (schdResultDTO.getRouteNo()!=null && !"".equals(schdResultDTO.getRouteNo())) {
            queryWrapper.eq("route_no", schdResultDTO.getRouteNo());
        }
        if (schdResultDTO.getDepartmentId() != null){
            queryWrapper.eq("department_id", schdResultDTO.getDepartmentId());
        }
        queryWrapper.eq("deleted", 0);
        queryWrapper.orderBy(true,false,"plan_day");
    }

    /**
     * @Description:获取符合条件的信息
     * @Author: lilanglang
     * @Date: 2021/7/8 19:01
     * @param schdResultDTO:
     **/
    public List<SchdResult> getResultList(SchdResultRowDTO schdResultDTO) {
        QueryWrapper<SchdResult> queryWrapper = new QueryWrapper<>();
        if(schdResultDTO != null) {
            getQueryWrapper(schdResultDTO, queryWrapper);
        }
        queryWrapper.eq("deleted", 0);
        queryWrapper.orderByAsc("route_no * 1, create_time");
        return baseMapper.selectList(queryWrapper);
    }

    /**
     * 线路切换更新操作
     * @param changeVOList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean change(List<SchdResultChangeVO> changeVOList){
        List<Long> idList = changeVOList.stream().map(SchdResultChangeVO::getId).collect(Collectors.toList());
        if (idList == null || idList.size() != 2){
            throw new BusinessException(1,"数据错误");
        }
        List<SchdResult> resultList = baseMapper.selectBatchIds(idList);

        SchdResult schdResult = new SchdResult();
        changeVOList.stream().forEach(t -> {
            BeanUtils.copyProperties(t, schdResult);
            schdResult.setUpdateTime(DateTimeUtil.getCurrentTimeStampMs());
            schdResult.setUpdateUser(UserContextHolder.getUserId());
            baseMapper.updateById(schdResult);
        });
        String content = resultList.stream().map(SchdResult::getRouteNo).collect(Collectors.joining(","));
        SchdResultRecord record = new SchdResultRecord();
        record.setPlanDay(resultList.get(0).getPlanDay());
        record.setDepartmentId(schdResult.getDepartmentId());
        record.setCreateTime(System.currentTimeMillis());
        record.setCategory(1);
        record.setContent("线路数据切换【"+content+"】");
        record.setOpType(0);
        long userId = UserContextHolder.getUserId();
        record.setCreateUser(userId);
        schdResultRecordService.save(record);
        return true;
    }

    /**
     * 检查当前选择的元素是否唯一
     * @param infoDTO
     */
    public void check(SchdResultInfoDTO infoDTO, SchdResult schdResult){
        long planDay = schdResult.getPlanDay();
        //查询其他所有线路数据
        QueryWrapper wrapper = Wrappers.query().eq("plan_day",planDay)
                .notIn("id",schdResult.getId())
                .eq("deleted", DeleteFlagEnum.NOT.getValue());
        List<SchdResult>  resultList = baseMapper.selectList(wrapper);

        resultList.stream().forEach(t -> {
            String routeNo = t.getRouteNo();
            if (!"".equals(t.getVehicleNo()) && t.getVehicleNo().equals(infoDTO.getVehicleNo())){
                throw new BusinessException(1,"该车牌已存在于"+routeNo+"号线");
            }
            if(t.getDriver() > 0 && t.getDriver().equals(infoDTO.getDriver())){
                throw new BusinessException(1,"该司机已存在于"+routeNo+"号线");
            }
            //护卫A
            if(t.getScurityA() > 0 && (t.getScurityA().equals(infoDTO.getScurityA()) || t.getScurityA().equals(infoDTO.getScurityB()))){
                throw new BusinessException(1,"该护卫已存在于"+routeNo+"号线");
            }
            //护卫B
            if(t.getScurityB() > 0 && (t.getScurityB().equals(infoDTO.getScurityA()) || t.getScurityB().equals(infoDTO.getScurityB()))){
                throw new BusinessException(1,"该护卫已存在于"+routeNo+"号线");
            }
            if(t.getKeyMan() > 0 && t.getKeyMan().equals(infoDTO.getKeyMan())){
                throw new BusinessException(1,"该钥匙员已存在于"+routeNo+"号线");
            }
            if(t.getOpMan() > 0 && t.getOpMan().equals(infoDTO.getOpMan())){
                throw new BusinessException(1,"该密码员已存在于"+routeNo+"号线");
            }
        });
    }

	public Result importSchd(Long planDay,Long departmentId,MultipartFile file) {
		
    	//线路编号位置
    	int routeNoCell = 0;
    	//密码员位置
    	int opManCell = 1;
    	//钥匙员位置
    	int keyManCell = 4;
    	//代理车长位置
    	int leaderCell = 9;
    	//线路编号后字符
    	String routeNoBack = "号线";
    	
    	//获取当天排班列表
    	QueryWrapper<SchdResult> queryWrapper = Wrappers.query();
    	SchdResultRowDTO schdResultDTO = new SchdResultRowDTO();
    	schdResultDTO.setPlanDay(planDay);
        getQueryWrapper(schdResultDTO, queryWrapper);
        List<SchdResult> schdResultList = baseMapper.selectList(queryWrapper);
        
        if(schdResultList == null || schdResultList.size() == 0){
        	return Result.fail(DateTimeUtil.getDateTimeDisplayString(planDay) + "没有排班记录，请先发起排班");
        }
        Map<String,Long> schdResultMap = schdResultList.stream().collect(Collectors.toMap(SchdResult::getRouteNo,SchdResult::getId));
    	
        //获取钥匙、密码人员列表
        List<Employee> employeeList = new ArrayList<>();
    	Integer[] jobTypeArr = new Integer[]{3,4};
    	for (Integer jobType : jobTypeArr) {
        	Employee employee = new Employee();
        	employee.setDepartmentId(departmentId);
    		employee.setJobType(jobType);
    		employee.setStatusT(0);
    		List<Employee> employeeByCondition = employeeService.getEmployeeByCondition(employee);
    		employeeList.addAll(employeeByCondition);
		}
    	Map<String,Long> employeeMap = employeeList.stream().collect(Collectors.toMap(Employee::getEmpName,Employee::getId));
        
    	List<SchdResult> schdResultEditList = new ArrayList<>();
    	
    	try {
    		String extString = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        	Workbook wb = ExcelImportUtil.readExcel(file.getInputStream(),extString);

        	if(wb == null){
        		return Result.fail("读取excel文件失败");
        	}
        	
    		Sheet sheet = wb.getSheetAt(0);
        	int x = 0;
    		for (int i = 2; x == 0; i++) {
    			//判断线路是否存在
    			String routeNo = ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(routeNoCell)).toString();
    			if(routeNo.equals("农商")){
    				x = 1;
    				if(!schdResultMap.containsKey("60") && !schdResultMap.containsKey("61")){
    					continue;
    				}
    				routeNo = schdResultMap.containsKey("60") ? "60" : "61";
    			}else{
    				routeNo = routeNo.substring(0,routeNo.indexOf(routeNoBack));
    				if(!schdResultMap.containsKey(routeNo)){
    					continue;
    				}
    			}
    			
    			
    			//判断人员是否存在
    			String keyMan = ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(keyManCell)).toString();
    			String opMan = ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(opManCell)).toString();
    			String leader = ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(leaderCell)).toString();
    			
    			if(!employeeMap.containsKey(keyMan)){
    				return Result.fail(keyMan + "不在系统钥匙密码人员列表中");
    			}
    			
    			if(!employeeMap.containsKey(opMan)){
    				return Result.fail(opMan + "不在系统钥匙密码人员列表中");
    			}
    			
    			if(!employeeMap.containsKey(leader)){
    				return Result.fail(leader + "不在系统钥匙密码人员列表中");
    			}
    			
    			SchdResult schdResult = new SchdResult();
    			schdResult.setId(schdResultMap.get(routeNo));
    			schdResult.setKeyMan(employeeMap.get(keyMan));
    			schdResult.setOpMan(employeeMap.get(opMan));
    			schdResult.setLeader(employeeMap.get(leader));
    			schdResultEditList.add(schdResult);
    			
//    			if(ExcelImportUtil.getCellFormatValue(sheet.getRow(i).getCell(routeNoCell)).toString().replace(" ", "").equals("农商")){
//    				break;
//    			}
    		}
		} catch (Exception e) {
			return Result.fail("文件解析出错，请检查所选文件是否正确");
		}
    	
    	if(schdResultEditList.size() == 0){
    		return Result.fail("所选excel中没有有效数据，请检查所选文件是否正确");
    	}
    	
    	resultMapper.updateAll(schdResultEditList);
    	
    	return Result.success();
	}

}

