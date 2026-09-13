<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { criarAlerta } from '@/api/alertas'
import { ESTADOS } from '../constants/EstadoOption.ts'
import type { ApiErrorResponse, FonteMonitoramento } from '@/types/api'
import MunicipioAutocomplete from '../components/MunicipioAutoComplete.vue'

const router = useRouter()

const palavrasChave = ref('')
const estado = ref('')
const municipio = ref('')
const fonte = ref<FonteMonitoramento>('ESTADUAL')

const carregando = ref(false)
const erro = ref('')

const estadoSelecionado = computed(() => ESTADOS.find((e) => e.sigla === estado.value))
const mostrarMunicipio = computed(() => fonte.value === 'MUNICIPAL' || fonte.value === 'TODOS')
const mostrarEstado = computed(() => fonte.value === 'ESTADUAL' || fonte.value === 'TODOS')
const mostrarAvisoFederal = computed(() => fonte.value === 'FEDERAL')

async function handleSubmit() {
  erro.value = ''
  carregando.value = true
  try {
    await criarAlerta({
      palavrasChave: palavrasChave.value,
      estado: mostrarEstado.value ? estado.value : '',
      municipio: mostrarMunicipio.value ? municipio.value : '',
      orgao: '',
      fonte: fonte.value,
    })
    router.push('/alertas')
  } catch (e) {
    if (axios.isAxiosError<ApiErrorResponse>(e) && e.response?.data?.mensagem) {
      erro.value = e.response.data.mensagem
    } else {
      erro.value = 'Não foi possível criar o alerta. Tente novamente.'
    }
  } finally {
    carregando.value = false
  }
}
</script>

<template>
  <div class="max-w-md mx-auto px-4 py-12">
    <div class="bg-surface rounded-2xl shadow-sm border border-gray-200 p-8">
      <h1 class="text-2xl font-bold text-primary mb-1">Novo alerta</h1>
      <p class="text-gray-500 text-sm mb-6">
        Configure as palavras-chave e filtros para monitorar.
      </p>

      <form @submit.prevent="handleSubmit" class="space-y-4">
        <div>
          <label for="palavrasChave" class="block text-sm font-medium text-primary mb-1"
            >Palavras-chave</label
          >
          <input
            id="palavrasChave"
            v-model="palavrasChave"
            type="text"
            required
            placeholder="ex: analista, técnico judiciário"
            class="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-accent"
          />
        </div>

        <div>
          <label for="fonte" class="block text-sm font-medium text-primary mb-1">Fonte</label>
          <select
            id="fonte"
            v-model="fonte"
            class="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-accent"
          >
            <option value="ESTADUAL">Estadual</option>
            <option value="MUNICIPAL">Municipal</option>
            <option value="FEDERAL">Federal (Diário Oficial da União)</option>
          </select>
        </div>

        <p v-if="mostrarAvisoFederal" class="text-xs text-gray-400 -mt-2">
          Busca em todo o Diário Oficial da União, sem filtro de estado ou município.
        </p>

        <div v-if="mostrarMunicipio">
          <label class="block text-sm font-medium text-primary mb-1">Município</label>
          <MunicipioAutocomplete v-model="municipio" />
          <p class="text-xs text-gray-400 mt-1"></p>
        </div>

        <div v-if="mostrarEstado">
          <label for="estado" class="block text-sm font-medium text-primary mb-1">Estado</label>
          <select
            id="estado"
            v-model="estado"
            class="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-accent"
          >
            <option value="">Todos os estados</option>
            <option v-for="e in ESTADOS" :key="e.sigla" :value="e.sigla">
              {{ e.nome }}{{ e.coberto ? '' : ' (sem integração ainda)' }}
            </option>
          </select>
          <p
            v-if="estadoSelecionado && !estadoSelecionado.coberto"
            class="text-xs text-amber-600 mt-1"
          >
            Este estado ainda não tem integração ativa, o alerta será salvo, mas não vai gerar
            notificações até a cobertura ser adicionada.
          </p>
        </div>

        <p v-if="erro" class="text-sm text-red-600">{{ erro }}</p>

        <div class="flex gap-3 pt-2">
          <RouterLink
            to="/alertas"
            class="flex-1 text-center rounded-full py-2.5 text-sm font-medium text-primary border border-gray-300 hover:bg-gray-50 transition-colors"
          >
            Cancelar
          </RouterLink>
          <button
            type="submit"
            :disabled="carregando"
            class="flex-1 bg-accent text-white font-medium rounded-full py-2.5 text-sm hover:opacity-90 transition-opacity disabled:opacity-60"
          >
            {{ carregando ? 'Criando...' : 'Criar alerta' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
