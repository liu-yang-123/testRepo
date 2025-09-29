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
      class="my-table"
    />
  </div>
</template>

<script>
// import Pagination from '@/components/Pagination'
import { formatMoney } from '@/utils/convert'
import myTable from '@/components/Table'
import { formatdate } from '@/utils/date'
import searchBar from '@/components/SearchBar'
import { listStock } from '@/api/statistic/stock'
import { authOption } from '@/api/common/selectOption'
import { dictionaryData } from '@/api/system/dictionary'
export default {
  components: { myTable, searchBar },
  data() {
    return {
      options: {
        departmentId: []
      },
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
        list: '/base/bankReport/stock',
        download: {
          url: '/base/bankReport/stockDownload',
          title: '各银行库存月度统计'
        }
      },
      tableList: [
        {
          label: '银行',
          prop: 'bankName'
        },
        {
          label: '日均库存（元）',
          prop: 'average',
          formatter: this.formatMoney
        },
        {
          label: '最高库存（元）',
          prop: 'high',
          formatter: this.formatMoney
        },
        {
          label: '最低库存（元）',
          prop: 'low',
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
      listStock(this.listQuery).then((res) => {
        this.list = res.data
        this.listLoading = false
      })
    },
    formatMoney,
    formatStatus(statusT) {
      switch (statusT) {
        case 0:
          return '<span style="color:#d12e22">新建</span>'
        case 1:
          return '<span style="color:#dfe000">已审核</span>'
        case 2:
          return '<span style="color:#aa6bbb">配钞完成</span>'
        case 3:
          return '<span style="color:#119fff">任务中...</span>'
        case 4:
          return '<span style="color:green">交接完成</span>'
      }
    },
    formatSeqno(seqno, row) {
      if (seqno === '') {
        return '-'
      } else {
        return `${seqno}/${row.lpno}`
      }
    },
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
    setPercent(row) {
      if (row.taskTotal > 0) {
        return Math.round((row.taskFinish / row.taskTotal) * 100)
      } else {
        return 0
      }
    },
    setStatus(row) {
      if (row.taskFinish === row.taskTotal) {
        return 'success'
      }
    },
    formatPercent(row) {
      return () => {
        return `完成：${row.taskFinish} 总计：${row.taskTotal}`
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
