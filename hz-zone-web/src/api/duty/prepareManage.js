import request from '@/utils/request'

export function listAlternate(params) {
  return request({
    url: '/base/alternate/list',
    method: 'get',
    params
  })
}

export function addAlternate(data) {
  return request({
    url: '/base/alternate/save',
    method: 'post',
    data
  })
}

export function updateAlternate(data) {
  return request({
    url: '/base/alternate/update',
    method: 'post',
    data
  })
}

export function deleteAlternate(id) {
  return request({
    url: `/base/alternate/delete/${id}`,
    method: 'post'
  })
}
