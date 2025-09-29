<template>
  <div class="app-container">
    <div class="filter-container">
      <el-select
        v-model="listQuery.departmentId"
        clearable
        placeholder="请选择所属部门"
        class="filter-item"
        @change="departmentChange"
      >
        <el-option
          v-for="item in depOption"
          :key="item.id"
          :label="item.name"
          :value="item.id"
        />
      </el-select>
      <el-select
        v-model="listQuery.bankId"
        placeholder="所属机构"
        style="width: 200px"
        filterable
        clearable
        class="filter-item"
      >
        <el-option
          v-for="item in bankOption"
          :key="item.id"
          :label="item.name"
          :value="item.id"
        />
      </el-select>
      <el-radio-group v-model="timeType" class="filter-item">
        <el-radio-button label="year">年份</el-radio-button>
        <el-radio-button label="month">月份</el-radio-button>
        <el-radio-button label="date">日期</el-radio-button>
        <el-radio-button label="daterange">区间</el-radio-button>
      </el-radio-group>
      <el-date-picker
        v-if="timeType === 'daterange'"
        key="daterange"
        v-model="dateArr"
        value-format="yyyy-MM-dd"
        type="daterange"
        placeholder="选择区间"
        :clearable="false"
        class="filter-item"
      />
      <el-date-picker
        v-else
        key="date"
        v-model="pickDate"
        value-format="yyyy-MM-dd"
        :type="timeType"
        placeholder="选择时间"
        :clearable="false"
        class="filter-item"
      />
      <el-button
        class="filter-item"
        type="primary"
        icon="el-icon-search"
        @click="getList()"
      >查找</el-button>
    </div>
    <my-table
      :list-loading="listLoading"
      :data-list="list"
      :table-list="tableList"
    >
      <!-- <template v-slot:operate>
        <el-table-column
          align="center"
          label="人员调整"
          class-name="small-padding fixed-width"
        >
          <template slot-scope="scope">
            <el-button
              v-if="scope.row.empChange === 1"
              v-permission="['/base/bankInquiry/routeEmpChange']"
              type="primary"
              plain
              size="mini"
              @click="handleDetail(scope.row)"
            >查看详情</el-button>
          </template>
        </el-table-column>
      </template> -->
    </my-table>
    <!-- 清机任务详情 -->
    <el-dialog
      title="详情"
      :visible.sync="detailFormVisible"
      :close-on-click-modal="false"
      width="80%"
    >
      <div>
        <my-table
          ref="myTable"
          :list-loading="deatilListLoading"
          :data-list="detailList"
          :table-list="deatilTableList"
          :height="'300'"
        />
      </div>
    </el-dialog>

    <el-dialog
      :visible.sync="cashboxVisible"
      width="40%"
    >
      <el-form
        :model="cashboxForm"
        label-width="100px"
        inline
      >
        <el-form-item label="装盒时间" style="width: 50%">
          <span>{{ formatDateTime(cashboxForm.packTime) }}</span>
        </el-form-item>
        <el-form-item label="任务日期" style="width: 50%">
          <span>{{ formatDate(cashboxForm.taskDate) }}</span>
        </el-form-item>
        <el-form-item label="清分员" style="width: 50%">
          <span>{{ cashboxForm.clearManName }}</span>
        </el-form-item>
        <el-form-item label="复核员" style="width: 50%">
          <span>{{ cashboxForm.checkManName }}</span>
        </el-form-item>
        <el-form-item label="清分机" style="width: 50%">
          <span>{{ cashboxForm.deviceNo }}</span>
        </el-form-item>
        <el-form-item label="券别" style="width: 50%">
          <span>{{ cashboxForm.denomName }}</span>
        </el-form-item>
        <el-form-item label="包装金额" style="width: 50%">
          <span>{{ formatMoney(cashboxForm.amount) }}</span>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script>
import { formatMoney } from '@/utils/convert'
import myTable from '@/components/Table'
import {
  listClearReport
} from '@/api/cleanCheck/cleanReport'
import { formatdate } from '@/utils/date'
import { authOption, bankTradeOption } from '@/api/common/selectOption'

export default {
  components: { myTable },
  data() {
    return {
      depOption: [],
      bankOption: [],
      list: [],
      timeType: 'month',
      pickDate: `${new Date().getFullYear()}-${new Date().getMonth() + 1}-01`,
      dateArr: [`${new Date().getFullYear()}-${new Date().getMonth() + 1}-01`, `${new Date().getFullYear()}-${new Date().getMonth() + 1}-01`],
      listQuery: {
        departmentId: null,
        year: new Date().getFullYear(),
        month: new Date().getMonth() + 1,
        day: null,
        beginDay: null,
        endDay: null
      },
      options: {
      },
      listLoading: true,
      total: 0,
      tableList: [
        {
          label: '所属机构',
          prop: 'bankName'
        },
        {
          label: '券别类型',
          prop: 'denomType',
          formatter: this.formatDenom
        },
        {
          label: '券别',
          prop: 'denomName'
        },
        {
          label: '清分量（捆）',
          prop: 'bundles'
        },
        {
          label: '单价',
          prop: 'price',
          formatter: this.formatMoney
        },
        {
          label: '费用',
          prop: 'feeAmount',
          formatter: this.formatMoney
        }
      ],
      expandList: [
        {
          label: '司机',
          prop: 'driverName'
        },
        {
          label: '复核时间',
          prop: 'dispCfmTime',
          formatter: this.formatTime
        },
        {
          label: '领取钞袋',
          prop: 'dispBagCount'
        },
        {
          label: '护卫',
          prop: 'securityAName',
          formatter: this.formatSecurity
        },
        {
          label: '交接人员',
          prop: 'hdoverOperManName',
          formatter: this.formatHdover
        },
        {
          label: '跟车人员',
          prop: 'followerName'
        },
        {
          label: '交接时间',
          prop: 'hdoverTime',
          formatter: this.formatTime
        },
        {
          label: '配钞员',
          prop: 'dispOperManName',
          formatter: this.formatDisp
        },
        {
          label: '交接钞袋',
          prop: 'returnBagCount'
        },
        {
          label: '配钞时间',
          prop: 'dispTime',
          formatter: this.formatTime
        },
        {
          label: '交接钞盒',
          prop: 'returnBoxCount'
        }
      ],
      copyRow: null,
      dataForm: {
        departmentId: null,
        follower: null,
        driver: null,
        id: null,
        // planBeginTime: null,
        // planFinishTime: null,
        routeDate: null,
        routeKeyMan: null,
        routeName: null,
        routeNo: null,
        routeOperMan: null,
        securityA: null,
        securityB: null,
        comments: null,
        vehicleId: null,
        templateType: 0
      },
      isDone: false,
      // 详情
      activeNames: ['1'],
      detailFormVisible: false,
      deatilListLoading: false,
      detailList: [],
      deatilTableList: [
        {
          label: '岗位类型',
          prop: 'jobType',
          formatter: this.formatJobTYpe
        },
        {
          label: '变更前人员',
          prop: 'oldManName'
        },
        {
          label: '变更后人员',
          prop: 'newManName'
        },
        {
          label: '备注',
          prop: 'comments'
        },
        {
          label: '调整时间',
          prop: 'createTime',
          formatter: this.formatDateTime
        }
      ],
      atmTaskCheck: {
        checkItemResult: {}
      },
      atmTaskClean: null,
      atmTaskRepair: null,
      cashboxVisible: false,
      cashboxForm: {},
      loading: null
    }
  },
  mounted() {
    authOption().then(res => {
      this.depOption = res.data
      if (this.depOption.length > 0) {
        this.listQuery.departmentId = this.depOption[0].id
        bankTradeOption(this.listQuery.departmentId, 3).then(res => {
          this.bankOption = res.data
        })
        this.getList()
      }
    })
  },
  methods: {
    getList(data) {
      if (data) {
        for (const key in data) {
          this.listQuery[key] = data[key]
        }
      }
      if (this.pickDate || this.dateArr.length > 0) {
        const arr = this.pickDate.split('-')
        console.log(this.timeType)
        switch (this.timeType) {
          case 'year':
            this.listQuery.year = arr[0]
            this.listQuery.month = this.listQuery.day = this.listQuery.beginDay = this.listQuery.endDay = null
            break
          case 'month':
            this.listQuery.year = arr[0]
            this.listQuery.month = arr[1]
            this.listQuery.day = this.listQuery.beginDay = this.listQuery.endDay = null
            break
          case 'date':
            this.listQuery.year = arr[0]
            this.listQuery.month = arr[1]
            this.listQuery.day = arr[2]
            this.listQuery.beginDay = this.listQuery.endDay = null
            break
          case 'daterange':
            this.listQuery.beginDay = this.dateArr[0]
            this.listQuery.endDay = this.dateArr[1]
            this.listQuery.month = this.listQuery.day = this.listQuery.year = null
        }
      }
      console.log(this.listQuery)
      listClearReport(this.listQuery).then((res) => {
        const data = res.data
        this.list = data
        this.departmentId = this.listQuery.departmentId
        this.total = data.total
        this.listLoading = false
      })
    },
    // handleDetail(row) {
    //   routeEmpChange(row.id).then((res) => {
    //     this.detailFormVisible = true
    //     this.detailList = res.data
    //   })
    // },
    formatMoney,
    formatDenom(type) {
      switch (type) {
        case 1:
          return '残损券'
        case 2:
          return '完整券'
      }
    },
    formatSecurity(name, row) {
      return `${name}、${row.securityBName}`
    },
    formatHdover(name, row) {
      return row.hdoverCheckManName === '' ? name : `${name}、${row.hdoverCheckManName}`
    },
    formatDisp(name, row) {
      return row.dispCheckManName === '' ? name : `${name}、${row.dispCheckManName}`
    },
    formatType(type) {
      switch (type) {
        case 0:
          return '固定线路'
        case 1:
          return '临时线路'
      }
    },
    formatTemplateType(type) {
      return this.options.templateType.filter(item => item.code === type)[0].content
    },
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
    formatDateMinute(timestamp) {
      if (!timestamp || timestamp === 0) {
        return '00:00'
      }
      const date = new Date(timestamp)
      return formatdate(date, 'hh:mm')
    },
    formatSeqno(seqno, row) {
      if (seqno === '') {
        return '-'
      } else {
        return `${seqno}/${row.lpno}`
      }
    },
    formatStatus(status) {
      switch (status) {
        case 0:
          return '<span style="color:#d12e22">新建</span>'
        case 1:
          return '<span style="color:#dfe000">已审核</span>'
        case 2:
          return '<span style="color:#aa6bbb">配钞完成</span>'
        case 3:
          return '<span style="color:#119fff">任务中...</span>'
        case 4:
          return '<span style="color:green">交接完成</span>'
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
    formatJobTYpe(type) {
      return this.options.jobType.find(item => item.code === type).content
    },
    openLoading() {
      this.loading = this.$loading({
        lock: true,
        text: '正在加载...请勿进行其它操作',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      })
    },
    departmentChange(value) {
      this.openLoading()
      bankTradeOption(value, 3).then((res) => {
        this.bankOption = res.data
        this.loading.close()
        this.listQuery.bankId = null
      })
    },
    timeChange(value) {
      switch (value) {
        case 'year':
      }
    }
  }
}
</script>

<style scoped lang="scss">
.filter-container > *:nth-child(n + 2) {
  margin-left: 10px;
}

.box-collapse {
  margin-top: 40px;
  ::v-deep .el-collapse-item__header{
    font-size: 18px;
    height: 60px;
    line-height: 60px;
    font-weight: 600;
    padding-left: 20px;
  }
}

.el-dialog__wrapper {
  ::v-deep .el-dialog__body {
    padding: 20px;
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
