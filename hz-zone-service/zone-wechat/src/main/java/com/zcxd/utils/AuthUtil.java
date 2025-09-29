package com.zcxd.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;


import java.io.IOException;

public class AuthUtil {
	public static final String LOCALHOST="127.0.0.1:80";
	public static final String APPID="wxfeaa7698c7c50663";
    public static final String APPSECRET="e0ff9d1829e8f4a3c05177032c0dc922";
    //public static final String APPID="wxa0ff727d5d306fef";//正式
   // public static final String APPSECRET="0b5345c911eec0c57b1d05dcd9f2dca3";
    public static final String BODY="微信付款";
    public static final String MchId="1254787301";
    public static final String PAYKEY="wpwpwpwpwpwpwpwpwpwpwpwpwpwpwpwp";
    //public static final String PAYKEY="64d9052654871ddc77c721c4334e1c38";
    public static final String Notify_url="http://mis.zjzcab.com.cn/weixinComeback";
    //public static final String Notify_url="http://www.bailuo-ai.com/weixinComeback";//正式
    //public static final String AppSercret = "54d89845c34efb1e42c1d4d70e606626";
    public static final String refund_file_path="退款时需要的文件地址";

    public static long iTokenTime=0;
    public static String TOKENID="最新的tokenid";
    public static String OPENID="最新的openid";

    /**
     * 预支付请求地址
     */
    public static final String  PrepayUrl = "https://api.mch.weixin.qq.com/pay/unifiedorder";

    /**
     * 查询订单地址
     */
    public static final String  OrderUrl = "https://api.mch.weixin.qq.com/pay/orderquery";

    /**
     * 关闭订单地址
     */
    public static final String  CloseOrderUrl = "https://api.mch.weixin.qq.com/pay/closeorder";

    /**
     * 申请退款地址
     */
    public static final String  RefundUrl = "https://api.mch.weixin.qq.com/secapi/pay/refund";

    /**
     * 查询退款地址
     */
    public static final String  RefundQueryUrl = "https://api.mch.weixin.qq.com/pay/refundquery";

    /**
     * 下载账单地址
     */
    public static final String  DownloadBillUrl = "https://api.mch.weixin.qq.com/pay/downloadbill";


    public static JSONObject doGetJson(String url) throws IOException {
        JSONObject jsonObject=null;
        DefaultHttpClient defaultHttpClient=new DefaultHttpClient();
        HttpGet httpGet=new HttpGet(url);
        HttpResponse httpResponse = defaultHttpClient.execute(httpGet);
        HttpEntity httpEntity=httpResponse.getEntity();
        if(httpEntity!=null){
            String result= EntityUtils.toString(httpEntity,"UTF-8");
            jsonObject= JSON.parseObject(result);
            //System.out.println("jsonObject:  "+jsonObject);
        }
        httpGet.releaseConnection();
        return jsonObject;
    }
}
