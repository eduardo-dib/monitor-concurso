package dev.eduardodib.service.contato;

import dev.eduardodib.domain.contato.CategoriaContato;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.List;

@ApplicationScoped
public class ContatoService {

    @Inject
    Mailer mailer;

    @ConfigProperty(name = "app.contato.email")
    String emailContato;

    public void enviarContato(String nome, String emailRemetente, CategoriaContato categoria, String mensagem) {
        String html = """
            <h2>Novo contato - VigiaConcursos</h2>
            <p><strong>Categoria:</strong> %s</p>
            <p><strong>Nome:</strong> %s</p>
            <p><strong>E-mail:</strong> %s</p>
            <p><strong>Mensagem:</strong></p>
            <p>%s</p>
        """.formatted(categoria, nome, emailRemetente, mensagem.replace("\n", "<br/>"));

        Mail mail = Mail.withHtml(
                emailContato,
                "[VigiaConcursos] " + categoria + " - " + nome,
                html
        ).setReplyTo(emailRemetente);

        mailer.send(mail);
    }
}
