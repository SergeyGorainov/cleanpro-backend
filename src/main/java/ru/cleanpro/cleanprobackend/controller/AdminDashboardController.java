package ru.cleanpro.cleanprobackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.cleanpro.cleanprobackend.model.OrderEntity;
import ru.cleanpro.cleanprobackend.service.DashboardService;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final DashboardService dashboardService;

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @GetMapping
    public String dashboard(Model model) {
        Map<String, Object> stats = dashboardService.buildDashboardStats();
        model.addAllAttributes(stats);
        return "admin/dashboard";
    }

    @GetMapping("/export/orders.csv")
    public ResponseEntity<byte[]> exportOrdersCsv() {
        List<OrderEntity> orders = dashboardService.getAllOrdersForExport();

        StringBuilder csv = new StringBuilder();
        // BOM для корректной кириллицы в Excel
        csv.append('\uFEFF');
        csv.append("ID;Дата;Имя;Телефон;Адрес;Услуга;Комментарий;Статус\n");

        for (OrderEntity o : orders) {
            csv.append(o.getId()).append(';')
                    .append(o.getCreatedAt() != null ? o.getCreatedAt().format(DATE_FMT) : "").append(';')
                    .append(escapeCsv(o.getName())).append(';')
                    .append(escapeCsv(o.getPhone())).append(';')
                    .append(escapeCsv(o.getAddress())).append(';')
                    .append(escapeCsv(o.getService() != null ? o.getService().getTitle() : "—")).append(';')
                    .append(escapeCsv(o.getComment())).append(';')
                    .append(escapeCsv(o.getStatus())).append('\n');
        }

        byte[] bytes = csv.toString().getBytes(StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));
        headers.setContentDispositionFormData("attachment", "orders.csv");

        return ResponseEntity.ok().headers(headers).body(bytes);
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        return value.replace(";", ",").replace("\n", " ").replace("\r", " ");
    }
}