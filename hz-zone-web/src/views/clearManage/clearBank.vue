<template>
  <div class="app-container">
    <div class="filter-container">
      <el-select v-model="listQuery.departmentId" filterable placeholder="请先选择部门" class="filter-item" style="width: 150px" @change="departmentChange">
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
      />
      <el-select v-model="listQuery.bankId" clearable filterable placeholder="请选择银行" class="filter-item" style="width: 150px">
        <el-option
          v-for="item in bankSearchOption"
          :key="item.id"
          :label="item.name"
          :value="item.id"
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
  </div>
</template>

<script>
import { formatMoney } from '@/utils/convert'
import myTable from '@/components/Table'
import { bankClearTopBank, authOption } from '@/api/common/selectOption'
import { exportBankTask, listBank } from '@/api/clearManage/clearTask'
import { formatdate } from '@/utils/date'
import { downloadFile } from '@/utils/downloadFile'
export default {
  name: 'TaskList',
  components: { myTable },
  data() {
    return {
      exportDisable: false,
      list: [],
      depOptions: [],
      bankSearchOption: [],
      listQuery: {
        departmentId: null,
        bankId: null,
        taskDate: formatdate(new Date(), 'yyyy-MM-dd')
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
          label: '银行机构名称',
          prop: 'bankName'
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
      listBank(this.listQuery).then((res) => {
        const data = res.data
        this.list = data.list
        this.total = data.total
        this.listLoading = false
      })
    },
    departmentChange(value) {
      bankClearTopBank(value).then(res => {
        this.bankSearchOption = res.data
      })
    },
    getOptions() {
      authOption().then(res => {
        this.depOptions = res.data
        if (this.depOptions.length > 0) {
          this.listQuery.departmentId = this.depOptions[0].id
          this.getList()
          bankClearTopBank(this.listQuery.departmentId).then(res => {
            this.bankSearchOption = res.data
          })
        }
      })
    },
    formatMoney,
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
        bankId: row.bankId
      }
      const title = row.taskDate + '-' + row.bankName
      downloadFile(exportBankTask, param, title, () => { this.exportDisable = false })
    }
  }
}
</script>

<style scoped lang="scss">
  .filter-container *:nth-child(n+2) {
    margin-left: 10px;
  }
</style>
