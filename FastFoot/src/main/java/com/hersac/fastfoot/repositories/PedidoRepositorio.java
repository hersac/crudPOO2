package com.hersac.fastfoot.repositories;

import com.hersac.fastfoot.config.Repositorio;
import com.hersac.fastfoot.models.Clientes;
import com.hersac.fastfoot.models.Menu;
 import com.hersac.fastfoot.models.Pedido;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PedidoRepositorio extends Repositorio<Pedido, Long> {
    @Override
    public Pedido mapearResultSetAEntidad(ResultSet resultSet) throws SQLException {
        Pedido pedido = new Pedido();
        pedido.setPedidoId(resultSet.getLong("pedidoid"));

        Clientes cliente = cargarClienteDesdeResultSet(resultSet);
        pedido.setCliente(cliente);

        Menu menu = cargarMenuDesdeResultSet(resultSet);
        pedido.setMenu(menu);
        
        pedido.setCantidad(resultSet.getInt("cantidad"));
        pedido.setEstado(resultSet.getBoolean("estado"));
        return pedido;
    }
    
    private Clientes cargarClienteDesdeResultSet(ResultSet resultSet) throws SQLException {
        Clientes cliente = new Clientes();
        cliente.setClienteId(resultSet.getLong("clienteid"));
        cliente.setNombre(resultSet.getString("nombre"));
        cliente.setNumeroDocumento(resultSet.getString("numerodocumento"));
        return cliente;
    }
    
    private Menu cargarMenuDesdeResultSet(ResultSet resultSet) throws SQLException {
        Menu menu = new Menu();
        menu.setMenuId(resultSet.getLong("menuid"));
        menu.setNombre(resultSet.getString("nombre"));
        menu.setPrecio(resultSet.getDouble("precio"));
        menu.setImagen(resultSet.getString("imagen"));
        menu.setDisponible(resultSet.getBoolean("disponible"));
        return menu;
    }
}
