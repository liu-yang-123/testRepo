<template>
  <div class="app-container">
    <el-tabs v-model="activeName" @tab-click="tabClick">
      <el-tab-pane label="指纹通行证备案" name="first">
        <search-bar :list-query="listQuery" :options="fingerOptions" :search-list="fingerSearchList" :role="role" @lookUp="getList" @create="handleCreate('finger')" />
        <my-table
          :list-loading="listLoading"
          :data-list="list"
          :table-list="fingerTableList"
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
                  v-permission="['/base/passAuth/update']"
                  type="primary"
                  size="mini"
                  @click="handleUpdate(scope.row,'finger')"
                >编辑</el-button>
                <el-button
                  v-permission="['/base/passAuth/delete']"
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
      </el-tab-pane>
      <el-tab-pane label="网点出入备案" name="second" lazy>
        <search-bar :list-query="listQuery" :options="inOutOptions" :search-list="inOutsearchList" :role="role" @lookUp="getList" @create="handleCreate('inOut')" />
        <my-table
          :list-loading="listLoading"
          :data-list="list"
          :table-list="inOuttableList"
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
                  v-permission="['/base/passAuth/update']"
                  type="primary"
                  size="mini"
                  @click="handleUpdate(scope.row,'inOut')"
                >编辑</el-button>
                <el-button
                  v-permission="['/base/passAuth/delete']"
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
      </el-tab-pane>
    </el-tabs>

    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogVisible" :close-on-click-modal="false">
      <el-form ref="dataForm" :rules="rules" :model="dataForm" status-icon label-position="right" label-width="100px" style="width: 80%; margin-left:8%;">
        <el-form-item label="所属部门">
          <span>{{ formatDepId(dataForm.departmentId) }}</span>
        </el-form-item>
        <el-form-item v-if="dialogStatus === 'create'" label="员工姓名" prop="empId">
          <el-select v-model="dataForm.empId" filterable placeholder="请选择" style="width: 40%">
            <el-option
              v-for="item in inOutOptions.empId"
              :key="item.id"
              :label="`${item.empNo}/${item.empName}`"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="银行网点" prop="bankId">
          <el-select v-if="type === 'finger'" v-model="dataForm.bankId" filterable placeholder="请选择" style="width: 40%">
            <el-option
              v-for="item in fingerOptions.bankId"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
          <treeSelect
            v-else
            v-model="dataForm.bankId"
            :normalizer="normalizer"
            :show-count="true"
            :disable-branch-nodes="true"
            :options="inOutOptions.bankId"
            placeholder="请选择"
            style="width:40%;font-size: 14px;"
          />
        </el-form-item>
        <el-form-item v-if="type === 'finger'" label="通行证编号" prop="passCode">
          <el-input v-model="dataForm.passCode" :minlength="8" :maxlength="16" style="width:40%" oninput="value=value.replace(/[^\w\.\/]/ig,'')" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="newData(dialogStatus)">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import searchBar from '@/components/SearchBar'
import Pagination from '@/components/Pagination'
import myTable from '@/components/Table'
import TreeSelect from '@riophae/vue-treeselect'
import { formatdate } from '@/utils/date'
import { listPassAuth, addPassAuth, updatePassAuth, deletePassAuth } from '@/api/duty/recordManage'
import { authOption, bankClearTopBank, jobNameOption, bankClearTree } from '@/api/common/selectOption'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
export default {
  components: { TreeSelect, searchBar, myTable, Pagination },
  data() {
    return {
      activeName: 'first',
      oldName: 'first',
      dialogStatus: '',
      type: '',
      loading: null,
      textMap: {
        update: '编辑',
        create: '创建'
      },
      departmentId: null,
      fingerSearchList: [
        { name: 'departmentId', label: '所属部门', type: 3, notClear: true, change: this.depId1Change },
        { name: 'bankId', label: '银行网点', type: 3 },
        { name: 'empId', label: '员工姓名', type: 3 }
      ],
      role: {
        list: '/base/passAuth/list',
        add: '/base/passAuth/save'
      },
      fingerOptions: {
        departmentId: [],
        bankId: [],
        empId: []
      },
      listQuery: {
        page: 1,
        limit: 10,
        passType: 0,
        departmentId: null,
        name: null,
        bankId: null
      },
      total: 0,
      listLoading: false,
      list: [],
      fingerTableList: [
        {
          label: '员工姓名',
          prop: 'empName'
        },
        {
          label: '岗位',
          prop: 'jobType',
          formatter: this.formatJobType
        },
        {
          label: '通行证编号',
          prop: 'passCode'
        },
        {
          label: '银行网点',
          prop: 'bankName'
        },
        {
          label: '更新时间',
          prop: 'updateTime',
          formatter: this.formatDateTime
        }
      ],
      dialogVisible: false,
      dataForm: {
        bankId: null,
        departmentId: null,
        id: null,
        empId: null,
        passCode: null,
        passType: null
      },
      rules: {
        bankId: [{ required: true, message: '银行网点不能为空', trigger: 'blur' }]
      },
      // 休息计划管理
      inOutsearchList: [
        { name: 'departmentId', label: '所属部门', type: 3, notClear: true, change: this.depId2Change },
        { name: 'bankId', label: '银行网点', type: 4 },
        { name: 'empId', label: '员工姓名', type: 3 }
      ],
      inOuttableList: [
        {
          label: '员工姓名',
          prop: 'empName'
        },
        {
          label: '岗位',
          prop: 'jobType',
          formatter: this.formatJobType
        },
        {
          label: '线路',
          prop: 'routeNo',
          formatter: this.formatRoute
        },
        {
          label: '银行网点',
          prop: 'bankName'
        },
        {
          label: '更新时间',
          prop: 'updateTime',
          formatter: this.formatDateTime
        }
      ],
      jobOption: [
        { code: 0, content: '其它' },
        { code: 1, content: '司机岗' },
        { code: 2, content: '护卫岗' },
        { code: 3, content: '钥匙岗' },
        { code: 4, content: '密码岗' },
        { code: 5, content: '清点岗' },
        { code: 6, content: '库管岗' }

      ],
      inOutOptions: {
        departmentId: [],
        bankId: [],
        empId: []
      }
    }
  },
  mounted() {
    this.openLoading()
    authOption().then(res => {
      this.fingerOptions.departmentId = res.data
      this.inOutOptions.departmentId = res.data
      this.listQuery.departmentId = this.fingerOptions.departmentId[0].id
      this.getList()
      this.getBankOption()
      this.getJobNameOption()
    })
  },
  methods: {
    tabClick() {
      if (this.activeName !== this.oldName) {
        this.listQuery.departmentId = this.departmentId
        this.listQuery.bankId = null
        this.listQuery.page = 1
        if (this.activeName === 'first') {
          this.listQuery.passType = 0
          this.getList()
          this.getBankOption()
        } else {
          this.listQuery.passType = 1
          this.getList()
          this.getBankTree()
        }
        this.oldName = this.activeName
      }
    },
    getList(data) {
      if (data) {
        for (const key in data) {
          this.listQuery[key] = data[key]
        }
      }
      this.listLoading = true
      listPassAuth(this.listQuery).then((res) => {
        this.list = res.data.list
        this.departmentId = this.listQuery.departmentId
        this.listLoading = false
        this.total = res.data.total
        this.loading.close()
      })
    },
    getBankOption() {
      return bankClearTopBank(this.listQuery.departmentId).then(res => {
        this.fingerOptions.bankId = res.data
      })
    },
    getBankTree() {
      return bankClearTree(this.listQuery.departmentId).then(res => {
        this.inOutOptions.bankId = res.data
      })
    },
    getJobNameOption() {
      return jobNameOption(this.listQuery.departmentId, '3,4').then(res => {
        this.inOutOptions.empId = res.data[3].concat(res.data[4])
        this.fingerOptions.empId = this.inOutOptions.empId
      })
    },
    handleCreate(type) {
      this.type = type
      if (type === 'finger') {
        this.dataForm.passType = 0
        this.rules.passCode = [
          { required: true, message: '通行证编号不能为空', trigger: 'blur' },
          { min: 8, max: 16, message: '长度在 8 到 16 个字符', trigger: 'blur' }
        ]
      } else {
        this.dataForm.passType = 1
        delete this.rules.passCode
      }
      this.rules.empId = [{ required: true, message: '员工姓名不能为空', trigger: 'blur' }]
      this.dataForm.empId = null
      this.dialogStatus = 'create'
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].resetFields()
        this.dataForm.departmentId = this.departmentId
      })
    },
    handleUpdate(row, type) {
      this.type = type
      if (type === 'finger') {
        this.dataForm.passType = 0
        this.rules.passCode = [
          { required: true, message: '通行证编号不能为空', trigger: 'blur' },
          { min: 8, max: 16, message: '长度在 8 到 16 个字符', trigger: 'blur' }
        ]
      } else {
        this.dataForm.passType = 1
        delete this.rules.passCode
      }
      delete this.rules.empId
      this.dialogStatus = 'update'
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
        for (const key in this.dataForm) {
          this.dataForm[key] = row[key]
        }
        this.dataForm.departmentId = this.departmentId
      })
    },
    handleDelete(row) {
      this.$confirm('确定删除吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.listLoading = true
        deletePassAuth(row.id)
          .then(() => {
            this.$notify.success({
              title: '成功',
              message: '删除成功'
            })
            const index = this.list.indexOf(row)
            this.list.splice(index, 1)
          }).finally(() => {
            this.listLoading = false
          })
      })
    },
    newData(type) {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          this.listLoading = true
          if (type === 'update') {
            updatePassAuth(this.dataForm)
              .then(() => {
                this.getList()
                this.dialogVisible = false
                this.$notify.success({
                  title: '成功',
                  message: '更新成功'
                })
              })
              .finally(() => {
                this.listLoading = false
              })
          } else {
            addPassAuth(this.dataForm)
              .then(() => {
                this.getList()
                this.dialogVisible = false
                this.$notify.success({
                  title: '成功',
                  message: '添加成功'
                })
              })
              .finally(() => {
                this.listLoading = false
              })
          }
        }
      })
    },
    // 失去焦点初始化
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
    formatRoute(routeNo) {
      return routeNo ? routeNo + '号线' : ''
    },
    formatJobType(jobType) {
      const [obj] = this.jobOption.filter(s => s.code === jobType)
      return obj !== undefined ? obj.content : ''
    },
    formatDepId(id) {
      if (id != null) {
        return this.fingerOptions.departmentId.filter(item => item.id === id)[0].name
      }
    },
    async depId1Change() {
      this.listQuery.bankId = null
      this.listQuery.name = null
      await this.getBankOption()
      await this.getJobNameOption()
    },
    async depId2Change() {
      this.listQuery.bankId = null
      this.listQuery.name = null
      await this.getBankTree()
      await this.getJobNameOption()
    },
    normalizer(node) {
      return {
        id: node.id,
        label: node.name,
        children: node.children
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
  .filter-container * {
    margin-left: 10px;
  }

  .scrollbar {
  ::v-deep .el-scrollbar__wrap {
    overflow: auto;
  }
}
</style>
