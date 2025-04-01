package il.cshaifasweng.OCSFMediatorExample.server;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailSender {

    private static final String FROM = "mamaskitchenteam9@gmail.com";
    private static final String PASSWORD = "lkgkhtokyhqaagrb";

    // Reusable thread pool for email sending,manages thread shutdown
    private static final ExecutorService emailExecutor = Executors.newFixedThreadPool(5);

    // Public method for async email sending
    public static void sendEmailAsync(String to, String subject, String body) {
        emailExecutor.submit(() -> sendEmail(to, subject, body));
    }

    // Internal method for actual email sending
    public static void sendEmail(String to, String subject, String body) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM, PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            System.out.println("Email sent successfully to " + to);

        } catch (MessagingException e) {
            System.err.println("Failed to send email to " + to);
            e.printStackTrace();
        }
    }

    // Optional: Shutdown hook for cleanup
    public static void shutdown() {
        emailExecutor.shutdown();
    }
}
