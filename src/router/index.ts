import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
      {
        path: '/',
        redirect: '/login'
      },
      {
        path: '/login',
        name: 'Login',
        component: () => import('@/views/login.vue'),
        meta: { requiresAuth: false }
      },
      {

      },
      {
          path: '/register',
          name: 'Register',
          component: () => import('@/views/Register.vue'),
          meta: { requiresAuth: false }
      },
      {
          path: '/adminDashboard',
          component: () => import('@/views/admin/AdminDashboard.vue'),
          meta: { requiresAuth: true },
          children: [
              {  path: "userProfile", component: () => import('@/views/public/userProfile.vue')  },
              {  path: 'userCenter_admin', component: () => import('@/views/admin/AdminList.vue')  },
              {  path: 'userCenter_user', component: () => import('@/views/admin/UserList.vue')  },
              {  path: 'userCenter_repm', component: () => import('@/views/admin/RepairmanList.vue')  },
              {  path: 'NoticesCtl', component: () => import('@/views/admin/NoticesCtl.vue')  },
              {  path: 'FAQsCtl', component: () => import('@/views/admin/FAQsCtl.vue')  },
              {  path: 'category', component: () => import('@/views/admin/CategoryList.vue')  },
              {  path: 'location', component: () => import('@/views/admin/LocationList.vue')  },
              {  path: 'RepairOrderList', component: () => import('@/views/admin/RepairOrderList.vue')  },
          ]
      },
      {
          path: '/repairmanDashboard',
          component: () => import('@/views/repairman/RepairmanDashboard.vue'),
          meta: { requiresAuth: true },
          children: [
              {  path: 'RepairRequests', component: () => import('@/views/repairman/RepairRequests.vue')  },
              {  path: 'Notices', component: () => import('@/views/public/Notices.vue')  },
              {  path: "userProfile", component: () => import('@/views/public/userProfile.vue')  },
          ]
      },
      {
          path: '/userDashboard',
          component: () => import('@/views/user/UserDashboard.vue'),
          meta: { requiresAuth: true },
          children: [
              {  path: "AddRepOrder", component: () => import('@/views/user/AddRepOrder.vue')  },
              {  path: 'MyOrders', component: () => import('@/views/user/MyOrders.vue')  },
              {  path: 'FAQ', component: () => import('@/views/public/FAQ.vue')  },
              {  path: 'Notices', component: () => import('@/views/public/Notices.vue')  },
              {  path: "userProfile", component: () => import('@/views/public/userProfile.vue')  },
          ]
      }


  ],
})

// 路由守卫 - 检查认证状态
router.beforeEach((to, from, next) => {
    // 获取用户信息
    const userInfo = localStorage.getItem('userInfo')
    const token = localStorage.getItem('token')
    // 检查目标路由是否需要认证
    if (to.meta.requiresAuth) {
        if (!userInfo || !token) {
            // 需要认证但未登录，重定向到登录页
            next('/login')
        } else {
            // 已登录，允许访问
            next()
        }
    } else {
        // 不需要认证的路由，直接允许访问
        next()
    }
})
export default router