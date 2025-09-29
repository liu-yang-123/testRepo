package com.zcxd.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.common.util.SecurityUtils;
import com.zcxd.db.mapper.EmployeeMapper;
import com.zcxd.db.model.Employee;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 员工业务层
 */
@Service
public class EmployeeService extends ServiceImpl<EmployeeMapper, Employee> {


    /**
     * @Description:根据工号得到员工信息
     * @Author: lilanglang
     * @Date: 2021/7/13 14:25
     * @param empNo:
     **/
    public Employee getByEmpNo(String empNo){
        QueryWrapper<Employee> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("emp_no",empNo);
        return this.baseMapper.selectOne(queryWrapper);
    }

    /**
     * @Description:根据openid获取用户信息
     * @Author: lilanglang
     * @Date: 2021/7/15 10:16
     * @param userOpenid:
     **/
    public List<Employee> listByOpenId(String userOpenid) {
        QueryWrapper<Employee> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("wx_openid",userOpenid);
        return this.baseMapper.selectList(queryWrapper);
    }

    /**
     * @Description:获取人员Map
     * @Author: lilanglang
     * @Date: 2021/7/16 16:42
     **/
    public Map<Long,List<Employee>> getEmployeeMap(){
        List<Employee> empList = this.list();
        Map<Long,List<Employee>> employeeMap=empList.stream().collect(Collectors.groupingBy(Employee::getId));
        return employeeMap;
    }
    /**
     * @Description:
     * @Author: lilanglang
     * @Date: 2021/10/29 10:19
     * @param empNo:
     * @param phone:
     **/
    public Employee getByEmpNoAndPhone(String empNo, String phone) {
        QueryWrapper<Employee> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("emp_no",empNo).eq("mobile", SecurityUtils.encryptAES(phone));
        return baseMapper.selectOne(queryWrapper);
    }
}