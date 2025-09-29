<template>
  <div class="app-container">
    <el-row :gutter="20">
      <div class="filter-container">
        <el-select
          v-model="unitDeptId"
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
        <el-button
          class="filter-item"
          type="primary"
          icon="el-icon-refresh"
          @click="getTree"
        >刷新</el-button>
        <el-button
          v-permission="['/base/bankBox/save']"
          class="filter-item"
          type="primary"
          icon="el-icon-edit"
          @click="handleCreate"
        >添加</el-button>
      </div>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="4">
        <el-scrollbar style="height: 500px" class="scrollbar">
          <el-tree
            ref="listTree"
            :data="tree"
            :props="defaultProps"
            node-key="id"
            highlight-current
            @node-click="handleNodeClick"
          />
        </el-scrollbar>
      </el-col>
      <el-col :span="20">
        <el-card v-if="tree.length > 0" v-permission="['/base/bankBox/info']" class="box-card">
          <div slot="header" class="clearfix">
            <span>网点信息</span>
            <span class="edit-item">
              <el-button
                v-permission="['/base/bankBox/update']"
                type="primary"
                size="mini"
                plain
                icon="el-icon-edit"
                @click="handleUpdate(bank)"
              >编辑</el-button>
              <el-button
                v-if="bank.statusT === 0"
                v-permission="['/base/bankBox/stop']"
                type="danger"
                size="mini"
                plain
                icon="el-icon-circle-close"
                @click="handleToggle(bank)"
              >停用</el-button>
              <el-button
                v-else
                v-permission="['/base/bankBox/enable']"
                type="success"
                size="mini"
                plain
                icon="el-icon-circle-close"
                @click="handleToggle(bank)"
              >启用</el-button>
            </span>
          </div>
          <el-form label-position="left" inline class="demo-table-expand-1">
            <el-form-item label="机构名称">
              <span>{{ bank.fullName }}</span>
            </el-form-item>
            <el-form-item label="机构简称">
              <span>{{ bank.shortName }}</span>
            </el-form-item>
            <el-form-item label="网点类型">
              <span>{{ options.bankLevel.find(item => item.id === bank.bankLevel).name }}</span>
            </el-form-item>
            <el-form-item label="机构种类">
              <span>{{ options.bankCategory.find(item => item.id === bank.bankCategory).name }}</span>
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

    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible">
      <el-form
        ref="dataForm"
        :rules="rules"
        status-icon
        label-position="right"
        :model="dataForm"
        label-width="130px"
        :hide-required-asterisk="false"
        style="width: 80%;margin-left:8%"
      >
        <el-form-item label="所属部门" prop="departmentId">
          <el-select v-model="dataForm.departmentId" filterable placeholder="请选择" style="width: 40%" @change="depChange">
            <el-option
              v-for="item in options.departmentId"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="上级银行/网点" prop="parentIds">
          <treeSelect
            v-model="dataForm.parentIds"
            :disabled="dataForm.departmentId == null"
            :normalizer="normalizer"
            :show-count="true"
            :options="options.parentIds"
            placeholder="顶级银行"
            style="width:40%"
          />
        </el-form-item>
        <el-form-item label="机构名称" prop="fullName">
          <el-input v-model="dataForm.fullName" :maxlength="32" style="width:40%" />
        </el-form-item>
        <el-form-item label="机构简称" prop="shortName">
          <el-input v-model="dataForm.shortName" :maxlength="32" style="width:40%" />
        </el-form-item>
        <el-form-item label="网点类型" prop="bankLevel">
          <el-select v-model="dataForm.bankLevel" filterable placeholder="请选择" style="width: 40%">
            <el-option
              v-for="item in options.bankLevel"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="机构种类" prop="bankCategory">
          <el-select v-model="dataForm.bankCategory" filterable placeholder="请选择" style="width: 40%">
            <el-option
              v-for="item in options.bankCategory"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="所在地区" prop="location">
          <el-cascader
            v-model="dataForm.location"
            style="width: 40%"
            placeholder="省市区"
            :options="regionData"
          />
        </el-form-item>
        <el-form-item label="详细地址" prop="address">
          <el-input v-model="dataForm.address" :maxlength="32" style="width:40%" />
        </el-form-item>
        <el-form-item label="联系人" prop="contact">
          <el-input v-model="dataForm.contact" :maxlength="32" style="width:40%" />
        </el-form-item>
        <el-form-item label="联系人电话" prop="contactPhone">
          <el-input
            v-model="dataForm.contactPhone"
            :maxlength="32"
            oninput="value=value.replace(/[^-+\d]/g,'')"
            style="width:40%"
          />
        </el-form-item>
        <el-form-item label="备注" prop="comments">
          <el-input v-model="dataForm.comments" :maxlength="32" style="width:40%" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button
          type="primary"
          @click="newData(dialogStatus)"
        >确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  addTailBox,
  updateTailBox,
  stopTailBox,
  enableTailBox,
  detailTailBox,
  treeTailBox
} from '@/api/base/tailBox'
import { formatdate } from '@/utils/date'
import { regionData, CodeToText, TextToCode } from 'element-china-area-data'
import { authOption, bankTree } from '@/api/common/selectOption'
import { dictionaryData } from '@/api/system/dictionary'
import TreeSelect from '@riophae/vue-treeselect'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'

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
  components: { TreeSelect },
  data() {
    return {
      list: [],
      tree: [],
      defaultProps: {
        children: 'children',
        label: 'name'
      },
      unitDeptId: null,
      unitDepts: [],
      bank: {},
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '创建'
      },
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
        comments: null,
        bankCategory: null,
        bankLevel: null
      },
      options: {
        parentIds: [],
        bankLevel: [
          { id: 1, name: '总行' },
          { id: 2, name: '分行' },
          { id: 3, name: '支行' },
          { id: 4, name: '网点' },
          { id: 5, name: '库房' }
        ],
        bankCategory: [
          { id: 1, name: '营业机构' },
          { id: 2, name: '非营业机构' },
          { id: 3, name: '库房' }
        ]
      },
      regionData,
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
        bankLevel: [
          { required: true, message: '网点类型不能为空', trigger: 'blur' }
        ],
        bankCategory: [
          { required: true, message: '机构种类不能为空', trigger: 'blur' }
        ]
      },
      loading: null
    }
  },
  mounted() {
    this.openLoading()
    this.getOptions()
  },
  methods: {
    getTree() {
      treeTailBox(this.unitDeptId).then((res) => {
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
    getOptions() {
      authOption().then(res => {
        this.unitDepts = res.data
        this.options.departmentId = res.data
        this.unitDeptId = this.unitDepts[0].id
        this.getTree()
      })
    },
    handleNodeClick(data) {
      detailTailBox(data.id).then((res) => {
        this.bank = res.data
        this.bank.id = data.id
        this.bank.parentIds = data.pid
      })
    },
    handleCreate() {
      this.dialogStatus = 'create'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].resetFields()
        this.dataForm.departmentId = this.unitDeptId
        bankTree(this.dataForm.departmentId).then(res => {
          this.options.parentIds = res.data
        })
      })
    },
    handleUpdate(bank) {
      this.dataForm = Object.assign(this.convertLocationToCode(bank), bank)
      this.dataForm.parentIds = this.dataForm.parentIds === '0' ? null : this.dataForm.parentIds
      this.dialogStatus = 'update'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
        bankTree(this.dataForm.departmentId).then(res => {
          this.options.parentIds = res.data
        })
      })
    },
    handleToggle(row) {
      let str
      if (row.statusT === 0) {
        str = '停用'
      } else {
        str = '启用'
      }
      this.$confirm(`确定${str}吗?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.openLoading()
        if (row.statusT === 0) {
          stopTailBox(row.id)
            .then(() => {
              this.$notify.success({
                title: '成功',
                message: '停用成功'
              })
              this.getTree()
            })
            .finally(() => {
              this.loading.close()
            })
        } else {
          enableTailBox(row.id).then(() => {
            this.$notify.success({
              title: '成功',
              message: '启用成功'
            })
            this.getTree()
          })
            .finally(() => {
              this.loading.close()
            })
        }
      })
    },
    newData(type) {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          this.openLoading()
          console.log(this.dataForm.parentIds)
          if (this.dataForm.parentIds === null || this.dataForm.parentIds === undefined) {
            this.dataForm.parentIds = 0
          }
          if (type === 'update') {
            updateTailBox(this.convertLocationToText(this.dataForm))
              .then(() => {
                this.getTree()
                this.dialogFormVisible = false
                this.$notify.success({
                  title: '成功',
                  message: '更新成功'
                })
              })
              .finally(() => {
                this.loading.close()
              })
          } else {
            addTailBox(this.convertLocationToText(this.dataForm))
              .then(() => {
                this.getTree()
                this.dialogFormVisible = false
                this.$notify.success({
                  title: '成功',
                  message: '添加成功'
                })
              })
              .finally(() => {
                this.loading.close()
              })
          }
        }
      })
    },
    normalizer(node) {
      return {
        id: node.id,
        label: node.name,
        children: node.children
      }
    },
    // 数字转文字
    convertLocationToText(dataForm) {
      // 转换省市县
      const ad = dataForm.location
      if (ad && ad.length > 0) {
        dataForm.province = CodeToText[ad[0]] || ''
        dataForm.city = CodeToText[ad[1]] || ''
        dataForm.district = CodeToText[ad[2]] || ''
      } else {
        dataForm.province = ''
        dataForm.city = ''
        dataForm.district = ''
      }
      return dataForm
    },
    // 文字转数字
    convertLocationToCode(row) {
      const defaultLocation = { location: [] }
      if (!row) {
        return defaultLocation
      }
      // 转换省市县
      const province = row.province ? TextToCode[row.province] : null
      if (!province) return defaultLocation
      const city = row.city ? TextToCode[row.province][row.city] : null
      if (!city) return defaultLocation
      const district = row.district ? TextToCode[row.province][row.city][row.district] : null
      if (!district) return defaultLocation
      const location = [province.code, city.code, district.code]
      return { location }
    },
    openLoading() {
      this.loading = this.$loading({
        lock: true,
        text: '正在加载...请勿进行其它操作',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      })
    },
    depChange(value) {
      if (value != null) {
        bankTree(value).then(res => {
          this.options.parentIds = res.data
          this.dataForm.parentIds = null
        })
      }
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
</style>
