import request from '@/utils/request'

// 获取用户列表数据
export function listUser(params) {
  return request({
    url: '/base/pdaUser/list',
    method: 'get',
    params
  })
}

export function createUser(data) {
  return request({
    url: '/base/pdaUser/save',
    method: 'post',
    data
  })
}

export function stopUser(id) {
  return request({
    url: `/base/pdaUser/stop/${id}`,
    method: 'post'
  })
}

export function enableUser(id) {
  return request({
    url: `/base/pdaUser/enable/${id}`,
    method: 'post'
  })
}

export function updateUser(params) {
  return request({
    url: '/base/pdaUser/update',
    method: 'post',
    params
  })
}

export function resetPwd(id) {
  return request({
    url: '/base/pdaUser/resetpwd/' + id,
    method: 'post'
  })
}

export function setAdmin(enable, id) {
  return request({
    url: '/base/pdaUser/set/admin',
    method: 'post',
    params: {
      enable,
      id
    }
  })
}
