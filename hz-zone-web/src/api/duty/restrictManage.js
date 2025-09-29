import request from '@/utils/request'

export function listRestrict(params) {
  return request({
    url: '/base/driving/restrict/list',
    method: 'get',
    params
  })
}

export function addRestrict(data) {
  return request({
    url: '/base/driving/restrict/save',
    method: 'post',
    data
  })
}

export function updateRestrict(data) {
  return request({
    url: '/base/driving/restrict/update',
    method: 'post',
    data
  })
}

export function deleteRestrict(id) {
  return request({
    url: `/base/driving/restrict/delete/${id}`,
    method: 'post'
  })
}

// 特殊限行

export function listRestrictXh(params) {
  return request({
    url: '/base/driving/restrictXh/list',
    method: 'get',
    params
  })
}

export function addRestrictXh(data) {
  return request({
    url: '/base/driving/restrictXh/save',
    method: 'post',
    data
  })
}

export function updateRestrictXh(data) {
  return request({
    url: '/base/driving/restrictXh/update',
    method: 'post',
    data
  })
}

export function deleteRestrictXh(id) {
  return request({
    url: `/base/driving/restrictXh/delete/${id}`,
    method: 'post'
  })
}
