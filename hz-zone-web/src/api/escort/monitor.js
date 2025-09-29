import request from '@/utils/request'

export function listMonitor(params) {
  return request({
    url: '/base/route/monitor',
    method: 'get',
    params
  })
}

export function atmTaskRoute(id) {
  return request({
    url: `/base/route/atmTask/${id}`,
    method: 'get'
  })
}
