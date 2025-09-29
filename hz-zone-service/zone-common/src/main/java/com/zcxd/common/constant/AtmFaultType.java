package com.zcxd.common.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * ATM故障定义
 */

public class AtmFaultType {

	private static final Map<String,String> faultMap = new HashMap<String,String>(){
			{
				put("cash","卡钞");
				put("card","取吞卡");
				put("printer","凭条打印机故障");
				put("safebox","保险箱故障");
				put("reader","读卡器故障");
				put("sensor","感应器故障");
				put("hardDisk","硬盘故障");
				put("outModule","吐钞机构故障");
				put("verifyModule","验钞机构故障");
				put("pm","PM");
				put("belt","皮带故障");
				put("power","电源故障");
				put("cashbox","钞箱故障");
				put("wheel","挖钞轮故障");
				put("monitor","显示器故障");
				put("reinstall","系统重装");
				put("pwdLock","密码锁故障");
				put("cashOutlet","出钞口故障");
				put("key","钥匙故障");
			}
	};

	public static String getFaultTypeText(String faultType) {
		return faultMap.getOrDefault(faultType,"");
	}

}
