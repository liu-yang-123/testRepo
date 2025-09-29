import request from '@/utils/request'

// 列表数据
export function listTask(data) {
  return request({
    url: '/base/taskReturn/list',
    method: 'get',
    params: data
  })
}
