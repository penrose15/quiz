package com.quiz.domain.users.service;

import com.quiz.domain.users.dto.UserNameDto;
import com.quiz.domain.users.entity.Users;
import com.quiz.domain.users.repository.UsersRepository;
import com.quiz.domain.users.dto.UsersRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class UsersService {
    private final UsersRepository usersRepository;

    public void save(UsersRequestDto request) {
        Users users = Users.builder()
                .email(request.getEmail())
                .name(request.getName())
                .provider(request.getProvider())
                .build();
        usersRepository.save(users);
    }

    public List<UserNameDto> findUsernameDtosByIds(List<Long> userIds) {
        return usersRepository.findUsernameByIds(userIds);
    }

}