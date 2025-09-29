package com.zcxd.pda.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.common.constant.Constant;
import com.zcxd.common.constant.DeleteFlagEnum;
import com.zcxd.common.util.ResultList;
import com.zcxd.db.mapper.BankTellerMapper;
import com.zcxd.db.mapper.FingerprintMapper;
import com.zcxd.db.model.*;
import com.zcxd.pda.config.UserContextHolder;
import com.zcxd.pda.dto.EmployeeUserDTO;
import com.zcxd.pda.exception.BusinessException;
import com.zcxd.pda.vo.BankTellerVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 
 * @ClassName SysLogServiceImpl
 * @Description 日志管理服务类
 * @author 秦江南
 * @Date 2021年5月7日上午10:50:05
 */
@Service
public class BankTellerService extends ServiceImpl<BankTellerMapper, BankTeller> {

    @Autowired
    private FingerprintMapper fingerprintMapper;

    /**
     * 根据柜员号查询
     * @param tellerNo
     * @return
     */
    public BankTeller getByTellerNo(String tellerNo, Long bankId) {
        BankTeller where = new BankTeller();
        where.setDeleted(0);
        where.setStatusT(0);
        where.setBankId(bankId);
        where.setTellerNo(tellerNo);
        return baseMapper.selectOne(new QueryWrapper<>(where));
    }

    /**
     * 查询可用的柜员用户
     * @param id
     * @return
     */
    public BankTeller getTellerById(long id) {
        BankTeller teller = baseMapper.selectById(id);
        if (null != teller && teller.getDeleted() == 0 && teller.getStatusT() == 0) {
            return teller;
        }
        return null;
    }

    /**
     * 银行员工列表
     * @param userId
     * @param page
     * @param limit
     * @return
     * @throws BusinessException
     */
    public ResultList listEmployee(Long userId, Integer page, Integer limit) throws BusinessException {
        BankTeller bankTeller = this.baseMapper.selectById(userId);
        if (bankTeller == null){
            throw new BusinessException("数据错误");
        }
        IPage<BankTeller> iPage = new Page<>(page,limit);

        QueryWrapper<BankTeller> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("bank_id", bankTeller.getBankId()).eq("deleted", DeleteFlagEnum.NOT.getValue());

        IPage<BankTeller> bankTellerIPage = this.baseMapper.selectPage(iPage,queryWrapper);
        long total = bankTellerIPage.getTotal();

        //提取银行员工ID
        Set<Long> tellerIds = bankTellerIPage.getRecords().stream().map(BankTeller::getId).collect(Collectors.toSet());
        Map<Long,List<Fingerprint>> printGroupMap = new HashMap<>();
        if (tellerIds.size() > 0){
            QueryWrapper wrapper = Wrappers.query().in("user_id",tellerIds).eq("user_type",1)
                    .eq("deleted",0);
            List<Fingerprint> fingerprintList = fingerprintMapper.selectList(wrapper);
            printGroupMap = fingerprintList.stream().collect(Collectors.groupingBy(Fingerprint::getUserId));
        }

        Map<Long, List<Fingerprint>> finalPrintGroupMap = printGroupMap;
        List<EmployeeUserDTO> employeeUserDTOList = bankTellerIPage.getRecords().stream().map(item -> {
            EmployeeUserDTO employeeUserDTO = new EmployeeUserDTO();
            employeeUserDTO.setId(item.getId());
            employeeUserDTO.setEmpNo(item.getTellerNo());
            employeeUserDTO.setEmpName(item.getTellerName());
            employeeUserDTO.setMobile(item.getMobile());
            employeeUserDTO.setManagerFlag(item.getManagerFlag());
            employeeUserDTO.setComment(item.getComments());
            List<Fingerprint> fingerprintList = finalPrintGroupMap.get(item.getId());
            if (fingerprintList != null && fingerprintList.size() > 0){
                employeeUserDTO.setIsFingerPrint(1);
            } else{
                employeeUserDTO.setIsFingerPrint(0);
            }
            return employeeUserDTO;
        }).collect(Collectors.toList());
        return ResultList.builder().total(total).list(employeeUserDTOList).build();
    }

    /**
     * 添加用户
     * @param bankTellerVO
     * @return
     */
    public boolean create(BankTellerVO bankTellerVO){
        //获取当前用户
        PdaUser user = UserContextHolder.getUser();
        Integer userType = user.getUserType();
        if (userType == Constant.USER_EMPLOYEE){
            return false;
        }
        Long id = user.getId();
        //查询当前用户
        BankTeller currentTeller = this.baseMapper.selectById(id);
        if (currentTeller == null){
            return false;
        }
        //验证员工编号是否已添加
        BankTeller bankTellerT = this.baseMapper.getByTellerNo(bankTellerVO.getTellerNo(), currentTeller.getBankId(), 0L);
        if (bankTellerT != null){
            throw new BusinessException("员工编号已经存在");
        }
        BankTeller bankTeller = new BankTeller();
        bankTeller.setTellerNo(bankTellerVO.getTellerNo());
        bankTeller.setBankId(currentTeller.getBankId());
        bankTeller.setDepartmentId(currentTeller.getDepartmentId());
        bankTeller.setTellerName(bankTellerVO.getTellerName());
        bankTeller.setMobile(bankTellerVO.getMobile());
        bankTeller.setPassword(DigestUtils.md5DigestAsHex(Constant.USER_DEFAULT_PWD.getBytes()).toUpperCase());
        bankTeller.setManagerFlag(bankTellerVO.getManagerFlag());
        bankTeller.setComments(bankTellerVO.getComments());
        bankTeller.setCreateUser(0L);
        bankTeller.setUpdateUser(0L);
        int result = this.baseMapper.insert(bankTeller);
        return result >= 1;
    }

    /**
     * 修改用户
     * @param bankTellerVO
     * @return
     */
    public boolean edit(BankTellerVO bankTellerVO){
        //获取当前用户
        PdaUser user = UserContextHolder.getUser();
        Integer userType = user.getUserType();
        if (userType == Constant.USER_EMPLOYEE){
            return false;
        }
        Long id = user.getId();
        //查询当前用户
        BankTeller currentTeller = this.baseMapper.selectById(id);
        if (currentTeller == null){
            return false;
        }
        //验证员工编号是否已添加
        BankTeller bankTellerT = this.baseMapper.getByTellerNo(bankTellerVO.getTellerNo(), currentTeller.getBankId(), bankTellerVO.getId());
        if (bankTellerT != null){
            throw new BusinessException("员工编号已经存在");
        }
        BankTeller bankTeller = new BankTeller();
        bankTeller.setId(bankTellerVO.getId());
        bankTeller.setTellerNo(bankTellerVO.getTellerNo());
        bankTeller.setTellerName(bankTellerVO.getTellerName());
        bankTeller.setMobile(bankTellerVO.getMobile());
        bankTeller.setManagerFlag(bankTellerVO.getManagerFlag());
        bankTeller.setComments(bankTellerVO.getComments());
        bankTeller.setCreateUser(0L);
        bankTeller.setUpdateUser(0L);
        int result = this.baseMapper.updateById(bankTeller);
        return result >= 1;
    }

    /**
     * 删除员工
     * @param id
     * @return
     */
    public boolean delete(Long id){
        //TODO 验证删除条件
        BankTeller bankTeller = new BankTeller();
        bankTeller.setId(id);
        bankTeller.setDeleted(DeleteFlagEnum.YES.getValue());
        bankTeller.setUpdateUser(0L);
        int result = this.baseMapper.updateById(bankTeller);
        //删除指纹
        Fingerprint fingerprint = new Fingerprint();
        fingerprint.setDeleted(DeleteFlagEnum.YES.getValue());
        QueryWrapper wrapper = Wrappers.query().in("user_id",id).eq("user_type",1)
                .eq("deleted",0);
        fingerprintMapper.update(fingerprint,wrapper);
        return result >= 1;
    }

    /**
     * 银行机构返回员工ID
     * @param bankId
     * @return
     */
    public Map<Long,String> getTellerMap(Long bankId){
        QueryWrapper wrapper = Wrappers.query().eq("bank_id",bankId).eq("deleted",DeleteFlagEnum.NOT.getValue());
        List<BankTeller> bankTellerList = this.baseMapper.selectList(wrapper);
        return bankTellerList.stream().collect(Collectors.toMap(BankTeller::getId,BankTeller::getTellerName));
    }
}
