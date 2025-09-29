<template>
  <div class="app-container">
    <search-bar
      :list-query="listQuery"
      :search-list="searchList"
      :options="options"
      :role="role"
      @lookUp="getList"
    />
    <my-table
      :list-loading="listLoading"
      :data-list="list"
      :table-list="tableList"
      :span-method="spanMethod"
      class="my-table"
    />
  </div>
</template>

<script>
// import Pagination from '@/components/Pagination'
import myTable from '@/components/Table'
import { formatdate } from '@/utils/date'
import { formatMoney } from '@/utils/convert'
import searchBar from '@/components/SearchBar'
import { listAmount } from '@/api/statistic/amount'
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
      list: [],
      recordList: [],
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
        list: '/base/bankReport/amount',
        download: {
          url: '/base/bankReport/amountDownload',
          title: '各银行加款金额月度统计'
        }
      },
      tableList: [
        {
          label: '银行',
          prop: 'bankName'
        },
        {
          label: '券别',
          prop: 'denomName'
        },
        {
          label: '加款台数（不含清机）',
          prop: 'cashNumber'
        },
        {
          label: '加款金额（万元）',
          prop: 'cashAmount',
          formatter: this.formatMoney
        },
        {
          label: '撤销台数',
          prop: 'undoNumber'
        },
        {
          label: '撤销金额（万元）',
          prop: 'undoAmount',
          formatter: this.formatMoney
        }
      ],
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '创建'
      }
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
        this.list = []
        const list = []
        const obj = {}
        res.data.forEach(elm => {
          if (!obj[elm.bankId]) {
            list.push({
              bankId: elm.bankId,
              list: [elm]
            })
            obj[elm.bankId] = true
          } else {
            list.forEach(item => {
              if (item.bankId === elm.bankId) {
                item.list.push(elm)
              }
            })
          }
        })
        list.forEach(item => {
          this.recordList.push({
            bankId: item.list[0].bankId,
            begIdx: this.list.length,
            quantity: item.list.length
          })
          this.list.push(...item.list)
        })
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
        const obj = this.recordList.find(item => item.bankId === row.bankId)
        if ((rowIndex - obj.begIdx) % obj.quantity === 0) {
          return [obj.quantity, 1]
        } else {
          return [0, 0]
        }
      }
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
</style>
