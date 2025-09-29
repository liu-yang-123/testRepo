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
              v-permission="['/base/subjects/update']"
              type="primary"
              size="mini"
              @click="handleUpdate(scope.row)"
            >编辑</el-button>
            <el-button
              v-permission="['/base/subjects/delete']"
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
      <el-form ref="dataForm" :rules="rules" :model="dataForm" status-icon label-position="right" label-width="140px" style="width: 400px; margin-left:50px;">
        <el-form-item label="培训日期" prop="trainDate">
          <el-date-picker v-model="dataForm.trainDate" value-format="timestamp" type="date" placeholder="选择日期" style="width:100%" />
        </el-form-item>
        <el-form-item label="培训类别" prop="trainType">
          <el-select
            v-model="dataForm.trainType"
            :placeholder="`请选择`"
            style="width: 100%"
          >
            <el-option
              v-for="item in trainList"
              :key="item.code"
              :label="item.content"
              :value="item.code"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="培训主题" prop="trainTitle">
          <el-input v-model="dataForm.trainTitle" :maxlength="64" />
        </el-form-item>
        <el-form-item label="培训地点" prop="place">
          <el-input v-model="dataForm.place" :maxlength="64" />
        </el-form-item>
        <el-form-item label="培训讲师" prop="trainer">
          <el-input v-model="dataForm.trainer" :maxlength="64" />
        </el-form-item>
        <el-form-item label="培训时长（小时）" prop="times">
          <el-input v-model="dataForm.times" :maxlength="64" />
        </el-form-item>
        <el-form-item label="培训内容" prop="trainContent">
          <el-input v-model="dataForm.trainContent" :maxlength="64" />
        </el-form-item>
        <el-form-item label="考核方式" prop="test">
          <el-input v-model="dataForm.test" :maxlength="64" />
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
import { listSubjects, addSubjects, updateSubjects, delectSubjects } from '@/api/employee/trainingSubject'
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
        trainDate: null,
        trainTitle: null,
        trainType: null
      },
      listLoading: true,
      total: 0,
      searchList: [
        { name: 'trainDate', label: '培训时间', type: 2 },
        { name: 'trainType', label: '培训类别', type: 1, dictionary: 'TRAINING_TYPE' },
        { name: 'trainTitle', label: '培训主题' }
      ],
      role: {
        list: '/base/subjects/list',
        add: '/base/subjects/save'
      },
      tableList: [
        {
          label: '培训时间',
          prop: 'trainDate',
          formatter: this.formatDateTime
        },
        {
          label: '培训类别',
          prop: 'trainType'
          // formatter: this.formatTrain
        },
        {
          label: '培训主题',
          prop: 'trainTitle'
        },
        {
          label: '培训地点',
          prop: 'place'
        },
        {
          label: '培训讲师',
          prop: 'trainer'
        },
        {
          label: '培训时长(小时)',
          prop: 'times'
        },
        {
          label: '培训内容',
          prop: 'trainContent'
        },
        {
          label: '考核方式',
          prop: 'test'
        }
      ],
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '创建'
      },
      dataForm: {
        place: '',
        test: '',
        times: null,
        trainContent: '',
        trainDate: null,
        trainTitle: '',
        trainType: '',
        trainer: '',
        id: null
      },
      trainList: [],
      rules: {
        trainContent: [
          { required: true, message: '培训内容不能为空', trigger: 'blur' }
        ],
        trainDate: [
          { required: true, message: '培训日期不能为空', trigger: 'blur' }
        ],
        trainTitle: [
          { required: true, message: '培训主题不能为空', trigger: 'blur' }
        ],
        trainType: [
          { required: true, message: '培训类别不能为空', trigger: 'blur' }
        ],
        trainer: [
          { required: true, message: '培训老师不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  mounted() {
    this.getList()
    this.getDictionary()
  },
  methods: {
    getList(data) {
      if (data) {
        for (const key in data) {
          this.listQuery[key] = data[key]
        }
      }
      listSubjects(this.listQuery).then((res) => {
        const data = res.data
        this.list = this.formatTrainType(data.list)
        this.total = data.total
        this.listLoading = false
      })
    },
    getDictionary() {
      reqDictionary('TRAINING_TYPE').then(res => {
        this.trainList = res.data
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
        delectSubjects(row.id)
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
            updateSubjects(this.dataForm)
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
            addSubjects(this.dataForm)
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
    formatTrainType(data) {
      reqDictionary('TRAINING_TYPE').then(res => {
        for (const item1 of data) {
          for (const item2 of res.data) {
            if (item1.trainType === item2.code) {
              item1.trainType = item2.content
              break
            }
          }
        }
      })
      return data
    }
  }
}
</script>

<style scoped lang="scss">
</style>
