import Layout from '@/layout'

const cleanRouter = {
  path: '/clean',
  component: Layout,
  redirect: 'noRedirect',
  name: 'clean',
  meta: {
    title: '加钞管理',
    icon: 'clean',
    roles: ['/base/atm/list', '/base/atmTask/list']
  },
  children: [
    {
      path: 'clean-machine',
      component: () => import('@/views/base/clean-machine'),
      name: 'CleanMachine',
      meta: {
        title: '加钞网点',
        roles: ['/base/bankClear/tree']
      }
    },
    {
      path: 'atm',
      component: () => import('@/views/clean/atm'),
      name: 'ATM',
      meta: {
        title: 'ATM设备',
        roles: ['/base/atm/list'], // or you can only set roles in sub nav
        noCache: true
      }
    },
    {
      path: 'atm-task',
      component: () => import('@/views/clean/atm-task'),
      name: 'ATMTask',
      meta: {
        title: 'ATM任务',
        roles: ['/base/atmTask/list'] // or you can only set roles in sub nav
      }
    },
    {
      path: 'task-record',
      component: () => import('@/views/clean/task-record'),
      name: 'taskRecord',
      meta: {
        title: '任务记录',
        roles: ['/base/atmTask/record'] // or you can only set roles in sub nav
      }
    },
    {
      path: 'gulp-record',
      component: () => import('@/views/clean/gulp-record'),
      name: 'gulpRecord',
      meta: {
        title: '吞卡记录',
        roles: ['/base/atmTaskCard/list'] // or you can only set roles in sub nav
      }
    },
    {
      path: 'inspect-record',
      component: () => import('@/views/clean/inspect'),
      name: 'inspectRecord',
      meta: {
        title: '网点巡检记录',
        roles: ['/base/bankCheck/list/route'] // or you can only set roles in sub nav
      }
    },
    {
      path: 'import-record',
      component: () => import('@/views/clean/import-record'),
      name: 'importRecord',
      meta: {
        title: '计划单',
        roles: ['/base/taskImportRecord/taskList'] // or you can only set roles in sub nav
      }
    },
    {
      path: 'spare-manage',
      component: () => import('@/views/clean/spare-manage'),
      name: 'spareManage',
      meta: {
        title: '备用金管理',
        roles: ['/base/addition/cash/list'] // or you can only set roles in sub nav
      }
    },
    {
      path: 'handoverLog',
      component: () => import('@/views/clean/handoverLog'),
      name: 'handoverLog',
      meta: {
        title: '交班日志',
        roles: ['/base/handoverLog/list']
      }
    }
  ]
}

export default cleanRouter
