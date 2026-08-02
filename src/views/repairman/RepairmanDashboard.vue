<script lang="ts" setup>
import Aside from "@/components/Aside.vue";
import Header from "@/components/Header.vue";
</script>

<template>
  <div class="common-layout">
    <el-container direction="vertical">
      <Header />
      <el-container>
        <Aside />
        <el-main class="main-content">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<style scoped>
.common-layout {
  height: 100vh;
}

.main-content {
  margin-top: 60px; /* Header高度 */
  margin-left: 220px; /* Aside宽度 */
  padding: 20px;
  min-height: calc(100vh - 60px);
  background-color: #f5f7fa;
}
</style>