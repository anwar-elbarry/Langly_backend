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
    private final String fromEmail;
    private final String baseUrl;
    private final boolean mailEnabled;

    public EmailService(@Value("${resend.api-key}") String apiKey,
                        @Value("${resend.from-email}") String fromEmail,
                        @Value("${app.base-url}") String baseUrl,
                        @Value("${app.mail.enabled:true}") boolean mailEnabled) {
        this.resend = new Resend(apiKey);
        this.fromEmail = fromEmail;
        this.baseUrl = baseUrl;
        this.mailEnabled = mailEnabled;
    }


    @Async
    public void sendInvitationEmail(String to, String name, String role, String email, String password, String schoolName) {
        String loginLink = baseUrl + "/login";

        CreateEmailOptions options = CreateEmailOptions.builder()
                .from(fromEmail)
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


    public EmailPreview sendInvitationEmailWithPreview(String to, String name, String role,
                                                       String email, String password, String schoolName) {
        String loginLink = baseUrl + "/login";
        String message = "Bonjour " + name + ", vous avez été invité à rejoindre " + schoolName
                + " en tant que " + role + ". Connectez-vous avec l'email : " + email
                + " et le mot de passe temporaire ci-dessous.";

        if (!mailEnabled) {
            return EmailPreview.builder()
                    .from(fromEmail)
                    .to(to)
                    .subject("Invitation à rejoindre " + schoolName + " sur Langly")
                    .message(message)
                    .loginLink(loginLink)
                    .temporaryPassword(password)
                    .build();
        }

        sendInvitationEmail(to, name, role, email, password, schoolName);
        return null;
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
