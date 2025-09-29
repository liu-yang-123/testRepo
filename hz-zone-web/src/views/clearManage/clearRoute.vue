<template>
  <div class="app-container">
    <div class="filter-container">
      <el-select v-model="listQuery.departmentId" filterable placeholder="请先选择部门" class="filter-item" style="width: 150px" @change="uploadRoute">
        <el-option
          v-for="item in depOptions"
          :key="item.id"
          :label="item.name"
          :value="item.id"
        />
      </el-select>
      <el-date-picker
        v-model="listQuery.taskDate"
        value-format="yyyy-MM-dd"
        type="date"
        placeholder="选择日期"
        :clearable="true"
        style="width: 150px"
        class="filter-item"
        @change="uploadRoute"
      />
      <el-select v-model="listQuery.routeId" filterable clearable placeholder="请选择线路" class="filter-item" style="width: 150px">
        <el-option
          v-for="item in routeOption"
          :key="item.value"
          :label="item.routeName + '/' +item.routeNo"
          :value="item.value"
        />
      </el-select>
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="getList">查找</el-button>
    </div>
    <my-table
      :list-loading="listLoading"
      :data-list="list"
      :table-list="tableList"
    >
      <!-- <template v-slot:operate>
        <el-table-column
          align="center"
          label="操作"
          class-name="small-padding fixed-width"
          width="160"
        >
          <template slot-scope="scope">
            <el-button
              type="success"
              size="mini"
              :disabled="exportDisable"
              @click="handleExport(scope.row)"
            >下载</el-button>
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
import { formatMoney } from '@/utils/convert'
import Pagination from '@/components/Pagination'
import myTable from '@/components/Table'
import { authOption, routeOption } from '@/api/common/selectOption'
import { exportRouteTask, listRoute } from '@/api/clearManage/clearTask'
import { formatdate } from '@/utils/date'
export default {
  name: 'TaskList',
  components: { Pagination, myTable },
  data() {
    return {
      exportDisable: false,
      list: [],
      depOptions: [],
      routeOption: [],
      listQuery: {
        departmentId: null,
        routeId: null,
        taskDate: formatdate(new Date(), 'yyyy-MM-dd'),
        limit: 10,
        page: 1
      },
      listLoading: true,
      total: 0,
      options: {
        statusT: [
          { code: 0, content: '启用' },
          { code: 1, content: '停用' }
        ],
        routeType: [
          { code: 0, content: '固定线路' },
          { code: 1, content: '临时线路' }
        ]
      },
      role: {
        list: '/base/atm/list'
      },
      tableList: [
        {
          label: '任务日期',
          prop: 'taskDate'
        },
        {
          label: '线路编号',
          prop: 'routeNo'
        },
        {
          label: '线路名称',
          prop: 'routeName'
        },
        {
          label: '线路类型',
          prop: 'routeType',
          formatter: this.formatRouteType
        },
        {
          label: '计划清分',
          prop: 'planAmount',
          formatter: this.formatMoney
        },
        {
          label: '实际清分',
          prop: 'clearAmount',
          formatter: this.formatMoney
        },
        {
          label: '总任务数',
          prop: 'totalTask'
        },
        {
          label: '已清分数',
          prop: 'doneTask'
        }
      ],
      dialogFormVisible: false,
      dialogStatus: '',
      bankClearTree: [],
      treeOptions: []
    }
  },
  mounted() {
    this.getOptions()
  },
  methods: {
    getList(data) {
      listRoute(this.listQuery).then((res) => {
        const data = res.data
        this.list = data.list
        this.total = data.total
        this.listLoading = false
      })
    },
    uploadRoute() {
      const taskDate = this.listQuery.taskDate
      const departmentId = this.listQuery.departmentId
      if (taskDate == null || departmentId == null) {
        this.routeOption = []
        return
      }
      const offset = new Date().getTimezoneOffset() * 60 * 1000
      // +8时间戳
      const routeDate8 = new Date(taskDate).getTime()
      const routeDate = routeDate8 + offset
      routeOption({ departmentId, routeDate: routeDate }).then(res => {
        this.routeOption = res.data
      })
    },
    getOptions() {
      authOption().then(res => {
        this.depOptions = res.data
        if (this.depOptions.length > 0) {
          this.listQuery.departmentId = this.depOptions[0].id
          this.getList()
          const routeDate = new Date(new Date().toLocaleDateString()).getTime()
          const departmentId = this.listQuery.departmentId
          routeOption({ departmentId, routeDate: routeDate }).then(res => {
            this.routeOption = res.data
          })
        }
      })
    },
    formatStatus(status) {
      for (const item of this.options.statusT) {
        if (item.code === status) {
          return item.content
        }
      }
    },
    normalizer(node) {
      return {
        id: node.id,
        label: node.name,
        children: node.children
      }
    },
    formatMoney,
    formatDate(timestamp) {
      if (!timestamp || timestamp === 0) {
        return '-'
      }
      const date = new Date(timestamp)
      return formatdate(date, 'yyyy-MM-dd')
    },
    formatRouteType(routeType) {
      const [obj] = this.options.routeType.filter(t => t.code === routeType)
      return obj.content
    },
    handleExport(row) {
      this.exportDisable = true
      const param = {
        taskDate: row.taskDate,
        routeId: row.routeId
      }
      const title = row.taskDate + '-' + row.routeName + '(' + row.routeNo + ')'
      exportRouteTask(param).then(function(response) {
        const filename = title + '.xls'
        const fileUrl = window.URL.createObjectURL(new Blob([response.data]))
        const fileLink = document.createElement('a')
        fileLink.href = fileUrl
        fileLink.setAttribute('download', filename)
        document.body.appendChild(fileLink)
        fileLink.click()
      }).finally(() => {
        this.exportDisable = false
      })
    }
  }
}
</script>

<style scoped lang="scss">
  .filter-container *:nth-child(n+2) {
    margin-left: 10px;
  }
</style>
