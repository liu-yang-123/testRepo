import request from '@/utils/request'

export function listLog(params) {
  return request({
    url: '/base/log/list',
    method: 'get',
    params
  })
}
