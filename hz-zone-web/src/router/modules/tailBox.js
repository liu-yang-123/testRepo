import Layout from '@/layout'

const cleanRouter = {
  path: '/tail-box',
  component: Layout,
  redirect: 'noRedirect',
  name: 'tailBox',
  meta: {
    title: '尾箱业务',
    icon: 'tailBox',
    roles: ['/base/bankBox/tree', '/base/boxpack/list', '/base/bankTeller/list', '/base/boxpackTask/list']
  },
  children: [
    {
      path: 'organization',
      component: () => import('@/views/tailBox/organization'),
      name: 'Organization',
      meta: {
        title: '机构管理',
        roles: ['/base/bankBox/tree']
      }
    },
    {
      path: 'employee',
      component: () => import('@/views/tailBox/employee'),
      name: 'Employee',
      meta: {
        title: '员工管理',
        roles: ['/base/bankTeller/list']
      }
    },
    {
      path: 'bags',
      component: () => import('@/views/tailBox/bags'),
      name: 'Bags',
      meta: {
        title: '箱包管理',
        roles: ['/base/boxpack/list']
      }
    },
    {
      path: 'task',
      component: () => import('@/views/tailBox/task'),
      name: 'TailTask',
      meta: {
        title: '任务管理',
        roles: ['/base/boxpackTask/list']
      }
    }
  ]
}

export default cleanRouter
