import request from '@/utils/request'

// 列表数据
export function listModel(params) {
  return request({
    url: '/base/deviceModel/list',
    method: 'get',
    params
  })
}
// 创建数据
export function createModel(data) {
  return request({
    url: '/base/deviceModel/save',
    method: 'post',
    data
  })
}
// 修改
export function updateModel(data) {
  return request({
    url: '/base/deviceModel/update',
    method: 'post',
    data
  })
}
// 删除
export function deleteModel(data) {
  return request({
    url: '/base/deviceModel/delete/' + data.id,
    method: 'post'
  })
}

export function modelOption() {
  return request({
    url: '/base/deviceModel/option',
    method: 'get'
  })
}

