import request from '@/utils/request'

// 休息计划
export function listGuard(params) {
  return request({
    url: '/base/vacation/guard/plan/list',
    method: 'get',
    params
  })
}

export function addGuard(data) {
  return request({
    url: '/base/vacation/guard/plan/save',
    method: 'post',
    data
  })
}

export function updateGuard(data) {
  return request({
    url: '/base/vacation/guard/plan/update',
    method: 'post',
    data
  })
}

export function deleteGuard(id) {
  return request({
    url: `/base/vacation/guard/plan/delete/${id}`,
    method: 'post'
  })
}

// 员工休息配置
export function listEmpGuard(params) {
  return request({
    url: '/base/vacation/guard/setting/list',
    method: 'get',
    params
  })
}

export function addEmpGuard(data) {
  return request({
    url: '/base/vacation/guard/setting/save',
    method: 'post',
    data
  })
}

export function updateEmpGuard(data) {
  return request({
    url: '/base/vacation/guard/setting/update',
    method: 'post',
    data
  })
}

export function deleteEmpGuard(id) {
  return request({
    url: `/base/vacation/guard/setting/delete/${id}`,
    method: 'post'
  })
}

export function listGuardSum(planId, jobType) {
  return request({
    url: '/base/vacation/guard/setting/sum',
    method: 'get',
    params: {
      planId,
      jobType
    }
  })
}
// 调整
export function listGuardAdjust(params) {
  return request({
    url: '/base/vacation/guard/adjust/list',
    method: 'get',
    params
  })
}

export function addGuardAdjust(data) {
  return request({
    url: '/base/vacation/guard/adjust/save',
    method: 'post',
    data
  })
}

export function updateGuardAdjust(data) {
  return request({
    url: '/base/vacation/guard/adjust/update',
    method: 'post',
    data
  })
}

export function deleteGuardAdjust(id) {
  return request({
    url: `/base/vacation/guard/adjust/delete/${id}`,
    method: 'post'
  })
}

// 备班
export function listGuardAlternate(params) {
  return request({
    url: '/base/alternate/guard/list',
    method: 'get',
    params
  })
}

export function addGuardAlternate(data) {
  return request({
    url: '/base/alternate/guard/save',
    method: 'post',
    data
  })
}

export function updateGuardAlternate(data) {
  return request({
    url: '/base/alternate/guard/update',
    method: 'post',
    data
  })
}

export function deleteGuardAlternate(id) {
  return request({
    url: `/base/alternate/guard/deleted/${id}`,
    method: 'post'
  })
}
