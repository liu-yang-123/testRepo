<template>
  <div class="app-container">
    <search-bar :search-list="searchList" :options="options" :list-query="listQuery" :role="role" @lookUp="getList" @print="hanlePrint">
      <template v-slot:more>
        <el-button
          v-permission="['/base/cashbox/produce']"
          class="filter-item"
          type="primary"
          icon="el-icon-edit-outline"
          @click="handleCreateLabel"
        >生成标签</el-button>
      </template>
    </search-bar>
    <my-table
      :list-loading="listLoading"
      :data-list="list"
      :table-list="tableList"
      :selection="true"
      @selectionChange="selectionChange"
    >
      <template v-slot:operate>
        <el-table-column
          align="center"
          label="操作"
          class-name="small-padding fixed-width"
          width="120"
        >
          <template slot-scope="scope">
            <el-button
              v-permission="['/base/cashbox/bind']"
              type="primary"
              size="mini"
              @click="handleUpdate(scope.row,'finger')"
            >编码绑定</el-button>
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

    <el-dialog title="生成标签" :visible.sync="createLabelVisible" :close-on-click-modal="false">
      <el-form ref="dataForm" :rules="rules" :model="dataForm" status-icon label-position="right" label-width="120px" style="width: 80%; margin-left:8%;">
        <el-form-item label="条码类别" prop="type">
          <el-radio-group v-model="dataForm.type">
            <el-radio :label="1">钞盒编码</el-radio>
            <el-radio :label="2">钞袋编码</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="生产条码个数" prop="count">
          <el-input-number v-model="dataForm.count" :min="1" :max="200" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="createLabelVisible = false">取消</el-button>
        <el-button type="primary" @click="createLabel">确定</el-button>
      </div>
    </el-dialog>

    <el-dialog title="批量打印" :visible.sync="printVisible" :close-on-click-modal="false" width="30%">
      <el-form ref="printForm" :model="printForm" status-icon label-position="right" label-width="120px" style="width: 80%; margin-left:6%;">
        <el-form-item label="打印份数" prop="copy">
          <el-radio-group v-model="printForm.copy">
            <el-radio :label="1">1</el-radio>
            <el-radio :label="2">2</el-radio>
            <el-radio :label="3">3</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="printVisible = false">取消</el-button>
        <el-button type="primary" @click="printSubmit">确定</el-button>
      </div>
    </el-dialog>

    <!-- 编码绑定 -->
    <el-dialog title="编码绑定" :visible.sync="dialogVisible" :close-on-click-modal="false">
      <el-form ref="bindForm" :rules="bindRules" :model="bindForm" status-icon label-position="right" label-width="100px" style="width: 80%; margin-left:8%;">
        <el-form-item label="编码" prop="rfid">
          <el-input v-model="bindForm.rfid" style="width:40%" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="updateData()">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import Pagination from '@/components/Pagination'
import myTable from '@/components/Table'
import searchBar from '@/components/SearchBar'
import { listBankBox, unbindBankBox, stopBankBox, printBox, cashboxUsed, cashboxProduce, bindBox } from '@/api/bankBox'
import Template from '../escort/template.vue'
export default {
  components: { Pagination, myTable, searchBar },
  data() {
    Template
    return {
      list: [],
      listQuery: {
        limit: 10,
        page: 1,
        boxNo: null
      },
      options: {
        used: [
          { code: 0, content: '否' },
          { code: 1, content: '是' }
        ],
        boxType: [
          { code: 1, content: '钞盒' },
          { code: 2, content: '钞袋' }
        ]
      },
      printList: [],
      listLoading: true,
      total: 0,
      searchList: [
        { name: 'boxNo', label: '钞盒编号' },
        { name: 'used', label: '是否已使用', type: 3 },
        { name: 'boxType', label: '钞盒钞袋类型', type: 3 },
        { name: 'rfid', label: '编码' }
      ],
      role: {
        list: '/base/cashbox/list',
        print: {
          permission: '/base/cashbox/used',
          disabled: true
        }
      },
      tableList: [
        {
          label: '钞盒编号',
          prop: 'boxNo'
        },
        {
          label: '编码',
          prop: 'rfid'
        },
        {
          label: '钞盒钞袋',
          prop: 'boxType',
          formatter: this.formatBoxType
        },
        {
          label: '是否已使用',
          prop: 'used',
          formatter: this.formatUsed
        },
        {
          label: '是否启用',
          prop: 'statusT',
          formatter: this.formatterStatus
        }
      ],
      boxIds: [],
      createLabelVisible: false,
      dataForm: {
        type: null,
        count: 1
      },
      rules: {
        type: [
          { required: true, message: '条码类型不能为空', trigger: 'blur' }
        ],
        count: [
          { required: true, message: '生产条码个数不能为空', trigger: 'blur' }
        ]
      },
      printVisible: false,
      printForm: {
        copy: 1
      },
      dialogVisible: false,
      bindForm: {
        id: null,
        rfid: null
      },
      bindRules: {
        rfid: [
          { required: true, message: '编码不能为空', trigger: 'blur' }
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
      listBankBox(this.listQuery).then((res) => {
        const data = res.data
        this.list = data.list
        console.log(this.list)
        this.total = data.total
        this.listLoading = false
      })
    },
    handleUnbind(row) {
      this.$confirm(`确定解绑${row.boxNo}吗?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.listLoading = true
        unbindBankBox(row.id)
          .then(() => {
            this.$notify.success({
              title: '成功',
              message: '解绑成功'
            })
            this.getList()
          }).finally(() => {
            this.listLoading = false
          })
      })
    },
    handleStop(row) {
      this.$confirm(`确定停用${row.boxNo}吗?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.listLoading = true
        stopBankBox(row.id)
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
    handleUpdate(row, type) {
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs['bindForm'].clearValidate()
        for (const key in this.bindForm) {
          this.bindForm[key] = row[key]
        }
      })
    },
    createLabel() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          this.openLoading()
          cashboxProduce(this.dataForm)
            .then(() => {
              this.createLabelVisible = false
              this.$notify.success({
                title: '成功',
                message: '生成成功'
              })
              this.getList()
            })
            .finally(() => {
              this.loading.close()
            })
        }
      })
    },
    updateData() {
      this.$refs['bindForm'].validate((valid) => {
        if (valid) {
          this.openLoading()
          bindBox(this.bindForm).then(() => {
            this.dialogVisible = false
            this.$notify.success({
              title: '成功',
              message: '绑定成功'
            })
            this.getList()
          })
            .finally(() => {
              this.loading.close()
            })
        }
      })
    },
    formatterStatus(status) {
      switch (status) {
        case 0:
          return '已启用'
        case 1:
          return '已停用'
      }
    },
    formatBoxType(type) {
      switch (type) {
        case 1:
          return '钞盒'
        case 2:
          return '钞袋'
      }
    },
    formatUsed(type) {
      switch (type) {
        case 0:
          return '否'
        case 1:
          return '是'
      }
    },
    selectionChange(val) {
      this.role.print.disabled = val.length === 0
      this.printList = val.map(({ boxNo }) => boxNo)
      this.boxIds = val.map(({ id }) => id)
    },
    handleCreateLabel() {
      this.createLabelVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].resetFields()
      })
    },
    hanlePrint() {
      this.printVisible = true
      this.$nextTick(() => {
        this.$refs['printForm'].resetFields()
      })
    },
    printSubmit() {
      const data = {
        title: '钞盒编号',
        boxNoList: this.printList,
        copy: this.printForm.copy
      }
      this.openLoading()
      printBox(data).then(() => {
        this.$notify.success({
          title: '成功',
          message: '打印成功'
        })
        this.printVisible = false
        cashboxUsed(this.boxIds).then(() => {
          this.getList()
        })
      })
        .finally(() => {
          this.loading.close()
        })
    },
    openLoading() {
      this.loading = this.$loading({
        lock: true,
        text: '正在加载...请勿进行其它操作',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      })
    }
  }
}
</script>

<style scoped lang="scss">
</style>
