package com.zcxd.pda.config;


import com.alibaba.fastjson.JSONObject;
import com.zcxd.db.model.PdaUser;
import com.zcxd.db.model.SysUser;

import java.util.Optional;

/**
 * 用户上下文
 */
public class UserContextHolder {

    private transient static ThreadLocal<JSONObject> threadLocal;

    private UserContextHolder() {
        threadLocal = new ThreadLocal<>();
    }

    /**
     * 创建实例
     *
     * @return
     */
    public static UserContextHolder getInstance() {
        return SingletonHolder.sInstance;
    }

    /**
     * 静态内部类单例模式
     * 单例初使化
     */
    private static class SingletonHolder {
        private static final UserContextHolder sInstance = new UserContextHolder();
    }

    /**
     * 用户上下文中放入信息
     *
     * @param JSONObject
     */
    public static void setContext(JSONObject JSONObject) {
        threadLocal.set(JSONObject);
    }

    /**
     * 获取上下文中的信息
     *
     * @return
     */
    public static JSONObject getContext() {
        return threadLocal.get();
    }

    /**
     * 获取上下文中的用户名
     *
     * @return
     */
    public static String getUsername() {
        return Optional.ofNullable(threadLocal.get()).orElse(new JSONObject()).getString("username");
    }

    /**
     * 获取上下文中的用户ID
     *
     * @return
     */
    public static Long getUserId() {
        return Optional.ofNullable(threadLocal.get()).orElse(new JSONObject()).getLong("id");
    }
    /**
     * 获取上下文中的用户类型
     *
     * @return
     */
    public static int getUserType() {
        return Optional.ofNullable(threadLocal.get()).orElse(new JSONObject()).getInteger("userType");
    }
    /**
     * 获取上下文中的用户
     *
     * @return
     */
    public static PdaUser getUser() {
        return Optional.ofNullable(threadLocal.get()).orElse(new JSONObject()).toJavaObject(PdaUser.class);
    }
    /**
     * 清空上下文
     */
    public void clear() {
        threadLocal.remove();
    }

}
