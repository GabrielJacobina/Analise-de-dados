package com.analise.dados.models;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Item {

    private Long id;
    private Long quantity;
    private Double price;
}
