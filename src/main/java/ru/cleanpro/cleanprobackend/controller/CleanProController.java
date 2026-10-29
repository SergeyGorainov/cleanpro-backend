package ru.cleanpro.cleanprobackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.cleanpro.cleanprobackend.dto.OrderCreateDto;
import ru.cleanpro.cleanprobackend.model.CategoryEntity;
import ru.cleanpro.cleanprobackend.model.OrderEntity;
import ru.cleanpro.cleanprobackend.model.UserEntity;
import ru.cleanpro.cleanprobackend.service.FaqService;
import ru.cleanpro.cleanprobackend.service.OrderService;
import ru.cleanpro.cleanprobackend.service.ReviewService;
import ru.cleanpro.cleanprobackend.service.ServiceService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CleanProController {

    private final OrderService orderService;
    private final ServiceService serviceService;
    private final ReviewService reviewService;
    private final FaqService faqService;

    @GetMapping("/")
    public String index(Model model, @AuthenticationPrincipal UserEntity currentUser) {
        List<CategoryEntity> categories = serviceService.getActiveCategories();
        model.addAttribute("categories", categories);
        model.addAttribute("reviews", reviewService.getActiveReviews());
        model.addAttribute("faqs", faqService.getActiveFaqs());
        model.addAttribute("currentUser", currentUser);
        return "index";
    }

    @GetMapping("/services")
    public String services(Model model) {
        List<CategoryEntity> categories = serviceService.getActiveCategories();
        model.addAttribute("categories", categories);
        return "services/index";
    }

    @GetMapping("/services/{slug}")
    public String serviceBySlug(@PathVariable String slug, Model model) {
        model.addAttribute("service", serviceService.getBySlug(slug));
        model.addAttribute("categories", serviceService.getActiveCategories());
        return "services/detail";
    }

    @GetMapping("/contacts")
    public String contacts() {
        return "contacts";
    }

    @PostMapping("/order")
    public String submitOrder(
            @Valid @ModelAttribute("orderForm") OrderCreateDto orderForm,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserEntity currentUser,
            Model model) {

        // ⚠ Заявки принимаются только от авторизованных
        if (currentUser == null) {
            return "redirect:/login?requireAuth";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            model.addAttribute("categories", serviceService.getActiveCategories());
            model.addAttribute("reviews", reviewService.getActiveReviews());
            model.addAttribute("faqs", faqService.getActiveFaqs());
            model.addAttribute("currentUser", currentUser);
            return "index";
        }

        OrderEntity saved = orderService.createOrder(orderForm, currentUser);
        model.addAttribute("clientName", saved.getName());
        model.addAttribute("orderId", saved.getId());
        return "order-success";
    }
}