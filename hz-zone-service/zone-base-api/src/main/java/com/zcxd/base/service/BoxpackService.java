package com.zcxd.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.base.dto.BoxpackDTO;
import com.zcxd.base.exception.BusinessException;
import com.zcxd.base.vo.BoxpackVO;
import com.zcxd.common.constant.BoxpackStatusEnum;
import com.zcxd.common.constant.DeleteFlagEnum;
import com.zcxd.db.mapper.BankMapper;
import com.zcxd.db.mapper.BoxpackMapper;
import com.zcxd.db.model.Bank;
import com.zcxd.db.model.Boxpack;
import com.zcxd.db.model.BoxpackTask;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author songanwei
 * @date 2021-11-22
 */
@AllArgsConstructor
@Service
public class BoxpackService extends ServiceImpl<BoxpackMapper, Boxpack>{

    private BankMapper bankMapper;


    /**
     * 查询总数
     * @param boxpack
     * @return
     */
    public Long getTotal(Boxpack boxpack){
        QueryWrapper wrapper = getQueryWrapper(boxpack);
        return (long)this.baseMapper.selectCount(wrapper);
    }

    /**
     * 查询列表数据
     * @param page
     * @param limit
     * @param boxpack
     * @return
     */
    public List<BoxpackDTO> getList(Integer page, Integer limit, Boxpack boxpack){
        QueryWrapper wrapper = getQueryWrapper(boxpack);
        int offset = (page - 1) * limit;
        wrapper.last("LIMIT " + offset +"," + limit);
        wrapper.orderByDesc("id");
        List<Boxpack> boxpackList = this.baseMapper.selectList(wrapper);

        Map<Long,String> bankMap = new HashMap<>();
        Set<Long> bankIds = boxpackList.stream().flatMap(o -> Stream.of(o.getBankId(),o.getShareBankId())).collect(Collectors.toSet());
        if (bankIds.size() > 0){
            List<Bank> bankList = bankMapper.selectBatchIds(bankIds);
            bankMap = bankList.stream().collect(Collectors.toMap(Bank::getId,Bank::getShortName));
        }

        Map<Long, String> finalBankMap = bankMap;
        return boxpackList.stream().map(item -> {
            BoxpackDTO boxpackDTO = new BoxpackDTO();
            BeanUtils.copyProperties(item, boxpackDTO);
            boxpackDTO.setBankName(Optional.ofNullable(finalBankMap.get(item.getBankId())).orElse(""));
            boxpackDTO.setShareBankName(Optional.ofNullable(finalBankMap.get(item.getShareBankId())).orElse(""));
            return boxpackDTO;
        }).collect(Collectors.toList());
    }

    private QueryWrapper getQueryWrapper(Boxpack boxpack){
        QueryWrapper wrapper = new QueryWrapper();
        if (boxpack != null){
            Long departmentId = boxpack.getDepartmentId();
            Long bankId = boxpack.getBankId();
            String boxNo = boxpack.getBoxNo();
            if (bankId > 0){
                Bank bank = bankMapper.selectById(bankId);
                if (bank != null && !"/0/".equals(bank.getParentIds())){
                    QueryWrapper queryWrapper = Wrappers.query().eq("bank_type",1).eq("deleted",0)
                            .like("parent_ids", "/"+bankId+"/");
                    List<Bank> bankList = bankMapper.selectList(queryWrapper);
                    Set<Long> bankIds = bankList.stream().map(Bank::getId).collect(Collectors.toSet());
                    bankIds.add(bankId);
                    wrapper.in("bank_id",bankIds);
                }
            }
            if (departmentId > 0){
                wrapper.eq("department_id", departmentId);
            }
            if (!StringUtils.isEmpty(boxNo)){
                wrapper.eq("box_no", boxNo);
            }
        }
        wrapper.eq("deleted", DeleteFlagEnum.NOT.getValue());
        return wrapper;
    }


    /**
     * 创建操作
     * @param boxpackVO
     * @return
     */
    public boolean create(BoxpackVO boxpackVO){
        //验证参数箱包编号
        Boxpack boxpackT = this.baseMapper.getByNo(boxpackVO.getBoxNo(), 0L);
        if (boxpackT != null){
            throw new BusinessException(1,"箱包编号已存在");
        }
        //验证RFID编号
        Boxpack boxpackS = this.baseMapper.getByRfid(boxpackVO.getRfid(), 0L);
        if (boxpackS != null){
            throw new BusinessException(1,"RFID编号已经存在");
        }
        Boxpack boxpack = new Boxpack();
        BeanUtils.copyProperties(boxpackVO,boxpack);
        boxpack.setStatusT(BoxpackStatusEnum.BANK.getValue());
        int result = this.baseMapper.insert(boxpack);
        return result >= 1;
    }

    /**
     * 修改操作
     * @param boxpackVO
     * @return
     */
    public boolean edit(BoxpackVO boxpackVO){
        //验证参数箱包编号
        Boxpack boxpackT = this.baseMapper.getByNo(boxpackVO.getBoxNo(), boxpackVO.getId());
        if (boxpackT != null){
            throw new BusinessException(1,"箱包编号已存在");
        }
        //验证RFID编号
        Boxpack boxpackS = this.baseMapper.getByRfid(boxpackVO.getRfid(), boxpackVO.getId());
        if (boxpackS != null){
            throw new BusinessException(1,"RFID编号已经存在");
        }
        Boxpack boxpack = new Boxpack();
        BeanUtils.copyProperties(boxpackVO,boxpack);
        int result = this.baseMapper.updateById(boxpack);
        return result >= 1;
    }

    /**
     * 删除操作
     * @param id 删除ID
     * @return
     */
    public boolean delete(Long id){
        //TODO 验证删除条件
        Boxpack boxpack = new Boxpack();
        boxpack.setId(id);
        boxpack.setDeleted(DeleteFlagEnum.YES.getValue());
        int result = this.baseMapper.updateById(boxpack);
        return result >= 1;
    }

    public Boxpack getById(Long id, Integer departmentId) {
        Boxpack boxpack = new Boxpack();
        boxpack.setId(id);
        boxpack.setDepartmentId(departmentId.longValue());
        QueryWrapper<Boxpack> queryWrapper = getQueryWrapper(boxpack);
        List<Boxpack> boxpacks = baseMapper.selectList(queryWrapper);
        return boxpacks.isEmpty() ? null : boxpacks.get(0);
    }

}
