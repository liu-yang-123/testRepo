import request from '@/utils/request'

export function listRecord(params) {
  return request({
    url: '/base/fileRecord/receiveList',
    method: 'get',
    params
  })
}
