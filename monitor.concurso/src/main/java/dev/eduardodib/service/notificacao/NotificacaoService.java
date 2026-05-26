package dev.eduardodib.service.notificacao;


import dev.eduardodib.domain.publicacaoencontrada.PublicacaoEncontradaEntity;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

@ApplicationScoped
public class NotificacaoService {

    private static final Logger LOG = Logger.getLogger(NotificacaoService.class);

    @Inject
    Mailer mailer;

    public void notificarMatch(PublicacaoEncontradaEntity publicacao) {
        String email = publicacao.alerta.usuario.email;
        String nome = publicacao.alerta.usuario.nome;
        String palavrasChave = publicacao.alerta.palavrasChave;

        try {
            mailer.send(
                    Mail.withHtml(email,
                            "🔔 Nova publicação encontrada: " + palavrasChave,
                            "<h2>Olá, " + nome + "!</h2>" +
                                    "<p>Uma nova publicação foi encontrada para o seu alerta <strong>" + palavrasChave + "</strong>.</p>" +
                                    "<p><strong>Data:</strong> " + publicacao.dataPublicacao + "</p>" +
                                    "<p><strong>Território:</strong> " + publicacao.territorio + "</p>" +
                                    "<p><a href='" + publicacao.link + "'>Clique aqui para ver a publicação</a></p>"
                    )
            );
            LOG.infof("E-mail enviado para %s", email);
        } catch (Exception e) {
            LOG.errorf(e, "Erro ao enviar e-mail para %s", email);
        }
    }
}
