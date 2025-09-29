<template>
  <div class="app-container">
    <div class="filter-container">
      <el-select
        v-model="listQuery.departmentId"
        placeholder="请选择所属部门"
        class="filter-item"
      >
        <el-option
          v-for="item in authOption"
          :key="item.id"
          :label="item.name"
          :value="item.id"
        />
      </el-select>
      <el-input
        v-model="listQuery.routeNo"
        clearable
        class="filter-item"
        style="width: 200px"
        placeholder="请选择线路编号"
        :maxlength="32"
      />
      <el-input
        v-model="listQuery.routeName"
        clearable
        class="filter-item"
        style="width: 200px"
        placeholder="请选择线路名称"
        :maxlength="32"
      />
      <el-button
        v-permission="['/base/routeTemplate/list']"
        class="filter-item"
        type="primary"
        icon="el-icon-search"
        @click="listQuery.page=1;getList()"
      >查找</el-button>
      <el-button
        v-permission="['/base/routeTemplate/save']"
        class="filter-item"
        type="primary"
        icon="el-icon-edit"
        @click="handleCreate"
      >添加</el-button>
      <el-button
        v-permission="['/base/routeTemplate/record']"
        class="filter-item"
        type="info"
        icon="el-icon-document"
        @click="handleRecord"
      >线路任务记录</el-button>
    </div>
    <my-table
      :list-loading="listLoading"
      :data-list="list"
      :table-list="tableList"
    >
      <template v-slot:operate>
        <el-table-column
          align="center"
          label="早送晚收"
          class-name="small-padding fixed-width"
          width="140"
        >
          <template slot-scope="scope">
            <el-button
              v-permission="['/base/routeTemplate/bankInfo']"
              type="primary"
              size="mini"
              plain
              @click="bankInfoVisible = true,selectedRouteId = scope.row.id,getBankInfo(scope.row.id)"
            >途经网点</el-button>
            <!-- <el-button
              v-permission="['/base/routeTemplate/atmInfo']"
              type="primary"
              size="mini"
              plain
              @click="handleDetail(scope.row.id, 'atm')"
            >设备列表详情</el-button> -->
          </template>
        </el-table-column>
        <!-- <el-table-column
          align="center"
          label="管理"
          class-name="small-padding fixed-width"
          width="180"
        >
          <template slot-scope="scope">
            <el-button
              v-permission="['/base/routeTemplate/atmInfo']"
              type="primary"
              size="mini"
              plain
              @click="handleManage(scope.row)"
            >管理设备列表</el-button>
          </template>
        </el-table-column> -->
        <el-table-column
          align="center"
          label="操作"
          class-name="small-padding fixed-width"
          width="260"
        >
          <template slot-scope="scope">
            <el-button
              v-permission="['/base/routeTemplate/update']"
              type="primary"
              size="mini"
              @click="handleUpdate(scope.row)"
            >编辑</el-button>
            <el-button
              v-permission="['/base/routeTemplate/delete']"
              type="danger"
              size="mini"
              @click="handleDelete(scope.row)"
            >删除</el-button>
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
    <!-- 添加和编辑 -->
    <el-dialog
      :title="textMap[dialogStatus]"
      :visible.sync="dialogFormVisible"
      :close-on-click-modal="false"
    >
      <el-form
        ref="dataForm"
        :rules="rules"
        :model="dataForm"
        status-icon
        label-position="right"
        label-width="120px"
        style="width: 700px; margin-left: 50px"
      >
        <el-form-item label="所属部门" prop="departmentId">
          <el-select
            v-model="dataForm.departmentId"
            placeholder="请选择所属部门"
            style="width:50%"
          >
            <el-option
              v-for="item in authOption"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="线路编号" prop="routeNo">
          <el-input
            v-model.number="dataForm.routeNo"
            :maxlength="3"
            style="width: 50%"
          />
        </el-form-item>
        <el-form-item label="线路名称" prop="routeName">
          <el-input
            v-model="dataForm.routeName"
            :maxlength="64"
            style="width: 50%"
          />
        </el-form-item>
        <!-- <el-form-item label="计划开始时间" prop="planBeginTime">
          <el-time-select
            v-model="dataForm.planBeginTime"
            placeholder="起始时间"
            :picker-options="{
              start: '00:00',
              step: '00:30',
              end: '24:00',
              maxTime: dataForm.planFinishTime,
            }"
            default-value="08:00"
            style="width: 50%"
          />
        </el-form-item>
        <el-form-item label="计划结束时间" prop="planFinishTime">
          <el-time-select
            v-model="dataForm.planFinishTime"
            placeholder="结束时间"
            :picker-options="{
              start: '00:00',
              step: '00:30',
              end: '24:00',
              minTime: dataForm.planBeginTime,
            }"
            :default-value="dataForm.planBeginTime ? dataForm.planBeginTime : '08:00' "
            style="width: 50%"
          />
        </el-form-item> -->
        <el-form-item label="线路类型" prop="routeType">
          <el-select
            v-model="dataForm.routeType"
            placeholder="请选择线路类型"
            style="width:50%"
          >
            <el-option
              v-for="item in options.routeType"
              :key="item.code"
              :label="item.content"
              :value="item.code"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="生成规则" prop="rule">
          <el-radio-group v-model="dataForm.rule" @change="ruleChange">
            <el-radio v-for="item in options.rule" :key="item.code" :label="item.code">{{ item.content }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-show="dataForm.rule === 1" label="生成标志" prop="sign">
          <el-radio v-for="item in options.sign" :key="item.code" v-model="dataForm.sign" :label="item.code">{{ item.content }}</el-radio>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="dataForm.sort" :min="0" label="描述文字" />
        </el-form-item>
        <el-form-item label="备注" prop="comments">
          <el-input
            v-model="dataForm.comments"
            :maxlength="64"
            style="width: 50%"
          />
        </el-form-item>
        <!-- <el-form-item label="ATM设备" prop="atmList">
          <el-table
            :data="atmList"
            border
            fit
            :header-cell-style="{ 'background-color': '#f5f5f5' }"
          >
            <el-table-column align="center" width="80">
              <template slot="header">
                <el-button
                  plain
                  type="primary"
                  icon="el-icon-circle-plus-outline"
                  @click="atmList.push({})"
                />
              </template>
              <template slot-scope="scope">
                <el-button
                  plain
                  type="danger"
                  icon="el-icon-remove-outline"
                  @click="atmList.splice(scope.$index, 1)"
                />
              </template>
            </el-table-column>
            <el-table-column align="center" label="清机机构" width="220">
              <template slot-scope="scope">
                <treeSelect
                  v-model="scope.row.bankId"
                  :disable-branch-nodes="true"
                  :append-to-body="true"
                  z-index="9999"
                  :normalizer="normalizer"
                  :show-count="true"
                  :options="bankClearTree"
                  placeholder="请选择清机机构"
                  @select="scope.row.atmId = []"
                />
              </template>
            </el-table-column>
            <el-table-column align="center" label="ATM设备">
              <template slot-scope="scope">
                <el-select
                  v-model="scope.row.atmId"
                  filterable
                  multiple
                  :placeholder="
                    scope.row.bankId == null
                      ? '请先选择清机机构'
                      : '请选择ATM设备'
                  "
                  :disabled="scope.row.bankId == null ? true : false"
                  style="width: 100%"
                >
                  <el-option
                    v-for="item in atmOption[scope.row.bankId]"
                    :key="item.id"
                    :label="item.name"
                    :value="item.id"
                  />
                </el-select>
              </template>
            </el-table-column>
          </el-table>
        </el-form-item> -->
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button
          type="primary"
          @click="newData(dialogStatus)"
        >确定</el-button>
      </div>
    </el-dialog>
    <!-- 详情 -->
    <!-- <el-dialog
      :title="detailTitle"
      :visible.sync="detailFormVisible"
      :close-on-click-modal="false"
      width="60%"
    >
      <my-table
        v-if="detailTitle === '设备列表'"
        :list-loading="detailListLoading"
        :data-list="detailAtmList"
        :table-list="atmTableList"
      />
      <my-table
        v-else
        :list-loading="detailListLoading"
        :data-list="detailBankList"
        :table-list="bankTableList"
      >
        <template v-slot:operate>
          <el-table-column
            align="center"
            label="所在地区"
            class-name="small-padding fixed-width"
          >
            <template slot-scope="scope">
              <el-button
                v-if="scope.row.province"
                type="primary"
                size="mini"
                plain
              >{{ scope.row.province }}</el-button>
              <el-button
                v-if="scope.row.province"
                type="primary"
                size="mini"
                plain
              >{{ scope.row.city }}</el-button>
              <el-button
                v-if="scope.row.province"
                type="primary"
                size="mini"
                plain
              >{{ scope.row.district }}</el-button>
            </template>
          </el-table-column>
          <el-table-column
            align="center"
            label="详细地址"
            prop="address"
          /></template></my-table>
      <pagination
        v-show="detailTotal > 0"
        :total="detailTotal"
        :page.sync="detailListQuery.page"
        :limit.sync="detailListQuery.limit"
        @pagination="getDetailList(detailTitle)"
      />
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailFormVisible = false">关闭</el-button>
      </div>
    </el-dialog> -->
    <!-- 管理 -->
    <el-dialog
      title="管理设备列表"
      :visible.sync="manageFormVisible"
      :close-on-click-modal="false"
      width="70%"
      class="manage"
    >
      <el-form label-position="left" label-width="100px" style="width: 100%">
        <el-form-item label="线路">
          <span>{{ manageRouteName }}</span>
        </el-form-item>
        <el-form-item label="所属银行">
          <treeSelect
            v-model="manageListQuery.bankId"
            :normalizer="normalizer"
            :show-count="true"
            :options="bankClearTopBank"
            placeholder="请选择银行网点"
            style="width: 24%; display: inline-block"
          />
          <el-button
            type="primary"
            class="manage-btn"
            :disabled="!sortable.options.disabled"
            @click="getManageList()"
          >查找</el-button>
          <el-button
            v-permission="['/base/routeTemplate/saveAtm']"
            type="primary"
            class="manage-btn"
            @click="handleManageCreate"
          >添加</el-button>
          <el-button
            v-if="sortable.options.disabled"
            v-permission="['/base/routeTemplate/updateAtmSort']"
            type="primary"
            class="manage-btn"
            @click="handleUpdateSort"
          >调整排序</el-button>
          <el-button
            v-else
            type="success"
            plain
            class="manage-btn"
            @click="sortable.options.disabled = true"
          >完成</el-button>
        </el-form-item>
        <el-table
          ref="manageList"
          v-loading="manageListLoading"
          :row-key="getRowKey"
          :data="manageList"
          style="width: 100%"
          height="400px"
        >
          <el-table-column
            v-for="(item, index) in manageTableList"
            :key="index"
            align="center"
            :label="item.label"
            :prop="item.prop"
            :width="item.width"
          >
            <template slot-scope="scope">
              <span
                v-if="item.formatter"
                :data-id="scope.row.id"
                v-html="item.formatter(scope.row[item.prop], scope.row)"
              />
              <span v-else :data-id="scope.row.id">{{
                scope.row[item.prop]
              }}</span>
            </template>
          </el-table-column>
          <el-table-column
            align="center"
            label="操作"
            class-name="small-padding fixed-width"
            width="220"
          >
            <template slot-scope="scope">
              <el-button
                v-permission="['/base/routeTemplate/deleteAtm']"
                type="danger"
                size="mini"
                @click="handleManageDelete(scope.row)"
              >删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-form>
    </el-dialog>
    <!-- 管理添加或编辑 -->
    <el-dialog
      :title="textMap[manageDialogStatus]"
      :visible.sync="manageAddVisible"
      :close-on-click-modal="false"
      width="40%"
      class="manage"
    >
      <el-form
        ref="addForm"
        :rules="addRules"
        :model="addForm"
        status-icon
        label-position="right"
        label-width="100px"
        style="width: 80%; margin-left: 20px"
      >
        <!-- <el-form-item label="所属银行" prop="bankId">
          <treeSelect
            v-model="addForm.bankId"
            :disable-branch-nodes="true"
            :normalizer="normalizer"
            :show-count="true"
            :options="bankClearTree"
            placeholder="请选择所属银行"
            style="width: 100%"
            @select="treeSelectChange"
          />
        </el-form-item> -->
        <el-form-item label="设备编号" prop="atmId" style="margin-top:24px">
          <el-select
            v-model="addForm.atmId"
            multiple
            filterable
            placeholder="请选择设备"
            style="width: 100%"
          >
            <el-option
              v-for="item in atmOption"
              :key="item.id"
              :label="`${item.terNo}/${item.bankName}`"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="manageAddVisible = false">取消</el-button>
        <el-button
          type="primary"
          @click="newManageData(manageDialogStatus)"
        >确定</el-button>
      </div>
    </el-dialog>

    <el-dialog
      title="线路定时生成任务记录"
      :visible.sync="recordVisible"
      :close-on-click-modal="false"
      width="65%"
    >
      <div>
        <my-table
          :list-loading="listLoading"
          :data-list="recordList"
          :table-list="recordTableList"
        >
          <template v-slot:operate>
            <el-table-column
              align="center"
              label="执行结果"
              class-name="small-padding fixed-width"
              width="120"
            >
              <template slot-scope="scope">
                <div v-if="scope.row.state == 1 ">
                  <span>执行成功</span>
                </div>
                <div v-else>
                  <span style="color: red">执行失败</span>
                </div>
                <el-button
                  v-if="checkToday(scope.row.routeDate) && scope.row.state === 0"
                  v-permission="['/base/routeTemplate/executeTask']"
                  type="primary"
                  size="mini"
                  @click="handleExecute()"
                >执行</el-button>
              </template>
            </el-table-column>
          </template>
        </my-table>

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

    <!-- 途经网点 -->
    <el-dialog
      title="途经网点"
      :visible.sync="bankInfoVisible"
      :close-on-click-modal="false"
      width="40%"
    >
      <div style="margin-bottom: 12px">
        <el-button
          v-permission="['/base/routeTemplate/saveBank']"
          type="primary"
          @click="bankInfoAdd()"
        >添加</el-button>
      </div>
      <div>
        <my-table
          :list-loading="bankInfoLoading"
          :data-list="bankInfoList"
          :table-list="bankInfoTableList"
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
                  v-permission="['/base/routeTemplate/deleteBank']"
                  type="danger"
                  size="mini"
                  @click="bankInfoDelete(scope.row)"
                >删除</el-button>
              </template>
            </el-table-column>
          </template>
        </my-table>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="bankInfoVisible = false">关闭</el-button>
      </div>
    </el-dialog>

    <!-- 途经网点添加 -->
    <el-dialog
      title="添加途经网点"
      :visible.sync="bankAddVisible"
      :close-on-click-modal="false"
      width="30%"
      class="manage"
    >
      <el-form
        ref="bankAddForm"
        :rules="bankAddRules"
        :model="bankAddForm"
        status-icon
        label-position="right"
        label-width="100px"
        style="width: 80%; margin-left: 20px"
      >
        <el-form-item label="途经网点" prop="bankId" style="margin-top:24px">
          <treeSelect
            v-model="bankAddForm.bankId"
            :disable-branch-nodes="true"
            :normalizer="normalizer"
            :show-count="true"
            :options="bankOption"
            placeholder="请选择取途经网点"
            style="width:80%"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="bankAddVisible = false">取消</el-button>
        <el-button
          type="primary"
          @click="newBankData()"
        >确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import Pagination from '@/components/Pagination'
import myTable from '@/components/Table'
import {
  listTemplate,
  addTemplate,
  updateTemplate,
  deleteTemplate,
  detailAtmInfo,
  detailBankInfo,
  addAtmInfo,
  updateAtmInfo,
  deleteAtmInfo,
  updateAtmSort,
  getRecord,
  executeRoute,
  listBankInfo,
  deleteBankInfo,
  addBankInfo
} from '@/api/escort/template'
import {
  authOption,
  atmOption,
  bankClearTopBank,
  bankOption
} from '@/api/common/selectOption'
import TreeSelect from '@riophae/vue-treeselect' // 引用下拉树组件
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
import { reqDictionary } from '@/api/system/dictionary'
import Sortable from 'sortablejs'
import { formatdate } from '@/utils/date'

export default {
  components: { Pagination, myTable, TreeSelect },
  data() {
    return {
      loading: null,
      list: [],
      listQuery: {
        limit: 10,
        page: 1,
        departmentId: null,
        routeName: null,
        routeNo: null
      },
      options: {
        routeType: [
          { code: 0, content: '离行式线路' },
          { code: 1, content: '附行式线路' }
        ],
        rule: [
          { code: 0, content: '每天' },
          { code: 1, content: '隔天' }
        ],
        sign: [
          { code: 0, content: '生成' },
          { code: 1, content: '不生成' }
        ]
      },
      departmentId: null,
      listLoading: true,
      total: 0,
      tableList: [
        {
          label: '线路编号',
          prop: 'routeNo'
        },
        {
          label: '线路名称',
          prop: 'routeName'
        },
        {
          label: '线路类型',
          prop: 'routeType',
          formatter: this.formatRouteType
        },
        {
          label: '线路生成规则',
          prop: 'rule',
          formatter: this.formatRuleType
        },
        {
          label: '线路生成标志',
          prop: 'sign',
          formatter: this.formatSignType
        },
        // {
        //   label: '计划开始时间',
        //   prop: 'planBeginTime'
        // },
        // {
        //   label: '计划结束时间',
        //   prop: 'planFinishTime'
        // },
        {
          label: '备注',
          prop: 'comments'
        },
        {
          label: '线路排序',
          prop: 'sort'
        }
      ],
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '创建'
      },
      dataForm: {
        comments: null,
        id: null,
        routeNo: null,
        routeName: null,
        departmentId: null,
        // planBeginTime: null,
        // planFinishTime: null,
        routeType: 0,
        rule: 0,
        sign: null,
        sort: 0
      },
      atmList: [],
      bankList: [],
      authOption: [],
      bankClearTopBank: [],
      atmOption: [],
      rules: {
        departmentId: [
          { required: true, message: '所属部门不能为空', trigger: 'blur' }
        ],
        routeName: [
          { required: true, message: '线路名称不能为空', trigger: 'blur' }
        ],
        routeNo: [
          { required: true, message: '线路编号不能为空', trigger: 'blur' },
          { type: 'number', message: '线路编号必须为数字值' }
        ],
        // planBeginTime: [
        //   { required: true, message: '计划开始时间不能为空', trigger: 'blur' }
        // ],
        // planFinishTime: [
        //   { required: true, message: '计划结束时间不能为空', trigger: 'blur' }
        // ],
        routeType: [
          { required: true, message: '线路类型不能为空', trigger: 'blur' }
        ],
        rule: [
          { required: true, message: '线路类型不能为空', trigger: 'blur' }
        ],
        sign: [
          { required: false, message: '线路类型不能为空', trigger: 'blur' }
        ]
      },
      // 详情
      detailTitle: '',
      detailListQuery: {
        limit: 10,
        page: 1,
        id: null
      },
      detailTotal: 0,
      detailOptions: {
        statusT: [
          { code: 0, content: '启用' },
          { code: 1, content: '停用' }
        ]
      },
      detailFormVisible: false,
      detailListLoading: true,
      detailAtmList: [],
      detailBankList: [],
      atmTableList: [
        {
          label: '银行名称',
          prop: 'bankName'
        },
        {
          label: '设备编号',
          prop: 'terNo'
        },
        {
          label: '设备品牌',
          prop: 'terFactory',
          formatter: this.formatTerFactory
        },
        {
          label: '设备类型',
          prop: 'terType',
          formatter: this.formatTerType
        },
        {
          label: '状态',
          prop: 'statusT',
          formatter: this.formatStatus,
          width: 60
        },
        {
          label: '用途',
          prop: 'locationType',
          formatter: this.formatLocationType
        },
        {
          label: '备注',
          prop: 'comments'
        }
      ],
      bankTableList: [
        {
          label: '机构名称',
          prop: 'fullName'
        },
        {
          label: '机构简称',
          prop: 'shortName'
        },
        {
          label: '联系人',
          prop: 'contact'
        },
        {
          label: '联系电话',
          prop: 'contactPhone'
        },
        {
          label: '状态',
          prop: 'statusT',
          formatter: this.formatStatus,
          width: 60
        }
      ],
      // 管理
      manageFormVisible: false,
      manageList: [],
      manageListLoading: false,
      manageRouteName: '',
      manageTableList: [
        {
          label: '所属银行',
          prop: 'headBank'
        },
        {
          label: '所属网点',
          prop: 'shortName'
        },
        {
          label: '设备编号',
          prop: 'terNo'
        }
      ],
      manageListQuery: {
        routeTemplateId: null,
        bankId: null
      },
      sortable: {
        options: {
          disabled: true
        }
      },
      tableData: true,
      selectedId: null,
      // 管理 添加
      manageAddVisible: false,
      addForm: {
        atmId: null,
        routeTemplateId: null,
        id: null
      },
      addRules: {
        atmId: [
          { required: true, message: '设备编号不能为空', trigger: 'blur' }
        ]
      },
      manageDialogStatus: '',
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
          label: '线路日期',
          prop: 'routeDate',
          formatter: this.formatDate
        },
        {
          label: '创建时间',
          prop: 'createTime',
          style: 'width:200px',
          formatter: this.formatDateTime
        }
      ],
      // 途经网点
      bankInfoLoading: false,
      bankInfoVisible: false,
      bankInfoList: [],
      bankInfoTableList: [
        {
          label: '网点名称',
          prop: 'bankName'
        }
      ],
      bankAddVisible: false,
      bankOption: [],
      bankAddForm: {
        bankId: null,
        routeTemplateId: null
      },
      bankAddRules: {
        bankId: [
          { required: true, message: '途径网点不能为空', trigger: 'blur' }
        ]
      },
      selectedRouteId: null
    }
  },
  mounted() {
    this.getOptions().then(() => {
      this.getList()
    })
  },
  methods: {
    getList(data) {
      if (data) {
        for (const key in data) {
          this.listQuery[key] = data[key]
        }
      }
      listTemplate(this.listQuery).then((res) => {
        const data = res.data
        this.list = data.list
        this.total = data.total
        this.listLoading = false
        this.departmentId = this.listQuery.departmentId
      })
    },
    getOptions() {
      return new Promise((resolve, reject) => {
        Promise.all([
          authOption(),
          reqDictionary('TER_FACTORY'),
          reqDictionary('TER_TYPE'),
          reqDictionary('LOCATION_TYPE')
        ])
          .then((res) => {
            const [res1, res4, res5, res6] = res
            this.authOption = res1.data
            this.detailOptions.terFactory = res4.data
            this.detailOptions.terType = res5.data
            this.detailOptions.locationType = res6.data
            this.listQuery.departmentId = this.authOption[0].id
            resolve()
          })
          .catch((err) => {
            reject(err)
          })
      })
    },
    getDetailList(title) {
      if (title === '网点列表') {
        detailBankInfo(this.detailListQuery).then((res) => {
          this.detailBankList = res.data.list
          this.detailListLoading = false
          this.detailTotal = res.data.total
        })
      } else {
        detailAtmInfo(this.detailListQuery).then((res) => {
          this.detailAtmList = res.data.list
          this.detailListLoading = false
          this.detailTotal = res.data.total
        })
      }
    },
    handleCreate() {
      // this.atmOption = res.data
      this.dialogStatus = 'create'
      this.dialogFormVisible = true
      // this.atmList = [{ atmId: [] }, { atmId: [] }, { atmId: [] }]
      this.$nextTick(() => {
        this.$refs['dataForm'].resetFields()
        this.dataForm.departmentId = this.listQuery.departmentId
      })
    },
    handleDetail(id, type) {
      this.detailListQuery.id = id
      this.detailFormVisible = true
      this.detailListLoading = true
      if (type === 'bank') {
        this.detailTitle = '网点列表'
      } else {
        this.detailTitle = '设备列表'
      }
      this.getDetailList(this.detailTitle)
    },
    handleUpdate(row) {
      this.dialogStatus = 'update'
      this.dialogFormVisible = true
      this.ruleChange(row.rule)
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
        for (const key in this.dataForm) {
          this.dataForm[key] = row[key]
        }
        this.dataForm.location = [row.province, row.city, row.district]
        this.dataForm.routeNo = +this.dataForm.routeNo
        // this.atmList = JSON.parse(row.atmList)
      })
    },
    handleDelete(row) {
      this.$confirm('确定删除吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.listLoading = true
        deleteTemplate(row.id)
          .then(() => {
            this.$notify.success({
              title: '成功',
              message: '删除成功'
            })
            const index = this.list.indexOf(row)
            this.list.splice(index, 1)
          })
          .finally(() => {
            this.listLoading = false
          })
      })
    },
    newData(type) {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          this.listLoading = true
          // if (this.bankList) {
          //   this.dataForm.bankList = this.convertIdToString(this.bankList)
          // }
          // this.dataForm.atmList = JSON.stringify(
          //   this.atmList.filter((item) => item.atmId.length !== 0)
          // )
          if (type === 'update') {
            updateTemplate(this.dataForm)
              .then(() => {
                this.getList()
                this.dialogFormVisible = false
                this.$notify.success({
                  title: '成功',
                  message: '更新成功'
                })
              })
              .finally(() => {
                this.listLoading = false
              })
          } else {
            addTemplate(this.dataForm)
              .then(() => {
                this.getList()
                this.dialogFormVisible = false
                this.$notify.success({
                  title: '成功',
                  message: '添加成功'
                })
              })
              .finally(() => {
                this.listLoading = false
              })
          }
        }
      })
    },
    async getManageList() {
      this.manageList = []
      await detailAtmInfo(this.manageListQuery).then((res) => {
        this.manageList = res.data
      })
    },
    handleManage(row) {
      this.manageListQuery.routeTemplateId = row.id
      this.manageRouteName = row.routeName
      bankClearTopBank(this.departmentId).then(res => {
        this.bankClearTopBank = res.data
        this.getManageList().then(() => {
          this.manageFormVisible = true
          this.manageListQuery.bankId = null
          this.selectedId = row.id
          this.$nextTick(() => {
            this.dragSort()
          })
        })
      })
    },
    getBankInfo(id) {
      this.bankInfoLoading = true
      listBankInfo(id).then(res => {
        this.bankInfoLoading = false
        this.bankInfoList = res.data
      })
    },
    bankInfoAdd() {
      bankOption(this.departmentId).then(res => {
        this.bankOption = res.data
        this.bankAddVisible = true
        this.$nextTick(() => {
          this.$refs['bankAddForm'].resetFields()
        })
      })
    },
    newBankData() {
      this.$refs['bankAddForm'].validate((valid) => {
        if (valid) {
          this.openLoading()
          this.bankAddForm.routeTemplateId = this.selectedRouteId
          addBankInfo(this.bankAddForm)
            .then(() => {
              this.getBankInfo(this.selectedRouteId)
              this.bankAddVisible = false
              this.$notify.success({
                title: '成功',
                message: '添加成功'
              })
            })
            .finally(() => {
              this.loading.close()
            })
        }
      })
    },
    bankInfoDelete(row) {
      this.$confirm('确定删除吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.bankInfoLoading = true
        deleteBankInfo(row.id)
          .then(() => {
            this.$notify.success({
              title: '成功',
              message: '删除成功'
            })
            const index = this.bankInfoList.indexOf(row)
            this.bankInfoList.splice(index, 1)
          })
          .finally(() => {
            this.bankInfoLoading = false
          })
      })
    },
    handleManageCreate() {
      atmOption(this.departmentId).then(res => {
        this.atmOption = res.data
        this.manageDialogStatus = 'create'
        this.sortable.options.disabled = true
        this.manageAddVisible = true
        this.$nextTick(() => {
          this.$refs['addForm'].resetFields()
        })
      })
    },
    handleManageDelete(row) {
      this.sortable.options.disabled = true
      this.$confirm('确定删除吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteAtmInfo(row.id).then(() => {
          this.$notify.success({
            title: '成功',
            message: '删除成功'
          })
          this.getManageList()
        })
      })
    },
    handleUpdateSort() {
      this.sortable.options.disabled = false
      if (this.manageListQuery.bankId != null) {
        this.manageListQuery.bankId = null
        this.getManageList()
      }
    },
    newManageData(type) {
      this.$refs['addForm'].validate((valid) => {
        if (valid) {
          this.openLoading()
          this.addForm.routeTemplateId = this.selectedId
          this.addForm.atmId = this.addForm.atmId.toString()
          if (type === 'update') {
            console.log(this.addForm)
            updateAtmInfo(this.addForm)
              .then(() => {
                this.getManageList()
                this.manageAddVisible = false
                this.$notify.success({
                  title: '成功',
                  message: '更新成功'
                })
              })
              .finally(() => {
                this.loading.close()
              })
          } else {
            addAtmInfo(this.addForm)
              .then(() => {
                this.getManageList()
                this.manageAddVisible = false
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
    // visibleChange(event, id) {
    //   if (event === true) {
    //     atmOption(id).then(res => {
    //       this.atmOption = res.data
    //     })
    //   }
    // },
    convertIdToString(ids) {
      let idsString = ''
      for (const elm of ids) {
        idsString += '/' + elm
      }
      if (idsString.length > 0) {
        idsString += '/'
      }
      return idsString
    },
    // 过滤
    formatStatus(status) {
      for (const item of this.detailOptions.statusT) {
        if (item.code === status) {
          return item.content
        }
      }
    },
    formatTerFactory(terFactory) {
      for (const item of this.detailOptions.terFactory) {
        if (item.code === terFactory) {
          return item.content
        }
      }
    },
    formatTerType(terType) {
      for (const item of this.detailOptions.terType) {
        if (item.code === terType) {
          return item.content
        }
      }
    },
    formatLocationType(locationType) {
      for (const item of this.detailOptions.locationType) {
        if (item.code === locationType) {
          return item.content
        }
      }
    },
    formatRouteType(type) {
      return this.options.routeType.filter(item => item.code === type)[0].content
    },
    formatRuleType(type) {
      return this.options.rule.filter(item => item.code === type)[0].content
    },
    formatSignType(type) {
      return this.options.sign.filter(item => item.code === type)[0].content
    },
    // 拖拽表格
    dragSort() {
      const el = this.$refs.manageList.$el.querySelectorAll(
        '.el-table__body-wrapper > table > tbody'
      )[0]
      const _this = this
      this.sortable = Sortable.create(el, {
        disabled: true,
        onEnd(evt) {
          if (evt.oldIndex !== evt.newIndex) {
            _this.manageListLoading = true
            const params = {}
            params.id =
              evt.item.querySelectorAll('td>.cell>span')[0].dataset.id
            params.newSort = evt.newIndex
            updateAtmSort(params).then(() => {
              _this.manageListLoading = false
            })
          }
          // const currRow = _this.tableData.splice(oldIndex, 1)[0]
          // _this.tableData.splice(newIndex, 0, currRow)
        }
      })
    },
    getRowKey(row) {
      return row.hhh
    },
    ruleChange(val) {
      this.dataForm.sign = null
      val === 1 ? this.rules.sign[0].required = true : this.rules.sign[0].required = false
    },
    openLoading() {
      this.loading = this.$loading({
        lock: true,
        text: '正在加载...请勿进行其它操作',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      })
    },
    handleRecord() {
      this.recordVisible = true
      this.getRecordList()
    },
    getRecordList() {
      this.recordListQuery.departmentId = this.listQuery.departmentId
      getRecord(this.recordListQuery).then((res) => {
        this.recordList = res.data.list
        this.recordTotal = res.data.total
      })
    },
    checkToday(dateTime) {
      const today = new Date(new Date().toLocaleDateString()).getTime()
      return today === dateTime
    },
    handleExecute() {
      executeRoute({ departmentId: this.listQuery.departmentId }).then(() => {
        this.getRecordList()
      })
    },
    formatDate(timestamp) {
      if (!timestamp || timestamp === 0) {
        return '-'
      }
      const date = new Date(timestamp)
      return formatdate(date, 'yyyy-MM-dd')
    },
    formatDateTime(timestamp) {
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
.filter-container *:nth-child(n + 2) {
  margin-left: 10px;
}

.manage {
  ::v-deep .el-dialog__body {
    padding-top: 20px;
  }

  .el-form-item {
    margin-bottom: 12px;
  }

  .manage-btn {
    margin-left: 10px;
    vertical-align: top;
  }
}
</style>
