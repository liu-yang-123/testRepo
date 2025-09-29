<template>
  <div class="app-container">
    <search-bar
      :list-query="listQuery"
      :search-list="searchList"
      :options="options"
      :role="role"
      @lookUp="getList"
    />
    <!-- <div class="filter-container">
      <el-date-picker
        v-model="listQuery.taskDate"
        value-format="yyyy-MM-dd"
        type="date"
        placeholder="选择日期"
        :clearable="true"
        style="width: 150px"
        class="filter-item"
        @change="updateRoute"
      />
      <el-select
        v-model="listQuery.routeId"
        filterable
        clearable
        placeholder="请选择线路"
        class="filter-item"
        style="width: 150px"
      >
        <el-option
          v-for="item in routeSearchOption"
          :key="item.value"
          :label="item.routeName + '/' + item.routeNo"
          :value="item.value"
        />
      </el-select>
      <el-select
        v-model="listQuery.statusT"
        clearable
        filterable
        placeholder="请先选清分状态"
        class="filter-item"
        style="width: 150px"
      >
        <el-option
          v-for="item in options.statusT"
          :key="item.code"
          :label="item.content"
          :value="item.code"
        />
      </el-select>
      <el-button
        class="filter-item"
        type="primary"
        icon="el-icon-search"
        @click="listQuery.page = 1;getList()"
      >查找</el-button>
    </div> -->
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
          width="100"
          header-align="center"
        >
          <template slot-scope="scope">
            <el-button
              v-permission="['/base/bankInquiry/clearTaskDetail']"
              plain
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

    <!-- 审核历史详细 -->
    <el-dialog
      title="审核记录"
      :visible.sync="auditDialogHistoryVisible"
      :close-on-click-modal="false"
    >
      <el-row>
        <el-timeline v-if="auditHistoryList.length > 0">
          <el-timeline-item
            v-for="audit in auditHistoryList"
            :key="audit.id"
            class="timeline-item"
          >
            <span v-text="audit.userName" />
            <span v-if="audit.status == 1">已同意</span>
            <span v-if="audit.status == 2">已拒绝</span>
            <span>{{ formatDateTime(audit.createTime) }}</span>
            <span v-text="audit.comments" />
          </el-timeline-item>
        </el-timeline>
        <el-empty v-else :image-size="200" />
      </el-row>
      <div slot="footer" class="dialog-footer">
        <el-button @click="auditDialogHistoryVisible = false">关闭</el-button>
      </div>
    </el-dialog>
    <!-- 详情 -->
    <el-dialog
      title="详情"
      :visible.sync="detailVisible"
      :close-on-click-modal="false"
    >
      <div>
        <my-table :data-list="detailList" :table-list="detailTableList" />
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailVisible = false">关闭</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import { formatMoney } from '@/utils/convert'
import Pagination from '@/components/Pagination'
import myTable from '@/components/Table'
import {
  bankInfoRouteTree,
  denomOption
} from '@/api/common/selectOption'
import searchBar from '@/components/SearchBar'
import {
  listTask,
  detailTask
} from '@/api/bankInfo/clearTask'
import { formatdate } from '@/utils/date'
export default {
  name: 'TaskList',
  components: { Pagination, searchBar, myTable },
  filters: {},
  data() {
    // const checkList = (rule, value, callback) => {
    //   if (value.length > 0) {
    //     callback()
    //   } else {
    //     callback(new Error('请添加明细'))
    //   }
    // }
    return {
      loading: null,
      list: [],
      routeSearchOption: [],
      routeOption: [],
      listQuery: {
        limit: 10,
        page: 1,
        statusT: null,
        date: null,
        beginDate: null,
        endDate: null,
        atmNo: null,
        errType: null
      },
      searchList: [
        { name: 'date', type: 6 },
        { name: 'atmNo', label: '设备号' },
        { name: 'errType', label: '任务类型', type: 3 }
      ],
      role: {
        list: '/base/bankInquiry/clearTaskList',
        download: {
          url: '/base/bankInquiry/clearTask/export',
          title: '清分任务'
        }
      },
      listLoading: true,
      total: 0,
      options: {
        statusT: [
          { code: 0, content: '未开始' },
          { code: 1, content: '已结束' }
        ],
        auditStatusT: [
          { code: 1, content: '审核中' },
          { code: 2, content: '审核通过' },
          { code: 3, content: '审核拒绝' }
        ],
        detailType: [
          { code: 1, content: '假币' },
          { code: 2, content: '残缺币' },
          { code: 3, content: '夹张' }
        ],
        denomId: [],
        errType: [
          { code: 0, content: '平账' },
          { code: 1, content: '长款' },
          { code: 2, content: '短款' },
          { code: 3, content: '假币' }
        ]
      },
      tableList: [
        {
          label: '任务日期',
          prop: 'taskDate'
        },
        {
          label: '线路',
          prop: 'routeText'
        },
        {
          label: '银行名称',
          prop: 'bankName'
        },
        {
          label: '设备编号',
          prop: 'terNo'
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
          label: '差错金额',
          prop: 'errorAmount',
          formatter: this.formatMoney
        },
        {
          label: '差错类型',
          prop: 'errorType',
          formatter: this.formatErrorType
        },
        {
          label: '清点员',
          prop: 'clearManName'
        },
        {
          label: '复核员',
          prop: 'checkManName'
        },
        {
          label: '状态',
          prop: 'statusT',
          formatter: this.formatStatus
        },
        {
          label: '备注',
          prop: 'comments'
        }
      ],
      dialogFormVisible: false,
      detailVisible: false,
      detailForm: {},
      detailList: [],
      detailTableList: [
        {
          label: '差错类型',
          prop: 'detailType',
          formatter: this.formatDetailType
        },
        {
          label: '券别',
          prop: 'denomId',
          formatter: this.formatDenomId
        },
        {
          label: '张数',
          prop: 'count'
        },
        {
          label: '金额',
          prop: 'amount',
          formatter: this.formatMoney
        },
        {
          label: '冠字号',
          prop: 'rmbSn'
        }
      ],
      uploadRules: {
        taskDate: [
          { required: true, message: '日期不能为空', trigger: 'blur' }
        ],
        bankType: [
          { required: true, message: '银行类型不能为空', trigger: 'blur' }
        ],
        fileList: [
          { required: true, message: '上传文件不能为空', trigger: 'blur' }
        ]
      },
      uploadDataForm: {
        taskDate: null,
        bankType: null,
        fileList: []
      },
      fileList: [],
      paramsData: {},
      bankTypeOption: [
        { id: 1, name: '北京银行' },
        { id: 2, name: '工商银行' },
        { id: 3, name: '农商银行' }
      ],
      auditList: [],
      auditTableList: [
        {
          label: '任务日期',
          prop: 'taskDate'
        },
        {
          label: '线路',
          prop: 'routeText'
        },
        {
          label: '银行名称',
          prop: 'bankName'
        },
        {
          label: '设备编号',
          prop: 'terNo'
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
          label: '差错金额',
          prop: 'errorAmount',
          formatter: this.formatMoney
        },
        {
          label: '清点员',
          prop: 'clearManName'
        },
        {
          label: '复核员',
          prop: 'checkManName'
        },
        {
          label: '审核状态',
          prop: 'statusT',
          formatter: this.formatAuditStatus
        },
        {
          label: '备注',
          prop: 'comments'
        },
        {
          label: '提交时间',
          prop: 'createTime',
          formatter: this.formatDateTime,
          width: 90
        }
      ],
      auditListLoading: true,
      auditTotal: 0,
      bankAuditSearchOption: [],
      routeAuditSearchOption: [],
      auditDataForm: {
        id: 0,
        status: 1,
        comments: ''
      },
      auditDialogHistoryVisible: false,
      auditHistoryList: [],
      statusT: false,
      // 上传确认
      dialogConfirmVisible: false,
      confirmForm: {},
      confirmList: [],
      confirmTableList: [
        {
          label: '设备编号',
          prop: 'terNo'
        },
        {
          label: '计划清分金额',
          prop: 'planAmount',
          formatter: this.formatMoney
        }
      ],
      confirmAmountTotal: 0
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
      const { date, ...params } = this.listQuery
      if (date) {
        params.beginDate = date[0]
        params.endDate = date[1]
      }
      listTask(params).then((res) => {
        const data = res.data
        this.list = data.list
        this.total = data.total
        this.listLoading = false
      })
    },
    async getOptions() {
      await denomOption().then((res) => {
        this.options.denomId = res.data
      })
    },
    handleDetail(row) {
      detailTask(row.id).then((res) => {
        this.detailVisible = true
        this.detailList = res.data.errorList
      })
    },
    handleTaskDetail(row) {
      this.detailVisible = true
      this.detailList = JSON.parse(row.errorList)
    },
    formatMoney,
    formatNum(e) {
      e.target.value = e.target.value.replace(/[^\d.]/g, '')
      e.target.value = e.target.value.replace(/\.{2,}/g, '.')
      e.target.value = e.target.value.replace(/^\./g, '0.')
      e.target.value = e.target.value.replace(
        /^\d*\.\d*\./g,
        e.target.value.substring(0, e.target.value.length - 1)
      )
      e.target.value = e.target.value.replace(/^0[^\.]+/g, '0')
      e.target.value = e.target.value.replace(/^(\d+)\.(\d\d).*$/, '$1.$2')
      return e.target.value
    },
    formatStatus(status) {
      for (const item of this.options.statusT) {
        if (item.code === status) {
          return item.content
        }
      }
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
    },
    formatAuditStatus(status) {
      for (const item of this.options.auditStatusT) {
        if (item.code === status) {
          return item.content
        }
      }
    },
    formatTerType(terType) {
      for (const item of this.options.terType) {
        if (item.code === terType) {
          return item.content
        }
      }
    },
    formatLocationType(locationType) {
      for (const item of this.options.locationType) {
        if (item.code === locationType) {
          return item.content
        }
      }
    },
    formatDetailType(type) {
      if (type != null) {
        return this.options.detailType.find((item) => item.code === type)
          .content
      }
    },
    formatDenomId(type) {
      if (type != null) {
        return this.options.denomId.find((item) => item.id === type).name
      }
    },
    formatBankType(type) {
      if (type != null) {
        return this.bankTypeOption.find((item) => item.id === type).name
      }
    },
    formatErrorType(type, row) {
      if (row.statusT === 0) {
        return '<span style="color:red">——</span>'
      }
      switch (type) {
        case 0:
          return '<span style="color:#31aa09">平账</span>'
        case 1:
          return '<span style="color:#fac11b">长款</span>'
        case 2:
          return '<span style="color:red">短款</span>'
      }
    },
    updateRoute() {
      const taskDate = this.listQuery.taskDate
      if (taskDate == null) {
        this.routeSearchOption = []
        return
      }
      const offset = new Date().getTimezoneOffset() * 60 * 1000
      // +8时间戳
      const routeDate8 = new Date(taskDate).getTime()
      const routeDate = routeDate8 + offset
      bankInfoRouteTree({ routeDate: routeDate }).then((res) => {
        this.routeSearchOption = res.data
      })
    }
  }
}
</script>

<style scoped lang="scss">
.filter-container *:nth-child(n + 2) {
  margin-left: 10px;
}
.timeline-item span {
  margin-right: 15px;
}

.confirm-info {
  .el-form-item {
    margin-right: 0;
    margin-bottom: 0;
    width: 50%;
    div {
      padding-left: 40px;
    }
  }
}

.el-form--inline .el-form-item {
  margin-right: 0;
  margin-bottom: 8px;
  width: 33%;
  ::v-deep .el-form-item__label {
    color: #99a9bf;
    margin-right: 12px;
    font-size: 16px;
  }
  ::v-deep .el-form-item__content {
    font-size: 16px;
  }
}
</style>

