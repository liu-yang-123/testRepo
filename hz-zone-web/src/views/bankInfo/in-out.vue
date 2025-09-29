<template>
  <div class="app-container">
    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-date-picker v-model="listQuery.orderDate" value-format="timestamp" style="width: 150px" type="date" placeholder="选择日期" class="filter-item" />
      <el-select v-model="listQuery.orderType" filterable placeholder="请选择类别" clearable class="filter-item" style="width: 150px">
        <el-option
          v-for="item in typeList"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        />
      </el-select>
      <el-select v-model="listQuery.subType" filterable placeholder="请选择类型" clearable class="filter-item" style="width: 150px">
        <el-option
          v-for="item in subTypeList"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        />
      </el-select>
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="getList">查找</el-button>
    </div>
    <!-- 查询结果 -->
    <el-table
      v-loading="listLoading"
      element-loading-text="正在查询中。。。"
      :data="list"
      :header-cell-style="{'background-color':'#f5f5f5'}"
      border
      fit
    >
      <el-table-column align="center" label="账务日期" prop="orderDate">
        <template slot-scope="scope">
          <i class="el-icon-time" />
          <span style="margin-left: 10px">{{ scope.row.orderDate | formatDateTime }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="机构名称" prop="bankName" />
      <el-table-column align="center" label="类别" prop="orderType" :formatter="formatType" />
      <el-table-column align="center" label="出入库类型" prop="subType" :formatter="formatSubType" />
      <el-table-column align="center" label="金额" prop="orderAmount" :formatter="formatMoney" />
      <el-table-column align="center" label="状态" prop="statusT" :formatter="formatStatus" />
      <el-table-column align="center" label="明细" width="100">
        <template slot-scope="scope">
          <el-button
            v-permission="['/base/bankInquiry/vaultOrderDetail']"
            plain
            type="primary"
            size="mini"
            @click="handleDetail(scope.row)"
          >详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

    <!-- 明细框 -->
    <el-dialog :title="detailDialogTitle === 'out' ? '出库明细' : '入库明细'" :visible.sync="detailDialogFormVisible" width="65%" :close-on-click-modal="false">
      <!-- <el-steps :active="orderStep" finish-status="success" simple style="margin-bottom: 20px;padding: 13px 2% !important;">
        <el-step v-for="item in stepMap[detailForm.orderType]" :key="item" class="step-box" :title="item" />
      </el-steps> -->
      <el-form label-position="left" inline>
        <el-card class="box-card">
          <el-row>
            <el-col :span="12">
              <el-form-item label="金融机构" style="margin-bottom: 0px;">
                <span v-text="detailForm.bankName" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="账务日期" style="margin-bottom: 0px;">
                <span>{{ detailForm.orderDate | formatDateTime }}</span>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="金额" style="margin-bottom: 0px;">
                <span v-text="formatMoney(detailForm)" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="状态" style="margin-bottom: 0px;">
                <span v-text="formatStatus(detailForm)" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item :label="detailDialogTitle === 'out' ? '出库人' : '入库人'" style="margin-bottom: 0px;">
                <span>{{ detailForm.whOpManName }} {{ detailForm.whCheckManName }} {{ detailForm.whConfirmManName }}</span>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item :label="detailDialogTitle === 'out' ? '出库时间' : '入库时间'" style="margin-bottom: 0px;">
                <span v-text="formatWhOptime(detailForm)" />
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="备注" style="margin-bottom: 0px;">
                <span>{{ detailForm.comments }}</span>
              </el-form-item>
            </el-col>
          </el-row>
        </el-card>
      </el-form>
      <el-divider v-if="detailForm.subType == 0 && detailForm.orderType == 1" content-position="center">ATM加钞任务</el-divider>
      <el-collapse v-if="detailForm.subType == 0 && detailForm.orderType == 1" v-model="activeNames">
        <el-collapse-item v-for="route in detailForm.routeList" :key="route.routeId" :title="getRouteTitle(route)" :name="route.routeId">
          <div v-for="(task,index) in route.taskList" :key="index" class="text item">
            {{ 'ATM设备编号： ' + task.terNo + '; 加钞金额：' + task.amount + '元' }}
          </div>
        </el-collapse-item>
      </el-collapse>
      <!--      <div v-if="detailForm.subType == 0" style="margin-top: 5px;padding-top: 4px">总金额：{{ formatMoney(detailForm) }}</div>-->

      <div v-for="denom in detailForm.denomList" :key="denom.denomType">
        <el-divider v-if="denom.dataList.length > 0" content-position="center">{{ denom.text }}</el-divider>
        <el-table
          v-if="denom.dataList.length > 0"
          :data="denom.dataList"
          :header-cell-style="{background:'#eef1f6',color:'#606266'}"
          style="width: 100%"
        >
          <el-table-column
            prop="denomText"
            label="面额"
          />
          <el-table-column
            prop="amount"
            label="金额(元)"
            :formatter="formatAmount"
          />
          <el-table-column prop="count" label="张数">
            <template slot-scope="scope">
              <div v-if="denom.denomType === 1">{{ scope.row.count }}</div>
              <div v-else>——</div>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <el-divider v-if="auditList.length > 0" content-position="center">出入库审核</el-divider>
      <el-timeline>
        <el-timeline-item v-for="audit in auditList" :key="audit.id" class="timeline-item">
          <span v-text="audit.userName" />
          <span v-if="audit.status == 1">已同意</span>
          <span v-if="audit.status == 2">已拒绝</span>
          <span>{{ audit.createTime | formatDateTimeT }}</span>
          <span v-if="audit.status == 2" v-text="audit.comments" />
        </el-timeline-item>
      </el-timeline>

      <el-divider v-if="undoList.length > 0" content-position="center">撤销审核</el-divider>
      <el-timeline>
        <el-timeline-item v-for="audit in undoList" :key="audit.id" class="timeline-item">
          <span v-text="audit.userName" />
          <span v-if="audit.status == 1">已同意</span>
          <span v-if="audit.status == 2">已拒绝</span>
          <span>{{ audit.createTime | formatDateTimeT }}</span>
          <span v-if="audit.status == 2" v-text="audit.comments" />
        </el-timeline-item>
      </el-timeline>

      <div slot="footer" class="dialog-footer">
        <el-button @click="detailDialogFormVisible = false">关闭</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import { formatMoney } from '@/utils/convert'
import Pagination from '@/components/Pagination'
import { listOrder, listOrderDetail } from '@/api/bankInfo/inOut'
import { formatdate } from '@/utils/date'
import { denomOption } from '@/api/base/denomination'
export default {
  name: 'VaultIn',
  components: { Pagination },
  filters: {
    formatDateTime(timestamp) {
      if (!timestamp || timestamp === 0) {
        return '-'
      }
      const date = new Date(timestamp)
      return formatdate(date, 'yyyy-MM-dd')
    },
    formatDateTimeT(timestamp) {
      if (!timestamp || timestamp === 0) {
        return '-'
      }
      const date = new Date(timestamp)
      return formatdate(date, 'yyyy-MM-dd hh:mm:ss')
    }
  },
  data() {
    return {
      selectedDenomId1: [],
      selectedDenomId2: [],
      selectedDenomId3: [],
      selectedDenomId4: [],
      allDenomNameList: [],
      taskIds: [],
      isUpdated: false,
      activeNames: [],
      routeTaskList: [],
      value: [1],
      value4: [1],
      list: null,
      usableDenomList: [{}],
      badDenomList: [{}],
      goodDenomList: [{}],
      unclearDenomList: [{}],
      denomNameList: [],
      badDenomNameList: [],
      auditList: [],
      undoList: [],
      denomList: [],
      depOptions: [],
      bankOption: [],
      bankSearchOption: [],
      total: 0,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 10,
        orderType: null
      },
      dataForm: {
        orderDate: 0,
        bankId: 0,
        orderType: 0,
        subType: 0,
        comments: ''
      },
      atmDataForm: {
        id: null,
        orderDate: null,
        bankId: null,
        orderType: null,
        subType: null,
        departmentId: null,
        taskIds: [],
        vaultRecordList: [],
        comments: ''
      },
      allDenomTableList: [
        {
          label: '券别',
          prop: 'denomId',
          formatter: this.formatDenom
        },
        {
          label: '金额',
          prop: 'amount',
          formatter: this.formatDenomMoney
        }
      ],
      orderStep: 0,
      detailForm: {
        step: 3,
        bankName: '',
        orderDate: 0,
        orderAmount: 0,
        subType: 0,
        denomList: [],
        routeList: [],
        comments: ''
      },

      orderType: 0,
      subType: 0,
      // 入库数据
      inOutDialogFormVisible: false,
      inOutFont: '',
      inDialogFormVisible: false,
      inDataForm: {
        orderDate: 0,
        bankId: 0,
        orderType: 0,
        subType: 0
      },
      stepMap: {
        0: ['新建入库', '审核中', '等待入库', '入库完成', '撤销中', '已撤销'],
        1: ['新建出库', '审核中', '等待出库', '出库完成', '撤销中', '已撤销']
      },
      detailDialogFormVisible: false,
      detailDialogTitle: '',
      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '创建'
      },
      auditDataForm: {
        id: 0,
        status: 1,
        comments: ''
      },
      typeList: [
        { value: 0, label: '入库' },
        { value: 1, label: '出库' }
      ],
      subTypeList: [
        { value: 0, label: 'ATM加钞' },
        { value: 1, label: '领缴款' }
      ],
      statusList: [
        { value: -1, label: '已撤销' },
        { value: 0, label: '创建' },
        { value: 1, label: '审核中' },
        { value: 2, label: '审核拒绝' },
        { value: 3, label: '审核通过' },
        { value: 4, label: '已入库' },
        { value: 5, label: '撤销中' },
        { value: 6, label: '撤销拒绝' }
      ]
    }
  },
  computed: {
    allDenomlist() {
      const that = this
      const list = this.taskIds.map(elm => {
        return that.routeTaskList.find(item => item.key === elm)
      })
      const obj = {}
      list.forEach(item => {
        const key = item.denomId
        if (!obj[key]) {
          obj[key] = 0
        }
        obj[key] += item.amount
      })
      const dataList = []
      for (const key in obj) {
        dataList.push({
          denomId: key,
          denomType: 0,
          count: 0,
          amount: obj[key]
        })
      }
      return dataList
    }
  },
  watch: {
    usableDenomList: {
      handler(val) {
        this.selectedDenomId1 = val.map(item => item.denomId)
      },
      deep: true
    },
    badDenomList: {
      handler(val) {
        this.selectedDenomId2 = val.map(item => item.denomId)
      },
      deep: true
    },
    goodDenomList: {
      handler(val) {
        this.selectedDenomId3 = val.map(item => item.denomId)
      },
      deep: true
    },
    unclearDenomList: {
      handler(val) {
        this.selectedDenomId4 = val.map(item => item.denomId)
      },
      deep: true
    }
  },
  created() {
    this.getList()
    this.getDenomNameList()
  },
  methods: {
    getList() {
      this.listLoading = true
      listOrder(this.listQuery).then(res => {
        console.log(res)
        this.list = res.data.list
        this.total = res.data.total
        this.listLoading = false
      }).catch(() => {
        this.list = []
        this.total = 0
        this.listLoading = false
      })
    },
    formatType(row, column) {
      const type = row.orderType
      const [obj] = this.typeList.filter(item => item.value === type)
      return obj.label || ''
    },
    formatSubType(row, column) {
      const type = row.subType
      const [obj] = this.subTypeList.filter(item => item.value === type)
      return obj.label || ''
    },
    formatStatus(row, column) {
      const status = row.statusT
      const orderType = row.orderType
      if (status === 4) {
        return orderType === 0 ? '已入库' : '已出库'
      }
      const [obj] = this.statusList.filter(item => item.value === status)
      return obj !== undefined ? (obj.label || '') : ''
    },
    formatMoney(row) {
      return formatMoney(row.orderAmount)
    },
    formatWhOptime(row) {
      const time = row.whOpTime
      if (!time || time === 0) {
        return '-'
      }
      const date = new Date(time)
      return formatdate(date, 'yyyy-MM-dd hh:mm:ss')
    },
    formatAmount(row) {
      return formatMoney(row.amount)
    },
    handleFilter() {
      this.listQuery.page = 1
      this.getList()
    },
    resetForm() {
      this.dataForm = {}
    },
    getDenomNameList() {
      denomOption().then(res => {
        this.allDenomNameList = res.data
        this.denomNameList = res.data.filter(item => item.version === 0)
        this.badDenomNameList = res.data.filter(item => item.version === 0 || item.version === 2)
      })
    },
    normalizer(node) {
      return {
        id: node.id,
        label: node.name,
        children: node.children
      }
    },
    handleChange(value, direction, movedKeys) {
      this.atmDataForm.taskIds = value
    },
    handleDetail(row) {
      this.detailDialogFormVisible = true
      row.orderType === 1 ? this.detailDialogTitle = 'out' : this.detailDialogTitle = 'in'
      this.detailForm = Object.assign({}, row)
      listOrderDetail({ orderId: row.id }).then(res => {
        const denomList = []
        denomList.push({ text: '可用券', denomType: 0, dataList: res.data.usable })
        denomList.push({ text: '残损券', denomType: 1, dataList: res.data.bad })
        denomList.push({ text: '五好券', denomType: 2, dataList: res.data.good })
        denomList.push({ text: '未清分', denomType: 3, dataList: res.data.unclear })
        this.$set(this.detailForm, 'denomList', denomList)
        this.$set(this.detailForm, 'routeList', res.data.routeList)
        this.auditList = res.data.audit
        this.undoList = res.data.undo
      })
      const orderStepMap = { '-1': 6, '0': 1, '1': 2, '2': 2, '3': 3, '4': 4, '5': 5 }
      // 订单步骤
      this.orderStep = orderStepMap[this.detailForm.statusT]
    },
    getRouteTitle(route) {
      const routeName = route.routeName
      const taskList = route.taskList
      const routeCash = route.cashAmount ? route.cashAmount : 0
      const totalAmount = taskList != null && taskList.length > 0 ? taskList.reduce((p, e) => p + e.amount, 0) : 0
      return routeName + '   ATM任务金额： ' + formatMoney(totalAmount) + '元  线路备用金或其他金额： ' + formatMoney(routeCash) + '元'
    },
    formatDenom(id) {
      return this.allDenomNameList.find(item => item.id === +id).value
    },
    checkAmount(id, amount) {
      const value = this.allDenomNameList.find(item => item.id === +id).value
      if (value > 0) {
        return amount % value
      }
      return false
    },
    formatDenomMoney(num) {
      return formatMoney(num)
    },
    openLoading() {
      this.loading = this.$loading({
        lock: true,
        text: '正在加载...请勿进行其它操作',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      })
    },
    countChange(val, row) {
      if (val && row.denomId) {
        const value = this.badDenomNameList.find(item => item.id === row.denomId).value
        row.amount = value * val
      }
    },
    amountChange(val, row) {
      if (+val && row.denomId) {
        const value = this.badDenomNameList.find(item => item.id === row.denomId).value
        if (value !== 0) {
          row.count = Math.ceil(+val / value)
        }
      }
    },
    getDisabled(val, arr) {
      if (arr.find(item => item === val)) {
        return true
      }
      return false
    }
  }
}
</script>

<style scoped lang="scss">
  .filter-container *:nth-child(n+2) {
    margin-left: 10px;
  }
  .timeline-item span{
    margin-right: 15px;
  }
  ::v-deep .el-transfer-panel{
    width: 210px
  }
</style>
<style>
  .transfer-footer {
    margin-left: 20px;
    padding: 6px 5px;
  }
  .el-transfer__buttons{
    padding: 0 10px !important;
  }
</style>
