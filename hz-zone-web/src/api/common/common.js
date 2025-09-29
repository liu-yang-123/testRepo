
import request from '@/utils/request'

export function getImage(fileUrl) {
  return request({
    url: '/base/common/showImg',
    method: 'post',
    params: {
      fileUrl
    }
  })
}

export const uploadPath = process.env.VUE_APP_BASE_API + '/base/common/fileUpload'
