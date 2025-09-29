package com.zcxd.base.controller.report;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import com.zcxd.base.dto.report.BankCardReportDTO;
import com.zcxd.base.dto.report.RouteTimeReportDTO;
import com.zcxd.base.service.report.RouteReportService;
import com.zcxd.base.util.ExcelUtil;
import com.zcxd.common.util.Result;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author songanwei
 * @date 2021-10-12
 */
@AllArgsConstructor
@RequestMapping("/routeReport")
@RestController
public class RouteReportController {

    private RouteReportService routeReportService;

    @GetMapping("/workload")
    public Result workload(Long departmentId, String date){
        List<RouteTimeReportDTO> reportDTOList = routeReportService.getWorkload(departmentId, date);
        return Result.success(reportDTOList);
    }

    @GetMapping("/workloadDownload")
    public void workloadDownload(Long departmentId, String date, HttpServletResponse response) throws IOException {
        //写入excel
        ClassPathResource resource = new ClassPathResource("doc/report/route_workload.xlsx");
        InputStream templateFileStream = resource.getInputStream();
        //excel基础数据设置
        ExcelUtil excelUtil = new ExcelUtil();
        ExcelWriter excelWriter = excelUtil.getTemplateWriter("线路出车工作时长汇总统计",templateFileStream,response);
        WriteSheet writeSheet = EasyExcel.writerSheet().build();
        FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.TRUE).build();
        List<RouteTimeReportDTO> reportDTOList = routeReportService.getWorkload(departmentId,date);
        excelWriter.fill(reportDTOList, fillConfig, writeSheet);

        Map<String, String> map = new HashMap<>(1);
        map.put("date",date.replace("-","年") + "月");
        excelWriter.fill(map, writeSheet);
        //关闭流
        excelWriter.finish();
    }
}
