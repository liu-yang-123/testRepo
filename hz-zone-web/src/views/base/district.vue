<template>
  <div class="app-container">
    <div class="filter-container">
      <el-input v-model="listQuery.name" clearable class="filter-item" style="width: 200px;" placeholder="请输入区域名称" :maxlength="32" />
      <el-button v-permission="['/base/jobs/list']" class="filter-item" type="primary" icon="el-icon-search" @click="getList()">查找</el-button>
      <el-button v-permission="['/base/jobs/save']" class="filter-item" type="primary" icon="el-icon-plus" @click="handleCreate">添加</el-button>
      <el-button v-permission="['/base/jobs/list']" class="filter-item" type="primary" icon="el-icon-refresh" @click="getList()">刷新</el-button>
      </div>

    <my-table :list-loading="listLoading" :list-query="listQuery" :data-list="list" :table-list="tableList">
      <template v-slot:operate>
        <el-table-column
          align="center"
          label="操作"
          class-name="small-padding fixed-width"
          width="240"
        >
          <template slot-scope="scope">
            <el-button
              v-permission="['/base/jobs/update']"
              type="primary"
              size="mini"
              @click="handleUpdate(scope.row)"
            >编辑</el-button>
            <el-button
              v-permission="['/base/jobs/delete']"
              type="danger"
              size="mini"
              @click="handleDelete(scope.row)"
            >删除</el-button>
          </template>
        </el-table-column>
      </template>
    </my-table>
    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible" :close-on-click-modal="false">
      <el-form ref="dataForm" :rules="rules" :model="dataForm" status-icon label-position="right" label-width="100px" style="width: 400px; margin-left:50px;">
        <el-form-item label="区域名称" prop="name">
          <el-input v-model="dataForm.name" :maxlength="64" />
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
import { listStation, deleteStation, addStation, updateStation } from '@/api/base/district'

export default {
  components: { Pagination, myTable },
  data() {
    return {
      list: [],
      listQuery: {
        limit: 10,
        page: 1,
        name: null
      },
      listLoading: true,
      total: 0,
      tableList: [
        {
          label: '区域名称',
          prop: 'districtName'
        }
      ],
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '创建'
      },
      dataForm: {
        name: '',
        id: null
      },
      rules: {
        name: [
          { required: true, message: '区域名称不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  mounted() {
    this.getList()
  },
  methods: {
    getList() {
      listStation(this.listQuery).then(res => {
        this.listLoading = false
        const data = res.data
        console.log(this.listQuery)
        this.list = data.list
        this.total = data.total
      }).catch(() => {
        this.listLoading = false
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
    //   this.dataForm = Object.assign({}, row)
      this.dialogStatus = 'update'
      this.dialogFormVisible = true
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
        deleteStation(row.id)
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
      this.$refs['dataForm'].validate(valid => {
        if (valid) {
          this.listLoading = true
          if (type === 'update') {
            updateStation(this.dataForm)
              .then(() => {
                this.getList()
                this.dialogFormVisible = false
                this.$notify.success({
                  title: '成功',
                  message: '更新成功'
                })
              }).finally(() => {
                this.listLoading = false
              })
          } else {
            addStation(this.dataForm)
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
    formatJob(type) {
      if (type != null) {
        for (const item of this.jobOption) {
          if (type === item.code) {
            return item.content
          }
        }
      } else {
        return '-'
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.filter-container > button{
    margin-left: 10px;
}
</style>
