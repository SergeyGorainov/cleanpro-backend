package ru.cleanpro.cleanprobackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.cleanpro.cleanprobackend.model.CategoryEntity;
import ru.cleanpro.cleanprobackend.service.ServiceService;

@Controller
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final ServiceService serviceService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("categories", serviceService.getAllCategories());
        return "admin/categories/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("category", new CategoryEntity());
        model.addAttribute("isNew", true);
        return "admin/categories/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("category", serviceService.getCategoryById(id));
        model.addAttribute("isNew", false);
        return "admin/categories/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute CategoryEntity category) {
        serviceService.saveCategory(category);
        return "redirect:/admin/categories";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        serviceService.deleteCategory(id);
        return "redirect:/admin/categories";
    }

    @PostMapping("/{id}/toggle")
    public String toggle(@PathVariable Long id) {
        CategoryEntity category = serviceService.getCategoryById(id);
        category.setActive(!category.getActive());
        serviceService.saveCategory(category);
        return "redirect:/admin/categories";
    }
}