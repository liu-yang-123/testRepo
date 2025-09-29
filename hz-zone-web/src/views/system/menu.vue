<template>
  <div class="app-container">

    <div class="filter-container">
      <el-button class="filter-item" type="primary" icon="el-icon-refresh" @click="handleFilter">刷新</el-button>
      <el-button v-permission="['/base/menu/save']" class="filter-item" type="primary" icon="el-icon-edit" @click="handleCreate">新增</el-button>
    </div>
    <!-- 查询结果 -->
    <el-table
      v-loading="listLoading"
      element-loading-text="正在查询中。。。"
      :data="list"
      row-key="id"
      :header-cell-style="{'background-color':'#f5f5f5'}"
      border
      fit
      :tree-props="{children: 'children'}"
    >

      <el-table-column
        prop="name"
        label="名称"
      />
      <el-table-column
        align="center"
        prop="url"
        label="地址"
      />
      <el-table-column
        align="center"
        prop="sort"
        label="排序"
      />
      <!--操作 -->
      <el-table-column align="center" label="操作" width="200" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            v-permission="['/base/menu/update']"
            type="primary"
            size="mini"
            @click="handleUpdate(scope.row)"
          >编辑</el-button>
          <el-button
            v-permission="['/base/menu/delete']"
            type="danger"
            size="mini"
            @click="handleDelete(scope.row)"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加或修改对话框 -->
    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible" :close-on-click-modal="false">
      <el-form
        ref="dataForm"
        :rules="rules"
        status-icon
        label-position="right"
        :model="dataForm"
        label-width="130px"
        :hide-required-asterisk="false"
        style="width:400px;margin-left:50px;"
      >
        <el-form-item label="上级菜单" prop="pid">
          <treeSelect
            v-model="dataForm.pid"
            :normalizer="normalizer"
            :show-count="true"
            :options="treeOptions"
            placeholder="选择上级菜单"
          />
        </el-form-item>
        <el-form-item label="菜单名称" prop="name">
          <el-input v-model="dataForm.name" :maxlength="16" />
        </el-form-item>
        <el-form-item label="显示排序" prop="sort">
          <el-input-number v-model="dataForm.sort" :min="0" />
        </el-form-item>
        <!-- <el-form-item label="菜单类型" prop="menuType">
              <el-radio-group v-model="dataForm.menuType">
                <el-radio label="C">目录</el-radio>
                <el-radio label="F">按钮</el-radio>
              </el-radio-group>
        </el-form-item> -->
        <!-- <el-form-item v-if="dataForm.menuType == 'F'" label="地址" prop="url"> -->
        <el-form-item label="地址" prop="url">
          <el-input v-model="dataForm.url" :maxlength="64" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button v-if="dialogStatus=='create'" type="primary" @click="createData">确定</el-button>
        <el-button v-else type="primary" @click="updateData">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import { listMenu, createMenu, updateMenu, deleteMenu } from '@/api/system/menu'
import TreeSelect from '@riophae/vue-treeselect' // 引用下拉树组件
import '@riophae/vue-treeselect/dist/vue-treeselect.css'

export default {
  name: 'Menu',
  components: { TreeSelect },
  data() {
    return {
      treeOptions: [], 	// 下拉树数据
      listLoading: true,
      list: [],
      dialogFormVisible: false,
      dialogStatus: 'create',
      textMap: {
        update: '编辑',
        create: '创建'
      },
      dataForm: {
        id: '',
        sort: 0
        // menuType: 'C'
      },
      // 添加或编辑规则
      rules: {
        // pid: [
        //   { required: true, message: '请选择上级菜单', trigger: 'blur' }
        // ],
        name: [
          { required: true, message: '菜单名称不能为空', trigger: 'blur' }
        ]

      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.listLoading = true
      listMenu()
        .then(response => {
          this.list = response.data
          this.treeOptions = this.list
          // this.recursiveTree(this.treeOptions)
          this.listLoading = false
        })
        .catch(() => {
          this.list = []
          this.listLoading = false
        })
    },
    // 查询
    handleFilter() {
      this.getList()
    },
    // 添加
    handleCreate() {
      this.resetForm()
      this.dialogStatus = 'create'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    resetForm() {
      this.dataForm = {
        id: '',
        sort: 0
      }
    },
    // //递归树结构
    // recursiveTree(tree) {
    //   //格式化：清掉空的children，将title换成label
    //     tree.map(item => {
    //         item.label = item.name;
    //         if (item.children) {
    //             item.children.map(child => {
    //                 child.label = child.name;
    //             });
    //             this.recursiveTree(item.children);
    //         } else {
    //             delete item.children
    //         }
    //     })
    // },
    normalizer(node) {
      return {
        id: node.id,
        label: node.name,
        children: node.children
      }
    },
    createData() {
      this.$refs['dataForm'].validate(valid => {
        if (valid) {
          this.openLoading()
          createMenu(this.dataForm)
            .then(response => {
              this.getList()
              this.dialogFormVisible = false
              this.$notify.success({
                title: '成功',
                message: '添加成功'
              })
            }).finally(() => {
              this.loading.close()
            })
        }
      })
    },
    // 添加时等待滚动条
    openLoading() {
      this.loading = this.$loading({
        lock: true,
        text: '正在加载...请勿进行其它操作',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      })
    },
    handleUpdate(row) {
      this.dataForm = Object.assign({}, row)
      if (this.dataForm.pid === 0) {
        this.dataForm.pid = null
      }
      this.dialogStatus = 'update'
      this.dialogFormVisible = true
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
        this.openLoading()
        deleteMenu(row).then(response => {
          this.$notify.success({
            title: '成功',
            message: '删除成功'
          })
          this.getList()
        })
      }).finally(() => {
        this.loading.close()
      })
    },
    updateData() {
      this.$refs['dataForm'].validate(valid => {
        if (valid) {
          this.openLoading()
          updateMenu(this.dataForm)
            .then(() => {
              this.getList()
              this.dialogFormVisible = false
              this.$notify.success({
                title: '成功',
                message: '更新成功'
              })
            }).finally(() => {
              this.loading.close()
            })
        }
      })
    }
  }
}
</script>
