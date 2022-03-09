package com.analise.dados.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Vendedor {

    private Long id;
    private String cpf;
    private String name;
    private Double salary;
}
