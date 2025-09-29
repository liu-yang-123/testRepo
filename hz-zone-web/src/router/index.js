import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

/* Layout */
import Layout from '@/layout'

/* Router Modules */
import deviceRouter from './modules/device'
import departmentRouter from './modules/department'
import employeeRouter from './modules/employee'
import vehicleRouter from './modules/vehicle'
import baseRouter from './modules/base'
import pdaRouter from './modules/pda'
import bankBoxRouter from './modules/bankBox'
import vaultRouter from './modules/vault'
import cleanRouter from './modules/clean'
import escortRouter from './modules/escort'
import clearTaskRouter from './modules/clearTask'
import dutyRouter from './modules/duty'
import bankInfoRouter from './modules/bankInfo'
import statisticRouter from './modules/statistic'
import tailBoxRouter from './modules/tailBox'
import cleanCheckRouter from './modules/cleanCheck'
import fileRouter from './modules/file'
/**
 * Note: sub-menu only appear when route children.length >= 1
 * Detail see: https://panjiachen.github.io/vue-element-admin-site/guide/essentials/router-and-nav.html
 *
 * hidden: true                   if set true, item will not show in the sidebar(default is false)
 * alwaysShow: true               if set true, will always show the root menu
 *                                if not set alwaysShow, when item has more than one children route,
 *                                it will becomes nested mode, otherwise not show the root menu
 * redirect: noRedirect           if set noRedirect will no redirect in the breadcrumb
 * name:'router-name'             the name is used by <keep-alive> (must set!!!)
 * meta : {
    roles: ['admin','editor']    control the page roles (you can set multiple roles)
    title: 'title'               the name show in sidebar and breadcrumb (recommend set)
    icon: 'svg-name'/'el-icon-x' the icon show in the sidebar
    noCache: true                if set true, the page will no be cached(default is false)
    affix: true                  if set true, the tag will affix in the tags-view
    breadcrumb: false            if set false, the item will hidden in breadcrumb(default is true)
    activeMenu: '/example/list'  if set path, the sidebar will highlight the path you set
  }
 */

/**
 * constantRoutes
 * a base page that does not have permission requirements
 * all roles can be accessed
 */
export const constantRoutes = [
  {
    path: '/redirect',
    component: Layout,
    hidden: true,
    children: [
      {
        path: '/redirect/:path(.*)',
        component: () => import('@/views/redirect/index')
      }
    ]
  },
  {
    path: '/login',
    component: () => import('@/views/login/index'),
    hidden: true
  },
  {
    path: '/404',
    component: () => import('@/views/error-page/404'),
    hidden: true
  },
  {
    path: '/401',
    component: () => import('@/views/error-page/401'),
    hidden: true
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        component: () => import('@/views/dashboard/index'),
        name: 'Dashboard',
        meta: { title: '首页', icon: 'dashboard', affix: true }
      }
    ]
  },
  {
    path: '/profile',
    component: Layout,
    redirect: '/profile/index',
    hidden: true,
    children: [
      {
        path: 'index',
        component: () => import('@/views/profile/index'),
        name: 'Profile',
        meta: { title: '个人中心', icon: 'user', noCache: true }
      }
    ]
  }
]

/**
 * asyncRoutes
 * the routes that need to be dynamically loaded based on user roles
 */
export const asyncRoutes = [
    {
    path: '/system',
    component: Layout,
    redirect: '/system/user',
    alwaysShow: true,
    name: 'System',
    meta: {
      title: '系统设置',
      icon: 'lock',
      roles: ['/base/user/list', '/base/menu/list', '/base/role/list']
    },
    children: [
      {
        path: 'menu',
        component: () => import('@/views/system/menu'),
        name: 'PagePermission',
        meta: {
          title: '菜单管理',
          roles: ['/base/menu/list'] // or you can only set roles in sub nav
        }
      },
      {
        path: 'role',
        component: () => import('@/views/system/role'),
        name: 'DirectivePermission',
        meta: {
          title: '角色管理',
          roles: ['/base/role/list']
        }
      },
      {
        path: 'user',
        component: () => import('@/views/system/user'),
        name: 'UserPermission',
        meta: {
          title: '用户管理',
          roles: ['/base/user/list']
        }
      },
      {
        path: 'dictionary',
        component: () => import('@/views/system/dictionary'),
        name: 'Dictionary',
        meta: {
          title: '数据字典',
          roles: ['/base/dictionary/list']
        }
      },
      {
        path: 'log',
        component: () => import('@/views/system/log'),
        name: 'Log',
        meta: {
          title: '操作日志',
          roles: ['/base/log/list']
        }
      },
      {
        path: 'white-list',
        component: () => import('@/views/system/white-list'),
        name: 'WhiteList',
        meta: {
          title: '客户端白名单',
          roles: ['/base/whiteList/list']
        }
      },
      {
        path: 'flow-manage',
        component: () => import('@/views/system/flow-manage'),
        name: 'FlowManage',
        meta: {
          title: '流程设置',
          roles: ['/base/workflow/list']
        }
      }
      // {
      //   path: 'menu',
      //   component: () => import('@/views/system/menu'),
      //   name: 'RolePermission',
      //   meta: {
      //     title: '菜单管理',
      //     roles: ['admin']
      //   }
      // }
    ]
  },
  /** when your routing map is too long, you can split it into small modules **/
  departmentRouter,
  employeeRouter,
  deviceRouter,
  vehicleRouter,
  baseRouter,
  pdaRouter,
  bankBoxRouter,
  dutyRouter,
  escortRouter,
  cleanRouter,
  clearTaskRouter,
  vaultRouter,
  bankInfoRouter,
  statisticRouter,
  tailBoxRouter,
  cleanCheckRouter,
  fileRouter,
  {
    path: '/form',
    component: Layout,
    meta: {
      title: '上报信息管理',
      icon: 'form',
      roles: ['/base/auth/finserviceLogin']
    },
    children: [
      {
        path: 'fill-in',
        redirect: '/dashboard',
        meta: { title: '表单填写', roles: ['/base/auth/finserviceLogin'] }
      },
      {
        path: 'data-list',
        redirect: '/dashboard',
        meta: { title: '表单数据', roles: ['/base/auth/finserviceLogin'] }
      }
      // ,
      // {
      //   path: 'document',
      //   redirect: '/dashboard',
      //   meta: { title: '上报信息管理', roles: ['/base/auth/finserviceLogin'] }
      // },
      // {
      //   path: 'information',
      //   redirect: '/dashboard',
      //   meta: { title: '总部通知管理', roles: ['/base/auth/finserviceLogin'] }
      // }
    ]
  },

  {
    path: '/file',
    component: Layout,
    redirect: 'noredirect',
    meta: {
      title: '文件收发管理',
      roles: ['/base/auth/safe/send', '/base/auth/product/send'],
      icon: 'form'
    },
    children: [
      {
        path: 'safe',
        redirect: 'noredirect',
        meta: { title: '安全保卫部文件', roles: ['/base/auth/safe/send'] },
        children: [
          {
            path: 'safe-issue',
            redirect: '/dashboard',
            meta: { title: '文件上送管理', perms: ['GET /base/auth/safe/send'] }
          },
          {
            path: 'safe-accept',
            redirect: '/dashboard',
            meta: { title: '文件接收管理', perms: ['GET /base/auth/safe/receive'] }
          }
        ]
      },
      {
        path: 'product',
        redirect: 'noredirect',
        meta: { title: '生产运营部文件', roles: ['/base/auth/product/send'] },
        children: [
          {
            path: 'product-issue',
            redirect: '/dashboard',
            meta: { title: '文件上送管理', perms: ['GET /base/auth/product/send'] }
          },
          {
            path: 'product-accept',
            redirect: '/dashboard',
            meta: { title: '文件接收管理', perms: ['GET /base/auth/product/receive'] }
          }
        ]
      }
      // {
      //   path: 'safe-information',
      //   redirect: '/dashboard',
      //   meta: { title: '安保部文件', roles: ['GET /auth/finserviceFileLogin'] }
      // },
      // {
      //   path: 'product-information',
      //   redirect: '/dashboard',
      //   meta: { title: '运营部文件', roles: ['GET /auth/finserviceFileLogin'] }
      // }
    ]
  },

  { path: '*', redirect: '/404', hidden: true }
  // {
  //   path: '/error',
  //   component: Layout,
  //   redirect: 'noRedirect',
  //   name: 'ErrorPages',
  //   meta: {
  //     title: 'Error Pages',
  //     icon: '404'
  //   },
  //   children: [
  //     {
  //       path: '401',
  //       component: () => import('@/views/error-page/401'),
  //       name: 'Page401',
  //       meta: { title: '401', noCache: true }
  //     },
  //     {
  //       path: '404',
  //       component: () => import('@/views/error-page/404'),
  //       name: 'Page404',
  //       meta: { title: '404', noCache: true }
  //     }
  //   ]
  // },
  // // 404 page must be placed at the end !!!
  // { path: '*', redirect: '/404', hidden: true }
]

export default new Router({
  // mode: 'history', // require service support
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRoutes
})

// const router = createRouter()

// Detail see: https://github.com/vuejs/vue-router/issues/1234#issuecomment-357941465
// export function resetRouter() {
//   const newRouter = createRouter()
//   router.matcher = newRouter.matcher // reset router
// }

// export default router
