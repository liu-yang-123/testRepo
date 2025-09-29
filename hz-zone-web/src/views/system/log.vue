<template>
  <div class="app-container">
    <search-bar :search-list="searchList" :list-query="listQuery" :options="options" :role="role" @lookUp="getList" />
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
import { listLog } from '@/api/system/log'
import { formatdate } from '@/utils/date'
export default {
  components: { Pagination, myTable, searchBar },
  data() {
    return {
      list: [],
      listQuery: {
        limit: 10,
        page: 1,
        userName: null,
        result: null,
        type: null
      },
      listLoading: true,
      total: 0,
      searchList: [
        { name: 'userName', label: '管理员名称' },
        { name: 'result', label: '操作结果', type: 3 },
        { name: 'type', label: '类型', type: 3 }
      ],
      options: {
        result: [
          { code: 'success', content: '成功' },
          { code: 'failed', content: '失败' }
        ],
        type: [
          { code: '0', content: '添加' },
          { code: '1', content: '修改' },
          { code: '2', content: '删除' }
        ]
      },
      role: {
        list: '/base/log/list'
      },
      tableList: [
        {
          label: '操作管理员',
          prop: 'nickName'
        },
        // {
        //   label: '行为',
        //   prop: 'action'
        // },
        {
          label: 'ip地址',
          prop: 'ip'
        },
        {
          label: '操作时间',
          prop: 'createTime',
          formatter: this.formatDateTime
        },
        {
          label: '操作内容',
          prop: 'content'
        },
        {
          label: '操作结果',
          prop: 'result',
          formatter: this.formatResult
        },
        {
          label: '类型',
          prop: 'typeT',
          formatter: this.formatType
        }
      ]
    }
  },
  mounted() {
    this.getList()
  },
  methods: {
    getList() {
      console.log(this.listQuery)
      listLog(this.listQuery).then((res) => {
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
    formatResult(result) {
      if (result === 'success') {
        return '成功'
      } else {
        return '失败'
      }
    },
    formatType(type) {
      switch (type) {
        case 0:
          return '添加'
        case 1:
          return '修改'
        case 2:
          return '删除'
      }
    }
  }
}
</script>

<style scoped lang="scss">
</style>
