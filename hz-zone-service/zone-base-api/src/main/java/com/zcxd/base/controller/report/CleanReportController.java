package com.zcxd.base.controller.report;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import com.zcxd.base.dto.report.CleanAmountReportDTO;
import com.zcxd.base.dto.report.CleanWorkloadReportDTO;
import com.zcxd.base.handler.ExcelMergeHandler;
import com.zcxd.base.service.report.CleanReportService;
import com.zcxd.base.util.ExcelUtil;
import com.zcxd.common.util.Result;
import com.zcxd.common.util.ResultList;
import com.zcxd.db.model.result.IdResult;

import lombok.AllArgsConstructor;

/**
 * @author songanwei
 * @date 2021-10-12
 */
@AllArgsConstructor
@RequestMapping("/cleanReport")
@RestController
public class CleanReportController {

    private CleanReportService cleanReportService;

    /**
     * 时间范围查询清分统计金额
     * @param departmentId 部门ID
     * @param date 日期 2021-10
     * @return
     */
    @GetMapping("/amount")
    public Result amount(Long departmentId, String date){
        List<CleanAmountReportDTO> reportDTOList = cleanReportService.getAmount(departmentId, date);
        return Result.success(reportDTOList);
    }

    /**
     *
     * @param departmentId 部门ID
     * @param date 日期 2021-10
     * @throws IOException
     */
    @GetMapping("/amountDownload")
    public void  amountDownload(Long departmentId, String date, HttpServletResponse response) throws IOException {
        List<CleanAmountReportDTO>  reportDTOList = cleanReportService.getAmount(departmentId, date);
        CleanAmountReportDTO totle = new CleanAmountReportDTO();
        totle.setBankName("合计");
        totle.setCleanCount(reportDTOList.stream().collect(Collectors.summingLong(CleanAmountReportDTO::getCleanCount)));
        totle.setCleanAmount(reportDTOList.stream().map(CleanAmountReportDTO::getCleanAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
        totle.setMoreNumber(reportDTOList.stream().collect(Collectors.summingLong(CleanAmountReportDTO::getMoreNumber)));
        totle.setMoreAmount(reportDTOList.stream().map(CleanAmountReportDTO::getMoreAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
        totle.setLessNumber(reportDTOList.stream().collect(Collectors.summingLong(CleanAmountReportDTO::getLessNumber)));
        totle.setLessAmount(reportDTOList.stream().map(CleanAmountReportDTO::getLessAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
        totle.setFalseNumber(reportDTOList.stream().collect(Collectors.summingLong(CleanAmountReportDTO::getFalseNumber)));
        totle.setFalseAmount(reportDTOList.stream().map(CleanAmountReportDTO::getFalseAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
        totle.setBringNumber(reportDTOList.stream().collect(Collectors.summingLong(CleanAmountReportDTO::getBringNumber)));
        totle.setBringAmount(reportDTOList.stream().map(CleanAmountReportDTO::getBringAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
        totle.setMissNumber(reportDTOList.stream().collect(Collectors.summingLong(CleanAmountReportDTO::getMissNumber)));
        totle.setMissAmount(reportDTOList.stream().map(CleanAmountReportDTO::getMissAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
        reportDTOList.add(totle);
        
        //写入excel
        ClassPathResource resource = new ClassPathResource("doc/report/bank_atm_clean_amount.xlsx");
        InputStream templateFileStream = resource.getInputStream();
        //excel基础数据设置
        ExcelUtil excelUtil = new ExcelUtil();
        ExcelWriter excelWriter = excelUtil.getTemplateWriter("银行清分金额统计",templateFileStream,response);
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
     *
     * @param departmentId 部门ID
     * @param date 日期  2021-10
     * @param empId 员工ID
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
        } else{
            List<IdResult> idResultList = cleanReportService.getWorkloadIdList(departmentId, date);
            total = idResultList.size();
            Integer offset = (page - 1) * limit;
            int to = (total > page * limit ) ? (int)page * limit : (int)total;
            idList = idResultList.subList(offset,to);
            index = offset + 1;
        }
        List<CleanWorkloadReportDTO> reportDTOList = cleanReportService.getWorkloadList(departmentId,date,idList,index);
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
        ClassPathResource resource = new ClassPathResource("doc/report/bank_atm_clean_workload.xlsx");
        InputStream templateFileStream = resource.getInputStream();
        //excel基础数据设置
        ExcelUtil excelUtil = new ExcelUtil();
        ExcelWriter excelWriter = excelUtil.getTemplateWriter("银行清分金额统计",templateFileStream,response);
        WriteSheet writeSheet = EasyExcel.writerSheet().registerWriteHandler(new ExcelMergeHandler()).build();
        FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.TRUE).build();
        if (empId != null && empId > 0){
            List<IdResult> idList = new ArrayList<>();
            IdResult idResult = new IdResult();
            idResult.setId(empId);
            idList.add(idResult);
            List<CleanWorkloadReportDTO> reportDTOList = cleanReportService.getWorkloadList(departmentId,date,idList,1);
            excelWriter.fill(reportDTOList, fillConfig, writeSheet);
        } else{
            List<IdResult> idResultList = cleanReportService.getWorkloadIdList(departmentId, date);
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
                List<CleanWorkloadReportDTO> reportDTOList = cleanReportService.getWorkloadList(departmentId,date,idList,index);
                excelWriter.fill(reportDTOList, fillConfig, writeSheet);
            }
        }

        Map<String, String> map = new HashMap<>(1);
        map.put("date",date.replace("-","年") + "月");
        excelWriter.fill(map, writeSheet);
        //关闭流
        excelWriter.finish();
    }

}
