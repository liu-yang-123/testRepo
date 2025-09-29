<template>
  <div class="app-container">
    <search-bar
      :list-query="listQuery"
      :search-list="searchList"
      :options="options"
      :role="role"
      @lookUp="getList"
      @create="handleCreate('single')"
    >
      <template v-slot:more>
        <el-button
          v-permission="['/base/addition/cash/saveBatch']"
          class="filter-item"
          type="primary"
          icon="el-icon-finished"
          @click="handleCreate('batch')"
        >批量添加</el-button>
        <el-button
          v-permission="['/base/vaultOrder/quickCashOut']"
          :disabled="batchList.length === 0"
          class="filter-item"
          type="success"
          icon="el-icon-finished"
          @click="batchOut()"
        >快捷出库单</el-button>
      </template>
    </search-bar>
    <my-table
      :list-loading="listLoading"
      :data-list="list"
      :table-list="tableList"
      class="my-table"
      :selection="{ name: 'statusT', optional: 1 }"
      @selectionChange="selectionChange"
    >
      <template v-slot:operate>
        <el-table-column
          align="center"
          label="操作"
          class-name="small-padding fixed-width"
          width="240"
        >
          <template slot-scope="scope">
            <el-button
              v-show="scope.row.statusT === 0 || scope.row.statusT === 3"
              v-permission="['/base/addition/cash/confirm']"
              :disabled="scope.row.statusT === 3"
              :plain="scope.row.statusT === 3"
              type="success"
              size="mini"
              @click="handleConfirm(scope.row)"
            >确认</el-button>
            <el-button
              v-show="!(scope.row.statusT === 0 || scope.row.statusT === 3)"
              v-permission="['/base/addition/cash/cancel']"
              type="warning"
              size="mini"
              @click="handleCancel(scope.row)"
            >撤销</el-button>
            <el-button
              v-permission="['/base/addition/cash/update']"
              :disabled="scope.row.statusT !== 0"
              type="primary"
              size="mini"
              :plain="scope.row.statusT !== 0"
              @click="handleUpdate(scope.row)"
            >编辑</el-button>
            <el-button
              v-permission="['/base/addition/cash/delete']"
              :disabled="scope.row.statusT !== 0"
              type="danger"
              :plain="scope.row.statusT !== 0"
              size="mini"
              @click="handleDelete(scope.row)"
            >删除</el-button>
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
    <!-- 添加 -->
    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible" :close-on-click-modal="false">
      <el-form ref="dataForm" :rules="rules" :model="dataForm" status-icon label-position="right" label-width="120px" style="width: 80%; margin-left:8%;">
        <el-form-item label="任务日期" prop="taskDate">
          <span>{{ formatDate(dataForm.taskDate) }}</span>
        </el-form-item>
        <el-form-item v-if="!isBatch" label="所属线路" prop="routeId">
          <el-select v-model="dataForm.routeId" filterable placeholder="请选择" style="width: 40%">
            <el-option
              v-for="item in routeIdOption"
              :key="item.value"
              :label="`${item.routeNo}号线`"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item v-else label="所属线路" prop="routeIds">
          <el-select v-model="dataForm.routeIds" multiple filterable placeholder="请选择" style="width: 40%">
            <el-option
              v-for="item in routeIdOption"
              :key="item.value"
              :label="`${item.routeNo}号线`"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="加款类型" prop="cashType">
          <el-select v-model="dataForm.cashType" filterable placeholder="请选择" style="width: 40%">
            <el-option
              v-for="item in options.cashType"
              :key="item.code"
              :label="item.content"
              :value="item.code"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="加钞银行" prop="bankId">
          <el-select v-model="dataForm.bankId" filterable placeholder="请选择" style="width: 40%">
            <el-option
              v-for="item in options.bankId"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="券别类型" prop="denomValue">
          <el-radio-group v-model="dataForm.denomValue">
            <el-radio :label="100">100</el-radio>
            <el-radio :label="10">10</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备用金额" prop="amount">
          <el-input v-model.number="dataForm.amount" :maxlength="32" style="width:40%" @blur="inputMoney($event,'amount')" />
          <span style="margin-left: 12px">元</span>
        </el-form-item>
        <el-form-item v-if="!isBatch" label="携带线路" prop="carryRouteId">
          <el-select v-model="dataForm.carryRouteId" filterable placeholder="请选择" style="width: 40%">
            <el-option
              v-for="item in carryRouteIdOption"
              :key="item.value"
              :label="`${item.routeNo}号线`"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="comments">
          <el-input v-model="dataForm.comments" type="textarea" :autosize="{ minRows: 3, maxRows: 4}" maxlength="100" style="width:60%" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="newData(dialogStatus)">确定</el-button>
      </div>
    </el-dialog>
    <!-- 快捷出库单 -->
    <el-dialog title="快捷出库单" :visible.sync="outVisible" :close-on-click-modal="false">
      <div>
        <my-table
          :data-list="outList"
          :table-list="outTableList"
        />
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="outVisible = false">取消</el-button>
        <el-button type="primary" @click="outSubmit()">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import Pagination from '@/components/Pagination'
import myTable from '@/components/Table'
import { formatdate } from '@/utils/date'
import { formatMoney } from '@/utils/convert'
import searchBar from '@/components/SearchBar'
import { listSpare, deleteSpare, addSpare, updateSpare, confirmSpare, cancelSpare, quickOut, addBatchSpare } from '@/api/clean/spareManage'
import { bankClearTopBank, authOption, routeTree } from '@/api/common/selectOption'
// 金额添加千分位
const comdify = function(n) {
  if (!n) return n
  const str = n.split('.')
  const re = /\d{1,3}(?=(\d{3})+$)/g
  const n1 = str[0].replace(re, '$&,')
  return str.length > 1 && str[1] ? `${n1}.${str[1]}` : `${n1}.00`
}
// 去除千分位中的‘，'
const delcommafy = function(num) {
  if (!num) return num
  num = num.toString()
  num = num.replace(/,/gi, '')
  return num
}

// 获取输入框的值
const getInputValue = function(el) {
  const inputVal = el.target.value || ''
  return comdify(delcommafy(inputVal))
}

export default {
  components: { myTable, searchBar, Pagination },
  data() {
    const valdateFn = (rule, val, cb) => {
      const MoneyTest = /((^[1-9]\d*)|^0)(\.\d{0,2}){0,1}$/
      if (!val) {
        return cb(new Error('金额不能为空'))
      }
      setTimeout(() => {
        const inputVal = delcommafy(val)
        if (MoneyTest.test(inputVal)) {
          cb()
        } else {
          cb(new Error('只能是数字金额,最多两位小数'))
        }
      }, 1000)
    }

    // 验证金额数字可以为负数
    // const moneyValid = function(rule, val, cb) {
    //   valdateFn(/((^-?[1-9]\d*)|^-?0)(\.\d{0,2}){0,1}$/, val, cb)
    // }
    // // 验证金额数字不可以为负数
    // const moneyNValid = function(rule, val, cb) {
    //   valdateFn(MoneyTest, val, cb)
    // }

    return {
      batchList: [],
      routeIdOption: [],
      carryRouteIdOption: [],
      options: {
        departmentId: [],
        bankId: [],
        routeId: [],
        cashType: [
          { code: 0, content: '备用金' },
          { code: 1, content: '其它' }
        ],
        carryRouteId: []
      },
      list: [],
      listQuery: {
        departmentId: null,
        limit: 10,
        page: 1,
        bankId: null,
        routeId: null,
        taskDate: new Date(new Date().toLocaleDateString()).getTime()
      },
      taskDate: null,
      departmentId: null,
      total: 0,
      listLoading: true,
      searchList: [
        { name: 'departmentId', label: '所属部门', type: 3, notClear: true, change: this.departmentChange },
        { name: 'taskDate', label: '任务日期', type: 2, notClear: true, change: this.dateChange },
        { name: 'bankId', label: '银行名称', type: 3 },
        { name: 'routeId', label: '线路', type: 3 }
      ],
      role: {
        list: '/base/addition/cash/list',
        add: '/base/addition/cash/save'
      },
      tableList: [
        {
          label: '任务日期',
          prop: 'taskDate',
          formatter: this.formatDate
        },
        {
          label: '所属线路',
          prop: 'routeNo',
          formatter: this.formatRoute
        },
        {
          label: '携带线路',
          prop: 'carryRouteNo',
          formatter: this.formatRoute
        },
        {
          label: '银行',
          prop: 'bankName'
        },
        {
          label: '加款类型',
          prop: 'cashType',
          formatter: this.formatCashType
        },
        {
          label: '券别',
          prop: 'denomName'
        },
        {
          label: '金额',
          prop: 'amount',
          formatter: this.formatMoney
        },
        {
          label: '状态',
          prop: 'statusText'
        },
        {
          label: '备注',
          prop: 'comments'
        }
      ],
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '创建'
      },
      isBatch: false,
      dataForm: {
        routeIds: [],
        amount: null,
        bankId: null,
        id: null,
        denomValue: null,
        routeId: null,
        taskDate: null,
        cashType: null,
        comments: null,
        carryRouteId: null
      },
      rules: {
        amount: [
          { required: true, validator: valdateFn, trigger: 'blur' }
        ],
        bankId: [
          { required: true, message: '加钞银行不能为空', trigger: 'blur' }
        ],
        denomValue: [
          { required: true, message: '券别类型不能为空', trigger: 'blur' }
        ],
        routeId: [
          { required: true, message: '所属线路不能为空', trigger: 'blur' }
        ],
        cashType: [
          { required: true, message: '加款类型不能为空', trigger: 'blur' }
        ],
        carryRouteId: [
          { required: true, message: '携带线路不能为空', trigger: 'blur' }
        ],
        routeIds: [
          { type: 'array', required: true, message: '请至少选择一条线路', trigger: 'change' }
        ]
      },
      outVisible: false,
      outList: [],
      outTableList: [
        {
          label: '银行名称',
          prop: 'bankName'
        },
        {
          label: '券别类型',
          prop: 'denomValue'
        },
        {
          label: '金额总计',
          prop: 'amount'
        }
      ]
    }
  },
  mounted() {
    this.openLoading()
    this.getAuthOption().then(() => {
      this.getList()
      this.getBankOption()
      this.getRouteOption()
    })
  },
  methods: {
    getList(data) {
      if (data) {
        for (const key in data) {
          this.listQuery[key] = data[key]
        }
      }
      this.listLoading = true
      listSpare(this.listQuery).then((res) => {
        this.list = res.data.list
        this.total = res.data.total
        this.taskDate = this.listQuery.taskDate
        this.departmentId = this.listQuery.departmentId
        this.listLoading = false
        this.loading.close()
      })
    },
    getAuthOption() {
      return authOption().then(res => {
        this.options.departmentId = res.data
        if (this.options.departmentId.length > 0) {
          this.listQuery.departmentId = this.options.departmentId[0].id
        }
      })
    },
    getBankOption() {
      this.listQuery.bankId = null
      return bankClearTopBank(this.listQuery.departmentId).then(res => {
        this.options.bankId = res.data
      })
    },
    getRouteOption() {
      this.listQuery.routeId = null
      const params = {
        routeDate: this.listQuery.taskDate,
        departmentId: this.listQuery.departmentId,
        routeType: 0
      }
      return routeTree(params).then(res => {
        this.options.routeId = res.data
      })
    },
    async departmentChange() {
      await this.getBankOption()
      await this.getRouteOption()
    },
    async dateChange() {
      await this.getRouteOption()
    },
    handleCreate(sign) {
      this.isBatch = sign !== 'single'
      const params1 = {
        routeDate: this.taskDate,
        departmentId: this.departmentId,
        routeType: 0
      }

      const params2 = {
        routeDate: this.taskDate,
        departmentId: this.departmentId,
        routeType: 1
      }
      this.openLoading()
      Promise.all([routeTree(params1), routeTree(params2)]).then(res => {
        const [res1, res2] = res
        this.routeIdOption = res1.data
        this.carryRouteIdOption = res2.data
        this.routeId = this.carryRouteId = null
        this.routeIds = []
        this.dialogStatus = 'create'
        this.dialogFormVisible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].resetFields()
          this.dataForm.taskDate = this.taskDate
        })
      }).finally(() => {
        this.loading.close()
      })
    },
    handleUpdate(row) {
      this.isBatch = false
      const params1 = {
        routeDate: this.taskDate,
        departmentId: this.departmentId,
        routeType: 0
      }

      const params2 = {
        routeDate: this.taskDate,
        departmentId: this.departmentId,
        routeType: 1
      }
      this.openLoading()
      Promise.all([routeTree(params1), routeTree(params2)]).then(res => {
        const [res1, res2] = res
        this.routeIdOption = res1.data
        this.carryRouteIdOption = res2.data
        this.dialogStatus = 'update'
        this.dialogFormVisible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].clearValidate()
          for (const key in this.dataForm) {
            this.dataForm[key] = row[key]
          }
          this.dataForm.taskDate = this.taskDate
        })
      }).finally(() => {
        this.loading.close()
      })
    },
    handleDelete(row) {
      this.$confirm('确认删除吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.listLoading = true
        deleteSpare(row.id)
          .then(() => {
            this.$notify.success({
              title: '成功',
              message: '删除成功'
            })
            this.getList()
          }).finally(() => {
            this.listLoading = false
          })
      })
    },
    newData(type) {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          this.openLoading()
          this.dataForm.amount = delcommafy(this.dataForm.amount)
          if (type === 'update') {
            updateSpare(this.dataForm)
              .then(() => {
                this.getList()
                this.dialogFormVisible = false
                this.$notify.success({
                  title: '成功',
                  message: '更新成功'
                })
              })
              .finally(() => {
                this.loading.close()
              })
          } else {
            if (!this.isBatch) {
              addSpare(this.dataForm)
                .then(() => {
                  this.getList()
                  this.dialogFormVisible = false
                  this.$notify.success({
                    title: '成功',
                    message: '添加成功'
                  })
                })
                .finally(() => {
                  this.loading.close()
                })
            } else {
              addBatchSpare(this.dataForm)
                .then(() => {
                  this.getList()
                  this.dialogFormVisible = false
                  this.$notify.success({
                    title: '成功',
                    message: '添加成功'
                  })
                })
                .finally(() => {
                  this.loading.close()
                })
            }
          }
        }
      })
    },
    handleConfirm(row) {
      this.$confirm('确定确认吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.listLoading = true
        confirmSpare(row.id)
          .then(() => {
            this.getList()
            this.$notify.success({
              title: '成功',
              message: '确认成功'
            })
          })
          .finally(() => {
            this.listLoading = false
          })
      })
    },
    handleCancel(row) {
      this.$confirm('确定撤销吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.listLoading = true
        cancelSpare(row.id)
          .then(() => {
            this.getList()
            this.$notify.success({
              title: '成功',
              message: '撤销成功'
            })
          })
          .finally(() => {
            this.listLoading = false
          })
      })
    },
    batchOut() {
      this.outVisible = true
      const arr = []
      this.outList = []
      this.batchList.forEach((item) => {
        if (!arr.find((elm) => elm.bankId === item.bankId && elm.denomValue === item.denomValue)) {
          arr.push({ bankId: item.bankId, denomValue: item.denomValue })
          // obj[item.bankId] = this.batchList.filter(
          //   (elm) => elm.bankId === item.bankId
          // )
          const obj = {
            bankName: item.bankName,
            denomValue: item.denomValue,
            amount: this.batchList.filter(elm => elm.bankId === item.bankId && elm.denomValue === item.denomValue).reduce((total, item) => total + item.amount, 0)
          }
          this.outList.push(obj)
        }
      })
    },
    outSubmit() {
      const ids = this.batchList.map(elm => elm.id)
      this.openLoading()
      quickOut(ids).then(() => {
        this.outVisible = false
        this.getList()
        this.$notify.success({
          title: '成功',
          message: '快捷出库成功'
        })
      })
        .finally(() => {
          this.loading.close()
        })
    },
    formatMoney,
    formatRoute(num) {
      return `${num}号线`
    },
    formatDeleted(type) {
      return this.options.deleted.find(item => item.code === type).content
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
    formatCashType(type) {
      if (type != null) {
        return this.options.cashType.find(item => item.code === type).content
      }
    },
    selectionChange(val) {
      this.batchList = val.map(({ id, bankId, denomValue, amount, bankName }) => {
        return {
          id,
          bankId,
          denomValue,
          amount,
          bankName
        }
      })
    },
    openLoading() {
      this.loading = this.$loading({
        lock: true,
        text: '正在加载...请勿进行其它操作',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      })
    },
    inputMoney(el, name) {
      this.dataForm[name] = getInputValue(el)
    }
  }
}
</script>

<style scoped lang="scss">

</style>
