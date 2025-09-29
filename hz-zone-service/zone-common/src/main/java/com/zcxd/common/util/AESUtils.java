package com.zcxd.common.util;

import org.apache.commons.lang.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;

/**
 * create by tongjie
 * 时间：2021/9/13/013
 */
public class AESUtils {
    //填写您的加密密钥
//    private  byte[] raw ;
//
//    private AESUtils(){
//        getRawkey("ZCXDHZZCZHJHMKEY");//密钥的长度对应，16位密钥= AES-128，24位密钥= AES-192，32位密钥= AES-256位
//    }
//    public static AESUtils getInstance(){
//        return Holder.instance;
//    }
//
//    private static class Holder{
//        private static AESUtils instance = new AESUtils();
//    }
//
//    public void getRawkey(String pwd){
//        try {
//            raw = pwd.getBytes("UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//    }

    public static byte[] getRawkey(String pwd){
        try {
            return pwd.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] hex2byte(String strhex) {
        if (strhex == null) {
            return null;
        }
        int l = strhex.length();
        if (l % 2 == 1) {
            return null;
        }
        byte[] b = new byte[l / 2];
        for (int i = 0; i != l / 2; i++) {
            b[i] = (byte) Integer.parseInt(strhex.substring(i * 2, i * 2 + 2),
                    16);
        }
        return b;
    }

    public static String byte2hex(byte[] b) {
        StringBuilder stringBuilder = new StringBuilder();
        String stmp;
        for (int n = 0; n < b.length; n++) {
            stmp = (Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1) {
                stringBuilder.append("0").append(stmp);
            } else {
                stringBuilder.append(stmp);
            }
        }
        return stringBuilder.toString().toUpperCase();
    }
    public static String decryptResponse(String response, String aeskey) throws Exception {
        if(StringUtils.isEmpty(response)){
            return "";
        }
        byte[] rawKey = getRawkey(aeskey);
        if (rawKey == null) {
            return response;
        }
        SecretKeySpec skeySpec = new SecretKeySpec(rawKey, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] encrypted1 = hex2byte(response);
//        byte[] encrypted1 = response.getBytes(response);
        byte[] original = cipher.doFinal(encrypted1);
        return new String(original,"utf-8");
    }

    public static String encryptParams(String params,String aeskey) throws Exception{
        if(StringUtils.isEmpty(params)){
            params = "";
        }
        byte[] rawKey = getRawkey(aeskey);
        if (rawKey == null) {
            return params;
        }
        SecretKeySpec skeySpec = new SecretKeySpec(rawKey, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(params.getBytes());
        params = byte2hex(encrypted);
        return params;
    }
}
