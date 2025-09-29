import Layout from '@/layout'

const baseRouter = {
  path: '/base',
  component: Layout,
  redirect: 'noRedirect',
  name: 'base',
  meta: {
    title: '基础数据',
    icon: 'base',
    roles: ['/base/bankBox/tree', '/base/denom/list', '/base/district/list']
  },
  alwaysShow: true,
  children: [
    // {
    //   path: 'tail-box',
    //   component: () => import('@/views/base/tail-box'),
    //   name: 'TailBox',
    //   meta: {
    //     title: '尾箱机构',
    //     roles: ['/base/bankBox/tree']
    //   }
    // },
    {
      path: 'denomination',
      component: () => import('@/views/base/denomination'),
      name: 'Denomination',
      meta: {
        title: '券别管理',
        roles: ['/base/denom/list']
      }
    },
    {
      path: 'organization',
      component: () => import('@/views/cleanCheck/organization'),
      name: 'CleanOrganization',
      meta: {
        title: '机构管理',
        roles: ['/base/bankTrade/tree']
      }
    },
    {
      path: 'district',
      component: () => import('@/views/base/district'),
      name: 'District',
      meta: {
        title: '区域管理',
        roles: ['/base/district/list']
      }
    }
  ]
}

export default baseRouter
