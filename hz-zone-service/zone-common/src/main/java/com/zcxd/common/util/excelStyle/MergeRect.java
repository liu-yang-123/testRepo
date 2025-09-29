package com.zcxd.common.util.excelStyle;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class  MergeRect{
    private ExcelRect address;
    private ExcelBorderStyle style;
    private int eachRow ;
    private int eachCol ;

    public MergeRect(ExcelRect address) {
        this.address = address;
    }

    public MergeRect(ExcelRect address, ExcelBorderStyle style) {
        this.address = address;
        this.style = style;
    }

    public MergeRect(ExcelRect address, int eachRow, int eachCol) {
        this.address = address;
        this.eachRow = eachRow;
        this.eachCol = eachCol;
    }

    public boolean contain(int x, int y) {
        if(address.getX()<=x&&address.getY()>=x && address.getZ()<=y&&address.getW()>=y){
            return true;
        }
        return false;
    }
}
