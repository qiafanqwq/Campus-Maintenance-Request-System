import request from '@/utils/request.ts'
//用户参数类型
export interface UserDTO {
    id?: number
    username: string
    nickname: string
    phone: string
    email: string
    AuthorityId?: number
}
//用户响应类型
export interface UserVO {
    id: number
    username: string
    nickname: string
    phone: string
    email: string
    status?: number
    AuthorityId?: number
}
// 查询用户参数类型
export interface UserPageQueryDTO {
    nickname: string
    page: number
    pageSize: number
}
export interface UserPwdDTO {
    id: number
    oldPassword: string
    newPassword: string
}

// 查询用户响应类型
export interface PageResult {
    total: number
    records: UserVO[]
}

// API 响应类型
export interface Result<T = any> {
    code: number
    msg: string
    data: T
}

// 查询管理员接口
export const pageAdminAPI = (params: UserPageQueryDTO): Promise<Result<PageResult>> => {
    return request.get('/admin/userCenter/pageAdmin', {params})
}

// 查询普通用户接口
export const pageUserAPI = (params: UserPageQueryDTO): Promise<Result<PageResult>> => {
    return request.get('/admin/userCenter/pageUser', {params})
}

// 查询维修人员接口
export const pageRepairmanAPI = (params: UserPageQueryDTO): Promise<Result<PageResult>> => {
    return request.get('/admin/userCenter/pageRepairman', {params})
}

//更新普通用户信息接口(管理端)
export const updateUserAPI1 = ( UserDTO :UserDTO): Promise<Result> => {
    return request.put('/admin/userCenter/editNormalUser', UserDTO)
}
//更新管理员或维修人员信息接口(管理端)
export const updateUserAPI2 = ( UserDTO :UserDTO): Promise<Result> => {
    return request.put('/admin/userCenter/editAdminOrRepairman', UserDTO)
}


//改变普通用户账户状态接口
export const starOrStopNormalUserAPI = ( id: number, status:number ): Promise<Result> => {
    return request.put(`/admin/userCenter/status1/${status}`,null, {params: {id}} )
}
//改变管理员或维修人员账户状态接口
export const starOrStopAdminOrRepairmanAPI = ( id: number, status:number ): Promise<Result> => {
    return request.put(`/admin/userCenter/status2/${status}`,null, {params: {id}} )
}


//添加普通用户接口
export const addUserAPI1 = ( UserDTO: UserDTO ): Promise<Result> => {
    return request.post(`/admin/userCenter/addNormalUser`, UserDTO)
}
//添加管理员或维修人员接口
export const addUserAPI2 = ( UserDTO: UserDTO ): Promise<Result> => {
    return request.post(`/admin/userCenter/addAdminOrRepairman`, UserDTO)
}


//列举维修人员
export const listRepairmanAPI = (): Promise<Result<UserVO>> => {
    return request.get(`/admin/faultList/listRepairman`)
}

//获取当前用户信息（普通用户）
export const displayNormalUserInfoAPI = ( id :number ): Promise<Result<UserVO>> => {
    return request.get(`/personalCenter/display1/${id}`)
}
//获取当前用户信息（管理员或维修人员）
export const displayAdminOrRepairmanInfoAPI = ( id :number ): Promise<Result<UserVO>> => {
    return request.get(`/personalCenter/display2/${id}`)
}

//更新用户信息接口(用户端)
export const updateUserAPI3 = ( UserDTO :UserDTO): Promise<Result> => {
    return request.put('/personalCenter/user', UserDTO)
}

//更新用户信息接口(维修人员端)
export const updateUserAPI4 = ( UserDTO :UserDTO): Promise<Result> => {
    return request.put('/personalCenter/repairman', UserDTO)
}

//普通用户修改密码
export const editPwd1 = ( UserPwdDTO :UserPwdDTO): Promise<Result> => {
    return request.put('/personalCenter/editPwd1', UserPwdDTO)
}
//管理员或维修人员修改密码
export const editPwd2 = ( UserPwdDTO :UserPwdDTO): Promise<Result> => {
    return request.put('/personalCenter/editPwd2', UserPwdDTO)
}