package com.hersac.fastfoot.repositories;

import com.hersac.fastfoot.config.Repositorio;
import com.hersac.fastfoot.models.Clientes;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


public class ClientesRepositorio extends Repositorio<Clientes, Long> {
    
    public ClientesRepositorio(Class<Clientes> claseEntidad) {
        super(claseEntidad);
    }

    @Override
    public Clientes mapearResultSetAEntidad(ResultSet resultSet) throws SQLException {
        Clientes cliente = new Clientes();
        cliente.setClientesId(resultSet.getLong("clientesid"));
        cliente.setNombre(resultSet.getString("nombre"));
        cliente.setNumeroDocumento(resultSet.getString("numerodocumento"));

        return cliente;
    }
    
    public Clientes findByNumeroDocumento(String numeroDocumento) {
        return findBy("numerodocumento", numeroDocumento);
    }
}
