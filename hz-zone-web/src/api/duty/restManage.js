import request from '@/utils/request'

// 休息计划
export function listVacation(params) {
  return request({
    url: '/base/vacation/plan/list',
    method: 'get',
    params
  })
}

export function addVacation(data) {
  return request({
    url: '/base/vacation/plan/save',
    method: 'post',
    data
  })
}

export function updateVacation(data) {
  return request({
    url: '/base/vacation/plan/update',
    method: 'post',
    data
  })
}

export function deleteVacation(id) {
  return request({
    url: `/base/vacation/plan/delete/${id}`,
    method: 'post'
  })
}

// 员工休息配置
export function listEmpVacation(params) {
  return request({
    url: '/base/vacation/setting/list',
    method: 'get',
    params
  })
}

export function addEmpVacation(data) {
  return request({
    url: '/base/vacation/setting/save',
    method: 'post',
    data
  })
}

export function updateEmpVacation(data) {
  return request({
    url: '/base/vacation/setting/update',
    method: 'post',
    data
  })
}

export function deleteEmpVacation(id) {
  return request({
    url: `/base/vacation/setting/delete/${id}`,
    method: 'post'
  })
}

export function listSum(planId, jobType) {
  return request({
    url: '/base/vacation/setting/sum',
    method: 'get',
    params: {
      planId,
      jobType
    }
  })
}
// 调整
export function listAdjust(params) {
  return request({
    url: '/base/vacation/adjust/list',
    method: 'get',
    params
  })
}

export function addAdjust(data) {
  return request({
    url: '/base/vacation/adjust/save',
    method: 'post',
    data
  })
}

export function updateAdjust(data) {
  return request({
    url: '/base/vacation/adjust/update',
    method: 'post',
    data
  })
}

export function deleteAdjust(id) {
  return request({
    url: `/base/vacation/adjust/delete/${id}`,
    method: 'post'
  })
}

// 备班

export function listAlternate(params) {
  return request({
    url: '/base/alternate/list',
    method: 'get',
    params
  })
}

export function addAlternate(data) {
  return request({
    url: '/base/alternate/save',
    method: 'post',
    data
  })
}

export function updateAlternate(data) {
  return request({
    url: '/base/alternate/update',
    method: 'post',
    data
  })
}

export function deleteAlternate(id) {
  return request({
    url: `/base/alternate/delete/${id}`,
    method: 'post'
  })
}
