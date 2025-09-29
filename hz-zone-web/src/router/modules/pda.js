import Layout from '@/layout'

const vehicleRouter = {
  path: '/pda',
  component: Layout,
  redirect: 'noRedirect',
  name: 'pda',
  meta: {
    title: 'PDA管理',
    icon: 'pda',
    roles: ['/base/pda/list', '/base/pdaMenu/list', '/base/pdaRole/list', '/base/pdaUser/list']
  },
  children: [
    {
      path: 'terminal',
      component: () => import('@/views/pda/terminal'),
      name: 'Terminal',
      meta: {
        title: '终端管理',
        roles: ['/base/pda/list'] // or you can only set roles in sub nav
      }
    },
    // {
    //   path: 'menu',
    //   component: () => import('@/views/pda/menu'),
    //   name: 'PagePermission',
    //   meta: {
    //     title: '菜单管理',
    //     roles: ['/base/pdaMenu/list'] // or you can only set roles in sub nav
    //   }
    // },
    // {
    //   path: 'role',
    //   component: () => import('@/views/pda/role'),
    //   name: 'DirectivePermission',
    //   meta: {
    //     title: '角色管理',
    //     roles: ['/base/pdaRole/list']
    //   }
    // },
    {
      path: 'user',
      component: () => import('@/views/pda/user'),
      name: 'PdaUserPermission',
      meta: {
        title: '用户管理',
        roles: ['/base/pdaUser/list']
      }
    }
  ]
}

export default vehicleRouter
