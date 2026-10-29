package ru.cleanpro.cleanprobackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.cleanpro.cleanprobackend.model.FaqEntity;
import ru.cleanpro.cleanprobackend.service.FaqService;

@Controller
@RequestMapping("/admin/faq")
@RequiredArgsConstructor
public class AdminFaqController {

    private final FaqService faqService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("faqs", faqService.getAllFaqs());
        return "admin/faq/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("faq", new FaqEntity());
        model.addAttribute("isNew", true);
        return "admin/faq/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("faq", faqService.getById(id));
        model.addAttribute("isNew", false);
        return "admin/faq/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute FaqEntity faq) {
        faqService.save(faq);
        return "redirect:/admin/faq";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        faqService.delete(id);
        return "redirect:/admin/faq";
    }

    @PostMapping("/{id}/toggle")
    public String toggle(@PathVariable Long id) {
        FaqEntity faq = faqService.getById(id);
        faq.setActive(!faq.getActive());
        faqService.save(faq);
        return "redirect:/admin/faq";
    }
}