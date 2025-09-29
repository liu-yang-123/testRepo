import request from '@/utils/request'

export function treeTailBox(departmentId) {
  return request({
    url: '/base/bankTrade/tree',
    method: 'get',
    params: {
      departmentId
    }
  })
}

export function addTailBox(data) {
  return request({
    url: '/base/bankTrade/save',
    method: 'post',
    data
  })
}

export function updateTailBox(data) {
  return request({
    url: '/base/bankTrade/update',
    method: 'post',
    data
  })
}

export function stopTailBox(id) {
  return request({
    url: `/base/bankTrade/stop/${id}`,
    method: 'post'
  })
}

export function enableTailBox(id) {
  return request({
    url: `/base/bankTrade/enable/${id}`,
    method: 'post'
  })
}

export function detailTailBox(id) {
  return request({
    url: `/base/bankTrade/info/${id}`,
    method: 'post'
  })
}
