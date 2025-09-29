<template>
  <div class="app-container">
    <div class="filter-container">
      <el-select
        v-model="listQuery.departmentId"
        placeholder="请选择所属部门"
        class="filter-item"
        @change="updateRoute"
      >
        <el-option
          v-for="item in authOption"
          :key="item.id"
          :label="item.name"
          :value="item.id"
        />
      </el-select>
      <el-date-picker
        v-model="listQuery.planDay"
        value-format="timestamp"
        type="date"
        placeholder="选择日期"
        class="filter-item"
      />
      <el-select
        v-model="listQuery.routeNo"
        filterable
        clearable
        placeholder="请选择线路"
        class="filter-item"
      >
        <el-option
          v-for="item in routeOption"
          :key="item.routeNo"
          :label="item.routeName + '/' + item.routeNo"
          :value="item.routeNo"
        />
      </el-select>
      <el-select
        v-model="listQuery.empId"
        filterable
        clearable
        placeholder="请选择员工"
        class="filter-item"
      >
        <el-option-group
          v-for="group in empOption"
          :key="group.label"
          :label="group.label"
        >
          <el-option
            v-for="item in group.options"
            :key="item.id"
            :label="item.empName + '/' + item.empNo"
            :value="item.id"
          />
        </el-option-group>
      </el-select>
      <el-button
        v-permission="['/base/route/list']"
        class="filter-item"
        type="primary"
        icon="el-icon-search"
        @click="getList()"
      >查找</el-button>
      <el-button
        v-permission="['/base/schdResult/save']"
        class="filter-item"
        type="warning"
        icon="el-icon-postcard"
        @click="handleCreate"
      >发起排班</el-button>
      <el-button
        v-permission="['/base/schdResult/importSchd']"
        class="filter-item"
        type="success"
        icon="el-icon-upload"
        @click="handleImport"
      >排班导入</el-button>
      <el-button
        v-permission="['/base/schdResult/record']"
        class="filter-item"
        type="info"
        icon="el-icon-document"
        @click="handleRecord"
      >排班记录</el-button>
      <el-button
        v-permission="['/base/schdResult/download']"
        class="filter-item"
        type="success"
        icon="el-icon-download"
        @click="handleDownload"
      >排班下载</el-button>
      <el-button
        v-permission="['/base/schdResult/export']"
        class="filter-item"
        type="success"
        icon="el-icon-download"
        @click="handleBankExport"
      >银行导出</el-button>
      <el-button
        v-permission="['/base/schdResult/change']"
        :disabled="batchList.length !== 2"
        class="filter-item"
        type="success"
        icon="el-icon-sort"
        @click="handleReplace"
      >线路调换</el-button>
    </div>
    <my-table
      :list-loading="listLoading"
      :data-list="list"
      :table-list="resultTableList"
      :selection="true"
      :select-length="2"
      @selectionChange="selectionChange"
      @rowClass="tableClass"
    >
      <template v-slot:operate>
        <el-table-column
          align="center"
          label="操作"
          class-name="small-padding fixed-width"
          width="120"
        >
          <template slot-scope="scope">
            <el-button
              v-if="getEditStatus(scope.row)"
              v-permission="['/base/schdResult/update']"
              type="primary"
              size="mini"
              @click="handleUpdate(scope.row)"
            >手动调班</el-button>
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
    <!-- 发起排班 -->
    <el-dialog
      title="发起排班"
      :visible.sync="createVisible"
      :close-on-click-modal="false"
    >
      <el-form
        ref="createForm"
        :rules="createRules"
        :model="createForm"
        status-icon
        label-position="right"
        label-width="100px"
        style="width: 300px; margin-left: 50px"
      >
        <el-form-item label="排班日期" prop="planDay">
          <el-date-picker
            v-model="createForm.planDay"
            :picker-options="pickerOptions"
            value-format="timestamp"
            type="date"
            placeholder="选择日期"
            class="filter-item"
            @change="checkTime"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="createVisible = false">取消</el-button>
        <el-button type="primary" @click="newResult">确定</el-button>
      </div>
    </el-dialog>

    <!-- 手动调班-->
    <el-dialog
      title="手动调班"
      :visible.sync="updateVisible"
      :close-on-click-modal="false"
    >
      <el-form
        ref="updateForm"
        :rules="updateRules"
        :model="updateForm"
        status-icon
        label-position="right"
        label-width="120px"
      >
        <el-form-item label="任务日期">
          <span>{{ formatDate(updateForm.planDay) }}</span>
        </el-form-item>
        <el-form-item label="线路">
          <span v-text="updateForm.routeNo">XX号线</span>
        </el-form-item>
        <el-form-item label="车辆" prop="vehicleNo">
          <el-select
            v-model="updateForm.vehicleNo"
            filterable
            placeholder="请选择车辆"
            style="width: 60%"
          >
            <el-option
              v-for="item in vehicleOption"
              :key="item.lpno"
              :label="item.lpno"
              :value="item.lpno"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="司机" prop="driver">
          <el-select
            v-model="updateForm.driver"
            filterable
            placeholder="请选择司机"
            style="width: 60%"
          >
            <el-option
              v-for="item in driverOption2"
              :key="item.id"
              :label="item.empName + '/' + item.empNo"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item v-if="updateForm.routeType !== 1" label="护卫" prop="securitys">
          <el-select
            v-model="updateForm.securitys"
            filterable
            multiple
            :multiple-limit="2"
            placeholder="请选择护卫(两名)"
            style="width: 60%"
          >
            <el-option
              v-for="item in securityOption"
              :key="item.id"
              :label="item.empName + '/' + item.empNo"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-radio-group v-if="updateForm.routeType !== 1" v-model="updateForm.leader" size="small" fill="#66b1ff">
          <el-form-item label="钥匙员" prop="keyMan">
            <el-select
              v-model="updateForm.keyMan"
              filterable
              placeholder="请选择钥匙员"
              style="width: 70%"
              @change="changeKeyMan"
            >
              <el-option
                v-for="item in keyOption"
                :key="item.id"
                :label="
                  item.empName + '/' + item.empNo
                "
                :value="item.id"
              />
            </el-select>
            <el-radio
              :label="updateForm.keyMan"
              :disabled="keyRadioDisable"
              style="margin-left: 20px"
            >车长</el-radio>
          </el-form-item>
          <el-form-item label="密码员" prop="opMan">
            <el-select
              v-model="updateForm.opMan"
              filterable
              placeholder="请选择密码员"
              style="width: 70%"
              @change="changeOpMan"
            >
              <el-option
                v-for="item in opOption"
                :key="item.id"
                :label="
                  item.empName + '/' + item.empNo
                "
                :value="item.id"
              />
            </el-select>
            <el-radio
              :label="updateForm.opMan"
              :disabled="opRadioDisable"
              style="margin-left: 20px"
            >车长</el-radio>
          </el-form-item>
        </el-radio-group>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="updateVisible = false">取消</el-button>
        <el-button type="primary" @click="updateResult()">确定</el-button>
      </div>
    </el-dialog>

    <!-- 人员替换 -->
    <el-dialog
      title="人员替换"
      :visible.sync="replaceVisible"
      :close-on-click-modal="false"
      width="70%"
    >
      <div>
        <div class="route-title">{{ replaceList1.length > 0 && replaceList1[0].routeNo }}号线</div>
        <my-table
          :list-loading="batchListLoading"
          :data-list="replaceList1"
          :table-list="replaceTableList"
        >
          <template v-slot:operate>
            <el-table-column
              align="center"
              label="车长"
              class-name="small-padding fixed-width"
              width="300"
            >
              <template slot-scope="scope">
                <el-radio-group v-model="scope.row.leader">
                  <el-radio :disabled="routeLeader[scope.row.keyMan] === 0" :label="scope.row.keyMan">{{ scope.row.keyManName }}</el-radio>
                  <el-radio :disabled="routeLeader[scope.row.opMan] === 0" :label="scope.row.opMan">{{ scope.row.opManName }}</el-radio>
                </el-radio-group>
              </template>
            </el-table-column>
          </template>
        </my-table>
        <div>
          <div v-for="item in replaceBtn" :key="item" class="replace-button">
            <el-button
              type="text"
              icon="el-icon-sort"
              @click="replaceStaff(replaceTableList[item - 1].prop)"
            />
          </div>
        </div>
        <div class="route-title">{{ replaceList2.length > 0 && replaceList2[0].routeNo }}号线</div>
        <my-table
          :list-loading="batchListLoading"
          :data-list="replaceList2"
          :table-list="replaceTableList"
        >
          <template v-slot:operate>
            <el-table-column
              align="center"
              label="车长"
              class-name="small-padding fixed-width"
              width="300"
            >
              <template slot-scope="scope">
                <el-radio-group v-model="scope.row.leader">
                  <el-radio :disabled="routeLeader[scope.row.keyMan] === 0" :label="scope.row.keyMan">{{ scope.row.keyManName }}</el-radio>
                  <el-radio :disabled="routeLeader[scope.row.opMan] === 0" :label="scope.row.opMan">{{ scope.row.opManName }}</el-radio>
                </el-radio-group>
              </template>
            </el-table-column>
          </template>
        </my-table>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="replaceVisible = false">取消</el-button>
        <el-button type="primary" @click="replaceResult()">确定</el-button>
      </div>
    </el-dialog>

    <!-- 排班记录 --->
    <el-dialog
      title="排班记录"
      :visible.sync="recordVisible"
      :close-on-click-modal="false"
      width="65%"
    >
      <div>
        <my-table
          :list-loading="listLoading"
          :data-list="recordList"
          :table-list="recordTableList"
        />
        <pagination
          v-show="recordTotal > 0"
          :total="recordTotal"
          :page.sync="recordListQuery.page"
          :limit.sync="recordListQuery.limit"
          @pagination="getRecordList"
        />
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="recordVisible = false">关闭</el-button>
      </div>
    </el-dialog>

    <!-- 上传对话框 -->
    <el-dialog
      title="上传文件"
      :visible.sync="importVisible"
      :close-on-click-modal="false"
      width="60%"
    >
      <el-form
        ref="uploadDataForm"
        :rules="uploadRules"
        :model="uploadDataForm"
        status-icon
        label-position="right"
        label-width="100px"
        style="width: 80%; margin-left: 50px"
      >
        <el-form-item label="权限部门" prop="departmentId">
          <el-select
            v-model="uploadDataForm.departmentId"
            placeholder="请选择所属部门"
            style="width: 40%"
          >
            <el-option
              v-for="item in authOption"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="日期" prop="planDay">
          <el-date-picker
            v-model="uploadDataForm.planDay"
            value-format="timestamp"
            type="date"
            placeholder="请选择日期"
            style="width: 40%"
          />
        </el-form-item>
        <el-form-item label="上传文件" prop="file">
          <el-upload
            ref="upload"
            :headers="headers"
            :data="paramsData"
            :on-remove="exportRemove"
            :on-error="exportError"
            :on-success="exportSuccess"
            :on-change="exportChange"
            class="upload-demo"
            :action="importPath"
            accept=".xlsx,.xls"
            :before-upload="beforeUpload"
            :file-list="uploadDataForm.file"
            :auto-upload="false"
            style="width: 40%"
          >
            <el-button
              slot="trigger"
              size="small"
              type="primary"
            >选取文件</el-button>
            <div slot="tip" class="el-upload__tip">只能上传xlsx/xls文件</div>
          </el-upload>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="importVisible = false">取消</el-button>
        <el-button type="primary" @click="importData()">导入</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import Pagination from '@/components/Pagination'
import myTable from '@/components/Table'
import { formatdate } from '@/utils/date'
import {
  authOption,
  jobNameOption,
  routeTemplateOption,
  vehicleOption
} from '@/api/common/selectOption'
import {
  listResult,
  deleteResult,
  addResult,
  updateResult,
  resultDownload,
  changeResult,
  getLeader, getRecord, checkTime, resultExport, importPath
} from '@/api/duty/schedulResult'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
import { getToken } from '@/utils/auth'
export default {
  components: { myTable, Pagination },
  data() {
    const checkRestList = (rule, value, callback) => {
      if (value.length === 2) {
        callback()
      } else {
        callback(new Error('请添加护卫'))
      }
    }
    return {
      listQuery: {
        limit: 30,
        page: 1,
        departmentId: null,
        routeNo: null,
        empId: null,
        planDay: new Date(new Date().toLocaleDateString()).getTime()
      },
      loading: null,
      batchList: [],
      authOption: [],
      routeOption: [],
      empOption: [],
      departmentId: null,
      total: 0,
      listLoading: false,
      list: [],
      resultTableList: [
        {
          label: '任务日期',
          prop: 'planDay',
          formatter: this.formatDate
        },
        {
          label: '线路',
          prop: 'routeNo',
          formatter: this.formatRouteNo
        },
        {
          label: '车辆',
          prop: 'vehicleNo'
        },
        {
          label: '司机',
          prop: 'driverName'
        },
        {
          label: '护卫1',
          prop: 'scurityAName',
          formatter: this.formatRouteTypeName
        },
        {
          label: '护卫2',
          prop: 'scurityBName',
          formatter: this.formatRouteTypeName
        },
        {
          label: '钥匙员',
          prop: 'keyManName',
          formatter: this.formatKeyManName
        },
        {
          label: '密码员',
          prop: 'opManName',
          formatter: this.formatOpManName
        },
        {
          label: '代理车长',
          prop: 'leaderName',
          formatter: this.formatRouteTypeName
        }
      ],
      createVisible: false,
      createForm: {
        planDay: null,
        departmentId: null
      },
      createRules: {
        planDay: [
          { required: true, message: '排班日期不能为空', trigger: 'change' }
        ]
      },
      isExist: false,
      vehicleOption: [],
      driverOption2: [],
      securityOption: [],
      keyOption: [],
      opOption: [],
      updateVisible: false,
      updateForm: {
        id: null,
        planDay: null,
        routeNo: null,
        vehicleNo: null,
        driver: null,
        securitys: [],
        scurityA: null,
        scurityB: null,
        keyMan: null,
        opMan: null,
        leader: null,
        routeType: null
      },
      updateRules: {
        vehicleNo: [
          { required: true, message: '车辆不能为空', trigger: 'blur' }
        ],
        driver: [{ required: true, message: '司机不能为空', trigger: 'blur' }],
        securitys: [
          { required: true, message: '护卫不能为空', trigger: 'blur' },
          {
            validator: checkRestList,
            message: '请选择两名护卫',
            trigger: 'blur'
          }
        ],
        keyMan: [
          { required: true, message: '钥匙员不能为空', trigger: 'blur' }
        ],
        opMan: [{ required: true, message: '密码员不能为空', trigger: 'blur' }]
      },
      keyRadioDisable: false,
      opRadioDisable: false,
      pickerOptions: {
        disabledDate(time) {
          return time.getTime() < Date.now()
        }
      },
      // 人员替换
      replaceVisible: false,
      batchListLoading: false,
      replaceBtn: 5,
      replaceList1: [],
      replaceList2: [],
      replaceTableList: [
        {
          label: '司机',
          prop: 'driverName'
        },
        {
          label: '护卫1',
          prop: 'scurityAName'
        },
        {
          label: '护卫2',
          prop: 'scurityBName'
        },
        {
          label: '密码员',
          prop: 'opManName'
        },
        {
          label: '钥匙员',
          prop: 'keyManName'
        }
      ],
      routeLeader: null,
      // 排班记录
      recordVisible: false,
      recordListQuery: {
        limit: 30,
        page: 1,
        departmentId: null
      },
      recordTotal: 0,
      recordList: [],
      recordTableList: [
        {
          label: '排班日期',
          prop: 'planDay',
          formatter: this.formatDate
        },
        {
          label: '类别',
          prop: 'category',
          formatter: this.formatRecordCategory
        },
        {
          label: '操作类型',
          prop: 'opType',
          formatter: this.formatRecordType
        },
        {
          label: '修改用户',
          prop: 'createUserName'
        },
        {
          label: '创建时间',
          prop: 'createTime',
          style: 'width:200px',
          formatter: this.formatDateTime
        },
        {
          label: '修改内容',
          prop: 'content'
        }
      ],
      // 上传
      importVisible: false,
      importPath,
      uploadDataForm: {
        departmentId: null,
        planDay: null,
        file: []
      },
      headers: {
        'X-Token': getToken(),
        'X-mac': this.$store.getters.mac
      },
      paramsData: {},
      uploadRules: {
        departmentId: [
          { required: true, message: '权限部门不能为空', trigger: 'blur' }
        ],
        planDay: [
          { required: true, message: '日期不能为空', trigger: 'blur' }
        ],
        file: [
          { required: true, message: '上传文件不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  mounted() {
    authOption().then((res) => {
      this.authOption = res.data
      this.listQuery.departmentId = this.authOption[0].id
      this.getList()
      this.updateRoute()
      this.getJobOption()
    })
  },
  methods: {
    updateRoute() {
      this.listQuery.routeNo = null
      this.listQuery.empId = null
      routeTemplateOption(this.listQuery.departmentId).then(res => {
        this.routeOption = res.data
      })
      this.getJobOption()
    },
    getList() {
      // 计算线路编号
      this.listLoading = true
      listResult(this.listQuery).then((res) => {
        this.list = res.data.list
        this.departmentId = this.listQuery.departmentId
        this.listLoading = false
        this.total = res.data.total
      })
    },
    getJobOption() {
      jobNameOption(this.listQuery.departmentId, '1,2,3,4').then((res) => {
        this.empOption = [
          {
            label: '司机岗',
            options: res.data[1]
          },
          {
            label: '护卫岗',
            options: res.data[2]
          },
          {
            label: '钥匙岗',
            options: res.data[3]
          },
          {
            label: '密码岗',
            options: res.data[4]
          }
        ]
      })
    },
    handleCreate() {
      this.createVisible = true
    },
    checkTime(value) {
      if (value === '' || value === null) {
        return
      }
      checkTime(value).then(res => {
        this.isExist = res.data.isExist
      })
    },
    getEditStatus(row) {
      return row['planDay'] > Date.now()
    },
    handleUpdate(row) {
      this.openLoading()
      Promise.all([
        vehicleOption(this.departmentId),
        jobNameOption(this.departmentId, '1,2,3,4'),
        getLeader(this.departmentId)
      ]).then(res => {
        const [res1, res2, res3] = res
        this.vehicleOption = res1.data
        this.driverOption2 = res2.data[1]
        this.securityOption = res2.data[2]
        this.keyOption = res2.data[3].concat(res2.data[4])
        this.opOption = res2.data[4].concat(res2.data[3])
        this.routeLeader = res3.data
        this.updateVisible = true
        this.$nextTick(() => {
          this.$refs['updateForm'].clearValidate()
          for (const key in this.updateForm) {
            this.updateForm[key] = row[key]
          }
          this.updateForm.securitys = []
          if (this.updateForm.scurityA) {
            this.updateForm.securitys.push(this.updateForm.scurityA)
          }
          if (this.updateForm.scurityB) {
            this.updateForm.securitys.push(this.updateForm.scurityB)
          }
          this.changeKeyMan(this.updateForm.keyMan)
          this.changeOpMan(this.updateForm.opMan)
        })
      }).finally(() => {
        this.loading.close()
      })
    },
    handleDelete(row) {
      this.$confirm('确定删除吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.openLoading()
        deleteResult(row.id)
          .then(() => {
            this.$message.success('删除成功')
            const index = this.list.indexOf(row)
            this.list.splice(index, 1)
          })
          .finally(() => {
            this.loading.close()
          })
      })
    },
    handleReplace() {
      if (this.routeLeader === null) {
        getLeader(this.departmentId).then(res => {
          this.routeLeader = res.data

          if (this.batchList[0].routeType === 2 || this.batchList[1].routeType === 2) {
            this.replaceBtn = 1
          } else {
            this.replaceBtn = 5
          }
          this.replaceList1 = [{ ...this.batchList[0] }]
          this.replaceList2 = [{ ...this.batchList[1] }]
          this.replaceVisible = true
        })
      } else {
        if (this.batchList[0].routeType === 2 || this.batchList[1].routeType === 2) {
          this.replaceBtn = 1
        } else {
          this.replaceBtn = 5
        }
        this.replaceList1 = [{ ...this.batchList[0] }]
        this.replaceList2 = [{ ...this.batchList[1] }]
        this.replaceVisible = true
      }
      console.log(this.batchList[0])
    },
    handleImport() {
      this.importVisible = true
      this.$nextTick(() => {
        this.$refs['uploadDataForm'].resetFields()
        this.uploadDataForm.departmentId = this.listQuery.departmentId
        this.uploadDataForm.planDay = new Date(new Date().setHours(24, 0, 0, 0)).getTime()
      })
    },
    replaceStaff(prop) {
      switch (prop) {
        case 'driverName':
          [
            this.replaceList1[0].driver,
            this.replaceList1[0].driverName,
            this.replaceList2[0].driver,
            this.replaceList2[0].driverName
          ] = [
            this.replaceList2[0].driver,
            this.replaceList2[0].driverName,
            this.replaceList1[0].driver,
            this.replaceList1[0].driverName
          ]
          break
        case 'scurityAName':
          [
            this.replaceList1[0].scurityA,
            this.replaceList1[0].scurityAName,
            this.replaceList2[0].scurityA,
            this.replaceList2[0].scurityAName
          ] = [
            this.replaceList2[0].scurityA,
            this.replaceList2[0].scurityAName,
            this.replaceList1[0].scurityA,
            this.replaceList1[0].scurityAName
          ]
          break
        case 'scurityBName':
          [
            this.replaceList1[0].scurityB,
            this.replaceList1[0].scurityBName,
            this.replaceList2[0].scurityB,
            this.replaceList2[0].scurityBName
          ] = [
            this.replaceList2[0].scurityB,
            this.replaceList2[0].scurityBName,
            this.replaceList1[0].scurityB,
            this.replaceList1[0].scurityBName
          ]
          break
        case 'keyManName': {
          const leader1 = [this.routeLeader[this.replaceList2[0].keyMan], this.routeLeader[this.replaceList1[0].opMan]]
          const leader2 = [this.routeLeader[this.replaceList1[0].keyMan], this.routeLeader[this.replaceList2[0].opMan]]
          console.log(leader1, leader2)
          const leader1Length = leader1.filter(item => item === 1).length
          const leader2Length = leader2.filter(item => item === 1).length
          if (leader1Length === 0) {
            this.$message.error(`${this.replaceList1[0].routeNo}号线替换后没有车长`)
          } else if (leader2Length === 0) {
            this.$message.error(`${this.replaceList2[0].routeNo}号线替换后没有车长`)
          } else {
            this.replaceList1[0].leader = null
            this.replaceList2[0].leader = null;
            [
              this.replaceList1[0].keyMan,
              this.replaceList1[0].keyManName,
              this.replaceList2[0].keyMan,
              this.replaceList2[0].keyManName
            ] = [
              this.replaceList2[0].keyMan,
              this.replaceList2[0].keyManName,
              this.replaceList1[0].keyMan,
              this.replaceList1[0].keyManName
            ]
          }
          break
        }
        case 'opManName': {
          const leader1 = [this.routeLeader[this.replaceList1[0].keyMan], this.routeLeader[this.replaceList2[0].opMan]]
          const leader2 = [this.routeLeader[this.replaceList2[0].keyMan], this.routeLeader[this.replaceList1[0].opMan]]
          console.log(leader1, leader2)
          const leader1Length = leader1.filter(item => item === 1).length
          const leader2Length = leader2.filter(item => item === 1).length
          if (leader1Length === 0) {
            this.$message.error(`${this.replaceList1[0].routeNo}号线替换后没有车长`)
          } else if (leader2Length === 0) {
            this.$message.error(`${this.replaceList2[0].routeNo}号线替换后没有车长`)
          } else {
            this.replaceList1[0].leader = null
            this.replaceList2[0].leader = null;
            [
              this.replaceList1[0].opMan,
              this.replaceList1[0].opManName,
              this.replaceList2[0].opMan,
              this.replaceList2[0].opManName
            ] = [
              this.replaceList2[0].opMan,
              this.replaceList2[0].opManName,
              this.replaceList1[0].opMan,
              this.replaceList1[0].opManName
            ]
          }
          break
        }
      }
    },
    replaceResult() {
      if (this.replaceList1[0].leader === null || this.replaceList2[0].leader === null) {
        this.$message.error('请选择车长')
      } else {
        this.$confirm('确定保存人员互换吗?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.openLoading()
          const list = this.replaceList1.concat(this.replaceList2)
          changeResult(list)
            .then(() => {
              this.$notify.success({
                title: '成功',
                message: '保存成功'
              })
              this.getList()
              this.replaceVisible = false
            })
            .finally(() => {
              this.loading.close()
            })
        })
      }
    },
    newResult() {
      this.createForm.departmentId = this.listQuery.departmentId
      // 增加确认提示信息
      this.$refs['createForm'].validate((valid) => {
        if (valid) {
          this.openLoading()
          if (this.isExist) {
            this.$confirm('<span style="color: red">系统已经进行了当前日期的预排班，点击确定将进行重排？</span>', '提示', {
              dangerouslyUseHTMLString: true,
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              type: 'warning'
            }).then(() => {
              addResult(this.createForm).then(() => {
                this.getList()
                this.createVisible = false
                this.$message.success('排班结果已生成')
              })
            }).finally(() => {
              this.loading.close()
            })
          } else {
            addResult(this.createForm).then(() => {
              this.getList()
              this.createVisible = false
              this.$message.success('排班结果已生成')
            }).finally(() => {
              this.loading.close()
            })
          }
        }
      })
    },
    updateResult() {
      this.$refs['updateForm'].validate((valid) => {
        if (valid) {
          // 设置护卫数据

          if (this.updateForm.leader == null) {
            this.$message.warning('请设置车长')
            return
          }
          if (this.updateForm.routeType !== 1) {
            this.updateForm.scurityA = this.updateForm.securitys[0]
            this.updateForm.scurityB = this.updateForm.securitys[1]
          }
          this.openLoading()
          updateResult(this.updateForm)
            .then(() => {
              this.getList()
              this.updateVisible = false
              this.$message.success('更新成功')
            })
            .finally(() => {
              this.loading.close()
            })
        }
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
    formatRouteNo(routeNo) {
      return `${routeNo}号线`
    },
    formatKeyManName(name, row) {
      if (row.routeType === 1) {
        return `<span style="color:red">——</span>`
      }
      if (row.keyManCodeList === null) {
        return name
      }
      return name + '<br />' + row.keyManCodeList
    },
    formatOpManName(name, row) {
      if (row.routeType === 1) {
        return `<span style="color:red">——</span>`
      }
      if (row.opManCodeList === null) {
        return name
      }
      return name + '<br />' + row.opManCodeList
    },
    formatRouteTypeName(name, row) {
      if (row.routeType === 1) {
        return `<span style="color:red">——</span>`
      }
      return name
    },
    handleDownload() {
      this.openLoading()
      const taskDate = this.formatDate(this.listQuery.planDay)
      const title = '线路排班-' + taskDate
      resultDownload(this.listQuery)
        .then(function(response) {
          const filename = title + '.xlsx'
          const fileUrl = window.URL.createObjectURL(new Blob([response.data]))
          const fileLink = document.createElement('a')
          fileLink.href = fileUrl
          fileLink.setAttribute('download', filename)
          document.body.appendChild(fileLink)
          fileLink.click()
        })
        .finally(() => {
          this.loading.close()
        })
    },
    handleBankExport() {
      this.openLoading()
      if (this.listQuery.planDay === undefined || this.listQuery.planDay === null) {
        this.$message.warning('请选择排班日期')
        return
      }
      const taskDate = this.formatDate(this.listQuery.planDay)
      const title = '线路加款排班表-' + taskDate
      resultExport(this.listQuery)
        .then(function(response) {
          const filename = title + '.xlsx'
          const fileUrl = window.URL.createObjectURL(new Blob([response.data]))
          const fileLink = document.createElement('a')
          fileLink.href = fileUrl
          fileLink.setAttribute('download', filename)
          document.body.appendChild(fileLink)
          fileLink.click()
        })
        .finally(() => {
          this.loading.close()
        })
    },
    changeKeyMan(value) {
      const routeLeader = this.routeLeader[value]
      routeLeader ? (this.keyRadioDisable = false) : (this.keyRadioDisable = true)

      this.updateForm.leader =
        this.updateForm.leader === this.updateForm.keyMan ||
        this.updateForm.leader === this.updateForm.opMan
          ? this.updateForm.leader
          : null
    },
    changeOpMan(value) {
      const routeLeader = this.routeLeader[value]
      routeLeader ? (this.opRadioDisable = false) : (this.opRadioDisable = true)

      this.updateForm.leader =
        this.updateForm.leader === this.updateForm.keyMan ||
        this.updateForm.leader === this.updateForm.opMan
          ? this.updateForm.leader
          : null
    },
    selectionChange(val) {
      if (val.length > 2) {
        val.length = 2
      }
      this.batchList = val.map(
        ({
          routeNo,
          routeType,
          driver,
          driverName,
          keyMan,
          keyManName,
          opMan,
          opManName,
          scurityA,
          scurityAName,
          scurityB,
          scurityBName,
          id,
          leader
        }) => {
          return {
            routeNo,
            routeType,
            driver,
            driverName,
            keyMan,
            keyManName,
            opMan,
            opManName,
            scurityA,
            scurityAName,
            scurityB,
            scurityBName,
            id,
            leader
          }
        }
      )
    },
    openLoading() {
      this.loading = this.$loading({
        lock: true,
        text: '正在加载...请勿进行其它操作',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      })
    },
    tableClass(row) {
      if (row.routeType === 1) {
        return 'warning-row'
      }
      return ''
    },
    handleRecord() {
      this.recordVisible = true
      this.getRecordList()
    },
    getRecordList() {
      this.recordListQuery.departmentId = this.listQuery.departmentId
      getRecord(this.recordListQuery).then(res => {
        this.recordList = res.data.list
        this.recordTotal = res.data.total
      })
    },
    formatRecordCategory(category) {
      if (category === 1) {
        return '手动排班'
      }
      return '系统排班'
    },
    formatRecordType(type) {
      if (type === 0) {
        return '创建'
      }
      return '更新'
    },
    formatDateTime(timestamp) {
      if (!timestamp || timestamp === 0) {
        return '-'
      }
      const date = new Date(timestamp)
      return formatdate(date, 'yyyy-MM-dd hh:mm:ss')
    },
    exportRemove(file, fileList) {
      this.uploadDataForm.file = fileList
    },
    exportError(res) {
      this.loading.close()
      this.$message.error('导入失败')
    },
    exportSuccess(res, file) {
      this.loading.close()
      if (res.code === -1) {
        this.$message.error(res.msg)
        this.$refs.upload.clearFiles()
      } else {
        this.$message.success('导入成功')
        this.importVisible = false
        this.getList()
      }
    },
    exportChange(file, fileList) {
      this.uploadDataForm.file = fileList.slice(-1)
    },
    beforeUpload(file) {
      const isExcel =
        file.type === 'application/vnd.ms-excel' ||
        file.type ===
          'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
      if (!isExcel) {
        this.$notify.error({
          title: '文件格式错误',
          message: '只支持xls,xlsx格式'
        })
      }
      return isExcel
    },
    importData() {
      this.$refs['uploadDataForm'].validate((valid) => {
        if (valid) {
          this.paramsData.planDay = this.uploadDataForm.planDay
          this.paramsData.departmentId = this.uploadDataForm.departmentId
          this.$refs.upload.submit()
          this.openLoading()
        }
      })
    }
  }
}
</script>

<style scoped lang="scss">
.filter-container * {
  margin-left: 10px;
}
.filter-container > .el-select,.filter-container > .el-input{
  width: 140px;
}
.scrollbar {
  ::v-deep .el-scrollbar__wrap {
    overflow: auto;
  }
}

.replace-button {
  width: calc(20% - 60px);
  text-align: center;
  display: inline-block;
  margin: 20px 0;
  button {
    font-size: 24px;
    color: #999;
  }
}

.route-title{
  font-size: 16px;
  color: #99a9bf;
  font-weight: 600;
  margin-bottom: 20px;
}
</style>
