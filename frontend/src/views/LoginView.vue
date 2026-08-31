<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { login } from '@/api/usuarios'
import { useAuthStore } from '@/stores/auth'
import type { ApiErrorResponse } from '@/types/api'

const router = useRouter()
const auth = useAuthStore()

const email = ref('')
const senha = ref('')
const carregando = ref(false)
const erro = ref('')

async function handleSubmit() {
  erro.value = ''
  carregando.value = true
  try {
    const resposta = await login({ email: email.value, senha: senha.value })
    auth.setUsuario(resposta.nome)
    router.push('/alertas')
  } catch (e) {
    if (axios.isAxiosError<ApiErrorResponse>(e) && e.response?.data?.mensagem) {
      erro.value = e.response.data.mensagem
    } else {
      erro.value = 'Não foi possível entrar. Tente novamente.'
    }
  } finally {
    carregando.value = false
  }
}
</script>

<template>
  <div class="flex justify-center items-center px-4 py-16">
    <div class="w-full max-w-md bg-surface rounded-2xl shadow-sm border border-gray-200 p-8">
      <h1 class="text-2xl font-bold text-primary mb-1">Entrar</h1>
      <p class="text-gray-500 text-sm mb-6">Acesse sua conta para gerenciar seus alertas.</p>

      <form @submit.prevent="handleSubmit" class="space-y-4">
        <div>
          <label for="email" class="block text-sm font-medium text-primary mb-1">E-mail</label>
          <input
            id="email"
            v-model="email"
            type="email"
            required
            class="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-accent"
          />
        </div>


        <div>
          <label for="senha" class="block text-sm font-medium text-primary mb-1">Senha</label>
          <input
            id="senha"
            v-model="senha"
            type="password"
            required
            class="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-accent"
          />
          <RouterLink
            to="/esqueci-senha"
            class="text-xs text-accent hover:underline block mt-1 text-right"
          >
            Esqueci minha senha
          </RouterLink>
        </div>

        <p v-if="erro" class="text-sm text-red-600">{{ erro }}</p>

        <button
          type="submit"
          :disabled="carregando"
          class="w-full bg-accent text-white font-medium rounded-full py-2.5 hover:opacity-90 transition-opacity disabled:opacity-60"
        >
          {{ carregando ? 'Entrando...' : 'Entrar' }}
        </button>
      </form>

      <p class="text-sm text-gray-500 mt-6 text-center">
        Ainda não tem conta?
        <RouterLink to="/cadastro" class="text-accent font-medium hover:underline"
          >Cadastre-se</RouterLink
        >
      </p>
    </div>
  </div>
</template>
