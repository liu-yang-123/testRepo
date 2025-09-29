package com.zcxd.common.util.excelStyle;

import com.alibaba.excel.write.metadata.style.WriteCellStyle;

import lombok.Data;

@Data
public class  ExcelStyle{
    private Point point;
    private WriteCellStyle style;


    public boolean contain(int x,int y){
        return point.contain(x,y);
    }

    public ExcelStyle(WriteCellStyle style) {
        this.style = style;
    }

    public ExcelStyle(int x, int y,int z,int w, WriteCellStyle style) {
        this.point = new ExcelRect(x,  y, z, w);
        this.style = style;
    }

    public ExcelStyle(int x, int y, WriteCellStyle style) {
        this.point = new ExcelPoint(x,  y);
        this.style = style;
    }
    public ExcelStyle(Point point, WriteCellStyle style) {
        this.point = point;
        this.style = style;
    }

}