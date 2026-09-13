<script setup lang="ts">
import { ref, computed } from 'vue'
import { listarMunicipios, type MunicipioOption } from '@/api/municipios'

const props = defineProps<{ modelValue: string }>()
const emit = defineEmits<{ 'update:modelValue': [value: string] }>()

const municipios = ref<MunicipioOption[]>([])
const carregando = ref(false)
const carregado = ref(false)
const erro = ref('')
const busca = ref('')
const aberto = ref(false)

function normalizar(texto: string) {
  return texto.normalize('NFD').replace(/[\u0300-\u036f]/g, '').toLowerCase()
}

const filtrados = computed(() => {
  if (!busca.value.trim()) return municipios.value.slice(0, 30)
  const termo = normalizar(busca.value)
  return municipios.value.filter((m) => normalizar(m.territory_name).includes(termo)).slice(0, 30)
})

const selecionado = computed(() =>
  municipios.value.find((m) => m.territory_id === props.modelValue),
)

async function carregarSeNecessario() {
  if (carregado.value || carregando.value) return
  carregando.value = true
  erro.value = ''
  try {
    municipios.value = await listarMunicipios()
    carregado.value = true
  } catch {
    erro.value = 'Não foi possível carregar a lista de municípios.'
  } finally {
    carregando.value = false
  }
}

function selecionar(m: MunicipioOption) {
  emit('update:modelValue', m.territory_id)
  busca.value = ''
  aberto.value = false
}

function limpar() {
  emit('update:modelValue', '')
}

function handleFocus() {
  aberto.value = true
  carregarSeNecessario()
}

function handleBlur() {

  window.setTimeout(() => {
    aberto.value = false
  }, 150)
}
</script>

<template>
  <div class="relative">
    <div
      v-if="selecionado"
      class="flex items-center justify-between rounded-lg border border-gray-300 px-3 py-2 text-sm"
    >
      <span>{{ selecionado.territory_name }} - {{ selecionado.state_code }}</span>
      <button type="button" @click="limpar" class="text-gray-400 hover:text-gray-600 ml-2">✕</button>
    </div>
    <input
      v-else
      v-model="busca"
      type="text"
      placeholder="Digite o nome do município..."
      class="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-accent"
      @focus="handleFocus"
      @blur="handleBlur"
    />

    <ul
      v-if="aberto && !selecionado"
      class="absolute z-10 mt-1 w-full max-h-60 overflow-auto rounded-lg border border-gray-200 bg-white shadow-lg text-sm"
    >
      <li v-if="carregando" class="px-3 py-2 text-gray-400">Carregando municípios...</li>
      <li v-else-if="erro" class="px-3 py-2 text-red-500">{{ erro }}</li>
      <li v-else-if="filtrados.length === 0" class="px-3 py-2 text-gray-400">
        Nenhum município encontrado na cobertura do Querido Diário.
      </li>
      <li
        v-for="m in filtrados"
        :key="m.territory_id"
        class="px-3 py-2 hover:bg-gray-100 cursor-pointer"
        @mousedown.prevent="selecionar(m)"
      >
        {{ m.territory_name }} - {{ m.state_code }}
      </li>
    </ul>
  </div>
</template>
