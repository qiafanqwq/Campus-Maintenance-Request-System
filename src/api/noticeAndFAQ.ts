import request from '@/utils/request.ts'

// 定义类型
export interface NoticeVO {
    id: number
    title: string
    publisher: string
    content: string
    createTime: string
}
export interface FAQVO {
    id: number
    title: string
    reason?: string
    solution?: string
}
export interface NoticeDTO {
    id?: number
    title: string
    publisher: string
    content: string
}
export interface FAQDTO {
    id?: number
    title: string
    reason: string
    solution: string
}

// API 响应类型
export interface Result<T = any> {
    code: number
    msg: string
    data: T
}

// 获取通知公告2
export const getNoticesAPI2 = (title: string): Promise<Result<NoticeVO[]>> => {
    return request.get('/user/notice/list2', {params: title})
}

// 获取通知公告
export const getNoticesAPI = (title: string): Promise<Result<NoticeVO[]>> => {
    return request.get('/admin/Notice/list1', {params: title} )
}

// 添加通知公告
export const addNoticeAPI = (NoticeDTO: NoticeDTO): Promise<Result> => {
    return request.post('/admin/Notice', NoticeDTO)
}

// 编辑通知公告
export const updateNoticeAPI = (NoticeDTO: NoticeDTO): Promise<Result> => {
    return request.put('/admin/Notice', NoticeDTO)
}

// 删除通知公告
export const deleteNoticeAPI = (id: number): Promise<Result> => {
    return request.delete(`/admin/Notice/${id}`)
}

// 获取常见问题(用户端）
export const getFAQsAPI = (title: string): Promise<Result<FAQVO[]>> => {
    return request.get('/user/FAQ/list', {params: title})
}

// 获取常见问题(管理端）
export const getFAQsAPI2 = (title: string): Promise<Result<FAQVO[]>> => {
    return request.get('/admin/FAQ/list', {params: title})
}

// 添加常见问题
export const addFAQAPI = (FAQDTO: FAQDTO): Promise<Result> => {
    return request.post('/admin/FAQ', FAQDTO)
}

// 编辑常见问题
export const updateFAQAPI = (FAQDTO: FAQDTO): Promise<Result> => {
    return request.put('/admin/FAQ', FAQDTO)
}

// 删除常见问题
export const deleteFAQAPI = (id: number): Promise<Result> => {
    return request.delete(`/admin/FAQ/${id}`)
}