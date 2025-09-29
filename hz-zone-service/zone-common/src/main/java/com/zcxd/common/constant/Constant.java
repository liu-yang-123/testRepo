package com.zcxd.common.constant;

import java.math.BigDecimal;

public class Constant {

	
    public static final String USER_DEFAULT_PWD = "123456";
    public static final String USER_DEFAULT_STRONG_PWD = "Zjzc2024!";

    public static final int USER_EMPLOYEE = 0;
    public static final int USER_BANK_TELLER = 1;

    public static final int EMPLOYEE_STATUS_WORK = 0;
    public static final int EMPLOYEE_STATUS_QUIT = 1;

//    public static final int ROLE_STORE_KEEPER = 1; //库管员
//    public static final int ROLE_CLEAR_MAN = 2; //清分员
//    public static final int ROLE_BUSINESS_MAN = 3; //线路业务员
//    public static final int ROLE_BANK_TELLER = 4; //

    //券别属性
    public static final String PAPER = "P"; //纸币
    public static final String COIN = "C"; //硬币

    //出入库类型

    //ATM默认券别
    public static final  int ATM_DEF_DENOM_VALUE = 100; //默认面额
    public static final  int ATM_DEF_BUNDLE_SIZE = 10; //默认把数/捆
    public static final  int ATM_DEF_WAD_SIZE = 100; //张数/把

    //状态
    public static final  int ENBALE = 1; //启用
    public static final  int DISABLE = 0; //禁用

    //atm加钞金额类型
    public static final int ATM_DENOM_100  = 100;
    public static final int ATM_DENOM_10  = 10;
    public static final int PIECES_PER_BOX = 1000; //一盒钞票张数

    public static final String TOP_BANK_PARENTS = "/0/";
    
//    //excel导入银行类型转换
//    public static final int HZICBCID = 1;
//    public static final int RCBID = 505;
//    public static final int BOBID = 601;
//   //测试 
//    //public static final int JDICBCID = 610;
//    //正式
//    public static final int JDICBCID = 614;
}
