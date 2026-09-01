<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { useAuthStore } from '@/stores/auth'
import { excluirConta } from '@/api/usuarios'
import type { ApiErrorResponse } from '@/types/api'

const auth = useAuthStore()
const router = useRouter()

const mostrarConfirmacao = ref(false)
const senha = ref('')
const textoConfirmacao = ref('')
const carregando = ref(false)
const erro = ref('')

async function handleExcluir() {
  erro.value = ''

  if (textoConfirmacao.value !== 'EXCLUIR') {
    erro.value = 'Digite EXCLUIR para confirmar.'
    return
  }

  carregando.value = true
  try {
    await excluirConta(senha.value)
    auth.limparSessaoLocal()
    router.push('/')
  } catch (e) {
    if (axios.isAxiosError<ApiErrorResponse>(e) && e.response?.data?.mensagem) {
      erro.value = e.response.data.mensagem
    } else {
      erro.value = 'Não foi possível excluir sua conta. Tente novamente.'
    }
  } finally {
    carregando.value = false
  }
}
</script>

<template>
  <div class="max-w-2xl mx-auto px-4 py-12 space-y-6">
    <div>
      <h1 class="text-2xl font-bold text-primary">Minha conta</h1>
      <p class="text-gray-500 text-sm mt-1">Seus dados de cadastro.</p>
    </div>

    <div class="bg-surface rounded-2xl border border-gray-200 p-6 space-y-4">
      <div>
        <p class="text-xs text-gray-400">Nome</p>
        <p class="text-primary font-medium">{{ auth.nome }}</p>
      </div>
      <p class="text-xs text-gray-400">
        A edição de dados de perfil (nome, e-mail, senha) estará disponível em breve.
      </p>
    </div>

    <div class="bg-surface rounded-2xl border border-red-200 p-6">
      <h2 class="font-semibold text-red-600 mb-1">Excluir conta</h2>
      <p class="text-sm text-gray-500 mb-4">
        Isso vai apagar permanentemente sua conta e todos os seus alertas. Essa ação não pode ser
        desfeita.
      </p>

      <button
        v-if="!mostrarConfirmacao"
        @click="mostrarConfirmacao = true"
        class="text-sm text-red-600 border border-red-300 rounded-full px-4 py-2 hover:bg-red-50 transition-colors"
      >
        Quero excluir minha conta
      </button>

      <form v-else @submit.prevent="handleExcluir" class="space-y-4">
        <div>
          <label for="senha" class="block text-sm font-medium text-primary mb-1">
            Confirme sua senha
          </label>
          <input
            id="senha"
            v-model="senha"
            type="password"
            required
            class="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-red-400"
          />
        </div>

        <div>
          <label for="confirmacao" class="block text-sm font-medium text-primary mb-1">
            Digite <span class="font-mono font-semibold">EXCLUIR</span> para confirmar
          </label>
          <input
            id="confirmacao"
            v-model="textoConfirmacao"
            type="text"
            required
            class="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-red-400"
          />
        </div>

        <p v-if="erro" class="text-sm text-red-600">{{ erro }}</p>

        <div class="flex gap-3">
          <button
            type="button"
            @click="mostrarConfirmacao = false"
            class="flex-1 rounded-full py-2.5 text-sm font-medium text-primary border border-gray-300 hover:bg-gray-50 transition-colors"
          >
            Cancelar
          </button>
          <button
            type="submit"
            :disabled="carregando"
            class="flex-1 bg-red-600 text-white font-medium rounded-full py-2.5 text-sm hover:opacity-90 transition-opacity disabled:opacity-60"
          >
            {{ carregando ? 'Excluindo...' : 'Excluir permanentemente' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
