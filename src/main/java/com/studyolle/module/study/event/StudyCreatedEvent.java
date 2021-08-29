package com.studyolle.module.study.event;

import com.studyolle.module.study.Study;
import lombok.Getter;

@Getter
public class StudyCreatedEvent {

    private Study study;

    public StudyCreatedEvent(Study study) {
        this.study = study;
    }

}
