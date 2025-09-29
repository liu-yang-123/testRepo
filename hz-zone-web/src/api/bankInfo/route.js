import request from '@/utils/request'

export function listRoute(params) {
  return request({
    url: '/base/bankInquiry/routeList',
    method: 'get',
    params
  })
}

export function routeEmpChange(id) {
  return request({
    url: `/base/bankInquiry/routeEmpChange/${id}`,
    method: 'get'
  })
}
