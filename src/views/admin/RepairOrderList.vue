<template>
  <div class="repair-order-container">
    <div class="repair-order-header">
      <h1>故障报修管理</h1>
    </div>

    <!-- 搜索条件 -->
    <div class="search-conditions">
      <el-form :model="searchForm" inline>
        <el-form-item label="报修单号">
          <el-input
              v-model="searchForm.id"
              placeholder="请输入报修单号"
              clearable
              style="width: 140px"
          />
        </el-form-item>

        <el-form-item label="报修用户">
          <el-input
              v-model="searchForm.nickName"
              placeholder="请输入报修用户"
              clearable
              style="width: 140px"
          />
        </el-form-item>

        <el-form-item label="故障地址">
          <el-input
              v-model="searchForm.address"
              placeholder="请输入故障地址"
              clearable
              style="width: 150px"
          />
        </el-form-item>

        <el-form-item label="维修人员">
          <el-input
              v-model="searchForm.repairmanName"
              placeholder="请输入维修人员"
              clearable
              style="width: 120px"
          />
        </el-form-item>

        <el-form-item label="报修状态">
          <el-select
              v-model="searchForm.status"
              placeholder="请选择状态"
              clearable
              style="width: 100px"
          >
            <el-option label="待处理" :value="0" />
            <el-option label="处理中" :value="1" />
            <el-option label="已完成" :value="2" />
            <el-option label="已取消" :value="3" />
            <el-option label="待转发" :value="4" />
          </el-select>
        </el-form-item>

        <el-form-item label="处理时间">
          <el-date-picker
              v-model="searchForm.repairTime"
              type="date"
              placeholder="选择处理时间"
              value-format="YYYY-MM-DD"
              style="width: 140px"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">
            查询
          </el-button>
          <el-button :icon="Refresh" @click="handleReset">
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 统计卡片区域 -->
    <div class="statistics-cards">
      <el-row :gutter="20">
        <el-col :span="4">
          <el-card class="stat-card">
            <div class="stat-item">
              <div class="stat-value">{{ statisticsData.totalStat.totalCount }}</div>
              <div class="stat-label">总报修数</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card class="stat-card" shadow="hover">
            <div class="stat-item">
              <div class="stat-value" style="color: #409EFF">{{ statisticsData.totalStat.pendingCount }}</div>
              <div class="stat-label">待处理</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card class="stat-card" shadow="hover">
            <div class="stat-item">
              <div class="stat-value" style="color: #E6A23C">{{ statisticsData.totalStat.processingCount }}</div>
              <div class="stat-label">处理中</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card class="stat-card" shadow="hover">
            <div class="stat-item">
              <div class="stat-value" style="color: #67C23A">{{ statisticsData.totalStat.completedCount }}</div>
              <div class="stat-label">已完成</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card class="stat-card" shadow="hover">
            <div class="stat-item">
              <div class="stat-value" style="color: #F56C6C">{{ statisticsData.totalStat.cancelledCount }}</div>
              <div class="stat-label">已取消</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="4">
          <el-card class="stat-card" shadow="hover">
            <div class="stat-item">
              <div class="stat-value" style="color: #909399">{{ statisticsData.totalStat.pendingTransferCount || 0 }}</div>
              <div class="stat-label">待转发</div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 统计图表区域 -->
    <div class="statistics-charts">
      <el-row :gutter="20">
        <!-- 按地址统计图表 -->
        <el-col :span="12">
          <el-card shadow="hover">
            <div slot="header" class="chart-header">
              <span>按故障地址统计</span>
            </div>
            <div class="chart-container">
              <div ref="addressChartRef" class="chart" :style="{width: '100%', height: '300px'}"></div>
            </div>
          </el-card>
        </el-col>
        <!-- 按状态统计图表 -->
        <el-col :span="12">
          <el-card shadow="hover">
            <div slot="header" class="chart-header">
              <span>按报修状态统计</span>
            </div>
            <div class="chart-container">
              <div ref="statusChartRef" class="chart" :style="{width: '100%', height: '300px'}"></div>
            </div>
          </el-card>
        </el-col>
        <!-- 按时间统计图表 -->
        <el-col :span="24">
          <el-card shadow="hover">
            <div slot="header" class="chart-header">
              <span>按处理时间统计</span>
            </div>
            <div class="chart-container">
              <div ref="timeChartRef" class="chart" :style="{width: '100%', height: '300px'}"></div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 工具栏 -->
    <div class="repair-order-toolbar">
      <div class="toolbar-left">
        <el-button type="primary" :icon="Plus" @click="handleAdd">
          新增报修
        </el-button>
        <el-button
            :icon="Download"
            @click="handleExport"
            :loading="exportLoading"
        >
          {{ exportLoading ? '导出中...' : '导出Excel' }}
        </el-button>
      </div>
    </div>

    <!-- 表格区域 -->
    <RepairOrderTable
        :table-data="tableData"
        @detail="handleDetail"
        @edit="handleEdit"
        @assign="handleAssign"
        @delete="handleDelete"
        @transfer="handleTransfer"
    />

    <!-- 分页区域 -->
    <RepairOrderPagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
    />

    <!-- 分配维修人员弹窗 -->
    <el-dialog
        v-model="assignVisible"
        :title="isTransferAction ? '转发报修单' : '分配维修人员'"
        width="500px"
        :before-close="handleCloseAssign"
    >
      <div class="assign-repairman-dialog">
        <!-- 维修人员信息 -->
        <div class="repairman-info">
          <el-alert
              :title="isTransferAction ? '请选择要转发的维修人员' : '请选择维修人员'"
              :type="isTransferAction ? 'warning' : 'info'"
              :closable="false"
              style="margin-bottom: 16px"
          />
        </div>

        <el-form :model="assignForm" label-width="80px">
          <el-form-item label="维修人员" prop="repairmanName">
            <el-select
                v-model="assignForm.repairmanName"
                placeholder="请选择维修人员"
                style="width: 100%"
                :loading="repairmanLoading"
                filterable
                clearable
            >
              <el-option
                  v-for="repairman in repairmanNameOptions"
                  :key="repairman.value"
                  :label="repairman.label"
                  :value="repairman.value"
              />
            </el-select>
            <div class="repairman-tips" v-if="repairmanList.length > 0" style="margin-top: 8px; color: #67C23A;">
              共有 {{ repairmanList.length }} 名维修人员可选
            </div>
            <div class="repairman-tips" v-else style="margin-top: 8px; color: #E6A23C;">
              暂无维修人员数据
            </div>
          </el-form-item>
        </el-form>
      </div>

      <template #footer>
        <el-button @click="handleCloseAssign">取消</el-button>
        <el-button
            type="primary"
            @click="confirmAssign"
            :loading="repairmanLoading"
        >
          {{ isTransferAction ? '确认转发' : '确定分配' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 添加/编辑报修弹窗 -->
    <el-dialog
        v-model="formVisible"
        :title="formType === 'add' ? '新增报修' : '编辑报修'"
        width="700px"
        :before-close="handleCloseForm"
    >
      <el-form
          :model="repairForm"
          :rules="formRules"
          ref="formRef"
          label-width="100px"
      >
        <el-form-item label="报修用户" prop="userName">
          <el-input
              v-model="repairForm.nickName"
              placeholder="请输入报修用户姓名"
              :disabled="formType !== 'add'"
          />
        </el-form-item>

        <el-form-item label="手机号码" prop="userPhone">
          <el-input
              v-model="repairForm.userPhone"
              placeholder="请输入手机号码"
              maxlength="11"
          />
        </el-form-item>

        <!-- 故障地址选择器 -->
        <el-form-item label="故障地址" prop="address">
          <div class="address-selector">
            <!-- 区域选择 -->
            <div class="area-selection">
              <div class="area-tabs">
                <el-radio-group v-model="selectedArea" @change="handleAreaChange">
                  <el-radio-button
                      v-for="area in locationCategories"
                      :key="area.id"
                      :label="area.name"
                      :disabled="!area.children || area.children.length === 0"
                  >
                    {{ area.name }}
                  </el-radio-button>
                </el-radio-group>
              </div>

              <!-- 修改建筑选择部分 -->
              <div class="building-selection">
                <el-select
                    v-model="selectedBuilding"
                    placeholder="请选择具体建筑"
                    style="width: 100%"
                    size="large"
                    @change="handleBuildingChange"
                    :disabled="!selectedArea || locationLoading"
                    :loading="locationLoading"
                >
                  <el-option
                      v-for="building in buildingOptions"
                      :key="building"
                      :label="building"
                      :value="building"
                  />
                </el-select>
              </div>
            </div>

            <!-- 房间号输入 -->
            <div class="room-input">
              <el-input
                  v-model="roomNumber"
                  placeholder="请输入房间号（选填）"
                  clearable
                  size="large"
                  @input="handleRoomInput"
              >
                <template #prefix>
                  <el-icon><OfficeBuilding /></el-icon>
                </template>
              </el-input>
            </div>

            <!-- 最终地址显示 -->
            <div class="final-address" v-if="finalAddress">
              <el-tag type="success" size="large">
                <el-icon><Location /></el-icon>
                最终地址：{{ finalAddress }}
              </el-tag>
            </div>
          </div>
        </el-form-item>

        <el-form-item label="报修分类" prop="categoryName">
          <el-tree-select
              v-model="repairForm.categoryName"
              :data="repairCategories"
              :props="treeProps"
              :loading="categoryLoading"
              check-strictly
              :render-after-expand="false"
              placeholder="请选择报修分类"
              style="width: 100%"
              size="large"
              popper-class="category-tree-select"
              :default-expand-all="true"
          >
            <template #default="{ data }">
            <span
                class="tree-node"
                :class="{ 'disabled-node': !data.children || data.children.length === 0 }"
            >
              <span class="tree-label">{{ data.name }}</span>
              <span v-if="data.description" class="tree-description">
                {{ data.description }}
              </span>
            </span>
            </template>
          </el-tree-select>
        </el-form-item>

        <el-form-item label="故障描述" prop="description">
          <el-input
              v-model="repairForm.description"
              placeholder="请详细描述故障情况"
              type="textarea"
              :rows="3"
          />
        </el-form-item>

        <el-form-item label="期望时间" prop="expectTime">
          <el-date-picker
              v-model="repairForm.expectTime"
              type="datetime"
              placeholder="选择期望处理时间"
              style="width: 100%"
              value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>

        <el-form-item label="备注">
          <el-input
              v-model="repairForm.remark"
              placeholder="请输入备注信息"
              type="textarea"
              :rows="2"
          />
        </el-form-item>

        <el-form-item label="处理时间" v-if="formType === 'edit'">
          <el-date-picker
              v-model="repairForm.repairTime"
              type="datetime"
              placeholder="选择处理时间"
              style="width: 100%"
              value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>

        <el-form-item label="处理过程" v-if="formType === 'edit'">
          <el-input
              v-model="repairForm.repairProcess"
              placeholder="请输入处理过程"
              type="textarea"
              :rows="3"
          />
        </el-form-item>

        <el-form-item label="报修状态" v-if="formType === 'edit'">
          <el-select v-model="repairForm.status" placeholder="请选择状态" style="width: 100%">
            <el-option label="待处理" :value="0" />
            <el-option label="处理中" :value="1" />
            <el-option label="已完成" :value="2" />
            <el-option label="已取消" :value="3" />
            <el-option label="待转发" :value="4" />
          </el-select>
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


    <!--详情弹窗 -->
    <el-dialog
        v-model="detailDialog.visible"
        :title="`报修单详情 - 报修单号: ${detailDialog.currentRepair?.id}`"
        width="800px"
        align-center
    >
      <div v-if="detailDialog.currentRepair" class="repair-detail">
        <!-- 基本信息 -->
        <el-descriptions :column="2" border size="large" style="margin-bottom: 20px;" title="基本信息">
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

          <el-descriptions-item label="报修时间">
            {{ formatDateTime(detailDialog.currentRepair.createTime || '') }}
          </el-descriptions-item>

          <el-descriptions-item label="用户备注">
            {{ detailDialog.currentRepair.remark || '无' }}
          </el-descriptions-item>

          <el-descriptions-item label="故障描述" :span="2">
            <div style="padding: 8px; background: #f8f9fa; border-radius: 4px;">
              {{ detailDialog.currentRepair.description }}
            </div>
          </el-descriptions-item>

          <el-descriptions-item label="用户反馈" :span="2">
            <div v-if="detailDialog.currentRepair.feedback && detailDialog.currentRepair.feedback.trim()" class="feedback-content">
              {{ detailDialog.currentRepair.feedback }}
            </div>
            <div v-else class="no-feedback">
              <span class="no-feedback-text">暂无反馈</span>
            </div>
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
            <div style="padding: 8px; background: #f8f9fa; border-radius: 4px; border-left: 3px solid #409EFF;">
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
import {ref, onMounted, reactive, computed, watch, nextTick, onUnmounted} from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh, Download, OfficeBuilding, Location } from '@element-plus/icons-vue'
import RepairOrderTable from '@/components/table/RepairOrderTable.vue'
import RepairOrderPagination from '@/components/table/RepairOrderPagination.vue'
import {
  pageFaultListAPI, addRepairOrderAPI, updateRepairOrderAPI, deleteRepairOrderAPI, assignRepairmanAPI,
  type RepairOrderVO, type RepairOrderDTO, type RepairOrderPageQueryDTO, getRepairStatisticsAPI,
  type RepairStatisticsVO, exportRepairOrderAPI, transferAPI, getRepairOrderDetailAPI3
} from '@/api/RepairOrder.ts'
import { getCategoryAPI, type CategoryVO } from '@/api/category'
import { listRepairmanAPI, type UserVO } from "@/api/usersPageQuery.ts";
import * as echarts from 'echarts'
import {getLocationAPI, type LocationVO} from "@/api/location.ts";

// 故障地址相关响应式数据
const selectedArea = ref('')
const selectedBuilding = ref('')
const roomNumber = ref('')
const finalAddress = ref('')
// 地址分类数据
const locationCategories = ref<LocationVO[]>([])
const locationLoading = ref(false)

// 计算属性：将树形地址数据转换为 buildingData 格式
const buildingData = computed(() => {
  const result: Record<string, string[]> = {}

  locationCategories.value.forEach(area => {
    if (area.children && area.children.length > 0) {
      result[area.name] = area.children.map(child => child.name)
    }
  })
  return result
})

// 响应式数据
const tableData = ref<RepairOrderVO[]>([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 统计相关数据
const statisticsData = ref<RepairStatisticsVO>({
  addressStats: [],
  timeStats: [],
  statusStats: [],
  totalStat: {
    totalCount: 0,
    pendingCount: 0,
    processingCount: 0,
    completedCount: 0,
    cancelledCount: 0,
    pendingTransferCount: 0
  }
})
const statisticsLoading = ref(false)

// 图表相关引用
const addressChartRef = ref<HTMLDivElement | null>(null)
const statusChartRef = ref<HTMLDivElement | null>(null)
const timeChartRef = ref<HTMLDivElement | null>(null)

// 分类数据
const repairCategories = ref<CategoryVO[]>([])
const categoryLoading = ref(false)

// 新增：监听统计数据变化，自动重新渲染图表
watch(
    () => statisticsData.value,
    () => {
      // 数据更新后，重新渲染所有图表
      nextTick(() => {
        renderAddressChart()
        renderStatusChart()
        renderTimeChart()
      })
    },
    { deep: true, immediate: true } // 确保初始就执行
)

// 维修人员数据
const repairmanList = ref<UserVO[]>([])
const repairmanLoading = ref(false)

// 搜索表单
const searchForm = reactive<RepairOrderPageQueryDTO>({
  id: undefined,
  address: '',
  repairmanName: '',
  nickName: '',
  status: undefined,
  repairTime: '',
  page: 1,
  pageSize: 10
})

// 弹窗控制
const formVisible = ref(false)
const assignVisible = ref(false)
const formType = ref<'add' | 'edit'>('add')
const isTransferAction = ref(false)

// 当前操作的数据
const currentOrder = ref<RepairOrderVO | null>(null)

// 表单数据
const repairForm = ref<RepairOrderDTO>({
  nickName: '',
  userPhone: '',
  address: '',
  categoryName: '',
  description: '',
  expectTime: '',
  remark: '',
  feedback: '',
  repairmanName: '',
  repairProcess: ''
})

// 分配表单
const assignForm = reactive({
  repairmanName: ''
})

// 计算属性：根据选择的区域显示对应的建筑选项
const buildingOptions = computed(() => {
  if (!selectedArea.value || !buildingData.value) return []
  return buildingData.value[selectedArea.value] || []
})

// 维修人员选项
const repairmanNameOptions = computed(() => {
  return repairmanList.value.map(repairman => ({
    value: repairman.nickname || repairman.username,
    label: repairman.nickname || repairman.username
  }))
})

// 树形选择器配置
const treeProps = {
  value: 'name',
  label: 'name',
  children: 'children',
  disabled: (data: CategoryVO) => {
    // 一级分类（有children的）不可选，只能选择二级分类
    return data.children && data.children.length > 0
  }
}

// 表单验证规则
const formRules = {
  nickName: [
    { required: true, message: '请输入报修用户姓名', trigger: 'blur' }
  ],
  userPhone: [
    { required: true, message: '请输入手机号码', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  address: [
    { required: true, message: '请选择故障地址', trigger: 'blur' }
  ],
  categoryName: [
    { required: true, message: '请选择报修分类', trigger: 'change' }
  ],
  description: [
    { required: true, message: '请输入故障描述', trigger: 'blur' }
  ],
  expectTime: [
    { required: true, message: '请选择期望处理时间', trigger: 'change' }
  ]
}

const formRef = ref()

// 加载维修人员数据
const loadRepairmanList = async () => {
  repairmanLoading.value = true
  try {
    const response = await listRepairmanAPI()
    if (response.code === 1) {
      repairmanList.value = response.data || []
    } else {
      ElMessage.error(response.msg || '加载维修人员失败')
    }
  } catch (error) {
    ElMessage.error('加载维修人员失败')
  } finally {
    repairmanLoading.value = false
  }
}

// 故障地址处理方法
const handleAreaChange = () => {
  selectedBuilding.value = ''
  roomNumber.value = ''
  updateFinalAddress()
}

const handleBuildingChange = () => {
  updateFinalAddress()
}

const handleRoomInput = () => {
  updateFinalAddress()
}

// 更新最终地址
const updateFinalAddress = () => {
  let address = '';

  if (selectedArea.value && selectedBuilding.value) {
    address = selectedArea.value + selectedBuilding.value + (roomNumber.value || '');
  } else if (selectedArea.value && !selectedBuilding.value) {
    address = selectedArea.value + (roomNumber.value || '');
  } else if (!selectedArea.value && selectedBuilding.value) {
    address = selectedBuilding.value + (roomNumber.value || '');
  }

  finalAddress.value = address;

  // 同时更新表单中的地址
  if (address) {
    repairForm.value.address = address;
  } else {
    repairForm.value.address = '';
  }
};

// 监听最终地址变化，更新表单数据
watch(finalAddress, (newAddress) => {
  repairForm.value.address = newAddress
})

// 重置地址选择器
const resetAddressSelector = () => {
  selectedArea.value = ''
  selectedBuilding.value = ''
  roomNumber.value = ''
  finalAddress.value = ''
}

// 编辑时填充地址数据
const fillAddressData = (address: string) => {
  if (!address) {
    resetAddressSelector();
    return;
  }

  // 设置最终地址
  finalAddress.value = address;

  // 重置选择器
  selectedArea.value = '';
  selectedBuilding.value = '';
  roomNumber.value = '';

  // 根据地址内容填充选择器
  // 从动态数据中检测区域
  for (const area of locationCategories.value) {
    if (address.startsWith(area.name)) {
      selectedArea.value = area.name;
      const remainingAddress = address.substring(area.name.length);

      // 从建筑选项中查找匹配的建筑
      const buildings = buildingData.value[area.name] || [];
      let matchedBuilding = '';

      // 尝试匹配建筑
      for (const building of buildings) {
        if (remainingAddress.startsWith(building)) {
          matchedBuilding = building;
          selectedBuilding.value = building;

          // 提取房间号（剩余的部分）
          const remaining = remainingAddress.substring(building.length);
          if (remaining) {
            roomNumber.value = remaining;
          }
          break;
        }
      }

      // 如果没有匹配到完整的建筑名
      if (!matchedBuilding) {
        // 查找包含数字的部分作为分割点
        const match = remainingAddress.match(/(.*?\d+.*?)(\d+.*)?$/);
        if (match) {
          const possibleBuilding = match[1] || '';
          const possibleRoom = match[2] || '';

          // 在建筑选项中查找包含可能的建筑名的选项
          const foundBuilding = buildings.find(b =>
              b.includes(possibleBuilding) || possibleBuilding.includes(b)
          );

          if (foundBuilding) {
            selectedBuilding.value = foundBuilding;
            // 计算房间号
            const roomStartIndex = remainingAddress.indexOf(foundBuilding) + foundBuilding.length;
            if (roomStartIndex < remainingAddress.length) {
              roomNumber.value = remainingAddress.substring(roomStartIndex);
            }
          } else if (possibleRoom) {
            // 如果没有找到建筑，假设所有都是房间号
            roomNumber.value = remainingAddress;
          }
        } else {
          // 如果没有数字，直接设为房间号
          roomNumber.value = remainingAddress;
        }
      }
      break;
    }
  }
};

// 获取报修分类数据
const loadRepairCategories = async () => {
  categoryLoading.value = true
  try {
    const response = await getCategoryAPI()
    if (response.code === 1) {
      // 将扁平数据转换为树形结构
      repairCategories.value = buildCategoryTree(response.data)
    } else {
      ElMessage.error('获取报修分类失败')
    }
  } catch (error) {
    ElMessage.error('获取报修分类失败')
  } finally {
    categoryLoading.value = false
  }
}

// 构建树形结构
const buildCategoryTree = (categorys: CategoryVO[]): CategoryVO[] => {
  const categoryMap = new Map<number, CategoryVO>()
  const tree: CategoryVO[] = []

  // 首先将所有分类存入map
  categorys.forEach(category => {
    categoryMap.set(category.id, { ...category, children: [] })
  })

  // 构建树形结构
  categorys.forEach(category => {
    const node = categoryMap.get(category.id)
    if (node) {
      if (category.parentId === 0) {
        // 根节点
        tree.push(node)
      } else {
        // 子节点，找到父节点并添加
        const parent = categoryMap.get(category.parentId)
        if (parent && parent.children) {
          parent.children.push(node)
        }
      }
    }
  })

  // 对每个节点的子节点按 sortOrder 排序
  const sortChildren = (categories: CategoryVO[]) => {
    categories.forEach(category => {
      if (category.children && category.children.length > 0) {
        category.children.sort((a, b) => a.sortOrder - b.sortOrder)
        sortChildren(category.children)
      }
    })
    return categories.sort((a, b) => a.sortOrder - b.sortOrder)
  }

  return sortChildren(tree)
}

// 加载统计数据
const loadStatisticsData = async () => {
  statisticsLoading.value = true;
  try {
    const response = await getRepairStatisticsAPI({
      ...searchForm,
      page: 1,
      pageSize: 10
    });
    if (response.code === 1) {
      statisticsData.value = response.data || {
        addressStats: [],
        timeStats: [],
        statusStats: [],
        totalStat: {
          totalCount: 0,
          pendingCount: 0,
          processingCount: 0,
          completedCount: 0,
          cancelledCount: 0
        }
      };

      // 使用 nextTick 确保DOM更新后再渲染图表
      nextTick(() => {
        renderAddressChart();
        renderStatusChart();
        renderTimeChart();
      });
    } else {
      ElMessage.error(response.msg || '加载统计数据失败');
    }
  } catch (error) {
    ElMessage.error('加载统计数据失败');
    console.error('统计数据加载异常:', error);
    statisticsData.value = {
      addressStats: [],
      timeStats: [],
      statusStats: [],
      totalStat: {
        totalCount: 0,
        pendingCount: 0,
        processingCount: 0,
        completedCount: 0,
        cancelledCount: 0
      }
    };
  } finally {
    statisticsLoading.value = false;
  }
};

// 渲染地址统计图表（饼图）
const renderAddressChart = () => {
  if (!addressChartRef.value) return;

  const chartDom = addressChartRef.value;

  // 先销毁现有的图表实例
  const existingChart = echarts.getInstanceByDom(chartDom);
  if (existingChart) {
    existingChart.dispose();
  }

  const myChart = echarts.init(chartDom);

  const data = statisticsData.value.addressStats.map(item => ({
    name: item.address,
    value: item.count
  }));

  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      textStyle: { fontSize: 12 },
      show: data.length > 0
    },
    series: [
      {
        name: '报修数量',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: data.length === 0,
          position: 'center',
          formatter: '暂无数据',
          fontSize: 16,
          color: '#909399'
        },
        emphasis: {
          label: {
            show: data.length > 0,
            fontSize: 16,
            fontWeight: 'bold'
          }
        },
        labelLine: { show: false },
        data: data
      }
    ]
  };

  myChart.setOption(option);

  // 清理之前的resize监听器
  const resizeHandler = () => myChart.resize();
  window.removeEventListener('resize', resizeHandler);
  window.addEventListener('resize', resizeHandler);
};

// 渲染状态统计图表（柱状图）
const renderStatusChart = () => {
  if (!statusChartRef.value) return;

  const chartDom = statusChartRef.value;

  // 先销毁现有的图表实例
  const existingChart = echarts.getInstanceByDom(chartDom);
  if (existingChart) {
    existingChart.dispose();
  }

  const myChart = echarts.init(chartDom);

  const xData = statisticsData.value.statusStats.map(item => item.statusName);
  const yData = statisticsData.value.statusStats.map(item => item.count);
  const colorList = ['#409EFF', '#E6A23C', '#909399', '#67C23A', '#F56C6C', '#9B59B6'];

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      formatter: '{b}: {c} 单'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: xData.length > 0 ? '3%' : '40%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: xData,
      axisLabel: { fontSize: 12 },
      show: xData.length > 0
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        formatter: '{value} 单'
      },
      minInterval: 1,
      interval: 1,
      min: 0,
      show: xData.length > 0
    },
    series: [
      {
        name: '报修数量',
        type: 'bar',
        data: yData,
        itemStyle: {
          color: (params: any) => colorList[params.dataIndex % colorList.length]
        },
        label: {
          show: xData.length > 0,
          position: 'top',
          fontSize: 12
        },
        barWidth: '40%'
      }
    ],
    graphic: xData.length === 0 ? [{
      type: 'text',
      left: 'center',
      top: '50%',
      style: {
        text: '暂无数据',
        fontSize: 16,
        color: '#909399'
      }
    }] : []
  };

  myChart.setOption(option);

  // 清理之前的resize监听器
  const resizeHandler = () => myChart.resize();
  window.removeEventListener('resize', resizeHandler);
  window.addEventListener('resize', resizeHandler);
};

// 渲染时间统计图表（折线图）
const renderTimeChart = () => {
  if (!timeChartRef.value) return;

  const chartDom = timeChartRef.value;

  // 先销毁现有的图表实例
  const existingChart = echarts.getInstanceByDom(chartDom);
  if (existingChart) {
    existingChart.dispose();
  }

  const myChart = echarts.init(chartDom);

  const xData = statisticsData.value.timeStats.map(item => item.date);
  const yData = statisticsData.value.timeStats.map(item => item.count);

  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: '日期: {b}<br/>报修数: {c} 单'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: xData.length > 0 ? '15%' : '40%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: xData,
      axisLabel: { fontSize: 11, rotate: 30 },
      show: xData.length > 0
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        formatter: '{value} 单'
      },
      minInterval: 1,
      interval: 1,
      min: 0,
      show: xData.length > 0
    },
    series: [
      {
        name: '报修数量',
        type: 'line',
        data: yData,
        smooth: true,
        itemStyle: { color: '#409EFF' },
        lineStyle: { width: 2 },
        symbol: 'circle',
        symbolSize: 6,
        label: {
          show: xData.length > 0,
          fontSize: 11
        }
      }
    ],
    graphic: xData.length === 0 ? [{
      type: 'text',
      left: 'center',
      top: '50%',
      style: {
        text: '暂无数据',
        fontSize: 16,
        color: '#909399'
      }
    }] : []
  };

  myChart.setOption(option);

  // 清理之前的resize监听器
  const resizeHandler = () => myChart.resize();
  window.removeEventListener('resize', resizeHandler);
  window.addEventListener('resize', resizeHandler);
};

// 方法
const handleReset = () => {
  Object.assign(searchForm, {
    id: undefined,
    address: '',
    repairmanName: '',
    userName: '',
    status: undefined,
    repairTime: '',
    page: 1,
    pageSize: 10
  })
  currentPage.value = 1
  loadRepairOrders()
}

const handleAdd = () => {
  formType.value = 'add'
  repairForm.value = {
    nickName: '',
    userPhone: '',
    address: '',
    categoryName: '',
    description: '',
    expectTime: '',
    remark: '',
    repairmanName: '',
    repairProcess: ''
  }
  resetAddressSelector()
  formVisible.value = true
}

const handleEdit = (order: RepairOrderVO) => {
  formType.value = 'edit'
  currentOrder.value = order
  // 强制格式化日期 - 将 ISO 格式转换为日期选择器需要的格式
  let formattedExpectTime = order.expectTime
  let formattedRepairTime = order.repairTime
  if (formattedExpectTime && formattedExpectTime.includes('T')) {
    formattedExpectTime = formattedExpectTime.replace('T', ' ')
  }
  if (formattedRepairTime && formattedRepairTime.includes('T')) {
    formattedRepairTime = formattedRepairTime.replace('T', ' ')
  }

  repairForm.value = {
    id: order.id,
    nickName: order.nickName,
    userPhone: order.userPhone,
    address: order.address,
    categoryName: order.categoryName,
    description: order.description,
    expectTime: formattedExpectTime,  // 使用转换后的日期
    remark: order.remark,
    repairmanName: order.repairmanName,
    repairTime: formattedRepairTime,
    repairProcess: order.repairProcess,
    status: order.status,
    rrId: order.rrId
  }
  fillAddressData(order.address)
  formVisible.value = true
}

// 在分配弹窗打开时加载维修人员数据
const handleAssign = (order: RepairOrderVO) => {
  currentOrder.value = order
  assignForm.repairmanName = order.repairmanName || ''
  assignVisible.value = true
  // 打开分配弹窗时加载维修人员数据
  loadRepairmanList()
}

// 在转发按钮点击时加载维修人员数据，弹窗和上面的分配弹窗复用了
const handleTransfer = (order: RepairOrderVO) => {
  currentOrder.value = order
  assignForm.repairmanName = order.repairmanName || ''
  isTransferAction.value = true  // 转发
  assignVisible.value = true
  loadRepairmanList()
}

const handleCloseForm = () => {
  formVisible.value = false
  formRef.value?.resetFields()
  currentOrder.value = null
  resetAddressSelector()
}

const handleCloseAssign = () => {
  assignVisible.value = false
  assignForm.repairmanName = ''
  currentOrder.value = null
}

const submitForm = async () => {
  if (!formRef.value) return

  try {
    const valid = await formRef.value.validate()
    if (valid) {
      if (formType.value === 'add') {
        const response = await addRepairOrderAPI(repairForm.value)
        if (response.code === 1) {
          ElMessage.success('报修单添加成功')
          handleCloseForm()
          loadRepairOrders()
        } else {
          ElMessage.error(response.msg || '添加失败')
        }
      } else {
        const response = await updateRepairOrderAPI(repairForm.value)
        if (response.code === 1) {
          ElMessage.success('报修单更新成功')
          handleCloseForm()
          loadRepairOrders()
        } else {
          ElMessage.error(response.msg || '更新失败')
        }
      }
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('操作失败，请重试')
  }
}

const confirmAssign = async () => {
  if (!assignForm.repairmanName) {
    ElMessage.warning('请选择维修人员')
    return
  }
  if (!currentOrder.value) {
    ElMessage.error('未找到要分配的报修单')
    return
  }
  repairmanLoading.value = true

  try {
    let response
    if (isTransferAction.value) {
      // 转发
      response = await transferAPI({
        id: currentOrder.value.id,
        repairmanName: assignForm.repairmanName,
        rrId: currentOrder.value.rrId
      })
    } else {
      // 分配
      response = await assignRepairmanAPI({
        id: currentOrder.value.id,
        repairmanName: assignForm.repairmanName
      })
    }

    if (response.code === 1) {
      ElMessage.success('维修人员分配成功')
      handleCloseAssign()
      loadRepairOrders()
    } else {
      ElMessage.error(response.msg || '分配失败')
    }
  } catch (error) {
    console.error('分配失败:', error)
    ElMessage.error('分配失败，请重试')
  } finally {
    repairmanLoading.value = false
  }
}

const handleDelete = async (order: RepairOrderVO) => {
  try {
    await ElMessageBox.confirm(
        `确定要删除报修单 "${order.id}" 吗？`,
        '删除确认',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        }
    )

    const response = await deleteRepairOrderAPI(order.rrId)
    if (response.code === 1) {
      ElMessage.success('报修单删除成功')
      loadRepairOrders()
    } else {
      ElMessage.error(response.msg || '删除失败')
    }
  } catch (error) {
    ElMessage.info('已取消删除')
  }
}

const handleSizeChange = (size: number) => {
  pageSize.value = size
  searchForm.pageSize = size
  loadRepairOrders()
}

const handleCurrentChange = (page: number) => {
  currentPage.value = page
  searchForm.page = page
  loadRepairOrders()
}

// 导出状态
const exportLoading = ref(false)

// 导出Excel
const handleExport = async () => {
  exportLoading.value = true
  try {
    // 构建查询参数（与搜索条件一致）
    const exportParams: RepairOrderPageQueryDTO = {
      ...searchForm,
      page: undefined,  // 导出不需要分页
      pageSize: undefined
    }

    // 移除空值参数
    Object.keys(exportParams).forEach(key => {
      if (exportParams[key as keyof RepairOrderPageQueryDTO] === '' ||
          exportParams[key as keyof RepairOrderPageQueryDTO] === undefined) {
        delete exportParams[key as keyof RepairOrderPageQueryDTO]
      }
    })

    const response = await exportRepairOrderAPI(exportParams)

    // 创建Blob对象并下载
    const blob = new Blob([response], {
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
    })

    // 创建下载链接
    const downloadUrl = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = downloadUrl

    // 生成文件名
    const date = new Date()
    const dateStr = `${date.getFullYear()}${(date.getMonth() + 1).toString().padStart(2, '0')}${date.getDate().toString().padStart(2, '0')}`
    link.download = `故障报修数据_${dateStr}.xlsx`

    // 触发下载
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(downloadUrl)

    ElMessage.success('导出成功')

  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败，请重试')
  } finally {
    exportLoading.value = false
  }
}

// 加载报修订单数据
const loadRepairOrders = async () => {
  loading.value = true
  try {
    // 构建查询参数
    const params: RepairOrderPageQueryDTO = {
      ...searchForm,
      page: currentPage.value,
      pageSize: pageSize.value
    }

    // 移除空值参数
    Object.keys(params).forEach(key => {
      if (params[key as keyof RepairOrderPageQueryDTO] === '' || params[key as keyof RepairOrderPageQueryDTO] === undefined) {
        delete params[key as keyof RepairOrderPageQueryDTO]
      }
    })

    const response = await pageFaultListAPI(params)
    if (response.code === 1) {
      tableData.value = response.data.records || []
      total.value = response.data.total || 0
      console.log(response.data.records)
      // 加载统计数据
      await loadStatisticsData()
    } else {
      ElMessage.error(response.msg || '加载报修数据失败')
    }
  } catch (error) {
    ElMessage.error('加载报修数据失败')
  } finally {
    loading.value = false
  }
}

// 搜索方法
const handleSearch = () => {
  currentPage.value = 1
  loadRepairOrders()
}


// 详情对话框
const detailDialog = reactive({
  visible: false,
  currentRepair: null as RepairOrderVO | null,
  repairHistory: [] as Array<{
    rrId?: number
    repairmanName?: string
    repairTime?: string
    repairProcess?: string
    isHistory?: boolean
  }>
})

// 查看详情
const handleDetail = async (row: RepairOrderVO) => {
  try {
    const result = await getRepairOrderDetailAPI3(row.id)

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

// 状态映射函数
const getStatusText = (status: number) => {
  const statusMap = ['待处理', '处理中', '已完成', '已取消', '待转发']
  return statusMap[status] || '未知状态'
}

const getStatusType = (status: number) => {
  const typeMap = ['warning', 'primary', 'success', 'danger', 'warning']
  return typeMap[status] || 'info'
}

// 格式化时间函数
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


// 获取地址分类数据
const loadLocationCategories = async () => {
  locationLoading.value = true
  try {
    const response = await getLocationAPI()
    if (response.code === 1) {
      // 将扁平数据转换为树形结构
      locationCategories.value = buildLocationTree(response.data)
    } else {
      ElMessage.error('获取地址分类失败')
    }
  } catch (error) {
    ElMessage.error('获取地址分类失败')
    console.error('获取地址分类失败:', error)
  } finally {
    locationLoading.value = false
  }
}

// 构建地址树形结构
const buildLocationTree = (locations: LocationVO[]): LocationVO[] => {
  const locationMap = new Map<number, LocationVO>()
  const tree: LocationVO[] = []

  locations.forEach(location => {
    locationMap.set(location.id, { ...location, children: [] })
  })

  locations.forEach(location => {
    const node = locationMap.get(location.id)
    if (node) {
      if (location.parentId === 0) {
        tree.push(node)
      } else {
        const parent = locationMap.get(location.parentId)
        if (parent && parent.children) {
          parent.children.push(node)
        }
      }
    }
  })

  return tree
}

// 组件挂载时加载数据
onMounted(() => {
  loadRepairCategories() // 加载分类数据
  loadRepairOrders()   // 加载报修数据
  loadLocationCategories()    // 加载地址数据
})

// 在组件卸载时清理图表
onUnmounted(() => {
  [addressChartRef.value, statusChartRef.value, timeChartRef.value].forEach(chartDom => {
    if (chartDom) {
      const chartInstance = echarts.getInstanceByDom(chartDom);
      if (chartInstance) {
        chartInstance.dispose();
      }
    }
  });
});
</script>

<style scoped>
.repair-order-container {
  padding: 20px;
  background-color: white;
  min-height: 100vh;
}

.repair-order-header {
  text-align: center;
  margin-bottom: 30px;
}

.repair-order-header h1 {
  font-size: 2.2rem;
  margin-bottom: 8px;
  font-weight: 600;
  color: #303133;
}

.repair-order-header p {
  font-size: 1rem;
  color: #606266;
}

/* 搜索条件样式 */
.search-conditions {
  margin-bottom: 20px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.search-conditions .el-form-item {
  margin-bottom: 0;
}

/* 统计卡片样式 */
.statistics-cards {
  margin-bottom: 20px;
}

.stat-card {
  text-align: center;
  border-radius: 8px;
}

.stat-item {
  padding: 16px 0;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #409EFF;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

/* 统计图表区域 */
.statistics-charts {
  margin-bottom: 20px;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-container {
  width: 100%;
  height: 100%;
}

/* 工具栏样式 */
.repair-order-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
}

.toolbar-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

/* 图片预览样式 */
.image-preview {
  text-align: center;
}

.carousel-image {
  width: 100%;
  height: 400px;
  object-fit: contain;
}

.no-images-text {
  text-align: center;
  color: #909399;
  padding: 40px 0;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .search-conditions .el-form--inline .el-form-item {
    display: block;
    margin-right: 0;
    margin-bottom: 10px;
  }

  .repair-order-toolbar {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }

  .toolbar-left {
    justify-content: center;
  }
}

@media (max-width: 768px) {
  .repair-order-container {
    padding: 16px;
  }

  .repair-order-header h1 {
    font-size: 1.8rem;
  }

  .statistics-cards .el-col {
    margin-bottom: 16px;
  }

  .statistics-charts .el-col {
    width: 100%;
    margin-bottom: 20px;
  }
}

/* 树形选择器样式 */
.tree-node {
  display: flex;
  flex-direction: column;
  padding: 4px 0;
}

.tree-label {
  font-weight: 500;
  color: #303133;
}

.tree-description {
  font-size: 12px;
  color: #909399;
  margin-top: 2px;
}

.disabled-node .tree-label {
  color: #c0c4cc;
}

/* 树形选择器下拉框样式 */
:deep(.category-tree-select) {
  .el-tree-node__content {
    height: auto;
    min-height: 36px;
  }

  .el-tree-node.is-disabled > .el-tree-node__content {
    cursor: not-allowed;
  }
}

.address-selector {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 16px;
  background-color: #f8f9fa;
}

.area-selection {
  margin-bottom: 16px;
}

.area-tabs {
  margin-bottom: 12px;
}

.building-selection {
  margin-bottom: 12px;
}

.room-input {
  margin-bottom: 12px;
}

.final-address {
  text-align: center;
  padding: 8px 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .address-selector {
    padding: 12px;
  }

  .area-tabs .el-radio-group {
    display: flex;
    flex-direction: column;
    gap: 8px;
  }

  .area-tabs .el-radio-button {
    width: 100%;
  }
}

/* 分配弹窗样式优化 */
.assign-repairman-dialog {
  padding: 10px 0;
}

.repairman-option {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.repairman-name {
  font-weight: 500;
}

.selected-repairman {
  margin-top: 8px;
}

.repairman-tips {
  font-size: 12px;
  margin-top: 4px;
}

.repair-detail {
  max-height: 70vh;
  overflow-y: auto;
  padding-right: 10px;
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
.location-loading {
  text-align: center;
  padding: 20px;
  color: #909399;
}
</style>