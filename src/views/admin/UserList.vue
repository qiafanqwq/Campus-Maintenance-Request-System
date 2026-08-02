<script lang="ts" setup>
import {onMounted, reactive, ref} from "vue";
import {ElMessage, ElMessageBox} from 'element-plus'
import {addUserAPI1, pageUserAPI, starOrStopNormalUserAPI, updateUserAPI1} from '@/api/usersPageQuery.ts'
import TableFilter from '@/components/table/TableFilter.vue'
import TableContent from "@/components/table/TableContent.vue";
import TablePagination from "@/components/table/TablePagination.vue";
import EditUserDialog from "@/components/table/EditUserDialog.vue";

const UserPageQueryDTO = reactive({
  nickname: '',
  page: 1,
  pageSize: 10
})
const pageResult = reactive({
  total: 0,
  records: [] as any[]
})

const editDialogVisible = ref(false)
const editForm = reactive({
  id: 1,
  nickname: '',
  username: '',
  phone: '',
  email: ''
})
const editFormRules = reactive({
  nickname: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号码', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
})

// 添加用户弹窗相关
const addDialogVisible = ref(false)
const addForm = reactive({
  nickname: '',
  username: '',
  phone: '',
  email: '',
  password: '',
  confirmPassword: '',
  authorityId: 0  //默认为普通用户
})
const addFormRules = reactive({
  nickname: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  username: [
    { required: true, message: '请输入学号/工号', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号码', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
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
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule: any, value: string, callback: any) => {
        if (value !== addForm.password) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  authorityId: [
    { required: true, message: '请选择用户权限', trigger: 'change' }
  ]
})

// 分页大小变化
const handleSizeChange = (val: number) => {
  UserPageQueryDTO.pageSize = val
  QueryUsers() // 分页大小改变时重新查询
}

// 当前页变化
const handleCurrentChange = (val: number) => {
  UserPageQueryDTO.page = val
  QueryUsers() // 页码改变时重新查询
}

//查询用户
const QueryUsers = async () => {
  try{
    const response = await pageUserAPI(UserPageQueryDTO)
    if (response.code === 1) {
      pageResult.total = response.data.total
      pageResult.records = response.data.records || []
    }else{
      ElMessage.error("查询失败")
    }
  }catch(error) {
    console.error('查询失败:', error)
  }
}

// 编辑操作
const handleEdit = (row: any) => {
  // 将行数据填充到表单中
  Object.assign(editForm, {
    id: row.id,
    nickname: row.nickname,
    username: row.username,
    phone: row.phone,
    email: row.email,
  })
  editDialogVisible.value = true
}

// 提交编辑
const submitEdit = async () => {
  try {
    const response = await updateUserAPI1(editForm)
    if (response.code === 1) {
      ElMessage.success('用户信息更新成功')
      editDialogVisible.value = false
    }
    // 重新加载数据
    await QueryUsers()
  } catch (error) {
    ElMessage.error('更新失败，请重试')
  }
}

// 关闭弹窗
const handleCloseDialog = (type: 'edit' | 'add') => {
  if (type === 'edit') {
    editDialogVisible.value = false
  } else {
    addDialogVisible.value = false
  }
}

//改变用户账户状态
const handleToggleStatus = (row: any) => {
  const action = row.status === 1 ? '禁用' : '启用'
  const newStatus = row.status === 1 ? 0 : 1

  ElMessageBox.confirm(
      `确定要${action}用户 "${row.nickname}" 吗？`,
      `${action}确认`,
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: row.status === 1 ? 'warning' : 'success',
      }
  ).then(async () => {
    try {
      // 调用更新状态的API
      const response = await starOrStopNormalUserAPI(row.id, newStatus)
      if (response.code === 1) {
        ElMessage.success(`用户已${action}`)
        // 重新加载数据
        await QueryUsers()
      }
    } catch (error) {
      ElMessage.error(`${action}失败，请重试`)
    }
  }).catch(() => {
    ElMessage.info('已取消操作')
  })
}


// 添加用户操作
const handleAdd = () => {
  // 重置表单
  Object.assign(addForm, {
    nickname: '',
    username: '',
    phone: '',
    email: '',
    password: '',
    confirmPassword: '',
    authorityId: 0
  })
  addDialogVisible.value = true
}

// 提交添加用户
const submitAdd = async () => {
  try {
    // 这里调用添加用户的API
    const response = await addUserAPI1(addForm)
    if(response.code === 1) {
      ElMessage.success('用户添加成功')
      addDialogVisible.value = false
      // 重新加载数据
      await QueryUsers()
    }
  } catch (error) {
    ElMessage.error('添加失败，请重试')
  }
}

// 页面加载时自动查询
onMounted(() => {
  QueryUsers()
})
</script>

<template>
  <!-- 筛选区和添加区 -->
  <TableFilter
      v-model:search-name="UserPageQueryDTO.nickname"
      @query="QueryUsers"
      @add="handleAdd"
  />

  <!-- 列表展示区 -->
  <TableContent
      :table-data="pageResult.records"
      @edit="handleEdit"
      @toggleStatus="handleToggleStatus"
  />

  <!-- 分页区 -->
  <TablePagination
      v-model:current-page="UserPageQueryDTO.page"
      v-model:page-size="UserPageQueryDTO.pageSize"
      :total="pageResult.total"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
  />

  <!-- 编辑弹窗和添加弹窗 -->
  <EditUserDialog
      v-model:visible="editDialogVisible"
      :form-data="editForm"
      :rules="editFormRules"
      @confirm="submitEdit"
      @close="() => handleCloseDialog('edit')"
  />
  <AddUserDialog
      v-model:visible="addDialogVisible"
      v-model:form-data="addForm"
      :rules="addFormRules"
      @confirm="submitAdd"
      @close="addDialogVisible = false"
  />

</template>

<style scoped>
</style>