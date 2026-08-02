<script setup lang="ts">
import type { RepairOrderVO } from '@/api/RepairOrder'

defineProps<{
  tableData: RepairOrderVO[]
}>()

const emit = defineEmits(['edit', 'assign', 'delete', 'viewImages', 'transfer', 'detail'])

// 方法定义
const getStatusText = (status: number) => {
  const statusMap = {
    0: '待处理',
    1: '处理中',
    2: '已完成',
    3: '已取消',
    4: '待转发'
  }
  return statusMap[status as keyof typeof statusMap] || '未知'
}

const getStatusType = (status: number) => {
  const typeMap = {
    0: 'warning',
    1: 'primary',
    2: 'success',
    3: 'danger',
    4: 'info'
  }
  return typeMap[status as keyof typeof typeMap] || 'info'
}

const formatDateTime = (time: string | null) => {
  if (!time) return '-'
  // 去掉T，将 2025-10-13T15:00:00 格式化为 2025-10-13 15:00:00
  return time.replace('T', ' ')
}
</script>

<template>
  <div class="list-content-area">
    <el-table :data="tableData" height="700" style="width: 100%" table-layout="fixed" border>
      <el-table-column prop="id" label="报修单号" width="100" align="center" fixed="left" />

      <el-table-column prop="address" label="故障地址" min-width="180" show-overflow-tooltip />

      <el-table-column prop="nickName" label="报修用户" width="100" align="center" />

      <el-table-column prop="userPhone" label="手机号码" width="120" align="center" />

      <el-table-column prop="status" label="报修状态" width="100" align="center" fixed="left">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column prop="categoryName" label="报修分类" min-width="150" show-overflow-tooltip />

      <el-table-column prop="description" label="报修描述" min-width="200" show-overflow-tooltip />

      <el-table-column prop="expectTime" label="期望处理时间" width="160" align="center">
        <template #default="scope">
          {{ formatDateTime(scope.row.expectTime) }}
        </template>
      </el-table-column>

      <el-table-column prop="repairmanName" label="维修人员" width="100" align="center">
        <template #default="scope">
          <span v-if="scope.row.repairmanName">{{ scope.row.repairmanName }}</span>
          <span v-else class="no-assign">未分配</span>
        </template>
      </el-table-column>

      <el-table-column prop="repairTime" label="处理时间" width="160" align="center">
        <template #default="scope">
          {{ formatDateTime(scope.row.repairTime) }}
        </template>
      </el-table-column>

      <el-table-column prop="repairProcess" label="处理过程" min-width="200" show-overflow-tooltip />

      <!-- 操作列 -->
      <el-table-column label="操作" width="200" fixed="right" align="center">
        <template #default="scope">
          <el-button
              size="small"
              type="primary"
              @click="$emit('detail', scope.row)"
          >
            详情
          </el-button>

          <el-button
              v-if="!scope.row.repairmanName && scope.row.status !== 2 && scope.row.status !== 3"
              size="small"
              type="primary"
              @click="emit('assign', scope.row)"
          >
            分配
          </el-button>

          <el-button
              v-if="scope.row.status === 4"
              size="small"
              type="primary"
              @click="emit('transfer', scope.row)"
          >
            转发
          </el-button>

          <el-button
              size="small"
              type="warning"
              @click="emit('edit', scope.row)"
          >
            编辑
          </el-button>

          <el-button
              size="small"
              type="danger"
              @click="emit('delete', scope.row)"
          >
            删除
          </el-button>


        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<style scoped>
.list-content-area {
  width: 100%;
}

.no-images,
.no-assign {
  color: #c0c4cc;
  font-style: italic;
  font-size: 12px;
}
</style>