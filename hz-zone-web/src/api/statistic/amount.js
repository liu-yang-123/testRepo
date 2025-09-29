import request from '@/utils/request'

export function listAmount(params) {
  return request({
    url: '/base/bankReport/amount',
    method: 'get',
    params
  })
}

export function bankDenom(params) {
  return request({
    url: '/base/bankInquiry/bankDenom',
    method: 'get',
    params
  })
}

