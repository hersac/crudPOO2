package com.hersac.fastfoot.services;

import com.hersac.fastfoot.models.Clientes;
import com.hersac.fastfoot.repositories.ClientesRepositorio;
import java.util.List;

public class ClientesService {

    private ClientesRepositorio clientesRepo = new ClientesRepositorio(Clientes.class);

    public List<Clientes> getClientes() {
        return clientesRepo.findAll();
    }

    public Clientes getClienteById(Long id) {
        return clientesRepo.findById(id);
    }

    public Clientes addCliente(Clientes cliente) {
        return clientesRepo.save(cliente);
    }

    public void updateCliente(Long id, Clientes cliente) {
        clientesRepo.update(id, cliente);
    }

    public void deleteClienteById(Long id) {
        clientesRepo.deleteById(id);
    }

    public Clientes findClientesByIdentificacion(String identificacion) {
        return this.clientesRepo.findByNumeroDocumento(identificacion);
    }
}
