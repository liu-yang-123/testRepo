import request from '@/utils/request'

export function listManage(params) {
  return request({
    url: '/base/fileRecord/all',
    method: 'get',
    params
  })
}

export function deleteManage(id) {
  return request({
    url: `/base/fileRecord/remove/${id}`,
    method: 'post'
  })
}
