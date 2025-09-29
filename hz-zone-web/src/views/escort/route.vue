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
      <el-date-picker
        v-model="listQuery.routeDate"
        value-format="timestamp"
        type="date"
        placeholder="选择日期"
        class="filter-item"
      />
      <el-select
        v-model="listQuery.routeType"
        clearable
        placeholder="请选择线路类型"
        class="filter-item"
      >
        <el-option
          v-for="item in options.routeType"
          :key="item.code"
          :label="item.content"
          :value="item.code"
        />
      </el-select>
      <el-input
        v-model="listQuery.routeNo"
        clearable
        class="filter-item"
        style="width: 200px"
        placeholder="请输入线路编号"
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
        v-permission="['/base/route/list']"
        class="filter-item"
        type="primary"
        icon="el-icon-search"
        @click="listQuery.page=1;getList()"
      >查找</el-button>
      <el-button
        v-permission="['/base/route/save']"
        class="filter-item"
        type="primary"
        icon="el-icon-edit"
        @click="handleCreate"
      >添加</el-button>
      <el-button
        v-permission="['/base/route/copy']"
        class="filter-item"
        :disabled="batchList.length !== 1"
        type="primary"
        icon="el-icon-document-copy"
        @click="handleCopy(batchList[0])"
      >复制</el-button>
      <el-button
        v-permission="['/base/route/over']"
        class="filter-item"
        :disabled="batchList.length !== 1"
        type="primary"
        icon="el-icon-document-copy"
        @click="handleOver(batchList[0])"
      >手动交接</el-button>
      <el-button
        v-permission="['/base/route/push/notice']"
        class="filter-item"
        :disabled="batchList.length === 0"
        type="primary"
        icon="el-icon-chat-line-round"
        @click="handleAlert(batchList)"
      >排班推送</el-button>
      <el-button
        v-permission="['/base/route/edit']"
        :disabled="batchList.length !== 1"
        class="filter-item"
        type="primary"
        icon="el-icon-chat-line-round"
        @click="handleChange(batchList[0])"
      >信息变更</el-button>
      <el-button
        v-permission="['/base/route/editHandover']"
        :disabled="batchList.length !== 1 || batchList[0].statusT !== 4"
        class="filter-item"
        type="primary"
        icon="el-icon-sort"
        @click="handleHandover(batchList[0])"
      >交接调整</el-button>
    </div>
    <my-table
      :list-loading="listLoading"
      :data-list="list"
      :table-list="tableList"
      :expand-list="expandList"
      :selection="true"
      @selectionChange="selectionChange"
    >
      <template v-slot:operate>
        <el-table-column
          align="center"
          label="车长日志"
          class-name="small-padding fixed-width"
        >
          <template slot-scope="scope">
            <el-button
              v-show="scope.row.leaderLog !== 0"
              v-permission="['/base/route/leaderLog']"
              :type="scope.row.leaderLog === 1 ? 'success' : 'danger'"
              size="mini"
              @click="handleDetailMaster(scope.row.id)"
            >查看详情</el-button>
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          label="交接调整"
          class-name="small-padding fixed-width"
        >
          <template slot-scope="scope">
            <el-button
              v-if="scope.row.handoverChange === 1"
              v-permission="['/base/route/handoverChange']"
              type="primary"
              plain
              size="mini"
              @click="handleHandoverDetail(scope.row.id)"
            >查看详情</el-button>
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          label="人员调整"
          class-name="small-padding fixed-width"
        >
          <template slot-scope="scope">
            <el-button
              v-if="scope.row.empChange === 1"
              v-permission="['/base/route/empChange']"
              type="primary"
              plain
              size="mini"
              @click="handleDetail(scope.row)"
            >查看详情</el-button>
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          label="配钞详情"
          class-name="small-padding fixed-width"
        >
          <template slot-scope="scope">
            <el-button
              v-permission="['/base/route/dispatch']"
              type="primary"
              plain
              size="mini"
              @click="handleDetailDispatch(scope.row.id)"
            >查看详情</el-button>
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          label="操作"
          class-name="small-padding fixed-width"
          width="240"
          fixed="right"
        >
          <template slot-scope="scope">
            <el-button
              v-permission="['/base/route/confirm']"
              :disabled="scope.row.statusT !== 0"
              type="success"
              :plain="scope.row.statusT !== 0"
              size="mini"
              @click="handleConfirm(scope.row)"
            >确认</el-button>
            <el-button
              v-permission="['/base/route/update']"
              :disabled="scope.row.statusT === 4"
              type="primary"
              :plain="scope.row.statusT === 4"
              size="mini"
              @click="handleUpdate(scope.row)"
            >{{ scope.row.statusT === 0 ? '编辑' : '人员调整' }}</el-button>
            <el-button
              v-permission="['/base/route/delete']"
              :disabled="scope.row.statusT !== 0"
              type="danger"
              :plain="scope.row.statusT !== 0"
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
        style="width: 80%;margin-left:8%"
      >
        <el-form-item v-show="!isDone" label="所属部门" prop="departmentId">
          <el-select
            v-model="dataForm.departmentId"
            placeholder="请选择所属部门"
            style="width:40%"
            @change="departmentIdChange"
          >
            <el-option
              v-for="item in authOption"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item v-show="!isDone" label="任务日期" prop="routeDate">
          <el-date-picker
            v-model="dataForm.routeDate"
            value-format="timestamp"
            type="date"
            placeholder="选择日期"
            style="width:40%"
          />
        </el-form-item>
        <el-form-item v-show="!isDone" label="线路编号" prop="routeNo">
          <el-input v-model.number="dataForm.routeNo" :maxlength="3" style="width:40%" />
        </el-form-item>
        <el-form-item v-show="!isDone" label="线路名称" prop="routeName">
          <el-input v-model="dataForm.routeName" :maxlength="64" style="width:40%" />
        </el-form-item>
        <!-- <el-form-item label="计划开始时间" prop="planBeginTime">
          <el-time-select
            v-model="dataForm.planBeginTime"
            :disabled="isTemp"
            placeholder="起始时间"
            :picker-options="{
              start: '00:00',
              step: '00:30',
              end: '24:00',
              maxTime: dataForm.planFinishTime,
            }"
            default-value="08:00"
            style="width:40%"
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
            style="width:40%"
          />
        </el-form-item> -->
        <el-form-item v-show="isChange || !isDone" label="分配车辆" prop="vehicleId">
          <el-select
            v-model="dataForm.vehicleId"
            filterable
            placeholder="请选择"
            style="width:40%"
          >
            <el-option
              v-for="item in vehicleOption"
              :key="item.id"
              :label="`${item.seqno}/${item.lpno}`"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item v-show="!isDone" label="线路类型" prop="templateType">
          <el-select
            v-model="dataForm.templateType"
            placeholder="请选择线路类型"
            style="width:40%"
            @change="templateTypeChange"
          >
            <el-option
              v-for="item in options.templateType"
              :key="item.code"
              :label="item.content"
              :value="item.code"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="司机" prop="driver">
          <el-select
            v-model="dataForm.driver"
            filterable
            placeholder="请选择"
            style="width:40%"
          >
            <el-option
              v-for="item in jobNameOption[1]"
              :key="item.id"
              :label="item.empNo + ' / ' + item.empName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item v-show="dataForm.templateType !== 1" label="业务-钥匙员" prop="routeKeyMan">
          <el-select
            v-model="dataForm.routeKeyMan"
            filterable
            placeholder="请选择"
            style="width:40%"
          >
            <el-option
              v-for="item in keyOption"
              :key="item.id"
              :disabled="item.id == dataForm.routeOperMan"
              :label="item.empNo + ' / ' + item.empName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item v-show="dataForm.templateType !== 1" label="业务-密码员" prop="routeOperMan">
          <el-select
            v-model="dataForm.routeOperMan"
            filterable
            placeholder="请选择"
            style="width:40%"
          >
            <el-option
              v-for="item in operOption"
              :key="item.id"
              :disabled="item.id == dataForm.routeKeyMan"
              :label="item.empNo + ' / ' + item.empName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item v-show="dataForm.templateType !== 1" label="护卫A" prop="securityA">
          <el-select
            v-model="dataForm.securityA"
            clearable
            filterable
            placeholder="请选择"
            style="width:40%"
          >
            <el-option
              v-for="item in jobNameOption[2]"
              :key="item.id"
              :disabled="item.id == dataForm.securityB"
              :label="item.empNo + ' / ' + item.empName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item v-show="dataForm.templateType !== 1" label="护卫B" prop="securityB">
          <el-select
            v-model="dataForm.securityB"
            clearable
            filterable
            placeholder="请选择"
            style="width:40%"
          >
            <el-option
              v-for="item in jobNameOption[2]"
              :key="item.id"
              :disabled="item.id == dataForm.securityA"
              :label="item.empNo + ' / ' + item.empName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item v-show="!isDone" label="跟车人员" prop="follower">
          <el-select
            v-model="dataForm.follower"
            filterable
            placeholder="请选择"
            style="width:40%"
            clearable
          >
            <el-option
              v-for="item in employeeOption"
              :key="item.id"
              :label="item.empNo + ' / ' + item.empName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="comments">
          <el-input v-model="dataForm.comments" :maxlength="64" style="width:40%" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button
          type="primary"
          @click="newData(dialogStatus)"
        >确定</el-button>
      </div>
    </el-dialog>
    <!-- 清机任务详情 -->
    <el-dialog
      title="详情"
      :visible.sync="detailFormVisible"
      :close-on-click-modal="false"
      width="80%"
    >
      <div>
        <my-table
          ref="myTable"
          :list-loading="deatilListLoading"
          :data-list="detailList"
          :table-list="deatilTableList"
          :height="'300'"
        />
        <!-- <el-collapse v-show="detailList.length > 0" class="box-collapse">
          <el-collapse-item v-if="atmTaskRepair" title="维修" name="1">
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
                <span>{{ atmTaskRepair.cashInBox }}</span>
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
          </el-collapse-item>
          <el-collapse-item v-if="atmTaskClean" title="加钞" name="2">
            <el-form
              ref="atmTaskClean"
              :model="atmTaskClean"
              label-width="200px"
              inline
            >
              <el-form-item label="加钞金额（十万元）" style="width:50%">
                <span>{{ atmTaskClean.amount }}</span>
              </el-form-item>
              <el-form-item label="加钞钞盒" style="width:50%">
                <el-button
                  v-for="item in atmTaskClean.cashboxMap"
                  :key="item.id"
                  size="small"
                  type="primary"
                  plain
                  @click="cashboxDetail(item.id)"
                >{{ item.boxNo }}</el-button>
              </el-form-item>
              <el-form-item label="现场清点标志" style="width:50%">
                <span>{{ formatResult(atmTaskClean.clearSite) }}</span>
              </el-form-item>
              <el-form-item label="回笼钞盒" style="width:50%">
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
          </el-collapse-item>
          <el-collapse-item v-if="atmTaskCheck" title="巡检" name="3">
            <el-form
              ref="atmTaskCheck"
              :model="atmTaskCheck"
              label-width="200px"
              inline
            >
              <el-form-item label="插卡口是否正常" style="width:50%">
                <span>{{ formatResult(atmTaskCheck.checkItemResult.cardReader) }}</span>
              </el-form-item>
              <el-form-item label="有无安装非法装置" style="width:50%">
                <span>{{ formatResult(atmTaskCheck.checkItemResult.thingInstall) }}</span>
              </el-form-item>
              <el-form-item label="出钞口是否正常" style="width:50%">
                <span>{{ formatResult(atmTaskCheck.checkItemResult.cashOutlet) }}</span>
              </el-form-item>
              <el-form-item label="有无非法张贴物" style="width:50%">
                <span>{{ formatResult(atmTaskCheck.checkItemResult.thingStick) }}</span>
              </el-form-item>
              <el-form-item label="密码键盘防窥罩" style="width:50%">
                <span>{{ formatResult(atmTaskCheck.checkItemResult.keypadMask) }}</span>
              </el-form-item>
              <el-form-item label="功能标识，操作提示是否齐全" style="width:50%">
                <span>{{ formatResult(atmTaskCheck.checkItemResult.operationTips) }}</span>
              </el-form-item>
              <el-form-item label="备注" style="width:50%">
                <span>{{ formatResult(atmTaskCheck.comments) }}</span>
              </el-form-item>
            </el-form>
          </el-collapse-item>
        </el-collapse> -->
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

    <el-dialog title="日志详情" :visible.sync="masterFormVisible">
      <div style="padding-left:6%">
        <my-table
          :data-list="masterDetailList"
          :table-list="masterTableList"
        />
        <el-form label-position="right" inline class="comment">
          <el-form-item
            label="其他问题"
            style="width: 100%"
          >
            <span>{{ masterComments }}</span>
          </el-form-item>
        </el-form>
      </div>

      <div slot="footer" class="dialog-footer">
        <el-button @click="masterFormVisible = false">关闭</el-button>
      </div>
    </el-dialog>

    <el-dialog title="配钞详情" :visible.sync="dispatchFormVisible">
      <div>
        <my-table
          :data-list="dispatchDetailList"
          :table-list="dispatchTableList"
        />
        <el-form label-position="right" inline class="bank-obj">
          <el-form-item
            v-for="(val,key,index) in bankObj"
            :key="index"
            :label="key"
            style="width: 33%"
          >
            <span>{{ val }}</span>
          </el-form-item>
        </el-form>
      </div>

      <div slot="footer" class="dialog-footer">
        <el-button @click="dispatchFormVisible = false">关闭</el-button>
      </div>
    </el-dialog>

    <el-dialog title="交接详情" :visible.sync="handoverDetailFormVisible">
      <div>
        <my-table
          :data-list="handoverDetailList"
          :table-list="handoverTableList"
        />
        <el-form label-position="right" inline class="bank-obj">
          <el-form-item
            v-for="(val,key,index) in bankObj"
            :key="index"
            :label="key"
            style="width: 33%"
          >
            <span>{{ val }}</span>
          </el-form-item>
        </el-form>
      </div>
    </el-dialog>

    <!-- 交接调整 -->
    <el-dialog title="交接调整" :visible.sync="handoverFormVisible" :close-on-click-modal="false">
      <el-form ref="handoverForm" :rules="handoverRules" :model="handoverForm" status-icon label-position="left" label-width="100px" style="width: 400px; margin-left:50px;">
        <el-form-item label="钞盒数" prop="boxCount">
          <el-input-number v-model="handoverForm.boxCount" step-strictly />
        </el-form-item>
        <el-form-item label="钞袋数" prop="bagCount">
          <el-input-number v-model="handoverForm.bagCount" step-strictly />
        </el-form-item>
        <el-form-item label="备注" prop="comments">
          <el-input v-model="handoverForm.comments" :maxlength="64" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="handoverFormVisible = false">取消</el-button>
        <el-button type="primary" @click="handoverData">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import Pagination from '@/components/Pagination'
import { formatMoney } from '@/utils/convert'
import myTable from '@/components/Table'
import {
  listRoute,
  addRoute,
  updateRoute,
  changeRoute,
  deleteRoute,
  confirmRoute,
  routeEmpChange,
  cashboxPack,
  copyRoute,
  overRoute,
  alertRoute,
  detailLeaderLog,
  detailDispatch,
  handoverRoute,
  detailHandover
} from '@/api/escort/route'
import { formatdate } from '@/utils/date'
import { dictionaryData, reqDictionary } from '@/api/system/dictionary'
import {
  authOption,
  vehicleOption,
  employeeOption,
  jobNameOption
} from '@/api/common/selectOption'

export default {
  components: { Pagination, myTable },
  data() {
    return {
      list: [],
      listQuery: {
        limit: 10,
        page: 1,
        departmentId: null,
        routeName: null,
        routeNo: null,
        routeType: null,
        routeDate: new Date(new Date().toLocaleDateString()).getTime()
      },
      options: {
        routeType: [
          { code: 0, content: '固定线路' },
          { code: 1, content: '临时线路' }
        ],
        templateType: [
          { code: 0, content: '离行式线路' },
          { code: 1, content: '附行式线路' }
        ],
        jobType: [
          { code: -1, content: '车牌号' },
          { code: 0, content: '其它' },
          { code: 1, content: '司机岗' },
          { code: 2, content: '护卫岗' },
          { code: 3, content: '钥匙岗' },
          { code: 4, content: '密码岗' },
          { code: 5, content: '清点岗' },
          { code: 6, content: '库管岗' }
        ]
      },
      departmentId: null,
      listLoading: true,
      total: 0,
      authOption: [],
      tableList: [
        {
          label: '线路编号',
          prop: 'routeNo',
          width: 80
        },
        {
          label: '线路名称',
          prop: 'routeName',
          width: 120
        },
        {
          label: '线路类型',
          prop: 'routeType',
          width: 100,
          formatter: this.formatType
        },
        {
          label: '模板类型',
          prop: 'templateType',
          width: 100,
          formatter: this.formatTemplateType
        },
        {
          label: '分配车辆',
          prop: 'seqno',
          width: 140,
          formatter: this.formatSeqno
        },
        {
          label: '任务日期',
          prop: 'routeDate',
          formatter: this.formatDate,
          width: 120
        },
        // {
        //   label: '计划开始时间',
        //   prop: 'planBeginTime',
        //   formatter: this.formatTime
        // },
        // {
        //   label: '计划结束时间',
        //   prop: 'planFinishTime',
        //   formatter: this.formatTime
        // },
        {
          label: '实际开始时间',
          prop: 'actBeginTime',
          formatter: this.formatTime
        },
        {
          label: '实际结束时间',
          prop: 'actFinishTime',
          formatter: this.formatTime
        },
        {
          label: '状态',
          prop: 'statusT',
          formatter: this.formatStatus
        }
      ],
      expandList: [
        {
          label: '司机',
          prop: 'driverName'
        },
        {
          label: '复核时间',
          prop: 'dispCfmTime',
          formatter: this.formatTime
        },
        {
          label: '业务员',
          prop: 'routeKeyManName',
          formatter: this.formatName
        },
        {
          label: '领取钞袋',
          prop: 'dispBagCount'
        },
        {
          label: '护卫',
          prop: 'securityAName',
          formatter: this.formatSecurity
        },
        {
          label: '交接人员',
          prop: 'hdoverOperManName',
          formatter: this.formatHdover
        },
        {
          label: '跟车人员',
          prop: 'followerName'
        },
        {
          label: '交接时间',
          prop: 'hdoverTime',
          formatter: this.formatTime
        },
        {
          label: '配钞员',
          prop: 'dispOperManName',
          formatter: this.formatDisp
        },
        {
          label: '交接钞袋',
          prop: 'returnBagCount'
        },
        {
          label: '配钞时间',
          prop: 'dispTime',
          formatter: this.formatTime
        },
        {
          label: '交接钞盒',
          prop: 'returnBoxCount'
        }
      ],
      batchList: [],
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '创建',
        copy: '复制',
        change: '信息变更'
      },
      dataForm: {
        departmentId: null,
        follower: null,
        driver: null,
        id: null,
        // planBeginTime: null,
        // planFinishTime: null,
        routeDate: null,
        routeKeyMan: null,
        routeName: null,
        routeNo: null,
        routeOperMan: null,
        securityA: null,
        securityB: null,
        comments: null,
        vehicleId: null,
        templateType: 0
      },
      isDone: false,
      isChange: false,
      employeeOption: [],
      jobNameOption: {},
      vehicleOption: [],
      dictionaryData,
      rules: {
        departmentId: [{ required: true, message: '所属部门不能为空', trigger: 'blur' }],
        driver: [{ required: true, message: '司机不能为空', trigger: 'blur' }],
        // planBeginTime: [
        //   { required: true, message: '计划开始时间不能为空', trigger: 'blur' }
        // ],
        // planFinishTime: [
        //   { required: true, message: '计划结束时间不能为空', trigger: 'blur' }
        // ],
        routeDate: [
          { required: true, message: '任务日期不能为空', trigger: 'blur' }
        ],
        templateType: [
          { required: true, message: '模板类型不能为空', trigger: 'blur' }
        ],
        routeKeyMan: [
          { required: true, message: '业务-钥匙员不能为空', trigger: 'blur' }
        ],
        routeName: [
          { required: true, message: '线路名称不能为空', trigger: 'blur' }
        ],
        routeNo: [
          { required: true, message: '线路编号不能为空', trigger: 'blur' },
          { type: 'number', message: '线路编号必须为数字值' }
        ],
        routeOperMan: [
          { required: true, message: '业务-密码员不能为空', trigger: 'blur' }
        ],
        securityA: [
          { required: true, message: '护卫A不能为空', trigger: 'blur' }
        ],
        securityB: [
          { required: true, message: '护卫B不能为空', trigger: 'blur' }
        ],
        vehicleId: [
          { required: true, message: '分配车辆不能为空', trigger: 'blur' }
        ]
      },
      // 详情
      activeNames: ['1'],
      detailFormVisible: false,
      deatilListLoading: false,
      detailList: [],
      deatilTableList: [
        {
          label: '类型',
          prop: 'jobType',
          formatter: this.formatJobTYpe
        },
        {
          label: '变更前',
          prop: 'oldManName'
        },
        {
          label: '变更后',
          prop: 'newManName'
        },
        {
          label: '备注',
          prop: 'comments'
        },
        {
          label: '调整时间',
          prop: 'createTime',
          formatter: this.formatDateTime
        }
      ],
      atmTaskCheck: {
        checkItemResult: {}
      },
      atmTaskClean: null,
      atmTaskRepair: null,
      cashboxVisible: false,
      cashboxForm: {},
      loading: null,
      // 车长
      masterDetailList: [],
      masterTableList: [
        {
          label: '检查项目',
          prop: 'content'
        },
        {
          label: '结果',
          prop: 'code',
          formatter: this.formatCheck,
          width: 100
        },
        {
          label: '备注',
          prop: 'comment'
        }
      ],
      masterComments: '',
      masterOption: [],
      masterFormVisible: false,
      // 配钞
      dispatchDetailList: [],
      dispatchTableList: [
        {
          label: '钞盒',
          prop: 'boxNo'
        },
        {
          label: '券别类型',
          prop: 'denomName'
        },
        {
          label: '金额（万元）',
          prop: 'amount',
          formatter: this.formatAmount
        },
        {
          label: '所属银行',
          prop: 'bankName'
        },
        {
          label: '状态',
          prop: 'statusT',
          formatter: this.formatDispatchStatus
        }
      ],
      bankObj: {},
      dispatchFormVisible: false,
      // 交接调整
      handoverFormVisible: false,
      handoverForm: {
        id: null,
        boxCount: 0,
        bagCount: 0,
        comments: ''
      },
      handoverRules: {
        boxCount: [{ required: true, message: '钞盒数不能为空', trigger: 'blur' }],
        bagCount: [{ required: true, message: '钞袋数不能为空', trigger: 'blur' }]
      },
      handoverDetailFormVisible: false,
      handoverDetailList: [],
      handoverTableList: [
        {
          label: '类型',
          prop: 'changeType'
        },
        {
          label: '变更前',
          prop: 'oldCount'
        },
        {
          label: '变更后',
          prop: 'newCount'
        },
        {
          label: '备注',
          prop: 'comments'
        },
        {
          label: '调整时间',
          prop: 'createTime',
          formatter: this.formatDateTime
        }
      ]
    }
  },
  computed: {
    keyOption() {
      if (this.jobNameOption[3]) {
        return this.jobNameOption[3].concat(this.jobNameOption[4])
      }
      return []
    },
    operOption() {
      if (this.jobNameOption[4]) {
        return this.jobNameOption[4].concat(this.jobNameOption[3])
      }
      return []
    }
  },
  mounted() {
    this.getOptions().then(() => {
      this.getList()
    })
    reqDictionary('ROUTE_LOG_ITEM').then(res => {
      this.masterOption = res.data
    })
  },
  methods: {
    getList(data) {
      if (data) {
        for (const key in data) {
          this.listQuery[key] = data[key]
        }
      }
      listRoute(this.listQuery).then((res) => {
        const data = res.data
        this.list = data.list
        this.departmentId = this.listQuery.departmentId
        this.total = data.total
        this.listLoading = false
      })
    },
    getOptions() {
      return new Promise((resolve, reject) => {
        Promise.all([authOption()])
          .then((res) => {
            this.authOption = res[0].data
            this.listQuery.departmentId = this.authOption[0].id
            resolve()
          })
          .catch((err) => {
            reject(err)
          })
      })
    },
    handleOptions(departmentId = this.departmentId) {
      return new Promise((reslove, reject) => {
        Promise.all([
          employeeOption(departmentId),
          jobNameOption(departmentId, '1,2,3,4'),
          vehicleOption(departmentId)
        ])
          .then((res) => {
            const [res1, res2, res3] = res
            this.employeeOption = res1.data
            this.jobNameOption = res2.data
            this.vehicleOption = res3.data
            reslove()
          })
          .catch((err) => {
            reject(err)
          })
      })
    },
    handleCreate() {
      this.isDone = this.isChange = false
      this.dialogStatus = 'create'
      this.rules.routeKeyMan[0].required = this.rules.routeOperMan[0].required = this.rules.securityA[0].required = this.rules.securityB[0].required = true
      if (this.employeeOption.length > 0) {
        this.dialogFormVisible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].resetFields()
          this.dataForm.departmentId = this.departmentId
        })
      } else {
        this.handleOptions().then(() => {
          this.dialogFormVisible = true
          this.$nextTick(() => {
            this.$refs['dataForm'].resetFields()
            this.dataForm.departmentId = this.departmentId
          })
        })
      }
    },
    handleUpdate(row) {
      this.handleOptions(row.departmentId).then(() => {
        this.isChange = false
        row.statusT === 0 ? this.isDone = false : this.isDone = true
        this.templateTypeChange(row.routeType)
        this.dialogStatus = 'update'
        this.dialogFormVisible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].clearValidate()
          for (const key in this.dataForm) {
            this.dataForm[key] = row[key]
          }
          this.dataForm.routeNo = +this.dataForm.routeNo
          if (this.isDone) {
            this.dataForm.comments = ''
          }
          // this.dataForm.planBeginTime = this.formatDateMinute(row.planBeginTime)
          // this.dataForm.planFinishTime = this.formatDateMinute(row.planFinishTime)
          this.dataForm.follower = this.dataForm.follower ? this.dataForm.follower : null
        })
      })
    },
    handleCopy(row) {
      this.handleOptions(row.departmentId).then(() => {
        this.isDone = this.isChange = false
        this.templateTypeChange(row.routeType)
        this.dialogStatus = 'copy'
        this.dialogFormVisible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].clearValidate()
          for (const key in this.dataForm) {
            this.dataForm[key] = row[key]
          }
          this.dataForm.routeNo = +this.dataForm.routeNo
          // this.dataForm.planBeginTime = this.formatDateMinute(row.planBeginTime)
          // this.dataForm.planFinishTime = this.formatDateMinute(row.planFinishTime)
          this.dataForm.follower = this.dataForm.follower ? this.dataForm.follower : null
        })
      })
    },
    handleChange(row) {
      this.handleOptions(row.departmentId).then(() => {
        this.isChange = true
        row.statusT === 0 ? this.isDone = false : this.isDone = true
        this.templateTypeChange(row.routeType)
        this.dialogStatus = 'change'
        this.dialogFormVisible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].clearValidate()
          for (const key in this.dataForm) {
            this.dataForm[key] = row[key]
          }
          this.dataForm.routeNo = +this.dataForm.routeNo
          if (this.isDone) {
            this.dataForm.comments = ''
          }
          this.dataForm.follower = this.dataForm.follower ? this.dataForm.follower : null
        })
      })
    },
    handleHandover(row) {
      this.handoverFormVisible = true
      this.$nextTick(() => {
        this.$refs['handoverForm'].clearValidate()
        this.handoverForm.bagCount = row.returnBagCount
        this.handoverForm.boxCount = row.returnBoxCount
        this.handoverForm.id = row.id
        this.handoverForm.comments = ''
      })
    },
    handleOver(row) {
      if (row.routeType !== 1) {
        return this.$message.warning('固定线路不能手动交接')
      }
      if (row.statusT < 2) {
        return this.$message({
          dangerouslyUseHTMLString: true,
          message: `当前线路状态为${this.formatStatus(row.statusT)}，不能进行手动交接`,
          type: 'warning'
        })
      }
      this.openLoading()
      overRoute(row.id).then(() => {
        this.getList()
        this.$notify.success({
          title: '成功',
          message: '手动交接成功'
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
        this.listLoading = true
        deleteRoute(row.id)
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
    handleConfirm(row) {
      this.$confirm('确定确认线路吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.listLoading = true
        confirmRoute(row.id)
          .then(() => {
            this.getList()
            this.$notify.success({
              title: '成功',
              message: '确认成功'
            })
          })
          .finally(() => {
            this.listLoading = false
          })
      })
    },
    handleDetail(row) {
      routeEmpChange(row.id).then((res) => {
        this.detailFormVisible = true
        this.detailList = res.data
      })
    },
    handleDetailMaster(id) {
      detailLeaderLog(id).then(res => {
        this.masterDetailList = res.data.details.map(item => {
          return {
            content: this.masterOption.find(elm => elm.code === item.code).content,
            code: item.chk,
            comment: item.cmt
          }
        })
        this.masterComments = res.data.comments
        this.masterFormVisible = true
      })
    },
    handleHandoverDetail(id) {
      detailHandover(id).then(res => {
        this.handoverDetailList = res.data
        this.handoverDetailFormVisible = true
      })
    },
    handleDetailDispatch(id) {
      detailDispatch(id).then(res => {
        this.dispatchDetailList = res.data
        this.dispatchFormVisible = true
        const bankObj = {}
        res.data.forEach(item => {
          if (bankObj[item.bankName] == null) {
            bankObj[item.bankName] = 1
          } else {
            bankObj[item.bankName]++
          }
        })
        this.bankObj = bankObj
      })
    },
    handleAlert(list) {
      this.$confirm('确认推送所选线路吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.listLoading = true
        alertRoute(list.map(item => item.id))
          .then(() => {
            this.$notify.success({
              title: '成功',
              message: '推送成功'
            })
            this.getList()
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
          if (type === 'update') {
            updateRoute(this.dataForm)
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
          } else if (type === 'change') {
            changeRoute(this.dataForm)
              .then(() => {
                this.getList()
                this.dialogFormVisible = false
                this.$notify.success({
                  title: '成功',
                  message: '变更成功'
                })
              })
              .finally(() => {
                this.listLoading = false
              })
          } else if (type === 'copy') {
            copyRoute(this.dataForm)
              .then(() => {
                this.getList()
                this.dialogFormVisible = false
                this.$notify.success({
                  title: '成功',
                  message: '复制成功'
                })
              })
              .finally(() => {
                this.listLoading = false
              })
          } else {
            addRoute(this.dataForm)
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
    cashboxDetail(id) {
      this.openLoading()
      cashboxPack(id).then(res => {
        this.cashboxForm = res.data
        this.cashboxVisible = true
      }).finally(() => {
        this.loading.close()
      })
    },
    handoverData() {
      this.$refs['handoverForm'].validate((valid) => {
        if (valid) {
          handoverRoute(this.handoverForm).then(() => {
            this.getList()
            this.handoverFormVisible = false
            this.$notify.success({
              title: '成功',
              message: '调整成功'
            })
          })
        }
      })
    },
    selectionChange(list) {
      this.batchList = list
    },
    formatMoney,
    formatName(name, row) {
      return `${name}、${row.routeOperManName}`
    },
    formatSecurity(name, row) {
      return `${name}、${row.securityBName}`
    },
    formatHdover(name, row) {
      return row.hdoverCheckManName === '' ? name : `${name}、${row.hdoverCheckManName}`
    },
    formatDisp(name, row) {
      return row.dispCheckManName === '' ? name : `${name}、${row.dispCheckManName}`
    },
    formatType(type) {
      switch (type) {
        case 0:
          return '固定线路'
        case 1:
          return '临时线路'
      }
    },
    formatDispatchStatus(status) {
      switch (status) {
        case 0:
          return '<span style="color: rgb(78, 223, 65)">封装</span>'
        case 2:
          return '<span style="color: rgb(87, 171, 219)">配钞</span>'
        case 3:
          return '<span style="color: rgb(48, 45, 212)">拆封</span>'
      }
    },
    formatAmount(num) {
      return num / 10000
    },
    formatTemplateType(type) {
      return this.options.templateType.filter(item => item.code === type)[0].content
    },
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
    formatDateMinute(timestamp) {
      if (!timestamp || timestamp === 0) {
        return '00:00'
      }
      const date = new Date(timestamp)
      return formatdate(date, 'hh:mm')
    },
    formatSeqno(seqno, row) {
      if (seqno === '') {
        return '-'
      } else {
        return `${seqno}/${row.lpno}`
      }
    },
    formatStatus(status) {
      switch (status) {
        case 0:
          return '<span style="color:#d12e22">新建</span>'
        case 1:
          return '<span style="color:#dfe000">已审核</span>'
        case 2:
          return '<span style="color:#aa6bbb">配钞完成</span>'
        case 3:
          return '<span style="color:#119fff">任务中...</span>'
        case 4:
          return '<span style="color:green">交接完成</span>'
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
    formatCleanStatus(type) {
      for (const item of dictionaryData['CLEAN_STATUS']) {
        if (type === item.code) {
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
    formatResult(num) {
      switch (num) {
        case 0:
          return '<span style="color: red">否</span>'
        case 1:
          return '是'
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
    formatJobTYpe(type) {
      return this.options.jobType.find(item => item.code === type).content
    },
    formatCheck(status) {
      switch (status) {
        case 0:
          return '否'
        case 1:
          return '是'
      }
    },
    departmentIdChange(val) {
      this.dataForm.follower = null
      this.dataForm.securityB = null
      this.dataForm.securityA = null
      this.dataForm.driver = null
      this.dataForm.routeKeyMan = null
      this.dataForm.routeOperMan = null
      this.dataForm.vehicleId = null
      this.handleOptions(val)
    },
    templateTypeChange(val) {
      this.dataForm.routeKeyMan = this.dataForm.routeOperMan = this.dataForm.securityA = this.dataForm.securityB = null
      if (val === 1) {
        this.rules.routeKeyMan[0].required = this.rules.routeOperMan[0].required = this.rules.securityA[0].required = this.rules.securityB[0].required = false
      } else {
        this.rules.routeKeyMan[0].required = this.rules.routeOperMan[0].required = this.rules.securityA[0].required = this.rules.securityB[0].required = true
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
.filter-container *:nth-child(n + 2) {
  margin-left: 10px;
}

.box-collapse {
  margin-top: 40px;
  ::v-deep .el-collapse-item__header{
    font-size: 18px;
    height: 60px;
    line-height: 60px;
    font-weight: 600;
    padding-left: 20px;
  }
}

.el-dialog__wrapper {
  ::v-deep .el-dialog__body {
    padding: 20px;
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

.title {
  font-size: 16px;
  font-weight: 600;
  margin: 20px 0;
}

.confirm-info {
  i{
    font-size: 20px;
    vertical-align: middle;
    font-weight: 600;
  }
  .el-icon-check {
    color: rgb(82, 240, 82);
  }
  .el-icon-close {
    color: rgb(248, 51, 51);
  }
}

.comment label {
  width: 120px;
  color: #99a9bf;
  display: flex;
  flex-wrap: wrap;
}

.bank-obj {
  font-size: 0;
  margin-top:20px
}
.bank-obj label {
  width: 120px;
  color: #99a9bf;
  display: flex;
  flex-wrap: wrap;
}
.bank-obj .el-form-item {
  margin-right: 0;
  margin-bottom: 0;
  width: 50%;
}
</style>
