<template>
  <div class="personal-center">
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <span class="title">
            <el-icon><UserFilled /></el-icon>
            个人信息
          </span>
          <div>
            <el-button
                type="primary"
                :icon="Edit"
                @click="editMode = true"
                v-if="!editMode && userInfo.id"
            >
              编辑信息
            </el-button>
            <el-button
                type="warning"
                :icon="Lock"
                @click="showPasswordDialog = true"
                v-if="!editMode && userInfo.id"
            >
              修改密码
            </el-button>
            <div v-else-if="editMode">
              <el-button type="success" :icon="Check" @click="saveUserInfo">保存</el-button>
              <el-button :icon="Close" @click="cancelEdit">取消</el-button>
            </div>
          </div>
        </div>
      </template>

      <!-- 取消编辑确认对话框 -->
      <el-dialog
          v-model="showCancelDialog"
          title="提示"
          width="400px"
          align-center
      >
        <span>确定取消编辑吗？所有更改将不会保存</span>

        <template #footer>
        <span class="dialog-footer">
          <el-button @click="showCancelDialog = false">取消</el-button>
          <el-button type="primary" @click="confirmCancel">确定</el-button>
        </span>
        </template>
      </el-dialog>

      <!-- 修改密码对话框 -->
      <el-dialog
          v-model="showPasswordDialog"
          title="修改密码"
          width="500px"
          align-center
          :before-close="handlePasswordDialogClose"
      >
        <el-form
            :model="passwordForm"
            :rules="passwordRules"
            ref="passwordFormRef"
            label-width="100px"
            label-position="left"
        >
          <el-form-item label="旧密码" prop="oldPassword">
            <el-input
                v-model="passwordForm.oldPassword"
                type="password"
                placeholder="请输入旧密码"
                show-password
                clearable
                size="large"
            />
          </el-form-item>

          <el-form-item label="新密码" prop="newPassword">
            <el-input
                v-model="passwordForm.newPassword"
                type="password"
                placeholder="请输入新密码"
                show-password
                clearable
                size="large"
            />
          </el-form-item>

          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input
                v-model="passwordForm.confirmPassword"
                type="password"
                placeholder="请再次输入新密码"
                show-password
                clearable
                size="large"
            />
          </el-form-item>
        </el-form>

        <template #footer>
          <span class="dialog-footer">
            <el-button
                @click="handlePasswordDialogClose"
                :disabled="passwordLoading"
            >
              取消
            </el-button>
            <el-button
                type="primary"
                @click="changePassword"
                :loading="passwordLoading"
            >
              确认修改
            </el-button>
          </span>
        </template>
      </el-dialog>

      <!-- 加载状态 -->
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="6" animated />
      </div>

      <!-- 查看模式 -->
      <div v-else-if="!editMode && userInfo.id" class="info-display">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="用户ID">
            <el-tag type="info">{{ userInfo.id }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="用户名">
            {{ userInfo.username }}
          </el-descriptions-item>
          <el-descriptions-item label="昵称">
            <el-tag effect="plain">{{ userInfo.nickname }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="手机号">
            <div class="contact-info">
              <el-icon><Iphone /></el-icon>
              {{ userInfo.phone || '未填写' }}
            </div>
          </el-descriptions-item>
          <el-descriptions-item label="邮箱">
            <div class="contact-info">
              <el-icon><Message /></el-icon>
              {{ userInfo.email || '未填写' }}
            </div>
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="userInfo.status === 1 ? 'success' : 'danger'">
              {{ userInfo.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>
      </div>

      <!-- 编辑模式 -->
      <div v-else-if="editMode" class="info-edit">
        <el-form
            :model="editForm"
            :rules="formRules"
            ref="formRef"
            label-width="100px"
            label-position="left"
        >
          <el-form-item label="用户名" prop="username">
            <el-input
                v-model="editForm.username"
                placeholder="请输入用户名"
                clearable
                disabled
            />
          </el-form-item>

          <el-form-item label="昵称" prop="nickname">
            <el-input
                v-model="editForm.nickname"
                placeholder="请输入昵称"
                clearable
            />
          </el-form-item>

          <el-form-item label="手机号" prop="phone">
            <el-input
                v-model="editForm.phone"
                placeholder="请输入手机号"
                clearable
                maxlength="11"
            >
              <template #prefix>
                <el-icon><Iphone /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item label="邮箱" prop="email">
            <el-input
                v-model="editForm.email"
                placeholder="请输入邮箱"
                clearable
            >
              <template #prefix>
                <el-icon><Message /></el-icon>
              </template>
            </el-input>
          </el-form-item>
        </el-form>
      </div>

      <!-- 无数据状态 -->
      <div v-else class="no-data">
        <el-empty description="暂无用户数据" />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { Edit, Check, Close, Iphone, Message, UserFilled, Lock } from '@element-plus/icons-vue'
import {
  updateUserAPI2,
  type UserDTO,
  type UserVO,
  updateUserAPI3,
  updateUserAPI4,
  displayNormalUserInfoAPI,
  displayAdminOrRepairmanInfoAPI,
  editPwd1, editPwd2, type UserPwdDTO
} from '@/api/usersPageQuery.ts'

// 响应式数据
const loading = ref(true)
const editMode = ref(false)
const showCancelDialog = ref(false)
const showPasswordDialog = ref(false)
const formRef = ref<FormInstance>()
const passwordFormRef = ref<FormInstance>()
const passwordLoading = ref(false)

const userInfo = ref<UserVO>({
  id: 0,
  username: '',
  nickname: '',
  phone: '',
  email: '',
  status: 0
})

const editForm = reactive<UserDTO>({
  id: 0,
  username: '',
  nickname: '',
  phone: '',
  email: ''
})

// 密码表单
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 个人信息表单验证规则
const formRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '用户名长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 1, max: 20, message: '昵称长度在 1 到 20 个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

// 密码表单验证规则
const passwordRules: FormRules = {
  oldPassword: [
    { required: true, message: '请输入旧密码', trigger: 'blur' },
    { min: 8, max: 15, message: '密码长度在 8 到 15 个字符', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 8, max: 15, message: '密码长度在 8 到 15 个字符', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value && value === passwordForm.oldPassword) {
          callback(new Error('新密码不能与旧密码相同'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    },
    {
      pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).+$/,
      message: '密码必须包含大小写字母和数字',
      trigger: 'blur'
    }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value && value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 获取当前用户ID（从localStorage或store中获取）
const getCurrentUserId = (): number => {
  // 从localStorage获取用户信息
  const userStr = localStorage.getItem('userInfo')
  const user = userStr ? JSON.parse(userStr) : null
  return user.id
}


// 加载用户信息
const loadUserInfo = async () => {
  try {
    loading.value = true
    const userStr = localStorage.getItem('userInfo')
    const user = userStr ? JSON.parse(userStr) : null

    let result = null;
    console.log(user)

    if (user.authorityId === 0 ) {
      result = await displayNormalUserInfoAPI(user.id)
    }else{
      result = await displayAdminOrRepairmanInfoAPI(user.id)
    }
    if (result.code === 1) {
      userInfo.value = result.data
      // 初始化编辑表单
      Object.assign(editForm, {
        id: userInfo.value.id,
        username: userInfo.value.username,
        nickname: userInfo.value.nickname,
        phone: userInfo.value.phone || '',
        email: userInfo.value.email || ''
      })
    } else {
      ElMessage.error(result.msg || '获取用户信息失败')
    }
  } catch (error: any) {
    ElMessage.error('获取用户信息失败: ' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

// 保存用户信息
const saveUserInfo = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
    const userStr = localStorage.getItem('userInfo')
    const user = userStr ? JSON.parse(userStr) : null
    let result = null;
    if( user.authorityId == 2 ){
      result = await updateUserAPI4(editForm)
    }else if( user.authorityId == 0 ){
      result = await updateUserAPI3(editForm)
    }else if(user.authorityId == 1  ||  user.authorityId == 3){
      result = await updateUserAPI2(editForm)
    } else {
      // 处理其他权限或无权限的情况
      ElMessage.error('当前用户权限无法执行此操作')
      return
    }

    if (result.code === 1) {
      ElMessage.success('个人信息更新成功')
      await loadUserInfo() // 重新加载数据
      editMode.value = false

      // 更新 localStorage 中的 nickname
      const currentUserInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
      if (currentUserInfo) {
        // 只更新 nickname，保持其他信息不变
        currentUserInfo.nickname = editForm.nickname
        localStorage.setItem('userInfo', JSON.stringify(currentUserInfo))
      }

    } else {
      ElMessage.error(result.msg || '更新失败')
    }
  } catch (error: any) {
    console.error('保存失败:', error)
    if (error.errors) {
      ElMessage.error('表单验证失败，请检查输入')
    } else {
      ElMessage.error('保存失败: ' + (error.message || '未知错误'))
    }
  }
}

// 取消编辑
const cancelEdit = async () => {
  showCancelDialog.value = true
}

// 确认取消
const confirmCancel = () => {
  editMode.value = false
  showCancelDialog.value = false
  // 重置表单为原始数据
  Object.assign(editForm, {
    username: userInfo.value.username,
    nickname: userInfo.value.nickname,
    phone: userInfo.value.phone || '',
    email: userInfo.value.email || ''
  })
  ElMessage.info('已取消编辑')
}

// 修改密码
const changePassword = async () => {
  if (!passwordFormRef.value) return

  // 先进行表单验证
  try {
    await passwordFormRef.value.validate()
  } catch (validationError) {
    // 表单验证失败，直接返回
    return
  }

  // 表单验证通过，进行API调用
  try {
    passwordLoading.value = true

    const userStr = localStorage.getItem('userInfo')
    const user = userStr ? JSON.parse(userStr) : null

    const userPwdDTO: UserPwdDTO = {
      id: getCurrentUserId(),
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    }
    let result = null
    if(user.authorityId == 0 ){
      result = await editPwd1(userPwdDTO)
    }else{
      result = await editPwd2(userPwdDTO)
    }
    if (result.code === 1) {
      ElMessage.success('密码修改成功')
      resetPasswordForm()
      showPasswordDialog.value = false
    } else {
      ElMessage.error(result.msg)
    }
  } catch (error: any) {
    // 这里捕获的只是API请求错误，不包括表单验证错误
    if (error.response) {
      ElMessage.error('密码修改失败: ' + (error.response.data?.msg))
    } else {
      ElMessage.error('密码修改失败: ' + (error.message))
    }
  } finally {
    passwordLoading.value = false
  }
}

// 重置密码表单
const resetPasswordForm = () => {
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  if (passwordFormRef.value) {
    nextTick(() => {
      passwordFormRef.value?.clearValidate()
    })
  }
}

// 处理密码对话框关闭
const handlePasswordDialogClose = () => {
  if (passwordLoading.value) {
    ElMessage.info('正在修改密码，请稍候...')
    return
  }
    resetPasswordForm()
    showPasswordDialog.value = false
}

// 生命周期
onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.personal-center {
  height: 100%;
  padding: 20px;
  background-color: #f5f7fa;
  overflow-y: auto;
}

.profile-card {
  border-radius: 8px;
  border: 1px solid #e4e7ed;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  min-height: 500px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
}

.card-header > div {
  display: flex;
  gap: 10px;
}

.title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 8px;
}

.info-display {
  padding: 16px 0;
}

.contact-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.info-edit {
  padding: 16px 0;
}

.loading-container {
  padding: 20px 0;
}

.no-data {
  padding: 40px 0;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

/* 适配侧边栏和头部固定布局 */
:deep(.el-descriptions) {
  margin-top: 8px;
}

:deep(.el-descriptions__label) {
  font-weight: 500;
  color: #606266;
  background-color: #fafafa;
  width: 120px;
}

:deep(.el-descriptions__content) {
  color: #303133;
}

:deep(.el-card__header) {
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
  background-color: #fafbfc;
}

:deep(.el-form-item__label) {
  color: #606266;
  font-weight: 500;
}

:deep(.el-input__inner) {
  border-radius: 6px;
}

/* 对话框样式优化 */
:deep(.el-dialog) {
  border-radius: 8px;
}

:deep(.el-dialog__header) {
  padding: 16px 20px;
  border-bottom: 1px solid #e4e7ed;
  margin-right: 0;
}

:deep(.el-dialog__title) {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

:deep(.el-dialog__body) {
  padding: 20px;
}

/* 密码对话框表单样式 */
:deep(.el-dialog .el-form-item) {
  margin-bottom: 22px;
}

:deep(.el-dialog .el-input__inner) {
  height: 40px;
}
</style>