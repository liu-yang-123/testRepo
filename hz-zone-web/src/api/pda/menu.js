import request from '@/utils/request'

export function listMenu() {
  return request({
    url: '/base/pdaMenu/list',
    method: 'get'
  })
}

export function createMenu(data) {
  return request({
    url: '/base/pdaMenu/save',
    method: 'post',
    data
  })
}

export function deleteMenu(data) {
  return request({
    url: '/base/pdaMenu/delete/' + data.id,
    method: 'post'
  })
}

export function updateMenu(data) {
  return request({
    url: '/base/pdaMenu/update',
    method: 'post',
    data
  })
}
