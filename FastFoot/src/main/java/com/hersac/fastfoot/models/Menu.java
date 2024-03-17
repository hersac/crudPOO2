package com.hersac.fastfoot.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Menu {
    private Long menuId;
    private String nombre;
    private String precio;
    private String imagen;
    private Boolean disponible;
}
