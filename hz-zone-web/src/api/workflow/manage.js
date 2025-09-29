import request from '@/utils/request'

// 列表数据
export function listWorkflow(params) {
  return request({
    url: '/base/workflow/list',
    method: 'get',
    params
  })
}
// 修改
export function updateWorkflow(data) {
  return request({
    url: '/base/workflow/update',
    method: 'post',
    data
  })
}
