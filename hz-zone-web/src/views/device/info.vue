<template>
  <div class="app-container">
    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-select v-model="listQuery.departmentId" filterable placeholder="请先选择部门" class="filter-item" style="width: 150px">
        <el-option
          v-for="item in depOptions"
          :key="item.id"
          :label="item.name"
          :value="item.id"
        />
      </el-select>
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查找</el-button>
      <el-button v-permission="['/base/device/save']" class="filter-item" type="primary" icon="el-icon-edit" @click="handleCreate">添加</el-button>
      <el-button
        v-permission="['/printer/deviceQrCode/multi']"
        class="filter-item"
        type="success"
        icon="el-icon-printer"
        :disabled="printList.length === 0"
        @click="handlePrint"
      >批量打印</el-button>
    </div>
    <!-- 查询结果 -->
    <my-table
      :list-loading="listLoading"
      :data-list="list"
      :table-list="tableList"
      class="my-table"
      :selection="true"
      @selectionChange="selectionChange"
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
              v-permission="['/base/device/maintain']"
              type="success"
              size="mini"
              @click="handleMaintain(scope.row)"
            >维修</el-button>
            <el-button
              v-permission="['/base/device/update']"
              type="primary"
              size="mini"
              @click="handleUpdate(scope.row)"
            >编辑</el-button>
            <el-button
              v-permission="['/base/device/delete']"
              type="danger"
              size="mini"
              @click="handleDelete(scope.row)"
            >删除</el-button>
          </template>
        </el-table-column>
      </template>
    </my-table>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

    <!-- 添加或修改对话框 -->
    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible" :close-on-click-modal="false">
      <el-form ref="dataForm" :rules="rules" :model="dataForm" status-icon label-position="left" label-width="100px" style="width: 400px; margin-left:50px;">
        <el-form-item label="所在部门" prop="departmentId">
          <el-select v-model="dataForm.departmentId" filterable placeholder="请先选择所在部门" style="width: 300px">
            <el-option
              v-for="item in depOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="设备编号" prop="deviceNo">
          <el-input v-model="dataForm.deviceNo" :maxlength="32" />
        </el-form-item>
        <el-form-item label="品牌型号" prop="modelId">
          <el-select v-model="dataForm.modelId" filterable placeholder="请先选择品牌型号" style="width: 300px">
            <el-option
              v-for="item in modelOption"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="出厂序列号" prop="desc">
          <el-input v-model="dataForm.deviceSn" :maxlength="64" />
        </el-form-item>
        <el-form-item label="购买日期" prop="enrollDate">
          <el-date-picker
            v-model="dataForm.enrollDate"
            style="width: 100%"
            value-format="timestamp"
            type="date"
            placeholder="选择日期"
          />
        </el-form-item>
        <el-form-item label="设备状态" prop="statusT">
          <el-select v-model="dataForm.statusT" filterable placeholder="请先选择设备状态" style="width: 300px">
            <el-option
              v-for="item in statusOption"
              :key="item.code"
              :label="item.content"
              :value="item.code"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button v-if="dialogStatus=='create'" type="primary" @click="createData">确定</el-button>
        <el-button v-else type="primary" @click="updateData">确定</el-button>
      </div>
    </el-dialog>

    <!-- 添加维修对话框 -->
    <el-dialog title="维修记录" :visible.sync="dialogMaintainFormVisible" :close-on-click-modal="false">
      <el-form ref="maintainDataForm" :rules="maintainRules" :model="maintainDataForm" status-icon label-position="left" label-width="100px" style="width: 400px; margin-left:50px;">
        <el-form-item label="维保日期" prop="mtDate">
          <el-date-picker
            v-model="maintainDataForm.mtDate"
            style="width: 100%"
            value-format="timestamp"
            type="date"
            placeholder="选择日期"
          />
        </el-form-item>
        <el-form-item label="维保类型" prop="mtType">
          <el-select v-model="maintainDataForm.mtType" filterable placeholder="请先选择维保类型" style="width: 300px">
            <el-option
              v-for="item in mtTypeOption"
              :key="item.code"
              :label="item.content"
              :value="item.code"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="故障原因" prop="mtReason">
          <el-input v-model="maintainDataForm.mtReason" :maxlength="64" />
        </el-form-item>
        <el-form-item label="维保内容" prop="mtContent">
          <el-input v-model="maintainDataForm.mtContent" :maxlength="64" />
        </el-form-item>
        <el-form-item label="维保成本" prop="mtCost">
          <el-input-number v-model="maintainDataForm.mtCost" :maxlength="64" />
        </el-form-item>
        <el-form-item label="维保工程师" prop="mtEngineer">
          <el-input v-model="maintainDataForm.mtEngineer" :maxlength="64" />
        </el-form-item>

        <el-form-item label="维保结果" prop="statusT">
          <el-select v-model="maintainDataForm.mtResult" filterable placeholder="请先选择设备状态" style="width: 300px">
            <el-option
              v-for="item in mtResultOption"
              :key="item.code"
              :label="item.content"
              :value="item.code"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogMaintainFormVisible = false">取消</el-button>
        <el-button type="primary" @click="createMaintain">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import Pagination from '@/components/Pagination'
import { createDevice, deleteDevice, listDevice, updateDevice, printDevice } from '@/api/device/info'
import { createMaintain } from '@/api/device/maintain'
import { modelOption } from '@/api/device/model'
import { authOption } from '@/api/common/selectOption'
import { formatdate } from '@/utils/date'
import { reqDictionary } from '@/api/system/dictionary'
import myTable from '@/components/Table'
export default {
  name: 'Info',
  components: { Pagination, myTable },
  data() {
    return {
      list: null,
      tableList: [
        {
          label: '设备编号',
          prop: 'deviceNo'
        },
        {
          label: '设备品牌',
          prop: 'factoryName'
        },
        {
          label: '设备型号',
          prop: 'modelName'
        },
        {
          label: '出厂序列号',
          prop: 'deviceSn'
        },
        {
          label: '购买日期',
          prop: 'enrollDate',
          formatter: this.formatDateTime
        },
        {
          label: '设备状态',
          prop: 'statusT',
          formatter: this.formatStatus
        }
      ],
      total: 0,
      listLoading: true,
      listQuery: {
        departmentId: null,
        page: 1,
        limit: 10
      },
      printList: [],
      depOptions: [],
      modelOption: [],
      dataForm: {
        id: 0,
        departmentId: 0,
        deviceNo: null,
        modelId: null,
        deviceSn: null,
        enrollDate: null,
        location: null,
        ipaddr: null,
        statusT: 1
      },
      statusOption: [],
      dialogFormVisible: false,
      dialogMaintainFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '创建'
      },
      maintainDataForm: {
        deviceId: null,
        mtDate: new Date().getTime(),
        mtResult: 1,
        mtType: null,
        mtReason: null,
        mtContent: null,
        mtCost: null,
        mtEngineer: null
      },
      maintainRules: {
        mtType: [{ required: true, message: '维保类型不能为空', trigger: ['change', 'blur'] }],
        mtEngineer: [{ required: true, message: '维保工程师能为空', trigger: 'blur' }],
        mtReason: [{ required: true, message: '维保原因不能为空', trigger: 'blur' }],
        mtContent: [{ required: true, message: '维保内容不能为空', trigger: 'blur' }],
        mtCost: [{ required: true, message: '维保成本不能为空', trigger: 'blur' }],
        mtDate: [{ required: true, message: '维保日期不能为空', trigger: 'blur' }]
      },
      rules: {
        departmentId: [{ required: true, message: '所在部门不能为空', trigger: ['change', 'blur'] }],
        deviceNo: [{ required: true, message: '设备编号不能为空', trigger: 'blur' }],
        modelId: [{ required: true, message: '品牌型号不能为空', trigger: ['change', 'blur'] }],
        enrollDate: [{ required: true, message: '购买日期不能为空', trigger: 'blur' }],
        statusT: [{ required: true, message: '设备状态不能为空', trigger: 'blur' }]
      },
      mtResultOption: [
        { code: 0, content: '维修失败' },
        { code: 1, content: '维修成功' }
      ],
      mtTypeOption: []
    }
  },
  created() {
    this.getOptions()
    reqDictionary('DEVICE_STATUS').then((res) => {
      this.statusOption = res.data
    })
    reqDictionary('DEV_MAINTAIN_TYPE').then((res) => {
      this.mtTypeOption = res.data
    })
    modelOption().then(res => {
      this.modelOption = res.data
    })
  },
  methods: {
    getList() {
      this.listLoading = true
      listDevice(this.listQuery).then(res => {
        this.list = res.data.list
        this.total = res.data.total
        this.listLoading = false
      }).catch(() => {
        this.list = []
        this.total = 0
        this.listLoading = false
      })
    },
    getOptions() {
      authOption().then(res => {
        this.depOptions = res.data
        if (this.depOptions.length > 0) {
          this.listQuery.departmentId = this.depOptions[0].id
          this.getList()
        }
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
          this.loading = this.$loading({ lock: true, text: '正在加载...请勿进行其它操作', spinner: 'el-icon-loading' })
          createDevice(this.dataForm).then(() => {
            this.getList()
            this.dialogFormVisible = false
            this.$message.success('添加成功')
          }).finally(() => {
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
          this.loading = this.$loading({ lock: true, text: '正在加载...请勿进行其它操作', spinner: 'el-icon-loading' })
          updateDevice(this.dataForm).then(() => {
            this.dialogFormVisible = false
            this.$message.success('更新成功')
            for (const v of this.list) {
              if (v.id === this.dataForm.id) {
                const index = this.list.indexOf(v)
                this.list.splice(index, 1, this.dataForm)
                break
              }
            }
          }).finally(() => {
            this.loading.close()
          })
        }
      })
    },
    handleMaintain(row) {
      this.dialogMaintainFormVisible = true
      this.maintainDataForm.deviceId = row.id
      this.$nextTick(() => {
        this.$refs['maintainDataForm'].resetFields()
      })
    },
    createMaintain() {
      this.$refs['maintainDataForm'].validate(valid => {
        if (valid) {
          this.loading = this.$loading({ lock: true, text: '正在加载...请勿进行其它操作', spinner: 'el-icon-loading' })
          createMaintain(this.maintainDataForm).then(() => {
            this.dialogMaintainFormVisible = false
            this.$message.success('维修成功')
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
        deleteDevice(row).then(() => {
          this.$message.success('删除成功')
          const index = this.list.indexOf(row)
          this.list.splice(index, 1)
        })
      })
    },
    handlePrint() {
      this.$confirm('确定打印所勾选设备吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const data = {
          deviceNoList: this.printList,
          copy: 1
        }
        printDevice(data).then(() => {
          this.$notify.success({
            title: '成功',
            message: '打印成功'
          })
          this.getList()
        })
      })
    },
    selectionChange(val) {
      this.printList = val.map(item => item.deviceNo)
    },
    formatStatus(status) {
      const [obj] = this.statusOption.filter(item => item.code === status)
      return obj !== undefined ? (obj.content || '') : ''
    },
    formatDateTime(num) {
      if (!num || num === 0) {
        return '-'
      }
      const date = new Date(num)
      return formatdate(date, 'yyyy-MM-dd')
    }
  }
}
</script>

<style scoped>
.filter-container > button{
  margin-left: 10px;
}
</style>
