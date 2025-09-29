import Vue from 'vue'

import Cookies from 'js-cookie'

import 'normalize.css/normalize.css' // a modern alternative to CSS resets

import Element from 'element-ui'
import './styles/element-variables.scss'

import '@/styles/index.scss' // global css

import App from './App'
import store from './store'
import router from './router'

import './icons' // icon
import './permission' // permission control
import './utils/error-log' // error log

import * as filters from './filters' // global filters

import permission from '@/directive/permission/index.js' // 权限判断指令

// import axios from 'axios'

import { message } from './utils/resetMessage.js'
import axios from 'axios'

Vue.directive('permission', permission)

/**
 * If you don't want to use mock-server
 * you want to use MockJs for mock api
 * you can execute: mockXHR()
 *
 * Currently MockJs will be used in the production environment,
 * please remove it before going online ! ! !
 */
// if (process.env.NODE_ENV === 'production') {
//   const { mockXHR } = require('../mock')
//   mockXHR()
// }

// const ws = new WebSocket('ws://127.0.0.1:8199/')
//axios.get('http://127.0.0.1:8199/').then(res => {
//  store.dispatch('user/getMac', res.data.text)
//  init()
//}).catch(() => {
//  init()
//})

// ws.onopen = () => {
//   ws.send('{"status": "0"}')
// }

// ws.onerror = () => {
//   init()
// }

// ws.onmessage = res => {
//   store.dispatch('user/getMac', JSON.parse(res.data).text)
//   init()
// }

init()

function init() {
  Vue.use(Element, {
    size: Cookies.get('size') || 'medium' // set element-ui default size
  })

  Vue.prototype.$message = message

  // register global utility filters
  Object.keys(filters).forEach(key => {
    Vue.filter(key, filters[key])
  })

  Vue.config.productionTip = false

  new Vue({
    el: '#app',
    router,
    store,
    render: h => h(App)
  })
}
// axios.get('./config.json').then(res => {
//   // axios.defaults.baseURL = res.data.baseURL

// })

