package com.eventos.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class NotificacionService {

    private final JavaMailSender mailSender;

    public NotificacionService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarBienvenida(String destinatario, String nombreUsuario) throws MessagingException {
        MimeMessage mensaje = mailSender.createMimeMessage();
        MimeMessageHelper ayudante = new MimeMessageHelper(mensaje, true);

        ayudante.setTo(destinatario);
        ayudante.setSubject("¡Bienvenido a la Plataforma de Eventos!");
        ayudante.setText(
            "<h2>Hola, " + nombreUsuario + "!</h2>" +
            "<p>Tu cuenta ha sido creada exitosamente en la Plataforma de Reservas de Eventos.</p>" +
            "<p>Ya puedes iniciar sesión y explorar los eventos disponibles.</p>" +
            "<br><p>Equipo de Gestión de Eventos</p>",
            true
        );
        mailSender.send(mensaje);
    }
}
