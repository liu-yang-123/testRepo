import request from '@/utils/request'

export function listEmployee(params) {
  return request({
    url: '/base/employee/list',
    method: 'get',
    params
  })
}

export function detailEmployee(id) {
  return request({
    url: `/base/employee/info/${id}`,
    method: 'get'
  })
}

export function delectEmployee(id) {
  return request({
    url: `/base/employee/delete/${id}`,
    method: 'post'
  })
}

export function quitEmployee(data) {
  return request({
    url: '/base/employee/quit',
    method: 'post',
    data
  })
}

export function addEmployee(data) {
  return request({
    url: '/base/employee/save',
    method: 'post',
    data
  })
}

export function updateEmployee(data) {
  return request({
    url: '/base/employee/update',
    method: 'post',
    data
  })
}
