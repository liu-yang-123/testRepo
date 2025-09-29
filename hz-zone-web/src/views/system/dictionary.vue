<template>
  <div class="app-container">
    <!-- <div class="filter-container">
      <el-input
        v-model="listQuery.code"
        clearable
        class="filter-item"
        style="width: 200px"
        placeholder="请输入编号"
        :maxlength="32"
      />
      <el-input
        v-model="listQuery.groups"
        clearable
        class="filter-item"
        style="width: 200px"
        placeholder="请输入分类"
        :maxlength="32"
      />
      <el-input
        v-model="listQuery.content"
        clearable
        class="filter-item"
        style="width: 200px"
        placeholder="请输入内容"
        :maxlength="32"
      />
      <el-button
        v-permission="['/base/dictionary/list']"
        class="filter-item"
        type="primary"
        icon="el-icon-search"
        @click="getList()"
      >查找</el-button>
      <el-button
        v-permission="['/base/dictionary/save']"
        class="filter-item"
        type="primary"
        icon="el-icon-edit"
        @click="handleCreate"
      >添加</el-button>
    </div> -->
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
              v-permission="['/base/dictionary/update']"
              type="primary"
              size="mini"
              @click="handleUpdate(scope.row)"
            >编辑</el-button>
            <el-button
              v-permission="['/base/dictionary/delete']"
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
      <el-form ref="dataForm" :rules="rules" :model="dataForm" status-icon label-position="left" label-width="100px" style="width: 400px; margin-left:50px;">
        <el-form-item label="编号" prop="code">
          <el-input v-model="dataForm.code" :maxlength="32" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="dataForm.content" :maxlength="64" />
        </el-form-item>
        <el-form-item label="分类名称" prop="groups">
          <el-input v-model="dataForm.groups" :maxlength="64" />
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
import {
  listDictionary,
  addDictionary,
  updateDictionary,
  delectDictionary
} from '@/api/system/dictionary'

export default {
  components: { Pagination, myTable, searchBar },
  data() {
    return {
      list: [],
      listQuery: {
        limit: 10,
        page: 1,
        code: null,
        content: null,
        groups: null
      },
      listLoading: true,
      total: 0,
      searchList: [
        { name: 'code', label: '编号' },
        { name: 'content', label: '内容' },
        { name: 'groups', label: '分类' }
      ],
      role: {
        list: '/base/dictionary/list',
        add: '/base/dictionary/save'
      },
      tableList: [
        {
          label: '编号',
          prop: 'code'
        },
        {
          label: '分类',
          prop: 'groups'
        },
        {
          label: '内容',
          prop: 'content'
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
      dataForm: {
        code: '',
        comments: '',
        content: '',
        groups: '',
        id: null
      },
      rules: {
        code: [
          { required: true, message: '编号不能为空', trigger: 'blur' }
        ],
        content: [
          { required: true, message: '内容不能为空', trigger: 'blur' }
        ],
        groups: [
          { required: true, message: '分类名称不能为空', trigger: 'blur' }
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
      listDictionary(this.listQuery).then((res) => {
        const data = res.data
        this.list = data.list
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
        delectDictionary(row.id)
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
            updateDictionary(this.dataForm)
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
            addDictionary(this.dataForm)
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
    }
  }
}
</script>

<style scoped lang="scss">
// .filter-container > .el-input:not(:first-child) {
//   margin-left: 10px;
// }
// .filter-container > button {
//   margin-left: 10px;
// }
</style>
