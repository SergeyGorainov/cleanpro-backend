package ru.cleanpro.cleanprobackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.cleanpro.cleanprobackend.dto.UserRegisterDto;
import ru.cleanpro.cleanprobackend.service.UserService;

@Controller
@RequiredArgsConstructor
public class RegisterController {

    private final UserService userService;

    @GetMapping("/register")
    public String showForm(Model model) {
        if (!model.containsAttribute("userForm")) {
            model.addAttribute("userForm", new UserRegisterDto());
        }
        return "register";
    }

    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute("userForm") UserRegisterDto userForm,
            BindingResult bindingResult,
            RedirectAttributes ra,
            Model model) {

        // Совпадение паролей — ручная проверка
        if (userForm.getPassword() != null
                && !userForm.getPassword().equals(userForm.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "error.userForm",
                    "Пароли не совпадают");
        }

        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {
            userService.registerUser(userForm);
        } catch (RuntimeException e) {
            model.addAttribute("registerError", e.getMessage());
            return "register";
        }

        ra.addFlashAttribute("registerSuccess", "Регистрация успешна! Войдите в систему.");
        return "redirect:/login";
    }
}