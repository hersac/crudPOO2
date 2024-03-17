package com.hersac.fastfoot.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pedido {
    private Long pedidoId;
    private Clientes cliente;
    private Menu menu;
    private Integer cantidad;
    private Boolean estado;
}
