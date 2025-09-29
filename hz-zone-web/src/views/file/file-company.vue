<template>
  <div class="app-container">
    <search-bar :search-list="searchList" :list-query="listQuery" :options="options" :role="role" @lookUp="getList" @create="handleCreate" />
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
              v-if="scope.row.statusT === 1"
              v-permission="['/base/fileCompany/enable']"
              type="success"
              size="mini"
              @click="handleEnable(scope.row.id)"
            >启用</el-button>
            <el-button
              v-else
              v-permission="['/base/fileCompany/stop']"
              type="warning"
              size="mini"
              @click="handleStop(scope.row.id)"
            >停用</el-button>
            <el-button
              v-permission="['/base/fileCompany/update']"
              type="primary"
              size="mini"
              @click="handleUpdate(scope.row)"
            >编辑</el-button>
            <el-button
              v-permission="['/base/fileCompany/delete']"
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

    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible" :close-on-click-modal="false">
      <el-form ref="dataForm" :rules="rules" :model="dataForm" status-icon label-position="right" label-width="100px" style="width: 400px; margin-left:50px;">
        <el-form-item label="单位号" prop="companyNo">
          <el-input v-model="dataForm.companyNo" :maxlength="64" />
        </el-form-item>
        <el-form-item label="单位名称" prop="companyName">
          <el-input v-model="dataForm.companyName" :maxlength="64" />
        </el-form-item>
        <el-form-item label="类别" prop="companyType">
          <el-radio-group v-model="dataForm.companyType">
            <el-radio :label="0">公司</el-radio>
            <el-radio :label="1">银行</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="文件目录" prop="fileDirectory">
          <el-input v-model="dataForm.fileDirectory" :maxlength="64" />
        </el-form-item>
        <el-form-item label="操作员" prop="userIds">
          <el-select v-model="dataForm.userIds" filterable placeholder="请选择" multiple style="width: 100%">
            <el-option
              v-for="item in options.userIds"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
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
import { listCompany, addCompany, deleteCompany, updateCompany, enableCompany, stopCompany } from '@/api/file/fileCompany'
import { fileCompanyUserOption } from '@/api/common/selectOption'
export default {
  components: { Pagination, myTable, searchBar },
  data() {
    return {
      list: [],
      listQuery: {
        limit: 10,
        page: 1,
        userName: null,
        result: null,
        type: null
      },
      listLoading: true,
      total: 0,
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '创建'
      },
      searchList: [
        { name: 'companyName', label: '单位名称' }
      ],
      options: {
        userIds: [],
        type: [
          { code: '0', content: '添加' },
          { code: '1', content: '修改' },
          { code: '2', content: '删除' }
        ]
      },
      role: {
        list: '/base/fileCompany/list',
        add: '/base/fileCompany/save'
      },
      tableList: [
        {
          label: '单位号',
          prop: 'companyNo'
        },
        {
          label: '单位名称',
          prop: 'companyName'
        },
        {
          label: '类别',
          prop: 'companyType',
          formatter: this.formatCompanyType
        },
        {
          label: '文件目录',
          prop: 'fileDirectory'
        },
        {
          label: '操作员',
          prop: 'operator'
        },
        {
          label: '状态',
          prop: 'statusT',
          formatter: this.formatStatusT
        }
      ],
      dataForm: {
        companyNo: null,
        companyName: null,
        companyType: 0,
        fileDirectory: null,
        userIds: [],
        id: null
      },
      rules: {
        companyNo: [
          { required: true, message: '单位号不能为空', trigger: 'blur' }
        ],
        companyName: [
          { required: true, message: '单位名称不能为空', trigger: 'blur' }
        ],
        companyType: [
          { required: true, message: '类别不能为空', trigger: 'blur' }
        ],
        fileDirectory: [
          { required: true, message: '文件目录不能为空', trigger: 'blur' }
        ],
        userIds: [
          { required: true, message: '操作员不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  mounted() {
    this.getList()

    fileCompanyUserOption().then(res => {
      this.options.userIds = res.data
    })
  },
  methods: {
    getList() {
      listCompany(this.listQuery).then((res) => {
        const data = res.data
        this.list = data.list
        this.total = data.total
        this.listLoading = false
      }).catch(() => {
        this.list = []
      })
    },
    handleCreate() {
      this.dialogStatus = 'create'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].resetFields()
      })
    },
    handleUpdate(row) {
      this.dialogStatus = 'update'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
        for (const key in this.dataForm) {
          if (key !== 'userIds') {
            this.dataForm[key] = row[key]
          }
        }
        this.dataForm.userIds = row.userIds.split('/').map(str => +str)
      })
    },
    handleDelete(row) {
      this.$confirm('确定删除吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.listLoading = true
        deleteCompany(row.id)
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
    handleEnable(id) {
      this.$confirm('确定启用吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.listLoading = true
        enableCompany(id)
          .then(() => {
            this.$notify.success({
              title: '成功',
              message: '启用成功'
            })
            this.getList()
          }).finally(() => {
            this.listLoading = false
          })
      })
    },
    handleStop(id) {
      this.$confirm('确定停用吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.listLoading = true
        stopCompany(id)
          .then(() => {
            this.$notify.success({
              title: '成功',
              message: '停用成功'
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
          this.listLoading = true
          const { userIds, ...newForm } = this.dataForm
          newForm.userIds = userIds.join('/')
          if (type === 'update') {
            updateCompany(newForm)
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
            addCompany(newForm)
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
    formatCompanyType(type) {
      switch (type) {
        case 0:
          return '公司'
        case 1:
          return '银行'
      }
    },
    formatStatusT(status) {
      switch (status) {
        case 0:
          return '启用'
        case 1:
          return '停用'
      }
    }
  }
}
</script>

  <style scoped lang="scss">
  </style>

