<script setup lang="ts">
import {ref, onMounted, reactive} from 'vue'
import { ElMessage } from 'element-plus'
import {Clock, Calendar, User, Edit} from '@element-plus/icons-vue'
import {
  feedback, getMyRepairOrdersAPI, getRepairOrderDetailAPI1, type RepairOrderDTO,
  type RepairOrderPageQueryDTO, type RepairOrderVO } from '@/api/RepairOrder.ts'
import TablePagination from "@/components/table/TablePagination.vue";

// 响应式数据
const repairList = ref<RepairOrderVO[]>([])
const loading = ref(false)
const detailVisible = ref(false)
const feedbackVisible = ref(false)
const currentRepair = ref<RepairOrderVO | null>(null)
const currentFeedback = ref('')

const repairHistory = ref<Array<{
  rrId?: number
  repairmanName?: string
  repairTime?: string
  repairProcess?: string
  isHistory?: boolean
}>>([])

// 分页参数
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 状态映射
const statusMap = {
  0: { text: '待处理', type: 'info' },
  1: { text: '处理中', type: 'warning' },
  2: { text: '已完成', type: 'success' },
  3: { text: '已取消', type: 'danger' },
  4: { text: '待转发', type: 'info' }
}

// 获取状态样式
const getStatusType = (status: number) => {
  return statusMap[status as keyof typeof statusMap]?.type || 'info'
}

const getStatusText = (status: number) => {
  return statusMap[status as keyof typeof statusMap]?.text || '未知'
}

// 格式化时间
const formatTime = (time: string) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  }).replace(/\//g, '-')
}

// 加载数据
const loadRepairOrders = async () => {
  loading.value = true
  try {
    // 从 localStorage 获取用户姓名
    const userInfoStr = localStorage.getItem('userInfo')
    if (userInfoStr) {
      const userInfo = JSON.parse(userInfoStr)
      const nickName = userInfo.nickname
      const params: RepairOrderPageQueryDTO = {
        page: pagination.current,
        pageSize: pagination.size,
        nickName: nickName,
      }
      // 清理空值参数
      Object.keys(params).forEach(key => {
        if (params[key as keyof RepairOrderPageQueryDTO] === '' || params[key as keyof RepairOrderPageQueryDTO] === undefined) {
          delete params[key as keyof RepairOrderPageQueryDTO]
        }
      })

      const response = await getMyRepairOrdersAPI(params)
      if (response.code === 1) {
        repairList.value = response.data.records
        pagination.total = response.data.total
      } else {
        ElMessage.error('获取报修记录失败')
      }
    }
  } catch (error) {
    ElMessage.error('获取报修记录失败')
  } finally {
    loading.value = false
  }
}

// 关闭详情时重置
const handleCloseDetail = () => {
  detailVisible.value = false
  currentRepair.value = null
  repairHistory.value = []
}

// 分页大小改变
const handleSizeChange = (size: number) => {
  pagination.size = size
  pagination.current = 1
  loadRepairOrders()
}

// 当前页改变
const handleCurrentChange = (page: number) => {
  pagination.current = page
  loadRepairOrders()
}

// 查看详情
const viewDetail = async (row: RepairOrderVO) => {
  try {
    const response = await getRepairOrderDetailAPI1(row.id)
    if (response.code === 1 && response.data && response.data.length > 0) {
      // 第一个元素是最新记录
      currentRepair.value = response.data[0]

      // 如果有多个记录，从第二个开始是历史记录
      if (response.data.length > 1) {
        repairHistory.value = response.data.slice(1).map(item => ({
          rrId: item.rrId,
          repairmanName: item.repairmanName,
          repairTime: item.repairTime,
          repairProcess: item.repairProcess,
          isHistory: true
        }))
      } else {
        repairHistory.value = []
      }

      detailVisible.value = true
    }
  } catch (error) {
    ElMessage.error('获取详情失败')
  }
}


// 打开反馈弹窗
const openFeedbackDialog = (row: RepairOrderVO) => {
  currentRepair.value = row
  currentFeedback.value = row.feedback || '' // 如果有历史反馈，预填充
  feedbackVisible.value = true
}

// 提交反馈
const submitFeedback = async () => {
  if (!currentRepair.value) return

  // 验证反馈内容
  if (!currentFeedback.value.trim()) {
    ElMessage.warning('请输入反馈内容')
    return
  }

  if (currentFeedback.value.length > 200) {
    ElMessage.warning('反馈内容不能超过200字')
    return
  }

  try {
    // 准备反馈数据
    const feedbackData: RepairOrderDTO = {
      id: currentRepair.value.id,
      nickName: currentRepair.value.nickName,
      userPhone: currentRepair.value.userPhone,
      address: currentRepair.value.address,
      categoryName: currentRepair.value.categoryName,
      description: currentRepair.value.description,
      expectTime: currentRepair.value.expectTime,
      remark: currentRepair.value.remark || '',
      feedback: currentFeedback.value,
      rrId: currentRepair.value.rrId,
      repairmanName: currentRepair.value.repairmanName,
      repairTime: currentRepair.value.repairTime,
      repairProcess: currentRepair.value.repairProcess,
      status: currentRepair.value.status
    }

    const response = await feedback(feedbackData)
    if (response.code === 1) {
      ElMessage.success('反馈提交成功')
      feedbackVisible.value = false
      currentFeedback.value = ''

      // 刷新列表数据
      loadRepairOrders()
    } else {
      ElMessage.error(response.msg || '反馈提交失败')
    }
  } catch (error) {
    ElMessage.error('提交反馈失败，请稍后重试')
  }
}

// 新增：关闭反馈弹窗
const handleCloseFeedback = () => {
  feedbackVisible.value = false
  currentFeedback.value = ''
  currentRepair.value = null
}
// 组件挂载时加载数据
onMounted(() => {
  loadRepairOrders()
})
</script>

<template>
  <div class="list-content-area">
    <el-table
        :data="repairList"
        height="85vh"
        style="width: 100%"
        table-layout="fixed"
        v-loading="loading"
        stripe
    >
      <el-table-column type="index" label="序号" width="90" align="center" />

      <el-table-column prop="categoryName" label="报修分类" width="240">
        <template #default="{ row }">
          <div class="category-info">
            <el-tag size="small" type="info">{{ row.categoryName }}</el-tag>
          </div>
        </template>
      </el-table-column>

      <el-table-column prop="address" label="故障地址" show-overflow-tooltip width="200"/>

      <el-table-column prop="description" label="故障描述" show-overflow-tooltip />

      <el-table-column prop="expectTime" label="期望时间" sortable>
        <template #default="{ row }">
          <div class="time-cell">
            <el-icon><Clock /></el-icon>
            {{ formatTime(row.expectTime) }}
          </div>
        </template>
      </el-table-column>

      <el-table-column prop="status" label="处理状态" align="center" width="100">
        <template #default="{ row }">
          <el-tag
              :type="getStatusType(row.status)"
          >
            {{ getStatusText(row.status) }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column prop="repairmanName" label="维修人员" >
        <template #default="{ row }">
          <span v-if="row.repairmanName" class="repairman-name">
            <el-icon><User /></el-icon>
            {{ row.repairmanName }}
          </span>
          <span v-else class="no-assign">待分配</span>
        </template>
      </el-table-column>

      <el-table-column prop="repairTime" label="处理时间" >
        <template #default="{ row }">
          <div v-if="row.repairTime" class="time-cell">
            <el-icon><Calendar /></el-icon>
            {{ formatTime(row.repairTime) }}
          </div>
          <span v-else class="no-time">-</span>
        </template>
      </el-table-column>

      <!-- 操作列 -->
      <el-table-column label="操作" fixed="right" align="center" width="160">
        <template #default="{ row }">
          <el-button
              size="small"
              type="primary"
              @click="viewDetail(row)"
          >
            详情
          </el-button>
          <el-button
              v-if="row.status === 2"
              size="small"
              type="success"
              :icon="Edit"
              @click="openFeedbackDialog(row)"
              :disabled="row.feedback && row.feedback.trim() !== ''"
          >
          {{ row.feedback && row.feedback.trim() !== '' ? '已反馈' : '反馈' }}
          </el-button>

        </template>
      </el-table-column>

      <template #empty>
        <el-empty
            description="暂无报修记录"
            :image-size="200"
        >
          <el-button type="primary" @click="$router.push('/userDashboard/AddRepOrder')">
            去报修
          </el-button>
        </el-empty>
      </template>
    </el-table>

    <!-- 分页组件 -->
    <TablePagination
        v-if="pagination.total > 0"
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
    />

    <!-- 详情弹窗 -->
    <el-dialog
        v-model="detailVisible"
        :title="`报修单详情 #${currentRepair?.id}`"
        width="600px"
        :before-close="handleCloseDetail"
    >
      <div v-if="currentRepair" class="repair-detail">
        <el-descriptions :column="1" border size="large">
          <el-descriptions-item label="报修单号">
            {{ currentRepair.id }}
          </el-descriptions-item>

          <el-descriptions-item label="报修分类">
            {{ currentRepair.categoryName }}
          </el-descriptions-item>

          <el-descriptions-item label="故障地址">
            {{ currentRepair.address }}
          </el-descriptions-item>

          <el-descriptions-item label="故障描述">
            {{ currentRepair.description }}
          </el-descriptions-item>

          <el-descriptions-item label="期望解决时间">
            <div class="time-info">
              <el-icon><Clock /></el-icon>
              {{ formatTime(currentRepair.expectTime) }}
            </div>
          </el-descriptions-item>

          <el-descriptions-item label="备注信息">
            {{ currentRepair.remark || '无' }}
          </el-descriptions-item>

          <el-descriptions-item label="处理状态">
            <el-tag
                :type="getStatusType(currentRepair.status)"
                size="large"
                :class="{
                'status-tag': true,
                'status-waiting-transfer': currentRepair.status === 4
              }"
            >
              {{ getStatusText(currentRepair.status) }}
              <span v-if="currentRepair.status === 4" class="status-tip">
                (等待管理员转发给其他维修人员)
              </span>
            </el-tag>
          </el-descriptions-item>

          <el-descriptions-item label="维修人员" v-if="currentRepair.repairmanName">
            <div class="repairman-info">
              <span style="margin-left: 8px">{{ currentRepair.repairmanName }}</span>
            </div>
          </el-descriptions-item>

          <el-descriptions-item label="处理时间" v-if="currentRepair.repairTime">
            <div class="time-info">
              <el-icon><Calendar /></el-icon>
              {{ formatTime(currentRepair.repairTime) }}
            </div>
          </el-descriptions-item>

          <el-descriptions-item label="处理过程" v-if="currentRepair.repairProcess">
            <div class="process-content">
              {{ currentRepair.repairProcess }}
            </div>
          </el-descriptions-item>

          <el-descriptions-item label="用户反馈">
            <div v-if="currentRepair.feedback && currentRepair.feedback.trim()" class="feedback-content">
              <div class="feedback-text">
                {{ currentRepair.feedback }}
              </div>
            </div>
            <div v-else class="no-feedback">
              <span class="no-feedback-text">暂无反馈</span>
            </div>
          </el-descriptions-item>

          <!-- 待转发状态说明 -->
          <el-descriptions-item v-if="currentRepair.status === 4">
            <template #label>
              <span class="status-explanation-label">状态说明</span>
            </template>
            <div class="transfer-explanation">
              <el-alert
                  title="当前维修单等待转发"
                  type="info"
                  :closable="false"
                  show-icon
              >
                <p>当前维修人员已完成部分工作，需要其他专业人员协助处理。</p>
                <p>管理员将会把此维修单转发给合适的维修人员继续处理。</p>
              </el-alert>
            </div>
          </el-descriptions-item>
        </el-descriptions>

        <!-- 维修历史记录（如果存在） -->
        <div v-if="repairHistory.length > 0" class="history-section">
          <div class="history-title">
            <el-divider>
      <span style="color: #409EFF; font-weight: bold;">
        <el-icon><Clock /></el-icon>
        维修历史记录 (共{{ repairHistory.length }}条)
      </span>
            </el-divider>
          </div>

          <div class="history-list">
            <div v-for="(record, index) in repairHistory" :key="index" class="history-item">
              <div class="history-header">
                <span class="history-index">历史记录 {{ repairHistory.length - index }}</span>
              </div>

              <div class="history-content">
                <div class="history-row">
                  <span class="history-label"><el-icon><User /></el-icon>维修人员:</span>
                  <span class="history-value">{{ record.repairmanName || '未知' }}</span>
                </div>

                <div class="history-row" v-if="record.repairTime">
                  <span class="history-label"><el-icon><Calendar /></el-icon>处理时间:</span>
                  <span class="history-value">{{ formatTime(record.repairTime) }}</span>
                </div>

                <div class="history-row">
                  <span class="history-label">处理过程:</span>
                  <div class="history-process">
                    {{ record.repairProcess || '无详细记录' }}
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleCloseDetail">关闭</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 新增：反馈弹窗 -->
    <el-dialog
        v-model="feedbackVisible"
        :title="`填写反馈 #${currentRepair?.id}`"
        width="500px"
        :before-close="handleCloseFeedback"
    >
      <div v-if="currentRepair" class="feedback-dialog">
        <el-form label-width="80px">
          <el-form-item label="报修分类">
            <span>{{ currentRepair.categoryName }}</span>
          </el-form-item>

          <el-form-item label="故障描述">
            <span>{{ currentRepair.description }}</span>
          </el-form-item>

          <el-form-item label="维修人员">
            <span>{{ currentRepair.repairmanName || '未知' }}</span>
          </el-form-item>

          <el-form-item label="您的反馈" required>
            <el-input
                v-model="currentFeedback"
                type="textarea"
                :rows="5"
                maxlength="500"
                placeholder="请输入您对本次维修服务的评价和建议（最多200字）"
                show-word-limit
                clearable
            />
          </el-form-item>
        </el-form>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleCloseFeedback">取消</el-button>
          <el-button type="primary" @click="submitFeedback">提交反馈</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.list-content-area {
  padding: 20px;
  background-color: white;
  border-radius: 4px;
  min-height: 700px;
}

/* 报修单号样式 */
.repair-no {
  font-family: 'Courier New', monospace;
  font-weight: 600;
  color: #409eff;
}

/* 分类信息样式 */
.category-info {
  display: flex;
  align-items: center;
}

/* 时间单元格样式 */
.time-cell {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #606266;
}

.time-cell .el-icon {
  color: #909399;
}

/* 维修人员样式 */
.repairman-name {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #409eff;
}

.no-assign {
  color: #909399;
  font-style: italic;
}

.no-time {
  color: #c0c4cc;
  font-style: italic;
}

/* 状态标签样式 */
.status-tag {
  font-weight: 500;
}

.status-waiting-transfer {
  background-color: #f0f9ff;
  border-color: #a0cfff;
  color: #337ecc;
}

.status-tip {
  font-size: 12px;
  margin-left: 4px;
  opacity: 0.8;
}

/* 详情弹窗样式 */
.repair-detail {
  padding: 10px 0;
}

.time-info {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #606266;
}

.repairman-info {
  display: flex;
  align-items: center;
}

.process-content {
  padding: 12px;
  background: #f8f9fa;
  border-radius: 4px;
  border-left: 4px solid #409eff;
  line-height: 1.6;
}

.status-explanation-label {
  font-weight: 600;
  color: #606266;
}

.transfer-explanation {
  margin-top: 8px;
}

/* 空状态样式 */
:deep(.el-empty__description) {
  margin-top: 16px;
  color: #909399;
}

/* 表格样式优化 */
:deep(.el-table) {
  border-radius: 4px;
}

:deep(.el-table th) {
  background-color: #f5f7fa;
  color: #606266;
  font-weight: 600;
}

:deep(.el-table .el-table__row:hover) {
  background-color: #f5f7fa;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .list-content-area {
    padding: 10px;
  }

  :deep(.el-table) {
    font-size: 14px;
  }
}

/* 历史记录样式 */
.history-section {
  margin-top: 20px;
}

.history-title {
  margin-bottom: 16px;
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.history-item {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 16px;
  background: #fafbfc;
}

.history-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px dashed #dcdfe6;
}

.history-index {
  font-weight: 600;
  color: #909399;
}

.history-content {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.history-row {
  display: flex;
  align-items: flex-start;
  gap: 8px;
}

.history-label {
  min-width: 80px;
  color: #606266;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 4px;
}

.history-value {
  color: #303133;
  flex: 1;
}

.history-process {
  flex: 1;
  background: white;
  padding: 12px;
  border-radius: 4px;
  border-left: 3px solid #409EFF;
  line-height: 1.6;
}

/* 操作按钮容器样式 */
.action-buttons {
  display: flex;
  gap: 8px;
  justify-content: center;
}

.action-buttons .el-button {
  flex: 1;
}

/* 反馈弹窗样式 */
.feedback-dialog {
  padding: 10px 0;
}

.feedback-dialog .el-form-item {
  margin-bottom: 20px;
}

.feedback-dialog .el-form-item span {
  color: #606266;
  font-size: 14px;
}

/* 针对已有反馈的按钮样式 */
.action-buttons .el-button.is-disabled {
  background-color: #f0f9ff;
  border-color: #c6e2ff;
  color: #a0cfff;
}
</style>