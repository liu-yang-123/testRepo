import request from '@/utils/request'
import requestBlob from '@/utils/requestBlob'

export function listImportRecord(params) {
  return request({
    url: '/base/taskImportRecord/taskList',
    method: 'get',
    params
  })
}

export function deleteImportRecord(id) {
  return request({
    url: `/base/taskImportRecord/taskDelete/${id}`,
    method: 'post'
  })
}

// 下载
export function exportClearTask(id) {
  return requestBlob({
    url: `/base/taskImportRecord/exportClearTask/${id}`,
    method: 'get'
  })
}
