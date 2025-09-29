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
    >
      <template v-slot:operate>
        <el-table-column
          align="center"
          label="操作"
          class-name="small-padding fixed-width"
          width="180"
        >
          <template slot-scope="scope">
            <el-button
              v-if="scope.row.deleted == 0 && scope.row.importType == 1"
              v-permission="['/base/taskImportRecord/exportClearTask']"
              type="success"
              plain
              size="mini"
              @click="handleExport(scope.row)"
            >下载</el-button>
            <el-button
              v-permission="['/base/taskImportRecord/taskDelete']"
              :disabled="scope.row.deleted == 1"
              type="danger"
              plain
              size="mini"
              @click="handleDelete(scope.row)"
            >删除</el-button>
          </template>
        </el-table-column>
      </template>
    </my-table>
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
import searchBar from '@/components/SearchBar'
import { listImportRecord, deleteImportRecord, exportClearTask } from '@/api/clean/importRecord'
import { downloadFile } from '@/utils/downloadFile'
import { authOption, bankClearTopBank } from '@/api/common/selectOption'
export default {
  components: { myTable, searchBar, Pagination },
  data() {
    return {
      options: {
        importType: [
          { code: 0, content: '计划表' },
          { code: 1, content: '回款明细' }
        ],
        bankType: [],
        deleted: [
          { code: 0, content: '已导入' },
          { code: 1, content: '已删除' }
        ],
        departmentId: []
      },
      bankTypeOption: [
        { code: 1, content: '北京银行' },
        { code: 2, content: '工商银行' },
        { code: 3, content: '农商银行' }
      ],
      list: [],
      listQuery: {
        importType: 0,
        limit: 10,
        page: 1,
        bankType: null,
        taskDate: null,
        departmentId: null
      },
      total: 0,
      listLoading: true,
      searchList: [
        { name: 'departmentId', label: '权限部门', type: 3, notClear: true, change: this.getBankOption },
        { name: 'taskDate', label: '任务日期', type: 2 },
        { name: 'bankType', label: '所属银行', type: 3 }
      ],
      role: {
        list: '/base/taskImportRecord/taskList'
      },
      tableList: [
        // {
        //   label: '导入类型',
        //   prop: 'importType',
        //   formatter: this.formatImportType
        // },
        {
          label: '任务日期',
          prop: 'taskDate',
          formatter: this.formatDate
        },
        {
          label: '银行类型',
          prop: 'bankType',
          formatter: this.formatBankType
        },
        {
          label: '文件名称',
          prop: 'fileName'
        },
        {
          label: '状态',
          prop: 'deleted',
          formatter: this.formatDeleted
        },
        {
          label: '导入时间',
          prop: 'createTime',
          formatter: this.formatDateTime
        }
      ],
      detailForm: {}
    }
  },
  mounted() {
    this.getOptions().then(() => {
      this.getList()
      this.getBankOption()
    })
  },
  methods: {
    getList(data) {
      if (data) {
        for (const key in data) {
          this.listQuery[key] = data[key]
        }
      }
      listImportRecord(this.listQuery).then((res) => {
        this.list = res.data.list
        this.total = res.data.total
        this.listLoading = false
      })
    },
    async getOptions() {
      await authOption().then((res) => {
        this.options.departmentId = res.data
        if (this.options.departmentId) {
          this.listQuery.departmentId = this.options.departmentId[0].id
        }
      })
    },
    async getBankOption() {
      this.listQuery.bankType = null
      await bankClearTopBank(this.listQuery.departmentId).then(res => {
        this.options.bankType = res.data
        console.log(111)
      })
    },
    handleDelete(row) {
      this.$confirm('会将该文件所导入的任务同时删除，是否继续', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.listLoading = true
        deleteImportRecord(row.id)
          .then(() => {
            this.$notify.success({
              title: '成功',
              message: '删除成功'
            })
            this.getList()
          }).finally(() => {
            this.listLoading = false
          })
      })
    },
    handleExport(row) {
      this.listLoading = true
      const title = row.fileName.substring(0, row.fileName.length - 4)
      downloadFile(exportClearTask, row.id, title, () => { this.listLoading = false })
    },
    formatDeleted(type) {
      return this.options.deleted.find(item => item.code === type).content
    },
    formatBankType(type) {
      return this.bankTypeOption.find(item => item.code === type).content
    },
    formatDateTime(timestamp) {
      if (!timestamp || timestamp === 0) {
        return '-'
      }
      const date = new Date(timestamp)
      return formatdate(date, 'yyyy-MM-dd hh:mm:ss')
    },
    formatDate(timestamp) {
      if (!timestamp || timestamp === 0) {
        return '-'
      }
      const date = new Date(timestamp)
      return formatdate(date, 'yyyy-MM-dd')
    }
  }
}
</script>

<style scoped lang="scss">

</style>
