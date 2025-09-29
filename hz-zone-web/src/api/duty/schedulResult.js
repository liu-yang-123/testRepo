import request from '@/utils/request'
import requestBlob from '@/utils/requestBlob'
// 排班结果
export function listResult(params) {
  return request({
    url: '/base/schdResult/list',
    method: 'get',
    params
  })
}
// 创建排班
export function addResult(params) {
  return request({
    url: '/base/schdResult/save',
    method: 'post',
    params
  })
}

// 修改排班
export function updateResult(data) {
  return request({
    url: '/base/schdResult/update',
    method: 'post',
    data
  })
}

// 删除排班结果
export function deleteResult(id) {
  return request({
    url: `/base/schdResult/delete/${id}`,
    method: 'post'
  })
}

// 排班导出
export function resultDownload(params) {
  return requestBlob({
    url: '/base/schdResult/download',
    method: 'get',
    params
  })
}

// 人员互换
export function changeResult(data) {
  return request({
    url: '/base/schdResult/change',
    method: 'post',
    data
  })
}

// 具备车长资格
export function getLeader(departmentId) {
  return request({
    url: '/base/employee/getLeader',
    method: 'get',
    params: {
      departmentId
    }
  })
}

// 排班记录历史
export function getRecord(params) {
  return request({
    url: '/base/schdResult/record',
    method: 'get',
    params
  })
}

// 选择日期检测
export function checkTime(planDay) {
  return request({
    url: '/base/schdResult/checkTime',
    method: 'get',
    params: {
      planDay
    }
  })
}

// 银行导出
export function resultExport(params) {
  return requestBlob({
    url: '/base/schdResult/export',
    method: 'get',
    params
  })
}

// 导入排班
export const importPath = process.env.VUE_APP_BASE_API + '/base/schdResult/importSchd'
