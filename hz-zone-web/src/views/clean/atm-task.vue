<template>
  <div class="app-container">
    <el-tabs v-model="activeName" @tab-click="tabClick">
      <el-tab-pane label="线路" name="first" />
      <el-tab-pane label="车组" name="second" />
    </el-tabs>
    <el-row :gutter="20">
      <div class="filter-container">
        <div style="margin-left: 10px">
          <el-select
            v-model="listQuery.departmentId"
            placeholder="请选择所属部门"
            class="filter-item"
            @change="
              getTree();
              getbankTopOption();
            "
          >
            <el-option
              v-for="item in options.departmentId"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
          <el-date-picker
            v-model="listQuery.routeDate"
            v-permission="['/base/route/option']"
            value-format="timestamp"
            type="date"
            placeholder="选择日期"
            :clearable="false"
            class="filter-item"
            @change="getTree()"
          />
          <el-select
            v-model="listQuery.bankId"
            placeholder="请选择所属银行"
            class="filter-item"
            clearable
            @change="
              getTree();
              routeListQuery.bankId = listQuery.bankId;
            "
          >
            <el-option
              v-for="item in options.bankId"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </div>
        <div>
          <el-button
            v-permission="['/base/atmTask/saveClean']"
            class="filter-item"
            type="primary"
            icon="el-icon-edit"
            @click="handleCreate('清机')"
          >添加清机</el-button>
          <el-button
            v-permission="['/base/atmTask/saveRepair']"
            class="filter-item"
            type="primary"
            icon="el-icon-edit"
            @click="handleCreate('维修')"
          >添加维修</el-button>
          <!-- <el-button
            v-permission="['/base/atmTask/saveTemplateClean']"
            :disabled="
              selectedRoute.status == null ||
                selectedRoute.status === 4 ||
                selectedRoute.type === 1
            "
            class="filter-item"
            type="primary"
            icon="el-icon-edit"
            @click="handleTemplateCreate()"
          >模板创建</el-button> -->
          <el-button
            v-permission="['/base/vaultOrder/quickOut']"
            class="filter-item"
            type="primary"
            icon="el-icon-s-finance"
            @click="handleOut()"
          >一键出库</el-button>
          <el-button
            v-permission="['/base/atmTask/importAtmTask']"
            class="filter-item"
            type="primary"
            icon="el-icon-upload2"
            @click="handleExport"
          >导入任务</el-button>
          <el-button
            v-permission="['/base/atmTask/saveRepair']"
            :disabled="batchList.length === 0"
            class="filter-item"
            type="success"
            icon="el-icon-finished"
            @click="batchConfirm()"
          >批量确认</el-button>
          <el-button
            v-permission="['/base/atmTask/batchCancel']"
            class="filter-item"
            type="warning"
            icon="el-icon-delete"
            @click="batchCancel()"
          >批量撤销</el-button>
          <el-button
            v-permission="['/base/atmTask/move']"
            class="filter-item"
            type="primary"
            icon="el-icon-notebook-2"
            @click="taskAllocate()"
          >任务分配</el-button>
        </div>
      </div>
    </el-row>
    <el-row :gutter="20">
      <el-col :span="4">
        <el-scrollbar style="height: 500px" class="scrollbar">
          <el-tree
            ref="listTree"
            :data="routeDataOption"
            node-key="value"
            highlight-current
            @node-click="handleNodeClick"
          >
            <span slot-scope="{ data }" class="custom-tree-node">
              <span style="width: 30%">{{ data.routeNo }}</span>
              <span>{{ data.routeName }}</span>
            </span>
          </el-tree>
        </el-scrollbar>
      </el-col>
      <el-col :span="20">
        <div v-if="routeDataOption.length > 0">
          <my-table
            ref="taskTable"
            :list-loading="listLoading"
            :data-list="list"
            :table-list="tableList"
            :selection="{ name: 'statusT', optional: 0 }"
            @selectionChange="selectionChange"
          >
            <template v-slot:operate>
              <el-table-column
                header-align="center"
                align="right"
                label="操作"
                class-name="small-padding fixed-width"
                width="230"
                fixed="right"
              >
                <template slot-scope="scope">
                  <el-button
                    v-show="scope.row.statusT == 0"
                    v-permission="['/base/atmTask/confirm']"
                    type="success"
                    size="mini"
                    @click="handleConfirm(scope.row)"
                  >确认</el-button>
                  <el-button
                    v-show="scope.row.statusT == 1"
                    v-permission="['/base/atmTask/revoke']"
                    type="warning"
                    size="mini"
                    @click="handleRevoke(scope.row)"
                  >撤销</el-button>
                  <el-button
                    v-permission="['/base/atmTask/info']"
                    type="primary"
                    size="mini"
                    @click="handleDetail(scope.row)"
                  >详情</el-button>
                  <el-button
                    v-permission="['/base/routeTemplate/delete']"
                    :disabled="scope.row.statusT != 0"
                    type="danger"
                    size="mini"
                    @click="handleDelete(scope.row)"
                  >删除</el-button>
                </template>
              </el-table-column>
            </template>
          </my-table>
          <div
            v-if="bankSum.length > 0 && listQuery.routeType === 0"
            class="sum"
          >
            <div style="width: 1000px">
              <el-form
                label-position="right"
                label-width="160px"
                inline
                class="bank-sum"
              >
                <el-form-item label="总任务数：">
                  <span>{{ bankSumTotal.totalTask }}</span>
                </el-form-item>
                <el-form-item label="撤销任务数：">
                  <span>{{ bankSumTotal.cancelTask }}</span>
                </el-form-item>
                <el-form-item label="（100元）撤销金额：">
                  <span>{{ bankSumTotal.hdCancelTotal }}</span>
                </el-form-item>
                <el-form-item label="（10元）撤销金额：">
                  <span>{{ bankSumTotal.tenCancelTotal }}</span>
                </el-form-item>
                <el-form-item label="总金额：">
                  <span>{{ bankSumTotal.amountTotal / 1000 }}</span>
                </el-form-item>
                <el-form-item label="新增任务数：">
                  <span>{{ bankSumTotal.newTask }}</span>
                </el-form-item>
                <el-form-item label="新增金额：">
                  <span>{{ bankSumTotal.hdNewTotal }}</span>
                </el-form-item>
                <el-form-item label="新增金额：">
                  <span>{{ bankSumTotal.tenNewTotal }}</span>
                </el-form-item>
                <el-form-item label="清机加钞数：">
                  <span>{{ bankSumTotal.cleanTotal }}</span>
                </el-form-item>
                <el-form-item />
                <el-form-item label="备用金金额：">
                  <span>{{ bankSumTotal.hdAddTotal }}</span>
                </el-form-item>
                <el-form-item label="备用金金额：">
                  <span>{{ bankSumTotal.tenAddTotal }}</span>
                </el-form-item>
                <el-form-item label="维护数：">
                  <span>{{ bankSumTotal.defend }}</span>
                </el-form-item>
                <el-form-item />
                <el-form-item label="可用金额：">
                  <span>{{
                    bankSumTotal.hdAddTotal +
                      bankSumTotal.hdCancelTotal -
                      bankSumTotal.hdNewTotal
                  }}</span>
                </el-form-item>
                <el-form-item label="可用金额：">
                  <span>{{
                    bankSumTotal.tenAddTotal +
                      bankSumTotal.tenCancelTotal -
                      bankSumTotal.tenNewTotal
                  }}</span>
                </el-form-item>
              </el-form>
            </div>
          </div>
        </div>
        <el-empty v-else style="height: 500px" description="暂无数据" />
      </el-col>
    </el-row>
    <!-- 添加清机 -->
    <el-dialog
      title="添加清机"
      :visible.sync="cleanFormVisible"
      :close-on-click-modal="false"
      width="90%"
    >
      <el-form
        ref="cleanForm"
        :rules="cleanRules"
        :model="cleanForm"
        status-icon
        label-position="right"
        label-width="100px"
        style="width: 90%; margin-left: 50px"
      >
        <el-form-item label="任务日期">
          <span>{{ formatDate(cleanForm.taskDate) }}</span>
        </el-form-item>
        <el-form-item label="清机任务" prop="atmTashCleanList">
          <el-table
            :data="cleanForm.atmTashCleanList"
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
                  @click="cleanForm.atmTashCleanList.push({ atmId: null })"
                />
              </template>
              <template slot-scope="scope">
                <el-button
                  plain
                  type="danger"
                  icon="el-icon-remove-outline"
                  @click="cleanForm.atmTashCleanList.splice(scope.$index, 1)"
                />
              </template>
            </el-table-column>
            <!-- <el-table-column align="center" label="清机机构" width="300">
              <template slot-scope="scope">
                <el-form-item
                  :prop="'atmTashCleanList.' + scope.$index + '.bankId'"
                  label-width="0px"
                  :rules="cleanListRules.bankId"
                >
                  <treeSelect
                    v-model="scope.row.bankId"
                    :disable-branch-nodes="true"
                    :append-to-body="true"
                    z-index="9999"
                    :normalizer="normalizer"
                    :show-count="true"
                    :options="options.bankId"
                    placeholder="清机机构"
                    @select="
                      treeSelectChange($event);
                      scope.row.atmId = null;
                    "
                  >
                    <label
                      slot="option-label"
                      slot-scope="{
                        node,
                        shouldShowCount,
                        count,
                        labelClassName,
                        countClassName,
                      }"
                      style="font-size: 14px"
                      :class="labelClassName"
                    >
                      {{ node.label }}
                      <span
                        v-if="shouldShowCount"
                        :class="countClassName"
                      >({{ count }})</span>
                    </label></treeSelect>
                </el-form-item>
              </template>
            </el-table-column> -->
            <el-table-column align="center" label="ATM设备" width="380">
              <template slot-scope="scope">
                <el-form-item
                  :prop="'atmTashCleanList.' + scope.$index + '.atmId'"
                  label-width="0px"
                  :rules="cleanListRules.atmId"
                >
                  <el-select
                    v-model="scope.row.atmId"
                    filterable
                    placeholder="请选择设备编号"
                    style="width: 100%"
                  >
                    <el-option
                      v-for="item in options.atmId"
                      :key="item.id"
                      :label="`${item.terNo}/${item.bankName}/${item.routeNo}`"
                      :value="item.id"
                    />
                  </el-select>
                </el-form-item>
              </template>
            </el-table-column>
            <el-table-column
              align="center"
              label="加钞金额（十万元）"
              width="160"
            >
              <template slot-scope="scope">
                <el-form-item
                  :prop="'atmTashCleanList.' + scope.$index + '.amount'"
                  label-width="0px"
                  :rules="cleanListRules.amount"
                >
                  <el-input
                    v-model="scope.row.amount"
                    class="number"
                    :maxlength="32"
                  />
                </el-form-item>
              </template>
            </el-table-column>
            <el-table-column align="center" label="任务备注" min-width="200">
              <template slot-scope="scope">
                <el-form-item
                  :prop="'atmTashCleanList.' + scope.$index + '.comments'"
                  label-width="0px"
                >
                  <el-input v-model="scope.row.comments" :maxlength="32" />
                </el-form-item>
              </template>
            </el-table-column>
          </el-table>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="cleanFormVisible = false">取消</el-button>
        <el-button type="primary" @click="newData()">确定</el-button>
      </div>
    </el-dialog>
    <!-- 添加维修 -->
    <el-dialog
      title="添加维修"
      :visible.sync="repairFormVisible"
      :close-on-click-modal="false"
      width="90%"
    >
      <el-form
        ref="repairForm"
        :rules="repairRules"
        :model="repairForm"
        status-icon
        label-position="right"
        label-width="100px"
        style="width: 90%; margin-left: 50px"
      >
        <el-form-item label="任务日期">
          <span>{{ formatDate(repairForm.taskDate) }}</span>
        </el-form-item>
        <el-form-item label="维修任务" prop="atmTashRepairList">
          <el-table
            :data="repairForm.atmTashRepairList"
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
                  @click="repairForm.atmTashRepairList.push({ atmId: null })"
                />
              </template>
              <template slot-scope="scope">
                <el-button
                  plain
                  type="danger"
                  icon="el-icon-remove-outline"
                  @click="repairForm.atmTashRepairList.splice(scope.$index, 1)"
                />
              </template>
            </el-table-column>
            <!-- <el-table-column align="center" label="清机机构" width="300">
              <template slot-scope="scope">
                <el-form-item
                  :prop="'atmTashRepairList.' + scope.$index + '.bankId'"
                  label-width="0px"
                  :rules="repairListRules.bankId"
                >
                  <treeSelect
                    v-model="scope.row.bankId"
                    :disable-branch-nodes="true"
                    :append-to-body="true"
                    z-index="9999"
                    :normalizer="normalizer"
                    :show-count="true"
                    :options="options.bankId"
                    placeholder="清机机构"
                    @select="
                      treeSelectChange($event);
                      scope.row.atmId = null;
                    "
                  >
                    <label
                      slot="option-label"
                      slot-scope="{
                        node,
                        shouldShowCount,
                        count,
                        labelClassName,
                        countClassName,
                      }"
                      style="font-size: 14px"
                      :class="labelClassName"
                    >
                      {{ node.label }}
                      <span
                        v-if="shouldShowCount"
                        :class="countClassName"
                      >({{ count }})</span>
                    </label></treeSelect>
                </el-form-item>
              </template>
            </el-table-column> -->
            <el-table-column align="center" label="ATM设备" width="340">
              <template slot-scope="scope">
                <el-form-item
                  :prop="'atmTashRepairList.' + scope.$index + '.atmId'"
                  label-width="0px"
                  :rules="repairListRules.atmId"
                >
                  <el-select
                    v-model="scope.row.atmId"
                    filterable
                    placeholder="请选择设备编号"
                    style="width: 100%"
                  >
                    <el-option
                      v-for="item in options.atmId"
                      :key="item.id"
                      :label="`${item.terNo}/${item.bankName}/${item.routeNo}`"
                      :value="item.id"
                    />
                  </el-select>
                </el-form-item>
              </template>
            </el-table-column>
            <el-table-column align="center" label="故障部位" max-width="200">
              <template slot-scope="scope">
                <el-form-item
                  :prop="'atmTashRepairList.' + scope.$index + '.content'"
                  label-width="0px"
                  :rules="repairListRules.content"
                >
                  <el-select
                    v-model="scope.row.content"
                    filterable
                    allow-create
                    default-first-option
                    placeholder="请输入或选择故障部位"
                    style="width: 100%"
                  >
                    <el-option
                      v-for="item in faultOptions"
                      :key="item"
                      :label="item"
                      :value="item"
                    />
                  </el-select>
                </el-form-item>
              </template>
            </el-table-column>
            <el-table-column align="center" label="公司方" max-width="200">
              <template slot-scope="scope">
                <el-form-item
                  :prop="'atmTashRepairList.' + scope.$index + '.repairCompany'"
                  label-width="0px"
                >
                  <el-input v-model="scope.row.repairCompany" :maxlength="32" />
                </el-form-item>
              </template>
            </el-table-column>
            <el-table-column align="center" label="预约时间" max-width="200">
              <template slot-scope="scope">
                <el-form-item
                  :prop="'atmTashRepairList.' + scope.$index + '.planTime'"
                  label-width="0px"
                >
                  <el-time-select
                    v-model="scope.row.planTime"
                    placeholder="起始时间"
                    default-value="08:00"
                    :picker-options="{
                      start: '00:00',
                      step: '00:15',
                      end: '24:00',
                    }"
                    style="width: 100%"
                  />
                </el-form-item>
              </template>
            </el-table-column>
          </el-table>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="repairFormVisible = false">取消</el-button>
        <el-button type="primary" @click="newData()">确定</el-button>
      </div>
    </el-dialog>
    <!-- 详情 -->
    <el-dialog
      title="详情"
      :visible.sync="dialogDetailFormVisible"
      :close-on-click-modal="false"
      width="80%"
    >
      <el-card v-if="atmTaskRepair" class="box-card" shadow="hover">
        <div slot="header" class="clearfix">
          <span class="title">维修</span>
        </div>
        <el-form label-width="200px" inline>
          <el-form-item label="ATM运行状态">
            <span>{{ formatRunStatus(atmTaskRepair.atmRunStatus) }}</span>
          </el-form-item>
          <el-form-item label="卡钞金额">
            <span>{{ atmTaskRepair.stuckAmount }}（元）</span>
          </el-form-item>
          <el-form-item label="预约时间">
            <span>{{ formatTime(atmTaskRepair.planTime) }}</span>
          </el-form-item>
          <el-form-item label="维修内容">
            <span>{{ atmTaskRepair.content }}</span>
          </el-form-item>
          <el-form-item label="维修公司">
            <span>{{ atmTaskRepair.repairCompany }}</span>
          </el-form-item>
          <el-form-item label="业务员到达时间">
            <span>{{ formatTime(atmTaskRepair.arriveTime) }}</span>
          </el-form-item>
          <el-form-item label="厂家到达时间">
            <span>{{ formatTime(atmTaskRepair.engineerArriveTime) }}</span>
          </el-form-item>
          <el-form-item label="维修人员">
            <span>{{ atmTaskRepair.engineerName }}</span>
          </el-form-item>
          <el-form-item label="是否更换钞箱">
            <span>{{ formatResult(atmTaskRepair.cashboxReplace) }}</span>
          </el-form-item>
          <el-form-item label="是否有遗留现金">
            <span>{{ formatResult(atmTaskRepair.cashInBox) }}</span>
          </el-form-item>
          <el-form-item label="故障描述">
            <span>{{ atmTaskRepair.description }}</span>
          </el-form-item>
          <el-form-item label="处理结果说明">
            <span>{{ atmTaskRepair.dealComments }}</span>
          </el-form-item>
          <el-form-item label="完成时间">
            <span>{{ atmTaskRepair.finishTime }}</span>
          </el-form-item>
          <el-form-item label="备注说明">
            <span>{{ atmTaskRepair.comments }}</span>
          </el-form-item>
        </el-form>
      </el-card>
      <el-card v-if="atmTaskClean" class="box-card" shadow="hover">
        <div slot="header" class="clearfix">
          <span class="title">加钞</span>
        </div>
        <el-form
          ref="atmTaskClean"
          :model="atmTaskClean"
          label-width="200px"
          inline
        >
          <el-form-item label="加钞金额（十万元）" style="width: 50%">
            <span>{{ atmTaskClean.amount }}</span>
          </el-form-item>
          <el-form-item label="加钞钞盒" style="width: 50%">
            <el-button
              v-for="item in atmTaskClean.cashboxMap"
              :key="item.id"
              size="small"
              type="primary"
              plain
              @click="cashboxDetail(item.id)"
            >{{ item.boxNo }}</el-button>
          </el-form-item>
          <el-form-item label="现场清点标志" style="width: 50%">
            <span>{{ formatResult(atmTaskClean.clearSite) }}</span>
          </el-form-item>
          <el-form-item label="回笼钞盒" style="width: 50%">
            <span>{{ atmTaskClean.barboxList.toString() }}</span>
          </el-form-item>
          <el-form-item label="清机密码员" style="width: 50%">
            <span>{{ atmTaskClean.cleanOpManName }}</span>
          </el-form-item>
          <el-form-item label="ATM运行状态" style="width: 50%">
            <span>{{ formatAtmStatus(atmTaskClean.atmRunStatus) }}</span>
          </el-form-item>
          <el-form-item label="清机钥匙员" style="width: 50%">
            <span>{{ atmTaskClean.cleanKeyManName }}</span>
          </el-form-item>
          <el-form-item label="卡钞金额（元）" style="width: 50%">
            <span>{{ atmTaskClean.stuckAmount }}</span>
          </el-form-item>
        </el-form>
      </el-card>
      <el-card v-if="atmTaskCheck" class="box-card" shadow="hover">
        <div slot="header" class="clearfix">
          <span class="title">巡检</span>
        </div>
        <el-form
          ref="atmTaskCheck"
          :model="atmTaskCheck"
          label-width="200px"
          inline
        >
          <el-form-item label="插卡口是否正常" style="width: 50%">
            <span>{{
              formatResult(atmTaskCheck.checkItemResult.cardReader)
            }}</span>
          </el-form-item>
          <el-form-item label="有无安装非法装置" style="width: 50%">
            <span>{{
              formatResult(atmTaskCheck.checkItemResult.thingInstall)
            }}</span>
          </el-form-item>
          <el-form-item label="出钞口是否正常" style="width: 50%">
            <span>{{
              formatResult(atmTaskCheck.checkItemResult.cashOutlet)
            }}</span>
          </el-form-item>
          <el-form-item label="有无非法张贴物" style="width: 50%">
            <span>{{
              formatResult(atmTaskCheck.checkItemResult.thingStick)
            }}</span>
          </el-form-item>
          <el-form-item label="密码键盘防窥罩是否完好" style="width: 50%">
            <span>{{
              formatResult(atmTaskCheck.checkItemResult.keypadMask)
            }}</span>
          </el-form-item>
          <el-form-item label="功能标识，操作提示是否齐全" style="width: 50%">
            <span>{{
              formatResult(atmTaskCheck.checkItemResult.operationTips)
            }}</span>
          </el-form-item>
        </el-form>
      </el-card>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogDetailFormVisible = false">关闭</el-button>
      </div>
    </el-dialog>
    <!-- 模板创建 -->
    <el-dialog
      title="模板创建"
      :visible.sync="dialogTemFormVisible"
      :close-on-click-modal="false"
      width="80%"
    >
      <el-form ref="atmTaskRepair" :model="atmTaskRepair" label-width="140px">
        <el-form-item
          label="线路名称"
          style="width: 25%; display: inline-block"
        >
          <span>{{ selectedRoute.routeName }}</span>
        </el-form-item>
        <el-form-item
          label="任务日期"
          style="width: 25%; display: inline-block"
        >
          <span>{{ formatDate(templateForm.taskDate) }} </span>
        </el-form-item>
        <el-form-item label="所属银行">
          <treeSelect
            v-model="tempListQuery.bankId"
            :normalizer="normalizer"
            :show-count="true"
            :options="tempBankClearTree"
            placeholder="请选择银行网点"
            style="width: 20%; display: inline-block"
          />
          <el-button
            type="primary"
            class="manage-btn"
            @click="getTempList()"
          >查找</el-button>
        </el-form-item>
        <el-table
          ref="tempList"
          v-loading="tempListLoading"
          :data="tempList"
          height="500px"
          style="width: 100%"
          show-summary
          :summary-method="getSummaries"
        >
          <el-table-column
            v-for="(item, index) in tempTableList"
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
            label="加钞金额（十万元）"
            width="160"
            prop="amount"
          >
            <template slot-scope="scope">
              <el-input
                v-model="scope.row.amount"
                :maxlength="32"
                @keyup.native="scope.row.amount = formatNum($event)"
              />
            </template>
          </el-table-column>
          <el-table-column align="center" label="任务备注" min-width="200">
            <template slot-scope="scope">
              <el-input
                v-model="scope.row.comments"
                :disabled="scope.row.amount == null || scope.row.amount == ''"
                :maxlength="32"
              />
            </template>
          </el-table-column>
        </el-table>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogTemFormVisible = false">取消</el-button>
        <el-button type="primary" @click="newTemplate()">确定</el-button>
      </div>
    </el-dialog>
    <!-- 出库 -->
    <el-dialog
      title="详情"
      :visible.sync="outFormVisible"
      :close-on-click-modal="false"
      width="80%"
    >
      <el-form label-width="80px" label-position="left" inline>
        <el-form-item v-if="options.departmentId.length > 0" label="所属部门">
          <span>{{
            options.departmentId.filter(
              (item) => item.id === listQuery.departmentId
            )[0].name
          }}</span>
        </el-form-item>
        <el-form-item label="任务时间">
          <span>{{ formatDate(listQuery.routeDate) }}</span>
        </el-form-item>
      </el-form>
      <div>
        <my-table
          ref="myTable"
          :list-loading="outListLoading"
          :data-list="outList"
          :table-list="outTableList"
          :height="'300'"
          @rowClick="rowClick"
        >
          <template v-slot:operate>
            <el-table-column
              align="center"
              label="操作"
              class-name="small-padding fixed-width"
              width="140"
            >
              <template slot-scope="scope">
                <el-button
                  v-permission="['/base/vaultOrder/quickOut']"
                  type="success"
                  size="mini"
                  @click="handleOutConfirm(scope.row)"
                >快捷出库单</el-button>
              </template>
            </el-table-column>
          </template>
        </my-table>
        <el-card v-show="outList.length > 0" class="box-card">
          <div slot="header" class="clearfix">
            <span class="title">出库详情</span>
          </div>
          <my-table
            :data-list="denomList"
            :table-list="denomTableList"
            :height="'300'"
          />
        </el-card>
      </div>
    </el-dialog>
    <!-- 导入清机任务 -->
    <el-dialog
      title="上传文件"
      :visible.sync="exportVisible"
      :close-on-click-modal="false"
      width="60%"
    >
      <el-form
        ref="exportForm"
        :rules="exportRules"
        :model="exportForm"
        status-icon
        label-position="right"
        label-width="100px"
        style="width: 80%; margin-left: 8%"
      >
        <el-form-item label="权限部门" prop="departmentId">
          <el-select
            v-model="exportForm.departmentId"
            placeholder="请选择所属部门"
            style="width: 40%"
            @change="getExportBankTopOption"
          >
            <el-option
              v-for="item in options.departmentId"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="日期" prop="taskDate">
          <el-date-picker
            v-model="exportForm.taskDate"
            value-format="timestamp"
            type="date"
            placeholder="请选择日期"
            style="width: 40%"
          />
        </el-form-item>
        <el-form-item label="所属银行" prop="bankType">
          <el-select
            v-model="exportForm.bankType"
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
            :on-success="exportSuccess"
            :on-change="exportChange"
            :on-error="exportError"
            class="upload-demo"
            :action="importPath"
            accept=".xlsx,.xls"
            :before-upload="beforeUpload"
            :file-list="exportForm.fileList"
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
        <el-button @click="exportVisible = false">取消</el-button>
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
          <el-form-item label="日期" style="width: 20%">
            <span>{{ formatDate(confirmForm.taskDate) }}</span>
          </el-form-item>
          <el-form-item label="银行" style="width: 20%">
            <span>{{ bankName }}</span>
          </el-form-item>
          <el-form-item label="文件名" style="width: 60%">
            <span>{{ confirmForm.fileName }}</span>
          </el-form-item>
        </el-form>
        <my-table
          :list-loading="confirmListLoading"
          :data-list="confirmList"
          :table-list="confirmTableList"
          height="360px"
          style="margin: 12px 0"
          :selection="true"
          @selectionChange="comfirmListChange"
        />
        <el-form label-position="right" inline class="confirm-info">
          <el-form-item label="总线路数" style="width: 20%">
            <span>{{ confirmList.length }}</span>
          </el-form-item>
          <el-form-item label="总任务数" style="width: 20%">
            <span>{{ confirmTerTotal }}</span>
          </el-form-item>
          <el-form-item label="总金额" style="width: 60%">
            <span>{{ formatMoney(confirmAmountTotal) }}（十万）</span>
          </el-form-item>
        </el-form>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="confirmVisibleCancel()">取消</el-button>
        <el-button
          v-permission="['/base/atmTask/saveImportClean']"
          :disabled="confirmForm.atmTaskCleanImportBatchVOs.length === 0"
          type="primary"
          @click="confirmExport()"
        >确认</el-button>
      </div>
    </el-dialog>
    <!-- 批量撤销 -->
    <el-dialog
      title="批量撤销"
      :visible.sync="cancelFormVisible"
      :close-on-click-modal="false"
      width="90%"
    >
      <el-form
        ref="cancelForm"
        :rules="cancelRules"
        :model="cancelForm"
        status-icon
        label-position="right"
        label-width="100px"
        style="width: 90%; margin-left: 50px"
      >
        <el-form-item label="任务日期">
          <span>{{ formatDate(cancelForm.taskDate) }}</span>
        </el-form-item>
        <el-form-item label="批量撤销" prop="taskIds">
          <el-table
            :data="cancelForm.taskIds"
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
                  @click="cancelForm.taskIds.push({ id: null })"
                />
              </template>
              <template slot-scope="scope">
                <el-button
                  plain
                  type="danger"
                  icon="el-icon-remove-outline"
                  @click="cancelForm.taskIds.splice(scope.$index, 1)"
                />
              </template>
            </el-table-column>
            <el-table-column align="center" label="ATM设备" width="380">
              <template slot-scope="scope">
                <el-form-item
                  :prop="'taskIds.' + scope.$index + '.id'"
                  label-width="0px"
                  :rules="[
                    { required: true, message: '请填写', trigger: 'blur' },
                  ]"
                >
                  <el-select
                    v-model="scope.row.id"
                    filterable
                    placeholder="请选择撤销任务"
                    style="width: 100%"
                    @change="taskIdChange($event, scope.row, cancelOption)"
                  >
                    <el-option
                      v-for="item in cancelOption"
                      :key="item.id"
                      :disabled="getDisabled(item.id)"
                      :label="`${item.terNo}/${item.routeNo}/${formatTaskType(
                        item.taskType
                      )}/${item.amount / 100000}/${item.comments}`"
                      :value="item.id"
                    />
                  </el-select>
                </el-form-item>
              </template>
            </el-table-column>
            <el-table-column
              align="center"
              label="任务类型"
              width="120"
              prop="taskType"
            />
            <el-table-column
              align="center"
              label="金额（十万）"
              width="120"
              prop="amount"
            />
            <el-table-column align="center" label="备注" prop="comments" />
          </el-table>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="cancelFormVisible = false">取消</el-button>
        <el-button type="primary" @click="cancelData()">确定</el-button>
      </div>
    </el-dialog>
    <!-- 任务分配 -->
    <el-dialog
      title="任务分配"
      :visible.sync="allocateFormVisible"
      :close-on-click-modal="false"
      width="90%"
    >
      <el-form
        ref="allocateForm"
        :rules="allocateRules"
        :model="allocateForm"
        status-icon
        label-position="right"
        label-width="100px"
        style="width: 90%; margin-left: 50px"
      >
        <el-form-item label="任务日期">
          <span>{{ formatDate(allocateForm.taskDate) }}</span>
        </el-form-item>
        <el-form-item label="原线路" prop="oldRouteId">
          <el-select
            v-model="allocateForm.oldRouteId"
            placeholder="请选择线路"
            style="width:20%"
            @change="oldRouteIdChange"
          >
            <el-option
              v-for="item in routeOption"
              :key="item.value"
              :disabled="item.value === allocateForm.routeId"
              :label="item.routeName"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="目标线路" prop="routeId">
          <el-select
            v-model="allocateForm.routeId"
            placeholder="请选择线路"
            style="width:20%"
          >
            <el-option
              v-for="item in routeOption"
              :key="item.value"
              :disabled="item.value === allocateForm.oldRouteId"
              :label="item.routeName"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="任务分配" prop="ids">
          <el-table
            :data="allocateForm.ids"
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
                  @click="allocateForm.ids.push({ id: null })"
                />
              </template>
              <template slot-scope="scope">
                <el-button
                  plain
                  type="danger"
                  icon="el-icon-remove-outline"
                  @click="allocateForm.ids.splice(scope.$index, 1)"
                />
              </template>
            </el-table-column>
            <el-table-column align="center" label="ATM设备" width="380">
              <template slot-scope="scope">
                <el-form-item
                  :prop="'ids.' + scope.$index + '.id'"
                  label-width="0px"
                  :rules="[
                    { required: true, message: '请填写', trigger: 'blur' },
                  ]"
                >
                  <el-select
                    v-model="scope.row.id"
                    filterable
                    placeholder="请选择任务"
                    style="width: 100%"
                    @change="taskIdChange($event, scope.row, moveOption)"
                  >
                    <el-option
                      v-for="item in moveOption"
                      :key="item.id"
                      :disabled="getDisabled(item.id)"
                      :label="`${item.terNo}/${formatTaskType(
                        item.taskType
                      )}/${item.amount / 100000}/${item.comments}`"
                      :value="item.id"
                    />
                  </el-select>
                </el-form-item>
              </template>
            </el-table-column>
            <el-table-column
              align="center"
              label="任务类型"
              width="120"
              prop="taskType"
            />
            <el-table-column
              align="center"
              label="金额（十万）"
              width="120"
              prop="amount"
            />
            <el-table-column align="center" label="备注" prop="comments" />
          </el-table>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="allocateFormVisible = false">取消</el-button>
        <el-button type="primary" @click="allocateData()">确定</el-button>
      </div>
    </el-dialog>
    <el-dialog
      :visible.sync="cashboxVisible"
      width="40%"
    >
      <el-form
        :model="cashboxForm"
        label-width="100px"
        inline
      >
        <el-form-item label="装盒时间" style="width: 50%">
          <span>{{ formatDateTime(cashboxForm.packTime) }}</span>
        </el-form-item>
        <el-form-item label="任务日期" style="width: 50%">
          <span>{{ formatDate(cashboxForm.taskDate) }}</span>
        </el-form-item>
        <el-form-item label="清分员" style="width: 50%">
          <span>{{ cashboxForm.clearManName }}</span>
        </el-form-item>
        <el-form-item label="复核员" style="width: 50%">
          <span>{{ cashboxForm.checkManName }}</span>
        </el-form-item>
        <el-form-item label="清分机" style="width: 50%">
          <span>{{ cashboxForm.deviceNo }}</span>
        </el-form-item>
        <el-form-item label="券别" style="width: 50%">
          <span>{{ cashboxForm.denomName }}</span>
        </el-form-item>
        <el-form-item label="包装金额" style="width: 50%">
          <span>{{ formatMoney(cashboxForm.amount) }}</span>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script>
// import Pagination from '@/components/Pagination'
import myTable from '@/components/Table'
// import SearchBar from '@/components/SearchBar'
import {
  listATMTask,
  updateATMTask,
  addATMCleanTask,
  addATMRepairTask,
  deleteATMTask,
  revokeATMTask,
  detailATMTask,
  importPath,
  confirmATMTask,
  batchConfirmATMTask,
  listTemplate,
  addTemplate,
  listBankTask,
  addVaultOrder,
  importData,
  batchCancel,
  cashboxPack,
  taskMove
} from '@/api/clean/atmTask'
import {
  atmOption,
  bankClearTree,
  denomOption,
  authOption,
  bankClearTopBank,
  cancelOption,
  routeTree,
  moveOption
} from '@/api/common/selectOption'
import TreeSelect from '@riophae/vue-treeselect' // 引用下拉树组件
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
import { formatdate } from '@/utils/date'
import { formatMoney } from '@/utils/convert'
import { dictionaryData } from '@/api/system/dictionary'
import { getToken } from '@/utils/auth'

export default {
  components: { myTable, TreeSelect },
  data() {
    const checkAtmList = (rule, value, callback) => {
      if (value.length > 0) {
        // for (const item of value) {
        //   console.log(item)
        //   if (item.bankId == null) {
        //     callback(new Error('清机机构必填'))
        //   } else if (item.atmId == null) {
        //     callback(new Error('ATM设备必填'))
        //   } else if (!item.amount === true) {
        //     callback(new Error('加钞金额必填'))
        //   } else {
        //     callback()
        //   }
        // }
        callback()
      } else {
        callback(new Error('请添加任务'))
      }
    }
    const checkNum = (rule, value, callback) => {
      if (!value) {
        return callback(new Error('请输入金额'))
      }
      if (isNaN(value)) {
        callback(new Error('请输入数字值'))
      } else {
        callback()
      }
    }
    return {
      activeName: 'first',
      oldName: 'first',
      exportVisible: false,
      headers: {
        'X-Token': getToken(),
        'X-mac': this.$store.getters.mac
      },
      exportRules: {
        taskDate: [
          { required: true, message: '日期不能为空', trigger: 'blur' }
        ],
        bankType: [
          { required: true, message: '银行类型不能为空', trigger: 'blur' }
        ],
        fileList: [
          { required: true, message: '上传文件不能为空', trigger: 'blur' }
        ],
        departmentId: [
          { required: true, message: '权限部门不能为空', trigger: 'blur' }
        ]
      },
      exportForm: {
        departmentId: null,
        taskDate: null,
        bankType: null,
        fileList: []
      },
      additionList: [],
      fileList: [],
      paramsData: {},
      bankTypeOption: [
        { id: 1, name: '北京银行' },
        { id: 2, name: '工商银行' },
        { id: 3, name: '农商银行' }
      ],

      listQuery: {
        routeDate: new Date(new Date().toLocaleDateString()).getTime(),
        departmentId: null,
        bankId: null,
        routeType: 0
      },
      departmentId: null,
      routeDataOption: [],
      loading: null,
      importPath,
      list: [],
      routeListQuery: {
        routeId: null,
        bankId: null,
        routeType: 0
      },
      selectedRoute: {
        status: null,
        routeName: null,
        type: null,
        routeNo: null
      },
      options: {
        taskType: [
          { code: 2, content: '清机' },
          { code: 1, content: '维修' }
        ],
        atmId: [],
        departmentId: [],
        bankId: []
      },
      listLoading: false,
      total: 0,
      tableList: [
        {
          label: '任务日期',
          prop: 'taskDate',
          formatter: this.formatDate,
          width: 120
        },
        {
          label: '所属银行',
          prop: 'headBank'
        },
        {
          label: '网点名称',
          prop: 'bankName',
          width: 140
        },
        {
          label: '设备编号',
          prop: 'terNo',
          width: 120
        },
        {
          label: '任务类型',
          prop: 'taskType',
          formatter: this.formatTaskType,
          width: 120
        },
        {
          label: '加钞金额（十万元）',
          prop: 'amount',
          width: 160,
          formatter: this.formatAmount
        },
        {
          label: '备注',
          prop: 'comments'
        },
        {
          label: '任务状态',
          prop: 'statusT',
          formatter: this.formatStatus
        },
        {
          label: '开始时间',
          prop: 'beginTime',
          formatter: this.formatTime,
          width: 120
        },
        {
          label: '结束时间',
          prop: 'endTime',
          formatter: this.formatTime,
          width: 120
        },
        {
          label: '设备巡检',
          prop: 'checkResult',
          formatter: this.formatCheckResult
        }
      ],
      cleanFormVisible: false,
      repairFormVisible: false,
      dialogDetailFormVisible: false,
      cleanForm: {
        id: null,
        departmentId: null,
        taskDate: null,
        atmTashCleanList: []
      },
      repairForm: {
        id: null,
        departmentId: null,
        taskDate: null,
        atmTashRepairList: []
      },
      bankOpiton: [],
      cleanRules: {
        atmTashCleanList: [
          { validator: checkAtmList, required: true, trigger: 'blur' }
        ]
      },
      cleanListRules: {
        atmId: [{ required: true, message: '请填写', trigger: 'blur' }],
        amount: [{ validator: checkNum, required: true, trigger: 'blur' }]
      },
      repairRules: {
        atmTashRepairList: [
          { validator: checkAtmList, required: true, trigger: 'blur' }
        ]
      },
      repairListRules: {
        atmId: [{ required: true, message: '请填写', trigger: 'blur' }],
        planTime: [{ required: true, message: '请填写', trigger: 'blur' }],
        content: [{ required: true, message: '请填写', trigger: 'blur' }],
        repairCompany: [{ required: true, message: '请填写', trigger: 'blur' }]
      },
      addStatus: '',
      atmTaskCheck: null,
      atmTaskClean: null,
      atmTaskRepair: null,
      isDisabled: true,
      batchList: [],
      // 模板
      dialogTemFormVisible: false,
      tempListQuery: {
        routeTemplateNo: null,
        bankId: null
      },
      templateForm: {
        atmTashCleanList: [],
        routeId: null,
        taskDate: null
      },
      tempBankClearTree: [],
      tempList: [],
      tempTableList: [
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
      tempListLoading: false,
      // 出库
      outFormVisible: false,
      outListLoading: false,
      outList: [],
      outTableList: [
        {
          label: '所属银行',
          prop: 'bankName'
        },
        {
          label: '确认任务金（元）',
          prop: 'confirmAmount',
          formatter: this.formatMoney
        },
        {
          label: '确认任务数',
          prop: 'confirmTaskCount'
        },
        // {
        //   label: '确认备用金（元）',
        //   prop: 'cashAmount',
        //   formatter: this.formatMoney
        // },
        {
          label: '待出库金额（元）',
          prop: 'outAmount',
          formatter: this.formatMoney
        },
        {
          label: '待出库任务数',
          prop: 'outTaskCount'
        },
        {
          label: '待确认金额（元）',
          prop: 'pendingAmount',
          formatter: this.formatMoney
        },
        {
          label: '待确认任务数',
          prop: 'pendingTaskCount'
        }
      ],
      denomList: [],
      denomTableList: [
        {
          label: '券别名称',
          prop: 'denomName'
        },
        {
          label: '券别金额',
          prop: 'taskAmount',
          formatter: this.formatMoney
        },
        {
          label: '状态',
          prop: 'statusT',
          formatter: this.formatDenomStatus
        }
      ],
      // 上传确认
      dialogConfirmVisible: false,
      confirmListLoading: false,
      confirmForm: {
        atmTaskCleanImportBatchVOs: [],
        bankType: null,
        fileName: null,
        taskDate: null
      },
      bankName: '',
      confirmList: [],
      confirmTableList: [
        {
          label: '线路编号',
          prop: 'routeNo'
        },
        {
          label: '任务个数',
          prop: 'terTotal'
        },
        {
          label: '任务总金额（十万）',
          prop: 'amount'
        }
      ],
      responseList: [],
      faultOptions: [
        '巡检',
        '断线',
        '卡钞',
        '废钞箱满',
        '存款箱满',
        '钞箱故障',
        '读卡器故障',
        '凭条打印机故障',
        '凭条打印机缺纸',
        '密码键盘故障',
        '保险箱',
        '皮带故障',
        '吐钞机构故障',
        '挖钞轮故障',
        '感应器故障',
        '验钞机构故障',
        '电源故障',
        'PM',
        '维修',
        '自助终端机断线',
        '自助终端机凭条打印机故障',
        '自助终端机凭条打印机缺纸',
        '自助终端机存折打印机故障',
        '监控故障',
        '布防故障',
        '显示器故障',
        '系统故障、系绽重装',
        '现场拿卡',
        '停电',
        '系统升级、改造',
        '其它',
        '缺钞',
        '新增出库',
        '新增加款任务',
        '出库关机',
        '暂停服务',
        '任务撤销',
        '取吞卡',
        '取吞卡 送网点',
        '布防测试',
        '杀毒'
      ],
      exportBankOption: [],
      // 批量撤销
      cancelFormVisible: false,
      cancelOption: [],
      cancelForm: {
        departmentId: null,
        taskDate: null,
        taskIds: []
      },
      selectedTaskIds: [],
      cancelRules: {
        taskIds: [{ validator: checkAtmList, required: true, trigger: 'blur' }]
      },
      cashboxVisible: false,
      cashboxForm: {},

      // 任务分配
      allocateFormVisible: false,
      allocateForm: {
        oldRouteId: null,
        routeId: null,
        taskDate: null,
        ids: []
      },
      allocateRules: {
        oldRouteId: [{ required: true, message: '原线路不能为空', trigger: 'blur' }],
        routeId: [{ required: true, message: '目标线路不能为空', trigger: 'blur' }],
        ids: [{ validator: checkAtmList, required: true, trigger: 'blur' }]
      },
      routeOption: [],
      moveOption: []
    }
  },
  computed: {
    amountTotal() {
      const total = this.list
        .filter((item) => item.statusT !== -1)
        .map((item) => item.amount)
      return total.reduce((n, m) => n + m, 0)
    },
    confirmTerTotal() {
      const total = this.confirmList.map((item) => item.terTotal)
      return total.reduce((n, m) => n + m, 0)
    },
    confirmAmountTotal() {
      const total = this.confirmList.map((item) => item.amount)
      return total.reduce((n, m) => n + m, 0)
    },
    bankSum() {
      const arr = []
      const obj = {}
      this.list.forEach((item) => {
        if (!arr.find((elm) => elm === item.bankId)) {
          arr.push(item.bankId)
          obj[item.bankId] = this.list.filter(
            (elm) => elm.bankId === item.bankId
          )
        }
      })
      if (arr.length === 1) {
        return obj[arr[0]]
      } else {
        return []
      }
    },
    bankSumTotal() {
      const obj = {
        totalTask: 0,
        cleanTotal: 0,
        defend: 0,
        cancelTask: 0,
        newTask: 0,
        hdCancelTotal: 0,
        tenCancelTotal: 0,
        hdNewTotal: 0,
        tenNewTotal: 0,
        hdAddTotal: 0,
        tenAddTotal: 0,
        amountTotal: 0
      }
      this.bankSum.forEach((item) => {
        if (item.statusT !== -1) {
          console.log(item.amount)
          obj.totalTask++
          obj.amountTotal += item.amount * 1000
        }
        if (
          (item.taskType === 2 || item.taskType === 3) &&
          item.statusT !== -1
        ) {
          obj.cleanTotal++
        }
        if (item.taskType === 1) {
          obj.defend++
        }
        if (item.statusT === -1) {
          obj.cancelTask++
        }
        if (item.importBatch === 0) {
          obj.newTask++
        }
        if (item.statusT === -1 && item.denom === 100) {
          obj.hdCancelTotal += item.amount
        }
        if (item.statusT === -1 && item.denom === 10) {
          obj.tenCancelTotal += item.amount
        }
        if (item.importBatch === 0 && item.denom === 100) {
          obj.hdNewTotal += item.amount
        }
        if (item.importBatch === 0 && item.denom === 10) {
          obj.tenNewTotal += item.amount
        }
      })
      const hdAdd = this.additionList.find(
        (item) =>
          item.bankId === this.bankSum[0].bankId && item.denomValue === 100
      )
      if (hdAdd) {
        obj.hdAddTotal = hdAdd.amount / 100000
      }
      const tenAdd = this.additionList.find(
        (item) =>
          item.bankId === this.bankSum[0].bankId && item.denomValue === 10
      )
      if (tenAdd) {
        obj.tenAddTotal = tenAdd.amount / 100000
      }

      return obj
    }
  },
  watch: {
    'cancelForm.taskIds': {
      handler(newVal) {
        this.selectedTaskIds = newVal.map((item) => item.id)
      },
      deep: true
    },
    'allocateForm.ids': {
      handler(newVal) {
        this.selectedTaskIds = newVal.map((item) => item.id)
      },
      deep: true
    }
  },
  mounted() {
    this.openLoading()
    this.getOptions().then(() => {
      this.getbankTopOption()
      this.getTree()
    })
  },
  updated() {
    if (this.$refs.tempList) {
      this.$nextTick(() => {
        this.$refs.tempList.doLayout()
      })
    }
  },
  methods: {
    formatMoney,
    tabClick() {
      if (this.activeName !== this.oldName) {
        this.oldName = this.activeName
        this.activeName === 'first'
          ? (this.listQuery.routeType = this.routeListQuery.routeType = 0)
          : (this.listQuery.routeType = this.routeListQuery.routeType = 1)
        this.getTree()
      }
    },
    getList() {
      this.listLoading = true
      listATMTask(this.routeListQuery).then((res) => {
        this.list = res.data.taskData
        this.additionList = res.data.additionCash
        this.listLoading = false
        this.$nextTick(() => {
          this.$refs.taskTable.$refs.myTable.toggleAllSelection()
        })
      })
    },
    async getTree() {
      await routeTree(this.listQuery)
        .then((res) => {
          const data = res.data
          this.routeDataOption = data
          if (this.routeDataOption.length > 0) {
            this.$nextTick(() => {
              this.$refs.listTree.setCurrentKey(this.routeDataOption[0].value)
            })
            this.handleNodeClick(this.routeDataOption[0])
          } else {
            this.list = []
            this.selectedRoute = {}
          }
        })
        .finally(() => {
          this.loading.close()
        })
    },
    getOptions() {
      return new Promise((resolve, reject) => {
        Promise.all([denomOption(), authOption()])
          .then((res) => {
            const [res2, res4] = res
            this.options.denomId = res2.data
            this.options.departmentId = res4.data
            this.listQuery.departmentId = this.options.departmentId[0].id
            resolve()
          })
          .catch((err) => {
            reject(err)
          })
      })
    },
    getbankTopOption() {
      this.listQuery.bankId = null
      this.routeListQuery.bankId = null
      bankClearTopBank(this.listQuery.departmentId).then((res) => {
        this.options.bankId = res.data
      })
    },
    getTempList() {
      this.tempListLoading = true
      listTemplate(this.tempListQuery).then((res) => {
        this.tempList = res.data
        this.tempListLoading = false
      })
    },
    getExportBankTopOption(val) {
      this.exportForm.bankType = null
      bankClearTopBank(val).then((res) => {
        this.exportBankOption = res.data
      })
    },
    handleNodeClick(val) {
      this.selectedRoute = val
      this.routeListQuery.routeId = val.value
      this.getList()
    },
    handleCreate(name) {
      atmOption(this.listQuery.departmentId).then((res) => {
        this.options.atmId = res.data
        this.addStatus = name
        name === '清机'
          ? (this.cleanFormVisible = true)
          : (this.repairFormVisible = true)
        this.cleanForm.atmTashCleanList = []
        this.repairForm.atmTashRepairList = []
        this.$nextTick(() => {
          if (name === '清机') {
            this.$refs['cleanForm'].resetFields()
            this.cleanForm.taskDate = this.listQuery.routeDate
          } else {
            this.$refs['repairForm'].resetFields()
            this.repairForm.taskDate = this.listQuery.routeDate
          }
        })
      })
    },
    handleTemplateCreate() {
      bankClearTree(this.listQuery.departmentId).then((res) => {
        this.tempBankClearTree = res.data
        this.dialogTemFormVisible = true
        this.templateForm.routeId = this.routeListQuery.routeId
        this.templateForm.taskDate = this.listQuery.routeDate
        this.tempListQuery.routeTemplateNo = this.selectedRoute.routeNo
        this.getTempList()
      })
    },
    handleDelete(row) {
      this.$confirm('确定删除吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.listLoading = true
        deleteATMTask(row.id)
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
    handleDetail(row) {
      detailATMTask(row.id).then((res) => {
        const { atmTaskCheck, atmTaskClean, atmTaskRepair } = res.data
        this.atmTaskCheck = atmTaskCheck
        this.atmTaskClean = atmTaskClean
        this.atmTaskRepair = atmTaskRepair
        this.dialogDetailFormVisible = true
      })
    },
    handleConfirm(row) {
      this.$confirm('确定确认吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.listLoading = true
        confirmATMTask(row.id)
          .then(() => {
            this.$notify.success({
              title: '成功',
              message: '确认成功'
            })
            this.getList()
          })
          .finally(() => {
            this.listLoading = false
          })
      })
    },
    handleRevoke(row) {
      this.$confirm('确定撤销吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.listLoading = true
        revokeATMTask(row.id)
          .then(() => {
            this.$notify.success({
              title: '成功',
              message: '撤销成功'
            })
            this.getList()
          })
          .finally(() => {
            this.listLoading = false
          })
      })
    },
    handleOut() {
      listBankTask(this.listQuery.routeDate, this.listQuery.departmentId).then(
        (res) => {
          this.outList = res.data
          this.outFormVisible = true
          if (this.outList.length > 0) {
            this.$nextTick(() => {
              this.$refs.myTable.setCurrent()
            })
          }
        }
      )
    },
    handleOutConfirm(row) {
      this.$confirm(
        `当前待出库金额 <span style="color: red">${row.outAmount}</span>，是否确认快捷出库?`,
        '提示',
        {
          dangerouslyUseHTMLString: true,
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      ).then(() => {
        const data = {}
        data.orderDate = this.listQuery.routeDate
        data.departmentId = this.listQuery.departmentId
        data.bankId = row.bankId
        data.orderType = 1
        data.vaultRecordList = row.denomList
          .filter((item) => item.statusT === 1)
          .map((item) => {
            return {
              denomId: item.denomId,
              denomType: 0,
              amount: item.taskAmount
            }
          })
        this.outListLoading = true
        addVaultOrder(data)
          .then(() => {
            this.$notify.success({
              title: '成功',
              message: '出库成功'
            })
            this.handleOut()
          })
          .finally(() => {
            this.outListLoading = false
          })
      })
    },
    batchConfirm() {
      this.$confirm('确定确认勾选的ATM任务吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.listLoading = true
        const ids = this.batchList.toString()
        batchConfirmATMTask(ids)
          .then(() => {
            this.$notify.success({
              title: '成功',
              message: '确认成功'
            })
            this.getList()
          })
          .finally(() => {
            this.listLoading = false
          })
      })
    },
    batchCancel() {
      this.openLoading()
      cancelOption(this.listQuery.departmentId, this.listQuery.routeDate).then(
        (res) => {
          this.loading.close()
          this.cancelOption = res.data
          this.cancelFormVisible = true
          this.cancelForm.taskIds = []
          this.$nextTick(() => {
            this.$refs['cancelForm'].resetFields()
            this.cancelForm.departmentId = this.listQuery.departmentId
            this.cancelForm.taskDate = this.listQuery.routeDate
          })
        }
      )
    },
    cancelData() {
      this.$refs['cancelForm'].validate((valid) => {
        if (valid) {
          this.$confirm('确定批量撤销ATM任务吗?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            this.openLoading()
            let arr = [...this.cancelForm.taskIds]
            arr = arr.map((item) => item.id)
            batchCancel(arr)
              .then(() => {
                this.getList()
                this.cancelFormVisible = false
                this.$notify.success({
                  title: '成功',
                  message: '批量撤销完成'
                })
              })
              .finally(() => {
                this.loading.close()
              })
          })
        }
      })
    },
    newData() {
      if (this.addStatus === '清机') {
        this.$refs['cleanForm'].validate((valid) => {
          if (valid) {
            this.$confirm('请确保任务信息正确，一旦提交无法修改', '提示', {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              type: 'warning'
            }).then(() => {
              this.openLoading()
              addATMCleanTask(this.cleanForm)
                .then(() => {
                  this.getList()
                  this.cleanFormVisible = false
                  this.$notify.success({
                    title: '成功',
                    message: '添加成功'
                  })
                })
                .finally(() => {
                  this.loading.close()
                })
            })
          }
        })
      } else {
        this.$refs['repairForm'].validate((valid) => {
          if (valid) {
            this.$confirm('请确保任务信息正确，一旦提交无法修改', '提示', {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              type: 'warning'
            }).then(() => {
              this.openLoading()
              addATMRepairTask(this.repairForm)
                .then(() => {
                  this.getList()
                  this.repairFormVisible = false
                  this.$notify.success({
                    title: '成功',
                    message: '添加成功'
                  })
                })
                .finally(() => {
                  this.loading.close()
                })
            })
          }
        })
      }
    },
    updateData() {
      this.$refs['addForm'].validate((valid) => {
        if (valid) {
          this.openLoading()
          if (this.addForm.taskType === 1) {
            this.addForm.amount = 0
          }
          updateATMTask(this.addForm)
            .then(() => {
              this.getList()
              this.dialogDetailFormVisible = false
              this.$notify.success({
                title: '成功',
                message: '更新成功'
              })
            })
            .finally(() => {
              this.loading.close()
            })
        }
      })
    },
    newTemplate() {
      const filterList = this.tempList.filter(
        (item) => item.amount && item.amount.length > 0
      )
      if (filterList.length > 0) {
        this.templateForm.atmTashCleanList = filterList.map((item) => {
          return {
            amount: +item.amount,
            atmId: item.atmId,
            comments: item.comments
          }
        })
        this.openLoading()
        addTemplate(this.templateForm)
          .then(() => {
            this.dialogTemFormVisible = false
            this.$notify.success({
              title: '成功',
              message: '创建成功'
            })
            this.getList()
          })
          .finally(() => {
            this.loading.close()
          })
      } else {
        this.$message.error('请创建至少一项模板')
      }
    },
    cashboxDetail(id) {
      this.openLoading()
      cashboxPack(id).then(res => {
        this.cashboxForm = res.data
        this.cashboxVisible = true
      }).finally(() => {
        this.loading.close()
      })
    },
    taskAllocate() {
      const params = {
        departmentId: this.listQuery.departmentId,
        routeDate: this.listQuery.routeDate,
        routeType: 1
      }
      this.openLoading()
      routeTree(params).then(res => {
        this.routeOption = res.data
        this.allocateFormVisible = true
        this.moveOption = []
        this.allocateForm.ids = []
        this.$nextTick(() => {
          this.$refs['allocateForm'].resetFields()
          this.allocateForm.taskDate = this.listQuery.routeDate
        })
      }).finally(() => {
        this.loading.close()
      })
    },
    allocateData() {
      this.$refs['allocateForm'].validate((valid) => {
        if (valid) {
          // eslint-disable-next-line no-unused-vars
          const { taskDate, oldRouteId, ...obj } = this.allocateForm
          obj.ids = obj.ids.map(item => item.id)
          this.openLoading()
          taskMove(obj).then(() => {
            this.getList()
            this.allocateFormVisible = false
            this.$notify.success({
              title: '成功',
              message: '分配成功'
            })
          }).finally(() => {
            this.loading.close()
          })
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

    // 过滤
    formatTime(timestamp) {
      if (!timestamp || timestamp === 0) {
        return '-'
      }
      const date = new Date(timestamp)
      return formatdate(date, 'hh:mm:ss')
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
    },
    formatDenom(id) {
      if (id != null) {
        for (const item of this.options.denomId) {
          if (item.id === id) {
            return item.name
          }
        }
      }
    },
    formatStatus(type) {
      for (const item of dictionaryData['CLEAN_STATUS']) {
        if (item.code === type) {
          return item.content
        }
      }
    },
    formatTaskType(str) {
      switch (str) {
        case 1:
          return '维修'
        case 2:
          return '加钞'
        case 3:
          return '清机'
        case 4:
          return '巡检'
      }
    },
    formatAmount(num, row) {
      if (row.taskType === 1) {
        return '-'
      }
      return num
    },
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
    formatDetailStatus(type) {
      switch (type) {
        case 0:
          return '未执行'
        case 1:
          return '已完成'
      }
    },
    formatDenomStatus(type) {
      switch (type) {
        case 0:
          return '未确认'
        case 1:
          return '已确认'
        case 2:
          return '待出库'
      }
    },
    formatResult(num) {
      switch (num) {
        case 0:
          return '否'
        case 1:
          return '是'
      }
    },
    formatRunStatus(status) {
      switch (status) {
        case 0:
          return '正常'
        case 1:
          return '无存取款项'
        case 2:
          return '部分功能正常'
        case 3:
          return '暂停服务'
      }
    },
    formatBankType(type) {
      if (type != null) {
        return this.bankTypeOption.find((item) => item.id === type).name
      }
    },
    formatBankId(type) {
      if (type != null) {
        return this.exportBankOption.find((item) => item.id === type).name
      }
    },
    formatAtmStatus(status) {
      switch (status) {
        case 0:
          return '正常'
        case 1:
          return '无存取款项'
        case 2:
          return '部分功能正常'
        case 3:
          return '暂停服务'
      }
    },
    formatCheckResult(status) {
      switch (status) {
        case 0:
          return ''
        case 1:
          return '正常'
        case 2:
          return '<span style="color: red">异常</span>'
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
    selectionChange(val) {
      this.batchList = val.map((item) => item.id)
    },
    comfirmListChange(val) {
      this.confirmForm.atmTaskCleanImportBatchVOs = val.map(item => {
        return this.responseList.find(elm => item.routeNo === elm.routeNo)
      })
    },
    getSummaries(param) {
      const { columns, data } = param
      const sums = []
      columns.forEach((column, index) => {
        if (index === 0) {
          sums[index] = `添加模板数：${
            data.filter((item) => item.amount && item.amount.length > 0).length
          }`
          return
        }
        const values = data.map((item) => Number(item[column.property]))
        if (index === 3 && !values.every((value) => isNaN(value))) {
          sums[index] = values.reduce((prev, curr) => {
            const value = Number(curr)
            if (!isNaN(value)) {
              return prev + curr
            } else {
              return prev
            }
          }, 0)
          sums[index] += ''
        } else {
          sums[index] = '-'
        }
      })

      return sums
    },
    rowClick(row) {
      this.denomList = row.denomList
    },
    // 上传
    handleExport() {
      bankClearTopBank(this.listQuery.departmentId).then((res) => {
        this.exportVisible = true
        this.exportBankOption = res.data
        this.$nextTick(() => {
          this.$refs['exportForm'].resetFields()
          this.exportForm.departmentId = this.listQuery.departmentId
        })
      })
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
      this.$refs['exportForm'].validate((valid) => {
        if (valid) {
          this.paramsData.taskDate = this.exportForm.taskDate
          this.paramsData.bankType = this.exportForm.bankType
          this.bankName = this.exportBankOption.find((item) => item.id === this.exportForm.bankType).name
          this.$refs.upload.submit()
          this.openLoading()
        }
      })
    },
    confirmVisibleCancel() {
      this.dialogConfirmVisible = false
      this.$nextTick(() => {
        this.$refs['exportForm'].resetFields()
      })
    },
    beforeClose(done) {
      this.$nextTick(() => {
        this.$refs['exportForm'].resetFields()
        done()
      })
    },
    confirmExport() {
      this.openLoading()
      importData(this.confirmForm)
        .then(() => {
          this.$notify.success({
            title: '成功',
            message: '导入成功'
          })
          this.dialogConfirmVisible = this.exportVisible = false
          this.getList()
        })
        .finally(() => {
          this.loading.close()
        })
    },
    exportChange(file, fileList) {
      this.exportForm.fileList = fileList.slice(-1)
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
        this.responseList = res.data
        this.confirmForm.fileName = file.name
        this.confirmForm.bankType = this.exportForm.bankType
        this.confirmForm.taskDate = this.exportForm.taskDate
        this.confirmForm.departmentId = this.exportForm.departmentId
        this.dialogConfirmVisible = true
        this.confirmList = res.data.map((elm) => {
          return {
            routeNo: elm.routeNo,
            terTotal: elm.atmTashCleanList.length,
            amount: elm.atmTashCleanList
              .map((item) => item.amount)
              .reduce((n, m) => n + m, 0)
          }
        })
      }
    },
    exportRemove(file, fileList) {
      this.exportForm.fileList = fileList
    },
    taskIdChange(id, row, option) {
      const obj = option.find((elm) => elm.id === id)
      row.taskType = this.formatTaskType(obj.taskType)
      row.amount = obj.amount / 100000
      row.comments = obj.comments
    },
    getDisabled(val) {
      if (this.selectedTaskIds.find((item) => item === val)) {
        return true
      }
      return false
    },
    oldRouteIdChange(val) {
      moveOption(val).then(res => {
        this.moveOption = res.data
        this.allocateForm.ids = []
      })
    }
  }
}
</script>

<style scoped lang="scss">
.filter-container *:nth-child(n + 2) {
  margin-left: 10px;
}

.vue-treeselect--open {
  font-weight: normal !important;
}

.box-card {
  margin-top: 20px;
  .title {
    font-weight: 600;
    font-size: 18px;
  }
}

.el-form--inline .el-form-item {
  margin-right: 0;
  margin-bottom: 8px;
  width: 33%;
  ::v-deep .el-form-item__label {
    color: #99a9bf;
    margin-right: 12px;
  }
}

.scrollbar {
  ::v-deep .el-scrollbar__wrap {
    overflow: auto;
  }
}
.number {
  ::v-deep input[type="number"] {
    -moz-appearance: textfield !important;
  }
  ::v-deep input::-webkit-outer-spin-button,
  ::v-deep input::-webkit-inner-spin-button {
    -webkit-appearance: none !important;
  }
}

.sum {
  display: flex;
  margin-top: 30px;
  font-size: 14px;
  color: #606266;
}

.el-table::before {
  z-index: inherit;
}

.custom-tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  font-size: 14px;
  padding-right: 8px;
}

.manage-btn {
  margin-left: 10px;
  vertical-align: top;
}

.confirm-info {
  font-size: 0;
  label {
    width: 120px;
    color: #99a9bf;
    display: flex;
    flex-wrap: wrap;
  }
  .el-form-item {
    margin-right: 0;
    margin-bottom: 0;
    width: 50%;
    div {
      padding-left: 40px;
    }
  }
}

.bank-sum {
  font-size: 0;
  .el-form-item {
    margin-bottom: 0;
    width: 25%;
    ::v-deep .el-form-item__label {
      margin-right: 0 !important;
    }
  }
}
</style>

