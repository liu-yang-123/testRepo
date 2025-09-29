import request from '@/utils/request'
import requestBlob from '@/utils/requestBlob'

// 列表数据
export function listTask(data) {
  return request({
    url: '/base/clearTask/list',
    method: 'get',
    params: data
  })
}

export function addTask(data) {
  return request({
    url: '/base/clearTask/save',
    method: 'post',
    data
  })
}

export function updateTask(data) {
  return request({
    url: '/base/clearTask/update',
    method: 'post',
    data
  })
}

export function finishTask(data) {
  return request({
    url: '/base/clearTask/finish',
    method: 'post',
    data
  })
}

export function deleteTask(id) {
  return request({
    url: `/base/clearTask/delete/${id}`,
    method: 'post'
  })
}

export function listRoute(data) {
  return request({
    url: '/base/clearTask/route',
    method: 'get',
    params: data
  })
}

// 详情
export function detailTask(taskId) {
  return request({
    url: '/base/clearTask/detail',
    method: 'get',
    params: {
      taskId
    }
  })
}

// 银行维度获取清分数据
export function listBank(data) {
  return request({
    url: '/base/clearTask/bank',
    method: 'get',
    params: data
  })
}

// 线路加载ATM设备
export function listDevice(data) {
  return request({
    url: '/base/clearTask/atmList',
    method: 'get',
    params: data
  })
}
// 上传清分任务
export function uploadTask(data) {
  return request({
    url: '/base/clearTask/uploadTask',
    method: 'post',
    data
  })
}
// 下载线路任务
export function exportRouteTask(data) {
  return requestBlob({
    url: '/base/clearTask/exportRouteTask',
    method: 'get',
    params: data
  })
}

// 下载线路任务
export function exportBankTask(data) {
  return requestBlob({
    url: '/base/clearTask/exportBankTask',
    method: 'get',
    params: data
  })
}

// 清分审核列表数据
export function listAuditTask(data) {
  return request({
    url: '/base/clearTaskAudit/list',
    method: 'get',
    params: data
  })
}
// 清分任务审核
export function auditTask(data) {
  return request({
    url: '/base/clearTaskAudit/audit',
    method: 'post',
    data
  })
}
// 清分任务审核历史
export function auditHistory(params) {
  return request({
    url: '/base/clearTaskAudit/auditHistory',
    method: 'get',
    params
  })
}

export function importData(data) {
  return request({
    url: '/base/clearTask/saveImportClear',
    method: 'post',
    data
  })
}

export const importPath = process.env.VUE_APP_BASE_API + '/base/clearTask/importClearTask'
