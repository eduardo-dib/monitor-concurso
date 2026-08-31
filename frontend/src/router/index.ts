import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

declare module 'vue-router' {
  interface RouteMeta {
    requiresAuth?: boolean
    somenteVisitante?: boolean
  }
}

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/alertas',
    },
    {
      path: '/cadastro',
      name: 'cadastro',
      component: () => import('@/views/CadastroView.vue'),
      meta: { somenteVisitante: true },
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/LoginView.vue'),
      meta: { somenteVisitante: true },
    },
    {
      path: '/alertas',
      name: 'alertas',
      component: () => import('@/views/AlertasView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/alertas/novo',
      name: 'criar-alerta',
      component: () => import('@/views/CriarAlertaView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/privacidade',
      name: 'privacidade',
      component: () => import('@/views/PoliticaPrivacidadeView.vue'),
    },
    {
      path: '/termos',
      name: 'termos',
      component: () => import('@/views/TermosUsoView.vue'),
    },
  ],
})

router.beforeEach(async (to) => {
  const auth = useAuthStore()

  if (to.meta.requiresAuth) {
    const autenticado = await auth.verificarSessao()
    if (!autenticado) {
      return { name: 'login' }
    }
  }

  if (to.meta.somenteVisitante && auth.autenticado) {
    return { name: 'alertas' }
  }
})

export default router
