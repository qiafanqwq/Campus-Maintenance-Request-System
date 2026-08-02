import request from '@/utils/request.ts'
import {type CategoryVO} from '@/api/category.ts'
// 报修单参数类型
export interface RepairOrderDTO {
    id?: number
    nickName: string
    userPhone: string
    address: string
    categoryName: string
    description: string
    expectTime: string
    remark: string
    feedback: string
    rrId?: number
    repairmanName: string
    repairTime: string
    repairProcess: string
    status?: number
}

// 报修单响应类型
export interface RepairOrderVO {
    id: number
    nickName: string
    userPhone: string
    address: string
    categoryName: string
    description: string
    expectTime: string
    remark: string
    feedback: string
    rrId?: number
    repairmanName: string
    repairTime: string
    repairProcess: string
    status: number
    createTime?: string
    totalCount?: number
}

// 查询故障单参数类型
export interface RepairOrderPageQueryDTO {
    id?: number
    address?: string
    repairmanName?: string
    nickName?: string
    status?: number
    repairTime?: string
    page: number
    pageSize: number
}

// 查询故障单响应类型
export interface PageResult {
    total: number
    records: RepairOrderVO[]
}

// API 响应类型
export interface Result<T = any> {
    code: number
    msg: string
    data: T
}

// 获取报修分类
export const getCategoriesAPI = (): Promise<Result<CategoryVO[]>> => {
    return request.get('/admin/Category')
}

// 获取故障单记录( 管理员端 )
export const pageFaultListAPI = (params: RepairOrderPageQueryDTO): Promise<Result<PageResult>> => {
    return request.get('/admin/faultList/pageQuery', { params })
}

// 添加报修单
export const addRepairOrderAPI = (data: RepairOrderDTO): Promise<Result> => {
    return request.post('/admin/faultList', data)
}

// 编辑报修单
export const updateRepairOrderAPI = (data: RepairOrderDTO): Promise<Result> => {
    return request.put('/admin/faultList', data)
}

// 删除报修单
export const deleteRepairOrderAPI = (rrId: number): Promise<Result> => {
    return request.delete(`/admin/faultList/${rrId}`)
}

// 分配维修人员
export const assignRepairmanAPI = (params: { id: number, repairmanName: string }): Promise<Result> => {
    return request.put('/admin/faultList/assign',null,{params})
}

export const getMyRepairOrdersAPI = (params: RepairOrderPageQueryDTO): Promise<Result<PageResult>> => {
    return request.get('/user/repairService/pageQuery', { params })
}

// 提交报修单
export const submitRepairAPI = (RepairOrderDTO:RepairOrderDTO): Promise<Result> => {
    return request.post(`/user/repairService`, RepairOrderDTO)
}

//点击详情获得报修单的信息(用户端)
export const getRepairOrderDetailAPI1 = (orderId: number): Promise<Result<RepairOrderVO[]>> => {
    return request.get(`/user/repairService/detail/${orderId}`)
}
//点击详情获得报修单的信息(维修人员端)
export const getRepairOrderDetailAPI2 = (orderId: number): Promise<Result<RepairOrderVO[]>> => {
    return request.get(`/repairman/repairRequests/detail/${orderId}`)
}
//点击详情获得报修单的信息(维修人员端)
export const getRepairOrderDetailAPI3 = (orderId: number): Promise<Result<RepairOrderVO[]>> => {
    return request.get(`/admin/faultList/detail/${orderId}`)
}

// 获取故障单记录( 维修人员端 )
export const pageRepairOrderAPI = (params: RepairOrderPageQueryDTO): Promise<Result<PageResult>> => {
    return request.get('/repairman/repairRequests/pageQuery', { params })
}

// 维修反馈(改变维修状态、填写维修过程、维修时间)
export const updateRepairOrder = (RepairOrderDTO: RepairOrderDTO): Promise<Result> => {
    return request.put('/repairman/repairRequests', RepairOrderDTO)
}

// 转发报修单
export const transferAPI = (params: { repairmanName: string; id: number; rrId: number}): Promise<Result> => {
    return request.put('/admin/faultList/transfer',null,{params})
}

// 用户填写反馈
export const feedback = (RepairOrderDTO: RepairOrderDTO): Promise<Result> => {
    return request.put('/user/repairService/feedback', RepairOrderDTO)
}



// 统计结果类型（对应后端RepairStatisticsVO）
export interface AddressStatVO {
    address: string; // 故障地址
    count: number; // 数量
    percentage: number; // 占比（%）
}

export interface TimeStatVO {
    date: string; // 日期（yyyy-MM-dd）
    count: number; // 数量
}

export interface StatusStatVO {
    status: number; // 状态值
    statusName: string; // 状态名称
    count: number; // 数量
    percentage: number; // 占比（%）
}

export interface TotalStatVO {
    totalCount: number; // 总数量
    pendingCount: number; // 待处理数量
    processingCount: number; // 处理中数量
    completedCount: number; // 已完成数量
    cancelledCount: number; // 已取消数量
    pendingTransferCount: number; //待转发数量
}

export interface RepairStatisticsVO {
    addressStats: AddressStatVO[];
    timeStats: TimeStatVO[];
    statusStats: StatusStatVO[];
    totalStat: TotalStatVO;
}

// 统计API
export const getRepairStatisticsAPI = (params: RepairOrderPageQueryDTO): Promise<Result<RepairStatisticsVO>> => {
    return request.get('/admin/faultList/statistics', { params });
};


// 导出接口
export const exportRepairOrderAPI = (params: RepairOrderPageQueryDTO): Promise<Blob> => {
    console.log('发送导出请求，参数:', params)
    return request.post('/admin/faultList/export', params, {
        responseType: 'blob'  // 处理文件流
    })
}