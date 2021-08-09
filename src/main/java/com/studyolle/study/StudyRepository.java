package com.studyolle.study;

import com.studyolle.domain.Study;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyRepository extends JpaRepository<Study, Long> {

    boolean existsByPath(String path);

    @EntityGraph(attributePaths = { "tags", "zones", "members", "managers" })
    Study findByPath(String path);

}
