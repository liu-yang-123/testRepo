import request from '@/utils/request'

export function listInspect(params) {
  return request({
    url: '/base/bankInquiry/bankCheckList',
    method: 'get',
    params
  })
}
