package com.analise.dados.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Venda {

    private Long id;
    private Long saleId;
    private List<Item> itens;
    private String salesMan;
}
