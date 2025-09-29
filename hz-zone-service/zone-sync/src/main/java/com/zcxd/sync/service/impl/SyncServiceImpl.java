package com.zcxd.sync.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zcxd.common.util.CharUtil;
import com.zcxd.common.util.CreateFileUtil;
import com.zcxd.common.util.DateTimeUtil;
import com.zcxd.sync.mapper.TemplateMapper;
import com.zcxd.sync.mapper.UploadLogMapper;
import com.zcxd.sync.model.TableMap;
import com.zcxd.sync.model.UploadLog;
import com.zcxd.sync.service.SyncService;
import com.zcxd.sync.util.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.ibatis.type.DateTypeHandler;
import org.apache.poi.ss.formula.functions.Count;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author songanwei
 * @date 2021/4/15 17:55
 */
@Slf4j
@Service("sync")
public class SyncServiceImpl implements SyncService {

    @Resource
    private TemplateMapper templateMapper;
    @Value("${upload.protect.pwd}")
    private String PASSWORD;
    @Value("${upload.company}")
    private String company;
    @Value("${upload.host-url}")
    private String URL;
    @Value("${upload.host-no-file-url}")
    private String nullFileUrl;
    @Value("${upload.dir.win.zip}")
    private String WINDOWS_PATH_ZIP;
    @Value("${upload.dir.linux.zip}")
    private String LINUX_PATH_ZIP;
    @Value("${upload.dir.win.tmp}")
    private String WINDOWS_PATH_TEMP;
    @Value("${upload.dir.linux.tmp}")
    private String LINUX_PATH_TEMP;
    @Value("${os.name}")
    private String os;
    @Value("${upload.protect.enable}")
    private int protectEnable;
    @Resource
    private UploadLogMapper uploadLogMapper;
    public static final int UPLOAD_SUCCESS = 1;
    public static final int UPLOAD_FAILED = 0;
    @Override
    public void upload(){
        String path = "/table.json";
        boolean saveLog=true;
        String date= DateTimeUtil.dateStr(new Date(),"yyyy-MM-dd");
        InputStream inputStream = getClass().getResourceAsStream(path);
        if (inputStream == null) {
            throw new RuntimeException("读取文件失败");
        }
        //读取文件json数据转化为数组对象
        String jsonString = new BufferedReader(new InputStreamReader(inputStream))
                .lines().collect(Collectors.joining(System.lineSeparator()));
        List<TableMap> tableMapList = JSON.parseArray(jsonString, TableMap.class);

        //并行流方式操作
        tableMapList.parallelStream().forEach(item -> {
            System.out.println(item);
            //记录开始执行时间
            long start = System.currentTimeMillis();
            List<Map>  mapList = templateMapper.select(item.getTableName(), item.getType(), item.getField());
            JSONArray jsonArray=new JSONArray();
            //数据JSON化
            String fieldString = JSON.toJSONString(mapList);
            jsonArray.add(fieldString);
            boolean isNull=mapList.size()>0?true:false;
            if(isNull){
                uploadData(jsonArray,fieldString,date,item.getFileType(),saveLog,mapList.size());
            }else{
                //上传空记录文件
                uploadData_OfNull(date,item.getFileType(),saveLog);
            }

//            //写入文本文件
//            //数据JSON化
//            String fieldString = JSON.toJSONString(mapList);
//            String fileName = item.getTableName()+".txt";
//            try(BufferedWriter out = new BufferedWriter(new FileWriter(fileName))){
//                out.write(fieldString);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            // 生成的压缩文件
//            ZipParameters zipParameters = new ZipParameters();
//            zipParameters.setEncryptFiles(true);
//            zipParameters.setEncryptionMethod(EncryptionMethod.AES);
//            // AES 256 is used by default.
//            zipParameters.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256);
//
//            //压缩包添加文件
//            List<File> filesToAdd = Arrays.asList(new File(fileName));
//            String zipName = item.getTableName() +".zip";
//            ZipFile zipFile = new ZipFile(zipName, PASSWORD.toCharArray());
//            try {
//                zipFile.addFiles(filesToAdd, zipParameters);
//            } catch (ZipException e) {
//                e.printStackTrace();
//            }finally {
//                //删除文件
//                filesToAdd.parallelStream().forEach(r -> r.delete());
//            }
//            //TODO 上传文件、并删除zip文件
            long end = System.currentTimeMillis();
            long between = end - start;
            log.info(item.getTableName()+"数据上传成功,执行时间："+between+"毫秒");
        });
    }

    /**
     * @Description:上传空记录文件
     * @Author: lilanglang
     * @Date: 2021/9/2 16:45
     * @param dataDate:
     * @param dataType:
     * @param saveLog:
     **/
    private int uploadData_OfNull(String dataDate,Integer dataType,boolean saveLog) {

        String uuid = CharUtil.getUUID();

        List<NameValuePair> paramList = new ArrayList<NameValuePair>();
        paramList.add(new BasicNameValuePair("count",""+0));
        paramList.add(new BasicNameValuePair("companyNo",company));
        paramList.add(new BasicNameValuePair("seqno",uuid));
        paramList.add(new BasicNameValuePair("fileType",dataType.toString()));
        paramList.add(new BasicNameValuePair("fileName","default"));
        paramList.add(new BasicNameValuePair("protect",""+protectEnable));
        try {
            String result = HttpUtils.post(paramList,  nullFileUrl);
            log.info("http result: " + result);
            if (checkHttpResult(result)) {
                if (saveLog) {
                    this.saveUploadLog(dataDate, "", dataType, 0, UPLOAD_SUCCESS, "", uuid);
                }
            } else {
                if (saveLog) {
                    this.saveUploadLog(dataDate, "", dataType, 0, UPLOAD_FAILED, "", uuid);
                }
            }
            return 1;
        }catch (IOException ex){
            log.error("同步异常",ex);
            //保存本地库
            if (saveLog) {
                this.saveUploadLog(dataDate, null, dataType,0, UPLOAD_FAILED, null, uuid);
            }
            return 0;
        }
    }

    /**
     * @Description:上传文件
     * @Author: lilanglang
     * @Date: 2021/9/2 10:55
     * @param jsonArray:
     * @param dataDate:
     * @param dataType:
     * @param saveLog:
     **/
    private int uploadData(JSONArray jsonArray,String fieldString,String dataDate,Integer dataType,boolean saveLog,Integer size) {
        String uuid = CharUtil.getUUID();
        String fileName = uuid + ".txt";
        String zipFileName = saveZipFile(jsonArray,uuid,dataDate);
        if (zipFileName == null) {
            return 0;
        }
        List<NameValuePair> paramList = new ArrayList<NameValuePair>();
        paramList.add(new BasicNameValuePair("count",""+size));
        paramList.add(new BasicNameValuePair("companyNo",company));
        paramList.add(new BasicNameValuePair("seqno",uuid));
        paramList.add(new BasicNameValuePair("fileType",dataType.toString()));
        paramList.add(new BasicNameValuePair("fileName",fileName));
        paramList.add(new BasicNameValuePair("protect",""+protectEnable));
        try {
            File file = new File(zipFileName);
            String result = HttpUtils.post(paramList, file, URL);
            log.info("http result: " + result);
            if (checkHttpResult(result)) {
                if (saveLog) {
                    this.saveUploadLog(dataDate, zipFileName, dataType, jsonArray.size(), UPLOAD_SUCCESS, fileName, uuid);
                    log.info(dataType+",记录数:"+ size+",上传成功");
                }
            } else {
                if (saveLog) {
                    this.saveUploadLog(dataDate, zipFileName, dataType, jsonArray.size(), UPLOAD_FAILED, fileName, uuid);
                    log.info(dataType+",记录数:"+ size+",上传失败");
                }
            }
            return 1;
        }catch (IOException ex){
            log.error("同步异常",ex);
            //保存本地库
            if (saveLog) {
                this.saveUploadLog(dataDate, zipFileName, dataType, jsonArray.size(), UPLOAD_FAILED, fileName, uuid);
            }
            return 0;
        }
    }

    public String saveZipFile(JSONArray jsonArray,String uuid,String dataDate){
        if(jsonArray == null || uuid == null){
            return null;
        }
        String fileName = tmpFileName(uuid + ".txt");
        try(BufferedWriter out = new BufferedWriter(new FileWriter(fileName))){
            out.write(jsonArray.toString());
        } catch (IOException e) {
                e.printStackTrace();
        }
        CreateFileUtil.createDir(new File(fileName).getParent());

        File file=new File(fileName);
        String zipFileName = zipFileName(uuid,dataDate);
        ZipParameters zipParameters = new ZipParameters();
        zipParameters.setEncryptFiles(true);
        zipParameters.setEncryptionMethod(EncryptionMethod.AES);
        // AES 256 is used by default.
        zipParameters.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256);

        try {
            CreateFileUtil.createDir(new File(zipFileName).getParent());
            ZipFile zipFile = new ZipFile(zipFileName, PASSWORD.toCharArray());
            zipFile.addFile(file, zipParameters);
        } catch (ZipException e) {
            e.printStackTrace();
        }finally {
            //删除文件
            file.delete();
        }
        return zipFileName;
    }

    /**
     * @Description:保存文件返回文件路径
     * @Author: lilanglang
     * @Date: 2021/9/2 10:47
     * @param jsonArray:
     * @param uuid:
     * @param dataDate:
     **/
    private String saveFile(JSONArray jsonArray, String uuid, String dataDate){
        if(jsonArray == null || uuid == null){
            return null;
        }
        String fileName = tmpFileName(uuid + ".txt");
        String zipFileName = zipFileName(uuid,dataDate);
        File file = new File(fileName);
        byte[] bytes=jsonArray.toString().getBytes();
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(bytes);
            CreateFileUtil.createDir(new File(zipFileName).getParent());
            ZipFile zipFile;
            if(protectEnable == 1) {
                zipFile = new ZipFile(zipFileName, PASSWORD.toLowerCase().toCharArray());
            } else {
                zipFile = new ZipFile(zipFileName);
            }
            zipFile.addFile(file);
            return zipFileName;
        }catch (IOException ex){
            log.error("写ZIP文件异常",ex);
            return null;
        }finally {
            if(fos!=null){
                try {
                    fos.close();
                } catch (IOException e) {
                    log.error("关闭输出流异常");
                }
            }
            CreateFileUtil.deleteFile(file);
        }
    }
    
    /**
     * @Description:txt文件保存路径
     * @Author: lilanglang
     * @Date: 2021/9/2 10:49
     * @param fileName: 
     **/
    private String tmpFileName(String fileName){
        if(os.toLowerCase().startsWith("win")){
            return WINDOWS_PATH_TEMP+fileName;
        }else {
            return LINUX_PATH_TEMP+fileName;
        }
    }
    
    /**
     * @Description:压缩文件保存路径
     * @Author: lilanglang
     * @Date: 2021/9/2 10:49
     * @param fileName: 
     * @param dateStr: 
     **/
    private String zipFileName(String fileName,String dateStr){
        String subDir = StringUtils.isEmpty(dateStr)? "" : (dateStr.replace("-","")+"/");
        if(os.toLowerCase().startsWith("win")){
            return WINDOWS_PATH_ZIP+subDir+fileName+".zip";
        }else {
            return LINUX_PATH_ZIP+subDir+fileName+".zip";
        }
    }

    private boolean checkHttpResult(String httpResult) {
        if (httpResult != null) {
            JSONObject jsonObject = JSON.parseObject(httpResult);
            String errno = jsonObject.getString("errno");
            if (errno != null && errno.compareToIgnoreCase("0") == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * @Description:保存上传日志
     * @Author: lilanglang
     * @Date: 2021/9/2 11:03
     * @param dataDate:
     * @param zipFileUrl:
     * @param dataType:
     * @param count:
     * @param status:
     * @param fileName:
     * @param uuid:
     **/
    private void saveUploadLog(String dataDate,
                               String zipFileUrl,
                               Integer dataType,
                               Integer count,
                               Integer status,
                               String fileName,
                               String uuid) {
        UploadLog uploadLog = new UploadLog();
        uploadLog.setDataType(dataType);
        uploadLog.setFileUrl(zipFileUrl);
        uploadLog.setDataDate(dataDate);
        uploadLog.setCount(count);
        uploadLog.setStatusT(status);
        uploadLog.setFileName(fileName);
        uploadLog.setUuid(uuid);
        uploadLog.setProtect(protectEnable);
        uploadLog.setUploadTime(System.currentTimeMillis());
        uploadLogMapper.insert(uploadLog);
    }
}
