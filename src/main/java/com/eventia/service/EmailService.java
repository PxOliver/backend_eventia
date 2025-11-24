package com.eventia.service;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailService {

    // API KEY de SendGrid (la guardas en Render)
    @Value("${SENDGRID_API_KEY}")
    private String sendGridApiKey;

    // Remitente
    @Value("${MAIL_FROM:no-reply@eventia.com}")
    private String fromAddress;

    // URL base del FRONTEND (Render o localhost)
    @Value("${FRONTEND_BASE_URL:http://localhost:3000}")
    private String frontendBaseUrl;

    public void sendVerificationEmail(String to, String token) {

        String verificationUrl = frontendBaseUrl + "/verificar?token=" + token;

        String subject = "Verifica tu cuenta en Eventia";

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

        Email from = new Email(fromAddress);
        Email toEmail = new Email(to);
        Content content = new Content("text/html", htmlContent);
        Mail mail = new Mail(from, subject, toEmail, content);

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);
            System.out.println("SendGrid status: " + response.getStatusCode());
            System.out.println("SendGrid body: " + response.getBody());

        } catch (IOException ex) {
            ex.printStackTrace(); // logueas pero NO rompes el flujo
        }
    }
}