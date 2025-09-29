<template>
  <div class="app-container">
    <!-- 查询和其他操作 -->
    <div class="filter-container">
      <el-select v-model="listQuery.departmentId" filterable placeholder="请先选择部门" class="filter-item" style="width: 150px" @change="departmentChange">
        <el-option
          v-for="item in depOptions"
          :key="item.id"
          :label="item.name"
          :value="item.id"
        />
      </el-select>
      <el-date-picker v-model="listQuery.orderDate" value-format="timestamp" style="width: 150px" type="date" placeholder="选择日期" class="filter-item" />
      <el-select v-model="listQuery.bankId" filterable placeholder="请选择银行" clearable class="filter-item" style="width: 150px">
        <el-option
          v-for="item in bankSearchOption"
          :key="item.id"
          :label="item.name"
          :value="item.id"
        />
      </el-select>
      <el-select v-model="listQuery.orderType" filterable placeholder="请选择类别" clearable class="filter-item" style="width: 150px">
        <el-option
          v-for="item in typeList"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        />
      </el-select>
      <el-select v-model="listQuery.subType" filterable placeholder="请选择类型" clearable class="filter-item" style="width: 150px">
        <el-option
          v-for="item in subTypeList"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        />
      </el-select>
      <el-button class="filter-item" type="primary" icon="el-icon-search" @click="getList">查找</el-button>
      <el-button v-permission="['/base/vaultOrder/save']" class="filter-item" type="success" icon="el-icon-edit" @click="handleInCreate">新增入库</el-button>
      <el-button v-permission="['/base/vaultOrder/outSave']" class="filter-item" type="warning" icon="el-icon-edit" @click="handleOutCreate">创建出库</el-button>
    </div>
    <!-- 查询结果 -->
    <el-table
      v-loading="listLoading"
      element-loading-text="正在查询中。。。"
      :data="list"
      :header-cell-style="{'background-color':'#f5f5f5'}"
      border
      fit
    >
      <el-table-column align="center" label="账务日期" prop="orderDate">
        <template slot-scope="scope">
          <i class="el-icon-time" />
          <span style="margin-left: 10px">{{ scope.row.orderDate | formatDateTime }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="机构名称" prop="bankName" />
      <el-table-column align="center" label="类别" prop="orderType" :formatter="formatType" />
      <el-table-column align="center" label="出入库类型" prop="subType" :formatter="formatSubType" />
      <el-table-column align="center" label="金额" prop="orderAmount" :formatter="formatMoney" />
      <el-table-column align="center" label="状态" prop="statusT" :formatter="formatStatus" />
      <el-table-column align="center" label="明细" width="100">
        <template slot-scope="scope">
          <el-button
            plain
            type="primary"
            size="mini"
            @click="handleDetail(scope.row)"
          >详情</el-button>
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
            v-show="scope.row.statusT === 0 || scope.row.statusT === 2"
            v-permission="['/base/vaultOrder/update']"
            type="primary"
            size="mini"
            @click="handleUpdate(scope.row)"
          >编辑</el-button>
          <el-button
            v-show="scope.row.statusT === 0"
            v-permission="['/base/vaultOrder/submitAudit']"
            type="warning"
            size="mini"
            @click="handleSubmitAudit(scope.row)"
          >提交审核</el-button>
          <el-button
            v-show="scope.row.audit"
            type="warning"
            size="mini"
            @click="handleAudit(scope.row)"
          >审核</el-button>
          <el-button
            v-show="scope.row.statusT === 3 || scope.row.statusT === 4 || scope.row.statusT === 6"
            v-permission="['/base/vaultOrder/undo']"
            type="success"
            size="mini"
            @click="handleUndo(scope.row)"
          >撤销</el-button>
          <el-button
            v-show="scope.row.statusT === 0"
            v-permission="['/base/vaultOrder/delete']"
            type="danger"
            size="mini"
            @click="handleDelete(scope.row)"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

    <!-- 明细框 -->
    <el-dialog :title="detailDialogTitle === 'out' ? '出库明细' : '入库明细'" :visible.sync="detailDialogFormVisible" width="65%" :close-on-click-modal="false">
      <!-- <el-steps :active="orderStep" finish-status="success" simple style="margin-bottom: 20px;padding: 13px 2% !important;">
        <el-step v-for="item in stepMap[detailForm.orderType]" :key="item" class="step-box" :title="item" />
      </el-steps> -->
      <el-form label-position="left" inline>
        <el-card class="box-card">
          <el-row>
            <el-col :span="12">
              <el-form-item label="金融机构" style="margin-bottom: 0px;">
                <span v-text="detailForm.bankName" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="账务日期" style="margin-bottom: 0px;">
                <span>{{ detailForm.orderDate | formatDateTime }}</span>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="金额" style="margin-bottom: 0px;">
                <span v-text="formatMoney(detailForm)" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="状态" style="margin-bottom: 0px;">
                <span v-text="formatStatus(detailForm)" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item :label="detailDialogTitle === 'out' ? '出库人' : '入库人'" style="margin-bottom: 0px;">
                <span>{{ detailForm.whOpManName }} {{ detailForm.whCheckManName }} {{ detailForm.whConfirmManName }}</span>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item :label="detailDialogTitle === 'out' ? '出库时间' : '入库时间'" style="margin-bottom: 0px;">
                <span v-text="formatWhOptime(detailForm)" />
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="备注" style="margin-bottom: 0px;">
                <span>{{ detailForm.comments }}</span>
              </el-form-item>
            </el-col>
          </el-row>
        </el-card>
      </el-form>
      <el-divider v-if="detailForm.subType == 0 && detailForm.orderType == 1" content-position="center">ATM加钞任务</el-divider>
      <el-collapse v-if="detailForm.subType == 0 && detailForm.orderType == 1" v-model="activeNames">
        <el-collapse-item v-for="route in detailForm.routeList" :key="route.routeId" :title="getRouteTitle(route)" :name="route.routeId">
          <div v-for="(task,index) in route.taskList" :key="index" class="text item">
            {{ 'ATM设备编号： ' + task.terNo + '; 加钞金额：' + task.amount + '元' }}
          </div>
        </el-collapse-item>
      </el-collapse>
      <!--      <div v-if="detailForm.subType == 0" style="margin-top: 5px;padding-top: 4px">总金额：{{ formatMoney(detailForm) }}</div>-->

      <div v-for="denom in detailForm.denomList" :key="denom.denomType">
        <el-divider v-if="denom.dataList.length > 0" content-position="center">{{ denom.text }}</el-divider>
        <el-table
          v-if="denom.dataList.length > 0"
          :data="denom.dataList"
          :header-cell-style="{background:'#eef1f6',color:'#606266'}"
          style="width: 100%"
        >
          <el-table-column
            prop="denomText"
            label="面额"
          />
          <el-table-column
            prop="amount"
            label="金额(元)"
            :formatter="formatAmount"
          />
          <el-table-column prop="count" label="张数">
            <template slot-scope="scope">
              <div v-if="denom.denomType === 4">{{ scope.row.count }}</div>
              <div v-else>——</div>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <el-divider v-if="auditList.length > 0" content-position="center">出入库审核</el-divider>
      <el-timeline>
        <el-timeline-item v-for="audit in auditList" :key="audit.id" class="timeline-item">
          <span v-text="audit.userName" />
          <span v-if="audit.status == 1">已同意</span>
          <span v-if="audit.status == 2">已拒绝</span>
          <span>{{ audit.createTime | formatDateTimeT }}</span>
          <span v-if="audit.status == 2" v-text="audit.comments" />
        </el-timeline-item>
      </el-timeline>

      <el-divider v-if="undoList.length > 0" content-position="center">撤销审核</el-divider>
      <el-timeline>
        <el-timeline-item v-for="audit in undoList" :key="audit.id" class="timeline-item">
          <span v-text="audit.userName" />
          <span v-if="audit.status == 1">已同意</span>
          <span v-if="audit.status == 2">已拒绝</span>
          <span>{{ audit.createTime | formatDateTimeT }}</span>
          <span v-if="audit.status == 2" v-text="audit.comments" />
        </el-timeline-item>
      </el-timeline>

      <div slot="footer" class="dialog-footer">
        <el-button @click="detailDialogFormVisible = false">关闭</el-button>
      </div>
    </el-dialog>

    <!-- 领缴款添加或修改对话框 -->
    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible" :close-on-click-modal="false" width="60%">
      <el-form ref="dataForm" :rules="rules" :model="dataForm" status-icon label-position="left" label-width="100px" style="width: 90%; margin-left:6%;">
        <el-form-item label="部门" prop="departmentId">
          <el-select v-model="dataForm.departmentId" filterable placeholder="请先选择部门" class="filter-item" style="width: 300px" @change="departmentChangeForm">
            <el-option
              v-for="item in depOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="机构名称" prop="bankId">
          <el-select v-model="dataForm.bankId" filterable placeholder="请先选择银行机构" class="filter-item" style="width: 300px" @change="loadData">
            <el-option
              v-for="item in bankOption"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="业务类型" prop="orderType">
          <el-select v-if="subType === 0" v-model="dataForm.subType" filterable placeholder="请先选择业务类型" class="filter-item" style="width: 300px" @change="loadData">
            <el-option
              v-for="item in subTypeList.slice(0,1)"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
          <el-select v-else v-model="dataForm.subType" filterable placeholder="请先选择业务类型" class="filter-item" style="width: 300px" @change="loadData">
            <el-option
              v-for="item in subTypeList.slice(1)"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="账务日期" prop="orderDate">
          <el-date-picker
            v-model="dataForm.orderDate"
            value-format="timestamp"
            type="date"
            style="width: 300px"
            placeholder="选择日期"
          />
        </el-form-item>
        <el-form-item label="备注" prop="comments">
          <el-input
            v-model="dataForm.comments"
            type="textarea"
            :maxlength="500"
            :autosize="{ minRows: 2, maxRows: 4}"
            placeholder="请输入内容"
          />
        </el-form-item>
        <el-divider style="width: 600px">可用券</el-divider>
        <el-table
          :data="usableDenomList"
          border
          fit
          :header-cell-style="{'background-color':'#f5f5f5'}"
        >
          <!-- 字段 -->
          <el-table-column align="center" width="80">
            <template slot="header">
              <el-button
                :disabled="dataForm.orderType === 1"
                plain
                type="primary"
                icon="el-icon-circle-plus-outline"
                @click="usableDenomList.push({})"
              />
            </template>
            <template slot-scope="scope">
              <el-button
                :disabled="dataForm.orderType === 1"
                plain
                type="danger"
                icon="el-icon-remove-outline"
                @click="usableDenomList.splice(scope.$index, 1);"
              />
            </template>
          </el-table-column>
          <el-table-column align="center" label="券别">
            <template slot-scope="scope">
              <el-select v-model="scope.row.denomId" :disabled="dataForm.orderType === 1" filterable placeholder="请先选择券别" class="filter-item" style="width: 90%">
                <el-option
                  v-for="item in denomNameList"
                  :key="item.id"
                  :disabled="getDisabled(item.id,selectedDenomId1)"
                  :label="item.name"
                  :value="item.id"
                />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column v-if="dataForm.orderType === 1" align="center" label="库存金额">
            <template slot-scope="scope">
              <span v-text="scope.row.oldAmount" />
            </template>
          </el-table-column>
          <el-table-column align="center" label="金额">
            <template slot-scope="scope">
              <el-input v-model="scope.row.amount" oninput="value=value.replace(/[^-+\d]/g,'')" style="width: 90%" :maxlength="32" />
            </template>
          </el-table-column>
        </el-table>

        <el-divider style="width: 600px">残损券</el-divider>
        <el-table
          :data="badDenomList"
          border
          fit
          :header-cell-style="{'background-color':'#f5f5f5'}"
        >
          <!-- 字段 -->
          <el-table-column align="center" width="80">
            <template slot="header">
              <el-button
                :disabled="dataForm.orderType === 1"
                plain
                type="primary"
                icon="el-icon-circle-plus-outline"
                @click="badDenomList.push({})"
              />
            </template>
            <template slot-scope="scope">
              <el-button
                :disabled="dataForm.orderType === 1"
                plain
                type="danger"
                icon="el-icon-remove-outline"
                @click="badDenomList.splice(scope.$index, 1)"
              />
            </template>
          </el-table-column>
          <el-table-column align="center" label="券别">
            <template slot-scope="scope">
              <el-select v-model="scope.row.denomId" filterable :disabled="dataForm.orderType === 1" placeholder="请先选择券别" class="filter-item" style="width: 90%">
                <el-option
                  v-for="item in badDenomNameList"
                  :key="item.id"
                  :disabled="getDisabled(item.id,selectedDenomId2)"
                  :label="item.name"
                  :value="item.id"
                />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column v-if="dataForm.orderType === 1" align="center" label="库存金额">
            <template slot-scope="scope">
              <span v-text="scope.row.oldAmount" />
            </template>
          </el-table-column>
          <el-table-column align="center" label="金额">
            <template slot-scope="scope">
              <el-input v-model.number="scope.row.amount" style="width: 100%" :disabled="scope.row.amountDisabled" :maxlength="32" />
            </template>
          </el-table-column>
        </el-table>

        <el-divider style="width: 600px">五好券</el-divider>
        <el-table
          :data="goodDenomList"
          border
          fit
          :header-cell-style="{'background-color':'#f5f5f5'}"
        >
          <!-- 字段 -->
          <el-table-column align="center" width="80">
            <template slot="header">
              <el-button
                :disabled="dataForm.orderType === 1"
                plain
                type="primary"
                icon="el-icon-circle-plus-outline"
                @click="goodDenomList.push({})"
              />
            </template>
            <template slot-scope="scope">
              <el-button
                :disabled="dataForm.orderType === 1"
                plain
                type="danger"
                icon="el-icon-remove-outline"
                @click="goodDenomList.splice(scope.$index, 1)"
              />
            </template>
          </el-table-column>
          <el-table-column align="center" label="券别">
            <template slot-scope="scope">
              <el-select v-model="scope.row.denomId" :disabled="dataForm.orderType === 1" filterable placeholder="请先选择券别" class="filter-item" style="width: 90%">
                <el-option
                  v-for="item in denomNameList"
                  :key="item.id"
                  :disabled="getDisabled(item.id,selectedDenomId3)"
                  :label="item.name"
                  :value="item.id"
                />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column v-if="dataForm.orderType === 1" align="center" label="库存金额">
            <template slot-scope="scope">
              <span v-text="scope.row.oldAmount" />
            </template>
          </el-table-column>
          <el-table-column align="center" label="金额">
            <template slot-scope="scope">
              <el-input v-model="scope.row.amount" oninput="value=value.replace(/[^-+\d]/g,'')" style="width: 90%" :maxlength="32" />
            </template>
          </el-table-column>
        </el-table>
        <el-divider style="width: 600px">未清分</el-divider>
        <el-table
          :data="unclearDenomList"
          border
          fit
          :header-cell-style="{'background-color':'#f5f5f5'}"
        >
          <!-- 字段 -->
          <el-table-column align="center" width="80">
            <template slot="header">
              <el-button
                :disabled="dataForm.orderType === 1"
                plain
                type="primary"
                icon="el-icon-circle-plus-outline"
                @click="unclearDenomList.push({})"
              />
            </template>
            <template slot-scope="scope">
              <el-button
                :disabled="dataForm.orderType === 1"
                plain
                type="danger"
                icon="el-icon-remove-outline"
                @click="unclearDenomList.splice(scope.$index, 1)"
              />
            </template>
          </el-table-column>
          <el-table-column align="center" label="券别">
            <template slot-scope="scope">
              <el-select v-model="scope.row.denomId" :disabled="dataForm.orderType === 1" filterable placeholder="请先选择券别" class="filter-item" style="width: 90%">
                <el-option
                  v-for="item in denomNameList"
                  :key="item.id"
                  :disabled="getDisabled(item.id,selectedDenomId4)"
                  :label="item.name"
                  :value="item.id"
                />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column v-if="dataForm.orderType === 1" align="center" label="库存金额">
            <template slot-scope="scope">
              <span v-text="scope.row.oldAmount" />
            </template>
          </el-table-column>
          <el-table-column align="center" label="金额">
            <template slot-scope="scope">
              <el-input v-model="scope.row.amount" oninput="value=value.replace(/[^-+\d]/g,'')" style="width: 90%" :maxlength="32" />
            </template>
          </el-table-column>
        </el-table>
        <el-divider style="width: 600px">尾零钞</el-divider>
        <el-table
          :data="remnantDenomList"
          border
          fit
          :header-cell-style="{'background-color':'#f5f5f5'}"
        >
          <!-- 字段 -->
          <el-table-column align="center" width="80">
            <template slot="header">
              <el-button
                :disabled="dataForm.orderType === 1"
                plain
                type="primary"
                icon="el-icon-circle-plus-outline"
                @click="remnantDenomList.push({count: 0,amount: null})"
              />
            </template>
            <template slot-scope="scope">
              <el-button
                :disabled="dataForm.orderType === 1"
                plain
                type="danger"
                icon="el-icon-remove-outline"
                @click="remnantDenomList.splice(scope.$index, 1)"
              />
            </template>
          </el-table-column>
          <el-table-column align="center" label="券别">
            <template slot-scope="scope">
              <el-select v-model="scope.row.denomId" :disabled="dataForm.orderType === 1" filterable placeholder="请先选择券别" class="filter-item" style="width: 90%" @change="denomChange($event,scope.row)">
                <el-option
                  v-for="item in remnantDenomNameList"
                  :key="item.id"
                  :disabled="getDisabled(item.id,selectedDenomId4)"
                  :label="item.name"
                  :value="item.id"
                />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column v-if="dataForm.orderType === 1" align="center" label="库存金额">
            <template slot-scope="scope">
              <span v-text="scope.row.oldAmount" />
            </template>
          </el-table-column>
          <el-table-column v-if="dataForm.orderType === 1" align="center" label="库存张数">
            <template slot-scope="scope">
              <span v-text="scope.row.oldCount" />
            </template>
          </el-table-column>
          <el-table-column align="center" label="金额">
            <template slot-scope="scope">
              <el-input v-model="scope.row.amount" oninput="value=value.replace(/[^-+\d]/g,'')" style="width: 90%" :maxlength="32" @change="amountChange($event,scope.row)" />
            </template>
          </el-table-column>
          <el-table-column align="center" label="张数">
            <template slot-scope="scope">
              <el-input-number v-model="scope.row.count" style="width:100%" :min="0" @change="countChange($event,scope.row)" />
            </template>
          </el-table-column>
        </el-table>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button v-if="dialogStatus=='create'" type="primary" @click="createData">确定</el-button>
        <el-button v-else type="primary" @click="updateData">确定</el-button>
      </div>
    </el-dialog>

    <!-- ATM加钞添加或修改对话框 --->
    <el-dialog :title="`ATM加钞${inOutFont}库`" :visible.sync="atmDialogFormVisible" top="3vh" :close-on-click-modal="false">
      <el-form ref="atmDataForm" :rules="atmRules" :model="atmDataForm" status-icon label-position="left" label-width="80px" style="width: 650px; margin-left:20px;">
        <el-form-item label="部门" prop="departmentId">
          <el-select v-model="atmDataForm.departmentId" :disabled="isUpdated" filterable placeholder="请先选择部门" class="filter-item" style="width: 300px" @change="atmDepartmentChange">
            <el-option
              v-for="item in depOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="机构名称" prop="bankId">
          <el-select v-model="atmDataForm.bankId" filterable placeholder="请先选择银行机构" class="filter-item" style="width: 300px" @change="atmLoadData">
            <el-option
              v-for="item in bankOption"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="操作类型" prop="orderType">
          <el-select v-model="atmDataForm.subType" :disabled="true" filterable placeholder="请先选择操作类型" class="filter-item" style="width: 300px">
            <el-option
              v-for="item in subTypeList"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="账务日期" prop="orderDate">
          <el-date-picker
            v-model="atmDataForm.orderDate"
            :disabled="isUpdated"
            value-format="timestamp"
            type="date"
            style="width: 300px"
            placeholder="选择日期"
            @change="atmLoadData"
          />
        </el-form-item>
        <el-form-item label="备注" prop="comments">
          <el-input
            v-model="atmDataForm.comments"
            type="textarea"
            :maxlength="500"
            :autosize="{ minRows: 2, maxRows: 4}"
            placeholder="请输入内容"
          />
        </el-form-item>
        <el-form-item label="ATM加钞" prop="taskIds">
          <div style="text-align: left;">
            <el-transfer
              v-model="taskIds"
              style="text-align: left; display: inline-block"
              filterable
              :titles="['ATM加钞任务', '选择结果']"
              :format="{ noChecked: '${total}', hasChecked: '${checked}/${total}'}"
              :data="routeTaskList"
              @change="handleChange"
            >
              <span slot-scope="{ option }">{{ option.label }} - {{ option.routeName }}</span>
            </el-transfer>
          </div>
        </el-form-item>
        <el-form-item label="券别" prop="orderDate">
          <my-table
            :data-list="allDenomlist"
            :table-list="allDenomTableList"
            class="my-table"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="atmDialogFormVisible = false">取消</el-button>
        <el-button v-if="dialogStatus=='create'" type="primary" @click="createATMData">确定</el-button>
        <el-button v-else type="primary" @click="updateATMData">确定</el-button>
      </div>
    </el-dialog>

    <!-- 审核框 -->
    <el-dialog title="审核操作" :visible.sync="auditDialogFormVisible" :close-on-click-modal="false">
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

    <!-- 选择类型 -->
    <el-dialog :title="`设置${inOutFont}库类型`" :visible.sync="inOutDialogFormVisible" :close-on-click-modal="false">
      <el-row>
        <el-col :span="16" :offset="8">
          <el-radio v-model="subType" :label="0">ATM加钞</el-radio>
          <el-radio v-model="subType" :label="1">其它</el-radio>
        </el-col>
      </el-row>
      <div slot="footer" class="dialog-footer">
        <el-button @click="inOutDialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="nextStep">下一步</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { formatMoney } from '@/utils/convert'
import Pagination from '@/components/Pagination'
import myTable from '@/components/Table'
import { listOrder, addOrder, updateOrder, deleteOrder, listOrderDetail, auditOrder, submitAudit, undoOrder, getTaskList } from '@/api/vault/in-out'
import { authOption } from '@/api/common/selectOption'
import { formatdate } from '@/utils/date'
import { denomOption } from '@/api/base/denomination'
import { listBank } from '@/api/vault/volume'
import { bankTradeOption } from '@/api/common/selectOption'
export default {
  name: 'VaultIn',
  components: { Pagination, myTable },
  filters: {
    formatDateTime(timestamp) {
      if (!timestamp || timestamp === 0) {
        return '-'
      }
      const date = new Date(timestamp)
      return formatdate(date, 'yyyy-MM-dd')
    },
    formatDateTimeT(timestamp) {
      if (!timestamp || timestamp === 0) {
        return '-'
      }
      const date = new Date(timestamp)
      return formatdate(date, 'yyyy-MM-dd hh:mm:ss')
    }
  },
  data() {
    return {
      selectedDenomId1: [],
      selectedDenomId2: [],
      selectedDenomId3: [],
      selectedDenomId4: [],
      allDenomNameList: [],
      taskIds: [],
      isUpdated: false,
      activeNames: [],
      routeTaskList: [],
      value: [1],
      value4: [1],
      list: null,
      usableDenomList: [{}],
      badDenomList: [{}],
      goodDenomList: [{}],
      unclearDenomList: [{}],
      remnantDenomList: [{}],
      denomNameList: [],
      remnantDenomNameList: [],
      badDenomNameList: [],
      auditList: [],
      undoList: [],
      denomList: [],
      depOptions: [],
      bankOption: [],
      bankSearchOption: [],
      total: 0,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 10,
        departmentId: null,
        bankId: null,
        orderType: null
      },
      dataForm: {
        orderDate: 0,
        bankId: 0,
        orderType: 0,
        subType: 0,
        comments: ''
      },
      atmDataForm: {
        id: null,
        orderDate: null,
        bankId: null,
        orderType: null,
        subType: null,
        departmentId: null,
        taskIds: [],
        vaultRecordList: [],
        comments: ''
      },
      allDenomTableList: [
        {
          label: '券别',
          prop: 'denomId',
          formatter: this.formatDenom
        },
        {
          label: '金额',
          prop: 'amount',
          formatter: this.formatDenomMoney
        }
      ],
      orderStep: 0,
      detailForm: {
        step: 3,
        bankName: '',
        orderDate: 0,
        orderAmount: 0,
        subType: 0,
        denomList: [],
        routeList: [],
        comments: ''
      },

      orderType: 0,
      subType: 0,
      // 入库数据
      inOutDialogFormVisible: false,
      inOutFont: '',
      inDialogFormVisible: false,
      atmDialogFormVisible: false,
      inDataForm: {
        orderDate: 0,
        bankId: 0,
        orderType: 0,
        subType: 0
      },
      stepMap: {
        0: ['新建入库', '审核中', '等待入库', '入库完成', '撤销中', '已撤销'],
        1: ['新建出库', '审核中', '等待出库', '出库完成', '撤销中', '已撤销']
      },
      dialogFormVisible: false,
      detailDialogFormVisible: false,
      detailDialogTitle: '',
      auditDialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '创建'
      },
      auditDataForm: {
        id: 0,
        status: 1,
        comments: ''
      },
      typeList: [
        { value: 0, label: '入库' },
        { value: 1, label: '出库' }
      ],
      subTypeList: [
        { value: 0, label: 'ATM加钞' },
        { value: 1, label: '领缴款' },
        { value: 2, label: '清点' },
        { value: 3, label: '代领缴' }
      ],
      statusList: [
        { value: -1, label: '已撤销' },
        { value: 0, label: '创建' },
        { value: 1, label: '审核中' },
        { value: 2, label: '审核拒绝' },
        { value: 3, label: '审核通过' },
        { value: 4, label: '已入库' },
        { value: 5, label: '撤销中' },
        { value: 6, label: '撤销拒绝' }
      ],
      rules: {
        departmentId: [
          { required: true, message: '部门不能为空', trigger: 'blur' }
        ],
        orderDate: [
          { required: true, message: '账务日期不能为空', trigger: 'blur' }
        ],
        bankId: [
          { required: true, message: '银行机构不能为空', trigger: 'blur' }
        ],
        orderType: [
          { required: true, message: '类型不能为空', trigger: 'blur' }
        ]
      },
      atmRules: {
        departmentId: [
          { required: true, message: '部门不能为空', trigger: 'blur' }
        ],
        orderDate: [
          { required: true, message: '账务日期不能为空', trigger: 'blur' }
        ],
        bankId: [
          { required: true, message: '银行机构不能为空', trigger: 'blur' }
        ],
        orderType: [
          { required: true, message: '类型不能为空', trigger: 'blur' }
        ],
        taskIds: [
          { type: 'array', required: true, message: 'atm加钞不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    allDenomlist() {
      const that = this
      const list = this.taskIds.map(elm => {
        return that.routeTaskList.find(item => item.key === elm)
      })
      const obj = {}
      list.forEach(item => {
        const key = item.denomId
        if (!obj[key]) {
          obj[key] = 0
        }
        obj[key] += item.amount
      })
      const dataList = []
      for (const key in obj) {
        dataList.push({
          denomId: key,
          denomType: 0,
          count: 0,
          amount: obj[key]
        })
      }
      return dataList
    }
  },
  watch: {
    usableDenomList: {
      handler(val) {
        this.selectedDenomId1 = val.map(item => item.denomId)
      },
      deep: true
    },
    badDenomList: {
      handler(val) {
        this.selectedDenomId2 = val.map(item => item.denomId)
      },
      deep: true
    },
    goodDenomList: {
      handler(val) {
        this.selectedDenomId3 = val.map(item => item.denomId)
      },
      deep: true
    },
    unclearDenomList: {
      handler(val) {
        this.selectedDenomId4 = val.map(item => item.denomId)
      },
      deep: true
    },
    remnantDenomList: {
      handler(val) {
        this.selectedDenomId4 = val.map(item => item.denomId)
      },
      deep: true
    }
  },
  created() {
    this.getOptions()
    this.getDenomNameList()
  },
  methods: {
    getList() {
      this.listLoading = true
      listOrder(this.listQuery).then(res => {
        this.list = res.data.list
        this.total = res.data.total
        this.listLoading = false
      }).catch(() => {
        this.list = []
        this.total = 0
        this.listLoading = false
      })
    },
    formatType(row, column) {
      const type = row.orderType
      const [obj] = this.typeList.filter(item => item.value === type)
      return obj.label || ''
    },
    formatSubType(row, column) {
      const type = row.subType
      const [obj] = this.subTypeList.filter(item => item.value === type)
      return obj.label || ''
    },
    formatStatus(row, column) {
      const status = row.statusT
      const orderType = row.orderType
      if (status === 4) {
        return orderType === 0 ? '已入库' : '已出库'
      }
      const [obj] = this.statusList.filter(item => item.value === status)
      return obj !== undefined ? (obj.label || '') : ''
    },
    formatMoney(row) {
      return formatMoney(row.orderAmount)
    },
    formatWhOptime(row) {
      const time = row.whOpTime
      if (!time || time === 0) {
        return '-'
      }
      const date = new Date(time)
      return formatdate(date, 'yyyy-MM-dd hh:mm:ss')
    },
    formatAmount(row) {
      return formatMoney(row.amount)
    },
    handleFilter() {
      this.listQuery.page = 1
      this.getList()
    },
    resetForm() {
      this.dataForm = {}
    },
    getOptions() {
      authOption().then(res => {
        this.depOptions = res.data
        if (this.depOptions.length > 0) {
          this.listQuery.departmentId = this.depOptions[0].id
          this.getList()
          bankTradeOption(this.listQuery.departmentId, 4).then(res => {
            this.bankSearchOption = res.data
            this.bankOption = res.data
          })
        }
      })
    },
    getDenomNameList() {
      denomOption().then(res => {
        this.allDenomNameList = res.data
        this.denomNameList = res.data.filter(item => item.version === 0)
        this.badDenomNameList = res.data.filter(item => item.version === 0 || item.version === 1)
        this.remnantDenomNameList = res.data.filter(item => item.version === 0 || item.version === 2)
      })
    },
    normalizer(node) {
      return {
        id: node.id,
        label: node.name,
        children: node.children
      }
    },
    departmentChange(value) {
      this.listQuery.bankId = null
      bankTradeOption(value, 4).then(res => {
        this.listQuery.bankId = null
        this.bankSearchOption = res.data
      })
    },
    departmentChangeForm(value) {
      bankTradeOption(value, 4).then(res => {
        this.bankOption = res.data
        this.$set(this.dataForm, 'bankId', this.bankOpiton.length > 0 ? this.bankOpiton[0].id : null)
      })
    },
    atmDepartmentChange(value) {
      bankTradeOption(value, 4).then(res => {
        this.bankOption = res.data
        this.$set(this.atmDataForm, 'bankId', this.bankOpiton.length > 0 ? this.bankOpiton[0].id : null)
      })
    },
    handleCreate() {
      this.dataForm = {}
      this.usableDenomList = [{}]
      this.badDenomList = [{}]
      this.goodDenomList = [{}]
      this.unclearDenomList = [{}]
      this.remnantDenomList = [{ count: 0, amount: null }]
      this.dialogStatus = 'create'
      this.dialogFormVisible = true
      this.selectedDenomId1 = this.selectedDenomId2 = this.selectedDenomId3 = this.selectedDenomId4 = []
      this.dataForm.orderType = this.orderType
      this.dataForm.subType = this.subType
      this.dataForm.orderDate = new Date(new Date().toLocaleDateString()).getTime()
      this.dataForm.departmentId = this.depOptions[0].id
      this.getList()
      bankTradeOption(this.dataForm.departmentId, 4).then(res => {
        this.bankOpiton = res.data
      })
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    handleInCreate() {
      this.inOutFont = '入'
      this.inOutDialogFormVisible = true
      this.orderType = 0
    },
    handleOutCreate() {
      this.inOutFont = '出'
      this.inOutDialogFormVisible = true
      this.orderType = 1
    },
    nextStep() {
      if (this.subType === 1 || this.orderType === 0) {
        this.handleCreate()
      } else {
        this.isUpdated = false
        this.usableDenomList = [{}]
        this.badDenomList = [{}]
        this.goodDenomList = [{}]
        this.unclearDenomList = [{}]
        this.remnantDenomList = [{ count: 0, amount: null }]
        this.dialogStatus = 'create'
        this.atmDialogFormVisible = true
        this.atmDataForm.orderType = this.orderType
        this.atmDataForm.subType = this.subType
        this.atmDataForm.orderDate = new Date(new Date().toLocaleDateString()).getTime()
        this.atmDataForm.departmentId = this.depOptions[0].id
        this.atmDataForm.vaultRecordList = []
        this.atmDataForm.taskIds = this.taskIds = []
        this.$nextTick(() => {
          this.$refs['atmDataForm'].resetFields()
        })
      }
      this.inOutDialogFormVisible = false
    },
    handleChange(value, direction, movedKeys) {
      this.atmDataForm.taskIds = value
    },
    createData() {
      this.$refs['dataForm'].validate(valid => {
        if (valid) {
          // 验证数据 组合数据 可用券 、残损券、五好券、未清分
          const self = this
          if (this.usableDenomList.length === 0 && this.badDenomList.length === 0 &&
            this.goodDenomList.length === 0 && this.unclearDenomList.length === 0 &&
            this.remnantDenomList.length === 0) {
            self.$message.warning('请至少填写一种券别')
            return
          }
          let index = 0
          let outIndex = 0
          let checkValue = 0
          this.usableDenomList.forEach(s => {
            if (s.amount !== undefined && s.amount !== null) {
              if (self.checkAmount(s.denomId, s.amount)) {
                checkValue++
                return
              }
            }
            if (self.dataForm.orderType === 1) {
              if (s.amount !== undefined && s.amount !== null && +s.amount !== 0) {
                outIndex++
                return
              }
            } else {
              if (s.denomId === undefined || s.denomId === null || s.amount === undefined || s.amount === null || +s.amount === 0) {
                index++
                return
              }
            }
          })
          this.remnantDenomList.forEach(s => {
            console.log(s)
            if (s.amount !== undefined && s.amount !== null) {
              if (self.checkAmount(s.denomId, s.amount)) {
                checkValue++
                return
              }
            }
            if (self.dataForm.orderType === 1) {
              if (s.amount > 0 || s.count > 0) {
                outIndex++
                return
              }
            } else {
              if (s.denomId == null) {
                index++
                return
              }
              if ((s.amount == null || +s.amount === 0) && (s.count == null || +s.count === 0)) {
                index++
                return
              }
            }
          })
          this.goodDenomList.forEach(s => {
            if (s.amount !== undefined && s.amount !== null) {
              if (self.checkAmount(s.denomId, s.amount)) {
                checkValue++
                return
              }
            }
            if (self.dataForm.orderType === 1) {
              if (s.amount !== undefined && s.amount !== null && +s.amount !== 0) {
                outIndex++
                return
              }
            } else {
              if (s.denomId === undefined || s.denomId === null || s.amount === undefined || s.amount === null || +s.amount === 0) {
                index++
                return
              }
            }
            // if (s.amount !== undefined && s.amount !== null) {
            //   if (self.checkAmount(s.denomId, s.amount)) {
            //     checkValue++
            //     return
            //   }
            // }
          })
          this.unclearDenomList.forEach(s => {
            if (s.amount !== undefined && s.amount !== null) {
              if (self.checkAmount(s.denomId, s.amount)) {
                checkValue++
                return
              }
            }
            if (self.dataForm.orderType === 1) {
              if (s.amount !== undefined && s.amount !== null && +s.amount !== 0) {
                outIndex++
                return
              }
            } else {
              if (s.denomId === undefined || s.denomId === null || s.amount === undefined || s.amount === null || +s.amount === 0) {
                index++
                return
              }
            }
            if (s.amount !== undefined && s.amount !== null) {
              if (self.checkAmount(s.denomId, s.amount)) {
                checkValue++
                return
              }
            }
          })
          this.badDenomList.forEach(s => {
            if (s.amount !== undefined && s.amount !== null) {
              if (self.checkAmount(s.denomId, s.amount)) {
                checkValue++
                return
              }
            }
            if (self.dataForm.orderType === 1) {
              if (s.amount !== undefined && s.amount !== null && +s.amount !== 0) {
                outIndex++
                return
              }
            } else {
              if (s.denomId === undefined || s.denomId === null || s.amount === undefined || s.amount === null || +s.amount === 0) {
                index++
                return
              }
            }
            if (s.amount !== undefined && s.amount !== null) {
              if (self.checkAmount(s.denomId, s.amount)) {
                checkValue++
                return
              }
            }
          })
          if (checkValue > 0) {
            self.$message.warning('券别金额不符合要求，请检查')
            return
          }
          console.log(index, outIndex)
          if (index > 0 && self.dataForm.orderType === 0) {
            self.$message.warning('请至少填写一种券别或金额')
            return
          }
          if (outIndex === 0 && self.dataForm.orderType === 1) {
            self.$message.warning('请至少填写一种券别或金额')
            return
          }
          const t = []
          this.usableDenomList.forEach(s => {
            if (s.amount != null && s.amount !== '' && s.amount !== 0) {
              t.push({ denomId: s.denomId, denomType: 0, amount: s.amount, count: 0 })
            }
          })
          this.badDenomList.forEach(s => {
            if (s.amount != null && s.amount !== '' && s.amount !== 0) {
              t.push({ denomId: s.denomId, denomType: 1, amount: s.amount, count: 0 })
            }
          })
          this.goodDenomList.forEach(s => {
            if (s.amount != null && s.amount !== '' && s.amount !== 0) {
              t.push({ denomId: s.denomId, denomType: 2, amount: s.amount, count: 0 })
            }
          })
          this.unclearDenomList.forEach(s => {
            if (s.amount != null && s.amount !== '' && s.amount !== 0) {
              t.push({ denomId: s.denomId, denomType: 3, amount: s.amount, count: 0 })
            }
          })
          this.remnantDenomList.forEach(s => {
            if (s.amount != null && s.amount !== '' && s.count !== 0) {
              t.push({ denomId: s.denomId, denomType: 4, amount: s.amount, count: s.count })
            }
          })
          this.dataForm.vaultRecordList = t
          this.loading = this.$loading({ lock: true, text: '正在加载...请勿进行其它操作', spinner: 'el-icon-loading' })
          addOrder(this.dataForm).then(() => {
            this.getList()
            this.dialogFormVisible = false
            this.$message.success('添加成功')
          }).finally(() => {
            this.loading.close()
          })
        }
      })
    },
    handleDetail(row) {
      this.detailDialogFormVisible = true
      row.orderType === 1 ? this.detailDialogTitle = 'out' : this.detailDialogTitle = 'in'
      this.detailForm = Object.assign({}, row)
      listOrderDetail({ orderId: row.id }).then(res => {
        const denomList = []
        denomList.push({ text: '可用券', denomType: 0, dataList: res.data.usable })
        denomList.push({ text: '残损券', denomType: 1, dataList: res.data.bad })
        denomList.push({ text: '五好券', denomType: 2, dataList: res.data.good })
        denomList.push({ text: '未清分', denomType: 3, dataList: res.data.unclear })
        denomList.push({ text: '尾零钞', denomType: 4, dataList: res.data.remnant })
        this.$set(this.detailForm, 'denomList', denomList)
        this.$set(this.detailForm, 'routeList', res.data.routeList)
        this.auditList = res.data.audit
        this.undoList = res.data.undo
      })
      const orderStepMap = { '-1': 6, '0': 1, '1': 2, '2': 2, '3': 3, '4': 4, '5': 5 }
      // 订单步骤
      this.orderStep = orderStepMap[this.detailForm.statusT]
    },
    getRouteTitle(route) {
      const routeName = route.routeName
      const taskList = route.taskList
      const routeCash = route.cashAmount ? route.cashAmount : 0
      const totalAmount = taskList != null && taskList.length > 0 ? taskList.reduce((p, e) => p + e.amount, 0) : 0
      return routeName + '   ATM任务金额： ' + formatMoney(totalAmount) + '元  线路备用金或其他金额： ' + formatMoney(routeCash) + '元'
    },
    handleUpdate(row) {
      this.subType = row.subType
      if (row.subType === 0 && row.orderType === 1) {
        for (const key in this.atmDataForm) {
          this.atmDataForm[key] = row[key]
        }
        this.atmLoadData(() => {
          this.isUpdated = true
          row.orderType === 0 ? this.inOutFont = '入' : this.inOutFont = '出'
          this.atmDialogFormVisible = true
          this.taskIds = this.routeTaskList.filter(elm => elm.isSelected === 1).map(elm => elm.key)
          this.atmDataForm.taskIds = this.taskIds
          this.$nextTick(() => {
            this.$refs['atmDataForm'].clearValidate()
          })
        })
      } else {
        this.dataForm = Object.assign({}, row)
        this.dialogStatus = 'update'
        this.dialogFormVisible = true
        this.selectedDenomId1 = this.selectedDenomId2 = this.selectedDenomId3 = this.selectedDenomId4 = []
        this.usableDenomList = []
        this.badDenomList = []
        this.goodDenomList = []
        this.unclearDenomList = []
        this.remnantDenomList = []
        bankTradeOption(this.dataForm.departmentId, 4).then(res => {
          this.bankOpiton = res.data
        })

        this.$nextTick(() => {
          this.$refs['dataForm'].clearValidate()
        })
        // 出库类型
        if (this.dataForm.orderType === 1 && this.dataForm.bankId !== '') {
          // 加载数据
          listBank({ bankId: this.dataForm.bankId }).then(res => {
            const data = res.data
            listOrderDetail({ orderId: row.id }).then(result => {
              const usableDenomListT = result.data.usable
              const badDenomListT = result.data.bad
              const goodDenomListT = result.data.good
              const unclearDenomListT = result.data.unclear
              const remnantDenomListT = result.data.remnant
              // id: 90, orderId: 52, denomType: 0, denomId: 67, denomText: "100元", amount: 123
              for (let k = 0; k < data.length; k++) {
                if (data[k].denomType === 0) {
                  // 可用券比较
                  const [obj] = usableDenomListT.filter(item => item.denomId === data[k].denomId)
                  if (obj !== undefined) {
                    this.usableDenomList.push({ id: obj.id, orderId: obj.orderId, denomId: data[k].denomId, denomType: 0, oldAmount: data[k].amount, amount: obj.amount })
                  } else {
                    this.usableDenomList.push({ id: 0, denomId: data[k].denomId, denomType: 0, oldAmount: data[k].amount })
                  }
                }
                if (data[k].denomType === 1) {
                  const [obj2] = badDenomListT.filter(item => item.denomId === data[k].denomId)
                  if (obj2 !== undefined) {
                    this.badDenomList.push({ id: obj2.id, orderId: obj2.orderId, denomId: data[k].denomId, denomType: 1, oldAmount: data[k].amount, amount: obj2.amount })
                  } else {
                    this.badDenomList.push({ id: 0, denomId: data[k].denomId, denomType: 1, oldAmount: data[k].amount })
                  }
                }
                if (data[k].denomType === 2) {
                  const [obj3] = goodDenomListT.filter(item => item.denomId === data[k].denomId)
                  if (obj3 !== undefined) {
                    this.goodDenomList.push({ id: obj3.id, orderId: obj3.orderId, denomId: data[k].denomId, denomType: 2, oldAmount: data[k].amount, amount: obj3.amount })
                  } else {
                    this.goodDenomList.push({ id: 0, denomId: data[k].denomId, denomType: 2, oldAmount: data[k].amount })
                  }
                }
                if (data[k].denomType === 3) {
                  const [obj4] = unclearDenomListT.filter(item => item.denomId === data[k].denomId)
                  if (obj4 !== undefined) {
                    this.unclearDenomList.push({ id: obj4.id, orderId: obj4.orderId, denomId: data[k].denomId, denomType: 3, oldAmount: data[k].amount, amount: obj4.amount })
                  } else {
                    this.unclearDenomList.push({ id: 0, denomId: data[k].denomId, denomType: 3, oldAmount: data[k].amount })
                  }
                }
                if (data[k].denomType === 4) {
                  const [obj5] = remnantDenomListT.filter(item => item.denomId === data[k].denomId)
                  if (obj5 !== undefined) {
                    this.remnantDenomList.push({ id: obj5.id, orderId: obj5.orderId, denomId: data[k].denomId, denomType: 4, oldAmount: data[k].amount, amount: obj5.amount, oldCount: data[k].count, count: obj5.count })
                  } else {
                    this.remnantDenomList.push({ id: 0, denomId: data[k].denomId, denomType: 3, oldAmount: data[k].amount, oldCount: data[k].count, amount: 0, count: 0 })
                  }
                }
              }
            })
          })
        } else {
          listOrderDetail({ orderId: row.id }).then(res => {
            this.usableDenomList = res.data.usable
            this.badDenomList = res.data.bad
            this.goodDenomList = res.data.good
            this.unclearDenomList = res.data.unclear
            this.remnantDenomList = res.data.remnant
          })
        }
      }
    },
    handleSubmitAudit(row) {
      this.$confirm(`是否确定提交审核吗?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.listLoading = true
        submitAudit(row.id).then(() => {
          this.$message.success({ title: '成功', message: '审核已提交，等待审核' })
          this.getList()
        }).finally(() => {
          this.listLoading = false
        })
      })
    },
    handleAudit(row) {
      this.auditDialogFormVisible = true
      this.auditDataForm.id = row.id
    },
    updateData() {
      this.$refs['dataForm'].validate(valid => {
        if (valid) {
          const self = this
          if (this.usableDenomList.length === 0 && this.badDenomList.length === 0 &&
            this.goodDenomList.length === 0 && this.unclearDenomList.length === 0 &&
            this.remnantDenomList.length === 0) {
            self.$message.warning('请至少填写一种券别')
            return
          }
          let usabelFlag = true
          let badFlag = true
          let goodFlag = true
          let unclearFlag = true
          let outIndex = 0
          let checkValue = 0
          this.usableDenomList.forEach(s => {
            if (s.amount !== undefined && s.amount !== null) {
              if (self.checkAmount(s.denomId, s.amount)) {
                checkValue++
                return
              }
            }
            if (self.dataForm.orderType === 1) {
              if (s.amount !== undefined && s.amount !== null) {
                outIndex++
                return
              }
            } else {
              if (s.denomId === undefined || s.denomId === null || s.amount === undefined || s.amount === null) {
                self.$message.warning('请填写可用券券别或金额')
                usabelFlag = false
                return
              }
            }
          })
          this.badDenomList.forEach(s => {
            if (s.amount !== undefined && s.amount !== null) {
              if (self.checkAmount(s.denomId, s.amount)) {
                checkValue++
                return
              }
            }
            if (self.dataForm.orderType === 1) {
              if (s.amount !== undefined && s.amount !== null) {
                outIndex++
                return
              }
            }
            if (s.denomId === undefined || s.denomId === null || s.amount === undefined || s.amount === null) {
              self.$message.warning('请填写残损券券别或金额')
              badFlag = false
              return
            }
          })
          this.goodDenomList.forEach(s => {
            if (s.amount !== undefined && s.amount !== null) {
              if (self.checkAmount(s.denomId, s.amount)) {
                checkValue++
                return
              }
            }
            if (self.dataForm.orderType === 1) {
              if (s.amount !== undefined && s.amount !== null) {
                outIndex++
                return
              }
            } else {
              if (s.denomId === undefined || s.denomId === null || s.amount === undefined || s.amount === null) {
                self.$message.warning('请填写五好券券别或金额')
                goodFlag = false
                return
              }
            }
          })
          this.unclearDenomList.forEach(s => {
            if (s.amount !== undefined && s.amount !== null) {
              if (self.checkAmount(s.denomId, s.amount)) {
                checkValue++
                return
              }
            }
            if (self.dataForm.orderType === 1) {
              if (s.amount !== undefined && s.amount !== null) {
                outIndex++
                return
              }
            } else {
              if (s.denomId === undefined || s.denomId === null || s.amount === undefined || s.amount === null) {
                self.$message.warning('请填写未清分券别或金额')
                unclearFlag = false
                return
              }
            }
          })
          this.remnantDenomList.forEach(s => {
            if (s.amount !== undefined && s.amount !== null) {
              if (self.checkAmount(s.denomId, s.amount)) {
                checkValue++
                return
              }
            }
            if (self.dataForm.orderType === 1) {
              if (s.amount !== undefined && s.amount !== null) {
                outIndex++
                return
              }
            } else {
              if (s.denomId === undefined || s.denomId === null || s.amount === undefined || s.amount === null) {
                self.$message.warning('请填写未清分券别或金额')
                unclearFlag = false
                return
              }
            }
          })
          if (checkValue > 0) {
            self.$message.warning('券别金额有误，请检查！')
            return
          }
          if (self.dataForm.orderType === 0) {
            if (!usabelFlag || !badFlag || !goodFlag || !unclearFlag) { return }
          } else {
            if (outIndex === 0) {
              self.$message.warning('请至少填写未清分券别或金额')
              return
            }
          }
          const t = []
          this.usableDenomList.forEach(s => {
            if (s.amount != null && s.amount !== '' && s.amount !== 0) {
              t.push({ id: s.id, denomId: s.denomId, orderId: this.dataForm.id, denomType: 0, amount: s.amount })
            }
          })
          this.badDenomList.forEach(s => {
            if (s.amount != null && s.amount !== '' && s.amount !== 0) {
              t.push({ id: s.id, denomId: s.denomId, orderId: this.dataForm.id, denomType: 1, amount: s.amount })
            }
          })
          this.goodDenomList.forEach(s => {
            if (s.amount != null && s.amount !== '' && s.amount !== 0) {
              t.push({ id: s.id, denomId: s.denomId, orderId: this.dataForm.id, denomType: 2, amount: s.amount })
            }
          })
          this.unclearDenomList.forEach(s => {
            if (s.amount != null && s.amount !== '' && s.amount !== 0) {
              t.push({ id: s.id, denomId: s.denomId, orderId: this.dataForm.id, denomType: 3, amount: s.amount })
            }
          })
          this.remnantDenomList.forEach(s => {
            if (s.amount !== null && s.amount !== '' && s.count !== 0) {
              t.push({ id: s.id, denomId: s.denomId, orderId: this.dataForm.id, denomType: 4, amount: s.amount, count: s.count })
            }
          })
          this.dataForm.vaultRecordList = t
          this.loading = this.$loading({ lock: true, text: '正在加载...请勿进行其它操作', spinner: 'el-icon-loading' })
          updateOrder(this.dataForm).then(() => {
            this.dialogFormVisible = false
            this.$message.success('更新成功')
            this.getList()
          }).finally(() => {
            this.loading.close()
          })
        }
      })
    },
    createATMData() {
      this.$refs['atmDataForm'].validate(valid => {
        if (valid) {
          this.atmDataForm.vaultRecordList = this.allDenomlist
          this.openLoading()
          addOrder(this.atmDataForm).then(() => {
            this.getList()
            this.atmDialogFormVisible = false
            this.$notify.success({
              title: '成功',
              message: '更新成功'
            })
          }).finally(() => {
            this.loading.close()
          })
        }
      })
    },
    updateATMData() {
      this.$refs['atmDataForm'].validate(valid => {
        if (valid) {
          this.atmDataForm.vaultRecordList = this.allDenomlist
          this.openLoading()
          updateOrder(this.atmDataForm).then(() => {
            this.getList()
            this.atmDialogFormVisible = false
            this.$notify.success({
              title: '成功',
              message: '更新成功'
            })
          }).finally(() => {
            this.loading.close()
          })
        }
      })
    },
    auditData() {
      this.$refs['auditForm'].validate(valid => {
        if (valid) {
          this.loading = this.$loading({ lock: true, text: '正在加载...请勿进行其它操作', spinner: 'el-icon-loading' })
          auditOrder(this.auditDataForm).then(() => {
            this.auditDialogFormVisible = false
            this.$message.success('审核成功')
            this.getList()
          }).finally(() => {
            this.loading.close()
          })
        }
      })
    },
    handleDelete(row) {
      this.$confirm('确定删除吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteOrder(row.id).then(() => {
          this.$message.success('删除成功')
          const index = this.list.indexOf(row)
          this.list.splice(index, 1)
        })
      })
    },
    handleUndo(row) {
      this.$confirm('确定撤销吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        undoOrder(row.id).then(() => {
          this.$message.success('撤销审核中，请等待...')
          this.getList()
        })
      })
    },
    loadData() {
      if (this.dataForm.orderType === 1 && this.dataForm.bankId !== '') {
        this.usableDenomList = []
        this.badDenomList = []
        this.goodDenomList = []
        this.unclearDenomList = []
        this.remnantDenomList = []
        // 加载数据
        listBank({ bankId: this.dataForm.bankId }).then(res => {
          const data = res.data
          for (let k = 0; k < data.length; k++) {
            if (data[k].denomType === 0) {
              this.usableDenomList.push({ denomId: data[k].denomId, denomType: 0, oldAmount: data[k].amount })
            }
            if (data[k].denomType === 1) {
              this.badDenomList.push({ denomId: data[k].denomId, denomType: 1, oldAmount: data[k].amount })
            }
            if (data[k].denomType === 2) {
              this.goodDenomList.push({ denomId: data[k].denomId, denomType: 2, oldAmount: data[k].amount })
            }
            if (data[k].denomType === 3) {
              this.unclearDenomList.push({ denomId: data[k].denomId, denomType: 3, oldAmount: data[k].amount })
            }
            if (data[k].denomType === 4) {
              this.remnantDenomList.push({ denomId: data[k].denomId, denomType: 4, oldAmount: data[k].amount, oldCount: data[k].count, count: 0, amount: '0.00' })
            }
          }
        })
      } else {
        this.usableDenomList = []
        this.badDenomList = []
        this.goodDenomList = []
        this.unclearDenomList = []
        this.remnantDenomList = []
      }
    },
    atmLoadData(fn) {
      // TODO
      if (this.atmDataForm.bankId !== '' && this.atmDataForm.orderDate !== '') {
        // 加载数据
        const params = { departmentId: this.atmDataForm.departmentId, bankId: this.atmDataForm.bankId, taskDate: this.atmDataForm.orderDate, orderId: this.atmDataForm.id }
        getTaskList(params).then(res => {
          const arr = []
          for (let i = 0; i < res.data.length; i++) {
            arr.push({
              key: res.data[i].id,
              routeName: res.data[i].routeName,
              label: res.data[i].terNo,
              amount: res.data[i].amount,
              denomId: res.data[i].denomId,
              isSelected: res.data[i].isSelected
            })
          }
          this.routeTaskList = arr
          typeof (fn) === 'function' ? fn() : undefined
        })
      } else {
        this.routeTaskList = []
      }
    },
    formatDenom(id) {
      return this.allDenomNameList.find(item => item.id === +id).value
    },
    checkAmount(id, amount) {
      const item = this.allDenomNameList.find(item => item.id === +id)
      let value
      if (item) {
        value = item.value
        return amount % value
      }
      return false
    },
    formatDenomMoney(num) {
      return formatMoney(num)
    },
    openLoading() {
      this.loading = this.$loading({
        lock: true,
        text: '正在加载...请勿进行其它操作',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      })
    },
    denomChange(val, row) {
      const value = this.remnantDenomNameList.find(item => item.id === val).value
      if (value === 0) {
        row.amount = 0
      }
    },
    countChange(val, row) {
      if (val && row.denomId) {
        const value = this.remnantDenomNameList.find(item => item.id === row.denomId).value
        row.amount = value * val
      }
    },
    amountChange(val, row) {
      if (!isNaN(val) && +val !== null && row.denomId) {
        const value = this.remnantDenomNameList.find(item => item.id === row.denomId).value
        if (value !== 0) {
          row.count = Math.ceil(+val / value)
        }
      }
    },
    getDisabled(val, arr) {
      if (arr.find(item => item === val)) {
        return true
      }
      return false
    }
  }
}
</script>

<style scoped lang="scss">
  .filter-container *:nth-child(n+2) {
    margin-left: 10px;
  }
  .timeline-item span{
    margin-right: 15px;
  }
  ::v-deep .el-transfer-panel{
    width: 210px
  }
</style>
<style>
  .transfer-footer {
    margin-left: 20px;
    padding: 6px 5px;
  }
  .el-transfer__buttons{
    padding: 0 10px !important;
  }
</style>
