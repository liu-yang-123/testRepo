package com.zcxd.common.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author songanwei
 * @description
 * @date 2021/04/27
 */
public class TokenUtils {

    /**
     * 过期时间设置(24小时)
     */
    private static final long EXPIRE_TIME = 24*60*60*1000;

    /**
     * 私钥设置
     */
    private static final String TOKEN_SECRET = "5xcJVrXNyQDIxK1l2RS9nw";

    /**
     * 获取token对象
     * @return
     */
    public static int  getTokenUserId(String token) {
        Algorithm algorithm = null;
        try {
            algorithm = Algorithm.HMAC256(TOKEN_SECRET);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        DecodedJWT jwt = JWT.require(algorithm).build().verify(token);
        //过期时间验证
        Date expireDate = jwt.getClaim("exp").asDate();
        Date currentDate = new Date();
        int compareTo = expireDate.compareTo(currentDate);
        if (compareTo < 0){
            return 0;
        }
        return jwt.getClaim("id").asInt();
    }

    /**
     * 获取token对象
     * @return
     */
    public static int  getTokenUserType(String token) {
        Algorithm algorithm = null;
        try {
            algorithm = Algorithm.HMAC256(TOKEN_SECRET);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        DecodedJWT jwt = JWT.require(algorithm).build().verify(token);
        return jwt.getClaim("role").asInt();
    }

    /**
     * 生成token字符串
     * @param userId
     * @return
     */
    public static String creatToken(Long userId,Integer roleType) {
        //这里是传入的是token对象，决定token的内容
        //交给上面的实现类得到token
        //过期时间和加密算法设置
        Date date=new Date(System.currentTimeMillis()+EXPIRE_TIME);
        Algorithm algorithm = null;
        try {
            algorithm = Algorithm.HMAC256(TOKEN_SECRET);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //头部信息
        Map<String,Object> header=new HashMap<>(2);
        header.put("typ","JWT");
        header.put("alg","HS256");

        return JWT.create()
                .withHeader(header)
                .withClaim("id",userId)
                .withClaim("role",roleType)
                .withExpiresAt(date)
                .sign(algorithm);
    }
}
