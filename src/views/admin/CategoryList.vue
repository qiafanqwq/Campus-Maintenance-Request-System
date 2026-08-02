<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, Search, Refresh } from '@element-plus/icons-vue'
import { getCategoryAPI, addCategoryAPI, updateCategoryAPI, deleteCategoryAPI, type CategoryVO, type CategoryDTO } from '@/api/category'

// 响应式数据
const categories = ref<CategoryVO[]>([])
const loading = ref(false)
const searchKeyword = ref('')
const formVisible = ref(false)
const formType = ref<'add' | 'edit'>('add')
const currentCategory = ref<CategoryVO | null>(null)

// 表单数据
const categoryForm = ref<CategoryDTO>({
  name: '',
  parentId: 0,
  description: ''
})

// 表单验证规则
const formRules = {
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' },
    { min: 2, max: 50, message: '分类名称长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  parentId: [
    { required: true, message: '请选择父级分类', trigger: 'change' }
  ]
}

const formRef = ref()

// 计算属性
const rootCategories = computed(() => {
  return categories.value.filter(cat => cat.parentId === 0)
})

const filteredCategories = computed(() => {
  if (!searchKeyword.value.trim()) {
    return categories.value
  }

  const keyword = searchKeyword.value.trim().toLowerCase()

  // 使用递归函数过滤树形数据
  const filterTree = (categories: CategoryVO[]): CategoryVO[] => {
    return categories.filter(category => {
      // 检查当前节点是否匹配
      const nameMatch = category.name?.toLowerCase().includes(keyword) || false
      const descMatch = category.description?.toLowerCase().includes(keyword) || false
      const currentMatch = nameMatch || descMatch

      // 如果有子节点，递归过滤子节点
      if (category.children && category.children.length > 0) {
        const filteredChildren = filterTree(category.children)
        // 如果当前节点匹配或子节点有匹配，则保留该节点
        if (currentMatch || filteredChildren.length > 0) {
          return {
            ...category,
            children: filteredChildren
          }
        }
        return false
      }

      // 没有子节点时，只检查当前节点是否匹配
      return currentMatch
    })
  }

  return filterTree(categories.value)
})

// 方法
const handleAddRoot = () => {
  formType.value = 'add'
  categoryForm.value = {
    name: '',
    parentId: 0,
    description: ''
  }
  formVisible.value = true
}

const handleAddChild = (parent: CategoryVO) => {
  formType.value = 'add'
  categoryForm.value = {
    name: '',
    parentId: parent.id,
    description: ''
  }
  formVisible.value = true
}

const handleEdit = (category: CategoryVO) => {
  formType.value = 'edit'
  currentCategory.value = category
  categoryForm.value = {
    id: category.id,
    name: category.name,
    parentId: category.parentId,
    description: category.description
  }
  formVisible.value = true
}

const handleCloseForm = () => {
  formVisible.value = false
  formRef.value?.resetFields()
  currentCategory.value = null
}

const submitForm = async () => {
  if (!formRef.value) return

  try {
    const valid = await formRef.value.validate()
    if (valid) {
      if (formType.value === 'add') {
        // 添加分类
        const response = await addCategoryAPI(categoryForm.value)
        if (response.code === 1) {
          ElMessage.success('分类添加成功')
          handleCloseForm()
          loadCategories()
        } else {
          ElMessage.error(response.msg || '添加失败')
        }
      } else {
        // 编辑分类 - 需要传递 id 和所有字段
        const editData: CategoryDTO = {
          id: currentCategory.value!.id,
          name: categoryForm.value.name,
          parentId: categoryForm.value.parentId,
          description: categoryForm.value.description
        }
        const response = await updateCategoryAPI(editData)
        if (response.code === 1) {
          ElMessage.success('分类更新成功')
          handleCloseForm()
          loadCategories()
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

const handleDelete = async (category: CategoryVO) => {
  try {
    await ElMessageBox.confirm(
        `确定要删除分类 "${category.name}" 吗？${
            category.children && category.children.length > 0
                ? '该分类下的子分类也将被删除！'
                : ''
        }`,
        '删除确认',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        }
    )

    const response = await deleteCategoryAPI(category.id)
    if (response.code === 1) {
      ElMessage.success('分类删除成功')
      loadCategories()
    } else {
      ElMessage.error(response.msg || '删除失败')
    }
  } catch (error) {
    ElMessage.info('已取消删除')
  }
}

// 构建树形结构
const buildCategoryTree = (flatList: CategoryVO[]): CategoryVO[] => {
  const map = new Map<number, CategoryVO>()
  const tree: CategoryVO[] = []

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
const loadCategories = async () => {
  loading.value = true
  try {
    const response = await getCategoryAPI()
    if (response.code === 1) {
      // 将扁平数据构建成树形结构
      categories.value = buildCategoryTree(response.data)
    } else {
      ElMessage.error(response.msg || '加载分类数据失败')
    }
  } catch (error) {
    ElMessage.error('加载分类数据失败')
    console.error('加载分类数据失败:', error)
  } finally {
    loading.value = false
  }
}

// 组件挂载时加载数据
onMounted(() => {
  loadCategories()
})
</script>

<template>
  <div class="category-container">
    <div class="category-header">
      <h1>报修分类管理</h1>
      <p>可以在此管理网络报修的分类体系</p>
    </div>

    <!-- 工具栏 -->
    <div class="category-toolbar">
      <div class="toolbar-left">
        <el-button type="primary" :icon="Plus" @click="handleAddRoot">
          添加根分类
        </el-button>
        <el-button :icon="Refresh" @click="loadCategories">
          刷新
        </el-button>
      </div>
      <div class="toolbar-right">
        <el-input
            v-model="searchKeyword"
            placeholder="搜索分类名称..."
            clearable
            style="width: 300px"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>
    </div>

    <!-- 分类树形表格 -->
    <div class="category-content">
      <el-table
          v-loading="loading"
          :data="filteredCategories"
          row-key="id"
          :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
          default-expand-all
          style="width: 100%"
      >
        <el-table-column prop="name" label="分类名称" min-width="200">
          <template #default="{ row }">
            <div class="category-name">
              <span v-if="row.parentId === 0" class="root-category">
                {{ row.name }}
              </span>
              <span v-else class="sub-category">
                {{ row.name }}
              </span>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="parentId" label="层级" width="100">
          <template #default="{ row }">
            <el-tag :type="row.parentId === 0 ? 'primary' : 'success'">
              {{ row.parentId === 0 ? '根分类' : '子分类' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="description" label="分类描述" min-width="250">
          <template #default="{ row }">
            <span v-if="row.description" class="description-text">
              {{ row.description }}
            </span>
            <span v-else class="no-description">暂无描述</span>
          </template>
        </el-table-column>

        <el-table-column prop="sortOrder" label="排序" width="80" align="center">
          <template #default="{ row }">
            <span class="sort-order">{{ row.sortOrder }}</span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button
                  v-if="row.parentId === 0"
                  size="small"
                  type="primary"
                  :icon="Plus"
                  @click="handleAddChild(row)"
              >
                添加子类
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
      </el-table>

      <!-- 空状态 -->
      <el-empty
          v-if="filteredCategories.length === 0 && !loading"
          description="暂无分类数据"
          :image-size="200"
      >
        <el-button type="primary" :icon="Plus" @click="handleAddRoot">
          添加第一个分类
        </el-button>
      </el-empty>
    </div>

    <!-- 添加/编辑分类弹窗 -->
    <el-dialog
        v-model="formVisible"
        :title="formType === 'add' ? '添加分类' : '编辑分类'"
        width="500px"
        :before-close="handleCloseForm"
    >
      <el-form
          :model="categoryForm"
          :rules="formRules"
          ref="formRef"
          label-width="80px"
      >
        <el-form-item label="分类名称" prop="name">
          <el-input
              v-model="categoryForm.name"
              placeholder="请输入分类名称"
              maxlength="50"
              show-word-limit
          />
        </el-form-item>

        <el-form-item label="父级分类" prop="parentId" v-if="formType === 'add'">
          <el-select
              v-model="categoryForm.parentId"
              placeholder="请选择父级分类"
              style="width: 100%"
              disabled
          >
            <el-option label="根分类" :value="0" />
            <el-option
                v-for="category in rootCategories"
                :key="category.id"
                :label="category.name"
                :value="category.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="分类描述" prop="description">
          <el-input
              v-model="categoryForm.description"
              type="textarea"
              :rows="3"
              placeholder="请输入分类描述"
              maxlength="200"
              show-word-limit
              resize="none"
          />
        </el-form-item>

        <!-- 移除排序输入框 -->
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
/* 样式保持不变 */
.category-container {
  padding: 20px;
  background-color: white;
  min-height: 100vh;
}

.category-header {
  text-align: center;
  margin-bottom: 30px;
}

.category-header h1 {
  font-size: 2.2rem;
  margin-bottom: 8px;
  font-weight: 600;
  color: #303133;
}

.category-header p {
  font-size: 1rem;
  color: #606266;
}

/* 工具栏样式 */
.category-toolbar {
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

/* 分类名称样式 */
.category-name {
  font-weight: 500;
}

.root-category {
  color: #409eff;
  font-weight: 600;
  font-size: 1.05rem;
}

.sub-category {
  color: #606266;
  padding-left: 8px;
  position: relative;
}

.sub-category::before {
  content: "↳";
  margin-right: 4px;
  color: #909399;
}

/* 描述文本样式 */
.description-text {
  color: #606266;
  line-height: 1.4;
}

.no-description {
  color: #c0c4cc;
  font-style: italic;
}

/* 排序样式 */
.sort-order {
  color: #909399;
  font-weight: 500;
}

/* 操作按钮样式 */
.action-buttons {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

/* 空状态样式 */
:deep(.el-empty__description) {
  margin-top: 16px;
  color: #909399;
}

/* 表格样式 */
:deep(.el-table) {
  border-radius: 8px;
  overflow: hidden;
}

:deep(.el-table .el-table__row) {
  transition: background-color 0.3s ease;
}

:deep(.el-table .el-table__row:hover) {
  background-color: #f5f7fa;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .category-container {
    padding: 16px;
  }

  .category-toolbar {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }

  .toolbar-left,
  .toolbar-right {
    justify-content: center;
  }

  .category-header h1 {
    font-size: 1.8rem;
  }

  .action-buttons {
    flex-direction: column;
  }
}
</style>