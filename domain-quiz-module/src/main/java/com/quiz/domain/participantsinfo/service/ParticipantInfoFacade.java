package com.quiz.domain.participantsinfo.service;

import com.quiz.domain.participantsinfo.entity.ParticipantInfo;
import com.quiz.domain.quiz.entity.Quiz;
import com.quiz.domain.quiz.service.QuizService;
import com.quiz.domain.participantsinfo.dto.ParticipantsRankResponseDto;
import com.quiz.domain.response.dto.ResponsesRequestDto;
import com.quiz.domain.response.dto.ResponsesSaveDto;
import com.quiz.domain.response.service.ResponsesFacade;
import com.quiz.domain.users.dto.UserNameDto;
import com.quiz.domain.users.service.UsersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class ParticipantInfoFacade {

    private final ParticipantInfoService participantInfoService;
    private final QuizService quizService;
    private final UsersService usersService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ResponsesFacade responsesFacade;

    public void saveParticipants(Long quizId, Long userId) {
        Quiz quiz = quizService.findById(quizId);
//        if(quiz.getStartDate().isBefore(LocalDateTime.now())) {
//            throw new RuntimeException("startDate is not now");
//        }
        int capacity = quiz.getCapacity();
        if(capacity > 0) {
            participantInfoService.saveFcfs(quizId, userId, capacity);
            return;
        }
        participantInfoService.save(quizId, userId);
    }

    public void updateParticipantAndSaveResponse(Long quizId, Long userId, List<ResponsesRequestDto> responses) {
        Integer quizCapacity = quizService.findById(quizId)
                .getCapacity();
        if (quizCapacity > 0) {
            participantInfoService.updateFcFs(quizId, userId, quizCapacity);
        }
        String participantInfoId = participantInfoService.findByQuizIdAndUserId(quizId, userId)
                .getId();
        log.info("participantInfoId = {}", participantInfoId);

        ResponsesSaveDto responsesSaveDto = ResponsesSaveDto.builder()
                .participantInfoId(participantInfoId)
                .responses(responses)
                .quizId(quizId)
                .build();

        log.info("save response start");
        //event 로 저장과정 분리
        applicationEventPublisher.publishEvent(responsesSaveDto);
//        responsesFacade.calculateScoreAndSaveResponse(quizId, responses, participantInfoId);
    }

    public void checkUserParticipatedOrOwner(Long quizId, Long userId) {
        boolean userIsParticipant = participantInfoService.isUserParticipated(quizId, userId);
        boolean userIsQuizOwner = quizService.isUserIsQuizOwner(userId, quizId);

        if(!(userIsParticipant || userIsQuizOwner)) throw new RuntimeException("not participate in quiz");
    }

    public List<ParticipantsRankResponseDto> showRanks(Long quizId) {
        List<ParticipantInfo> result = participantInfoService.findParticipantInfoByQuizId(quizId);

        List<Long> userIds = result.stream()
                .map(ParticipantInfo::getUserId)
                .toList();

        Map<Long, UserNameDto> userDtoMap = usersService.findUsernameDtosByIds(userIds)
                .stream()
                .collect(Collectors.toMap(UserNameDto::getId, i2 -> i2));

       return result.stream()
                .map(p -> ParticipantsRankResponseDto
                        .builder()
                        .id(p.getId())
                        .userId(p.getUserId())
                        .username(userDtoMap.get(p.getId()).getName())
                        .number(p.getNumber())
                        .totalScore(p.getTotalScore())
                        .build())
                .toList();
    }

}