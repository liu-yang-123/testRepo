import Layout from '@/layout'

const vaultRouter = {
  path: '/vault',
  component: Layout,
  redirect: 'noRedirect',
  name: 'storage',
  meta: {
    title: '金库管理',
    icon: 'vault',
    roles: ['/base/vaultOrder/list', '/base/vaultVolume/list', '/base/vaultCheck/list']
  },
  children: [
    {
      path: 'in-out',
      component: () => import('@/views/vault/in-out'),
      name: 'in-out',
      meta: {
        title: '出入库管理',
        roles: ['/base/vaultOrder/list']
      }
    },
    {
      path: 'volume',
      component: () => import('@/views/vault/volume'),
      name: 'volume',
      meta: {
        title: '库存查询',
        roles: ['/base/vaultVolume/list']
      }
    },
    {
      path: 'check',
      component: () => import('@/views/vault/check'),
      name: 'Check',
      meta: {
        title: '盘点记录',
        roles: ['/base/vaultCheck/list']
      }
    }
  ]
}

export default vaultRouter
