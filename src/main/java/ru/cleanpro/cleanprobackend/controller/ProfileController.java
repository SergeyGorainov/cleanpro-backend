package ru.cleanpro.cleanprobackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.cleanpro.cleanprobackend.dto.PasswordChangeDto;
import ru.cleanpro.cleanprobackend.model.UserEntity;
import ru.cleanpro.cleanprobackend.service.DashboardService;
import ru.cleanpro.cleanprobackend.service.OrderService;
import ru.cleanpro.cleanprobackend.service.UserService;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final OrderService orderService;
    private final DashboardService dashboardService;
    private final UserService userService;

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal UserEntity currentUser, Model model) {
        if (currentUser == null) {
            return "redirect:/login";
        }

        if (currentUser.isAdmin()) {
            model.addAttribute("user", currentUser);
            model.addAllAttributes(dashboardService.buildDashboardStats());
            return "profile-admin";
        }

        model.addAttribute("user", currentUser);
        model.addAttribute("orders", orderService.getOrdersByUser(currentUser));
        return "profile";
    }

    // Форма смены пароля
    @GetMapping("/profile/change-password")
    public String changePasswordForm(@AuthenticationPrincipal UserEntity currentUser,
                                     Model model) {
        if (currentUser == null) {
            return "redirect:/login";
        }
        model.addAttribute("passwordForm", new PasswordChangeDto());
        model.addAttribute("user", currentUser);
        return "change-password";
    }

    // Обработка смены пароля
    @PostMapping("/profile/change-password")
    public String changePassword(@AuthenticationPrincipal UserEntity currentUser,
                                 @Valid @ModelAttribute("passwordForm") PasswordChangeDto dto,
                                 BindingResult bindingResult,
                                 Model model,
                                 RedirectAttributes ra) {

        if (currentUser == null) {
            return "redirect:/login";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("user", currentUser);
            return "change-password";
        }

        try {
            userService.changePassword(currentUser.getId(), dto);
            ra.addFlashAttribute("successMessage", "Пароль успешно изменён");
            return "redirect:/profile";
        } catch (RuntimeException e) {
            model.addAttribute("user", currentUser);
            model.addAttribute("errorMessage", e.getMessage());
            return "change-password";
        }
    }
}