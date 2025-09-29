import request from '@/utils/request'

export function listInspect(routeId) {
  return request({
    url: '/base/bankCheck/list/route',
    method: 'get',
    params: {
      routeId
    }
  })
}
