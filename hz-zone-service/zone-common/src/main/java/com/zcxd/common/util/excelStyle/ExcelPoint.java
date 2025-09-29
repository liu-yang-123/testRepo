package com.zcxd.common.util.excelStyle;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExcelPoint extends Point {
    private int x; //row
    private int y; //col

    public boolean contain(int x, int y) {
        if(this.x==x&&this.y==y){
            return true;
        }
        return false;
    }
}