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
      <el-select v-model="listQuery.bankId" filterable placeholder="请选择银行" clearable class="filter-item" style="width: 150px">
        <el-option
          v-for="item in bankSearchOption"
          :key="item.id"
          :label="item.name"
          :value="item.id"
        />
      </el-select>
      <el-date-picker
        v-model="listQuery.time"
        type="daterange"
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        value-format="timestamp"
      />
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="getList">查找</el-button>
    </div>
    <my-table
      :list-loading="listLoading"
      :data-list="list"
      :table-list="tableList"
      :expand-list="expandList"
    />
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
import Pagination from '@/components/Pagination'
import myTable from '@/components/Table'
import { formatdate } from '@/utils/date'
import { formatMoney } from '@/utils/convert'
import { listCheck } from '@/api/vault/check'
import { bankTradeOption, authOption } from '@/api/common/selectOption'
export default {
  components: { Pagination, myTable },
  data() {
    return {
      list: [],
      depOptions: [],
      bankSearchOption: [],
      treeOptions: [],
      listQuery: {
        limit: 10,
        page: 1,
        departmentId: null,
        bankId: null,
        time: []
      },
      listLoading: true,
      total: 0,
      role: {
        list: '/base/vaultCheck/list'
      },
      tableList: [
        {
          label: '机构名称',
          prop: 'bankName',
          width: 140
        },
        {
          label: '盘点时间',
          prop: 'whOpTime',
          width: 120,
          formatter: this.formatDateTime
        },
        {
          label: '可用券余额',
          prop: 'usableBalance',
          width: 140,
          formatter: this.formatMoney
        },
        {
          label: '残损券余额',
          prop: 'badBalance',
          width: 140,
          formatter: this.formatMoney
        },
        {
          label: '五好券余额',
          prop: 'goodBalance',
          width: 140,
          formatter: this.formatMoney
        },
        {
          label: '未清分余额',
          prop: 'unclearBalance',
          width: 140,
          formatter: this.formatMoney
        },
        {
          label: '尾零余额',
          prop: 'remnantBalance',
          width: 140,
          formatter: this.formatMoney
        },
        {
          label: '余额总计',
          width: 140,
          formatter: this.formatBalance
        },
        {
          label: '可用券清点金额',
          prop: 'usableAmount',
          width: 140,
          formatter: this.formatMoney
        },
        {
          label: '残损券清点金额',
          prop: 'badAmount',
          width: 140,
          formatter: this.formatMoney
        },
        {
          label: '五好券清点金额',
          prop: 'goodAmount',
          width: 140,
          formatter: this.formatMoney
        },
        {
          label: '未清分清点金额',
          prop: 'unclearAmount',
          width: 140,
          formatter: this.formatMoney
        },
        {
          label: '尾零清点金额',
          prop: 'remnantAmount',
          width: 140,
          formatter: this.formatMoney
        },
        {
          label: '清点金额总计',
          width: 140,
          formatter: this.formatAmount
        }
      ],
      expandList: [
        {
          label: '查库人1',
          prop: 'whOpManName'
        },
        {
          label: '查库人2',
          prop: 'whCheckManName'
        },
        {
          label: '查库人3',
          prop: 'whConfirmManName'
        },
        {
          label: '备注',
          prop: 'comments'
        }
      ]
    }
  },
  mounted() {
    // this.getList()
    this.getOptions()
  },
  methods: {
    getList() {
      const { time, ...newForm } = this.listQuery
      if (time && time.length > 0) {
        newForm.beginDate = time[0]
        newForm.endDate = time[1]
      }
      listCheck(newForm).then((res) => {
        const data = res.data
        this.list = data.list
        this.total = data.total
        this.listLoading = false
      })
    },
    getOptions() {
      authOption().then(res => {
        this.depOptions = res.data
        if (this.depOptions.length > 0) {
          this.listQuery.departmentId = this.depOptions[0].id
          this.getList()
          bankTradeOption(this.listQuery.departmentId, 4).then(res => {
            this.bankSearchOption = res.data
          })
        }
      })
    },
    normalizer(node) {
      return {
        id: node.id,
        label: node.name,
        children: node.children
      }
    },
    departmentChange(value) {
      this.listQuery.bankId = null
      bankTradeOption(value, 4).then(res => {
        this.bankSearchOption = res.data
      })
    },
    formatDateTime(timestamp) {
      if (!timestamp || timestamp === 0) {
        return '-'
      }
      const date = new Date(timestamp)
      return formatdate(date, 'yyyy-MM-dd')
    },
    formatMoney,
    formatBalance(none, row) {
      const balanace = +row.usableBalance + +row.badBalance + +row.goodBalance + +row.unclearBalance + row.remnantBalance
      return `<span style="color: red">${formatMoney(balanace)}</span>`
    },
    formatAmount(none, row) {
      const amount = +row.usableAmount + +row.badAmount + +row.goodAmount + +row.unclearAmount + row.remnantAmount
      return `<span style="color: red">${formatMoney(amount)}</span>`
    }
  }
}
</script>

<style scoped lang="scss">
  .filter-container *:nth-child(n+2) {
    margin-left: 10px;
  }
</style>
