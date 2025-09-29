package com.zcxd.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.common.constant.JobTypeEnum;
import com.zcxd.common.util.DateTimeUtil;
import com.zcxd.db.mapper.MessagePushMapper;
import com.zcxd.db.model.Employee;
import com.zcxd.db.model.MessagePush;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author lilanglang
 * @date 2021-10-20 15:46
 */
@Service
public class MessagePushService extends ServiceImpl<MessagePushMapper, MessagePush> {
    @Resource
    private EmployeeService employeeService;
    /**
     * @Description:获取大于等于两天前的排班信息
     * @Author: lilanglang
     * @Date: 2021/10/29 15:23
     * @param jobType:
     * @param empId:
     **/
    public List<MessagePush> listMessagePush(Integer jobType, int type,Long empId) {
        QueryWrapper<MessagePush> queryWrapper=new QueryWrapper<>();
        Date time=DateTimeUtil.getPreDateByDate(new Date(),2);
        long date=time.getTime();
        queryWrapper.ge("create_time",date).eq("type",type);
        //TODO 根据岗位类型筛选该岗位下的人员排班信息
        if(jobType.equals(JobTypeEnum.DRIVER.getValue())){
            queryWrapper.eq("driver",empId);
        }else if(jobType.equals(JobTypeEnum.GUARD.getValue())){
            queryWrapper.and(wrapper -> wrapper.in("scurity_a", empId).or().eq("scurity_b", empId));
        }else if(jobType.equals(JobTypeEnum.KEY.getValue())||jobType.equals(JobTypeEnum.CLEAN.getValue())){
            queryWrapper.and(wrapper -> wrapper.in("route_key_man", empId).or().eq("route_oper_man", empId));
        }
        queryWrapper.orderByDesc("create_time");
        return this.baseMapper.selectList(queryWrapper);
    }

    /**
     * @Description:获取指定数据
     * @Author: lilanglang
     * @Date: 2021/10/29 16:02
     * @param list:
     * @param jobType:
     **/
    public List<MessagePush> getByList(List<MessagePush> list, Integer jobType) {
        List<Employee> employeeList=employeeService.list();
        Map employeeMap=employeeList.stream().collect(Collectors.toMap(Employee::getId,Employee::getEmpName));
        if(list.size()>1){
            List<MessagePush> valueList = list.stream().map(item->{
                MessagePush messagePush=new MessagePush();
                BeanUtils.copyProperties(item,messagePush);
                messagePush.setName(jobType.equals(JobTypeEnum.DRIVER.getValue())?employeeMap.getOrDefault(messagePush.getDriver(),"").toString():
                        (jobType==2?employeeMap.getOrDefault(messagePush.getSecurityA(),"").toString()+"、"+employeeMap.getOrDefault(messagePush.getSecurityB(),"").toString()
                                :employeeMap.getOrDefault(messagePush.getRouteKeyMan(),"").toString()+"、"+employeeMap.getOrDefault(messagePush.getRouteOperMan(),"").toString()));
                return messagePush;
            }).collect(Collectors.toList());
            return valueList;
        }
        List<MessagePush> pushList = new ArrayList<>();
        MessagePush messagePush=list.get(0);

        messagePush.setName(jobType.equals(JobTypeEnum.DRIVER.getValue())?employeeMap.getOrDefault(messagePush.getDriver(),"").toString():
                (jobType==2?employeeMap.getOrDefault(messagePush.getSecurityA(),"").toString()+"、"+employeeMap.getOrDefault(messagePush.getSecurityB(),"").toString()
                        :employeeMap.getOrDefault(messagePush.getRouteKeyMan(),"").toString()+"、"+employeeMap.getOrDefault(messagePush.getRouteOperMan(),"").toString()));
        pushList.add(messagePush);
        return pushList;
    }
}