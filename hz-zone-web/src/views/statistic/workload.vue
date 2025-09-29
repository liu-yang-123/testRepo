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
    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="listQuery.page"
      :limit.sync="listQuery.limit"
      @pagination="getList"
    />
  </div>
</template>

<script>
import Pagination from '@/components/Pagination'
import myTable from '@/components/Table'
import { formatdate } from '@/utils/date'
import { formatMoney } from '@/utils/convert'
import searchBar from '@/components/SearchBar'
import { listWorkload } from '@/api/statistic/workload'
import { authOption, jobNameOption } from '@/api/common/selectOption'
import { dictionaryData } from '@/api/system/dictionary'
export default {
  components: { myTable, searchBar, Pagination },
  data() {
    return {
      options: {
        routeType: [
          { code: 0, content: '固定线路' },
          { code: 1, content: '临时线路' }
        ],
        departmentId: [],
        empId: []
      },
      total: 0,
      list: [],
      listQuery: {
        departmentId: null,
        date: this.formatMonth(new Date()),
        page: 1,
        limit: 10,
        empId: null
      },
      listLoading: true,
      searchList: [
        { name: 'departmentId', label: '权限部门', type: 3, notClear: true, change: this.getJobNameOption },
        { name: 'empId', label: '员工', type: 3 },
        { name: 'date', type: 5, notClear: true }
      ],
      role: {
        list: '/base/bankReport/workload',
        download: {
          url: '/base/bankReport/workloadDownload',
          title: '业务员加钞清机任务统计'
        }
      },
      tableList: [
        {
          label: '序号',
          prop: 'index',
          width: 100
        },
        {
          label: '姓名',
          prop: 'name'
        },
        {
          label: '加款台数',
          prop: 'cashNumber'
        },
        {
          label: '加款金额（万元）',
          prop: 'amount',
          formatter: this.formatMoney
        },
        {
          label: '清机台数',
          prop: 'cleanNumber'
        },
        {
          label: '维护台数',
          prop: 'maintainNumber'
        },
        {
          label: '总台数',
          prop: 'totalNumber'
        },
        {
          label: '上班天数',
          prop: 'workDay'
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
      this.getJobNameOption()
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
      listWorkload(this.listQuery).then((res) => {
        this.list = res.data.list
        this.total = res.data.total
        this.listLoading = false
      })
    },
    async getJobNameOption() {
      this.listQuery.empId = null
      await jobNameOption(this.listQuery.departmentId, '3,4').then(res => {
        this.options.empId = res.data[3].concat(res.data[4])
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
    formatRouteType(type) {
      return this.options.routeType.filter(item => item.code === type)[0].content
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
