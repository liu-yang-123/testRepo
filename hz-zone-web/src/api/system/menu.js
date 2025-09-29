import request from '@/utils/request'

export function listMenu() {
  return request({
    url: '/base/menu/list',
    method: 'get'
  })
}

export function createMenu(data) {
  return request({
    url: '/base/menu/save',
    method: 'post',
    data
  })
}

export function deleteMenu(data) {
  return request({
    url: '/base/menu/delete/' + data.id,
    method: 'post'
  })
}

export function updateMenu(data) {
  return request({
    url: '/base/menu/update',
    method: 'post',
    data
  })
}
