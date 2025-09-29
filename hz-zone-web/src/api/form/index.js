import request from '@/utils/request'

export function getFinToken() {
  return request({
    url: '/base/auth/finserviceLogin',
    method: 'get'
  })
}

export function getSafeToken() {
  return request({
    url: '/base/auth/safe/send',
    method: 'get'
  })
}

export function getSafeReceiveToken() {
  return request({
    url: '/base/auth/safe/receive',
    method: 'get'
  })
}

export function getProductToken() {
  return request({
    url: '/base/auth/product/send',
    method: 'get'
  })
}

export function getProductReceiveToken() {
  return request({
    url: '/base/auth/product/receive',
    method: 'get'
  })
}

