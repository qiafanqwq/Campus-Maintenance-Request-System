<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { CircleCloseFilled } from "@element-plus/icons-vue"

const router = useRouter()
const route = useRoute()

// 标签页数据
const tabs = ref<Array<{ name: string; path: string; title: string; closable: boolean }>>([])
const editableTabsValue = ref('')

// 完整的路由名称映射（中文标题）
const routeNameMap: Record<string, string> = {
  // 登录注册
  'Login': '登录',
  'Register': '注册',

  // 公共页面
  'userProfile': '个人中心',
  'Notices': '公告列表',
  'FAQ': '常见问题',

  // 管理员页面
  'userCenter_admin': '管理员管理',
  'userCenter_user': '用户管理',
  'userCenter_repm': '维修人员管理',
  'NoticesCtl': '公告管理',
  'FAQsCtl': 'FAQ管理',
  'category': '分类管理',
  'location': '地址管理',
  'RepairOrderList': '报修单管理',

  // 维修人员页面
  'RepairRequests': '报修信息',

  // 普通用户页面
  'AddRepOrder': '我要报修',
  'MyOrders': '我的报修'
}

// 路径到标题的映射（用于没有name的路由）
const pathNameMap: Record<string, string> = {
  '/adminDashboard/userProfile': '个人中心',
  '/adminDashboard/userCenter_admin': '管理员管理',
  '/adminDashboard/userCenter_user': '用户管理',
  '/adminDashboard/userCenter_repm': '维修人员管理',
  '/adminDashboard/NoticesCtl': '公告管理',
  '/adminDashboard/FAQsCtl': 'FAQ管理',
  '/adminDashboard/category': '分类管理',
  '/adminDashboard/location': '地址管理',
  '/adminDashboard/RepairOrderList': '报修单管理',

  '/repairmanDashboard/RepairRequests': '报修信息',
  '/repairmanDashboard/Notices': '公告列表',
  '/repairmanDashboard/userProfile': '个人中心',

  '/userDashboard/AddRepOrder': '我要报修',
  '/userDashboard/MyOrders': '我的报修',
  '/userDashboard/FAQ': '常见问题',
  '/userDashboard/Notices': '公告列表',
  '/userDashboard/userProfile': '个人中心'
}

// 不需要显示标签页的路由
const excludeRoutes = ['/', '/login', '/register']

// 获取路由标题
const getRouteTitle = (routeName: string, path: string): string => {
  // 优先使用路由名称映射
  if (routeName && routeNameMap[routeName]) {
    return routeNameMap[routeName]
  }

  // 其次使用路径映射
  if (pathNameMap[path]) {
    return pathNameMap[path]
  }

  // 最后使用路径自动生成
  const pathParts = path.split('/').filter(part => part)
  const lastPart = pathParts[pathParts.length - 1]

  // 简单的路径到中文的映射
  const autoMap: Record<string, string> = {
    'adminDashboard': '管理员面板',
    'repairmanDashboard': '维修员面板',
    'userDashboard': '用户面板',
    'userProfile': '个人中心',
    'userCenter': '用户中心',
    'Notices': '公告',
    'FAQ': '常见问题',
    'RepairRequests': '报修信息',
    'AddRepOrder': '我要报修',
    'MyOrders': '我的报修'
  }

  return autoMap[lastPart] ||
      lastPart?.replace(/([A-Z])/g, ' $1').replace(/^./, str => str.toUpperCase()).trim() ||
      '未知页面'
}

// 添加标签页
const addTab = (routePath: string, routeName: string) => {
  if (excludeRoutes.includes(routePath)) return

  const existingTab = tabs.value.find(tab => tab.path === routePath)
  if (!existingTab) {
    const newTab = {
      name: routeName || routePath,
      path: routePath,
      title: getRouteTitle(routeName, routePath),
      closable: routePath !== '/adminDashboard/RepairOrderList' &&
          routePath !== '/repairmanDashboard/RepairRequests' &&
          routePath !== '/userDashboard/MyOrders'
    }
    tabs.value.push(newTab)
  }
  editableTabsValue.value = routePath
}

// 移除标签页
const removeTab = (targetPath: string) => {
  const currentIndex = tabs.value.findIndex(tab => tab.path === editableTabsValue.value)
  const targetIndex = tabs.value.findIndex(tab => tab.path === targetPath)

  if (targetIndex === -1) return

  tabs.value.splice(targetIndex, 1)

  // 如果关闭的是当前激活的标签页
  if (targetPath === editableTabsValue.value) {
    // 跳转到前一个标签页或第一个标签页
    if (tabs.value.length > 0) {
      const newActiveTab = tabs.value[Math.min(targetIndex, tabs.value.length - 1)]
      editableTabsValue.value = newActiveTab.path
      router.push(newActiveTab.path)
    } else {
      // 如果没有标签页了，跳转到默认页面
      const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
      if (userInfo.userAuthority === 1 || userInfo.userAuthority === 4) {
        router.push('/adminDashboard/RepairOrderList')
      } else if (userInfo.userAuthority === 2) {
        router.push('/repairmanDashboard/RepairRequests')
      } else if (userInfo.userAuthority === 3) {
        router.push('/userDashboard/MyOrders')
      }
    }
  }
}

// 切换标签页
const handleTabClick = (tab: any) => {
  if (tab.paneName !== route.path) {
    router.push(tab.paneName)
  }
}

// 退出登录
const logout = () => {
  localStorage.removeItem('userInfo')
  localStorage.removeItem('tabs') // 清除保存的标签页
  router.push('/login')
}

// 保存标签页到localStorage
const saveTabs = () => {
  localStorage.setItem('tabs', JSON.stringify(tabs.value))
  localStorage.setItem('activeTab', editableTabsValue.value)
}

// 从localStorage恢复标签页
const restoreTabs = () => {
  const savedTabs = localStorage.getItem('tabs')
  const savedActiveTab = localStorage.getItem('activeTab')

  if (savedTabs) {
    tabs.value = JSON.parse(savedTabs)
    if (savedActiveTab && tabs.value.some(tab => tab.path === savedActiveTab)) {
      editableTabsValue.value = savedActiveTab
    } else if (tabs.value.length > 0) {
      editableTabsValue.value = tabs.value[0].path
    }
  }
}

// 监听路由变化
watch(
    () => route.path,
    (newPath, oldPath) => {
      if (newPath !== oldPath) {
        addTab(newPath, route.name as string)
        saveTabs()
      }
    },
    { immediate: true }
)

// 初始化
onMounted(() => {
  restoreTabs()
  // 添加当前路由到标签页
  if (route.path && !excludeRoutes.includes(route.path)) {
    addTab(route.path, route.name as string)
  }
})
</script>

<template>
  <el-header class="fixed-header">
    <div class="header-left">
      <img src="@/assets/logo.png" width="50px" alt="" class="logo">
      <span class="system-title">网络故障报修系统</span>
    </div>

    <div class="header-center">
      <el-tabs
          v-model="editableTabsValue"
          type="card"
          class="dynamic-tabs"
          closable
          @tab-click="handleTabClick"
          @tab-remove="removeTab"
      >
        <el-tab-pane
            v-for="item in tabs"
            :key="item.path"
            :label="item.title"
            :name="item.path"
            :closable="item.closable"
        />
      </el-tabs>
    </div>

    <div class="header-right">
      <el-dropdown>
        <img src="@/assets/avatar.png" width="50px" alt="" class="user-avatar">
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item :icon="CircleCloseFilled" @click="logout">
              退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </el-header>
</template>

<style scoped>
.fixed-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  background-color: #a5bacf;
  color: white;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 60px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.header-left {
  display: flex;
  align-items: center;
  flex-shrink: 0;
}

.logo {
  margin-right: 12px;
}

.system-title {
  color: #000000;
  font-size: 18px;
  font-weight: 600;
  white-space: nowrap;
}

.header-center {
  flex: 1;
  display: flex;
  justify-content: center;
  margin: 0 20px;
  min-width: 0;
}

.header-right {
  flex-shrink: 0;
}

.user-avatar {
  border-radius: 50%;
  cursor: pointer;
}

/* 动态标签页样式 */
:deep(.dynamic-tabs) {
  height: 40px;
}

:deep(.dynamic-tabs .el-tabs__header) {
  margin-bottom: 0;
  border-bottom: none;
}

:deep(.dynamic-tabs .el-tabs__nav) {
  border: none;
  border-radius: 6px;
}

:deep(.dynamic-tabs .el-tabs__item) {
  height: 32px;
  line-height: 32px;
  padding: 0 16px;
  background-color: rgba(255, 255, 255, 0.8);
  border: 1px solid #dcdfe6;
  margin-right: 4px;
  border-radius: 4px;
  color: #606266;
  font-size: 14px;
  transition: all 0.3s;
}

:deep(.dynamic-tabs .el-tabs__item:hover) {
  background-color: rgba(255, 255, 255, 0.9);
  color: #409eff;
}

:deep(.dynamic-tabs .el-tabs__item.is-active) {
  background-color: #ffffff;
  border-color: #409eff;
  color: #409eff;
  font-weight: 500;
}

:deep(.dynamic-tabs .el-tabs__item .is-closable:hover) {
  background-color: #f56c6c;
  color: white;
}

:deep(.dynamic-tabs .el-tabs__nav-wrap) {
  padding-bottom: 0;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .system-title {
    font-size: 16px;
  }

  :deep(.dynamic-tabs .el-tabs__item) {
    padding: 0 12px;
    font-size: 13px;
  }
}

@media (max-width: 768px) {
  .fixed-header {
    padding: 0 10px;
  }

  .system-title {
    display: none;
  }

  .header-center {
    margin: 0 10px;
  }
}
</style>