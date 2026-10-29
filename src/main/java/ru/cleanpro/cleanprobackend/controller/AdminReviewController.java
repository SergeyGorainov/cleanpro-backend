package ru.cleanpro.cleanprobackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.cleanpro.cleanprobackend.model.ReviewEntity;
import ru.cleanpro.cleanprobackend.service.ReviewService;

@Controller
@RequestMapping("/admin/reviews")
@RequiredArgsConstructor
public class AdminReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("reviews", reviewService.getAllReviews());
        return "admin/reviews/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("review", new ReviewEntity());
        model.addAttribute("isNew", true);
        return "admin/reviews/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("review", reviewService.getById(id));
        model.addAttribute("isNew", false);
        return "admin/reviews/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute ReviewEntity review) {
        reviewService.save(review);
        return "redirect:/admin/reviews";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        reviewService.delete(id);
        return "redirect:/admin/reviews";
    }

    @PostMapping("/{id}/toggle")
    public String toggle(@PathVariable Long id) {
        ReviewEntity review = reviewService.getById(id);
        review.setActive(!review.getActive());
        reviewService.save(review);
        return "redirect:/admin/reviews";
    }
}