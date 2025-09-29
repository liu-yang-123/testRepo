<template>
  <div class="app-container">
    <div class="filter-container">
      <el-button class="filter-item" type="primary" icon="el-icon-refresh" @click="getList">刷新</el-button>
    </div>
    <my-table
      :list-loading="listLoading"
      :data-list="list"
      :table-list="tableList"
      is-summary
      @getSummaries="getSummaries"
    >
      <template v-slot:operate>
        <el-table-column align="center" label="明细" width="100">
          <template slot-scope="scope">
            <el-button
              v-permission="['/base/bankInquiry/bankDenom']"
              plain
              type="primary"
              size="mini"
              @click="handleDetail(scope.row)"
            >打开</el-button>
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

    <!-- 明细框 -->
    <el-dialog title="银行机构券别明细" :visible.sync="detailDialogFormVisible" width="65%" :close-on-click-modal="false">

      <el-divider>可用券</el-divider>
      <el-table
        :data="bankDenomList[0]"
        show-summary
        :summary-method="getSummaries"
        :header-cell-style="{background:'#eef1f6',color:'#606266'}"
        style="width: 100%"
      >
        <el-table-column
          prop="denomName"
          label="面额"
        />
        <el-table-column
          prop="amount"
          label="金额(元)"
          :formatter="((row) => {return formatMoney(row.amount)})"
        />
        <el-table-column label="张数">
          <template slot-scope="scope">
            <div v-if="scope.row.denomType === 1">{{ scope.row.count }}</div>
            <div v-else>——</div>
          </template>
        </el-table-column>
      </el-table>

      <el-divider>残损券</el-divider>
      <el-table
        :data="bankDenomList[1]"
        show-summary
        :summary-method="getBadSummaries"
        :header-cell-style="{background:'#eef1f6',color:'#606266'}"
        style="width: 100%"
      >
        <el-table-column
          prop="denomName"
          label="面额"
        />
        <el-table-column
          prop="amount"
          label="金额(元)"
          :formatter="((row) => {return formatMoney(row.amount)})"
        />
        <el-table-column label="张数" prop="count">
          <template slot-scope="scope">
            <div v-if="scope.row.denomType === 1">{{ scope.row.count }}</div>
            <div v-else>——</div>
          </template>
        </el-table-column>
      </el-table>

      <el-divider>五好券</el-divider>
      <el-table
        :data="bankDenomList[2]"
        show-summary
        :summary-method="getSummaries"
        :header-cell-style="{background:'#eef1f6',color:'#606266'}"
        style="width: 100%"
      >
        <el-table-column
          prop="denomName"
          label="面额"
        />
        <el-table-column
          prop="amount"
          label="金额(元)"
          :formatter="((row) => {return formatMoney(row.amount)})"
        />
        <el-table-column label="张数">
          <template slot-scope="scope">
            <div v-if="scope.row.denomType === 1">{{ scope.row.count }}</div>
            <div v-else>——</div>
          </template>
        </el-table-column>
      </el-table>

      <el-divider>未清分券</el-divider>
      <el-table
        :data="bankDenomList[3]"
        show-summary
        :summary-method="getSummaries"
        :header-cell-style="{background:'#eef1f6',color:'#606266'}"
        style="width: 100%"
      >
        <el-table-column
          prop="denomName"
          label="面额"
        />
        <el-table-column
          prop="amount"
          label="金额(元)"
          :formatter="((row) => {return formatMoney(row.amount)})"
        />
        <el-table-column label="张数">
          <template slot-scope="scope">
            <div v-if="scope.row.denomType === 1">{{ scope.row.count }}</div>
            <div v-else>——</div>
          </template>
        </el-table-column>
      </el-table>

      <div slot="footer" class="dialog-footer">
        <el-button @click="detailDialogFormVisible = false">关闭</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import { formatMoney } from '@/utils/convert'
import Pagination from '@/components/Pagination'
import myTable from '@/components/Table'
import { listVolume, bankDenom } from '@/api/bankInfo/volume'
export default {
  components: { Pagination, myTable },
  data() {
    return {
      list: [],
      bankDenomList: {},
      depOptions: [],
      detailDialogFormVisible: false,
      bankSearchOption: [],
      denomTypeList: [{ code: 0, content: '可用券' }, { code: 1, content: '残损券' }, { code: 2, content: '五好券' }, { code: 3, content: '未清分' }],
      listQuery: {
        limit: 10,
        page: 1,
        departmentId: null,
        bankId: null
      },
      listLoading: true,
      total: 0,
      role: {
        list: '/base/vaultVolume/list'
      },
      tableList: [
        {
          label: '机构名称',
          prop: 'bankName'
        },
        {
          label: '金额',
          prop: 'amount',
          formatter: this.formatMoney
        }
      ]
    }
  },
  mounted() {
    this.getList()
  },
  methods: {
    formatMoney,
    getList() {
      listVolume(this.listQuery).then((res) => {
        const data = res.data
        this.list = data.list
        this.total = data.total
        this.listLoading = false
      })
    },
    normalizer(node) {
      return {
        id: node.id,
        label: node.name,
        children: node.children
      }
    },
    handleDetail(row) {
      this.detailDialogFormVisible = true
      this.detailForm = Object.assign({}, row)
      bankDenom({ bankId: row.bankId }).then(res => {
        this.bankDenomList = res.data
      })
    },
    getSummaries(param, cb) {
      const { columns, data } = param
      const sums = []
      columns.forEach((column, index) => {
        if (index === 0) {
          sums[index] = '合计'
          return
        }
        const values = data.map((item) => Number(item[column.property]))
        if (index === 1 && !values.every((value) => isNaN(value))) {
          const total = values.reduce((prev, curr) => {
            const value = Number(curr)
            if (!isNaN(value)) {
              return prev + curr
            } else {
              return prev
            }
          }, 0)
          sums[index] = formatMoney(total)
        } else {
          sums[index] = '——'
        }
      })
      cb(sums)
    },
    getBadSummaries(param) {
      const { columns, data } = param
      const sums = []
      columns.forEach((column, index) => {
        if (index === 0) {
          sums[index] = '合计'
          return
        }
        const values = data.map((item) => Number(item[column.property]))
        console.log(column.property)
        if (index === 1 && !values.every((value) => isNaN(value))) {
          const total = values.reduce((prev, curr) => {
            const value = Number(curr)
            if (!isNaN(value)) {
              return prev + curr
            } else {
              return prev
            }
          }, 0)
          sums[index] = formatMoney(total)
        } else if (index === 2) {
          const total = values.reduce((prev, curr) => {
            console.log(curr)
            const value = Number(curr)
            if (!isNaN(value)) {
              return prev + curr
            } else {
              return prev
            }
          }, 0)
          sums[index] = total
        } else {
          sums[index] = ''
        }
      })

      return sums
    }
  }
}
</script>

<style scoped lang="scss">
  .filter-container *:nth-child(n+2) {
    margin-left: 10px;
  }
</style>
