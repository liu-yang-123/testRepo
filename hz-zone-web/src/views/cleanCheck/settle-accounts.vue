<template>
  <div class="app-container">
    <el-row :gutter="20">
      <div class="filter-container">
        <el-select
          v-model="authId"
          v-permission="['/base/department/auth']"
          class="filter-item"
          style="width: 200px"
          placeholder="请选择部门"
          :clearable="false"
          @change="getTree()"
        >
          <el-option
            v-for="item in authOption"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
        <el-button
          class="filter-item"
          type="primary"
          icon="el-icon-refresh"
          @click="getTree"
        >刷新</el-button>
        <el-button
          v-permission="['/base/clear/charge/rule/add']"
          class="filter-item"
          type="primary"
          icon="el-icon-edit"
          @click="handleCreate"
        >添加</el-button>
      </div>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="5" style="padding-left: 0">
        <el-input
          v-model="filterText"
          placeholder="输入关键字进行过滤"
        />
        <el-scrollbar style="height: 500px;margin-top:12px" class="scrollbar">
          <el-tree
            ref="listTree"
            :data="bankOption"
            :props="defaultProps"
            node-key="id"
            highlight-current
            :filter-node-method="filterNode"
            :default-expanded-keys="expandArr"
            @node-click="handleNodeClick"
          />
        </el-scrollbar>
      </el-col>
      <el-col :span="19">
        <my-table
          :list-loading="listLoading"
          :data-list="list"
          :table-list="tableList"
          height="450px"
        >
          <template v-slot:operate>
            <el-table-column
              align="center"
              label="操作"
              class-name="small-padding fixed-width"
              width="160"
            >
              <template slot-scope="scope">
                <el-button
                  v-permission="['/base/clear/charge/rule/update']"
                  type="primary"
                  size="mini"
                  @click="handleUpdate(scope.row)"
                >编辑</el-button>
                <el-button
                  v-permission="['/base/clear/charge/rule/del']"
                  type="danger"
                  size="mini"
                  @click="handleDelete(scope.row)"
                >删除</el-button>
              </template>
            </el-table-column>
          </template>
        </my-table>
        <!-- <pagination
          v-show="total > 0"
          :total="total"
          :page.sync="listQuery.page"
          :limit.sync="listQuery.limit"
          @pagination="getList"
        /> -->
      </el-col>
    </el-row>

    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible" :close-on-click-modal="false">
      <el-form ref="dataForm" :rules="rules" :model="dataForm" status-icon label-position="right" label-width="100px" style="width: 400px; margin-left:50px;">
        <el-form-item label="所属银行" prop="bankId">
          <treeSelect
            v-model="dataForm.bankId"
            :disable-branch-nodes="true"
            :normalizer="normalizer"
            :show-count="true"
            :options="bankOption"
            placeholder="请选择取所属银行"
            style="width:80%"
          />
        </el-form-item>
        <el-form-item label="券别类型" prop="gbFlag">
          <el-radio-group v-model="dataForm.gbFlag">
            <el-radio :label="1">残损券</el-radio>
            <el-radio :label="2">完整券</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="纸硬币" prop="attr">
          <el-radio-group v-model="dataForm.attr" @change="attrChange">
            <el-radio label="P">纸币</el-radio>
            <el-radio label="C">硬币</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="券别" prop="denom">
          <el-select
            v-model="dataForm.denom"
            filterable
            clearable
            placeholder="请选择"
            style="width: 80%"
          >
            <el-option
              v-for="item in clearDenomOption"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input v-model="dataForm.price" style="width:80%" oninput="value=value.match(/\d+\.?\d{0,2}/)" @blur="dataForm.price= +$event.target.value" />
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
// import Pagination from '@/components/Pagination'
import myTable from '@/components/Table'
// import searchBar from '@/components/SearchBar'
import { addAccount, updateAccount, listAccount, deleteAccount } from '@/api/cleanCheck/settleAccounts'
import { regionData, CodeToText } from 'element-china-area-data'
import TreeSelect from '@riophae/vue-treeselect'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
import { authOption, clearDenomOption, bankTradeOption } from '@/api/common/selectOption'

export default {
  filters: {
    formatText(code) {
      return CodeToText[code] || ''
    }
  },
  components: { myTable, TreeSelect },
  data() {
    return {
      authId: null,
      expandArr: [],
      list: [],
      listQuery: {
        bankId: null
      },
      role: {
        list: '/base/atm/list'
      },
      listLoading: true,
      loading: null,
      total: 0,
      printList: [],
      options: {
        statusT: [
          { code: 0, content: '启用' },
          { code: 1, content: '停用' }
        ],
        denom: [100, 10],
        locationType: [
          { code: 1, content: '离行式' },
          { code: 2, content: '附行式' },
          { code: 3, content: '大堂式' }
        ],
        managerFlag: [
          { code: 0, content: '否' },
          { code: 1, content: '是' }
        ]
      },
      authOption: [],
      denomOption: [],
      clearDenomOption: [],
      filterText: '',
      defaultProps: {
        children: 'children',
        label: 'name'
      },
      tableList: [
        {
          label: '种类',
          prop: 'gbFlag',
          formatter: this.formatGb
        },
        {
          label: '纸硬币',
          prop: 'attr',
          formatter: this.formatAttr
        },
        {
          label: '券别',
          prop: 'denomName'
        },
        {
          label: '收费 （元/捆）',
          prop: 'price'
        }
      ],
      CodeToText,
      regionData,
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '创建'
      },
      oldNodeId: null,
      dataForm: {
        departmentId: null,
        bankId: null,
        gbFlag: null,
        id: null,
        attr: null,
        denom: null,
        price: null
      },
      bankOption: [],
      treeOptions: [],
      empOption: [],
      rules: {
        bankId: [
          { required: true, message: '所属银行不能为空', trigger: 'blur' }
        ],
        gbFlag: [
          { required: true, message: '券别类型不能为空', trigger: 'blur' }
        ],
        attr: [
          { required: true, message: '纸硬币不能为空', trigger: 'blur' }
        ],
        denom: [
          { required: true, message: '券别不能为空', trigger: 'blur' }
        ],
        price: [
          { required: true, message: '价格不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  watch: {
    filterText(val) {
      this.$refs.listTree.filter(val)
    }
  },
  mounted() {
    this.openLoading()
    this.getOptions().then(() => {
      this.getTree()
    })
    clearDenomOption().then(res => {
      this.denomOption = res.data
    })
  },
  methods: {
    getList(data) {
      this.listLoading = true
      if (data) {
        for (const key in data) {
          this.listQuery[key] = data[key]
        }
      }
      listAccount(this.listQuery).then((res) => {
        const data = res.data
        this.list = data
        this.listLoading = false
      })
    },
    async getTree() {
      await bankTradeOption(this.authId, 3).then((res) => {
        const data = res.data
        this.bankOption = data
        if (this.bankOption.length > 0) {
          this.expandArr = [this.bankOption[0].id]
          this.$nextTick(() => {
            this.$refs.listTree.setCurrentKey(this.bankOption[0].id)
          })
          this.handleNodeClick(this.bankOption[0])
        } else {
          this.listLoading = false
        }
        this.loading.close()
      })
    },
    getOptions() {
      return new Promise((resolve, reject) => {
        Promise.all([
          authOption()
        ])
          .then((res) => {
            const [res1] = res
            this.authOption = res1.data
            if (this.authOption[0]) {
              this.authId = this.authOption[0].id
            }
            resolve()
          })
          .catch((err) => {
            this.flag = true
            reject(err)
          })
      })
    },
    handleNodeClick(val) {
      this.listQuery.bankId = val.id
      this.getList()
    },
    handleCreate() {
      this.dialogStatus = 'create'
      this.dialogFormVisible = true
      this.clearDenomOption = []
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
          this.dataForm[key] = row[key]
        }
        this.clearDenomOption = this.denomOption.filter(item => item.type === this.dataForm.attr)
        this.clearDenomOption.unshift({ value: '0', label: '通用' })
      })
    },
    handleDelete(row) {
      this.$confirm('确定删除吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.listLoading = true
        deleteAccount(row.id)
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
          this.dataForm.departmentId = this.authId
          this.openLoading()
          if (type === 'update') {
            updateAccount(this.dataForm)
              .then(() => {
                this.getList()
                this.dialogFormVisible = false
                this.$notify.success({
                  title: '成功',
                  message: '更新成功'
                })
              })
              .finally(() => {
                this.loading.close()
              })
          } else {
            addAccount(this.dataForm)
              .then(() => {
                this.getList()
                this.dialogFormVisible = false
                this.$notify.success({
                  title: '成功',
                  message: '添加成功'
                })
              })
              .finally(() => {
                this.loading.close()
              })
          }
        }
      })
    },
    formatGb(type) {
      switch (type) {
        case 1:
          return '残损券'
        case 2:
          return '完整券'
      }
    },
    formatAttr(type) {
      switch (type) {
        case 'P':
          return '纸币'
        case 'C':
          return '硬币'
      }
    },
    formatDenom(value) {
      return this.denomOption.find(item => item.value === value)?.label
    },
    filterNode(value, data) {
      if (!value) return true
      return data.name.indexOf(value) !== -1
    },
    normalizer(node) {
      return {
        id: node.id,
        label: node.name,
        children: node.children
      }
    },
    openLoading() {
      this.loading = this.$loading({
        lock: true,
        text: '正在加载...请勿进行其它操作',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      })
    },
    attrChange(val) {
      this.clearDenomOption = this.denomOption.filter(item => item.type === val)
      this.clearDenomOption.unshift({ value: '0', label: '通用' })
      this.dataForm.denom = null
    }
  }
}
</script>

<style scoped lang="scss">
.filter-container *:nth-child(n + 2) {
  margin-left: 10px;
}
.scrollbar {
  ::v-deep .el-scrollbar__wrap {
    overflow: auto;
  }
}
</style>
