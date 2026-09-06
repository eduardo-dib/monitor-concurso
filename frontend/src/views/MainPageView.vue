<script setup lang="ts">
import { ESTADOS } from '@/constants/EstadoOption.ts'

const estadosCobertos = ESTADOS.filter((e) => e.coberto).length
</script>

<template>
  <div>
    <section class="max-w-5xl mx-auto px-4 pt-16 pb-16 grid sm:grid-cols-2 gap-10 items-center">
      <div>
        <p class="text-accent font-semibold text-sm tracking-wide uppercase mb-3">
          Monitoramento de diários oficiais
        </p>
        <h1 class="text-3xl sm:text-4xl font-bold text-primary mb-4 leading-tight">
          Não perca mais nenhuma nomeação ou edital
        </h1>
        <p class="text-gray-600 mb-8">
          O VigiaConcursos acompanha os diários oficiais por você e avisa por e-mail assim que sair
          algo com as palavras-chave que você configurar. Sem precisar ficar checando site nenhum
          manualmente.
        </p>
        <div class="flex flex-col sm:flex-row gap-3">
          <RouterLink
            to="/cadastro"
            class="bg-accent text-white font-medium rounded-full px-6 py-3 text-center hover:opacity-90 transition-opacity"
          >
            Criar conta grátis
          </RouterLink>
          <RouterLink
            to="/login"
            class="text-primary font-medium rounded-full px-6 py-3 text-center border border-gray-300 hover:bg-gray-50 transition-colors"
          >
            Já tenho conta
          </RouterLink>
        </div>
      </div>

      <div class="bg-surface rounded-2xl border border-gray-200 shadow-sm p-5">
        <div class="flex items-center gap-2 mb-4 text-xs text-gray-400">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="w-4 h-4 text-accent"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
            stroke-width="2"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"
            />
          </svg>
          <span>Nova notificação por e-mail</span>
        </div>

        <p class="text-sm font-semibold text-primary mb-3">
          Encontramos 1 nova publicação para o seu alerta
          <span class="text-accent">"analista judiciário"</span>
        </p>

        <div class="flex gap-4 text-xs text-gray-400 mb-3">
          <span><span class="font-medium text-gray-500">Data:</span> 31/08/2026</span>
          <span><span class="font-medium text-gray-500">Território:</span> PR</span>
        </div>

        <p class="text-sm text-gray-600 leading-relaxed line-clamp-3">
          "...nomeação dos candidatos aprovados no concurso público para o cargo de Analista
          Judiciário, conforme Edital nº 12/2026, para posse em até 30 dias..."
        </p>

        <p class="text-xs text-accent font-medium mt-4">Ver publicação →</p>
      </div>
    </section>

    <section class="max-w-2xl mx-auto px-4 pb-16">
      <h2 class="text-xl font-bold text-primary text-center mb-10">Como funciona</h2>
      <div class="space-y-8">
        <div class="flex gap-4">
          <div
            class="w-8 h-8 rounded-full border-2 border-accent text-accent flex items-center justify-center font-bold text-sm shrink-0"
          >
            1
          </div>
          <div>
            <p class="font-medium text-primary mb-1">Crie seu alerta</p>
            <p class="text-sm text-gray-500">
              Defina palavras-chave e, se quiser, filtre por estado, município ou órgão.
            </p>
          </div>
        </div>
        <div class="flex gap-4">
          <div
            class="w-8 h-8 rounded-full border-2 border-accent text-accent flex items-center justify-center font-bold text-sm shrink-0"
          >
            2
          </div>
          <div>
            <p class="font-medium text-primary mb-1">Nós monitoramos</p>
            <p class="text-sm text-gray-500">
              Acompanhamos os diários oficiais automaticamente, todos os dias.
            </p>
          </div>
        </div>
        <div class="flex gap-4">
          <div
            class="w-8 h-8 rounded-full border-2 border-accent text-accent flex items-center justify-center font-bold text-sm shrink-0"
          >
            3
          </div>
          <div>
            <p class="font-medium text-primary mb-1">Você recebe o alerta</p>
            <p class="text-sm text-gray-500">
              Assim que sair uma publicação compatível, avisamos por e-mail.
            </p>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>
