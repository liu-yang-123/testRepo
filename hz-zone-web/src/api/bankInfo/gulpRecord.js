import request from '@/utils/request'

export function listCard(params) {
  return request({
    url: '/base/bankInquiry/taskCardList',
    method: 'get',
    params
  })
}
