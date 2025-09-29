import request from '@/utils/request'

export function listStation(params) {
  return request({
    url: '/base/district/list',
    method: 'get',
    params
  })
}

export function deleteStation(id) {
  return request({
    url: `/base/district/delete/${id}`,
    method: 'post'
  })
}

export function addStation(data) {
  return request({
    url: '/base/district/save',
    method: 'post',
    data
  })
}

export function updateStation(data) {
  return request({
    url: '/base/district/update',
    method: 'post',
    data
  })
}
