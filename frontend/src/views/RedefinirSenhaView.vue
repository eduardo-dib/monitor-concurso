<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'
import { redefinirSenha } from '@/api/usuarios'
import type { ApiErrorResponse } from '@/types/api'

const route = useRoute()

const token = computed(() => {
  const t = route.query.token
  return typeof t === 'string' ? t : ''
})

const novaSenha = ref('')
const confirmarSenha = ref('')
const carregando = ref(false)
const erro = ref('')
const sucesso = ref(false)

async function handleSubmit() {
  erro.value = ''

  if (novaSenha.value !== confirmarSenha.value) {
    erro.value = 'As senhas não coincidem.'
    return
  }

  carregando.value = true
  try {
    await redefinirSenha(token.value, novaSenha.value)
    sucesso.value = true
  } catch (e) {
    if (axios.isAxiosError<ApiErrorResponse>(e) && e.response?.data?.mensagem) {
      erro.value = e.response.data.mensagem
    } else {
      erro.value = 'Não foi possível redefinir sua senha. Tente novamente.'
    }
  } finally {
    carregando.value = false
  }
}
</script>

<template>
  <div class="flex justify-center items-center px-4 py-16">
    <div class="w-full max-w-md bg-surface rounded-2xl shadow-sm border border-gray-200 p-8">
      <template v-if="!token">
        <h1 class="text-2xl font-bold text-primary mb-1">Link inválido</h1>
        <p class="text-gray-500 text-sm">
          Este link de redefinição está incompleto ou é inválido. Solicite um novo em
          <RouterLink to="/esqueci-senha" class="text-accent hover:underline"
            >esqueci minha senha</RouterLink
          >.
        </p>
      </template>

      <template v-else-if="sucesso">
        <h1 class="text-2xl font-bold text-primary mb-1">Senha redefinida!</h1>
        <p class="text-gray-500 text-sm mb-6">
          Sua senha foi alterada com sucesso. Agora você já pode entrar com a nova senha.
        </p>
        <RouterLink
          to="/login"
          class="block text-center w-full bg-accent text-white font-medium rounded-full py-2.5 hover:opacity-90 transition-opacity"
        >
          Ir para o login
        </RouterLink>
      </template>

      <template v-else>
        <h1 class="text-2xl font-bold text-primary mb-1">Redefinir senha</h1>
        <p class="text-gray-500 text-sm mb-6">Escolha uma nova senha para sua conta.</p>

        <form @submit.prevent="handleSubmit" class="space-y-4">
          <div>
            <label for="novaSenha" class="block text-sm font-medium text-primary mb-1"
              >Nova senha</label
            >
            <input
              id="novaSenha"
              v-model="novaSenha"
              type="password"
              required
              minlength="6"
              class="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-accent"
            />
          </div>

          <div>
            <label for="confirmarSenha" class="block text-sm font-medium text-primary mb-1"
              >Confirmar nova senha</label
            >
            <input
              id="confirmarSenha"
              v-model="confirmarSenha"
              type="password"
              required
              minlength="6"
              class="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-accent"
            />
          </div>

          <p v-if="erro" class="text-sm text-red-600">{{ erro }}</p>

          <button
            type="submit"
            :disabled="carregando"
            class="w-full bg-accent text-white font-medium rounded-full py-2.5 hover:opacity-90 transition-opacity disabled:opacity-60"
          >
            {{ carregando ? 'Redefinindo...' : 'Redefinir senha' }}
          </button>
        </form>
      </template>
    </div>
  </div>
</template>
