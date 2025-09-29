package com.zcxd.db.utils;

public class CacheKeysDef {

    public static final String PdaBoxMaping = "boxmap_";
    public static final String PdaFinishAtmTask = "atm_task_";
    public static final String AtmTaskUploadMap = "atm_upload_map";
    public static final String ComAppUserPrefix = "AppUser_"; //app用户信息
    public static final String BankPdaUserPrefix = "BankPdaUser_"; //银行app用户信息

    public static final String RoleAuthSetPrefix = "RoleAuthSet_"; //角色授权列表
    public static final String WhiteIpsSet = "WhiteIpsSet"; //ip白名单列表
    public static final String WhiteMacsSet = "WhiteMacsSet"; //MAC白名单列表
    public static final String SysUserPrefix = "SysUser_"; //系统用户信息

    //微信推送通知
    public static final String Scheduling = "Scheduling"; //排班结果
    public static final String SchdChangeQueue = "SchdChangeQueue"; //排班变动通知
    //【任务变动通知】当前线路撤销1个任务，请登录app查看！
    //【任务变动通知】当前线路增加1个任务，请登录app查看！
    public static final String TaskChangeQueue = "TaskChangeQueue"; //任务变动通知
}
