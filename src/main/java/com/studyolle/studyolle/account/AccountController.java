package com.studyolle.studyolle.account;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccountController {

    @GetMapping("/sign-up")
    public String signUp(SignUpForm signUpForm, Model model) {
        model.addAttribute("signUpForm", signUpForm);
        return "account/sign-up";
    }

}
