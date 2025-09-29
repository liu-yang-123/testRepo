import request from '@/utils/request'

export function listRole(data) {
  return request({
    url: '/base/pdaRole/list',
    method: 'get',
    params: data
  })
}

export function createRole(data) {
  return request({
    url: '/base/pdaRole/save',
    method: 'post',
    data
  })
}

export function deleteRole(data) {
  return request({
    url: '/base/pdaRole/delete/' + data.id,
    method: 'post'
  })
}

export function updateRole(data) {
  return request({
    url: '/base/pdaRole/update',
    method: 'post',
    data
  })
}

export function getPermission(query) {
  return request({
    url: '/base/pdaRole/permissions/get',
    method: 'get',
    params: query
  })
}

export function updatePermission(data) {
  return request({
    url: '/base/pdaRole/permissions/update',
    method: 'post',
    data
  })
}
