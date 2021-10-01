package wifi.codewl.pdfviewer.Email;


import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import wifi.codewl.pdfviewer.Kernel.Status;

public class Gmail {
    public static void messages(final String from, final String password, String to, String title, String description, List<String> files) {
        //Get properties object
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        //get Session
        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(from, password);
                    }
                });

        //compose message
        try {
            MimeMessage message = new MimeMessage(session);
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(description);
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            MimeBodyPart mimeBodyPart = null;
            for (int i = 0; i < files.size(); i++) {
                mimeBodyPart = new MimeBodyPart();
                mimeBodyPart.setDataHandler(new DataHandler(new FileDataSource(files.get(i))));
                mimeBodyPart.setFileName(files.get(i));
                multipart.addBodyPart(mimeBodyPart);
            }

            message.setSubject(title);
            message.setContent(multipart);
            //send message
            Transport.send(message);
            Status.send = true;

        } catch (MessagingException e) {
           Status.send = false;
        }

    }



}
