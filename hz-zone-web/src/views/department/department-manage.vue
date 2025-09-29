<template>
  <div class="app-container">
    <div>
      <el-button
        class="filter-item"
        type="primary"
        icon="el-icon-refresh"
        @click="getTree"
      >刷新</el-button>
      <el-button
        v-permission="['/base/department/save']"
        class="filter-item"
        type="primary"
        icon="el-icon-edit"
        @click="handleCreate"
      >新增</el-button>
    </div>
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="5">
        <el-scrollbar v-loading="listLoading" class="scroll">
          <el-tree
            ref="listTree"
            :data="tree"
            :props="defaultProps"
            node-key="id"
            @node-click="handleNodeClick"
          />
        </el-scrollbar>
      </el-col>
      <el-col :span="19">
        <el-card v-loading="listLoading" v-permission="['/base/department/info']" class="box-card">
          <div slot="header" class="flex">
            <span>部门信息</span>
            <el-button
              v-permission="['/base/department/update']"
              type="primary"
              size="mini"
              plain
              icon="el-icon-edit"
              @click="handleUpdate(info)"
            >编辑</el-button>
          </div>
          <el-form label-position="left" inline class="demo-table-expand-1">
            <el-form-item label="部门名称">
              <span>{{ info.name }}</span>
            </el-form-item>
            <el-form-item label="部门描述">
              <span>{{ info.descript }}</span>
            </el-form-item>
            <el-form-item label="上级部门">
              <span>{{ info.parentName }}</span>
            </el-form-item>
            <el-form-item label="负责人">
              <span>{{ info.linkmanName }}</span>
            </el-form-item>
            <el-form-item label="联系电话">
              <span>{{ info.linkmanMobile }}</span>
            </el-form-item>
            <el-form-item label="创建时间">
              <span>{{ info.createTime | formatDateTime }}</span>
            </el-form-item>
            <el-form-item label="更新时间">
              <span>{{ info.updateTime | formatDateTime }}</span>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
    <el-dialog
      :title="textMap[dialogStatus]"
      :visible.sync="dialogFormVisible"
      :close-on-click-modal="false"
    >
      <el-form
        ref="dataForm"
        :rules="rules"
        :model="dataForm"
        status-icon
        label-position="left"
        label-width="100px"
        style="width: 80%;margin-left:8%"
      >
        <el-form-item label="上级部门" prop="parentIds">
          <treeSelect
            v-model="dataForm.parentIds"
            :normalizer="normalizer"
            :show-count="true"
            :options="tree"
            placeholder="选择上级菜单"
            style="width:40%"
          />
        </el-form-item>
        <el-form-item label="部门名称" prop="name">
          <el-input v-model="dataForm.name" :maxlength="32" style="width:40%" />
        </el-form-item>
        <el-form-item label="部门描述" prop="description">
          <el-input v-model="dataForm.description" :maxlength="64" style="width:40%" />
        </el-form-item>
        <el-form-item label="负责人姓名" prop="linkmanName">
          <el-input v-model="dataForm.linkmanName" style="width:40%" />
        </el-form-item>
        <el-form-item label="联系电话	" prop="linkmanMobile">
          <el-input v-model="dataForm.linkmanMobile" style="width:40%" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button
          type="primary"
          @click="newData(dialogStatus)"
        >确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listDepartmentTree,
  listDepartment,
  addDepartment,
  updateDepartment
} from '@/api/department/departmentManage'
import { formatdate } from '@/utils/date'
import { validatePhone } from '@/utils/validate'
import TreeSelect from '@riophae/vue-treeselect'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'

export default {
  components: { TreeSelect },
  filters: {
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
    }
  },
  data() {
    return {
      tree: [],
      listLoading: true,
      defaultProps: {
        children: 'children',
        label: 'name'
      },
      info: [],
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '创建'
      },
      dataForm: {
        id: null,
        name: '',
        description: '',
        linkmanMobile: '',
        linkmanName: '',
        parentIds: null
      },
      rules: {
        name: [
          { required: true, message: '部门名称不能为空', trigger: 'blur' }
        ],
        linkmanMobile: [
          { required: false, validator: validatePhone, trigger: 'blur' }
        ]
      }
    }
  },
  mounted() {
    this.getTree()
  },
  methods: {
    getTree() {
      this.listLoading = true
      listDepartmentTree()
        .then((res) => {
          this.tree = res.data
          if (this.tree.length > 0) {
            this.handleNodeClick(this.tree[0])
          }
        })
        .finally(() => {
          this.listLoading = false
        })
    },
    handleNodeClick(data) {
      listDepartment(data.id).then((res) => {
        this.info = res.data
        console.log(this.info)
        this.info.id = data.id
        this.info.parentIds = data.pid
      })
    },
    handleCreate() {
      this.dialogStatus = 'create'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].resetFields()
      })
    },
    handleUpdate(data) {
      this.dialogStatus = 'update'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].resetFields()
        for (const key in this.dataForm) {
          this.dataForm[key] = this.info[key]
        }
        if (this.dataForm.parentIds === '0') {
          this.dataForm.parentIds = null
        }
      })
    },
    newData(type) {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          this.listLoading = true
          if (this.dataForm.parentIds == null) {
            this.dataForm.parentIds = 0
          }
          if (type === 'update') {
            updateDepartment(this.dataForm)
              .then(() => {
                this.getTree()
                this.dialogFormVisible = false
                this.$notify.success({
                  title: '成功',
                  message: '更新成功'
                })
              })
              .finally(() => {
                this.listLoading = false
              })
          } else {
            addDepartment(this.dataForm)
              .then(() => {
                this.getTree()
                this.dialogFormVisible = false
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
    normalizer(node) {
      return {
        id: node.id,
        label: node.name,
        children: node.children
      }
    },
    // 注册
    validatePhone,
    formatdate
  }
}
</script>
<style lang="scss" scoped>
.box-card {
  width: 100%;
}
.flex {
  display: flex;
  justify-content: space-between;
}
</style>
<style lang="scss">
.demo-table-expand-1 label {
  width: 100px !important;
}
.demo-table-expand-1 .el-form-item {
  margin-right: 0;
  margin-bottom: 0;
  width: 100%;
}
.scroll {
  height: 500px;
  border-right: 1px solid #e6ebf5;
  .el-scrollbar__wrap {
    overflow-x: auto;
  }
}
</style>
