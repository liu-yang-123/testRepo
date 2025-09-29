package com.zcxd.base.controller.report;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import com.zcxd.base.dto.report.*;
import com.zcxd.base.handler.ExcelMergeHandler;
import com.zcxd.base.service.report.BankReportService;
import com.zcxd.base.util.ExcelUtil;
import com.zcxd.common.util.Result;
import com.zcxd.common.util.ResultList;
import com.zcxd.db.model.result.IdResult;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author songanwei
 * @date 2021-10-12
 */
@AllArgsConstructor
@RequestMapping("/bankReport")
@RestController
public class BankReportController {

    private BankReportService bankReportService;

    /**
     * 各银行加钞数量统计
     * @param departmentId 部门ID
     * @date 日期   2021-10
     * @return
     */
    @GetMapping("/task")
    public Result task(Long departmentId, String date){
        List<BankTaskReportDTO>  reportDTOList = bankReportService.getTask(departmentId, date);
        return Result.success(reportDTOList);
    }

    /**
     * 各银行加钞数量统计下载
     * @param departmentId 部门ID
     * @param date 日期  2021-10
     * @param response
     * @throws IOException
     */
    @GetMapping("/taskDownload")
    public void  taskDownload(Long departmentId, String date, HttpServletResponse response) throws IOException {
        List<BankTaskReportDTO>  reportDTOList = bankReportService.getTask(departmentId, date);
        //写入excel
        ClassPathResource resource = new ClassPathResource("doc/report/bank_atm_task_number.xlsx");
        InputStream templateFileStream = resource.getInputStream();
        //excel基础数据设置
        ExcelUtil excelUtil = new ExcelUtil();
        ExcelWriter excelWriter = excelUtil.getTemplateWriter("银行加钞数量统计",templateFileStream,response);
        //请求数据
        WriteSheet writeSheet = EasyExcel.writerSheet().build();
        FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.TRUE).build();
        if (reportDTOList != null) {
            excelWriter.fill(reportDTOList, fillConfig, writeSheet);
        }
        Map<String, String> map = new HashMap<>(1);
        map.put("date",date.replace("-","年") + "月");
        excelWriter.fill(map, writeSheet);
        //关闭流
        excelWriter.finish();
    }

    /**
     * 各银行加款金额统计
     * @param departmentId 部门ID
     * @param date 日期 2021-10
     * @return
     */
    @GetMapping("/amount")
    public Result amount(Long departmentId, String date){
        List<BankAmountReportDTO>  reportDTOList = bankReportService.getAmount(departmentId, date);
        return Result.success(reportDTOList);
    }

    /**
     * 各银行加款金额统计下载
     * @param departmentId 部门ID
     * @param date 日期 2021-10
     * @param response 响应对象
     * @throws IOException
     */
    @GetMapping("/amountDownload")
    public void amountDownload(Long departmentId, String date, HttpServletResponse response) throws IOException {
        List<BankAmountReportDTO>  reportDTOList = bankReportService.getAmount(departmentId, date);
        //写入excel
        ClassPathResource resource = new ClassPathResource("doc/report/bank_atm_cash_amount.xlsx");
        InputStream templateFileStream = resource.getInputStream();
        //excel基础数据设置
        ExcelUtil excelUtil = new ExcelUtil();
        ExcelWriter excelWriter = excelUtil.getTemplateWriter("银行加款金额统计",templateFileStream,response);
        //请求数据
        WriteSheet writeSheet = EasyExcel.writerSheet().registerWriteHandler(new ExcelMergeHandler()).build();
        FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.TRUE).build();
        if (reportDTOList != null){
            excelWriter.fill(reportDTOList, fillConfig, writeSheet);
        }
        Map<String, String> map = new HashMap<>(1);
        map.put("date",date.replace("-","年") + "月");
        excelWriter.fill(map, writeSheet);
        //关闭流
        excelWriter.finish();
    }

    /**
     * 各银行库存金额统计
     * @param departmentId 部门ID
     * @param date 查询日期  2021-10
     */
    @GetMapping("/stock")
    public Result stock(Long departmentId, String date){
       List<BankStockReportDTO> reportDTOList = bankReportService.getStock(departmentId, date);
       return Result.success(reportDTOList);
    }

    /**
     * 各银行库存金额统计下载
     * @param departmentId 部门ID
     * @param date 查询日期 2021-10
     * @param response
     * @throws IOException
     */
    @GetMapping("/stockDownload")
    public void stockDownload(Long departmentId, String date, HttpServletResponse response) throws IOException {
        List<BankStockReportDTO>  reportDTOList = bankReportService.getStock(departmentId, date);
        //写入excel
        ClassPathResource resource = new ClassPathResource("doc/report/bank_stock.xlsx");
        InputStream templateFileStream = resource.getInputStream();
        //excel基础数据设置
        ExcelUtil excelUtil = new ExcelUtil();
        ExcelWriter excelWriter = excelUtil.getTemplateWriter("银行库存金额统计",templateFileStream,response);
        //请求数据
        WriteSheet writeSheet = EasyExcel.writerSheet().registerWriteHandler(new ExcelMergeHandler()).build();
        FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.TRUE).build();
        if (reportDTOList != null){
            excelWriter.fill(reportDTOList, fillConfig, writeSheet);
        }
        Map<String, String> map = new HashMap<>(1);
        map.put("date",date.replace("-","年") + "月");
        excelWriter.fill(map, writeSheet);
        //关闭流
        excelWriter.finish();
    }

    /**
     * 加钞工作量统计
     * @param departmentId 部门ID
     * @param date 日期  2021-10
     * @param page 当前页
     * @param limit 页显示数量
     * @return
     */
    @GetMapping("/workload")
    public Result workload(Long departmentId, String date,Long empId,Integer page, Integer limit){
        Integer index = 1;
        long total = 0;
        List<IdResult> idList = new ArrayList<>();
        if (empId != null && empId > 0){
            IdResult idResult = new IdResult();
            idResult.setId(empId);
            idList.add(idResult);
        } else {
            List<IdResult> idResultList = bankReportService.getWorkloadIdList(departmentId, date);
            total = idResultList.size();
            Integer offset = (page - 1) * limit;
            int to = (total > page * limit) ? (int) page * limit : (int) total;
            idList = idResultList.subList(offset, to);
            index = offset + 1;
        }
        List<BankWorkloadReportDTO> reportDTOList = bankReportService.getWorkloadList(departmentId,date,idList, index);
        ResultList resultList = ResultList.builder().total(total).list(reportDTOList).build();
        return Result.success(resultList);
    }

    /**
     *
     * @param departmentId 部门ID
     * @param date 日期 2021-10
     * @throws IOException
     */
    @GetMapping("/workloadDownload")
    public void  workloadDownload(Long departmentId, String date,Long empId, HttpServletResponse response) throws IOException {
        //写入excel
        ClassPathResource resource = new ClassPathResource("doc/report/bank_atm_cash_workload.xlsx");
        InputStream templateFileStream = resource.getInputStream();
        //excel基础数据设置
        ExcelUtil excelUtil = new ExcelUtil();
        ExcelWriter excelWriter = excelUtil.getTemplateWriter("银行清机加款任务统计",templateFileStream,response);
        WriteSheet writeSheet = EasyExcel.writerSheet().registerWriteHandler(new ExcelMergeHandler()).build();
        FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.TRUE).build();

        if (empId != null && empId > 0){
            List<IdResult> idList = new ArrayList<>();
            IdResult idResult = new IdResult();
            idResult.setId(empId);
            idList.add(idResult);
            List<BankWorkloadReportDTO> reportDTOList = bankReportService.getWorkloadList(departmentId,date,idList, 1);
            excelWriter.fill(reportDTOList, fillConfig, writeSheet);
        } else{
            List<IdResult> idResultList = bankReportService.getWorkloadIdList(departmentId, date);
            long total = idResultList.size();
            //定义分页数量
            int pageSize = 100;
            int totalPage = (int) Math.ceil((double) total /pageSize);
            for (int i =1; i <= totalPage; i++){
                int offset = (i - 1) * pageSize;
                List<IdResult> idList = null;
                if (i == totalPage){
                    idList = idResultList.subList(offset, (int) (total - offset));
                } else{
                    idList = idResultList.subList(offset,pageSize);
                }
                Integer index = offset + 1;
                List<BankWorkloadReportDTO> reportDTOList = bankReportService.getWorkloadList(departmentId,date,idList, index);
                excelWriter.fill(reportDTOList, fillConfig, writeSheet);
            }
        }
        Map<String, String> map = new HashMap<>(1);
        map.put("date",date.replace("-","年") + "月");
        excelWriter.fill(map, writeSheet);
        //关闭流
        excelWriter.finish();
    }

    /**
     * 领缴款月度统计
     * @param departmentId 部门ID
     * @param date 日期 2021-10
     * @return
     */
    @GetMapping("/receivePayment")
    public Result receivePayment(Long departmentId, String date){
        List<BankReceivePaymentReportDTO> reportDTOList = bankReportService.getReceivePayment(departmentId, date);
        return Result.success(reportDTOList);
    }

    /**
     * 领缴款月度统计下载
     * @param departmentId 部门ID
     * @param date 日期 2021-10
     * @param response
     * @throws IOException
     */
    @GetMapping("/receivePaymentDownload")
    public void  receivePaymentDownload(Long departmentId, String date, HttpServletResponse response) throws IOException {
        //写入excel
        ClassPathResource resource = new ClassPathResource("doc/report/bank_receive_payment.xlsx");
        InputStream templateFileStream = resource.getInputStream();
        //excel基础数据设置
        ExcelUtil excelUtil = new ExcelUtil();
        ExcelWriter excelWriter = excelUtil.getTemplateWriter("银行领缴款金额统计",templateFileStream,response);
        WriteSheet writeSheet = EasyExcel.writerSheet().registerWriteHandler(new ExcelMergeHandler()).build();
        FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.TRUE).build();

        List<BankReceivePaymentReportDTO> reportDTOList = bankReportService.getReceivePayment(departmentId,date);
        //填充数据
        excelWriter.fill(reportDTOList, fillConfig, writeSheet);

        Map<String, String> map = new HashMap<>(1);
        map.put("date",date.replace("-","年") + "月");
        excelWriter.fill(map, writeSheet);
        //关闭流
        excelWriter.finish();
    }

    @GetMapping("/device")
    public Result device(Long departmentId, String date,String terNo,Integer page, Integer limit){
        Long total = bankReportService.getDeviceTotal(departmentId,terNo);
        List<BankDeviceCashReportDTO> reportDTOList = bankReportService.getDeviceList(departmentId, date, page, limit,terNo);
        ResultList resultList = ResultList.builder().total(total).list(reportDTOList).build();
        return Result.success(resultList);
    }

    @GetMapping("/deviceDownload")
    public void deviceDownload(Long departmentId, String date,String terNo, HttpServletResponse response) throws IOException {
        //写入excel
        ClassPathResource resource = new ClassPathResource("doc/report/bank_device.xlsx");
        InputStream templateFileStream = resource.getInputStream();
        //excel基础数据设置
        ExcelUtil excelUtil = new ExcelUtil();
        ExcelWriter excelWriter = excelUtil.getTemplateWriter("银行设备加钞统计",templateFileStream,response);
        WriteSheet writeSheet = EasyExcel.writerSheet().build();
        FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.TRUE).build();

        long total = bankReportService.getDeviceTotal(departmentId,terNo);
        //定义分页数量
        int pageSize = 100;
        int totalPage = (int) Math.ceil((double) total /pageSize);
        for (int i =1; i <= totalPage; i++){
            List<BankDeviceCashReportDTO> reportDTOList = bankReportService.getDeviceList(departmentId,date,i, pageSize,terNo);
            //填充数据
            if (reportDTOList != null){
                excelWriter.fill(reportDTOList, fillConfig, writeSheet);
            }
        }
        Map<String, String> map = new HashMap<>(1);
        map.put("date",date.replace("-","年") + "月");
        excelWriter.fill(map, writeSheet);
        //关闭流
        excelWriter.finish();
    }

    /**
     * 吞没卡报表数据查询
     * @param departmentId
     * @param date
     * @param terNo ATM设备编号
     * @param page
     * @param limit
     * @return
     */
    @GetMapping("/card")
    public Result card(Long departmentId, String date, String terNo, Integer page, Integer limit){
        Long total = bankReportService.getCardTotal(departmentId, date, terNo);
        List<BankCardReportDTO> reportDTOList = bankReportService.getCardList(departmentId, date, page, limit, terNo);
        ResultList resultList = ResultList.builder().total(total).list(reportDTOList).build();
        return Result.success(resultList);
    }

    /**
     * 吞没卡报表下载
     * @param departmentId
     * @param date
     * @param response
     * @throws IOException
     */
    @GetMapping("/cardDownload")
    public void cardDownload(Long departmentId, String date,String terNo, HttpServletResponse response) throws IOException {
        //写入excel
        ClassPathResource resource = new ClassPathResource("doc/report/bank_card.xlsx");
        InputStream templateFileStream = resource.getInputStream();
        //excel基础数据设置
        ExcelUtil excelUtil = new ExcelUtil();
        ExcelWriter excelWriter = excelUtil.getTemplateWriter("银行吞没卡汇总统计",templateFileStream,response);
        WriteSheet writeSheet = EasyExcel.writerSheet().build();
        FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.TRUE).build();

        long total = bankReportService.getCardTotal(departmentId,date,terNo);
        //定义分页数量
        int pageSize = 100;
        int totalPage = (int) Math.ceil((double) total /pageSize);
        for (int i =1; i <= totalPage; i++){
            List<BankCardReportDTO> reportDTOList = bankReportService.getCardList(departmentId,date,i, pageSize, terNo);
            excelWriter.fill(reportDTOList, fillConfig, writeSheet);
        }

        Map<String, String> map = new HashMap<>(1);
        map.put("date",date.replace("-","年") + "月");
        excelWriter.fill(map, writeSheet);
        //关闭流
        excelWriter.finish();
    }


}
