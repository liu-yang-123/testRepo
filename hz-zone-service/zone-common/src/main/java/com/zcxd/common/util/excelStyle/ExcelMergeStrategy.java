package com.zcxd.common.util.excelStyle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.write.merge.AbstractMergeStrategy;

public  class ExcelMergeStrategy extends AbstractMergeStrategy {

    //合并坐标集合
    private Map<ExcelPoint, MergeRect> addresses = new HashMap<>();
    private List<MergeRect> eachMerges=new ArrayList<>();

    /**
     * 创建左下角坐标点，在左下角执行合并时所有单元格都已创建完毕，否则样式不会生效
     * @param firstRow
     * @param lastRow
     * @param firstCol
     * @param lastCol
     * @param style
     */
    public void add(int firstRow, int lastRow, int firstCol, int lastCol, ExcelBorderStyle style) {
        ExcelRect address= new ExcelRect(firstRow,lastRow,firstCol,lastCol);
        addresses.put(new ExcelPoint(lastRow,firstCol),new MergeRect(address,style));
    }

    public void add(int firstRow, int lastRow, int firstCol, int lastCol) {
        ExcelRect address= new ExcelRect(firstRow,lastRow,firstCol,lastCol);
        addresses.put(new ExcelPoint(lastRow,firstCol),new MergeRect(address));
    }

    public void addEachMerge(int firstRow, int lastRow, int firstCol, int lastCol,int eachRow,int eachCol) {
        ExcelRect address= new ExcelRect(firstRow,lastRow,firstCol,lastCol);
        eachMerges.add(new MergeRect(address,eachRow,eachCol));
    }
    public void addEachMerge(int firstRow, int lastRow, int firstCol, int lastCol,int eachRow,int eachCol,ExcelBorderStyle style) {
        ExcelRect address= new ExcelRect(firstRow,lastRow,firstCol,lastCol);
        eachMerges.add(new MergeRect(address,style,eachRow,eachCol));
    }

    public boolean contain(ExcelPoint point){
        if(addresses.containsKey(point)){
            return true;
        }  else{
            return false;
        }
    }



    public MergeRect get(ExcelPoint point){
        return addresses.get(point);
    }

    @Override
    protected void merge(Sheet sheet, Cell cell, Head head, Integer relativeRowIndex) {
        //合并单元格
        int x=cell.getRowIndex();
        int y=cell.getColumnIndex();
        ExcelPoint point=new ExcelPoint(x,y);
        if (contain(point)) {

            MergeRect mergeRect= get(point);
            ExcelRect address=mergeRect.getAddress();
            CellRangeAddress cellRangeAddress =new CellRangeAddress(address.getX(),address.getY(),address.getZ(),address.getW());
            //检查一下,如何格子大小为1,就不需要合并了,免得报错了，在外面控制也麻烦
            if(!(address.getX()==address.getY()&&address.getZ()==address.getW())){
                //执行合并
                sheet.addMergedRegion(cellRangeAddress);
                //执行style
                ExcelBorderStyle style= mergeRect.getStyle();
                if(style!=null)
                    ExcelStyleUtil.buildBorderStyle(sheet,cellRangeAddress,style);
            }

        }else{
            //遍历
            for(MergeRect mRect:eachMerges){
                //判断该单元格是否处于本合并规则区域
                if(mRect.contain(cell.getRowIndex(),cell.getColumnIndex())){
                    //取出规则参数
                    int eachRow=mRect.getEachRow();//隔几行合并
                    int eachCol=mRect.getEachCol();//隔几列合并
                    ExcelRect address=mRect.getAddress();
                    //计算出该点是否在合并的点中
                    if((x-address.getX())%eachRow==0&&(y-address.getZ())%eachCol==0){
                        //创建合并单元，执行合并
                        CellRangeAddress cellRangeAddress= new CellRangeAddress(x,x+eachRow-1,y,y+eachCol-1);
                        if(!(x==x+eachRow-1&&y==y+eachCol-1)){
                            sheet.addMergedRegion(cellRangeAddress);
                            ExcelBorderStyle style= mRect.getStyle();
                            if(style!=null)
                                ExcelStyleUtil.buildBorderStyle(sheet,cellRangeAddress,style);
                        }
                    }
                }
            }

        }

    }
}