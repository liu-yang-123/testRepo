import request from '@/utils/request'

export function listDictionary(params) {
  return request({
    url: '/base/dictionary/list',
    method: 'get',
    params
  })
}

export function addDictionary(data) {
  return request({
    url: '/base/dictionary/save',
    method: 'post',
    data
  })
}

export function updateDictionary(data) {
  return request({
    url: '/base/dictionary/update',
    method: 'post',
    data
  })
}

export function delectDictionary(id) {
  return request({
    url: `/base/dictionary/delete/${id}`,
    method: 'post'
  })
}

export function reqDictionary(groups) {
  return request({
    url: '/base/dictionary/group',
    method: 'post',
    params: {
      groups
    }
  })
}

export const dictionaryData = {
  // 性别
  'GENDER': [
    { code: 0, content: '男' },
    { code: 1, content: '女' }
  ],
  // 婚姻状况
  'MARITAL_STATUS': [
    { code: 0, content: '未婚' },
    { code: 1, content: '已婚' },
    { code: 2, content: '丧偶' },
    { code: 3, content: '离婚' }
  ],
  // 学历
  'EDUCATION': [
    { code: '0', content: '本科' },
    { code: '1', content: '大专' },
    { code: '2', content: '中专' },
    { code: '3', content: '高中' },
    { code: '4', content: '初中' },
    { code: '5', content: '小学' },
    { code: '6', content: '硕士' },
    { code: '7', content: '博士' },
    { code: '8', content: '其它' }
  ],
  // 政治面貌
  'POLITICAL_OUTLOOK': [
    { code: '0', content: '群众' },
    { code: '1', content: '中共党员' },
    { code: '2', content: '中共预备党员' },
    { code: '3', content: '共青团员' },
    /*
    {code: '4', content: '民革党员'},
    {code: '5', content: '民盟盟员'},
    {code: '6', content: '民建会员'},
    {code: '7', content: '民进会员'},
    {code: '8', content: '农工党党员'},
    {code: '9', content: '致公党党员'},
    {code: '10', content: '九三学社社员'},
    {code: '11', content: '台盟盟员'},
    {code: '12', content: '无党派人士'},
    */
    { code: '13', content: '其它' }
  ],
  // 兵役状况
  'MILITARY_SERVICE_STATUS': [
    { code: 0, content: '未服兵役' },
    { code: 1, content: '退役' },
    { code: 2, content: '预备役' },
    { code: 3, content: '现役' }
  ],
  // 民族
  'NATION': [
    '汉族', '满族', '蒙古族', '回族', '藏族', '维吾尔族', '苗族', '彝族', '壮族', '布依族', '侗族', '瑶族', '白族', '土家族', '哈尼族', '哈萨克族',
    '傣族', '黎族', '傈僳族', '佤族', '畲族', '高山族', '拉祜族', '水族', '东乡族', '纳西族', '景颇族', '柯尔克孜族', '土族', '达斡尔族',
    '仫佬族', '羌族', '布朗族', '撒拉族', '毛南族', '仡佬族', '锡伯族', '阿昌族', '普米族', '朝鲜族', '塔吉克族', '怒族', '乌孜别克族', '俄罗斯族',
    '鄂温克族', '德昂族', '保安族', '裕固族', '京族', '塔塔尔族', '独龙族', '鄂伦春族', '赫哲族', '门巴族', '珞巴族', '基诺族', '其它'
  ],
  // 设备状态
  'DEVICE_STATUS': [
    { code: 0, content: '正常使用' },
    { code: 1, content: '维修中' },
    { code: 2, content: '报废' }
  ],
  // 设备维保结果
  'DEV_MAINTAIN_RESULT': [
    { code: 1, content: '维修成功' },
    { code: 0, content: '维修失败' }
  ],
  // 员工在职状态
  'EMPLOYEE_STATUS': [
    { code: 0, content: '在职' },
    { code: 1, content: '离职' }
  ],
  // 考勤打卡类别
  'CLOCKIN_TYPE': [
    { code: 1, content: '补卡' },
    { code: 2, content: '打卡' }
  ],
  // 培训类型
  'TRAIN_TYPE': [
    { code: 'A', content: '类型A' },
    { code: 'B', content: '类型B' }
  ],
  // 奖惩类型
  'AWARDS_TYPE': [
    { code: 'G1', content: '表扬' },
    { code: 'G2', content: '发放奖金' },
    { code: 'B1', content: '批评' },
    { code: 'B2', content: '扣发奖金' },
    { code: 'B3', content: '记过' },
    { code: 'B4', content: '开除' }
  ],
  // 清分批次状态
  'CLEAR_BATCH_STATUS': [
    { code: '0', content: '未完成' },
    { code: '1', content: '已完成' }
  ],
  // 清分任务状态
  'CLEAR_TASK_STATUS': [
    { code: 0, content: '正在创建' },
    { code: 1, content: '等待出库' },
    { code: 2, content: '正在清分' },
    { code: 3, content: '出库撤销中' },
    { code: 4, content: '等待入库' },
    { code: 5, content: '任务完成' },
    { code: 6, content: '入库撤销中' }
  ],
  // 清分差错状态
  'CLEAR_ERROR_STATUS': [
    { code: 0, content: '待确认' },
    { code: 1, content: '审核拒绝' },
    { code: 2, content: '已确认' },
    { code: 3, content: '审核通过' },
    { code: 4, content: '撤销中' },
    { code: 5, content: '已撤销' }
  ],
  // 清分差错状态（）
  'CLEAR_ERROR_STATUS_WH': [
    { code: 2, content: '待审核' },
    { code: 3, content: '已通过' },
    { code: 1, content: '已拒绝' },
    { code: 4, content: '撤销中' },
    { code: 5, content: '已撤销' }
  ],
  // 清分出库/入库审核状态
  'CLEAR_STOCK_STATUS': [
    { code: 1, content: '待审核' },
    { code: 2, content: '已拒绝' },
    { code: 3, content: '已通过' }
  ],
  // 清分出库/入库审核结果
  'CLEAR_STOCK_CHECK': [
    { code: '1', content: '通过' },
    { code: '0', content: '拒绝' }
  ],
  // 缴款出库类型
  'CLEAR_OUT_TYPE': [
    { code: 1, content: '代缴人行发行库' },
    { code: 2, content: '商业银行自提' }
  ],
  // 反馈-问题分类
  'CLEAR_FEEDBACK_TYPE': [
    { code: '1', content: '类型1' },
    { code: '2', content: '类型2' }
  ],
  // 反馈-响应级别
  'CLEAR_FEEDBACK_LEVEL': [
    { code: '0', content: '一般' },
    { code: '1', content: '严重' },
    { code: '2', content: '紧急' }
  ],
  // 反馈-问题分类
  'CLEAR_FEEDBACK_STATUS': [
    { code: 0, content: '未处理' },
    { code: 1, content: '已处理' }
  ],
  // 清分任务状态
  'CLEAR_TASK_STATUS_2': [
    { code: 0, content: '未完成' },
    { code: 1, content: '已完成' }
  ],
  // 清分出库状态
  'CLEAR_TASK_OUT_STATUS_2': [
    { code: 0, content: '正在创建' },
    { code: 1, content: '等待出库' },
    { code: 2, content: '出库完成' },
    { code: 3, content: '申请撤销' },
    { code: 4, content: '撤销成功' }
  ],
  // 清分入库状态
  'CLEAR_TASK_IN_STATUS_2': [
    { code: 0, content: '正在创建' },
    { code: 1, content: '等待入库' },
    { code: 2, content: '入库完成' },
    { code: 3, content: '申请撤销' },
    { code: 4, content: '撤销成功' }
  ],
  // 纸硬币类型
  'CURRENCY_COIN_PAPER': [
    { code: 'P', content: '纸币' },
    { code: 'C', content: '硬币' }
  ],
  // 人员类型
  'PERSONNEL_TYPE': [
    { code: 0, content: '员工' },
    { code: 1, content: '柜员' }
  ],
  // 维修结果
  'MAINTENANCE_RESULT': [
    { code: 1, content: '维修失败' },
    { code: 0, content: '维修成功' }
  ],
  // 清机任务状态
  'CLEAN_STATUS': [
    { code: -1, content: '<span style="color:#d12e22">已取消</span>' },
    { code: 0, content: '<span style="color:#dfe000">已创建</span>' },
    { code: 1, content: '<span style="color:skyblue">待执行</span>' },
    { code: 2, content: '<span style="color:green">已完成</span>' }
  ]
}
