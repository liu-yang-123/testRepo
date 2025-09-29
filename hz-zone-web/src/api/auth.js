import request from '@/utils/request'

export function login(data) {
  return request({
    url: '/base/auth/login',
    method: 'post',
    data
  })
}

export function getInfo(token) {
  return request({
    url: '/base/auth/info',
    method: 'get',
    params: { token }
  })
}

