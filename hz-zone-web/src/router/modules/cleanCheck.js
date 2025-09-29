import Layout from '@/layout'

const cleanRouter = {
  path: '/clean-check',
  component: Layout,
  redirect: 'noRedirect',
  name: 'tailBox',
  meta: {
    title: '商业清分',
    icon: 'tailBox',
    roles: ['/base/trade/clearTask/list', '/base/clearError/list', '/base/clear/charge/rule/list', '/base/clearReport/calcFee']
  },
  children: [
    {
      path: 'task',
      component: () => import('@/views/cleanCheck/task'),
      name: 'ClearTask',
      meta: {
        title: '清分任务',
        roles: ['/base/trade/clearTask/list']
      }
    },
    {
      path: 'clear-error',
      component: () => import('@/views/cleanCheck/clearError'),
      name: 'ClearError',
      meta: {
        title: '清分差错',
        roles: ['/base/clearError/list']
      }
    },
    {
      path: 'settle-accounts',
      component: () => import('@/views/cleanCheck/settle-accounts'),
      name: 'SettleAccounts',
      meta: {
        title: '结算标准',
        roles: ['/base/clear/charge/rule/list']
      }
    },
    {
      path: 'report',
      component: () => import('@/views/cleanCheck/clear-report'),
      name: 'ClearReport',
      meta: {
        title: '费用核算',
        roles: ['/base/clearReport/calcFee']
      }
    }
  ]
}

export default cleanRouter
