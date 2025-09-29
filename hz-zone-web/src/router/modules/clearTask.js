import Layout from '@/layout'

const clearTaskRouter = {
  path: '/clear-manage',
  component: Layout,
  redirect: 'noRedirect',
  name: 'clearManage',
  meta: {
    title: 'ATM清分',
    roles: ['/base/clearTask/list', '/base/clearTask/route', '/base/clearTask/bank'],
    icon: 'clear-task'
  },
  children: [
    {
      path: 'clear-task',
      component: () => import('@/views/clearManage/clearTask'),
      name: 'ClearTaskList',
      meta: {
        title: '清分任务',
        roles: ['/base/clearTask/list']
      }
    },
    {
      path: 'clear-route',
      component: () => import('@/views/clearManage/clearRoute'),
      name: 'ClearRouteList',
      meta: {
        title: '清分线路',
        roles: ['/base/clearTask/route']
      }
    },
    {
      path: 'clear-bank',
      component: () => import('@/views/clearManage/clearBank'),
      name: 'ClearBankList',
      meta: {
        title: '清分银行',
        roles: ['/base/clearTask/bank']
      }
    },
    {
      path: 'cashbox-record',
      component: () => import('@/views/clearManage/cashbox-record'),
      name: 'CashboxRecord',
      meta: {
        title: '装盒记录',
        roles: ['/base/cashboxPack/list']
      }
    },
    {
      path: 'task-return',
      component: () => import('@/views/clearManage/task-return'),
      name: 'TaskReturn',
      meta: {
        title: '回笼记录',
        roles: ['/base/taskReturn/list']
      }
    },
    {
      path: 'cash-collection',
      component: () => import('@/views/clearManage/cash-collection'),
      name: 'cashCollection',
      meta: {
        title: '回款明细',
        roles: ['/base/taskImportRecord/clearList']
      }
    }
  ]
}

export default clearTaskRouter
