package com.zcxd.gun.feign;

import com.zcxd.common.util.Result;
import com.zcxd.db.model.Employee;
import com.zcxd.db.model.Route;
import com.zcxd.db.model.SchdResult;
import com.zcxd.db.model.Vehicle;
import com.zcxd.gun.config.FeignRequestInterceptor;
import com.zcxd.gun.vo.EmployeeVO;
import com.zcxd.gun.vo.VehicleVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zccc
 */
@Component
@FeignClient(name = "base", fallback = RemoteProvider.RemoteProviderFallback.class,
        configuration = FeignRequestInterceptor.class)
public interface RemoteProvider {
    /**
     * 查询部门是否有权限
     * @param
     */
    @GetMapping("/department/isAuth")
    Result<Boolean> isAuthDepartment(@RequestParam("id")Integer id);

    /**
     * 查询车辆信息
     * @param
     */
    @PostMapping(value = "/vehicle/findVehicleByCondition")
    Result<List<Vehicle>> findVehicleByCondition(@RequestBody VehicleVO vehicleVO);

    /**
     * 查询路线信息
     * @param
     */
    @PostMapping(value = "/route/findById")
    Result<Route> findRouteById(@RequestParam("id") Long id);

    /**
     * 查找排班结果
     * @param
     */
    @PostMapping(value = "/schdResult/findByIds")
    Result<List<SchdResult>> findSchdResultByIds(@RequestParam("ids") List<Long> ids,
                                                 @RequestParam("departmentId") Long departmentId);

    /**
     * 查找员工信息
     * @param
     */
    @PostMapping("/employee/findById}")
    Result findEmployeeById(@PathVariable("id") Long id, @RequestParam("departmentId") Long departmentId);

    @PostMapping(value = "/employee/findByCondition")
    Result<List<Employee>> findEmployeeByCondition(@RequestBody EmployeeVO employeeVO);

    class RemoteProviderFallback implements RemoteProvider {
        String failedStr = "远程调用失败";
        @Override
        public Result<Boolean> isAuthDepartment(Integer id) {
            return Result.fail(failedStr);
        }
        @Override
        public Result<List<Vehicle>> findVehicleByCondition(VehicleVO vehicleVO) {
            return Result.fail(failedStr);
        }

        @Override
        public Result<Route> findRouteById(Long id) {
            return Result.fail(failedStr);
        }

        @Override
        public Result<List<SchdResult>> findSchdResultByIds(List<Long> ids, Long departmentId) {
            return Result.fail(failedStr);
        }

        @Override
        public Result findEmployeeById(Long id, Long departmentId) {
            return Result.fail(failedStr);
        }

        @Override
        public Result<List<Employee>> findEmployeeByCondition(EmployeeVO employeeVO) {
            return Result.fail(failedStr);
        }

    }
}
