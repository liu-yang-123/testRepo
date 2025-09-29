<template>
  <div class="app-container">
    <el-row :gutter="20">
      <div class="filter-container">
        <el-select
          v-model="listQuery.departmentId"
          class="filter-item"
          style="width: 200px"
          placeholder="请选择部门"
          @change="getTree()"
        >
          <el-option
            v-for="item in unitDepts"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
        <el-date-picker
          v-model="listQuery.routeDate"
          v-permission="['/base/route/option']"
          value-format="timestamp"
          type="date"
          placeholder="选择日期"
          :clearable="false"
          class="filter-item"
          @change="getTree()"
        />
        <el-button
          class="filter-item"
          type="primary"
          icon="el-icon-refresh"
          @click="getTree"
        >刷新</el-button>
      </div>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="4">
        <el-scrollbar style="height: 500px" class="scrollbar">
          <el-tree
            ref="listTree"
            :data="tree"
            node-key="value"
            highlight-current
            @node-click="handleNodeClick"
          >
            <span slot-scope="{ data }" class="custom-tree-node">
              <span style="width: 30%">{{ data.routeNo }}</span>
              <span>{{ data.routeName }}</span>
            </span>
          </el-tree>
        </el-scrollbar>
      </el-col>
      <el-col :span="20">
        <div v-if="tree.length > 0">
          <my-table
            :list-loading="listLoading"
            :data-list="list"
            :table-list="tableList"
          >
            <template v-slot:operate>
              <el-table-column
                header-align="center"
                align="left"
                label="操作"
                class-name="small-padding fixed-width"
                width="80"
                fixed="right"
              >
                <template slot-scope="scope">
                  <el-button
                    v-permission="['/base/atmTask/info']"
                    type="primary"
                    size="mini"
                    @click="handleDetail(scope.row)"
                  >详情</el-button>
                </template>
              </el-table-column>
            </template>
          </my-table>
        </div>
        <el-empty v-else style="height:500px" description="暂无数据" />
      </el-col>
    </el-row>

    <el-dialog title="详情" :visible.sync="dialogFormVisible">
      <div style="padding-left:6%">
        <div class="title">加钞间</div>
        <el-form label-position="right" inline class="confirm-info">
          <el-form-item label="门窗、锁、门禁是否正常">
            <i :class="detailForm.roomDoor ? 'el-icon-check' : 'el-icon-close'" />
          </el-form-item>
          <el-form-item label="消防设备是否完好">
            <i :class="detailForm.roomFireEqt ? 'el-icon-check' : 'el-icon-close'" />
          </el-form-item>
          <el-form-item label="空调灯具是否正常">
            <i :class="detailForm.roomlight ? 'el-icon-check' : 'el-icon-close'" />
          </el-form-item>
        </el-form>
        <div class="title">取款大厅</div>
        <el-form label-position="right" inline class="confirm-info">
          <el-form-item label="防护舱门、锁、插销是否完好">
            <i :class="detailForm.hallLock ? 'el-icon-check' : 'el-icon-close'" />
          </el-form-item>
          <el-form-item label="自助银行大门是否完好">
            <i :class="detailForm.hallDoor ? 'el-icon-check' : 'el-icon-close'" />
          </el-form-item>
          <!-- <el-form-item label="报警按钮、对讲是否正常">
            <i :class="detailForm.hallSpeaker ? 'el-icon-check' : 'el-icon-close'" />
          </el-form-item> -->
          <el-form-item label="环境是否整洁">
            <i :class="detailForm.hallEmv ? 'el-icon-check' : 'el-icon-close'" />
          </el-form-item>
        </el-form>
      </div>

      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listInspect
} from '@/api/clean/inspect'
import myTable from '@/components/Table'
import { formatdate } from '@/utils/date'
import { authOption, routeOption } from '@/api/common/selectOption'
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
    }
  },
  components: { myTable },
  data() {
    return {
      tree: [],
      listQuery: {
        routeDate: new Date(new Date().toLocaleDateString()).getTime(),
        departmentId: null
      },
      unitDeptId: null,
      unitDepts: [],
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '创建'
      },
      listLoading: false,
      list: [],
      tableList: [
        {
          label: '银行名称',
          prop: 'bankName'
        },
        {
          label: '银行机构名称',
          prop: 'subBankName'
        },
        {
          label: '巡检时间',
          prop: 'checkTime',
          formatter: this.formatDateTime
        },
        {
          label: '撤防时间',
          prop: 'revokeAlarmTime',
          formatter: this.formatDateTime
        },
        {
          label: '布防时间',
          prop: 'setAlarmTime',
          formatter: this.formatDateTime
        },
        {
          label: '巡检备注',
          prop: 'comments'
        },
        {
          label: '巡检结果',
          prop: 'checkNormal',
          formatter: this.formatResult
        }
      ],
      dataForm: {
        departmentId: null,
        parentIds: null,
        fullName: null,
        id: null,
        shortName: null,
        location: null,
        address: null,
        contact: null,
        contactPhone: null,
        comments: null
      },
      options: {
        parentIds: []
      },
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
        ]
      },
      loading: null,
      detailForm: {}
    }
  },
  mounted() {
    this.openLoading()
    this.getOptions()
  },
  methods: {
    formatDateTime(timestamp) {
      if (!timestamp || timestamp === 0) {
        return '-'
      }
      const date = new Date(timestamp)
      return formatdate(date, 'hh:mm:ss')
    },
    formatResult(status) {
      if (status === 0) {
        return '<span>正常</span>'
      } else {
        return '<span style="color: red">异常</span>'
      }
    },
    getTree() {
      routeOption(this.listQuery).then((res) => {
        this.tree = res.data
        if (this.tree.length > 0) {
          this.$nextTick(() => {
            this.$refs.listTree.setCurrentKey(this.tree[0].value)
          })
          this.handleNodeClick(this.tree[0])
        } else {
          this.list = []
        }
        this.loading.close()
      })
    },
    getOptions() {
      authOption().then(res => {
        this.unitDepts = res.data
        this.options.departmentId = res.data
        this.listQuery.departmentId = this.unitDepts[0].id
        this.getTree()
      })
    },
    handleNodeClick(data) {
      this.listLoading = true
      listInspect(data.value).then((res) => {
        this.list = res.data
        this.listLoading = false
      })
    },
    handleDetail(row) {
      this.dialogFormVisible = true
      this.detailForm = { ...row.hallCheckResults, ...row.roomCheckResults }
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

.custom-tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  font-size: 14px;
  padding-right: 8px;
}

.title {
  font-size: 16px;
  font-weight: 600;
  margin: 20px 0;
}

.confirm-info {
  i{
    font-size: 20px;
    vertical-align: middle;
    font-weight: 600;
  }
  .el-icon-check {
    color: rgb(82, 240, 82);
  }
  .el-icon-close {
    color: rgb(248, 51, 51);
  }
}

.el-form--inline .el-form-item {
  margin-right: 0;
  margin-bottom: 8px;
  width: 50%;
  ::v-deep .el-form-item__label {
    width: 50%;
    color: #99a9bf;
    margin-right: 24px;
  }
}
</style>
