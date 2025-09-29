<template>
  <div class="app-container">
    <!-- 查询栏 -->
    <div class="filter-container">
      <treeSelect
        v-model="listQuery.departmentId"
        class="filter-item"
        style="
          width: 200px;
          display: inline-block;
          margin-left: 6px;
          font-size: 14px;
        "
        :clearable="false"
        :normalizer="normalizer"
        :show-count="true"
        :options="departmentTree"
        placeholder="所在部门"
      />
      <el-input
        v-model="listQuery.empName"
        clearable
        class="filter-item"
        style="width: 120px"
        placeholder="姓名"
        :maxlength="10"
      />
      <el-input
        v-model="listQuery.maxAge"
        clearable
        class="filter-item"
        style="width: 120px"
        placeholder="年龄大于"
        :maxlength="6"
        oninput="value=value.replace(/^\.+|[^\d.]/g,'')"
      />
      <el-input
        v-model="listQuery.minAge"
        clearable
        class="filter-item"
        style="width: 120px"
        placeholder="年龄小于"
        :maxlength="32"
        oninput="value=value.replace(/^\.+|[^\d.]/g,'')"
      />
      <el-select
        v-model="listQuery.sex"
        placeholder="性别"
        class="filter-item"
        clearable
        style="width: 120px"
      >
        <el-option
          v-for="item in dictionaryData['GENDER']"
          :key="item.code"
          :label="item.content"
          :value="item.code"
        />
      </el-select>
      <el-select
        v-model="listQuery.education"
        placeholder="学历"
        class="filter-item"
        clearable
        style="width: 120px"
      >
        <el-option
          v-for="item in dictionaryData['EDUCATION']"
          :key="item.code"
          :label="item.content"
          :value="item.code"
        />
      </el-select>
      <el-select
        v-model="listQuery.status"
        placeholder="状态"
        class="filter-item"
        clearable
        style="width: 120px"
      >
        <el-option
          v-for="item in dictionaryData['EMPLOYEE_STATUS']"
          :key="item.code"
          :label="item.content"
          :value="item.code"
        />
      </el-select>
      <el-select
        v-model="listQuery.politic"
        placeholder="政治面貌"
        class="filter-item"
        clearable
        style="width: 120px"
      >
        <el-option
          v-for="item in dictionaryData['POLITICAL_OUTLOOK']"
          :key="item.code"
          :label="item.content"
          :value="item.code"
        />
      </el-select>
      <el-select
        v-model="listQuery.jobId"
        placeholder="岗位"
        class="filter-item"
        clearable
        style="width: 120px"
      >
        <el-option
          v-for="item in jobTree"
          :key="item.id"
          :label="item.name"
          :value="item.id"
        />
      </el-select>
      <el-button
        v-permission="['/base/employee/list']"
        class="filter-item"
        type="primary"
        icon="el-icon-search"
        @click="getList()"
      >查找</el-button>
      <el-button
        v-permission="['/base/employee/save']"
        class="filter-item"
        type="primary"
        icon="el-icon-edit"
        @click="handleCreate"
      >添加</el-button>
    </div>
    <!-- 表格 -->
    <my-table
      :list-loading="listLoading"
      :data-list="list"
      :table-list="tableList"
    >
      <template v-slot:operate>
        <el-table-column
          align="center"
          label="详情"
          class-name="small-padding fixed-width"
          width="100"
        >
          <template slot-scope="scope">
            <el-button
              v-permission="['/base/employee/info']"
              type="primary"
              size="mini"
              @click="handleDetail(scope.row.id)"
            >查看</el-button>
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          label="操作"
          class-name="small-padding fixed-width"
          width="240"
        >
          <template slot-scope="scope">
            <el-button
              v-permission="['/base/employee/quit']"
              type="warning"
              size="mini"
              :disabled="scope.row.statusT === 1"
              @click="handleQuit(scope.row)"
            >离职</el-button>
            <el-button
              v-permission="['/base/employee/update']"
              type="primary"
              size="mini"
              @click="handleUpdate(scope.row)"
            >编辑</el-button>
            <el-button
              v-permission="['/base/employee/delete']"
              type="danger"
              size="mini"
              @click="handleDelete(scope.row)"
            >删除</el-button>
          </template>
        </el-table-column>
      </template>
    </my-table>
    <!-- 分页 -->
    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="listQuery.page"
      :limit.sync="listQuery.limit"
      @pagination="getList"
    />
    <!-- 添加 编辑 -->
    <el-dialog
      :title="textMap[dialogStatus]"
      :visible.sync="dialogFormVisible"
      :close-on-click-modal="false"
    >
      <el-form
        ref="dataForm"
        :rules="rules"
        status-icon
        label-position="right"
        :model="dataForm"
        label-width="130px"
        :hide-required-asterisk="false"
        style="width: 500px; margin-left: 50px"
      >
        <el-form-item label="入职日期" prop="entryDate">
          <el-date-picker
            v-model="dataForm.entryDate"
            style="width: 100%"
            value-format="timestamp"
            type="date"
            placeholder="选择日期"
          />
        </el-form-item>
        <el-form-item label="工号" prop="empNo">
          <el-input
            v-model="dataForm.empNo"
            :maxlength="32"
            oninput="value=value.replace(/[^-\w\d]/g,'')"
          />
        </el-form-item>
        <el-form-item label="姓名" prop="empName">
          <el-input v-model="dataForm.empName" :maxlength="32" />
        </el-form-item>
        <el-form-item label="性别" prop="sex">
          <el-radio v-for="item in dictionaryData['GENDER']" :key="item.code" v-model="dataForm.sex" :label="item.code">{{ item.content }}</el-radio>
        </el-form-item>
        <el-form-item label="部门" prop="departmentId">
          <treeSelect
            v-model="dataForm.departmentId"
            style="font-size: 14px"
            :normalizer="normalizer"
            :show-count="true"
            :options="departmentTree"
            placeholder="所在部门"
          />
        </el-form-item>
        <el-form-item label="编制" prop="manningQuotas">
          <el-select
            v-model="dataForm.manningQuotas"
            placeholder="编制"
            clearable
            style="width:100%"
          >
            <el-option
              v-for="item in quotasOption"
              :key="item.code"
              :label="item.content"
              :value="item.code"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="所属公司" prop="affiliatedCompany">
          <el-select
            v-model="dataForm.affiliatedCompany"
            placeholder="所属公司"
            clearable
            style="width:100%"
          >
            <el-option
              v-for="item in companyOption"
              :key="item.code"
              :label="item.content"
              :value="item.code"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="服务证" prop="serviceCertificate">
          <el-input v-model="dataForm.serviceCertificate" :maxlength="32" />
        </el-form-item>
        <el-form-item label="合同到期时间" prop="expirationDate">
          <el-date-picker
            v-model="dataForm.expirationDate"
            style="width: 100%"
            value-format="timestamp"
            type="date"
            placeholder="选择日期"
          />
        </el-form-item>
        <el-form-item label="岗位" prop="jobIds">
          <el-select
            v-model="dataForm.jobIds"
            placeholder="岗位"
            clearable
            style="width:100%"
          >
            <el-option
              v-for="item in jobTree"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="职务" prop="title">
          <el-radio v-for="item in jobOptions" :key="item.code" v-model="dataForm.title" :label="item.code">{{ item.content }}</el-radio>
        </el-form-item>
        <el-form-item label="身份证号" prop="idno">
          <el-input
            v-model="dataForm.idno"
            :maxlength="32"
            oninput="value=value.replace(/[^X\d]/g,'')"
          />
        </el-form-item>
        <el-form-item label="民族" prop="nation">
          <el-select
            v-model="dataForm.nation"
            filterable
            placeholder="请选择"
            style="width: 100%"
          >
            <el-option
              v-for="item in dictionaryData['NATION']"
              :key="item"
              :label="item"
              :value="item"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="婚姻" prop="marriage">
          <el-select
            v-model="dataForm.marriage"
            placeholder="请选择"
            style="width: 100%"
          >
            <el-option
              v-for="item in dictionaryData['MARITAL_STATUS']"
              :key="item.code"
              :label="item.content"
              :value="item.code"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="学历" prop="education">
          <el-select
            v-model="dataForm.education"
            placeholder="请选择"
            style="width: 100%"
          >
            <el-option
              v-for="item in dictionaryData['EDUCATION']"
              :key="item.code"
              :label="item.content"
              :value="item.code"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="毕业院校" prop="school">
          <el-input v-model="dataForm.school" :maxlength="32" />
        </el-form-item>
        <el-form-item label="政治面貌" prop="politic">
          <el-select
            v-model="dataForm.politic"
            placeholder="请选择"
            style="width: 100%"
          >
            <el-option
              v-for="item in dictionaryData['POLITICAL_OUTLOOK']"
              :key="item.code"
              :label="item.content"
              :value="item.code"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="兵役情况" prop="military">
          <el-select
            v-model="dataForm.military"
            placeholder="请选择"
            style="width: 100%"
          >
            <el-option
              v-for="item in dictionaryData['MILITARY_SERVICE_STATUS']"
              :key="item.code"
              :label="item.content"
              :value="item.code"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="户籍所在地" prop="registerList">
          <el-cascader
            v-model="registerList"
            style="width: 100%"
            placeholder="省市区"
            :options="regionData"
          />
        </el-form-item>
        <el-form-item label="户籍详细地址" prop="idcardAddr">
          <el-input v-model="dataForm.idcardAddr" :maxlength="32" />
        </el-form-item>
        <el-form-item label="居住地址" prop="address">
          <el-input v-model="dataForm.address" :maxlength="32" />
        </el-form-item>
        <el-form-item label="手机号码" prop="mobile">
          <el-input
            v-model="dataForm.mobile"
            :maxlength="32"
            oninput="value=value.replace(/[^\d]/g,'')"
          />
        </el-form-item>
        <el-form-item label="微信号码" prop="wxId">
          <el-input
            v-model="dataForm.wxId"
            :maxlength="32"
            oninput="value=value.replace(/[^-_\w\d]/g,'')"
          />
        </el-form-item>
        <el-form-item label="紧急联系人姓名" prop="contactName">
          <el-input v-model="dataForm.contactName" :maxlength="32" />
        </el-form-item>
        <el-form-item label="紧急联系人电话" prop="contactMobile">
          <el-input
            v-model="dataForm.contactMobile"
            :maxlength="32"
            oninput="value=value.replace(/[^\d]/g,'')"
          />
        </el-form-item>
        <el-form-item label="紧急联系人关系" prop="contactRelationship">
          <el-select
            v-model="dataForm.contactRelationship"
            placeholder="请选择"
            style="width: 100%"
          >
            <el-option
              v-for="item in relationshipList"
              :key="item.code"
              :label="item.content"
              :value="item.code"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="担保人姓名" prop="guarantorName">
          <el-input v-model="dataForm.guarantorName" :maxlength="32" />
        </el-form-item>
        <el-form-item label="担保人电话" prop="guarantorMobile">
          <el-input
            v-model="dataForm.guarantorMobile"
            :maxlength="32"
            oninput="value=value.replace(/[^\d]/g,'')"
          />
        </el-form-item>
        <el-form-item label="照片" prop="photoUrl">
          <el-upload
            :action="uploadPath"
            :class="[dataForm.photoUrl === '' ? '' : 'disUoloadSty']"
            list-type="picture-card"
            :file-list="dialogImageList"
            :limit="1"
            :headers="headers"
            :on-success="imageUploadSuccess"
            :on-remove="imageRemove"
            :before-upload="beforeImageUpload"
            :on-preview="imageUploadPreview"
            accept=".jpg,.jpeg,.png,.gif"
          >
            <i class="el-icon-plus" />
          </el-upload>

          <el-dialog :visible.sync="dialogImageVisible">
            <img width="100%" :src="dialogImageUrl" alt="">
          </el-dialog>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false,dialogImageList = []">取消</el-button>
        <el-button
          type="primary"
          @click="newData(dialogStatus)"
        >确定</el-button>
      </div>
    </el-dialog>
    <!-- 详情 -->
    <el-dialog
      title="员工信息明细"
      :visible.sync="detailVisible"
      width="60%"
      :close-on-click-modal="false"
    >
      <!-- 其它任务信息 -->
      <el-card style="margin-bottom: 10px">
        <el-form
          label-position="left"
          inline
          class="detail-table-expand"
          :model="detailForm"
        >
          <el-form-item label="身份证号">
            <span>{{ detailForm.idno }}</span>
          </el-form-item>
          <el-form-item label="民族">
            <span>{{ detailForm.nation }}</span>
          </el-form-item>
          <el-form-item label="出生日期">
            <span>{{ formatDateTime(detailForm.birthday) }}</span>
          </el-form-item>
          <el-form-item label="婚姻状态">
            <dictionary :dic="dictionaryData['MARITAL_STATUS']" :code="detailForm.marriage" />
          </el-form-item>
          <el-form-item label="学历">
            <dictionary :dic="dictionaryData['EDUCATION']" :code="detailForm.education" />
          </el-form-item>
          <el-form-item label="毕业院校">
            <span>{{ detailForm.school }}</span>
          </el-form-item>
          <el-form-item label="编制">
            <span>{{ formatQuotas(detailForm.manningQuotas) }}</span>
          </el-form-item>
          <el-form-item label="所属公司">
            <span>{{ formatCompany(detailForm.affiliatedCompany) }}</span>
          </el-form-item>
          <el-form-item label="服务证">
            <span>{{ detailForm.serviceCertificate }}</span>
          </el-form-item>
          <el-form-item label="合同到期时间">
            <span>{{ formatDateTime(detailForm.expirationDate) }}</span>
          </el-form-item>
          <el-form-item label="政治面貌">
            <dictionary :dic="dictionaryData['POLITICAL_OUTLOOK']" :code="detailForm.politic" />
          </el-form-item>
          <el-form-item label="兵役状态">
            <dictionary :dic="dictionaryData['MILITARY_SERVICE_STATUS']" :code="detailForm.military" />
          </el-form-item>
          <el-form-item label="户籍地区">
            <span>
              {{ detailForm.idcardPrivince | regionCodeToText }}
              {{ detailForm.idcardCity | regionCodeToText }}
              {{ detailForm.idcardDistrict | regionCodeToText }}
            </span>
          </el-form-item>
          <el-form-item label="户籍地址">
            <span>{{ detailForm.idcardAddr }}</span>
          </el-form-item>
          <el-form-item label="居住地址">
            <span>{{ detailForm.address }}</span>
          </el-form-item>
          <el-form-item label="手机号码">
            <span>{{ detailForm.mobile }}</span>
          </el-form-item>
          <el-form-item label="微信号">
            <span>{{ detailForm.wechat }}</span>
          </el-form-item>
          <el-form-item label="紧急联系人姓名">
            <span>{{ detailForm.contactName }}</span>
          </el-form-item>
          <el-form-item label="紧急联系人手机">
            <span>{{ detailForm.contactMobile }}</span>
          </el-form-item>
          <el-form-item label="紧急联系人关系">
            <span :dic="relationshipList" :code="detailForm.contactRelationship" />
          </el-form-item>
          <el-form-item label="担保人姓名">
            <span>{{ detailForm.guarantorName }}</span>
          </el-form-item>
          <el-form-item label="担保人手机">
            <span>{{ detailForm.guarantorMobile }}</span>
          </el-form-item>
          <el-form-item label="离职日期">
            <span>{{ formatDateTime(detailForm.quitDate) }}</span>
          </el-form-item>
          <el-form-item label="下次家访时间">
            <span>{{ formatDateTime(detailForm.nextVisitDate) }}</span>
          </el-form-item>
          <el-form-item label="离职原因">
            <span>{{ detailForm.comments }}</span>
          </el-form-item>
          <el-form-item label="照片">
            <el-image
              v-if="detailPhoto"
              style="max-width: 160px; max-height: 160px"
              :src="detailPhoto"
              fit="cover"
              :preview-src-list="
                detailPhoto ? [detailPhoto] : []
              "
            >
              <div slot="error" class="image-slot">
                <span style="color: gray">图片获取失败</span>
              </div>
            </el-image>
            <span v-else>(未上传)</span>
          </el-form-item>
        </el-form>
      </el-card>
      <!-- 操作 -->
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailVisible = false">关闭</el-button>
      </div>
    </el-dialog>
    <!-- 离职 -->
    <el-dialog
      title="离职信息"
      :visible.sync="quitDialogFormVisible"
      :close-on-click-modal="false"
    >
      <el-form
        ref="quitForm"
        :rules="quitRules"
        :model="quitForm"
        status-icon
        label-position="left"
        label-width="100px"
        style="width: 80%;margin-left:8%"
      >
        <el-form-item label="员工工号" prop="empNo">
          <el-input v-model="empNo" :maxlength="32" disabled style="width:40%" />
        </el-form-item>
        <el-form-item label="员工姓名" prop="empName">
          <el-input v-model="empName" :maxlength="64" disabled style="width:40%" />
        </el-form-item>
        <el-form-item label="离职日期" prop="quitDate">
          <el-date-picker
            v-model="quitForm.quitDate"
            type="date"
            placeholder="选择日期"
            value-format="timestamp"
            style="width:40%"
          />
        </el-form-item>
        <el-form-item label="离职原因" prop="reasons">
          <el-input v-model="quitForm.reasons" type="textarea" :rows="2" style="width:40%" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="quitDialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="quitSubmit(empName)">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import Pagination from '@/components/Pagination'
import myTable from '@/components/Table'
import TreeSelect from '@riophae/vue-treeselect'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
import {
  listEmployee,
  detailEmployee,
  delectEmployee,
  quitEmployee,
  addEmployee,
  updateEmployee
} from '@/api/employee/employeeManage'
import { formatdate } from '@/utils/date'
import { dictionaryData, reqDictionary } from '@/api/system/dictionary'
import { jobTree, authTree } from '@/api/common/selectOption'
import { getImage, uploadPath } from '@/api/common/common'
import { getToken } from '@/utils/auth'
import { regionData, CodeToText } from 'element-china-area-data'
import Dictionary from '@/components/Dictionary'

export default {
  components: { Pagination, myTable, TreeSelect, Dictionary },
  filters: {
    regionCodeToText(code) {
      if (code === '') {
        return ''
      } else {
        return CodeToText[code]
      }
    }
  },
  data() {
    return {
      loading: null,
      list: [],
      listQuery: {
        limit: 10,
        page: 1,
        departmentId: null,
        education: null,
        empName: null,
        empNo: null,
        maxAge: null,
        minAge: null,
        nation: null,
        politic: null,
        sex: null,
        status: 0,
        jobId: null
      },
      dictionaryData,
      relationshipList: [],
      listLoading: true,
      total: 0,
      departmentTree: [],
      jobTree: [],
      // 户籍
      registerList: [],
      quotasOption: [],
      companyOption: [],
      regionData,
      role: {
        list: '/base/dictionary/list',
        add: '/base/dictionary/save'
      },
      tableList: [
        {
          label: '工号',
          prop: 'empNo',
          width: 120
        },
        {
          label: '姓名',
          prop: 'empName',
          width: 120
        },
        {
          label: '年龄',
          prop: 'age',
          width: 60
        },
        {
          label: '性别',
          prop: 'sex',
          width: 60,
          formatter: this.formatSex
        },
        {
          label: '部门',
          prop: 'departmentName'
        },
        {
          label: '岗位',
          prop: 'jobIds',
          formatter: this.formatJob
        },
        {
          label: '职务',
          prop: 'title',
          formatter: this.formatTitle
        },
        {
          label: '入职日期',
          prop: 'entryDate',
          formatter: this.formatDateTime
        },
        {
          label: '在职状态',
          prop: 'statusT',
          formatter: this.formatStatus
        }
      ],
      dialogFormVisible: false,
      quitDialogFormVisible: false,
      empNo: null,
      empName: null,
      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '创建'
      },
      dataForm: {
        address: '',
        contactMobile: '',
        contactName: '',
        contactRelationship: '',
        departmentId: null,
        education: null,
        empName: '',
        empNo: '',
        entryDate: null,
        guarantorMobile: '',
        guarantorName: '',
        id: null,
        idcardAddr: '',
        idcardCity: '',
        idcardDistrict: '',
        idcardPrivince: '',
        idno: '',
        jobIds: null,
        marriage: null,
        military: null,
        mobile: '',
        nation: '',
        photoUrl: '',
        politic: '',
        school: '',
        sex: null,
        title: '',
        wxId: '',
        manningQuotas: '',
        affiliatedCompany: '',
        serviceCertificate: '',
        expirationDate: null
      },
      jobOptions: [
        { code: 0, content: '员工' },
        { code: 1, content: '主管' }
      ],
      quitForm: {
        id: null,
        quitDate: null,
        reasons: null
      },
      rules: {
        entryDate: [
          { required: true, message: '请选择入职日期', trigger: 'blur' }
        ],
        empNo: [{ required: true, message: '请填写工号', trigger: 'blur' }],
        empName: [{ required: true, message: '请填写姓名', trigger: 'blur' }],
        sex: [{ required: true, message: '请选择性别', trigger: 'blur' }],
        birthday: [
          { required: true, message: '请选择出生日期', trigger: 'blur' }
        ],
        departmentId: [
          { required: true, message: '请选择所属部门', trigger: 'blur' }
        ],
        idno: [
          { required: true, message: '请填写身份证号码', trigger: 'blur' },
          {
            pattern: /^[1-9]\d{7}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}$|^[1-9]\d{5}[1-9]\d{3}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}([0-9]|X)$/,
            message: '身份证格式不正确',
            trigger: 'blur'
          }
        ],
        nation: [
          { required: true, message: '请填写民族信息', trigger: 'blur' }
        ],
        marriage: [
          { required: true, message: '请选择婚姻状况', trigger: 'blur' }
        ],
        education: [{ required: true, message: '请选择学历', trigger: 'blur' }],
        politic: [
          { required: true, message: '请选择政治面貌', trigger: 'blur' }
        ],
        mobile: [
          { required: true, message: '请填写手机号码', trigger: 'blur' },
          {
            pattern: /^1[3456789]\d{9}$/,
            message: '手机号码不正确',
            trigger: 'blur'
          }
        ],
        contactMobile: [
          {
            pattern: /^1[3456789]\d{9}$/,
            message: '手机号码不正确',
            trigger: 'blur'
          }
        ],
        guarantorMobile: [
          {
            pattern: /^1[3456789]\d{9}$/,
            message: '手机号码不正确',
            trigger: 'blur'
          }
        ],
        manningQuotas: [{ required: true, message: '请选择编制', trigger: 'blur' }],
        affiliatedCompany: [{ required: true, message: '请选择编制', trigger: 'blur' }],
        jobIds: [{ required: true, message: '请选择岗位', trigger: 'blur' }],
        title: [{ required: true, message: '请选择职务', trigger: 'blur' }]
      },
      quitRules: {
        quitDate: [
          { required: true, message: '请选择离职日期', trigger: 'blur' }
        ],
        reasons: [
          { required: true, message: '请填写离职原因', trigger: 'blur' }
        ]
      },
      // 上传
      uploadPath,
      headers: {
        'X-Token': getToken(),
        'X-mac': this.$store.getters.mac
      },
      dialogImageList: [],
      dialogImageVisible: false,
      dialogImageUrl: '',
      // 详情
      detailVisible: false,
      detailForm: {},
      detailPhoto: ''
    }
  },
  mounted() {
    this.getDictionary().then(() => {
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
      listEmployee(this.listQuery).then((res) => {
        const data = res.data
        this.list = data.list
        this.total = data.total
        this.listLoading = false
      })
    },
    handleCreate() {
      this.dialogStatus = 'create'
      this.dialogFormVisible = true
      this.dialogImageList = []
      this.dialogImageUrl = ''
      this.$nextTick(() => {
        this.registerList = []
        this.$refs['dataForm'].resetFields()
      })
    },
    handleQuit(row) {
      this.quitDialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['quitForm'].clearValidate()
        this.empNo = row.empNo
        this.empName = row.empName
        for (const key in this.quitForm) {
          this.quitForm[key] = row[key]
        }
      })
    },
    handleDetail(id) {
      detailEmployee(id).then((res) => {
        this.detailForm = res.data
        this.detailPhoto = ''
        this.detailVisible = true
        if (this.detailForm.photoUrl) {
          getImage(this.detailForm.photoUrl).then(res => {
            this.detailPhoto = res.data
          })
        }
      })
    },
    handleUpdate(row) {
      detailEmployee(row.id).then((res) => {
        const data = res.data
        this.dialogStatus = 'update'
        this.dialogFormVisible = true
        this.dialogImageList = []
        this.dialogImageUrl = ''
        this.$nextTick(() => {
          this.$refs['dataForm'].clearValidate()
          for (const key in row) {
            if (Object.prototype.hasOwnProperty.call(this.dataForm, key)) {
              this.dataForm[key] = row[key]
            }
          }
          for (const key in data) {
            if (Object.prototype.hasOwnProperty.call(this.dataForm, key)) {
              this.dataForm[key] = data[key]
            }
          }
          this.registerList =
            data.idcardPrivince && data.idcardCity && data.idcardDistrict
              ? [data.idcardPrivince, data.idcardCity, data.idcardDistrict]
              : []
          if (this.dataForm.photoUrl) {
            getImage(this.dataForm.photoUrl).then(res => {
              this.dialogImageList = [{ name: this.dataForm.pohotoUrl, url: res.data }]
              this.dialogImageUrl = ''
            })
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
        this.listLoading = true
        delectEmployee(row.id)
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
    quitSubmit(name) {
      this.$refs['quitForm'].validate((valid) => {
        if (valid) {
          this.$confirm(`确定将${name}设为离职状态吗?`, '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            this.openLoading()
            quitEmployee(this.quitForm)
              .then(() => {
                this.$notify.success({
                  title: '成功',
                  message: '确定成功'
                })
                this.quitDialogFormVisible = false
                this.getList()
              })
              .finally(() => {
                this.loading.close()
              })
          })
        }
      })
    },
    newData(type) {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          this.openLoading()
          this.dialogImageList = []
          this.dataForm = this.convertBeforeSend(this.dataForm)
          if (type === 'update') {
            updateEmployee(this.dataForm)
              .then(() => {
                this.getList()
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
            addEmployee(this.dataForm)
              .then(() => {
                this.getList()
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
    formatDateTime(timestamp) {
      if (!timestamp || timestamp === 0) {
        return '-'
      }
      const date = new Date(timestamp)
      return formatdate(date, 'yyyy-MM-dd')
    },
    formatSex(sex) {
      if (sex === 0) {
        return '男'
      } else {
        return '女'
      }
    },
    formatStatus(status) {
      if (status === 0) {
        return '在职'
      } else {
        return '离职'
      }
    },
    formatJob(type) {
      if (type) {
        for (const item of this.jobTree) {
          if (type === item.id) {
            return item.name
          }
        }
      }
    },
    formatTitle(title) {
      return this.jobOptions.find(item => item.code === title).content
    },
    formatQuotas(type) {
      if (type != null) {
        return this.quotasOption.find(item => item.code === type).content
      }
    },
    formatCompany(type) {
      if (type != null) {
        return this.companyOption.find(item => item.code === type).content
      }
    },
    getDictionary() {
      return new Promise((resolve, reject) => {
        Promise.all([
          authTree(),
          jobTree(),
          reqDictionary('RELATIONSHIP_TYPE'),
          reqDictionary('MANNING_QUOTAS'),
          reqDictionary('AFFILIATED_COMPANY')
        ])
          .then((res) => {
            const [res1, res2, res3, res4, res5] = res
            this.departmentTree = res1.data
            this.listQuery.departmentId = this.departmentTree[0].id
            this.jobTree = res2.data
            this.relationshipList = res3.data
            this.quotasOption = res4.data
            this.companyOption = res5.data
            resolve()
          })
          .catch((err) => {
            reject(err)
          })
      })
    },
    normalizer(node) {
      return {
        id: node.id,
        label: node.name,
        children: node.children
      }
    },
    // 文件上传前
    beforeImageUpload(file) {
      const test = /^image\/(jpeg|png|jpg|gif)$/.test(file.type)
      if (!test) {
        this.$notify.error({
          title: '图片格式错误',
          message: '只支持jpeg、png、jpg、gif格式'
        })
      }
      return test
    },
    imageUploadSuccess(res) {
      this.dataForm.photoUrl = res.data
    },
    imageUploadPreview(file) {
      this.dialogImageUrl = file.url
      this.dialogImageVisible = true
    },
    imageRemove() {
      this.dataForm.photoUrl = ''
    },
    // 转换数据
    convertBeforeSend(dataForm) {
      // 转换省市县
      const ad = this.registerList
      if (ad && ad.length > 0) {
        dataForm.idcardPrivince = ad[0] || ''
        dataForm.idcardCity = ad[1] || ''
        dataForm.idcardDistrict = ad[2] || ''
      } else {
        dataForm.idcardPrivince = ''
        dataForm.idcardCity = ''
        dataForm.idcardDistrict = ''
      }
      return dataForm
    },
    convertIdToString(ids) {
      // 转多选为字符串
      let idsString = ''
      for (const elm of ids) {
        idsString += '/' + elm
      }
      if (idsString.length > 0) {
        idsString += '/'
      }
      return idsString
    },
    convertRoleIdsToList(idsString) {
      // 转字符串为多选
      const ids = idsString.substring(1, idsString.length - 1).split('/')
      return ids.map(Number)
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
.filter-container {
  .el-input:not(:first-child) {
    margin-left: 6px;
  }
  .el-select {
    margin-left: 6px;
  }
}
.filter-container > button {
  margin-left: 10px;
}

.detail-table-expand {
  font-size: 0;
}

.detail-table-expand label {
  width: 120px;
  color: #99a9bf;
}

.detail-table-expand .el-form-item {
  margin-right: 0;
  margin-bottom: 0;
  width: 50%;
}
</style>
<style lang="scss">
.disUoloadSty .el-upload--picture-card {
  display: none; /* 上传按钮隐藏 */
}
</style>
