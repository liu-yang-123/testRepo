<template>
  <div class="app-container">
    <search-bar v-if="flag" :search-list="searchList" :list-query="listQuery" :options="options" :role="role" @lookUp="getList" @create="handleCreate" />
    <my-table
      :list-loading="listLoading"
      :data-list="list"
      :table-list="tableList"
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
              v-permission="['/base/pda/update']"
              type="primary"
              size="mini"
              @click="handleUpdate(scope.row)"
            >编辑</el-button>
            <el-button
              v-permission="['/base/pda/delete']"
              type="danger"
              size="mini"
              @click="handleDelete(scope.row)"
            >删除</el-button>
            <el-button
              v-if="scope.row.statusT === 0"
              v-permission="['/base/pda/stop']"
              type="warning"
              size="mini"
              @click="handleToggle(scope.row)"
            >停用</el-button>
            <el-button
              v-else
              v-permission="['/base/pda/enable']"
              type="success"
              size="mini"
              @click="handleToggle(scope.row)"
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

    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible" :close-on-click-modal="false">
      <el-form ref="dataForm" :rules="rules" :model="dataForm" status-icon label-position="right" label-width="100px" style="width: 400px; margin-left:50px;">
        <el-form-item label="所属部门" prop="departmentId">
          <el-select v-model="dataForm.departmentId" filterable placeholder="请选择" style="width: 100%" @change="departmentChange">
            <el-option
              v-for="item in depOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="用途" prop="useType">
          <el-radio v-for="item in options.useType" :key="item.code" v-model="dataForm.useType" :label="item.code">{{ item.content }}</el-radio>
        </el-form-item>
        <el-form-item v-if="dataForm.useType == 1 && dataForm.departmentId != null" label="银行名称" prop="bankId">
          <treeSelect
            v-model="dataForm.bankId"
            :disable-branch-nodes="true"
            :normalizer="normalizer"
            :show-count="true"
            :options="treeOptions"
            placeholder="请先选择所属部门"
          />
        </el-form-item>
        <el-form-item label="终端编号" prop="tersn">
          <el-input v-model="dataForm.tersn" :maxlength="64" />
        </el-form-item>
        <el-form-item label="物理地址" prop="mac">
          <el-input v-model="dataForm.mac" :maxlength="64" />
        </el-form-item>
        <el-form-item label="备注" prop="comments">
          <el-input v-model="dataForm.comments" :maxlength="64" />
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
import searchBar from '@/components/SearchBar'
import { listTerminal, addTerminal, updateTerminal, deleteTerminal, stopTerminal, enableTerminal } from '@/api/pda/terminal'
import { dictionaryData } from '@/api/system/dictionary'
import TreeSelect from '@riophae/vue-treeselect' // 引用下拉树组件
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
import { authOption, bankTree } from '@/api/common/selectOption'
export default {
  components: { Pagination, myTable, searchBar, TreeSelect },
  data() {
    return {
      flag: false,
      list: [],
      listQuery: {
        limit: 10,
        page: 1,
        statusT: null,
        tersn: null,
        useType: null,
        departmentId: null
      },
      listLoading: true,
      total: 0,
      searchList: [
        { name: 'departmentId', label: '所属部门', type: 3, notClear: true },
        { name: 'tersn', label: '终端编号' },
        { name: 'useType', label: '用途', type: 3 },
        { name: 'statusT', label: '状态', type: 3 }
      ],
      options: {
        statusT: [
          { code: 0, content: '启用' },
          { code: 1, content: '停用' }
        ],
        useType: [
          { code: 0, content: '公司' },
          { code: 1, content: '银行' }
        ]
      },
      role: {
        list: '/base/pda/list',
        add: '/base/pda/save'
      },
      tableList: [
        {
          label: '用途',
          prop: 'useType',
          formatter: this.formatUseType
        },
        {
          label: '银行名称',
          prop: 'bankName'
        },
        {
          label: '终端编号',
          prop: 'tersn'
        },
        {
          label: '物理地址',
          prop: 'mac'
        },
        {
          label: '状态',
          prop: 'statusT',
          formatter: this.formatStatus
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
      dataForm: {
        bankId: null,
        comments: null,
        id: null,
        mac: null,
        tersn: null,
        useType: null,
        departmentId: null
      },
      depOptions: [],
      treeOptions: [],
      dictionaryData,
      rules: {
        departmentId: [
          { required: true, message: '所属部门不能为空', trigger: 'blur' }
        ],
        tersn: [
          { required: true, message: '终端编号不能为空', trigger: 'blur' }
        ],
        useType: [
          { required: true, message: '用途不能为空', trigger: 'blur' }
        ],
        bankId: [
          { required: true, message: '银行名称不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  mounted() {
    this.getOptions()
  },
  methods: {
    getList(data) {
      if (data) {
        for (const key in data) {
          this.listQuery[key] = data[key]
        }
      }
      listTerminal(this.listQuery).then((res) => {
        const data = res.data
        this.list = data.list
        this.total = data.total
        this.listLoading = false
      })
    },
    getOptions() {
      authOption().then(res => {
        this.depOptions = res.data
        this.options.departmentId = res.data
        if (this.options.departmentId.length > 0) {
          this.listQuery.departmentId = this.options.departmentId[0].id
        }
        this.flag = true
        this.getList()
      })
    },
    handleCreate() {
      this.dialogStatus = 'create'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].resetFields()
        this.dataForm.departmentId = this.listQuery.departmentId
        bankTree(this.dataForm.departmentId).then(res => {
          this.treeOptions = res.data
        })
      })
    },
    handleUpdate(row) {
      //   this.dataForm = Object.assign({}, row)
      this.dialogStatus = 'update'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
        for (const key in this.dataForm) {
          this.dataForm[key] = row[key]
        }
        if (this.dataForm.departmentId != null) {
          bankTree(this.dataForm.departmentId).then(res => {
            this.treeOptions = res.data
          })
        }
        this.dataForm.bankId = this.dataForm.bankId === 0 ? null : this.dataForm.bankId
      })
    },
    handleDelete(row) {
      this.$confirm('确定删除吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.listLoading = true
        deleteTerminal(row.id)
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
        str = '停用'
      } else {
        str = '启用'
      }
      this.$confirm(`确定${str}吗?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.listLoading = true
        if (row.statusT === 0) {
          stopTerminal(row.id)
            .then(() => {
              this.$notify.success({
                title: '成功',
                message: '停用成功'
              })
              this.getList()
            })
            .finally(() => {
              this.listLoading = false
            })
        } else {
          enableTerminal(row.id).then(() => {
            this.$notify.success({
              title: '成功',
              message: '启用成功'
            })
            this.getList()
          })
            .finally(() => {
              this.listLoading = false
            })
        }
      })
    },
    newData(type) {
      if (this.dataForm.useType !== 1) {
        this.dataForm.bankId = 0
      }
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          this.listLoading = true
          if (type === 'update') {
            updateTerminal(this.dataForm)
              .then(() => {
                this.getList()
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
            addTerminal(this.dataForm)
              .then(() => {
                this.getList()
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
    formatStatus(status) {
      for (const item of this.options.statusT) {
        if (item.code === status) {
          return item.content
        }
      }
    },
    formatUseType(useType) {
      for (const item of this.options.useType) {
        if (item.code === useType) {
          return item.content
        }
      }
    },
    formatDepartment(id) {
      console.log('ceshi', this.depOptions)
      for (const item of this.depOptions) {
        if (item.id === id) {
          return item.name
        }
      }
    },
    normalizer(node) {
      return {
        id: node.id,
        label: node.name,
        children: node.children
      }
    },
    departmentChange(value) {
      bankTree(value).then(res => {
        this.treeOptions = res.data
        this.dataForm.bankId = null
      })
    }
  }
}
</script>

<style scoped lang="scss">
</style>
