<template>
  <div class="app-container">
    <search-bar :search-list="searchList" :options="options" :list-query="listQuery" :role="role" @lookUp="getList" />
    <el-table
      v-loading="listLoading"
      element-loading-text="正在查询中。。。"
      :data="list"
      :header-cell-style="{'background-color':'#f5f5f5'}"
      border
      fit
    >
      <el-table-column align="center" label="流程名称" prop="eventName" />
      <el-table-column align="center" label="审批流程" prop="flowProcess" :formatter="formatProcess" />
      <el-table-column align="center" label="状态" prop="status" :formatter="formatStatus" />
      <el-table-column align="center" label="消息通知" prop="msgStatus" :formatter="formatMsgStatus" />
      <el-table-column align="center" label="更新时间" prop="updateTime" :formatter="formatDateTime" />
      <el-table-column
        align="center"
        label="操作"
        class-name="small-padding fixed-width"
        width="240"
      >
        <template slot-scope="scope">
          <el-button
            v-permission="['/base/workflow/update']"
            type="primary"
            size="mini"
            @click="handleUpdate(scope.row)"
          >编辑</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="listQuery.page"
      :limit.sync="listQuery.limit"
      @pagination="getList"
    />

    <el-dialog title="编辑" :visible.sync="dialogFormVisible" :close-on-click-modal="false">
      <el-form ref="dataForm" :rules="rules" :model="dataForm" status-icon label-position="left" label-width="100px" style="width: 600px; margin-left:50px;">
        <el-form-item label="流程名称" prop="eventName">
          <el-input v-model="dataForm.eventName" disabled />
        </el-form-item>
        <el-form-item label="审批流程" prop="nodeList">
          <el-table
            :data="nodeList"
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
                  @click="nodeList.push({})"
                />
              </template>
              <template slot-scope="scope">
                <el-button
                  plain
                  type="danger"
                  icon="el-icon-remove-outline"
                  @click="nodeList.splice(scope.$index, 1)"
                />
              </template>
            </el-table-column>
            <el-table-column align="center" label="节点名称">
              <template slot-scope="scope">
                <el-input v-model="scope.row.nodeName" :maxlength="64" />
              </template>
            </el-table-column>
            <el-table-column align="center" label="审批用户">
              <template slot-scope="scope">
                <el-select v-model="scope.row.userIds" multiple filterable placeholder="请先选择审核用户" style="width: 100%">
                  <el-option
                    v-for="item in userOption"
                    :key="item.id"
                    :label="item.name"
                    :value="item.id"
                  />
                </el-select>
              </template>
            </el-table-column>
          </el-table>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="dataForm.status">
            <el-radio :label="1">开启</el-radio>
            <el-radio :label="0">关闭</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="消息通知" prop="msgStatus">
          <el-radio-group v-model="dataForm.msgStatus">
            <el-radio :label="1">开启</el-radio>
            <el-radio :label="0">关闭</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="newData()">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import Pagination from '@/components/Pagination'
import searchBar from '@/components/SearchBar'
import { authOption } from '@/api/common/selectOption'
import { userOption } from '@/api/system/user'
import { listWorkflow, updateWorkflow } from '@/api/workflow/manage'
import { formatdate } from '@/utils/date'

export default {
  components: { Pagination, searchBar },
  data() {
    return {
      list: [],
      listQuery: {
        limit: 10,
        page: 1,
        departmentId: null,
        eventName: null
      },
      listLoading: true,
      total: 0,
      searchList: [
        { name: 'departmentId', label: '部门', type: 3, notClear: true },
        { name: 'eventName', label: '流程名称' }
      ],
      options: {
        departmentId: []
      },
      role: {
        list: '/base/workflow/list'
      },
      statusText: [
        {
          value: 1,
          class: 'success',
          text: '开启'
        },
        {
          value: 0,
          class: 'danger',
          text: '关闭'
        }
      ],
      nodeList: [],
      userOption: [],
      dialogFormVisible: false,
      dataForm: {
        eventName: '',
        status: 1,
        msgStatus: 1,
        nodeList: [],
        id: 0
      },
      rules: {
        ipAddress: [
          { required: true, message: 'ip地址不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  mounted() {
    authOption().then(res => {
      this.options.departmentId = res.data
      this.listQuery.departmentId = this.options.departmentId[0].id
      this.getList()
    })

    userOption().then(res => {
      this.userOption = res.data
    })
  },
  methods: {
    getList(data) {
      if (data) {
        for (const key in data) {
          this.listQuery[key] = data[key]
        }
      }
      listWorkflow(this.listQuery).then((res) => {
        const data = res.data
        this.list = data.list
        this.total = data.total
        this.listLoading = false
      })
    },
    handleUpdate(row) {
      this.nodeList = []
      this.dataForm = Object.assign({}, row)
      const workflowList = row.workflowList
      const nodeList = []
      for (let k = 0; k < workflowList.length; k++) {
        const userIds = workflowList[k].userIds
        const userIdsArr = userIds.split(',')
        const userIdsT = []
        for (let k = 0; k < userIdsArr.length; k++) {
          userIdsT.push(parseInt(userIdsArr[k]))
        }
        nodeList.push({
          id: workflowList[k].id,
          nodeName: workflowList[k].nodeName,
          sort: workflowList[k].sort,
          eventId: workflowList[k].eventId,
          userIds: userIdsT
        })
      }
      this.nodeList = nodeList
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
        for (const key in this.dataForm) {
          this.dataForm[key] = row[key]
        }
      })
    },
    newData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          this.listLoading = true
          this.nodeList.forEach(function(item, index) {
            item.id = item.id ? item.id : 0
            item.sort = index + 1
            item.userIds = item.userIds.join(',')
          })
          this.dataForm.workflowList = this.nodeList
          updateWorkflow(this.dataForm).then(() => {
            this.getList()
            this.dialogFormVisible = false
            this.$message.success({ title: '成功', message: '更新成功' })
          }).finally(() => {
            this.listLoading = false
          })
        }
      })
    },
    formatProcess(row) {
      const workflowList = row.workflowList
      const nodeNameArr = workflowList.map(function(item) { return item.nodeName })
      return nodeNameArr.length > 0 ? nodeNameArr.join('->') : '----'
    },
    formatStatus(row) {
      const [obj] = this.statusText.filter(item => item.value === row.status)
      return <el-tag type={obj.class }>{obj.text}</el-tag>
    },
    formatMsgStatus(row) {
      const [obj] = this.statusText.filter(item => item.value === row.msgStatus)
      return <el-tag type={obj.class }>{obj.text}</el-tag>
    },
    formatDateTime(row) {
      const timestamp = row.updateTime
      if (!timestamp || timestamp === 0) {
        return '-'
      }
      const date = new Date(timestamp)
      return formatdate(date, 'yyyy-MM-dd hh:mm:ss')
    }
  }
}
</script>

<style scoped lang="scss">
</style>
