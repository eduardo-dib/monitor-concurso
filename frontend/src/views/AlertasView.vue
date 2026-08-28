<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { useAuthStore } from '@/stores/auth'
import { listarAlertas, deletarAlerta } from '@/api/alertas'
import type { AlertaResponse, ApiErrorResponse } from '@/types/api'

const auth = useAuthStore()
const router = useRouter()

const alertas = ref<AlertaResponse[]>([])
const carregando = ref(true)
const erro = ref('')
const deletandoId = ref<number | null>(null)

async function carregarAlertas() {
  carregando.value = true
  erro.value = ''
  try {
    alertas.value = await listarAlertas()
  } catch {
    erro.value = 'Não foi possível carregar seus alertas. Tente novamente.'
  } finally {
    carregando.value = false
  }
}

async function handleDeletar(id: number) {
  if (!confirm('Tem certeza que deseja excluir este alerta?')) return

  deletandoId.value = id
  try {
    await deletarAlerta(id)
    alertas.value = alertas.value.filter((a) => a.id !== id)
  } catch (e) {
    if (axios.isAxiosError<ApiErrorResponse>(e) && e.response?.data?.mensagem) {
      erro.value = e.response.data.mensagem
    } else {
      erro.value = 'Não foi possível excluir o alerta. Tente novamente.'
    }
  } finally {
    deletandoId.value = null
  }
}

async function handleLogout() {
  await auth.logout()
  router.push('/login')
}

onMounted(carregarAlertas)
</script>

<template>
  <div class="max-w-2xl mx-auto px-4 py-12">
    <div class="flex items-center justify-between mb-8">
      <div>
        <h1 class="text-2xl font-bold text-primary">Bem-vindo, {{ auth.nome }}!</h1>
        <p class="text-gray-500 text-sm mt-1">Seus alertas de concursos</p>
      </div>
      <div class="flex items-center gap-4 shrink-0">
        <RouterLink
          to="/alertas/novo"
          class="bg-accent text-white text-sm font-medium px-4 py-2 rounded-full hover:opacity-90 transition-opacity"
        >
          Novo alerta
        </RouterLink>
        <button @click="handleLogout" class="text-sm text-accent hover:underline">Sair</button>
      </div>
    </div>

    <p v-if="erro" class="text-sm text-red-600 mb-4">{{ erro }}</p>

    <div v-if="carregando" class="text-gray-500 text-sm">Carregando seus alertas...</div>

    <div
      v-else-if="alertas.length === 0"
      class="bg-surface rounded-2xl border border-gray-200 p-8 text-center"
    >
      <p class="text-gray-500">Você ainda não tem nenhum alerta cadastrado.</p>
    </div>

    <ul v-else class="space-y-3">
      <li
        v-for="alerta in alertas"
        :key="alerta.id"
        class="bg-surface rounded-2xl border border-gray-200 p-5 flex items-start justify-between gap-4"
      >
        <div>
          <p class="font-medium text-primary">{{ alerta.palavrasChave }}</p>
          <p class="text-sm text-gray-500 mt-1">
            {{ [alerta.orgao, alerta.municipio, alerta.estado].filter(Boolean).join(' · ') }}
          </p>
          <span
            class="inline-block mt-2 text-xs px-2 py-0.5 rounded-full"
            :class="alerta.ativo ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-500'"
          >
            {{ alerta.ativo ? 'Ativo' : 'Inativo' }}
          </span>
        </div>
        <button
          @click="handleDeletar(alerta.id)"
          :disabled="deletandoId === alerta.id"
          class="text-sm text-red-600 hover:underline shrink-0 disabled:opacity-50"
        >
          {{ deletandoId === alerta.id ? 'Excluindo...' : 'Excluir' }}
        </button>
      </li>
    </ul>
  </div>
</template>
