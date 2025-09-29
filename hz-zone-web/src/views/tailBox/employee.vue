<template>
  <div class="app-container">
    <el-row :gutter="20">
      <div class="filter-container">
        <el-select
          v-model="authId"
          v-permission="['/base/department/auth']"
          class="filter-item"
          style="width: 200px"
          placeholder="请选择部门"
          :clearable="false"
          @change="getTree()"
        >
          <el-option
            v-for="item in authOption"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
        <el-button
          class="filter-item"
          type="primary"
          icon="el-icon-refresh"
          @click="getTree"
        >刷新</el-button>
        <el-button
          v-permission="['/base/bankTeller/save']"
          class="filter-item"
          type="primary"
          icon="el-icon-edit"
          @click="handleCreate"
        >添加</el-button>
      </div>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="5" style="padding-left: 0">
        <el-input
          v-model="filterText"
          placeholder="输入关键字进行过滤"
        />
        <el-scrollbar style="height: 500px;margin-top:12px" class="scrollbar">
          <el-tree
            ref="listTree"
            :data="bankOption"
            :props="defaultProps"
            node-key="id"
            highlight-current
            :filter-node-method="filterNode"
            :default-expanded-keys="expandArr"
            @node-click="handleNodeClick"
          />
        </el-scrollbar>
      </el-col>
      <el-col :span="19">
        <my-table
          :list-loading="listLoading"
          :data-list="list"
          :table-list="tableList"
          height="450px"
        >
          <template v-slot:operate>
            <el-table-column
              align="center"
              label="操作"
              class-name="small-padding fixed-width"
              width="300"
            >
              <template slot-scope="scope">
                <el-button
                  v-permission="['/base/bankTeller/update']"
                  :disabled="scope.row.bankCategory === 3"
                  type="primary"
                  size="mini"
                  @click="handleUpdate(scope.row)"
                >编辑</el-button>
                <el-button
                  v-permission="['/base/bankTeller/delete']"
                  type="danger"
                  size="mini"
                  @click="handleDelete(scope.row)"
                >删除</el-button>
                <el-button
                  v-if="scope.row.statusT === 0"
                  v-permission="['/base/bankTeller/quit']"
                  :disabled="scope.row.bankCategory === 3"
                  type="warning"
                  size="mini"
                  @click="handleToggle(scope.row)"
                >离职</el-button>
                <el-button
                  v-else
                  v-permission="['/base/bankTeller/back']"
                  :disabled="scope.row.bankCategory === 3"
                  type="success"
                  size="mini"
                  @click="handleToggle(scope.row)"
                >复岗</el-button>
                <el-button
                  v-permission="['/base/bankTeller/resetpwd']"
                  :disabled="scope.row.bankCategory === 3"
                  type="info"
                  size="mini"
                  @click="handleReset(scope.row)"
                >重置密码</el-button>
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
      </el-col>
    </el-row>

    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible" :close-on-click-modal="false">
      <el-form ref="dataForm" :rules="rules" :model="dataForm" status-icon label-position="right" label-width="100px" style="width: 400px; margin-left:50px;">
        <el-form-item label="所属机构" prop="bankId">
          <treeSelect
            v-model="dataForm.bankId"
            :disable-branch-nodes="true"
            :normalizer="normalizer"
            :show-count="true"
            :options="newBankOption"
            placeholder="请选择取所属机构"
            style="width:80%"
            @select="bankChange"
          />
        </el-form-item>
        <el-form-item v-if="isEmp" label="员工" prop="empId">
          <el-select
            v-model="dataForm.empId"
            filterable
            :placeholder="!dataForm.bankId ? '请先选择所属机构':'请选择员工'"
            style="width:80%"
            clearable
            :disabled="!dataForm.bankId"
          >
            <el-option
              v-for="item in empOption"
              :key="item.id"
              :label="item.empNo + ' / ' + item.empName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <template v-else>
          <el-form-item label="柜员编号" prop="tellerNo">
            <el-input v-model="dataForm.tellerNo" :maxlength="64" style="width:80%" />
          </el-form-item>
          <el-form-item label="柜员姓名" prop="tellerName">
            <el-input v-model="dataForm.tellerName" :maxlength="64" style="width:80%" />
          </el-form-item>
          <el-form-item
            label="联系电话"
            prop="mobile"
            :rules="[{
              pattern: /^1[3456789]\d{9}$/,
              message: '手机号码格式不正确',
              trigger: 'blur'
            }]"
          >
            <el-input v-model="dataForm.mobile" :maxlength="64" style="width:80%" />
          </el-form-item>
          <el-form-item label="管理员标志" prop="managerFlag">
            <el-radio-group v-model="dataForm.managerFlag">
              <el-radio v-for="item in options.managerFlag" :key="item.code" :label="item.code">{{ item.content }}</el-radio>
            </el-radio-group>
          </el-form-item>
        </template>
        <el-form-item label="备注" prop="comments">
          <el-input v-model="dataForm.comments" :maxlength="64" style="width:80%" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="newData(dialogStatus)">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import Pagination from '@/components/Pagination'
import myTable from '@/components/Table'
// import searchBar from '@/components/SearchBar'
import { bankOption, addEmp, updateEmp, listEmp, quitEmp, backEmp, deleteEmp, resetEmp } from '@/api/tailBox/employee'
import { regionData, CodeToText } from 'element-china-area-data'
import TreeSelect from '@riophae/vue-treeselect'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
import { authOption, jobNameOption } from '@/api/common/selectOption'

export default {
  filters: {
    formatText(code) {
      return CodeToText[code] || ''
    }
  },
  components: { Pagination, myTable, TreeSelect },
  data() {
    return {
      authId: null,
      expandArr: [],
      list: [],
      listQuery: {
        limit: 10,
        page: 1,
        bankId: null
      },
      role: {
        list: '/base/atm/list'
      },
      listLoading: true,
      loading: null,
      total: 0,
      printList: [],
      options: {
        statusT: [
          { code: 0, content: '启用' },
          { code: 1, content: '停用' }
        ],
        denom: [100, 10],
        locationType: [
          { code: 1, content: '离行式' },
          { code: 2, content: '附行式' },
          { code: 3, content: '大堂式' }
        ],
        managerFlag: [
          { code: 0, content: '否' },
          { code: 1, content: '是' }
        ]
      },
      authOption: [],
      filterText: '',
      defaultProps: {
        children: 'children',
        label: 'name'
      },
      tableList: [
        {
          label: '员工编号',
          prop: 'tellerNo'
        },
        {
          label: '员工名称',
          prop: 'tellerName'
        },
        {
          label: '所属机构',
          prop: 'bankName'
        },
        {
          label: '是否管理员',
          prop: 'managerFlag',
          formatter: this.formatManFlag
        },
        {
          label: '联系电话',
          prop: 'mobile'
        },
        {
          label: '状态',
          prop: 'statusT',
          formatter: this.formatStatus
        }
      ],
      CodeToText,
      regionData,
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '创建'
      },
      oldNodeId: null,
      dataForm: {
        departmentId: null,
        bankId: null,
        comments: null,
        id: null,
        managerFlag: 0,
        mobile: null,
        tellerName: null,
        tellerNo: null,
        empId: null
      },
      bankOption: [],
      newBankOption: [],
      treeOptions: [],
      empOption: [],
      isEmp: true,
      rules: {
        managerFlag: [
          { required: true, message: '管理员标志不能为空', trigger: 'blur' }
        ],
        tellerName: [
          { required: true, message: '柜员姓名不能为空', trigger: 'blur' }
        ],
        tellerNo: [
          { required: true, message: '柜员编号不能为空', trigger: 'blur' }
        ],
        empId: [
          { required: true, message: '员工不能为空', trigger: 'blur' }
        ],
        bankId: [
          { required: true, message: '银行不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  watch: {
    filterText(val) {
      this.$refs.listTree.filter(val)
    }
  },
  mounted() {
    this.openLoading()
    this.getOptions().then(() => {
      this.getTree()
    })
  },
  methods: {
    getList(data) {
      this.listLoading = true
      if (data) {
        for (const key in data) {
          this.listQuery[key] = data[key]
        }
      }
      listEmp(this.listQuery).then((res) => {
        const data = res.data
        this.list = data.list
        this.total = data.total
        this.listLoading = false
      })
    },
    async getTree() {
      await bankOption(this.authId).then((res) => {
        this.bankOption = res.data
        if (this.bankOption.length > 0) {
          this.expandArr = [this.bankOption[0].id]
          this.$nextTick(() => {
            this.$refs.listTree.setCurrentKey(this.bankOption[0].id)
          })
          this.handleNodeClick(this.bankOption[0])
        } else {
          this.listLoading = false
        }
        this.loading.close()
      })
    },
    getOptions() {
      return new Promise((resolve, reject) => {
        Promise.all([
          authOption()
        ])
          .then((res) => {
            const [res1] = res
            this.authOption = res1.data
            if (this.authOption[0]) {
              this.authId = this.authOption[0].id
            }
            resolve()
          })
          .catch((err) => {
            this.flag = true
            reject(err)
          })
      })
    },
    handleNodeClick(val) {
      this.listQuery.bankId = val.id
      this.listQuery.page = 1
      this.getList()
    },
    handleCreate() {
      this.oldNodeId = null
      this.dialogStatus = 'create'
      this.isEmp = true
      this.newBankOption = this.bankOption
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.dataForm = {
          departmentId: null,
          bankId: null,
          comments: null,
          id: null,
          managerFlag: 0,
          mobile: null,
          tellerName: null,
          tellerNo: null,
          empId: null
        }
      })
    },
    handleUpdate(row) {
      this.oldNodeId = null
      this.dialogStatus = 'update'
      this.newBankOption = this.getNewBankOption(this.bankOption)
      this.dialogFormVisible = true
      if (row.bankCategory === 3) {
        this.isEmp = true
        jobNameOption(this.authId, '6').then(res => {
          this.empOption = res.data[6]
        })
      } else {
        this.isEmp = false
      }
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
        for (const key in this.dataForm) {
          this.dataForm[key] = row[key]
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
        deleteEmp(row.id)
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
    handleToggle(row) {
      let str
      if (row.statusT === 0) {
        str = '离职'
      } else {
        str = '复岗'
      }
      this.$confirm(`确定${str}吗?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.listLoading = true
        if (row.statusT === 0) {
          quitEmp(row.id)
            .then(() => {
              this.$notify.success({
                title: '成功',
                message: '离职成功'
              })
              this.getList()
            })
            .finally(() => {
              this.listLoading = false
            })
        } else {
          backEmp(row.id).then(() => {
            this.$notify.success({
              title: '成功',
              message: '复岗成功'
            })
            this.getList()
          })
            .finally(() => {
              this.listLoading = false
            })
        }
      })
    },
    handleReset(row) {
      this.$confirm('确定重置密码吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.listLoading = true
        resetEmp(row.id)
          .then(() => {
            this.$notify.success({
              title: '成功',
              message: '重置密码成功'
            })
            this.getList()
          })
          .finally(() => {
            this.listLoading = false
          })
      })
    },
    newData(type) {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          this.dataForm.departmentId = this.authId
          this.openLoading()
          if (type === 'update') {
            updateEmp(this.dataForm)
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
            addEmp(this.dataForm)
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
      })
    },
    formatStatus(status) {
      switch (status) {
        case 0:
          return '正常'
        case 1:
          return '离职'
      }
    },
    formatManFlag(type) {
      return this.options.managerFlag.find(item => item.code === type).content
    },
    filterNode(value, data) {
      if (!value) return true
      return data.name.indexOf(value) !== -1
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
    },
    bankChange(node) {
      if (node.id !== this.oldNodeId) {
        this.oldNodeId = node.id
        this.dataForm.empId = null
        if (node.bankCategory === 3) {
          this.isEmp = true
          this.dataForm.managerFlag = this.dataForm.mobile = this.dataForm.tellerName = this.dataForm.tellerNo = null
          jobNameOption(this.authId, '6').then(res => {
            this.empOption = res.data[6]
          })
        } else {
          this.dataForm.managerFlag = 0
          this.isEmp = false
        }
      }
    },
    getNewBankOption(option) {
      return option.map(item => {
        // item.bankCategory === 3 ? item.isDisabled = true : item.isDisabled = false
        if (item.children) {
          const list = this.getNewBankOption(item.children)
          return {
            ...item,
            children: list,
            isDisabled: item.bankCategory === 3
          }
        } else {
          return {
            ...item,
            isDisabled: item.bankCategory === 3
          }
        }
      })
    }
  }
}
</script>

<style scoped lang="scss">
.filter-container *:nth-child(n + 2) {
  margin-left: 10px;
}
.scrollbar {
  ::v-deep .el-scrollbar__wrap {
    overflow: auto;
  }
}
</style>
