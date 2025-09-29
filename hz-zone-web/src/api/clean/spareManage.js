import request from '@/utils/request'

export function listSpare(params) {
  return request({
    url: '/base/addition/cash/list',
    method: 'get',
    params
  })
}

export function deleteSpare(id) {
  return request({
    url: `/base/addition/cash/delete/${id}`,
    method: 'post'
  })
}

export function addSpare(data) {
  return request({
    url: '/base/addition/cash/save',
    method: 'post',
    data
  })
}

export function addBatchSpare(data) {
  return request({
    url: '/base/addition/cash/saveBatch',
    method: 'post',
    data
  })
}

export function updateSpare(data) {
  return request({
    url: '/base/addition/cash/update',
    method: 'post',
    data
  })
}

export function confirmSpare(id) {
  return request({
    url: `/base/addition/cash/confirm/${id}`,
    method: 'post'
  })
}

export function cancelSpare(id) {
  return request({
    url: `/base/addition/cash/cancel/${id}`,
    method: 'post'
  })
}

export function batchConfirmSpare(ids) {
  return request({
    url: '/base/addition/cash/batchConfirm',
    method: 'post',
    data: {
      ids
    }
  })
}

export function quickOut(ids) {
  return request({
    url: '/base/vaultOrder/quickCashOut',
    method: 'post',
    data: {
      ids
    }
  })
}
