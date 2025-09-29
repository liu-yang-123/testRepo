import request from '@/utils/request'
import requestPrint from '@/utils/requestPrint'

// 列表数据
export function listDevice(params) {
  return request({
    url: '/base/device/list',
    method: 'get',
    params
  })
}
// 创建数据
export function createDevice(data) {
  return request({
    url: '/base/device/save',
    method: 'post',
    data
  })
}
// 修改
export function updateDevice(data) {
  return request({
    url: '/base/device/update',
    method: 'post',
    data
  })
}
// 删除
export function deleteDevice(data) {
  return request({
    url: '/base/device/delete/' + data.id,
    method: 'post'
  })
}

// 打印
export function printDevice(data) {
  return requestPrint({
    url: '/printer/deviceQrCode/multi',
    method: 'post',
    data
  })
}
