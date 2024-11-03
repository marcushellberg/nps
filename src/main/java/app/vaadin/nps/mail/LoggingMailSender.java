package app.vaadin.nps.mail;

import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class LoggingMailSender implements JavaMailSender {
    private static final Logger log = LoggerFactory.getLogger(LoggingMailSender.class);

    @Override
    public void send(SimpleMailMessage message) {
        log.info("""
            ******************************************
            Simulated email:
            To: {}
            Subject: {}
            Text: {}
            ******************************************
            """, message.getTo()[0], message.getSubject(), message.getText());
    }

    @Override
    public void send(SimpleMailMessage... messages) {
        for (SimpleMailMessage message : messages) {
            send(message);
        }
    }

    @Override
    public void send(MimeMessage mimeMessage) {
        log.info("MIME message sending simulated");
    }

    @Override
    public void send(MimeMessage... mimeMessages) {
        log.info("MIME messages sending simulated");
    }

    @Override
    public void send(MimeMessagePreparator mimeMessagePreparator) {
        log.info("MIME message preparation simulated");
    }

    @Override
    public void send(MimeMessagePreparator... mimeMessagePreparators) {
        log.info("MIME messages preparation simulated");
    }

    @Override
    public MimeMessage createMimeMessage() {
        return null;
    }

    @Override
    public MimeMessage createMimeMessage(InputStream contentStream) {
        return null;
    }
}