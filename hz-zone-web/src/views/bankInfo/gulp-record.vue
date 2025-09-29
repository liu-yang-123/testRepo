<template>
  <div class="app-container">
    <el-tabs v-model="activeName" @tab-click="tabClick">
      <el-tab-pane label="送卡" name="first">
        <div class="filter-container">
          <el-date-picker
            v-model="listQuery.queryDay"
            value-format="timestamp"
            type="date"
            placeholder="选择送卡日期"
            class="filter-item"
            clearable
          />
          <el-input
            v-model="listQuery.routeNo"
            clearable
            class="filter-item"
            style="width: 200px"
            placeholder="请输入送卡线路"
            :maxlength="32"
          />
          <el-select
            v-model="listQuery.statusT"
            placeholder="请选择状态"
            class="filter-item"
            clearable
          >
            <el-option
              v-for="item in options.statusT"
              :key="item.code"
              :label="item.content"
              :value="item.code"
            />
          </el-select>
          <el-input
            v-model="listQuery.cardNo"
            clearable
            class="filter-item"
            style="width: 200px"
            placeholder="请输入银行卡号"
            :maxlength="32"
          />
          <el-button
            class="filter-item"
            type="primary"
            icon="el-icon-search"
            @click="listQuery.page = 1;getList()"
          >查找</el-button>
        </div>
      </el-tab-pane>
      <el-tab-pane label="回笼" name="second">
        <div class="filter-container">
          <el-date-picker
            v-model="listQuery.queryDay"
            value-format="timestamp"
            type="date"
            placeholder="选择回笼日期"
            class="filter-item"
            clearable
          />
          <el-input
            v-model="listQuery.routeNo"
            clearable
            class="filter-item"
            style="width: 200px"
            placeholder="请输入回笼线路"
            :maxlength="32"
          />
          <el-select
            v-model="listQuery.statusT"
            placeholder="请选择状态"
            class="filter-item"
            clearable
          >
            <el-option
              v-for="item in options.statusT"
              :key="item.code"
              :label="item.content"
              :value="item.code"
            />
          </el-select>
          <el-input
            v-model="listQuery.cardNo"
            clearable
            class="filter-item"
            style="width: 200px"
            placeholder="请输入银行卡号"
            :maxlength="32"
          />
          <el-button
            class="filter-item"
            type="primary"
            icon="el-icon-search"
            @click="getList()"
          >查找</el-button>
        </div>
      </el-tab-pane>
    </el-tabs>
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
import { listCard } from '@/api/bankInfo/gulpRecord'
export default {
  components: { myTable, Pagination },
  data() {
    return {
      activeName: 'first',
      oldName: 'first',
      options: {
        routeType: [
          { code: 0, content: '固定线路' },
          { code: 1, content: '临时线路' }
        ],
        statusT: [
          { code: 0, content: '取回' },
          { code: 1, content: '入库' },
          { code: 2, content: '分配' },
          { code: 3, content: '派送' },
          { code: 4, content: '领取' }
        ]
      },
      total: 0,
      loading: null,
      list: [],
      listQuery: {
        limit: 10,
        page: 1,
        statusT: null,
        atmTerNo: null,
        queryDay: null,
        routeNo: null,
        queryType: 1,
        cardNo: null
      },
      cardList: [],
      listLoading: true,
      expandList: [
        {
          label: '交接人',
          prop: 'collectManAName'
        },
        {
          label: '交接时间',
          prop: 'collectTime',
          formatter: this.formatDateTime
        },
        {
          label: '配卡人',
          prop: 'dispatchManAName'
        },
        {
          label: '配卡时间',
          prop: 'dispatchTime',
          formatter: this.formatDateTime
        },
        {
          label: '派送方式',
          prop: 'deliverType',
          formatter: this.formatDelType
        },
        {
          label: '移交人证件号码',
          prop: 'receiverIdno'
        },
        {
          label: '移交人姓名',
          prop: 'receiverName'
        },
        {
          label: '移交时间',
          prop: 'receiveTime',
          formatter: this.formatDateTime
        },
        {
          label: '备注',
          prop: 'comments'
        }
      ],
      tableList: [
        {
          label: '取卡线路',
          prop: 'routeNo',
          formatter: this.formatRouteNo
        },
        {
          label: '类别',
          prop: 'category',
          formatter: this.formatCategory
        },
        {
          label: '设备编号',
          prop: 'atmTerNo'
        },
        {
          label: '吞卡卡号',
          prop: 'cardNo',
          width: 200
        },
        {
          label: '发卡行',
          prop: 'cardBank'
        },
        {
          label: '送卡线路',
          prop: 'deliverRouteNo',
          formatter: this.formatRouteNo
        },
        {
          label: '交卡网点',
          prop: 'deliverBankName'
        },
        {
          label: '上交日期',
          prop: 'deliverDay'
        },
        {
          label: '状态',
          prop: 'statusT',
          width: 100,
          formatter: this.formatStatusT
        },
        {
          label: '取卡方式',
          prop: 'deliverType',
          formatter: this.formatDeliverType
        }
      ],
      dialogFormVisible: false,
      dataForm: {
        id: null,
        deliverDay: null,
        deliverBankId: null,
        deliverRouteNo: null
      },
      rules: {
        deliverBankId: [
          { required: true, message: '送卡网点不能为空', trigger: 'blur' }
        ],
        deliverDay: [
          { required: true, message: '日期不能为空', trigger: 'blur' }
        ],
        deliverRouteNo: [
          { required: true, message: '送卡线路不能为空', trigger: 'blur' }
        ]
      },
      assignFormVisible: false,
      assignForm: {
        deliverDay: null
      },
      categoryOption: [
        { code: 0, content: '银行卡' },
        { code: 1, content: '回执单' }
      ],
      assignRules: {
        deliverDay: [
          { required: true, message: '日期不能为空', trigger: 'blur' }
        ]
      },
      pickerOptions: {
        disabledDate(time) {
          return time.getTime() <= Date.now() - 8.64e7
        }
      },
      subOption: [],
      // 添加
      addFormVisible: false,
      routeOption: [],
      taskOption: [],
      addForm: {
        taskDate: new Date((new Date()).toLocaleDateString()).getTime(),
        routeId: null,
        routeNo: null,
        taskId: null,
        cardBank: null,
        cardNo: null,
        category: 0
      },
      addRules: {
        routeId: [
          { required: true, message: '线路编号不能为空', trigger: 'blur' }
        ],
        taskId: [
          { required: true, message: 'ATM任务不能为空', trigger: 'blur' }
        ],
        cardBank: [
          { required: true, message: '发卡行不能为空', trigger: 'blur' }
        ],
        cardNo: [
          { required: true, message: '银行卡号不能为空', trigger: 'blur' }
        ],
        category: [
          { required: true, message: '类型不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  mounted() {
    this.getList()
  },
  methods: {
    tabClick(tab) {
      if (tab.name !== this.oldName) {
        this.oldName = tab.name
        tab.name === 'first' ? this.listQuery.queryType = 1 : this.listQuery.queryType = 0
        this.listQuery.page = 1
        this.listQuery.queryDay = this.listQuery.routeNo = null
        this.getList()
      }
    },
    getList(data) {
      if (data) {
        for (const key in data) {
          this.listQuery[key] = data[key]
        }
      }
      listCard(this.listQuery).then((res) => {
        this.list = res.data.list
        this.total = res.data.total
        this.listLoading = false
      })
    },
    formatDate(timestamp) {
      if (!timestamp || timestamp === 0) {
        return ''
      }
      const date = new Date(timestamp)
      return formatdate(date, 'yyyy年MM月dd日')
    },
    formatDateTime(timestamp) {
      if (!timestamp || timestamp === 0) {
        return '-'
      }
      const date = new Date(timestamp)
      return formatdate(date, 'yyyy-MM-dd hh:mm:ss')
    },
    formatStatusT(status) {
      return this.options.statusT.filter(item => item.code === status)[0].content
    },
    formatDelType(type) {
      switch (type) {
        case 0:
          return '上缴银行'
        case 1:
          return '自取'
      }
    },
    formatRouteNo(routeNo) {
      if (routeNo !== '') {
        return routeNo + '号线'
      }
      return '-'
    },
    formatCategory(type) {
      switch (type) {
        case 0:
          return '银行卡'
        case 1:
          return '回执单'
      }
    },
    formatDeliverType(type) {
      switch (type) {
        case 1:
          return '上交银行'
        case 2:
          return '现场取卡'
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

.filter-container *:nth-child(n+2) {
  margin-left: 10px;
}

.filter-item {
  &:not(button){
    width:160px
  }
}
</style>
