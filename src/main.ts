import { createApp, markRaw } from 'vue'
import App from './App.vue'
import router from './router'

// 导入图标
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

const app = createApp(App)

// 注册图标 - 使用 markRaw 包装组件
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, markRaw(component))
}

app.use(router)

app.mount('#app')