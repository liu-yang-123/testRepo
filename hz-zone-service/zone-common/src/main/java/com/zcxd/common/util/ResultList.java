package com.zcxd.common.util;

import lombok.Data;

import java.util.List;

/**
 * @author songanwei
 * @date 2021/4/20 14:43
 */
@Data
public class ResultList<T> {

    /**
     * 总数
     */
    private long total;

    /**
     * 列表数据
     */
    private List<T> list;

    /**
     * builder对象赋值数据
     * @param builder
     */
    public ResultList(Builder builder) {
       this.total = builder.total;
       this.list =  builder.list;
    }

    /**
     * 获取builder实例对象
     * @return
     */
    public static Builder builder(){
        return new Builder();
    }

    /**
     * Builder构造器
     */
    public static class Builder<T> {

        /**
         * 总数
         */
        private long total;

        /**
         * 列表数据
         */
        private List<T> list;

        public Builder total(long total) {
            this.total = total;
            return this;
        }

        public Builder list(List<T> list) {
            this.list = list;
            return this;
        }

        public ResultList build() {
            return new ResultList(this);
        }
    }

}
