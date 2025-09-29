package com.zcxd.gun.utils;

import io.minio.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author zccc
 */
@Service
public class MinioUtil {
    @Autowired
    private MinioClient minioClient;

    /**
     * 判断桶是否存在
     * @param bucketName 桶名称
     * @return 是否存在
     */
    public boolean bucketExists(String bucketName) {
        boolean flag = false;
        try {
            flag = minioClient.bucketExists(bucketName);

        } catch (Exception e){
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 判断文件是否存在
     * @param bucketName 桶名称
     * @param objectPath
     * @return
     */
    public boolean objectExists(String bucketName, String objectPath){
        try {
            minioClient.statObject(bucketName, objectPath);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断文件状态
     * @param bucketName
     * @param objectPath
     * @return
     */
    public ObjectStat statObject(String bucketName, String objectPath) {
        boolean flag = bucketExists(bucketName);
        if (flag) {
            try {
                ObjectStat statObject = minioClient.statObject(bucketName, objectPath);
                return statObject;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 给定文件路径上传
     * @param bucketName
     * @param localPath
     * @return
     */
    public boolean putObject(String bucketName, String minioObjectPath, String localPath) {
        boolean flag = bucketExists(bucketName);
        if (flag) {
            try {
                minioClient.putObject(bucketName, minioObjectPath, localPath, null);
                ObjectStat statObject = statObject(bucketName, minioObjectPath);
                if (statObject != null && statObject.length() > 0) {
                    return true;
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 下载文件
     * @param bucketName
     * @param objectPath
     * @return 输入流
     */
    public InputStream getObject(String bucketName, String objectPath) {
        boolean flag = bucketExists(bucketName);
        if (flag) {
            ObjectStat statObject = statObject(bucketName, objectPath);
            if (statObject != null && statObject.length() > 0) {
                try {
                    return minioClient.getObject(bucketName, objectPath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 下载并保存到指定位置
     * @param bucketName
     * @param objectPath
     * @param outputPath
     * @return
     */
    public boolean getObject(String bucketName, String objectPath, String outputPath) {
        boolean flag = bucketExists(bucketName);
        if (flag) {
            ObjectStat statObject = statObject(bucketName, objectPath);
            if (statObject != null) {
                try {
                    File outputFile = new File(outputPath);
                    if (!outputFile.getParentFile().exists()){
                        outputFile.getParentFile().mkdirs();
                    }
                    minioClient.getObject(bucketName, objectPath, outputPath);
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * 获取文件下载url
     * @param bucketName
     * @param objectPath
     * @return
     */
    public String getObjectUrl(String bucketName, String objectPath) {
        boolean flag = bucketExists(bucketName);
        String url = "";
        if (flag) {
            try {
                url = minioClient.getObjectUrl(bucketName, objectPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return url;
    }

    /**
     * 创建桶
     * @param bucketName
     * @return
     */
    public boolean makeBucket(String bucketName) {
        boolean flag = bucketExists(bucketName);
        if (!flag) {
            try {
                minioClient.makeBucket(bucketName);
                String bucketPolicy = String.format("{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetBucketLocation\"],\"Resource\":[\"arn:aws:s3:::%s\"]}" +
                        ",{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:ListBucket\"],\"Resource\":[\"arn:aws:s3:::%s\"],\"Condition\":{\"StringEquals\":{\"s3:prefix\":[\"*.*\"]}}}," +
                        "{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetObject\"],\"Resource\":[\"arn:aws:s3:::%s/*.**\"]}]}", bucketName, bucketName, bucketName);
                minioClient.setBucketPolicy(bucketName, bucketPolicy);
            } catch (Exception e){
                e.printStackTrace();
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * 通过流上传
     * @param in 流
     * @param objectPath minio地址
     * @param bucketName 桶名称
     * @param covered 是否可覆盖
     * @return 若失败，返回文件名称
     */
    public String uploadFileByStream(InputStream in, String objectPath, String bucketName, Boolean covered) throws Exception {
        // bucket创建
        makeBucket(bucketName);
        // 上传文件
        String result = null;
        if (objectExists(bucketName, objectPath) && !covered){
            result = objectPath.split("/")[objectPath.split("/").length - 1] + "文件已存在于仓库中";
        }else {
            minioClient.putObject(bucketName, objectPath, in, new PutObjectOptions(in.available(), -1));
        }
        try {
            in.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return result;
    }
    /**
     * 删除单个文件
     * @param bucketName
     * @param objectPath
     * @return
     */
    public boolean removeObject(String bucketName, String objectPath) {
        boolean flag = bucketExists(bucketName);
        if (flag) {
            try {
                minioClient.removeObject(bucketName, objectPath);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    public String getFilePathInMinioByLocalPath(String filePath){
        String path = new File(filePath).getName();
        if (path.contains("清单")) {
//            path = "清单/" + project + "/" + path;
            path = "清单/" + path;
        } else if (path.contains("修改项")) {
            path = "修改项/" + path;
        } else {
            path = "物料/" + path;
        }
        return path;
    }
}
