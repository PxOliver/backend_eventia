package com.eventia.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    // En local, si no hay variable, usa http://localhost:3000
    @Value("${app.frontend.base-url:http://localhost:3000}")
    private String frontendBaseUrl;

    // Remitente opcional (puedes configurarlo igual en el properties/env)
    @Value("${spring.mail.username:no-reply@eventia.com}")
    private String fromAddress;

    public void sendVerificationEmail(String to, String token) {

        String subject = "Verifica tu cuenta en Eventia";

        // Construir URL hacia el front (local o Render según env var)
        String verificationUrl = frontendBaseUrl + "/verificar?token=" + token;

        String htmlContent = """
            <div style="font-family: Arial, sans-serif; padding: 20px; background-color: #f7f7f7;">
                <div style="max-width: 500px; margin: auto; background: white; padding: 25px; border-radius: 10px; box-shadow: 0px 4px 12px rgba(0,0,0,0.1);">
                    
                    <h2 style="color: #5C2A9D; text-align: center; margin-top: 0;">¡Bienvenido a <span style="color:#FFD700;">Eventia</span>!</h2>
                    
                    <p style="font-size: 15px; color: #333; line-height: 1.6;">
                        Gracias por registrarte en nuestro sistema de reservas de espacios para eventos.
                        Para <strong>activar tu cuenta</strong> y comenzar a usar la plataforma,
                        haz clic en el siguiente botón:
                    </p>
                    
                    <div style="text-align: center; margin: 30px 0;">
                        <a href="%s" 
                           style="background-color: #5C2A9D; color: white; padding: 12px 28px; 
                                  text-decoration: none; font-size: 16px; border-radius: 8px;
                                  display: inline-block; font-weight: 600;">
                            Verificar mi cuenta
                        </a>
                    </div>

                    <p style="font-size: 13px; color: #555; line-height: 1.5;">
                        Si el botón no funciona, también puedes copiar y pegar este enlace en tu navegador:
                        <br/>
                        <span style="font-size: 12px; color: #777;">%s</span>
                    </p>

                    <p style="font-size: 12px; color: #999; margin-top: 25px;">
                        Si tú no creaste esta cuenta, puedes ignorar este mensaje con tranquilidad.
                    </p>

                    <p style="font-size: 11px; text-align:center; color: #aaa; margin-top: 15px;">
                        © Eventia · Sistema de Reservas de Espacios para Eventos
                    </p>

                </div>
            </div>
        """.formatted(verificationUrl, verificationUrl);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);   // true = HTML
            helper.setFrom(fromAddress);         // remitente

            mailSender.send(message);

            // Opcional: loguear éxito
            System.out.println("Email de verificación enviado a: " + to
                    + " con URL: " + verificationUrl);

        } catch (MessagingException e) {
            // NO rompemos el flujo, solo log
            System.err.println("Error enviando correo de verificación a " + to);
            e.printStackTrace();
        }
    }
}