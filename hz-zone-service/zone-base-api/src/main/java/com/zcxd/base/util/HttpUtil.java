package com.zcxd.base.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @description: Http请求工具类
 * @author: yiwenli
 * @create: 2021-04-08 09:32
 **/
public class HttpUtil {

    public static void main(String[] args) throws IOException {
        String url = "http://192.168.7.191:8010/auth/login";
        JSONObject paramMap = new JSONObject();
        paramMap.put("username", "admin");
        paramMap.put("password", "cbpm58062888");
        String result = httpRequest(url,paramMap,"POST");
        JSONObject resultObject = JSONObject.parseObject(result);
        System.out.println(resultObject);
    }

    /**
     * 发送http请求
     *
     * @param requestUrl    请求地址
     * @param params        请求参数
     * @param requestMethod 请求方式  POST/GET
     */
    public static String httpRequest(String requestUrl, JSONObject params, String requestMethod) {
        String result = null;
        StringBuilder paramsStr = new StringBuilder();
        params.forEach((key, value) -> {
            try {
                paramsStr.append(URLEncoder.encode(key, "UTF-8")).append("=").append(URLEncoder.encode(String.valueOf(value), "UTF-8")).append("&");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        });
        if("POST".equals(requestMethod)) {

            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(30 * 1000)
                    .setConnectTimeout(30 * 1000)
                    .setConnectionRequestTimeout(30 * 1000)
                    .build();
            HttpPost httpPost = new HttpPost(requestUrl);
            httpPost.setConfig(requestConfig);
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            httpPost.setEntity(new StringEntity(paramsStr.substring(0, paramsStr.length() - 1), StandardCharsets.UTF_8));

            try (CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();
                 CloseableHttpResponse response = httpclient.execute(httpPost)) {
                HttpEntity entity = response.getEntity();
                result = EntityUtils.toString(entity, "UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if("GET".equals(requestMethod)) {

            String requestUrlWithParam = requestUrl + "?" + paramsStr.substring(0, paramsStr.length() - 1);
            HttpGet httpGet = new HttpGet(requestUrlWithParam);
            try(CloseableHttpClient httpClient = HttpClients.createDefault();
                CloseableHttpResponse httpResponse = httpClient.execute(httpGet)) {
                HttpEntity httpEntity = httpResponse.getEntity();
                result = EntityUtils.toString(httpEntity, "UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return result;
    }

}

