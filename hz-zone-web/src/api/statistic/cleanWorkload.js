import request from '@/utils/request'

export function listWorkload(params) {
  return request({
    url: '/base/cleanReport/workload',
    method: 'get',
    params
  })
}
