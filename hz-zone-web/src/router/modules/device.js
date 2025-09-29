/** When your routing table is too long, you can split it into small modules **/

import Layout from '@/layout'

const nestedRouter = {
  path: '/device',
  component: Layout,
  redirect: 'noRedirect',
  name: 'Device',
  meta: {
    title: '设备管理',
    icon: 'nested',
    roles: ['/base/device/list', '/base/deviceMaintain/list', '/base/deviceFactory/list', '/base/deviceModel/list']
  },
  children: [
    {
      path: 'info',
      component: () => import('@/views/device/info'),
      name: 'Device-Info',
      meta: { title: '设备信息', roles: ['/base/device/list'] }
    },
    {
      path: 'maintain',
      component: () => import('@/views/device/maintain'),
      name: 'Device-Maintain',
      meta: { title: '维修管理', roles: ['/base/deviceMaintain/list'] }
    },
    {
      path: 'brand',
      component: () => import('@/views/device/brand'),
      name: 'Device-Brand',
      meta: { title: '品牌管理', roles: ['/base/deviceFactory/list'] }
    },
    {
      path: 'model',
      component: () => import('@/views/device/model'),
      name: 'Device-Model',
      meta: { title: '型号管理', roles: ['/base/deviceModel/list'] }
    }
  ]
}

export default nestedRouter
