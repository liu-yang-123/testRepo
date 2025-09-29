<template>
  <div class="app-container">
    <search-bar v-if="flag" :options="options" :list-query="listQuery" :search-list="searchList" :role="role" @lookUp="getList" @create="handleCreate" />
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
              v-permission="['/base/vehicleMaintain/update']"
              type="primary"
              size="mini"
              @click="handleUpdate(scope.row)"
            >编辑</el-button>
            <el-button
              v-permission="['/base/vehicleMaintain/delete']"
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
        <el-form-item label="所属部门" prop="departmentId">
          <el-select v-model="dataForm.departmentId" filterable placeholder="请选择" style="width: 100%" @change="depChange">
            <el-option
              v-for="item in options.departmentId"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="车辆编号" prop="vehicleId">
          <el-select v-model="dataForm.vehicleId" filterable placeholder="请选择" :disabled="dataForm.departmentId ? false : true" style="width: 100%">
            <el-option
              v-for="item in vehicleOption"
              :key="item.id"
              :label="`${item.seqno}/${item.lpno}`"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="维保日期" prop="mtDate">
          <el-date-picker v-model="dataForm.mtDate" value-format="timestamp" type="date" placeholder="选择日期" style="width:100%" />
        </el-form-item>
        <el-form-item label="经办人" prop="mtEmployee">
          <el-select v-model="dataForm.mtEmployee" filterable placeholder="请选择" style="width: 100%">
            <el-option
              v-for="item in empOptions"
              :key="item.id"
              :label="item.empNo + ' / ' + item.empName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="维保类型" prop="mtType">
          <el-input v-model="dataForm.mtType" :maxlength="64" />
        </el-form-item>
        <el-form-item label="维保成本" prop="mtCost">
          <el-input v-model="dataForm.mtCost" :maxlength="64" />
        </el-form-item>
        <el-form-item label="维保内容" prop="mtContent">
          <el-input v-model="dataForm.mtContent" :maxlength="64" />
        </el-form-item>
        <el-form-item label="故障原因" prop="mtReason">
          <el-input v-model="dataForm.mtReason" :maxlength="64" />
        </el-form-item>
        <el-form-item label="维保结果" prop="mtResult">
          <el-select v-model="dataForm.mtResult" placeholder="请选择" style="width: 100%">
            <el-option
              v-for="item in dictionaryData['MAINTENANCE_RESULT']"
              :key="item.code"
              :label="item.content"
              :value="item.code"
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
import { listMaintenance, addMaintenance, updateMaintenance, delectMaintenance } from '@/api/vehicle/maintenance'
import { formatdate } from '@/utils/date'
import { dictionaryData } from '@/api/system/dictionary'
import { vehicleOption, authOption, employeeOption } from '@/api/common/selectOption'
import { formatMoney } from '@/utils/convert'

export default {
  components: { Pagination, myTable, searchBar },
  data() {
    return {
      flag: false,
      list: [],
      listQuery: {
        limit: 10,
        page: 1,
        departmentId: null,
        lpno: null,
        mtResult: null,
        mtType: null,
        seqno: null
      },
      departmentId: null,
      listLoading: true,
      total: 0,
      searchList: [
        { name: 'departmentId', label: '所属部门', type: 3, notClear: true },
        { name: 'seqno', label: '车辆编号' },
        { name: 'lpno', label: '车牌号码' },
        { name: 'mtResult', label: '维保结果', type: 1, dictionary: 'MAINTENANCE_RESULT' },
        { name: 'mtType', label: '维保类型' }
      ],
      options: {},
      role: {
        list: '/base/vehicleMaintain/list',
        add: '/base/vehicleMaintain/save'
      },
      tableList: [
        {
          label: '车辆编号',
          prop: 'seqno'
        },
        {
          label: '车牌号码',
          prop: 'lpno'
        },
        {
          label: '维保日期',
          prop: 'mtDate',
          formatter: this.formatDateTime
        },
        {
          label: '经办人',
          prop: 'empName',
          formatter: this.formatEmpName
        },
        {
          label: '维保类型',
          prop: 'mtType'
        },
        {
          label: '维保成本(元)',
          prop: 'mtCost',
          formatter: this.formatMoney
        },
        {
          label: '维保内容',
          prop: 'mtContent'
        },
        {
          label: '故障原因',
          prop: 'mtReason'
        },
        {
          label: '维保结果',
          prop: 'mtResult',
          dictionary: 'MAINTENANCE_RESULT'
        }
      ],
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '创建'
      },
      dataForm: {
        mtDate: null,
        mtContent: null,
        id: null,
        mtCost: null,
        mtEmployee: null,
        mtReason: null,
        mtResult: null,
        mtType: null,
        vehicleId: null,
        departmentId: null
      },
      empOptions: [],
      vehicleOption: [],
      dictionaryData,
      rules: {
        mtDate: [
          { required: true, message: '维保日期不能为空', trigger: 'blur' }
        ],
        mtEmployee: [
          { required: true, message: '经办人不能为空', trigger: 'blur' }
        ],
        mtResult: [
          { required: true, message: '维修结果不能为空', trigger: 'blur' }
        ],
        mtType: [
          { required: true, message: '维保类型不能为空', trigger: 'blur' }
        ],
        departmentId: [
          { required: true, message: '所属部门不能为空', trigger: 'blur' }
        ],
        vehicleId: [
          { required: true, message: '车辆编号不能为空', trigger: 'blur' }
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
      listMaintenance(this.listQuery).then((res) => {
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
          const [res2] = res
          this.options.departmentId = res2.data
          this.listQuery.departmentId = this.options.departmentId[0].id
          this.flag = true
          reslove()
        }).catch(err => {
          reject(err)
        })
      })
    },
    handleOptions(departmentId = this.departmentId) {
      return new Promise((reslove, reject) => {
        Promise.all([
          employeeOption(departmentId),
          vehicleOption(departmentId)
        ])
          .then((res) => {
            const [res1, res3] = res
            this.empOptions = res1.data
            this.vehicleOption = res3.data
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
        this.dataForm.departmentId = this.departmentId
        this.handleOptions()
      })
    },
    handleUpdate(row) {
      if (row.departmentId) {
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
      }
    },
    depChange(value) {
      if (value != null) {
        this.dataForm.vehicleId = null
        this.dataForm.mtEmployee = null
        this.handleOptions(value)
      }
    },
    handleDelete(row) {
      this.$confirm('确定删除吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.listLoading = true
        delectMaintenance(row.id)
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
          if (type === 'update') {
            updateMaintenance(this.dataForm)
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
            addMaintenance(this.dataForm)
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
    formatMoney,
    formatEmpName(name, row) {
      return `${row.empNo}/${name}`
    },
    formatDateTime(timestamp) {
      if (!timestamp || timestamp === 0) {
        return '-'
      }
      const date = new Date(timestamp)
      return formatdate(date, 'yyyy-MM-dd')
    }
  }
}
</script>

<style scoped lang="scss">
</style>
