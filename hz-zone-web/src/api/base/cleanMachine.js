import request from '@/utils/request'

export function treeCleanBox(departmentId) {
  return request({
    url: '/base/bankClear/tree',
    method: 'get',
    params: {
      departmentId
    }
  })
}

export function addCleanBox(data) {
  return request({
    url: '/base/bankClear/save',
    method: 'post',
    data
  })
}

export function updateCleanBox(data) {
  return request({
    url: '/base/bankClear/update',
    method: 'post',
    data
  })
}

export function stopCleanBox(id) {
  return request({
    url: `/base/bankClear/stop/${id}`,
    method: 'post'
  })
}

export function enableCleanBox(id) {
  return request({
    url: `/base/bankClear/enable/${id}`,
    method: 'post'
  })
}

export function detailCleanBox(id) {
  return request({
    url: `/base/bankClear/info/${id}`,
    method: 'post'
  })
}
