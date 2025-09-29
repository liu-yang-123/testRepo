package com.zcxd.pda.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zcxd.common.constant.*;
import com.zcxd.common.util.ResultList;
import com.zcxd.db.model.*;
import com.zcxd.db.model.Currency;
import com.zcxd.pda.config.UserContextHolder;
import com.zcxd.pda.dto.*;
import com.zcxd.pda.exception.BaseException;
import com.zcxd.pda.exception.BusinessException;
import com.zcxd.pda.exception.SystemErrorType;
import com.zcxd.pda.service.impl.*;
import com.zcxd.pda.vo.VaultCheckResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 
 * @ClassName WarehouseService
 * @Description 金库管理相关服务
 * @author shijin
 * @Date 2021年5月18日上午17:30:28
 */
@Slf4j
@Service
public class WarehouseService {

	@Resource
	private VaultOrderService vaultOrderService;
	@Resource
	private VaultOrderRecordService vaultOrderRecordService;
	@Resource
	private VaultVolumService vaultVolumService;
	@Resource
	private VaultCheckService vaultCheckService;
	@Resource
	private BankService bankService;
	@Resource
	private DenomService denomService;
	@Resource
	private CurrencyService currencyService;
	@Resource
	private CashboxService cashboxService;
	@Resource
	private AtmAdditionCashService atmAdditionCashService;
	@Resource
	private VaultOrderTaskService vaultOrderTaskService;
	@Resource
	private EmployeeService employeeService;


	/**
	 * 查询未处理出入库单
	 * @param page
	 * @param limit
	 */
	public ResultList listUndoVaultOrder(int page, int limit) throws BusinessException {

		Employee employee = employeeService.getEmployeeUserById(UserContextHolder.getUserId());
		if (null == employee) {
			return new ResultList.Builder().total(0).list(new ArrayList()).build();
		}
		IPage<VaultOrder> orderIPage = vaultOrderService.listOrderPage(employee.getDepartmentId(),OrderStatusEnum.CHECK_APPROVE.getValue(),null,page,limit);
		if (orderIPage.getRecords().size() == 0) {
			return new ResultList.Builder().total(0).list(new ArrayList()).build();
		}
		Set<Long> bankIdSet = new HashSet<>();
		orderIPage.getRecords().forEach(vaultOrder -> {
			bankIdSet.add(vaultOrder.getBankId());
		});

		List<Bank> bankList = bankService.listByIds(bankIdSet);
		if (null == bankList || bankList.size() == 0) {
			throw new BaseException(SystemErrorType.BUSINESS_PROCESS_ERROR);
		}
		Map<Long,String> bankMap = new HashMap<>();
		bankList.forEach(bank -> {
			if (!bankMap.containsKey(bank.getId())) {
				bankMap.put(bank.getId(),bank.getFullName());
			}
		});

		List<VaultOrderDTO> vaultOrderDTOList = orderIPage.getRecords().stream().map(vaultOrder -> {
			VaultOrderDTO vaultOrderDTO = new VaultOrderDTO();
			BeanUtils.copyProperties(vaultOrder, vaultOrderDTO);
			vaultOrderDTO.setBankId(vaultOrder.getBankId());
			vaultOrderDTO.setBankName(bankMap.getOrDefault(vaultOrder.getBankId(),""));
			return vaultOrderDTO;
		}).collect(Collectors.toList());

		return new ResultList.Builder().total(orderIPage.getTotal()).list(vaultOrderDTOList).build();
	}

	/**
	 * 查询出入库单券别明细
	 * @param orderId
	 * @return
	 */
	public JSONObject getVaultOrderDenomRecords(Long orderId) {

		Map<String,String> currencyMap = new HashMap<>(); //缓存货币代码信息，避免重复查询
		List<DenomRecordDTO> goodRecords = new ArrayList<>(); //五好券
		List<DenomRecordDTO> badRecords = new ArrayList<>(); //残损券
		List<DenomRecordDTO> usableRecords=new ArrayList<>(); //可用券
		List<DenomRecordDTO> unclearRecords = new ArrayList<>(); //未清分
		List<DenomRecordDTO> remnantRecords = new ArrayList<>(); //尾零钞

		List<VaultOrderRecord> vaultOrderRecordList = vaultOrderRecordService.getListByOrderId(orderId);
		vaultOrderRecordList.forEach(vaultOrderRecord -> {
			DenomRecordDTO denomRecordDTO = new DenomRecordDTO();
			denomRecordDTO.setId(vaultOrderRecord.getId());
			denomRecordDTO.setAmount(vaultOrderRecord.getAmount());
			Denom denom = denomService.getById(vaultOrderRecord.getDenomId());
			if (denom != null) {
				denomRecordDTO.setDenomId(denom.getId());
				denomRecordDTO.setCoin(0 == Constant.COIN.compareToIgnoreCase(denom.getAttr()));
				denomRecordDTO.setDenomName(denom.getName());
				denomRecordDTO.setDenomValue(denom.getValue());

				if (vaultOrderRecord.getAmount().compareTo(BigDecimal.ZERO) == 0) {
					if(vaultOrderRecord.getCount().equals(0)){
						return;
					}
					denomRecordDTO.setPiecesCount(vaultOrderRecord.getCount().longValue());
					denomRecordDTO.setBundleCount(0);
					denomRecordDTO.setWadCount(0);
					denomRecordDTO.setBagCount(0);
				} else {
					denomRecordDTO.setPiecesCount(denom.toPieces(vaultOrderRecord.getAmount()));
					denomRecordDTO.setWadCount(denom.toWad(vaultOrderRecord.getAmount()));
					denomRecordDTO.setBundleCount(denom.toBundle(vaultOrderRecord.getAmount()));
					denomRecordDTO.setBagCount(denom.toBag(vaultOrderRecord.getAmount()));
				}
				if (currencyMap.containsKey(denom.getCurCode())) {
					denomRecordDTO.setCurName(currencyMap.get(denom.getCurCode()));
				} else {
					Currency currency = currencyService.getByCurCode(denom.getCurCode());
					if (currency != null) {
						denomRecordDTO.setCurName(currency.getCurName());
						currencyMap.put(currency.getCurCode(),currency.getCurName());
					}
				}
			}
			if (vaultOrderRecord.getDenomType() == DenomTypeEnum.BAD.getValue()) {
				badRecords.add(denomRecordDTO);
			} else if (vaultOrderRecord.getDenomType() == DenomTypeEnum.GOOD.getValue()) {
				goodRecords.add(denomRecordDTO);
			} else if (vaultOrderRecord.getDenomType() == DenomTypeEnum.USABLE.getValue()) {
				usableRecords.add(denomRecordDTO);
			} else if (vaultOrderRecord.getDenomType() == DenomTypeEnum.UNCLEAR.getValue()) {
				unclearRecords.add(denomRecordDTO);
			} else if (vaultOrderRecord.getDenomType() == DenomTypeEnum.REMNANT.getValue()) {
				remnantRecords.add(denomRecordDTO);
			}
		});
		JSONObject jsonObject  = new JSONObject();
		jsonObject.put("goodRecords", goodRecords);
		jsonObject.put("badRecords", badRecords);
		jsonObject.put("usableRecords", usableRecords);
		jsonObject.put("unclearRecords", unclearRecords);
		jsonObject.put("remnantRecords", remnantRecords);
		return jsonObject;
	}

	/**
	 * 订单入库操作
	 * @param orderProcDTO
	 */
	@Transactional
	public void orderStockIn(OrderProcDTO orderProcDTO) throws BusinessException {
		VaultOrder vaultOrder = vaultOrderService.getById(orderProcDTO.getOrderId());
		if (null == vaultOrder) {
			log.warn("无效订单: "+orderProcDTO.toString());
			throw new BusinessException("无效订单");
		}
		if (vaultOrder.getOrderType() != OrderTypeEnum.STOCK_IN.getValue()
		|| vaultOrder.getStatusT() != OrderStatusEnum.CHECK_APPROVE.getValue()) {
			log.warn("订单状态不正确: " + vaultOrder.getStatusT());
			throw new BusinessException("订单状态不正确");
		}
		/**
		 * 查询明细
		 */
		List<VaultOrderRecord> vaultOrderRecordList = vaultOrderRecordService.getListByOrderId(orderProcDTO.getOrderId());
		if (vaultOrderRecordList.size() > 0) {
			Map<Long,Denom> denomMap = new HashMap<>();
			Set<Long> denomIds = vaultOrderRecordList.stream().map(VaultOrderRecord::getDenomId).collect(Collectors.toSet());
			if (denomIds.size() > 0) {
				List<Denom> denomList = denomService.listByIds(denomIds);
				denomMap = denomList.stream().collect(Collectors.toMap(Denom::getId, Function.identity(),(key1,key2)->key2));
			}
			for(VaultOrderRecord vaultOrderRecord : vaultOrderRecordList) {
				Denom denom = denomMap.get(vaultOrderRecord.getDenomId());
				if (null == denom) {
					log.error("无效券别: " + vaultOrderRecord.getDenomId());
					throw new BusinessException("无效券别");
				}
				BigDecimal amount = BigDecimal.ZERO;
				long count = 0L;
				if (denom.getVersion() == DenomVerFlagEnum.VER_BAD.getValue()) { //残缺币按张数入库
					count = vaultOrderRecord.getCount().longValue();
					amount = denom.getValue().multiply(new BigDecimal(count));
				} else { //其他按金额入库
					amount = vaultOrderRecord.getAmount();
					count = amount.divide(denom.getValue(),BigDecimal.ROUND_DOWN).longValue();
				}
				vaultVolumService.addVolum(vaultOrder.getDepartmentId(),vaultOrder.getBankId(),denom,vaultOrderRecord.getDenomType(),amount,count);
			}
			VaultOrder newVaultOrder = new VaultOrder();
			newVaultOrder.setId(vaultOrder.getId());
			newVaultOrder.setStatusT(OrderStatusEnum.FINISH.getValue());
			newVaultOrder.setFinish(1);
			newVaultOrder.setWhOpMan(orderProcDTO.getWhOpMan());
			newVaultOrder.setWhCheckMan(orderProcDTO.getWhCheckMan());
			newVaultOrder.setWhOpTime(System.currentTimeMillis());
			vaultOrderService.updateById(newVaultOrder);
		}
	}

	/**
	 * 订单出库操作
	 * @param orderProcDTO
	 */
	@Transactional
	public void orderStockOut(OrderProcDTO orderProcDTO) throws BusinessException{
		VaultOrder vaultOrder = vaultOrderService.getById(orderProcDTO.getOrderId());
		if (null == vaultOrder) {
			log.error("无效订单");
			throw new BusinessException("无效订单");
		}
		if (vaultOrder.getOrderType() != OrderTypeEnum.STOCK_OUT.getValue()
				|| vaultOrder.getStatusT() != OrderStatusEnum.CHECK_APPROVE.getValue()) {
			log.error("订单状态不正确");
			throw new BusinessException("订单状态不正确");
		}
		/**
		 * 查询明细
		 */
		List<VaultOrderRecord> vaultOrderRecordList = vaultOrderRecordService.getListByOrderId(orderProcDTO.getOrderId());
		if (vaultOrderRecordList.size() > 0) {
			Map<Long,Denom> denomMap = new HashMap<>();
			Set<Long> denomIds = vaultOrderRecordList.stream().map(VaultOrderRecord::getDenomId).collect(Collectors.toSet());
			if (denomIds.size() > 0) {
				List<Denom> denomList = denomService.listByIds(denomIds);
				denomMap = denomList.stream().collect(Collectors.toMap(Denom::getId, Function.identity(),(key1,key2)->key2));
			}
			for (VaultOrderRecord vaultOrderRecord : vaultOrderRecordList){
				Denom denom = denomMap.get(vaultOrderRecord.getDenomId());
				if (null == denom) {
					log.error("无效券别: " + vaultOrderRecord.getDenomId());
					throw new BusinessException("无效券别");
				}
				BigDecimal amount = BigDecimal.ZERO;
				long count = 0L;
				if (denom.getVersion() == DenomVerFlagEnum.VER_BAD.getValue()) { //残缺币按张数入库
					count = vaultOrderRecord.getCount().longValue();
					amount = denom.getValue().multiply(new BigDecimal(count));
				} else { //其他按金额入库
					amount = vaultOrderRecord.getAmount();
					count = amount.divide(denom.getValue(),BigDecimal.ROUND_DOWN).longValue();
				}
				int iRet= vaultVolumService.subVolum(vaultOrder.getBankId(), denom, vaultOrderRecord.getDenomType(), amount,count);
				if (0 == iRet) {
					log.warn("库存余额不足");
					throw new BusinessException("库存余额不足");
				}
			}

			/**
			 * 本次出库是否包括备用金
			 * 设置备用金记录为已出库状态
			 */
			List<VaultOrderTask> vaultOrderTasks = vaultOrderTaskService.listByOrderId(vaultOrder.getId(),1);
			if (vaultOrderTasks.size() > 0) {
				List<AtmAdditionCash> atmAdditionCashes = vaultOrderTasks.stream().map(vaultOrderTask -> {
					AtmAdditionCash atmAdditionCash = new AtmAdditionCash();
					atmAdditionCash.setId(vaultOrderTask.getTaskId());
					atmAdditionCash.setStatusT(AtmAdditionCashStatusEnum.OUTSTORE.getValue());
					return atmAdditionCash;
				}).collect(Collectors.toList());
				atmAdditionCashService.updateBatchById(atmAdditionCashes);
			}
			VaultOrder newVaultOrder = new VaultOrder();
			newVaultOrder.setId(vaultOrder.getId());
			newVaultOrder.setStatusT(OrderStatusEnum.FINISH.getValue());
			newVaultOrder.setFinish(1);
			newVaultOrder.setWhOpMan(orderProcDTO.getWhOpMan());
			newVaultOrder.setWhCheckMan(orderProcDTO.getWhCheckMan());
			newVaultOrder.setWhOpTime(System.currentTimeMillis());
			vaultOrderService.updateById(newVaultOrder);
		}
	}

	/**
	 * 统计查询银行库存
	 * @return
	 */
	public List<VaultVolumDTO> getAllBankVaultVolum() {
		Employee employee = employeeService.getEmployeeUserById(UserContextHolder.getUserId());
		if (null == employee) {
			return new ArrayList<>();
		}
		List<VaultVolumDTO> vaultVolumDTOList = vaultVolumService.sumVaultVolum(employee.getDepartmentId());
		vaultVolumDTOList.forEach(vaultVolumDTO -> {
			Bank bank = bankService.getById(vaultVolumDTO.getBankId());
			if (null != bank) {
				vaultVolumDTO.setBankName(bank.getFullName());
			}
		});
		return vaultVolumDTOList;
	}

	/**
	 * 查询银行库存明细
	 * @param bankId
	 * @return
	 */
	public JSONObject getBankVaultVolumDetail(Long bankId) {
		Map<String,String> currencyMap = new HashMap<>(); //缓存货币代码信息，避免重复查询
		List<DenomRecordDTO> goodRecords = new ArrayList<>(); //五好券
		List<DenomRecordDTO> badRecords = new ArrayList<>(); //残损券
		List<DenomRecordDTO> usableRecords = new ArrayList<>(); //可用券
		List<DenomRecordDTO> unclearRecords = new ArrayList<>(); //未清分
		List<DenomRecordDTO> remnantRecords = new ArrayList<>(); //尾零钞

		List<VaultVolum> vaultVolumList = vaultVolumService.listBankVaultVolum(bankId);
		vaultVolumList.forEach(vaultVolum -> {
			DenomRecordDTO denomRecordDTO = new DenomRecordDTO();
			denomRecordDTO.setId(vaultVolum.getId());
			denomRecordDTO.setAmount(vaultVolum.getAmount());
			Denom denom = denomService.getById(vaultVolum.getDenomId());
			if (denom != null) {
				denomRecordDTO.setDenomId(denom.getId());
				denomRecordDTO.setCoin(0 == Constant.COIN.compareToIgnoreCase(denom.getAttr()));
				denomRecordDTO.setDenomName(denom.getName());
				denomRecordDTO.setDenomValue(denom.getValue());

				if (vaultVolum.getAmount().compareTo(BigDecimal.ZERO) == 0) {
					if(vaultVolum.getCount().equals(0L)){
						return;
					}
					denomRecordDTO.setPiecesCount(vaultVolum.getCount());
					denomRecordDTO.setBundleCount(0);
					denomRecordDTO.setWadCount(0);
					denomRecordDTO.setBagCount(0);
				} else {
					denomRecordDTO.setPiecesCount(denom.toPieces(vaultVolum.getAmount()));
					denomRecordDTO.setWadCount(denom.toWad(vaultVolum.getAmount()));
					denomRecordDTO.setBundleCount(denom.toBundle(vaultVolum.getAmount()));
					denomRecordDTO.setBagCount(denom.toBag(vaultVolum.getAmount()));
				}
				if (currencyMap.containsKey(denom.getCurCode())) {
					denomRecordDTO.setCurName(currencyMap.get(denom.getCurCode()));
				} else {
					Currency currency = currencyService.getByCurCode(denom.getCurCode());
					if (currency != null) {
						denomRecordDTO.setCurName(currency.getCurName());
						currencyMap.put(currency.getCurCode(),currency.getCurName());
					}
				}
			}
			if (vaultVolum.getDenomType() == DenomTypeEnum.BAD.getValue()) {
				badRecords.add(denomRecordDTO);
			} else if (vaultVolum.getDenomType() == DenomTypeEnum.GOOD.getValue()) {
				goodRecords.add(denomRecordDTO);
			} else if (vaultVolum.getDenomType() == DenomTypeEnum.USABLE.getValue()) {
				usableRecords.add(denomRecordDTO);
			} else if (vaultVolum.getDenomType() == DenomTypeEnum.UNCLEAR.getValue()) {
				unclearRecords.add(denomRecordDTO);
			} else if (vaultVolum.getDenomType() == DenomTypeEnum.REMNANT.getValue()) {
				remnantRecords.add(denomRecordDTO);
			}
		});
		JSONObject jsonObject  = new JSONObject();
		jsonObject.put("goodRecords", goodRecords);
		jsonObject.put("badRecords", badRecords);
		jsonObject.put("usableRecords", usableRecords);
		jsonObject.put("unclearRecords", unclearRecords);
		jsonObject.put("remnantRecords", remnantRecords);
		return jsonObject;
	}

	/**
	 * 盘点
	 * @param vaultCheckResultVO
	 */
	public void saveVolumCheckResult(VaultCheckResultVO vaultCheckResultVO) {
		Bank bank = bankService.getById(vaultCheckResultVO.getBankId());
		VaultCheck vaultCheck = new VaultCheck();
		BeanUtils.copyProperties(vaultCheckResultVO,vaultCheck);
		vaultCheck.setDepartmentId(bank.getDepartmentId());
		vaultCheck.setUsableBalance(vaultCheckResultVO.getUsableAmount());
		vaultCheck.setBadBalance(vaultCheckResultVO.getBadAmount());
		vaultCheck.setGoodBalance(vaultCheckResultVO.getGoodAmount());
		vaultCheck.setUnclearBalance(vaultCheckResultVO.getUnclearAmount());
		vaultCheck.setRemnantBalance(vaultCheckResultVO.getRemnantAmount());
		vaultCheck.setWhOpTime(System.currentTimeMillis());
		vaultCheckService.save(vaultCheck);
	}

	/**
	 * 绑定钞盒&RFID
	 * @param cashboxDTOList
	 */
	@Transactional
	public void cashboxBindBatch(List<CashboxDTO> cashboxDTOList) throws BusinessException{
		for (CashboxDTO cashboxDTO : cashboxDTOList) {
			this.cashboxBindOne(cashboxDTO);
		}
	}

	@Transactional
	public void cashboxBindSingle(CashboxDTO cashboxDTO) throws BusinessException{
		this.cashboxBindOne(cashboxDTO);
	}

	private void cashboxBindOne(CashboxDTO cashboxDTO) throws BusinessException{
		Cashbox cashbox = cashboxService.getByBoxNo(cashboxDTO.getBoxNo());
		if (null != cashbox && !StringUtils.isEmpty(cashbox.getRfid())) {
			throw new BusinessException("钞盒已绑定，请先解绑");
		}

		Cashbox cashboxOther = cashboxService.getByRfid(cashboxDTO.getRfid());
		if (null != cashboxOther) {
			throw new BusinessException("RFID标签重复使用");
		}
		cashbox = new Cashbox();
		BeanUtils.copyProperties(cashboxDTO,cashbox);
		cashbox.setCreateUser(UserContextHolder.getUserId());
		boolean bRet = cashboxService.save(cashbox);
		if (!bRet) {
			throw new BusinessException("绑定失败");
		}
	}

	public CashboxDTO getCashBoxByRfid(String rfid) {
		CashboxDTO cashboxDTO = null;
		Cashbox cashbox = cashboxService.getByRfid(rfid);
		if (null != cashbox) {
			cashboxDTO = new CashboxDTO();
			BeanUtils.copyProperties(cashbox,cashboxDTO);
		}
		return cashboxDTO;
	}

}
