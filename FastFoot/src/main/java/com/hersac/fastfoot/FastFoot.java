package com.hersac.fastfoot;

import com.hersac.fastfoot.models.Clientes;
import com.hersac.fastfoot.services.ClientesService;

public class FastFoot {
    
    public static void main(String[] args) {
        ClientesService clientesService = new ClientesService();
        Clientes nuevoCliente = new Clientes();
        nuevoCliente.setNombre("Lonnie");
        nuevoCliente.setNumeroDocumento("1075245465");
        System.out.println("Desde aqui: " + clientesService.addClienteById(nuevoCliente));
    }
}
