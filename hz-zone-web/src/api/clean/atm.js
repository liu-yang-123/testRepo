import request from '@/utils/request'
import requestPrint from '@/utils/requestPrint'

// 获取用户列表数据
export function listATM(params) {
  return request({
    url: '/base/atm/list',
    method: 'get',
    params
  })
}

export function addATM(data) {
  return request({
    url: '/base/atm/save',
    method: 'post',
    data
  })
}

export function stopATM(id) {
  return request({
    url: `/base/atm/stop/${id}`,
    method: 'post'
  })
}

export function enableATM(id) {
  return request({
    url: `/base/atm/enable/${id}`,
    method: 'post'
  })
}

export function updateATM(data) {
  return request({
    url: '/base/atm/update',
    method: 'post',
    data
  })
}

export function deleteATM(id) {
  return request({
    url: `/base/atm/delete/${id}`,
    method: 'post'
  })
}

export function printATM(data) {
  return requestPrint({
    url: '/printer/atmQrCode/multi',
    method: 'post',
    data
  })
}
