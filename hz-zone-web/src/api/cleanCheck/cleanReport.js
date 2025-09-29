import request from '@/utils/request'

export function listClearReport(data) {
  return request({
    url: '/base/clearReport/calcFee',
    method: 'post',
    data
  })
}

