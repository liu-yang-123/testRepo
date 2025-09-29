package com.zcxd.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zcxd.domain.WechatMenu;

public class WechatUtil {
	
	//获取access_token
	private static final String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
 
	//获取access_token
	public static AccessToken getAccessToken(String appId,String appSecert){
		AccessToken accessToken = new AccessToken();
		String url = ACCESS_TOKEN_URL.replace("APPID", appId).replace("APPSECRET", appSecert);
		JSONObject res = HttpsUtil.httpRequest(url, "GET", null);
//		String errcode = (String) res.get("errcode");
	    try{    	
			String token = (String) res.get("access_token");
			accessToken.setAccessToken(token);
			Long expiresIn = res.getLong("expires_in");
			accessToken.setExpiresIn(expiresIn);
		}catch (Exception e) {
			accessToken = null;
		}
		return accessToken;
	}
	
	
	//自定义创建菜单
		private static final String CREATE_MENU = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN";
	 
	//自定义菜单
		public static  Integer createMenu(String appId, String appSecert, WechatMenu wechatMenu){
			AccessToken accessToken = getAccessToken(appId, appSecert);
			String url = CREATE_MENU.replace("ACCESS_TOKEN", accessToken.getAccessToken());

			JSONObject res = HttpsUtil.httpRequest(url, "POST", JSON.toJSONString(wechatMenu));
			return (Integer) res.get("errcode");
		}
	
}
