import request from '@/utils/request'

// 获取用户列表数据
export function listTerminal(params) {
  return request({
    url: '/base/pda/list',
    method: 'get',
    params
  })
}

export function addTerminal(data) {
  return request({
    url: '/base/pda/save',
    method: 'post',
    data
  })
}

export function stopTerminal(id) {
  return request({
    url: `/base/pda/stop/${id}`,
    method: 'post'
  })
}

export function enableTerminal(id) {
  return request({
    url: `/base/pda/enable/${id}`,
    method: 'post'
  })
}

export function updateTerminal(data) {
  return request({
    url: '/base/pda/update',
    method: 'post',
    data
  })
}

export function deleteTerminal(id) {
  return request({
    url: `/base/pda/delete/${id}`,
    method: 'post'
  })
}

