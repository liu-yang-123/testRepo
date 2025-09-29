import request from '@/utils/request'

export function listHoliday(params) {
  return request({
    url: '/base/holiday/list',
    method: 'get',
    params
  })
}

export function addHoliday(data) {
  return request({
    url: '/base/holiday/save',
    method: 'post',
    data
  })
}

export function updateHoliday(data) {
  return request({
    url: '/base/holiday/update',
    method: 'post',
    data
  })
}

export function deleteHoliday(id) {
  return request({
    url: `/base/holiday/delete/${id}`,
    method: 'post'
  })
}
