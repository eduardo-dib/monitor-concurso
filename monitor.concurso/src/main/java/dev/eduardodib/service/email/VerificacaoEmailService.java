package dev.eduardodib.service.email;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class VerificacaoEmailService {

    @Inject
    Mailer mailer;

    public void enviarCodigoVerificacao(String emailDestino, String codigo) {
        String html = """
            <h2>Confirme seu e-mail - VigiaConcursos</h2>
            <p>Use o código abaixo para confirmar seu cadastro. Ele expira em 15 minutos.</p>
            <h1 style="letter-spacing: 4px;">%s</h1>
            <p>Se você não se cadastrou no VigiaConcursos, ignore este e-mail.</p>
        """.formatted(codigo);
        mailer.send(Mail.withHtml(emailDestino, "Confirme seu cadastro", html));
    }
}