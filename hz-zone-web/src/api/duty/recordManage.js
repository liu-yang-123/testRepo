import request from '@/utils/request'

export function listPassAuth(params) {
  return request({
    url: '/base/passAuth/list',
    method: 'get',
    params
  })
}

export function addPassAuth(data) {
  return request({
    url: '/base/passAuth/save',
    method: 'post',
    data
  })
}

export function updatePassAuth(data) {
  return request({
    url: '/base/passAuth/update',
    method: 'post',
    data
  })
}

export function deletePassAuth(id) {
  return request({
    url: `/base/passAuth/delete/${id}`,
    method: 'post'
  })
}
