package utils;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.Multipart;

public class EmailUtil {

    private final Properties mailProps = new Properties();
    private final String username;
    private final String password;
    private final Session sharedSession;
    private final String from;

    public EmailUtil() {
        try (InputStream in = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("mail.properties")) {
            if (in == null) {
                throw new RuntimeException("mail.properties not found on the classpath.");
            }
            mailProps.load(in);
            username = mailProps.getProperty("mail.username");
            password = mailProps.getProperty("mail.password");
            if (username == null || username.isBlank() || password == null || password.isBlank()) {
                throw new RuntimeException("SMTP username/password missing in mail.properties");
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error reading mail.properties: " + ex.getMessage(), ex);
        }

        // Build reusable Session
        Properties props = new Properties();
        props.put("mail.smtp.auth", mailProps.getProperty("mail.smtp.auth", "true"));
        props.put("mail.smtp.starttls.enable", mailProps.getProperty("mail.smtp.starttls.enable", "true"));
        props.put("mail.smtp.host", mailProps.getProperty("mail.smtp.host", "smtp.gmail.com"));
        props.put("mail.smtp.port", mailProps.getProperty("mail.smtp.port", "587"));
        props.put("mail.smtp.connectiontimeout", mailProps.getProperty("mail.smtp.connectiontimeout", "10000"));
        props.put("mail.smtp.timeout", mailProps.getProperty("mail.smtp.timeout", "10000"));
        // props.put("mail.smtp.starttls.required", "true"); // optional

        sharedSession = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        from = mailProps.getProperty("mail.from", username);
    }

    public void sendHtml(String to, String subject, String htmlContent) {
        if (to == null || to.isBlank()) {
            throw new IllegalArgumentException("Recipient address 'to' is required");
        }

        // Normalise separators (allow ; or ,)
        String normalizedTo = to.replace(";", ",");

        try {
            MimeMessage message = new MimeMessage(sharedSession);
            // Set from (handle encoding)
            try {
                message.setFrom(new InternetAddress(from, "WebNovel", "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                message.setFrom(new InternetAddress(from)); // fallback
            }

            // Strict parse will throw AddressException for invalid addresses
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(normalizedTo, true));
            message.setSubject(subject, "UTF-8");

            MimeBodyPart body = new MimeBodyPart();
            body.setContent(htmlContent, "text/html; charset=UTF-8");

            Multipart mp = new MimeMultipart();
            mp.addBodyPart(body);
            message.setContent(mp);

            // For simple use Transport.send is fine (will connect, send, disconnect)
            Transport.send(message);
        } catch (MessagingException me) {
            // TODO: replace with logger and proper retry / dead-letter handling
            me.printStackTrace();
            throw new RuntimeException("Failed to send email: " + me.getMessage(), me);
        }
    }
}
