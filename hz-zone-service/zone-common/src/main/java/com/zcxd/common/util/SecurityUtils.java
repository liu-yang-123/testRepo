package com.zcxd.common.util;

import org.springframework.util.StringUtils;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

/**
 * 数据安全加密工具类 
 * @author cuiyh
 * @version 1.0
 *
 */
public class SecurityUtils {
	
	private static final String DEFAULT_AES_CRYPT_KEY = "CCDVASDEWQ012345";// AES加密秘钥
	private static final String AES_IV_STRING = "ASDFG123456QWERT";// AES 初始化向量

	/**
	 * 对输入的字符串进行MD5加密 
	 * @param str 需要加密的字符串
	 * @return MD5加密后的字符串
	 */
	public static String getMD5(String str) {
		if (StringUtils.isEmpty(str)) {
			return null;
		}
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			byte[] digest = messageDigest.digest(str.getBytes());
			return new String(Hex.encodeHex(digest));
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}
	
	/**
	 * 使用默认密钥进行 AES加密
	 * @param content 须加密内容
	 * @return 字符串
	 */
	public static String encryptAES(String content){
		return encryptAES(content,DEFAULT_AES_CRYPT_KEY,AES_IV_STRING);
	}
	
	/**
	 * AES加密
	 * @param content 须加密内容
	 * @param key 密钥
	 * @return 字符串
	 */
	public static String encryptAES(String content, String key, String iv) {  
        try {             
                KeyGenerator kgen = KeyGenerator.getInstance("AES"); 
                SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");     
                secureRandom.setSeed(key.getBytes());  
                kgen.init(128,secureRandom);  
                SecretKey secretKey = kgen.generateKey();  
                byte[] enCodeFormat = secretKey.getEncoded();  
                SecretKeySpec secretKeySpec = new SecretKeySpec(enCodeFormat, "AES");  
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");// 创建密码器  
                byte[] byteContent = content.getBytes("utf-8");  
                byte[] ivBytes  = iv.getBytes();
                IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);     
                cipher.init(Cipher.ENCRYPT_MODE,  secretKeySpec, ivSpec);
                byte[] result = cipher.doFinal(byteContent);  
                return parseByte2HexStr(result); // 加密  
        } catch (Exception e) {  
                e.printStackTrace();  
                throw new RuntimeException(e.getMessage());
        } 
}  

	/**
	 * 使用默认密钥进行 AES解密
	 * @param content
	 * @return 字符串
	 */
	public static String decryptAES(String content){
		return decryptAES(content,DEFAULT_AES_CRYPT_KEY,AES_IV_STRING);
	}
	/** 
	 * AES解密 
	 * @param content  待解密内容 
	 * @param key 解密密钥 
	 * @return 字符串
	 */  
	public static String decryptAES(String content, String key,String iv) {  
	        try {  
	                 KeyGenerator kgen = KeyGenerator.getInstance("AES");  
	                 SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");     
	                 secureRandom.setSeed(key.getBytes());  
	                 kgen.init(128,secureRandom);  
	                 SecretKey secretKey = kgen.generateKey();  
	                 byte[] enCodeFormat = secretKey.getEncoded();  
	                 SecretKeySpec secretKeySpec = new SecretKeySpec(enCodeFormat, "AES");              
	                 Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");// 创建密码器  
	                 byte[] ivBytes  = iv.getBytes();
	                 IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);     
	                 cipher.init(Cipher.DECRYPT_MODE,  secretKeySpec, ivSpec);
	                 byte[] result = cipher.doFinal(parseHexStr2Byte(content));  
	                 return new String(result); // 加密  
	        } catch (Exception e) {  
                e.printStackTrace();
                return "";
                //throw new RuntimeException(e.getMessage());
	        }  
	} 

	
	/**
	 * 将二进制转换成16进制
	 * @param buf 字节数组
	 * @return 字符串
	 */
	public static String parseByte2HexStr(byte buf[]) {  
        StringBuffer sb = new StringBuffer();  
        for (int i = 0; i < buf.length; i++) {  
                String hex = Integer.toHexString(buf[i] & 0xFF);  
                if (hex.length() == 1) {  
                        hex = '0' + hex;  
                }  
                sb.append(hex.toUpperCase());  
        }  
        return sb.toString();  
	} 
	
	/**将16进制转换为二进制 
	 * @param hexStr 16进制字符串
	 * @return 字节数组
	 */  
	public static byte[] parseHexStr2Byte(String hexStr) {  
	        if (hexStr.length() < 1) {
                return null;
            }
	        byte[] result = new byte[hexStr.length()/2];  
	        for (int i = 0;i< hexStr.length()/2; i++) {  
	                int high = Integer.parseInt(hexStr.substring(i*2, i*2+1), 16);  
	                int low = Integer.parseInt(hexStr.substring(i*2+1, i*2+2), 16);  
	                result[i] = (byte) (high * 16 + low);  
	        }  
	        return result;  
	}
	
	/**
	 * 对字符串进行MD5编码(用户密码加密)
	 * @param str 待编码字符串
	 * @return 字符串
	 */
	public static String encodeByMD5(String str) {
		if (StringUtils.isEmpty(str)) {
			return null;
		}
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			byte[] digest = messageDigest.digest(str.getBytes());
			return new String(Hex.encodeHex(digest));
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	// 加密
    public static String Encrypt(String sSrc, String sKey) throws Exception {
        if (sKey == null) {
            System.out.print("Key为空null");
            return null;
        }
        // 判断Key是否为16位
        if (sKey.length() != 16) {
            System.out.print("Key长度不是16位");
            return null;
        }
        byte[] raw = sKey.getBytes("utf-8");
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");//"算法/模式/补码方式"
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));
 
        return new Base64().encodeToString(encrypted);//此处使用BASE64做转码功能，同时能起到2次加密的作用。
    }
 
    // 解密
    public static String Decrypt(String sSrc, String sKey) throws Exception {
        try {
            // 判断Key是否正确
            if (sKey == null) {
                System.out.print("Key为空null");
                return null;
            }
            // 判断Key是否为16位
            if (sKey.length() != 16) {
                System.out.print("Key长度不是16位");
                return null;
            }
            byte[] raw = sKey.getBytes("utf-8");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] encrypted1 = new Base64().decode(sSrc);//先用base64解密
            try {
                byte[] original = cipher.doFinal(encrypted1);
                String originalString = new String(original,"utf-8");
                return originalString;
            } catch (Exception e) {
                System.out.println(e.toString());
                return null;
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
            return null;
        }
    }
 
    public static String bytesToHexString(byte[] src){   
        StringBuilder stringBuilder = new StringBuilder("");   
        if (src == null || src.length <= 0) {   
            return null;   
        }   
        for (int i = 0; i < src.length; i++) {   
            int v = src[i] & 0xFF;   
            String hv = Integer.toHexString(v);   
            if (hv.length() < 2) {   
                stringBuilder.append(0);   
            }   
            stringBuilder.append(hv);   
        }   
        return stringBuilder.toString();   
    }   

    
    public static byte[] hexStringToBytes(String hexString) {   
        if (hexString == null || hexString.equals("")) {   
            return null;   
        }   
        hexString = hexString.toUpperCase();   
        int length = hexString.length() / 2;   
        char[] hexChars = hexString.toCharArray();   
        byte[] d = new byte[length];   
        for (int i = 0; i < length; i++) {   
            int pos = i * 2;   
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));   
        }   
        return d;   
    }   
    
    public static byte charToByte(char c) {   
        return (byte) "0123456789ABCDEF".indexOf(c);   
    }  
    
    public static String aesDecrypt(String str, String key) throws Exception {
        if (str == null || key == null) return null;
        
        //判断Key是否为16位  
        if (key.length() != 16) {  
            System.out.print("Key长度不是16位");  
            return null;  
        }
        
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key.getBytes("GBK"), "AES"));
        byte[] bytes = hexStringToBytes(str);
        bytes = cipher.doFinal(bytes);
        return new String(bytes, "utf-8");
    }

    public static void main(String[] args) {
		try {
			String result = encryptAES("14800001111");
			System.out.println("enc result = " + result);
			result = decryptAES(result);
			System.out.println("dec result = " + result);


		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}