package com.example.sendmail_reciver;

import com.example.sendmail_reciver.model.Users;
import com.example.sendmail_reciver.reponsitory.UsersRepository;
import com.example.sendmail_reciver.service.SendMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.List;

@SpringBootApplication
@EnableJpaAuditing
public class SendmailReciverApplication {

    public static void main(String[] args) {
        SpringApplication.run(SendmailReciverApplication.class, args);
    }


}
