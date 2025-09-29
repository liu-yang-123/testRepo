<template>
  <div class="app-container">
    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <treeSelect
        v-model="listQuery.departmentId"
        :normalizer="normalizer"
        :show-count="true"
        :options="departmentTree"
        :clearable="false"
        placeholder="选择上级部门"
        class="filter-item"
        style="
          width: 200px;
          display: inline-block;
          margin-left: 6px;
          font-size: 14px;
        "
      />
      <el-select
        v-model="listQuery.jobId"
        placeholder="请选择岗位"
        class="filter-item"
        clearable
        style="width: 200px"
      >
        <el-option
          v-for="item in jobTree"
          :key="item.id"
          :label="item.name"
          :value="item.id"
        />
      </el-select>
      <el-input v-model="listQuery.empNo" clearable class="filter-item" style="width: 200px;" placeholder="请输入员工编号" :maxlength="32" />
      <el-input v-model="listQuery.empName" clearable class="filter-item" style="width: 200px;" placeholder="请输入员工名称" :maxlength="32" />
      <el-select
        v-model="listQuery.pdaEnable"
        clearable
        placeholder="PDA状态"
        class="filter-item"
      >
        <el-option
          v-for="item in pdaOption"
          :key="item.code"
          :label="item.content"
          :value="item.code"
        />
      </el-select>
      <el-button v-permission="['/base/pdaUser/list']" class="filter-item" type="primary" icon="el-icon-search" @click="getList">查找</el-button>
    </div>

    <!-- 查询结果 -->
    <my-table
      :list-loading="listLoading"
      :data-list="list"
      :table-list="tableList"
      :need-index="true"
    >
      <template v-slot:operate>
        <el-table-column
          align="center"
          label="pda管理员"
          class-name="small-padding fixed-width"
          width="100"
        >
          <template slot-scope="scope">
            <el-switch
              v-model="scope.row.pdaAdmin"
              :inactive-value="0"
              :active-value="1"
              active-color="#13ce66"
              @change="pdaAdminChange($event,scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          label="操作"
          class-name="small-padding fixed-width"
          width="240"
        >
          <template slot-scope="scope">
            <el-button
              v-permission="['/base/pdaUser/resetpwd']"
              type="warning"
              size="mini"
              @click="handleReset(scope.row)"
            >重置</el-button>
            <el-button
              v-if="scope.row.pdaEnable === 1"
              v-permission="['/base/pdaUser/stop']"
              type="danger"
              size="mini"
              @click="handleStop(scope.row)"
            >停用</el-button>
            <el-button
              v-else
              v-permission="['/base/pdaUser/enable']"
              type="success"
              size="mini"
              @click="hanleEnable(scope.row)"
            >启用</el-button>
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

  </div>
</template>

<script>
import {
  listUser,
  createUser,
  updateUser,
  stopUser,
  enableUser,
  resetPwd,
  setAdmin
} from '@/api/pda/user'
import Pagination from '@/components/Pagination'
import checkPermission from '@/utils/permission'
import TreeSelect from '@riophae/vue-treeselect' // 引用下拉树组件
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
import { authTree, RoleOption, jobTree } from '@/api/common/selectOption'
import myTable from '@/components/Table'
export default {
  name: 'User',
  components: { Pagination, TreeSelect, myTable },
  data() {
    return {
      flag: false,
      list: [],
      total: 0,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 10,
        departmentId: null,
        empName: null,
        empNo: null,
        pdaEnable: null,
        jobId: null
      },
      pdaOption: [
        { code: 0, content: '未启用' },
        { code: 1, content: '已启用' }
      ],
      tableList: [
        { label: '部门名称', prop: 'departmentName' },
        { label: '员工编号', prop: 'empNo' },
        { label: '员工名称', prop: 'empName' },
        { label: '岗位名称', prop: 'jobName' },
        { label: '职务', prop: 'title', formatter: this.formatTitle },
        { label: 'PDA状态', prop: 'pdaEnable', formatter: this.formatStatus }
      ],
      dataForm: {
        roleId: null,
        id: null
      },
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '创建'
      },
      rules: {
        roleId: [
          { required: true, message: '用户名称不能为空', trigger: 'blur' }
        ]
      },
      departmentTree: [],
      roleOption: [],
      jobTree: []
    }
  },
  created() {
    this.getDictionary().then(() => {
      this.getList()
    })
  },
  methods: {
    formatJob(type) {
      if (type) {
        const arr = this.convertRoleIdsToList(type)
        let str = ''
        for (const val of arr) {
          for (const elm of this.jobTree) {
            if (+val === elm.id) {
              str += `${elm.name},`
            }
          }
        }
        return str.substring(0, str.length - 1)
      }
    },
    formatStatus(status) {
      for (const item of this.pdaOption) {
        if (status === item.code) {
          return item.content
        }
      }
    },
    formatTitle(title) {
      switch (title) {
        case 0:
          return '员工'
        case 1:
          return '主管'
      }
    },
    getList() {
      this.listLoading = true
      listUser(this.listQuery)
        .then((response) => {
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
    convertRoleIdsToString(roleIds) {
      let roleIdsString = ''
      roleIds.forEach((elm) => {
        roleIdsString += '/' + elm
      })
      if (roleIdsString.length > 0) {
        roleIdsString += '/'
      }
      return roleIdsString
    },
    convertRoleIdsToList(idsString) {
      // 转字符串为多选
      const ids = idsString.substring(1, idsString.length - 1).split('/')
      return ids.map(Number)
    },
    // 加载选项
    getDictionary() {
      return new Promise((resolve, reject) => {
        Promise.all([
          authTree(),
          jobTree(),
          RoleOption()
        ])
          .then((res) => {
            const [res1, res2, res3] = res
            this.departmentTree = res1.data
            this.jobTree = res2.data
            this.roleOption = res3.data
            if (this.departmentTree.length > 0) {
              this.listQuery.departmentId = this.departmentTree[0].id
            }
            this.roleOption.forEach((elm) => {
              elm.value = elm.value.toString()
            })
            resolve()
          })
          .catch((err) => {
            reject(err)
          })
      })
    },
    handleCreate() {
      this.dialogStatus = 'create'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].resetFields()
      })
    },
    createData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          this.openLoading()
          createUser(this.dataForm)
            .then((response) => {
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
    updateData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          this.openLoading()
          updateUser(this.dataForm)
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
        }
      })
    },
    handleStop(row) {
      this.$confirm('确定停用吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.openLoading()
        stopUser(row.id)
          .then((response) => {
            this.getList()
            this.$notify.success({
              title: '成功',
              message: '停用成功'
            })
          })
          .finally(() => {
            this.loading.close()
          })
      })
    },
    hanleEnable(row) {
      this.$confirm('确定启用吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.openLoading()
        enableUser(row.id)
          .then((response) => {
            this.getList()
            this.$notify.success({
              title: '成功',
              message: '启用成功'
            })
          })
          .finally(() => {
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
          .then((response) => {
            this.getList()
            this.$notify.success({
              title: '成功',
              message: '重置成功'
            })
          })
          .finally(() => {
            this.loading.close()
          })
      })
    },
    pdaAdminChange(val, row) {
      setAdmin(val, row.id).then(() => {
        this.$notify.success({
          title: '成功',
          message: '设置PDA管理员成功'
        })
      }).catch(() => {
        row.pdaAdmin = !val
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
    }
  }
}
</script>
<style lang="scss" scoped>
.filter-container *:nth-child(n+2) {
  margin-left: 10px;
}
</style>
