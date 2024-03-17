
package com.hersac.fastfoot.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Clientes {
    private Long clienteId;
    private String nombre;
    private String numeroDocumento;
}
