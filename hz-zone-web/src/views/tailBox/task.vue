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
        <!-- <el-button
          v-permission="['/base/bankTeller/save']"
          class="filter-item"
          type="primary"
          icon="el-icon-edit"
          @click="handleCreate"
        >添加</el-button> -->
      </div>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="4" style="padding-left: 0">
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
      <el-col :span="20">
        <template v-if="bankOption.length > 0">
          <search-bar :list-query="listQuery" :search-list="searchList" :options="options" :role="role" size="small" @lookUp="getList" />
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
                width="220"
              >
                <template slot-scope="scope">
                  <el-button
                    v-permission="['/base/boxpackTask/info']"
                    type="primary"
                    size="mini"
                    @click="handleDetail(scope.row)"
                  >详情</el-button>
                  <!-- <el-button
                    v-permission="['/base/bankTeller/update']"
                    type="primary"
                    size="mini"
                    @click="handleUpdate(scope.row)"
                  >编辑</el-button>
                  <el-button
                    v-permission="['/base/bankTeller/delete']"
                    type="danger"
                    size="mini"
                    @click="handleDelete(scope.row)"
                  >删除</el-button> -->
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
        </template>
        <template v-else>
          <el-empty style="height: 500px" description="暂无数据" />
        </template>
      </el-col>
    </el-row>
    <!-- 添加 修改 -->
    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible" :close-on-click-modal="false">
      <el-form ref="dataForm" :rules="rules" :model="dataForm" status-icon label-position="right" label-width="100px" style="width: 400px; margin-left:50px;">
        <el-form-item label="所属机构" prop="bankId">
          <treeSelect
            v-model="dataForm.bankId"
            :disable-branch-nodes="true"
            :normalizer="normalizer"
            :show-count="true"
            :options="bankOption"
            placeholder="请选择取所属机构"
            style="width:80%"
          />
        </el-form-item>
        <el-form-item label="箱包编号" prop="boxNo">
          <el-input v-model="dataForm.boxNo" :maxlength="64" style="width:80%" />
        </el-form-item>
        <el-form-item label="箱包名称" prop="boxName">
          <el-input v-model="dataForm.boxName" :maxlength="64" style="width:80%" />
        </el-form-item>
        <el-form-item label="共用机构" prop="shareBankId">
          <treeSelect
            v-model="dataForm.shareBankId"
            :disable-branch-nodes="true"
            :normalizer="normalizer"
            :show-count="true"
            :options="bankOption"
            placeholder="请选择取共用机构"
            style="width:80%"
          />
        </el-form-item>
        <el-form-item label="RFID编号" prop="rfid">
          <el-input v-model="dataForm.rfid" :maxlength="64" style="width:80%" />
        </el-form-item>
        <el-form-item label="备注" prop="comments">
          <el-input v-model="dataForm.comments" :maxlength="64" style="width:80%" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="newData(dialogStatus)">确定</el-button>
      </div>
    </el-dialog>
    <!-- 详情 -->
    <el-dialog
      title="详情"
      :visible.sync="detailFormVisible"
      :close-on-click-modal="false"
      width="1000px"
    >

      <el-form label-width="200px" inline>
        <el-form-item label="任务日期">
          <span>{{ formatDate(detailForm.taskDate) }}</span>
        </el-form-item>
        <el-form-item label="任务类型">
          <span>{{ formatTaskType(detailForm.taskType) }}</span>
        </el-form-item>
        <el-form-item label="上报机构">
          <span>{{ detailForm.bankName }}</span>
        </el-form-item>
        <el-form-item label="上报人">
          <span>{{ detailForm.createUserName }}</span>
        </el-form-item>
        <el-form-item label="上报时间">
          <span>{{ formatDateTime(detailForm.createTime) }}</span>
        </el-form-item>
        <el-form-item label="线路编号">
          <span>{{ formatRouteNo(detailForm.routeNo) }}</span>
        </el-form-item>
        <el-form-item label="任务用车">
          <span>{{ detailForm.lpno }}</span>
        </el-form-item>
        <el-form-item label="任务状态">
          <span>{{ formatStatus(detailForm.statusT) }}</span>
        </el-form-item>
        <el-form-item label="操作员">
          <span>{{ detailForm.handOpMansName }}</span>
        </el-form-item>
        <el-form-item label="交接时间">
          <span>{{ formatDateTime(detailForm.handTime) }}</span>
        </el-form-item>
        <el-form-item label="押运员" style="width: 100%">
          <el-image
            style="width: 180px; height: 220px"
            :src="detailForm.keyManPhoto"
            fit="fill"
            :preview-src-list="[detailForm.keyManPhoto]"
          />
          <el-image
            style="width: 180px; height: 220px;margin-left: 12px"
            :src="detailForm.operManPhoto"
            fit="fill"
            :preview-src-list="[detailForm.operManPhoto]"
          />
        </el-form-item>
        <el-form-item label="箱包" style="width: 100%">
          <my-table
            style="width:600px"
            :data-list="boxList"
            :table-list="boxTableList"
            height="300px"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailFormVisible = false">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import Pagination from '@/components/Pagination'
import myTable from '@/components/Table'
import searchBar from '@/components/SearchBar'
import { bankOption } from '@/api/tailBox/employee'
import { addTask, updateTask, listTask, deleteTask, detailTask } from '@/api/tailBox/task'
import TreeSelect from '@riophae/vue-treeselect'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
import { authOption } from '@/api/common/selectOption'
import { formatdate } from '@/utils/date'

export default {
  components: { Pagination, myTable, TreeSelect, searchBar },
  data() {
    return {
      authId: null,
      expandArr: [],
      list: [],
      listQuery: {
        limit: 10,
        page: 1,
        bankId: null,
        departmentId: null,
        taskType: null,
        statusT: null,
        taskDate: null,
        routeNo: null
      },
      role: {
        list: '/base/atm/list'
      },
      listLoading: true,
      loading: null,
      total: 0,
      printList: [],
      searchList: [
        { name: 'taskDate', label: '任务日期', type: 2 },
        { name: 'routeNo', label: '线路编号' },
        { name: 'taskType', label: '任务类型', type: 3 },
        { name: 'statusT', label: '任务状态', type: 3 }
      ],
      options: {
        statusT: [
          { code: 1, content: '已审核' },
          { code: 2, content: '已完成' }
        ],
        taskType: [
          { code: 1, content: '固定下发' },
          { code: 2, content: '固定上缴' },
          { code: 3, content: '临时下发' },
          { code: 4, content: '临时上缴' }
        ],
        managerFlag: [
          { code: 0, content: '否' },
          { code: 1, content: '是' }
        ]
      },
      authOption: [],
      filterText: '',
      defaultProps: {
        children: 'children',
        label: 'name'
      },
      tableList: [
        {
          label: '任务类型',
          prop: 'taskType',
          formatter: this.formatTaskType,
          width: 100
        },
        {
          label: '任务状态',
          prop: 'statusT',
          formatter: this.formatStatus,
          width: 100
        },
        {
          label: '任务用车',
          prop: 'lpno',
          width: 100
        },
        {
          label: '线路编号',
          prop: 'routeNo',
          formatter: this.formatRouteNo,
          width: 100
        },
        {
          label: '任务日期',
          prop: 'taskDate',
          formatter: this.formatDate
        },
        {
          label: '上报机构',
          prop: 'bankName',
          width: 200
        },
        {
          label: '上报时间',
          prop: 'createTime',
          formatter: this.formatDateTime
        },
        {
          label: '箱包数',
          prop: 'boxList',
          formatter: this.formatBoxNum
        }
      ],
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
        comments: null,
        id: null,
        boxNo: null,
        boxName: null,
        rfid: null,
        shareBankId: null
      },
      bankOption: [],
      treeOptions: [],
      empOption: [],
      rules: {
        boxNo: [
          { required: true, message: '箱包编号不能为空', trigger: 'blur' }
        ],
        boxName: [
          { required: true, message: '箱包名称不能为空', trigger: 'blur' }
        ],
        rfid: [
          { required: true, message: 'RFID编号不能为空', trigger: 'blur' }
        ],
        bankId: [
          { required: true, message: '银行不能为空', trigger: 'blur' }
        ]
      },
      // 详情
      detailFormVisible: false,
      detailForm: {
        taskType: null,
        statusT: null,
        lpno: null,
        routeNo: null,
        taskDate: null,
        bankName: null,
        createTime: null
      },
      boxList: [],
      boxTableList: [
        {
          label: '箱包编号',
          prop: 'boxNo'
        },
        {
          label: '箱包名称',
          prop: 'boxName'
        }
      ]
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
      this.listQuery.departmentId = this.authId
      if (data) {
        for (const key in data) {
          this.listQuery[key] = data[key]
        }
      }
      listTask(this.listQuery).then((res) => {
        const data = res.data
        this.list = data.list
        this.total = data.total
        this.listLoading = false
      })
    },
    async getTree() {
      await bankOption(this.authId).then((res) => {
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
    handleDetail(row) {
      detailTask(row.id).then(res => {
        this.detailFormVisible = true
        for (const key in this.detailForm) {
          this.detailForm[key] = row[key]
        }
        this.detailForm = { ...this.detailForm, ...res.data }
        this.boxList = this.detailForm.boxpackList
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
      })
    },
    handleDelete(row) {
      this.$confirm('确定删除吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.listLoading = true
        deleteTask(row.id)
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
            updateTask(this.dataForm)
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
            addTask(this.dataForm)
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
    formatTaskType(type) {
      switch (type) {
        case 1:
          return '固定下发'
        case 2:
          return '固定上缴'
        case 3:
          return '临时下发'
        case 4:
          return '临时上缴'
      }
    },
    formatDate(timestamp) {
      if (!timestamp || timestamp === 0) {
        return '-'
      }
      const date = new Date(timestamp)
      return formatdate(date, 'yyyy-MM-dd')
    },
    formatDateTime(timestamp) {
      if (!timestamp || timestamp === 0) {
        return '-'
      }
      const date = new Date(timestamp)
      return formatdate(date, 'yyyy-MM-dd hh:mm:ss')
    },
    formatBoxNum(str) {
      return str === '' ? 0 : str.split(',').length
    },
    formatStatus(status) {
      return status ? this.options.statusT.find(item => item.code === status).content : ''
    },
    formatRouteNo(num) {
      return `${num}号线`
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

.el-form--inline .el-form-item {
  margin-right: 0;
  margin-bottom: 8px;
  width: 50%;
  ::v-deep .el-form-item__label {
    color: #99a9bf;
    margin-right: 12px;
  }
}
</style>
