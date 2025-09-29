<template>
  <div class="app-container">
    <search-bar :options="options" :list-query="listQuery" :search-list="searchList" :role="role" @lookUp="getList" />
    <my-table
      :list-loading="listLoading"
      :data-list="list"
      :table-list="tableList"
    >
      <template v-slot:operate>
        <el-table-column
          align="center"
          label="是否具备车长资格"
          class-name="small-padding fixed-width"
        >
          <template slot-scope="scope">
            <el-switch
              v-model="scope.row.routeLeader"
              v-permission="['/base/schdConductor/update']"
              active-color="#13ce66"
              inactive-color="#ff4949"
              :active-value="1"
              :inactive-value="0"
              @change="handleUpdate($event,scope.row)"
            />
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

  </div>
</template>

<script>
import Pagination from '@/components/Pagination'
import myTable from '@/components/Table'
import searchBar from '@/components/SearchBar'
import { listConductor, updateConductor } from '@/api/duty/conductorManage'
import { authOption } from '@/api/common/selectOption'

export default {
  components: { Pagination, myTable, searchBar },
  data() {
    return {
      list: [],
      listQuery: {
        limit: 10,
        page: 1,
        departmentId: null,
        empName: null,
        leader: null
      },
      departmentId: null,
      listLoading: true,
      total: 0,
      searchList: [
        { name: 'departmentId', label: '所属部门', type: 3, notClear: true },
        { name: 'empName', label: '姓名' },
        { name: 'leader', label: '车长资格', type: 3 }
      ],
      options: {
        departmentId: [],
        driver: [],
        routeId: [],
        vehicleNo: [],
        leader: [
          { code: 0, content: '不具备' },
          { code: 1, content: '具备' }
        ]
      },
      role: {
        list: '/base/schdConductor/list'
      },
      tableList: [
        {
          label: '业务员',
          prop: 'empName',
          formatter: this.formatEmp
        },
        {
          label: '岗位',
          prop: 'jobName'
        }
      ],
      dialogFormVisible: false,
      dialogStatus: '',
      textMap: {
        update: '编辑',
        create: '创建'
      },
      dataForm: {
        assignType: null,
        departmentId: null,
        id: null,
        vehicleNo: null,
        routeId: null,
        driver: null,
        driverType: null
      },
      empOptions: []
    }
  },
  mounted() {
    this.getOptions().then(() => {
      this.getList()
    })
  },
  methods: {
    getList(data) {
      console.log(data)
      if (data) {
        for (const key in data) {
          this.listQuery[key] = data[key]
        }
      }
      console.log(this.listQuery)
      listConductor(this.listQuery).then((res) => {
        const data = res.data
        this.list = data.list
        this.departmentId = this.listQuery.departmentId
        this.total = data.total
        this.listLoading = false
      })
    },
    getOptions() {
      return new Promise((reslove, reject) => {
        Promise.all([
          authOption()
        ]).then(res => {
          const [res1] = res
          this.options.departmentId = res1.data
          this.listQuery.departmentId = this.options.departmentId[0].id
          reslove()
        }).catch(err => {
          reject(err)
        })
      })
    },
    handleUpdate(val, row) {
      const params = {
        routeLeader: val,
        id: row.id
      }
      updateConductor(params).then(() => {
        this.$notify.success({
          title: '成功',
          message: '资格更改成功'
        })
      }).catch(() => {
        this.$notify.error({
          title: '失败',
          message: '资格更改失败'
        })
        if (val === 1) {
          row.routeLeader = 0
        } else {
          row.routeLeader = 1
        }
      })
    },
    formatEmp(name, row) {
      return `${row.empNo}/${name}`
    }
  }
}
</script>

<style scoped lang="scss">
</style>
