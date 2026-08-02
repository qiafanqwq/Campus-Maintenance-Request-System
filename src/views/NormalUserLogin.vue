<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { loginAPI, type UserLoginDTO} from "@/api/login.ts"
import 'element-plus/es/components/message-box/style/css'
import 'element-plus/es/components/message/style/css'

// 路由
const router = useRouter()
// 响应式数据
const loginFormRef = ref()
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: '',
  authority: 'user' // 默认选择普通用户
})

// 表单验证规则
const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 8, max: 15, message: '用户名长度在 8 到 15 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 8, max: 15, message: '密码长度在 8 到 15 个字符', trigger: 'blur' },
    {
      pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).+$/,
      message: '密码必须包含大小写字母和数字',
      trigger: 'blur'
    }
  ]
}

// 登录方法
const handleLogin = async () => {
  if (!loginFormRef.value) return
  try {
    const valid = await loginFormRef.value.validate()
    if (!valid) return

    loading.value = true

    const loginData: UserLoginDTO = {
      username: loginForm.username,
      password: loginForm.password,
      authority: loginForm.authority
    }

    const response = await loginAPI(loginData)

    if (response.code === 1) {
      const userData = response.data

      // 分离存储：userInfo 使其不包含 token
      const userInfo = {
        id: userData.id,
        username: userData.username,
        nickname: userData.nickname,
        authorityId: userData.authorityId
      }

      localStorage.setItem('userInfo', JSON.stringify(userInfo))
      localStorage.setItem('token', userData.token)

      ElMessage.success("登录成功")


      await router.push('/userDashboard/AddRepOrder')

    } else {
      ElMessage.error(response.msg || "登录失败")
    }
  } catch(error){
    console.error('登录错误:', error)
    ElMessage.error('登录失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 跳转到注册页面
const goToRegister = () => {
  router.push('/register')
}
</script>

<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <h2>系统登录</h2>
        <p class="login-subtitle">欢迎回来，请登录您的账户</p>
      </div>

      <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          class="login-form"
          @submit.prevent="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
              v-model="loginForm.username"
              placeholder="请输入用户名"
              size="large"
              :prefix-icon="User"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              size="large"
              :prefix-icon="Lock"
              show-password
              @keyup.enter="handleLogin"
          />
        </el-form-item>

        <el-form-item>
          <el-button
              type="primary"
              size="large"
              class="login-btn"
              :loading="loading"
              @click="handleLogin"
          >
            {{ loading ? '登录中...' : '登录' }}
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 用户类型选择 -->
      <div class="user-type-selector">
        <el-radio-group v-model="loginForm.authority" class="type-radio-group">
          <el-radio label="user" size="large">普通用户</el-radio>
          <el-radio label="adminOrRepman" size="large">管理员/维修人员</el-radio>
        </el-radio-group>
      </div>

      <!-- 注册链接 -->
      <div class="register-link">
        <span>没有账号？</span>
        <el-link type="primary" @click="goToRegister" :underline="false">
          前往注册
        </el-link>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-box {
  width: 400px;
  padding: 40px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
  position: relative;
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-header h2 {
  color: #333;
  font-weight: 600;
  margin: 0 0 8px 0;
  font-size: 24px;
}

.login-subtitle {
  color: #909399;
  font-size: 14px;
  margin: 0;
}

.user-type-selector {
  margin-bottom: 25px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.type-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 10px;
  font-weight: 500;
}

.type-radio-group {
  display: flex;
  gap: 20px;
}

.type-radio-group :deep(.el-radio) {
  margin-right: 0;
}

.type-radio-group :deep(.el-radio__label) {
  font-size: 14px;
  color: #606266;
}

.login-form {
  width: 100%;
}

.login-btn {
  width: 100%;
  margin-top: 10px;
  height: 44px;
  font-size: 16px;
  font-weight: 500;
}

.register-link {
  text-align: center;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
  color: #606266;
  font-size: 14px;
}

.register-link .el-link {
  font-size: 14px;
  font-weight: 500;
  margin-left: 4px;
}

:deep(.el-input__wrapper) {
  border-radius: 6px;
  padding: 12px 16px;
}

:deep(.el-input__inner) {
  font-size: 14px;
}

/* 响应式设计 */
@media (max-width: 480px) {
  .login-box {
    width: 90%;
    padding: 30px 20px;
    margin: 20px;
  }

  .type-radio-group {
    flex-direction: column;
    gap: 10px;
  }
}
</style>