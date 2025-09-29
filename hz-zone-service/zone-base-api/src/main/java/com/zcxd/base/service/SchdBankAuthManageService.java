package com.zcxd.base.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.base.dto.SchdCleanPassAuthDTO;
import com.zcxd.base.dto.SchdVacatePlanDTO;
import com.zcxd.base.dto.SchdVacateSettingDTO;
import com.zcxd.base.exception.BusinessException;
import com.zcxd.base.exception.ExceptionCode;
import com.zcxd.base.vo.SchdCleanPassCodeVO;
import com.zcxd.common.constant.DeleteFlagEnum;
import com.zcxd.common.constant.PassTypeEnum;
import com.zcxd.common.util.ResultList;
import com.zcxd.db.mapper.SchdCleanPassAuthMapper;
import com.zcxd.db.model.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 清机通行证授权 服务实现类
 * </p>
 *
 * @author admin
 * @since 2021-07-05
 */
@Service
public class SchdBankAuthManageService {

    @Resource
    SchdCleanPassAuthService schdCleanPassAuthService;
    @Resource
    SysUserService sysUserService;
    @Resource
    EmployeeService employeeService;
    @Resource
    EmployeeJobService employeeJobService;
    @Resource
    BankService bankService;


    private boolean validatePassType(int passType) {
        return  (passType == PassTypeEnum.CLEAN_CODE.getValue()
                || passType == PassTypeEnum.SUB_BANK.getValue());
    }
    /**
     * 保存
     * @param schdCleanPassCodeVO
     */
    public boolean save(SchdCleanPassCodeVO schdCleanPassCodeVO) throws BusinessException {
        boolean isOk = this.validatePassType(schdCleanPassCodeVO.getPassType());
        if (!isOk) {
            throw new BusinessException(ExceptionCode.BusinessError,"无效参数passType");
        }
        SchdCleanPassAuth schdCleanPassAuth = schdCleanPassAuthService.getByEmpId(schdCleanPassCodeVO.getBankId(),schdCleanPassCodeVO.getEmpId());
        if (null != schdCleanPassAuth) {
            throw new BusinessException(ExceptionCode.BusinessError,"不能重复添加人员");
        }

        SchdCleanPassAuth newSchdCleanPassAuth = new SchdCleanPassAuth();
        BeanUtils.copyProperties(schdCleanPassCodeVO,newSchdCleanPassAuth);
        newSchdCleanPassAuth.setId(null);
        return schdCleanPassAuthService.save(newSchdCleanPassAuth);
    }


    public boolean update(SchdCleanPassCodeVO schdCleanPassCodeVO) {
        boolean isOk = this.validatePassType(schdCleanPassCodeVO.getPassType());
        if (!isOk) {
            throw new BusinessException(ExceptionCode.BusinessError,"无效参数passType");
        }
        SchdCleanPassAuth schdCleanPassAuth = schdCleanPassAuthService.getById(schdCleanPassCodeVO.getId());
        if (null == schdCleanPassAuth) {
            throw new BusinessException(ExceptionCode.BusinessError,"无效参数id");
        }
        SchdCleanPassAuth schdCleanPassAuthTmp = schdCleanPassAuthService.getByEmpId(schdCleanPassCodeVO.getBankId(),schdCleanPassCodeVO.getEmpId());
        if (null != schdCleanPassAuthTmp && schdCleanPassAuthTmp.getId().longValue() != schdCleanPassAuth.getId().longValue()) {
            throw new BusinessException(ExceptionCode.BusinessError,"不能重复添加人员");
        }

        SchdCleanPassAuth newSchdCleanPassAuth = new SchdCleanPassAuth();
        BeanUtils.copyProperties(schdCleanPassCodeVO,newSchdCleanPassAuth);
        newSchdCleanPassAuth.setDepartmentId(null);
        return schdCleanPassAuthService.updateById(newSchdCleanPassAuth);
    }

    public boolean delete(Long id) {
        SchdCleanPassAuth schdCleanPassAuth = schdCleanPassAuthService.getById(id);
        if (null == schdCleanPassAuth) {
            throw new BusinessException(ExceptionCode.BusinessError,"无效参数id");
        }
        SchdCleanPassAuth newSchdCleanPassAuth = new SchdCleanPassAuth();
        newSchdCleanPassAuth.setId(id);
        newSchdCleanPassAuth.setDeleted(DeleteFlagEnum.YES.getValue());
        return schdCleanPassAuthService.updateById(newSchdCleanPassAuth);
    }

    /**
     * 查询详情
     * @param id
     * @return
     */
    public SchdCleanPassAuthDTO getInfo(Long id) {
        SchdCleanPassAuth schdCleanPassAuth = schdCleanPassAuthService.getById(id);
        if (null == schdCleanPassAuth) {
            throw new BusinessException(ExceptionCode.BusinessError,"无效参数id");
        }
        SchdCleanPassAuthDTO schdCleanPassAuthDTO = new SchdCleanPassAuthDTO();
        BeanUtils.copyProperties(schdCleanPassAuth,schdCleanPassAuthDTO);

        if (schdCleanPassAuth.getCreateUser() != 0L) {
            SysUser sysUser = sysUserService.getById(schdCleanPassAuth.getCreateUser());
            if (null == sysUser) {
                schdCleanPassAuthDTO.setCreateUserName(sysUser.getUsername());
            }
        }
        if (schdCleanPassAuth.getUpdateUser() != 0L) {
            SysUser sysUser = sysUserService.getById(schdCleanPassAuth.getUpdateUser());
            if (null == sysUser) {
                schdCleanPassAuthDTO.setUpdateUserName(sysUser.getUsername());
            }
        }
        return schdCleanPassAuthDTO;
    }

    public ResultList<SchdCleanPassAuthDTO> listPage(int page, int limit,long departmentId,int passType, Long bankId, Long empId) {
        List<Employee> employeeList = null;
        Set<Long> empIds = new HashSet<Long>();
        if (empId != null){
            empIds.add(empId);
        }
        IPage<SchdCleanPassAuth> resultPage = schdCleanPassAuthService.listPage(page,limit,departmentId,passType,bankId,empIds);
        empIds = resultPage.getRecords().stream().map(SchdCleanPassAuth::getEmpId).collect(Collectors.toSet());
        Map<Long, Employee> employeeMap = new HashMap<>();
        if (empIds.size() > 0){
            employeeList = employeeService.listByIds(empIds);
            employeeMap = employeeList.stream().collect(Collectors.toMap(Employee::getId, r -> r));
        }
        //查询银行网点名称
        List<Bank> bankList = null;
        Map<Long, Bank> bankMap = new HashMap<>();
        Set<Long> bankIds = resultPage.getRecords().stream().map(SchdCleanPassAuth::getBankId).collect(Collectors.toSet());
        if (bankIds.size() > 0){
            bankList = bankService.listByIds(bankIds);
            bankMap = bankList.stream().collect(Collectors.toMap(Bank::getId, r -> r));
        }

        Map<Long, Bank> finalBankMap = bankMap;
        Map<Long, Employee> finalEmployeeMap = employeeMap;
        List<SchdCleanPassAuthDTO> authDTOList = resultPage.getRecords().stream().map(item -> {
            SchdCleanPassAuthDTO authDTO = new SchdCleanPassAuthDTO();
            BeanUtils.copyProperties(item,authDTO);
            Employee employee = finalEmployeeMap.get(item.getEmpId());
            if(employee != null){
                authDTO.setEmpName(employee.getEmpName());
                authDTO.setJobType(employee.getJobType());
            }
            Bank bank = finalBankMap.get(item.getBankId());
            if (bank != null) {
                authDTO.setBankName(bank.getFullName());
                authDTO.setBankId(bank.getId());
                authDTO.setRouteNo(bank.getRouteNo());
            }
            return authDTO;
        }).collect(Collectors.toList());
        return ResultList.builder().total(resultPage.getTotal()).list(authDTOList).build();
    }
}
