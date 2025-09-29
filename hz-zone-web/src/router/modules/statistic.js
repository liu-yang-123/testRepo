import Layout from '@/layout'

const cleanRouter = {
  path: '/statistic',
  component: Layout,
  redirect: 'noRedirect',
  name: 'statistic',
  meta: {
    title: '统计报表',
    icon: 'statistic',
    roles: ['/base/bankReport/task', '/base/bankReport/amount', '/base/cleanReport/amount', '/base/cleanReport/workload', '/base/bankReport/stock', '/base/bankReport/workload', '/base/bankReport/receivePayment', '/base/bankReport/device']
  },
  children: [
    {
      path: 'task',
      component: () => import('@/views/statistic/task'),
      name: 'StaTask',
      meta: {
        title: '各银行加钞数量月度统计',
        roles: ['/base/bankReport/task']
      }
    },
    {
      path: 'amount',
      component: () => import('@/views/statistic/amount'),
      name: 'Amount',
      meta: {
        title: '各银行加款金额月度统计',
        roles: ['/base/bankReport/amount']
      }
    },
    {
      path: 'clean-amount',
      component: () => import('@/views/statistic/clean-amount'),
      name: 'CleanAmount',
      meta: {
        title: '各银行清分月度统计',
        roles: ['/base/cleanReport/amount']
      }
    },
    {
      path: 'clean-workload',
      component: () => import('@/views/statistic/clean-workload'),
      name: 'CleanWorkload',
      meta: {
        title: '清点员工作量月度统计',
        roles: ['/base/cleanReport/workload']
      }
    },
    {
      path: 'stock',
      component: () => import('@/views/statistic/stock'),
      name: 'Stock',
      meta: {
        title: '各银行库存月度统计',
        roles: ['/base/bankReport/stock']
      }
    },
    {
      path: 'workload',
      component: () => import('@/views/statistic/workload'),
      name: 'Workload',
      meta: {
        title: '业务员加钞清机任务统计',
        roles: ['/base/bankReport/workload']
      }
    },
    {
      path: 'receivePayment',
      component: () => import('@/views/statistic/receive-payment'),
      name: 'receivePayment',
      meta: {
        title: '银行领缴款月度统计',
        roles: ['/base/bankReport/receivePayment']
      }
    },
    {
      path: 'device',
      component: () => import('@/views/statistic/device'),
      name: 'StaDevice',
      meta: {
        title: '银行设备加钞统计',
        roles: ['/base/bankReport/device']
      }
    },
    {
      path: 'card',
      component: () => import('@/views/statistic/card'),
      name: 'Card',
      meta: {
        title: '银行吞没卡统计',
        roles: ['/base/bankReport/card']
      }
    },
    {
      path: 'route-workload',
      component: () => import('@/views/statistic/route-workload'),
      name: 'RouteWorkload',
      meta: {
        title: '线路出车工作时间统计',
        roles: ['/base/routeReport/workload']
      }
    }

  ]
}

export default cleanRouter
