<script setup lang="ts">
import { ElConfigProvider } from 'element-plus'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'

// 双向绑定
const currentPage = defineModel<number>('currentPage', { required: true })
const pageSize = defineModel<number>('pageSize', { required: true })

defineProps({
  total: {
    type: Number,
    required: true
  }
})

// 事件
const emit = defineEmits(['sizeChange', 'currentChange'])

const handleSizeChange = (size: number) => {
  emit('sizeChange', size)
}

const handleCurrentChange = (page: number) => {
  emit('currentChange', page)
}
</script>

<template>
  <div class="demo-pagination-block">
    <el-config-provider :locale="zhCn">
      <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 30, 40]"
          size="small"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
      />
    </el-config-provider>
  </div>
</template>

<style scoped>
.demo-pagination-block {
  display: flex;
  justify-content: center;
  margin: 0 auto;
}

.demo-pagination-block + .demo-pagination-block {
  margin-top: 10px;
}

.demo-pagination-block .demonstration {
  margin-bottom: 16px;
}
</style>