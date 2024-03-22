package com.hersac.fastfoot.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class Conexion {
    private Connection conexion;
    
    public Conexion(){
        try {
            Class.forName("org.postgresql.Driver");
            
            //Se hace la conexion
            String url = "jdbc:postgresql://localhost:5432/fastfoot";
            String usuario = "heri";
            String contraseña = "Heriberto1995**";
            conexion = DriverManager.getConnection(url, usuario, contraseña);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
    
    public Statement createStatement() throws SQLException {
        if(conexion != null){
            return conexion .createStatement();
        } else {
            throw new SQLException("La conexion no esta disponible");
        }
    }
    
    public PreparedStatement prepareStatement(String query) throws SQLException {
        return conexion.prepareStatement(query);
    }
}
