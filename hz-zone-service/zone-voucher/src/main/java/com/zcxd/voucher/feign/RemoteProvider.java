package com.zcxd.voucher.feign;

import com.zcxd.common.util.Result;
import com.zcxd.db.model.*;
import com.zcxd.voucher.config.FeignRequestInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * @author zccc
 */
@FeignClient(name = "base", fallback = RemoteProvider.RemoteProviderFallback.class,
    configuration = FeignRequestInterceptor.class)
public interface RemoteProvider {
    @GetMapping("/department/isAuth")
    Result<Boolean> isAuthDepartment(@RequestParam("id")Integer id);

    /**
     * 查询箱包信息
     * @param
     */
    @PostMapping(value = "/boxpackTask/info/{id}")
    Result<BoxpackTask> findBoxpackById(@RequestParam("id") Long id);

    /**
     * 查询银行信息
     * @param
     */
    @PostMapping(value = "/bank/listByCondition")
    Result<Bank> findBankByCondition(@RequestBody Bank bank);


    class RemoteProviderFallback implements RemoteProvider {
        String failedStr = "远程调用失败";
        @Override
        public Result<Boolean> isAuthDepartment(Integer id) {
            return Result.fail(failedStr);
        }

        @Override
        public Result<BoxpackTask> findBoxpackById(Long id) {
            return Result.fail(failedStr);
        }

        @Override
        public Result<Bank> findBankByCondition(Bank bank) {
            return Result.fail(failedStr);
        }
    }
}
