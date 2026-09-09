<script setup lang="ts">
import { ref } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const router = useRouter()
const menuAberto = ref(false)

async function handleLogout() {
  menuAberto.value = false
  await auth.logout()
  router.push('/login')
}

function fecharMenu() {
  menuAberto.value = false
}
</script>

<template>
  <header class="bg-surface border-b border-gray-200">
    <div class="max-w-5xl mx-auto px-4 sm:px-6 h-16 flex items-center justify-between">
      <RouterLink
        to="/"
        class="flex items-center gap-2 text-primary font-bold text-lg shrink-0"
        @click="fecharMenu"
      >
        <svg
          xmlns="http://www.w3.org/2000/svg"
          class="w-6 h-6 text-accent shrink-0"
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

      <!-- Nav desktop -->
      <nav class="hidden sm:flex items-center gap-4 text-sm">
        <template v-if="auth.autenticado">
          <RouterLink to="/alertas" class="text-primary hover:text-accent transition-colors"
            >Meus alertas</RouterLink
          >
          <RouterLink to="/avisos" class="text-primary hover:text-accent transition-colors"
            >Avisos</RouterLink
          >
          <RouterLink to="/conta" class="text-primary hover:text-accent transition-colors"
            >Minha conta</RouterLink
          >
          <button @click="handleLogout" class="text-primary hover:text-accent transition-colors">
            Sair
          </button>
        </template>
        <template v-else>
          <RouterLink to="/avisos" class="text-primary hover:text-accent transition-colors"
            >Avisos</RouterLink
          >
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

      <!-- Botão hamburguer (só aparece em telas pequenas) -->
      <button
        class="sm:hidden p-2 text-primary"
        @click="menuAberto = !menuAberto"
        :aria-expanded="menuAberto"
        aria-label="Abrir menu"
      >
        <svg
          v-if="!menuAberto"
          xmlns="http://www.w3.org/2000/svg"
          class="w-6 h-6"
          fill="none"
          viewBox="0 0 24 24"
          stroke="currentColor"
          stroke-width="2"
        >
          <path stroke-linecap="round" stroke-linejoin="round" d="M4 6h16M4 12h16M4 18h16" />
        </svg>
        <svg
          v-else
          xmlns="http://www.w3.org/2000/svg"
          class="w-6 h-6"
          fill="none"
          viewBox="0 0 24 24"
          stroke="currentColor"
          stroke-width="2"
        >
          <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
        </svg>
      </button>
    </div>

    <!-- Menu mobile expandido -->
    <nav
      v-if="menuAberto"
      class="sm:hidden border-t border-gray-200 px-4 py-3 flex flex-col gap-3 text-sm bg-surface"
    >
      <template v-if="auth.autenticado">
        <RouterLink
          to="/alertas"
          class="text-primary hover:text-accent transition-colors"
          @click="fecharMenu"
          >Meus alertas</RouterLink
        >
        <RouterLink
          to="/avisos"
          class="text-primary hover:text-accent transition-colors"
          @click="fecharMenu"
          >Avisos</RouterLink
        >
        <RouterLink
          to="/conta"
          class="text-primary hover:text-accent transition-colors"
          @click="fecharMenu"
          >Minha conta</RouterLink
        >
        <button
          @click="handleLogout"
          class="text-left text-primary hover:text-accent transition-colors"
        >
          Sair
        </button>
      </template>
      <template v-else>
        <RouterLink
          to="/avisos"
          class="text-primary hover:text-accent transition-colors"
          @click="fecharMenu"
          >Avisos</RouterLink
        >
        <RouterLink
          to="/login"
          class="text-primary hover:text-accent transition-colors"
          @click="fecharMenu"
          >Entrar</RouterLink
        >
        <RouterLink
          to="/cadastro"
          class="bg-accent text-white px-4 py-2 rounded-full font-medium hover:opacity-90 transition-opacity text-center"
          @click="fecharMenu"
        >
          Cadastrar
        </RouterLink>
      </template>
    </nav>
  </header>
</template>
