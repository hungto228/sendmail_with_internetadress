package com.example.sendmail_reciver.controller;

import com.example.sendmail_reciver.model.Users;
import com.example.sendmail_reciver.reponsitory.UsersRepository;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.module.ResolutionException;

@RestController
public class UserController {
    @Autowired
    private UsersRepository usersRepository;

    @GetMapping("/users")
    public Page<Users> getAllUser(Pageable pageable) {
        return usersRepository.findAll(pageable);
    }

    @PostMapping("/users")
    public Users createUser(@Valid @RequestBody Users user) {
        return usersRepository.save(user);
    }

    @PostMapping("/users/{mailId}")
    public Users users(@PathVariable Long ID, @Valid @RequestBody Users mailRequest) {
        return usersRepository.findById(ID).map(mailModel -> {
            mailModel.setId(mailRequest.getId());
            mailModel.setEmail(mailModel.getEmail());
            mailModel.setBody(mailModel.getBody());
            mailModel.setTopic(mailModel.getTopic());
            return usersRepository.save(mailModel);
        }).orElseThrow(() -> new ResourceNotFoundException("mailid" + ID + "notfound"));
    }
}
