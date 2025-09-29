<template>
  <div class="app-container">
    <el-tabs v-model="activeName" @tab-click="tabClick">
      <el-tab-pane label="送卡" name="first">
        <div class="filter-container">
          <el-select
            v-model="listQuery.departmentId"
            placeholder="请选择所属部门"
            class="filter-item"
          >
            <el-option
              v-for="item in options.departmentId"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
          <el-select
            v-model="listQuery.bankId"
            placeholder="请选择所属机构"
            class="filter-item"
            clearable
          >
            <el-option
              v-for="item in options.bankId"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
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
          <div style="margin-left:0">
            <el-button
              v-permission="['/base/atmTaskCard/batchDistribute']"
              class="filter-item"
              type="success"
              icon="el-icon-finished"
              @click="handleAssign()"
            >分配</el-button>
            <el-button
              v-permission="['/base/atmTaskCard/handover']"
              class="filter-item"
              :disabled="cardList.length === 0"
              type="warning"
              icon="el-icon-sold-out"
              @click="cardManage('takeIn')"
            >收卡</el-button>
            <el-button
              v-permission="['/base/atmTaskCard/handover']"
              class="filter-item"
              :disabled="cardList.length === 0"
              type="warning"
              icon="el-icon-sell"
              @click="cardManage('takeOut')"
            >配卡</el-button>
            <el-button
              v-permission="['/base/atmTaskCard/download']"
              class="filter-item"
              type="success"
              icon="el-icon-sell"
              @click="handleExport()"
            >线路导出</el-button>
            <el-button
              v-permission="['/base/atmTaskCard/exportDeliver']"
              class="filter-item"
              type="success"
              icon="el-icon-download"
              @click="handleBankExport()"
            >派送汇总</el-button>
          </div>
        </div>
      </el-tab-pane>
      <el-tab-pane label="回笼" name="second">
        <div class="filter-container">
          <el-select
            v-model="listQuery.departmentId"
            placeholder="请选择所属部门"
            class="filter-item"
          >
            <el-option
              v-for="item in options.departmentId"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
          <el-select
            v-model="listQuery.bankId"
            placeholder="请选择所属机构"
            class="filter-item"
            clearable
          >
            <el-option
              v-for="item in options.bankId"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
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
          <div style="margin-left:0">
            <el-button
              v-permission="['/base/atmTaskCard/save']"
              class="filter-item"
              type="success"
              icon="el-icon-finished"
              @click="handleCreate()"
            >添加</el-button>
            <el-button
              v-permission="['/base/atmTaskCard/batchDistribute']"
              class="filter-item"
              type="success"
              icon="el-icon-finished"
              @click="handleAssign()"
            >分配</el-button>
            <el-button
              v-permission="['/base/atmTaskCard/handover']"
              class="filter-item"
              :disabled="cardList.length === 0"
              type="warning"
              icon="el-icon-sold-out"
              @click="cardManage('takeIn')"
            >收卡</el-button>
            <el-button
              v-permission="['/base/atmTaskCard/handover']"
              class="filter-item"
              :disabled="cardList.length === 0"
              type="warning"
              icon="el-icon-sell"
              @click="cardManage('takeOut')"
            >配卡</el-button>
            <el-button
              v-permission="['/base/atmTaskCard/download']"
              class="filter-item"
              type="success"
              icon="el-icon-sell"
              @click="handleExport()"
            >线路导出</el-button>
            <el-button
              v-permission="['/base/atmTaskCard/exportCollect']"
              class="filter-item"
              type="success"
              icon="el-icon-download"
              @click="handleBankExport()"
            >回收汇总</el-button>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>
    <my-table
      :list-loading="listLoading"
      :data-list="list"
      :table-list="tableList"
      :expand-list="expandList"
      :selection="true"
      @selectionChange="selectionChange"
    >
      <template v-slot:operate>
        <el-table-column
          align="center"
          label="操作"
          class-name="small-padding fixed-width"
          :width="listQuery.queryType === 0 ? 240 : 120"
        >
          <template slot-scope="scope">
            <el-button
              v-permission="['/base/atmTaskCard/update']"
              :disabled="scope.row.statusT !== 1"
              type="primary"
              plain
              size="mini"
              @click="handleUpdate(scope.row)"
            >修改配送</el-button>
            <el-button
              v-show="listQuery.queryType === 0"
              v-permission="['/base/atmTaskCard/edit']"
              type="primary"
              size="mini"
              @click="handleEdit(scope.row)"
            >编辑</el-button>
            <el-button
              v-show="listQuery.queryType === 0"
              v-permission="['/base/atmTaskCard/delete']"
              type="danger"
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
    <el-dialog
      title="添加"
      :visible.sync="addFormVisible"
      :close-on-click-modal="false"
    >
      <el-form ref="addForm" :rules="addRules" :model="addForm" status-icon label-position="right" label-width="100px" style="width:80%;margin-left:8%;">
        <el-form-item label="任务日期" prop="taskDate">
          <span>{{ formatDate(addForm.taskDate) }}</span>
        </el-form-item>
        <el-form-item label="线路编号" prop="routeId">
          <el-select
            v-model="addForm.routeId"
            filterable
            placeholder="请选择线路编号"
            style="width:40%"
            :disabled="routeOption.length === 0"
            @change="routeChange"
          >
            <el-option
              v-for="item in routeOption"
              :key="item.value"
              :label="item.routeName"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="ATM任务" prop="taskId">
          <el-select
            v-model="addForm.taskId"
            filterable
            placeholder="请选择ATM任务"
            style="width:40%"
            :disabled="taskOption.length === 0"
          >
            <el-option
              v-for="item in taskOption"
              :key="item.id"
              :label="`${item.terNo}/${formatTaskType(
                item.taskType
              )}/${item.amount / 100000}/${item.comments}`"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="类型" prop="category">
          <el-select
            v-model="addForm.category"
            filterable
            placeholder="请选择类型"
            style="width:40%"
          >
            <el-option
              v-for="item in categoryOption"
              :key="item.code"
              :label="item.content"
              :value="item.code"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="发卡行" prop="cardBank">
          <el-input v-model="addForm.cardBank" :maxlength="32" autosize placeholder="请输入发卡行" style="width:40%" />
        </el-form-item>
        <el-form-item label="银行卡号" prop="cardNo">
          <el-input v-model="addForm.cardNo" :maxlength="32" autosize placeholder="请输入银行卡号" style="width:40%" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="addFormVisible = false">取消</el-button>
        <el-button type="primary" @click="newData">确定</el-button>
      </div>
    </el-dialog>
    <!-- 修改配送 -->
    <el-dialog
      title="修改"
      :visible.sync="dialogFormVisible"
      :close-on-click-modal="false"
    >
      <el-form ref="dataForm" :rules="rules" :model="dataForm" status-icon label-position="right" label-width="100px" style="width:80%;margin-left:8%;">
        <el-form-item label="送卡网点" prop="deliverBankId">
          <el-select
            v-model="dataForm.deliverBankId"
            filterable
            placeholder="请选择送卡网点"
            style="width:40%"
            @change="bankChange"
          >
            <el-option
              v-for="item in subOption"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="送卡线路" prop="deliverRouteNo">
          <el-input v-model="dataForm.deliverRouteNo" :maxlength="32" autosize placeholder="请输入送卡线路" style="width:40%" />
        </el-form-item>
        <el-form-item label="日期" prop="deliverDay">
          <el-date-picker v-model="dataForm.deliverDay" value-format="timestamp" type="date" :picker-options="pickerOptions" placeholder="选择日期" style="width:40%" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="updateData">确定</el-button>
      </div>
    </el-dialog>
    <!-- 编辑 -->
    <el-dialog
      title="修改"
      :visible.sync="editFormVisible"
      :close-on-click-modal="false"
    >
      <el-form ref="editForm" :rules="editRules" :model="editForm" status-icon label-position="right" label-width="100px" style="width:80%;margin-left:8%;">
        <el-form-item label="发卡行" prop="cardBank">
          <el-input v-model="editForm.cardBank" :maxlength="32" autosize placeholder="请输入发卡行" style="width:40%" />
        </el-form-item>
        <el-form-item label="银行卡号" prop="cardNo">
          <el-input v-model="editForm.cardNo" :maxlength="32" autosize placeholder="请输入银行卡号" style="width:40%" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="editFormVisible = false">取消</el-button>
        <el-button type="primary" @click="editData">确定</el-button>
      </div>
    </el-dialog>
    <!-- 分配 -->
    <el-dialog
      title="分配"
      :visible.sync="assignFormVisible"
      :close-on-click-modal="false"
    >
      <el-form ref="assignForm" :rules="assignRules" :model="assignForm" status-icon label-position="right" label-width="100px" style="width:80%;margin-left:8%;">
        <el-form-item label="日期" prop="deliverDay">
          <el-date-picker v-model="assignForm.deliverDay" value-format="timestamp" type="date" placeholder="选择日期" style="width:40%" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="assignFormVisible = false">取消</el-button>
        <el-button type="primary" @click="assignData()">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import Pagination from '@/components/Pagination'
import myTable from '@/components/Table'
import { formatdate } from '@/utils/date'
import { listCard, updateCard, batchCard, handover, exportCard, exportDeliver, exportCollect, addCard, editCard, deleteCard } from '@/api/clean/gulpRecord'
import { authOption, subOption, bankClearTopBank, routeTree, routeTaskList } from '@/api/common/selectOption'
import { downloadFile } from '@/utils/downloadFile'
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
        departmentId: [],
        statusT: [
          { code: 0, content: '取回' },
          { code: 1, content: '入库' },
          { code: 2, content: '分配' },
          { code: 3, content: '派送' },
          { code: 4, content: '领取' }
        ],
        bankId: []
      },
      total: 0,
      loading: null,
      list: [],
      listQuery: {
        departmentId: null,
        bankId: null,
        limit: 10,
        page: 1,
        statusT: null,
        atmTerNo: null,
        queryDay: null,
        routeNo: null,
        queryType: 1,
        cardNo: null
      },
      departmentId: null,
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
      },
      // 编辑
      editFormVisible: false,
      editForm: {
        id: null,
        cardBank: null,
        cardNo: null
      },
      editRules: {
        cardBank: [
          { required: true, message: '发卡行不能为空', trigger: 'blur' }
        ],
        cardNo: [
          { required: true, message: '银行卡号不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  watch: {
    'listQuery.departmentId': function(val) {
      bankClearTopBank(val).then(res => {
        this.options.bankId = res.data
      })
    }
  },
  mounted() {
    this.getOptions()
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
        this.departmentId = this.listQuery.departmentId
        this.listLoading = false
      })
    },
    getOptions() {
      authOption().then((res) => {
        this.options.departmentId = res.data
        if (this.options.departmentId) {
          this.listQuery.departmentId = this.options.departmentId[0].id
        }
        this.getList()
      })
    },
    cardManage(status) {
      let tips = ''
      let type = 0
      if (status === 'takeIn') {
        let isAbled = this.cardList.map(elm => elm.stautsT).find(item => item === 1 || item === 2)
        isAbled = isAbled === undefined
        if (!isAbled || this.cardList.length === 0) {
          return this.$notify({
            title: '警告',
            message: '请选择状态为“取回”、“派送”的记录',
            type: 'warning'
          })
        }
        tips = '收卡'
        type = 0
      } else {
        let isAbled = this.cardList.map(elm => elm.stautsT).find(item => item !== 2)
        isAbled = isAbled === undefined
        if (!isAbled || this.cardList.length === 0) {
          return this.$notify({
            title: '警告',
            message: '请选择状态为“分配”的记录',
            type: 'warning'
          })
        }
        tips = '派卡'
        type = 1
      }
      this.$confirm(`确认${tips}吗`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.listLoading = true
        const data = {
          cardIdList: this.cardList.map(item => item.id),
          type
        }
        handover(data)
          .then(() => {
            this.$notify.success({
              title: '成功',
              message: `${tips}成功`
            })
            this.getList()
          }).finally(() => {
            this.listLoading = false
          })
      })
    },
    handleCreate() {
      const params = {
        departmentId: this.departmentId,
        routeType: 1,
        routeDate: this.addForm.taskDate
      }
      routeTree(params).then(res => {
        this.routeOption = res.data
        this.addFormVisible = true
        this.$nextTick(() => {
          this.$refs['addForm'].resetFields()
        })
      })
    },
    handleUpdate(row) {
      this.openLoading()
      subOption(this.departmentId, row.bankId).then(res => {
        this.subOption = res.data
        this.dialogFormVisible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].clearValidate()
          for (const key in this.dataForm) {
            this.dataForm[key] = row[key]
          }
          this.dataForm.deliverDay = new Date(this.dataForm.deliverDay).getTime()
        })
      }).finally(() => {
        this.loading.close()
      })
    },
    handleEdit(row) {
      this.editFormVisible = true
      this.$nextTick(() => {
        this.$refs['editForm'].clearValidate()
        for (const key in this.editForm) {
          this.editForm[key] = row[key]
        }
      })
    },
    handleDelete(row) {
      this.$confirm('确定删除吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.listLoading = true
        deleteCard(row.id)
          .then(() => {
            this.$notify.success({
              title: '成功',
              message: '删除成功'
            })
            this.getList()
          })
          .finally(() => {
            this.listLoading = false
          })
      })
    },
    handleAssign() {
      this.assignFormVisible = true
      this.$nextTick(() => {
        this.$refs['assignForm'].resetFields()
      })
    },
    handleExport() {
      if (!this.listQuery.queryDay) {
        return this.$message.warning('请先选择日期')
      }
      this.openLoading()
      const params = Object.assign({}, this.listQuery)
      delete params.limit
      delete params.page
      const title = `${this.formatDate(params.queryDay)}吞没卡汇总表`
      downloadFile(exportCard, params, title, () => { this.loading.close() }, '.xlsx')
    },
    handleBankExport() {
      const params = Object.assign({}, this.listQuery)
      // 验证参数是否选中
      if (params.departmentId == null) {
        this.$message.warning('请选择所属部门')
        return
      }
      if (params.queryDay == null) {
        return this.$message.warning('请选择日期')
      }
      this.openLoading()
      delete params.limit
      delete params.page
      if (params.queryType === 1) {
        const title = `${new Date(params.queryDay).getFullYear()}年 金融服务业务部 接收/移交吞没卡汇总表`
        downloadFile(exportDeliver, params, title, () => { this.loading.close() }, '.xlsx')
      } else {
        const title = `${this.formatDate(params.queryDay)} 由金融服务业务部带回的吞没卡汇总表`
        downloadFile(exportCollect, params, title, () => { this.loading.close() }, '.xlsx')
      }
    },
    newData() {
      this.$refs['addForm'].validate((valid) => {
        if (valid) {
          this.addForm.routeNo = this.routeOption.find(item => item.value === this.addForm.routeId).routeNo
          this.openLoading()
          addCard(this.addForm).then(() => {
            this.getList()
            this.addFormVisible = false
            this.$notify.success({
              title: '成功',
              message: '添加成功'
            })
          })
            .finally(() => {
              this.loading.close()
            })
        }
      })
    },
    updateData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          this.openLoading()
          updateCard(this.dataForm).then(() => {
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
        }
      })
    },
    editData() {
      this.$refs['editForm'].validate((valid) => {
        if (valid) {
          this.openLoading()
          editCard(this.editForm).then(() => {
            this.getList()
            this.editFormVisible = false
            this.$notify.success({
              title: '成功',
              message: '更新成功'
            })
          })
            .finally(() => {
              this.loading.close()
            })
        }
      })
    },
    assignData() {
      this.$refs['assignForm'].validate((valid) => {
        if (valid) {
          this.openLoading()
          batchCard(this.assignForm).then(() => {
            this.getList()
            this.assignFormVisible = false
            this.$notify.success({
              title: '成功',
              message: '分配成功'
            })
          })
            .finally(() => {
              this.loading.close()
            })
        }
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
    },
    selectionChange(val) {
      this.cardList = val.map(item => {
        return {
          id: item.id,
          stautsT: item.statusT
        }
      })
    },
    bankChange(val) {
      this.dataForm.deliverRouteNo = this.subOption.find(item => item.id === val).routeNo
    },
    routeChange(val) {
      this.addForm.taskId = null
      routeTaskList(val).then(res => {
        this.taskOption = res.data
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
