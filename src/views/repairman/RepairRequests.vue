<template>
  <div class="repair-requests">
    <!-- 搜索区域 -->
    <el-card class="search-card">
      <el-form :model="searchForm" :inline="true" class="search-form">
        <el-form-item label="报修单号">
          <el-input
              v-model="searchForm.id"
              placeholder="请输入报修单号"
              clearable
              style="width: 150px"
          />
        </el-form-item>

        <el-form-item label="故障地址">
          <el-input
              v-model="searchForm.address"
              placeholder="请输入故障地址"
              clearable
              style="width: 180px"
          />
        </el-form-item>

        <el-form-item label="报修用户">
          <el-input
              v-model="searchForm.nickName"
              placeholder="请输入报修用户"
              clearable
              style="width: 150px"
          />
        </el-form-item>

        <el-form-item label="报修状态">
          <el-select
              v-model="searchForm.status"
              placeholder="请选择状态"
              clearable
              style="width: 150px"
          >
            <el-option label="待处理" :value="0" />
            <el-option label="处理中" :value="1" />
            <el-option label="已完成" :value="2" />
            <el-option label="已取消" :value="3" />
            <el-option label="待转发" :value="4" />
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格区域 -->
    <el-card class="table-card">
      <template #header>
        <div class="table-header">
          <span class="title">报修信息列表</span>
          <div class="header-actions">
            <el-button :icon="Refresh" @click="loadRepairOrders">刷新</el-button>
          </div>
        </div>
      </template>

      <el-table
          :data="tableData"
          v-loading="loading"
          border
          stripe
          style="width: 100%; height: 70vh"
      >
        <el-table-column prop="id" label="报修单号" width="100" align="center" />
        <el-table-column prop="address" label="故障地址" min-width="180" show-overflow-tooltip />
        <el-table-column prop="nickName" label="报修用户" width="120" align="center" />
        <el-table-column prop="categoryName" label="报修分类" width="150" show-overflow-tooltip />
        <el-table-column prop="description" label="报修描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="userPhone" label="联系电话" width="130" align="center" />
        <el-table-column prop="expectTime" label="期望处理时间" width="180" align="center">
          <template #default="{ row }">
            {{ formatDateTime(row.expectTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="用户备注" min-width="150" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.remark || '无' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="报修状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag
                :type="getStatusType(row.status)"
                effect="light"
            >
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right" align="center">
          <template #default="{ row }">
            <div style="display: flex; gap: 8px; justify-content: center;">
              <el-button
                  type="primary"
                  link
                  :icon="View"
                  @click="handleViewDetail(row)"
              >
                详情
              </el-button>
              <el-button
                  type="warning"
                  link
                  :icon="Edit"
                  @click="handleFeedback(row)"
                  :disabled="isOperationDisabled(row.status)"
              >
                维修反馈
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-config-provider :locale="zhCn">
          <el-pagination
              v-model:current-page="pagination.current"
              v-model:page-size="pagination.size"
              :page-sizes="[10, 20, 50, 100]"
              :total="pagination.total"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
          />
        </el-config-provider>
      </div>
    </el-card>

    <!-- 维修反馈对话框 -->
    <el-dialog
        v-model="feedbackDialog.visible"
        :title="`维修反馈 - 报修单号: ${feedbackDialog.currentRow?.id}`"
        width="600px"
        align-center
    >
      <el-form
          :model="feedbackForm"
          :rules="feedbackRules"
          ref="feedbackFormRef"
          label-width="100px"
      >
        <el-form-item label="处理状态" prop="status">
          <el-select
              v-model="feedbackForm.status"
              placeholder="请选择处理状态"
              style="width: 100%"
          >
            <el-option label="已完成" :value="2" />
            <el-option label="待转发" :value="4" />
          </el-select>
          <div class="status-tips">
            <p v-if="feedbackForm.status === 2" class="tip-text">
              • 选择"已完成"：表示当前维修已完成
            </p>
            <p v-if="feedbackForm.status === 4" class="tip-text">
              • 选择"待转发"：表示需要其他维修人员协助处理，等待管理员转发给其他维修人员
            </p>
          </div>
        </el-form-item>

        <el-form-item label="维修过程" prop="repairProcess">
          <el-input
              v-model="feedbackForm.repairProcess"
              type="textarea"
              :rows="4"
              placeholder="请详细描述维修过程和处理结果。如果选择'待转发'，请说明需要转发的原因和下一步处理建议。"
              maxlength="500"
              show-word-limit
          />
        </el-form-item>

      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="feedbackDialog.visible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmitFeedback" :loading="feedbackDialog.loading">
            提交反馈
          </el-button>
        </span>
      </template>
    </el-dialog>


    <!-- 详情对话框 -->
    <el-dialog
        v-model="detailDialog.visible"
        :title="`报修单详情 - 报修单号: ${detailDialog.currentRepair?.id}`"
        width="800px"
        align-center
    >
      <div v-if="detailDialog.currentRepair" class="repair-detail">
        <!-- 基本信息 -->
        <el-descriptions :column="2" border size="large" style="margin-bottom: 20px;">
          <el-descriptions-item label="报修单号">
            {{ detailDialog.currentRepair.id }}
          </el-descriptions-item>

          <el-descriptions-item label="报修分类">
            {{ detailDialog.currentRepair.categoryName }}
          </el-descriptions-item>

          <el-descriptions-item label="故障地址">
            {{ detailDialog.currentRepair.address }}
          </el-descriptions-item>

          <el-descriptions-item label="报修用户">
            {{ detailDialog.currentRepair.nickName }}
          </el-descriptions-item>

          <el-descriptions-item label="联系电话">
            {{ detailDialog.currentRepair.userPhone }}
          </el-descriptions-item>

          <el-descriptions-item label="报修状态">
            <el-tag :type="getStatusType(detailDialog.currentRepair.status)" effect="light">
              {{ getStatusText(detailDialog.currentRepair.status) }}
            </el-tag>
          </el-descriptions-item>

          <el-descriptions-item label="期望处理时间">
            {{ formatDateTime(detailDialog.currentRepair.expectTime) }}
          </el-descriptions-item>

          <el-descriptions-item label="用户备注">
            {{ detailDialog.currentRepair.remark || '无' }}
          </el-descriptions-item>

          <el-descriptions-item label="故障描述" :span="2">
            {{ detailDialog.currentRepair.description }}
          </el-descriptions-item>
        </el-descriptions>

        <!-- 当前维修记录 -->
        <el-descriptions title="当前处理记录" :column="2" border size="large" style="margin-bottom: 20px;">
          <el-descriptions-item label="维修人员">
            {{ detailDialog.currentRepair.repairmanName || '未分配' }}
          </el-descriptions-item>

          <el-descriptions-item label="处理时间" v-if="detailDialog.currentRepair.repairTime">
            {{ formatDateTime(detailDialog.currentRepair.repairTime) }}
          </el-descriptions-item>

          <el-descriptions-item label="处理过程" :span="2" v-if="detailDialog.currentRepair.repairProcess">
            <div style="padding: 8px; background: #f8f9fa; border-radius: 4px;">
              {{ detailDialog.currentRepair.repairProcess }}
            </div>
          </el-descriptions-item>
        </el-descriptions>

        <!-- 历史维修记录 -->
        <div v-if="detailDialog.repairHistory.length > 0" class="history-section">
          <el-divider>
        <span style="color: #409EFF; font-weight: bold;">
          <el-icon><Clock /></el-icon>
          维修历史记录 (共{{ detailDialog.repairHistory.length }}条)
        </span>
          </el-divider>

          <div class="history-list">
            <div v-for="(record, index) in detailDialog.repairHistory" :key="index" class="history-item">
              <div class="history-header">
                <span class="history-index">历史记录 {{ detailDialog.repairHistory.length - index }}</span>
              </div>

              <div class="history-content">
                <div class="history-row">
                  <span class="history-label"><el-icon><User /></el-icon>维修人员:</span>
                  <span class="history-value">{{ record.repairmanName || '未知' }}</span>
                </div>

                <div class="history-row" v-if="record.repairTime">
                  <span class="history-label"><el-icon><Calendar /></el-icon>处理时间:</span>
                  <span class="history-value">{{ formatDateTime(record.repairTime) }}</span>
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
      <el-button @click="detailDialog.visible = false">关闭</el-button>
    </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import {ElConfigProvider, ElMessage, type FormInstance, type FormRules} from 'element-plus'
import {Search, Refresh, Edit, View} from '@element-plus/icons-vue'
import {
  type RepairOrderPageQueryDTO,
  type RepairOrderVO,
  type RepairOrderDTO,
  pageRepairOrderAPI,
  updateRepairOrder,
  getRepairOrderDetailAPI2
} from '@/api/RepairOrder'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'

// 搜索表单
const searchForm = reactive({
  id: undefined as number | undefined,
  address: '',
  nickName: '',
  status: undefined as number | undefined
})

// 分页参数
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 获取当前登录的维修人员姓名
const getCurrentRepairmanName = (): string => {
  try {
    const userStr = localStorage.getItem('userInfo')
    if (userStr) {
      const user = JSON.parse(userStr)
      return user.nickname
    }
    return ''
  } catch (error) {
    return ''
  }
}

// 表格数据
const tableData = ref<RepairOrderVO[]>([])
const loading = ref(false)

// 维修反馈对话框
const feedbackDialog = reactive({
  visible: false,
  loading: false,
  currentRow: null as RepairOrderVO | null
})

// 详情对话框
const detailDialog = reactive({
  visible: false,
  currentRepair: null as RepairOrderVO | null,
  repairHistory: [] as Array<{
    rrId?: number
    repairmanName?: string
    repairTime?: string
    repairProcess?: string
  }>
})

// 维修反馈表单
const feedbackForm = reactive({
  id: 0,
  status: 2, // 默认选择待审核
  repairProcess: '',
})

const feedbackFormRef = ref<FormInstance>()

// 表单验证规则
const feedbackRules: FormRules = {
  status: [
    { required: true, message: '请选择处理状态', trigger: 'change' }
  ],
  repairProcess: [
    { required: true, message: '请输入维修过程', trigger: 'blur' },
    { min: 10, message: '维修过程至少10个字符', trigger: 'blur' }
  ]
}

// 状态映射
const getStatusText = (status: number) => {
  const statusMap = ['待处理', '处理中', '已完成', '已取消', '待转发']
  return statusMap[status] || '未知状态'
}

const getStatusType = (status: number) => {
  const typeMap = ['warning', 'primary', 'success', 'danger', 'warning']
  return typeMap[status] || 'info'
}

// 判断操作是否禁用 - 修改禁用条件
const isOperationDisabled = (status: number) => {
  // 已完成(2)、已取消(3)、待转发(4) 状态下都禁用操作
  return [2, 3, 4].includes(status)
}

// 格式化日期时间
const formatDateTime = (time: string) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  }).replace(/\//g, '-')
}

// 加载报修订单数据
const loadRepairOrders = async () => {
  try {
    loading.value = true

    // 获取当前维修人员姓名
    const repairmanName = getCurrentRepairmanName()
    if (!repairmanName) {
      ElMessage.error('无法获取维修人员信息，请重新登录')
      return
    }
    const params: RepairOrderPageQueryDTO = {
      page: pagination.current,
      pageSize: pagination.size,
      repairmanName: repairmanName,
      ...searchForm
    }

    // 清理空值参数
    Object.keys(params).forEach(key => {
      if (params[key as keyof RepairOrderPageQueryDTO] === '' || params[key as keyof RepairOrderPageQueryDTO] === undefined) {
        delete params[key as keyof RepairOrderPageQueryDTO]
      }
    })

    const result = await pageRepairOrderAPI(params)

    if (result.code === 1) {
      tableData.value = result.data.records
      pagination.total = result.data.total
    } else {
      ElMessage.error(result.msg || '获取数据失败')
    }
  } catch (error) {
    console.error('获取报修订单失败:', error)
    ElMessage.error('获取数据失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  loadRepairOrders()
}

// 重置搜索
const handleReset = () => {
  Object.assign(searchForm, {
    id: undefined,
    address: '',
    nickName: '',
    status: undefined
  })
  pagination.current = 1
  loadRepairOrders()
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

// 打开维修反馈对话框
const handleFeedback = (row: RepairOrderVO) => {
  feedbackDialog.currentRow = row
  Object.assign(feedbackForm, {
    id: row.id,
    status: 2, // 默认选择已完成
    repairProcess: row.repairProcess || '',
  })
  feedbackDialog.visible = true
}

// 提交维修反馈
const handleSubmitFeedback = async () => {
  if (!feedbackFormRef.value) return

  try {
    await feedbackFormRef.value.validate()

    feedbackDialog.loading = true

    // 获取当前维修人员姓名
    const repairmanName = getCurrentRepairmanName()
    if (!repairmanName) {
      ElMessage.error('无法获取维修人员信息，请重新登录')
      return
    }

    const submitData: RepairOrderDTO = {
      id: feedbackForm.id,
      status: feedbackForm.status,
      repairProcess: feedbackForm.repairProcess,
      repairmanName: repairmanName,
      nickName: feedbackDialog.currentRow?.nickName || '',
      userPhone: feedbackDialog.currentRow?.userPhone || '',
      address: feedbackDialog.currentRow?.address || '',
      categoryName: feedbackDialog.currentRow?.categoryName || '',
      description: feedbackDialog.currentRow?.description || '',
      expectTime: feedbackDialog.currentRow?.expectTime || '',
      remark: feedbackDialog.currentRow?.remark || '',
      rrId: feedbackDialog.currentRow?.rrId,
    }

    const result = await updateRepairOrder(submitData)

    if (result.code === 1) {
      const statusText = feedbackForm.status === 2 ? '已完成' : '待转发'
      ElMessage.success(`维修反馈提交成功，状态已更新为${statusText}`)
      feedbackDialog.visible = false
      await loadRepairOrders() // 重新加载数据
    } else {
      ElMessage.error(result.msg || '提交失败')
    }
  } catch (error) {
    console.error('提交维修反馈失败:', error)
    ElMessage.error('提交失败')
  } finally {
    feedbackDialog.loading = false
  }
}

// 查看详情函数
const handleViewDetail = async (row: RepairOrderVO) => {
  try {
    const result = await getRepairOrderDetailAPI2(row.id)

    if (result.code === 1 && result.data && result.data.length > 0) {
      // 第一个元素是最新记录
      detailDialog.currentRepair = result.data[0]

      // 如果有多个记录，从第二个开始是历史记录
      if (result.data.length > 1) {
        detailDialog.repairHistory = result.data.slice(1).map(item => ({
          rrId: item.rrId,
          repairmanName: item.repairmanName,
          repairTime: item.repairTime,
          repairProcess: item.repairProcess
        }))
      } else {
        detailDialog.repairHistory = []
      }

      detailDialog.visible = true
    }
  } catch (error) {
    console.error('获取详情失败:', error)
    ElMessage.error('获取详情失败')
  }
}

// 初始化加载数据
onMounted(() => {
  loadRepairOrders()
})
</script>

<style scoped>
.repair-requests {
  padding: 20px;
}

.search-card {
  margin-bottom: 16px;
  border-radius: 8px;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.table-card {
  border-radius: 8px;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.status-tips {
  margin-top: 8px;
  padding: 8px 12px;
  background-color: #f8f9fa;
  border-radius: 4px;
  border-left: 4px solid #409eff;
}

.tip-text {
  margin: 0;
  font-size: 12px;
  color: #606266;
  line-height: 1.5;
}

:deep(.el-card__header) {
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
  background-color: #fafbfc;
}

:deep(.el-table) {
  border-radius: 8px;
}

:deep(.el-table .cell) {
  padding: 8px 12px;
}

:deep(.el-form-item) {
  margin-bottom: 16px;
}

:deep(.el-dialog__body) {
  padding: 20px;
}
.history-section {
  margin-top: 20px;
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

:deep(.el-descriptions__title) {
  font-weight: 600;
  margin-bottom: 12px;
  color: #303133;
}

:deep(.el-descriptions__label) {
  font-weight: 500;
  background-color: #f5f7fa !important;
}
</style>