package com.hersac.fastfoot.repositories;

import com.hersac.fastfoot.config.Repositorio;
import com.hersac.fastfoot.models.Clientes;
import com.hersac.fastfoot.models.Menu;
 import com.hersac.fastfoot.models.Pedido;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PedidoRepositorio extends Repositorio<Pedido, Long> {
    public PedidoRepositorio(Class<Pedido> claseEntidad) {
        super(claseEntidad);
    }

    @Override
    public Pedido mapearResultSetAEntidad(ResultSet resultSet) throws SQLException {
        Pedido pedido = new Pedido();
        pedido.setPedidoId(resultSet.getLong("pedidoid"));

        Clientes cliente = cargarClienteDesdeResultSet(resultSet);
        pedido.setClientesId(cliente);
        
        pedido.setCantidadMenu1(resultSet.getInt("cantidadmenu1"));
        pedido.setCantidadMenu2(resultSet.getInt("cantidadmenu2"));
        pedido.setCantidadMenu3(resultSet.getInt("cantidadmenu3"));
        pedido.setCantidadMenu4(resultSet.getInt("cantidadmenu4"));
        pedido.setEstado(resultSet.getBoolean("estado"));
        return pedido;
    }
    
    private Clientes cargarClienteDesdeResultSet(ResultSet resultSet) throws SQLException {
        Clientes cliente = new Clientes();
        cliente.setClientesId(resultSet.getLong("clientesid"));
        cliente.setNombre(resultSet.getString("nombre"));
        cliente.setNumeroDocumento(resultSet.getString("numerodocumento"));
        return cliente;
    }
}
