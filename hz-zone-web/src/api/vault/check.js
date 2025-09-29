import request from '@/utils/request'

export function listCheck(params) {
  return request({
    url: '/base/vaultCheck/list',
    method: 'get',
    params
  })
}
