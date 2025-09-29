import request from '@/utils/request'

export function listCompany(params) {
  return request({
    url: '/base/fileCompany/list',
    method: 'get',
    params
  })
}

export function addCompany(data) {
  return request({
    url: '/base/fileCompany/save',
    method: 'post',
    data
  })
}

export function updateCompany(data) {
  return request({
    url: '/base/fileCompany/update',
    method: 'post',
    data
  })
}

export function deleteCompany(id) {
  return request({
    url: `/base/fileCompany/delete/${id}`,
    method: 'post'
  })
}

export function enableCompany(id) {
  return request({
    url: `/base/fileCompany/enable/${id}`,
    method: 'post'
  })
}

export function stopCompany(id) {
  return request({
    url: `/base/fileCompany/stop/${id}`,
    method: 'post'
  })
}
