<template>
  <div class="app-container">
    <search-bar
      :list-query="listQuery"
      :search-list="searchList"
      :options="options"
      :role="role"
      @lookUp="getList"
    >
      <template v-slot:more>
        <el-button
          class="filter-item"
          type="success"
          icon="el-icon-edit"
          @click="adjustRecord"
        >调整记录</el-button>
        <el-button
          v-permission="['/base/atmTask/exportRecord']"
          class="filter-item"
          type="warning"
          icon="el-icon-download"
          @click="exportRecord"
        >导出派遣单</el-button>
      </template>
    </search-bar>
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
      @pagination="pageChange"
    />
    <div class="total">
      <template v-if="addMap">
        <label>新增任务个数：</label>
        <span>{{ addMap.count }}</span>
        <label>新增任务金额：</label>
        <span>{{ addMap.amount / 100000 }}</span>
      </template>
      <template v-if="cancelMap">
        <label>撤销任务个数：</label>
        <span>{{ cancelMap.count }}</span>
        <label>撤销任务金额：</label>
        <span>{{ cancelMap.amount / 100000 }}</span>
      </template>
    </div>
  </div>
</template>

<script>
// import Pagination from '@/components/Pagination'
import myTable from '@/components/Table'
import Pagination from '@/components/Pagination'
import { formatdate } from '@/utils/date'
import searchBar from '@/components/SearchBar'
import { listTaskRecord, exportRecord } from '@/api/clean/taskRecord'
import { authOption, bankClearTopBank } from '@/api/common/selectOption'
import { dictionaryData } from '@/api/system/dictionary'
import { downloadFile } from '@/utils/downloadFile'

export default {
  components: { myTable, searchBar, Pagination },
  data() {
    return {
      options: {
        routeType: [
          { code: 0, content: '固定线路' },
          { code: 1, content: '临时线路' }
        ],
        taskType: [
          { code: 1, content: '维修' },
          { code: 2, content: '加钞' },
          { code: 3, content: '清机' }
        ],
        importBatch: [
          { code: 0, content: '新增' },
          { code: 1, content: '预排' }
        ],
        status: [
          { code: -1, content: '已取消' },
          { code: 0, content: '已创建' },
          { code: 1, content: '已确认' },
          { code: 2, content: '已完成' }
        ],
        departmentId: [],
        bankId: []
      },
      addMap: null,
      cancelMap: null,
      list: [],
      total: 0,
      listQuery: {
        departmentId: null,
        bankId: null,
        limit: 10,
        page: 1,
        importBatch: null,
        taskDate: null,
        taskType: null,
        terNo: null,
        status: null,
        type: 0
      },
      listLoading: true,
      searchList: [
        { name: 'departmentId', label: '权限部门', type: 3, notClear: true, width: '160px' },
        { name: 'bankId', label: '所属银行', type: 3, notClear: true, width: '160px' },
        { name: 'taskDate', label: '任务日期', type: 2, width: '160px' },
        { name: 'importBatch', label: '下发类型', type: 3, width: '160px' },
        { name: 'taskType', label: '任务类型', type: 3, width: '160px' },
        { name: 'status', label: '任务状态', type: 3, width: '160px' },
        { name: 'terNo', label: '设备编号', width: '160px' }
      ],
      role: {
        list: '/base/atmTask/record'
      },
      tableList: [
        {
          label: '任务日期',
          prop: 'taskDate',
          formatter: this.formatDate,
          width: 120
        },
        {
          label: '线路',
          prop: 'routeNo',
          width: 80
        },
        {
          label: '所属银行',
          prop: 'headBank'
        },
        {
          label: '加款点',
          prop: 'bankName',
          width: 140
        },
        {
          label: '设备编号',
          prop: 'terNo',
          width: 120
        },
        {
          label: '任务类型',
          prop: 'taskType',
          formatter: this.formatTaskType,
          width: 120
        },
        {
          label: '加钞金额（十万元）',
          prop: 'amount',
          width: 160,
          formatter: this.formatAmount
        },
        {
          label: '备注',
          prop: 'comments'
        },
        {
          label: '下发类型',
          prop: 'importBatch',
          formatter: this.formatBatch
        },
        {
          label: '任务状态',
          prop: 'statusT',
          formatter: this.formatStatus
        },
        {
          label: '开始时间',
          prop: 'beginTime',
          formatter: this.formatDateTime
        },
        {
          label: '结束时间',
          prop: 'endTime',
          formatter: this.formatDateTime
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
    this.getAuthOption().then(() => {
      this.getTopBank().then(() => {
        this.getList()
      })
    })
  },
  methods: {
    getList(data) {
      if (data) {
        for (const key in data) {
          this.listQuery[key] = data[key]
        }
      }
      this.listQuery.type = 0
      this.listLoading = true
      this.addMap = this.cancelMap = null
      listTaskRecord(this.listQuery).then((res) => {
        this.list = res.data.data.list
        this.total = res.data.data.total
        this.listLoading = false
      })
    },
    async getAuthOption() {
      await authOption().then((res) => {
        this.options.departmentId = res.data
        if (this.options.departmentId.length > 0) {
          this.listQuery.departmentId = this.options.departmentId[0].id
        }
      })
    },
    async getTopBank() {
      await bankClearTopBank(this.listQuery.departmentId).then(res => {
        this.options.bankId = res.data
        if (this.options.bankId.length > 0) {
          this.listQuery.bankId = this.options.bankId[0].id
        }
      })
    },
    adjustRecord() {
      if (!this.listQuery.taskDate) {
        return this.$message.warning('请先选择日期')
      }
      this.listQuery.type = 1
      this.listLoading = true
      listTaskRecord(this.listQuery).then(res => {
        this.list = res.data.data.list
        this.total = res.data.data.total
        this.addMap = res.data.addMap
        this.cancelMap = res.data.cancelMap
        this.listLoading = false
      })
    },
    exportRecord() {
      if (this.listQuery.bankId == null || this.listQuery.taskDate == null) {
        return this.$message.warning('请先选择日期')
      }
      this.$confirm('确定导出派遣单吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const params = {
          departmentId: this.listQuery.departmentId,
          taskDate: this.listQuery.taskDate,
          bankId: this.listQuery.bankId
        }
        const title = this.options.bankId.find(item => item.id === this.listQuery.bankId).name + '任务派遣单' + this.formatDate(this.listQuery.taskDate)
        downloadFile(exportRecord, params, title, () => { this.loading.close() }, '.xlsx')
      })
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
    formatAmount(num, row) {
      if (row.taskType === 1) {
        return '-'
      }
      return num
    },
    formatStatus(type) {
      for (const item of dictionaryData['CLEAN_STATUS']) {
        if (item.code === type) {
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
    formatBatch(num) {
      switch (num) {
        case 0:
          return '新增'
        default:
          return '预排'
      }
    },
    pageChange() {
      this.listQuery.type === 0 ? this.getList() : this.adjustRecord()
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

.total{
    margin-top: 40px;
    margin-left: 20px;
    label{
        font-size: 14px;
        width: 140px;
        display:inline-block;
    }
    span {
        font-size: 14px;
        display: inline-block;
        width: 80px
    }
}
</style>
