package ru.cleanpro.cleanprobackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.cleanpro.cleanprobackend.model.CalculatorPriceEntity;
import ru.cleanpro.cleanprobackend.service.CalculatorPriceService;

import java.math.BigDecimal;

@Controller
@RequestMapping("/admin/calculator")
@RequiredArgsConstructor
public class AdminCalculatorController {

    private final CalculatorPriceService calculatorPriceService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("prices", calculatorPriceService.getAll());
        return "admin/calculator/list";
    }

    @PostMapping("/save")
    public String saveAll(@RequestParam java.util.Map<String, String> params) {
        // Все параметры формы приходят как code=value
        for (var entry : params.entrySet()) {
            String code = entry.getKey();
            if (!code.startsWith("price_")) continue;

            String realCode = code.substring("price_".length());
            String valueStr = entry.getValue();

            try {
                BigDecimal value = new BigDecimal(valueStr);
                calculatorPriceService.getAll().stream()
                        .filter(p -> p.getCode().equals(realCode))
                        .findFirst()
                        .ifPresent(p -> {
                            p.setValue(value);
                            calculatorPriceService.save(p);
                        });
            } catch (NumberFormatException ignored) {
                // пропускаем некорректные значения
            }
        }
        return "redirect:/admin/calculator";
    }
}