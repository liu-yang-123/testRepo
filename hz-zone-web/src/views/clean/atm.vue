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
          v-permission="['/base/atm/save']"
          class="filter-item"
          type="primary"
          icon="el-icon-edit"
          @click="handleCreate"
        >添加</el-button>
        <el-button
          v-permission="['/printer/atmQrCode/multi']"
          class="filter-item"
          type="success"
          icon="el-icon-printer"
          :disabled="printList.length === 0"
          @click="handlePrint"
        >批量打印</el-button>
      </div>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="5">
        <el-input
          v-model="filterText"
          placeholder="输入关键字进行过滤"
        />
        <el-scrollbar style="height: 500px;margin-top:12px" class="scrollbar">
          <el-tree
            ref="listTree"
            :data="bankClearTree"
            :props="defaultProps"
            node-key="id"
            highlight-current
            :filter-node-method="filterNode"
            @node-click="handleNodeClick"
          />
        </el-scrollbar>
      </el-col>
      <el-col :span="19">
        <search-bar :list-query="listQuery" :search-list="searchList" :role="role" size="small" @lookUp="getList" />
        <my-table
          :list-loading="listLoading"
          :data-list="list"
          :table-list="tableList"
          :selection="true"
          :expand-list="expandList"
          height="450px"
          @selectionChange="selectionChange"
        >
          <template v-slot:operate>
            <el-table-column
              align="center"
              label="操作"
              class-name="small-padding fixed-width"
              width="180"
            >
              <template slot-scope="scope">
                <el-button
                  v-permission="['/base/atm/update']"
                  type="primary"
                  size="mini"
                  @click="handleUpdate(scope.row)"
                >编辑</el-button>
                <!-- <el-button
                  v-permission="['/base/atm/delete']"
                  type="danger"
                  size="mini"
                  @click="handleDelete(scope.row)"
                >删除</el-button> -->
                <el-button
                  v-if="scope.row.statusT === 0"
                  v-permission="['/base/atm/stop']"
                  type="warning"
                  size="mini"
                  @click="handleToggle(scope.row)"
                >停用</el-button>
                <el-button
                  v-else
                  v-permission="['/base/atm/enable']"
                  type="success"
                  size="mini"
                  @click="handleToggle(scope.row)"
                >启用</el-button>
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
      </el-col>
    </el-row>

    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible" :close-on-click-modal="false">
      <el-form ref="dataForm" :rules="rules" :model="dataForm" status-icon label-position="right" label-width="100px" style="width: 400px; margin-left:50px;">
        <el-form-item label="银行名称" prop="bankId">
          <treeSelect
            v-model="dataForm.bankId"
            :disable-branch-nodes="true"
            :normalizer="normalizer"
            :show-count="true"
            :options="bankClearTree"
            placeholder="请选择取银行"
          />
        </el-form-item>
        <el-form-item label="终端编号" prop="terNo">
          <el-input v-model="dataForm.terNo" :maxlength="64" />
        </el-form-item>
        <el-form-item label="设备品牌" prop="terFactory">
          <el-input v-model="dataForm.terFactory" :maxlength="64" />
        </el-form-item>
        <el-form-item label="设备类型" prop="terType">
          <el-input v-model="dataForm.terType" :maxlength="64" />
        </el-form-item>
        <el-form-item label="位置类型" prop="locationType">
          <el-select v-model="dataForm.locationType" filterable placeholder="请选择" style="width: 100%">
            <el-option
              v-for="item in options.locationType"
              :key="item.code"
              :label="item.content"
              :value="item.code"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="加钞券别" prop="denom">
          <el-radio v-for="item in options.denom" :key="item" v-model="dataForm.denom" :label="item">{{ item }}</el-radio>
        </el-form-item>
        <el-form-item label="取吞卡网点" prop="gulpBankId">
          <treeSelect
            v-model="dataForm.gulpBankId"
            :disable-branch-nodes="true"
            :normalizer="normalizer"
            :show-count="true"
            :options="bankClearTree"
            placeholder="请选择取吞卡网点"
          />
        </el-form-item>
        <el-form-item label="装机信息" prop="installInfo">
          <el-input v-model="dataForm.installInfo" :maxlength="128" type="textarea" autosize placeholder="请输入装机信息" />
        </el-form-item>
        <!-- <el-form-item label="银行名称" prop="bankId">
          <treeSelect
            v-model="dataForm.bankId"
            :disable-branch-nodes="true"
            :normalizer="normalizer"
            :show-count="true"
            :options="bankClearTree"
            placeholder="请选择银行名称"
            @input="select"
          />
        </el-form-item> -->
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
import { listATM, addATM, updateATM, deleteATM, stopATM, enableATM, printATM } from '@/api/clean/atm'
import { regionData, CodeToText } from 'element-china-area-data'
import TreeSelect from '@riophae/vue-treeselect'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
import { bankClearTree, authOption } from '@/api/common/selectOption'

export default {
  filters: {
    formatText(code) {
      return CodeToText[code] || ''
    }
  },
  components: { Pagination, myTable, TreeSelect, searchBar },
  data() {
    return {
      authId: null,
      list: [],
      listQuery: {
        limit: 10,
        page: 1,
        bankId: null,
        terNo: null,
        routeNo: null
      },
      searchList: [
        { name: 'routeNo', label: '线路编号' },
        { name: 'terNo', label: '设备编号' }
      ],
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
        ]
      },
      authOption: [],
      filterText: '',
      defaultProps: {
        children: 'children',
        label: 'name'
      },
      expandList: [
        {
          label: '装机信息',
          prop: 'installInfo',
          width: '100%'
        },
        {
          label: '备注',
          prop: 'comments',
          width: '100%'
        }
      ],
      tableList: [
        {
          label: '设备编号',
          prop: 'terNo'
        },
        {
          label: '设备品牌',
          prop: 'terFactory'
        },
        {
          label: '设备类型',
          prop: 'terType'
        },
        {
          label: '位置类型',
          prop: 'locationType',
          formatter: this.formatLocationType
        },
        {
          label: '加钞券别',
          prop: 'denom'
        },
        {
          label: '加款点',
          prop: 'bankName',
          width: 140
        },
        {
          label: '取吞卡网点',
          prop: 'gulpBankName',
          width: 140
        },
        {
          label: '状态',
          prop: 'statusT',
          formatter: this.formatStatus
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
      dataForm: {
        bankId: null,
        comments: null,
        id: null,
        city: null,
        district: null,
        province: null,
        locationType: null,
        terFactory: null,
        terNo: null,
        terType: null,
        denom: 100,
        installInfo: null,
        gulpBankId: null
      },
      bankClearTree: [],
      treeOptions: [],
      rules: {
        locationType: [
          { required: true, message: '经办人不能为空', trigger: 'blur' }
        ],
        terFactory: [
          { required: true, message: '设备品牌不能为空', trigger: 'blur' }
        ],
        terNo: [
          { required: true, message: '设备编号不能为空', trigger: 'blur' }
        ],
        terType: [
          { required: true, message: '设备类型不能为空', trigger: 'blur' }
        ],
        denom: [
          { required: true, message: '加钞券别不能为空', trigger: 'blur' }
        ],
        gulpBankId: [
          { required: true, message: '取吞卡网点不能为空', trigger: 'blur' }
        ],
        bankId: [
          { required: true, message: '银行不能为空', trigger: 'blur' }
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
  },
  methods: {
    getList(data) {
      this.listLoading = true
      if (data) {
        for (const key in data) {
          this.listQuery[key] = data[key]
        }
      }
      listATM(this.listQuery).then((res) => {
        const data = res.data
        this.list = data.list
        this.total = data.total
        this.listLoading = false
      })
    },
    async getTree() {
      await bankClearTree(this.authId).then((res) => {
        const data = res.data
        this.bankClearTree = data
        if (this.bankClearTree.length > 0) {
          this.$nextTick(() => {
            this.$refs.listTree.setCurrentKey(this.bankClearTree[0].id)
          })
          this.handleNodeClick(this.bankClearTree[0])
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
      this.listQuery.page = 1
      this.getList()
    },
    handleCreate() {
      this.dialogStatus = 'create'
      this.dialogFormVisible = true
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
        this.dataForm.location = [row.province, row.city, row.district]
      })
    },
    handleDelete(row) {
      this.$confirm('确定删除吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.listLoading = true
        deleteATM(row.id)
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
    handleToggle(row) {
      let str
      if (row.statusT === 0) {
        str = '停用'
      } else {
        str = '启用'
      }
      this.$confirm(`确定${str}吗?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.listLoading = true
        if (row.statusT === 0) {
          stopATM(row.id)
            .then(() => {
              this.$notify.success({
                title: '成功',
                message: '停用成功'
              })
              this.getList()
            })
            .finally(() => {
              this.listLoading = false
            })
        } else {
          enableATM(row.id).then(() => {
            this.$notify.success({
              title: '成功',
              message: '启用成功'
            })
            this.getList()
          })
            .finally(() => {
              this.listLoading = false
            })
        }
      })
    },
    newData(type) {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          if (this.dataForm.location) {
            this.dataForm.province = this.dataForm.location[0]
            this.dataForm.city = this.dataForm.location[1]
            this.dataForm.district = this.dataForm.location[2]
          }
          this.openLoading()
          if (type === 'update') {
            updateATM(this.dataForm)
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
            addATM(this.dataForm)
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
    formatStatus(status) {
      for (const item of this.options.statusT) {
        if (item.code === status) {
          return item.content
        }
      }
    },
    formatLocationType(locationType) {
      return this.options.locationType.filter(item => item.code === locationType)[0].content
    },
    selectionChange(val) {
      this.printList = val.map(({ bankName, terNo, bankNo }) => {
        return {
          bankName,
          bankNo,
          atmNo: terNo
        }
      })
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
    handlePrint() {
      this.$confirm('确定打印所勾选设备吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const data = {
          atmList: this.printList,
          copy: 1
        }
        printATM(data).then(() => {
          this.$notify.success({
            title: '成功',
            message: '打印成功'
          })
          this.getList()
        })
      })
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
