export function downloadFile(fn, params, title, finCallback, suffix = '.xls') {
  fn(params).then(res => {
    const filename = title + suffix
    const fileUrl = window.URL.createObjectURL(new Blob([res.data]))
    const fileLink = document.createElement('a')
    fileLink.href = fileUrl
    fileLink.setAttribute('download', filename)
    document.body.appendChild(fileLink)
    fileLink.click()
  }).finally(finCallback)
}
