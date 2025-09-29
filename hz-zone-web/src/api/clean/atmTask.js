import request from '@/utils/request'

// 获取用户列表数据
export function listATMTask(params) {
  return request({
    url: '/base/atmTask/list',
    method: 'get',
    params
  })
}

export function addATMCleanTask(data) {
  return request({
    url: '/base/atmTask/saveClean',
    method: 'post',
    data
  })
}

export function addATMRepairTask(data) {
  return request({
    url: '/base/atmTask/saveRepair',
    method: 'post',
    data
  })
}

export function updateATMTask(data) {
  return request({
    url: '/base/atmTask/update',
    method: 'post',
    data
  })
}

export function deleteATMTask(id) {
  return request({
    url: `/base/atmTask/delete/${id}`,
    method: 'post'
  })
}

export function detailATMTask(atmTaskId) {
  return request({
    url: `/base/atmTask/info/${atmTaskId}`,
    method: 'post'
  })
}

export function confirmATMTask(id) {
  return request({
    url: `/base/atmTask/confirm/${id}`,
    method: 'post'
  })
}

export function revokeATMTask(id) {
  return request({
    url: `/base/atmTask/revoke/${id}`,
    method: 'post'
  })
}

// 批量确认
export function batchConfirmATMTask(ids) {
  return request({
    url: '/base/atmTask/batchConfirm',
    method: 'post',
    params: {
      ids
    }
  })
}

// 模板
export function listTemplate(params) {
  return request({
    url: '/base/atmTask/templateAtmInfo',
    method: 'get',
    params
  })
}

export function addTemplate(data) {
  return request({
    url: '/base/atmTask/saveTemplateClean',
    method: 'post',
    data
  })
}

// 出库
export function listBankTask(taskDate, departmentId) {
  return request({
    url: '/base/atmTask/getBankTask',
    method: 'get',
    params: {
      taskDate,
      departmentId
    }
  })
}

export function addVaultOrder(data) {
  return request({
    url: '/base/vaultOrder/quickOut',
    method: 'post',
    data
  })
}

// 导入

export function importData(data) {
  return request({
    url: '/base/atmTask/saveImportClean',
    method: 'post',
    data
  })
}

export function batchCancel(ids) {
  return request({
    url: '/base/atmTask/batchCancel',
    method: 'post',
    data: {
      ids
    }
  })
}

export function cashboxPack(id) {
  return request({
    url: `/base/atmTask/cashboxPack/${id}`,
    method: 'get'
  })
}

export function taskMove(data) {
  return request({
    url: '/base/atmTask/move',
    method: 'post',
    data
  })
}

export const importPath = process.env.VUE_APP_BASE_API + '/base/atmTask/importAtmTask'
