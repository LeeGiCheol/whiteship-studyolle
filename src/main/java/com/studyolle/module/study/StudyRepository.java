package com.studyolle.module.study;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import static org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.FETCH;
import static org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.LOAD;

public interface StudyRepository extends JpaRepository<Study, Long>, StudyRepositoryExtension {

    boolean existsByPath(String path);

    @EntityGraph(attributePaths = { "tags", "zones", "members", "managers" }, type = LOAD)
    Study findByPath(String path);

    @EntityGraph(attributePaths = { "tags", "managers" }, type = FETCH)
    Study findStudyWithTagsByPath(String path);

    @EntityGraph(attributePaths = { "zones", "managers" }, type = FETCH)
    Study findStudyWithZonesByPath(String path);

    @EntityGraph(attributePaths = "managers", type = FETCH)
    Study findStudyWithManagersByPath(String path);

    @EntityGraph(attributePaths = "members", type = FETCH)
    Study findStudyWithMembersByPath(String path);

    Study findStudyOnlyByPath(String path);

    @EntityGraph(attributePaths = { "tags", "zones" }, type = FETCH)
    Study findStudyWithTagsAndZonesById(Long id);

    @EntityGraph(attributePaths = { "managers", "members" }, type = FETCH)
    Study findStudyWithManagersAndMembersById(Long id);

}
