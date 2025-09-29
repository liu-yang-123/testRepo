<template>
  <div class="app-container">
    <el-tabs v-model="activeName" @tab-click="tabClick">
      <el-tab-pane label="清分任务" name="first">
        <div class="filter-container">
          <el-select
            v-model="listQuery.departmentId"
            filterable
            placeholder="请先选择部门"
            class="filter-item"
            style="width: 150px"
            @change="departmentChange"
          >
            <el-option
              v-for="item in depOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
          <el-date-picker
            v-model="listQuery.taskDate"
            value-format="yyyy-MM-dd"
            type="date"
            placeholder="选择日期"
            :clearable="true"
            style="width: 150px"
            class="filter-item"
            @change="updateRoute"
          />
          <el-select
            v-model="listQuery.bankId"
            clearable
            filterable
            placeholder="请先选择银行"
            class="filter-item"
            style="width: 150px"
          >
            <el-option
              v-for="item in bankSearchOption"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
          <el-select
            v-model="listQuery.routeId"
            filterable
            clearable
            placeholder="请选择线路"
            class="filter-item"
            style="width: 150px"
          >
            <el-option
              v-for="item in routeSearchOption"
              :key="item.value"
              :label="item.routeName + '/' + item.routeNo"
              :value="item.value"
            />
          </el-select>
          <el-select
            v-model="listQuery.errorType"
            clearable
            filterable
            placeholder="请选择差错类型"
            class="filter-item"
            style="width: 150px"
            @change="listQuery.statusT = null"
          >
            <el-option
              v-for="item in options.errorType"
              :key="item.code"
              :label="item.content"
              :value="item.code"
            />
          </el-select>
          <el-select
            v-model="listQuery.statusT"
            :disabled="listQuery.errorType !== null && listQuery.errorType !== ''"
            clearable
            filterable
            placeholder="请选择清分状态"
            class="filter-item"
            style="width: 150px"
          >
            <el-option
              v-for="item in options.statusT"
              :key="item.code"
              :label="item.content"
              :value="item.code"
            />
          </el-select>
          <el-select
            v-model="listQuery.clearManId"
            clearable
            filterable
            placeholder="请选择清点人"
            class="filter-item"
            style="width: 150px"
          >
            <el-option
              v-for="item in options.clearManId"
              :key="item.id"
              :label="`${item.empNo}/${item.empName}`"
              :value="item.id"
            />
          </el-select>
          <el-button
            class="filter-item"
            type="primary"
            icon="el-icon-search"
            @click="listQuery.page = 1;getList()"
          >查找</el-button>
          <el-button
            v-permission="['/base/clearTask/save']"
            class="filter-item"
            type="primary"
            icon="el-icon-edit"
            @click="handleCreate"
          >添加</el-button>
          <el-button
            v-permission="['/base/clearTask/importClearTask']"
            class="filter-item"
            type="success"
            icon="el-icon-upload"
            @click="handleExportDialog"
          >导入清分任务</el-button>
        </div>
        <my-table
          :list-loading="listLoading"
          :data-list="list"
          :table-list="tableList"
        >
          <template v-slot:operate>
            <el-table-column
              align="left"
              label="操作"
              class-name="small-padding fixed-width"
              width="300"
              header-align="center"
            >
              <template slot-scope="scope">
                <el-button
                  plain
                  size="mini"
                  @click="handleDetail(scope.row)"
                >详情</el-button>
                <el-button
                  v-permission="['/base/clearTask/update']"
                  type="primary"
                  size="mini"
                  @click="handleUpdate(scope.row)"
                >编辑</el-button>
                <el-button
                  v-if="scope.row.statusT != 1"
                  v-permission="['/base/clearTask/delete']"
                  type="danger"
                  size="mini"
                  @click="handleDelete(scope.row)"
                >删除</el-button>
                <el-button
                  v-if="scope.row.statusT == 0"
                  v-permission="['/base/clearTask/finish']"
                  type="success"
                  size="mini"
                  @click="handleFinish(scope.row)"
                >完成</el-button>
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
      </el-tab-pane>

      <el-tab-pane v-permission="['/base/clearTaskAudit/list']" label="清分审批任务" name="second">
        <div class="filter-container">
          <el-select
            v-model="auditListQuery.departmentId"
            filterable
            placeholder="请先选择部门"
            class="filter-item"
            style="width: 150px"
            @change="departmentAuditChange"
          >
            <el-option
              v-for="item in depOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
          <el-date-picker
            v-model="auditListQuery.taskDate"
            value-format="yyyy-MM-dd"
            type="date"
            placeholder="选择任务日期"
            :clearable="true"
            style="width: 150px"
            class="filter-item"
            @change="updateAuditRoute"
          />
          <el-select
            v-model="auditListQuery.bankId"
            clearable
            filterable
            placeholder="请先选择银行"
            class="filter-item"
            style="width: 150px"
          >
            <el-option
              v-for="item in bankAuditSearchOption"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
          <el-select
            v-model="auditListQuery.routeId"
            filterable
            clearable
            placeholder="请选择线路"
            class="filter-item"
            style="width: 150px"
          >
            <el-option
              v-for="item in routeAuditSearchOption"
              :key="item.value"
              :label="item.routeName + '/' + item.routeNo"
              :value="item.value"
            />
          </el-select>
          <el-select
            v-model="auditListQuery.statusT"
            clearable
            filterable
            placeholder="请先选审核状态"
            class="filter-item"
            style="width: 150px"
          >
            <el-option
              v-for="item in options.auditStatusT"
              :key="item.code"
              :label="item.content"
              :value="item.code"
            />
          </el-select>
          <el-button
            class="filter-item"
            type="primary"
            icon="el-icon-search"
            @click="auditListQuery.page = 1;getAuditList()"
          >查找</el-button>
        </div>
        <my-table
          :list-loading="listLoading"
          :data-list="auditList"
          :table-list="auditTableList"
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
                  plain
                  size="mini"
                  @click="handleTaskDetail(scope.row)"
                >详情</el-button>
                <el-button
                  v-if="scope.row.statusT == 1 && scope.row.audit"
                  v-permission="['/base/clearTaskAudit/audit']"
                  type="primary"
                  size="mini"
                  @click="handleAudit(scope.row)"
                >审核</el-button>
                <el-button
                  v-permission="['/base/clearTaskAudit/auditHistory']"
                  type="success"
                  size="mini"
                  @click="handleAuditHistory(scope.row)"
                >审核记录</el-button>
              </template>
            </el-table-column>
          </template>
        </my-table>
        <pagination
          v-show="auditTotal > 0"
          :total="auditTotal"
          :page.sync="auditListQuery.page"
          :limit.sync="auditListQuery.limit"
          @pagination="getAuditList"
        />
      </el-tab-pane>
    </el-tabs>

    <!-- 添加 -->
    <el-dialog
      title="添加清分任务"
      :visible.sync="dialogFormCreateVisible"
      :close-on-click-modal="false"
    >
      <el-form
        ref="addDataForm"
        :rules="addRules"
        :model="addDataForm"
        status-icon
        label-position="right"
        label-width="80px"
        style="width: 700px"
      >
        <el-form-item label="所属部门" prop="departmentId">
          <el-select
            v-model="addDataForm.departmentId"
            placeholder="请选择所属部门"
            style="width: 300px"
            @change="changeDepartment"
          >
            <el-option
              v-for="item in depOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="线路编号" prop="routeId">
          <el-select
            v-model="addDataForm.routeId"
            filterable
            placeholder="请先选择线路"
            class="filter-item"
            style="width: 300px"
            @change="changeRoute"
          >
            <el-option
              v-for="item in routeOption"
              :key="item.value"
              :label="item.routeName + ' / ' + item.routeNo"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="ATM清分" prop="atmList">
          <el-table
            :data="atmList"
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
            <el-table-column align="center" label="ATM设备" width="220">
              <template slot-scope="scope">
                <el-select
                  v-model="scope.row.atmId"
                  filterable
                  placeholder="请选择ATM设备"
                  style="width: 100%"
                >
                  <el-option
                    v-for="item in atmOption"
                    :key="item.id"
                    :label="item.terNo"
                    :value="item.id"
                  />
                </el-select>
              </template>
            </el-table-column>
            <el-table-column align="center" label="计划清分(元)">
              <template slot-scope="scope">
                <el-input-number
                  v-model="scope.row.planAmount"
                  :precision="2"
                  :step="0.1"
                  :min="0"
                  clearable
                  class="filter-item"
                  style="width: 250px"
                  placeholder="请输入计划清分金额"
                  :maxlength="32"
                />
              </template>
            </el-table-column>
          </el-table>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormCreateVisible = false">取消</el-button>
        <el-button type="primary" @click="newData()">确定</el-button>
      </div>
    </el-dialog>
    <!-- 编辑 -->
    <el-dialog
      :title="textMap[dialogStatus]"
      :visible.sync="dialogFormVisible"
      :close-on-click-modal="false"
      width="80%"
    >
      <el-form
        ref="dataForm"
        :rules="rules"
        :model="dataForm"
        status-icon
        label-position="right"
        label-width="100px"
        style="width: 80%; margin-left: 8%"
      >
        <el-form-item label="计划清分" prop="planAmount">
          <el-input
            v-if="statusT"
            v-model="dataForm.planAmount"
            :maxlength="64"
            style="width: 20%"
          />
          <span v-else>{{ dataForm.planAmount }}</span>
        </el-form-item>
        <el-form-item v-if="!statusT" label="实际清分" prop="clearAmount">
          <el-input
            v-model="dataForm.clearAmount"
            :maxlength="64"
            style="width: 20%"
            oninput="value=value.match(/\d+\.?\d{0,2}/)"
            @focus="dataForm.clearAmount === '0.00' ? dataForm.clearAmount = '' : false"
            @blur="dataForm.clearAmount= +$event.target.value"
          />
        </el-form-item>
        <el-form-item v-if="!statusT" label="差错金额" prop="errorAmount">
          <span>{{ dataForm.clearAmount - dataForm.planAmount }}</span>
        </el-form-item>
        <template v-if="dialogStatus === 'finish'">
          <el-form-item label="清分员" prop="clearMan">
            <el-select
              v-model="dataForm.clearMan"
              filterable
              placeholder="请选择清分员"
              style="width: 20%"
            >
              <el-option
                v-for="item in options.clearManId"
                :key="item.id"
                :label="`${item.empNo}/${item.empName}`"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="复核员" prop="checkMan">
            <el-select
              v-model="dataForm.checkMan"
              filterable
              placeholder="请选择复核员"
              style="width: 20%"
            >
              <el-option
                v-for="item in options.clearManId"
                :key="item.id"
                :label="`${item.empNo}/${item.empName}`"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="差错复核主管" prop="errorConfirmMan">
            <el-select
              v-model="dataForm.errorConfirmMan"
              filterable
              placeholder="请选择差错复核主管"
              style="width: 20%"
            >
              <el-option
                v-for="item in options.clearManId"
                :key="item.id"
                :label="`${item.empNo}/${item.empName}`"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
        </template>
        <el-form-item v-if="!statusT" label="差错明细" prop="errorList">
          <el-table
            ref="restTable"
            :data="dataForm.errorList"
            style="margin-top: 20px"
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
                  @click="
                    dataForm.errorList.push({
                      detailType: null,
                      denomId: null,
                      count: null,
                      amount: null,
                      rmbSn: null,
                    })
                  "
                />
              </template>
              <template slot-scope="scope">
                <el-button
                  plain
                  type="danger"
                  icon="el-icon-remove-outline"
                  @click="dataForm.errorList.splice(scope.$index, 1)"
                />
              </template>
            </el-table-column>
            <el-table-column align="center" label="差错类型">
              <template slot-scope="scope">
                <el-form-item
                  :prop="'errorList.' + scope.$index + '.detailType'"
                  label-width="0px"
                  :rules="{
                    required: true,
                    message: '请填写',
                    trigger: 'blur',
                  }"
                >
                  <el-select
                    v-model="scope.row.detailType"
                    filterable
                    placeholder="请选择差错类型"
                    style="width: 100%"
                  >
                    <el-option
                      v-for="item in options.detailType"
                      :key="item.code"
                      :label="item.content"
                      :value="item.code"
                    />
                  </el-select>
                </el-form-item>
              </template>
            </el-table-column>
            <el-table-column align="center" label="券别">
              <template slot-scope="scope">
                <el-form-item
                  :prop="'errorList.' + scope.$index + '.denomId'"
                  label-width="0px"
                  :rules="{
                    required: true,
                    message: '请填写',
                    trigger: 'blur',
                  }"
                >
                  <el-select
                    v-model="scope.row.denomId"
                    filterable
                    placeholder="请选择券别"
                    style="width: 100%"
                  >
                    <el-option
                      v-for="item in options.denomId"
                      :key="item.id"
                      :label="item.name"
                      :value="item.id"
                    />
                  </el-select>
                </el-form-item>
              </template>
            </el-table-column>
            <el-table-column align="center" label="张数">
              <template slot-scope="scope">
                <el-form-item
                  :prop="'errorList.' + scope.$index + '.count'"
                  label-width="0px"
                  :rules="{
                    required: true,
                    message: '请填写',
                    trigger: 'blur',
                  }"
                >
                  <el-input
                    v-model="scope.row.count"
                    :maxlength="32"
                    oninput="value=value.replace(/[^\d]/g,'')"
                  />
                </el-form-item>
              </template>
            </el-table-column>
            <el-table-column align="center" label="金额">
              <template slot-scope="scope">
                <el-form-item
                  :prop="'errorList.' + scope.$index + '.amount'"
                  label-width="0px"
                  :rules="{
                    required: true,
                    message: '请填写',
                    trigger: 'blur',
                  }"
                >
                  <el-input
                    v-model="scope.row.amount"
                    :maxlength="32"
                    @keyup.native="scope.row.amount = formatNum($event)"
                  />
                </el-form-item>
              </template>
            </el-table-column>
            <el-table-column align="center" label="冠字号">
              <template slot-scope="scope">
                <el-form-item
                  :prop="'errorList.' + scope.$index + '.rmbSn'"
                  label-width="0px"
                  :rules="{
                    required: true,
                    message: '请填写',
                    trigger: 'blur',
                  }"
                >
                  <el-input v-model="scope.row.rmbSn" :maxlength="64" />
                </el-form-item>
              </template>
            </el-table-column>
          </el-table>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="updateData(dialogStatus)">确定</el-button>
      </div>
    </el-dialog>
    <!-- 审核框 -->
    <el-dialog
      title="审核操作"
      :visible.sync="auditDialogFormVisible"
      :close-on-click-modal="false"
    >
      <el-row>
        <el-col :span="16" :offset="4">
          <el-form ref="auditForm" :model="auditDataForm" label-width="80px">
            <el-form-item label="审核状态">
              <el-radio-group v-model="auditDataForm.status">
                <el-radio :label="1">审核通过</el-radio>
                <el-radio :label="2">审核拒绝</el-radio>
              </el-radio-group>
            </el-form-item>

            <el-form-item label="审核内容">
              <el-input v-model="auditDataForm.comments" type="textarea" />
            </el-form-item>
          </el-form>
        </el-col>
      </el-row>
      <div slot="footer" class="dialog-footer">
        <el-button @click="auditDialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="auditData">确定</el-button>
      </div>
    </el-dialog>
    <!-- 审核历史详细 -->
    <el-dialog
      title="审核记录"
      :visible.sync="auditDialogHistoryVisible"
      :close-on-click-modal="false"
    >
      <el-row>
        <el-timeline v-if="auditHistoryList.length > 0">
          <el-timeline-item
            v-for="audit in auditHistoryList"
            :key="audit.id"
            class="timeline-item"
          >
            <span v-text="audit.userName" />
            <span v-if="audit.status == 1">已同意</span>
            <span v-if="audit.status == 2">已拒绝</span>
            <span>{{ formatDateTime(audit.createTime) }}</span>
            <span v-text="audit.comments" />
          </el-timeline-item>
        </el-timeline>
        <el-empty v-else :image-size="200" />
      </el-row>
      <div slot="footer" class="dialog-footer">
        <el-button @click="auditDialogHistoryVisible = false">关闭</el-button>
      </div>
    </el-dialog>
    <!-- 详情 -->
    <el-dialog
      title="详情"
      :visible.sync="detailVisible"
      :close-on-click-modal="false"
    >
      <div>
        <my-table :data-list="detailList" :table-list="detailTableList" />
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailVisible = false">关闭</el-button>
      </div>
    </el-dialog>

    <!-- 上传对话框 -->
    <el-dialog
      title="上传文件"
      :visible.sync="dialogExportVisible"
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
            @change="getExportBankTopOption"
          >
            <el-option
              v-for="item in depOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="日期" prop="taskDate">
          <el-date-picker
            v-model="uploadDataForm.taskDate"
            value-format="timestamp"
            type="date"
            placeholder="请选择日期"
            style="width: 40%"
          />
        </el-form-item>
        <el-form-item label="所属银行" prop="bankType">
          <el-select
            v-model="uploadDataForm.bankType"
            placeholder="请选择所属银行"
            style="width: 40%"
            clearable
          >
            <el-option
              v-for="item in exportBankOption"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="上传文件" prop="fileList">
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
            :file-list="uploadDataForm.fileList"
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
        <el-button @click="dialogExportVisible = false">取消</el-button>
        <el-button type="primary" @click="exportData()">导入</el-button>
      </div>
    </el-dialog>

    <!-- 上传确认框 -->
    <el-dialog
      title="确认文件"
      :visible.sync="dialogConfirmVisible"
      :close-on-click-modal="false"
      width="60%"
      :before-close="beforeClose"
    >
      <div>
        <el-form label-position="right" inline class="confirm-info">
          <el-form-item label="日期" style="width: 30%">
            <span>{{ formatDate(confirmForm.taskDate) }}</span>
          </el-form-item>
          <el-form-item label="银行" style="width: 30%">
            <span>{{ bankName }}</span>
          </el-form-item>
          <el-form-item label="文件名" style="width: 40%">
            <span>{{ confirmForm.fileName }}</span>
          </el-form-item>
        </el-form>
        <div v-for="item,index in confirmList" :key="index">
          <div v-if="item.routeNo" style="font-size:16px; font-weight:600;margin-top:20px">{{ item.routeNo }}号线</div>
          <my-table
            :data-list="item.atmTashClearList"
            :table-list="confirmTableList"
            height="360px"
            style="margin: 12px 0 20px 0"
          />
          <el-form label-position="right" inline class="confirm-info">
            <el-form-item label="任务个数" style="width: 30%">
              <span style="font-szie:14px">{{ item.atmTashClearList.length }}</span>
            </el-form-item>
            <el-form-item label="总金额" style="width: 70%">
              <span style="font-szie:14px">{{ formatMoney(amountTotal(item.atmTashClearList)) }}</span>
            </el-form-item>
          </el-form>
          <el-divider v-if="confirmList.length > 1" />
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="confirmVisibleCancel()">取消</el-button>
        <el-button v-permission="['/base/clearTask/saveImportClear']" type="primary" @click="confirmExport()">确认</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { formatMoney } from '@/utils/convert'
import Pagination from '@/components/Pagination'
import myTable from '@/components/Table'
import {
  bankClearTopBank,
  authOption,
  routeOption,
  denomOption,
  jobNameOption
} from '@/api/common/selectOption'
import {
  listTask,
  updateTask,
  addTask,
  deleteTask,
  finishTask,
  listDevice,
  listAuditTask,
  auditTask,
  auditHistory,
  importPath,
  detailTask,
  importData
} from '@/api/clearManage/clearTask'
import { getToken } from '@/utils/auth'
import { formatdate } from '@/utils/date'
export default {
  name: 'TaskList',
  components: { Pagination, myTable },
  filters: {},
  data() {
    // const checkList = (rule, value, callback) => {
    //   if (value.length > 0) {
    //     callback()
    //   } else {
    //     callback(new Error('请添加明细'))
    //   }
    // }
    return {
      loading: null,
      activeName: 'first',
      oldName: 'first',
      list: [],
      bankSearchOption: [],
      depOptions: [],
      jobNameOption: [],
      routeSearchOption: [],
      routeOption: [],
      listQuery: {
        limit: 10,
        page: 1,
        statusT: null,
        bankId: null,
        departmentId: null,
        taskDate: null,
        errorType: null,
        clearManId: null
      },
      listLoading: true,
      total: 0,
      options: {
        statusT: [
          { code: 0, content: '未开始' },
          { code: 1, content: '已结束' }
        ],
        auditStatusT: [
          { code: 1, content: '审核中' },
          { code: 2, content: '审核通过' },
          { code: 3, content: '审核拒绝' }
        ],
        detailType: [
          { code: 1, content: '假币' },
          { code: 2, content: '残缺币' },
          { code: 3, content: '夹张' }
        ],
        errorType: [
          { code: 0, content: '平账' },
          { code: 1, content: '长款' },
          { code: 2, content: '短款' }
        ],
        denomId: [],
        clearManId: []
      },
      role: {
        list: '/base/clearTask/list',
        add: '/base/clearTask/save'
      },
      tableList: [
        {
          label: '任务日期',
          prop: 'taskDate'
        },
        {
          label: '线路',
          prop: 'routeText'
        },
        {
          label: '银行名称',
          prop: 'bankName'
        },
        {
          label: '设备编号',
          prop: 'terNo'
        },
        {
          label: '计划清分',
          prop: 'planAmount',
          formatter: this.formatMoney
        },
        {
          label: '实际清分',
          prop: 'clearAmount',
          formatter: this.formatMoney
        },
        {
          label: '差错金额',
          prop: 'errorAmount',
          formatter: this.formatMoney
        },
        {
          label: '差错类型',
          prop: 'errorType',
          formatter: this.formatErrorType
        },
        {
          label: '清点员',
          prop: 'clearManName'
        },
        {
          label: '复核员',
          prop: 'checkManName'
        },
        {
          label: '状态',
          prop: 'statusT',
          formatter: this.formatStatus
        },
        {
          label: '备注',
          prop: 'comments'
        }
      ],
      dialogFormCreateVisible: false,
      dialogFormVisible: false,
      addDataForm: {
        departmentId: null,
        routeId: null,
        atmList: []
      },
      atmOption: [],
      atmList: [],
      dataForm: {
        planAmount: null,
        clearAmount: null,
        id: null,
        atmId: null,
        errorList: [],
        clearMan: null,
        checkMan: null,
        errorConfirmMan: null
      },
      dialogExportVisible: false,
      rules: {
        planAmount: [
          { required: true, message: '计划清分不能为空', trigger: 'blur' }
        ],
        clearAmount: [
          { required: true, message: '实际清分不能为空', trigger: 'blur' }
        ],
        clearMan: [
          { required: true, message: '清分员不能为空', trigger: 'blur' }
        ],
        checkMan: [
          { required: true, message: '复核员不能为空', trigger: 'blur' }
        ]
      },
      detailVisible: false,
      detailForm: {},
      detailList: [],
      detailTableList: [
        {
          label: '差错类型',
          prop: 'detailType',
          formatter: this.formatDetailType
        },
        {
          label: '券别',
          prop: 'denomId',
          formatter: this.formatDenomId
        },
        {
          label: '张数',
          prop: 'count'
        },
        {
          label: '金额',
          prop: 'amount',
          formatter: this.formatMoney
        },
        {
          label: '冠字号',
          prop: 'rmbSn'
        }
      ],
      // 上传
      headers: {
        'X-Token': getToken(),
        'X-mac': this.$store.getters.mac
      },
      importPath,
      uploadRules: {
        departmentId: [
          { required: true, message: '权限部门不能为空', trigger: 'blur' }
        ],
        taskDate: [
          { required: true, message: '日期不能为空', trigger: 'blur' }
        ],
        bankType: [
          { required: true, message: '银行类型不能为空', trigger: 'blur' }
        ],
        fileList: [
          { required: true, message: '上传文件不能为空', trigger: 'blur' }
        ]
      },
      uploadDataForm: {
        departmentId: null,
        taskDate: null,
        bankType: null,
        fileList: []
      },
      bankName: '',
      exportBankOption: [],
      fileList: [],
      paramsData: {},
      bankTypeOption: [
        { id: 1, name: '北京银行' },
        { id: 2, name: '工商银行' },
        { id: 3, name: '农商银行' }
      ],
      addRules: {
        departmentId: [
          { required: true, message: '所属部门不能为空', trigger: 'blur' }
        ],
        routeId: [
          { required: true, message: '清分线路不能为空', trigger: 'blur' }
        ]
      },
      auditListQuery: {
        limit: 10,
        page: 1,
        statusT: 1,
        bankId: null,
        departmentId: null,
        taskDate: null
      },
      auditList: [],
      auditTableList: [
        {
          label: '任务日期',
          prop: 'taskDate'
        },
        {
          label: '线路',
          prop: 'routeText'
        },
        {
          label: '银行名称',
          prop: 'bankName'
        },
        {
          label: '设备编号',
          prop: 'terNo'
        },
        {
          label: '计划清分',
          prop: 'planAmount',
          formatter: this.formatMoney
        },
        {
          label: '实际清分',
          prop: 'clearAmount',
          formatter: this.formatMoney
        },
        {
          label: '差错金额',
          prop: 'errorAmount',
          formatter: this.formatMoney
        },
        {
          label: '清点员',
          prop: 'clearManName'
        },
        {
          label: '复核员',
          prop: 'checkManName'
        },
        {
          label: '审核状态',
          prop: 'statusT',
          formatter: this.formatAuditStatus
        },
        {
          label: '备注',
          prop: 'comments'
        },
        {
          label: '提交时间',
          prop: 'createTime',
          formatter: this.formatDateTime,
          width: 90
        }
      ],
      auditListLoading: true,
      auditTotal: 0,
      bankAuditSearchOption: [],
      routeAuditSearchOption: [],
      auditDataForm: {
        id: 0,
        status: 1,
        comments: ''
      },
      auditDialogFormVisible: false,
      auditDialogHistoryVisible: false,
      auditHistoryList: [],
      statusT: false,
      // 上传确认
      dialogConfirmVisible: false,
      confirmForm: {},
      confirmList: [],
      confirmTableList: [
        {
          label: '设备编号',
          prop: 'terNo'
        },
        {
          label: '计划清分金额',
          prop: 'planAmount',
          formatter: this.formatMoney
        }
      ],
      confirmAmountTotal: 0,
      dialogStatus: '',
      textMap: {
        update: '编辑',
        finish: '完成'
      }
    }
  },
  mounted() {
    this.getOptions().then(() => {
      this.getClearManOption()
    })
  },
  methods: {
    tabClick() {
      if (this.activeName !== this.oldName) {
        if (this.activeName === 'first') {
          this.getList()
        } else {
          this.auditListQuery.departmentId = this.depOptions[0].id
          this.departmentAuditChange(this.auditListQuery.departmentId)
          this.getAuditList()
        }
      }
    },
    getList(data) {
      listTask(this.listQuery).then((res) => {
        const data = res.data
        this.list = data.list
        this.total = data.total
        this.listLoading = false
      })
    },
    getAuditList(data) {
      listAuditTask(this.auditListQuery).then((res) => {
        const data = res.data
        this.auditList = data.list
        this.auditTotal = data.total
        this.auditListLoading = false
      })
    },
    departmentChange(value) {
      bankClearTopBank(value).then((res) => {
        this.bankSearchOption = res.data
      })
      // 更新查询线路
      this.getClearManOption()
      this.updateRoute()
    },
    async getOptions() {
      await authOption().then((res) => {
        this.depOptions = res.data
        if (this.depOptions.length > 0) {
          this.listQuery.departmentId = this.depOptions[0].id
          this.getList()
          bankClearTopBank(this.listQuery.departmentId).then((res) => {
            this.bankSearchOption = res.data
          })
        }
      })
      denomOption().then((res) => {
        this.options.denomId = res.data
      })
    },
    getClearManOption() {
      jobNameOption(this.listQuery.departmentId, '5').then(res => {
        this.options.clearManId = res.data[5]
      })
    },
    handleCreate() {
      this.dialogFormCreateVisible = true
      this.atmList = [{ atmId: [] }]
      this.$nextTick(() => {
        this.$refs['addDataForm'].resetFields()
      })
    },
    handleDetail(row) {
      detailTask(row.id).then((res) => {
        this.detailVisible = true
        this.detailList = res.data.errorList
      })
    },
    handleTaskDetail(row) {
      console.log(row)
      this.detailVisible = true
      this.detailList = JSON.parse(row.errorList)
    },
    handleUpdate(row) {
      this.statusT = row.statusT === 0
      this.dialogStatus = 'update'
      this.dialogFormVisible = true
      if (!this.statusT) {
        detailTask(row.id).then((res) => {
          this.$nextTick(() => {
            this.$refs['dataForm'].clearValidate()
            for (const key in this.dataForm) {
              this.dataForm[key] = row[key]
            }
            this.dataForm.errorList = res.data.errorList
          })
        })
      } else {
        this.$nextTick(() => {
          this.$refs['dataForm'].clearValidate()
          for (const key in this.dataForm) {
            this.dataForm[key] = row[key]
          }
        })
      }
    },
    handleFinish(row) {
      this.statusT = false
      this.dialogStatus = 'finish'
      this.dialogFormVisible = true
      detailTask(row.id).then((res) => {
        this.$nextTick(() => {
          this.$refs['dataForm'].clearValidate()
          for (const key in this.dataForm) {
            this.dataForm[key] = row[key]
          }
          if (this.dataForm.clearMan === 0) this.dataForm.clearMan = null
          if (this.dataForm.checkMan === 0) this.dataForm.checkMan = null
          if (this.dataForm.errorConfirmMan === 0) this.dataForm.errorConfirmMan = null
          this.dataForm.errorList = res.data.errorList
        })
      })
    },
    handleExportDialog() {
      bankClearTopBank(this.listQuery.departmentId).then((res) => {
        this.exportBankOption = res.data
        this.dialogExportVisible = true
        this.$nextTick(() => {
          this.$refs['uploadDataForm'].resetFields()
          this.uploadDataForm.departmentId = this.listQuery.departmentId
          this.uploadDataForm.taskDate = new Date(new Date().setHours(0, 0, 0, 0)).getTime()
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
        deleteTask(row.id)
          .then(() => {
            this.$message.success('删除成功')
            const index = this.list.indexOf(row)
            this.list.splice(index, 1)
          })
          .finally(() => {
            this.listLoading = false
          })
      })
    },
    newData() {
      const self = this
      this.$refs['addDataForm'].validate((valid) => {
        if (valid) {
          // 验证列表数据
          let atmIdFlag = true
          let atmAmountFlag = true
          this.atmList.forEach(function(item, index) {
            const line = index + 1
            if (
              item.atmId === undefined ||
              item.atmId === null ||
              item.atmId === ''
            ) {
              self.$message.warning(
                'ATM清分字段数据: 第' + line + '行ATM设备请选择'
              )
              atmIdFlag = false
              return
            }
            if (item.planAmount === undefined) {
              self.$message.warning(
                'ATM清分字段数据: 第' + line + '行ATM清分金额请填写'
              )
              atmAmountFlag = false
              return
            }
          })
          if (atmIdFlag === false || atmAmountFlag === false) {
            return
          }
          this.listLoading = true
          // 计算bankId
          self.atmList.forEach(function(s) {
            const [obj] = self.atmOption.filter((m) => m.id === s.atmId)
            if (obj !== undefined) {
              s.bankId = parseInt(obj.bankId)
            } else {
              s.bankId = 0
            }
          })
          this.addDataForm.atmList = this.atmList
          addTask(this.addDataForm)
            .then(() => {
              this.getList()
              this.dialogFormCreateVisible = false
              this.$message.success({ title: '成功', message: '添加成功' })
            })
            .finally(() => {
              this.listLoading = false
            })
        }
      })
    },
    updateData(status) {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          this.listLoading = true
          if (status === 'update') {
            // eslint-disable-next-line no-unused-vars
            const { clearMan, checkMan, errorConfirmMan, ...newForm } = this.dataForm
            updateTask(newForm)
              .then((res) => {
                this.getList()
                this.dialogFormVisible = false
                this.$message.success(res.msg)
              })
              .finally(() => {
                this.listLoading = false
              })
          } else {
            finishTask(this.dataForm).then((res) => {
              this.getList()
              this.dialogFormVisible = false
              this.$message.success(res.msg)
            })
              .finally(() => {
                this.listLoading = false
              })
          }
        }
      })
    },
    formatMoney,
    formatNum(e) {
      e.target.value = e.target.value.replace(/[^\d.]/g, '')
      e.target.value = e.target.value.replace(/\.{2,}/g, '.')
      e.target.value = e.target.value.replace(/^\./g, '0.')
      e.target.value = e.target.value.replace(
        /^\d*\.\d*\./g,
        e.target.value.substring(0, e.target.value.length - 1)
      )
      e.target.value = e.target.value.replace(/^0[^\.]+/g, '0')
      e.target.value = e.target.value.replace(/^(\d+)\.(\d\d).*$/, '$1.$2')
      return e.target.value
    },
    formatStatus(status) {
      for (const item of this.options.statusT) {
        if (item.code === status) {
          return item.content
        }
      }
    },
    formatDateTime(timestamp) {
      if (!timestamp || timestamp === 0) {
        return '-'
      }
      const date = new Date(timestamp)
      return formatdate(date, 'yyyy-MM-dd hh:mm:ss')
    },
    formatDate(timestamp) {
      if (!timestamp || timestamp === 0) {
        return '-'
      }
      const date = new Date(timestamp)
      return formatdate(date, 'yyyy-MM-dd')
    },
    formatAuditStatus(status) {
      for (const item of this.options.auditStatusT) {
        if (item.code === status) {
          return item.content
        }
      }
    },
    formatTerType(terType) {
      for (const item of this.options.terType) {
        if (item.code === terType) {
          return item.content
        }
      }
    },
    formatLocationType(locationType) {
      for (const item of this.options.locationType) {
        if (item.code === locationType) {
          return item.content
        }
      }
    },
    formatDetailType(type) {
      if (type != null) {
        return this.options.detailType.find((item) => item.code === type)
          .content
      }
    },
    formatDenomId(type) {
      if (type != null) {
        return this.options.denomId.find((item) => item.id === type).name
      }
    },
    formatBankId(type) {
      if (type != null) {
        return this.exportBankOption.find((item) => item.id === type).name
      }
    },
    formatErrorType(type, row) {
      if (row.statusT === 0) {
        return '<span style="color:red">——</span>'
      }
      switch (type) {
        case 0:
          return '<span style="color:#31aa09">平账</span>'
        case 1:
          return '<span style="color:#fac11b">长款</span>'
        case 2:
          return '<span style="color:red">短款</span>'
      }
    },
    handleAudit(row) {
      this.auditDialogFormVisible = true
      this.auditDataForm.id = row.id
      this.$nextTick(() => {
        this.$refs['auditForm'].clearValidate()
        this.auditDataForm.status = 1
        this.auditDataForm.comments = ''
      })
    },
    handleAuditHistory(row) {
      this.auditDialogHistoryVisible = true
      auditHistory({ auditId: row.id }).then((res) => {
        this.auditHistoryList = res.data
      })
    },
    auditData() {
      this.$refs['auditForm'].validate((valid) => {
        if (valid) {
          this.loading = this.$loading({
            lock: true,
            text: '正在加载...请勿进行其它操作',
            spinner: 'el-icon-loading'
          })
          auditTask(this.auditDataForm)
            .then(() => {
              this.auditDialogFormVisible = false
              this.$message.success('审核成功')
              this.getAuditList()
            })
            .finally(() => {
              this.loading.close()
            })
        }
      })
    },
    changeDepartment(value) {
      this.routeOption = []
      this.addDataForm.routeId = null
      this.atmOption = []
      this.atmList = []
      const routeDate = new Date(new Date().toLocaleDateString()).getTime()
      routeOption({ departmentId: value, routeDate: routeDate }).then((res) => {
        this.routeOption = res.data
      })
    },
    changeRoute(value) {
      this.atmOption = []
      this.atmList = []
      listDevice({ routeId: value }).then((res) => {
        const atmIds = res.data.atmId
        const atmList = res.data.atmList
        this.atmList = []
        for (let k = 0; k < atmIds.length; k++) {
          this.atmList.push({ atmId: atmIds[k], planAmount: 0.0 })
        }
        this.atmOption = atmList
      })
    },
    departmentChangeForm(value) {
      bankClearTopBank(value).then((res) => {
        this.$set(
          this.dataForm,
          'bankId',
          res.data.length > 0 ? res.data[0].id : null
        )
      })
    },
    updateRoute() {
      const taskDate = this.listQuery.taskDate
      const departmentId = this.listQuery.departmentId
      if (taskDate == null || departmentId == null) {
        this.routeSearchOption = []
        return
      }
      const offset = new Date().getTimezoneOffset() * 60 * 1000
      // +8时间戳
      const routeDate8 = new Date(taskDate).getTime()
      const routeDate = routeDate8 + offset
      routeOption({ departmentId, routeDate: routeDate }).then((res) => {
        this.routeSearchOption = res.data
      })
    },
    departmentAuditChange(value) {
      bankClearTopBank(value).then((res) => {
        this.bankAuditSearchOption = res.data
      })
      // 更新查询线路
      this.updateAuditRoute()
    },
    getExportBankTopOption(val) {
      this.uploadDataForm.bankType = null
      bankClearTopBank(val).then((res) => {
        this.exportBankOption = res.data
      })
    },
    updateAuditRoute() {
      const taskDate = this.auditListQuery.taskDate
      const departmentId = this.auditListQuery.departmentId
      if (taskDate == null || departmentId == null) {
        this.routeAuditSearchOption = []
        return
      }
      const offset = new Date().getTimezoneOffset() * 60 * 1000
      // +8时间戳
      const routeDate8 = new Date(taskDate).getTime()
      const routeDate = routeDate8 + offset
      routeOption({ departmentId, routeDate: routeDate }).then((res) => {
        this.routeAuditSearchOption = res.data
      })
    },
    exportRemove(file, fileList) {
      this.uploadDataForm.fileList = fileList
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
        this.confirmForm = res.data
        this.confirmForm.fileName = file.name
        this.confirmForm.bankType = this.uploadDataForm.bankType
        this.confirmForm.taskDate = this.uploadDataForm.taskDate
        this.confirmForm.departmentId = this.uploadDataForm.departmentId
        this.dialogConfirmVisible = true
        this.confirmList = res.data.atmTashClearList
      }
    },
    exportChange(file, fileList) {
      this.uploadDataForm.fileList = fileList.slice(-1)
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
    exportData() {
      this.$refs['uploadDataForm'].validate((valid) => {
        if (valid) {
          this.paramsData.taskDate = this.uploadDataForm.taskDate
          this.paramsData.bankType = this.uploadDataForm.bankType
          this.bankName = this.exportBankOption.find((item) => item.id === this.uploadDataForm.bankType).name
          this.$refs.upload.submit()
          this.openLoading()
        }
      })
    },
    confirmVisibleCancel() {
      this.dialogConfirmVisible = false
      this.$nextTick(() => {
        this.$refs['uploadDataForm'].resetFields()
      })
    },
    confirmExport() {
      this.openLoading()
      importData(this.confirmForm).then(() => {
        this.$notify.success({
          title: '成功',
          message: '导入成功'
        })
        this.dialogConfirmVisible = this.dialogExportVisible = false
        this.getList()
      })
        .finally(() => {
          this.loading.close()
        })
    },
    beforeClose(done) {
      this.$nextTick(() => {
        this.$refs['uploadDataForm'].resetFields()
        done()
      })
    },
    openLoading() {
      this.loading = this.$loading({
        lock: true,
        text: '正在加载...请勿进行其它操作',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      })
    },
    amountTotal(list) {
      const total = list.map((item) => item.planAmount)
      return total.reduce((n, m) => n + m, 0)
    }
  }
}
</script>

<style scoped lang="scss">
.filter-container *:nth-child(n + 2) {
  margin-left: 10px;
}
.timeline-item span {
  margin-right: 15px;
}

.confirm-info {
  .el-form-item {
    margin-right: 0;
    margin-bottom: 0;
    width: 50%;
    div {
      padding-left: 40px;
    }
  }
}

.el-form--inline .el-form-item {
  margin-right: 0;
  margin-bottom: 8px;
  width: 33%;
  ::v-deep .el-form-item__label {
    color: #99a9bf;
    margin-right: 12px;
    font-size: 16px;
  }
  ::v-deep .el-form-item__content {
    font-size: 16px;
  }
}
</style>

