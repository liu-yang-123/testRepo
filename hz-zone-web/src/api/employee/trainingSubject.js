import request from '@/utils/request'

export function listSubjects(params) {
  return request({
    url: '/base/subjects/list',
    method: 'get',
    params
  })
}

export function addSubjects(data) {
  return request({
    url: '/base/subjects/save',
    method: 'post',
    data
  })
}

export function updateSubjects(data) {
  return request({
    url: '/base/subjects/update',
    method: 'post',
    data
  })
}

export function delectSubjects(id) {
  return request({
    url: `/base/subjects/delete/${id}`,
    method: 'post'
  })
}
