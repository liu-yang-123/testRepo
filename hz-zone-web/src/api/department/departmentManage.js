import request from '@/utils/request'

export function listDepartmentTree() {
  return request({
    url: '/base/department/tree',
    method: 'get'
  })
}

export function listDepartment(id) {
  return request({
    url: `/base/department/info/${id}`,
    method: 'post'
  })
}

export function addDepartment(data) {
  return request({
    url: '/base/department/save',
    method: 'post',
    data
  })
}

export function updateDepartment(data) {
  return request({
    url: '/base/department/update',
    method: 'post',
    data
  })
}
