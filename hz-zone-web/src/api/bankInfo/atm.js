import request from '@/utils/request'

export function listATM(params) {
  return request({
    url: '/base/bankInquiry/atmList',
    method: 'get',
    params
  })
}
