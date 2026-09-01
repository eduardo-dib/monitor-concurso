<script setup lang="ts">
import { RouterLink, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const router = useRouter()

async function handleLogout() {
  await auth.logout()
  router.push('/login')
}
</script>

<template>
  <header class="bg-surface border-b border-gray-200">
    <div class="max-w-5xl mx-auto px-6 h-16 flex items-center justify-between">
      <RouterLink to="/" class="flex items-center gap-2 text-primary font-bold text-lg">
        <svg
          xmlns="http://www.w3.org/2000/svg"
          class="w-6 h-6 text-accent"
          fill="none"
          viewBox="0 0 24 24"
          stroke="currentColor"
          stroke-width="2"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"
          />
        </svg>
        VigiaConcursos
      </RouterLink>

      <nav class="flex items-center gap-4 text-sm">
        <template v-if="auth.autenticado">
          <RouterLink to="/alertas" class="text-primary hover:text-accent transition-colors"
            >Meus alertas</RouterLink
          >
          <RouterLink to="/conta" class="text-primary hover:text-accent transition-colors"
            >Minha conta</RouterLink
          >
          <button @click="handleLogout" class="text-primary hover:text-accent transition-colors">
            Sair
          </button>
        </template>
        <template v-else>
          <RouterLink to="/login" class="text-primary hover:text-accent transition-colors"
            >Entrar</RouterLink
          >
          <RouterLink
            to="/cadastro"
            class="bg-accent text-white px-4 py-2 rounded-full font-medium hover:opacity-90 transition-opacity"
          >
            Cadastrar
          </RouterLink>
        </template>
      </nav>
    </div>
  </header>
</template>
