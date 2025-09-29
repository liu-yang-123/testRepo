import request from '@/utils/request'

export function listLog(params) {
  return request({
    url: '/base/handoverLog/list',
    method: 'post',
    params
  })
}

export function deleteLog(id) {
  return request({
    url: `/base/handoverLog/del/${id}`,
    method: 'post'
  })
}

export function addLog(data) {
  return request({
    url: '/base/handoverLog/save',
    method: 'post',
    data
  })
}

export function updateLog(data) {
  return request({
    url: '/base/handoverLog/update',
    method: 'post',
    data
  })
}

export function imgBase64(url) {
  return request({
    url: '/base/common/showImg',
    method: 'post',
    params: { fileUrl: url }
  })
}

