import request from '@/utils/request'

export function listDenom(params) {
  return request({
    url: '/base/denom/list',
    method: 'get',
    params
  })
}

export function addDenom(data) {
  return request({
    url: '/base/denom/save',
    method: 'post',
    data
  })
}

export function updateDenom(data) {
  return request({
    url: '/base/denom/update',
    method: 'post',
    data
  })
}

export function delectDenom(id) {
  return request({
    url: `/base/denom/delete/${id}`,
    method: 'post'
  })
}

export function denomOption() {
  return request({
    url: '/base/denom/option',
    method: 'get'
  })
}
