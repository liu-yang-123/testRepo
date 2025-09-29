package com.zcxd.common.util.excelStyle;

import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFFooter;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFSheet;

import com.alibaba.excel.write.handler.AbstractSheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;

public  class ExcelSheetStrategy extends AbstractSheetWriteHandler {
    //页脚信息
    private HashMap<String,String> footInfo =new HashMap<>();
    //列宽
    HashMap<Integer,Integer> columnWidth=new HashMap<>();

    public void appendFootInfo(String key,String info){
        footInfo.put(key,info);
    }

    public void appendColumnWidth(String key,Integer value) {
        //转换一下
            if(key.contains("-")){
                String[] columns=key.split("-");
                int min=Integer.valueOf(columns[0])-1;
                int max=Integer.valueOf(columns[1]);
                for(int i=min;i<max;i++){
                    columnWidth.put(i,value);
                }
            }else{
                int c= Integer.valueOf(key)-1;
                columnWidth.put(c,value);
            }
    }

    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {

        Sheet sheet= writeSheetHolder.getSheet();
        //设置列宽
        for (Map.Entry<Integer, Integer> entry : columnWidth.entrySet()) {
            sheet.setColumnWidth(entry.getKey(),entry.getValue());
        }
        PrintSetup printSetup = sheet.getPrintSetup();
        //设置打印方向是否为横向
        printSetup.setLandscape(true); // 打印方向，true：横向，false：纵向
        //设置打印纸张大小
        printSetup.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
        //设置页眉页脚的边距
        printSetup.setHeaderMargin(0.196f);//英寸<->0.5厘米
        printSetup.setFooterMargin(0.275f);//英寸<->0.5厘米
        //设置打印缩放为100%
        printSetup.setScale((short) 100);
        //设置页边距
        sheet.setMargin(SXSSFSheet.TopMargin,0.7874f);//英寸<->0.9厘米
        sheet.setMargin(SXSSFSheet.BottomMargin,0.5905f);//英寸<->0.9厘米
        sheet.setMargin(SXSSFSheet.RightMargin,0.3543f);//英寸<->0.8厘米
        sheet.setMargin(SXSSFSheet.LeftMargin,0.3543f);//英寸<->0.8厘米
        //设置整体水平居中
        sheet.setHorizontallyCenter(true);
        //设置页眉
      /*  Header header = sheet.getHeader();
        //给页眉赋值及设置字体样式
        header.setCenter(HSSFHeader.font("宋体","")+HSSFHeader.fontSize((short) 18)+"这是页眉");*/

        //设置页脚
        Footer footer = sheet.getFooter();
        String footerInfo="第"+ HSSFFooter.page()+"页, 共"+HSSFFooter.numPages()+"页";
        if(footInfo.size()>=1){
            for (Map.Entry<String, String> entry : footInfo.entrySet()) {
                footerInfo+=" "+entry.getKey()+":"+entry.getValue();
            }
        }
        footer.setCenter(HSSFFooter.font("宋体","")+HSSFFooter.fontSize((short) 12)+footerInfo);
    }
}
