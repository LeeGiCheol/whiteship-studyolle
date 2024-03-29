package com.studyolle.module.study;

import com.studyolle.WithAccount;
import com.studyolle.infra.AbstractContainerBaseTest;
import com.studyolle.infra.MockMvcTest;
import com.studyolle.module.account.Account;
import com.studyolle.module.account.AccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcTest
class StudyControllerTest extends AbstractContainerBaseTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    StudyService studyService;

    @Autowired
    AccountRepository accountRepository;


    @Test
    @WithAccount("CHEEOLEE")
    @DisplayName("스터디 개설 폼 조회")
    void createStudyForm() throws Exception {
        mockMvc.perform(get("/new-study"))
                .andExpect(status().isOk())
                .andExpect(view().name("study/form"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("studyForm"))
        ;
    }


    @Test
    @WithAccount("CHEEOLEE")
    @DisplayName("스터디 개설 완료")
    void createStudy() throws Exception {
        mockMvc.perform(post("/new-study")
                .param("path", "test-path")
                .param("title", "study title")
                .param("shortDescription", "short Description")
                .param("fullDescription", "fullDescription")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/test-path"))
        ;

        Study byPath = studyService.findByPath("test-path");
        assertNotNull(byPath);

        Account cheeolee = accountRepository.findByNickname("CHEEOLEE");
        assertTrue(byPath.getManagers().contains(cheeolee));
    }


    @Test
    @WithAccount("CHEEOLEE")
    @DisplayName("스터디 개설 실패")
    void createStudy_fail() throws Exception {
        mockMvc.perform(post("/new-study")
                .param("path", "wrong path")
                .param("title", "study title")
                .param("shortDescription", "short Description")
                .param("fullDescription", "fullDescription")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("study/form"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("studyForm"))
        ;

        Study wrong_path = studyService.findByPath("wrong path");
        assertNull(wrong_path);
    }


    @Test
    @WithAccount("CHEEOLEE")
    @DisplayName("스터디 조회")
    void viewStudy() throws Exception {
        Study study = new Study();
        study.setPath("test-path");
        study.setTitle("test study");
        study.setShortDescription("short description");
        study.setFullDescription("<p>full description</p>");

        Account cheeolee = accountRepository.findByNickname("CHEEOLEE");
        studyService.createNewStudy(study, cheeolee);

        mockMvc.perform(get("/study/test-path"))
                .andExpect(view().name("study/view"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("study"))
        ;
    }

}