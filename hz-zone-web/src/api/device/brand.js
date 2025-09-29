import request from '@/utils/request'

// 列表数据
export function listBrand(data) {
  return request({
    url: '/base/deviceFactory/list',
    method: 'get',
    params: data
  })
}
// 创建数据
export function createBrand(data) {
  return request({
    url: '/base/deviceFactory/save',
    method: 'post',
    data
  })
}
// 修改
export function updateBrand(data) {
  return request({
    url: '/base/deviceFactory/update',
    method: 'post',
    data
  })
}
// 删除
export function deleteBrand(data) {
  return request({
    url: '/base/deviceFactory/delete/' + data.id,
    method: 'post'
  })
}
// 品牌选项
export function brandOption() {
  return request({
    url: '/base/deviceFactory/option',
    method: 'get'
  })
}
