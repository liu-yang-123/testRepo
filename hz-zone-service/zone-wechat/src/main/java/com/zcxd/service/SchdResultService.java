package com.zcxd.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.common.util.DateTimeUtil;
import com.zcxd.db.mapper.SchdResultMapper;
import com.zcxd.db.model.Employee;
import com.zcxd.db.model.Route;
import com.zcxd.db.model.SchdResult;
import com.zcxd.dto.SchdResultDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Description:排班信息业务层
 * @Author: lilanglang
 * @Date: 2021/7/15 15:24
 **/
@Service
@Slf4j
public class SchdResultService extends ServiceImpl<SchdResultMapper, SchdResult> {
    @Autowired
    private EmployeeService employeeService;
    @Value("${wx.mp.configs.templateId}")
    private String template;
    @Value("${wx.mp.configs.host}")
    private String host;
    @Value("${wx.mp.configs.appId}")
    private String appId;
    @Value("${wx.mp.configs.secret}")
    private String secret;

    /**
     * @Description:获取有效排班
     * @Author: lilanglang
     * @Date: 2021/7/16 14:05
     * @param jobType :1/司机岗 2/护卫岗 3/钥匙岗 4/密码岗
     * @param empId : 员工Id
     **/
    public List<SchdResult> listSchdResult(Integer jobType,Long empId){
        QueryWrapper<SchdResult> queryWrapper=new QueryWrapper<>();
        long date=DateTimeUtil.getDailyStartTimeMs(DateTimeUtil.getCurrentTimeStampMs());
        queryWrapper.ge("plan_day",date);
        //TODO 根据岗位类型筛选该岗位下的人员排班信息
        if(jobType==1){
            queryWrapper.in("driver",empId);
        }else if(jobType==2){
            queryWrapper.and(wrapper -> wrapper.in("scurity_a", empId).or().eq("scurity_b", empId));
        }else if(jobType==3){
            queryWrapper.and(wrapper -> wrapper.in("key_man", empId).or().eq("leader", empId));
        }else {
            queryWrapper.and(wrapper -> wrapper.in("op_man", empId).or().eq("leader", empId));
        }
        queryWrapper.orderByDesc("plan_day");
        return this.baseMapper.selectList(queryWrapper);
    }

    /**
     * @Description:返回交互排班信息
     * @Author: lilanglang
     * @Date: 2021/7/16 16:38
     * @param list:
     **/
    public List<SchdResultDTO> getDtoList(List<SchdResult> list){
        Map<Long,List<Employee>> employeeMap=employeeService.getEmployeeMap();
        List<SchdResultDTO> dtoList = list.stream().map(schdResult->{
            SchdResultDTO schdResultRetutnDto=new SchdResultDTO();
            BeanUtils.copyProperties(schdResult,schdResultRetutnDto);
            schdResultRetutnDto.setDriverName(employeeMap.get(schdResult.getDriver()).size()>0 ?employeeMap.get(schdResult.getDriver()).get(0).getEmpName():"");
            schdResultRetutnDto.setKeyManName(employeeMap.get(schdResult.getKeyMan()).size()>0?employeeMap.get(schdResult.getKeyMan()).get(0).getEmpName():"");
            schdResultRetutnDto.setLeaderName(employeeMap.get(schdResult.getLeader()).size()>0?employeeMap.get(schdResult.getLeader()).get(0).getEmpName():"");
            schdResultRetutnDto.setScurityAName(employeeMap.get(schdResult.getScurityA()).get(0).getEmpName()+"、"+
                    employeeMap.get(schdResult.getScurityB()).get(0).getEmpName());
            schdResultRetutnDto.setScurityBName(employeeMap.get(schdResult.getScurityB()).size()>0?employeeMap.get(schdResult.getScurityB()).get(0).getEmpName():"");
            schdResultRetutnDto.setOpManName(employeeMap.get(schdResult.getOpMan()).size()>0?employeeMap.get(schdResult.getOpMan()).get(0).getEmpName():"");
            schdResultRetutnDto.setPlanDayStr(DateTimeUtil.timeStampMs2Date(schdResult.getPlanDay(),"yyyy-MM-dd"));
            return schdResultRetutnDto;
        }).collect(Collectors.toList());
        return dtoList;
    }

    /**
     * @Description:根据日期获取排班记录
     * @Author: lilanglang
     * @Date: 2021/7/19 14:17
     * @param time:
     **/
    public  List<SchdResult> listResultByDate(long time) {
        QueryWrapper<SchdResult> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("plan_day",time);
        return this.baseMapper.selectList(queryWrapper);
    }



    /**
     * @Description:获取司机的oprnId
     * @Author: lilanglang
     * @Date: 2021/7/19 14:49
     * @param route:
     * @param type :1 司机 2业务员 3护卫
     **/
    public List<String> getOPenIdlistByDriver(Route route,Integer type){
        List<Long> empIdList=new ArrayList<>();
        if(type==1){
            empIdList.add(route.getDriver());
        }else if(type==2){
            empIdList.add(route.getSecurityA());
            empIdList.add(route.getSecurityB());
        }else{
            empIdList.add(route.getRouteKeyMan());
            empIdList.add(route.getRouteOperMan());
        }
        //去重
        empIdList=empIdList.stream().distinct().collect(Collectors.toList());
        List<Employee> employeeList=employeeService.listByIds(empIdList);
        return getOpenIdList(employeeList);
     }

    /**
     * @Description:获取指定人员集合的openId集合
     * @Author: lilanglang
     * @Date: 2021/10/18 14:59
     * @param empIds:
     **/
    public List<String> listOpeatorOpenId(Set<Long> empIds) {
        List<Employee> employeeList=employeeService.listByIds(empIds);
        return getOpenIdList(employeeList);
    }

    public List<String> getOpenIdList(List<Employee> list){
        //提取openId返回
        List<String> openIdList=list.stream().map(Employee::getWxOpenid).filter(item-> item!="")
                .collect(Collectors.toList());
        return openIdList;
    }
}