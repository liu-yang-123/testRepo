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
      :row-class-name="rowClassName"
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
import { listPayment } from '@/api/statistic/receivePayment'
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
      listQuery: {
        date: this.formatMonth(new Date()),
        departmentId: null
      },
      recordList: [],
      listLoading: true,
      searchList: [
        { name: 'departmentId', label: '权限部门', type: 3, notClear: true },
        { name: 'date', type: 5, notClear: true }
      ],
      role: {
        list: '/base/bankReport/receivePayment',
        download: {
          url: '/base/bankReport/receivePaymentDownload',
          title: '银行领缴款月度统计'
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
          label: '领款金额（元）',
          prop: 'receiveAmount',
          formatter: this.formatMoney
        },
        {
          label: '缴款金额（元）',
          prop: 'paymentAmount',
          formatter: this.formatMoney
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
      listPayment(this.listQuery).then((res) => {
        this.recordList = []
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
          item.list.push({
            bankId: item.list[0].bankId,
            bankName: item.list[0].bankName,
            denomName: '合计',
            receiveAmount: item.list.map(elm => elm.receiveAmount).reduce((n, m) => n + m),
            paymentAmount: item.list.map(elm => elm.paymentAmount).reduce((n, m) => n + m)
          })
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
    },
    rowClassName({ row }) {
      if (row.denomName === '合计') {
        return 'total-row'
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
<style lang="scss">
.el-table .total-row {
    background: rgba(247, 214, 214, 0.418);
  }
</style>
