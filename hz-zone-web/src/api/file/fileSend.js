import request from '@/utils/request'

export function listRecord(params) {
  return request({
    url: '/base/fileRecord/sendList',
    method: 'get',
    params
  })
}

export function addRecord(data) {
  return request({
    url: '/base/fileRecord/save',
    method: 'post',
    data
  })
}

export const uploadPath = process.env.VUE_APP_BASE_API + '/base/common/fileSend'

export function deleteRecord(id) {
  return request({
    url: `/base/fileRecord/delete/${id}`,
    method: 'post'
  })
}
