import request from '@/utils/request'

export function listVolume(params) {
  return request({
    url: '/base/vaultVolume/list',
    method: 'get',
    params
  })
}

export function listBank(params) {
  return request({
    url: '/base/vaultVolume/bank',
    method: 'get',
    params
  })
}

export function bankDenom(params) {
  return request({
    url: '/base/vaultVolume/bankDenom',
    method: 'get',
    params
  })
}

