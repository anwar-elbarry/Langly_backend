package com.langly.app.email;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final Resend resend;
    private final String formEmail;
    private final String baseUrl;

    public EmailService(@Value("${resend.api-key}") String apiKey,
                        @Value("${resend.from-email}") String fromEmail,
                        @Value("${app.base-url}") String baseUrl) {
        this.resend = new Resend(apiKey);
        this.formEmail = fromEmail;
        this.baseUrl = baseUrl;

    }
    @Async
    public void sendInvitationEmail(String to, String name, String role, String email, String password, String schoolName) {
        String loginLink = baseUrl + "/login";

        CreateEmailOptions options = CreateEmailOptions.builder()
                .from(formEmail)
                .to(to)
                .subject("Invitation à rejoindre " + schoolName + " sur Langly")
                .html(buildEmailContent(name, role, email, password, loginLink, schoolName))
                .build();

        try {
            resend.emails().send(options);
        } catch (ResendException e) {
            throw new IllegalStateException("Échec de l'envoi de l'email", e);
        }
    }

    private String buildEmailContent(String name, String role, String email, String password, String loginLink, String schoolName) {
        return """
            <html>
            <body>
                <h2>Bonjour %s,</h2>
                <p>Vous avez été invité à rejoindre <strong>%s</strong> en tant que <strong>%s</strong>.</p>
                <p>Voici vos identifiants de connexion :</p>
                <ul>
                    <li><strong>Email :</strong> %s</li>
                    <li><strong>Mot de passe :</strong> %s</li>
                </ul>
                <p>Cliquez sur le lien ci-dessous pour vous connecter :</p>
                <a href="%s">Se connecter</a>
                <p>Nous vous recommandons de changer votre mot de passe après la première connexion.</p>
            </body>
            </html>
            """.formatted(name, schoolName, role, email, password, loginLink);
    }
}
