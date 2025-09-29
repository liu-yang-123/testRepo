import Layout from '@/layout'

const departmentRouter = {
  path: '/department',
  component: Layout,
  redirect: 'noRedirect',
  name: 'Department',
  meta: {
    title: '部门管理',
    icon: 'department',
    roles: ['/base/department/tree', '/base/jobs/list']
  },
  children: [
    {
      path: 'department-manage',
      component: () => import('@/views/department/department-manage'),
      name: 'Department-manage',
      meta: {
        title: '部门管理',
        roles: ['/base/department/tree']
      }
    },
    {
      path: 'station-manage',
      component: () => import('@/views/department/station-manage'),
      name: 'StationManage',
      meta: {
        title: '岗位管理',
        roles: ['/base/jobs/list']
      }
    }
  ]
}

export default departmentRouter
