<template>
  <div class="app-container">
    <search-bar :search-list="searchList" :list-query="listQuery" :role="role" @lookUp="getList" @create="handleCreate" />
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
              v-permission="['/base/awards/update']"
              type="primary"
              size="mini"
              @click="handleUpdate(scope.row)"
            >编辑</el-button>
            <el-button
              v-permission="['/base/awards/delete']"
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
        <el-form-item label="奖惩日期" prop="awardsDate">
          <el-date-picker v-model="dataForm.awardsDate" value-format="timestamp" type="date" placeholder="选择日期" style="width:100%" />
        </el-form-item>
        <el-form-item label="奖惩类型" prop="awardsType">
          <el-select v-model="dataForm.awardsType" placeholder="请选择" style="width: 100%">
            <el-option
              v-for="item in awardsList"
              :key="item.code"
              :label="item.content"
              :value="item.code"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="员工" prop="empId">
          <el-select v-model="dataForm.empId" filterable placeholder="请选择" style="width: 100%">
            <el-option
              v-for="item in empOptions"
              :key="item.id"
              :label="item.empNo + ' / ' + item.empName"
              :value="item.id"
            />
          </el-select>
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
import { listAwards, addAwards, updateAwards, delectAwards } from '@/api/employee/rewardManage'
import { employeeOption } from '@/api/common/selectOption'
import { formatdate } from '@/utils/date'
import { reqDictionary } from '@/api/system/dictionary'

export default {
  components: { Pagination, myTable, searchBar },
  data() {
    return {
      list: [],
      listQuery: {
        limit: 10,
        page: 1,
        empName: null,
        empNo: null,
        awardsType: null
      },
      listLoading: true,
      total: 0,
      searchList: [
        { name: 'awardsType', label: '奖惩类型', type: 1, dictionary: 'REWARD_TYPE' },
        { name: 'empNo', label: '员工工号' },
        { name: 'empName', label: '员工名称' }
      ],
      role: {
        list: '/base/awards/list',
        add: '/base/awards/save'
      },
      tableList: [
        {
          label: '奖惩日期',
          prop: 'awardsDate',
          formatter: this.formatDateTime
        },
        {
          label: '奖惩类型',
          prop: 'awardsType',
          dictionary: 'REWARD_TYPE'
        },
        {
          label: '员工编号',
          prop: 'empNo'
        },
        {
          label: '员工名称',
          prop: 'empName'
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
      empOptions: [],
      awardsList: [],
      dataForm: {
        empId: null,
        awardsType: null,
        id: null,
        awardsDate: null,
        comments: null
      },
      rules: {
        awardsDate: [
          { required: true, message: '奖惩日期不能为空', trigger: 'blur' }
        ],
        awardsType: [
          { required: true, message: '奖惩类型不能为空', trigger: 'blur' }
        ],
        empId: [
          { required: true, message: '员工不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  mounted() {
    this.getList()
    this.getNameList()
    this.getDictionary()
  },
  methods: {
    getList(data) {
      if (data) {
        for (const key in data) {
          this.listQuery[key] = data[key]
        }
      }
      listAwards(this.listQuery).then((res) => {
        const data = res.data
        this.list = data.list
        console.log(this.list)
        this.total = data.total
        this.listLoading = false
      })
    },
    getNameList() {
      employeeOption().then(res => {
        this.empOptions = res.data
      })
    },
    getDictionary() {
      reqDictionary('REWARD_TYPE').then(res => {
        this.awardsList = res.data
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
        delectAwards(row.id)
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
            updateAwards(this.dataForm)
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
            addAwards(this.dataForm)
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
    }
  }
}
</script>

<style scoped lang="scss">
</style>
