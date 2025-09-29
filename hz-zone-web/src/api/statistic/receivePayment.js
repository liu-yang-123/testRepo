import request from '@/utils/request'

export function listPayment(params) {
  return request({
    url: '/base/bankReport/receivePayment',
    method: 'get',
    params
  })
}
