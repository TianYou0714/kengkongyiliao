import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    component: () => import('@/layout/MainLayout.vue'),
    redirect: '/chat',
    children: [
      {
        path: 'chat',
        name: 'Chat',
        component: () => import('@/views/ChatView.vue'),
        meta: { title: 'AI 智能问诊' },
      },
      {
        path: 'appointment',
        name: 'Appointment',
        component: () => import('@/views/AppointmentView.vue'),
        meta: { title: '预约挂号' },
      },
      {
        path: 'doctor',
        name: 'Doctor',
        component: () => import('@/views/DoctorView.vue'),
        meta: { title: '医生管理' },
      },
      {
        path: 'department',
        name: 'Department',
        component: () => import('@/views/DepartmentView.vue'),
        meta: { title: '科室管理' },
      },
      {
        path: 'patient',
        name: 'Patient',
        component: () => import('@/views/PatientView.vue'),
        meta: { title: '患者管理' },
      },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.afterEach((to) => {
  document.title = to.meta.title ? `${to.meta.title} · 可控医疗` : '可控医疗'
})

export default router
