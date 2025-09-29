<template>
  <div class="app-container">
    <search-bar :options="options" :list-query="listQuery" :search-list="searchList" :role="role" @lookUp="getList" @create="handleCreate" />
    <my-table
      :list-loading="listLoading"
      :data-list="list"
      :expand-list="expandList"
      :table-list="tableList"
    >
      <template v-slot:operate>
        <el-table-column
          align="center"
          label="操作"
          class-name="small-padding fixed-width"
          width="300"
        >
          <template slot-scope="scope">
            <el-button
              v-if="scope.row.statusT == 0"
              v-permission="['/base/vehicle/repair']"
              type="warning"
              size="mini"
              @click="handleRepair(scope.row)"
            >维修</el-button>
            <el-button
              v-else-if="scope.row.statusT == 1"
              v-permission="['/base/vehicle/enable']"
              type="success"
              size="mini"
              @click="handleEnable(scope.row)"
            >启用</el-button>
            <el-button
              v-permission="['/base/vehicle/scrap']"
              :disabled="scope.row.statusT == 2"
              :type="scope.row.statusT == 2 ? 'info' : 'danger'"
              plain
              size="mini"
              @click="handleScrap(scope.row)"
            >报废</el-button>
            <el-button
              v-permission="['/base/vehicle/update']"
              type="primary"
              size="mini"
              @click="handleUpdate(scope.row)"
            >编辑</el-button>
            <el-button
              v-permission="['/base/vehicle/delete']"
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
          <el-select v-model="dataForm.departmentId" filterable placeholder="请选择" style="width: 100%">
            <el-option
              v-for="item in options.departmentId"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="车辆编号" prop="seqno">
          <el-input v-model="dataForm.seqno" :maxlength="64" />
        </el-form-item>
        <el-form-item label="购买日期" prop="enrollDate">
          <el-date-picker v-model="dataForm.enrollDate" value-format="timestamp" type="date" placeholder="选择日期" style="width:100%" />
        </el-form-item>
        <el-form-item label="车牌号码" prop="lpno">
          <el-input v-model="dataForm.lpno" :maxlength="64" />
        </el-form-item>
        <el-form-item label="制造商" prop="factory">
          <el-input v-model="dataForm.factory" :maxlength="32" />
        </el-form-item>
        <el-form-item label="车辆型号" prop="model">
          <el-select v-model="dataForm.model" filterable placeholder="请选择" style="width: 100%">
            <el-option
              v-for="item in options.model"
              :key="item.code"
              :label="item.content"
              :value="item.code"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="车辆种类" prop="vhType">
          <el-select v-model="dataForm.vhType" filterable placeholder="请选择" style="width: 100%">
            <el-option
              v-for="item in options.vhType"
              :key="item.code"
              :label="item.content"
              :value="item.code"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="车辆类型" prop="vehicleType">
          <el-select v-model="dataForm.vehicleType" filterable placeholder="请选择" style="width: 100%">
            <el-option
              v-for="item in options.vehicleType"
              :key="item.code"
              :label="item.content"
              :value="item.code"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="车架号" prop="frameNumber">
          <el-input v-model="dataForm.frameNumber" :maxlength="64" onkeyup="value=value.replace(/[\W]/g,'')" />
        </el-form-item>
        <el-form-item label="发动机号" prop="engineNumber">
          <el-input v-model="dataForm.engineNumber" :maxlength="64" onkeyup="value=value.replace(/[\W]/g,'')" />
        </el-form-item>
        <el-form-item label="排放标准" prop="emissionStandard">
          <el-select v-model="dataForm.emissionStandard" filterable placeholder="请选择" style="width: 100%">
            <el-option
              v-for="item in options.emissionStandard"
              :key="item.code"
              :label="item.content"
              :value="item.code"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="出厂日期" prop="productionDate">
          <el-date-picker v-model="dataForm.productionDate" value-format="timestamp" type="date" placeholder="选择日期" style="width:100%" />
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
import { reqDictionary } from '@/api/system/dictionary'
import { listVehicle, addVehicle, updateVehicle, delectVehicle, repairVehicle, enableVehicle, scraptVehicle } from '@/api/vehicle/vehicleInformation'
import { formatdate } from '@/utils/date'
import { authOption } from '@/api/common/selectOption'

export default {
  components: { Pagination, myTable, searchBar },
  data() {
    return {
      list: [],
      listQuery: {
        limit: 10,
        page: 1,
        lpno: null,
        seqno: null,
        departmentId: null,
        statusT: null
      },
      listLoading: true,
      total: 0,
      searchList: [
        { name: 'departmentId', label: '所属部门', type: 3, notClear: true },
        { name: 'seqno', label: '车辆编号' },
        { name: 'lpno', label: '车牌号码' },
        { name: 'statusT', label: '状态', type: 3 }
      ],
      options: {
        statusT: [
          { code: 0, content: '正常使用' },
          { code: 1, content: '维修中' },
          { code: 2, content: '报废' }
        ],
        vhType: [
          { code: 1, content: '押运车' },
          { code: 2, content: '小轿车' }
        ],
        vehicleType: [],
        model: [],
        emissionStandard: [],
        departmentId: []
      },
      role: {
        list: '/base/vehicle/list',
        add: '/base/vehicle/save'
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
          label: '车辆种类',
          prop: 'vhType',
          formatter: this.formatVhType
        },
        {
          label: '制造商',
          prop: 'factory'
        },
        {
          label: '型号',
          prop: 'model',
          formatter: this.formatModel
        },
        {
          label: '购买日期',
          prop: 'enrollDate',
          formatter: this.formatDateTime
        },
        {
          label: '状态',
          prop: 'statusT',
          formatter: this.formatStatusT
        }
      ],
      expandList: [
        {
          label: '车辆类型',
          prop: 'vehicleType',
          formatter: this.formatVehicleType
        },
        {
          label: '车架号',
          prop: 'frameNumber'
        },
        {
          label: '发动机号',
          prop: 'engineNumber'
        },
        {
          label: '排放标准',
          prop: 'emissionStandard',
          formatter: this.formatStandardType
        },
        {
          label: '出厂日期',
          prop: 'productionDate',
          formatter: this.formatDateTime
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
        enrollDate: null,
        factory: null,
        departmentId: null,
        id: null,
        lpno: null,
        model: null,
        seqno: null,
        vhType: null,
        comments: null,
        frameNumber: null,
        vehicleType: null,
        engineNumber: null,
        emissionStandard: null,
        productionDate: null
      },
      rules: {
        factory: [
          { required: true, message: '制造商不能为空', trigger: 'blur' }
        ],
        lpno: [
          { required: true, message: '车牌号码不能为空', trigger: 'blur' }
        ],
        model: [
          { required: true, message: '型号不能为空', trigger: 'blur' }
        ],
        seqno: [
          { required: true, message: '车辆编号不能为空', trigger: 'blur' }
        ],
        vhType: [
          { required: true, message: '车辆种类不能为空', trigger: 'blur' }
        ],
        departmentId: [
          { required: true, message: '所属部门不能为空', trigger: 'blur' }
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
      listVehicle(this.listQuery).then((res) => {
        const data = res.data
        this.list = data.list
        this.total = data.total
        this.listLoading = false
      })
    },
    getOptions() {
      return new Promise((resolve, reject) => {
        Promise.all([
          authOption(),
          reqDictionary('VEHICLE_MODEL'),
          reqDictionary('VEHICLE_TYPE'),
          reqDictionary('EMISSION_STANDARD')
        ])
          .then((res) => {
            const [res1, res2, res3, res4] = res
            this.options.departmentId = res1.data
            this.options.model = res2.data
            this.options.vehicleType = res3.data
            this.options.emissionStandard = res4.data
            this.listQuery.departmentId = this.options.departmentId[0].id
            this.getList()
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
        this.dataForm.departmentId = this.listQuery.departmentId
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
        delectVehicle(row.id)
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
    handleRepair(row) {
      this.$confirm('确定维修车辆吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.listLoading = true
        repairVehicle(row.id)
          .then(() => {
            this.$notify.success({
              title: '成功',
              message: '车辆维修成功'
            })
            this.getList()
          }).finally(() => {
            this.listLoading = false
          })
      })
    },
    handleEnable(row) {
      this.$confirm('确定启用车辆吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.listLoading = true
        enableVehicle(row.id)
          .then(() => {
            this.$notify.success({
              title: '成功',
              message: '车辆启用成功'
            })
            this.getList()
          }).finally(() => {
            this.listLoading = false
          })
      })
    },
    handleScrap(row) {
      this.$confirm('确定报废车辆吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.listLoading = true
        scraptVehicle(row.id)
          .then(() => {
            this.$notify.success({
              title: '成功',
              message: '车辆报废成功'
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
          if (type === 'update') {
            updateVehicle(this.dataForm)
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
            addVehicle(this.dataForm)
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
      return formatdate(date, 'yyyy-MM-dd')
    },
    formatStatusT(status) {
      return this.options.statusT.find(item => item.code === status).content
    },
    formatVhType(type) {
      return this.options.vhType.find(item => item.code === type).content
    },
    formatVehicleType(type) {
      if (type != null) {
        return this.options.vehicleType.find(item => item.code === type).content
      }
    },
    formatStandardType(type) {
      if (type != null) {
        return this.options.emissionStandard.find(item => item.code === type).content
      }
    },
    formatModel(type) {
      return this.options.model.find(item => item.code === type).content
    }
  }
}
</script>

<style scoped lang="scss">
</style>
