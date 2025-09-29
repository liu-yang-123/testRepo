import request from '@/utils/request'

// 获取用户列表数据
export function listUser(params) {
  return request({
    url: '/base/user/list',
    method: 'get',
    params
  })
}

export function createUser(data) {
  return request({
    url: '/base/user/save',
    method: 'post',
    data
  })
}

export function createBankUser(data) {
  return request({
    url: '/base/user/bankSave',
    method: 'post',
    data
  })
}

export function deleteUser(data) {
  return request({
    url: '/base/user/delete/' + data.id,
    method: 'post'
  })
}

export function updateUser(data) {
  return request({
    url: '/base/user/update',
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

export function userOption() {
  return request({
    url: '/base/user/option',
    method: 'get'
  })
}
