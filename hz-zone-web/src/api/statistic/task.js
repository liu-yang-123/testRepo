import request from '@/utils/request'

export function listTask(params) {
  return request({
    url: '/base/bankReport/task',
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

