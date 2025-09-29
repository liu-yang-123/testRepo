package com.zcxd.base.controller;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.zcxd.base.util.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zcxd.base.config.UserContextHolder;
import com.zcxd.base.service.SysMenuService;
import com.zcxd.base.service.SysPermissionService;
import com.zcxd.base.service.SysUserService;
import com.zcxd.base.vo.LoginVO;
import com.zcxd.common.util.Result;
import com.zcxd.common.util.TokenUtils;
import com.zcxd.db.model.SysUser;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;

/**
 * 
 * @ClassName SysAuthController
 * @Description 登录管理控制器
 * @author 秦江南
 * @Date 2021年5月7日上午11:18:24
 */
@RestController
@RequestMapping("/auth")
@Api(tags = "登录管理")
public class SysAuthController {

    @Autowired
    private SysUserService userService;

    @Autowired
    private SysPermissionService permissionService;
    @Value("${finservice.username}")
    private String username;
    @Value("${finservice.password}")
    private String password;
    @Value("${finservice.url}")
    private String url;
    @Value("${finservice.area-username}")
    private String areaUsername;
    @Value("${finservice.area-password}")
    private String areaPassword;
    @Value("${finservice.area-url}")
    private String areaUrl;



    
    @ApiOperation(value = "登录")
    @ApiImplicitParam(name = "loginVO", value = "登录数据", required = true, dataType = "LoginVO")
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @PostMapping("/login")
    public Result login(@RequestBody @Validated LoginVO loginVO) {
    	QueryWrapper wrapper = Wrappers.query().eq("username", loginVO.getUsername()).eq("deleted", 0);
    	SysUser user = userService.getOne(wrapper);
    	if (user == null) {
            return  Result.fail("找不到用户（" + loginVO.getUsername() + "）的帐号信息");
        }
        if("1".equals(user.getStatusT())){
            return Result.fail("用户（" + loginVO.getUsername() + "）帐号已锁定不可用");
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(loginVO.getPassword(), user.getPassword())) {
            return Result.fail("您输入的密码错误");
        }
    	//生成token
        String token = TokenUtils.creatToken(user.getId(), null);
        
        Map<Object, Object> result = new HashMap<>(1);
        result.put("token", token);
        return Result.success(result);
    }
    
    @ApiOperation(value = "查询当前用户权限", notes = "查询当前用户权限")
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @GetMapping("/info")
    public Result info() {
        JSONObject admin = UserContextHolder.getContext();
        String roleId = admin.getString("roleIds");
        String[] roleIds = roleId.substring(1,roleId.length()-1).split("/");
        Set<String> permissions = permissionService.getPermissionByRoleIds(roleIds);
        
        Map<String, Object> data = new HashMap<>(3);
        data.put("name", admin.getString("username"));
        data.put("avatar", admin.getString("avatar"));
        data.put("roles", permissions);
        return Result.success(data);
    }
    
    /**
     * 
     * @Title permission
     * @Description 获得角色拥有的权限列表
     * @param userId
     * @return
     * @return 返回类型 Result
     */
    @PostMapping("/checkPermission")
    public Result permission(@RequestParam("userId") Integer userId, @RequestParam("url") String url) {

//        QueryWrapper queryWrapper = Wrappers.query().eq("url",url);
//        SysMenu menu = menuService.getOne(queryWrapper);
//        if (menu == null){
//            return Result.success("无需验证");
//        }
//        SysUser user = userService.getById(userId);
        SysUser user = userService.getUserById(userId.longValue());
        String roleId = user.getRoleIds();
        String[] roleIds = roleId.substring(1,roleId.length()-1).split("/");

        if (roleIds.length > 0) {
            boolean exist = false;
            for (int i = 0; i< roleIds.length; i++ ) {
                exist = permissionService.checkPerssionByRoleId(Long.parseLong(roleIds[i]),url);
                if (exist) {
                    return Result.success("验证成功");
                }
            }
        }
//        Set<String> permissions = permissionService.getPermissionByRoleIds(roleIds);
//        if (permissions.contains(url)){
//            return Result.success("验证成功");
//        }
        return Result.fail("验证失败");
    }

    @ApiOperation(value = "登录总平台", notes = "登录总平台")
    @ApiResponse(code = 0, message = "处理成功", response = Result.class)
    @GetMapping("/finserviceLogin")
    public Result finserviceLogin(){
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("username",username);
        jsonObject.put("password",password);
        String result=HttpUtil.httpRequest(url,jsonObject,"POST");
        JSONObject json=JSONObject.parseObject(result);
        if(!json.get("errno").toString().equals("0")){
            return Result.fail(json.get("errmsg").toString());
        }
        String dadata = json.get("data").toString();
        String token =JSONObject.parseObject(dadata).get("token").toString();
        return Result.success(token);
    }

    @ApiOperation(value = "文件上送管理", notes = "文件上送管理")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @GetMapping("/safe/send")
    public Result send(){
        return fileLogin();
    }

    @ApiOperation(value = "文件接收管理", notes = "文件接收管理")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @GetMapping("/safe/receive")
    public Result receive(){
        return fileLogin();
    }

    @ApiOperation(value = "文件上送管理", notes = "文件上送管理")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @GetMapping("/product/send")
    public Result sendProduct(){
        return fileLogin();
    }

    @ApiOperation(value = "文件接收管理", notes = "文件接收管理")
    @ApiResponse(code = 200, message = "处理成功", response = Result.class)
    @GetMapping("/product/receive")
    public Result receiveProduct(){
        return fileLogin();
    }


    /**
     * @Description:文件收发管理登录辅助方法
     * @Author: lilanglang
     * @Date: 2022/7/11 10:01
     **/
    private Result fileLogin(){
        UserContextHolder.getInstance();
        JSONObject admin =UserContextHolder.getContext();
        String account=admin.getString("username");
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("username",areaUsername);
        jsonObject.put("password",areaPassword);
        jsonObject.put("areaAccount",account);
        String result= HttpUtil.httpRequest(areaUrl,jsonObject,"POST");
        JSONObject json=JSONObject.parseObject(result);
        if(!json.get("errno").toString().equals("0")){
            return Result.fail(json.get("errmsg").toString());
        }
        String data = json.get("data").toString();
        String token =JSONObject.parseObject(data).get("token").toString();
        return Result.success(token);
    }


}
