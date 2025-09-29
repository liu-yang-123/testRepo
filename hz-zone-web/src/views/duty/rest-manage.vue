<template>
  <div class="app-container">
    <el-tabs v-model="activeName" @tab-click="tabClick">
      <el-tab-pane label="季度管理" name="first">
        <search-bar :list-query="quarterListQuery" :options="quarterOptions" :search-list="quarterSearchList" :role="quarterRole" @lookUp="getQuarterList" @create="quarterCreate" />
        <my-table
          :list-loading="quarterlistLoading"
          :data-list="quarterList"
          :table-list="quarterTableList"
          :height="'480px'"
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
                  v-permission="[planType === 0 ? '/base/vacation/plan/update' : '/base/vacation/guard/plan/update']"
                  type="primary"
                  size="mini"
                  @click="quarterUpdate(scope.row)"
                >编辑</el-button>
                <el-button
                  v-permission="[planType === 0 ? '/base/vacation/plan/delete' : '/base/vacation/guard/plan/delete']"
                  type="danger"
                  size="mini"
                  @click="quarterDelete(scope.row)"
                >删除</el-button>
              </template>
            </el-table-column>
          </template>
        </my-table>
        <pagination
          v-show="quarterTotal > 0"
          :total="quarterTotal"
          :page.sync="quarterListQuery.page"
          :limit.sync="quarterListQuery.limit"
          @pagination="getQuarterList"
        />
      </el-tab-pane>
      <el-tab-pane label="计划管理" name="second" lazy>
        <el-row :gutter="20">
          <div class="filter-container">
            <el-select
              v-model="departmentId"
              placeholder="请选择所属部门"
              class="filter-item"
              @change="getTree()"
            >
              <el-option
                v-for="item in restOptions.departmentId"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
            <el-select
              v-if="planType === 0"
              v-model="restListQuery.jobType"
              placeholder="请选择岗位"
              clearable
              class="filter-item"
              @change="handleNodeClick()"
            >
              <el-option
                v-for="item in restOptions.jobType"
                :key="item.code"
                :label="item.content"
                :value="item.code"
              />
            </el-select>
            <el-select
              v-model="restListQuery.weekday"
              placeholder="请选择星期"
              clearable
              class="filter-item"
              @change="handleNodeClick()"
            >
              <el-option
                v-for="item in restOptions.weekdays"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
            <el-button
              v-permission="[planType === 0 ? '/base/vacation/plan/save':'/base/vacation/guard/plan/save']"
              class="filter-item"
              type="primary"
              icon="el-icon-edit"
              @click="restCreate()"
            >添加</el-button>
          </div>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="4">
            <el-scrollbar style="height: 500px" class="scrollbar">
              <el-tree
                ref="listTree"
                :data="restOptions.planId"
                :props="defaultProps"
                node-key="id"
                highlight-current
                @node-click="handleNodeClick"
              />
            </el-scrollbar>
          </el-col>
          <el-col :span="20">
            <el-scrollbar style="height: 68vh" class="scrollbar">
              <my-table
                :list-loading="restListLoading"
                :data-list="restList"
                :table-list="restTableList"
              >
                <template v-slot:operate>
                  <el-table-column
                    align="center"
                    label="操作"
                    class-name="small-padding fixed-width"
                    width="200"
                  >
                    <template slot-scope="scope">
                      <el-button
                        v-permission="[planType === 0 ? '/base/vacation/setting/update' : '/base/vacation/guard/setting/update']"
                        type="primary"
                        size="mini"
                        @click="restUpdate(scope.row)"
                      >编辑</el-button>
                      <el-button
                        v-permission="[planType === 0 ? '/base/vacation/setting/delete' : '/base/vacation/guard/setting/delete']"
                        type="danger"
                        size="mini"
                        @click="restDelete(scope.row)"
                      >删除</el-button>
                    </template>
                  </el-table-column>
                </template>
              </my-table>
              <pagination
                v-show="restTotal > 0"
                :total="restTotal"
                :page.sync="restListQuery.page"
                :limit.sync="restListQuery.limit"
                @pagination="getRestList"
              />
              <el-card style="margin-top: 20px">
                <div slot="header">
                  <span style="">排班人数统计</span>
                </div>
                <my-table
                  :list-loading="restListLoading"
                  :data-list="sumList"
                  :table-list="sumTableList"
                />
              </el-card>
            </el-scrollbar>
          </el-col>
        </el-row>
      </el-tab-pane>
      <el-tab-pane label="加班管理" name="third" lazy>
        <search-bar :list-query="overtimeListQuery" :options="overtimeOptions" :search-list="overtimeSearchList" :role="overtimeRole" @lookUp="getOvertimeList" @create="overtimeCreate" />
        <my-table
          :list-loading="overtimeListLoading"
          :data-list="overtimeList"
          :table-list="overtimeTableList"
          :height="'480px'"
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
                  v-permission="[planType === 0 ? '/base/vacation/adjust/delete' : '/base/vacation/guard/adjust/delete']"
                  type="danger"
                  size="mini"
                  @click="overtimeDelete(scope.row)"
                >删除</el-button>
              </template>
            </el-table-column>
          </template>
        </my-table>
        <pagination
          v-show="overtimeTotal > 0"
          :total="overtimeTotal"
          :page.sync="overtimeListQuery.page"
          :limit.sync="overtimeListQuery.limit"
          @pagination="getOvertimeList"
        />
      </el-tab-pane>
      <el-tab-pane label="主备班管理" name="fourth" lazy>
        <search-bar :options="prepareOptions" :list-query="prepareListQuery" :search-list="prepareSearchList" :role="prepareRole" @lookUp="getPrepareList" @create="handleCreate" />
        <my-table
          :list-loading="prepareListLoading"
          :data-list="prepareList"
          :table-list="prepareTableList"
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
                  v-permission="[planType === 0 ? '/base/alternate/update' : '/base/alternate/guard/update']"
                  type="primary"
                  size="mini"
                  @click="handleUpdate(scope.row)"
                >编辑</el-button>
                <el-button
                  v-permission="[planType === 0 ? '/base/alternate/delete': '/base/alternate/guard/deleted']"
                  type="danger"
                  size="mini"
                  @click="handleDelete(scope.row)"
                >删除</el-button>
              </template>
            </el-table-column>
          </template>
        </my-table>
        <pagination
          v-show="prepareTotal > 0"
          :total="prepareTotal"
          :page.sync="prepareListQuery.page"
          :limit.sync="prepareListQuery.limit"
          @pagination="getPrepareList"
        />
      </el-tab-pane>
    </el-tabs>
    <!-- 季度管理 添加编辑 -->
    <el-dialog :title="textMap[dialogStatus]" :visible.sync="quarterVisible" :close-on-click-modal="false">
      <el-form ref="quarterForm" :rules="quarterRules" :model="quarterForm" status-icon label-position="right" label-width="100px" style="width: 80%;margin-left:8%">
        <el-form-item label="计划名称" prop="name">
          <el-input v-model="quarterForm.name" :maxlength="32" style="width:40%" />
        </el-form-item>
        <el-form-item label="日期范围" prop="date">
          <el-date-picker
            v-model="quarterForm.date"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="timestamp"
            style="width:40%"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="quarterVisible = false">取消</el-button>
        <el-button type="primary" @click="newQuarter(dialogStatus)">确定</el-button>
      </div>
    </el-dialog>
    <!-- 计划管理添加 -->
    <el-dialog title="添加" :visible.sync="restVisible" :close-on-click-modal="false">
      <el-form ref="restForm" :rules="restRules" :model="restForm" status-icon label-position="right" label-width="100px" style="width: 80%;margin-left:8% ">
        <el-form-item label="计划名称" prop="planId">
          <el-select v-model="restForm.planId" filterable placeholder="请选择" style="width: 40%">
            <el-option
              v-for="item in restOptions.planId"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
          <el-checkbox v-if="planType === 1" v-model="checkAll" style="margin-left: 24px" @change="isCheckAll">全选</el-checkbox>
        </el-form-item>
        <el-form-item v-if="planType === 0" label="岗位名称">
          <el-select v-model="jobType" filterable placeholder="请选择员工岗位" style="width: 40%" @change="jobChange">
            <el-option
              v-for="item in restOptions.jobType"
              :key="item.code"
              :label="item.content"
              :value="item.code"
            />
          </el-select>
          <el-checkbox v-model="checkAll" style="margin-left: 24px" @change="isCheckAll">全选</el-checkbox>
        </el-form-item>
        <el-form-item
          label="人员列表"
          prop="vacatePlanItemVOS"
        >
          <el-table
            ref="restTable"
            :data="restForm.vacatePlanItemVOS"
            style="margin-top:20px"
            height="40vh"
            border
            fit
            :header-cell-style="{ 'background-color': '#f5f5f5' }"
          >
            <!-- 字段 -->
            <el-table-column align="center" width="80">
              <template slot="header">
                <el-button
                  plain
                  type="primary"
                  icon="el-icon-circle-plus-outline"
                  @click="addClick"
                />
              </template>
              <template slot-scope="scope">
                <el-button
                  plain
                  type="danger"
                  icon="el-icon-remove-outline"
                  @click="restForm.vacatePlanItemVOS.splice(scope.$index, 1)"
                />
              </template>
            </el-table-column>
            <el-table-column align="center" label="员工">
              <template slot-scope="scope">
                <el-form-item
                  :prop="'vacatePlanItemVOS.' + scope.$index + '.empId'"
                  label-width="0px"
                  :rules="{ required: true, message: '请填写', trigger: 'blur' }"
                >
                  <el-select
                    v-model="scope.row.empId"
                    filterable
                    placeholder="请选择员工"
                    style="width: 100%"
                  >
                    <el-option
                      v-for="item in restOptions.empId"
                      :key="item.id"
                      :label="`${item.empNo}/${item.empName}`"
                      :value="item.id"
                      :disabled="getDisabled(item.id)"
                    />
                  </el-select>
                </el-form-item>
              </template>
            </el-table-column>
            <el-table-column align="center" label="休息日">
              <template slot-scope="scope">
                <el-form-item
                  :prop="'vacatePlanItemVOS.' + scope.$index + '.weekdays'"
                  label-width="0px"
                  :rules="{ required: true, message: '请填写', trigger: 'blur' }"
                >
                  <el-select
                    v-model="scope.row.weekdays"
                    multiple
                    filterable
                    placeholder="请选择休息日"
                    style="width: 100%"
                  >
                    <el-option
                      v-for="item in restOptions.weekdays"
                      :key="item.id"
                      :label="item.name"
                      :value="item.id"
                    />
                  </el-select>
                </el-form-item>
              </template>
            </el-table-column>
          </el-table>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="restVisible = false">取消</el-button>
        <el-button type="primary" @click="newRest()">确定</el-button>
      </div>
    </el-dialog>
    <!-- 计划管理编辑 -->
    <el-dialog title="编辑" :visible.sync="updateRestVisible" :close-on-click-modal="false">
      <el-form ref="updateForm" :rules="updateRestRules" :model="updateForm" status-icon label-position="right" label-width="100px" style="width: 80%;margin-left:8% ">
        <el-form-item label="员工姓名">
          <span>{{ selected.empName }}</span>
        </el-form-item>
        <el-form-item label="岗位名称">
          <span>{{ selected.jobName }}</span>
        </el-form-item>
        <el-form-item label="休息星期" prop="weekdays">
          <el-select v-model="updateForm.weekdays" multiple filterable placeholder="请选择" style="width: 40%">
            <el-option
              v-for="item in restOptions.weekdays"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="updateRestVisible = false">取消</el-button>
        <el-button type="primary" @click="updateRest()">确定</el-button>
      </div>
    </el-dialog>
    <!-- 加班管理 添加编辑 -->
    <el-dialog :title="textMap[dialogStatus]" :visible.sync="overtimeVisible" :close-on-click-modal="false">
      <el-form ref="overtimeForm" :rules="overtimeRules" :model="overtimeForm" status-icon label-position="right" label-width="100px" style="width: 80%;margin-left:8% ">
        <el-form-item label="日期" prop="adjustDate">
          <el-date-picker v-model="overtimeForm.adjustDate" value-format="yyyy-MM-dd" type="date" placeholder="选择日期" style="width:40%" />
        </el-form-item>
        <el-form-item label="类型" prop="adjustType">
          <el-select v-model="overtimeForm.adjustType" filterable placeholder="请选择" style="width: 40%" @change="adjustTypeChange">
            <el-option
              v-for="item in overtimeOptions.adjustType"
              :key="item.code"
              :label="item.content"
              :value="item.code"
            />
          </el-select>
        </el-form-item>
        <el-form-item v-if="planType===0" label="岗位" prop="jobType">
          <el-select v-model="jobType" filterable placeholder="请选择" style="width: 40%" @change="overtimeJobChange">
            <el-option
              v-for="item in overtimeOptions.jobType"
              :key="item.code"
              :label="item.content"
              :value="item.code"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="人员" prop="empId">
          <el-select v-model="overtimeForm.empId" filterable placeholder="请选择" style="width: 40%">
            <el-option
              v-for="item in overtimeOptions.empId"
              :key="item.id"
              :disabled="overtimeForm.repEmpId === item.id"
              :label="`${item.empNo}/${item.empName}`"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item v-show="overtimeForm.adjustType === 2" label="替班人员" prop="repEmpId">
          <el-select v-model="overtimeForm.repEmpId" filterable placeholder="请选择" style="width: 40%" clearable>
            <el-option
              v-for="item in overtimeOptions.empId"
              :key="item.id"
              :disabled="overtimeForm.empId === item.id"
              :label="`${item.empNo}/${item.empName}`"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="原因" prop="reason">
          <el-input v-model="overtimeForm.reason" :maxlength="64" style="width: 40%" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="overtimeVisible = false">取消</el-button>
        <el-button type="primary" @click="newOvertime(dialogStatus)">确定</el-button>
      </div>
    </el-dialog>
    <!-- 主备班管理 添加编辑 -->
    <el-dialog :title="textMap[dialogStatus]" :visible.sync="prepareVisible" :close-on-click-modal="false">
      <el-form ref="prepareForm" :rules="prepareRules" :model="prepareForm" status-icon label-position="right" label-width="100px" style="width: 80%;margin-left:8%">
        <el-form-item :label="planType === 0 ? '司机' : '护卫'" prop="employeeId">
          <el-select v-model="prepareForm.employeeId" filterable placeholder="请选择" style="width: 40%">
            <el-option
              v-for="item in prepareOptions.employeeId"
              :key="item.id"
              :label="`${item.empNo}/${item.empName}`"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="计划名称" prop="planId">
          <el-select v-model="prepareForm.planId" filterable placeholder="请选择" style="width: 40%">
            <el-option
              v-for="item in planIdOption"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="类型" prop="alternateType">
          <el-select v-model="prepareForm.alternateType" filterable placeholder="请选择" style="width: 40%" @change="alternateTypeChange">
            <el-option
              v-for="item in prepareOptions.alternateType"
              :key="item.code"
              :label="item.name"
              :value="item.code"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="线路" prop="routeIds">
          <el-select v-if="update" v-model="prepareForm.routeIds" value-key="id" :multiple="multiple" :disabled="prepareForm.alternateType == null" filterable placeholder="请选择" style="width: 40%">
            <el-option
              v-for="item in prepareOptions.routeIds"
              :key="item.id"
              :label="item.routeName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item v-if="planType === 0" label="车辆" prop="vehicleNos">
          <el-select v-model="prepareForm.vehicleNos" :disabled="prepareForm.alternateType == null" filterable placeholder="请选择" style="width: 40%">
            <el-option
              v-for="item in prepareOptions.vehicleNos"
              :key="item.lpno"
              :label="`${item.seqno}/${item.lpno}`"
              :value="item.lpno"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="prepareVisible = false">取消</el-button>
        <el-button type="primary" @click="newPrepare(dialogStatus)">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import searchBar from '@/components/SearchBar'
import Pagination from '@/components/Pagination'
import myTable from '@/components/Table'
import { formatdate } from '@/utils/date'
import { listAlternate, addAlternate, updateAlternate, deleteAlternate, listVacation, addVacation, updateVacation, deleteVacation, listEmpVacation, addEmpVacation, updateEmpVacation, deleteEmpVacation, listSum, listAdjust, addAdjust, deleteAdjust } from '@/api/duty/restManage'
import { listGuardAlternate, addGuardAlternate, updateGuardAlternate, deleteGuardAlternate, listGuard, addGuard, updateGuard, deleteGuard, listEmpGuard, addEmpGuard, updateEmpGuard, deleteEmpGuard, listGuardSum, listGuardAdjust, addGuardAdjust, deleteGuardAdjust } from '@/api/duty/guardManage'
import { authOption, vacationOption, jobNameOption, routeTemplateOption, vehicleOption } from '@/api/common/selectOption'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
export default {
  components: { searchBar, myTable, Pagination },
  data() {
    const checkRestList = (rule, value, callback) => {
      if (value.length > 0) {
        callback()
      } else {
        callback(new Error('请添加任务'))
      }
    }
    const planType = this.$route.name === 'restManage' ? 0 : 1
    return {
      multiple: false,
      update: true,
      activeName: 'first',
      oldName: 'first',
      dialogStatus: '',
      planType: 0,
      textMap: {
        update: '编辑',
        create: '创建'
      },
      departmentId: null,
      quarterSearchList: [
        { name: 'departmentId', label: '所属部门', type: 3, notClear: true },
        { name: 'name', label: '计划名称' }
      ],
      quarterRole: {
        list: planType === 0 ? '/base/vacation/plan/list' : '/base/vacation/guard/plan/list',
        add: planType === 0 ? '/base/vacation/plan/save' : '/base/vacation/guard/plan/save'
      },
      quarterOptions: {
        departmentId: []
      },
      quarterListQuery: {
        page: 1,
        limit: 10,
        name: null,
        departmentId: null,
        planType: null
      },
      quarterTotal: 0,
      quarterlistLoading: false,
      quarterList: [],
      quarterTableList: [
        {
          label: '计划名称',
          prop: 'name'
        },
        {
          label: '开始日期',
          prop: 'beginDate',
          formatter: this.formatDate
        },
        {
          label: '结束日期',
          prop: 'endDate',
          formatter: this.formatDate
        }
      ],
      quarterVisible: false,
      quarterForm: {
        name: null,
        id: null,
        date: null,
        beginDate: null,
        endDate: null,
        departmentId: null,
        planType: null
      },
      quarterRules: {
        name: [{ required: true, message: '计划名称不能为空', trigger: 'blur' }],
        date: [{ required: true, message: '日期范围不能为空', trigger: 'change' }]
      },
      // 休息计划管理
      restOptions: {
        planId: [],
        jobType: [
          { code: 1, content: '司机岗' },
          { code: 3, content: '钥匙岗' },
          { code: 4, content: '密码岗' }
        ],
        departmentId: [],
        weekdays: [
          { id: 1, name: '星期一' },
          { id: 2, name: '星期二' },
          { id: 3, name: '星期三' },
          { id: 4, name: '星期四' },
          { id: 5, name: '星期五' },
          { id: 6, name: '星期六' },
          { id: 7, name: '星期天' }
        ]
      },
      checkAll: false,
      defaultProps: {
        label: 'name'
      },
      restListQuery: {
        page: 1,
        limit: 10,
        planId: null,
        jobType: null,
        departmentId: null,
        planType: null,
        weekday: null
      },
      loading: null,
      restListLoading: false,
      restList: [],
      restTableList: [
        {
          label: '员工姓名',
          prop: 'empName'
        },
        {
          label: '岗位名称',
          prop: 'jobName'
        },
        {
          label: '计划名称',
          prop: 'planName'
        },
        {
          label: '休息星期',
          prop: 'weekdays',
          formatter: this.formatWeekdays,
          width: 180
        },
        {
          label: '更新时间',
          prop: 'updateTime',
          formatter: this.formatDateTime
        }
      ],
      restTotal: 0,
      restVisible: false,
      restForm: {
        planId: null,
        vacatePlanItemVOS: []
      },
      sumList: [],
      sumTableList: [
        {
          label: '岗位名称',
          prop: 'jobName'
        },
        {
          label: '星期一',
          prop: 'countMon'
        },
        {
          label: '星期二',
          prop: 'countTue'
        },
        {
          label: '星期三',
          prop: 'countWed'
        },
        {
          label: '星期四',
          prop: 'countThu'
        },
        {
          label: '星期五',
          prop: 'countFri'
        },
        {
          label: '星期六',
          prop: 'countSat'
        },
        {
          label: '星期日',
          prop: 'countSun'
        }
      ],
      jobType: null,
      restRules: {
        planId: [{ required: true, message: '计划名称不能为空', trigger: 'blur' }],
        vacatePlanItemVOS: [{ validator: checkRestList, required: true }]
      },
      selected: {},
      updateRestVisible: false,
      updateForm: {
        id: null,
        weekdays: null
      },
      updateRestRules: {
        weekdays: [{ required: true, message: '休息星期不能为空', trigger: 'blur' }]
      },
      // 加班管理
      overtimeListQuery: {
        page: 1,
        limit: 10,
        name: null,
        departmentId: null,
        adjustType: null,
        adjustDate: null,
        planType: null
      },
      overtimeOptions: {
        departmentId: [],
        adjustType: [
          { code: 0, content: '休假' },
          { code: 1, content: '加班' },
          { code: 2, content: '替班' }
        ],
        jobType: [
          { code: 1, content: '司机岗' },
          { code: 3, content: '钥匙岗' },
          { code: 4, content: '密码岗' }
        ],
        empId: []
      },
      overtimeSearchList: [
        { name: 'departmentId', label: '所属部门', type: 3, notClear: true },
        { name: 'adjustDate', label: '日期', type: 2 },
        { name: 'adjustType', label: '类型', type: 3 },
        { name: 'name', label: '姓名' }
      ],
      overtimeRole: {
        list: planType === 0 ? '/base/vacation/adjust/list' : '/base/vacation/guard/adjust/list',
        add: planType === 0 ? '/base/vacation/adjust/save' : '/base/vacation/guard/adjust/save'
      },
      overtimeListLoading: false,
      overtimeList: [],
      overtimeTableList: [
        {
          label: '日期',
          prop: 'adjustDate'
        },
        {
          label: '姓名',
          prop: 'empName'
        },
        {
          label: '类型',
          prop: 'adjustType',
          formatter: this.formatAdjustType
        },
        {
          label: '顶替人',
          prop: 'repEmpName'
        },
        {
          label: '原因',
          prop: 'reason'
        }
      ],
      overtimeTotal: 0,
      overtimeVisible: false,
      overtimeForm: {
        adjustDate: null,
        adjustType: null,
        empId: null,
        repEmpId: null,
        reason: null,
        id: null,
        planType: null,
        departmentId: null
      },
      overtimeRules: {
        adjustDate: [{ required: true, message: '日期不能为空', trigger: 'blur' }],
        adjustType: [{ required: true, message: '类型不能为空', trigger: 'blur' }],
        empId: [{ required: true, message: '人员不能为空', trigger: 'blur' }],
        repEmpId: [{ required: true, message: '替换人员不能为空', trigger: 'blur' }]
      },
      // 主备班管理
      prepareList: [],
      prepareListQuery: {
        limit: 10,
        page: 1,
        departmentId: null,
        name: null,
        planType: null,
        routeId: null,
        alternateType: null,
        planId: null
      },
      prepareListLoading: true,
      prepareTotal: 0,
      prepareSearchList: [
        { name: 'departmentId', label: '所属部门', type: 3, notClear: true },
        { name: 'name', label: '姓名' },
        { name: 'routeId', label: '线路编号', type: 3 },
        { name: 'alternateType', label: '主替班', type: 3 },
        { name: 'planId', label: '计划名称', type: 3 }
      ],
      prepareOptions: {
        departmentId: [],
        employeeId: [],
        alternateType: [
          { code: 0, name: '主班' },
          { code: 1, name: '替班' }
        ],
        routeIds: [],
        vehicleNos: [],
        planId: [],
        routeId: []
      },
      prepareRole: {
        list: planType === 0 ? '/base/alternate/list' : '/base/alternate/guard/list',
        add: planType === 0 ? '/base/alternate/save' : '/base/alternate/guard/save'
      },
      prepareTableList: [
        {
          label: '员工',
          prop: 'employeeName'
        },
        {
          label: '计划名称',
          prop: 'planName'
        },
        {
          label: '主替班',
          prop: 'alternateType',
          formatter: this.formatDriver
        },
        {
          label: '当前线路',
          prop: 'routeNos'
        },
        {
          label: '修改时间',
          prop: 'updateTime',
          formatter: this.formatDate
        }
      ],
      prepareVisible: false,
      prepareForm: {
        departmentId: null,
        id: null,
        vehicleNos: '',
        routeIds: null,
        employeeId: null,
        alternateType: null,
        planType: null,
        planId: null
      },
      planIdOption: [],
      prepareRules: {
        planId: [
          { required: true, message: '季度不能为空', trigger: 'blur' }
        ],
        employeeId: [
          { required: true, message: '司机不能为空', trigger: 'blur' }
        ],
        alternateType: [
          { required: true, message: '类型不能为空', trigger: 'blur' }
        ],
        routeIds: [
          { required: true, message: '线路不能为空', trigger: 'blur' }
        ],
        vehicleNos: [
          { required: true, message: '车辆不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  watch: {
    multiple() {
      this.update = false
      setTimeout(() => {
        this.update = true
      })
    }
  },
  created() {
    if (this.$route.name === 'restManage') {
      this.planType = 0
      this.quarterListQuery.planType = this.overtimeListQuery.planType = this.prepareListQuery.planType = 0
      this.quarterForm.planType = this.overtimeForm.planType = this.prepareForm.planType = 0
      this.prepareTableList.splice(-1, 0, {
        label: '车牌号码',
        prop: 'vehicleNos'
      })
    } else {
      this.planType = 1
      this.quarterListQuery.planType = this.overtimeListQuery.planType = this.prepareListQuery.planType = 1
      this.quarterForm.planType = this.overtimeForm.planType = this.prepareForm.planType = 1
    }
  },
  mounted() {
    this.openLoading()
    authOption().then(res => {
      this.quarterOptions.departmentId = res.data
      this.overtimeOptions.departmentId = res.data
      this.restOptions.departmentId = res.data
      this.prepareOptions.departmentId = res.data
      this.quarterListQuery.departmentId = this.quarterOptions.departmentId[0].id
      this.getQuarterList()
    })
  },
  methods: {
    tabClick() {
      if (this.activeName !== this.oldName) {
        if (this.activeName === 'first') {
          this.quarterListQuery.departmentId = this.departmentId
          this.getQuarterList()
        } else if (this.activeName === 'second') {
          this.openLoading()
          this.getTree()
        } else if (this.activeName === 'third') {
          this.overtimeListQuery.departmentId = this.departmentId
          this.getOvertimeList()
        } else if (this.activeName === 'fourth') {
          this.prepareListQuery.departmentId = this.departmentId
          this.getPrepareOptions().then(() => {
            this.getPrepareList()
          })
        }
        this.oldName = this.activeName
      }
    },
    getQuarterList(data) {
      if (data) {
        for (const key in data) {
          this.restListQuery[key] = data[key]
        }
      }
      this.quarterlistLoading = true
      if (this.planType === 0) {
        listVacation(this.quarterListQuery).then((res) => {
          this.quarterList = res.data.list
          this.departmentId = this.quarterListQuery.departmentId
          this.quarterlistLoading = false
          this.quarterTotal = res.data.total
        }).finally(() => {
          this.loading.close()
        })
      } else {
        listGuard(this.quarterListQuery).then((res) => {
          this.quarterList = res.data.list
          this.departmentId = this.quarterListQuery.departmentId
          this.quarterlistLoading = false
          this.quarterTotal = res.data.total
        }).finally(() => {
          this.loading.close()
        })
      }
    },
    quarterCreate() {
      this.dialogStatus = 'create'
      this.quarterVisible = true
      this.$nextTick(() => {
        this.$refs['quarterForm'].resetFields()
      })
    },
    quarterUpdate(row) {
      this.dialogStatus = 'update'
      this.quarterVisible = true
      this.$nextTick(() => {
        this.$refs['quarterForm'].clearValidate()
        for (const key in this.quarterForm) {
          this.quarterForm[key] = row[key]
        }
        this.quarterForm.date = [row.beginDate, row.endDate]
      })
    },
    quarterDelete(row) {
      this.$confirm('确定删除吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.quarterListLoading = true
        if (this.planType === 0) {
          deleteVacation(row.id)
            .then(() => {
              this.$notify.success({
                title: '成功',
                message: '删除成功'
              })
              const index = this.quarterList.indexOf(row)
              this.quarterList.splice(index, 1)
            }).finally(() => {
              this.quarterListLoading = false
            })
        } else {
          deleteGuard(row.id)
            .then(() => {
              this.$notify.success({
                title: '成功',
                message: '删除成功'
              })
              const index = this.quarterList.indexOf(row)
              this.quarterList.splice(index, 1)
            }).finally(() => {
              this.quarterListLoading = false
            })
        }
      })
    },
    newQuarter(type) {
      this.$refs['quarterForm'].validate((valid) => {
        if (valid) {
          const { date, ...obj } = this.quarterForm
          obj.beginDate = date[0]
          obj.endDate = date[1]
          obj.departmentId = this.departmentId
          this.openLoading()
          if (type === 'update') {
            if (this.planType === 0) {
              updateVacation(obj)
                .then(() => {
                  this.getQuarterList()
                  this.quarterVisible = false
                  this.$notify.success({
                    title: '成功',
                    message: '更新成功'
                  })
                })
                .finally(() => {
                  this.loading.close()
                })
            } else {
              updateGuard(obj)
                .then(() => {
                  this.getQuarterList()
                  this.quarterVisible = false
                  this.$notify.success({
                    title: '成功',
                    message: '更新成功'
                  })
                })
                .finally(() => {
                  this.loading.close()
                })
            }
          } else {
            if (this.planType === 0) {
              addVacation(obj)
                .then(() => {
                  this.getQuarterList()
                  this.quarterVisible = false
                  this.$notify.success({
                    title: '成功',
                    message: '添加成功'
                  })
                })
                .finally(() => {
                  this.loading.close()
                })
            } else {
              addGuard(obj)
                .then(() => {
                  this.getQuarterList()
                  this.quarterVisible = false
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
        }
      })
    },
    async getTree() {
      await vacationOption(this.departmentId, this.planType).then(res => {
        this.restOptions.planId = res.data
        if (this.restOptions.planId.length > 0) {
          this.$nextTick(() => {
            this.$refs.listTree.setCurrentKey(this.restOptions.planId[0].id)
          })
          this.handleNodeClick(this.restOptions.planId[0])
        } else {
          this.restList = []
        }
        this.loading.close()
      })
    },
    getRestList() {
      this.restListLoading = true
      if (this.planType === 0) {
        listEmpVacation(this.restListQuery).then((res) => {
          this.restList = res.data.list
          this.restListLoading = false
          this.restTotal = res.data.total
        })
      } else {
        listEmpGuard(this.restListQuery).then((res) => {
          this.restList = res.data.list
          this.restListLoading = false
          this.restTotal = res.data.total
        })
      }
    },
    getSum(planId, jobType) {
      if (this.planType === 0) {
        listSum(planId, jobType).then(res => {
          this.sumList = res.data
        })
      } else {
        listGuardSum(planId, jobType).then(res => {
          this.sumList = res.data
        })
      }
    },
    handleNodeClick(val) {
      if (val != null) {
        this.restListQuery.planId = val.id
      }
      this.planType === 1 ? this.restListQuery.jobType = 2 : false
      this.restListQuery.page = 1
      this.getRestList()
      this.getSum(this.restListQuery.planId, this.restListQuery.jobType)
    },
    restCreate() {
      this.dialogStatus = 'create'
      this.checkAll = false
      this.restVisible = true
      this.restForm.vacatePlanItemVOS = []
      this.$nextTick(() => {
        this.$refs['restForm'].resetFields()
        this.planType === 0 ? this.jobType = this.restOptions.jobType[0].code : this.jobType = 2
        this.jobChange(this.jobType)
      })
    },
    newRest() {
      this.$refs['restForm'].validate((valid) => {
        if (valid) {
          this.openLoading()
          if (this.planType === 0) {
            addEmpVacation(this.restForm)
              .then(() => {
                this.getRestList()
                this.getSum(this.restListQuery.planId, this.restListQuery.jobType)
                this.restVisible = false
                this.$notify.success({
                  title: '成功',
                  message: '添加成功'
                })
              })
              .finally(() => {
                this.loading.close()
              })
          } else {
            addEmpGuard(this.restForm)
              .then(() => {
                this.getRestList()
                this.getSum(this.restListQuery.planId, this.restListQuery.jobType)
                this.restVisible = false
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
    restUpdate(row) {
      this.updateRestVisible = true
      this.selected = row
      this.$nextTick(() => {
        this.$refs['updateForm'].clearValidate()
        for (const key in this.updateForm) {
          this.updateForm[key] = row[key]
        }
      })
    },
    updateRest() {
      this.$refs['updateForm'].validate((valid) => {
        if (valid) {
          this.openLoading()
          if (this.planType === 0) {
            updateEmpVacation(this.updateForm).then(() => {
              this.getRestList()
              this.getSum(this.restListQuery.planId, this.restListQuery.jobType)
              this.updateRestVisible = false
              this.$notify.success({
                title: '成功',
                message: '修改成功'
              })
            })
              .finally(() => {
                this.loading.close()
              })
          } else {
            updateEmpGuard(this.updateForm).then(() => {
              this.getRestList()
              this.getSum(this.restListQuery.planId, this.restListQuery.jobType)
              this.updateRestVisible = false
              this.$notify.success({
                title: '成功',
                message: '修改成功'
              })
            })
              .finally(() => {
                this.loading.close()
              })
          }
        }
      })
    },
    restDelete(row) {
      this.$confirm('确定删除吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.restListLoading = true
        if (this.planType === 0) {
          deleteEmpVacation(row.id)
            .then(() => {
              this.$notify.success({
                title: '成功',
                message: '删除成功'
              })
              const index = this.restList.indexOf(row)
              this.restList.splice(index, 1)
              this.getSum(this.restListQuery.planId, this.restListQuery.jobType)
            }).finally(() => {
              this.restListLoading = false
            })
        } else {
          deleteEmpGuard(row.id)
            .then(() => {
              this.$notify.success({
                title: '成功',
                message: '删除成功'
              })
              const index = this.restList.indexOf(row)
              this.restList.splice(index, 1)
              this.getSum(this.restListQuery.planId, this.restListQuery.jobType)
            }).finally(() => {
              this.restListLoading = false
            })
        }
      })
    },
    // 加班管理
    getOvertimeList() {
      this.overtimeListLoading = true
      if (this.planType === 0) {
        listAdjust(this.overtimeListQuery).then(res => {
          this.overtimeList = res.data.list
          this.departmentId = this.overtimeListQuery.departmentId
          this.overtimeListLoading = false
          this.overtimeTotal = res.data.total
        })
      } else {
        listGuardAdjust(this.overtimeListQuery).then(res => {
          this.overtimeList = res.data.list
          this.departmentId = this.overtimeListQuery.departmentId
          this.overtimeListLoading = false
          this.overtimeTotal = res.data.total
        })
      }
    },
    overtimeCreate() {
      this.dialogStatus = 'create'
      this.overtimeVisible = true
      this.$nextTick(() => {
        this.$refs['overtimeForm'].resetFields()
        this.planType === 0 ? this.jobType = this.overtimeOptions.jobType[0].code : this.jobType = 2
        this.overtimeJobChange(this.jobType)
      })
    },
    newOvertime(type) {
      this.$refs['overtimeForm'].validate((valid) => {
        if (valid) {
          this.overtimeForm.departmentId = this.departmentId
          this.openLoading()
          if (this.planType === 0) {
            addAdjust(this.overtimeForm)
              .then(() => {
                this.getOvertimeList()
                this.overtimeVisible = false
                this.$notify.success({
                  title: '成功',
                  message: '添加成功'
                })
              })
              .finally(() => {
                this.loading.close()
              })
          } else {
            addGuardAdjust(this.overtimeForm)
              .then(() => {
                this.getOvertimeList()
                this.overtimeVisible = false
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
    // overtimeUpdate(row) {
    //   this.dialogStatus = 'update'
    //   this.overtimeVisible = true
    //   this.$nextTick(() => {
    //     this.$refs['overtimeForm'].clearValidate()
    //     for (const key in this.overtimeForm) {
    //       this.quarterForm[key] = row[key]
    //     }
    //     this.quarterForm.date = [row.beginDate, row.endDate]
    //   })
    // },
    overtimeDelete(row) {
      this.$confirm('确定删除吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.overtimeListLoading = true
        if (this.planType === 0) {
          deleteAdjust(row.id)
            .then(() => {
              this.$notify.success({
                title: '成功',
                message: '删除成功'
              })
              const index = this.overtimeList.indexOf(row)
              this.overtimeList.splice(index, 1)
            }).finally(() => {
              this.overtimeListLoading = false
            })
        } else {
          deleteGuardAdjust(row.id)
            .then(() => {
              this.$notify.success({
                title: '成功',
                message: '删除成功'
              })
              const index = this.overtimeList.indexOf(row)
              this.overtimeList.splice(index, 1)
            }).finally(() => {
              this.overtimeListLoading = false
            })
        }
      })
    },
    getPrepareOptions() {
      return new Promise((reslove, reject) => {
        this.prepareListQuery.routeId = null
        this.prepareListQuery.planId = null
        Promise.all([
          routeTemplateOption(this.prepareListQuery.departmentId),
          vacationOption(this.prepareListQuery.departmentId, this.planType)
        ])
          .then((res) => {
            const [res1, res2] = res
            this.prepareOptions.routeId = res1.data.map(elm => {
              return {
                content: `${elm.routeName}/${elm.routeNo}`,
                code: elm.id
              }
            })
            this.prepareOptions.planId = res2.data
            reslove()
          })
          .catch((err) => {
            reject(err)
          })
      })
    },
    // 主备班管理
    getPrepareList(data) {
      if (data) {
        for (const key in data) {
          this.prepareListQuery[key] = data[key]
        }
      }
      if (this.planType === 0) {
        listAlternate(this.prepareListQuery).then((res) => {
          const data = res.data
          this.prepareList = data.list
          this.departmentId = this.prepareListQuery.departmentId
          this.prepareTotal = data.total
          this.prepareListLoading = false
        })
      } else {
        listGuardAlternate(this.prepareListQuery).then((res) => {
          const data = res.data
          this.prepareList = data.list
          this.departmentId = this.prepareListQuery.departmentId
          this.prepareTotal = data.total
          this.prepareListLoading = false
        })
      }
    },
    handleOptions(departmentId = this.departmentId) {
      return new Promise((reslove, reject) => {
        Promise.all([
          jobNameOption(departmentId, '1,2'),
          vehicleOption(departmentId),
          routeTemplateOption(departmentId),
          vacationOption(departmentId, this.planType)
        ])
          .then((res) => {
            const [res1, res2, res3, res4] = res
            if (this.planType === 0) {
              this.prepareOptions.employeeId = res1.data[1]
            } else {
              this.prepareOptions.employeeId = res1.data[2]
            }
            // 护卫
            this.prepareOptions.vehicleNos = res2.data
            this.prepareOptions.routeIds = res3.data
            this.planIdOption = res4.data
            reslove()
          })
          .catch((err) => {
            reject(err)
          })
      })
    },
    handleCreate() {
      this.dialogStatus = 'create'
      this.prepareVisible = true
      this.$nextTick(() => {
        this.$refs['prepareForm'].resetFields()
        this.handleOptions()
      })
    },
    handleUpdate(row) {
      this.handleOptions().then(() => {
        this.dialogStatus = 'update'
        this.prepareVisible = true
        this.$nextTick(() => {
          this.$refs['prepareForm'].clearValidate()
          for (const key in this.prepareForm) {
            this.prepareForm[key] = row[key]
          }
          this.multiple = this.prepareForm.alternateType === 1
          if (this.multiple) {
            this.prepareForm.routeIds = this.prepareForm.routeIds.split(',').map(Number)
          } else {
            this.prepareForm.routeIds = +this.prepareForm.routeIds
          }
        })
      })
    },
    handleDelete(row) {
      this.$confirm('确定删除吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.openLoading()
        if (this.planType === 0) {
          deleteAlternate(row.id)
            .then(() => {
              this.$notify.success({
                title: '成功',
                message: '删除成功'
              })
              const index = this.prepareList.indexOf(row)
              this.prepareList.splice(index, 1)
            }).finally(() => {
              this.loading.close()
            })
        } else {
          deleteGuardAlternate(row.id)
            .then(() => {
              this.$notify.success({
                title: '成功',
                message: '删除成功'
              })
              const index = this.prepareList.indexOf(row)
              this.prepareList.splice(index, 1)
            }).finally(() => {
              this.loading.close()
            })
        }
      })
    },
    newPrepare(type) {
      this.$refs['prepareForm'].validate((valid) => {
        if (valid) {
          this.openLoading()
          this.prepareForm.departmentId = this.departmentId
          const { ...obj } = this.prepareForm
          if (this.prepareForm.alternateType === 1) {
            obj.vehicleNos = obj.vehicleNos ? obj.vehicleNos.toString() : null
            obj.routeIds = obj.routeIds.toString()
          }
          if (type === 'update') {
            if (this.planType === 0) {
              updateAlternate(obj)
                .then(() => {
                  this.getPrepareList()
                  this.prepareVisible = false
                  this.$notify.success({
                    title: '成功',
                    message: '更新成功'
                  })
                })
                .finally(() => {
                  this.loading.close()
                })
            } else {
              updateGuardAlternate(obj)
                .then(() => {
                  this.getPrepareList()
                  this.prepareVisible = false
                  this.$notify.success({
                    title: '成功',
                    message: '更新成功'
                  })
                })
                .finally(() => {
                  this.loading.close()
                })
            }
          } else {
            if (this.planType === 0) {
              addAlternate(obj)
                .then(() => {
                  this.getPrepareList()
                  this.prepareVisible = false
                  this.$notify.success({
                    title: '成功',
                    message: '添加成功'
                  })
                })
                .finally(() => {
                  this.loading.close()
                })
            } else {
              addGuardAlternate(obj)
                .then(() => {
                  this.getPrepareList()
                  this.prepareVisible = false
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
        }
      })
    },
    formatDriver(type) {
      switch (type) {
        case 0:
          return '主班'
        case 1:
          return '替班'
      }
    },
    formatAssign(type) {
      switch (type) {
        case 0:
          return '随机'
        case 1:
          return '固定'
      }
    },
    formatDate(timestamp) {
      if (!timestamp || timestamp === 0) {
        return '-'
      }
      const date = new Date(timestamp)
      return formatdate(date, 'yyyy-MM-dd')
    },
    formatWeekdays(arr) {
      if (arr.length > 0) {
        let str = ''
        for (const item of arr) {
          const name = this.restOptions.weekdays.filter(elm => elm.id === item)[0].name
          str += `<span class="el-tag el-tag--medium el-tag--light" style="margin-left:6px">${name}</span>`
        }
        return str
      }
    },
    formatDateTime(timestamp) {
      if (!timestamp || timestamp === 0) {
        return '-'
      }
      const date = new Date(timestamp)
      return formatdate(date, 'hh:mm:ss')
    },
    formatAdjustType(type) {
      if (type != null) {
        return this.overtimeOptions.adjustType.filter(item => item.code === type)[0].content
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
    jobChange(val) {
      jobNameOption(this.departmentId, val).then(res => {
        this.restOptions.empId = res.data[val]
        this.isCheckAll(this.checkAll)
      })
    },
    overtimeJobChange(val) {
      jobNameOption(this.departmentId, val).then(res => {
        this.overtimeOptions.empId = res.data[val]
        this.overtimeForm.empId = null
        this.overtimeForm.repEmpId = null
      })
    },
    adjustTypeChange(val) {
      val === 2 ? this.overtimeRules.repEmpId[0].required = true : (this.overtimeRules.repEmpId[0].required = false, this.overtimeForm.repEmpId = null)
    },
    alternateTypeChange(val) {
      this.prepareForm.vehicleNos = null
      if (val === 1) {
        this.multiple = true
        this.prepareForm.routeIds = []
      } else {
        this.multiple = false
        this.prepareForm.routeIds = null
      }
    },
    addClick() {
      this.restForm.vacatePlanItemVOS.push({ empId: null, weekdays: null })
      this.$nextTick(() => {
        // this.$refs.restTable.$el.querySelector('.el-table__body-wrapper').scrollTo(0, this.$refs.restTable.$el.querySelector('.el-table__body').scrollHeight + 'px')
        this.$refs.restTable.$el.querySelector('.el-table__body-wrapper').scrollTop = this.$refs.restTable.$el.querySelector('.el-table__body').scrollHeight
      })
    },
    isCheckAll(val) {
      if (val) {
        this.restForm.vacatePlanItemVOS = []
        this.restOptions.empId.forEach(item => {
          this.restForm.vacatePlanItemVOS.push({ empId: item.id, weekdays: null })
        })
      } else {
        this.restForm.vacatePlanItemVOS = []
      }
    },
    getDisabled(id) {
      if (this.restForm.vacatePlanItemVOS.find((item) => item.empId === id)) {
        return true
      }
      return false
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
