package com.ayakkabi.aya1;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.util.Properties;

public class EmailService {
    // Gmail hesap bilgileri
    private static final String GMAIL_USERNAME = ""; // Gmail adresinizi buraya yazın
    private static final String GMAIL_PASSWORD = ""; // Gmail şifrenizi veya uygulama şifrenizi buraya yazın

    public static void sendEmail(String recipient, String subject, String body, File attachment) throws Exception {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true"); // SSL kullan
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "465"); // SSL portu
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(GMAIL_USERNAME, GMAIL_PASSWORD);
            }
        });

        // Debug modunu açın (isteğe bağlı)
        session.setDebug(true);

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(GMAIL_USERNAME));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
        message.setSubject(subject);

        // E-posta içeriği
        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText(body);

        // Ek dosya
        MimeBodyPart attachmentPart = new MimeBodyPart();
        attachmentPart.attachFile(attachment);

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(textPart);
        multipart.addBodyPart(attachmentPart);

        message.setContent(multipart);

        // E-postayı gönder
        Transport.send(message);
    }
}