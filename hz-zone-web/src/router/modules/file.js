import Layout from '@/layout'

const fileRouter = {
  path: '/file-manage',
  component: Layout,
  redirect: 'noRedirect',
  name: 'fileManage',
  meta: {
    title: '文件管理',
    icon: 'file',
    roles: ['/base/fileCompany/list', '/base/fileRecord/sendList']
  },
  children: [
    {
      path: 'file-company',
      component: () => import('@/views/file/file-company'),
      name: 'fileCompany',
      meta: {
        title: '单位管理',
        roles: ['/base/fileCompany/list']
      }
    },
    {
      path: 'file-send',
      component: () => import('@/views/file/file-send'),
      name: 'fileSend',
      meta: {
        title: '上传文件',
        roles: ['/base/fileRecord/sendList']
      }
    },
    {
      path: 'file-receive',
      component: () => import('@/views/file/file-receive'),
      name: 'fileReceive',
      meta: {
        title: '接收文件',
        roles: ['/base/fileRecord/receiveList']
      }
    },
    {
      path: 'file-manage',
      component: () => import('@/views/file/file-manage'),
      name: 'fileManage',
      meta: {
        title: '文件管理',
        roles: ['/base/fileRecord/all']
      }
    }
  ]
}

export default fileRouter
