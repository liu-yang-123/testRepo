import Layout from '@/layout'

const vehicleRouter = {
  path: '/vehicle',
  component: Layout,
  redirect: 'noRedirect',
  name: 'vehicle',
  meta: {
    title: '车辆管理',
    icon: 'vehicle',
    roles: ['/base/vehicle/list', '/base/vehicleMaintain/list']
  },
  children: [
    {
      path: 'vehicle-information',
      component: () => import('@/views/vehicle/vehicle-information'),
      name: 'VehicleInformation',
      meta: {
        title: '车辆信息',
        roles: ['/base/vehicle/list']
      }
    },
    {
      path: 'maintenance',
      component: () => import('@/views/vehicle/maintenance'),
      name: 'Maintenance',
      meta: {
        title: '维修保养',
        roles: ['/base/vehicleMaintain/list']
      }
    }
  ]
}

export default vehicleRouter
