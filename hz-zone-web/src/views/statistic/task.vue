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
import searchBar from '@/components/SearchBar'
import { listTask } from '@/api/statistic/task'
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
      listLoading: true,
      searchList: [
        { name: 'departmentId', label: '权限部门', type: 3, notClear: true },
        { name: 'date', type: 5, notClear: true }
      ],
      role: {
        list: '/base/bankReport/task',
        download: {
          url: '/base/bankReport/taskDownload',
          title: '各银行加钞数量月度统计'
        }
      },
      tableList: [
        {
          label: '银行',
          prop: 'bankName'
        },
        {
          label: '加钞台数',
          prop: 'cashNumber'
        },
        {
          label: '清机（出库）台数',
          prop: 'cleanNumber'
        },
        {
          label: '维护台数',
          prop: 'maintainNumber'
        },
        {
          label: '撤销台数',
          prop: 'undoNumber'
        },
        {
          label: '应急任务',
          prop: 'emergencyNumber'
        }
      ],
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
      listTask(this.listQuery).then((res) => {
        this.list = res.data
        this.listLoading = false
      })
    },
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
