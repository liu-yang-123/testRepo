<template>
  <div class="app-container">
    <search-bar
      :list-query="listQuery"
      :search-list="searchList"
      :options="options"
      :role="role"
      @lookUp="getList"
    />
    <el-table
      ref="myTable"
      v-loading="listLoading"
      element-loading-text="正在查询中。。。"
      :data="list"
      :header-cell-style="{ 'background-color': '#f5f5f5' }"
      class="table-fiexd"
      highlight-current-row
      border
      fit
      show-summary
      :summary-method="getSummaries"
      :max-height="height"
      size="small"
    >
      <el-table-column prop="bankName" label="银行" align="center" />
      <el-table-column prop="cleanAmount" label="清分金额（元）" align="center">
        <template slot-scope="scope">
          <span>{{ formatMoney(scope.row.cleanAmount) }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="cleanCount" label="清分笔数" align="center" />
      <el-table-column label="差错" align="center">
        <el-table-column label="长款" align="center">
          <el-table-column prop="moreNumber" label="笔数" align="center" />
          <el-table-column prop="moreAmount" label="金额（元）" align="center">
            <template slot-scope="scope">
              <span>{{ formatMoney(scope.row.moreAmount) }}</span>
            </template>
          </el-table-column>
        </el-table-column>
        <el-table-column label="短款" align="center">
          <el-table-column prop="lessNumber" label="笔数" align="center" />
          <el-table-column prop="lessAmount" label="金额（元）" align="center">
            <template slot-scope="scope">
              <span>{{ formatMoney(scope.row.lessAmount) }}</span>
            </template>
          </el-table-column>
        </el-table-column>
        <el-table-column label="假疑币" align="center">
          <el-table-column prop="falseNumber" label="笔数" align="center" />
          <el-table-column prop="falseAmount" label="金额（元）" align="center">
            <template slot-scope="scope">
              <span>{{ formatMoney(scope.row.falseAmount) }}</span>
            </template>
          </el-table-column>
        </el-table-column>
        <el-table-column label="夹张" align="center">
          <el-table-column prop="bringNumber" label="笔数" align="center" />
          <el-table-column prop="bringAmount" label="金额（元）" align="center">
            <template slot-scope="scope">
              <span>{{ formatMoney(scope.row.bringAmount) }}</span>
            </template>
          </el-table-column>
        </el-table-column>
        <el-table-column label="残缺币" align="center">
          <el-table-column prop="missNumber" label="笔数" align="center" />
          <el-table-column prop="missAmount" label="金额（元）" align="center">
            <template slot-scope="scope">
              <span>{{ formatMoney(scope.row.missAmount) }}</span>
            </template>
          </el-table-column>
        </el-table-column>
      </el-table-column>
      <slot name="operate" />
    </el-table>

    <!-- 清机任务详情 -->
    <el-dialog
      title="详情"
      :visible.sync="detailFormVisible"
      :close-on-click-modal="false"
      width="80%"
    >
      <div>
        <my-table
          ref="myTable"
          :list-loading="deatilListLoading"
          :data-list="detailList"
          :table-list="deatilTableList"
          :height="'500'"
        />
      </div>
    </el-dialog>
  </div>
</template>

<script>
// import Pagination from '@/components/Pagination'
import myTable from '@/components/Table'
import { formatdate } from '@/utils/date'
import { formatMoney } from '@/utils/convert'
import searchBar from '@/components/SearchBar'
import { listAmount } from '@/api/statistic/cleanAmount'
import { authOption } from '@/api/common/selectOption'
import { dictionaryData } from '@/api/system/dictionary'
export default {
  components: { myTable, searchBar },
  data() {
    return {
      options: {
        routeType: [
          { code: 0, content: '固定线路' },
          { code: 1, content: '临时线路' }
        ],
        departmentId: []
      },
      height: document.documentElement.clientHeight - 270,
      list: [],
      listQuery: {
        date: this.formatMonth(new Date()),
        departmentId: null
      },
      listLoading: true,
      searchList: [
        { name: 'departmentId', label: '权限部门', type: 3, notClear: true },
        { name: 'date', type: 5, notClear: true }
      ],
      role: {
        list: '/base/cleanReport/amount',
        download: {
          url: '/base/cleanReport/amountDownload',
          title: '各银行清分月度统计'
        }
      },
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '创建'
      },
      // 详情
      detailFormVisible: false,
      deatilListLoading: false,
      detailList: [],
      deatilTableList: [
        {
          label: '所属银行',
          prop: 'headBank'
        },
        {
          label: '加款点',
          prop: 'bankName'
        },
        {
          label: '设备编号',
          prop: 'terNo'
        },
        {
          label: '任务配置',
          prop: 'taskType',
          formatter: this.formatTaskType
        },
        {
          label: '加钞金额（十万元）',
          prop: 'amount'
        },
        {
          label: '备注',
          prop: 'comments'
        },
        {
          label: '开始时间',
          prop: 'beginTime',
          formatter: this.formatDateTime,
          width: 120
        },
        {
          label: '结束时间',
          prop: 'endTime',
          formatter: this.formatDateTime,
          width: 120
        },
        {
          label: '任务状态',
          prop: 'statusT',
          formatter: this.formatCleanStatus
        }
      ]
    }
  },
  mounted() {
    this.getOptions().then(() => {
      this.getList()
    })
  },
  methods: {
    async getOptions() {
      await authOption().then((res) => {
        this.options.departmentId = res.data
        if (this.options.departmentId) {
          this.listQuery.departmentId = this.options.departmentId[0].id
        }
      })
    },
    getList(data) {
      if (data) {
        for (const key in data) {
          this.listQuery[key] = data[key]
        }
      }
      listAmount(this.listQuery).then((res) => {
        this.list = res.data
        this.listLoading = false
      })
    },
    formatMoney,
    formatDateTime(timestamp) {
      if (!timestamp || timestamp === 0) {
        return '-'
      }
      const date = new Date(timestamp)
      return formatdate(date, 'hh:mm:ss')
    },
    formatDate(timestamp) {
      if (!timestamp || timestamp === 0) {
        return '-'
      }
      const date = new Date(timestamp)
      return formatdate(date, 'yyyy-MM-dd')
    },
    formatMonth(timestamp) {
      if (!timestamp || timestamp === 0) {
        return '-'
      }
      const date = new Date(timestamp)
      return formatdate(date, 'yyyy-MM')
    },
    formatCleanStatus(type) {
      for (const item of dictionaryData['CLEAN_STATUS']) {
        if (type === item.code) {
          return item.content
        }
      }
    },
    formatTaskType(str) {
      switch (str) {
        case 1:
          return '维修'
        case 2:
          return '加钞'
        case 3:
          return '清机'
        case 4:
          return '巡检'
      }
    },
    spanMethod({ row, column, rowIndex, columnIndex }) {
      if (columnIndex === 0) {
        if (rowIndex % 2 === 0) {
          return [2, 1]
        } else {
          return [0, 0]
        }
      }
    },
    getSummaries(param) {
      const { columns, data } = param
      const sums = []
      columns.forEach((column, index) => {
        if (index === 0) {
          sums[index] = '合计'
          return
        }
        const values = data.map(item => Number(item[column.property]))
        if (!values.every(value => isNaN(value))) {
          sums[index] = values.reduce((prev, curr) => {
            const value = Number(curr)
            if (!isNaN(value)) {
              return prev + curr
            } else {
              return prev
            }
          }, 0)

          if (column.property === 'cleanAmount' || column.property === 'moreAmount' || column.property === 'lessAmount' || column.property === 'falseAmount' || column.property === 'bringAmount' || column.property === 'missAmount') {
            sums[index] = formatMoney(sums[index])
          }
        } else {
          sums[index] = 'N/A'
        }
      })

      return sums
    }
  }
}
</script>

<style scoped lang="scss">
.my-table {
  ::v-deep .el-progress-bar__innerText {
    color: #666;
  }
}
.table-fiexd {
  font-size: 14px;
}
</style>
