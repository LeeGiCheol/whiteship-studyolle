package com.studyolle.event;

import com.studyolle.domain.Account;
import com.studyolle.domain.Event;
import com.studyolle.domain.Study;
import com.studyolle.event.form.EventForm;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;


    public Event createEvent(Event event, Study study, Account account) {
        event.setCreatedBy(account);
        event.setCreatedDateTime(LocalDateTime.now());
        event.setStudy(study);

        return eventRepository.save(event);
    }

    public Optional<Event> findById(Long id) {
        return eventRepository.findById(id);
    }

    public List<Event> findByStudyOrderByStartDateTime(Study study) {
        return eventRepository.findByStudyOrderByStartDateTime(study);
    }

    public void updateEvent(EventForm eventForm, Event event) {
        modelMapper.map(eventForm, event);

        // TODO 모집 인원이 늘어났다면, 자동으로 추가된 인원의 참가 신청을 확정으로 변경해야 한다.
    }
}
