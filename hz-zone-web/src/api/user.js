import request from '@/utils/request'
// 登录
export function login(data) {
  return request({
    url: '/base/auth/login',
    method: 'post',
    data
  })
}
// 获取用户信息
export function getInfo(token) {
  return request({
    url: '/base/auth/info',
    method: 'get',
    params: { token }
  })
}
// 获取用户列表数据
export function getUserList(data) {
  return request({
    url: '/base/user/list',
    method: 'get',
    data
  })
}
// 退出
export function logout() {
  return request({
    url: '/base/user/logout',
    method: 'post'
  })
}

// 修改密码
export function profile(data) {
  return request({
    url: '/base/user/editPwd',
    method: 'post',
    data
  })
}
