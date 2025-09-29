import request from '@/utils/request'

export function listCashbox(data) {
  return request({
    url: '/base/cashboxPack/list',
    method: 'get',
    params: data
  })
}

export function detailCashbox(id) {
  return request({
    url: `/base/cashboxPack/info/${id}`,
    method: 'get'
  })
}
