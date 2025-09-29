import Layout from '@/layout'

const employeeRouter = {
  path: '/employee',
  component: Layout,
  redirect: 'noRedirect',
  name: 'employee',
  meta: {
    title: '员工管理',
    icon: 'employee',
    roles: ['/base/employee/list', '/base/subjects/list', '/base/records/list', '/base/awards/list', '/base/employee/list']
  },
  children: [
    {
      path: 'employee-manage',
      component: () => import('@/views/employee/employee-manage'),
      name: 'employeeManage',
      meta: {
        title: '员工管理',
        roles: ['/base/employee/list']
      }
    },
    {
      path: 'employee-train',
      component: { render: (e) => e('router-view') },
      name: 'EmployeeTrain',
      meta: {
        title: '员工培训',
        roles: ['/base/subjects/list']
      },
      redirect: 'noredirect',
      children: [
        {
          path: 'training-subject',
          component: () => import('@/views/employee/training-subject'),
          name: 'TrainingSubject',
          meta: {
            roles: ['/base/subjects/list', '/base/records/list'],
            title: '培训主题',
            noCache: true
          }
        },
        {
          path: 'training-record',
          component: () => import('@/views/employee/training-record'),
          name: 'TrainingRecord',
          meta: {
            roles: ['/base/records/list'],
            title: '培训成绩',
            noCache: true
          }
        }
      ]
    },
    {
      path: 'reward-manage',
      component: () => import('@/views/employee/reward-manage'),
      name: 'RewardManage',
      meta: {
        title: '奖惩管理',
        roles: ['/base/awards/list']
      }
    },
    // {
    //   path: 'visit-manage',
    //   component: () => import('@/views/employee/visit-manage'),
    //   name: 'VisitManage',
    //   meta: {
    //     title: '家访管理',
    //     roles: ['/base/employee/list']
    //   }
    // },
    {
      path: 'fingerprint-manage',
      component: () => import('@/views/employee/fingerprint-manage'),
      name: 'FingerprintManage',
      meta: {
        title: '指纹管理',
        roles: ['/base/employee/list']
      }
    }
  ]
}

export default employeeRouter
