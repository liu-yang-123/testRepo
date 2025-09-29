<template>
  <div class="app-container">
    <search-bar :options="options" :list-query="listQuery" :search-list="searchList" :role="role" @lookUp="getList" @create="handleCreate" />
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
              v-permission="['/base/driver/update']"
              type="primary"
              size="mini"
              @click="handleUpdate(scope.row)"
            >编辑</el-button>
            <el-button
              v-permission="['/base/driver/delete']"
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
        <el-form-item label="司机" prop="driver">
          <el-select v-model="dataForm.driver" filterable placeholder="请选择" style="width: 100%">
            <el-option
              v-for="item in options.driver"
              :key="item.id"
              :label="`${item.empNo}/${item.empName}`"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="类型" prop="driverType">
          <el-select v-model="dataForm.driverType" filterable placeholder="请选择" style="width: 100%">
            <el-option
              v-for="item in options.driverType"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="线路" prop="routeId">
          <el-select v-model="dataForm.routeId" filterable placeholder="请选择" style="width: 100%">
            <el-option
              v-for="item in options.routeId"
              :key="item.id"
              :label="item.routeName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="排班方式" prop="assignType">
          <el-select v-model="dataForm.assignType" filterable placeholder="请选择" style="width: 100%">
            <el-option
              v-for="item in options.assignType"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="车辆" prop="vehicleNo">
          <el-select v-model="dataForm.vehicleNo" filterable placeholder="请选择" style="width: 100%">
            <el-option
              v-for="item in options.vehicleNo"
              :key="item.lpno"
              :label="`${item.seqno}/${item.lpno}`"
              :value="item.lpno"
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
import { listDriver, addDriver, updateDriver, deleteDriver } from '@/api/duty/prepareManage'
import { vehicleOption, authOption, jobNameOption, routeTemplateOption } from '@/api/common/selectOption'

export default {
  components: { Pagination, myTable, searchBar },
  data() {
    return {
      list: [],
      listQuery: {
        limit: 10,
        page: 1,
        departmentId: null,
        name: null
      },
      departmentId: null,
      listLoading: true,
      total: 0,
      searchList: [
        { name: 'departmentId', label: '所属部门', type: 3, notClear: true },
        { name: 'name', label: '姓名' }
      ],
      options: {
        departmentId: [],
        driver: [],
        assignType: [
          { id: 0, name: '随机' },
          { id: 1, name: '固定' }
        ],
        driverType: [
          { id: 0, name: '主班' },
          { id: 1, name: '替班' }
        ],
        routeId: [],
        vehicleNo: []
      },
      role: {
        list: '/base/driver/list',
        add: '/base/driver/save'
      },
      tableList: [
        {
          label: '司机',
          prop: 'driverName'
        },
        {
          label: '主替班',
          prop: 'driverType',
          formatter: this.formatDriver
        },
        {
          label: '当前线路',
          prop: 'routeName',
          formatter: this.formatRoute
        },
        {
          label: '排班方式',
          prop: 'assignType',
          formatter: this.formatAssign
        },
        {
          label: '车牌号码',
          prop: 'vehicleNo'
        }
      ],
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '创建'
      },
      dataForm: {
        assignType: null,
        departmentId: null,
        id: null,
        vehicleNo: null,
        routeId: null,
        driver: null,
        driverType: null
      },
      empOptions: [],
      rules: {
        assignType: [
          { required: true, message: '排班方式不能为空', trigger: 'blur' }
        ],
        driver: [
          { required: true, message: '司机不能为空', trigger: 'blur' }
        ],
        driverType: [
          { required: true, message: '类型不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  mounted() {
    this.getOptions().then(() => {
      this.getList()
    })
  },
  methods: {
    getList(data) {
      if (data) {
        for (const key in data) {
          this.listQuery[key] = data[key]
        }
      }
      listDriver(this.listQuery).then((res) => {
        const data = res.data
        this.list = data.list
        this.departmentId = this.listQuery.departmentId
        this.total = data.total
        this.listLoading = false
      })
    },
    getOptions() {
      return new Promise((reslove, reject) => {
        Promise.all([
          authOption()
        ]).then(res => {
          const [res1] = res
          this.options.departmentId = res1.data
          this.listQuery.departmentId = this.options.departmentId[0].id
          reslove()
        }).catch(err => {
          reject(err)
        })
      })
    },
    handleOptions(departmentId = this.departmentId) {
      return new Promise((reslove, reject) => {
        Promise.all([
          jobNameOption(departmentId, '1'),
          vehicleOption(departmentId),
          routeTemplateOption(departmentId)
        ])
          .then((res) => {
            const [res1, res2, res3] = res
            this.options.driver = res1.data[1]
            this.options.vehicleNo = res2.data
            this.options.routeId = res3.data
            reslove()
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
        this.handleOptions()
      })
    },
    handleUpdate(row) {
      this.handleOptions().then(() => {
        this.dialogStatus = 'update'
        this.dialogFormVisible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].clearValidate()
          for (const key in this.dataForm) {
            this.dataForm[key] = row[key]
          }
        })
      })
    },
    handleDelete(row) {
      this.$confirm('确定删除吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.listLoading = true
        deleteDriver(row.id)
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
          this.dataForm.departmentId = this.departmentId
          if (type === 'update') {
            updateDriver(this.dataForm)
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
            addDriver(this.dataForm)
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
    formatDriver(type) {
      switch (type) {
        case 0:
          return '主班'
        case 1:
          return '替班'
      }
    },
    formatRoute(name, row) {
      return `${row.routeNo}/${name}`
    },
    formatAssign(type) {
      switch (type) {
        case 0:
          return '随机'
        case 1:
          return '固定'
      }
    }
  }
}
</script>

<style scoped lang="scss">
</style>
