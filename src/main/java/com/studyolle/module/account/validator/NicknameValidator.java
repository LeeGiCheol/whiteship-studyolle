package com.studyolle.module.account.validator;

import com.studyolle.module.account.AccountRepository;
import com.studyolle.module.account.Account;
import com.studyolle.module.account.form.NicknameForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class NicknameValidator implements Validator {

    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> aClass) {
        return NicknameForm.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        NicknameForm nicknameForm = (NicknameForm) o;
        Account byNickname = accountRepository.findByNickname(nicknameForm.getNickname());

        if (byNickname != null) {
            errors.rejectValue("nickname", "wrong.value", "입력하신 닉네임을 사용할 수 없습니다.");
        }
    }
}
