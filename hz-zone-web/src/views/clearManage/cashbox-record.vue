<template>
  <div class="app-container">
    <search-bar :options="options" :list-query="listQuery" :search-list="searchList" :role="role" @lookUp="getList" />
    <my-table
      :list-loading="listLoading"
      :data-list="list"
      :table-list="tableList"
    >
      <template v-slot:operate>
        <el-table-column
          align="center"
          label="操作"
          class-name="small-padding fixed-width"
          width="140"
        >
          <template slot-scope="scope">
            <el-button
              v-permission="['/base/cashboxPack/info']"
              type="primary"
              size="mini"
              @click="handleDetail(scope.row)"
            >详情</el-button>
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

    <el-dialog
      title="详情"
      :visible.sync="detailFormVisible"
      :close-on-click-modal="false"
      width="40%"
    >
      <div>
        <el-form label-width="120px" class="detail">
          <el-form-item label="分配线路">
            <span>{{ detailForm.routeNo ? `${detailForm.routeNo}号线` : '' }}</span>
          </el-form-item>
          <el-form-item label="加钞设备">
            <span>{{ detailForm.atmTerNo }} {{ detailForm.secondAtmTerNo }}</span>
          </el-form-item>
          <el-form-item label="扫码记录">
            <my-table
              ref="myTable"
              :data-list="detailList"
              :table-list="deatilTableList"
              :height="'500'"
              style="width:80%"
            />
          </el-form-item>
        </el-form>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import Pagination from '@/components/Pagination'
import myTable from '@/components/Table'
import searchBar from '@/components/SearchBar'
import { listCashbox, detailCashbox } from '@/api/clearManage/cashboxRecord'
import { formatdate } from '@/utils/date'
import { authOption, bankClearTopBank, jobNameOption } from '@/api/common/selectOption'

export default {
  components: { Pagination, myTable, searchBar },
  data() {
    return {
      list: [],
      listQuery: {
        limit: 10,
        page: 1,
        taskDate: null,
        packTime: null,
        bankId: null,
        clearManId: null,
        boxNo: null,
        departmentId: null,
        statusT: null,
        denom: null
      },
      listLoading: true,
      total: 0,
      searchList: [
        { name: 'departmentId', label: '所属部门', type: 3, width: '180px', notClear: true },
        { name: 'taskDate', label: '任务日期', type: 2, width: '180px' },
        { name: 'packTime', label: '清点日期', type: 2, width: '180px' },
        { name: 'bankId', label: '所属银行', type: 3, width: '180px' },
        { name: 'clearManId', label: '清点人', type: 3, width: '180px' },
        { name: 'boxNo', label: '钞盒编号', width: '180px' },
        { name: 'statusT', label: '状态', type: 3, width: '180px' },
        { name: 'denom', label: '面额', type: 3, width: '180px' }
      ],
      options: {
        departmentId: [],
        bankId: [],
        clearManId: [],
        statusT: [
          { code: 0, name: '封装' },
          { code: 2, name: '配钞' },
          { code: 3, name: '拆封' }
        ],
        denom: [
          { code: 10, name: '10元' },
          { code: 100, name: '100元' }
        ]
      },
      role: {
        list: '/base/cashboxPack/list'
      },
      tableList: [
        {
          label: '任务日期',
          prop: 'taskDate',
          formatter: this.formatDate
        },
        {
          label: '钞盒',
          prop: 'boxNo'
        },
        {
          label: '券别类型',
          prop: 'denomName'
        },
        {
          label: '金额（万元）',
          prop: 'amount',
          formatter: this.formatAmount
        },
        {
          label: '所属银行',
          prop: 'bankName'
        },
        {
          label: '清点人',
          prop: 'clearManName'
        },
        {
          label: '复核人',
          prop: 'checkManName'
        },
        {
          label: '清点时间',
          prop: 'packTime',
          formatter: this.formatDateTime
        },
        {
          label: '清点设备',
          prop: 'deviceNo'
        },
        {
          label: '状态',
          prop: 'statusT',
          formatter: this.formatStatus
        }
      ],
      detailFormVisible: false,
      detailForm: {},
      detailList: [],
      deatilTableList: [
        {
          label: '扫描时间',
          prop: 'scanTime',
          formatter: this.formatDateTime
        },
        {
          label: '扫描人',
          prop: 'scanUser'
        },
        {
          label: '扫描节点',
          prop: 'scanNode',
          formatter: this.formatNode
        }
      ]
    }
  },
  mounted() {
    this.getAuthOption().then(() => {
      this.getOptions()
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
      listCashbox(this.listQuery).then((res) => {
        const data = res.data
        this.list = data.list
        this.total = data.total
        this.listLoading = false
      })
    },
    getAuthOption() {
      return new Promise((reslove, reject) => {
        authOption()
          .then(res => {
            this.options.departmentId = res.data
            this.listQuery.departmentId = this.options.departmentId[0].id
            reslove()
          }).catch(err => {
            reject(err)
          })
      })
    },
    async getOptions() {
      this.options.bankId = null
      this.options.clearManId = null
      await Promise.all([
        bankClearTopBank(this.listQuery.departmentId),
        jobNameOption(this.listQuery.departmentId, '5')
      ]).then(res => {
        const [res1, res2] = res
        this.options.bankId = res1.data
        this.options.clearManId = res2.data[5]
      })
    },
    handleDetail(row) {
      detailCashbox(row.id).then(res => {
        this.detailForm = res.data
        this.detailFormVisible = true
        this.detailList = res.data.scans
      })
    },
    formatDate(timestamp) {
      if (!timestamp || timestamp === 0) {
        return '-'
      }
      const date = new Date(timestamp)
      return formatdate(date, 'yyyy-MM-dd')
    },
    formatDateTime(timestamp) {
      if (!timestamp || timestamp === 0) {
        return '-'
      }
      const date = new Date(timestamp)
      return formatdate(date, 'yyyy-MM-dd hh:mm:ss')
    },
    formatStatus(status) {
      switch (status) {
        case 0:
          return '<span style="color: rgb(78, 223, 65)">封装</span>'
        case 2:
          return '<span style="color: rgb(87, 171, 219)">配钞</span>'
        case 3:
          return '<span style="color: rgb(48, 45, 212)">拆封</span>'
      }
    },
    formatAmount(num) {
      return num / 10000
    },
    formatNode(num) {
      switch (num) {
        case 0:
          return '包装'
        case 1:
          return '入库'
        case 2:
          return '出库'
        case 3:
          return '盘点'
        case 4:
          return '配钞'
        case 5:
          return '配钞复核'
        case 6:
          return '加钞'
        case 7:
          return '原装钞盒重绑'
        case 8:
          return '手工解绑'
      }
    }
  }
}
</script>

<style scoped lang="scss">
.detail {
  font-size: 0;
  .el-form-item {
    margin-bottom: 18px;
    ::v-deep .el-form-item__label {
      margin-right: 0 !important;
    }
  }
}
</style>
