<template>
  <div class="app-container">
    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-select
        v-model="listQuery.departmentId"
        filterable
        placeholder="请先选择部门"
        class="filter-item"
        style="width: 200px"
        @change="departmentChange"
      >
        <el-option
          v-for="item in depOptions"
          :key="item.id"
          :label="item.name"
          :value="item.id"
        />
      </el-select>
      <el-select
        v-model="listQuery.bankId"
        placeholder="商业银行"
        style="width: 200px"
        filterable
        clearable
        class="filter-item"
      >
        <el-option
          v-for="item in options.banknode"
          :key="item.id"
          :label="item.name"
          :value="item.id"
        />
      </el-select>
      <el-select
        v-model="listQuery.denomId"
        clearable
        class="filter-item"
        style="width: 200px"
        placeholder="券别"
      >
        <el-option
          v-for="item in options['denom-0']"
          :key="item.id"
          :label="item.name"
          :value="item.id"
        />
      </el-select>
      <el-date-picker
        v-model="listQueryTmp.date"
        clearable
        class="filter-item"
        type="daterange"
        value-format="timestamp"
        start-placeholder="发现日期开始"
        range-separator="至"
        end-placeholder="发现日期结束"
      />
      <!-- <el-select
        v-model="listQuery.status"
        clearable
        class="filter-item"
        style="width: 150px"
        placeholder="审核状态"
      >
        <el-option
          v-for="item in dictionaries['CLEAR_ERROR_STATUS']"
          :key="item.code"
          :label="item.content"
          :value="item.code"
        />
      </el-select> -->
      <!-- <el-input
        v-model="listQuery.subBanknode"
        clearable
        class="filter-item"
        style="width: 200px"
        placeholder="错款支行"
      /> -->
      <el-input
        v-model="listQuery.findName"
        clearable
        class="filter-item"
        style="width: 200px"
        placeholder="发现人"
      />
      <!-- <el-input
        v-model="listQuery.clearMan"
        clearable
        class="filter-item"
        style="width: 200px"
        placeholder="发现人"
      />
      <el-input
        v-model="listQuery.checkMan"
        clearable
        class="filter-item"
        style="width: 200px"
        placeholder="复核人"
      /> -->
      <el-button
        class="filter-item"
        type="primary"
        icon="el-icon-search"
        @click="handleFilter"
      >查找</el-button>
      <el-button
        v-permission="['/base/clearError/addmulti']"
        class="filter-item"
        type="primary"
        icon="el-icon-edit"
        @click="handleCreate"
      >添加</el-button>
      <!-- <el-button
        v-permission="['POST /clearError/confirm/multi']"
        class="filter-item"
        icon="el-icon-check"
        :disabled="disableChecked"
        :type="disableChecked ? 'info' : 'success'"
        @click="handleCheck"
      >确认</el-button> -->
      <!-- <el-button
        v-permission="['POST /clearError/download/error']"
        class="filter-item"
        type="primary"
        icon="el-icon-download"
        @click="exportExcel"
      >导出
      </el-button> -->
      <a id="download-a" />
    </div>

    <!-- 查询结果 -->
    <el-table
      v-loading="listLoading"
      :data="list"
      element-loading-text="正在查询中。。。"
      border
      fit
      highlight-current-row
      :header-cell-style="{ 'background-color': '#f5f5f5' }"
      @selection-change="handleSelectionChange"
    >
      <!-- 字段 -->
      <el-table-column align="center" type="selection" width="50" />
      <!--审核状态-->
      <el-table-column align="center" label="审核状态" width="100">
        <template slot-scope="scope">
          {{ dictionaries['CLEAR_ERROR_STATUS'].find(item => item.code === scope.row.status).content }}
          <!-- <dictionary
            :dic="dictionaries['CLEAR_ERROR_STATUS']"
            :code="scope.row.status"
            :style="{ color: statusColor(scope.row.status) }"
          /> -->
        </template>
      </el-table-column>
      <el-table-column
        align="center"
        label="商业银行"
        prop="banknodeName"
        width="150"
      />
      <el-table-column align="center" label="发现日期" width="100">
        <template slot-scope="scope">
          <span>{{ formatDateT(scope.row.clearDate) }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="封签日期" width="100">
        <template slot-scope="scope">
          <span>{{ scope.row.sealDate }}</span>
        </template>
      </el-table-column>
      <el-table-column
        align="center"
        label="券别"
        prop="denomName"
        width="150"
      />
      <el-table-column
        align="center"
        label="错款支行"
        prop="subBank"
        width="150"
      />
      <el-table-column
        align="center"
        label="错款人"
        prop="errorMan"
        width="100"
      />
      <el-table-column
        align="center"
        label="封签人"
        prop="sealMan"
        width="100"
      />
      <el-table-column align="center" label="错款性质">
        <el-table-column align="center" label="长款">
          <el-table-column
            align="center"
            label="笔数"
            prop="cashOverCount"
            width="50"
          />
          <el-table-column
            align="center"
            label="金额"
            :formatter="fmtAmount"
            prop="cashOverAmount"
            width="100"
          />
        </el-table-column>
        <el-table-column align="center" label="短款">
          <el-table-column
            align="center"
            label="笔数"
            prop="cashShortCount"
            width="50"
          />
          <el-table-column
            align="center"
            label="金额"
            :formatter="fmtAmount"
            prop="cashShortAmount"
            width="100"
          />
        </el-table-column>
        <el-table-column align="center" label="假币">
          <el-table-column
            align="center"
            label="笔数"
            prop="fakeCount"
            width="50"
          />
          <el-table-column
            align="center"
            label="金额"
            :formatter="fmtAmount"
            prop="fakeAmount"
            width="100"
          />
        </el-table-column>
        <el-table-column align="center" label="夹张/异币">
          <el-table-column
            align="center"
            label="笔数"
            prop="carryCount"
            width="50"
          />
          <el-table-column
            align="center"
            label="金额"
            :formatter="fmtAmount"
            prop="carryAmount"
            width="100"
          />
        </el-table-column>
      </el-table-column>
      <el-table-column align="center" label="合计">
        <el-table-column align="center" label="笔数" prop="param" width="50">
          <template slot-scope="scope">
            <span>{{
              scope.row.cashOverCount +
                scope.row.cashShortCount +
                scope.row.fakeCount +
                scope.row.carryCount
            }}</span>
          </template>
        </el-table-column>
        <el-table-column align="center" label="金额" prop="param" width="100">
          <template slot-scope="scope">
            <span>{{
              (scope.row.cashOverAmount +
                scope.row.cashShortAmount +
                scope.row.fakeAmount +
                scope.row.carryAmount)
                | formatAmount
            }}</span>
          </template>
        </el-table-column>
      </el-table-column>
      <el-table-column
        align="center"
        label="发现人"
        prop="clearMan"
        width="100"
      />
      <el-table-column
        align="center"
        label="复核人"
        prop="checkMan"
        width="100"
      />
      <el-table-column
        align="center"
        label="备注"
        prop="comments"
        width="200"
      />
      <!--操作 -->
      <el-table-column
        align="center"
        label="操作"
        width="160"
        class-name="small-padding fixed-width"
      >
        <template slot-scope="scope">
          <el-button
            v-permission="['/base/clearError/update']"
            size="mini"
            :disabled="scope.row.status !== 0 && scope.row.status !== 1"
            :type="
              scope.row.status !== 0 && scope.row.status !== 1
                ? 'info'
                : 'primary'
            "
            @click="handleUpdate(scope.row)"
          >编辑</el-button>
          <el-button
            v-permission="['/base/clearError/del']"
            size="mini"
            :disabled="scope.row.status !== 0 && scope.row.status !== 1"
            :type="
              scope.row.status !== 0 && scope.row.status !== 1
                ? 'info'
                : 'danger'
            "
            @click="handleDelete(scope.row)"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页控件 -->
    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="listQuery.page"
      :limit.sync="listQuery.limit"
      @pagination="getList"
    />

    <!-- 添加对话框 -->
    <el-dialog
      title="差错记录"
      :visible.sync="dialogFormVisible"
      :close-on-click-modal="false"
      width="100%"
    >
      <!--公共属性-->
      <div class="filter-container">
        <span style="font-weight: bold">差错银行：</span>
        <el-select
          v-model="dataForm.bankId"
          placeholder="商业银行"
          style="width: 200px"
          filterable
        >
          <el-option
            v-for="item in options.banknode"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </div>
      <!--列表-->
      <el-table
        :data="dataForm.dataList"
        border
        fit
        :row-style="{ height: '20px' }"
        :cell-style="{ padding: '0px' }"
        cell-class-name="no-padding-cell"
        style="font-size: 10px"
        :header-cell-style="{ 'background-color': '#f5f5f5' }"
      >
        <!-- 字段 -->
        <el-table-column align="center" width="65">
          <template slot="header">
            <el-button
              plain
              type="primary"
              icon="el-icon-circle-plus-outline"
              size="mini"
              @click="appendRecordRow(dataForm.dataList)"
            />
          </template>
          <template slot-scope="scope">
            <el-button
              plain
              type="danger"
              icon="el-icon-remove-outline"
              size="mini"
              @click="removeRecordRow(dataForm.dataList, scope.$index)"
            />
          </template>
        </el-table-column>
        <el-table-column align="center" label="发现日期" width="130">
          <template slot-scope="scope">
            <el-date-picker
              v-model="scope.row.clearDate"
              style="width: 100%"
              class="input-nb"
              value-format="timestamp"
              type="date"
              placeholder="发现日期"
            />
          </template>
        </el-table-column>
        <el-table-column align="center" label="封签日期" width="130">
          <template slot-scope="scope">
            <el-input
              v-model="scope.row.sealDate"
              class="input-nb"
              placeholder="封签日期"
              oninput="value=value.replace(/[^-\*\d]/g,'')"
            />
            <!--<el-date-picker style="width: 100%" class="input-nb"
                            value-format="timestamp"
                            v-model="scope.row.sealDate"
                            type="date"
                            placeholder="封签日期">
            </el-date-picker>-->
          </template>
        </el-table-column>
        <el-table-column align="center" label="券别" width="140">
          <template slot-scope="scope">
            <el-select
              v-model="scope.row.denomId"
              placeholder="券别"
              style="width: 100%"
              class="input-nb"
            >
              <el-option
                v-for="item in options['denom-0']"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column align="center" label="错款支行" width="120">
          <template slot-scope="scope">
            <el-autocomplete
              v-model="scope.row.subBank"
              class="input-nb"
              :fetch-suggestions="queryBankNode"
              placeholder="错款支行"
            />
          </template>
        </el-table-column>
        <el-table-column align="center" label="错款人" width="70">
          <template slot-scope="scope">
            <el-input
              v-model="scope.row.errorMan"
              class="input-nb"
              placeholder="错款人"
            />
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          label="封签人"
          prop="sealMan"
          width="70"
        >
          <template slot-scope="scope">
            <el-input
              v-model="scope.row.sealMan"
              class="input-nb"
              placeholder="封签人"
            />
          </template>
        </el-table-column>
        <el-table-column align="center" label="错款性质">
          <el-table-column align="center" label="长款">
            <el-table-column
              align="center"
              label="笔数"
              prop="cashOverCount"
              width="50"
            >
              <template slot-scope="scope">
                <el-input
                  v-model="scope.row.cashOverCount"
                  class="input-nb"
                  placeholder="长"
                  oninput="value=value.replace(/[^\d]/g,'')"
                />
              </template>
            </el-table-column>
            <el-table-column
              align="center"
              label="金额"
              prop="cashOverAmount"
              width="70"
            >
              <template slot-scope="scope">
                <el-input
                  v-model="scope.row.cashOverAmount"
                  class="input-nb"
                  placeholder="金额"
                  oninput="value=value.replace(/[^-\.\d]/g,'')"
                />
              </template>
            </el-table-column>
          </el-table-column>
          <el-table-column align="center" label="短款">
            <el-table-column
              align="center"
              label="笔数"
              prop="cashShortCount"
              width="50"
            >
              <template slot-scope="scope">
                <el-input
                  v-model="scope.row.cashShortCount"
                  class="input-nb"
                  placeholder="短"
                  oninput="value=value.replace(/[^\d]/g,'')"
                />
              </template>
            </el-table-column>
            <el-table-column
              align="center"
              label="金额"
              prop="cashShortAmount"
              width="70"
            >
              <template slot-scope="scope">
                <el-input
                  v-model="scope.row.cashShortAmount"
                  class="input-nb"
                  placeholder="金额"
                  oninput="value=value.replace(/[^-\.\d]/g,'')"
                />
              </template>
            </el-table-column>
          </el-table-column>
          <el-table-column align="center" label="假币">
            <el-table-column
              align="center"
              label="笔数"
              prop="fakeCount"
              width="50"
            >
              <template slot-scope="scope">
                <el-input
                  v-model="scope.row.fakeCount"
                  class="input-nb"
                  placeholder="假"
                  oninput="value=value.replace(/[^\d]/g,'')"
                />
              </template>
            </el-table-column>
            <el-table-column
              align="center"
              label="金额"
              prop="fakeAmount"
              width="70"
            >
              <template slot-scope="scope">
                <el-input
                  v-model="scope.row.fakeAmount"
                  class="input-nb"
                  placeholder="金额"
                  oninput="value=value.replace(/[^-\.\d]/g,'')"
                />
              </template>
            </el-table-column>
          </el-table-column>
          <el-table-column align="center" label="夹张/异币">
            <el-table-column
              align="center"
              label="笔数"
              prop="carryCount"
              width="50"
            >
              <template slot-scope="scope">
                <el-input
                  v-model="scope.row.carryCount"
                  class="input-nb"
                  placeholder="夹"
                  oninput="value=value.replace(/[^\d]/g,'')"
                />
              </template>
            </el-table-column>
            <el-table-column
              align="center"
              label="金额"
              prop="carryAmount"
              width="70"
            >
              <template slot-scope="scope">
                <el-input
                  v-model="scope.row.carryAmount"
                  class="input-nb"
                  placeholder="金额"
                  oninput="value=value.replace(/[^-\.\d]/g,'')"
                />
              </template>
            </el-table-column>
          </el-table-column>
        </el-table-column>
        <el-table-column align="center" label="合计">
          <el-table-column align="center" label="笔数" prop="param" width="50">
            <template slot-scope="scope">
              <span>{{ scope.row | errorCount }}</span>
            </template>
          </el-table-column>
          <el-table-column align="center" label="金额" prop="param" width="70">
            <template slot-scope="scope">
              <span>{{ scope.row | errorSum }}</span>
            </template>
          </el-table-column>
        </el-table-column>
        <el-table-column
          align="center"
          label="发现人"
          prop="clearMan"
          width="70"
        >
          <template slot-scope="scope">
            <el-input
              v-model="scope.row.clearMan"
              class="input-nb"
              placeholder="发现人"
            />
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          label="复核人"
          prop="checkMan"
          width="70"
        >
          <template slot-scope="scope">
            <el-input
              v-model="scope.row.checkMan"
              class="input-nb"
              placeholder="复核人"
            />
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          label="备注"
          prop="comments"
          width="150"
        >
          <template slot-scope="scope">
            <el-input
              v-model="scope.row.comments"
              class="input-nb"
              placeholder="备注"
            />
          </template>
        </el-table-column>
      </el-table>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="createData">确定</el-button>
        <el-button @click="dialogFormVisible = false">取消</el-button>
      </div>
    </el-dialog>
    <!-- 添加对话框 END -->

    <!-- 编辑对话框 -->
    <el-dialog
      title="差错记录"
      :visible.sync="editFormVisible"
      :close-on-click-modal="false"
    >
      <el-form
        ref="editForm"
        :rules="rules"
        status-icon
        label-position="right"
        :model="editForm"
        label-width="130px"
        :hide-required-asterisk="true"
        style="width: 400px; margin-left: 50px"
      >
        <!-- 表单项 -->
        <el-form-item label="商业银行" prop="bankId">
          <el-select
            v-model="editForm.bankId"
            placeholder="商业银行"
            style="width: 100%"
            filterable
            disabled
          >
            <el-option
              v-for="item in options.banknode"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="发现日期" prop="clearDate">
          <el-date-picker
            v-model="editForm.clearDate"
            style="width: 100%"
            value-format="timestamp"
            type="date"
            placeholder="选择日期"
          />
        </el-form-item>
        <el-form-item label="封签日期" prop="sealDate">
          <el-input
            v-model="editForm.sealDate"
            oninput="value=value.replace(/[^-\*\d]/g,'')"
          />
          <!--<el-date-picker style="width: 100%"
                          value-format="timestamp"
                          v-model="editForm.sealDate"
                          type="date"
                          placeholder="选择日期">
          </el-date-picker>-->
        </el-form-item>
        <el-form-item label="网点" prop="subBanknode">
          <el-input
            v-model="editForm.subBanknode"
          />
        </el-form-item>
        <el-form-item label="错款人" prop="errorMan">
          <el-input
            v-model="editForm.errorMan"
          />
        </el-form-item>
        <el-form-item label="封签人" prop="sealMan">
          <el-input
            v-model="editForm.sealMan"
          />
        </el-form-item>
        <el-form-item label="面额" prop="denomId">
          <el-select
            v-model="editForm.denomId"
            filterable
            clearable
            placeholder="请选择"
            style="width: 100%"
          >
            <el-option
              v-for="item in options['denom-0']"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="长款" prop="cashOverCount">
          <el-input
            v-model="editForm.cashOverCount"
            style="width: 30%"
            placeholder="笔数"
            oninput="value=value.replace(/[^\d]/g,'')"
          />
          <el-input
            v-model="editForm.cashOverAmount"
            style="width: 68%"
            placeholder="金额"
            oninput="value=value.replace(/[^-\.\d]/g,'')"
          />
        </el-form-item>
        <el-form-item label="短款" prop="cashOverCount">
          <el-input
            v-model="editForm.cashShortCount"
            style="width: 30%"
            placeholder="笔数"
            oninput="value=value.replace(/[^\d]/g,'')"
          />
          <el-input
            v-model="editForm.cashShortAmount"
            style="width: 68%"
            placeholder="金额"
            oninput="value=value.replace(/[^-\.\d]/g,'')"
          />
        </el-form-item>
        <el-form-item label="假钞" prop="cashOverCount">
          <el-input
            v-model="editForm.fakeCount"
            style="width: 30%"
            placeholder="笔数"
            oninput="value=value.replace(/[^\d]/g,'')"
          />
          <el-input
            v-model="editForm.fakeAmount"
            style="width: 68%"
            placeholder="金额"
            oninput="value=value.replace(/[^-\.\d]/g,'')"
          />
        </el-form-item>
        <el-form-item label="夹张/异币" prop="cashOverCount">
          <el-input
            v-model="editForm.carryCount"
            style="width: 30%"
            placeholder="笔数"
            oninput="value=value.replace(/[^\d]/g,'')"
          />
          <el-input
            v-model="editForm.carryAmount"
            style="width: 68%"
            placeholder="金额"
            oninput="value=value.replace(/[^-\.\d]/g,'')"
          />
        </el-form-item>
        <el-form-item label="发现人" prop="clearMan">
          <el-input
            v-model="editForm.clearMan"
          />
        </el-form-item>
        <el-form-item label="复核人" prop="checkMan">
          <el-input
            v-model="editForm.checkMan"
          />
        </el-form-item>
        <el-form-item label="备注" prop="comments">
          <el-input
            v-model="editForm.comments"
            type="textarea"
            :row="2"
          />
        </el-form-item>
        <!-- 表单项 END -->
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="updateData">确定</el-button>
        <el-button @click="editFormVisible = false">取消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  deleteClearError,
  addClearError,
  updateClearError,
  // confirm,
  listClearError
} from '@/api/cleanCheck/cleanError'
import { authOption, denomOption as reqDenom, bankTradeOption } from '@/api/common/selectOption'
import Pagination from '@/components/Pagination'
// import Dictionary from '@/components/Dictionary/index'
import { formatMoney } from '@/utils/convert'
import { MessageBox } from 'element-ui'
import { formatdate } from '@/utils/date'
// import { downLoadFile } from '@/utils/fileRequstClear'
// import fileTools from '@/utils/fileTools'

export default {
  name: 'ClearError',
  components: { Pagination },
  filters: {
    formatDate(timestamp) {
      const m = new Date(timestamp)
      return formatdate(m, 'yyyy-MM-dd hh:mm:ss')
    },
    formatDateTime(timestamp) {
      return formatdate(timestamp, 'yyyy-MM-dd hh:mm:ss')
    },
    // 格式化数量或金额
    formatAmount(amount) {
      return formatMoney(amount)
    },
    errorCount(row) {
      const count =
        parseInt(row.cashOverCount || 0) +
        parseInt(row.cashShortCount || 0) +
        parseInt(row.fakeCount || 0) +
        parseInt(row.carryCount || 0)
      return count
    },
    // 差错金额小计
    errorSum(row) {
      const sum =
        parseFloat(row.cashOverAmount || 0) +
        parseFloat(row.cashShortAmount || 0) +
        parseFloat(row.fakeAmount || 0) +
        parseFloat(row.carryAmount || 0)
      return sum.toFixed(2)
    }
  },
  data() {
    return {
      depOptions: [],
      list: [],
      total: 0,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 10,
        departmentId: null,
        denomId: null,
        findName: null,
        dateBegin: null,
        dateEnd: null
      },
      listQueryTmp: {
        date: []
      },
      dataForm: {},
      editForm: {},
      dialogFormVisible: false,
      editFormVisible: false,
      downloadLoading: false,
      textMap: {
        update: '编辑',
        create: '创建'
      },
      // 添加或编辑规则
      rules: {
        taskName: [{ required: true, message: '请选择任务', trigger: 'blur' }],
        denomName: [{ required: true, message: '不能为空', trigger: 'blur' }]
      },
      // 选择器数据
      options: {
        banknode: []
      },
      // 数据字典
      dictionaries: {},
      // 已选中行
      multipleSelection: []
    }
  },
  computed: {
    disableChecked() {
      let flag = false
      if (!this.multipleSelection || this.multipleSelection.length < 1) {
        return true
      }
      this.multipleSelection.forEach((value) => {
        if (value.status !== 0 && value.status !== 1) {
          flag = true
          return false
        }
      })
      return flag
    }
  },
  async created() {
    await authOption().then((res) => {
      this.depOptions = res.data
      if (this.depOptions.length > 0) {
        this.listQuery.departmentId = this.depOptions[0].id
        this.getList()
        bankTradeOption(this.listQuery.departmentId, 3).then((res) => {
          this.options.banknode = res.data
        })
      }
    })
    this.loadSelectOptions()
    this.loadDictionary().then(this.getList)
  },
  methods: {
    // 加载列表
    getList() {
      // 处理时间段
      this.listQuery.dateBegin =
        (this.listQueryTmp.date && this.listQueryTmp.date[0]) || undefined
      this.listQuery.dateEnd =
        (this.listQueryTmp.date && this.listQueryTmp.date[1]) || undefined

      this.listLoading = true
      listClearError(this.listQuery)
        .then((res) => {
          this.list = res.data.list
          this.total = res.data.total
          this.listLoading = false
        })
        .catch(() => {
          this.list = []
          this.total = 0
          this.listLoading = false
        })
    },
    // 加载选项
    loadSelectOptions() {
      // 面额（含版别）
      reqDenom()
        .then((res) => {
          this.options['denom-0'] = res.data.filter(item => item.version === 0)
        })
        .catch((reason) => {
          this.options['denom-0'] = []
        })
    },
    // 加载数据字典
    loadDictionary() {
      return new Promise((resolve, reject) => {
        this.dictionaries['CLEAR_ERROR_STATUS'] = [
          { code: 0, content: '待确认' },
          { code: 2, content: '已确认' },
          { code: 1, content: '审核拒绝' },
          { code: 3, content: '审核通过' },
          { code: 4, content: '撤销中' },
          { code: 5, content: '已撤销' }
        ]
        this.dictionaries['CURRENCY_COIN_PAPER'] = [
          { code: 'P', content: '纸币' },
          { code: 'C', content: '硬币' }
        ]
        resolve()
      })
    },
    formatDateT(timestamp) {
      const m = new Date(timestamp)
      return formatdate(m, 'yyyy-MM-dd')
    },
    handleFilter() {
      this.listQuery.page = 1
      this.getList()
    },
    handleCreate(row) {
      this.dataForm = {
        dataList: []
      }
      for (let i = 0; i < 5; i++) {
        this.appendRecordRow(this.dataForm.dataList)
      }
      this.dialogFormVisible = true
    },
    handleUpdate(row) {
      this.editForm = Object.assign({}, row)
      this.editFormVisible = true
      this.$nextTick(() => {
        this.$refs['editForm'].clearValidate()
      })
    },
    handleDelete(row) {
      this.$confirm('确定删除吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteClearError(row)
          .then((response) => {
            this.$notify.success({
              title: '成功',
              message: '删除成功'
            })
            this.getList()
          })
          .catch((response) => {
            this.$notify.error({
              title: '失败',
              message: response.data.errmsg
            })
          })
      })
    },
    createData() {
      const bankId = this.dataForm.bankId
      if (!bankId) {
        MessageBox.alert('请选择商业银行', '失败', {
          confirmButtonText: '确定',
          type: 'warning'
        })
        return
      }
      const dataList = []
      this.dataForm.dataList.forEach((value) => {
        if (
          value.clearDate &&
          value.denomId &&
          (value.carryCount ||
            value.carryAmount ||
            value.cashOverCount ||
            value.cashOverAmount ||
            value.cashShortCount ||
            value.cashShortAmount ||
            value.fakeCount ||
            value.fakeAmount)
        ) {
          value.bankId = bankId
          value.departmentId = this.listQuery.departmentId
          dataList.push(value)
        }
      })
      if (dataList.length < 1) {
        MessageBox.alert('请至少填写一条有效数据', '失败', {
          confirmButtonText: '确定',
          type: 'warning'
        })
        return
      }
      this.openLoading()
      addClearError(dataList)
        .then((response) => {
          this.getList()
          this.dialogFormVisible = false
          this.$notify.success({
            title: '成功',
            message: '添加成功'
          })
        })
        .catch((response) => {
          this.$notify.error({
            title: '失败',
            message: response.data.errmsg
          })
        })
        .finally(() => {
          this.loading.close()
        })
    },
    updateData() {
      this.$refs['editForm'].validate((valid) => {
        if (valid) {
          updateClearError(this.editForm)
            .then(() => {
              this.getList()
              this.editFormVisible = false
              this.$notify.success({
                title: '成功',
                message: '更新成功'
              })
            })
            .catch((response) => {
              this.$notify.error({
                title: '失败',
                message: response.data.errmsg
              })
            })
        }
      })
    },
    fmtAmount(row, column, cellValue, index) {
      if (!cellValue || cellValue === 0) {
        return '-'
      } else {
        return cellValue.toLocaleString()
      }
    },
    // 增加一行
    appendRecordRow(list) {
      list.push({
        clearDate: new Date().getTime()
      })
    },
    // 移除一行
    removeRecordRow(list, index) {
      list.splice(index, 1)
    },
    // 表格已选项更变
    handleSelectionChange(val) {
      this.multipleSelection = val
    },
    // 筛选小网点
    queryBankNode(queryString, cb) {
      const bankNodes = this.options.littleBank || []
      const results = queryString
        ? bankNodes.filter(this.createFilter(queryString))
        : bankNodes
      // 调用 callback 返回建议列表的数据
      cb(results)
    },
    createFilter(queryString) {
      return (bankNode) => {
        return (
          bankNode.fullName.toLowerCase().indexOf(queryString.toLowerCase()) ===
          0
        )
      }
    },
    // 状态字体颜色标示
    statusColor(status) {
      return (
        {
          0: 'blue',
          1: 'red',
          3: 'green',
          4: 'red',
          5: 'gray'
        }[status] || ''
      )
    },
    // 检查权限
    // 添加时等待滚动条
    openLoading() {
      this.loading = this.$loading({
        lock: true,
        text: '正在提交...请勿进行其它操作',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      })
    },
    departmentChange(value) {
      bankTradeOption(value, 3).then((res) => {
        console.log(res.data)
        this.options.banknode = res.data
        this.listQuery.bankId = null
      })
      // 更新查询线路
      // this.getClearManOption()
      // this.updateRoute()
    }
  }
}
</script>

<style>

.filter-container *:nth-child(n + 2) {
  margin-left: 10px;
}
.demo-table-expand {
  font-size: 0;
}

.demo-table-expand label {
  width: 120px;
  color: #99a9bf;
}

.demo-table-expand .el-form-item {
  margin-right: 0;
  margin-bottom: 0;
  width: 50%;
}

.input-nb input {
  border: none;
  text-align: center;
  font-size: 10px;
}

.no-padding-cell .cell {
  padding: 0px !important;
}
</style>
