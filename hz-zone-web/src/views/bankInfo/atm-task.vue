<template>
  <div class="app-container">
    <el-row :gutter="20">
      <div class="filter-container">
        <div style="margin-left: 10px">
          <el-date-picker
            v-model="listQuery.routeDate"
            v-permission="['/base/route/option']"
            value-format="timestamp"
            type="date"
            placeholder="选择日期"
            :clearable="false"
            class="filter-item"
            @change="getTree()"
          />
        </div>
      </div>
    </el-row>
    <el-row :gutter="20">
      <el-col :span="4">
        <el-scrollbar style="height: 500px" class="scrollbar">
          <el-tree
            ref="listTree"
            :data="routeDataOption"
            node-key="value"
            highlight-current
            @node-click="handleNodeClick"
          >
            <span slot-scope="{ data }" class="custom-tree-node">
              <span style="width: 30%">{{ data.routeNo }}</span>
              <span>{{ data.routeName }}</span>
            </span>
          </el-tree>
        </el-scrollbar>
      </el-col>
      <el-col :span="20">
        <div v-if="routeDataOption.length > 0">
          <my-table
            ref="taskTable"
            :list-loading="listLoading"
            :data-list="list"
            :table-list="tableList"
          >
            <template v-slot:operate>
              <el-table-column
                header-align="center"
                align="center"
                label="操作"
                class-name="small-padding fixed-width"
                width="100"
                fixed="right"
              >
                <template slot-scope="scope">
                  <el-button
                    v-permission="['/base/bankInquiry/atmTaskInfo']"
                    type="primary"
                    size="mini"
                    @click="handleDetail(scope.row)"
                  >详情</el-button>
                </template>
              </el-table-column>
            </template>
          </my-table>
          <div
            v-if="bankSum.length > 0"
            class="sum"
          >
            <div style="width: 1000px">
              <el-form
                label-position="right"
                label-width="160px"
                inline
                class="bank-sum"
              >
                <el-form-item label="总任务数：">
                  <span>{{ bankSumTotal.totalTask }}</span>
                </el-form-item>
                <el-form-item label="撤销任务数：">
                  <span>{{ bankSumTotal.cancelTask }}</span>
                </el-form-item>
                <el-form-item label="（100元）撤销金额：">
                  <span>{{ bankSumTotal.hdCancelTotal }}</span>
                </el-form-item>
                <el-form-item label="（10元）撤销金额：">
                  <span>{{ bankSumTotal.tenCancelTotal }}</span>
                </el-form-item>
                <el-form-item label="总金额：">
                  <span>{{ bankSumTotal.amountTotal / 1000 }}</span>
                </el-form-item>
                <el-form-item label="新增任务数：">
                  <span>{{ bankSumTotal.newTask }}</span>
                </el-form-item>
                <el-form-item label="新增金额：">
                  <span>{{ bankSumTotal.hdNewTotal }}</span>
                </el-form-item>
                <el-form-item label="新增金额：">
                  <span>{{ bankSumTotal.tenNewTotal }}</span>
                </el-form-item>
                <el-form-item label="清机加钞数：">
                  <span>{{ bankSumTotal.cleanTotal }}</span>
                </el-form-item>
                <el-form-item />
                <el-form-item label="备用金金额：">
                  <span>{{ bankSumTotal.hdAddTotal }}</span>
                </el-form-item>
                <el-form-item label="备用金金额：">
                  <span>{{ bankSumTotal.tenAddTotal }}</span>
                </el-form-item>
                <el-form-item label="维护数：">
                  <span>{{ bankSumTotal.defend }}</span>
                </el-form-item>
                <el-form-item />
                <el-form-item label="可用金额：">
                  <span>{{
                    bankSumTotal.hdAddTotal +
                      bankSumTotal.hdCancelTotal -
                      bankSumTotal.hdNewTotal
                  }}</span>
                </el-form-item>
                <el-form-item label="可用金额：">
                  <span>{{
                    bankSumTotal.tenAddTotal +
                      bankSumTotal.tenCancelTotal -
                      bankSumTotal.tenNewTotal
                  }}</span>
                </el-form-item>
              </el-form>
            </div>
          </div>
        </div>
        <el-empty v-else style="height: 500px" description="暂无数据" />
      </el-col>
    </el-row>
    <!-- 详情 -->
    <el-dialog
      title="详情"
      :visible.sync="dialogDetailFormVisible"
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
        <el-button @click="dialogDetailFormVisible = false">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
// import Pagination from '@/components/Pagination'
import myTable from '@/components/Table'
// import SearchBar from '@/components/SearchBar'
import {
  listATMTask,
  detailATMTask
} from '@/api/bankInfo/atmTask'
import {
  bankInfoRouteTree
} from '@/api/common/selectOption'
import { formatdate } from '@/utils/date'
import { formatMoney } from '@/utils/convert'
import { dictionaryData } from '@/api/system/dictionary'
import { getToken } from '@/utils/auth'

export default {
  components: { myTable },
  data() {
    const checkNum = (rule, value, callback) => {
      if (!value) {
        return callback(new Error('请输入金额'))
      }
      if (isNaN(value)) {
        callback(new Error('请输入数字值'))
      } else {
        callback()
      }
    }
    return {
      exportVisible: false,
      headers: {
        'X-Token': getToken()
      },
      exportRules: {
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
      exportForm: {
        taskDate: null,
        bankType: null,
        fileList: []
      },
      additionList: [],
      fileList: [],
      paramsData: {},
      bankTypeOption: [
        { id: 1, name: '北京银行' },
        { id: 2, name: '工商银行' },
        { id: 3, name: '农商银行' }
      ],

      listQuery: {
        routeDate: new Date(new Date().toLocaleDateString()).getTime()
      },
      departmentId: null,
      routeDataOption: [],
      loading: null,
      list: [],
      routeListQuery: {
        routeId: null
      },
      selectedRoute: {
        status: null,
        routeName: null,
        type: null,
        routeNo: null
      },
      options: {
        taskType: [
          { code: 2, content: '清机' },
          { code: 1, content: '维修' }
        ],
        atmId: [],
        departmentId: [],
        bankId: []
      },
      listLoading: false,
      total: 0,
      tableList: [
        {
          label: '任务日期',
          prop: 'taskDate',
          formatter: this.formatDate,
          width: 120
        },
        {
          label: '所属银行',
          prop: 'headBank'
        },
        {
          label: '网点名称',
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
          label: '任务状态',
          prop: 'statusT',
          formatter: this.formatStatus
        },
        {
          label: '开始时间',
          prop: 'beginTime',
          formatter: this.formatTime,
          width: 120
        },
        {
          label: '结束时间',
          prop: 'endTime',
          formatter: this.formatTime,
          width: 120
        }
      ],
      cleanFormVisible: false,
      repairFormVisible: false,
      dialogDetailFormVisible: false,
      cleanForm: {
        id: null,
        departmentId: null,
        taskDate: null,
        atmTashCleanList: []
      },
      repairForm: {
        id: null,
        departmentId: null,
        taskDate: null,
        atmTashRepairList: []
      },
      bankOpiton: [],
      cleanListRules: {
        atmId: [{ required: true, message: '请填写', trigger: 'blur' }],
        amount: [{ validator: checkNum, required: true, trigger: 'blur' }]
      },
      addStatus: '',
      atmTaskCheck: null,
      atmTaskClean: null,
      atmTaskRepair: null,
      isDisabled: true,
      batchList: []
    }
  },
  computed: {
    amountTotal() {
      const total = this.list
        .filter((item) => item.statusT !== -1)
        .map((item) => item.amount)
      return total.reduce((n, m) => n + m, 0)
    },
    bankSum() {
      const arr = []
      const obj = {}
      this.list.forEach((item) => {
        if (!arr.find((elm) => elm === item.bankId)) {
          arr.push(item.bankId)
          obj[item.bankId] = this.list.filter(
            (elm) => elm.bankId === item.bankId
          )
        }
      })
      if (arr.length === 1) {
        return obj[arr[0]]
      } else {
        return []
      }
    },
    bankSumTotal() {
      const obj = {
        totalTask: 0,
        cleanTotal: 0,
        defend: 0,
        cancelTask: 0,
        newTask: 0,
        hdCancelTotal: 0,
        tenCancelTotal: 0,
        hdNewTotal: 0,
        tenNewTotal: 0,
        hdAddTotal: 0,
        tenAddTotal: 0,
        amountTotal: 0
      }
      this.bankSum.forEach((item) => {
        if (item.statusT !== -1) {
          console.log(item.amount)
          obj.totalTask++
          obj.amountTotal += item.amount * 1000
        }
        if (
          (item.taskType === 2 || item.taskType === 3) &&
          item.statusT !== -1
        ) {
          obj.cleanTotal++
        }
        if (item.taskType === 1) {
          obj.defend++
        }
        if (item.statusT === -1) {
          obj.cancelTask++
        }
        if (item.importBatch === 0) {
          obj.newTask++
        }
        if (item.statusT === -1 && item.denom === 100) {
          obj.hdCancelTotal += item.amount
        }
        if (item.statusT === -1 && item.denom === 10) {
          obj.tenCancelTotal += item.amount
        }
        if (item.importBatch === 0 && item.denom === 100) {
          obj.hdNewTotal += item.amount
        }
        if (item.importBatch === 0 && item.denom === 10) {
          obj.tenNewTotal += item.amount
        }
      })
      const hdAdd = this.additionList.find(
        (item) =>
          item.bankId === this.bankSum[0].bankId && item.denomValue === 100
      )
      if (hdAdd) {
        obj.hdAddTotal = hdAdd.amount / 100000
      }
      const tenAdd = this.additionList.find(
        (item) =>
          item.bankId === this.bankSum[0].bankId && item.denomValue === 10
      )
      if (tenAdd) {
        obj.tenAddTotal = tenAdd.amount / 100000
      }

      return obj
    }
  },
  mounted() {
    this.openLoading()
    this.getTree()
  },
  methods: {
    formatMoney,
    getList() {
      this.listLoading = true
      listATMTask(this.routeListQuery.routeId).then((res) => {
        this.list = res.data.taskData
        this.additionList = res.data.additionCash
        this.listLoading = false
        this.$nextTick(() => {
          this.$refs.taskTable.$refs.myTable.toggleAllSelection()
        })
      })
    },
    async getTree() {
      await bankInfoRouteTree(this.listQuery)
        .then((res) => {
          const data = res.data
          this.routeDataOption = data
          if (this.routeDataOption.length > 0) {
            this.$nextTick(() => {
              this.$refs.listTree.setCurrentKey(this.routeDataOption[0].value)
            })
            this.handleNodeClick(this.routeDataOption[0])
          } else {
            this.list = []
            this.selectedRoute = {}
          }
        })
        .finally(() => {
          this.loading.close()
        })
    },
    handleNodeClick(val) {
      this.selectedRoute = val
      this.routeListQuery.routeId = val.value
      this.getList()
    },
    handleDetail(row) {
      detailATMTask(row.id).then((res) => {
        const { atmTaskCheck, atmTaskClean, atmTaskRepair } = res.data
        this.atmTaskCheck = atmTaskCheck
        this.atmTaskClean = atmTaskClean
        this.atmTaskRepair = atmTaskRepair
        this.dialogDetailFormVisible = true
      })
    },
    normalizer(node) {
      return {
        id: node.id,
        label: node.name,
        children: node.children
      }
    },

    // 过滤
    formatTime(timestamp) {
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
    formatDateTime(timestamp) {
      if (!timestamp || timestamp === 0) {
        return '-'
      }
      const date = new Date(timestamp)
      return formatdate(date, 'yyyy-MM-dd hh:mm:ss')
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
    formatAmount(num, row) {
      if (row.taskType === 1) {
        return '-'
      }
      return num
    },
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
    formatDetailStatus(type) {
      switch (type) {
        case 0:
          return '未执行'
        case 1:
          return '已完成'
      }
    },
    formatDenomStatus(type) {
      switch (type) {
        case 0:
          return '未确认'
        case 1:
          return '已确认'
        case 2:
          return '待出库'
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
    formatBankType(type) {
      if (type != null) {
        return this.bankTypeOption.find((item) => item.id === type).name
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
    openLoading() {
      this.loading = this.$loading({
        lock: true,
        text: '正在加载...请勿进行其它操作',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      })
    }
  }
}
</script>

<style scoped lang="scss">
.filter-container *:nth-child(n + 2) {
  margin-left: 10px;
}

.vue-treeselect--open {
  font-weight: normal !important;
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

.scrollbar {
  ::v-deep .el-scrollbar__wrap {
    overflow: auto;
  }
}
.number {
  ::v-deep input[type="number"] {
    -moz-appearance: textfield !important;
  }
  ::v-deep input::-webkit-outer-spin-button,
  ::v-deep input::-webkit-inner-spin-button {
    -webkit-appearance: none !important;
  }
}

.sum {
  display: flex;
  margin-top: 30px;
  font-size: 14px;
  color: #606266;
}

.el-table::before {
  z-index: inherit;
}

.custom-tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  font-size: 14px;
  padding-right: 8px;
}

.manage-btn {
  margin-left: 10px;
  vertical-align: top;
}

.confirm-info {
  font-size: 0;
  label {
    width: 120px;
    color: #99a9bf;
    display: flex;
    flex-wrap: wrap;
  }
  .el-form-item {
    margin-right: 0;
    margin-bottom: 0;
    width: 50%;
    div {
      padding-left: 40px;
    }
  }
}

.bank-sum {
  font-size: 0;
  .el-form-item {
    margin-bottom: 0;
    width: 25%;
    ::v-deep .el-form-item__label {
      margin-right: 0 !important;
    }
  }
}
</style>

