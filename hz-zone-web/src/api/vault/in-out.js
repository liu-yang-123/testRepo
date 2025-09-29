import request from '@/utils/request'

export function listOrder(params) {
  return request({
    url: '/base/vaultOrder/list',
    method: 'get',
    params
  })
}

export function addOrder(data) {
  return request({
    url: '/base/vaultOrder/save',
    method: 'post',
    data
  })
}

export function updateOrder(data) {
  return request({
    url: '/base/vaultOrder/update',
    method: 'post',
    data
  })
}

export function deleteOrder(id) {
  return request({
    url: `/base/vaultOrder/delete/${id}`,
    method: 'post'
  })
}

export function listOrderDetail(params) {
  return request({
    url: '/base/vaultOrder/detail',
    method: 'get',
    params
  })
}

export function auditOrder(data) {
  return request({
    url: '/base/vaultOrder/audit',
    method: 'post',
    data
  })
}
// 提交审核
export function submitAudit(id) {
  return request({
    url: `/base/vaultOrder/submitAudit/${id}`,
    method: 'post'
  })
}
// 提交撤销
export function undoOrder(id) {
  return request({
    url: `/base/vaultOrder/undo/${id}`,
    method: 'post'
  })
}

export function getTaskList(params) {
  return request({
    url: '/base/atmTask/getTaskList',
    method: 'get',
    params
  })
}
