import request from '@/utils/request'
import requestBlob from '@/utils/requestBlob'

export function listCard(params) {
  return request({
    url: '/base/atmTaskCard/list',
    method: 'get',
    params
  })
}
export function addCard(data) {
  return request({
    url: '/base/atmTaskCard/save',
    method: 'post',
    data
  })
}

export function updateCard(data) {
  return request({
    url: '/base/atmTaskCard/update',
    method: 'post',
    data
  })
}

export function editCard(data) {
  return request({
    url: '/base/atmTaskCard/edit',
    method: 'post',
    data
  })
}

export function deleteCard(id) {
  return request({
    url: `/base/atmTaskCard/delete/${id}`,
    method: 'post'
  })
}

export function batchCard(params) {
  return request({
    url: '/base/atmTaskCard/batchDistribute',
    method: 'post',
    params
  })
}

export function handover(data) {
  return request({
    url: '/base/atmTaskCard/handover',
    method: 'post',
    data
  })
}

export function exportCard(params) {
  return requestBlob({
    url: '/base/atmTaskCard/download',
    method: 'get',
    params
  })
}

export function exportDeliver(params) {
  return requestBlob({
    url: '/base/atmTaskCard/exportDeliver',
    method: 'get',
    params
  })
}

export function exportCollect(params) {
  return requestBlob({
    url: '/base/atmTaskCard/exportCollect',
    method: 'get',
    params
  })
}
