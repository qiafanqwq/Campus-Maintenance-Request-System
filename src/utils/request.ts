import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'
import type { AxiosRequestConfig } from 'axios'

const request = axios.create({
    baseURL: '/api',
    timeout: 10000,
    headers: {
        'Content-Type': 'application/json'
    }
})

// 日期时间格式化函数
const formatDateTimeForBackend = (obj: any): any => {
    if (obj === null || obj === undefined) {
        return obj
    }

    if (obj instanceof Date) {
        const year = obj.getFullYear()
        const month = String(obj.getMonth() + 1).padStart(2, '0')
        const day = String(obj.getDate()).padStart(2, '0')
        const hours = String(obj.getHours()).padStart(2, '0')
        const minutes = String(obj.getMinutes()).padStart(2, '0')
        const seconds = String(obj.getSeconds()).padStart(2, '0')
        return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
    }

    if (typeof obj === 'string') {
        // 检查是否是ISO格式的日期时间字符串
        const isoDateTimeRegex = /^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}/
        if (isoDateTimeRegex.test(obj)) {
            const date = new Date(obj)
            const year = date.getFullYear()
            const month = String(date.getMonth() + 1).padStart(2, '0')
            const day = String(date.getDate()).padStart(2, '0')
            const hours = String(date.getHours()).padStart(2, '0')
            const minutes = String(date.getMinutes()).padStart(2, '0')
            const seconds = String(date.getSeconds()).padStart(2, '0')
            return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
        }
        return obj
    }

    if (Array.isArray(obj)) {
        return obj.map(item => formatDateTimeForBackend(item))
    }

    if (typeof obj === 'object') {
        const result: any = {}
        for (const key in obj) {
            if (obj.hasOwnProperty(key)) {
                result[key] = formatDateTimeForBackend(obj[key])
            }
        }
        return result
    }

    return obj
}

// 获取token的函数
const getToken = (): string | null => {
    return localStorage.getItem('token')
}

// 请求拦截器
request.interceptors.request.use(
    (config: AxiosRequestConfig) => {
        // 添加token到请求头
        const token = getToken()
        if (token && config.headers) {
            config.headers['token'] = token
        }

        // 格式化请求数据中的日期时间
        if (config.data) {
            config.data = formatDateTimeForBackend(config.data)
        }

        if (config.params) {
            config.params = formatDateTimeForBackend(config.params)
        }

        return config
    },
    (error) => {
        return Promise.reject(error)
    }
)


// 简化响应拦截器 - 只处理网络错误，业务错误由具体页面处理
request.interceptors.response.use(
    (response) => {
        return response.data
    },
    (error) => {
        if (error.response?.status === 401) {
            localStorage.removeItem('token')
            localStorage.removeItem('userInfo')
            ElMessage.error('登录已过期，请重新登录')
            // 清除本地存储的token和用户信息
            localStorage.removeItem('token')
            localStorage.removeItem('userInfo')
            // 跳转到登录页
            router.push('/login')
        } else if (!error.response) {   // 只处理网络层面的错误
            ElMessage.error('网络连接失败，请检查网络')
        } else if (error.response.status === 500) {
            ElMessage.error('服务器内部错误，请稍后重试')
        }

        return Promise.reject(error)
    }
)

export default request