package com.example.sendmail_reciver.service;

import com.example.sendmail_reciver.model.Users;
import com.example.sendmail_reciver.reponsitory.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.List;
import java.util.Properties;

@Configuration
@EnableScheduling
public class SendMailService {
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    SendMailService sendMailService;

    @Value("${spring.mail.host}")
    String SMTP_HOST_NAME;
    @Value("${spring.mail.username}")
    private String SMTP_AUTH_USER;
    @Value("${spring.mail.password}")
    private String SMTP_AUTH_PASSWORD;
    @Value("${spring.mail.port}")
    private String PORT;
    @Value("${EMAIL_SUBJECT}")
    private String EMAIL_SUBJECT;
    @Value("${EMAIL_CONTENT}")
    private String EMAIL_CONTENT;
    @Value("${PATH_IMAGE}")
    private String PATH_IMAGE;
    @Value("${NAME_FILE}")
    private String NAME_FILE;


    int x = 0;
    //send 5s one again
    @Scheduled(cron = "*/5 * * * * *")
    public void sendMail() throws MessagingException {
        x++;
        //fetch email template from db

        List<Users> userList = usersRepository.findAll();

        String[] recipientsList = new String[userList.size()];
        for (int i = 0; i < userList.size(); i++) {
            recipientsList[i] = userList.get(i).getEmail();

        }
        this.sendEmail(recipientsList, EMAIL_SUBJECT, EMAIL_CONTENT, SMTP_AUTH_USER);
        System.out.println("mail is being sent from batch..." + x);


    }

    //logic to send mail
    @Autowired
    private JavaMailSender javaMailSender;

    public void sendEmail(String[] recipientsList, String emailSubject, String emailContent, String senderAddress) throws MessagingException {

        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_HOST_NAME);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        Authenticator authenticator = new CredentialsAuthenticator();
        Session session = Session.getDefaultInstance(props, authenticator);
        session.setDebug(false);
        //Create Email
        Message emailMessage = new MimeMessage(session);
        InternetAddress senderInternetAddress = new InternetAddress(senderAddress);
        emailMessage.setFrom(senderInternetAddress);

        InternetAddress[] recipientInternetAddress = new InternetAddress[recipientsList.length];
        for (int i = 0; i < recipientsList.length; i++) {
            recipientInternetAddress[i] = new InternetAddress(recipientsList[i]);
        }
        emailMessage.setRecipients(Message.RecipientType.TO, recipientInternetAddress);
        emailMessage.setSubject(emailSubject);


        //send Image
        MimeMultipart multipart = new MimeMultipart("related");
        BodyPart messageBodyPart = new MimeBodyPart();

        //create htmltext convert to image
        // add img src to show image
        String htmlText = EMAIL_CONTENT + "<br/><img src=\"cid:image\">";

        messageBodyPart.setContent(htmlText, "text/html");
        // add it
        multipart.addBodyPart(messageBodyPart);

        // second part (the image)
        messageBodyPart = new MimeBodyPart();
        DataSource fds = new FileDataSource(
                PATH_IMAGE);

        messageBodyPart.setDataHandler(new DataHandler(fds));
        messageBodyPart.setFileName(NAME_FILE);
        messageBodyPart.setHeader("Content-ID", "<image>");

        // add image to the multipart
        multipart.addBodyPart(messageBodyPart);

        emailMessage.setContent(multipart, "text/html; charset=utf-8");

        //or send text message
        //  emailMessage.setContent(emailContent,"text/html; charset=utf-8");
        Transport.send(emailMessage);

    }

    private class CredentialsAuthenticator extends javax.mail.Authenticator {

        public PasswordAuthentication getPasswordAuthentication() {
            String username = SMTP_AUTH_USER;
            String password = SMTP_AUTH_PASSWORD;
            return new PasswordAuthentication(username, password);
        }
    }


}
