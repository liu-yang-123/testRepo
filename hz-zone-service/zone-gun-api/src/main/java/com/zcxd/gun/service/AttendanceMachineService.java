package com.zcxd.gun.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.common.util.Result;
import com.zcxd.gun.db.mapper.AttendanceMachineMapper;
import com.zcxd.gun.db.model.AttendanceMachine;
import com.zcxd.gun.dto.AttendanceMachineDTO;
import com.zcxd.gun.vo.AttendanceMachineQueryVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zccc
 */
@Service
public class AttendanceMachineService extends ServiceImpl<AttendanceMachineMapper, AttendanceMachine> {

    /**
     * 分页查询持枪证
     * @param page 页数
     * @param limit 数量
     * @param vo 查询条件
     * @return 查询结果
     * @throws IllegalAccessException 错误
     */
    public Object findListByPage(Integer page, Integer limit, AttendanceMachineQueryVO vo) throws IllegalAccessException {
        Page<AttendanceMachine> ipage = new Page<>(page,limit);
        LambdaQueryWrapper<AttendanceMachine> queryWrapper = getQueryWrapper(vo);
        Page<AttendanceMachine> machinePage = baseMapper.selectPage(ipage, queryWrapper);

        List<AttendanceMachineDTO> attendanceMachines = machinePage.getRecords().stream().map(attendanceMachine -> {
            AttendanceMachineDTO attendanceMachineDTO = new AttendanceMachineDTO();
            BeanUtils.copyProperties(attendanceMachine, attendanceMachineDTO);
            return attendanceMachineDTO;
        }).collect(Collectors.toList());

        Page<AttendanceMachineDTO> machineDTOPage = new Page<>(page, limit);
        BeanUtils.copyProperties(machinePage, machineDTOPage);
        machineDTOPage.setRecords(attendanceMachines);
        return Result.success(machineDTOPage);
    }

    /**
     * 获取
     * @param vo 查询条件
     * @return 结果
     */
    public Result list(AttendanceMachineQueryVO vo) {
        LambdaQueryWrapper<AttendanceMachine> queryWrapper = getQueryWrapper(vo);
        List<AttendanceMachine> attendanceMachines = baseMapper.selectList(queryWrapper);
        return Result.success(attendanceMachines);
    }

    private LambdaQueryWrapper<AttendanceMachine> getQueryWrapper(AttendanceMachineQueryVO vo) {
        LambdaQueryWrapper<AttendanceMachine> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(!StringUtils.isBlank(vo.getAddress()), AttendanceMachine::getAddress, vo.getAddress())
                .eq(vo.getPort() != null, AttendanceMachine::getPort, vo.getPort())
                .eq(vo.getStatus() != null, AttendanceMachine::getStatus, vo.getStatus())
                .eq(vo.getMachineNum() != null, AttendanceMachine::getMachineNum, vo.getMachineNum())
                .eq(AttendanceMachine::getDepartmentId, vo.getDepartmentId());
        return queryWrapper;
    }
}
