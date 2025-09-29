<template>
  <div class="app-container">
    <search-bar :search-list="searchList" :list-query="listQuery" :options="options" :role="role" @lookUp="getList" />
    <!-- 查询结果 -->
    <el-table
      v-loading="listLoading"
      element-loading-text="正在查询中。。。"
      :data="list"
      :header-cell-style="{'background-color':'#f5f5f5'}"
      border
      fit
    >
      <el-table-column align="center" prop="mtDate" label="维保日期">
        <template slot-scope="scope">
          <span>{{ scope.row.mtDate | formatDateTime }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" prop="deviceNo" label="设备编号" />
      <el-table-column align="center" prop="mtType" label="维保类型">
        <template slot-scope="scope">
          <span>{{ scope.row.mtType | formatType(options['mtType']) }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" prop="mtReason" label="故障原因" />
      <el-table-column align="center" prop="mtContent" label="维保内容" />
      <el-table-column align="center" prop="mtCost" label="维保成本" />
      <el-table-column align="center" prop="mtEngineer" label="维保工程师" />
      <el-table-column align="center" prop="mtResult" label="维保结果">
        <template slot-scope="scope">
          <span>{{ scope.row.mtResult | formatType(options['mtResult']) }}</span>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />
  </div>
</template>

<script>
import searchBar from '@/components/SearchBar'
import Pagination from '@/components/Pagination'
import { listMaintain } from '@/api/device/maintain'
import { reqDictionary } from '@/api/system/dictionary'
import { formatdate } from '@/utils/date'
import { authOption } from '@/api/common/selectOption'
export default {
  name: 'DeviceMaintain',
  components: { Pagination, searchBar },
  filters: {
    formatType(type, typeOption) {
      const [obj] = typeOption.filter(item => item.code === type)
      return obj.content || ''
    },
    formatDateTime(timestamp) {
      if (!timestamp || timestamp === 0) {
        return '-'
      }
      const date = new Date(timestamp)
      return formatdate(date, 'yyyy-MM-dd')
    }
  },
  data() {
    return {
      list: null,
      total: 0,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 10,
        deviceNo: null,
        mtEngineer: null,
        mtType: null,
        mtResult: null,
        departmentId: null
      },
      searchList: [
        { name: 'departmentId', label: '所在部门', type: 3, notClear: true },
        { name: 'deviceNo', label: '设备编号' },
        { name: 'mtEngineer', label: '维保工程师' },
        { name: 'mtType', label: '维保类型', type: 3 },
        { name: 'mtResult', label: '维保结果', type: 3 }
      ],
      options: {
        mtResult: [
          { code: 0, content: '维修失败' },
          { code: 1, content: '维修成功' }
        ],
        mtType: [],
        departmentId: []
      },
      role: {
        list: '/base/deviceMaintain/list'
      }
    }
  },
  created() {
    reqDictionary('DEV_MAINTAIN_TYPE').then((res) => {
      this.options['mtType'] = res.data
    })
    this.getOptions()
  },
  methods: {
    getList(data) {
      this.listLoading = true
      if (data) {
        for (const key in data) {
          this.listQuery[key] = data[key]
        }
      }
      listMaintain(this.listQuery).then(res => {
        this.list = res.data.list
        this.total = res.data.total
        this.listLoading = false
      }).catch(() => {
        this.list = []
        this.total = 0
        this.listLoading = false
      })
    },
    getOptions() {
      authOption().then(res => {
        const t = []
        for (let k = 0; k < res.data.length; k++) {
          t.push({ code: res.data[k].id, content: res.data[k].name })
        }
        this.options['departmentId'] = t
        if (this.options['departmentId'].length > 0) {
          this.listQuery.departmentId = res.data[0].id
          this.getList()
        }
      })
    },
    handleFilter() {
      this.listQuery.page = 1
      this.getList()
    }
  }
}
</script>

<style lang="scss" scoped>
  .filter-container > button{
    margin-left: 10px;
  }
</style>
