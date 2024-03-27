package com.quiz.api.participant_info;


import com.quiz.domain.participants_info.dto.ParticipantQueueDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Slf4j
@RequiredArgsConstructor
@Controller
public class ParticipantQueueController {

    @MessageMapping("/queue")
    public void getQueue() {
        log.info("connected");
    }
}