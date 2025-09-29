<template>
  <div class="app-container">
    <search-bar :search-list="searchList" :list-query="listQuery" :role="role" @lookUp="getList" @create="handleCreate" />
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
              v-permission="['/base/whiteList/update']"
              type="primary"
              size="mini"
              @click="handleUpdate(scope.row)"
            >编辑</el-button>
            <el-button
              v-permission="['/base/whiteList/delete']"
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
      <el-form ref="dataForm" :rules="rules" :model="dataForm" status-icon label-position="left" label-width="100px" style="width: 400px; margin-left:50px;">
        <el-form-item label="ip地址" prop="ipAddress">
          <el-input v-model.trim="dataForm.ipAddress" :maxlength="32" @keyup.native="dataForm.ipAddress = formatNum($event)" @input="ipInput" />
        </el-form-item>
        <el-form-item label="mac地址" prop="macAddress">
          <el-input v-model.trim="dataForm.macAddress" @input="macInput" />
        </el-form-item>
        <el-form-item label="ip描述" prop="ipRemarks">
          <el-input v-model="dataForm.ipRemarks" :maxlength="64" />
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
import { listWhiteList, addWhiteList, updateWhiteList, delectWhiteList } from '@/api/system/whhite-list'
import { formatdate } from '@/utils/date'

export default {
  components: { Pagination, myTable, searchBar },
  data() {
    return {
      list: [],
      listQuery: {
        limit: 10,
        page: 1,
        ipAddress: null
      },
      listLoading: true,
      total: 0,
      searchList: [
        { name: 'ipAddress', label: 'IP地址' }
      ],
      role: {
        list: '/base/whiteList/list',
        add: '/base/whiteList/save'
      },
      tableList: [
        {
          label: 'IP地址',
          prop: 'ipAddress'
        },
        {
          label: 'MAC地址',
          prop: 'macAddress'
        },
        {
          label: 'IP描述',
          prop: 'ipRemarks'
        },
        {
          label: '创建时间',
          prop: 'createTime',
          formatter: this.formatDateTime
        },
        {
          label: '更新时间',
          prop: 'updateTime',
          formatter: this.formatDateTime
        }
      ],
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '创建'
      },
      dataForm: {
        ipAddress: '',
        ipRemarks: null,
        id: null,
        macAddress: ''
      },
      rules: {
        ipAddress: [
          { required: true, message: 'ip地址不能为空', trigger: 'blur' },
          { pattern: /^(\d+\.\d+\.\d+\.\d+)?$/, message: 'ip地址格式不正确', trigger: 'blur' }
        ],
        macAddress: [
          { required: true, message: 'mac地址不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  mounted() {
    this.getList()
  },
  methods: {
    getList(data) {
      if (data) {
        for (const key in data) {
          this.listQuery[key] = data[key]
        }
      }
      listWhiteList(this.listQuery).then((res) => {
        const data = res.data
        this.list = data.list
        console.log(this.list)
        this.total = data.total
        this.listLoading = false
      })
    },
    handleCreate() {
      this.dialogStatus = 'create'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].resetFields()
        this.rules.ipAddress[0].required = true
        this.rules.macAddress[0].required = true
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
        this.ipInput()
        this.macInput()
      })
    },
    handleDelete(row) {
      this.$confirm('确定删除吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.listLoading = true
        delectWhiteList(row.id)
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
          if (this.dataForm.macAddress) {
            this.dataForm.macAddress = this.dataForm.macAddress.toUpperCase()
          }
          if (type === 'update') {
            updateWhiteList(this.dataForm)
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
            addWhiteList(this.dataForm)
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
    formatDateTime(timestamp) {
      if (!timestamp || timestamp === 0) {
        return '-'
      }
      const date = new Date(timestamp)
      return formatdate(date, 'yyyy-MM-dd hh:mm:ss')
    },
    formatNum(e) {
      e.target.value = e.target.value.replace(/[^\.\d]/g, '')
      return e.target.value
    },
    ipInput() {
      this.dataForm.ipAddress.length > 0 ? this.rules.macAddress[0].required = false : this.rules.macAddress[0].required = true
    },
    macInput() {
      this.dataForm.macAddress.length > 0 ? this.rules.ipAddress[0].required = false : this.rules.ipAddress[0].required = true
    }
  }
}
</script>

<style scoped lang="scss">
</style>
