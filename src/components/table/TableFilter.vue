<script setup lang="ts">
const emit = defineEmits(['update:searchValue', 'query', 'add'])
const searchName = defineModel<string>('searchName', { default: '' })

</script>

<template>
  <!-- 筛选区和添加区 -->
  <div class="list-page">
    <!-- 左侧查询区域 -->
    <el-form style="display: inline-flex;" @submit.prevent>
      <el-form-item label="用户姓名:" style="margin-left: 30px; margin-bottom: 0;">
        <el-input
            style="width: 200px"
            placeholder="请输入"
            v-model="searchName"
            @keyup.enter="emit('query')"
        >
        </el-input>
        <el-button type="primary" style="margin-left: 20px" @click="emit('query')">查询</el-button>
      </el-form-item>
    </el-form>

    <!-- 右侧添加按钮 -->
    <el-button
        type="primary"
        style="position: absolute; right: 30px;"
        @click="emit('add')"
    >
      + 添加用户
    </el-button>
  </div>
</template>

<style scoped>
.list-page {
  margin-bottom: 45px;
  background-color: white;
  border-radius: 4px;
  height: 60px;
  display: flex;
  align-items: center;
  position: relative; /* 关键：为绝对定位提供参照 */
}
</style>