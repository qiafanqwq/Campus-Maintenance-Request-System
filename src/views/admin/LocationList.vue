<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, Search, Refresh, Location } from '@element-plus/icons-vue'
import { getLocationAPI, addLocationAPI, updateLocationAPI, deleteLocationAPI, type LocationVO, type LocationDTO } from '@/api/location'

// 响应式数据
const locations = ref<LocationVO[]>([])
const loading = ref(false)
const searchKeyword = ref('')
const formVisible = ref(false)
const formType = ref<'add' | 'edit'>('add')
const currentLocation = ref<LocationVO | null>(null)

// 表单数据
const locationForm = ref<LocationDTO>({
  name: '',
  parentId: 0
})

// 表单验证规则
const formRules = {
  name: [
    { required: true, message: '请输入地址名称', trigger: 'blur' },
    { min: 2, max: 50, message: '地址名称长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  parentId: [
    { required: true, message: '请选择父级地址', trigger: 'change' }
  ]
}

const formRef = ref()

// 计算属性
const rootLocations = computed(() => {
  return locations.value.filter(loc => loc.parentId === 0)
})

const filteredLocations = computed(() => {
  if (!searchKeyword.value.trim()) {
    return locations.value
  }

  const keyword = searchKeyword.value.trim().toLowerCase()

  // 使用递归函数过滤树形数据
  const filterTree = (locations: LocationVO[]): LocationVO[] => {
    return locations.filter(location => {
      // 检查当前节点是否匹配
      const nameMatch = location.name?.toLowerCase().includes(keyword) || false
      const currentMatch = nameMatch

      // 如果有子节点，递归过滤子节点
      if (location.children && location.children.length > 0) {
        const filteredChildren = filterTree(location.children)
        // 如果当前节点匹配或子节点有匹配，则保留该节点
        if (currentMatch || filteredChildren.length > 0) {
          return {
            ...location,
            children: filteredChildren
          }
        }
        return false
      }

      // 没有子节点时，只检查当前节点是否匹配
      return currentMatch
    })
  }

  return filterTree(locations.value)
})

// 方法
const handleAddRoot = () => {
  formType.value = 'add'
  locationForm.value = {
    name: '',
    parentId: 0
  }
  formVisible.value = true
}

const handleAddChild = (parent: LocationVO) => {
  formType.value = 'add'
  locationForm.value = {
    name: '',
    parentId: parent.id
  }
  formVisible.value = true
}

const handleEdit = (location: LocationVO) => {
  formType.value = 'edit'
  currentLocation.value = location
  locationForm.value = {
    id: location.id,
    name: location.name,
    parentId: location.parentId
  }
  formVisible.value = true
}

const handleCloseForm = () => {
  formVisible.value = false
  formRef.value?.resetFields()
  currentLocation.value = null
}

const submitForm = async () => {
  if (!formRef.value) return

  try {
    const valid = await formRef.value.validate()
    if (valid) {
      if (formType.value === 'add') {
        // 添加地址
        const response = await addLocationAPI(locationForm.value)
        if (response.code === 1) {
          ElMessage.success('地址添加成功')
          handleCloseForm()
          loadLocations()
        } else {
          ElMessage.error(response.msg || '添加失败')
        }
      } else {
        // 编辑地址
        const editData: LocationDTO = {
          id: currentLocation.value!.id,
          name: locationForm.value.name,
          parentId: locationForm.value.parentId
        }
        const response = await updateLocationAPI(editData)
        if (response.code === 1) {
          ElMessage.success('地址更新成功')
          handleCloseForm()
          loadLocations()
        } else {
          ElMessage.error(response.msg || '更新失败')
        }
      }
    }
  } catch (error) {
    ElMessage.error('操作失败，请重试')
    console.error('操作失败:', error)
  }
}

const handleDelete = async (location: LocationVO) => {
  try {
    await ElMessageBox.confirm(
        `确定要删除地址 "${location.name}" 吗？${
            location.children && location.children.length > 0
                ? '该地址下的子地址也将被删除！'
                : ''
        }`,
        '删除确认',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        }
    )

    const response = await deleteLocationAPI(location.id)
    if (response.code === 1) {
      ElMessage.success('地址删除成功')
      loadLocations()
    } else {
      ElMessage.error(response.msg || '删除失败')
    }
  } catch (error) {
    ElMessage.info('已取消删除')
  }
}

// 构建树形结构
const buildLocationTree = (flatList: LocationVO[]): LocationVO[] => {
  const map = new Map<number, LocationVO>()
  const tree: LocationVO[] = []

  // 创建映射
  flatList.forEach(item => {
    map.set(item.id, { ...item, children: [] })
  })

  // 构建树形结构
  flatList.forEach(item => {
    const node = map.get(item.id)
    if (node) {
      if (item.parentId === 0) {
        tree.push(node)
      } else {
        const parent = map.get(item.parentId)
        if (parent && parent.children) {
          parent.children.push(node)
        }
      }
    }
  })

  return tree
}

// 加载数据
const loadLocations = async () => {
  loading.value = true
  try {
    const response = await getLocationAPI()
    if (response.code === 1) {
      // 将扁平数据构建成树形结构
      locations.value = buildLocationTree(response.data)
    } else {
      ElMessage.error(response.msg || '加载地址数据失败')
    }
  } catch (error) {
    ElMessage.error('加载地址数据失败')
    console.error('加载地址数据失败:', error)
  } finally {
    loading.value = false
  }
}

// 组件挂载时加载数据
onMounted(() => {
  loadLocations()
})
</script>

<template>
  <div class="location-container">
    <div class="location-header">
      <h1>
        <el-icon class="header-icon"><Location /></el-icon>
        报修地址管理
      </h1>
      <p>管理报修地址分类，支持多级地址结构</p>
    </div>

    <!-- 工具栏 -->
    <div class="location-toolbar">
      <div class="toolbar-left">
        <el-button type="primary" :icon="Plus" @click="handleAddRoot">
          添加根地址
        </el-button>
        <el-button :icon="Refresh" @click="loadLocations">
          刷新
        </el-button>
      </div>
      <div class="toolbar-right">
        <el-input
            v-model="searchKeyword"
            placeholder="搜索地址名称..."
            clearable
            style="width: 300px"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>
    </div>

    <!-- 地址树形表格 -->
    <div class="location-content">
      <el-table
          v-loading="loading"
          :data="filteredLocations"
          row-key="id"
          :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
          default-expand-all
          style="width: 100%"
          class="location-table"
      >
        <el-table-column prop="name" label="地址名称" min-width="250">
          <template #default="{ row }">
            <div class="location-name">
              <el-icon v-if="row.parentId === 0" class="root-icon">
                <Location />
              </el-icon>
              <span v-if="row.parentId === 0" class="root-location">
                {{ row.name }}
              </span>
              <span v-else class="sub-location">
                {{ row.name }}
              </span>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="parentId" label="地址层级" width="120">
          <template #default="{ row }">
            <el-tag :type="row.parentId === 0 ? 'primary' : 'success'" effect="plain">
              {{ row.parentId === 0 ? '一级地址' : '二级地址' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="ID" width="100" align="center">
          <template #default="{ row }">
            <span class="id-text">
              {{ row.id }}
            </span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="380" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button
                  v-if="row.parentId === 0"
                  size="small"
                  type="primary"
                  :icon="Plus"
                  @click="handleAddChild(row)"
              >
                添加子地址
              </el-button>
              <el-button
                  size="small"
                  type="warning"
                  :icon="Edit"
                  @click="handleEdit(row)"
              >
                编辑
              </el-button>
              <el-button
                  size="small"
                  type="danger"
                  :icon="Delete"
                  @click="handleDelete(row)"
              >
                删除
              </el-button>
            </div>
          </template>
        </el-table-column>

        <!-- 空状态插槽 -->
        <template #empty>
          <div class="table-empty-state">
            <el-empty
                description="暂无地址数据"
                :image-size="180"
            >
              <el-button type="primary" :icon="Plus" @click="handleAddRoot">
                添加第一个地址
              </el-button>
            </el-empty>
          </div>
        </template>
      </el-table>
    </div>

    <!-- 添加/编辑地址弹窗 -->
    <el-dialog
        v-model="formVisible"
        :title="formType === 'add' ? '添加地址' : '编辑地址'"
        width="500px"
        :before-close="handleCloseForm"
    >
      <el-form
          :model="locationForm"
          :rules="formRules"
          ref="formRef"
          label-width="100px"
      >
        <el-form-item label="地址名称" prop="name">
          <el-input
              v-model="locationForm.name"
              placeholder="请输入地址名称，如：行政楼、田径场等"
              maxlength="50"
              show-word-limit
          />
        </el-form-item>

        <el-form-item label="父级地址" prop="parentId" v-if="formType === 'add'">
          <el-select
              v-model="locationForm.parentId"
              placeholder="请选择父级地址"
              style="width: 100%"
              clearable
              disabled="disabled"
          >
            <el-option label="根地址（一级地址）" :value="0" />
            <el-option
                v-for="location in rootLocations"
                :key="location.id"
                :label="location.name"
                :value="location.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item v-if="formType === 'edit'" label="当前层级">
          <div class="current-level">
            <el-tag :type="currentLocation?.parentId === 0 ? 'primary' : 'success'">
              {{ currentLocation?.parentId === 0 ? '一级地址' : '二级地址' }}
            </el-tag>
          </div>
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
.location-container {
  padding: 20px;
  background-color: #f8f9fa;
  min-height: 100vh;
}

.location-header {
  text-align: center;
  margin-bottom: 30px;
  padding: 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.location-header h1 {
  font-size: 2.2rem;
  margin-bottom: 12px;
  font-weight: 600;
  color: #303133;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

.header-icon {
  font-size: 2rem;
  color: #409eff;
}

.location-header p {
  font-size: 1rem;
  color: #606266;
  margin: 0;
}

/* 工具栏样式 */
.location-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.toolbar-left,
.toolbar-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

/* 地址名称样式 */
.location-name {
  display: flex;
  align-items: center;
  gap: 8px;
}

.root-icon {
  color: #409eff;
  font-size: 1.2rem;
}

.root-location {
  color: #409eff;
  font-weight: 600;
  font-size: 1.05rem;
}

.sub-location {
  color: #606266;
  padding-left: 8px;
  position: relative;
}

.sub-location::before {
  content: "›";
  margin-right: 6px;
  color: #67c23a;
  font-weight: bold;
}

/* ID文本样式 */
.id-text {
  color: #909399;
  font-family: 'Courier New', monospace;
  font-weight: 500;
}

/* 排序标签 */
.sort-tag {
  background-color: #f0f9ff;
  color: #409eff;
  border-color: #d9ecff;
}

/* 操作按钮样式 */
.action-buttons {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

/* 表格样式 */
.location-table {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

:deep(.location-table .el-table__row) {
  transition: all 0.3s ease;
}

:deep(.location-table .el-table__row:hover) {
  background-color: #f5f9ff !important;
  transform: translateY(-1px);
}

:deep(.location-table .el-table__cell) {
  padding: 16px 0;
}

/* 表单提示 */
.form-tip {
  margin-top: 8px;
  color: #909399;
  font-size: 0.85rem;
}

.current-level {
  padding: 8px 0;
}

/* 空状态样式 */
:deep(.el-empty__description) {
  margin-top: 16px;
  color: #909399;
  font-size: 1.1rem;
}

:deep(.el-empty__image img) {
  opacity: 0.8;
}

/* 对话框样式 */
:deep(.el-dialog) {
  border-radius: 12px;
}

:deep(.el-dialog__header) {
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;
  margin-right: 0;
}

:deep(.el-dialog__body) {
  padding: 30px 20px;
}

:deep(.el-dialog__footer) {
  padding: 20px;
  border-top: 1px solid #f0f0f0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .location-container {
    padding: 12px;
  }

  .location-header {
    padding: 16px;
    margin-bottom: 20px;
  }

  .location-header h1 {
    font-size: 1.8rem;
    flex-direction: column;
    gap: 8px;
  }

  .location-toolbar {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
    padding: 16px;
  }

  .toolbar-left,
  .toolbar-right {
    justify-content: center;
  }

  .toolbar-left .el-button,
  .toolbar-right .el-input {
    width: 100%;
  }

  .action-buttons {
    flex-direction: column;
  }

  :deep(.location-table .el-table__cell) {
    padding: 12px 0;
  }
}

/* 添加一些动画效果 */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.location-container {
  animation: fadeIn 0.5s ease-out;
}

/* 悬停效果增强 */
.el-button:hover {
  transform: translateY(-2px);
  transition: all 0.3s ease;
}

.el-tag:hover {
  opacity: 0.9;
  transition: opacity 0.3s ease;
}
</style>