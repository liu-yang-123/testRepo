package com.zcxd.common.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * @Description:在本地磁盘创建文件目录
 * @Author: lilanglang
 * @Date: 2021/9/2 10:53
 **/
public class CreateFileUtil {

    private static final Logger logger = LoggerFactory.getLogger(CreateFileUtil.class);

    public static boolean createFile(String destFileName) {
        File file = new File(destFileName);
        if (file.exists()) {
            logger.warn("创建单个文件" + destFileName + "失败，目标文件已存在！");
            return false;
        }
        if (destFileName.endsWith(File.separator)) {
            logger.warn("创建单个文件" + destFileName + "失败，目标文件不能为目录！");
            return false;
        }
        //判断目标文件所在的目录是否存在
        if (!file.getParentFile().exists()) {
            //如果目标文件所在的目录不存在，则创建父目录
            logger.warn("目标文件所在目录不存在，准备创建它！");
            if (!file.getParentFile().mkdirs()) {
                logger.warn("创建目标文件所在目录失败！");
                return false;
            }
        }
        //创建目标文件
        try {
            if (file.createNewFile()) {
                logger.warn("创建单个文件" + destFileName + "成功！");
                return true;
            } else {
                logger.warn("创建单个文件" + destFileName + "失败！");
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.warn("创建单个文件" + destFileName + "失败！" + e.getMessage());
            return false;
        }
    }


    public static boolean createDir(String destDirName) {
        File dir = new File(destDirName);
        if (dir.exists()) {
            logger.warn("创建目录" + destDirName + "失败，目标目录已经存在");
            return false;
        }
        if (!destDirName.endsWith(File.separator)) {
            destDirName = destDirName + File.separator;
        }
        //创建目录
        if (dir.mkdirs()) {
            logger.warn("创建目录" + destDirName + "成功！");
            return true;
        } else {
            logger.warn("创建目录" + destDirName + "失败！");
            return false;
        }
    }


    public static String createTempFile(String prefix, String suffix, String dirName) {
        File tempFile = null;
        if (dirName == null) {
            try {
                //在默认文件夹下创建临时文件
                tempFile = File.createTempFile(prefix, suffix);
                //返回临时文件的路径
                return tempFile.getCanonicalPath();
            } catch (IOException e) {
                e.printStackTrace();
                logger.warn("创建临时文件失败！" + e.getMessage());
                return null;
            }
        } else {
            File dir = new File(dirName);
            //如果临时文件所在目录不存在，首先创建
            if (!dir.exists()) {
                if (!CreateFileUtil.createDir(dirName)) {
                    logger.warn("创建临时文件失败，不能创建临时文件所在的目录！");
                    return null;
                }
            }
            try {
                //在指定目录下创建临时文件
                tempFile = File.createTempFile(prefix, suffix, dir);
                return tempFile.getCanonicalPath();
            } catch (IOException e) {
                e.printStackTrace();
                logger.warn("创建临时文件失败！" + e.getMessage());
                return null;
            }
        }
    }

    public static boolean deleteFile(File file) {
        if (file.exists()) {
            file.delete();
            return true;
        }
        return false;
    }

    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        String zipfile = "D://syncdata/20190101/test.txt";
        File file = new File(zipfile);
        System.out.println("parent = " + file.getParent());
        System.out.println("dd = " + "2019-09-09".replace("-",""));
        CreateFileUtil.createDir(file.getParent());
        CreateFileUtil.createFile(zipfile);


    }
}