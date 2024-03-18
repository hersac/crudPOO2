package com.hersac.fastfoot.repositories;

import com.hersac.fastfoot.config.Repositorio;
import com.hersac.fastfoot.models.Menu;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MenuRepositorio extends Repositorio<Menu, Long> {
    @Override
    public Menu mapearResultSetAEntidad(ResultSet resultSet) throws SQLException {
        Menu menu = new Menu();
        menu.setMenuId(resultSet.getLong("menuid"));
        menu.setNombre(resultSet.getString("nombre"));
        menu.setPrecio(resultSet.getDouble("precio"));
        menu.setImagen(resultSet.getString("imagen"));
        menu.setDisponible(resultSet.getBoolean("disponible"));
        return menu;
    }
}
