package com.hersac.fastfoot.config;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public abstract class Repositorio<T, DATO> {
    private Conexion conn;
    private String nombreTabla;
    private Statement statement;
    
    public Repositorio(){
        this.conn = new Conexion(); // Inicializa la conexión
        this.nombreTabla = obtenerNombreTabla();
        
        try {
            this.statement = conn.createStatement();
            String query = generarQueryCreacionTabla();
            this.statement.execute(query);
            
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
    
    private String obtenerNombreTabla() {
        ParameterizedType superClass = (ParameterizedType) getClass().getGenericSuperclass();
        @SuppressWarnings("unchecked")
        Class<T> tipoEntidad = (Class<T>) superClass.getActualTypeArguments()[0]; // Corregido aquí
        return tipoEntidad.getSimpleName().toLowerCase();
    }
    
    private String generarQueryCreacionTabla() {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("CREATE TABLE IF NOT EXISTS ").append(nombreTabla).append(" (");

        Field[] fields = obtenerCamposClase();
        for (Field field : fields) {
            String nombreCampo = field.getName();
            Class<?> tipoCampo = field.getType();
            queryBuilder.append(nombreCampo).append(" ").append(obtenerTipoBaseDatos(tipoCampo)).append(", ");
        }
        queryBuilder.delete(queryBuilder.length() - 2, queryBuilder.length()); // Eliminar la última coma
        queryBuilder.append(")");

        return queryBuilder.toString();
    }

    private Field[] obtenerCamposClase() {
        ParameterizedType superClass = (ParameterizedType) getClass().getGenericSuperclass();
        return ((Class<T>) superClass.getActualTypeArguments()[0]).getDeclaredFields();
    }

    private String obtenerTipoBaseDatos(Class<?> tipoCampo) {
        if (tipoCampo == int.class || tipoCampo == Integer.class || tipoCampo == long.class || tipoCampo == Long.class) {
            return "SERIAL PRIMARY KEY";
        } else if (tipoCampo == long.class || tipoCampo == Long.class) {
            return "BIGINT";
        } else if (tipoCampo == String.class) {
            return "VARCHAR(255)";
        }
        // Agrega más tipos de datos según sea necesario

        throw new IllegalArgumentException("Tipo de campo no compatible: " + tipoCampo);
    }
    
    public List<T> findAll() {
        List<T> resultados = new ArrayList<>();

        try {
            String query = "SELECT * FROM " + this.nombreTabla;
            try (ResultSet resultSet = statement.executeQuery(query)) {
                while (resultSet.next()) {
                    T entidad = mapearResultSetAEntidad(resultSet);
                    resultados.add(entidad);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return resultados;
    }
    
    public T findById(DATO id){
        T resultado = null;
        Field[] nombresCampos = obtenerCamposClase();
        String campoId = nombresCampos[0].getName();

        try {
            String query = "SELECT * FROM " + this.nombreTabla + " WHERE " + campoId + " = ?";
            try (PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setObject(1, id);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        resultado = mapearResultSetAEntidad(resultSet);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultado;
    }
    
    public T save(T entidad) {
    T resultado = null;
    try {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("INSERT INTO ").append(this.nombreTabla).append(" (");

        Field[] fields = entidad.getClass().getDeclaredFields();
        boolean primerCampo = true;
        for (Field field : fields) {
            if (primerCampo) {
                primerCampo = false;
            } else {
                String nombreCampo = field.getName();
                queryBuilder.append(nombreCampo).append(", ");
            }
        }
        queryBuilder.delete(queryBuilder.length() - 2, queryBuilder.length());
        queryBuilder.append(") VALUES (");
        
        for (Field field : fields) {
            field.setAccessible(true);
            Object valorCampo = field.get(entidad);
            if (valorCampo instanceof String) {
                queryBuilder.append("'").append(valorCampo).append("', ");
            } else {
                queryBuilder.append(valorCampo).append(", ");
            }
        }
        queryBuilder.delete(queryBuilder.length() - 2, queryBuilder.length());
        queryBuilder.append(")");
        String query = queryBuilder.toString();

        // Ejecutar la consulta SQL
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.executeUpdate();
        }
    } catch (IllegalAccessException | SQLException e) {
        e.printStackTrace();
    }
    return resultado;
}

    public void deleteById(DATO id){
        
    }
    
    protected abstract T mapearResultSetAEntidad(ResultSet resultSet) throws SQLException;
}
