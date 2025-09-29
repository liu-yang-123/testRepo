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
              v-permission="['/base/records/update']"
              type="primary"
              size="mini"
              @click="handleUpdate(scope.row)"
            >编辑</el-button>
            <el-button
              v-permission="['/base/records/delete']"
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

    <el-dialog title="编辑" :visible.sync="dialogFormVisible" :close-on-click-modal="false">
      <el-form ref="dataForm" :rules="rules" :model="dataForm" status-icon label-position="right" label-width="140px" style="width: 400px; margin-left:50px;">
        <el-form-item label="培训讲师" prop="trainTitle">
          <el-input
            v-model="dataForm.trainTitle"
            readonly
            placeholder="点击选择"
            @focus="handleSubject(dataForm)"
          />
        </el-form-item>
        <el-form-item label="考试员工" prop="empId">
          <el-select v-model="dataForm.empId" filterable placeholder="请选择" style="width: 100%">
            <el-option
              v-for="item in empOptions"
              :key="item.id"
              :label="item.empNo + ' / ' + item.empName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="考核分数" prop="score">
          <el-input v-model="dataForm.score" :maxlength="32" oninput="value=value.replace(/[^\.\d]/g,'')" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="updateData()">确定</el-button>
      </div>
    </el-dialog>

    <!-- 批量添加对话框 -->
    <el-dialog title="批量添加" :visible.sync="multiFormVisible" :close-on-click-modal="false" width="35%">
      <!--公共属性-->
      <div class="filter-container">
        <span style="font-weight: bold">培训主题：</span>
        <el-input
          v-model="addForm.trainTitle"
          readonly
          placeholder="点击选择"
          style="width: 250px;"
          @focus="handleSubject(addForm)"
        />
      </div>
      <!--列表-->
      <el-table
        :data="addForm.recordDtoList"
        border
        fit
        :header-cell-style="{'background-color':'#f5f5f5'}"
      >
        <!-- 字段 -->
        <el-table-column align="center" width="80">
          <template slot="header">
            <el-button
              plain
              type="primary"
              icon="el-icon-circle-plus-outline"
              @click="addForm.recordDtoList.push({})"
            />
          </template>
          <template slot-scope="scope">
            <el-button
              plain
              type="danger"
              icon="el-icon-remove-outline"
              @click="addForm.recordDtoList.splice(scope.$index, 1)"
            />
          </template>
        </el-table-column>
        <el-table-column align="center" label="考试员工">
          <template slot-scope="scope">
            <el-select v-model="scope.row.empId" filterable placeholder="请选择" style="width: 100%">
              <el-option
                v-for="item in empOptions"
                :key="item.id"
                :label="item.empNo + ' / ' + item.empName"
                :value="item.id"
              />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column align="center" label="考试成绩">
          <template slot-scope="scope">
            <el-input
              v-model="scope.row.score"
              class="input-nb"
              oninput="value=value.replace(/[^\.\d]/g,'')"
              :maxlength="32"
            />
          </template>
        </el-table-column>
      </el-table>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="createData">确定</el-button>
        <el-button @click="multiFormVisible = false">取消</el-button>
      </div>
    </el-dialog>

    <!-- 选择培训主题面板 -->
    <el-dialog title="选择主题" :visible.sync="subjectSelectionVisible" width="60%">
      <div class="filter-container">
        <el-date-picker
          v-model="subjectListQuery.trainDate"
          style="width: 150px"
          class="filter-item"
          clearable
          value-format="timestamp"
          type="date"
          placeholder="培训日期"
        />
        <el-input
          v-model="subjectListQuery.trainTitle"
          clearable
          class="filter-item"
          style="width: 150px;"
          placeholder="培训主题"
          :maxlength="32"
        />
        <el-select v-model="subjectListQuery.trainType" clearable placeholder="培训类别" style="width: 150px;" class="filter-item">
          <el-option
            v-for="item in trainList"
            :key="item.code"
            :label="item.content"
            :value="item.code"
          />
        </el-select>
        <el-button class="filter-item" type="primary" icon="el-icon-search" @click="filterSubject">查找</el-button>
      </div>
      <el-table
        v-loading="subjectListLoading"
        :data="subjectList"
        element-loading-text="正在查询中。。。"
        border
        fit
        highlight-current-row
        :header-cell-style="{'background-color':'#f5f5f5'}"
      >
        <!-- 字段 -->
        <el-table-column align="center" label="培训日期">
          <template slot-scope="scope">
            <span>{{ formatDateTime(scope.row.trainDate) }}</span>
          </template>
        </el-table-column>
        <el-table-column align="center" label="培训类别">
          <template slot-scope="scope">
            <dictionary :dic="trainList" :code="scope.row.trainType" />
          </template>
        </el-table-column>
        <el-table-column align="center" label="培训主题" prop="trainTitle" />
        <el-table-column align="center" label="培训讲师" prop="trainer" />
        <!--操作 -->
        <el-table-column align="center" label="操作" width="100" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button v-if="subjectSelection.id === scope.row.id" type="danger" size="mini" @click="checkedSubject(scope.row)">取消</el-button>
            <el-button v-else type="primary" size="mini" @click="checkedSubject(scope.row)">选择</el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination
        v-show="subjectTotal>0"
        :total="subjectTotal"
        :page.sync="subjectListQuery.page"
        :limit.sync="subjectListQuery.limit"
        @pagination="getSubjectList"
      />
      <div slot="footer" class="dialog-footer">
        <el-button @click="subjectSelectionVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmSubject">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import Pagination from '@/components/Pagination'
import myTable from '@/components/Table'
import searchBar from '@/components/SearchBar'
import { listRecord, addRecord, updateRecord, delectRecord } from '@/api/employee/trainingRecord'
import { listSubjects } from '@/api/employee/trainingSubject'
import { employeeOption } from '@/api/common/selectOption'
import { formatdate } from '@/utils/date'
import { reqDictionary } from '@/api/system/dictionary'
import Dictionary from '@/components/Dictionary'

export default {
  components: { Pagination, myTable, searchBar, Dictionary },
  data() {
    return {
      list: [],
      listQuery: {
        limit: 10,
        page: 1,
        ipAddress: null
      },
      listLoading: true,
      total: 0,
      searchList: [
        { name: 'trainTitle', label: '培训主题' },
        { name: 'empNo', label: '员工工号' },
        { name: 'empName', label: '员工名称' }
      ],
      role: {
        list: '/base/records/list',
        add: '/base/records/savemulti'
      },
      tableList: [
        {
          label: '培训时间',
          prop: 'trainDate',
          formatter: this.formatDateTime
        },
        {
          label: '培训主题',
          prop: 'trainTitle'
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
          label: '考试成绩',
          prop: 'score'
        }
      ],
      // 编辑
      dialogFormVisible: false,
      dataForm: {
        empId: '',
        id: '',
        score: null,
        trainId: '',
        trainTitle: ''
      },
      // 批量添加
      multiFormVisible: false,
      addForm: {
        recordDtoList: [],
        trainId: null
      },
      empOptions: [],
      // 选择培训
      subjectSelectionVisible: false,
      subjectListLoading: false,
      subjectSelection: {
        id: null,
        name: ''
      },
      subjectListQuery: {
        page: 1,
        limit: 10,
        sort: 'create_time',
        order: 'desc'
      },
      subjectList: [],
      subjectTotal: 0,
      formStatus: '',

      trainList: [],
      rules: {
        trainTitle: [
          { required: true, message: '培训主题不能为空', trigger: 'blur' }
        ],
        empId: [
          { required: true, message: '考试员工不能为空', trigger: 'blur' }
        ],
        score: [
          { required: true, message: '考核分数不能为空', trigger: 'blur' }
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
      listRecord(this.listQuery).then((res) => {
        const data = res.data
        this.list = data.list
        console.log(this.list)
        this.total = data.total
        this.listLoading = false
      })
    },
    getDictionary() {
      reqDictionary('TRAINING_TYPE').then(res => {
        this.trainList = res.data
      })
    },
    getNameList() {
      employeeOption().then(res => {
        this.empOptions = res.data
      })
    },
    getSubjectList() {
      this.subjectListLoading = true
      listSubjects(this.subjectListQuery).then(res => {
        this.subjectList = res.data.list
        this.subjectTotal = res.data.total
        this.subjectListLoading = false
      }).catch(() => {
        this.subjectList = []
        this.subjectTotal = 0
        this.subjectListLoading = false
      })
    },
    handleCreate() {
      this.multiFormVisible = true
      this.formStatus = 'addForm'
      this.addForm.recordDtoList = [{}]
    },
    handleUpdate(row) {
      //   this.dataForm = Object.assign({}, row)
      this.formStatus = 'dataForm'
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
        delectRecord(row.id)
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
    updateData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          this.listLoading = true
          delete this.dataForm.trainTitle
          updateRecord(this.dataForm)
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
        }
      })
    },
    createData() {
      if (!this.addForm.trainId) {
        this.$notify.error({
          title: '添加失败',
          message: '请选择培训课题！'
        })
        return
      }
      this.addForm.recordDtoList = this.addForm.recordDtoList.filter(item => JSON.stringify(item) !== '{}')
      console.log(this.addForm.recordDtoList)
      delete this.addForm.trainTitle
      addRecord(this.addForm).then(response => {
        this.getList()
        this.multiFormVisible = false
        this.$notify.success({
          title: '成功',
          message: '添加成功'
        })
      }).catch(response => {
        this.$notify.error({
          title: '失败',
          message: response.data.errmsg
        })
      }).finally(() => {
        this.listLoading = false
      })
    },
    handleSubject(form) {
      this.subjectSelectionVisible = true
      this.subjectSelection = {
        id: form.trainId,
        name: form.trainTitle
      }
      this.filterSubject()
    },
    checkedSubject(row) {
      if (this.subjectSelection.id === row.id) {
        this.subjectSelection = {
          id: null,
          name: ''
        }
      } else {
        this.subjectSelection = {
          id: row.id,
          name: row.trainTitle
        }
      }
    },
    confirmSubject() {
      this.subjectSelectionVisible = false
      if (this.formStatus === 'addForm') {
        this.addForm.trainId = this.subjectSelection.id
        this.addForm.trainTitle = this.subjectSelection.name
      } else if (this.formStatus === 'dataForm') {
        this.dataForm.trainId = this.subjectSelection.id
        this.dataForm.trainTitle = this.subjectSelection.name
      }
    },
    // 搜索岗位
    filterSubject() {
      this.subjectListQuery.page = 1
      this.getSubjectList()
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
.filter-container div:nth-child(n+2) {
  margin-left: 10px;
}
.filter-container > button {
  margin-left: 10px;
}
</style>
