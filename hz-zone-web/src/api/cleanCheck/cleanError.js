import request from '@/utils/request'

export function listClearError(data) {
  return request({
    url: '/base/clearError/list',
    method: 'post',
    data
  })
}

export function addClearError(data) {
  return request({
    url: '/base/clearError/addmulti',
    method: 'post',
    data
  })
}

export function updateClearError(data) {
  return request({
    url: `/base/clearError/update/${data.id}`,
    method: 'post',
    data
  })
}

export function deleteClearError(data) {
  return request({
    url: `/base/clearError/del/${data.id}`,
    method: 'post'
  })
}
