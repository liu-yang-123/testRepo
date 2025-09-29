<template>
  <div class="app-container">
    <search-bar :list-query="listQuery" :search-list="searchList" :role="role" @refresh="getList" @create="handleCreate" />
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
              v-permission="['/base/denom/update']"
              type="primary"
              size="mini"
              @click="handleUpdate(scope.row)"
            >编辑</el-button>
            <el-button
              v-permission="['/base/denom/delete']"
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
      <el-form ref="dataForm" :rules="rules" :model="dataForm" status-icon label-position="right" label-width="120px" style="width: 500px; margin-left:50px;">
        <el-form-item label="货币类型" prop="curCode">
          <el-input v-model="dataForm.curCode" :maxlength="32" />
        </el-form-item>
        <el-form-item label="券别名称" prop="name">
          <el-input v-model="dataForm.name" :maxlength="64" />
        </el-form-item>
        <el-form-item label="面额价值" prop="value">
          <el-input v-model="dataForm.value" :maxlength="64" oninput="value=value.replace(/[^\.\d]/g,'')" />
        </el-form-item>
        <el-form-item label="版别类型" prop="version">
          <el-radio v-for="item in verFlag" :key="item.value" v-model="dataForm.version" :label="item.value">{{ item.text }}</el-radio>
        </el-form-item>
        <el-form-item label="券别形态" prop="attr">
          <el-radio v-for="item in attrType" :key="item.value" v-model="dataForm.attr" :label="item.value">{{ item.text }}</el-radio>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input v-model="dataForm.sort" :maxlength="64" />
        </el-form-item>
        <el-form-item label="每把张数" prop="wadSize">
          <el-input v-model="dataForm.wadSize" :maxlength="64" oninput="value=value.replace(/[^\d]/g,'')" />
        </el-form-item>
        <el-form-item label="每捆把数" prop="bundleSize">
          <el-input v-model="dataForm.bundleSize" :maxlength="64" oninput="value=value.replace(/[^\d]/g,'')" />
        </el-form-item>
        <el-form-item label="每袋捆数" prop="bagSize">
          <el-input v-model="dataForm.bagSize" :maxlength="64" oninput="value=value.replace(/[^\d]/g,'')" />
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
import { listDenom, addDenom, updateDenom, delectDenom } from '@/api/base/denomination'
import { formatdate } from '@/utils/date'
import { dictionaryData } from '@/api/system/dictionary'

export default {
  components: { Pagination, myTable, searchBar },
  data() {
    return {
      list: [],
      listQuery: {
        limit: 10,
        page: 1
      },
      listLoading: true,
      total: 0,
      searchList: [],
      attrType: [
        { value: 'P', text: '纸币' },
        { value: 'C', text: '硬币' }
      ],
      verFlag: [
        { value: 0, text: '不含版别' },
        { value: 1, text: '包含版别' },
        { value: 2, text: '残缺券' }
      ],
      role: {
        refresh: true,
        add: '/base/denom/save'
      },
      tableList: [
        {
          label: '货币类型',
          prop: 'curCode'
        },
        {
          label: '券别名称',
          prop: 'name',
          width: 180
        },
        {
          label: '面额值',
          prop: 'value'
        },
        {
          label: '版别类型',
          prop: 'version',
          formatter: this.formatterVersion
        },
        {
          label: '物理形态',
          prop: 'attr',
          formatter: this.formatterAttr
        },
        {
          label: '每把张数',
          prop: 'wadSize',
          width: 120
        },
        {
          label: '每捆张量',
          prop: 'bundleSize',
          width: 120
        },
        {
          label: '每袋捆数',
          prop: 'bagSize',
          width: 120
        },
        {
          label: '排序',
          prop: 'sort'
        }
      ],
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '创建'
      },
      dataForm: {
        attr: null,
        bagSize: null,
        bundleSize: null,
        curCode: null,
        name: null,
        sort: null,
        value: null,
        version: null,
        wadSize: null,
        id: null
      },
      dictionaryData,
      rules: {
        attr: [
          { required: true, message: '物理形态不能为空', trigger: 'blur' }
        ],
        bagSize: [
          { required: true, message: '每袋捆数不能为空', trigger: 'blur' }
        ],
        bundleSize: [
          { required: true, message: '每捆张量不能为空', trigger: 'blur' }
        ],
        curCode: [
          { required: true, message: '货币代码不能为空', trigger: 'blur' }
        ],
        name: [
          { required: true, message: '券别名称不能为空', trigger: 'blur' }
        ],
        value: [
          { required: true, message: '面额值不能为空', trigger: 'blur' }
        ],
        version: [
          { required: true, message: '版别类型不能为空', trigger: 'blur' }
        ],
        wadSize: [
          { required: true, message: '每把张数不能为空', trigger: 'blur' }
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
      listDenom(this.listQuery).then((res) => {
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
        delectDenom(row.id)
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
            updateDenom(this.dataForm)
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
            addDenom(this.dataForm)
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
    formatterAttr(attr) {
      switch (attr) {
        case 'P':
          return '纸币'
        case 'C':
          return '硬币'
      }
    },
    formatterVersion(version) {
      switch (version) {
        case 0:
          return '不含版别'
        case 1:
          return '包含版别'
      }
    }
  }
}
</script>

<style scoped lang="scss">
</style>
