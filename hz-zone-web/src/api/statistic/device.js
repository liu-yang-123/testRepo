import request from '@/utils/request'

export function listDevice(params) {
  return request({
    url: '/base/bankReport/device',
    method: 'get',
    params
  })
}
