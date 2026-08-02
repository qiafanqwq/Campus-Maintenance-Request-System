<script setup lang="ts">
defineProps<{
  tableData: any[]
}>()
const emit = defineEmits(['edit', 'toggleStatus'])
</script>


<template>
  <div class="list-content-area">
    <el-table :data="tableData" height="700" style="width: 100%" table-layout="fixed">
      <el-table-column prop="id" label="ID"/>
      <el-table-column prop="nickname" label="姓名"/>
      <el-table-column prop="username" label="学号/工号" />
      <el-table-column prop="phone" label="手机号码"/>
      <el-table-column prop="email" label="邮箱" />
      <el-table-column prop="status" label="账户状态">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
            {{ scope.row.status === 1 ? '正常' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <!-- 操作列 -->
      <el-table-column label="操作" fixed="right" align="center">
        <template #default="scope">
          <el-button
              size="small"
              type="primary"
              @click="emit('edit', scope.row)"
          >
            编辑
          </el-button>

          <el-button
              size="small"
              :type="scope.row.status === 1 ? 'warning' : 'success'"
              @click="emit('toggleStatus', scope.row)"
          >
            {{ scope.row.status === 1 ? '禁用' : '启用' }}
          </el-button>

        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<style scoped>

</style>