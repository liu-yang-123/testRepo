package com.zcxd.common.util.excelStyle;

import lombok.Data;

@Data
public abstract  class Point{
    public abstract boolean contain(int x,int y);
}