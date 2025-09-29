import request from '@/utils/request'

export function treeCleanBox() {
  return request({
    url: '/base/bankInquiry/bankClearTree',
    method: 'get'
  })
}

export function detailCleanBox(id) {
  return request({
    url: `/base/bankInquiry/bankClearInfo/${id}`,
    method: 'post'
  })
}
