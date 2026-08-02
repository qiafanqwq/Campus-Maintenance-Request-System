<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { type NoticeDTO, type NoticeVO, addNoticeAPI,
          updateNoticeAPI, deleteNoticeAPI, getNoticesAPI } from '@/api/noticeAndFAQ.ts'
import { Search, User, ArrowRight, Plus, Edit, Delete, Clock  } from '@element-plus/icons-vue'

// 响应式数据
const notices = ref<NoticeVO[]>([])
const loading = ref(false)
const detailVisible = ref(false)
const formVisible = ref(false)
const currentNotice = ref<NoticeVO | null>(null)
const searchKeyword = ref('')
const formType = ref<'add' | 'edit'>('add')

// 表单数据
const noticeForm = ref({
  title: '',
  publisher: '',
  content: ''
})

// 表单验证规则
const formRules = {
  title: [
    { required: true, message: '请输入公告标题', trigger: 'blur' },
    { min: 2, max: 50, message: '标题长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  publisher: [
    { required: true, message: '请输入发布者', trigger: 'blur' },
    { min: 2, max: 20, message: '发布者长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入公告内容', trigger: 'blur' },
    { min: 10, message: '公告内容至少10个字符', trigger: 'blur' }
  ]
}

const formRef = ref()

// 截断内容（用于卡片显示）
const truncateContent = (content: string, maxLength: number = 100) => {
  if (content.length <= maxLength) {
    return content
  }
  return content.substring(0, maxLength) + '...'
}

// 查看公告详情
const viewNoticeDetail = (notice: NoticeVO) => {
  currentNotice.value = notice
  detailVisible.value = true
}

// 关闭详情
const handleCloseDetail = () => {
  detailVisible.value = false
  currentNotice.value = null
}

// 打开添加表单
const handleAdd = () => {
  formType.value = 'add'
  noticeForm.value = {
    title: '',
    publisher: '',
    content: ''
  }
  formVisible.value = true
}

// 打开编辑表单
const handleEdit = (notice: NoticeDTO) => {
  formType.value = 'edit'
  currentNotice.value = notice
  noticeForm.value = {
    title: notice.title,
    publisher: notice.publisher,
    content: notice.content
  }
  formVisible.value = true
}

// 关闭表单
const handleCloseForm = () => {
  formVisible.value = false
  formRef.value?.resetFields()
}

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return

  try {
    const valid = await formRef.value.validate()
    if (valid) {
      if (formType.value === 'add') {
        // 添加公告
        const response = await addNoticeAPI(noticeForm.value)
        if (response.code === 1) {
          ElMessage.success('公告添加成功')
          handleCloseForm()
          await loadNotices()
        }
      } else {
        // 编辑公告
        const response = await updateNoticeAPI({
          id: currentNotice.value!.id,
          ...noticeForm.value
        })
        if (response.code === 1) {
          ElMessage.success('公告更新成功')
          handleCloseForm()
          await loadNotices()
        }
      }
    }
  } catch (error) {
    ElMessage.error('操作失败，请重试')
  }
}

// 添加删除相关的响应式数据
const deleteDialogVisible = ref(false)
const deleteTarget = ref<NoticeVO | null>(null)

// 修改删除函数
const handleDelete = async (notice: NoticeVO) => {
  deleteTarget.value = notice
  deleteDialogVisible.value = true
}

// 确认删除
const confirmDelete = async () => {
  if (!deleteTarget.value) return

  try {
    const response = await deleteNoticeAPI(deleteTarget.value.id)
    if (response.code === 1) {
      ElMessage.success('公告删除成功')
      deleteDialogVisible.value = false
      deleteTarget.value = null
      loadNotices()
    }
  } catch (error) {
    ElMessage.error('删除失败，请重试')
  }
}

// 取消删除
const handleDeleteCancel = () => {
  deleteDialogVisible.value = false
  deleteTarget.value = null
  ElMessage.info('已取消删除')
}

// 加载数据
const loadNotices = async () => {
  loading.value = true
  try {
    // 移除搜索参数，始终获取所有数据
    const response = await getNoticesAPI('')
    if (response.code === 1) {
      notices.value = response.data
    } else {
      ElMessage.error(response.msg || '加载通知公告失败')
    }
  } catch (error) {
    ElMessage.error('加载通知公告失败')
    console.error('加载失败:', error)
  } finally {
    loading.value = false
  }
}

// 过滤后的公告列表
const filteredNotices = computed(() => {
  const keyword = searchKeyword.value.trim().toLowerCase()
  if (!keyword) {
    return notices.value
  }

  return notices.value.filter(notice => {
    const titleMatch = notice.title?.toLowerCase().includes(keyword) || false
    const contentMatch = notice.content?.toLowerCase().includes(keyword) || false
    const publisherMatch = notice.publisher?.toLowerCase().includes(keyword) || false

    return titleMatch || contentMatch || publisherMatch
  })
})

// 格式化创建时间
const formatCreateTime = (timeString: string) => {
  if (!timeString) return ''

  try {
    const date = new Date(timeString)
    // 格式化为：YYYY-MM-DD HH:mm
    return date.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    }).replace(/\//g, '-')
  } catch (error) {
    return timeString
  }
}


// 组件挂载时加载数据
onMounted(() => {
  loadNotices()
})
</script>

<template>
  <div class="notice-container">
    <div class="notice-header">
      <h1>通知公告管理</h1>
      <p>在此添加、编辑、删除通知公告</p>
    </div>

    <!-- 工具栏 -->
    <div class="notice-toolbar">
      <div class="toolbar-left">
        <el-button
            type="primary"
            :icon="Plus"
            @click="handleAdd"
        >
          添加公告
        </el-button>
      </div>
      <div class="toolbar-right">
        <el-input
            v-model="searchKeyword"
            placeholder="搜索公告标题或内容..."
            clearable
            style="width: 300px"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>
    </div>

    <div class="notice-content">
      <!-- 公告卡片网格 -->
      <div class="notice-grid" v-loading="loading">
        <div
            v-for="notice in filteredNotices"
            :key="notice.id"
            class="notice-card"
        >
          <div class="card-header">
            <h3 class="notice-title">{{ notice.title }}</h3>
            <span class="create-time">{{ formatCreateTime(notice.createTime) }}</span>
            <div class="card-actions">
              <el-tag type="primary" size="small" class="publisher-tag">
                {{ notice.publisher }}
              </el-tag>
              <div class="action-buttons">
                <el-button
                    size="small"
                    type="primary"
                    :icon="Edit"
                    @click.stop="handleEdit(notice)"
                >
                  编辑
                </el-button>
                <el-button
                    size="small"
                    type="danger"
                    :icon="Delete"
                    @click.stop="handleDelete(notice)"
                >
                  删除
                </el-button>
              </div>
            </div>
          </div>

          <div class="card-content" @click="viewNoticeDetail(notice)">
            <p class="notice-content-text">{{ truncateContent(notice.content) }}</p>
          </div>

          <div class="card-footer" @click="viewNoticeDetail(notice)">
            <span class="view-more">
              点击查看详情
              <el-icon><ArrowRight /></el-icon>
            </span>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <el-empty
          v-if="filteredNotices.length === 0 && !loading"
          description="暂无通知公告"
          :image-size="200"
      >
        <el-button type="primary" :icon="Plus" @click="handleAdd">
          添加第一条公告
        </el-button>
      </el-empty>
    </div>

    <!-- 公告详情弹窗 -->
    <el-dialog
        v-model="detailVisible"
        :title="currentNotice?.title"
        width="600px"
        :before-close="handleCloseDetail"
        center
    >
      <div v-if="currentNotice" class="notice-detail">
        <div class="detail-header">
          <div class="detail-meta">
            <span class="publisher">
              <el-icon><User /></el-icon>
              发布者：{{ currentNotice.publisher }}
            </span>
            <span class="create-time-detail">
              <el-icon><Clock /></el-icon>
              发布时间：{{ formatCreateTime(currentNotice.createTime) }}
            </span>
          </div>
        </div>

        <div class="detail-content">
          <p>{{ currentNotice.content }}</p>
        </div>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleCloseDetail">关闭</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 添加/编辑表单弹窗 -->
    <el-dialog
        v-model="formVisible"
        :title="formType === 'add' ? '添加公告' : '编辑公告'"
        width="600px"
        :before-close="handleCloseForm"
        center
    >
      <el-form
          :model="noticeForm"
          :rules="formRules"
          ref="formRef"
          label-width="80px"
      >
        <el-form-item label="公告标题" prop="title">
          <el-input
              v-model="noticeForm.title"
              placeholder="请输入公告标题"
              maxlength="50"
              show-word-limit
          />
        </el-form-item>

        <el-form-item label="发布者" prop="publisher">
          <el-input
              v-model="noticeForm.publisher"
              placeholder="请输入发布者"
              maxlength="20"
              show-word-limit
          />
        </el-form-item>

        <el-form-item label="公告内容" prop="content">
          <el-input
              v-model="noticeForm.content"
              type="textarea"
              :rows="6"
              placeholder="请输入公告内容"
              maxlength="1000"
              show-word-limit
              resize="none"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleCloseForm">取消</el-button>
          <el-button type="primary" @click="submitForm">
            {{ formType === 'add' ? '添加' : '更新' }}
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 删除确认弹窗 -->
    <el-dialog
        v-model="deleteDialogVisible"
        title="删除确认"
        width="400px"
        :before-close="handleDeleteCancel"
        destroy-on-close
        center
    >
      <div style="text-align: center;">
        <p>确定要删除公告 "{{ deleteTarget?.title }}" 吗？</p>
      </div>
      <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleDeleteCancel">取消</el-button>
        <el-button type="danger" @click="confirmDelete">确定删除</el-button>
      </span>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.notice-container {
  padding: 20px;
  background-color: white;
  min-height: 100vh;
}

.notice-header {
  text-align: center;
  margin-bottom: 30px;
}

.notice-header h1 {
  font-size: 2.2rem;
  margin-bottom: 8px;
  font-weight: 600;
  color: #303133;
}

.notice-header p {
  font-size: 1rem;
  color: #606266;
}

.notice-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
}

.toolbar-left,
.toolbar-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

/* 公告网格布局 */
.notice-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}

/* 公告卡片样式 */
.notice-card {
  background: white;
  border: 1px solid #e4e7ed;
  border-radius: 12px;
  padding: 20px;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  height: 240px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.notice-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.12);
  border-color: #409eff;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
  flex-shrink: 0;
}

.notice-title {
  font-size: 1.1rem;
  font-weight: 600;
  color: #303133;
  margin: 0;
  flex: 1;
  margin-right: 12px;
  line-height: 1.4;
}

.card-actions {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 8px;
}

.action-buttons {
  display: flex;
  gap: 6px;
}

.publisher-tag {
  flex-shrink: 0;
}

.card-content {
  flex: 1;
  overflow: hidden;
  margin-bottom: 12px;
  cursor: pointer;
}

.notice-content-text {
  color: #606266;
  line-height: 1.6;
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 4;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-footer {
  flex-shrink: 0;
  border-top: 1px solid #f0f0f0;
  padding-top: 12px;
  cursor: pointer;
}

.view-more {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  color: #409eff;
  font-size: 0.9rem;
  gap: 4px;
  transition: all 0.3s ease;
}

.notice-card:hover .view-more {
  transform: translateX(4px);
}

/* 详情弹窗样式 */
.notice-detail {
  padding: 10px 0;
}

.detail-header {
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e4e7ed;
}

.detail-meta {
  display: flex;
  align-items: center;
  gap: 16px;
}

.publisher {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #606266;
  font-size: 0.95rem;
}

.detail-content {
  line-height: 1.8;
  color: #303133;
  font-size: 1rem;
}

.detail-content p {
  margin: 0;
  white-space: pre-wrap;
}

/* 空状态样式 */
:deep(.el-empty__description) {
  margin-top: 16px;
  color: #909399;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .notice-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .notice-container {
    padding: 16px;
  }

  .notice-grid {
    grid-template-columns: 1fr;
  }

  .notice-toolbar {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }

  .toolbar-left,
  .toolbar-right {
    justify-content: center;
  }

  .notice-header h1 {
    font-size: 1.8rem;
  }

  .card-header {
    flex-direction: column;
    gap: 12px;
  }

  .card-actions {
    flex-direction: row;
    align-items: center;
    justify-content: space-between;
    width: 100%;
  }
}

/* 加载状态样式 */
:deep(.el-loading-mask) {
  border-radius: 12px;
}

/* 卡片头部的左侧区域样式 */
.header-left {
  flex: 1;
  margin-right: 12px;
}

.create-time {
  display: block;
  font-size: 0.85rem;
  color: #909399;
  margin-top: 4px;
}

/* 详情页的创建时间样式 */
.create-time-detail {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #606266;
  font-size: 0.95rem;
}

/* 详情页元信息布局调整 */
.detail-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 16px;
}
</style>

