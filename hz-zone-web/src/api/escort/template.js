import request from '@/utils/request'

export function listTemplate(params) {
  return request({
    url: '/base/routeTemplate/list',
    method: 'get',
    params
  })
}

export function addTemplate(data) {
  return request({
    url: '/base/routeTemplate/save',
    method: 'post',
    data
  })
}

export function deleteTemplate(id) {
  return request({
    url: `/base/routeTemplate/delete/${id}`,
    method: 'post'
  })
}

export function updateTemplate(data) {
  return request({
    url: '/base/routeTemplate/update',
    method: 'post',
    data
  })
}

export function detailAtmInfo(params) {
  return request({
    url: '/base/routeTemplate/atmInfo',
    method: 'get',
    params
  })
}

export function detailBankInfo(params) {
  return request({
    url: '/base/routeTemplate/bankInfo',
    method: 'get',
    params
  })
}

export function addAtmInfo(data) {
  return request({
    url: '/base/routeTemplate/saveAtm',
    method: 'post',
    data
  })
}

export function updateAtmInfo(data) {
  return request({
    url: '/base/routeTemplate/updateAtm',
    method: 'post',
    data
  })
}

export function deleteAtmInfo(id) {
  return request({
    url: `/base/routeTemplate/deleteAtm/${id}`,
    method: 'post'
  })
}

export function updateAtmSort(params) {
  return request({
    url: '/base/routeTemplate/updateAtmSort',
    method: 'post',
    params
  })
}

// 任务记录历史
export function getRecord(params) {
  return request({
    url: '/base/routeTemplate/record',
    method: 'get',
    params
  })
}
// 执行操作
export function executeRoute(params) {
  return request({
    url: '/base/routeTemplate/executeTask',
    method: 'post',
    params
  })
}

// 途经网点

export function listBankInfo(id) {
  return request({
    url: `/base/routeTemplate/bankInfo/${id}`,
    method: 'get'
  })
}

export function deleteBankInfo(id) {
  return request({
    url: `/base/routeTemplate/deleteBank/${id}`,
    method: 'post'
  })
}

export function addBankInfo(data) {
  return request({
    url: '/base/routeTemplate/saveBank',
    method: 'post',
    data
  })
}
