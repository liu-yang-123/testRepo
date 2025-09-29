<template>
  <div class="app-container">

    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-select
        v-model="listQuery.departmentId"
        filterable
        placeholder="请先选择部门"
        class="filter-item"
        style="width: 200px"
      >
        <el-option
          v-for="item in depOptions"
          :key="item.id"
          :label="item.name"
          :value="item.id"
        />
      </el-select>
      <el-date-picker
        v-model="listQueryTmp.date"
        clearable
        class="filter-item"
        type="daterange"
        value-format="timestamp"
        start-placeholder="开始日期"
        range-separator="至"
        end-placeholder="结束日期"
        @change="changeQueryDate"
      />
      <el-input
        v-model="listQuery.title"
        clearable
        class="filter-item"
        style="width: 200px;"
        placeholder="主题"
        :maxlength="128"
      />
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">查找</el-button>
      <el-button v-permission="['/base/handoverLog/save']" class="filter-item" type="primary" icon="el-icon-edit" @click="handleCreate">添加</el-button>
      <a id="download-a" />
    </div>

    <!-- 查询结果 -->
    <el-table
      v-loading="listLoading"
      :data="list"
      element-loading-text="正在查询中。。。"
      border
      fit
      highlight-current-row
      :header-cell-style="{'background-color':'#f5f5f5'}"
    >
      <!-- 展开字段 -->
      <el-table-column type="expand">
        <template slot-scope="props">
          <el-form label-position="left" inline class="demo-table-expand">
            <el-form-item label="创建时间">
              <span> {{ formatDateT(props.row.createTime) }}</span>
            </el-form-item>
            <el-form-item label="创建人">
              <span>{{ props.row.createUserName }}</span>
            </el-form-item>
            <el-form-item label="最后修改时间">
              <span>{{ formatDateT(props.row.updateTime) }}</span>
            </el-form-item>
            <el-form-item label="修改人">
              <span>{{ props.row.updateUser }}</span>
            </el-form-item>
          </el-form>
        </template>
      </el-table-column>
      <!-- 字段 -->
      <el-table-column align="center" label="创建日期" prop="createTime" width="120">
        <template slot-scope="scope">
          <span> {{ formatDateT(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="主题" prop="title" />
      <el-table-column align="center" label="内容" prop="contents">
        <template slot-scope="scope">
          <span>{{ scope.row.contents.length > 20 ? scope.row.contents.substring(0,20)+'...' : scope.row.contents }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="发布人" prop="createUserName" />
      <el-table-column align="center" label="详细" width="80">
        <template slot-scope="scope">
          <el-button type="primary" size="mini" @click="viewDetail(scope.row)">查看</el-button>
        </template>
      </el-table-column>
      <!-- 操作 -->
      <el-table-column
        v-if="hasPerm(['/base/handoverLog/update','/base/handoverLog/del'])"
        align="center"
        label="操作"
        width="160"
        class-name="small-padding fixed-width"
      >
        <template slot-scope="scope">
          <el-button
            v-permission="['/base/handoverLog/update']"
            type="primary"
            size="mini"
            @click="handleUpdate(scope.row)"
          >编辑
          </el-button>
          <el-button
            v-permission="['/base/handoverLog/del']"
            type="danger"
            size="mini"
            @click="handleDelete(scope.row)"
          >删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="listQuery.page"
      :limit.sync="listQuery.limit"
      @pagination="getList"
    />

    <!-- 添加或修改对话框 -->
    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible" :close-on-click-modal="false" width="40%">
      <el-form
        ref="dataForm"
        :rules="rules"
        status-icon
        label-position="right"
        :model="dataForm"
        label-width="130px"
        :hide-required-asterisk="false"
        style="width:90%;margin-left:0px;"
      >
        <el-form-item label="主题" prop="title">
          <el-input v-model="dataForm.title" placeholder="请输入主题" :maxlength="128" />
        </el-form-item>
        <el-form-item label="内容" prop="contents">
          <el-input v-model="dataForm.contents" type="textarea" :rows="16" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="附图">
          <el-upload
            :action="uploadPath"
            :headers="headers"
            list-type="picture-card"
            :file-list="dialogImageList"
            :limit="1"
            :before-upload="beforeImageUpload"
            :on-success="imageUploadSuccess"
            :on-remove="imageRemove"
            :on-preview="imagePreview"
          >
            <i class="el-icon-plus" />
          </el-upload>
        </el-form-item>
        <el-form-item label="附件">
          <el-upload
            :action="uploadPath"
            :headers="headers"
            :file-list="dialogFileList"
            :limit="1"
            :on-success="fileUploadSuccess"
            :on-remove="fileRemove"
          >
            <el-button size="small" type="primary">点击上传</el-button>
          </el-upload>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button v-if="dialogStatus=='create'" type="primary" @click="createData">确定</el-button>
        <el-button v-else type="primary" @click="updateData">确定</el-button>
      </div>
    </el-dialog>

    <el-dialog :visible.sync="dialogImageVisible">
      <img width="100%" :src="dialogImageUrl" alt="">
    </el-dialog>

    <!-- 明细 -->
    <el-dialog title="内容" :visible.sync="detailVisible" width="40%" :close-on-click-modal="false">
      <el-form
        ref="detailForm"
        status-icon
        label-position="right"
        :model="detailForm"
        label-width="130px"
        :hide-required-asterisk="false"
        style="width:90%;margin-left:0px;"
      >
        <el-form-item label="主题" prop="title">
          <el-input v-model="detailForm.title" readonly />
        </el-form-item>
        <el-form-item label="内容" prop="contents">
          <el-input v-model="detailForm.contents" type="textarea" :rows="16" readonly />
        </el-form-item>
        <el-form-item label="附图">
          <el-image
            :style="{height: dialogImageUrl?'150px':'30px'}"
            fit="contain"
            :src="dialogImageUrl"
            :preview-src-list="dialogImageUrl?[dialogImageUrl]:[]"
          >
            <div slot="placeholder">
              <span style="color: gray; margin-left: 15px;">加载中...</span>
            </div>
            <div slot="error">
              <span style="color: gray; margin-left: 15px;">无附图</span>
            </div>
          </el-image>
        </el-form-item>
        <el-form-item label="附件">
          <span>{{ detailForm.fileName }}</span>
          <el-button
            size="small"
            plain
            style="margin-left: 10px;"
            :type="detailForm.fileUrl?'primary':'info'"
            :disabled="!detailForm.fileUrl"
            @click="fileDownload"
          >下载</el-button>
        </el-form-item>
      </el-form>
      <!-- 操作 -->
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailVisible = false">关闭</el-button>
      </div>
    </el-dialog>
    <!-- 明细END -->

    <el-tooltip placement="top" content="返回顶部">
      <back-to-top :visibility-height="100" />
    </el-tooltip>
  </div>
</template>

<script>
import { authOption } from '@/api/common/selectOption'
import { listLog, deleteLog, addLog, updateLog, imgBase64 } from '@/api/clean/handoverLog'
import BackToTop from '@/components/BackToTop/index'
import Pagination from '@/components/Pagination/index'
import checkPermission from '@/utils/permission'
import { formatdate } from '@/utils/date'
import { getToken } from '@/utils/auth'
import { uploadPath } from '@/api/common/common'
import { downLoadFile } from '@/utils/fileRequest'

export default {
  name: 'HandoverLog',
  components: { BackToTop, Pagination },
  filters: {
    formatDateTime(timestamp) {
      return formatdate(timestamp, 'yyyy-MM-dd hh:mm:ss')
    }
  },
  data() {
    return {
      depOptions: [],
      list: [],
      total: 0,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 10,
        departmentId: null
      },
      listQueryTmp: {
        date: []
      },
      dataForm: {},
      dialogFormVisible: false,
      downloadLoading: false,
      dialogStatus: 'create',
      textMap: {
        update: '编辑',
        create: '创建'
      },
      // 添加或编辑规则
      rules: {
        type: [
          { required: true, message: '请选择日志类型', trigger: 'change' }
        ],
        title: [
          { required: true, message: '请输入主题', trigger: 'blur' }
        ],
        contents: [
          { required: true, message: '请输入内容', trigger: 'blur' }
        ]
      },
      // 查看明细
      detailVisible: false,
      detailForm: {},
      // 选择器数据
      options: {},
      // 数据字典
      dictionaries: {},
      // 上传相关
      baseUrl: process.env.VUE_APP_BASE_CLEAR_API,
      uploadPath,
      headers: {
        'X-Token': getToken()
      },
      dialogImageVisible: false,
      dialogImageList: [],
      dialogImageUrl: '',
      dialogFileList: []
    }
  },
  created() {
    authOption().then((res) => {
      this.depOptions = res.data
      if (this.depOptions.length > 0) {
        this.listQuery.departmentId = this.depOptions[0].id
        this.getList()
      }
    })
  },
  methods: {
    // 加载列表
    getList() {
      this.listLoading = true
      listLog(this.listQuery).then(res => {
        this.list = res.data.list
        this.total = res.data.total
        this.listLoading = false
      }).catch(() => {
        this.list = []
        this.total = 0
        this.listLoading = false
      })
    },
    handleFilter() {
      this.listQuery.page = 1
      this.getList()
    },
    handleCreate(row) {
      this.dataForm = Object.assign({}, row)
      this.dialogStatus = 'create'
      this.dialogFormVisible = true
      this.dialogImageList = []
      this.dialogImageUrl = ''
      this.dialogFileList = []
      this.dataForm.departmentId = this.listQuery.departmentId
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    handleUpdate(row) {
      this.dataForm = Object.assign({}, row)
      this.dialogStatus = 'update'
      this.dialogFormVisible = true
      this.dialogImageList = []
      this.dialogImageUrl = ''
      if (row.imageUrl) {
        imgBase64(row.imageUrl).then(res => {
          const src = res.data
          this.dialogImageList = [{ name: row.imageUrl, url: src }]
          this.dialogImageUrl = ''
        })
      }
      if (row.fileUrl) {
        this.dialogFileList = [{ name: row.fileName, url: row.fileUrl }]
      } else {
        this.dialogFileList = []
      }
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    handleDelete(row) {
      this.$confirm('确定删除吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteLog(row.id).then(response => {
          this.$notify.success({
            title: '成功',
            message: '删除成功'
          })
          this.getList()
        }).catch(response => {
          this.$notify.error({
            title: '失败',
            message: response.data.msg
          })
        })
      })
    },
    createData() {
      this.$refs['dataForm'].validate(valid => {
        if (valid) {
          this.openLoading()
          addLog(this.dataForm)
            .then(response => {
              this.getList()
              this.dialogFormVisible = false
              this.$notify.success({
                title: '成功',
                message: '添加成功'
              })
            })
            .catch(response => {
              this.$notify.error({
                title: '失败',
                message: response.data.msg
              })
            })
            .finally(() => {
              this.loading.close()
            })
        }
      })
    },
    updateData() {
      this.$refs['dataForm'].validate(valid => {
        if (valid) {
          updateLog(this.dataForm)
            .then(() => {
              this.getList()
              this.dialogFormVisible = false
              this.$notify.success({
                title: '成功',
                message: '更新成功'
              })
            })
            .catch(response => {
              this.$notify.error({
                title: '失败',
                message: response.data.msg
              })
            })
        }
      })
    },
    viewDetail(row) {
      this.detailForm = { ...row }
      this.detailVisible = true
      this.dialogImageUrl = ''
      if (this.detailForm.imageUrl) {
        imgBase64(this.detailForm.imageUrl).then(res => {
          this.dialogImageUrl = res.data
        })
      }
    },
    // 检查权限
    hasPerm(value) {
      return checkPermission(value)
    },
    changeQueryDate() {
      this.listQuery.dateBegin = (this.listQueryTmp.date && this.listQueryTmp.date[0]) || undefined
      this.listQuery.dateEnd = (this.listQueryTmp.date && this.listQueryTmp.date[1]) || undefined
    },
    // 添加时等待滚动条
    openLoading() {
      this.loading = this.$loading({
        lock: true,
        text: '正在提交...请勿进行其它操作',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      })
    },
    //
    beforeImageUpload(file) {
      const test = /^image\/(jpeg|png|jpg|gif)$/.test(file.type)
      if (!test) {
        this.$notify.error({
          title: '图片格式错误',
          message: '只支持jpeg、png、jpg、gif格式'
        })
      }
      return test
    },
    imageUploadSuccess(res, file) {
      if (res.code === 0) {
        this.dataForm.imageUrl = res.data
      } else {
        this.$message.error(res.msg)
      }
    },
    formatDateT(timestamp) {
      const m = new Date(timestamp)
      return formatdate(m, 'yyyy-MM-dd')
    },
    imagePreview(file) {
      imgBase64(this.dataForm.imageUrl).then(res => {
        this.dialogImageUrl = res.data.data
        this.dialogImageVisible = true
      })
    },
    imageRemove(file, fileList) {
      this.dataForm.imageUrl = ''
    },
    fileUploadSuccess(res, file) {
      this.dataForm.fileUrl = res.data
      this.dataForm.fileName = file.name
    },
    fileRemove(file, fileList) {
      this.dataForm.fileUrl = ''
      this.dataForm.fileName = ''
    },
    fileDownload() {
      const url = '/base/common/fileDownLoad'
      const that = this
      this.openLoading()
      downLoadFile(url, { fileName: this.detailForm.fileName, fileUrl: this.detailForm.fileUrl }, function(res) {
        that.loading.close()
        if (res.data) {
          const file = res.data
          const fileName = that.detailForm.fileName
          const blobObject = new Blob([file])
          // 是IE浏览器
          if (!!window.ActiveXObject || 'ActiveXObject' in window) {
            window.navigator.msSaveOrOpenBlob(blobObject, fileName)
          } else { // 火狐谷歌都兼容
            // 模板中要有一个预定义好的a标签
            const link = document.getElementById('download-a')
            link.href = URL.createObjectURL(blobObject)
            link.download = fileName
            link.click()
          }
        } else {
          console.log('失败')
        }
      }, function(err) {
        that.loading.close()
        console.log(err)
      })
    }
  }
}
</script>

<style>
  .filter-container *:nth-child(n + 2) {
    margin-left: 10px;
  }
  .demo-table-expand {
    font-size: 0;
  }

  .demo-table-expand label {
    width: 120px;
    color: #99a9bf;
  }

  .demo-table-expand .el-form-item {
    margin-right: 0;
    margin-bottom: 0;
    width: 50%;
  }
</style>
