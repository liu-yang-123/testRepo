<template>
  <div class="app-container">
    <el-tabs v-model="activeName" @tab-click="tabClick">
      <el-tab-pane label="市区限行" name="first">
        <search-bar :list-query="restrictListQuery" :options="restrictOptions" :search-list="restrictSearchList" :role="restrictRole" @lookUp="getRestrictList" @create="restrictCreate" />
        <my-table
          :list-loading="restrictlistLoading"
          :data-list="restrictList"
          :table-list="restrictTableList"
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
                  v-permission="['/base/driving/restrict/update']"
                  type="primary"
                  size="mini"
                  @click="restrictUpdate(scope.row)"
                >编辑</el-button>
                <el-button
                  v-permission="['/base/driving/restrict/delete']"
                  type="danger"
                  size="mini"
                  @click="restrictDelete(scope.row)"
                >删除</el-button>
              </template>
            </el-table-column>
          </template>
        </my-table>
        <pagination
          v-show="restrictTotal > 0"
          :total="restrictTotal"
          :page.sync="restrictListQuery.page"
          :limit.sync="restrictListQuery.limit"
          @pagination="getRestrictList"
        />
      </el-tab-pane>
      <el-tab-pane label="西湖景区限行" name="second" lazy>
        <search-bar :list-query="restrictXHListQuery" :options="restrictXHOptions" :search-list="restrictXHSearchList" :role="restrictXHRole" @lookUp="getRestrictXHList" @create="restrictXHCreate" />
        <my-table
          :list-loading="restrictXHListLoading"
          :data-list="restrictXHList"
          :table-list="restrictXHTableList"
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
                  v-permission="['/base/driving/restrictXh/update']"
                  type="primary"
                  size="mini"
                  @click="restrictXHUpdate(scope.row)"
                >编辑</el-button>
                <el-button
                  v-permission="['/base/driving/restrictXh/delete']"
                  type="danger"
                  size="mini"
                  @click="restrictXHDelete(scope.row)"
                >删除</el-button>
              </template>
            </el-table-column>
          </template>
        </my-table>
        <pagination
          v-show="restrictXHTotal > 0"
          :total="restrictXHTotal"
          :page.sync="restrictXHListQuery.page"
          :limit.sync="restrictXHListQuery.limit"
          @pagination="getRestrictXHList"
        />
      </el-tab-pane>
    </el-tabs>
    <!-- 尾号限行 添加编辑 -->
    <el-dialog :title="textMap[dialogStatus]" :visible.sync="restrictVisible" :close-on-click-modal="false">
      <el-form ref="restrictForm" :rules="restrictRules" :model="restrictForm" status-icon label-position="right" label-width="120px" style="width: 80%; margin-left:8%;">
        <el-form-item label="星期" prop="weekday">
          <el-select v-model="restrictForm.weekday" filterable placeholder="请选择" style="width: 40%">
            <el-option
              v-for="item in restrictOptions.weekday"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="早高峰起始时间" prop="beginTimeAm">
          <el-time-select
            v-model="restrictForm.beginTimeAm"
            style="width: 40%"
            placeholder="请选择"
            :picker-options="{
              start: '00:00',
              step: '00:30',
              end: '24:00',
              maxTime: restrictForm.endTimeAm
            }"
            default-value="09:00"
          />
        </el-form-item>
        <el-form-item label="早高峰结束时间" prop="endTimeAm">
          <el-time-select
            v-model="restrictForm.endTimeAm"
            style="width: 40%"
            placeholder="请选择"
            :picker-options="{
              start: '00:00',
              step: '00:30',
              end: '24:00',
              minTime: restrictForm.beginTimeAm
            }"
            :default-value="restrictForm.beginTimeAm ? restrictForm.beginTimeAm : '09:00' "
          />
        </el-form-item>
        <el-form-item label="晚高峰起始时间" prop="beginTimePm">
          <el-time-select
            v-model="restrictForm.beginTimePm"
            style="width: 40%"
            placeholder="请选择"
            :picker-options="{
              start: '00:00',
              step: '00:30',
              end: '24:00',
              maxTime: restrictForm.endTimePm
            }"
            default-value="09:00"
          />
        </el-form-item>
        <el-form-item label="晚高峰结束时间" prop="endTimePm">
          <el-time-select
            v-model="restrictForm.endTimePm"
            style="width: 40%"
            placeholder="请选择"
            :picker-options="{
              start: '00:00',
              step: '00:30',
              end: '24:00',
              minTime: restrictForm.beginTimePm
            }"
            :default-value="restrictForm.beginTimePm ? restrictForm.beginTimePm : '09:00' "
          />
        </el-form-item>
        <el-form-item label="限行尾号" prop="forbidNumberList">
          <el-select v-model="restrictForm.forbidNumberList" multiple filterable placeholder="请选择" style="width: 40%">
            <el-option
              v-for="item in restrictOptions.forbidNumberList"
              :key="item"
              :label="item"
              :value="item"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="restrictVisible = false">取消</el-button>
        <el-button type="primary" @click="newRestrict(dialogStatus)">确定</el-button>
      </div>
    </el-dialog>
    <!-- 西湖限行 添加编辑 -->
    <el-dialog :title="textMap[dialogStatus]" :visible.sync="restrictXHVisible" :close-on-click-modal="false">
      <el-form ref="restrictXhForm" :rules="restrictXHRules" :model="restrictXhForm" status-icon label-position="right" label-width="100px" style="width: 80%;margin-left:8% ">
        <el-form-item label="单双日" prop="dayType">
          <el-select v-model="restrictXhForm.dayType" filterable placeholder="请选择" style="width: 40%" :disabled="dialogStatus === 'update'">
            <el-option
              v-for="item in restrictXHOptions.dayType"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="起始时间" prop="beginTime">
          <el-time-select
            v-model="restrictXhForm.beginTime"
            style="width: 40%"
            placeholder="请选择"
            :picker-options="{
              start: '00:00',
              step: '00:30',
              end: '24:00',
              maxTime: restrictXhForm.endTime
            }"
            default-value="09:00"
          />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-time-select
            v-model="restrictXhForm.endTime"
            style="width: 40%"
            placeholder="请选择"
            :picker-options="{
              start: '00:00',
              step: '00:30',
              end: '24:00',
              minTime: restrictXhForm.beginTime
            }"
            :default-value="restrictXhForm.beginTime ? restrictXhForm.beginTime : '09:00' "
          />
        </el-form-item>
        <el-form-item label="允许尾号" prop="permitNumberList">
          <el-select v-model="restrictXhForm.permitNumberList" multiple filterable placeholder="请选择" style="width: 40%">
            <el-option
              v-for="item in restrictXHOptions.permitNumberList"
              :key="item"
              :label="item"
              :value="item"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="影响线路" prop="routeList">
          <el-select v-model="restrictXhForm.routeList" multiple filterable placeholder="请选择" style="width: 40%">
            <el-option
              v-for="item in restrictXHOptions.routeList"
              :key="item.id"
              :label="item.routeName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="restrictXHVisible = false">取消</el-button>
        <el-button type="primary" @click="newRestrictXH(dialogStatus)">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import searchBar from '@/components/SearchBar'
import Pagination from '@/components/Pagination'
import myTable from '@/components/Table'
import { formatdate } from '@/utils/date'
import { listRestrict, addRestrict, updateRestrict, deleteRestrict, listRestrictXh, addRestrictXh, updateRestrictXh, deleteRestrictXh } from '@/api/duty/restrictManage'
import { authOption, routeTemplateOption } from '@/api/common/selectOption'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
export default {
  components: { searchBar, myTable, Pagination },
  data() {
    return {
      activeName: 'first',
      oldName: 'first',
      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '创建'
      },
      departmentId: null,
      restrictSearchList: [
        { name: 'departmentId', label: '所属部门', type: 3, notClear: true }
      ],
      restrictRole: {
        list: '/base/driving/restrict/list',
        add: '/base/driving/restrict/save'
      },
      restrictOptions: {
        departmentId: [],
        weekday: [
          { id: 1, name: '星期一' },
          { id: 2, name: '星期二' },
          { id: 3, name: '星期三' },
          { id: 4, name: '星期四' },
          { id: 5, name: '星期五' },
          { id: 6, name: '星期六' },
          { id: 7, name: '星期天' }
        ],
        forbidNumberList: [1, 2, 3, 4, 5, 6, 7, 8, 9, 0]
      },
      restrictListQuery: {
        page: 1,
        limit: 10,
        departmentId: null
      },
      restrictTotal: 0,
      restrictlistLoading: false,
      restrictList: [],
      restrictTableList: [
        {
          label: '星期',
          prop: 'weekday',
          formatter: this.formatWeekday
        },
        {
          label: '限行尾号',
          prop: 'forbidNumberList',
          formatter: this.formatPermit
        },
        {
          label: '早高峰起始时间',
          prop: 'beginTimeAm'
        },
        {
          label: '早高峰结束时间',
          prop: 'endTimeAm'
        },
        {
          label: '晚高峰起始时间',
          prop: 'beginTimePm'
        },
        {
          label: '晚高峰结束时间',
          prop: 'endTimePm'
        }
      ],
      restrictVisible: false,
      restrictForm: {
        beginTimeAm: null,
        beginTimePm: null,
        departmentId: null,
        endTimeAm: null,
        endTimePm: null,
        forbidNumberList: null,
        id: null,
        weekday: null
      },
      restrictRules: {
        weekday: [{ required: true, message: '星期不能为空', trigger: 'blur' }],
        beginTimeAm: [{ required: true, message: '早高峰起始时间不能为空', trigger: 'blur' }],
        beginTimePm: [{ required: true, message: '早高峰结束时间不能为空', trigger: 'blur' }],
        endTimeAm: [{ required: true, message: '晚高峰起始时间不能为空', trigger: 'blur' }],
        endTimePm: [{ required: true, message: '晚高峰结束时间不能为空', trigger: 'blur' }],
        forbidNumberList: [{ required: true, message: '限行尾号不能为空', trigger: 'blur' }]
      },
      // 休息计划管理
      restrictXHOptions: {
        departmentId: [],
        weekday: [
          { id: 1, name: '星期一' },
          { id: 2, name: '星期二' },
          { id: 3, name: '星期三' },
          { id: 4, name: '星期四' },
          { id: 5, name: '星期五' },
          { id: 6, name: '星期六' },
          { id: 7, name: '星期天' }
        ],
        dayType: [
          { id: 0, name: '双日' },
          { id: 1, name: '单日' }
        ],
        permitNumberList: [1, 2, 3, 4, 5, 6, 7, 8, 9, 0],
        routeList: []
      },
      restrictXHSearchList: [
        { name: 'departmentId', label: '所属部门', type: 3, notClear: true }
      ],
      restrictXHRole: {
        list: '/base/driving/restrictXh/list',
        add: '/base/driving/restrictXh/save'
      },
      restrictXHListQuery: {
        page: 1,
        limit: 10,
        departmentId: null
      },
      loading: null,
      restrictXHListLoading: false,
      restrictXHList: [],
      restrictXHTableList: [
        {
          label: '单双日',
          prop: 'dayType',
          formatter: this.formatDayType
        },
        {
          label: '允许尾号',
          prop: 'permitNumberList',
          formatter: this.formatPermit
        },
        {
          label: '开始时间',
          prop: 'beginTime'
        },
        {
          label: '结束时间',
          prop: 'endTime'
        },
        {
          label: '影响线路',
          prop: 'routeList',
          formatter: this.formatRoute
        }
      ],
      restrictXHTotal: 0,
      restrictXHVisible: false,
      restrictXhForm: {
        dayType: null,
        beginTime: null,
        departmentId: null,
        endTime: null,
        id: null,
        permitNumberList: null,
        routeList: null
      },
      restrictXHRules: {
        dayType: [{ required: true, message: '单双日不能为空', trigger: 'blur' }],
        beginTime: [{ required: true, message: '起始时间不能为空', trigger: 'blur' }],
        endTime: [{ required: true, message: '结束时间不能为空', trigger: 'blur' }],
        permitNumberList: [{ required: true, message: '允许尾号不能为空', trigger: 'blur' }],
        routeList: [{ required: true, message: '影响线路不能为空', trigger: 'blur' }]
      }
    }
  },
  mounted() {
    this.openLoading()
    authOption().then(res => {
      this.restrictOptions.departmentId = res.data
      this.restrictXHOptions.departmentId = res.data
      this.restrictListQuery.departmentId = this.restrictOptions.departmentId[0].id
      this.getRestrictList()
    })
  },
  methods: {
    tabClick() {
      if (this.activeName !== this.oldName) {
        if (this.activeName === 'first') {
          this.restrictListQuery.departmentId = this.departmentId
          this.getRestrictList()
        } else {
          this.openLoading()
          this.restrictXHListQuery.departmentId = this.departmentId
          this.getRestrictXHList()
        }
        this.oldName = this.activeName
      }
    },
    getRestrictList(data) {
      if (data) {
        for (const key in data) {
          this.restrictListQuery[key] = data[key]
        }
      }
      this.restrictlistLoading = true
      listRestrict(this.restrictListQuery).then((res) => {
        this.restrictList = res.data.list
        this.departmentId = this.restrictListQuery.departmentId
        this.loading.close()
        this.restrictlistLoading = false
        this.restrictTotal = res.data.total
      })
    },
    restrictCreate() {
      this.dialogStatus = 'create'
      this.restrictVisible = true
      this.$nextTick(() => {
        this.$refs['restrictForm'].resetFields()
      })
    },
    restrictUpdate(row) {
      this.dialogStatus = 'update'
      this.restrictVisible = true
      this.$nextTick(() => {
        this.$refs['restrictForm'].clearValidate()
        for (const key in this.restrictForm) {
          this.restrictForm[key] = row[key]
        }
        this.restrictForm.date = [row.beginDate, row.endDate]
      })
    },
    restrictDelete(row) {
      this.$confirm('确定删除吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.restrictListLoading = true
        deleteRestrict(row.id)
          .then(() => {
            this.$notify.success({
              title: '成功',
              message: '删除成功'
            })
            const index = this.restrictList.indexOf(row)
            console.log(index)
            this.restrictList.splice(index, 1)
          }).finally(() => {
            this.restrictListLoading = false
          })
      })
    },
    newRestrict(type) {
      this.$refs['restrictForm'].validate((valid) => {
        if (valid) {
          this.restrictListLoading = true
          this.restrictForm.departmentId = this.departmentId
          if (type === 'update') {
            updateRestrict(this.restrictForm)
              .then(() => {
                this.getRestrictList()
                this.restrictVisible = false
                this.$notify.success({
                  title: '成功',
                  message: '更新成功'
                })
              })
              .finally(() => {
                this.restrictListLoading = false
              })
          } else {
            addRestrict(this.restrictForm)
              .then(() => {
                this.getRestrictList()
                this.restrictVisible = false
                this.$notify.success({
                  title: '成功',
                  message: '添加成功'
                })
              })
              .finally(() => {
                this.restrictListLoading = false
              })
          }
        }
      })
    },
    getRestrictXHList() {
      this.restrictXHListLoading = true
      listRestrictXh(this.restrictXHListQuery).then(res => {
        this.departmentId = this.restrictXHListQuery.departmentId
        routeTemplateOption(this.departmentId).then(val => {
          this.restrictXHOptions.routeList = val.data
          this.restrictXHList = res.data.list
          this.restrictXHListLoading = false
          this.restTotal = res.data.total
          this.loading.close()
        })
      })
    },
    restrictXHCreate() {
      this.dialogStatus = 'create'
      this.restrictXHVisible = true
      this.$nextTick(() => {
        this.$refs['restrictXhForm'].resetFields()
      })
    },
    newRestrictXH(type) {
      this.$refs['restrictXhForm'].validate((valid) => {
        if (valid) {
          this.restrictXHListLoading = true
          this.restrictXhForm.departmentId = this.departmentId
          if (type === 'update') {
            updateRestrictXh(this.restrictXhForm)
              .then(() => {
                this.getRestrictXHList()
                this.restrictXHVisible = false
                this.$notify.success({
                  title: '成功',
                  message: '更新成功'
                })
              })
              .finally(() => {
                this.restrictXHListLoading = false
              })
          } else {
            addRestrictXh(this.restrictXhForm)
              .then(() => {
                this.getRestrictXHList()
                this.restrictXHVisible = false
                this.$notify.success({
                  title: '成功',
                  message: '添加成功'
                })
              })
              .finally(() => {
                this.restrictXHListLoading = false
              })
          }
        }
      })
    },
    restrictXHUpdate(row) {
      this.restrictXHVisible = true
      this.dialogStatus = 'update'
      this.$nextTick(() => {
        this.$refs['restrictXhForm'].clearValidate()
        for (const key in this.restrictXhForm) {
          this.restrictXhForm[key] = row[key]
        }
      })
    },
    restrictXHDelete(row) {
      this.$confirm('确定删除吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.restrictXHListLoading = true
        deleteRestrictXh(row.id)
          .then(() => {
            this.$notify.success({
              title: '成功',
              message: '删除成功'
            })
            const index = this.restrictXHList.indexOf(row)
            this.restrictXHList.splice(index, 1)
          }).finally(() => {
            this.restrictXHListLoading = false
          })
      })
    },
    // 失去焦点初始化
    formatDate(timestamp) {
      if (!timestamp || timestamp === 0) {
        return '-'
      }
      const date = new Date(timestamp)
      return formatdate(date, 'yyyy-MM-dd')
    },
    formatWeekday(id) {
      return this.restrictOptions.weekday.filter(item => item.id === id)[0].name
    },
    formatDateTime(timestamp) {
      if (!timestamp || timestamp === 0) {
        return '-'
      }
      const date = new Date(timestamp)
      return formatdate(date, 'hh:mm:ss')
    },
    formatDayType(type) {
      switch (type) {
        case 0:
          return '双日'
        case 1:
          return '单日'
      }
    },
    formatRoute(arr) {
      if (arr.length > 0) {
        let str = ''
        for (const item of arr) {
          const route = this.restrictXHOptions.routeList.filter(elm => elm.id === item)[0]
          console.log(route)
          if (route !== undefined) {
            str += `<span class="el-tag el-tag--medium el-tag--light" style="margin-left:6px">${route.routeName}</span>`
          }
        }
        return str
      }
    },
    formatPermit(arr) {
      if (arr.length > 0) {
        let str = ''
        const sortArr = arr.slice().sort()
        for (const item of sortArr) {
          str += `<span class="el-tag el-tag--medium el-tag--light" style="margin-left:6px">${item}</span>`
        }
        return str
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
  .filter-container * {
    margin-left: 10px;
  }

  .scrollbar {
  ::v-deep .el-scrollbar__wrap {
    overflow: auto;
  }
}
</style>
