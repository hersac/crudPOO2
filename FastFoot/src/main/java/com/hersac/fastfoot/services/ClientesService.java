package com.hersac.fastfoot.services;

import com.hersac.fastfoot.models.Clientes;
import com.hersac.fastfoot.repositories.ClientesRepositorio;
import java.util.List;

public class ClientesService {
    
    private ClientesRepositorio clientesRepo = new ClientesRepositorio();;
    
    public List<Clientes> getClientes(){
        return this.clientesRepo.findAll();
    }
    
    public Clientes getClienteById(Long id){
        return this.clientesRepo.findById(id);
    }
    
    public Clientes addClienteById(Clientes cliente){
        return this.clientesRepo.save(cliente);
    }
    
    public Clientes updateCliente(Long id, Clientes cliente){
        return this.clientesRepo.update(id, cliente);
    }
    
    public void deleteClienteById(Long id){
        this.clientesRepo.deleteById(id);
    }
    
    public Clientes findClientesByIdentificacion(String identificacion){
        return this.clientesRepo.findByNumeroDocumento(identificacion);
    }
}
