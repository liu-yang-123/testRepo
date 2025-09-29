import request from '@/utils/request'

export function treeTailBox(departmentId) {
  return request({
    url: '/base/bankBox/tree',
    method: 'get',
    params: {
      departmentId
    }
  })
}

export function addTailBox(data) {
  return request({
    url: '/base/bankBox/save',
    method: 'post',
    data
  })
}

export function updateTailBox(data) {
  return request({
    url: '/base/bankBox/update',
    method: 'post',
    data
  })
}

export function stopTailBox(id) {
  return request({
    url: `/base/bankBox/stop/${id}`,
    method: 'post'
  })
}

export function enableTailBox(id) {
  return request({
    url: `/base/bankBox/enable/${id}`,
    method: 'post'
  })
}

export function detailTailBox(id) {
  return request({
    url: `/base/bankBox/info/${id}`,
    method: 'post'
  })
}
