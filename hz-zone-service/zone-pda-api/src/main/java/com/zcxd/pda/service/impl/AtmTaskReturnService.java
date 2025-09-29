package com.zcxd.pda.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.db.mapper.AtmClearTaskMapper;
import com.zcxd.db.mapper.AtmTaskReturnMapper;
import com.zcxd.db.model.AtmClearTask;
import com.zcxd.db.model.AtmTaskReturn;
import org.checkerframework.checker.units.qual.A;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 
 * @ClassName AtmClearTaskService
 * @Description atm回笼钞袋钞盒服务
 * @author shijin
 * @Date 2021年5月20日上午10:20:05
 */
@Service
public class AtmTaskReturnService extends ServiceImpl<AtmTaskReturnMapper, AtmTaskReturn> {

    /**
     * 查询未清分钞袋钞盒回笼记录
     * @param boxBarCode
     * @param taskDay
     * @return
     */
    public AtmTaskReturn getByBoxNo(String boxBarCode, String taskDay) {
        AtmTaskReturn where = new AtmTaskReturn();
        where.setBoxBarCode(boxBarCode);
        where.setTaskDate(taskDay);
//        if (clearFlag != null) {
//            where.setClearFlag(clearFlag);
//        }
        List<AtmTaskReturn> list = baseMapper.selectList(new QueryWrapper<>(where));
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    /**
     * 查询未清分钞袋钞盒回笼记录
     * @param boxBarCode
     * @param taskDay
     * @return
     */
    public List<AtmTaskReturn> listByBoxNo(String boxBarCode, String taskDay) {
        AtmTaskReturn where = new AtmTaskReturn();
        where.setBoxBarCode(boxBarCode);
        where.setTaskDate(taskDay);
        return baseMapper.selectList(new QueryWrapper<>(where));
    }

    /**
     * 根据任务查询所有回笼钞盒钞袋
     * @param taskId
     * @return
     */
    public List<AtmTaskReturn> listReturnBoxs(Long taskId) {
        AtmTaskReturn where = new AtmTaskReturn();
        where.setTaskId(taskId);
        return baseMapper.selectList(new QueryWrapper<>(where));
    }

    /**
     * 根据ATM查询关联的回笼钞盒钞袋
     * @param atmId
     * @param taskDate
     * @return
     */
    public List<AtmTaskReturn> listReturnByAtm(Long atmId,String taskDate,Integer clearFlag) {
        AtmTaskReturn where = new AtmTaskReturn();
        where.setAtmId(atmId);
        where.setTaskDate(taskDate);
        if (clearFlag != null) {
            where.setClearFlag(clearFlag);
        }
        return baseMapper.selectList(new QueryWrapper<>(where));
    }
}
