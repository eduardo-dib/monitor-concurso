<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { cadastrarUsuario } from '@/api/usuarios'
import type { ApiErrorResponse } from '@/types/api'

const router = useRouter()

const nome = ref('')
const email = ref('')
const senha = ref('')
const confirmarSenha = ref('')

const carregando = ref(false)
const erro = ref('')

async function handleSubmit() {
  erro.value = ''

  if (senha.value !== confirmarSenha.value) {
    erro.value = 'As senhas não coincidem.'
    return
  }

  carregando.value = true
  try {
    await cadastrarUsuario({ nome: nome.value, email: email.value, senha: senha.value })
    router.push('/login')
  } catch (e) {
    if (axios.isAxiosError<ApiErrorResponse>(e) && e.response?.data?.mensagem) {
      erro.value = e.response.data.mensagem
    } else {
      erro.value = 'Não foi possível concluir o cadastro. Tente novamente.'
    }
  } finally {
    carregando.value = false
  }
}
</script>

<template>
  <div class="flex justify-center items-center px-4 py-16">
    <div class="w-full max-w-md bg-surface rounded-2xl shadow-sm border border-gray-200 p-8">
      <h1 class="text-2xl font-bold text-primary mb-1">Criar conta</h1>
      <p class="text-gray-500 text-sm mb-6">
        Cadastre-se para começar a receber alertas de concursos.
      </p>

      <form @submit.prevent="handleSubmit" class="space-y-4">
        <div>
          <label for="nome" class="block text-sm font-medium text-primary mb-1">Nome</label>
          <input
            id="nome"
            v-model="nome"
            type="text"
            required
            class="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-accent"
          />
        </div>

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
            minlength="6"
            class="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-accent"
          />
        </div>

        <div>
          <label for="confirmarSenha" class="block text-sm font-medium text-primary mb-1"
            >Confirmar senha</label
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
          {{ carregando ? 'Criando conta...' : 'Criar conta' }}
        </button>
      </form>

      <p class="text-sm text-gray-500 mt-6 text-center">
        Já tem conta?
        <RouterLink to="/login" class="text-accent font-medium hover:underline">Entrar</RouterLink>
      </p>
    </div>
  </div>
</template>
