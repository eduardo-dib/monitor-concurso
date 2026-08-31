<script setup lang="ts">
import { ref } from 'vue'
import { solicitarRecuperacaoSenha } from '@/api/usuarios'

const email = ref('')
const carregando = ref(false)
const enviado = ref(false)
const erro = ref('')

async function handleSubmit() {
  erro.value = ''
  carregando.value = true
  try {
    await solicitarRecuperacaoSenha(email.value)
    enviado.value = true
  } catch {
    erro.value = 'Não foi possível processar sua solicitação. Tente novamente em instantes.'
  } finally {
    carregando.value = false
  }
}
</script>

<template>
  <div class="flex justify-center items-center px-4 py-16">
    <div class="w-full max-w-md bg-surface rounded-2xl shadow-sm border border-gray-200 p-8">
      <template v-if="enviado">
        <h1 class="text-2xl font-bold text-primary mb-1">Verifique seu e-mail</h1>
        <p class="text-gray-500 text-sm mb-2">
          Se o e-mail informado estiver cadastrado, você vai receber um link para redefinir sua
          senha em instantes.
        </p>
        <p class="text-gray-400 text-xs">
          Se pedir um novo link mais tarde, o link anterior deixa de funcionar.
        </p>
      </template>

      <template v-else>
        <h1 class="text-2xl font-bold text-primary mb-1">Esqueci minha senha</h1>
        <p class="text-gray-500 text-sm mb-6">
          Informe seu e-mail e enviaremos um link para redefinir sua senha.
        </p>

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

          <p v-if="erro" class="text-sm text-red-600">{{ erro }}</p>

          <button
            type="submit"
            :disabled="carregando"
            class="w-full bg-accent text-white font-medium rounded-full py-2.5 hover:opacity-90 transition-opacity disabled:opacity-60"
          >
            {{ carregando ? 'Enviando...' : 'Enviar link de recuperação' }}
          </button>
        </form>
      </template>

      <p class="text-sm text-gray-500 mt-6 text-center">
        <RouterLink to="/login" class="text-accent font-medium hover:underline"
          >Voltar para o login</RouterLink
        >
      </p>
    </div>
  </div>
</template>
