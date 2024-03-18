package com.hersac.fastfoot.models;

import com.hersac.fastfoot.config.RelacionEntidad;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pedido {
    private Long pedidoId;
    
    @RelacionEntidad(nombreTabla = "Clientes", nombreCampoClave = "clienteId")
    private Clientes cliente;
    
    @RelacionEntidad(nombreTabla = "Menu", nombreCampoClave = "menuId")
    private Menu menu;
    
    private Integer cantidad;
    private Boolean estado;
}
