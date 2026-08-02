<script setup lang="ts">
import { ref } from 'vue'

// 使用双向绑定
const dialogVisible = defineModel<boolean>('visible', { default: false })
const formData = defineModel('formData', { required: true })

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
      title="添加用户"
      width="500px"
      :before-close="handleClose"
  >
    <el-form
        :model="formData"
        :rules="rules"
        ref="formRef"
        label-width="100px"
    >
      <el-form-item label="姓名" prop="nickname">
        <el-input
            v-model="formData.nickname"
            placeholder="请输入姓名"
            maxlength="20"
            show-word-limit
        />
      </el-form-item>

      <el-form-item label="学号/工号" prop="username">
        <el-input
            v-model="formData.username"
            placeholder="请输入学号或工号"
        />
      </el-form-item>

      <el-form-item label="手机号码" prop="phone">
        <el-input
            v-model="formData.phone"
            placeholder="请输入手机号码"
        />
      </el-form-item>

      <el-form-item label="邮箱" prop="email">
        <el-input
            v-model="formData.email"
            placeholder="请输入邮箱地址"
        />
      </el-form-item>

      <el-form-item label="密码" prop="password">
        <el-input
            v-model="formData.password"
            type="password"
            placeholder="请输入密码"
            show-password
        />
      </el-form-item>

      <el-form-item label="确认密码" prop="confirmPassword">
        <el-input
            v-model="formData.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            show-password
        />
      </el-form-item>

      <el-form-item label="用户权限" prop="userAuthority">
        <el-select v-model="formData.authorityId" placeholder="请选择用户权限">
          <el-option label="普通用户" :value="0" />
          <el-option label="普通管理员" :value="3" />
          <el-option label="维修人员" :value="2" />
        </el-select>
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