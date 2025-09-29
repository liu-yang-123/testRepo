import request from '@/utils/request'

export function listRoute(params) {
  return request({
    url: '/base/route/list',
    method: 'get',
    params
  })
}

export function addRoute(data) {
  return request({
    url: '/base/route/save',
    method: 'post',
    data
  })
}

export function deleteRoute(id) {
  return request({
    url: `/base/route/delete/${id}`,
    method: 'post'
  })
}

export function updateRoute(data) {
  return request({
    url: '/base/route/update',
    method: 'post',
    data
  })
}

export function changeRoute(data) {
  return request({
    url: '/base/route/edit',
    method: 'post',
    data
  })
}

export function copyRoute(data) {
  return request({
    url: '/base/route/copy',
    method: 'post',
    data
  })
}

export function overRoute(id) {
  return request({
    url: `/base/route/over/${id}`,
    method: 'post'
  })
}

// 线路推送
export function alertRoute(ids) {
  return request({
    url: '/base/route/push/notice',
    method: 'post',
    data: {
      ids
    }
  })
}

export function confirmRoute(id) {
  return request({
    url: `/base/route/confirm/${id}`,
    method: 'post'
  })
}

export function routeEmpChange(id) {
  return request({
    url: `/base/route/empChange/${id}`,
    method: 'get'
  })
}

export function atmTaskInfo(id) {
  return request({
    url: `/base/atmTask/info/${id}`,
    method: 'post'
  })
}

export function cashboxPack(id) {
  return request({
    url: `/base/atmTask/cashboxPack/${id}`,
    method: 'get'
  })
}

export function detailLeaderLog(routeId) {
  return request({
    url: '/base/route/leaderLog',
    method: 'post',
    params: {
      routeId
    }
  })
}

export function detailDispatch(routeId) {
  return request({
    url: '/base/route/dispatch',
    method: 'post',
    params: {
      routeId
    }
  })
}

export function handoverRoute(data) {
  return request({
    url: '/base/route/editHandover',
    method: 'post',
    data
  })
}

export function detailHandover(id) {
  return request({
    url: `/base/route/handoverChange/${id}`,
    method: 'get'
  })
}
