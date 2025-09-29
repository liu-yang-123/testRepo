<template>
  <div class="app-container">
    <search-bar
      :list-query="listQuery"
      :search-list="searchList"
      :options="options"
      :role="role"
      @lookUp="getList"
      @create="handleCreate"
    />
    <my-table
      :list-loading="listLoading"
      :data-list="list"
      :table-list="tableList"
      class="my-table"
    >
      <template v-slot:operate>
        <el-table-column
          align="center"
          label="券别明细"
          class-name="small-padding fixed-width"
          width="120"
        >
          <template slot-scope="scope">
            <el-button
              v-permission="['/base/trade/clearTask/detail']"
              :disabled="scope.row.haveDetail !== 1"
              type="text"
              size="mini"
              @click="handleDetail(scope.row)"
            >明细</el-button>
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          label="结果明细"
          class-name="small-padding fixed-width"
          width="120"
        >
          <template slot-scope="scope">
            <el-button
              v-permission="['/base/trade/clearTask/resultDetail']"
              type="text"
              size="mini"
              @click="handleResult(scope.row)"
            >明细</el-button>
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          label="操作"
          class-name="small-padding fixed-width"
          width="400"
        >
          <template slot-scope="scope">
            <el-button
              v-permission="['/base/trade/clearTask/update']"
              :disabled="scope.row.status !== 0"
              type="primary"
              size="mini"
              @click="handleUpdate(scope.row)"
            >编辑</el-button>
            <el-button
              v-permission="['/base/trade/clearTask/delete']"
              :disabled="scope.row.status !== 0"
              type="danger"
              size="mini"
              @click="handleDelete(scope.row)"
            >删除</el-button>
            <el-button
              v-permission="['/base/trade/clearTask/confirm']"
              :disabled="scope.row.status !== 0"
              type="info"
              size="mini"
              @click="handleConfirm(scope.row)"
            >确认</el-button>
            <el-button
              v-permission="['/base/trade/clearTask/finish']"
              :disabled="scope.row.status !== 1"
              type="success"
              size="mini"
              @click="handleFinish(scope.row)"
            >完成</el-button>
            <el-button
              v-permission="['/base/trade/clearTask/cancel']"
              :disabled="scope.row.status !== 1 && scope.row.status !== 2"
              type="warning"
              size="mini"
              @click="handleCancel(scope.row)"
            >撤销</el-button>
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

    <!-- <el-dialog title="详情" :visible.sync="dialogFormVisible">
      <div style="padding-left:6%">
        <div class="title">加钞间</div>
        <el-form label-position="right" inline class="confirm-info">
          <el-form-item label="门窗、锁、门禁是否正常">
            <i :class="detailForm.roomDoor ? 'el-icon-check' : 'el-icon-close'" />
          </el-form-item>
          <el-form-item label="消防设备是否完好">
            <i :class="detailForm.roomFireEqt ? 'el-icon-check' : 'el-icon-close'" />
          </el-form-item>
          <el-form-item label="空调灯具是否正常">
            <i :class="detailForm.roomlight ? 'el-icon-check' : 'el-icon-close'" />
          </el-form-item>
        </el-form>
        <div class="title">取款大厅</div>
        <el-form label-position="right" inline class="confirm-info">
          <el-form-item label="防护舱门、锁、插销是否完好">
            <i :class="detailForm.hallLock ? 'el-icon-check' : 'el-icon-close'" />
          </el-form-item>
          <el-form-item label="自助银行大门是否完好">
            <i :class="detailForm.hallDoor ? 'el-icon-check' : 'el-icon-close'" />
          </el-form-item>
          <el-form-item label="环境是否整洁">
            <i :class="detailForm.hallEmv ? 'el-icon-check' : 'el-icon-close'" />
          </el-form-item>
        </el-form>
      </div>

      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">关闭</el-button>
      </div>
    </el-dialog> -->
    <!-- 添加编辑 -->
    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible" :close-on-click-modal="false" width="60%">
      <el-form ref="dataForm" :rules="rules" :model="dataForm" status-icon label-position="left" label-width="100px" style="width: 90%; margin-left:6%;">
        <div v-show="dialogStatus != 'finish'">
          <el-form-item label="账务日期" prop="taskDate">
            <el-date-picker
              v-model="dataForm.taskDate"
              value-format="timestamp"
              type="date"
              style="width: 300px"
              placeholder="选择日期"
            />
          </el-form-item>
          <el-form-item label="清分类型" prop="clearType">
            <el-radio-group v-model="dataForm.clearType">
              <el-radio :label="1">领现</el-radio>
              <el-radio :label="2">回笼</el-radio>
              <el-radio :label="3">尾箱</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="机构名称" prop="bankId">
            <el-select v-model="dataForm.bankId" filterable placeholder="请先选择银行机构" class="filter-item" style="width: 300px">
              <el-option
                v-for="item in options.bankId"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="券别明细" prop="haveDetail">
            <el-radio-group v-model="dataForm.haveDetail" @change="detailChange">
              <el-radio :label="0">无明细</el-radio>
              <el-radio :label="1">有明细</el-radio>
            </el-radio-group>
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
        </div>
        <template v-if="dataForm.haveDetail === 1">
          <el-divider style="width: 600px">可用券</el-divider>
          <el-form-item label-width="0px" prop="usableDenomList">
            <el-table
              :data="dataForm.usableDenomList"
              border
              fit
              :header-cell-style="{'background-color':'#f5f5f5'}"
            >
              <!-- 字段 -->
              <el-table-column align="center" width="80">
                <template slot="header">
                  <el-button
                    plain
                    type="primary"
                    icon="el-icon-circle-plus-outline"
                    @click="dataForm.usableDenomList.push({})"
                  />
                </template>
                <template slot-scope="scope">
                  <el-button
                    plain
                    type="danger"
                    icon="el-icon-remove-outline"
                    @click="dataForm.usableDenomList.splice(scope.$index, 1);"
                  />
                </template>
              </el-table-column>
              <el-table-column align="center" label="券别">
                <template slot-scope="scope">
                  <el-form-item
                    :prop="'usableDenomList.' + scope.$index + '.denomId'"
                    label-width="0px"
                    :rules="[{ required: true, trigger: 'blur' }]"
                  >
                    <el-select v-model="scope.row.denomId" filterable placeholder="请先选择券别" class="filter-item" style="width: 90%">
                      <el-option
                        v-for="item in denomNameList"
                        :key="item.id"
                        :disabled="getDisabled(item.id,dataForm.usableDenomList)"
                        :label="item.name"
                        :value="item.id"
                      />
                    </el-select>
                  </el-form-item>
                </template>
              </el-table-column>
              <el-table-column align="center" label="金额">
                <template slot-scope="scope">
                  <el-form-item
                    :prop="'usableDenomList.' + scope.$index + '.amount'"
                    label-width="0px"
                    :rules="[{ required: true, trigger: 'blur' }]"
                  >
                    <el-input v-model="scope.row.amount" oninput="value=value.replace(/[^-+\d]/g,'')" style="width: 90%" :maxlength="32" />
                  </el-form-item>
                </template>
              </el-table-column>
            </el-table>
          </el-form-item>
          <el-divider style="width: 600px">残损券</el-divider>
          <el-form-item label-width="0px" prop="badDenomList">
            <el-table
              :data="dataForm.badDenomList"
              border
              fit
              :header-cell-style="{'background-color':'#f5f5f5'}"
            >
              <!-- 字段 -->
              <el-table-column align="center" width="80">
                <template slot="header">
                  <el-button
                    plain
                    type="primary"
                    icon="el-icon-circle-plus-outline"
                    @click="dataForm.badDenomList.push({})"
                  />
                </template>
                <template slot-scope="scope">
                  <el-button
                    plain
                    type="danger"
                    icon="el-icon-remove-outline"
                    @click="dataForm.badDenomList.splice(scope.$index, 1)"
                  />
                </template>
              </el-table-column>
              <el-table-column align="center" label="券别">
                <template slot-scope="scope">
                  <el-form-item
                    :prop="'badDenomList.' + scope.$index + '.denomId'"
                    label-width="0px"
                    :rules="[{ required: true, trigger: 'blur' }]"
                  >
                    <el-select v-model="scope.row.denomId" filterable placeholder="请先选择券别" class="filter-item" style="width: 90%">
                      <el-option
                        v-for="item in badDenomNameList"
                        :key="item.id"
                        :disabled="getDisabled(item.id,dataForm.badDenomList)"
                        :label="item.name"
                        :value="item.id"
                      />
                    </el-select>
                  </el-form-item>
                </template>
              </el-table-column>
              <el-table-column align="center" label="金额">
                <template slot-scope="scope">
                  <el-form-item
                    :prop="'badDenomList.' + scope.$index + '.amount'"
                    label-width="0px"
                    :rules="[{ required: true, trigger: 'blur' }]"
                  >
                    <el-input v-model.number="scope.row.amount" style="width: 100%" :disabled="scope.row.amountDisabled" :maxlength="32" />
                  </el-form-item>
                </template>
              </el-table-column>
            </el-table>
          </el-form-item>
          <el-divider style="width: 600px">完整券</el-divider>
          <el-form-item label-width="0px" prop="goodDenomList">
            <el-table
              :data="dataForm.goodDenomList"
              border
              fit
              :header-cell-style="{'background-color':'#f5f5f5'}"
            >
              <!-- 字段 -->
              <el-table-column align="center" width="80">
                <template slot="header">
                  <el-button

                    plain
                    type="primary"
                    icon="el-icon-circle-plus-outline"
                    @click="dataForm.goodDenomList.push({})"
                  />
                </template>
                <template slot-scope="scope">
                  <el-button
                    plain
                    type="danger"
                    icon="el-icon-remove-outline"
                    @click="dataForm.goodDenomList.splice(scope.$index, 1)"
                  />
                </template>
              </el-table-column>
              <el-table-column align="center" label="券别">
                <template slot-scope="scope">
                  <el-form-item
                    :prop="'goodDenomList.' + scope.$index + '.denomId'"
                    label-width="0px"
                    :rules="[{ required: true, trigger: 'blur' }]"
                  >
                    <el-select v-model="scope.row.denomId" filterable placeholder="请先选择券别" class="filter-item" style="width: 90%">
                      <el-option
                        v-for="item in denomNameList"
                        :key="item.id"
                        :disabled="getDisabled(item.id,dataForm.goodDenomList)"
                        :label="item.name"
                        :value="item.id"
                      />
                    </el-select>
                  </el-form-item>
                </template>
              </el-table-column>
              <el-table-column align="center" label="金额">
                <template slot-scope="scope">
                  <el-form-item
                    :prop="'goodDenomList.' + scope.$index + '.amount'"
                    label-width="0px"
                    :rules="[{ required: true, trigger: 'blur' }]"
                  >
                    <el-input v-model="scope.row.amount" oninput="value=value.replace(/[^-+\d]/g,'')" style="width: 90%" :maxlength="32" />
                  </el-form-item>
                </template>
              </el-table-column>
            </el-table>
          </el-form-item>
          <el-divider style="width: 600px">未清分</el-divider>
          <el-form-item label-width="0px" prop="unclearDenomList">
            <el-table
              :data="dataForm.unclearDenomList"
              border
              fit
              :header-cell-style="{'background-color':'#f5f5f5'}"
            >
              <!-- 字段 -->
              <el-table-column align="center" width="80">
                <template slot="header">
                  <el-button

                    plain
                    type="primary"
                    icon="el-icon-circle-plus-outline"
                    @click="dataForm.unclearDenomList.push({})"
                  />
                </template>
                <template slot-scope="scope">
                  <el-button

                    plain
                    type="danger"
                    icon="el-icon-remove-outline"
                    @click="dataForm.unclearDenomList.splice(scope.$index, 1)"
                  />
                </template>
              </el-table-column>
              <el-table-column align="center" label="券别">
                <template slot-scope="scope">
                  <el-form-item
                    :prop="'unclearDenomList.' + scope.$index + '.denomId'"
                    label-width="0px"
                    :rules="[{ required: true, trigger: 'blur' }]"
                  >
                    <el-select v-model="scope.row.denomId" filterable placeholder="请先选择券别" class="filter-item" style="width: 90%">
                      <el-option
                        v-for="item in denomNameList"
                        :key="item.id"
                        :disabled="getDisabled(item.id,dataForm.unclearDenomList)"
                        :label="item.name"
                        :value="item.id"
                      />
                    </el-select>
                  </el-form-item>
                </template>
              </el-table-column>
              <el-table-column align="center" label="金额">
                <template slot-scope="scope">
                  <el-form-item
                    :prop="'unclearDenomList.' + scope.$index + '.amount'"
                    label-width="0px"
                    :rules="[{ required: true, trigger: 'blur' }]"
                  >
                    <el-input v-model="scope.row.amount" oninput="value=value.replace(/[^-+\d]/g,'')" style="width: 90%" :maxlength="32" />
                  </el-form-item>
                </template>
              </el-table-column>
            </el-table>
          </el-form-item>
          <el-divider style="width: 600px">尾零钞</el-divider>
          <el-form-item label-width="0px" prop="remnantDenomList">
            <el-table
              :data="dataForm.remnantDenomList"
              border
              fit
              :header-cell-style="{'background-color':'#f5f5f5'}"
            >
              <el-table-column align="center" width="80">
                <template slot="header">
                  <el-button
                    plain
                    type="primary"
                    icon="el-icon-circle-plus-outline"
                    @click="dataForm.remnantDenomList.push({count: 0,amount: null,denomId: null})"
                  />
                </template>
                <template slot-scope="scope">
                  <el-button

                    plain
                    type="danger"
                    icon="el-icon-remove-outline"
                    @click="dataForm.remnantDenomList.splice(scope.$index, 1)"
                  />
                </template>
              </el-table-column>
              <el-table-column align="center" label="券别">
                <template slot-scope="scope">
                  <el-form-item
                    :prop="'remnantDenomList.' + scope.$index + '.denomId'"
                    label-width="0px"
                    :rules="[{ required: true, trigger: 'blur' }]"
                  >
                    <el-select v-model="scope.row.denomId" filterable placeholder="请先选择券别" class="filter-item" style="width: 90%">
                      <el-option
                        v-for="item in remnantDenomNameList"
                        :key="item.id"
                        :disabled="getDisabled(item.id,dataForm.remnantDenomList)"
                        :label="item.name"
                        :value="item.id"
                      />
                    </el-select>
                  </el-form-item>
                </template>
              </el-table-column>
              <el-table-column align="center" label="金额">
                <template slot-scope="scope">
                  <el-form-item
                    :prop="'remnantDenomList.' + scope.$index + '.amount'"
                    label-width="0px"
                    :rules="[{ required: true, trigger: 'blur' }]"
                  >
                    <el-input v-model="scope.row.amount" oninput="value=value.replace(/[^-+\d]/g,'')" style="width: 90%" :maxlength="32" @change="amountChange($event,scope.row)" />
                  </el-form-item>
                </template>
              </el-table-column>
              <el-table-column align="center" label="张数">
                <template slot-scope="scope">
                  <el-form-item
                    :prop="'remnantDenomList.' + scope.$index + '.count'"
                    label-width="0px"
                    :rules="[{ required: true, trigger: 'blur' }]"
                  >
                    <el-input-number v-model="scope.row.count" style="width:100%" :min="0" @change="countChange($event,scope.row)" />
                  </el-form-item>
                </template>
              </el-table-column>
            </el-table>
          </el-form-item>
        </template>
        <el-form-item v-else label="任务金额" prop="totalAmount">
          <el-input
            v-model="dataForm.totalAmount"
            style="width: 300px"
            placeholder="请输入任务金额"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="newData(dialogStatus)">确定</el-button>
      </div>
    </el-dialog>

    <!-- 任务明细框 -->
    <el-dialog title="结果明细" :visible.sync="detailDialogFormVisible" width="65%" :close-on-click-modal="false">
      <div v-if="bankDenomList.usable && bankDenomList.usable.length > 0">
        <el-divider>可用券</el-divider>
        <el-table
          :data="bankDenomList.usable"
          show-summary
          :summary-method="getSummaries"
          :header-cell-style="{background:'#eef1f6',color:'#606266'}"
          style="width: 100%"
        >
          <el-table-column
            prop="denomText"
            label="券别"
          />
          <el-table-column
            prop="amount"
            label="金额(元)"
            :formatter="((row) => {return formatMoney(row.amount)})"
          />
          <el-table-column label="张数">
            <template slot-scope="scope">
              <div v-if="scope.row.denomType === 1">{{ scope.row.count }}</div>
              <div v-else>——</div>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div v-if="bankDenomList.bad && bankDenomList.bad.length > 0">
        <el-divider>残损券</el-divider>
        <el-table
          :data="bankDenomList.bad"
          show-summary
          :summary-method="getBadSummaries"
          :header-cell-style="{background:'#eef1f6',color:'#606266'}"
          style="width: 100%"
        >
          <el-table-column
            prop="denomText"
            label="券别"
          />
          <el-table-column
            prop="amount"
            label="金额(元)"
            :formatter="((row) => {return formatMoney(row.amount)})"
          />
          <el-table-column label="张数" prop="count">
            <template slot-scope="scope">
              <div v-if="scope.row.denomType === 1">{{ scope.row.count }}</div>
              <div v-else>——</div>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div v-if="bankDenomList.good && bankDenomList.good.length > 0">
        <el-divider>完整券</el-divider>
        <el-table
          :data="bankDenomList.good"
          show-summary
          :summary-method="getSummaries"
          :header-cell-style="{background:'#eef1f6',color:'#606266'}"
          style="width: 100%"
        >
          <el-table-column
            prop="denomText"
            label="券别"
          />
          <el-table-column
            prop="amount"
            label="金额(元)"
            :formatter="((row) => {return formatMoney(row.amount)})"
          />
          <el-table-column label="张数">
            <template slot-scope="scope">
              <div v-if="scope.row.denomType === 1">{{ scope.row.count }}</div>
              <div v-else>——</div>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div v-if="bankDenomList.unclear && bankDenomList.unclear.length > 0">
        <el-divider>未清分券</el-divider>
        <el-table
          :data="bankDenomList.unclear"
          show-summary
          :summary-method="getSummaries"
          :header-cell-style="{background:'#eef1f6',color:'#606266'}"
          style="width: 100%"
        >
          <el-table-column
            prop="denomText"
            label="券别"
          />
          <el-table-column
            prop="amount"
            label="金额(元)"
            :formatter="((row) => {return formatMoney(row.amount)})"
          />
          <el-table-column label="张数">
            <template slot-scope="scope">
              <div v-if="scope.row.denomType === 1">{{ scope.row.count }}</div>
              <div v-else>——</div>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div v-if="bankDenomList.remnant && bankDenomList.remnant.length > 0">
        <el-divider>尾零钞</el-divider>
        <el-table
          :data="bankDenomList.remnant"
          show-summary
          :summary-method="getBadSummaries"
          :header-cell-style="{background:'#eef1f6',color:'#606266'}"
          style="width: 100%"
        >
          <el-table-column
            prop="denomText"
            label="券别"
          />
          <el-table-column
            prop="amount"
            label="金额(元)"
            :formatter="((row) => {return formatMoney(row.amount)})"
          />
          <el-table-column label="张数" prop="count">
            <template slot-scope="scope">
              <div v-if="scope.row.denomType === 4">{{ scope.row.count }}</div>
              <div v-else>——</div>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailDialogFormVisible = false">关闭</el-button>
      </div>
    </el-dialog>

    <!-- 券别明细 -->
    <el-dialog title="券别明细" :visible.sync="denomDialogFormVisible" top="10vh" width="65%" :close-on-click-modal="false">
      <div v-if="detailForm.usable && detailForm.usable.length > 0">
        <el-divider style="width: 600px">可用券</el-divider>
        <my-table
          :data-list="detailForm.usable"
          :table-list="detailTableList"
          class="my-table"
        />
      </div>
      <div v-if="detailForm.bad && detailForm.bad.length > 0">
        <el-divider style="width: 600px">残损券</el-divider>
        <my-table
          :data-list="detailForm.bad"
          :table-list="detailTableList"
          class="my-table"
        />
      </div>
      <div v-if="detailForm.good && detailForm.good.length > 0">
        <el-divider style="width: 600px">完整券</el-divider>
        <my-table
          :data-list="detailForm.good"
          :table-list="detailTableList"
          class="my-table"
        />
      </div>
      <div v-if="detailForm.unclear && detailForm.unclear.length > 0">
        <el-divider style="width: 600px">未清分</el-divider>
        <my-table
          :data-list="detailForm.unclear"
          :table-list="detailTableList"
          class="my-table"
        />
      </div>
      <div v-if="detailForm.remnant && detailForm.remnant.length > 0">
        <el-divider style="width: 600px">尾零钞</el-divider>
        <my-table
          :data-list="detailForm.remnant"
          :table-list="[...detailTableList, {label:'张数',prop:'count'}]"
          class="my-table"
        />
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="denomDialogFormVisible = false">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import Pagination from '@/components/Pagination'
import myTable from '@/components/Table'
import { formatdate } from '@/utils/date'
import { formatMoney } from '@/utils/convert'
import searchBar from '@/components/SearchBar'
import { listTask, addTask, updateTask, deleteTask, detailTask, finishTask, resultTask, cancelTask, confirmTask } from '@/api/cleanCheck/task'
import { dictionaryData } from '@/api/system/dictionary'
import { bankTradeOption, authOption, denomOption } from '@/api/common/selectOption'
export default {
  components: { myTable, searchBar, Pagination },
  data() {
    return {
      options: {
        clearType: [
          { code: 1, content: '领现' },
          { code: 2, content: '回笼' },
          { code: 3, content: '尾箱' }
        ],
        bankId: [],
        departmentId: []
      },
      list: [],
      total: 0,
      listQuery: {
        page: 1,
        limit: 10,
        departmentId: null,
        bankId: null,
        dateBegin: null,
        dateEnd: null,
        clearType: null
      },
      listLoading: true,
      searchList: [
        { name: 'departmentId', label: '部门', type: 3, notClear: true, change: this.depChange },
        { name: 'date', type: 6 },
        { name: 'bankId', label: '银行网点', type: 3 },
        { name: 'clearType', label: '类型', type: 3 }
      ],
      role: {
        list: '/base/trade/clearTask/list',
        add: '/base/trade/clearTask/save'
      },
      tableList: [
        {
          label: '任务日期',
          prop: 'taskDate',
          formatter: this.formatDate
        },
        {
          label: '银行',
          prop: 'bankName'
        },
        {
          label: '清分类型',
          prop: 'clearType',
          formatter: this.formatClearType
        },
        {
          label: '任务金额',
          prop: 'totalAmount',
          formatter: this.formatMoney
        },
        {
          label: '任务状态',
          prop: 'status',
          formatter: this.formatStatus
        },
        {
          label: '清点金额',
          prop: 'realityAmount',
          formatter: this.formatMoney
        }
      ],
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '创建',
        finish: '完成'
      },
      dataForm: {
        id: null,
        departmentId: null,
        taskDate: null,
        clearType: null,
        bankId: null,
        haveDetail: 0,
        totalAmount: null,
        comments: null,
        taskRecordList: [],
        usableDenomList: [],
        badDenomList: [],
        goodDenomList: [],
        unclearDenomList: [],
        remnantDenomList: []
      },
      rules: {
        taskDate: [
          { required: true, message: '日期不能为空', trigger: 'blur' }
        ],
        clearType: [
          { required: true, message: '清分类型不能为空', trigger: 'blur' }
        ],
        bankId: [
          { required: true, message: '银行不能为空', trigger: 'blur' }
        ],
        haveDetail: [
          { required: true, message: '券别明细不能为空', trigger: 'blur' }
        ],
        totalAmount: [
          { required: true, message: '任务金额不能为空', trigger: 'blur' }
        ]
      },
      denomNameList: [],
      remnantDenomNameList: [],
      badDenomNameList: [],
      // 任务明细
      detailDialogFormVisible: false,
      bankDenomList: {},
      // 券别明细
      denomDialogFormVisible: false,
      detailForm: {
        bad: [],
        good: [],
        remnant: [],
        unclear: [],
        usable: []
      },
      detailTableList: [
        {
          label: '券别',
          prop: 'denomText'
        },
        {
          label: '金额',
          prop: 'amount',
          formatter: this.formatMoney
        }
      ]
    }
  },
  mounted() {
    denomOption().then(res => {
      this.denomNameList = res.data.filter(item => item.version === 0)
      this.badDenomNameList = res.data.filter(item => item.version === 0 || item.version === 1)
      this.remnantDenomNameList = res.data.filter(item => item.version === 0 || item.version === 2)
    })
    authOption().then(res => {
      this.options.departmentId = res.data
      if (this.options.departmentId.length > 0) {
        this.listQuery.departmentId = this.options.departmentId[0].id
        this.getList()
        this.getBankOption()
      }
    })
  },
  methods: {
    getList(data) {
      if (data) {
        for (const key in data) {
          this.listQuery[key] = data[key]
        }
      }
      const { date, ...params } = this.listQuery
      if (date) {
        params.dateBegin = date[0]
        params.dateEnd = date[1]
      }
      listTask(params).then((res) => {
        this.list = res.data.list
        this.total = res.data.total
        this.listLoading = false
      })
    },
    getBankOption() {
      bankTradeOption(this.listQuery.departmentId, 3).then(res => {
        this.options.bankId = res.data
      })
    },
    handleCreate() {
      this.dialogStatus = 'create'
      this.dialogFormVisible = true
      this.dataForm.usableDenomList = []
      this.dataForm.badDenomList = []
      this.dataForm.goodDenomList = []
      this.dataForm.unclearDenomList = []
      this.dataForm.remnantDenomList = []
      this.dataForm.totalAmount = null
      this.$nextTick(() => {
        this.$refs['dataForm'].resetFields()
        this.rules.totalAmount[0].required = this.rules.haveDetail[0].required = this.rules.taskDate[0].required = this.rules.clearType[0].required = this.rules.bankId[0].required = true
        this.dataForm.usableDenomList = []
        this.dataForm.badDenomList = []
        this.dataForm.goodDenomList = []
        this.dataForm.unclearDenomList = []
        this.dataForm.remnantDenomList = []
      })
    },
    handleUpdate(row) {
      detailTask(row.id).then(res => {
        const { bad, good, remnant, unclear, usable } = res.data
        this.dataForm.badDenomList = bad
        this.dataForm.goodDenomList = good
        this.dataForm.remnantDenomList = remnant
        this.dataForm.unclearDenomList = unclear
        this.dataForm.usableDenomList = usable
        this.dialogStatus = 'update'
        this.dialogFormVisible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].clearValidate()
          this.rules.totalAmount[0].required = this.rules.haveDetail[0].required = this.rules.taskDate[0].required = this.rules.clearType[0].required = this.rules.bankId[0].required = true
          for (const key in this.dataForm) {
            if (row[key] != null) {
              this.dataForm[key] = row[key]
            }
          }
        })
      })
    },
    handleDetail(row) {
      detailTask(row.id).then(res => {
        this.detailForm = res.data
        this.denomDialogFormVisible = true
      })
    },
    handleDelete(row) {
      this.$confirm('确定删除吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteTask(row).then(() => {
          this.$message.success('删除成功')
          const index = this.list.indexOf(row)
          this.list.splice(index, 1)
        })
      })
    },
    handleFinish(row) {
      this.dialogStatus = 'finish'
      this.dataForm.id = row.id
      this.dialogFormVisible = true
      this.dataForm.usableDenomList = []
      this.dataForm.badDenomList = []
      this.dataForm.goodDenomList = []
      this.dataForm.unclearDenomList = []
      this.dataForm.remnantDenomList = []
      this.$nextTick(() => {
        this.$refs['dataForm'].resetFields()
        this.dataForm.haveDetail = 1
        this.rules.totalAmount[0].required = this.rules.haveDetail[0].required = this.rules.taskDate[0].required = this.rules.clearType[0].required = this.rules.bankId[0].required = false
        this.dataForm.usableDenomList = []
        this.dataForm.badDenomList = []
        this.dataForm.goodDenomList = []
        this.dataForm.unclearDenomList = []
        this.dataForm.remnantDenomList = []
      })
    },
    handleConfirm(row) {
      this.$confirm('确定确认吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        confirmTask(row).then(() => {
          this.$message.success('确认成功')
          this.getList()
        })
      })
    },
    handleCancel(row) {
      this.$confirm('确定撤销吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        cancelTask(row).then(() => {
          this.$message.success('撤销成功')
          this.getList()
        })
      })
    },
    handleResult(row) {
      resultTask(row.id).then(res => {
        this.detailDialogFormVisible = true
        this.bankDenomList = res.data
      })
    },
    newData(type) {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          const { usableDenomList, badDenomList, goodDenomList, unclearDenomList, remnantDenomList, ...newForm } = this.dataForm
          newForm.departmentId = this.listQuery.departmentId
          if (newForm.haveDetail === 1) {
            newForm.totalAmount = null
            newForm.taskRecordList = [
              ...usableDenomList.map(item => { return { gbFlag: 0, count: 0, ...item } }),
              ...badDenomList.map(item => { return { gbFlag: 1, count: 0, ...item } }),
              ...goodDenomList.map(item => { return { gbFlag: 2, count: 0, ...item } }),
              ...unclearDenomList.map(item => { return { gbFlag: 3, count: 0, ...item } }),
              ...remnantDenomList.map(item => { return { gbFlag: 4, ...item } })
            ]
            if (newForm.taskRecordList.length === 0) {
              return this.$message.error('至少输入一条数据')
            }
          } else {
            newForm.taskRecordList = []
          }
          this.openLoading()
          if (type === 'update') {
            updateTask(newForm)
              .then(() => {
                this.dialogFormVisible = false
                this.$notify.success({
                  title: '成功',
                  message: '更新成功'
                })
                this.getList()
              })
              .finally(() => {
                this.loading.close()
              })
          } else if (type === 'create') {
            addTask(newForm)
              .then(() => {
                this.dialogFormVisible = false
                this.$notify.success({
                  title: '成功',
                  message: '添加成功'
                })
                this.getList()
              })
              .finally(() => {
                this.loading.close()
              })
          } else {
            const finishForm = (({ id, taskRecordList }) => ({ id, taskRecordList }))(newForm)
            finishTask(finishForm).then(() => {
              this.dialogFormVisible = false
              this.$notify.success({
                title: '成功',
                message: '任务成功完成'
              })
              this.getList()
            })
              .finally(() => {
                this.loading.close()
              })
          }
        }
      })
    },
    formatRouuteNo(num) {
      if (num) {
        return `${num}号线`
      }
      return '-'
    },
    formatStatus(statusT) {
      switch (statusT) {
        case -1:
          return '<span style="color:#d12e22">已撤销</span>'
        case 0:
          return '<span style="color:#dfe000">已创建</span>'
        case 1:
          return '<span style="color:#aa6bbb">已确认</span>'
        case 2:
          return '<span style="color:#119fff">已完成</span>'
      }
    },
    formatClearType(type) {
      return this.options.clearType.find(item => item.code === type).content
    },
    formatRouteType(type) {
      return this.options.routeType.filter(item => item.code === type)[0].content
    },
    formatDateTime(timestamp) {
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
    formatMoney,
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
    formatCheckResult(num) {
      switch (num) {
        case 0:
          return '正常'
        case 1:
          return '<span style="color: red">异常</span>'
      }
    },
    setPercent(row) {
      if (row.taskTotal > 0) {
        return Math.round((row.taskFinish / row.taskTotal) * 100)
      } else {
        return 0
      }
    },
    setStatus(row) {
      if (row.taskFinish === row.taskTotal) {
        return 'success'
      }
    },
    formatPercent(row) {
      return () => {
        return `完成：${row.taskFinish} 总计：${row.taskTotal}`
      }
    },
    async depChange() {
      await this.getBankOption()
      this.listQuery.bankId = null
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
      if (arr.find(item => item.denomId === val)) {
        return true
      }
      return false
    },
    getSummaries(param) {
      const { columns, data } = param
      const sums = []
      columns.forEach((column, index) => {
        if (index === 0) {
          sums[index] = '合计'
          return
        }
        const values = data.map((item) => Number(item[column.property]))
        if (index === 1 && !values.every((value) => isNaN(value))) {
          const total = values.reduce((prev, curr) => {
            const value = Number(curr)
            if (!isNaN(value)) {
              return prev + curr
            } else {
              return prev
            }
          }, 0)
          sums[index] = formatMoney(total)
        } else {
          sums[index] = '——'
        }
      })

      return sums
    },
    getBadSummaries(param) {
      const { columns, data } = param
      const sums = []
      columns.forEach((column, index) => {
        if (index === 0) {
          sums[index] = '合计'
          return
        }
        const values = data.map((item) => Number(item[column.property]))
        console.log(column.property)
        if (index === 1 && !values.every((value) => isNaN(value))) {
          const total = values.reduce((prev, curr) => {
            const value = Number(curr)
            if (!isNaN(value)) {
              return prev + curr
            } else {
              return prev
            }
          }, 0)
          sums[index] = formatMoney(total)
        } else if (index === 2) {
          const total = values.reduce((prev, curr) => {
            console.log(curr)
            const value = Number(curr)
            if (!isNaN(value)) {
              return prev + curr
            } else {
              return prev
            }
          }, 0)
          sums[index] = total
        } else {
          sums[index] = ''
        }
      })

      return sums
    },
    detailChange(val) {
      if (val === 0) {
        this.rules.totalAmount.required = true
      } else {
        this.rules.totalAmount.required = false
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
.my-table {
  ::v-deep .el-progress-bar__innerText {
    color: #666;
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

.el-form--inline .el-form-item {
  margin-right: 0;
  margin-bottom: 8px;
  width: 50%;
  ::v-deep .el-form-item__label {
    width: 50%;
    color: #99a9bf;
    margin-right: 24px;
  }
}
</style>
