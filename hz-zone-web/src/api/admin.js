import request from '@/utils/request'

export function listAdmin(query) {
  return request({
    url: '/base/user/index',
    method: 'get',
    params: query
  })
}

export function createAdmin(data) {
  return request({
    url: '/base/user/create',
    method: 'post',
    data
  })
}

export function readminAdmin(data) {
  return request({
    url: '/base/user/readmin',
    method: 'get',
    data
  })
}

export function updateAdmin(data) {
  return request({
    url: '/base/user/update',
    method: 'post',
    data
  })
}

export function deleteAdmin(data) {
  return request({
    url: '/base/user/delete',
    method: 'post',
    data
  })
}

export function resetPwd(id) {
  return request({
    url: '/base/user/resetpwd/' + id,
    method: 'post'
  })
}
