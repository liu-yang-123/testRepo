import request from '@/utils/request'

export function listWorkload(params) {
  return request({
    url: '/base/bankReport/workload',
    method: 'get',
    params
  })
}
