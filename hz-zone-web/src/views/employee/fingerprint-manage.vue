<template>
  <div class="app-container">
    <search-bar :search-list="searchList" :options="options" :list-query="listQuery" :role="role" @lookUp="getList" />
    <my-table
      :list-loading="listLoading"
      :data-list="list"
      :table-list="tableList"
    />
    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="listQuery.page"
      :limit.sync="listQuery.limit"
      @pagination="getList"
    />
  </div>
</template>

<script>
import Pagination from '@/components/Pagination'
import myTable from '@/components/Table'
import searchBar from '@/components/SearchBar'
import { listFingerprint } from '@/api/employee/fingerprintManage'
import { jobTree } from '@/api/common/selectOption'
import { formatdate } from '@/utils/date'

export default {
  components: { Pagination, myTable, searchBar },
  data() {
    return {
      list: [],
      listQuery: {
        limit: 10,
        page: 1,
        empName: null,
        empNo: null,
        userType: null,
        jobId: null
      },
      options: {
        jobId: []
      },
      listLoading: true,
      total: 0,
      searchList: [
        { name: 'userType', label: '人员类型', type: 1, dictionary: 'PERSONNEL_TYPE' },
        { name: 'jobId', label: '岗位名称', type: 3 },
        { name: 'empNo', label: '员工工号' },
        { name: 'empName', label: '员工名称' }
      ],
      role: {
        list: '/base/fingerprint/list'
      },
      tableList: [
        {
          label: '人员类型',
          prop: 'userType',
          dictionary: 'PERSONNEL_TYPE'
        },
        {
          label: '岗位名称',
          prop: 'jobName'
        },
        {
          label: '员工工号',
          prop: 'empNo'
        },
        {
          label: '员工名称',
          prop: 'empName'
        },
        {
          label: '手指序号',
          prop: 'fingerIdx',
          formatter: this.formatIdx
        }
      ]
    }
  },
  mounted() {
    this.getOptions().then(() => {
      this.getList()
    })
  },
  methods: {
    getOptions() {
      return new Promise((resolve, reject) => {
        jobTree().then(res => {
          this.options.jobId = res.data
          resolve()
        }).catch(err => {
          reject(err)
        })
      })
    },
    getList(data) {
      if (data) {
        for (const key in data) {
          this.listQuery[key] = data[key]
        }
      }
      listFingerprint(this.listQuery).then((res) => {
        const data = res.data
        this.list = data.list
        console.log(this.list)
        this.total = data.total
        this.listLoading = false
      })
    },
    formatDateTime(timestamp) {
      if (!timestamp || timestamp === 0) {
        return '-'
      }
      const date = new Date(timestamp)
      return formatdate(date, 'yyyy-MM-dd hh:mm:ss')
    },
    formatIdx(idx) {
      switch (idx) {
        case 0:
          return '左手小指'
        case 1:
          return '左手无名指'
        case 2:
          return '左手中指'
        case 3:
          return '左手食指'
        case 4:
          return '左手大拇指'
        case 5:
          return '右手大拇指'
        case 6:
          return '右手食指'
        case 7:
          return '右手中指'
        case 8:
          return '右手无名指'
        case 9:
          return '右手小指'
      }
    }
  }
}
</script>

<style scoped lang="scss">
</style>
