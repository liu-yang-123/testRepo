import request from '@/utils/request'

export function listTask(params) {
  return request({
    url: '/base/boxpackTask/list',
    method: 'get',
    params
  })
}

export function addTask(data) {
  return request({
    url: '/base/boxpack/create',
    method: 'post',
    data
  })
}

export function detailTask(id) {
  return request({
    url: `/base/boxpackTask/info/${id}`,
    method: 'post'
  })
}

export function updateTask(data) {
  return request({
    url: '/base/boxpack/update',
    method: 'post',
    data
  })
}

export function deleteTask(id) {
  return request({
    url: `/base/boxpack/delete/${id}`,
    method: 'post'
  })
}

