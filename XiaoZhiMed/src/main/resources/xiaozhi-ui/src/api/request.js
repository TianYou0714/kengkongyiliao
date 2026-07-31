import axios from 'axios'
import { ElMessage } from 'element-plus'

// 统一 axios 实例，/api 前缀由 vite 代理转发到后端 8080
const request = axios.create({
  baseURL: '/api',
  timeout: 15000,
})

request.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res && typeof res === 'object' && 'code' in res) {
      if (res.code !== 200) {
        ElMessage.error(res.msg || '操作失败')
        return Promise.reject(new Error(res.msg || 'Error'))
      }
      return res
    }
    return res
  },
  (error) => {
    ElMessage.error(error.message || '网络异常，请稍后重试')
    return Promise.reject(error)
  },
)

export default request
