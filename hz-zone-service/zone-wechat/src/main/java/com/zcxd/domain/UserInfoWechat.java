package com.zcxd.domain;

import lombok.Data;

import java.util.Date;
@Data
public class UserInfoWechat {


	//主键id
	private Long userId;
	//用户头像
	private String userAvatar;
	//用户昵称
	private String userNickname;
	//用户性别
	private Integer userSex;
	//用户真实名称
	private String userName;
	//推荐用户id
	private Long upUserId;
	//用户所在国家
	private String userCountry;
	//用户所在省份
	private String userProvince;
	//用户所在城市
	private String userCity;
	//用户openid
	private String userOpenid;
	//0:不是,1:是
	private Integer userIsDistributor;
	//
	private Date userCreateTime;
	//
	private Date userUpdateTime;

	private String qrcode;

	private String upUserName;
	
	private String phone;
	
	private String headimgurl;
	
	private String villageName;
	
	private String ownerName;

	private String empNo;


}
