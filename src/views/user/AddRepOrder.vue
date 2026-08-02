<script setup lang="ts">
import { ref, reactive, onMounted, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import {User, Avatar, Iphone, Location, Check, Refresh, OfficeBuilding, InfoFilled} from '@element-plus/icons-vue'
import {getCategoriesAPI, submitRepairAPI} from '@/api/RepairOrder.ts'
import { type CategoryVO } from '@/api/category.ts'
import {type FAQVO, getFAQsAPI} from "@/api/noticeAndFAQ.ts";
import {getLocationAPI, type LocationVO} from "@/api/location.ts";
// 表单数据
const repairForm = reactive({
  nickName: '',
  username: '',
  userPhone: '',
  address: '',
  categoryName: '',
  description: '',
  expectTime: '',
  remark: ''
})

// FAQ相关数据
const allFAQs = ref<FAQVO[]>([])  // 存储所有FAQ
const selectedSolutions = ref<string[]>([])  // 选中的解决方案
const showSolutionPanel = ref(false)  // 是否显示面板

// 响应式数据
const selectedArea = ref('')
const selectedBuilding = ref('')
const roomNumber = ref('')
const finalAddress = ref('')


// 地址分类数据
const locationCategories = ref<LocationVO[]>([])
const locationLoading = ref(false)

// 计算属性：将树形数据转换为 buildingData 格式
const buildingData = computed(() => {
  const result: Record<string, string[]> = {}

  locationCategories.value.forEach(area => {
    if (area.children && area.children.length > 0) {
      result[area.name] = area.children.map(child => child.name)
    }
  })

  return result
})

// 根据选择的区域显示对应的建筑选项
const buildingOptions = computed(() => {
  if (!selectedArea.value || !buildingData.value) return []
  return buildingData.value[selectedArea.value] || []
})

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

// 构建地址树形结构（复用已有的构建函数，或创建新函数）
const buildLocationTree = (locations: LocationVO[]): LocationVO[] => {
  const locationMap = new Map<number, LocationVO>()
  const tree: LocationVO[] = []

  // 首先将所有地址存入map
  locations.forEach(location => {
    locationMap.set(location.id, { ...location, children: [] })
  })

  // 构建树形结构
  locations.forEach(location => {
    const node = locationMap.get(location.id)
    if (node) {
      if (location.parentId === 0) {
        // 根节点（一级地址）
        tree.push(node)
      } else {
        // 子节点（二级地址），找到父节点并添加
        const parent = locationMap.get(location.parentId)
        if (parent && parent.children) {
          parent.children.push(node)
        }
      }
    }
  })

  return tree
}


// 处理方法
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
  if (selectedArea.value && selectedBuilding.value) {
    let address = `${selectedArea.value}${selectedBuilding.value}`
    if (roomNumber.value) {
      address += `${roomNumber.value}`
    }
    finalAddress.value = address
  } else {
    finalAddress.value = ''
  }
}

// 监听最终地址变化，更新表单数据
watch(finalAddress, (newAddress) => {
  repairForm.address = newAddress
})

// 报修分类数据
const repairCategories = ref<CategoryVO[]>([])
const categoryLoading = ref(false)

// 表单验证规则
const repairRules = {
  nickName: [
    { required: true, message: '请输入用户姓名', trigger: 'blur' },
    { min: 2, max: 4, message: '姓名长度在 2 到 4 个字符', trigger: 'blur' }
  ],
  userPhone: [
    { required: true, message: '请输入手机号码', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  categoryName: [
    { required: true, message: '请选择报修分类', trigger: 'change' }
  ],
  address: [
    { required: true, message: '请输入故障地址', trigger: 'blur' },
    { min: 5, message: '地址至少5个字符', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入故障描述', trigger: 'blur' },
    { min: 5, message: '请详细描述故障现象（至少5个字符）', trigger: 'blur' }
  ],
  expectTime: [
    { required: true, message: '请选择期望解决时间', trigger: 'change' }
  ]
}

const repairFormRef = ref()
const submitting = ref(false)

// 获取报修分类数据
const loadRepairCategories = async () => {
  categoryLoading.value = true
  try {
    const response = await getCategoriesAPI()
    if (response.code === 1) {
      // 将扁平数据转换为树形结构
      repairCategories.value = buildCategoryTree(response.data)
    } else {
      ElMessage.error('获取报修分类失败')
    }
  } catch (error) {
    ElMessage.error('获取报修分类失败')
    console.error('获取报修分类失败:', error)
  } finally {
    categoryLoading.value = false
  }
}

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

// 禁用今天之前的日期
const disabledDate = (time: Date) => {
  return time.getTime() < Date.now() - 24 * 60 * 60 * 1000
}

// 提交报修
const submitRepair = async () => {
  if (!repairFormRef.value) return
  console.log(repairForm)
  try {
    const valid = await repairFormRef.value.validate()
    if (valid) {
      submitting.value = true
      // 调用提交API
      const response = await submitRepairAPI(repairForm)
      if(response.code === 1){
        ElMessage.success('报修提交成功！')
        resetForm()
      }
    }
  } catch (error) {
    ElMessage.error('提交失败，请重试')
  } finally {
    submitting.value = false
  }
}

// 重置表单
const resetForm = () => {
  // 重置 Element Plus 表单字段
  repairFormRef.value?.resetFields()

  // 手动重置所有表单字段到空值
  Object.assign(repairForm, {
    nickName: '',
    userPhone: '',
    address: '',
    categoryName: '',
    description: '',
    expectTime: '',
    remark: ''
  })
  // 恢复用户名（从 localStorage 重新获取）
  const userInfoStr = localStorage.getItem('userInfo')
  if (userInfoStr) {
    const userInfo = JSON.parse(userInfoStr)
    if (userInfo.username) {
      repairForm.username = userInfo.username
      repairForm.nickName = userInfo.nickname;
    }
  }
  // 重置地址选择相关数据
  selectedArea.value = ''
  selectedBuilding.value = ''
  roomNumber.value = ''
  finalAddress.value = ''
}



// 加载所有FAQ数据
const loadAllFAQs = async () => {
  try {
    // 查询空字符串获取所有FAQ
    const response = await getFAQsAPI('')
    if (response.code === 1 && response.data) {
      allFAQs.value = response.data
    }
  } catch (error) {
    console.error('加载FAQ失败:', error)
  }
}

// 匹配函数
const findSolutions = (categoryName: string) => {
  selectedSolutions.value = []
  showSolutionPanel.value = false

  if (!categoryName) return

  // 遍历所有FAQ数据
  for (const faq of allFAQs.value) {
    if (!faq.reason || !faq.solution) continue

    // 检查原因中是否包含分类名称的关键词
    if (faq.reason.includes(categoryName)) {
      // 直接返回整个解决方案
      const solutions = faq.solution.split('；')
      selectedSolutions.value = solutions.map(s => s.trim())
      showSolutionPanel.value = true
      return
    }

    // 检查分类名称是否包含FAQ原因中的关键词
    const reasons = faq.reason.split('；')
    for (const reason of reasons) {
      const keywords = extractKeywords(reason)
      for (const keyword of keywords) {
        if (categoryName.includes(keyword) && keyword.length > 2) {
          // 找到匹配的解决方案
          const solutions = faq.solution.split('；')
          const index = reasons.indexOf(reason)
          if (solutions[index]) {
            selectedSolutions.value = [solutions[index].trim()]
            showSolutionPanel.value = true
            return
          }
        }
      }
    }
  }

  // 如果没有找到，显示通用提示
  if (selectedSolutions.value.length === 0) {
    selectedSolutions.value = ['请在下方详细描述您的问题']
    showSolutionPanel.value = true
  }
}

// 提取关键词的简单函数
const extractKeywords = (text: string): string[] => {
  // 移除括号内容，提取主要关键词
  const cleanText = text.replace(/（[^）]*）|[（）]/g, '').trim()
  // 按中文分隔符分割
  const keywords = cleanText.split(/[、;；]/)
  // 过滤空值和短词
  return keywords.filter(keyword => keyword.trim().length > 1)
}

// 监听分类变化
watch(() => repairForm.categoryName, (newCategory) => {
  findSolutions(newCategory)
})


// 在组件挂载后设置 账号和姓名，还有加载分类数据
onMounted(() => {
  const userInfoStr = localStorage.getItem('userInfo')
  if (userInfoStr) {
    const userInfo = JSON.parse(userInfoStr)
    if (userInfo.username) {
      repairForm.username = userInfo.username
      repairForm.nickName = userInfo.nickname
    }
  }
  loadRepairCategories()
  loadAllFAQs()
  loadLocationCategories()
})
</script>


<template>
  <div class="repair-container">
    <div class="repair-header">
      <h1>我要报修</h1>
      <p>请填写报修信息，我们将尽快为您处理</p>
    </div>

    <div class="repair-form-container">
      <el-card class="repair-card" shadow="hover">
        <el-form
            :model="repairForm"
            :rules="repairRules"
            ref="repairFormRef"
            label-width="120px"
            label-position="left"
            status-icon
        >
          <!-- 用户信息行 -->
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="用户姓名" prop="nickName">
                <el-input
                    v-model="repairForm.nickName"
                    placeholder="请输入您的姓名"
                    clearable
                    size="large"
                    disabled="disabled"
                >
                  <template #prefix>
                    <el-icon><User /></el-icon>
                  </template>
                </el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="账号" prop="account">
                <el-input
                    v-model="repairForm.username"
                    clearable
                    size="large"
                    disabled="disabled"
                >
                  <template #prefix>
                    <el-icon><Avatar /></el-icon>
                  </template>
                </el-input>
              </el-form-item>
            </el-col>
          </el-row>

          <!-- 联系信息和报修分类 -->
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="手机号码" prop="userPhone">
                <el-input
                    v-model="repairForm.userPhone"
                    placeholder="请输入手机号码"
                    clearable
                    size="large"
                >
                  <template #prefix>
                    <el-icon><Iphone /></el-icon>
                  </template>
                </el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="报修分类" prop="categoryId">
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

              <!-- 自助解决方案 -->
              <div v-if="showSolutionPanel && selectedSolutions.length > 0" class="solution-box">
                <div class="solution-title">
                  <el-icon><InfoFilled /></el-icon>
                  自助解决方案
                </div>
                <div class="solution-content">
                  <p>在提交报修前，您可以尝试：</p>
                  <ul>
                    <li v-for="(solution, index) in selectedSolutions" :key="index">
                      {{ solution }}
                    </li>
                  </ul>
                </div>
              </div>
            </el-col>
          </el-row>

          <!-- 故障地址和期望时间 -->
          <el-row :gutter="20">
            <el-col :span="12">
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
            </el-col>
            <el-col :span="12">
              <el-form-item label="期望解决时间" prop="expectedTime">
                <el-date-picker
                    v-model="repairForm.expectTime"
                    type="datetime"
                    placeholder="选择期望解决时间"
                    style="width: 100%"
                    size="large"
                    :disabled-date="disabledDate"
                    value-format="YYYY-MM-DD HH:mm:ss"
                />
              </el-form-item>
            </el-col>
          </el-row>

          <!-- 故障描述 -->
          <el-form-item label="故障描述" prop="description">
            <el-input
                v-model="repairForm.description"
                type="textarea"
                :rows="4"
                placeholder="请详细描述故障现象、问题表现等"
                maxlength="500"
                show-word-limit
                resize="none"
            />
          </el-form-item>

          <!-- 备注信息 -->
          <el-form-item label="备注信息" prop="remark">
            <el-input
                v-model="repairForm.remark"
                type="textarea"
                :rows="3"
                placeholder="可填写其他需要说明的信息（选填）"
                maxlength="300"
                show-word-limit
                resize="none"
            />
          </el-form-item>

          <!-- 提交和重置按钮 -->
          <el-form-item>
            <div class="form-actions">
              <el-button
                  type="primary"
                  size="large"
                  @click="submitRepair"
                  :loading="submitting"
                  style="width: 200px"
              >
                <template #icon>
                  <el-icon><Check /></el-icon>
                </template>
                提交报修
              </el-button>

              <el-button
                  size="large"
                  @click="resetForm"
                  style="width: 120px"
              >
                <template #icon>
                  <el-icon><Refresh /></el-icon>
                </template>
                重置
              </el-button>
            </div>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
</template>



<style scoped>
.repair-container {
  padding: 20px;
  min-height: 88vh;
  background-size: cover; /* 覆盖整个容器 */
  background-position: center; /* 居中显示 */
  background-repeat: no-repeat; /* 不重复 */
  background-attachment: fixed; /* 固定背景，滚动时不动 */

}

.repair-header {
  text-align: center;
  margin-bottom: 30px;
}

.repair-header h1 {
  font-size: 2.5rem;
  margin-bottom: 10px;
  font-weight: 600;
  color: #303133;
}

.repair-header p {
  font-size: 1.1rem;
  color: #606266;
}

.repair-form-container {
  max-width: 900px;
  margin: 0 auto;
}

.repair-card {
  border-radius: 12px;
  border: none;
}

.form-actions {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 30px;
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
  line-height: 1.4;
}
/* 树形选择器弹出框样式 */

.solution-box {
  margin: 15px 0;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 8px;
  border-left: 4px solid #409eff;
}

.solution-title {
  font-weight: bold;
  color: #409eff;
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.solution-content {
  color: #333;
}

.solution-content p {
  margin-bottom: 8px;
}

.solution-content ul {
  margin: 0;
  padding-left: 20px;
}

.solution-content li {
  margin-bottom: 6px;
  line-height: 1.5;
}
</style>

<!--全局样式-->
<style>
.category-tree-select {
  width: 380px !important;
  max-width: 380px !important;
}

.category-tree-select .el-tree {
  max-height: 280px !important;
  overflow-y: auto;
}

.category-tree-select .el-tree-node__content {
  height: auto !important;
  min-height: 36px;
}
</style>