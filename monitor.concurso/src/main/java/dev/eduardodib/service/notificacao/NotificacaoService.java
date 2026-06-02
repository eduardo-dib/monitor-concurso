package dev.eduardodib.service.notificacao;



import dev.eduardodib.domain.alertamonitoramento.AlertaMonitoramentoEntity;
import dev.eduardodib.domain.publicacaoencontrada.PublicacaoEncontradaEntity;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import java.util.List;

@ApplicationScoped
public class NotificacaoService {

    private static final Logger LOG = Logger.getLogger(NotificacaoService.class);

    @Inject
    Mailer mailer;

    public void notificarMatches(AlertaMonitoramentoEntity alerta, List<PublicacaoEncontradaEntity> publicacoes) {
        if (publicacoes.isEmpty()) return;

        String email = alerta.usuario.email;
        String nome = alerta.usuario.nome;
        String palavrasChave = alerta.palavrasChave;

        StringBuilder corpo = new StringBuilder();
        corpo.append("<h2>Olá, ").append(nome).append("!</h2>");
        corpo.append("<p>Encontramos <strong>").append(publicacoes.size()).append(" nova(s) publicação(ões)</strong> para o seu alerta <strong>").append(palavrasChave).append("</strong>.</p>");
        corpo.append("<hr/>");
        for (PublicacaoEncontradaEntity pub : publicacoes) {
            corpo.append("<p><strong>Data:</strong> ").append(pub.dataPublicacao).append("</p>");
            corpo.append("<p><strong>Território:</strong> ").append(pub.territorio).append("</p>");

            if (pub.edicao != null && !pub.edicao.isEmpty()) {
                corpo.append("<p><strong>Edição:</strong> ").append(pub.edicao).append("</p>");
            }

            if (pub.pagina != null && !pub.pagina.isEmpty()) {
                corpo.append("<p><strong>Página:</strong> ").append(pub.pagina).append("</p>");
            }

            if (pub.conteudo != null && !pub.conteudo.isEmpty()) {
                corpo.append("<p><strong>Trecho:</strong> <em>").append(pub.conteudo).append("</em></p>");
            }

            if (pub.link != null && !pub.link.isEmpty()) {
                corpo.append("<p><a href='").append(pub.link).append("'>Ver publicação</a></p>");
            }

            corpo.append("<hr/>");
        }

        try {
            mailer.send(
                    Mail.withHtml(email,
                             publicacoes.size() + " nova(s) publicação(ões) encontrada(s): " + palavrasChave,
                            corpo.toString()
                    )
            );
            LOG.infof("E-mail enviado para %s com %d publicações", email, publicacoes.size());
        } catch (Exception e) {
            LOG.errorf(e, "Erro ao enviar e-mail para %s", email);
        }
    }
}
