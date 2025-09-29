import request from '@/utils/request'

export function listMonitor(params) {
  return request({
    url: '/base/bankInquiry/routeMonitor',
    method: 'get',
    params
  })
}

export function atmTaskRoute(id) {
  return request({
    url: `/base/bankInquiry/atmTask/${id}`,
    method: 'get'
  })
}
