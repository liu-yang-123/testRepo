import request from '@/utils/request'

export function listFingerprint(params) {
  return request({
    url: '/base/fingerprint/list',
    method: 'get',
    params
  })
}
