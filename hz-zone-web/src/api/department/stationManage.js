import request from '@/utils/request'

export function listStation(params) {
  return request({
    url: '/base/jobs/list',
    method: 'get',
    params
  })
}

export function delectStation(id) {
  return request({
    url: `/base/jobs/delete/${id}`,
    method: 'post'
  })
}

export function addStation(data) {
  return request({
    url: '/base/jobs/save',
    method: 'post',
    data
  })
}

export function updateStation(data) {
  return request({
    url: '/base/jobs/update',
    method: 'post',
    data
  })
}
