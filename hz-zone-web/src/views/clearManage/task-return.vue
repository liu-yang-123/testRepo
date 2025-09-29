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
      <!-- <template v-slot:operate>
        <el-table-column
          align="center"
          label="操作"
          class-name="small-padding fixed-width"
          width="180"
        >
          <template slot-scope="scope">
            <el-button
              v-show="scope.row.deleted == 0 && scope.row.importType == 1"
              v-permission="['/base/taskImportRecord/clearDelete']"
              type="success"
              plain
              size="mini"
              @click="handleExport(scope.row)"
            >下载</el-button>
            <el-button
              v-permission="['/base/taskImportRecord/exportClearTask']"
              :disabled="scope.row.deleted == 1"
              type="danger"
              plain
              size="mini"
              @click="handleDelete(scope.row)"
            >删除</el-button>
          </template>
        </el-table-column>
      </template> -->
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
import { listTask } from '@/api/clearManage/taskReturn'
import { authOption } from '@/api/common/selectOption'
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
        limit: 10,
        page: 1,
        boxBarCode: null,
        departmentId: null
      },
      total: 0,
      listLoading: true,
      searchList: [
        { name: 'departmentId', label: '权限部门', type: 3, notClear: true },
        { name: 'boxBarCode', label: '钞袋编号' }
      ],
      role: {
        list: '/base/taskReturn/list'
      },
      tableList: [
        {
          label: '钞袋编号',
          prop: 'boxBarCode'
        },
        {
          label: '装运方式',
          prop: 'carryType'
        },
        {
          label: '任务日期',
          prop: 'taskDate'
        },
        {
          label: '线路名称',
          prop: 'routeName'
        },
        {
          label: '网点名称',
          prop: 'bankName'
        },
        {
          label: '设备编号',
          prop: 'terNo'
        }
      ],
      detailForm: {}
    }
  },
  mounted() {
    this.getOptions().then(() => {
      this.getList()
    })
  },
  methods: {
    getList(data) {
      if (data) {
        for (const key in data) {
          this.listQuery[key] = data[key]
        }
      }
      listTask(this.listQuery).then((res) => {
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
