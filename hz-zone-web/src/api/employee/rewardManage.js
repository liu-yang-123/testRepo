import request from '@/utils/request'

export function listAwards(params) {
  return request({
    url: '/base/awards/list',
    method: 'get',
    params
  })
}

export function addAwards(data) {
  return request({
    url: '/base/awards/save',
    method: 'post',
    data
  })
}

export function updateAwards(data) {
  return request({
    url: '/base/awards/update',
    method: 'post',
    data
  })
}

export function delectAwards(id) {
  return request({
    url: `/base/awards/delete/${id}`,
    method: 'post'
  })
}
