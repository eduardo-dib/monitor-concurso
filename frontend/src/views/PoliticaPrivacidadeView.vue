<script setup lang="ts"></script>

<template>
  <div class="max-w-3xl mx-auto px-4 py-12">
    <div class="bg-surface rounded-2xl border border-gray-200 p-8 sm:p-10">
      <h1 class="text-2xl font-bold text-primary mb-1">Política de Privacidade</h1>
      <p class="text-sm text-gray-500 mb-8">Última atualização: agosto de 2026</p>

      <div class="space-y-8 text-sm text-gray-700 leading-relaxed">
        <section>
          <h2 class="text-base font-semibold text-primary mb-2">1. Quem somos</h2>
          <p>
            O VigiaConcursos é a plataforma responsável pelo tratamento dos seus dados pessoais para
            os fins descritos nesta política, atuando como controladora das informações nos termos
            da Lei Geral de Proteção de Dados (Lei nº 13.709/2018 — LGPD).
          </p>
        </section>

        <section>
          <h2 class="text-base font-semibold text-primary mb-2">2. Quais dados coletamos</h2>
          <ul class="list-disc pl-5 space-y-1">
            <li>
              <strong>Dados de cadastro:</strong> nome e e-mail, informados por você no momento do
              cadastro.
            </li>
            <li>
              <strong>Senha:</strong> armazenada sempre de forma criptografada (hash), nunca em
              texto legível, nem mesmo nós temos acesso à sua senha original.
            </li>
            <li>
              <strong>Dados dos alertas:</strong> palavras-chave, estado, município e órgão que você
              configura para monitoramento.
            </li>
          </ul>
        </section>

        <section>
          <h2 class="text-base font-semibold text-primary mb-2">3. Para que usamos seus dados</h2>
          <p>Usamos os dados coletados exclusivamente para:</p>
          <ul class="list-disc pl-5 space-y-1 mt-1">
            <li>Autenticar seu acesso à plataforma;</li>
            <li>Monitorar diários oficiais de acordo com os alertas que você configurar;</li>
            <li>
              Enviar notificações por e-mail quando uma publicação corresponder a um dos seus
              alertas.
            </li>
          </ul>
          <p class="mt-2">
            Não usamos seus dados para fins de publicidade, nem os vendemos ou cedemos a terceiros
            para fins comerciais.
          </p>
        </section>

        <section>
          <h2 class="text-base font-semibold text-primary mb-2">4. Cookies</h2>
          <p>
            Utilizamos um único cookie, estritamente necessário para manter sua sessão autenticada
            (do tipo <code class="text-xs bg-gray-100 px-1 py-0.5 rounded">HttpOnly</code>, que não
            pode ser lido por scripts). Não utilizamos cookies de rastreamento, publicidade ou
            ferramentas de analytics de terceiros.
          </p>
        </section>

        <section>
          <h2 class="text-base font-semibold text-primary mb-2">
            5. Compartilhamento com terceiros
          </h2>
          <p>
            Seus dados são compartilhados apenas com os prestadores de serviço estritamente
            necessários para o funcionamento da plataforma:
          </p>
          <ul class="list-disc pl-5 space-y-1 mt-1">
            <li>
              <strong>Provedor de envio de e-mail</strong> (Brevo), usado exclusivamente para
              entregar as notificações de alerta;
            </li>
            <li>
              <strong>Provedor de hospedagem em nuvem</strong> (Oracle Cloud), onde os dados ficam
              armazenados.
            </li>
          </ul>
        </section>

        <section>
          <h2 class="text-base font-semibold text-primary mb-2">6. Como protegemos seus dados</h2>
          <p>
            Senhas são armazenadas com hash criptográfico (bcrypt) e nunca em texto puro. Toda
            comunicação entre seu navegador e nossos servidores é feita via HTTPS. A autenticação
            usa cookie de sessão inacessível a scripts, reduzindo o risco de roubo de sessão mesmo
            em caso de vulnerabilidades no próprio site.
          </p>
        </section>

        <section>
          <h2 class="text-base font-semibold text-primary mb-2">
            7. Por quanto tempo guardamos seus dados
          </h2>
          <p>
            Seus dados são mantidos enquanto sua conta estiver ativa. Você pode solicitar a exclusão
            da sua conta e de todos os dados associados a qualquer momento pelo contato abaixo. A
            exclusão pela própria interface está em desenvolvimento e estará disponível em breve.
          </p>
        </section>

        <section>
          <h2 class="text-base font-semibold text-primary mb-2">8. Seus direitos</h2>
          <p>Nos termos da LGPD, você tem direito a, a qualquer momento:</p>
          <ul class="list-disc pl-5 space-y-1 mt-1">
            <li>Confirmar a existência de tratamento dos seus dados;</li>
            <li>Acessar os dados que temos sobre você;</li>
            <li>Corrigir dados incompletos, inexatos ou desatualizados;</li>
            <li>Solicitar a anonimização, bloqueio ou eliminação de dados desnecessários;</li>
            <li>Solicitar a portabilidade dos seus dados;</li>
            <li>Solicitar a exclusão dos dados tratados com base no seu consentimento;</li>
            <li>Revogar seu consentimento a qualquer momento.</li>
          </ul>
          <p class="mt-2">
            Para exercer qualquer um desses direitos, entre em contato pelo e-mail abaixo.
          </p>
        </section>

        <section>
          <h2 class="text-base font-semibold text-primary mb-2">9. Alterações nesta política</h2>
          <p>
            Esta política pode ser atualizada periodicamente. Alterações relevantes serão
            comunicadas por e-mail ou por aviso na própria plataforma.
          </p>
        </section>

        <section>
          <h2 class="text-base font-semibold text-primary mb-2">10. Contato</h2>
          <p>
            Dúvidas ou solicitações sobre seus dados pessoais podem ser enviadas para
            <a href="mailto:cardozoedudib@gmail.com" class="text-accent hover:underline"
              >vigiaconcursoshml@gmail.com</a
            >.
          </p>
        </section>
      </div>
    </div>
  </div>
</template>
