import request from '@/utils/request'

export function bankOption(departmentId) {
  return request({
    url: '/base/bankBox/option',
    method: 'get',
    params: {
      departmentId
    }
  })
}

export function listEmp(params) {
  return request({
    url: '/base/bankTeller/list',
    method: 'get',
    params
  })
}

export function addEmp(data) {
  return request({
    url: '/base/bankTeller/save',
    method: 'post',
    data
  })
}

export function updateEmp(data) {
  return request({
    url: '/base/bankTeller/update',
    method: 'post',
    data
  })
}

export function deleteEmp(id) {
  return request({
    url: `/base/bankTeller/delete/${id}`,
    method: 'post'
  })
}

// 离职
export function quitEmp(id) {
  return request({
    url: `/base/bankTeller/quit/${id}`,
    method: 'post'
  })
}

// 复岗
export function backEmp(id) {
  return request({
    url: `/base/bankTeller/back/${id}`,
    method: 'post'
  })
}

// 重置
export function resetEmp(id) {
  return request({
    url: `/base/bankTeller/resetpwd/${id}`,
    method: 'post'
  })
}
