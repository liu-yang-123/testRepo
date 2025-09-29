<template>
  <div class="app-container">

    <!-- 查询和其他操作 -->
    <search-bar :list-query="listQuery" :search-list="searchList" :role="role" @lookUp="handleFilter" @create="handleCreate" />
    <!-- 查询结果 -->
    <el-table
      v-loading="listLoading"
      element-loading-text="正在查询中。。。"
      :data="list"
      :header-cell-style="{'background-color':'#f5f5f5'}"
      border
      fit
    >
      <el-table-column align="center" label="角色名称" prop="roleName" sortable />
      <el-table-column align="center" label="说明" prop="describes" />
      <el-table-column
        v-if="hasPerm(['/base/role/update','/base/role/delete','/base/role/permissions/update'])"
        align="center"
        label="操作"
        class-name="small-padding fixed-width"
        width="240"
      >
        <template slot-scope="scope">
          <el-button
            v-permission="['/base/role/update']"
            type="primary"
            size="mini"
            @click="handleUpdate(scope.row)"
          >编辑</el-button>
          <el-button
            v-permission="['/base/role/delete']"
            type="danger"
            size="mini"
            @click="handleDelete(scope.row)"
          >删除</el-button>
          <el-button
            v-permission="['/base/role/permissions/get']"
            type="primary"
            size="mini"
            @click="handlePermission(scope.row)"
          >授权</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

    <!-- 添加或修改对话框 -->
    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible" :close-on-click-modal="false">
      <el-form ref="dataForm" :rules="rules" :model="dataForm" status-icon label-position="left" label-width="100px" style="width: 400px; margin-left:50px;">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="dataForm.roleName" :maxlength="32" />
        </el-form-item>
        <el-form-item label="说明" prop="describes">
          <el-input v-model="dataForm.describes" :maxlength="64" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button v-if="dialogStatus=='create'" type="primary" @click="createData">确定</el-button>
        <el-button v-else type="primary" @click="updateData">确定</el-button>
      </div>
    </el-dialog>

    <!-- 权限配置对话框 -->
    <el-dialog :visible.sync="permissionDialogFormVisible" title="权限配置">
      <el-tree
        ref="tree"
        :data="systemPermissions"
        :default-checked-keys="assignedPermissions"
        show-checkbox
        :default-expand-all="false"
        node-key="url"
      >
        <span slot-scope="{ data }" class="custom-tree-node">
          <span>{{ data.name }}</span>
        </span>
      </el-tree>
      <div slot="footer" class="dialog-footer">
        <el-button @click="permissionDialogFormVisible = false">取消</el-button>
        <el-button v-permission="['/base/role/permissions/update']" type="primary" @click="updatePermission">确定</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import { listRole, createRole, updateRole, deleteRole, getPermission, updatePermission } from '@/api/system/role'
import Pagination from '@/components/Pagination'
import checkPermission from '@/utils/permission'
import searchBar from '@/components/SearchBar'

export default {
  name: 'Role',
  components: { Pagination, searchBar },
  data() {
    return {
      list: null,
      total: 0,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 10
      },
      searchList: [
        { name: 'roleName', label: '角色名称' }
      ],
      role: {
        list: '/base/role/list',
        add: '/base/role/save'
      },
      dataForm: {},
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '创建'
      },
      rules: {
        roleName: [
          { required: true, message: '角色名称不能为空', trigger: 'blur' }
        ]
      },
      permissionDialogFormVisible: false,
      systemPermissions: null,
      assignedPermissions: null,
      permissionForm: {
        roleId: undefined,
        permissions: []
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.listLoading = true
      listRole(this.listQuery)
        .then(response => {
          this.list = response.data.list
          this.total = response.data.total
          this.listLoading = false
        })
        .catch(() => {
          this.list = []
          this.total = 0
          this.listLoading = false
        })
    },
    handleFilter() {
      this.listQuery.page = 1
      this.getList()
    },
    resetForm() {
      this.dataForm = {}
    },
    handleCreate() {
      this.resetForm()
      this.dialogStatus = 'create'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    createData() {
      this.$refs['dataForm'].validate(valid => {
        if (valid) {
          this.openLoading()
          createRole(this.dataForm)
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
    handleUpdate(row) {
      this.dataForm = Object.assign({}, row)
      this.dialogStatus = 'update'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    updateData() {
      this.$refs['dataForm'].validate(valid => {
        if (valid) {
          this.openLoading()
          updateRole(this.dataForm)
            .then(() => {
              for (const v of this.list) {
                if (v.id === this.dataForm.id) {
                  const index = this.list.indexOf(v)
                  this.list.splice(index, 1, this.dataForm)
                  break
                }
              }
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
    handleDelete(row) {
      this.$confirm('确定删除吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.openLoading()
        deleteRole(row)
          .then(response => {
            this.$notify.success({
              title: '成功',
              message: '删除成功'
            })
            const index = this.list.indexOf(row)
            this.list.splice(index, 1)
          }).finally(() => {
            this.loading.close()
          })
      })
    },
    handlePermission(row) {
      this.permissionDialogFormVisible = true
      this.permissionForm.roleId = row.id
      getPermission({ roleId: row.id })
        .then(response => {
          this.recursiveTree(response.data.systemPermissions)
          this.systemPermissions = response.data.systemPermissions
          this.assignedPermissions = response.data.assignedPermissions
        })
    },
    updatePermission() {
      this.permissionForm.permissions = this.$refs.tree.getCheckedKeys(true)
      this.openLoading()
      updatePermission(this.permissionForm)
        .then(response => {
          this.permissionDialogFormVisible = false
          this.$notify.success({
            title: '成功',
            message: '授权成功'
          })
        }).finally(() => {
          this.loading.close()
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
    // 递归树结构
    recursiveTree(tree) {
      // 格式化：将url为空的菜单更换为name
      tree.map(item => {
        if (!item.url) {
          item.url = item.name
        }

        if (item.children) {
          item.children.map(child => {
            child.label = child.name
          })
          this.recursiveTree(item.children)
        }
      })
    }
  }
}
</script>
