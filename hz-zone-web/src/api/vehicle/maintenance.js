import request from '@/utils/request'

export function listMaintenance(params) {
  return request({
    url: '/base/vehicleMaintain/list',
    method: 'get',
    params
  })
}

export function addMaintenance(data) {
  return request({
    url: '/base/vehicleMaintain/save',
    method: 'post',
    data
  })
}

export function updateMaintenance(data) {
  return request({
    url: '/base/vehicleMaintain/update',
    method: 'post',
    data
  })
}

export function delectMaintenance(id) {
  return request({
    url: `/base/vehicleMaintain/delete/${id}`,
    method: 'post'
  })
}
