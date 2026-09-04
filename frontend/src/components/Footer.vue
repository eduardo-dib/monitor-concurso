<script setup lang="ts"></script>
<template>
  <footer class="bg-surface border-t border-gray-200 mt-auto">
    <div
      class="max-w-5xl mx-auto px-6 py-6 flex flex-col sm:flex-row items-center justify-between gap-3 text-sm text-gray-500"
    >
      <span>VigiaConcursos</span>
      <div class="flex gap-4">
        <RouterLink to="/contato" class="hover:text-accent transition-colors"
          >Contato</RouterLink
        >

        <RouterLink to="/termos" class="hover:text-accent transition-colors"
          >Termos de Uso</RouterLink
        >
        <RouterLink to="/privacidade" class="hover:text-accent transition-colors"
          >Política de Privacidade</RouterLink
        >
      </div>
    </div>
  </footer>
</template>
