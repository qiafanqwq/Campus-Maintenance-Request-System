import request from '@/utils/request.ts'

// API 响应类型
export interface Result<T = any> {
    code: number
    msg: string
    data: T
}
export interface LocationDTO{
    id?: number
    name: string
    parentId: number
}
export interface LocationVO {
    id: number
    name: string
    parentId: number
    sortOrder: number
    children?: LocationVO[]
}

// 获取地址分类
export const getLocationAPI = (): Promise<Result<LocationVO[]>> => {
    return request.get('/admin/Location')
}

// 添加地址分类
export const addLocationAPI = (LocationDTO: LocationDTO): Promise<Result> => {
    return request.post('/admin/Location', LocationDTO)
}

// 编辑地址分类
export const updateLocationAPI = (LocationDTO: LocationDTO): Promise<Result> => {
    return request.put('/admin/Location', LocationDTO)
}

// 删除地址分类
export const deleteLocationAPI = (id: number): Promise<Result> => {
    return request.delete(`/admin/Location/${id}`)
}

