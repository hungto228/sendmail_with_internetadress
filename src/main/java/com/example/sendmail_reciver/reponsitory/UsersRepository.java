package com.example.sendmail_reciver.reponsitory;

import com.example.sendmail_reciver.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long > {
}
