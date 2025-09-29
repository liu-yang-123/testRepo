import Layout from '@/layout'

const cleanRouter = {
  path: '/bank-info',
  component: Layout,
  redirect: 'noRedirect',
  name: 'bankInfo',
  meta: {
    title: '银行业务查询',
    icon: 'info',
    roles: ['/base/bankInquiry/routeList', '/base/bankInquiry/routeMonitor', '/base/bankInquiry/bankClearTree', '/base/bankInquiry/atmList', '/base/bankInquiry/atmTaskList', '/base/bankInquiry/taskCardList', '/base/bankInquiry/clearTaskList', '/base/bankInquiry/vaultOrderList', '/base/bankInquiry/vaultVolumeList']
  },
  children: [
    {
      path: 'route',
      component: () => import('@/views/bankInfo/route'),
      name: 'BankRoute',
      meta: {
        title: '线路查询',
        roles: ['/base/bankInquiry/routeList']
      }
    },
    {
      path: 'monitor',
      component: () => import('@/views/bankInfo/monitor'),
      name: 'BankMonitor',
      meta: {
        title: '线路监控',
        roles: ['/base/bankInquiry/routeMonitor']
      }
    },
    {
      path: 'clean-machine',
      component: () => import('@/views/bankInfo/clean-machine'),
      name: 'BankCleanMachine',
      meta: {
        title: '加钞网点',
        roles: ['/base/bankInquiry/bankClearTree']
      }
    },
    {
      path: 'atm',
      component: () => import('@/views/bankInfo/atm'),
      name: 'BankATM',
      meta: {
        title: '设备管理',
        roles: ['/base/bankInquiry/atmList']
      }
    },
    {
      path: 'atm-task',
      component: () => import('@/views/bankInfo/atm-task'),
      name: 'BankATMTask',
      meta: {
        title: '加钞任务',
        roles: ['/base/bankInquiry/atmTaskList']
      }
    },
    {
      path: 'task-record',
      component: () => import('@/views/bankInfo/task-record'),
      name: 'TaskRecord',
      meta: {
        title: 'ATM任务记录',
        roles: ['/base/bankInquiry/atmTask/list']
      }
    },
    {
      path: 'gulp-record',
      component: () => import('@/views/bankInfo/gulp-record'),
      name: 'GulpRecord',
      meta: {
        title: '吞没卡记录',
        roles: ['/base/bankInquiry/taskCardList']
      }
    },
    {
      path: 'inspect',
      component: () => import('@/views/bankInfo/inspect'),
      name: 'Inspect',
      meta: {
        title: '网点巡检记录',
        roles: ['/base/bankInquiry/bankCheckList']
      }
    },
    {
      path: 'clear-task',
      component: () => import('@/views/bankInfo/clearTask'),
      name: 'InfoClearTask',
      meta: {
        title: '清分任务',
        roles: ['/base/bankInquiry/clearTaskList']
      }
    },
    {
      path: 'in-out',
      component: () => import('@/views/bankInfo/in-out'),
      name: 'InOut',
      meta: {
        title: '出入库记录',
        roles: ['/base/bankInquiry/vaultOrderList']
      }
    },
    {
      path: 'volume',
      component: () => import('@/views/bankInfo/volume'),
      name: 'Volume',
      meta: {
        title: '库存查询',
        roles: ['/base/bankInquiry/vaultVolumeList']
      }
    }

  ]
}

export default cleanRouter
