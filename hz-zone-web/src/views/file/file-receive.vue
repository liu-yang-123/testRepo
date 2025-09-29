<template>
  <div class="app-container">
    <search-bar :search-list="searchList" :list-query="listQuery" :options="options" :role="role" @lookUp="getList" @create="handleCreate" />
    <my-table
      :list-loading="listLoading"
      :data-list="list"
      :table-list="tableList"
      :is-download="isDownload"
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
              v-permission="['/base/common/fileSendDownLoad']"
              type="primary"
              size="mini"
              @click="handleDownload(scope.row)"
            >下载</el-button>
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
  </div>
</template>

<script>
import Pagination from '@/components/Pagination'
import myTable from '@/components/Table'
import searchBar from '@/components/SearchBar'
import { listRecord } from '@/api/file/fileReceive'
import { fileCompanyOption } from '@/api/common/selectOption'
import { formatdate } from '@/utils/date'
import { downLoadFile } from '@/utils/fileRequest'

export default {
  components: { Pagination, myTable, searchBar },
  data() {
    return {
      list: [],
      listQuery: {
        limit: 10,
        page: 1,
        recordTitle: null,
        companyId: null,
        date: []
      },
      listLoading: true,
      total: 0,
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '创建'
      },
      searchList: [
        { name: 'recordTitle', label: '标题' },
        { name: 'companyId', label: '单位', type: 3 },
        { name: 'date', label: '日期', type: 6 }
      ],
      options: {
        companyId: [],
        type: [
          { code: '0', content: '添加' },
          { code: '1', content: '修改' },
          { code: '2', content: '删除' }
        ]
      },
      role: {
        list: '/base/fileRecord/receiveList'
      },
      tableList: [
        {
          label: '发送人',
          prop: 'userName'
        },
        {
          label: '发送单位',
          prop: 'companyName'
        },
        {
          label: '标题',
          prop: 'recordTitle'
        },
        {
          label: '日期时间',
          prop: 'createTime',
          formatter: this.formatDateTime
        },
        {
          label: '文件名称',
          prop: 'fileName'
        },
        {
          label: '文件大小',
          prop: 'fileSize',
          formatter: this.formatFileSize
        },
        {
          label: '是否已读',
          prop: 'readSign',
          formatter: this.formatReadSign
        }
      ],
      dataForm: {
        companyId: null,
        recordTitle: null,
        fileName: null,
        fileUrl: null
      },
      rules: {
        companyId: [
          { required: true, message: '接收单位不能为空', trigger: 'blur' }
        ],
        recordTitle: [
          { required: true, message: '标题不能为空', trigger: 'blur' }
        ],
        fileUrl: [
          { required: true, message: '文件不能为空', trigger: 'blur' }
        ]
      },
      isDownload: false
    }
  },
  mounted() {
    this.getList()

    fileCompanyOption().then(res => {
      this.options.companyId = res.data
    })
  },
  methods: {
    getList() {
      const { date, ...newQuery } = this.listQuery
      if (date) {
        newQuery.dateBegin = date[0]
        newQuery.dateEnd = date[1]
      }
      listRecord(newQuery).then((res) => {
        const data = res.data
        this.list = data.list
        this.total = data.total
        this.listLoading = false
      }).catch(() => {
        this.list = []
      })
    },
    handleCreate() {
      this.dialogStatus = 'create'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].resetFields()
      })
    },
    handleDownload(row) {
      this.$confirm('确定下载吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.listLoading = true
        this.isDownload = true
        const that = this
        downLoadFile('/file', { fileName: row.fileName, fileUrl: row.filePath, recordId: row.id }, function(res) {
          that.listLoading = false
          that.isDownload = false
          if (res.data) {
            const file = res.data
            const fileName = row.fileName
            const blobObject = new Blob([file])
            // 是IE浏览器
            if (!!window.ActiveXObject || 'ActiveXObject' in window) {
              window.navigator.msSaveOrOpenBlob(blobObject, fileName)
            } else { // 火狐谷歌都兼容
            // 模板中要有一个预定义好的a标签
              const link = document.createElement('a')
              link.href = URL.createObjectURL(blobObject)
              link.download = fileName
              link.click()

              URL.revokeObjectURL(link.href)
            }
            that.getList()
          } else {
            console.log('失败')
          }
        }, function(err) {
          that.listLoading = false
          that.isDownload = false
          console.log(err)
          that.$notify.error({
            title: '错误',
            message: err
          })
        }, process.env.VUE_APP_DOWNLOAD_API)
      })
    },
    formatCompanyType(type) {
      switch (type) {
        case 0:
          return '公司'
        case 1:
          return '银行'
      }
    },
    formatReadSign(status) {
      console.log(status)
      switch (status) {
        case 0:
          return '未读'
        case 1:
          return '已读'
      }
    },
    formatFileSize(size) {
      const kbSize = size / 1024
      return kbSize > 1024 ? `${(kbSize / 1024).toFixed(2)} MB` : `${kbSize.toFixed(2)} KB`
    },
    formatDateTime(timestamp) {
      if (!timestamp || timestamp === 0) {
        return '-'
      }
      const date = new Date(timestamp)
      return formatdate(date, 'yyyy-MM-dd hh:mm:ss')
    },
    fileUploadSuccess(res, file) {
      this.dataForm.fileUrl = res.data
      this.dataForm.fileName = file.name
    },
    fileRemove(file, fileList) {
      this.dataForm.fileUrl = ''
      this.dataForm.fileName = ''
    }
  }
}
</script>

  <style scoped lang="scss">
  </style>

