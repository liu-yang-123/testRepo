<template>
  <div class="app-container">

    <!-- 查询和其他操作 -->
    <search-bar :list-query="listQuery" :search-list="searchList" :role="role" @lookUp="handleFilter" @create="handleCreate">
      <template v-slot:more>
        <el-button
          v-permission="['/base/cashbox/produce']"
          class="filter-item"
          type="primary"
          icon="el-icon-edit"
          @click="handleCreateBank"
        >添加银行用户</el-button>
      </template>
    </search-bar>

    <!-- 查询结果 -->
    <el-table
      v-loading="listLoading"
      element-loading-text="正在查询中。。。"
      :data="list"
      :header-cell-style="{'background-color':'#f5f5f5'}"
      border
      fit
    >

      <!--列表显示-->
      <el-table-column align="center" label="序号" width="50">
        <template slot-scope="scope">
          {{ scope.$index+1 }}
        </template>
      </el-table-column>
      <el-table-column align="center" label="用户名称" prop="username" />
      <el-table-column align="center" label="真实姓名" prop="nickName" />
      <el-table-column align="center" label="用户角色" prop="roleIds">
        <template slot-scope="scope">
          <el-tag v-for="roleId in scope.row.roleIds" :key="roleId" type="primary" style="margin-right: 20px;"> {{ formatRole(roleId) }} </el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="权限部门" prop="authDepartments">
        <template slot-scope="scope">
          <el-tag v-for="authDepartment in scope.row.authDepartments" :key="authDepartment" type="primary" style="margin-right: 20px;"> {{ formatAuth(authDepartment) }} </el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="所属银行" prop="bankName" />
      <el-table-column align="center" label="库存网点" prop="stockBankName" />
      <!--操作-->
      <el-table-column
        v-if="hasPerm(['/base/user/resetpwd','/base/user/update','/base/user/delete'])"
        align="center"
        label="操作"
        class-name="small-padding fixed-width"
        width="240"
      >
        <template slot-scope="scope">
          <el-button v-permission="['/base/user/resetpwd']" type="warning" size="mini" @click="handleReset(scope.row)">重置</el-button>
          <el-button v-permission="['/base/user/update']" type="primary" size="mini" @click="handleUpdate(scope.row)">编辑</el-button>
          <el-button v-permission="['/base/user/delete']" type="danger" size="mini" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

    <!-- 添加或修改对话框 -->
    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible" :close-on-click-modal="false">
      <el-form ref="dataForm" :rules="rules" :model="dataForm" status-icon label-position="left" label-width="100px" style="width: 400px; margin-left:50px;">
        <el-form-item label="用户名称" prop="username">
          <el-input v-model="dataForm.username" :maxlength="32" />
        </el-form-item>
        <el-form-item label="真实姓名" prop="nickName">
          <el-input v-model="dataForm.nickName" :maxlength="32" />
        </el-form-item>
        <el-form-item label="用户角色" prop="roleIds">
          <el-select v-model="dataForm.roleIds" multiple placeholder="请选择" style="width: 100%">
            <el-option
              v-for="item in roleOption"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="权限部门" prop="authDepartments">
          <el-select v-model="dataForm.authDepartments" multiple placeholder="选择权限部门" style="width: 100%">
            <el-option
              v-for="item in authOption"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button v-if="dialogStatus=='create'" type="primary" @click="createData">确定</el-button>
        <el-button v-else type="primary" @click="updateData">确定</el-button>
      </div>
    </el-dialog>

    <!-- 添加银行用户 -->
    <el-dialog :title="textMap[dialogStatus]" :visible.sync="bankFormVisible" :close-on-click-modal="false">
      <el-form ref="bankForm" :rules="rules" :model="bankForm" status-icon label-position="left" label-width="100px" style="width: 400px; margin-left:50px;">
        <el-form-item label="用户名称" prop="username">
          <el-input v-model="bankForm.username" :maxlength="32" />
        </el-form-item>
        <el-form-item label="真实姓名" prop="nickName">
          <el-input v-model="bankForm.nickName" :maxlength="32" />
        </el-form-item>
        <el-form-item label="用户角色" prop="roleIds">
          <el-select v-model="bankForm.roleIds" multiple placeholder="请选择" style="width: 100%">
            <el-option
              v-for="item in roleOption"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="所属部门" prop="authDepartments">
          <el-select v-model="bankForm.authDepartments" placeholder="选择所属部门" style="width: 100%" @change="depChange">
            <el-option
              v-for="item in authOption"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="所属银行" prop="bankId">
          <el-select v-model="bankForm.bankId" :disabled="bankForm.authDepartments == null" placeholder="选择所属银行" style="width: 100%">
            <el-option
              v-for="item in bankOption"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="库存网点" prop="stockBank">
          <el-select v-model="bankForm.stockBank" multiple placeholder="选择库存网点" style="width: 100%">
            <el-option
              v-for="item in allBankOption"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="bankFormVisible = false">取消</el-button>
        <el-button v-if="dialogStatus=='create'" type="primary" @click="createBankData">确定</el-button>
        <el-button v-else type="primary" @click="updateBankData">确定</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import { listUser, createUser, updateUser, deleteUser, resetPwd, createBankUser } from '@/api/system/user'
import Pagination from '@/components/Pagination'
import checkPermission from '@/utils/permission'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
import { DepartmentTree, RoleOption, departmentOption, bankClearTopBank, allBankOption } from '@/api/common/selectOption'
import searchBar from '@/components/SearchBar'
export default {
  name: 'User',
  components: { Pagination, searchBar },
  data() {
    return {
      list: null,
      total: 0,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 10,
        userName: null
      },
      searchList: [
        { name: 'userName', label: '用户名称' }
      ],
      role: {
        list: '/base/user/list',
        add: '/base/user/save'
      },
      dataForm: {
        roleIds: [],
        authDepartments: []
      },
      bankFormVisible: false,
      bankForm: {
        username: null,
        nickName: null,
        roleIds: [],
        authDepartments: null,
        bankId: null,
        stockBank: []
      },
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '创建'
      },
      rules: {
        username: [
          { required: true, message: '用户名称不能为空', trigger: 'blur' }
        ],
        nickName: [
          { required: true, message: '真实姓名不能为空', trigger: 'blur' }
        ],
        authDepartments: [
          { required: true, message: '权限部门不能为空', trigger: 'blur' }
        ],
        roleIds: [
          { required: true, message: '用户角色不能为空', trigger: 'blur' }
        ],
        bankId: [
          { required: true, message: '所属银行不能为空', trigger: 'blur' }
        ]
      },
      departmentTree: [],
      roleOption: [],
      authOption: [],
      bankOption: [],
      allBankOption: []
    }
  },
  created() {
    this.getList()
    this.loadSelectOptions()
  },
  methods: {
    formatRole(roleId) {
      for (let i = 0; i < this.roleOption.length; i++) {
        if (roleId === this.roleOption[i].value) {
          return this.roleOption[i].label
        }
      }
      return ''
    },
    formatAuth(authId) {
      for (const item of this.authOption) {
        if (authId === item.id) {
          return item.name
        }
      }
    },
    getList() {
      this.listLoading = true
      listUser(this.listQuery)
        .then(response => {
          this.list = response.data.list
          this.total = response.data.total
          this.listLoading = false
          this.list.forEach(elm => {
            elm.authDepartments = this.convertRoleIdsToList(elm.authDepartments)
            elm.roleIds = this.convertRoleIdsToList(elm.roleIds)
          })
        })
        .catch(() => {
          this.list = []
          this.total = 0
          this.listLoading = false
        })
    },
    convertRoleIdsToString(roleIds) {
      let roleIdsString = ''
      roleIds.forEach(elm => {
        roleIdsString += '/' + elm
      })
      if (roleIdsString.length > 0) {
        roleIdsString += '/'
      }
      return roleIdsString
    },
    convertRoleIdsToList(roleIds) {
      const ids = roleIds.substring(1, roleIds.length - 1).split('/')
      return ids
    },
    // 加载选项
    loadSelectOptions() {
      return new Promise((resolve, reject) => {
        Promise.all([
          DepartmentTree(),
          RoleOption(),
          departmentOption(),
          allBankOption()
        ]).then(res => {
          const [res1, res2, res3, res4] = res
          this.departmentTree = res1.data
          this.roleOption = res2.data
          this.authOption = res3.data
          this.allBankOption = res4.data
          this.authOption.forEach(elm => {
            elm.id = elm.id.toString()
          })
          this.roleOption.forEach(elm => {
            elm.value = elm.value.toString()
          })
          resolve()
        }).catch(err => {
          reject(err)
        })
      })
    },
    handleFilter() {
      this.listQuery.page = 1
      this.getList()
    },
    resetForm() {
      this.dataForm = {
        roleIds: []
      }
    },
    handleCreate() {
      this.resetForm()
      this.dialogStatus = 'create'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    handleCreateBank() {
      this.dialogStatus = 'create'
      this.bankFormVisible = true
      this.$nextTick(() => {
        this.$refs['bankForm'].resetFields()
      })
    },
    createData() {
      this.$refs['dataForm'].validate(valid => {
        if (valid) {
          this.dataForm.roleIds = this.convertRoleIdsToString(this.dataForm.roleIds)
          this.dataForm.authDepartments = this.convertRoleIdsToString(this.dataForm.authDepartments)
          this.openLoading()
          createUser(this.dataForm)
            .then(response => {
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
      })
    },
    createBankData() {
      this.$refs['bankForm'].validate(valid => {
        if (valid) {
          this.bankForm.roleIds = this.convertRoleIdsToString(this.bankForm.roleIds)
          this.bankForm.authDepartments = `/${this.bankForm.authDepartments}/`
          const { stockBank, ...newForm } = this.bankForm
          newForm.stockBank = stockBank.toString()
          this.openLoading()
          createBankUser(newForm)
            .then(res => {
              this.getList()
              this.bankFormVisible = false
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
    handleUpdate(row) {
      this.dialogStatus = 'update'
      if (!row.bankName) {
        this.dataForm = Object.assign({}, row)
        this.dialogFormVisible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].clearValidate()
        })
      } else {
        this.bankForm = Object.assign({}, row)
        this.bankForm.authDepartments = this.bankForm.authDepartments[0]
        this.bankForm.stockBank = row.stockBank ? row.stockBank.split(',').map(num => +num) : []
        bankClearTopBank(this.bankForm.authDepartments).then(res => {
          this.bankOption = res.data
          this.bankFormVisible = true
          this.$nextTick(() => {
            this.$refs['bankForm'].clearValidate()
          })
        })
      }
    },
    updateData() {
      this.$refs['dataForm'].validate(valid => {
        if (valid) {
          this.openLoading()
          this.dataForm.roleIds = this.convertRoleIdsToString(this.dataForm.roleIds)
          this.dataForm.authDepartments = this.convertRoleIdsToString(this.dataForm.authDepartments)
          updateUser(this.dataForm)
            .then(() => {
              this.getList()
              this.dialogFormVisible = false
              this.$notify.success({
                title: '成功',
                message: '更新成功'
              })
            }).finally(() => {
              this.loading.close()
            })
        }
      })
    },
    updateBankData() {
      this.$refs['bankForm'].validate(valid => {
        if (valid) {
          this.openLoading()
          this.bankForm.roleIds = this.convertRoleIdsToString(this.bankForm.roleIds)
          this.bankForm.authDepartments = `/${this.bankForm.authDepartments}/`
          const { stockBank, ...newForm } = this.bankForm
          newForm.stockBank = stockBank.toString()
          updateUser(newForm)
            .then(() => {
              this.getList()
              this.bankFormVisible = false
              this.$notify.success({
                title: '成功',
                message: '更新成功'
              })
            }).finally(() => {
              this.loading.close()
            })
        }
      })
    },
    handleDelete(row) {
      this.$confirm('确定删除吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.openLoading()
        deleteUser(row)
          .then(response => {
            this.getList()
            this.$notify.success({
              title: '成功',
              message: '删除成功'
            })
          }).finally(() => {
            this.loading.close()
          })
      })
    },
    handleReset(row) {
      this.$confirm('确定重置该用户密码吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.openLoading()
        resetPwd(row.id)
          .then(response => {
            this.getList()
            this.$notify.success({
              title: '成功',
              message: '重置成功'
            })
          }).finally(() => {
            this.loading.close()
          })
      })
    },
    // 检查权限
    hasPerm(value) {
      return checkPermission(value)
    },
    // 添加时等待滚动条
    openLoading() {
      this.loading = this.$loading({
        lock: true,
        text: '正在加载...请勿进行其它操作',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      })
    },
    normalizer(node) {
      return {
        id: node.id,
        label: node.name,
        children: node.children
      }
    },
    depChange(id) {
      bankClearTopBank(id).then(res => {
        this.bankOption = res.data
      })
    }
  }
}
</script>
