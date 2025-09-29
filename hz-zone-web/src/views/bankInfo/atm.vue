<template>
  <div class="app-container">
    <el-row :gutter="20">
      <div class="filter-container">
        <el-button
          class="filter-item"
          type="primary"
          icon="el-icon-refresh"
          @click="getTree"
        >刷新</el-button>
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
          :expand-list="expandList"
          height="450px"
        />
        <pagination
          v-show="total > 0"
          :total="total"
          :page.sync="listQuery.page"
          :limit.sync="listQuery.limit"
          @pagination="getList"
        />
      </el-col>
    </el-row>

  </div>
</template>

<script>
import Pagination from '@/components/Pagination'
import myTable from '@/components/Table'
import searchBar from '@/components/SearchBar'
import { listATM } from '@/api/bankInfo/atm'
import { regionData, CodeToText } from 'element-china-area-data'
import { bankInfoClearTree } from '@/api/common/selectOption'

export default {
  filters: {
    formatText(code) {
      return CodeToText[code] || ''
    }
  },
  components: { Pagination, myTable, searchBar },
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
        list: '/base/bankInquiry/atmList'
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
    this.getTree()
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
      await bankInfoClearTree().then((res) => {
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
    handleNodeClick(val) {
      this.listQuery.bankId = val.id
      this.listQuery.page = 1
      this.getList()
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
</style>
