package com.example.sendmail_reciver.service;

import com.example.sendmail_reciver.model.Users;
import com.example.sendmail_reciver.reponsitory.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Properties;
@Service
public class SendMailService {
    @Autowired
    private UsersRepository usersRepository;
    @Autowired SendMailService sendMailService;
    private static final String SMTP_HOST_NAME = "smtp.googlemail.com";
    private static final String SMTP_AUTH_USER = "hungto2288@gmail.com";
    private static final String SMTP_AUTH_PASSWORD = "baybay99";
    private static final String EMAIL_SUBJECT="SSS333";
    private static final String EMAIL_CONTENT="SSS";
    @Scheduled
    public void sendEmail(String[] recipientsList, String emailSubject,String emailContent, String senderAddress)
            throws MessagingException {
        //Set the host smtp address
        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_HOST_NAME);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
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
        emailMessage.setContent(emailContent, "text/plain");
        Transport.send(emailMessage);
    }
    private class CredentialsAuthenticator extends javax.mail.Authenticator {

        public PasswordAuthentication getPasswordAuthentication() {
            String username = SMTP_AUTH_USER;
            String password = SMTP_AUTH_PASSWORD;
            return new PasswordAuthentication(username, password);
        }
    }
    @EventListener(ApplicationReadyEvent.class)
    public void trigerWhenSattus() throws MessagingException {
        List<Users> userList = usersRepository.findAll();

        String[] recipientsList = new String[userList.size()];
        for (int i =0; i< userList.size(); i++){
            recipientsList[i] = userList.get(i).getEmail();

        }

        sendMailService.sendEmail(recipientsList,EMAIL_SUBJECT,EMAIL_CONTENT,SMTP_AUTH_USER);
        System.out.println(userList.size()+" is sended");
    }
//    public static void main(String args[]) throws Exception {
//        SendMailService emailSender = new SendMailService();
//        String[] recipientsList = {"hungto228@gmail.com"};
//        String emailSubject = "Subject Header";
//        String emailContent = "This is the body of the email";
//        emailSender.sendEmail(recipientsList, emailSubject, emailContent, SMTP_AUTH_USER);
//        System.out.println("Email(s) sent successfully");
//    }
}
