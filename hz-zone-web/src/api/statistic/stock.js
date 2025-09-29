import request from '@/utils/request'

export function listStock(params) {
  return request({
    url: '/base/bankReport/stock',
    method: 'get',
    params
  })
}
