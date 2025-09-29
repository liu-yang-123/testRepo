import request from '@/utils/request'

export function listVehicle(params) {
  return request({
    url: '/base/vehicle/list',
    method: 'get',
    params
  })
}

export function addVehicle(data) {
  return request({
    url: '/base/vehicle/save',
    method: 'post',
    data
  })
}

export function updateVehicle(data) {
  return request({
    url: '/base/vehicle/update',
    method: 'post',
    data
  })
}

export function delectVehicle(id) {
  return request({
    url: `/base/vehicle/delete/${id}`,
    method: 'post'
  })
}

export function repairVehicle(id) {
  return request({
    url: `/base/vehicle/repair/${id}`,
    method: 'post'
  })
}

export function enableVehicle(id) {
  return request({
    url: `/base/vehicle/enable/${id}`,
    method: 'post'
  })
}

export function scraptVehicle(id) {
  return request({
    url: `/base/vehicle/scrap/${id}`,
    method: 'post'
  })
}
