package com.studyolle.event;

import com.studyolle.WithAccount;
import com.studyolle.domain.*;
import com.studyolle.study.StudyControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EventControllerTest extends StudyControllerTest {

    @Autowired
    EventService eventService;

    @Autowired
    EnrollmentRepository enrollmentRepository;


    @Test
    @DisplayName("선착순 모임에 참가 신청 - 자동 수락")
    @WithAccount("CHEEOLEE")
    void newEnrollment_to_FCFS_event_accepted() throws Exception {
        Account lee = createAccount("LEE");
        Study study = createStudy("test-study", lee);
        Event event = createEvent("test-event", EventType.FCFS, 2, study, lee);

        mockMvc.perform(post("/study/" + study.getPath() + "/events/" + event.getId() + "/enroll")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getPath() + "/events/" + event.getId()));

        Account cheeolee = accountRepository.findByNickname("CHEEOLEE");
        isAccepted(cheeolee, event);
    }

    @Test
    @DisplayName("선착순 모임에 참가 신청 - 대기중 (이미 인원이 꽉차서)")
    @WithAccount("CHEEOLEE")
    void newEnrollment_to_FCFS_event_not_accepted() throws Exception {
        Account lee = createAccount("lee");
        Study study = createStudy("test-study", lee);
        Event event = createEvent("test-event", EventType.FCFS, 2, study, lee);

        Account may = createAccount("may");
        Account june = createAccount("june");
        eventService.newEnrollment(may, event);
        eventService.newEnrollment(june, event);

        mockMvc.perform(post("/study/" + study.getPath() + "/events/" + event.getId() + "/enroll")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getPath() + "/events/" + event.getId()));

        Account cheeolee = accountRepository.findByNickname("CHEEOLEE");
        isNotAccepted(cheeolee, event);
    }

    @Test
    @DisplayName("참가신청 확정자가 선착순 모임에 참가 신청을 취소하는 경우, 바로 다음 대기자를 자동으로 신청 확인한다.")
    @WithAccount("CHEEOLEE")
    void accepted_account_cancelEnrollment_to_FCFS_event_not_accepted() throws Exception {
        Account cheeolee = accountRepository.findByNickname("CHEEOLEE");
        Account lee = createAccount("LEE");
        Account may = createAccount("may");
        Study study = createStudy("test-study", lee);
        Event event = createEvent("test-event", EventType.FCFS, 2, study, lee);

        eventService.newEnrollment(may, event);
        eventService.newEnrollment(lee, event);
        eventService.newEnrollment(cheeolee, event);

        isAccepted(may, event);
        isAccepted(lee, event);
        isNotAccepted(cheeolee, event);

        mockMvc.perform(post("/study/" + study.getPath() + "/events/" + event.getId() + "/disenroll")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getPath() + "/events/" + event.getId()));

        isAccepted(may, event);
        isAccepted(lee, event);
        assertNull(enrollmentRepository.findByEventAndAccount(event, cheeolee));
    }

    @Test
    @DisplayName("참가신청 비확정자가 선착순 모임에 참가 신청을 취소하는 경우, 기존 확정자를 그대로 유지하고 새로운 확정자는 없다.")
    @WithAccount("CHEEOLEE")
    void not_accepted_account_cancelEnrollment_to_FCFS_event_not_accepted() throws Exception {
        Account cheeolee = accountRepository.findByNickname("CHEEOLEE");
        Account lee = createAccount("LEE");
        Account may = createAccount("may");
        Study study = createStudy("test-study", lee);
        Event event = createEvent("test-event", EventType.FCFS, 2, study, lee);

        eventService.newEnrollment(may, event);
        eventService.newEnrollment(lee, event);
        eventService.newEnrollment(cheeolee, event);

        isAccepted(may, event);
        isAccepted(lee, event);
        isNotAccepted(cheeolee, event);

        mockMvc.perform(post("/study/" + study.getPath() + "/events/" + event.getId() + "/disenroll")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getPath() + "/events/" + event.getId()));

        isAccepted(may, event);
        isAccepted(lee, event);
        assertNull(enrollmentRepository.findByEventAndAccount(event, cheeolee));
    }

    @Test
    @DisplayName("관리자 확인 모임에 참가 신청 - 대기중")
    @WithAccount("CHEEOLEE")
    void newEnrollment_to_CONFIMATIVE_event_not_accepted() throws Exception {
        Account lee = createAccount("LEE");
        Study study = createStudy("test-study", lee);
        Event event = createEvent("test-event", EventType.CONFIRMATIVE, 2, study, lee);

        mockMvc.perform(post("/study/" + study.getPath() + "/events/" + event.getId() + "/enroll")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + study.getPath() + "/events/" + event.getId()));

        Account cheeolee = accountRepository.findByNickname("CHEEOLEE");
        isNotAccepted(cheeolee, event);
    }

    private void isNotAccepted(Account account, Event event) {
        assertFalse(enrollmentRepository.findByEventAndAccount(event, account).isAccepted());
    }

    private void isAccepted(Account account, Event event) {
        assertTrue(enrollmentRepository.findByEventAndAccount(event, account).isAccepted());
    }

    private Event createEvent(String eventTitle, EventType eventType, int limit, Study study, Account account) {
        Event event = new Event();
        event.setEventType(eventType);
        event.setLimitOfEnrollments(limit);
        event.setTitle(eventTitle);
        event.setCreatedDateTime(LocalDateTime.now());
        event.setEndEnrollmentDateTime(LocalDateTime.now().plusDays(1));
        event.setStartDateTime(LocalDateTime.now().plusDays(1).plusHours(5));
        event.setEndDateTime(LocalDateTime.now().plusDays(1).plusHours(7));
        return eventService.createEvent(event, study, account);
    }

}