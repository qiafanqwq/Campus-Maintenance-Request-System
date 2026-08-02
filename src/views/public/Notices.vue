<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getNoticesAPI2, type NoticeVO} from '@/api/noticeAndFAQ.ts'
import { User, ArrowRight, Clock } from '@element-plus/icons-vue'

// 响应式数据
const notices = ref<NoticeVO[]>([])
const loading = ref(false)
const detailVisible = ref(false)
const currentNotice = ref<NoticeVO | null>(null)
const searchKeyword = ref('')

// 过滤后的公告列表
const filteredNotices = computed(() => {
  if (!searchKeyword.value) {
    return notices.value
  }

  const keyword = searchKeyword.value.toLowerCase()
  return notices.value.filter(notice =>
      notice.title.toLowerCase().includes(keyword) ||
      notice.content.toLowerCase().includes(keyword) ||
      notice.publisher.toLowerCase().includes(keyword)
  )
})

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

// 加载数据
const loadNotices = async () => {
  loading.value = true
  try {
    const response = await getNoticesAPI2(searchKeyword.value)    // API调用
    if (response.code === 1) {
      notices.value = response.data
    }
  } catch (error) {
    ElMessage.error('加载通知公告失败')
  } finally {
    loading.value = false
  }
}

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
      <h1>通知公告</h1>
    </div>

    <!-- 搜索和筛选区域 -->
    <div class="notice-toolbar">
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

    <div class="notice-content">
      <!-- 公告卡片网格 -->
      <div class="notice-grid" v-loading="loading">
        <div
            v-for="notice in filteredNotices"
            :key="notice.id"
            class="notice-card"
            @click="viewNoticeDetail(notice)"
        >
          <div class="card-header">
            <h3 class="notice-title">{{ notice.title }}</h3>
            <span class="create-time">{{ formatCreateTime(notice.createTime) }}</span>
            <el-tag type="primary" size="small" class="publisher-tag">
              {{ notice.publisher }}
            </el-tag>
          </div>

          <div class="card-content">
            <p class="notice-content-text">{{ truncateContent(notice.content) }}</p>
          </div>

          <div class="card-footer">
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
      />
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
  justify-content: flex-end;
  margin-bottom: 24px;
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
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  height: 200px;
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

.publisher-tag {
  flex-shrink: 0;
}

.card-content {
  flex: 1;
  overflow: hidden;
  margin-bottom: 12px;
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
    justify-content: center;
  }

  .notice-header h1 {
    font-size: 1.8rem;
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

/* 详情页元信息布局 */
.detail-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;  /* 改为两端对齐 */
  flex-wrap: wrap;
  gap: 16px;
}
</style>