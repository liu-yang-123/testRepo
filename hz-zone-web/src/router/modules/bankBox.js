import Layout from '@/layout'

const bankBoxRouter = {
  path: '/bank-box',
  component: Layout,
  // redirect: '/bankBox/',
  // name: 'BankBox',
  meta: {
    icon: 'box',
    roles: ['/base/cashbox/list']
  },
  children: [
    // {
    //   path: 'bank-case',
    //   component: { render: (e) => e('router-view') },
    //   name: 'bankCase',
    //   meta: {
    //     title: '钞箱信息'
    //   },
    //   redirect: 'noredirect',
    //   children: [
    //     {
    //       path: 'bank-bag',
    //       component: () => import('@/views/bankEquip/bank-bag'),
    //       name: 'BankBag',
    //       meta: {
    //         perms: ['/base/subjects/list'],
    //         title: '钞袋管理',
    //         noCache: true
    //       }
    //     },
    //     {
    //       path: 'bank-box',
    //       component: () => import('@/views/bankEquip/bank-box'),
    //       name: 'BankBox',
    //       meta: {
    //         perms: ['/base/records/list'],
    //         title: '钞盒管理',
    //         noCache: true
    //       }
    //     }
    //   ]
    // },
    {
      path: 'index',
      component: () => import('@/views/bankBox'),
      name: 'BankBox',
      meta: {
        title: '钞盒管理'
      }
    }
  ]
}

export default bankBoxRouter
