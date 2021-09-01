package com.studyolle.module.study;

import com.studyolle.module.tag.Tag;
import com.studyolle.module.zone.Zone;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Transactional(readOnly = true)
public interface StudyRepositoryExtension {

    Page<Study> findByKeyword(Pageable pageable, String keyword);

    List<Study> findByAccount(Set<Tag> tags, Set<Zone> zones);
}
