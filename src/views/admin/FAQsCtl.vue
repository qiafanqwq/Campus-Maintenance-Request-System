<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getFAQsAPI2, type FAQDTO, type FAQVO, addFAQAPI, updateFAQAPI, deleteFAQAPI } from '@/api/noticeAndFAQ.ts'
import { Search, Warning, SuccessFilled, Plus, Edit, Delete } from '@element-plus/icons-vue'

console.log('FAQsCtl 组件被加载') // 检查组件是否加载
// 响应式数据
const faqs = ref<FAQVO[]>([])
const loading = ref(false)
const activeNames = ref<number[]>([])
const searchKeyword = ref('')
const formVisible = ref(false)
const formType = ref<'add' | 'edit'>('add')
const currentFAQ = ref<FAQVO | null>(null)

// 表单数据
const faqForm = ref({
  title: '',
  reason: '',
  solution: ''
})

// 表单验证规则
const formRules = {
  title: [
    { required: true, message: '请输入问题标题', trigger: 'blur' },
    { min: 5, max: 100, message: '标题长度在 5 到 100 个字符', trigger: 'blur' }
  ],
  reason: [
    { required: true, message: '请输入可能原因', trigger: 'blur' },
    { min: 10, message: '原因描述至少10个字符', trigger: 'blur' }
  ],
  solution: [
    { required: true, message: '请输入解决方法', trigger: 'blur' },
    { min: 10, message: '解决方法至少10个字符', trigger: 'blur' }
  ]
}

const formRef = ref()

// 过滤后的FAQ列表
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
  if (!reason) return []
  return reason.split('；').filter(item => item.trim())
}

// 格式化解决方法列表
const formatSolutions = (solution: string | null): string[] => {
  if (!solution) return []
  return solution.split('；').filter(item => item.trim())
}

// 打开添加表单
const handleAdd = () => {
  formType.value = 'add'
  faqForm.value = {
    title: '',
    reason: '',
    solution: ''
  }
  formVisible.value = true
}

// 打开编辑表单
const handleEdit = (faq: FAQDTO) => {
  formType.value = 'edit'
  currentFAQ.value = faq
  faqForm.value = {
    title: faq.title || '',
    reason: faq.reason || '',
    solution: faq.solution || ''
  }
  formVisible.value = true
}

// 关闭表单
const handleCloseForm = () => {
  formVisible.value = false
  formRef.value?.resetFields()
  currentFAQ.value = null
}

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return

  try {
    const valid = await formRef.value.validate()
    if (valid) {
      if (formType.value === 'add') {
        // 添加常见问题
        const response = await addFAQAPI(faqForm.value)
        if (response.code === 1) {
          ElMessage.success('常见问题添加成功')
          handleCloseForm()
          loadFaqs()
        }
      } else {
        // 编辑常见问题
        const response = await updateFAQAPI({
          id: currentFAQ.value!.id,
          ...faqForm.value
        })
        if (response.code === 1) {
          ElMessage.success('常见问题更新成功')
          handleCloseForm()
          loadFaqs()
        }
      }
    }
  } catch (error) {
    ElMessage.error('操作失败，请重试')
  }
}

// 删除常见问题
const handleDelete = async (faq: FAQDTO) => {
  try {
    await ElMessageBox.confirm(
        `确定要删除常见问题 "${faq.title}" 吗？`,
        '删除确认',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        }
    )
    const response = await deleteFAQAPI(faq.id)
    if (response.code === 1) {
      ElMessage.success('常见问题删除成功')
      loadFaqs()
    }
  } catch (error) {
    ElMessage.info('已取消删除')
  }
}

// 加载数据
const loadFaqs = async () => {
  loading.value = true
  try {
    const response = await getFAQsAPI2(searchKeyword.value)
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
      <h1>常见问题管理</h1>
      <p>在此添加、编辑、删除常见问题</p>
    </div>

    <div class="faq-content">
      <!-- 工具栏 -->
      <div class="faq-toolbar">
        <div class="toolbar-left">
          <el-button
              type="primary"
              :icon="Plus"
              @click="handleAdd"
          >
            添加问题
          </el-button>
        </div>
        <div class="toolbar-right">
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
                <div class="faq-actions">
                  <el-tag type="primary" size="small" class="problem-tag">
                    问题 {{ faq.id }}
                  </el-tag>
                  <div class="action-buttons">
                    <el-button
                        size="small"
                        type="primary"
                        :icon="Edit"
                        @click.stop="handleEdit(faq)"
                    >
                      编辑
                    </el-button>
                    <el-button
                        size="small"
                        type="danger"
                        :icon="Delete"
                        @click.stop="handleDelete(faq)"
                    >
                      删除
                    </el-button>
                  </div>
                </div>
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
          description="暂无常见问题"
          :image-size="200"
      >
        <el-button type="primary" :icon="Plus" @click="handleAdd">
          添加第一个问题
        </el-button>
      </el-empty>
    </div>

    <!-- 添加/编辑表单弹窗 -->
    <el-dialog
        v-model="formVisible"
        :title="formType === 'add' ? '添加常见问题' : '编辑常见问题'"
        width="700px"
        :before-close="handleCloseForm"
        center
    >
      <el-form
          :model="faqForm"
          :rules="formRules"
          ref="formRef"
          label-width="100px"
      >
        <el-form-item label="问题标题" prop="title">
          <el-input
              v-model="faqForm.title"
              placeholder="请输入问题标题"
              maxlength="100"
              show-word-limit
          />
        </el-form-item>

        <el-form-item label="可能原因" prop="reason">
          <el-input
              v-model="faqForm.reason"
              type="textarea"
              :rows="4"
              placeholder="请输入可能原因，使用中文分号；分隔不同的原因"
              maxlength="500"
              show-word-limit
              resize="none"
          />
          <div class="form-tip">提示：使用中文分号；分隔不同的原因项</div>
        </el-form-item>

        <el-form-item label="解决方法" prop="solution">
          <el-input
              v-model="faqForm.solution"
              type="textarea"
              :rows="4"
              placeholder="请输入解决方法，使用中文分号；分隔不同的步骤"
              maxlength="500"
              show-word-limit
              resize="none"
          />
          <div class="form-tip">提示：使用中文分号；分隔不同的解决步骤</div>
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

/* 工具栏样式 */
.faq-toolbar {
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

/* 折叠面板样式 */
.faq-list {
  max-width: 1200px;
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
  width: 100%;
}

.title-text {
  font-size: 1.1rem;
  color: #303133;
  flex: 1;
  margin-right: 16px;
}

.faq-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.action-buttons {
  display: flex;
  gap: 8px;
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

/* 表单提示 */
.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
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
  min-height: 70px;
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
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }

  .toolbar-left,
  .toolbar-right {
    justify-content: center;
  }

  .faq-header h1 {
    font-size: 1.8rem;
  }

  .faq-title {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .title-text {
    margin-right: 0;
  }

  .faq-actions {
    width: 100%;
    justify-content: space-between;
  }
}
</style>