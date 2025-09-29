import request from '@/utils/request'

export function listConductor(params) {
  return request({
    url: '/base/schdConductor/list',
    method: 'get',
    params
  })
}

export function updateConductor(params) {
  return request({
    url: '/base/schdConductor/update',
    method: 'post',
    params
  })
}
