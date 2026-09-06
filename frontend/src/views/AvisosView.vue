<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { listarAvisos } from '@/api/avisos'
import type { AvisoResponse, TipoAviso } from '@/types/api'

const avisos = ref<AvisoResponse[]>([])
const carregando = ref(true)
const erro = ref('')

const estilosPorTipo: Record<TipoAviso, { badge: string; borda: string; label: string }> = {
  INFO: {
    badge: 'bg-blue-100 text-blue-800',
    borda: 'border-blue-400',
    label: 'Informação',
  },
  ALERTA: {
    badge: 'bg-amber-100 text-amber-800',
    borda: 'border-amber-400',
    label: 'Alerta',
  },
  MANUTENCAO: {
    badge: 'bg-violet-100 text-violet-800',
    borda: 'border-violet-400',
    label: 'Manutenção',
  },
}

function formatarData(iso: string): string {
  return new Intl.DateTimeFormat('pt-BR', {
    day: '2-digit',
    month: 'long',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  }).format(new Date(iso))
}

onMounted(async () => {
  try {
    avisos.value = await listarAvisos()
  } catch {
    erro.value = 'Não foi possível carregar os avisos no momento.'
  } finally {
    carregando.value = false
  }
})
</script>

<template>
  <div class="max-w-2xl mx-auto px-4 py-12">
    <h1 class="text-2xl font-bold text-primary mb-6">Avisos</h1>

    <p v-if="carregando" class="text-sm text-gray-500">Carregando avisos...</p>

    <p v-else-if="erro" class="text-sm text-red-600">{{ erro }}</p>

    <p v-else-if="avisos.length === 0" class="text-sm text-gray-500">Nenhum aviso no momento.</p>

    <ul v-else class="space-y-4">
      <li
        v-for="aviso in avisos"
        :key="aviso.id"
        :class="[
          'bg-surface rounded-xl shadow-sm border-l-4 p-5',
          estilosPorTipo[aviso.tipo].borda,
        ]"
      >
        <div class="flex items-center justify-between mb-2">
          <span
            :class="[
              'text-xs font-semibold px-2 py-0.5 rounded-full',
              estilosPorTipo[aviso.tipo].badge,
            ]"
          >
            {{ estilosPorTipo[aviso.tipo].label }}
          </span>
          <span class="text-xs text-gray-400">{{ formatarData(aviso.criadoEm) }}</span>
        </div>
        <h2 class="text-base font-semibold text-primary mb-1">{{ aviso.titulo }}</h2>
        <p class="text-sm text-gray-600">{{ aviso.mensagem }}</p>
      </li>
    </ul>
  </div>
</template>
