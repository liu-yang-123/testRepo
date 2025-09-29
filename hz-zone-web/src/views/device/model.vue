<template>
  <div class="app-container">
    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-input v-model="listQuery.modelName" clearable class="filter-item" style="width: 200px;" placeholder="请输入型号" :maxlength="32" />
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查找</el-button>
      <el-button v-permission="['/base/deviceModel/save']" class="filter-item" type="primary" icon="el-icon-edit" @click="handleCreate">添加</el-button>
    </div>
    <!-- 查询结果 -->
    <el-table
      v-loading="listLoading"
      element-loading-text="正在查询中。。。"
      :data="list"
      :header-cell-style="{'background-color':'#f5f5f5'}"
      border
      fit
    >
      <el-table-column align="center" label="设备分类" prop="deviceType" />
      <el-table-column align="center" label="品牌" prop="name">
        <template slot-scope="scope">
          <span>{{ scope.row.factoryId | formatFactory(brandOption) }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="型号" prop="modelName" />
      <el-table-column align="center" label="规格" prop="size" />
      <el-table-column align="center" label="产能(每分)" prop="speed" />
      <el-table-column
        align="center"
        label="操作"
        class-name="small-padding fixed-width"
        width="240"
      >
        <template slot-scope="scope">
          <el-button
            v-permission="['/base/deviceModel/update']"
            type="primary"
            size="mini"
            @click="handleUpdate(scope.row)"
          >编辑</el-button>
          <el-button
            v-permission="['/base/deviceModel/delete']"
            type="danger"
            size="mini"
            @click="handleDelete(scope.row)"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

    <!-- 添加或修改对话框 -->
    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible" :close-on-click-modal="false">
      <el-form ref="dataForm" :rules="rules" :model="dataForm" status-icon label-position="left" label-width="100px" style="width: 400px; margin-left:50px;">
        <el-form-item label="设备分类" prop="deviceType">
          <el-select v-model="dataForm.deviceType" placeholder="请选择" style="width: 100%">
            <el-option
              v-for="item in typeOption"
              :key="item.code"
              :label="item.content"
              :value="item.code"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="品牌" prop="factoryId">
          <el-select v-model="dataForm.factoryId" placeholder="请选择" style="width: 100%">
            <el-option
              v-for="item in brandOption"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="型号" prop="modelName">
          <el-input v-model="dataForm.modelName" :maxlength="64" />
        </el-form-item>
        <el-form-item label="规格" prop="size">
          <el-input v-model="dataForm.size" :maxlength="64" />
        </el-form-item>
        <el-form-item label="产能(每分钟)" prop="speed">
          <el-input v-model="dataForm.speed" :maxlength="64" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button v-if="dialogStatus=='create'" type="primary" @click="createData">确定</el-button>
        <el-button v-else type="primary" @click="updateData">确定</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import Pagination from '@/components/Pagination'
import { createModel, deleteModel, listModel, updateModel } from '@/api/device/model'
import { brandOption } from '@/api/device/brand'
import { reqDictionary } from '@/api/system/dictionary'
export default {
  name: 'DeviceModel',
  components: { Pagination },
  filters: {
    formatFactory(factoryId, brandOption) {
      console.log(brandOption)
      const [obj] = brandOption.filter(item => item.value === factoryId)
      return obj != null ? (obj.label || '') : ''
    }
  },
  data() {
    return {
      list: null,
      total: 0,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 10,
        modelName: null
      },
      typeOption: [],
      brandOption: [],
      dataForm: {},
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '创建'
      },
      rules: {
        deviceType: [
          { required: true, message: '设备分类不能为空', trigger: 'blur' }
        ],
        factoryId: [
          { required: true, message: '品牌不能为空', trigger: 'blur' }
        ],
        modelName: [
          { required: true, message: '型号不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    Promise.resolve(reqDictionary('DEVICE_TYPE')).then((res) => {
      this.typeOption = res.data
      this.getList()
    })
    brandOption().then(res => {
      this.brandOption = res.data
    })
  },
  methods: {
    getList() {
      this.listLoading = true
      listModel(this.listQuery).then(res => {
        this.list = res.data.list
        this.total = res.data.total
        this.listLoading = false
      }).catch(() => {
        this.list = []
        this.total = 0
        this.listLoading = false
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
          createModel(this.dataForm).then(() => {
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
          updateModel(this.dataForm).then(() => {
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
    handleDelete(row) {
      this.$confirm('确定删除吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteModel(row).then(() => {
          this.$message.success('删除成功')
          const index = this.list.indexOf(row)
          this.list.splice(index, 1)
        })
      })
    }
  }
}
</script>

<style lang="scss" scoped>
  .filter-container > button{
    margin-left: 10px;
  }
</style>
