import request from '@/utils/request'

export function listWhiteList(params) {
  return request({
    url: '/base/whiteList/list',
    method: 'get',
    params
  })
}

export function addWhiteList(data) {
  return request({
    url: '/base/whiteList/save',
    method: 'post',
    data
  })
}

export function updateWhiteList(data) {
  return request({
    url: '/base/whiteList/update',
    method: 'post',
    data
  })
}

export function delectWhiteList(id) {
  return request({
    url: `/base/whiteList/delete/${id}`,
    method: 'post'
  })
}
