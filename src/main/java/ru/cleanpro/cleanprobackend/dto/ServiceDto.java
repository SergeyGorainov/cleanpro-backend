package ru.cleanpro.cleanprobackend.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceDto {
    private Long id;
    private String title;
    private String description;
    private int price;
}