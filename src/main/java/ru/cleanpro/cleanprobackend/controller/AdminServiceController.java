package ru.cleanpro.cleanprobackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.cleanpro.cleanprobackend.model.ServiceEntity;
import ru.cleanpro.cleanprobackend.service.ServiceService;

@Controller
@RequestMapping("/admin/services")
@RequiredArgsConstructor
public class AdminServiceController {

    private final ServiceService serviceService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("services", serviceService.getAllServices());
        return "admin/services/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("service", new ServiceEntity());
        model.addAttribute("categories", serviceService.getAllCategories());
        model.addAttribute("isNew", true);
        return "admin/services/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("service", serviceService.getById(id));
        model.addAttribute("categories", serviceService.getAllCategories());
        model.addAttribute("isNew", false);
        return "admin/services/form";
    }

    @PostMapping("/save")
    public String save(@RequestParam(value = "categoryId", required = false) Long categoryId,
                       @ModelAttribute ServiceEntity service) {
        if (categoryId != null) {
            service.setCategory(serviceService.getCategoryById(categoryId));
        }
        serviceService.save(service);
        return "redirect:/admin/services";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        serviceService.delete(id);
        return "redirect:/admin/services";
    }

    @PostMapping("/{id}/toggle")
    public String toggle(@PathVariable Long id) {
        ServiceEntity service = serviceService.getById(id);
        service.setActive(!service.getActive());
        serviceService.save(service);
        return "redirect:/admin/services";
    }
}