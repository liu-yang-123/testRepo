<template>
  <div class="app-container">
    <search-bar :options="options" :list-query="listQuery" :search-list="searchList" :role="role" @lookUp="getList" @create="handleCreate" />
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
              v-permission="['/base/holiday/update']"
              type="primary"
              size="mini"
              @click="handleUpdate(scope.row)"
            >编辑</el-button>
            <el-button
              v-permission="['/base/holiday/delete']"
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
        <el-form-item label="计划日期" prop="planDay">
          <el-date-picker v-model="dataForm.planDay" value-format="timestamp" type="date" placeholder="选择日期" style="width:100%" />
        </el-form-item>
        <el-form-item label="类型" prop="holidayType">
          <el-select v-model="dataForm.holidayType" filterable placeholder="请选择" style="width: 100%">
            <el-option
              v-for="item in options.holidayType"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="计划说明" prop="comments">
          <el-input v-model="dataForm.comments" :maxlength="64" />
        </el-form-item>
        <el-form-item label="调班星期" prop="weekday">
          <el-select v-model="dataForm.weekday" filterable placeholder="请选择" style="width: 100%">
            <el-option
              v-for="item in options.weekday"
              :key="item.id"
              :label="item.name"
              :value="item.id"
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
import { listHoliday, addHoliday, updateHoliday, deleteHoliday } from '@/api/duty/leaveManage'
import { formatdate } from '@/utils/date'
import { dictionaryData } from '@/api/system/dictionary'
import { authOption } from '@/api/common/selectOption'

export default {
  components: { Pagination, myTable, searchBar },
  data() {
    return {
      list: [],
      listQuery: {
        limit: 10,
        page: 1,
        planDay: null
      },
      departmentId: null,
      listLoading: true,
      total: 0,
      searchList: [
        { name: 'planDay', label: '计划日期', type: 2 }
      ],
      options: {
        holidayType: [
          { id: 0, name: '放假' },
          { id: 1, name: '调班' }
        ],
        weekday: [
          { id: 1, name: '星期一' },
          { id: 2, name: '星期二' },
          { id: 3, name: '星期三' },
          { id: 4, name: '星期四' },
          { id: 5, name: '星期五' },
          { id: 6, name: '星期六' },
          { id: 7, name: '星期天' }
        ]
      },
      role: {
        list: '/base/holiday/list',
        add: '/base/holiday/save'
      },
      tableList: [
        {
          label: '计划日期',
          prop: 'planDay',
          formatter: this.formatDateTime
        },
        {
          label: '计划类型',
          prop: 'holidayType',
          formatter: this.formatHoliday
        },
        {
          label: '计划说明',
          prop: 'comments'
        },
        {
          label: '休假/调班星期',
          prop: 'weekday',
          formatter: this.formatWeekday
        }
      ],
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '创建'
      },
      dataForm: {
        comments: null,
        holidayType: null,
        id: null,
        planDay: null,
        weekday: null
      },
      empOptions: [],
      vehicleOption: [],
      dictionaryData,
      rules: {
        planDay: [
          { required: true, message: '计划日期不能为空', trigger: 'blur' }
        ],
        weekday: [
          { required: true, message: '调班星期不能为空', trigger: 'blur' }
        ],
        holidayType: [
          { required: true, message: '计划类型不能为空', trigger: 'blur' }
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
      listHoliday(this.listQuery).then((res) => {
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
          const [res1] = res
          this.options.departmentId = res1.data
          this.listQuery.departmentId = this.options.departmentId[0].id
          reslove()
        }).catch(err => {
          reject(err)
        })
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
        deleteHoliday(row.id)
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
            updateHoliday(this.dataForm)
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
            addHoliday(this.dataForm)
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
    formatHoliday(type) {
      switch (type) {
        case 0:
          return '放假'
        case 1:
          return '调休'
      }
    },
    formatWeekday(id) {
      return this.options.weekday.filter(item => item.id === id)[0].name
    }
  }
}
</script>

<style scoped lang="scss">
</style>
