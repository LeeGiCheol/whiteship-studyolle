package com.studyolle.study;

import com.studyolle.domain.Study;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyRepository extends JpaRepository<Study, Long> {

    boolean existsByPath(String path);

    @EntityGraph(attributePaths = { "tags", "zones", "members", "managers" }, type = EntityGraph.EntityGraphType.LOAD)
    Study findByPath(String path);

    @EntityGraph(attributePaths = { "tags", "managers" }, type = EntityGraph.EntityGraphType.FETCH)
    Study findAccountWithTagsByPath(String path);

    @EntityGraph(attributePaths = { "zones", "managers" }, type = EntityGraph.EntityGraphType.FETCH)
    Study findAccountWithZonesByPath(String path);

}
