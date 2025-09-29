import request from '@/utils/request'

export function listWorkload(params) {
  return request({
    url: '/base/routeReport/workload',
    method: 'get',
    params
  })
}
