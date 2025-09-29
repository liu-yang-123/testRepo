import request from '@/utils/request'

// 列表数据
export function listMaintain(params) {
  return request({
    url: '/base/deviceMaintain/list',
    method: 'get',
    params
  })
}
// 创建数据
export function createMaintain(data) {
  return request({
    url: '/base/deviceMaintain/save',
    method: 'post',
    data
  })
}

