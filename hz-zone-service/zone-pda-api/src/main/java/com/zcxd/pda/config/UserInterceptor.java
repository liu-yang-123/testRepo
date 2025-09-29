package com.zcxd.pda.config;

import com.alibaba.fastjson.JSONObject;
import com.zcxd.common.util.TokenUtils;
import com.zcxd.db.model.BankTeller;
import com.zcxd.db.model.Employee;
import com.zcxd.db.model.PdaUser;
import com.zcxd.common.constant.Constant;
import com.zcxd.pda.service.impl.BankTellerService;
import com.zcxd.pda.service.impl.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class UserInterceptor implements HandlerInterceptor {

    @Autowired
    private BankTellerService bankTellerService;
    @Autowired
    private EmployeeService employeeService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //从网关获取并校验,通过校验就可信任x-client-token-user中的信息
        String token=request.getHeader("X-Token");
        if (token!=null) {
        	int userId = TokenUtils.getTokenUserId(token);
        	int userType = TokenUtils.getTokenUserType(token);
        	PdaUser user = new PdaUser();
        	if (Constant.USER_EMPLOYEE == userType) {
//                Employee employee = employeeService.getById(userId);
                Employee employee = employeeService.getEmployeeUserById((long)userId);
                user.setId(employee.getId());
                user.setUserType(userType);
                user.setUserName(employee.getEmpNo());
            } else {
        	    BankTeller bankTeller = bankTellerService.getById(userId);
                user.setId(bankTeller.getId());
                user.setUserType(userType);
                user.setUserName(bankTeller.getTellerNo());
            }
            UserContextHolder.getInstance().setContext((JSONObject) JSONObject.toJSON(user));

        }
        return true;
    }

    private void checkToken(String token) {
        //TODO 从网关获取并校验,通过校验就可信任token中的信息
        log.debug("//TODO 校验token:{}", token);

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        UserContextHolder.getInstance().clear();
    }
}