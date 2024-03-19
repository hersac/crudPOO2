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
    
    private Integer cantidadMenu1;
    private Integer cantidadMenu2;
    private Integer cantidadMenu3;
    private Integer cantidadMenu4;
    private Boolean estado;
}
