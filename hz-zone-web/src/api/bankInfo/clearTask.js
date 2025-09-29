import request from '@/utils/request'

// 列表数据
export function listTask(data) {
  return request({
    url: '/base/bankInquiry/clearTaskList',
    method: 'get',
    params: data
  })
}

// 详情
export function detailTask(taskId) {
  return request({
    url: '/base/bankInquiry/clearTaskDetail',
    method: 'get',
    params: {
      taskId
    }
  })
}
