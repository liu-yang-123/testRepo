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
          width="120"
        >
          <template slot-scope="scope">
            <el-button
              v-permission="['/base/bankInquiry/atmTaskInfo']"
              type="primary"
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

    <!-- 清机任务详情 -->
    <el-dialog
      title="详情"
      :visible.sync="detailFormVisible"
      :close-on-click-modal="false"
      width="80%"
    >
      <el-card v-if="atmTaskRepair" class="box-card" shadow="hover">
        <div slot="header" class="clearfix">
          <span class="title">维修</span>
        </div>
        <el-form label-width="200px" inline>
          <el-form-item label="ATM运行状态">
            <span>{{ formatRunStatus(atmTaskRepair.atmRunStatus) }}</span>
          </el-form-item>
          <el-form-item label="卡钞金额">
            <span>{{ atmTaskRepair.stuckAmount }}（元）</span>
          </el-form-item>
          <el-form-item label="预约时间">
            <span>{{ formatTime(atmTaskRepair.planTime) }}</span>
          </el-form-item>
          <el-form-item label="维修内容">
            <span>{{ atmTaskRepair.content }}</span>
          </el-form-item>
          <el-form-item label="维修公司">
            <span>{{ atmTaskRepair.repairCompany }}</span>
          </el-form-item>
          <el-form-item label="业务员到达时间">
            <span>{{ formatTime(atmTaskRepair.arriveTime) }}</span>
          </el-form-item>
          <el-form-item label="厂家到达时间">
            <span>{{ formatTime(atmTaskRepair.engineerArriveTime) }}</span>
          </el-form-item>
          <el-form-item label="维修人员">
            <span>{{ atmTaskRepair.engineerName }}</span>
          </el-form-item>
          <el-form-item label="是否更换钞箱">
            <span>{{ formatResult(atmTaskRepair.cashboxReplace) }}</span>
          </el-form-item>
          <el-form-item label="是否有遗留现金">
            <span>{{ formatResult(atmTaskRepair.cashInBox) }}</span>
          </el-form-item>
          <el-form-item label="故障描述">
            <span>{{ atmTaskRepair.description }}</span>
          </el-form-item>
          <el-form-item label="处理结果说明">
            <span>{{ atmTaskRepair.dealComments }}</span>
          </el-form-item>
          <el-form-item label="完成时间">
            <span>{{ atmTaskRepair.finishTime }}</span>
          </el-form-item>
          <el-form-item label="备注说明">
            <span>{{ atmTaskRepair.comments }}</span>
          </el-form-item>
        </el-form>
      </el-card>
      <el-card v-if="atmTaskClean" class="box-card" shadow="hover">
        <div slot="header" class="clearfix">
          <span class="title">加钞</span>
        </div>
        <el-form
          ref="atmTaskClean"
          :model="atmTaskClean"
          label-width="200px"
          inline
        >
          <el-form-item label="加钞金额（十万元）" style="width: 50%">
            <span>{{ atmTaskClean.amount }}</span>
          </el-form-item>
          <el-form-item label="加钞钞盒" style="width: 50%">
            <el-button
              v-for="item in atmTaskClean.cashboxMap"
              :key="item.id"
              size="small"
              type="primary"
              plain
            >{{ item.boxNo }}</el-button>
          </el-form-item>
          <el-form-item label="现场清点标志" style="width: 50%">
            <span>{{ formatResult(atmTaskClean.clearSite) }}</span>
          </el-form-item>
          <el-form-item label="回笼钞盒" style="width: 50%">
            <span>{{ atmTaskClean.barboxList.toString() }}</span>
          </el-form-item>
          <el-form-item label="清机密码员" style="width: 50%">
            <span>{{ atmTaskClean.cleanOpManName }}</span>
          </el-form-item>
          <el-form-item label="ATM运行状态" style="width: 50%">
            <span>{{ formatAtmStatus(atmTaskClean.atmRunStatus) }}</span>
          </el-form-item>
          <el-form-item label="清机钥匙员" style="width: 50%">
            <span>{{ atmTaskClean.cleanKeyManName }}</span>
          </el-form-item>
          <el-form-item label="卡钞金额（元）" style="width: 50%">
            <span>{{ atmTaskClean.stuckAmount }}</span>
          </el-form-item>
        </el-form>
      </el-card>
      <el-card v-if="atmTaskCheck" class="box-card" shadow="hover">
        <div slot="header" class="clearfix">
          <span class="title">巡检</span>
        </div>
        <el-form
          ref="atmTaskCheck"
          :model="atmTaskCheck"
          label-width="200px"
          inline
        >
          <el-form-item label="插卡口是否正常" style="width: 50%">
            <span>{{
              formatResult(atmTaskCheck.checkItemResult.cardReader)
            }}</span>
          </el-form-item>
          <el-form-item label="有无安装非法装置" style="width: 50%">
            <span>{{
              formatResult(atmTaskCheck.checkItemResult.thingInstall)
            }}</span>
          </el-form-item>
          <el-form-item label="出钞口是否正常" style="width: 50%">
            <span>{{
              formatResult(atmTaskCheck.checkItemResult.cashOutlet)
            }}</span>
          </el-form-item>
          <el-form-item label="有无非法张贴物" style="width: 50%">
            <span>{{
              formatResult(atmTaskCheck.checkItemResult.thingStick)
            }}</span>
          </el-form-item>
          <el-form-item label="密码键盘防窥罩" style="width: 50%">
            <span>{{
              formatResult(atmTaskCheck.checkItemResult.keypadMask)
            }}</span>
          </el-form-item>
          <el-form-item label="功能标识，操作提示是否齐全" style="width: 50%">
            <span>{{
              formatResult(atmTaskCheck.checkItemResult.operationTips)
            }}</span>
          </el-form-item>
          <el-form-item label="备注" style="width: 50%">
            <span>{{ formatResult(atmTaskCheck.comments) }}</span>
          </el-form-item>
        </el-form>
      </el-card>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailFormVisible = false">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { formatMoney } from '@/utils/convert'
import Pagination from '@/components/Pagination'
import myTable from '@/components/Table'
import { formatdate } from '@/utils/date'
import searchBar from '@/components/SearchBar'
import { listRecord, atmTaskRoute } from '@/api/bankInfo/taskRecord'
import { dictionaryData } from '@/api/system/dictionary'
export default {
  components: { myTable, searchBar, Pagination },
  data() {
    return {
      options: {
        taskType: [
          { code: 1, content: '维修' },
          { code: 2, content: '加钞' },
          { code: 3, content: '清机' }
        ],
        status: [
          { code: -1, content: '已取消' },
          { code: 0, content: '已创建' },
          { code: 1, content: '已确认' },
          { code: 2, content: '已完成' }
        ],
        errType: [
          { code: 1, content: '卡钞' },
          { code: 2, content: '巡检异常' }
        ],
        importBatch: [
          { code: 0, content: '新增' },
          { code: 1, content: '预排' }
        ]
      },
      list: [],
      total: 0,
      listQuery: {
        page: 1,
        limit: 10,
        date: null,
        beginDate: null,
        endDate: null,
        terNo: null,
        taskType: null,
        status: null,
        errType: null,
        importBatch: null
      },
      listLoading: true,
      searchList: [
        { name: 'date', type: 6 },
        { name: 'terNo', label: '设备编号' },
        { name: 'taskType', label: '任务类型', type: 3 },
        { name: 'status', label: '任务状态', type: 3 },
        { name: 'errType', label: '异常类型', type: 3 },
        { name: 'importBatch', label: '下发类型', type: 3 }
      ],
      role: {
        list: '/base/bankInquiry/routeMonitor',
        download: {
          url: '/base/bankInquiry/atmTask/export',
          title: 'ATM任务记录'
        }
      },
      tableList: [
        {
          label: '任务日期',
          prop: 'taskDate',
          formatter: this.formatDate
        },
        {
          label: '线路',
          prop: 'routeNo',
          formatter: this.formatRouteNo
        },
        {
          label: '网点',
          prop: 'bankName'
        },
        {
          label: '设备编号',
          prop: 'terNo'
        },
        {
          label: '任务类型',
          prop: 'taskType',
          formatter: this.formatTaskType
        },
        {
          label: '加钞金额（十万元）',
          prop: 'amount',
          formatter: this.formatMoney
        },
        {
          label: '备注',
          prop: 'comments'
        },
        {
          label: '下发类型',
          prop: 'importBatch',
          formatter: this.formatImportBatch
        },
        {
          label: '状态',
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
        },
        {
          label: '卡超金额（十万元）',
          prop: 'stuckAmount',
          formatter: this.formatMoney
        },
        {
          label: '巡检结果',
          prop: 'checkResult',
          formatter: this.formatCheckResult
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
      atmTaskCheck: null,
      atmTaskClean: null,
      atmTaskRepair: null
    }
  },
  mounted() {
    this.getList()
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
      listRecord(params).then((res) => {
        this.list = res.data.list
        this.total = res.data.total
        this.listLoading = false
      })
    },
    handleDetail(row) {
      atmTaskRoute(row.id).then((res) => {
        this.detailFormVisible = true
        const { atmTaskCheck, atmTaskClean, atmTaskRepair } = res.data
        this.atmTaskCheck = atmTaskCheck
        this.atmTaskClean = atmTaskClean
        this.atmTaskRepair = atmTaskRepair
      })
    },
    formatStatus(statusT) {
      return this.options.status.find(item => item.code === statusT).content
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
    formatCleanStatus(type) {
      for (const item of dictionaryData['CLEAN_STATUS']) {
        if (type === item.code) {
          return item.content
        }
      }
    },
    formatRouteNo(num) {
      if (num) {
        return `${num}号线`
      }
      return '-'
    },
    formatTaskType(type) {
      return this.options.taskType.find(item => item.code === type).content
    },
    formatImportBatch(type) {
      switch (type) {
        case 0:
          return '新增'
        default:
          return '预排'
      }
    },
    formatCheckResult(num) {
      switch (num) {
        case 0:
          return ''
        case 1:
          return '正常'
        case 2:
          return '异常'
      }
    },
    formatResult(num) {
      switch (num) {
        case 0:
          return '否'
        case 1:
          return '是'
      }
    },
    formatAtmStatus(status) {
      switch (status) {
        case 0:
          return '正常'
        case 1:
          return '无存取款项'
        case 2:
          return '部分功能正常'
        case 3:
          return '暂停服务'
      }
    },
    formatMoney,
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
    },
    formatRunStatus(status) {
      switch (status) {
        case 0:
          return '正常'
        case 1:
          return '无存取款项'
        case 2:
          return '部分功能正常'
        case 3:
          return '暂停服务'
      }
    },
    // 过滤
    formatTime(timestamp) {
      if (!timestamp || timestamp === 0) {
        return '-'
      }
      const date = new Date(timestamp)
      return formatdate(date, 'hh:mm:ss')
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

.box-card {
  margin-top: 20px;
  .title {
    font-weight: 600;
    font-size: 18px;
  }
}

.el-form--inline .el-form-item {
  margin-right: 0;
  margin-bottom: 8px;
  width: 33%;
  ::v-deep .el-form-item__label {
    color: #99a9bf;
    margin-right: 12px;
  }
}
</style>
