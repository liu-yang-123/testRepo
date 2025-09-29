package com.zcxd.common.util.excelStyle;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExcelRect extends Point {
    private int x;//row1
    private int y;//row2
    private int z;//col1
    private int w;//col2

    @Override
    public boolean contain(int x, int y) {//row,col
        if(this.x<=x&&this.y>=x && this.z<=y&&this.w>=y){
            return true;
        }
        return false;
    }
}