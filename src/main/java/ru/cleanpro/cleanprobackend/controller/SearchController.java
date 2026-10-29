package ru.cleanpro.cleanprobackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.cleanpro.cleanprobackend.service.ServiceService;

@Controller
@RequiredArgsConstructor
public class SearchController {

    private final ServiceService serviceService;

    @GetMapping("/search")
    public String search(@RequestParam(value = "q", required = false) String query,
                         Model model) {
        model.addAttribute("query", query);
        model.addAttribute("results", serviceService.searchServices(query));
        return "search";
    }
}