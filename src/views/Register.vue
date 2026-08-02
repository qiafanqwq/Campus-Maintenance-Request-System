<template>
  <div class="register-container">
    <div class="register-form-wrapper">
      <el-card class="register-card">
        <template #header>
          <div class="card-header">
            <h2 class="register-title">用户注册</h2>
            <p class="register-subtitle">请使用学号进行注册</p>
          </div>
        </template>

        <el-form
            :model="registerForm"
            :rules="registerRules"
            ref="registerFormRef"
            label-width="0"
            class="register-form"
            size="large"
        >
          <el-form-item prop="username">
            <el-input
                v-model="registerForm.username"
                placeholder="请输入学号"
                clearable
                :prefix-icon="User"
            />
          </el-form-item>

          <el-form-item prop="nickname">
            <el-input
                v-model="registerForm.nickname"
                placeholder="请输入昵称"
                clearable
                :prefix-icon="UserFilled"
            />
          </el-form-item>

          <el-form-item prop="phone">
            <el-input
                v-model="registerForm.phone"
                placeholder="请输入手机号"
                clearable
                maxlength="11"
                :prefix-icon="Iphone"
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
                v-model="registerForm.password"
                type="password"
                placeholder="请输入密码"
                clearable
                show-password
                :prefix-icon="Lock"
            />
          </el-form-item>

          <el-form-item prop="confirmPassword">
            <el-input
                v-model="registerForm.confirmPassword"
                type="password"
                placeholder="请再次输入密码"
                clearable
                show-password
                :prefix-icon="Lock"
            />
          </el-form-item>

          <el-form-item>
            <el-button
                type="primary"
                class="register-btn"
                :loading="loading"
                @click="handleRegister"
            >
              {{ loading ? '注册中...' : '立即注册' }}
            </el-button>
          </el-form-item>

          <div class="login-link">
            已有账号？
            <el-link type="primary" @click="goToLogin">立即登录</el-link>
          </div>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { User, UserFilled, Iphone, Lock } from '@element-plus/icons-vue'
import type { UserRegisterDTO } from '@/api/login'
import { registerAPI } from '@/api/login'

const router = useRouter()
const registerFormRef = ref<FormInstance>()
const loading = ref(false)

// 注册表单数据
const registerForm = reactive({
  username: '',
  nickname: '',
  phone: '',
  password: '',
  confirmPassword: ''
})

// 表单验证规则
const registerRules: FormRules = {
  username: [
    { required: true, message: '请输入学号', trigger: 'blur' },
    { pattern: /^\d+$/, message: '学号必须为数字', trigger: 'blur' },
    { min: 10, max: 10, message: '学号长度为 10 个字符', trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '昵称长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 8, max: 15, message: '密码长度在 8 到 15 个字符', trigger: 'blur' },
    {
      pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).+$/,
      message: '密码必须包含大小写字母和数字',
      trigger: 'blur'
    }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    {
      validator: (rule: any, value: string, callback: any) => {
        if (value !== registerForm.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 处理注册
const handleRegister = async () => {
  if (!registerFormRef.value) return

  try {
    // 表单验证
    await registerFormRef.value.validate()

    loading.value = true

    // 准备注册数据
    const registerData: UserRegisterDTO = {
      username: registerForm.username,
      password: registerForm.password,
      nickname: registerForm.nickname,
      phone: registerForm.phone
    }

    // 调用注册API
    const result = await registerAPI(registerData)

    if (result.code === 1) {
      ElMessage.success('注册成功！')
      // 注册成功后跳转到登录页面
      setTimeout(() => {
        router.push('/login')
      }, 1500)
    } else {
      ElMessage.error(result.msg || '注册失败')
    }
  } catch (error: any) {
    if (error.errors) {
      ElMessage.error('请完善表单信息')
    } else {
      ElMessage.error('注册失败，请稍后重试')
    }
  } finally {
    loading.value = false
  }
}

// 跳转到登录页面
const goToLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.register-form-wrapper {
  width: 100%;
  max-width: 420px;
}

.register-card {
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  border: none;
}

.card-header {
  text-align: center;
  padding: 10px 0;
}

.register-title {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.register-subtitle {
  margin: 0;
  font-size: 14px;
  color: #909399;
}

.register-form {
  padding: 10px 0;
}

.register-form :deep(.el-form-item) {
  margin-bottom: 20px;
}

.register-form :deep(.el-input__wrapper) {
  border-radius: 8px;
  padding: 12px 16px;
}

.register-form :deep(.el-input__inner) {
  font-size: 14px;
}

.register-btn {
  width: 100%;
  height: 48px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  margin-top: 10px;
}

.login-link {
  text-align: center;
  margin-top: 20px;
  color: #606266;
  font-size: 14px;
}

.login-link .el-link {
  font-size: 14px;
  margin-left: 4px;
}

/* 响应式设计 */
@media (max-width: 480px) {
  .register-form-wrapper {
    max-width: 100%;
  }

  .register-container {
    padding: 16px;
  }
}
</style>