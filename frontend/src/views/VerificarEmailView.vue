<script setup lang="ts">
import { ref, onUnmounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { AxiosError } from 'axios'
import { verificarEmail, reenviarCodigo } from '@/api/usuarios'
import type { ApiErrorResponse } from '@/types/api'

const route = useRoute()
const router = useRouter()

const email = computed(() => String(route.query.email ?? ''))

const codigo = ref('')
const carregando = ref(false)
const reenviando = ref(false)
const erro = ref('')
const mensagemReenvio = ref('')
const cooldown = ref(0)

let intervalo: ReturnType<typeof setInterval> | undefined

function onInputCodigo(event: Event) {
  const valor = (event.target as HTMLInputElement).value
  codigo.value = valor.replace(/\D/g, '').slice(0, 6)
}

function extrairMensagemErro(err: unknown, fallback: string): string {
  if (err instanceof AxiosError && err.response?.data) {
    const apiError = err.response.data as ApiErrorResponse
    return apiError.mensagem ?? fallback
  }
  return fallback
}

function iniciarCooldown(segundos: number) {
  cooldown.value = segundos
  intervalo = setInterval(() => {
    cooldown.value -= 1
    if (cooldown.value <= 0 && intervalo) {
      clearInterval(intervalo)
    }
  }, 1000)
}

async function onSubmit() {
  if (codigo.value.length !== 6) {
    erro.value = 'Digite o código de 6 dígitos.'
    return
  }

  erro.value = ''
  carregando.value = true

  try {
    await verificarEmail({ email: email.value, codigo: codigo.value })
    router.push({ path: '/login', query: { verificado: '1' } })
  } catch (err) {
    erro.value = extrairMensagemErro(err, 'Código inválido ou expirado.')
  } finally {
    carregando.value = false
  }
}

async function onReenviar() {
  if (cooldown.value > 0) return

  erro.value = ''
  mensagemReenvio.value = ''
  reenviando.value = true

  try {
    const resposta = await reenviarCodigo({ email: email.value })
    mensagemReenvio.value = resposta
    iniciarCooldown(60)
  } catch (err) {
    erro.value = extrairMensagemErro(err, 'Não foi possível reenviar o código.')
  } finally {
    reenviando.value = false
  }
}

onUnmounted(() => {
  if (intervalo) clearInterval(intervalo)
})
</script>

<template>
  <div class="min-h-[70vh] flex items-center justify-center bg-background px-4">
    <div class="w-full max-w-md bg-surface rounded-xl shadow-md p-8">
      <h1 class="text-2xl font-bold text-primary mb-2 text-center">Verifique seu e-mail</h1>
      <p class="text-sm text-gray-600 text-center mb-6">
        Enviamos um código de 6 dígitos para
        <span class="font-medium text-primary">{{ email }}</span
        >. Ele expira em 15 minutos.
      </p>

      <form @submit.prevent="onSubmit" class="space-y-4">
        <input
          :value="codigo"
          @input="onInputCodigo"
          type="text"
          inputmode="numeric"
          autocomplete="one-time-code"
          maxlength="6"
          placeholder="000000"
          class="w-full text-center text-2xl tracking-[0.5em] font-mono border border-gray-300 rounded-lg py-3 focus:outline-none focus:ring-2 focus:ring-accent"
        />

        <p v-if="erro" class="text-sm text-red-600 text-center">{{ erro }}</p>
        <p v-if="mensagemReenvio" class="text-sm text-green-600 text-center">
          {{ mensagemReenvio }}
        </p>

        <button
          type="submit"
          :disabled="carregando"
          class="w-full bg-primary text-white rounded-lg py-2.5 font-medium hover:opacity-90 transition disabled:opacity-50"
        >
          {{ carregando ? 'Verificando...' : 'Verificar e-mail' }}
        </button>
      </form>

      <div class="text-center mt-4">
        <button
          @click="onReenviar"
          :disabled="reenviando || cooldown > 0"
          class="text-sm text-accent font-medium hover:underline disabled:text-gray-400 disabled:no-underline"
        >
          {{ cooldown > 0 ? `Reenviar código em ${cooldown}s` : 'Reenviar código' }}
        </button>
      </div>
    </div>
  </div>
</template>
