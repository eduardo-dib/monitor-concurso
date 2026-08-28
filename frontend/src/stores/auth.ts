import { defineStore } from 'pinia'
import { ref } from 'vue'
import api from '@/api/api'
import { buscarUsuarioLogado } from '@/api/usuarios'

export const useAuthStore = defineStore('auth', () => {
  const nome = ref<string | null>(null)
  const autenticado = ref(false)
  const sessaoVerificada = ref(false)

  function setUsuario(nomeUsuario: string) {
    nome.value = nomeUsuario
    autenticado.value = true
    sessaoVerificada.value = true
  }

  async function verificarSessao(): Promise<boolean> {
    if (sessaoVerificada.value) return autenticado.value

    try {
      const usuario = await buscarUsuarioLogado()
      nome.value = usuario.nome
      autenticado.value = true
    } catch {
      autenticado.value = false
    } finally {
      sessaoVerificada.value = true
    }

    return autenticado.value
  }

  async function logout() {
    await api.post('/usuarios/logout')
    nome.value = null
    autenticado.value = false
    sessaoVerificada.value = false
  }

  return { nome, autenticado, sessaoVerificada, setUsuario, verificarSessao, logout }
})
