package com.studyolle.event;

import com.studyolle.domain.Event;
import com.studyolle.domain.Study;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    @EntityGraph(attributePaths = "enrollments", type = EntityGraph.EntityGraphType.FETCH)
    List<Event> findByStudyOrderByStartDateTime(Study study);

}
