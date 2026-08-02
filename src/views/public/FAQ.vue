<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getFAQsAPI, type FAQVO} from '@/api/noticeAndFAQ.ts'
import { Search, Warning, SuccessFilled } from '@element-plus/icons-vue'

// 响应式数据
const faqs = ref<FAQVO[]>([])
const loading = ref(false)
const activeNames = ref<number[]>([])
const searchKeyword = ref('')

const filteredFaqs = computed(() => {
  if (!searchKeyword.value) {
    return faqs.value
  }

  const keyword = searchKeyword.value.toLowerCase()
  return faqs.value.filter(faq => {
    const title = faq.title?.toLowerCase() || ''
    const reason = faq.reason?.toLowerCase() || ''
    const solution = faq.solution?.toLowerCase() || ''

    return title.includes(keyword) ||
        reason.includes(keyword) ||
        solution.includes(keyword)
  })
})

// 格式化原因列表
const formatReasons = (reason: string | null): string[] => {
  if (!reason) return []  // 处理null或空字符串
  return reason.split('；').filter(item => item.trim())
}

// 格式化解决方法列表
const formatSolutions = (solution: string | null): string[] => {
  if (!solution) return []  // 处理null或空字符串
  return solution.split('；').filter(item => item.trim())
}

// 加载数据
const loadFaqs = async () => {
  loading.value = true
  try {
    // 模拟API调用
    const response = await getFAQsAPI(searchKeyword.value)
    if (response.code === 1) {
      faqs.value = response.data
    }
  } catch (error) {
    ElMessage.error('加载常见问题失败')
  } finally {
    loading.value = false
  }
}

// 组件挂载时加载数据
onMounted(() => {
  loadFaqs()
})
</script>

<template>
  <div class="faq-container">
    <div class="faq-header">
      <h1>常见问题</h1>
      <p>网络使用中遇到的常见问题及解决方法</p>
    </div>

    <div class="faq-content">
      <!-- 搜索区域 -->
      <div class="faq-toolbar">
        <el-input
            v-model="searchKeyword"
            placeholder="搜索问题标题、原因或解决方法..."
            clearable
            style="width: 400px"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>

      <!-- 问题列表 -->
      <div class="faq-list" v-loading="loading">
        <el-collapse v-model="activeNames" accordion>
          <el-collapse-item
              v-for="faq in filteredFaqs"
              :key="faq.id"
              :name="faq.id"
              class="faq-item"
          >
            <template #title>
              <div class="faq-title">
                <span class="title-text">{{ faq.title }}</span>
                <el-tag type="primary" size="small" class="problem-tag">
                  问题 {{ faq.id }}
                </el-tag>
              </div>
            </template>

            <div class="faq-content-detail">
              <!-- 问题原因 -->
              <div class="reason-section">
                <h4 class="section-title">
                  <el-icon><Warning /></el-icon>
                  可能原因：
                </h4>
                <div class="reason-list">
                  <div
                      v-for="(reason, index) in formatReasons(faq.reason)"
                      :key="index"
                      class="reason-item"
                  >
                    <span class="reason-number">{{ index + 1 }}.</span>
                    <span class="reason-text">{{ reason }}</span>
                  </div>
                </div>
              </div>

              <!-- 解决方法 -->
              <div class="solution-section">
                <h4 class="section-title">
                  <el-icon><SuccessFilled /></el-icon>
                  解决方法：
                </h4>
                <div class="solution-list">
                  <div
                      v-for="(solution, index) in formatSolutions(faq.solution)"
                      :key="index"
                      class="solution-item"
                  >
                    <span class="solution-number">{{ index + 1 }}.</span>
                    <span class="solution-text">{{ solution }}</span>
                  </div>
                </div>
              </div>
            </div>
          </el-collapse-item>
        </el-collapse>
      </div>

      <!-- 空状态 -->
      <el-empty
          v-if="filteredFaqs.length === 0 && !loading"
          description="未找到相关问题"
          :image-size="200"
      />
    </div>
  </div>
</template>

<style scoped>
.faq-container {
  padding: 20px;
  background-color: white;
  min-height: 100vh;
}

.faq-header {
  text-align: center;
  margin-bottom: 30px;
}

.faq-header h1 {
  font-size: 2.2rem;
  margin-bottom: 8px;
  font-weight: 600;
  color: #303133;
}

.faq-header p {
  font-size: 1rem;
  color: #606266;
}

.faq-toolbar {
  display: flex;
  justify-content: center;
  margin-bottom: 24px;
}

/* 折叠面板样式 */
.faq-list {
  max-width: 1000px;
  margin: 0 auto;
}

.faq-item {
  margin-bottom: 16px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s ease;
}

.faq-item:hover {
  border-color: #409eff;
  box-shadow: 0 2px 12px rgba(64, 158, 255, 0.1);
}

/* 标题样式 */
.faq-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  font-weight: 600;
}

.title-text {
  font-size: 1.1rem;
  color: #303133;
  flex: 1;
  margin-right: 16px;
}

.problem-tag {
  flex-shrink: 0;
}

/* 内容区域样式 */
.faq-content-detail {
  padding: 0 20px 20px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 20px 0 12px 0;
  color: #303133;
  font-size: 1rem;
}

.section-title .el-icon {
  font-size: 1.1rem;
}

/* 原因列表样式 */
.reason-list,
.solution-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.reason-item,
.solution-item {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  line-height: 1.6;
}

.reason-number,
.solution-number {
  color: #409eff;
  font-weight: 600;
  flex-shrink: 0;
  min-width: 20px;
}

.reason-text,
.solution-text {
  color: #606266;
  flex: 1;
}

/* 解决方法特殊样式 */
.solution-section {
  margin-top: 24px;
}

.solution-item .solution-number {
  color: #67c23a;
}

.solution-item .solution-text {
  color: #303133;
  font-weight: 500;
}

/* 空状态样式 */
:deep(.el-empty__description) {
  margin-top: 16px;
  color: #909399;
}

/* 折叠面板深度样式 */
:deep(.el-collapse) {
  border: none;
}

:deep(.el-collapse-item__header) {
  background-color: #f8f9fa;
  border: none;
  font-size: 1rem;
  padding: 0 20px;
  height: auto;
  min-height: 60px;
}

:deep(.el-collapse-item__content) {
  padding: 0;
  background-color: white;
}

:deep(.el-collapse-item__wrap) {
  border: none;
  background-color: white;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .faq-container {
    padding: 16px;
  }

  .faq-toolbar {
    justify-content: center;
  }

  .faq-header h1 {
    font-size: 1.8rem;
  }

  .faq-title {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .title-text {
    margin-right: 0;
  }
}
</style>