import request from '@/utils/request'

export function listTask(data) {
  return request({
    url: '/base/trade/clearTask/list',
    method: 'post',
    data
  })
}

export function addTask(data) {
  return request({
    url: '/base/trade/clearTask/save',
    method: 'post',
    data
  })
}

export function updateTask(data) {
  return request({
    url: '/base/trade/clearTask/update',
    method: 'post',
    data
  })
}

export function deleteTask(data) {
  return request({
    url: `/base/trade/clearTask/delete/${data.id}`,
    method: 'post'
  })
}

export function detailTask(taskId) {
  return request({
    url: '/base/trade/clearTask/detail',
    method: 'get',
    params: {
      taskId
    }
  })
}

export function finishTask(data) {
  return request({
    url: '/base/trade/clearTask/finish',
    method: 'post',
    data
  })
}

export function confirmTask(data) {
  return request({
    url: `/base/trade/clearTask/confirm/${data.id}`,
    method: 'post'
  })
}

export function resultTask(taskId) {
  return request({
    url: '/base/trade/clearTask/resultDetail',
    method: 'get',
    params: {
      taskId
    }
  })
}

export function cancelTask(data) {
  return request({
    url: `/base/trade/clearTask/cancel/${data.id}`,
    method: 'post'
  })
}
