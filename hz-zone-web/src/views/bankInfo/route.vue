<template>
  <div class="app-container">
    <div class="filter-container">
      <el-date-picker
        v-model="listQuery.routeDate"
        value-format="timestamp"
        type="date"
        placeholder="选择日期"
        :clearable="false"
        class="filter-item"
      />
      <el-select
        v-model="listQuery.routeType"
        clearable
        placeholder="请选择线路类型"
        class="filter-item"
      >
        <el-option
          v-for="item in options.routeType"
          :key="item.code"
          :label="item.content"
          :value="item.code"
        />
      </el-select>
      <el-input
        v-model="listQuery.routeNo"
        clearable
        class="filter-item"
        style="width: 200px"
        placeholder="请输入线路编号"
        :maxlength="32"
      />
      <el-input
        v-model="listQuery.routeName"
        clearable
        class="filter-item"
        style="width: 200px"
        placeholder="请选择线路名称"
        :maxlength="32"
      />
      <el-button
        v-permission="['/base/bankInquiry/routeList']"
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
      :expand-list="expandList"
    >
      <template v-slot:operate>
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
      </template>
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
  listRoute,
  routeEmpChange
} from '@/api/bankInfo/route'
import { formatdate } from '@/utils/date'
import { dictionaryData } from '@/api/system/dictionary'

export default {
  components: { myTable },
  data() {
    return {
      list: [],
      listQuery: {
        routeName: null,
        routeNo: null,
        routeType: null,
        routeDate: new Date(new Date().toLocaleDateString()).getTime()
      },
      options: {
        routeType: [
          { code: 0, content: '固定线路' },
          { code: 1, content: '临时线路' }
        ],
        templateType: [
          { code: 0, content: '离行式线路' },
          { code: 1, content: '附行式线路' }
        ],
        jobType: [
          { code: 0, content: '其它' },
          { code: 1, content: '司机岗' },
          { code: 2, content: '护卫岗' },
          { code: 3, content: '钥匙岗' },
          { code: 4, content: '密码岗' },
          { code: 5, content: '清点岗' },
          { code: 6, content: '库管岗' }
        ]
      },
      departmentId: null,
      listLoading: true,
      total: 0,
      authOption: [],
      tableList: [
        {
          label: '线路编号',
          prop: 'routeNo',
          width: 80
        },
        {
          label: '线路名称',
          prop: 'routeName',
          width: 120
        },
        {
          label: '线路类型',
          prop: 'routeType',
          width: 100,
          formatter: this.formatType
        },
        {
          label: '模板类型',
          prop: 'templateType',
          width: 100,
          formatter: this.formatTemplateType
        },
        {
          label: '分配车辆',
          prop: 'seqno',
          width: 140,
          formatter: this.formatSeqno
        },
        {
          label: '任务日期',
          prop: 'routeDate',
          formatter: this.formatDate,
          width: 120
        },
        // {
        //   label: '计划开始时间',
        //   prop: 'planBeginTime',
        //   formatter: this.formatTime
        // },
        // {
        //   label: '计划结束时间',
        //   prop: 'planFinishTime',
        //   formatter: this.formatTime
        // },
        {
          label: '实际开始时间',
          prop: 'actBeginTime',
          formatter: this.formatTime
        },
        {
          label: '实际结束时间',
          prop: 'actFinishTime',
          formatter: this.formatTime
        },
        {
          label: '状态',
          prop: 'statusT',
          formatter: this.formatStatus
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
          label: '业务员',
          prop: 'routeKeyManName',
          formatter: this.formatName
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
      dictionaryData,
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
    this.getList()
  },
  methods: {
    getList(data) {
      if (data) {
        for (const key in data) {
          this.listQuery[key] = data[key]
        }
      }
      listRoute(this.listQuery).then((res) => {
        const data = res.data
        this.list = data
        this.departmentId = this.listQuery.departmentId
        this.total = data.total
        this.listLoading = false
      })
    },
    handleDetail(row) {
      routeEmpChange(row.id).then((res) => {
        this.detailFormVisible = true
        this.detailList = res.data
      })
    },
    formatMoney,
    formatName(name, row) {
      return `${name}、${row.routeOperManName}`
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
    formatCleanStatus(type) {
      for (const item of dictionaryData['CLEAN_STATUS']) {
        if (type === item.code) {
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
    }
  }
}
</script>

<style scoped lang="scss">
.filter-container *:nth-child(n + 2) {
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
