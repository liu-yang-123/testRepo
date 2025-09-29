import request from '@/utils/request'

export function listRecord(params) {
  return request({
    url: '/base/records/list',
    method: 'get',
    params
  })
}

export function addRecord(data) {
  return request({
    url: '/base/records/savemulti',
    method: 'post',
    data
  })
}

export function updateRecord(data) {
  return request({
    url: '/base/records/update',
    method: 'post',
    data
  })
}

export function delectRecord(id) {
  return request({
    url: `/base/records/delete/${id}`,
    method: 'post'
  })
}
