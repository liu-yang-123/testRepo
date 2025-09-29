import request from '@/utils/request'
import requestPrint from '@/utils/requestPrint'

export function listBankBox(params) {
  return request({
    url: '/base/cashbox/list',
    method: 'get',
    params
  })
}

export function unbindBankBox(id) {
  return request({
    url: `/base/cashbox/unbind/${id}`,
    method: 'post'
  })
}

export function stopBankBox(id) {
  return request({
    url: `/base/cashbox/stop/${id}`,
    method: 'post'
  })
}

export function printBox(data) {
  return requestPrint({
    // url: '/printer/boxBarCode/multi',
    url: '/printer/boxQrCode/multi',
    method: 'post',
    data
  })
}

export function cashboxUsed(boxIds) {
  return request({
    url: '/base/cashbox/used',
    method: 'post',
    data: {
      boxIds
    }
  })
}

export function cashboxProduce(params) {
  return request({
    url: '/base/cashbox/produce',
    method: 'post',
    params
  })
}

export function bindBox(data) {
  return request({
    url: '/base/cashbox/bind',
    method: 'post',
    data
  })
}
