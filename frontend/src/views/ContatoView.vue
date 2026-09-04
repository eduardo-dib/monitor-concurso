<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import axios from 'axios'
import { enviarContato } from '@/api/contato'
import type { ApiErrorResponse, CategoriaContato } from '@/types/api'

const CATEGORIAS: { value: CategoriaContato; label: string }[] = [
  { value: 'DUVIDA', label: 'Dúvida' },
  { value: 'SUGESTAO', label: 'Sugestão' },
  { value: 'RECLAMACAO', label: 'Reclamação' },
  { value: 'ELOGIO', label: 'Elogio' },
  { value: 'OUTRO', label: 'Outro' },
]

const MENSAGEM_MAX = 2000

const form = reactive({
  nome: '',
  email: '',
  categoria: 'OUTRO' as CategoriaContato,
  mensagem: '',
})

const enviando = ref(false)
const erro = ref('')
const sucesso = ref('')

const emailValido = computed(() => /^[^@\s]+@[^@\s]+\.[^@\s]+$/.test(form.email))
const mensagemRestante = computed(() => MENSAGEM_MAX - form.mensagem.length)

const formValido = computed(
  () =>
    form.nome.trim().length > 0 &&
    emailValido.value &&
    form.mensagem.trim().length > 0 &&
    form.mensagem.length <= MENSAGEM_MAX,
)

async function handleSubmit() {
  erro.value = ''
  sucesso.value = ''

  if (!form.nome.trim() || !form.email.trim() || !form.mensagem.trim()) {
    erro.value = 'Nome, e-mail e mensagem são obrigatórios'
    return
  }
  if (!emailValido.value) {
    erro.value = 'E-mail inválido'
    return
  }
  if (form.mensagem.length > MENSAGEM_MAX) {
    erro.value = `Mensagem muito longa (máximo ${MENSAGEM_MAX} caracteres)`
    return
  }

  enviando.value = true
  try {
    const resposta = await enviarContato({ ...form })
    sucesso.value = resposta || 'Mensagem enviada com sucesso! Retornaremos em breve.'
    form.nome = ''
    form.email = ''
    form.categoria = 'OUTRO'
    form.mensagem = ''
  } catch (e) {
    if (axios.isAxiosError<ApiErrorResponse>(e) && e.response?.data?.mensagem) {
      erro.value = e.response.data.mensagem
    } else {
      erro.value = 'Não foi possível enviar sua mensagem. Tente novamente.'
    }
  } finally {
    enviando.value = false
  }
}
</script>

<template>
  <div class="max-w-2xl mx-auto px-4 py-12">
    <div class="mb-8">
      <h1 class="text-2xl font-bold text-primary">Fale conosco</h1>
      <p class="text-gray-500 text-sm mt-1">
        Dúvidas, sugestões ou problemas? Envie uma mensagem para a nossa equipe.
      </p>
    </div>

    <div v-if="sucesso" class="bg-green-50 border border-green-200 rounded-2xl p-5 mb-6">
      <p class="text-sm text-green-700">{{ sucesso }}</p>
    </div>

    <form
      v-else
      @submit.prevent="handleSubmit"
      class="bg-surface rounded-2xl border border-gray-200 p-8 space-y-5"
    >
      <p v-if="erro" class="text-sm text-red-600">{{ erro }}</p>

      <div>
        <label for="nome" class="block text-sm font-medium text-primary mb-1">Nome</label>
        <input
          id="nome"
          v-model="form.nome"
          type="text"
          maxlength="200"
          class="w-full rounded-xl border border-gray-200 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-accent"
        />
      </div>

      <div>
        <label for="email" class="block text-sm font-medium text-primary mb-1">E-mail</label>
        <input
          id="email"
          v-model="form.email"
          type="email"
          class="w-full rounded-xl border border-gray-200 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-accent"
        />
      </div>

      <div>
        <label for="categoria" class="block text-sm font-medium text-primary mb-1">
          Categoria
        </label>
        <select
          id="categoria"
          v-model="form.categoria"
          class="w-full rounded-xl border border-gray-200 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-accent"
        >
          <option v-for="cat in CATEGORIAS" :key="cat.value" :value="cat.value">
            {{ cat.label }}
          </option>
        </select>
      </div>

      <div>
        <div class="flex items-center justify-between mb-1">
          <label for="mensagem" class="block text-sm font-medium text-primary">Mensagem</label>
          <span class="text-xs" :class="mensagemRestante < 0 ? 'text-red-600' : 'text-gray-400'">
            {{ form.mensagem.length }}/{{ MENSAGEM_MAX }}
          </span>
        </div>
        <textarea
          id="mensagem"
          v-model="form.mensagem"
          rows="6"
          class="w-full rounded-xl border border-gray-200 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-accent resize-none"
        />
      </div>

      <button
        type="submit"
        :disabled="enviando || !formValido"
        class="w-full bg-accent text-white text-sm font-medium rounded-xl py-2.5 disabled:opacity-50"
      >
        {{ enviando ? 'Enviando...' : 'Enviar mensagem' }}
      </button>
    </form>
  </div>
</template>
