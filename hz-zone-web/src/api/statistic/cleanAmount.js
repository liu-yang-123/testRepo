import request from '@/utils/request'

export function listAmount(params) {
  return request({
    url: '/base/cleanReport/amount',
    method: 'get',
    params
  })
}
