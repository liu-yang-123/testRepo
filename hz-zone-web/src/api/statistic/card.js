import request from '@/utils/request'

export function listCard(params) {
  return request({
    url: '/base/bankReport/card',
    method: 'get',
    params
  })
}
