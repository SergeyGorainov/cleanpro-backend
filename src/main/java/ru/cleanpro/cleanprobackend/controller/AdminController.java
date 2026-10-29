package ru.cleanpro.cleanprobackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.cleanpro.cleanprobackend.model.OrderEntity;
import ru.cleanpro.cleanprobackend.service.OrderService;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final OrderService orderService;

    // ===== Заявки — доступно ADMIN и EMPLOYEE =====

    @GetMapping("/orders")
    public String orders(
            @RequestParam(value = "status", required = false) String status,
            Model model) {

        List<OrderEntity> orders = (status == null || status.isBlank())
                ? orderService.getAllOrders()
                : orderService.getOrdersByStatus(status);

        model.addAttribute("orders", orders);
        model.addAttribute("currentStatus", status);
        model.addAttribute("newCount", orderService.countNewOrders());
        return "admin/orders";
    }

    // ===== Смена статуса — доступно ADMIN и EMPLOYEE =====

    @PostMapping("/orders/{id}/status")
    public String updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        orderService.updateStatus(id, status);
        return "redirect:/admin/orders";
    }

    // ===== Удаление — только ADMIN =====

    @PostMapping("/orders/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return "redirect:/admin/orders";
    }
}