import request from '@/utils/request.ts'

// 登录参数类型
export interface UserLoginDTO {
    username: string
    password: string
    authority: string
}

// 登录响应类型
export interface UserLoginVO {
    id: bigint
    username: string
    nickname: string
    authorityId: number
    token: string;
}
// 注册参数类型
export interface UserRegisterDTO {
    username: string
    password: string
    nickname: string
    phone: string
}
// API 响应类型
export interface Result<T = any> {
    code: number
    msg: string
    data: T
}

// 登录接口
export const loginAPI = (userData: UserLoginDTO): Promise<Result<UserLoginVO>> => {
    return request.post('/users/login', userData)
}

// 登录接口
export const registerAPI = (userData: UserRegisterDTO): Promise<Result> => {
    return request.post('/users/register', userData)
}