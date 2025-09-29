import request from '@/utils/request'
import requestBlob from '@/utils/requestBlob'

export function listTaskRecord(params) {
  return request({
    url: '/base/atmTask/record',
    method: 'get',
    params
  })
}

export function exportRecord(params) {
  return requestBlob({
    url: '/base/atmTask/exportRecord',
    method: 'get',
    params
  })
}
