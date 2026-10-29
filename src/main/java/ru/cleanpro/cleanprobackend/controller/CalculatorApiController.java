package ru.cleanpro.cleanprobackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.cleanpro.cleanprobackend.service.CalculatorPriceService;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CalculatorApiController {

    private final CalculatorPriceService calculatorPriceService;

    /** GET /api/calculator-prices → {"service_general": 80, "type_house": 1.3, ...} */
    @GetMapping("/calculator-prices")
    public ResponseEntity<Map<String, Double>> getCalculatorPrices() {
        return ResponseEntity.ok(calculatorPriceService.getAsMap());
    }
}