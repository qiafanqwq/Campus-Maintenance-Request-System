import request from '@/utils/request.ts'

// API 响应类型
export interface Result<T = any> {
    code: number
    msg: string
    data: T
}
export interface CategoryDTO{
    id?: number
    name: string
    parentId: number
    description: string
}
export interface CategoryVO {
    id: number
    name: string
    parentId: number
    description: string
    sortOrder: number
    children?: CategoryVO[]
}

// 获取报修分类
export const getCategoryAPI = (): Promise<Result<CategoryVO[]>> => {
    return request.get('/admin/Category')
}

// 添加报修分类
export const addCategoryAPI = (CategoryDTO: CategoryDTO): Promise<Result> => {
    return request.post('/admin/Category', CategoryDTO)
}

// 编辑报修分类
export const updateCategoryAPI = (CategoryDTO: CategoryDTO): Promise<Result> => {
    return request.put('/admin/Category', CategoryDTO)
}

// 删除报修分类
export const deleteCategoryAPI = (id: number): Promise<Result> => {
    return request.delete(`/admin/Category/${id}`)
}

