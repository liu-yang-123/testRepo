import request from '@/utils/request'

export function listOrder(params) {
  return request({
    url: '/base/bankInquiry/vaultOrderList',
    method: 'get',
    params
  })
}

export function listOrderDetail(params) {
  return request({
    url: '/base/bankInquiry/vaultOrderDetail',
    method: 'get',
    params
  })
}
