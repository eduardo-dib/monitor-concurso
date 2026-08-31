package dev.eduardodib.service.email;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class RecuperacaoSenhaService {

    @Inject
    Mailer mailer;

    @ConfigProperty(name = "app.frontend.url")
    String frontendUrl;

    public void enviarEmailRecuperacao(String emailDestino, String token) {
        String link = frontendUrl + "/redefinir-senha?token=" + token;
        String html = """
            <h2>Recuperação de Senha - VigiaConcursos</h2>
            <p>Você solicitou a redefinição de senha da sua conta.</p>
            <p>Clique no link abaixo para criar uma nova senha. Este link expira em 1 hora.</p>
            <a href="%s">Redefinir minha senha</a>
            <br/><br/>
            <p>Se você não solicitou, ignore este e-mail.</p>
        """.formatted(link);
        mailer.send(Mail.withHtml(emailDestino, "Redefinição de Senha", html));
    }
}