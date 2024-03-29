package com.studyolle.module.study;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyolle.module.account.CurrentAccount;
import com.studyolle.module.account.Account;
import com.studyolle.module.tag.Tag;
import com.studyolle.module.zone.Zone;
import com.studyolle.module.account.form.TagForm;
import com.studyolle.module.account.form.ZoneForm;
import com.studyolle.module.study.form.StudyDescriptionForm;
import com.studyolle.module.tag.TagService;
import com.studyolle.module.zone.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/study/{path}/settings")
@RequiredArgsConstructor
public class StudySettingController {

    private final StudyService studyService;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;
    private final TagService tagService;
    private final ZoneRepository zoneRepository;


    @GetMapping("/description")
    public String viewStudySetting(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Study study = studyService.getStudyToUpdate(account, path);

        model.addAttribute(account);
        model.addAttribute(study);
        model.addAttribute(modelMapper.map(study, StudyDescriptionForm.class));
        return "study/settings/description";
    }

    @PostMapping("/description")
    public String updateStudyInfo(@CurrentAccount Account account, @PathVariable String path,
                                  @Valid StudyDescriptionForm studyDescriptionForm, Errors errors,
                                  Model model, RedirectAttributes attributes) {

        Study study = studyService.getStudyToUpdate(account, path);

        if (errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(study);

            return "study/settings/description";
        }

        studyService.updateStudyDescription(study, studyDescriptionForm);
        attributes.addFlashAttribute("message", "스터디 소개를 수정했습니다.");

        return "redirect:/study/" + study.getEncodePath() + "/settings/description";
    }

    @GetMapping("/banner")
    public String studyImageForm(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Study study = studyService.getStudyToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(study);

        return "study/settings/banner";
    }

    @PostMapping("/banner")
    public String studyImageSubmit(@CurrentAccount Account account, @PathVariable String path,
                                   String image, RedirectAttributes attributes) {

        Study study = studyService.getStudyToUpdate(account, path);
        studyService.updateStudyImage(study, image);
        attributes.addFlashAttribute("message", "스터디 이미지를 수정했습니다.");

        return "redirect:/study/" + study.getEncodePath() + "/settings/banner";
    }

    @PostMapping("/banner/enable")
    public String enableStudyBanner(@CurrentAccount Account account, @PathVariable String path) {
        Study study = studyService.getStudyToUpdate(account, path);
        studyService.enableStudyBanner(study);

        return "redirect:/study/" + study.getEncodePath() + "/settings/banner";
    }

    @PostMapping("/banner/disable")
    public String disableStudyBanner(@CurrentAccount Account account, @PathVariable String path) {
        Study study = studyService.getStudyToUpdate(account, path);
        studyService.disableStudyBanner(study);

        return "redirect:/study/" + study.getEncodePath() + "/settings/banner";
    }
    
    @GetMapping("/tags")
    public String studyTagsForm(@CurrentAccount Account account, @PathVariable String path, Model model) throws JsonProcessingException {
        Study study = studyService.getStudyToUpdate(account, path);

        List<String> tags = study.getTags().stream()
                .map(Tag::getTitle)
                .collect(Collectors.toList());

        List<String> allTagTitles = tagService.findAll().stream()
                .map(Tag::getTitle)
                .collect(Collectors.toList());

        model.addAttribute(study);
        model.addAttribute(account);
        model.addAttribute("tags", tags);
        model.addAttribute("whiteList", objectMapper.writeValueAsString(allTagTitles));

        return "study/settings/tags";
    }

    @PostMapping("/tags/add")
    public ResponseEntity<?> addTags(@CurrentAccount Account account, @PathVariable String path,
                                  @RequestBody TagForm tagForm) {
        Study study = studyService.getStudyToUpdateTag(account, path);
        Tag tag = tagService.findOrCreateNew(tagForm.getTagTitle());
        studyService.addTag(study, tag);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/tags/remove")
    public ResponseEntity<?> removeTags(@CurrentAccount Account account, @PathVariable String path,
                                  @RequestBody TagForm tagForm) {
        Study study = studyService.getStudyToUpdateTag(account, path);
        Tag tag = tagService.findByTitle(tagForm.getTagTitle());

        if (tag == null) {
            return ResponseEntity.badRequest().build();
        }
        studyService.removeTag(study, tag);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/zones")
    public String studyZonesForm(@CurrentAccount Account account, @PathVariable String path, Model model) throws JsonProcessingException {
        Study study = studyService.getStudyToUpdate(account, path);

        List<String> zones = study.getZones().stream()
                .map(Zone::toString)
                .collect(Collectors.toList());

        List<String> whiteList = zoneRepository.findAll().stream()
                .map(Zone::toString)
                .collect(Collectors.toList());

        model.addAttribute(account);
        model.addAttribute(study);
        model.addAttribute("zones", zones);
        model.addAttribute("whiteList", objectMapper.writeValueAsString(whiteList));

        return "study/settings/zones";
    }

    @PostMapping("/zones/add")
    public ResponseEntity<?> addZone(@CurrentAccount Account account, @PathVariable String path,
                                     @RequestBody ZoneForm zoneForm) {

        Study study = studyService.getStudyToUpdateZone(account, path);
        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvince());

        if (zone == null) {
            return ResponseEntity.badRequest().build();
        }

        studyService.addZone(study, zone);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/zones/remove")
    public ResponseEntity<?> removeZone(@CurrentAccount Account account, @PathVariable String path,
                                     @RequestBody ZoneForm zoneForm) {

        Study study = studyService.getStudyToUpdateZone(account, path);
        Zone zone = zoneRepository.findByCityAndProvince(zoneForm.getCityName(), zoneForm.getProvince());

        if (zone == null) {
            return ResponseEntity.badRequest().build();
        }

        studyService.removeZone(study, zone);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/study")
    public String studyForm(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Study study = studyService.getStudyToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(study);

        return "study/settings/study";
    }

    @PostMapping("/study/publish")
    public String publishStudy(@CurrentAccount Account account, @PathVariable String path,
                                RedirectAttributes attributes) {

        Study study = studyService.getStudyToUpdateStatus(account, path);
        studyService.publish(study);
        attributes.addFlashAttribute("message", "스터디를 공개했습니다.");

        return "redirect:/study/" + study.getEncodePath() + "/settings/study";
    }

    @PostMapping("/study/close")
    public String closeStudy(@CurrentAccount Account account, @PathVariable String path,
                               RedirectAttributes attributes) {

        Study study = studyService.getStudyToUpdateStatus(account, path);
        studyService.close(study);
        attributes.addFlashAttribute("message", "스터디를 종료했습니다.");

        return "redirect:/study/" + study.getEncodePath() + "/settings/study";
    }

    @PostMapping("/recruit/start")
    public String startRecruit(@CurrentAccount Account account, @PathVariable String path,
                              Model model, RedirectAttributes attributes) {

        Study study = studyService.getStudyToUpdateStatus(account, path);

        if (!study.canUpdateRecruiting()) {
            attributes.addFlashAttribute("message", "1시간 안에 인원 모집 설정을 여러번 변경할 수 없습니다.");
            return "redirect:/study/" + study.getEncodePath() + "/settings/study";
        }

        studyService.startRecruit(study);
        attributes.addFlashAttribute("message", "인원 모집을 시작합니다.");
        return "redirect:/study/" + study.getEncodePath() + "/settings/study";
    }

    @PostMapping("/recruit/stop")
    public String stopRecruit(@CurrentAccount Account account, @PathVariable String path,
                               Model model, RedirectAttributes attributes) {

        Study study = studyService.getStudyToUpdateStatus(account, path);

        if (!study.canUpdateRecruiting()) {
            attributes.addFlashAttribute("message", "1시간 안에 인원 모집 설정을 여러번 변경할 수 없습니다.");
            return "redirect:/study/" + study.getEncodePath() + "/settings/study";
        }

        studyService.stopRecruit(study);
        attributes.addFlashAttribute("message", "인원 모집을 종료합니다.");
        return "redirect:/study/" + study.getEncodePath() + "/settings/study";
    }

    @PostMapping("/study/path")
    public String updateStudyPath(@CurrentAccount Account account, @PathVariable String path,
                                  @RequestParam String newPath, Model model, RedirectAttributes attributes) {

        Study study = studyService.getStudyToUpdateStatus(account, path);

        if (!studyService.isValidPath(newPath)) {
            model.addAttribute(account);
            model.addAttribute(study);
            model.addAttribute("studyPathError", "해당 스터디 경로는 사용할 수 없습니다. 다른 값을 입력하세요.");
            return "study/settings/study";
        }

        studyService.updateStudyPath(study, newPath);
        attributes.addFlashAttribute("message", "스터디 경로를 수정했습니다.");

        return "redirect:/study/" + study.getEncodePath() + "/settings/study";
    }

    @PostMapping("/study/title")
    public String updateStudyTitle(@CurrentAccount Account account, @PathVariable String path,
                                   @RequestParam String newTitle, Model model, RedirectAttributes attributes) {

        Study study = studyService.getStudyToUpdateStatus(account, path);

        if (!studyService.isValidTitle(newTitle)) {
            model.addAttribute(account);
            model.addAttribute(study);
            model.addAttribute("studyTitleError", "스터디 이름을 다시 입력하세요.");

            return "study/settings/study";
        }

        studyService.updateStudyTitle(study, newTitle);
        attributes.addFlashAttribute("message", "스터디 이름을 수정했습니다.");

        return "redirect:/study/" + study.getEncodePath() + "/settings/study";
    }

    @PostMapping("/study/remove")
    public String removeStudy(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Study study = studyService.getStudyToUpdateStatus(account, path);
        studyService.remove(study);

        return "redirect:/";
    }

}
