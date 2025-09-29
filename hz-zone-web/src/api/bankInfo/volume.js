import request from '@/utils/request'

export function listVolume(params) {
  return request({
    url: '/base/bankInquiry/vaultVolumeList',
    method: 'get',
    params
  })
}

export function bankDenom(params) {
  return request({
    url: '/base/bankInquiry/bankDenom',
    method: 'get',
    params
  })
}

