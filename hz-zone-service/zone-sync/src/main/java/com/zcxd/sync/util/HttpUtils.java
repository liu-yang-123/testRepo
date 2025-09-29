package com.zcxd.sync.util;

import com.alibaba.fastjson.JSON;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

/**
 * http接口工具类
 * @Author: lilanglang
 * @Date: 2021/9/2 11:01
 **/
public class HttpUtils {
    public static String post(List<NameValuePair> message, File file, String to)
            throws ParseException, IOException {
        HttpPost post = new HttpPost(to);
        MultipartEntity reqEntity = new MultipartEntity();
        FileBody bin = new FileBody(file);
        Charset charset = Charset.forName("UTF-8");//设置编码
        if (message != null) {
            for (NameValuePair nameValuePair : message) {
                StringBody comment = new StringBody(nameValuePair.getValue(), charset);
                reqEntity.addPart(nameValuePair.getName(), comment);
            }
        }
        reqEntity.addPart("file",bin);
        post.setEntity(reqEntity);
        System.out.println(("[] Post \"" + message + "\" to " + post.getURI()));
        CloseableHttpClient client = HttpClients.createDefault();
        CloseableHttpResponse response = client.execute(post);
        int statusCode = response.getStatusLine().getStatusCode();
        System.out.println("[] Server Status Code: " + statusCode);

        String respString = null;
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            respString = EntityUtils.toString(entity, "UTF-8");
        }
        EntityUtils.consume(entity);

        return JSON.parseObject(respString.trim()).toJSONString();

    }

    public static String post(List<NameValuePair> message,  String to)
            throws ParseException, IOException {
        HttpPost post = new HttpPost(to);
        //post.setHeader(new BasicHeader("Content-Type","multipart/form-data"));
        MultipartEntity reqEntity = new MultipartEntity();
        Charset charset = Charset.forName("UTF-8");//设置编码
        if (message != null) {
            for (NameValuePair nameValuePair : message) {
                StringBody comment = new StringBody(nameValuePair.getValue(), charset);
                reqEntity.addPart(nameValuePair.getName(), comment);
            }
        }
        post.setEntity(reqEntity);
        System.out.println(("[] Post \"" + message + "\" to " + post.getURI()));
        CloseableHttpClient client = HttpClients.createDefault();
        CloseableHttpResponse response = client.execute(post);
        int statusCode = response.getStatusLine().getStatusCode();
        System.out.println("[] Server Status Code: " + statusCode);

        String respString = null;
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            respString = EntityUtils.toString(entity, "UTF-8");
        }
        EntityUtils.consume(entity);

        return JSON.parseObject(respString.trim()).toJSONString();

    }
}