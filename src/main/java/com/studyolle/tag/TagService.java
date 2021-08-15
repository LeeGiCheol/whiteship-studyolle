package com.studyolle.tag;

import com.studyolle.domain.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;


    public List<Tag> findAll() {
        return tagRepository.findAll();
    }

    public Tag findOrCreateNew(String tagTitle) {
        Tag tag = findByTitle(tagTitle);

        if (tag == null) {
            tag = tagRepository.save(Tag.builder().title(tagTitle).build());
        }

        return tag;
    }

    public Tag findByTitle(String title) {
        return tagRepository.findByTitle(title);
    }
}
