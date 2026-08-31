<script setup lang="ts"></script>

<template>
  <div class="max-w-3xl mx-auto px-4 py-12">
    <div class="bg-surface rounded-2xl border border-gray-200 p-8 sm:p-10">
      <h1 class="text-2xl font-bold text-primary mb-1">Termos de Uso</h1>
      <p class="text-sm text-gray-500 mb-8">Última atualização: agosto de 2026</p>

      <div class="space-y-8 text-sm text-gray-700 leading-relaxed">
        <section>
          <h2 class="text-base font-semibold text-primary mb-2">1. Aceitação dos termos</h2>
          <p>
            Ao criar uma conta ou usar o VigiaConcursos, você concorda com estes Termos de Uso e com
            nossa
            <RouterLink to="/privacidade" class="text-accent hover:underline"
              >Política de Privacidade</RouterLink
            >. Se não concordar com algum ponto, pedimos que não utilize o serviço.
          </p>
        </section>

        <section>
          <h2 class="text-base font-semibold text-primary mb-2">2. O que é o VigiaConcursos</h2>
          <p>
            O VigiaConcursos é um serviço gratuito que monitora diários oficiais estaduais e
            municipais e envia alertas por e-mail quando encontra publicações que correspondem às
            palavras-chave e filtros configurados por você. O serviço depende da disponibilidade e
            do funcionamento dos sites públicos de terceiros (diários oficiais), sobre os quais não
            temos controle.
          </p>
        </section>

        <section>
          <h2 class="text-base font-semibold text-primary mb-2">3. Cadastro e conta</h2>
          <ul class="list-disc pl-5 space-y-1">
            <li>
              Você deve fornecer um e-mail válido e mantê-lo atualizado, já que é o único canal de
              notificação.
            </li>
            <li>
              Você é responsável por manter sua senha em sigilo e por qualquer atividade realizada
              na sua conta.
            </li>
            <li>
              Você pode solicitar a exclusão da sua conta a qualquer momento (ver seção de contato).
            </li>
          </ul>
        </section>

        <section>
          <h2 class="text-base font-semibold text-primary mb-2">4. Uso aceitável</h2>
          <p>Ao usar o VigiaConcursos, você concorda em não:</p>
          <ul class="list-disc pl-5 space-y-1 mt-1">
            <li>Utilizar o serviço para fins ilegais ou não autorizados;</li>
            <li>Tentar acessar contas de outros usuários ou dados que não sejam seus;</li>
            <li>Tentar sobrecarregar, comprometer ou explorar vulnerabilidades da plataforma;</li>
            <li>Automatizar cadastros em massa ou usar o serviço de forma abusiva.</li>
          </ul>
        </section>

        <section>
          <h2 class="text-base font-semibold text-primary mb-2">
            5. Sobre a confiabilidade do serviço
          </h2>
          <p>
            Fazemos o possível para monitorar as fontes configuradas e enviar alertas de forma
            oportuna, mas
            <strong
              >não garantimos disponibilidade contínua, ausência de erros, nem prazo fixo de entrega
              das notificações</strong
            >. Como o serviço depende de sites de terceiros que podem sair do ar, mudar de formato
            ou ficar temporariamente indisponíveis, publicações podem eventualmente não ser
            detectadas. O VigiaConcursos é uma ferramenta de apoio e não substitui o acompanhamento
            direto do diário oficial pelo interessado em situações de prazo crítico.
          </p>
        </section>

        <section>
          <h2 class="text-base font-semibold text-primary mb-2">6. Propriedade intelectual</h2>
          <p>
            O código, design e marca do VigiaConcursos pertencem ao seu desenvolvedor. As
            publicações e conteúdos exibidos são informação pública originária dos diários oficiais
            consultados; não reivindicamos propriedade sobre esse conteúdo.
          </p>
        </section>

        <section>
          <h2 class="text-base font-semibold text-primary mb-2">
            7. Limitação de responsabilidade
          </h2>
          <p>
            O VigiaConcursos é fornecido "como está", sem garantias de qualquer tipo. Na máxima
            extensão permitida por lei, não nos responsabilizamos por perdas decorrentes de
            publicações não notificadas, atrasos no envio de alertas, ou decisões tomadas com base
            nas informações fornecidas pelo serviço. Recomendamos sempre confirmar informações
            críticas diretamente na fonte oficial.
          </p>
        </section>

        <section>
          <h2 class="text-base font-semibold text-primary mb-2">8. Encerramento de conta</h2>
          <p>
            Você pode encerrar sua conta a qualquer momento. Podemos suspender ou encerrar contas
            que violem estes Termos, mediante aviso prévio quando possível.
          </p>
        </section>

        <section>
          <h2 class="text-base font-semibold text-primary mb-2">9. Alterações nestes termos</h2>
          <p>
            Podemos atualizar estes Termos periodicamente. Mudanças relevantes serão comunicadas por
            e-mail ou aviso na plataforma antes de entrarem em vigor.
          </p>
        </section>

        <section>
          <h2 class="text-base font-semibold text-primary mb-2">10. Lei aplicável</h2>
          <p>
            Estes Termos são regidos pelas leis do Brasil. Eventuais disputas serão submetidas ao
            foro do domicílio do responsável pelo serviço.
          </p>
        </section>

        <section>
          <h2 class="text-base font-semibold text-primary mb-2">11. Contato</h2>
          <p>
            Dúvidas sobre estes Termos podem ser enviadas para
            <a href="mailto:vigiaconcursoshml@gmail.com" class="text-accent hover:underline"
              >vigiaconcursoshml@gmail.com</a
            >.
          </p>
        </section>
      </div>
    </div>
  </div>
</template>
