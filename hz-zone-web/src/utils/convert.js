import { Decimal } from 'decimal.js'

// 数组转换成以separator分隔的字符串
export function listToString(list, separator) {
  if (!list || list.length < 1) { return '' }
  if (!separator) { separator = ',' }
  let listString = ''
  for (let i = 0; i < list.length; i++) {
    listString += list[i]
    if (i < list.length - 1) { listString += separator }
  }
  return listString
}

// 以separator分割，字符串转换成数组，convertToInt决定是否转换成int类型的数组
export function stringToList(listString, separator, convertToInt) {
  if (!listString) { return [] }
  if (!separator) { separator = ',' }
  const list = listString.split(separator)
  if (convertToInt && convertToInt === true) {
    const intList = []
    for (let i = 0; i < list.length; i++) {
      intList.push(parseInt(list[i]))
    }
    return intList
  }
  return list
}

// 格式化金额（千分位）
export function formatMoney(amount) {
  const n = 2
  amount = parseFloat((amount + '').replace(/[^\d\.-]/g, '')).toFixed(n) + ''
  const l = amount.split('.')[0].split('')
  const r = amount.split('.')[1]
  let t = ''
  for (let i = 0; i < l.length; i++) {
    t += l[i]
  }
  t = parseFloat(t)
  return t.toLocaleString() + '.' + r
}

// 格式化金额（千分位）
export function formatTenThousand(amount) {
  if (!amount) { return '-' }
  let num = new Decimal(amount)
  num = num.div(10000)
  const numStr = num.toString()
  if (numStr.indexOf('.') > -1) {
    const l = numStr.split('.')[0]
    const r = numStr.split('.')[1]
    return parseInt(l).toLocaleString() + '.' + r
  } else {
    return num.toNumber().toLocaleString()
  }
}

// 格式化金额（千分位）
export function formatTenThousandWithSuffix(amount) {
  if (!amount) { return '-' }
  let num = new Decimal(amount)
  num = num.div(10000)
  const numStr = num.toString()
  if (numStr.indexOf('.') > -1) {
    const l = numStr.split('.')[0]
    const r = numStr.split('.')[1]
    return parseInt(l).toLocaleString() + '.' + r + (r.length < 2 ? '0' : '')
  } else {
    return num.toNumber().toLocaleString() + '.00'
  }
}

export function convertKbz(money, denomValue, wadSize, bundleSize) {
  try {
    money = parseFloat(money)
    denomValue = parseFloat(denomValue)
  } catch (e) {
    return '-'
  }
  let k = 0; let b = 0; let z = 0
  z = Math.floor(money / denomValue)
  // b = Math.floor(z / 100)
  // k = Math.floor(b / 10)
  // z %= 100
  // b %= 10
  b = Math.floor(z / wadSize)
  k = Math.floor(b / bundleSize)
  z %= wadSize
  b %= bundleSize
  return (k !== 0 ? (k + '捆') : '') + (b !== 0 ? (b + '把') : '') + (z !== 0 || (k === 0 && b === 0) ? (z + '张') : '')
}

// 格式化金额（大写）
export function EACurrency(money) {
  if (!money) {
    money = '0'
  }
  // 汉字的数字
  const cnNums = ['零', '壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖']
  // 基本单位
  const cnIntRadice = ['', '拾', '佰', '仟']
  // 对应整数部分扩展单位
  const cnIntUnits = ['', '万', '亿', '兆']
  // 对应小数部分单位
  const cnDecUnits = ['角', '分', '毫', '厘']
  // 整数金额时后面跟的字符
  const cnInteger = '整'
  // 整型完以后的单位
  const cnIntLast = '元'
  // 最大处理的数字
  const maxNum = 999999999999999.9999
  // 金额整数部分
  let integerNum
  // 金额小数部分
  let decimalNum
  // 输出的中文金额字符串
  let chineseStr = ''
  // 分离金额后用的数组，预定义
  let parts
  if (money === '') {
    return ''
  }
  money = parseFloat(money)
  if (money >= maxNum) {
    // 超出最大处理数字
    return ''
  }
  if (money === 0) {
    chineseStr = cnNums[0] + cnIntLast + cnInteger
    return chineseStr
  }
  // 转换为字符串
  money = money.toString()
  if (money.indexOf('.') === -1) {
    integerNum = money
    decimalNum = ''
  } else {
    parts = money.split('.')
    integerNum = parts[0]
    decimalNum = parts[1].substr(0, 4)
  }
  // 获取整型部分转换
  if (parseInt(integerNum, 10) > 0) {
    let zeroCount = 0
    const IntLen = integerNum.length
    for (let i = 0; i < IntLen; i++) {
      const n = integerNum.substr(i, 1)
      const p = IntLen - i - 1
      const q = p / 4
      const m = p % 4
      if (n === '0') {
        zeroCount++
      } else {
        if (zeroCount > 0) {
          chineseStr += cnNums[0]
        }
        // 归零
        zeroCount = 0
        chineseStr += cnNums[parseInt(n)] + cnIntRadice[m]
      }
      if (m === 0 && zeroCount < 4) {
        chineseStr += cnIntUnits[q]
      }
    }
    chineseStr += cnIntLast
  }
  // 小数部分
  if (decimalNum !== '') {
    const decLen = decimalNum.length
    for (let i = 0; i < decLen; i++) {
      const n = decimalNum.substr(i, 1)
      if (n !== '0') {
        chineseStr += cnNums[Number(n)] + cnDecUnits[i]
      }
    }
  }
  if (chineseStr === '') {
    chineseStr += cnNums[0] + cnIntLast + cnInteger
  } else if (decimalNum === '') {
    chineseStr += cnInteger
  }
  return chineseStr
}
