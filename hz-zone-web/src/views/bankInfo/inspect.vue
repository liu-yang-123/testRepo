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
              v-permission="['/base/bankInquiry/atmTask']"
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

    <el-dialog title="详情" :visible.sync="dialogFormVisible">
      <div style="padding-left:6%">
        <div class="title">加钞间</div>
        <el-form label-position="right" inline class="confirm-info">
          <el-form-item label="门窗、锁、门禁是否正常">
            <i :class="detailForm.roomDoor ? 'el-icon-check' : 'el-icon-close'" />
          </el-form-item>
          <el-form-item label="消防设备是否完好">
            <i :class="detailForm.roomFireEqt ? 'el-icon-check' : 'el-icon-close'" />
          </el-form-item>
          <el-form-item label="空调灯具是否正常">
            <i :class="detailForm.roomlight ? 'el-icon-check' : 'el-icon-close'" />
          </el-form-item>
        </el-form>
        <div class="title">取款大厅</div>
        <el-form label-position="right" inline class="confirm-info">
          <el-form-item label="防护舱门、锁、插销是否完好">
            <i :class="detailForm.hallLock ? 'el-icon-check' : 'el-icon-close'" />
          </el-form-item>
          <el-form-item label="自助银行大门是否完好">
            <i :class="detailForm.hallDoor ? 'el-icon-check' : 'el-icon-close'" />
          </el-form-item>
          <!-- <el-form-item label="报警按钮、对讲是否正常">
            <i :class="detailForm.hallSpeaker ? 'el-icon-check' : 'el-icon-close'" />
          </el-form-item> -->
          <el-form-item label="环境是否整洁">
            <i :class="detailForm.hallEmv ? 'el-icon-check' : 'el-icon-close'" />
          </el-form-item>
        </el-form>
      </div>

      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import Pagination from '@/components/Pagination'
import myTable from '@/components/Table'
import { formatdate } from '@/utils/date'
import searchBar from '@/components/SearchBar'
import { listInspect } from '@/api/bankInfo/inspect'
import { dictionaryData } from '@/api/system/dictionary'
import { subBankOption } from '@/api/common/selectOption'
export default {
  components: { myTable, searchBar, Pagination },
  data() {
    return {
      options: {
        checkResult: [
          { code: 0, content: '正常' },
          { code: 1, content: '异常' }
        ],
        subBankId: []
      },
      list: [],
      total: 0,
      listQuery: {
        page: 1,
        limit: 10,
        date: null,
        beginDate: null,
        endDate: null,
        subBankId: null,
        checkResult: null
      },
      listLoading: true,
      searchList: [
        { name: 'date', type: 6 },
        { name: 'subBankId', label: '银行网点', type: 3 },
        { name: 'checkResult', label: '巡检结果', type: 3 }
      ],
      role: {
        list: '/base/bankInquiry/routeMonitor'
      },
      tableList: [
        {
          label: '线路编号',
          prop: 'routeNo',
          formatter: this.formatRouuteNo
        },
        {
          label: '网点',
          prop: 'subBankName'
        },
        {
          label: '巡检时间',
          prop: 'checkTime',
          formatter: this.formatDate
        },
        {
          label: '撤防时间',
          prop: 'revokeAlarmTime',
          formatter: this.formatDateTime
        },
        {
          label: '布防时间',
          prop: 'setAlarmTime',
          formatter: this.formatDateTime
        },
        {
          label: '备注',
          prop: 'comments'
        },
        {
          label: '巡检结果',
          prop: 'checkNormal',
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
      detailForm: {}
    }
  },
  mounted() {
    this.getList()
    this.getBankOption()
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
      listInspect(params).then((res) => {
        this.list = res.data.list
        this.total = res.data.total
        this.listLoading = false
      })
    },
    getBankOption() {
      subBankOption().then(res => {
        this.options.subBankId = res.data
      })
    },
    handleDetail(row) {
      this.dialogFormVisible = true
      this.detailForm = { ...row.hallCheckResults, ...row.roomCheckResults }
    },
    formatRouuteNo(num) {
      if (num) {
        return `${num}号线`
      }
      return '-'
    },
    formatStatus(statusT) {
      switch (statusT) {
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
      return formatdate(date, 'yyyy-MM-dd hh:mm:ss')
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
    formatCheckResult(num) {
      switch (num) {
        case 0:
          return '正常'
        case 1:
          return '<span style="color: red">异常</span>'
      }
    },
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

.title {
  font-size: 16px;
  font-weight: 600;
  margin: 20px 0;
}

.confirm-info {
  i{
    font-size: 20px;
    vertical-align: middle;
    font-weight: 600;
  }
  .el-icon-check {
    color: rgb(82, 240, 82);
  }
  .el-icon-close {
    color: rgb(248, 51, 51);
  }
}

.el-form--inline .el-form-item {
  margin-right: 0;
  margin-bottom: 8px;
  width: 50%;
  ::v-deep .el-form-item__label {
    width: 50%;
    color: #99a9bf;
    margin-right: 24px;
  }
}
</style>
