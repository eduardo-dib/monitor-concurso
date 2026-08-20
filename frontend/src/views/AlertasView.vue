<script setup lang="ts">
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'

const auth = useAuthStore()
const router = useRouter()

async function handleLogout() {
  await auth.logout()
  router.push('/login')
}
</script>

<template>
  <div class="max-w-2xl mx-auto px-4 py-16">
    <h1 class="text-2xl font-bold text-primary">Bem-vindo, {{ auth.nome }}!</h1>
    <p class="text-gray-500 mt-2">A listagem de alertas vem no próximo passo.</p>
    <button @click="handleLogout" class="mt-6 text-sm text-accent hover:underline">Sair</button>
  </div>
</template>
