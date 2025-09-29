package com.zcxd.base.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.base.config.UserContextHolder;
import com.zcxd.base.dto.ClearErrorCancelDTO;
import com.zcxd.base.dto.ClearErrorCheckDTO;
import com.zcxd.base.dto.TradeClearErrorDto;
import com.zcxd.base.exception.BusinessException;
import com.zcxd.common.constant.CheckTypeEnum;
import com.zcxd.common.constant.ClearErrorStatusEnum;
import com.zcxd.common.util.DateTimeUtil;
import com.zcxd.common.util.Result;
import com.zcxd.db.mapper.BankMapper;
import com.zcxd.db.mapper.DenomMapper;
import com.zcxd.db.mapper.TradeClearErrorMapper;
import com.zcxd.db.model.Bank;
import com.zcxd.db.model.Denom;
import com.zcxd.db.model.TradeClearError;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 商业清分-清分差错 服务类
 * </p>
 *
 * @author admin
 * @since 2020-09-16
 */
@Service
public class TradeClearErrorService extends ServiceImpl<TradeClearErrorMapper, TradeClearError> {
    @Resource
    private DenomMapper denomMapper;
    @Resource
    private BankMapper bankMapper;

    private DecimalFormat decimalFormat = new DecimalFormat("###,##0.0");


    public Map<String,Object> findListByPage(Integer page,
                                             Integer limit,
                                             Long departmentId,
                                             Long bankId,
                                             Long dateBegin,
                                             Long dateEnd,
                                             Long denomId,
                                             String findName){
        IPage<TradeClearError> wherePage = new Page<>(page, limit);
        QueryWrapper<TradeClearError> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("department_id",departmentId)
                .eq(bankId != null,"bank_id",bankId)
                .eq(denomId != null,"denom_id",denomId)
                .like(!StringUtils.isEmpty(findName),"clear_man",findName);

        if (dateBegin != null && dateBegin != 0L) {
            Long dayTimeBegin = DateTimeUtil.getDailyStartTimeMs(dateBegin);
            queryWrapper.ge("clear_date",dayTimeBegin);
        }
        if (dateEnd != null && dateEnd != 0L) {
            Long dayTimeEnd = DateTimeUtil.getDailyEndTimeMs(dateEnd);
            queryWrapper.le("clear_date",dayTimeEnd);
        }
        queryWrapper.orderBy(true, false, "clear_date");

        IPage<TradeClearError> errorIPage = baseMapper.selectPage(wherePage, queryWrapper);

        if (errorIPage.getRecords().size() == 0) {
            Map<String,Object> resultMap = new HashMap<>();
            resultMap.put("total",errorIPage.getTotal());
            resultMap.put("list",errorIPage.getRecords());
            return resultMap;
        }
        Set<Long> bankIdSet = new HashSet<>();
        Set<Long> denomIdSet = new HashSet<>();

        errorIPage.getRecords().forEach(clearError -> {
            if (!denomIdSet.contains(clearError.getDenomId())) {
                denomIdSet.add(clearError.getDenomId());
            }
            if (!bankIdSet.contains(clearError.getBankId())) {
                bankIdSet.add(clearError.getBankId());
            }
        });
        List<Denom> clearDenomList = denomMapper.selectBatchIds(denomIdSet);
        Map<Long, Denom> denomMap = clearDenomList.stream().collect(Collectors.toMap(Denom::getId, Function.identity()));

        List<Bank> banknodeList = bankMapper.selectBatchIds(bankIdSet);
        Map<Long, String> bankNameMap = banknodeList.stream().collect(Collectors.toMap(Bank::getId, Bank::getShortName));

        List<JSONObject> jsonObjectList = new ArrayList<>();

        errorIPage.getRecords().forEach(clearError -> {
            JSONObject jsonObject = (JSONObject)JSONObject.toJSON(clearError);
            Denom clearDenom = denomMap.get(clearError.getDenomId());
            if (clearDenom != null) {
                jsonObject.put("denomName", clearDenom.getName());
                jsonObject.put("denomValue", clearDenom.getValue());
                jsonObject.put("curCode", clearDenom.getCurCode());
            }
            String bankName = bankNameMap.getOrDefault(clearError.getBankId(),"");
            jsonObject.put("banknodeName", bankName);
            jsonObjectList.add(jsonObject);
        });
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("total",errorIPage.getTotal());
        resultMap.put("list",jsonObjectList);
        return resultMap;
    }


    /**
     * 新增差错记录（批量）
     * @param clearErrorDtoList
     * @return
     * @throws BusinessException
     */
    public boolean addMulti(List<TradeClearErrorDto> clearErrorDtoList) throws BusinessException {


        List<TradeClearError> tradeClearErrorList = clearErrorDtoList.stream().map(tradeClearErrorDto -> {
            TradeClearError clearError = new TradeClearError();
            clearError.setDepartmentId(tradeClearErrorDto.getDepartmentId());
            clearError.setClearDate(tradeClearErrorDto.getClearDate());
            clearError.setCreateDay(DateTimeUtil.timeStampMs2Date(tradeClearErrorDto.getClearDate(),"yyyyMMdd"));
            clearError.setDenomId(tradeClearErrorDto.getDenomId());
            clearError.setBankId(tradeClearErrorDto.getBankId());
            clearError.setCashOverCount(tradeClearErrorDto.getCashOverCount());
            clearError.setCashOverAmount(tradeClearErrorDto.getCashOverAmount());
            clearError.setCashShortCount(tradeClearErrorDto.getCashShortCount());
            clearError.setCashShortAmount(tradeClearErrorDto.getCashShortAmount());
            clearError.setFakeCount(tradeClearErrorDto.getFakeCount());
            clearError.setFakeAmount(tradeClearErrorDto.getFakeAmount());
            clearError.setCarryCount(tradeClearErrorDto.getCarryCount());
            clearError.setCarryAmount(tradeClearErrorDto.getCarryAmount());
            clearError.setComments(tradeClearErrorDto.getComments());
            clearError.setErrorMan(tradeClearErrorDto.getErrorMan());
            clearError.setClearMan(tradeClearErrorDto.getClearMan());
            clearError.setCheckMan(tradeClearErrorDto.getCheckMan());
            clearError.setSealDate(tradeClearErrorDto.getSealDate());
            clearError.setSealMan(tradeClearErrorDto.getSealMan());
            clearError.setCreateUser(UserContextHolder.getUserId());
            clearError.setCreateTime(System.currentTimeMillis());
            return clearError;
        }).collect(Collectors.toList());

        return this.saveBatch(tradeClearErrorList);
    }

    /**
     * 删除差错记录
     * @param id
     * @return
     */
    public boolean delete(Long id){
        int iRet = baseMapper.deleteById(id);
        return iRet > 0;
    }

    /**
     * 更新清分记录
     * @param id
     * @param clearErrorDto
     * @return
     */
    public boolean edit(Long id, TradeClearErrorDto clearErrorDto) throws BusinessException{

        TradeClearError clearError = new TradeClearError();

        clearError.setId(id);
        clearError.setDenomId(clearErrorDto.getDenomId());
        clearError.setClearDate(clearErrorDto.getClearDate());
        clearError.setCreateDay(DateTimeUtil.timeStampMs2Date(clearErrorDto.getClearDate(),"yyyyMMdd"));
        clearError.setCashOverCount(clearErrorDto.getCashOverCount());
        clearError.setCashOverAmount(clearErrorDto.getCashOverAmount());
        clearError.setCashShortCount(clearErrorDto.getCashShortCount());
        clearError.setCashShortAmount(clearErrorDto.getCashShortAmount());
        clearError.setFakeCount(clearErrorDto.getFakeCount());
        clearError.setFakeAmount(clearErrorDto.getFakeAmount());
        clearError.setCarryCount(clearErrorDto.getCarryCount());
        clearError.setCarryAmount(clearErrorDto.getCarryAmount());
        clearError.setErrorMan(clearErrorDto.getErrorMan());
        clearError.setClearMan(clearErrorDto.getClearMan());
        clearError.setCheckMan(clearErrorDto.getCheckMan());
        clearError.setSealMan(clearErrorDto.getSealMan());
        clearError.setSealDate(clearErrorDto.getSealDate());
        clearError.setComments(clearErrorDto.getComments());
        clearError.setUpdateUser(UserContextHolder.getUserId());
        clearError.setUpdateTime(System.currentTimeMillis());
        int iRet = baseMapper.updateById(clearError);
        return iRet > 0;
    }

//    public JSONObject findById(Long id) throws BusinessException{
//        ClearError clearError = baseMapper.selectById(id);
//        if (clearError == null) {
//            throw new BusinessException("无效id");
//        }
//        ClearBanknode clearBanknode = clearBanknodeMapper.selectById(clearError.getBanknodeId());
//        if (clearBanknode == null) {
//            throw new BaseException(SystemErrorType.BUSINESS_PROCESS_ERROR);
//        }
//        JSONObject jsonObject = (JSONObject) JSONObject.toJSON(clearError);
//        jsonObject.put("bankNodeName", clearBanknode.getFullName());
//
//        ClearDenom clearDenom = clearDenomMapper.selectById(clearError.getDenomId());
//        if (clearDenom != null) {
//            jsonObject.put("denomName", clearDenom.getName());
//        }
//        return jsonObject;
//    }


    /**
     * 确认差错/提交审核
     * @param id
     * @return
     */
    @Transactional
    public Object confirm(Long id) throws BusinessException {
        TradeClearError clearError = baseMapper.selectById(id);
        if (clearError == null) {
            throw new BusinessException(-1,"无效参数");
        }
        if (clearError.getStatus()  != ClearErrorStatusEnum.CREATE.getValue()) {
            throw new BusinessException(-1,"差错已提交");
        }

        clearError.setUpdateTime(System.currentTimeMillis());
        clearError.setUpdateUser(UserContextHolder.getUserId());
        clearError.setStatus(ClearErrorStatusEnum.CONFIRM.getValue());
        baseMapper.updateById(clearError);

        return Result.success();
    }

    /**
     * 撤销差错
     * @param clearErrorCancelDto
     * @return
     */
    public Object cancel(ClearErrorCancelDTO clearErrorCancelDto) throws BusinessException{
        TradeClearError clearError = baseMapper.selectById(clearErrorCancelDto.getId());
        if (clearError == null) {
            throw new BusinessException(-1,"无效id");
        }
        if(clearError.getStatus() != ClearErrorStatusEnum.CONFIRM.getValue()) {
            throw new BusinessException(-1,"不允许撤销");
        }

        clearError.setCancelTime(System.currentTimeMillis());
        clearError.setCancelUser(UserContextHolder.getUserId());
        clearError.setStatus(ClearErrorStatusEnum.CANCELING.getValue());
        baseMapper.updateById(clearError);
        return Result.success();
    }

    /**
     * 撤销审核
     * @param clearErrorCheckDto
     * @return
     */
    public Object updateCancel(ClearErrorCheckDTO clearErrorCheckDto) throws BusinessException {
        TradeClearError clearError = baseMapper.selectById(clearErrorCheckDto.getId());
        if (clearError == null) {
            throw new BusinessException(-1,"无效id");
        }
        if (clearError.getStatus()  != ClearErrorStatusEnum.CANCELING.getValue()) {
            throw new BusinessException(-1,"不允许操作");
        }
        if (clearErrorCheckDto.getCheck() == CheckTypeEnum.APPROVE.getValue()) {
            clearError.setStatus(ClearErrorStatusEnum.CALCELED.getValue());
        } else { //退回正常已审核状态
            clearError.setStatus(ClearErrorStatusEnum.APPROVE.getValue());
        }
        baseMapper.updateById(clearError);

        return Result.success();
    }
}
