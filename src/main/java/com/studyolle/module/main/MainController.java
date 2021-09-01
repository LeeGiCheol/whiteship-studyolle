package com.studyolle.module.main;

import com.studyolle.module.account.Account;
import com.studyolle.module.account.AccountRepository;
import com.studyolle.module.account.CurrentAccount;
import com.studyolle.module.event.Enrollment;
import com.studyolle.module.event.EnrollmentRepository;
import com.studyolle.module.study.Study;
import com.studyolle.module.study.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final AccountRepository accountRepository;
    private final StudyRepository studyRepository;
    private final EnrollmentRepository enrollmentRepository;



    @GetMapping("/")
    public String home(@CurrentAccount Account account, Model model) {
        if (account != null) {
            Account findAccount = accountRepository.findTagsAndZonesById(account.getId());
            List<Enrollment> enrollmentList = enrollmentRepository.findByAccountAndAcceptedOrderByEnrolledAtDesc(findAccount, true);
            List<Study> studyList = studyRepository.findByAccount(findAccount.getTags(), findAccount.getZones());
            List<Study> studyManagerOf = studyRepository.findTop5ByManagersContainingAndClosedOrderByPublishedDateTimeDesc(findAccount, false);
            List<Study> studyMemberOf = studyRepository.findTop5ByMembersContainingAndClosedOrderByPublishedDateTimeDesc(findAccount, false);

            model.addAttribute("account", findAccount);
            model.addAttribute("enrollmentList", enrollmentList);
            model.addAttribute("studyList", studyList);
            model.addAttribute("studyManagerOf", studyManagerOf);
            model.addAttribute("studyMemberOf", studyMemberOf);

            return "index-after-login";
        }

        List<Study> studyList = studyRepository.findTop9ByPublishedAndClosedOrderByPublishedDateTimeDesc(true, false);
        model.addAttribute(studyList);

        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/search/study")
    public String searchStudy(String keyword, Model model,
            @PageableDefault(size = 9, sort = "publishedDateTime", direction = Sort.Direction.DESC)
                    Pageable pageable) {
        Page<Study> studyPage = studyRepository.findByKeyword(pageable, keyword);
        model.addAttribute("studyPage", studyPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortProperty", pageable.getSort().toString().contains("publishedDateTime") ? "publishedDateTime" : "memberCount");

        return "search";
    }

}
