import Layout from '@/layout'

const dutyRouter = {
  path: '/duty',
  component: Layout,
  redirect: '/vault/volume',
  name: 'duty',
  meta: {
    title: '排班管理',
    roles: ['/base/alternate/guard/list', '/base/vacation/guard/adjust/list', '/base/vacation/guard/setting/list', '/base/vacation/guard/plan/list', '/base/alternate/list', '/base/vacation/adjust/list', '/base/vacation/setting/list', '/base/vacation/plan/list', '/base/passAuth/list', '/base/driving/restrict/list', '/base/holiday/list', '/base/driver/list', '/base/schdConductor/list', '/base/schdResult/list'],
    icon: 'duty'
  },
  children: [
    {
      path: 'rest-manage',
      component: () => import('@/views/duty/rest-manage'),
      name: 'restManage',
      meta: {
        title: '清机计划管理',
        roles: ['/base/alternate/list', '/base/vacation/adjust/list', '/base/vacation/setting/list', '/base/vacation/plan/list']
      }
    },
    {
      path: 'guard-manage',
      component: () => import('@/views/duty/rest-manage'),
      name: 'guardManage',
      meta: {
        title: '护卫计划管理',
        roles: ['/base/alternate/guard/list', '/base/vacation/guard/adjust/list', '/base/vacation/guard/setting/list', '/base/vacation/guard/plan/list']
      }
    },
    {
      path: 'record-manage',
      component: () => import('@/views/duty/record-manage'),
      name: 'recordManage',
      meta: {
        title: '员工备案管理',
        roles: ['/base/passAuth/list']
      }
    },
    {
      path: 'restrict-manage',
      component: () => import('@/views/duty/restrict-manage'),
      name: 'restrictManage',
      meta: {
        title: '车辆限行管理',
        roles: ['/base/driving/restrict/list']
      }
    },
    {
      path: 'leave-manage',
      component: () => import('@/views/duty/leave-manage'),
      name: 'leaveManage',
      meta: {
        title: '放假及调休管理',
        roles: ['/base/holiday/list']
      }
    },
    {
      path: 'conductor-manage',
      component: () => import('@/views/duty/conductor-manage'),
      name: 'conductorManage',
      meta: {
        title: '车长资格管理',
        roles: ['/base/schdConductor/list']
      }
    },
    {
      path: 'schedul-result',
      component: () => import('@/views/duty/schedul-result'),
      name: 'schedulResult',
      meta: {
        title: '线路排班结果',
        roles: ['/base/schdResult/list']
      }
    }
  ]
}

export default dutyRouter
