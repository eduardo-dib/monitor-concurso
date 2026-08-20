import { defineStore } from 'pinia'
import { ref } from 'vue'
import api from '@/api/api'

export const useAuthStore = defineStore('auth', () => {
  const nome = ref<string | null>(null)

  function setUsuario(nomeUsuario: string) {
    nome.value = nomeUsuario
  }

  async function logout() {
    await api.post('/usuarios/logout')
    nome.value = null
  }

  return { nome, setUsuario, logout }
})
