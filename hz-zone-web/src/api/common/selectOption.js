import request from '@/utils/request'

export function DepartmentTree() {
  return request({
    url: '/base/department/option',
    method: 'get'
  })
}

export function RoleOption() {
  return request({
    url: '/base/role/option',
    method: 'get'
  })
}

export function jobTree() {
  return request({
    url: '/base/jobs/option',
    method: 'get'
  })
}
// 顶级部门
export function departmentOption() {
  return request({
    url: '/base/department/tops',
    method: 'get'
  })
}

export function pdaRoleOption() {
  return request({
    url: '/base/pdaRole/option',
    method: 'get'
  })
}

export function bankTree(departmentId) {
  return request({
    url: '/base/bankBox/option',
    method: 'get',
    params: {
      departmentId
    }
  })
}

// 下拉权限 银行树
export function bankClearTree(departmentId) {
  return request({
    url: '/base/bankClear/option',
    method: 'get',
    params: {
      departmentId
    }
  })
}

// 查询权限 银行树
export function treeTailBox(departmentId) {
  return request({
    url: '/base/bankBox/tree',
    method: 'get',
    params: {
      departmentId
    }
  })
}

// 下拉 银行查看 银行树
export function bankInfoClearTree() {
  return request({
    url: '/base/bankInquiry/bankClearOption',
    method: 'get'
  })
}
// 权限部门列表
export function authOption() {
  return request({
    url: '/base/department/auth',
    method: 'get'
  })
}

// atm列表
export function atmOption(departmentId) {
  return request({
    url: '/base/atm/option',
    method: 'get',
    params: {
      departmentId
    }
  })
}

// 车辆列表
export function vehicleOption(departmentId) {
  return request({
    url: '/base/vehicle/option',
    method: 'get',
    params: {
      departmentId
    }
  })
}

// 员工列表
export function employeeOption(departmentId) {
  return request({
    url: '/base/employee/namelist',
    method: 'get',
    params: {
      departmentId
    }
  })
}

// 岗位员工列表
export function jobNameOption(departmentId, jobTypes) {
  return request({
    url: '/base/employee/jobNameList',
    method: 'get',
    params: {
      departmentId,
      jobTypes
    }
  })
}

// 线路下拉选项
export function routeOption(params) {
  return request({
    url: '/base/route/option',
    method: 'get',
    params
  })
}

// 线路模板下拉选项
export function routeTemplateOption(departmentId) {
  return request({
    url: '/base/routeTemplate/listOption',
    method: 'get',
    params: {
      departmentId
    }
  })
}

// 券别下拉选项
export function denomOption() {
  return request({
    url: '/base/denom/option',
    method: 'get'
  })
}

// 顶级银行机构选项
export function bankClearTopBank(departmentId) {
  return request({
    url: '/base/bankClear/topOption',
    method: 'get',
    params: {
      departmentId
    }
  })
}

// 权限部门树
export function authTree() {
  return request({
    url: '/base/department/authOption',
    method: 'get'
  })
}

// 休息计划
export function vacationOption(departmentId, planType) {
  return request({
    url: '/base/vacation/plan/all',
    method: 'get',
    params: {
      departmentId,
      planType
    }
  })
}

// 银行获取网点
export function subOption(departmentId, bankId) {
  return request({
    url: '/base/bankClear/subOption',
    method: 'get',
    params: {
      departmentId,
      bankId
    }
  })
}

export function cancelOption(departmentId, taskDate) {
  return request({
    url: '/base/atmTask/cancelOption',
    method: 'get',
    params: {
      departmentId,
      taskDate
    }
  })
}

export function moveOption(id) {
  return request({
    url: `/base/atmTask/moveOption/${id}`,
    method: 'get'
  })
}

export function routeTree(params) {
  return request({
    url: '/base/atmTask/routeOption',
    method: 'get',
    params
  })
}

export function bankInfoRouteTree(params) {
  return request({
    url: '/base/bankInquiry/routeOption',
    method: 'get',
    params
  })
}

export function routeTaskList(routeId) {
  return request({
    url: `/base/atmTask/getRouteTaskList/${routeId}`,
    method: 'get'
  })
}

// 银行 获取银行网点
export function subBankOption() {
  return request({
    url: '/base/bankInquiry/subBankOption',
    method: 'get'
  })
}

// 获取尾箱网点
export function bankOption(departmentId) {
  return request({
    url: '/base/bankBox/option',
    method: 'get',
    params: {
      departmentId
    }
  })
}

// 清分清点 -> 银行网点
export function bankTradeOption(departmentId, businessMode) {
  return request({
    url: '/base/bankTrade/option',
    method: 'get',
    params: {
      departmentId,
      businessMode
    }
  })
}

// 清分清点 -> 结算标准
export function clearDenomOption() {
  return request({
    url: '/base/clear/charge/rule/denomOption',
    method: 'post'
  })
}

// 文件管理 -> 操作员
export function fileCompanyUserOption() {
  return request({
    url: '/base/user/option',
    method: 'get'
  })
}

// 文件管理 -> 单位
export function fileCompanyOption() {
  return request({
    url: '/base/fileCompany/option',
    method: 'get'
  })
}

// 用户管理 -> 所有部门网点
export function allBankOption() {
  return request({
    url: '/base/bankClear/allOption',
    method: 'get'
  })
}
