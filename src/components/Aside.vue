<script setup lang="ts">
import { ref, onMounted } from 'vue'
import {User, Menu as IconMenu, Document, Notification, Setting, Tools, Location} from '@element-plus/icons-vue'
import {useRouter} from "vue-router";

const router = useRouter()

interface MenuItem {
  index: string
  title: string
  icon: any
  route?: string  // 添加路由路径
  children?: MenuItem[]
}

const userAuthority = ref('')
const menuItems = ref<MenuItem[]>([])

onMounted(() => {
  // 从localStorage或store获取用户角色
  const userInfo = localStorage.getItem('userInfo')
  if (userInfo) {
    const userData = JSON.parse(userInfo)
    userAuthority.value = userData.authorityId
  }
  //在组件挂载时生成菜单
  generateMenu()
})

const generateMenu = () => {
  switch ( Number(userAuthority.value) ) {
    case 1:
    case 3:
      menuItems.value = [
        { index: '1', title: '故障列表', icon: IconMenu, route: '/adminDashboard/RepairOrderList' },
        { index: '2', title: '用户中心', icon: User,
          children: [
            { index: '2-1', title: '管理员', icon: null, route: '/adminDashboard/userCenter_admin'},
            { index: '2-2', title: '普通用户', icon: null, route: '/adminDashboard/userCenter_user'},
            { index: '2-3', title: '维修人员', icon: null, route: '/adminDashboard/userCenter_repm'}
          ]
        },
        { index: '3', title: '报修分类', icon: Document, route: '/adminDashboard/category' },
        { index: '4', title: '通知公告', icon: Notification, route: '/adminDashboard/NoticesCtl' },
        { index: '5', title: '常见问题', icon: Setting, route: '/adminDashboard/FAQsCtl' },
        { index: '6', title: '地址分类', icon: Location, route: '/adminDashboard/location' },
        { index: '7', title: '个人中心', icon: Setting, route: '/adminDashboard/userProfile' }
      ]
      break
    case 2:
      menuItems.value = [
        { index: '1', title: '报修信息', icon: Tools, route: '/repairmanDashboard/RepairRequests'},
        { index: '2', title: '通知公告', icon: Notification, route: '/repairmanDashboard/Notices'},
        { index: '3', title: '个人中心', icon: User, route: '/repairmanDashboard/userProfile'}
      ]
      break
    case 0:
      menuItems.value = [
        {
          index: '1',
          title: '报修服务',
          icon: IconMenu,
          children: [
            { index: '1-1', title: '我要报修', icon: null, route: '/userDashboard/AddRepOrder'},
            { index: '1-2', title: '我的报修情况', icon: null, route: '/userDashboard/MyOrders'}
          ]
        },
        { index: '2', title: '通知公告', icon: Notification, route: '/userDashboard/Notices'},
        { index: '3', title: '常见问题', icon: Setting, route: '/userDashboard/FAQ'},
        { index: '4', title: '个人中心', icon: User, route: '/userDashboard/userProfile' }
      ]
      break
    default:
      menuItems.value = []
  }
}

// 菜单点击处理
const handleMenuClick = (item: MenuItem) => {
  if (item.route) {
    router.push(item.route)
  }
}
</script>

<template>
  <el-aside class="sidebar">
    <el-menu
        active-text-color="#ffd04b"
        background-color="#545c64"
        class="sidebar-menu"
        :default-active="$route.path"
        text-color="#fff"
        router
    >
      <template v-for="item in menuItems" :key="item.index">
        <!--用来渲染有子类菜单的-->
        <el-sub-menu v-if="item.children" :index="item.index">
          <template #title>
            <el-icon><component :is="item.icon" /></el-icon>
            <span>{{ item.title }}</span>
          </template>
          <el-menu-item
              v-for="child in item.children"
              :key="child.index"
              :index="child.index"
              @click="handleMenuClick(child)"
          >
            <span>{{ child.title }}</span>
          </el-menu-item>
        </el-sub-menu>

        <!--用来渲染没有子类菜单的-->
        <el-menu-item v-else :index="item.index" @click="handleMenuClick(item)">
          <el-icon><component :is="item.icon" /></el-icon>
          <span>{{ item.title }}</span>
        </el-menu-item>
      </template>

    </el-menu>
  </el-aside>
</template>

<style scoped>
.sidebar {
  width: 220px;
  position: fixed;
  top: 60px;
  left: 0;
  bottom: 0;
  overflow: hidden;
}

.sidebar-menu {
  height: 100%;
  overflow-y: auto;
  border-right: none;
}
</style>