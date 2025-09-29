<template>
  <div>
    <!-- tableList:表头数据 expand 展开数据 label:第一排序号 formatter:过滤 dictionary:查询字典 -->
    <el-table
      ref="myTable"
      v-loading="listLoading"
      :element-loading-text="isDownload ? '正在下载中。。。' : '正在查询中。。。'"
      :data="dataList"
      :header-cell-style="{ 'background-color': '#f5f5f5' }"
      class="table-fiexd"
      highlight-current-row
      border
      fit
      :max-height="height"
      size="small"
      :span-method="spanMethod"
      :row-class-name="rowClassName"
      :show-summary="isSummary"
      :summary-method="isSummary ? getSummaries : undefined"
      @row-click="clickRow"
      @selection-change="handleSelectionChange"
      @select="selectLength ? handleSelect($event) : undefined"
      @select-all="selectLength ? handleSelectAll($event) : undefined"
    >
      <el-table-column
        v-if="selection"
        type="selection"
        :selectable="typeof selection === 'boolean' ? undefined : selectable"
        width="55"
      />
      <el-table-column v-if="expandList" type="expand">
        <template slot-scope="scope">
          <el-form label-position="right" inline class="demo-table-expand">
            <el-form-item
              v-for="(item, index) in expandList"
              :key="index"
              :label="item.label"
              :style="{ width: item.width || '50%' }"
            >
              <span
                v-if="item.formatter"
                v-html="item.formatter(scope.row[item.prop], scope.row)"
              />
              <span v-else>{{ scope.row[item.prop] }}</span>
            </el-form-item>
          </el-form>
        </template>
      </el-table-column>
      <el-table-column v-if="needIndex" align="center" label="序号" width="50">
        <template slot-scope="scope">
          {{ scope.$index + 1 }}
        </template>
      </el-table-column>
      <el-table-column
        v-for="(item, index) in tableList"
        :key="index"
        align="center"
        :label="item.label"
        :prop="item.prop"
        :width="item.width"
      >
        <template slot-scope="scope">
          <span
            v-if="item.formatter"
            v-html="item.formatter(scope.row[item.prop], scope.row)"
          />
          <dictionary
            v-else-if="item.dictionary"
            ref="dic"
            :dic="dictionary[item.dictionary]"
            :code="scope.row[item.prop]"
          />
          <span v-else>{{ scope.row[item.prop] }}</span>
        </template>
      </el-table-column>
      <slot name="operate" />
    </el-table>
  </div>
</template>

<script>
import { dictionaryData, reqDictionary } from '@/api/system/dictionary'
import Dictionary from '@/components/Dictionary'

export default {
  components: { Dictionary },
  props: {
    dataList: Array,
    tableList: Array,
    // 多选
    selection: {
      type: [Boolean, Object],
      required: false,
      default: false
    },
    // 多选限制条数
    selectLength: {
      type: Number,
      required: false,
      default: 0
    },
    // 表头展开列表
    expandList: {
      type: Array,
      required: false,
      default: null
    },
    listLoading: {
      type: Boolean,
      required: false,
      default: false
    },
    // 首页序号
    needIndex: {
      type: Boolean,
      required: false,
      default: false
    },
    // 是否展开
    isExpand: {
      type: Boolean,
      required: false,
      default: false
    },
    height: {
      type: [Number, String],
      default: document.documentElement.clientHeight - 270,
      required: false
    },
    spanMethod: {
      type: Function,
      default: null,
      required: false
    },
    rowClassName: {
      type: Function,
      default: null,
      required: false
    },
    isSummary: {
      type: Boolean,
      required: false,
      default: false
    },
    isDownload: {
      type: Boolean,
      required: false,
      default: false
    }
  },
  data() {
    return {
      dictionary: {}
    }
  },
  mounted() {
    this.getDictionary()
    window.onresize = () => {
      this.height = document.documentElement.clientHeight - 270
    }
  },
  methods: {
    getDictionary() {
      for (const item of this.tableList) {
        if (item.dictionary) {
          if (dictionaryData[item.dictionary]) {
            this.dictionary[item.dictionary] = dictionaryData[item.dictionary]
          } else {
            reqDictionary(item.dictionary).then((res) => {
              this.dictionary[item.dictionary] = res.data
            })
          }
        }
      }
    },
    clickRow(row) {
      this.$emit('rowClick', row)
    },
    setCurrent() {
      this.$refs.myTable.setCurrentRow(this.dataList[0])
      this.clickRow(this.dataList[0])
    },
    handleSelectionChange(val) {
      this.$emit('selectionChange', val)
    },
    getSummaries(param) {
      let data
      this.$emit('getSummaries', param, val => { data = val })
      return data
    },
    handleSelect(selection) {
      if (selection.length > this.selectLength) {
        this.$refs.myTable.toggleRowSelection(selection.shift(), false)
      }
    },
    handleSelectAll(selection) {
      if (selection.length > this.selectLength) {
        selection.length = this.selectLength
      }
    },
    selectable(row) {
      if (row[this.selection.name] === this.selection.optional) {
        return true
      } else {
        return false
      }
    }
  }
}
</script>

<style>
.demo-table-expand {
  font-size: 0;
}
.demo-table-expand label {
  width: 120px;
  color: #99a9bf;
  display: flex;
  flex-wrap: wrap;
}
.demo-table-expand .el-form-item {
  margin-right: 0;
  margin-bottom: 0;
  width: 50%;
}

.demo-table-expand .el-form-item div {
  padding-left: 40px;
}

.table-fiexd {
  font-size: 14px;
}

.table-fiexd .el-table__fixed-right {
  height: 100% !important;
}

.el-table .warning-row {
  background: oldlace;
}

.el-table__body tr.current-row > td {
  background-color: rgba(80, 115, 148, 0.336) !important;
  /* color: #f19944; */
}
</style>
