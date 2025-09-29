import request from '@/utils/request'

export function listRecord(params) {
  return request({
    url: '/base/bankInquiry/atmTask/list',
    method: 'get',
    params
  })
}

export function atmTaskRoute(id) {
  return request({
    url: `/base/bankInquiry/atmTaskInfo/${id}`,
    method: 'post'
  })
}
