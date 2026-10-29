package ru.cleanpro.cleanprobackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.cleanpro.cleanprobackend.dto.UserEditDto;
import ru.cleanpro.cleanprobackend.model.UserEntity;
import ru.cleanpro.cleanprobackend.service.UserService;

@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    public String list(@RequestParam(required = false) String role,
                       @AuthenticationPrincipal UserEntity currentUser,
                       Model model) {

        model.addAttribute("users",
                (role == null || role.isBlank())
                        ? userService.getAllUsersForAdmin()
                        : userService.getUsersByRole(role));

        model.addAttribute("currentRole", role);
        model.addAttribute("currentUserId", currentUser.getId());
        model.addAttribute("adminsCount", userService.countAdmins());
        model.addAttribute("usersCount", userService.countUsers());
        model.addAttribute("employeesCount", userService.countEmployees());

        return "admin/users/list";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        UserEntity user = userService.getById(id);

        UserEditDto dto = UserEditDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();

        model.addAttribute("userForm", dto);
        model.addAttribute("editUserId", id);
        return "admin/users/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("userForm") UserEditDto dto,
                         BindingResult bindingResult,
                         @AuthenticationPrincipal UserEntity currentUser,
                         Model model,
                         RedirectAttributes ra) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("editUserId", id);
            return "admin/users/form";
        }

        // Защита: нельзя снять с себя роль ADMIN
        if (id.equals(currentUser.getId())
                && !"ADMIN".equals(dto.getRole())) {
            model.addAttribute("editUserId", id);
            model.addAttribute("errorMessage", "Нельзя снять с себя роль администратора");
            return "admin/users/form";
        }

        try {
            userService.updateUser(id, dto);
            ra.addFlashAttribute("successMessage", "Данные пользователя обновлены");
        } catch (RuntimeException e) {
            model.addAttribute("editUserId", id);
            model.addAttribute("errorMessage", e.getMessage());
            return "admin/users/form";
        }

        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id,
                         @AuthenticationPrincipal UserEntity currentUser,
                         RedirectAttributes ra) {
        try {
            userService.deleteUser(id, currentUser.getId());
            ra.addFlashAttribute("successMessage", "Пользователь удалён");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/toggle-role")
    public String toggleRole(@PathVariable Long id,
                             @AuthenticationPrincipal UserEntity currentUser,
                             RedirectAttributes ra) {
        try {
            UserEntity user = userService.getById(id);
            if (id.equals(currentUser.getId())) {
                ra.addFlashAttribute("errorMessage", "Нельзя менять свою роль");
                return "redirect:/admin/users";
            }

            String newRole = "ADMIN".equals(user.getRole()) ? "USER" : "ADMIN";
            UserEditDto dto = UserEditDto.builder()
                    .id(user.getId())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .email(user.getEmail())
                    .role(newRole)
                    .build();
            userService.updateUser(id, dto);
            ra.addFlashAttribute("successMessage",
                    "Роль изменена на " + newRole);
        } catch (RuntimeException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/reset-password")
    @PreAuthorize("hasRole('ADMIN')")
    public String resetPassword(@PathVariable Long id, RedirectAttributes ra) {
        String tempPassword = userService.resetPassword(id);
        ra.addFlashAttribute("successMessage",
                "Новый пароль: " + tempPassword + " — передайте пользователю!");
        return "redirect:/admin/users";
    }
}