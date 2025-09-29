import request from '@/utils/request'

export function listBags(params) {
  return request({
    url: '/base/boxpack/list',
    method: 'get',
    params
  })
}

export function addBags(data) {
  return request({
    url: '/base/boxpack/create',
    method: 'post',
    data
  })
}

export function updateBags(data) {
  return request({
    url: '/base/boxpack/update',
    method: 'post',
    data
  })
}

export function deleteBags(id) {
  return request({
    url: `/base/boxpack/delete/${id}`,
    method: 'post'
  })
}

