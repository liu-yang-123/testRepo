import axios from 'axios'
import { getToken } from '@/utils/auth'
import store from '@/store'

export function downLoadFile(url, params, _cb, _catchcb, baseURL = process.env.VUE_APP_BASE_API) {
  return axios.create({
    baseURL
  }).get(url, {
    headers: {
      'Content-Type': 'application/json',
      'X-Token': getToken(),
      'X-mac': store.getters.mac
    },
    responseType: 'blob',
    params
  }).then(_cb).catch(_catchcb)
}
