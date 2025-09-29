import request from '@/utils/request'

export function listAccount(params) {
  return request({
    url: '/base/clear/charge/rule/list',
    method: 'post',
    params
  })
}

export function addAccount(data) {
  return request({
    url: '/base/clear/charge/rule/add',
    method: 'post',
    data
  })
}

export function updateAccount(data) {
  return request({
    url: '/base/clear/charge/rule/update',
    method: 'post',
    data
  })
}

export function deleteAccount(id) {
  return request({
    url: `/base/clear/charge/rule/del/${id}`,
    method: 'post'
  })
}

