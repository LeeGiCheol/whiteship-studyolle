package com.studyolle.settings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyolle.WithAccount;
import com.studyolle.account.AccountRepository;
import com.studyolle.account.AccountService;
import com.studyolle.account.SignUpForm;
import com.studyolle.domain.Account;
import com.studyolle.domain.Tag;
import com.studyolle.settings.form.TagForm;
import com.studyolle.tag.TagRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class SettingsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    TagRepository tagRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void beforeEach() {
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setNickname("LEE");
        signUpForm.setPassword("12345678");
        signUpForm.setEmail("leegicheolgc@gmail.com");
        accountService.processNewAccount(signUpForm);
    }

    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
    }


    @Test
    @DisplayName("프로필 수정 화면 1번 방법")
    @WithUserDetails(value = "LEE", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateProfileForm() throws Exception {
        mockMvc.perform(get(SettingsController.SETTINGS_PROFILE_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"))
        ;
    }


    @Test
    @DisplayName("프로필 수정하기 - 입력값 정상 1번 방법")
    @WithUserDetails(value = "LEE", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateProfile() throws Exception {
        String bio = "짧은 소개를 수정하는 경우";

        mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)
                .param("bio", bio)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTINGS_PROFILE_URL))
                .andExpect(flash().attributeExists("message"))
        ;

        Account lee = accountService.findByNickname("LEE");
        assertEquals(bio, lee.getBio());
    }

    @Test
    @DisplayName("프로필 수정하기 - 입력값 정상 2번 방법")
    @WithAccount("GICHEOL")
    void updateProfile2() throws Exception {
        String bio = "짧은 소개를 수정하는 경우";

        mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)
                .param("bio", bio)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTINGS_PROFILE_URL))
                .andExpect(flash().attributeExists("message"))
        ;

        Account lee = accountService.findByNickname("GICHEOL");
        assertEquals(bio, lee.getBio());
    }


    @Test
    @DisplayName("프로필 수정하기 - 입력값 에러 2번 방법")
    @WithAccount("GICHEOL")
    void updateProfile_error() throws Exception {
        String bio = "길게 소개를 수정하는 경우 길게 소개를 수정하는 경우 길게 소개를 수정하는 경우 길게 소개를 수정하는 경우 길게 소개를 수정하는 경우 길게 소개를 수정하는 경우 길게 소개를 수정하는 경우 길게 소개를 수정하는 경우 길게 소개를 수정하는 경우 길게 소개를 수정하는 경우 길게 소개를 수정하는 경우 길게 소개를 수정하는 경우 길게 소개를 수정하는 경우 ";

        mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)
                .param("bio", bio)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTINGS_PROFILE_VIEW_NAME))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().hasErrors())
        ;

        Account lee = accountService.findByNickname("GICHEOL");
        assertNull(lee.getBio());
    }


    @Test
    @DisplayName("패스워드 수정 폼")
    @WithAccount("GICHEOL")
    void updatePasswordForm() throws Exception {
        mockMvc.perform(get(SettingsController.SETTINGS_PASSWORD_URL)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTINGS_PASSWORD_VIEW_NAME))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("passwordForm"))
        ;
    }

    @Test
    @DisplayName("패스워드 수정 - 입력값 정상")
    @WithAccount("GICHEOL")
    void updatePassword() throws Exception {
        String newPassword = "12345678";

        mockMvc.perform(post(SettingsController.SETTINGS_PASSWORD_URL)
                .param("newPassword", newPassword)
                .param("newPasswordConfirm", newPassword)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(SettingsController.SETTINGS_PASSWORD_URL))
                .andExpect(flash().attributeExists("message"))
        ;

        Account gicheol = accountService.findByNickname("GICHEOL");
        assertTrue(passwordEncoder.matches(newPassword, gicheol.getPassword()));
    }


    @Test
    @DisplayName("패스워드 수정 - 입력값 에러 - 패스워드 불일치")
    @WithAccount("GICHEOL")
    void updatePassword_fail() throws Exception {
        String newPassword = "12345678";
        String newPasswordConfirm = "87654321";

        mockMvc.perform(post(SettingsController.SETTINGS_PASSWORD_URL)
                .param("newPassword", newPassword)
                .param("newPasswordConfirm", newPasswordConfirm)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTINGS_PASSWORD_VIEW_NAME))
                .andExpect(model().hasErrors())
        ;

        Account gicheol = accountService.findByNickname("GICHEOL");
        assertTrue(passwordEncoder.matches(newPassword, gicheol.getPassword()));
    }


    @Test
    @DisplayName("닉네임 수정화면")
    @WithAccount("GICHEOL")
    void updateNicknameForm() throws Exception {
        mockMvc.perform(get(SettingsController.SETTINGS_ACCOUNT_URL)
                    .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("nicknameForm"))
                .andExpect(view().name(SettingsController.SETTINGS_ACCOUNT_VIEW_NAME))
        ;
    }

    @Test
    @DisplayName("닉네임 수정 - 입력값 정상")
    @WithAccount("GICHEOL")
    void updateNickname() throws Exception {
        String nickname = "CHEEOLEE";

        mockMvc.perform(post(SettingsController.SETTINGS_ACCOUNT_URL)
                    .param("nickname", nickname)
                    .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("message"))
                .andExpect(redirectedUrl(SettingsController.SETTINGS_ACCOUNT_URL))
        ;

        Account byNickname = accountService.findByNickname(nickname);
        assertEquals(nickname, byNickname.getNickname());
    }

    @Test
    @DisplayName("닉네임 수정 - 입력값 에러 - 닉네임 길이 5자 이내")
    @WithAccount("GICHEOL")
    void updateNickname_fail() throws Exception {
        String nickname = "LEE";

        mockMvc.perform(post(SettingsController.SETTINGS_ACCOUNT_URL)
                .param("nickname", nickname)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("account"))
                .andExpect(view().name(SettingsController.SETTINGS_ACCOUNT_VIEW_NAME))
        ;
    }

    @WithAccount("CHEEOLEE")
    @DisplayName("태그 수정 폼")
    @Test
    void updateTagsForm() throws Exception {
        mockMvc.perform(get(SettingsController.SETTINGS_TAGS_URL))
                .andExpect(view().name(SettingsController.SETTINGS_TAGS_VIEW_NAME))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("whiteList"))
                .andExpect(model().attributeExists("tags"))
        ;
    }

    @WithAccount("CHEEOLEE")
    @DisplayName("태그 추가")
    @Test
    void addTags() throws Exception {
        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("newTag");

        mockMvc.perform(post(SettingsController.SETTINGS_TAGS_URL + "/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagForm))
                        .with(csrf()))
                .andExpect(status().isOk())
        ;

        Tag newTag = tagRepository.findByTitle("newTag");
        assertNotNull(newTag);
        assertTrue(accountRepository.findByNickname("CHEEOLEE").getTags().contains(newTag));
    }

    @WithAccount("CHEEOLEE")
    @DisplayName("태그 삭제")
    @Test
    void removeTags() throws Exception {
        Account cheeolee = accountRepository.findByNickname("CHEEOLEE");

        TagForm tagForm = new TagForm();
        tagForm.setTagTitle("newTag");

        Tag saveTag = tagRepository.save(Tag.builder().title(tagForm.getTagTitle()).build());
        accountService.addTag(cheeolee, saveTag);

        assertTrue(cheeolee.getTags().contains(saveTag));


        mockMvc.perform(post(SettingsController.SETTINGS_TAGS_URL + "/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagForm))
                        .with(csrf()))
                .andExpect(status().isOk())
        ;

        assertFalse(cheeolee.getTags().contains(saveTag));
    }



}