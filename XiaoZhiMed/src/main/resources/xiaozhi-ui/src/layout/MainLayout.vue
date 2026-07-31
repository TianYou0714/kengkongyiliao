<template>
  <div class="layout">
    <!-- 左侧业务导航栏 -->
    <aside class="sidebar">
      <div class="brand">
        <div class="brand-icon">
          <el-icon :size="26" color="#0e7490"><FirstAidKit /></el-icon>
        </div>
        <div class="brand-text">
          <span class="brand-name">可控医疗</span>
          <span class="brand-sub">KENGKONG MEDICAL</span>
        </div>
      </div>

      <div class="menu-label">智能服务</div>
      <!-- AI 对话入口：显眼位置 -->
      <router-link to="/chat" class="ai-entry" :class="{ active: isActive('/chat') }">
        <div class="ai-entry-icon">
          <el-icon :size="22"><ChatDotRound /></el-icon>
        </div>
        <div class="ai-entry-text">
          <span class="ai-entry-title">AI 智能问诊</span>
          <span class="ai-entry-desc">7×24 在线医疗助手</span>
        </div>
        <span class="ai-entry-badge">AI</span>
      </router-link>

      <div class="menu-label">业务管理</div>
      <nav class="menu">
        <router-link
          v-for="item in menus"
          :key="item.path"
          :to="item.path"
          class="menu-item"
          :class="{ active: isActive(item.path) }"
        >
          <el-icon :size="18"><component :is="item.icon" /></el-icon>
          <span>{{ item.title }}</span>
        </router-link>
      </nav>

      <div class="sidebar-footer">
        <span>可控医疗管理平台</span>
        <span class="version">v2.0</span>
      </div>
    </aside>

    <!-- 右侧内容区 -->
    <div class="main">
      <header class="header">
        <div class="header-title">
          <span class="page-title">{{ route.meta.title || '可控医疗' }}</span>
          <span class="page-desc">{{ pageDesc }}</span>
        </div>
        <div class="header-right">
          <el-tag type="success" effect="plain" round size="small">系统运行中</el-tag>
          <div class="avatar">
            <el-icon :size="18" color="#0e7490"><UserFilled /></el-icon>
          </div>
        </div>
      </header>
      <main class="content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const menus = [
  { path: '/appointment', title: '预约挂号', icon: 'Calendar' },
  { path: '/doctor', title: '医生管理', icon: 'UserFilled' },
  { path: '/department', title: '科室管理', icon: 'OfficeBuilding' },
  { path: '/patient', title: '患者管理', icon: 'User' },
]

const descMap = {
  '/chat': '与 AI 医疗助手对话，快速获取健康建议',
  '/appointment': '管理患者的预约挂号记录',
  '/doctor': '维护医生信息与出诊安排',
  '/department': '维护医院科室信息',
  '/patient': '维护患者档案信息',
}

const isActive = (path) => route.path === path
const pageDesc = computed(() => descMap[route.path] || '')
</script>

<style scoped>
.layout {
  display: flex;
  height: 100vh;
  background: #f1f5f9;
}

/* ---------- 左侧导航 ---------- */
.sidebar {
  width: 248px;
  flex-shrink: 0;
  background: linear-gradient(180deg, #155e75 0%, #0e7490 55%, #0f766e 100%);
  display: flex;
  flex-direction: column;
  padding: 20px 14px;
  color: #fff;
  box-shadow: 2px 0 12px rgba(14, 116, 144, 0.25);
  z-index: 10;
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 4px 8px 18px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.15);
}

.brand-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.15);
}

.brand-text {
  display: flex;
  flex-direction: column;
}

.brand-name {
  font-size: 20px;
  font-weight: 700;
  letter-spacing: 2px;
}

.brand-sub {
  font-size: 10px;
  opacity: 0.65;
  letter-spacing: 1px;
  margin-top: 2px;
}

.menu-label {
  font-size: 12px;
  opacity: 0.55;
  margin: 18px 10px 8px;
  letter-spacing: 1px;
}

/* AI 对话入口卡片 */
.ai-entry {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.14);
  border: 1px solid rgba(255, 255, 255, 0.25);
  text-decoration: none;
  color: #fff;
  transition: all 0.25s ease;
  position: relative;
}

.ai-entry:hover {
  background: rgba(255, 255, 255, 0.22);
  transform: translateY(-1px);
}

.ai-entry.active {
  background: #fff;
  color: #0e7490;
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.18);
}

.ai-entry-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.ai-entry.active .ai-entry-icon {
  background: linear-gradient(135deg, #06b6d4, #0e7490);
  color: #fff;
}

.ai-entry-text {
  display: flex;
  flex-direction: column;
  flex: 1;
  min-width: 0;
}

.ai-entry-title {
  font-size: 15px;
  font-weight: 600;
}

.ai-entry-desc {
  font-size: 11px;
  opacity: 0.7;
  margin-top: 2px;
}

.ai-entry-badge {
  font-size: 10px;
  font-weight: 700;
  padding: 2px 7px;
  border-radius: 999px;
  background: linear-gradient(135deg, #f59e0b, #f97316);
  color: #fff;
  flex-shrink: 0;
}

/* 普通菜单 */
.menu {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 11px 14px;
  border-radius: 10px;
  color: rgba(255, 255, 255, 0.82);
  text-decoration: none;
  font-size: 14px;
  transition: all 0.2s ease;
}

.menu-item:hover {
  background: rgba(255, 255, 255, 0.12);
  color: #fff;
}

.menu-item.active {
  background: #fff;
  color: #0e7490;
  font-weight: 600;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.12);
}

.sidebar-footer {
  margin-top: auto;
  padding: 14px 10px 4px;
  border-top: 1px solid rgba(255, 255, 255, 0.15);
  font-size: 12px;
  opacity: 0.6;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.version {
  background: rgba(255, 255, 255, 0.15);
  padding: 1px 8px;
  border-radius: 999px;
  font-size: 11px;
}

/* ---------- 右侧主区域 ---------- */
.main {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.header {
  height: 64px;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  box-shadow: 0 1px 4px rgba(15, 23, 42, 0.06);
  flex-shrink: 0;
}

.header-title {
  display: flex;
  align-items: baseline;
  gap: 12px;
}

.page-title {
  font-size: 18px;
  font-weight: 700;
  color: #0f172a;
}

.page-desc {
  font-size: 12px;
  color: #94a3b8;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 14px;
}

.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #ecfeff;
  border: 1px solid #a5f3fc;
  display: flex;
  align-items: center;
  justify-content: center;
}

.content {
  flex: 1;
  padding: 20px 24px;
  overflow: hidden;
  min-height: 0;
}

/* 移动端适配 */
@media (max-width: 768px) {
  .sidebar {
    width: 72px;
    padding: 14px 8px;
  }
  .brand-text,
  .menu-label,
  .ai-entry-text,
  .ai-entry-badge,
  .menu-item span,
  .sidebar-footer {
    display: none;
  }
  .brand {
    justify-content: center;
    padding-bottom: 12px;
  }
  .ai-entry,
  .menu-item {
    justify-content: center;
    padding: 12px 0;
  }
}
</style>
