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
            :data="tree"
            :props="defaultProps"
            node-key="id"
            highlight-current
            :filter-node-method="filterNode"
            @node-click="handleNodeClick"
          />
        </el-scrollbar>
      </el-col>
      <el-col :span="19">
        <el-card v-if="tree.length > 0" v-permission="['/base/bankInquiry/bankClearInfo']" class="box-card">
          <div slot="header" class="clearfix">
            <span>网点信息</span>
          </div>
          <el-form label-position="right" inline class="demo-table-expand-1" label-width="160px">
            <el-form-item label="机构名称">
              <span>{{ bank.fullName }}</span>
            </el-form-item>
            <el-form-item label="机构简称">
              <span>{{ bank.shortName }}</span>
            </el-form-item>
            <el-form-item label="所在地区">
              <el-tag v-if="bank.province" type="info">{{
                bank.province
              }}</el-tag>
              <el-tag v-if="bank.city" type="info">{{ bank.city }}</el-tag>
              <el-tag v-if="bank.district" type="info">{{
                bank.district
              }}</el-tag>
            </el-form-item>
            <el-form-item label="详细地址">
              <span>{{ bank.address }}</span>
            </el-form-item>
            <el-form-item label="联系人">
              <span>{{ bank.contact }}</span>
            </el-form-item>
            <el-form-item label="联系电话">
              <span>{{ bank.contactPhone }}</span>
            </el-form-item>
            <el-form-item label="线路编号">
              <span>{{ bank.routeNo }}</span>
            </el-form-item>
            <el-form-item label="营业时间及应急联系人">
              <span>{{ bank.workInfo }}</span>
            </el-form-item>
            <el-form-item label="机构状态">
              <span>{{ bank.statusT | formatterStatus }}</span>
            </el-form-item>
            <el-form-item label="备注">
              <span>{{ bank.comments }}</span>
            </el-form-item>
            <el-form-item label="创建时间">
              <span>{{ bank.createTime | formatDateTime }}</span>
            </el-form-item>
            <el-form-item label="更新时间">
              <span>{{ bank.updateTime | formatDateTime }}</span>
            </el-form-item>
          </el-form>
        </el-card>
        <el-empty v-else style="height:500px" description="暂无数据" />
      </el-col>
    </el-row>

  </div>
</template>

<script>
import {
  treeCleanBox,
  detailCleanBox
} from '@/api/bankInfo/cleanMachine'
import { formatdate } from '@/utils/date'
import { dictionaryData } from '@/api/system/dictionary'

export default {
  filters: {
    formatterStatus(status) {
      return (
        {
          0: '正常',
          1: '停用'
        }[status] || status
      )
    },
    formatDateTime(timestamp) {
      if (!timestamp || timestamp === 0) {
        return '-'
      }
      const date = new Date(timestamp)
      return formatdate(date, 'yyyy-MM-dd')
    }
  },
  data() {
    return {
      list: [],
      tree: [],
      filterText: '',
      defaultProps: {
        children: 'children',
        label: 'name'
      },
      unitDepts: [],
      bank: {},
      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '创建'
      },
      options: {
        parentIds: []
      },
      carryType: [
        { value: 0, text: '钞袋' },
        { value: 1, text: '钞盒' }
      ],
      dictionaryData,
      rules: {
        departmentId: [
          { required: true, message: '所属部门不能为空', trigger: 'blur' }
        ],
        fullName: [
          { required: true, message: '机构名称不能为空', trigger: 'blur' }
        ],
        shortName: [
          { required: true, message: '机构简称不能为空', trigger: 'blur' }
        ],
        carryType: [
          { required: true, message: '回笼方式不能为空', trigger: 'blur' }
        ],
        routeNo: [
          { required: true, message: '线路编号不能为空', trigger: 'blur' }
        ]
      },
      loading: null
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
    getTree() {
      treeCleanBox().then((res) => {
        const data = res.data
        this.tree = data
        if (this.tree.length > 0) {
          this.$nextTick(() => {
            this.$refs.listTree.setCurrentKey(this.tree[0].id)
          })
          this.handleNodeClick(this.tree[0])
        } else {
          this.list = []
        }
        this.loading.close()
      })
    },
    handleNodeClick(data) {
      detailCleanBox(data.id).then((res) => {
        this.bank = res.data
        this.bank.id = data.id
        this.bank.parentIds = data.pid
      })
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
    },
    filterNode(value, data) {
      if (!value) return true
      return data.name.indexOf(value) !== -1
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

.clearfix:before,
.clearfix:after {
  display: table;
  content: "";
}
.clearfix:after {
  clear: both;
}
.box-card {
  width: 80%;
}

.edit-item {
  float: right;
}

.demo-table-expand-1 {
  font-size: 0;
  ::v-deep label {
    width: 90px;
    color: #99a9bf;
  }
  ::v-deep .el-form-item {
    margin-right: 0;
    margin-bottom: 0;
    width: 100%;
  }
}

.el-form-item__content{
  span{
    margin-left: 20px;
  }
}
</style>
