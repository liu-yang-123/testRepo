<template>
  <!-- 1:select[数据字典] 2:date 3:select[option] 4:tree 5:date(月份) 6:date(区间) -->
  <div class="filter-container">
    <template v-for="(item, index) in searchList">
      <el-date-picker
        v-if="item.type === 2"
        :key="index"
        v-model="listQuery[item.name]"
        :size="size"
        value-format="timestamp"
        type="date"
        :placeholder="`选择${item.label}` || '选择日期'"
        class="filter-item"
        :style="{width: item.width || '200px'}"
        :clearable="item.notClear ? false : true"
        @change="item.change ? handleChange($event,item.change) : false"
      />
      <el-select
        v-else-if="item.type === 1"
        :key="index"
        v-model="listQuery[item.name]"
        :size="size"
        clearable
        :placeholder="`请选择${item.label}`"
        class="filter-item"
        :style="{width: item.width || '200px'}"
      >
        <el-option
          v-for="item1 in item.options"
          :key="item1.code"
          :label="item1.content"
          :value="item1.code"
        />
      </el-select>
      <el-select
        v-else-if="item.type === 3"
        :key="index"
        v-model="listQuery[item.name]"
        :size="size"
        :clearable="item.notClear ? false : true"
        filterable
        :placeholder="`请选择${item.label}`"
        class="filter-item"
        :style="{width: item.width || '200px'}"
        @change="item.change ? handleChange($event,item.change) : false"
      >
        <el-option
          v-for="item1 in options[item.name]"
          :key="selectedValue(item1)"
          :label="item1.content || item1.name || item1.label || item1.empName || `${item1.routeNo}号线`"
          :value="selectedValue(item1)"
        />
      </el-select>
      <treeSelect
        v-else-if="item.type === 4"
        :key="index"
        v-model="listQuery[item.name]"
        :normalizer="normalizer"
        :show-count="true"
        :options="options[item.name]"
        :placeholder="`请选择${item.label}`"
        class="filter-item"
        :style="{width: item.width || '200px',fontSize: '14px'}"
      />
      <el-date-picker
        v-else-if="item.type === 5"
        :key="index"
        v-model="listQuery[item.name]"
        :clearable="item.notClear ? false : true"
        type="month"
        class="filter-item"
        value-format="yyyy-MM"
        :placeholder="item.label ? `选择${item.label}` : '选择月份'"
        :style="{width: item.width || '200px'}"
      />
      <el-date-picker
        v-else-if="item.type === 6"
        :key="index"
        v-model="listQuery[item.name]"
        type="daterange"
        class="filter-item"
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        value-format="timestamp"
      />
      <el-input
        v-else
        :key="index"
        v-model="listQuery[item.name]"
        :size="size"
        clearable
        class="filter-item"
        :style="{width: item.width || '200px'}"
        :placeholder="`请输入${item.label}`"
        :maxlength="32"
      />
    </template>
    <el-button
      v-if="role.refresh"
      class="filter-item"
      :size="size"
      type="primary"
      icon="el-icon-refresh"
      @click="refresh"
    >刷新</el-button>
    <el-button
      v-permission="[role.list]"
      class="filter-item"
      :size="size"
      type="primary"
      icon="el-icon-search"
      @click="lookUp"
    >查找</el-button>
    <el-button
      v-permission="[role.add]"
      class="filter-item"
      :size="size"
      type="primary"
      icon="el-icon-edit"
      @click="handleCreate"
    >添加</el-button>
    <el-button
      v-for="item in role.addList"
      :key="item.permission"
      v-permission="[item.permission]"
      class="filter-item"
      :size="size"
      type="primary"
      icon="el-icon-edit"
      @click="handleCreate(item.name)"
    >添加{{ item.name }}</el-button>
    <el-button
      v-if="role.print"
      v-permission="[role.print.permission]"
      class="filter-item"
      :size="size"
      type="success"
      icon="el-icon-printer"
      :disabled="role.print.disabled"
      @click="handlePrint"
    >批量打印</el-button>
    <el-button
      v-if="role.download"
      v-permission="[role.download.url]"
      class="filter-item"
      :size="size"
      type="primary"
      icon="el-icon-download"
      @click="handleDownload"
    >下载</el-button>
    <el-upload
      v-permission="[role.import]"
      style="display:inline-block"
      :action="importPath"
      :size="size"
      accept=".xlsx,.xls"
      :show-file-list="false"
      :before-upload="beforeUpload"
      :on-success="handleSuccess"
    >
      <el-button
        class="filter-item"
        type="primary"
        :size="size"
        icon="el-icon-upload2"
      >导入列表</el-button>
    </el-upload>
    <slot name="more" />
  </div>
</template>

<script>
// import { TreeSelect } from 'module'
import { dictionaryData, reqDictionary } from '@/api/system/dictionary'
import TreeSelect from '@riophae/vue-treeselect'
import requestBlob from '@/utils/requestBlob'
import { downloadFile } from '@/utils/downloadFile'
import { formatdate } from '@/utils/date'

export default {
  components: { TreeSelect },
  props: {
    searchList: {
      type: Array,
      default() {
        return []
      }
    },
    role: {
      type: Object,
      default: null
    },
    options: {
      type: Object,
      default: null
    },
    listQuery: {
      type: Object,
      default: null
    },
    importPath: {
      type: String,
      default: '',
      required: false
    },
    size: {
      type: String,
      default: 'medium',
      required: false
    }
  },
  data() {
    return {
      loading: null
    }
  },
  computed: {
    selectedValue() {
      return function(item) {
        if (item.code === 0) {
          return item.code
        } else {
          return item.code || item.id || item.value
        }
      }
    }
  },
  mounted() {
    this.getDictionary()
  },
  methods: {
    lookUp() {
      this.listQuery.page = 1
      this.$emit('lookUp', this.listQuery)
    },
    refresh() {
      this.$emit('refresh')
    },
    handleCreate(name) {
      this.$emit('create', name)
    },
    getDictionary() {
      for (const item of this.searchList) {
        if (item.dictionary) {
          if (dictionaryData[item.dictionary]) {
            this.$set(item, 'options', dictionaryData[item.dictionary])
          } else {
            reqDictionary(item.dictionary).then(res => {
              this.$set(item, 'options', res.data)
            })
          }
        }
      }
    },
    normalizer(node) {
      return {
        id: node.id,
        label: node.name,
        children: node.children
      }
    },
    // 上传
    beforeUpload(file) {
      const isExcel = file.type === 'application/vnd.ms-excel' || file.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
      if (!isExcel) {
        this.$notify.error({
          title: '文件格式错误',
          message: '只支持xls,xlsx格式'
        })
      }
      return isExcel
    },
    handleSuccess(response, file, fileList) {
      if (response.code === 0) {
        this.$notify.success({
          title: '成功',
          message: '文件上传成功'
        })
      } else {
        this.$notify.error({
          title: '失败',
          message: response.msg
        })
      }
    },
    handleDownload() {
      if (this.listQuery.date) {
        const params = Object.assign({}, this.listQuery)
        delete params.limit
        delete params.page
        let title
        if (typeof (this.listQuery.date) === 'object') {
          if (this.listQuery.date[1] - this.listQuery.date[0] > 2678400000) {
            return this.$message.warning('时间范围不能超过31天')
          }
          params.beginDate = this.listQuery.date[0]
          params.endDate = this.listQuery.date[1]
          delete params.date
          title = this.role.download.title ? `${this.formatDate(this.listQuery.date[0])} ~ ${this.formatDate(this.listQuery.date[1])} ${this.role.download.title}` : '银行信息打印表'
        } else {
          title = this.role.download.title ? `${this.listQuery.date} ${this.role.download.title}` : '银行信息打印表'
        }
        this.openLoading()
        downloadFile(this.requestFn, params, title, () => { this.loading.close() }, '.xlsx')
      } else {
        return this.$message.warning('请先选择日期')
      }
    },
    async handleChange(val, fn) {
      this.openLoading()
      await fn().finally(() => {
        this.loading.close()
      })
    },
    handlePrint() {
      this.$emit('print')
    },
    formatDate(timestamp) {
      if (!timestamp || timestamp === 0) {
        return '-'
      }
      const date = new Date(timestamp)
      return formatdate(date, 'yyyy-MM-dd')
    },
    openLoading() {
      this.loading = this.$loading({
        lock: true,
        text: '正在加载...请勿进行其它操作',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      })
    },
    requestFn(params) {
      return requestBlob({
        url: this.role.download.url,
        method: 'get',
        params
      })
    }
  }
}
</script>

<style scoped lang="scss">
.filter-container  > div{
  margin-right: 10px;
}
</style>
