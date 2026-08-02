<script setup lang="ts">
import { ref } from 'vue'

// 使用双向绑定
const dialogVisible = defineModel<boolean>('visible', { default: false })
const formData = defineModel<any>('formData', { required: true })

// 定义其他属性
defineProps({
  rules: Object
});
// 定义事件
const emit = defineEmits(['confirm', 'close'])

// 表单引用（重要！）
const formRef = ref()

// 处理关闭
const handleClose = () => {
  emit('close')
}

// 处理确认（带表单验证）
const handleConfirm = async () => {
  if (!formRef.value) return

  // 验证表单
  const valid = await formRef.value.validate()
  if (valid) {
    emit('confirm')
  }
}
</script>

<template>
  <el-dialog
      v-model="dialogVisible"
      title="编辑用户信息"
      width="500px"
      :before-close="handleClose"
  >
    <el-form
        :model="formData"
        :rules="rules"
        ref="formRef"
        label-width="100px"
    >
      <el-form-item label="ID" prop="id">
        <el-input v-model="formData.id" disabled />
      </el-form-item>

      <el-form-item label="姓名" prop="nickname">
        <el-input v-model="formData.nickname" placeholder="请输入姓名" />
      </el-form-item>

      <el-form-item label="学号/工号" prop="username">
        <el-input v-model="formData.username" disabled />
      </el-form-item>

      <el-form-item label="手机号码" prop="phone">
        <el-input v-model="formData.phone" placeholder="请输入手机号码" />
      </el-form-item>

      <el-form-item label="邮箱" prop="email">
        <el-input v-model="formData.email" placeholder="请输入邮箱地址" />
      </el-form-item>
    </el-form>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="handleConfirm">确定</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<style>
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>