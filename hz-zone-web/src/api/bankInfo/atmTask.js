import request from '@/utils/request'

export function listATMTask(id) {
  return request({
    url: `/base/bankInquiry/atmTaskList/${id}`,
    method: 'get'
  })
}

export function detailATMTask(atmTaskId) {
  return request({
    url: `/base/bankInquiry/atmTaskInfo/${atmTaskId}`,
    method: 'post'
  })
}
