import Layout from '@/layout'

const escortRouter = {
  path: '/escort',
  component: Layout,
  redirect: 'noRedirect',
  name: 'escort',
  meta: {
    title: '押运管理',
    icon: 'escort',
    roles: ['/base/routeTemplate/list', '/base/route/list', '/base/route/monitor']
  },
  children: [
    {
      path: 'template',
      component: () => import('@/views/escort/template'),
      name: 'Template',
      meta: {
        title: '线路模板',
        roles: ['/base/routeTemplate/list'] // or you can only set roles in sub nav
      }
    },
    {
      path: 'route',
      component: () => import('@/views/escort/route'),
      name: 'EscortRoute',
      meta: {
        title: '线路管理',
        roles: ['/base/route/list'] // or you can only set roles in sub nav
      }
    },
    {
      path: 'monitor',
      component: () => import('@/views/escort/monitor'),
      name: 'EscortMonitor',
      meta: {
        title: '线路监控',
        roles: ['/base/route/monitor'] // or you can only set roles in sub nav
      }
    }
  ]
}

export default escortRouter
