package com.zcxd.pda.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcxd.common.constant.Constant;
import com.zcxd.common.constant.DeleteFlagEnum;
import com.zcxd.common.constant.EmployeeStatusEnum;
import com.zcxd.db.mapper.EmployeeMapper;
import com.zcxd.db.model.Employee;
import com.zcxd.db.model.SysUser;
import com.zcxd.db.utils.CacheKeysDef;
import com.zcxd.db.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @ClassName EmployeeService
 * @Description 员工服务
 * @author shijin
 * @Date 2021年5月25日上午10:50:05
 */
@Service
public class EmployeeService extends ServiceImpl<EmployeeMapper, Employee> {

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 缓存读取员工信息
     * @param id
     * @return
     */
    public Employee getEmployeeUserById(Long id) {
        Employee employee = null;
        String key = CacheKeysDef.ComAppUserPrefix+id;
        if (redisUtil.hasKey(key)) {
            String value = redisUtil.get(key);
            employee = JSON.parseObject(value,Employee.class);
        } else {
            employee = getEmployeeById(id);
            if (null != employee) {
                employee.setPassword(null);
                String value = JSON.toJSONString(employee);
                redisUtil.setEx(key,value,1, TimeUnit.DAYS);
            }
        }
        return employee;
    }

    public void deleteEmployeeUserCache(Serializable id) {
        String key = CacheKeysDef.ComAppUserPrefix+id;
        redisUtil.delete(key);
    }

     /**
     * 根据员工工号查询员工信息
     * @param empNo
     * @return
     */
    public Employee getByEmployeeNo(String empNo) {
        Employee whereEmployee = new Employee();
        whereEmployee.setDeleted(0);
        whereEmployee.setStatusT(0);
        whereEmployee.setEmpNo(empNo);
        return baseMapper.selectOne(new QueryWrapper<>(whereEmployee));
    }

    /**
     * 查询可用的员工
     * @param id
     * @return
     */
    public Employee getEmployeeById(long id) {
        Employee employee = baseMapper.selectById(id);
//        if (null != employee && employee.getDeleted() == 0 && employee.getPdaEnable() != 0) {
        if (null != employee && employee.getDeleted() == 0) {
            return employee;
        }
        return null;
    }

    /**
     * 分页查询员工Pda用户
     * @param page
     * @param limit
     * @return
     */
    public IPage<Employee> listPdaEmployeePage(Long departmentId,Integer page, Integer limit) {
        IPage<Employee> iPage = new Page<>(page,limit);
        Employee where = new Employee();
        where.setDepartmentId(departmentId);
        where.setDeleted(DeleteFlagEnum.NOT.getValue());
        where.setPdaEnable(Constant.ENBALE);
        return baseMapper.selectPage(iPage,new QueryWrapper<>(where));
    }

    /**
     * 返回在职员工列表
     * @return
     */
    public List<Employee> listAllWorkEmployee() {
        QueryWrapper<Employee> employeeQueryWrapper = new QueryWrapper<>();
        employeeQueryWrapper.select("id","emp_no","emp_name")
                .eq("status_t",EmployeeStatusEnum.WORK.getValue());
        return baseMapper.selectList(employeeQueryWrapper);
    }
}
