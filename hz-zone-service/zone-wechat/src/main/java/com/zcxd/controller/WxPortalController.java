package com.zcxd.controller;

import com.alibaba.fastjson.JSONObject;
import com.zcxd.common.annotation.OperateLog;
import com.zcxd.common.annotation.OperateType;
import com.zcxd.common.util.SecurityUtils;
import com.zcxd.db.model.Employee;
import com.zcxd.domain.UserInfoWechat;
import com.zcxd.service.EmployeeService;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 账号管理控制层
 *@author lilanglang
 */
@Controller
public class WxPortalController {
    @Autowired
    private EmployeeService employeeService;


    /**
     * @Description:短信验证
     * @Author: lilanglang
     * @Date: 2021/7/15 17:19
     * @param empNo:
     * @param phone:
     **/
    @ResponseBody
    @RequestMapping("/sendsms")
    public JSONObject sendsms(String empNo, String phone) {
        JSONObject jsonRet = new JSONObject();
        Employee employee=employeeService.getByEmpNo(empNo);
        if(employee==null){
            jsonRet.put("code",10000);
            jsonRet.put("msg","非系统员工不允许绑定");
            return jsonRet;
        }
//        String phoneNum=SecurityUtils.encryptAES(phone);
        if(!SecurityUtils.decryptAES(employee.getMobile()).equals(phone)){
            jsonRet.put("code",10000);
            jsonRet.put("msg","请使用本人手机号码进行操作");
            return jsonRet;
        }

//        String Url = "http://106.ihuyi.com/webservice/sms.php?method=Submit";
//
//        HttpClient client = new HttpClient();
//        PostMethod method = new PostMethod(Url);
//
//        client.getParams().setContentCharset("GBK");
//        method.setRequestHeader("ContentType","application/x-www-form-urlencoded;charset=GBK");
//
//        int mobile_code = (int)((Math.random()*9+1)*100000);
//
//        String content = new String("您的验证码是：" + mobile_code + "。请不要把验证码泄露给其他人。");
//        System.out.println(content);
//        NameValuePair[] data = {//提交短信
//                new NameValuePair("account", "C07123652"), //查看用户名 登录用户中心->验证码通知短信>产品总览->API接口信息->APIID
//                new NameValuePair("password", "2d41836af974daf01651b64dc05fc589"), //查看密码 登录用户中心->验证码通知短信>产品总览->API接口信息->APIKEY
//                //new NameValuePair("password", util.StringUtil.MD5Encode("密码")),
//                new NameValuePair("mobile", phone),
//                new NameValuePair("content", content),
//        };
//        method.setRequestBody(data);

        jsonRet.put("result","2");
//        jsonRet.put("code",mobile_code);
        jsonRet.put("tel",phone);
        return jsonRet;

    }

    /**
     * @Description:账号绑定
     * @Author: lilanglang
     * @Date: 2021/7/13 14:34
     * @param userinfo:
     **/
    @OperateLog(value="账号绑定", type= OperateType.EDIT)
    @RequestMapping("/loginInfo")
    public String loginInfo(UserInfoWechat userinfo) {
        //绑定phone与openid
        Employee employee=employeeService.getByEmpNo(userinfo.getEmpNo());
        if(employee==null)
        {
            return "failedMessage";
        }else if(!SecurityUtils.decryptAES(employee.getMobile()).equals(userinfo.getPhone())){
            return "failedPhoneMessage";
        }
        else
        {

            //判断该账号是否已经被绑定，已绑定则不允许重复绑定
            Employee emp = employeeService.getByEmpNoAndPhone(userinfo.getEmpNo(),userinfo.getPhone());
            if(emp!=null&& StringUtils.isNotEmpty(emp.getWxOpenid())){
                return "alreadyBing";
            }
            employee.setWxOpenid(userinfo.getUserOpenid());
            if(employeeService.updateById(employee))
            {
                return "operationSuccess";
            }else
            {
                return "operationFailed";
            }
        }
    }

}
