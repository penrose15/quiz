package com.quiz.global.security.userdetails;

import com.quiz.domain.users.entity.Users;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

@Getter
public class UserAccount extends User {
    private final Users users;

    public UserAccount(Users users) {
        super(users.getEmail(), "", List.of(new SimpleGrantedAuthority(users.getRole())));
        this.users = users;
    }
}
